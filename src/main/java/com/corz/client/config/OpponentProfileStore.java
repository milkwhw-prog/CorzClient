package com.corz.client.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import net.fabricmc.loader.api.FabricLoader;
import com.corz.client.utils.DirExtractionUtils;
import com.corz.client.utils.HabitsRecordData;
import com.corz.client.utils.MinVec;

public final class OpponentProfileStore {
   private static final Pattern USERNAME_PATTERN = Pattern.compile("[a-z0-9_]{3,16}");
   private final ConcurrentLinkedQueue<OpponentProfileStore.Loaded> load;
   private final LinkedHashMap<String, OpponentProfileStore.Entry> entriesByName = new LinkedHashMap<>(16, 0.75F, true);
   private static final String WORKER_THREAD_NAME = "corz-bot-learn";
   private static final String JSON_EXTENSION = ".json";
   private final ConcurrentLinkedQueue<OpponentProfileStore.Entry> retryQueue;
   private boolean persistenceEnabled;
   private static final int FORMAT_VERSION = 1;
   private final Supplier<Path> dir;
   private static final Pattern PROFILE_KEY_PATTERN = Pattern.compile("o_[a-z0-9_]{3,16}");
   private volatile int off;
   private final Executor io;
   private static final String TEMP_FILE_SUFFIX = ".json.tmp";
   private final LongSupplier clockMs;
   private boolean hasSavedOnce;
   private static final Pattern PLAYER_FILE_PATTERN = Pattern.compile("o_[a-z0-9_]{3,16}\\.json");
   private volatile Path cachedDirPath;
   private static final Pattern TEMP_FILE_PATTERN = Pattern.compile("o_[a-z0-9_]{3,16}\\.json\\.tmp");
   private final Random random;

   private static int countProfileFiles(Path path)  throws java.io.IOException {
      int linksNofollow = 0;

      try (DirectoryStream directoryStream = Files.newDirectoryStream(path)) {
         for (Path resolve : (Iterable<java.nio.file.Path>) (directoryStream)) {
            if (PLAYER_FILE_PATTERN.matcher(resolve.getFileName().toString()).matches() && Files.isRegularFile(resolve, LinkOption.NOFOLLOW_LINKS)) {
               linksNofollow++;
            }
         }
      }

      return linksNofollow;
   }

   private String formatEntryState(OpponentProfileStore.Entry entry) {
      if (entry.min2.isFlag()) {
         return "L";
      } else {
         return entry.value == 1 ? "P" : "-";
      }
   }

   public void setPersistenceEnabled(boolean persistFilesFlag) {
      boolean persistenceEnabled2 = this.persistenceEnabled;
      this.persistenceEnabled = persistFilesFlag;
      if (persistFilesFlag && !persistenceEnabled2) {
         for (OpponentProfileStore.Entry entry : new ArrayList<>(this.entriesByName.values())) {
            this.scheduleLoad(entry);
         }
      }
   }

   private void loadProfileFromDisk(OpponentProfileStore.Entry entry, String linksText) {
      HabitsRecordData habitsRecordData = null;

      try {
         if (!DirExtractionUtils.isFlag()) {
            Path pathDir = this.getProfileDir();
            Path resolve = pathDir.resolve(linksText + ".json");
            if (Files.isRegularFile(resolve, LinkOption.NOFOLLOW_LINKS) && Files.size(resolve) <= 16384L) {
               String smallUtf8File = readSmallUtf8File(resolve);
               if (smallUtf8File != null) {
                  habitsRecordData = HabitsRecordJsonCodec.parseHabitsRecord(smallUtf8File);
               }
            }

            if (Files.isDirectory(pathDir)) {
               this.off = countProfileFiles(pathDir);
            }
         }
      } catch (IOException | RuntimeException e2) {
         reportIoError("load", e2);
      }

      this.load.add(new OpponentProfileStore.Loaded(entry, habitsRecordData));
   }

   public static String toProfileKey(String compileRootText) {
      if (compileRootText == null) {
         return null;
      } else {
         String lowerCase = compileRootText.toLowerCase(Locale.ROOT);
         return USERNAME_PATTERN.matcher(lowerCase).matches() ? "o_" + lowerCase : null;
      }
   }

   private void saveEntryIfDue(OpponentProfileStore.Entry entry, boolean persistFilesFlag) {
      MinVec min2 = entry.min2;
      if (this.persistenceEnabled && min2.getText() != null && min2.isUidActive() && !DirExtractionUtils.isFlag()) {
         if (entry.value != 2) {
            this.scheduleLoad(entry);
         } else {
            long longValue = this.clockMs.getAsLong();
            if (persistFilesFlag || entry.longValue == Long.MIN_VALUE || longValue < entry.longValue || longValue - entry.longValue >= 30000L) {
               String formatArms = HabitsRecordJsonCodec.serializeHabitsRecordWithinLimit(min2.getUidMin(longValue));
               if (formatArms != null) {
                  entry.longValue = longValue;
                  boolean pruneExistingFlag = !this.hasSavedOnce;
                  this.hasSavedOnce = true;
                  String persistFilesText = min2.getText();
                  this.trySubmit(() -> this.writeProfileFile(entry, persistFilesText, formatArms, pruneExistingFlag));
               }
            }
         }
      }
   }

   private static long getLastModifiedMs(Path path)  throws java.io.IOException {
      return Files.getLastModifiedTime(path, LinkOption.NOFOLLOW_LINKS).toMillis();
   }

   public String describeState() {
      StringBuilder stringBuilder = new StringBuilder("dir=");

      Path path;
      try {
         path = this.getProfileDir();
      } catch (RuntimeException e) {
         path = null;
      }

      stringBuilder.append(path == null ? "?" : path.toString());
      stringBuilder.append(" persist=").append(this.persistenceEnabled ? "on" : "off");
      int off = this.off;
      stringBuilder.append(" files=").append(off < 0 ? "?" : String.valueOf(off));
      ArrayList arrayList = new ArrayList<>(this.entriesByName.values());
      Collections.reverse(arrayList);
      int persistFilesCounter = 0;

      for (OpponentProfileStore.Entry entry : (Iterable<com.corz.client.config.OpponentProfileStore.Entry>) (arrayList)) {
         if (persistFilesCounter++ >= 16) {
            break;
         }

         stringBuilder.append(' ')
            .append(entry.min2.getText() == null ? "mem" : entry.min2.getText())
            .append(':')
            .append(entry.min2.getMin())
            .append(':')
            .append(this.formatEntryState(entry));
      }

      return stringBuilder.toString();
   }

   private void scheduleLoad(OpponentProfileStore.Entry entry) {
      String persistFilesText = entry.min2.getText();
      if (entry.value == 0 && persistFilesText != null && this.persistenceEnabled && !DirExtractionUtils.isFlag()) {
         entry.value = 1;
         if (!this.trySubmit(() -> this.loadProfileFromDisk(entry, persistFilesText))) {
            entry.value = 0;
         }
      }
   }

   public void forgetProfile(String persistFilesText) {
      this.entriesByName.remove(toLowerKey(persistFilesText));
      String profileKey = toProfileKey(persistFilesText);
      if (profileKey != null && this.persistenceEnabled && !DirExtractionUtils.isFlag()) {
         this.trySubmit(() -> this.deleteProfileFiles(profileKey));
      }
   }

   private static String toLowerKey(String lowerCaseText) {
      return lowerCaseText == null ? "" : lowerCaseText.toLowerCase(Locale.ROOT);
   }

   public static OpponentProfileStore createDefault() {
      return new OpponentProfileStore(OpponentProfileStore::getDefaultProfileDir, new OpponentProfileStore.LazyDaemon(), System::currentTimeMillis, new Random());
   }

   private void deleteProfileFiles(String forgetCompileText) {
      if (!DirExtractionUtils.isFlag() && PROFILE_KEY_PATTERN.matcher(forgetCompileText).matches()) {
         try {
            Path pathDir = this.getProfileDir();
            Files.deleteIfExists(pathDir.resolve(forgetCompileText + ".json"));
            Files.deleteIfExists(pathDir.resolve(forgetCompileText + ".json.tmp"));
            if (Files.isDirectory(pathDir)) {
               this.off = countProfileFiles(pathDir);
            }
         } catch (RuntimeException | IOException e) {
            reportIoError("forget", e);
         }
      }
   }

   public String describeProfile(MinVec minVec) {
      if (minVec == null) {
         return "-";
      } else {
         OpponentProfileStore.Entry filespersistmap = this.entriesByName.get(toLowerKey(minVec.getUidText()));
         String memPersistText = !this.persistenceEnabled ? "off" : (minVec.getText() == null ? "mem" : minVec.getText());
         String memPersistText2 = filespersistmap != null && filespersistmap.min2 == minVec ? this.formatEntryState(filespersistmap) : (minVec.isFlag() ? "L" : "-");
         return memPersistText + ":" + minVec.getMin() + ":" + memPersistText2;
      }
   }

   private void evictOldEntries() {
      Iterator iterator = this.entriesByName.values().iterator();

      while (this.entriesByName.size() > 64 && iterator.hasNext()) {
         OpponentProfileStore.Entry entry = (OpponentProfileStore.Entry)iterator.next();
         iterator.remove();
         if (entry.min2.isUidActive()) {
            this.saveEntryIfDue(entry, true);
         }
      }
   }

   public void applyLoadedProfiles() {
      OpponentProfileStore.Loaded loaded;
      while ((loaded = this.load.poll()) != null) {
         OpponentProfileStore.Entry entry = loaded.entry();
         entry.value = 2;
         HabitsRecordData record = loaded.record();
         if (record != null && uuidMatchesText(entry.min2.getUUID(), record.name())) {
            entry.min2.handleMinArms(record);
         }
      }

      OpponentProfileStore.Entry poll2;
      while ((poll2 = this.retryQueue.poll()) != null) {
         poll2.longValue = Long.MIN_VALUE;
         poll2.min2.updateFlag();
      }
   }

   private static String readSmallUtf8File(Path path)  throws java.io.IOException {
      String formatUtf;
      try (InputStream inputStream = Files.newInputStream(path)) {
         byte[] nBytes = inputStream.readNBytes(16385);
         if (nBytes.length > 16384) {
            return null;
         }

         formatUtf = new String(nBytes, StandardCharsets.UTF_8);
      }

      return formatUtf;
   }

   public boolean isPersistenceEnabled() {
      return this.persistenceEnabled;
   }

   private static Path getDefaultProfileDir() {
      try {
         return FabricLoader.getInstance().getConfigDir().resolve("corzclient").resolve("bots").resolve("opponents");
      } catch (Throwable e) {
         return Path.of(System.getProperty("user.dir"), "config", "corzclient", "bots", "opponents");
      }
   }

   private static boolean uuidMatchesText(UUID uuid, String dirJsonText) {
      return uuid != null && dirJsonText != null && uuid.toString().equalsIgnoreCase(dirJsonText);
   }

   public void saveAllEntries() {
      for (OpponentProfileStore.Entry entry : new ArrayList<>(this.entriesByName.values())) {
         if (entry.min2.isUidActive()) {
            this.saveEntryIfDue(entry, true);
         }
      }
   }

   private void writeProfileFile(OpponentProfileStore.Entry entry, String pruneExistingText, String formatArms, boolean pruneExistingFlag) {
      if (!DirExtractionUtils.isFlag() && PROFILE_KEY_PATTERN.matcher(pruneExistingText).matches()) {
         byte[] bytes = formatArms.getBytes(StandardCharsets.UTF_8);
         if (bytes.length <= 16384) {
            Path path = null;

            Path profileDir2;
            try {
               profileDir2 = this.getProfileDir();
               Files.createDirectories(profileDir2);
               Path resolve = profileDir2.resolve(pruneExistingText + ".json");
               path = profileDir2.resolve(pruneExistingText + ".json.tmp");
               Files.write(path, bytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
               if (DirExtractionUtils.isFlag()) {
                  Files.deleteIfExists(path);
                  return;
               }

               try {
                  Files.move(path, resolve, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
               } catch (AtomicMoveNotSupportedException e) {
                  Files.move(path, resolve, StandardCopyOption.REPLACE_EXISTING);
               }

               path = null;
            } catch (RuntimeException | IOException e2) {
               reportIoError("save", e2);
               if (path != null) {
                  deleteQuietly(path);
               }

               this.retryQueue.add(entry);
               return;
            }

            try {
               int off = countProfileFiles(profileDir2);
               if (pruneExistingFlag || off > 200) {
                  this.pruneOldProfiles(profileDir2, pruneExistingText);
                  off = countProfileFiles(profileDir2);
               }

               this.off = off;
            } catch (Exception e3) {
               reportIoError("prune", e3);
            }
         }
      }
   }

   private static void deleteQuietly(Path path) {
      try {
         Files.deleteIfExists(path);
      } catch (IOException | RuntimeException e) {
         reportIoError("cleanup", e);
      }
   }

   public MinVec getOrCreateProfile(String persistFilesText, UUID uUID) {
      String lowerCase = toLowerKey(persistFilesText);
      OpponentProfileStore.Entry filespersistmap = this.entriesByName.get(lowerCase);
      if (filespersistmap != null && uUID != null && !uUID.equals(filespersistmap.min2.getUUID())) {
         this.entriesByName.remove(lowerCase);
         filespersistmap = null;
      }

      if (filespersistmap == null) {
         filespersistmap = new OpponentProfileStore.Entry(new MinVec(persistFilesText, uUID, this.random));
         this.entriesByName.put(lowerCase, filespersistmap);
         this.evictOldEntries();
      }

      if (this.persistenceEnabled) {
         this.scheduleLoad(filespersistmap);
      }

      return filespersistmap.min2;
   }

   private static void reportIoError(String dirJsonText, Throwable throwable) {
   }

   private Path getProfileDir() {
      Path path = this.cachedDirPath;
      if (path == null) {
         path = this.dir.get();
         this.cachedDirPath = path;
      }

      return path;
   }

   public void saveProfile(MinVec minVec, boolean persistFilesFlag) {
      if (minVec != null) {
         OpponentProfileStore.Entry filespersistmap = this.entriesByName.get(toLowerKey(minVec.getUidText()));
         if (filespersistmap != null && filespersistmap.min2 == minVec) {
            this.saveEntryIfDue(filespersistmap, persistFilesFlag);
         }
      }
   }

   private boolean trySubmit(Runnable runnable) {
      try {
         this.io.execute(runnable);
         return true;
      } catch (RejectedExecutionException e) {
         reportIoError("submit", e);
         return false;
      }
   }

   private void pruneOldProfiles(Path path, String compileLinksText)  throws java.io.IOException {
      long asLong = this.clockMs.getAsLong();
      ArrayList arrayList = new ArrayList();

      try (DirectoryStream directoryStream = Files.newDirectoryStream(path)) {
         for (Path resolve : (Iterable<java.nio.file.Path>) (directoryStream)) {
            String compileCommandText = resolve.getFileName().toString();
            if (Files.isRegularFile(resolve, LinkOption.NOFOLLOW_LINKS)) {
               if (PLAYER_FILE_PATTERN.matcher(compileCommandText).matches()) {
                  if (!compileCommandText.equals(compileLinksText + ".json")) {
                     arrayList.add(new OpponentProfileStore.Aged(resolve, getLastModifiedMs(resolve), compileCommandText));
                  }
               } else if (TEMP_FILE_PATTERN.matcher(compileCommandText).matches() && !compileCommandText.equals(compileLinksText + ".json.tmp") && asLong - getLastModifiedMs(resolve) > 60000L) {
                  Files.deleteIfExists(resolve);
               }
            }
         }
      }

      int compileLinksLimit = arrayList.size() + 1 - 200;
      if (compileLinksLimit > 0) {
         arrayList.sort(Comparator.comparingLong(OpponentProfileStore.Aged::modified).thenComparing(OpponentProfileStore.Aged::name));

         for (int i = 0; i < compileLinksLimit && i < arrayList.size(); i++) {
            Files.deleteIfExists(((OpponentProfileStore.Aged)arrayList.get(i)).path());
         }
      }
   }

   public OpponentProfileStore(Supplier<Path> supplier, Executor executor, LongSupplier longSupplier, Random random) {
      this.load = new ConcurrentLinkedQueue<>();
      this.retryQueue = new ConcurrentLinkedQueue<>();
      this.off = -1;
      this.dir = Objects.requireNonNull(supplier, "dir");
      this.io = Objects.requireNonNull(executor, "io");
      this.clockMs = Objects.requireNonNull(longSupplier, "clockMs");
      this.random = random == null ? new Random() : random;
   }

   private record Aged(Path path, long modified, String name) {
   }

   private static final class Entry {
      long longValue;
      int value = 0;
      final MinVec min2;

      Entry(MinVec min) {
         this.longValue = Long.MIN_VALUE;
         this.min2 = min;
      }
   }

   private static final class LazyDaemon implements Executor {
      private ThreadPoolExecutor threadPoolExecutor;

      @Override
      public synchronized void execute(Runnable runnable) {
         if (this.threadPoolExecutor == null) {
            ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1, 30L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), OpponentProfileStore.LazyDaemon::newWorkerThread);
            threadPoolExecutor.allowCoreThreadTimeOut(true);
            this.threadPoolExecutor = threadPoolExecutor;
         }

         this.threadPoolExecutor.execute(runnable);
      }

      private static Thread newWorkerThread(Runnable runnable) {
         Thread thread = new Thread(runnable, "corz-bot-learn");
         thread.setDaemon(true);
         return thread;
      }
   }

   private record Loaded(OpponentProfileStore.Entry entry, HabitsRecordData record) {
   }
}
