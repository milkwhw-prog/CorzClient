package com.corz.client.utils;

import com.corz.client.utils.C2Assets.Loaded;
import com.corz.client.utils.C2Model.Clip;
import com.corz.client.utils.C2Model.Cue;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.Reference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.sound.sampled.AudioFormat;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.sound.StaticSound;
import net.minecraft.util.Identifier;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;
import com.corz.client.utils.C2Assets;
import com.corz.client.utils.C2Model;
import com.corz.client.utils.C2Cues;
import com.corz.client.utils.C2Butterfly;
import com.corz.client.utils.C2Knife;
import com.corz.client.utils.C2Clips;
import com.corz.client.utils.KnifeSoundCues;

public final class C2PackAudio {
   static final String SOUNDS_PACK = "sounds/c2pack/";
   private static volatile Map<String, C2PackAudio.Pack> packBadMap;
   private static long decoded;
   public static final String KNIFE_PACKS = "knife_packs";
   static final Pattern compile = Pattern.compile("c2visuals-knives-([a-z0-9_-]{1,32})\\.zip");
   static final Pattern c2PackPattern = Pattern.compile("assets/([a-z0-9_]+)/(knife_sounds|sounds)/([a-z0-9_./-]+)\\.ogg");
   private static long played;
   private static CompletableFuture<Void> runAsync;
   static final Pattern pattern = Pattern.compile("sounds/c2pack/(\\d{1,5})\\.ogg");
   private static final ExecutorService threadExecutor = Executors.newSingleThreadExecutor(runnable -> {
      Thread thread = new Thread(runnable, "Corz knife pack audio");
      thread.setDaemon(true);
      thread.setPriority(4);
      return thread;
   });
   private static long packMuchTime;
   private static final List<String> CORZCLIENT_OGG_ITEMS = new ArrayList<>();
   private static final Map<String, Integer> CORZCLIENT_OGG_MAP = new HashMap<>();
   public static final String PACK = "c2pack|";

   public static synchronized void updateMuchPack() {
      packMuchTime = 0L;
   }

   public static String formatCompile(String compileText) {
      if (compileText != null && !compileText.contains("..") && !compileText.contains("//")) {
         Matcher matcher = c2PackPattern.matcher(compileText);
         return matcher.matches() ? matcher.group(1) + ":" + matcher.group(3) : null;
      } else {
         return null;
      }
   }

   public static String formatText(String c2PackText, String c2PackText2) {
      return "c2pack|" + c2PackText + "|" + c2PackText2;
   }

   static synchronized String formatCorzclientOgg(Identifier id) {
      if (id != null && "corzclient".equals(id.getNamespace())) {
         Matcher matcher = pattern.matcher(id.getPath());
         if (!matcher.matches()) {
            return null;
         } else {
            int int2 = Integer.parseInt(matcher.group(1));
            return int2 < CORZCLIENT_OGG_ITEMS.size() ? CORZCLIENT_OGG_ITEMS.get(int2) : null;
         }
      } else {
         return null;
      }
   }

   public static boolean canPack(String packText) {
      return packText != null && packText.startsWith("c2pack|");
   }

   public static List<Path> getLinksNofollow(Path path) {
      var arrayList = new ArrayList();
      if (path != null && Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
         try (DirectoryStream directoryStream = Files.newDirectoryStream(path)) {
            for (Path resolve : (Iterable<java.nio.file.Path>) (directoryStream)) {
               if (formatCompileForString(resolve.getFileName().toString()) != null && Files.isRegularFile(resolve, LinkOption.NOFOLLOW_LINKS)) {
                  long filesSize = Files.size(resolve);
                  if (filesSize > 0L && filesSize <= 67108864L) {
                     arrayList.add(resolve);
                  }
               }
            }
         } catch (IOException | RuntimeException e) {
            return new ArrayList<>();
         }

         arrayList.sort((obj, obj2) -> ((java.nio.file.Path) obj).getFileName().toString().compareTo(((java.nio.file.Path) obj2).getFileName().toString()));
         return arrayList.size() > 8 ? new ArrayList<>(arrayList.subList(0, 8)) : arrayList;
      } else {
         return arrayList;
      }
   }

   public static List<C2Cues.Contact> getTimeEffect(List<Cue> list, float effectValue, Function<String, String> function) {
      if (list != null && !list.isEmpty()) {
         var arrayList = new ArrayList();
         float effectValue2 = getAmount();

         for (Cue cue : list) {
            if (!(cue.time() < 0.0F) && !(cue.time() > effectValue)) {
               String apply = (String)function.apply(cue.effect());
               if (apply != null) {
                  arrayList.add(new C2Cues.Contact(cue.time(), apply, effectValue2, 1.0F));
               }
            }
         }

         return arrayList.isEmpty() ? null : List.copyOf(arrayList);
      } else {
         return null;
      }
   }

   public static List<Cue> getEffectTime(List<Cue> list, float effectLimit, float wrapDegreesBound) {
      if (list != null && !list.isEmpty()) {
         float effectFactor = effectLimit > 0.0F && wrapDegreesBound > 0.0F ? wrapDegreesBound / effectLimit : 1.0F;
         var arrayList = new ArrayList(list.size());

         for (Cue cue : list) {
            arrayList.add(new Cue(effectFactor == 1.0F ? cue.time() : Math.min(wrapDegreesBound, cue.time() * effectFactor), cue.effect()));
         }

         return List.copyOf(arrayList);
      } else {
         return List.of();
      }
   }

   public static boolean canDeploySwish(String deploySwishText) {
      boolean deploySwishFlag = "deploy".equals(deploySwishText) || "deploy_butterfly".equals(deploySwishText);
      if (!deploySwishFlag && !"swish".equals(deploySwishText)) {
         return false;
      } else if (packBadMap != null && C2Butterfly.isC2ButterflyActive()) {
         C2Knife playerHand = C2Butterfly.getPlayerHand();
         if (playerHand == null) {
            return false;
         } else {
            Loaded supplyAsync = C2Assets.getSupplyAsync(playerHand);
            C2Model c2Model = supplyAsync == null ? null : supplyAsync.model();
            return canUnmodifiableClassics(playerHand, c2Model, deploySwishFlag ? (c2Model == null ? playerHand.formatDrawAngel() : C2Clips.formatDrawUnmodifiable(playerHand, c2Model)) : "melee_*");
         }
      } else {
         return false;
      }
   }

   public static C2Knife getButterflyEmerald(C2Knife c2Knife) {
      return c2Knife == C2Knife.BUTTERFLY ? C2Knife.BUTTERFLY_EMERALD : null;
   }

   private C2PackAudio() {
   }

   public static boolean canSoundsPack(Identifier id) {
      return id != null && "corzclient".equals(id.getNamespace()) && id.getPath().startsWith("sounds/c2pack/");
   }

   public static Map<String, String> getUnmodifiableMap(List<String> list) {
      var linkedHashMap = new LinkedHashMap();
      var linkedHashMap2 = new LinkedHashMap();

      for (String unmodifiableText : list) {
         String l = formatCompile(unmodifiableText);
         if (l != null) {
            var linkedHashMap3 = unmodifiableText.contains("/knife_sounds/") ? linkedHashMap : linkedHashMap2;
            if (linkedHashMap3.size() < 2048) {
               linkedHashMap3.putIfAbsent(l, unmodifiableText);
            }
         }
      }

      var linkedHashMap4 = new LinkedHashMap(linkedHashMap);

      for (Entry entry : (Iterable<java.util.Map.Entry>) (linkedHashMap2.entrySet())) {
         if (linkedHashMap4.size() < 2048) {
            linkedHashMap4.putIfAbsent((String)entry.getKey(), (String)entry.getValue());
         }
      }

      return Collections.unmodifiableMap(linkedHashMap4);
   }

   public static Path getResolve() {
      return FabricLoader.getInstance().getConfigDir().resolve("corzclient").resolve("knife_packs");
   }

   public static synchronized String formatDecodedPlayed() {
      return "played=" + played + " decoded=" + decoded + " slots=" + CORZCLIENT_OGG_ITEMS.size();
   }

   public static synchronized void updateAsyncRun() {
      if (runAsync == null) {
         runAsync = CompletableFuture.runAsync(() -> {
            Map unmodifiableMap2;
            try {
               unmodifiableMap2 = getUnmodifiableMap(getResolve());
            } catch (RuntimeException e) {
               System.err.println("[C" + e);
               unmodifiableMap2 = Map.of();
            }

            packBadMap = unmodifiableMap2;
            int asyncRunTotal = 0;

            for (C2PackAudio.Pack pack : (Iterable<com.corz.client.utils.C2PackAudio.Pack>) (unmodifiableMap2.values())) {
               asyncRunTotal += pack.entries().size();
            }
         }, threadExecutor);
      }
   }

   public static CompletableFuture<StaticSound> getSupplyAsync(Identifier id) {
      String packKnifeText = formatCorzclientOgg(id);
      return CompletableFuture.supplyAsync(() -> {
         try {
            if (packKnifeText == null) {
               throw new IOException("Unknown knife pack sound " + id);
            } else {
               synchronized (C2PackAudio.class) {
                  if (packMuchTime > 134217728L) {
                     throw new IOException("Knife pack sounds use too much memory");
                  }
               }

               C2PackAudio.Pcm packKnife = getPackKnife(packKnifeText);
               synchronized (C2PackAudio.class) {
                  decoded++;
                  packMuchTime = packMuchTime + packKnife.data().limit();
               }

               return new StaticSound(packKnife.data(), packKnife.format());
            }
         } catch (IOException | RuntimeException e) {
            System.err.println("[C" + packKnifeText + ": " + e);
            throw new CompletionException(e);
         }
      }, threadExecutor);
   }

   static String formatText(Map<String, C2PackAudio.Pack> map, String c2PackText, String c2PackText2) {
      if (map != null && c2PackText2 != null) {
         C2PackAudio.Pack pack2 = c2PackText == null ? null : (C2PackAudio.Pack)map.get(c2PackText);
         if (pack2 != null && pack2.entries().containsKey(c2PackText2)) {
            return formatText(pack2.name(), c2PackText2);
         } else {
            for (C2PackAudio.Pack pack3 : map.values()) {
               if (pack3.entries().containsKey(c2PackText2)) {
                  return formatText(pack3.name(), c2PackText2);
               }
            }

            return null;
         }
      } else {
         return null;
      }
   }

   static C2PackAudio.Pcm getPackKnife(String packKnifeText)  throws java.io.IOException {
      String substring = packKnifeText.substring("c2pack|".length());
      int indexOf = substring.indexOf(124);
      if (indexOf < 0) {
         throw new IOException("Bad knife pack key");
      } else {
         var packBadMap2 = packBadMap;
         C2PackAudio.Pack pack = packBadMap2 == null ? null : (C2PackAudio.Pack)packBadMap2.get(substring.substring(0, indexOf));
         String packKnifeText2 = pack == null ? null : pack.entries().get(substring.substring(indexOf + 1));
         if (packKnifeText2 == null) {
            throw new IOException("Knife pack sound missing");
         } else {
            ZipFile zipFile = new ZipFile(pack.zip().toFile());

            byte[] packKnifeBytes;
            try {
               ZipEntry zipEntry = zipFile.getEntry(packKnifeText2);
               if (zipEntry == null) {
                  throw new IOException("Knife pack sound missing");
               }

               try (InputStream inputStream = zipFile.getInputStream(zipEntry)) {
                  packKnifeBytes = inputStream.readNBytes(2097153);
               }
            } catch (Throwable e) {
               try {
                  zipFile.close();
               } catch (Throwable e2) {
                  e.addSuppressed(e2);
               }

               throw e;
            }

            zipFile.close();
            if (packKnifeBytes.length > 2097152L) {
               throw new IOException("Knife pack sound is too big");
            } else {
               return getStbVorbis(packKnifeBytes);
            }
         }
      }
   }

   static C2PackAudio.Pcm getStbVorbis(byte[] stbVorbisBytes)  throws java.io.IOException {
      ByteBuffer allocateDirect = ByteBuffer.allocateDirect(stbVorbisBytes.length);
      allocateDirect.put(stbVorbisBytes).flip();
      MemoryStack stackPush = MemoryStack.stackPush();

      C2PackAudio.Pcm stbVorbis;
      try {
         IntBuffer mallocInt = stackPush.mallocInt(1);
         long stb_vorbis_open_memory = STBVorbis.stb_vorbis_open_memory(allocateDirect, mallocInt, null);
         if (stb_vorbis_open_memory == 0L) {
            throw new IOException("Not a valid ogg (" + mallocInt.get(0) + ")");
         }

         try {
            label92: {
               STBVorbisInfo stb_vorbis_get_info = STBVorbis.stb_vorbis_get_info(stb_vorbis_open_memory, STBVorbisInfo.malloc(stackPush));
               int channels = stb_vorbis_get_info.channels();
               int sample_rate = stb_vorbis_get_info.sample_rate();
               if (channels >= 1 && channels <= 2 && sample_rate >= 8000 && sample_rate <= 96000) {
                  long stbVorbisFactor = STBVorbis.stb_vorbis_stream_length_in_samples(stb_vorbis_open_memory);
                  long stbVorbisValue = stbVorbisFactor * channels * 2L;
                  if (stbVorbisFactor > 0L && stbVorbisValue <= 8388608L) {
                     ByteBuffer order = ByteBuffer.allocateDirect((int)stbVorbisValue).order(ByteOrder.nativeOrder());
                     int stbVorbisFactor2 = STBVorbis.stb_vorbis_get_samples_short_interleaved(stb_vorbis_open_memory, channels, order.asShortBuffer());
                     if (stbVorbisFactor2 <= 0) {
                        throw new IOException("Knife pack sound is empty");
                     }

                     order.limit(Math.min(order.capacity(), stbVorbisFactor2 * channels * 2));
                     AudioFormat audioFormat = new AudioFormat(sample_rate, 16, channels, true, ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN);
                     stbVorbis = new C2PackAudio.Pcm(order, audioFormat);
                     break label92;
                  }

                  throw new IOException("Knife pack sound is too long");
               }

               throw new IOException("Unsupported ogg format");
            }
         } finally {
            STBVorbis.stb_vorbis_close(stb_vorbis_open_memory);
            Reference.reachabilityFence(allocateDirect);
         }
      } catch (Throwable e) {
         if (stackPush != null) {
            try {
               stackPush.close();
            } catch (Throwable e2) {
               e.addSuppressed(e2);
            }
         }

         throw e;
      }

      if (stackPush != null) {
         stackPush.close();
      }

      return stbVorbis;
   }

   static Clip getModelUnmodifiable(C2Knife c2Knife, Clip clip) {
      C2Knife butterflyEmerald = getButterflyEmerald(c2Knife);
      if (butterflyEmerald == null) {
         return null;
      } else {
         C2Assets.handleSupplyAsync(butterflyEmerald);
         Loaded supplyAsync = C2Assets.getSupplyAsync(c2Knife);
         Loaded supplyAsync2 = C2Assets.getSupplyAsync(butterflyEmerald);
         if (supplyAsync2 == null) {
            return null;
         } else {
            String formatInspectDraw = C2Clips.formatInspectDraw(c2Knife, supplyAsync == null ? null : supplyAsync.model(), clip.name(), butterflyEmerald, supplyAsync2.model());
            return formatInspectDraw == null ? null : supplyAsync2.model().unmodifiableMap.get(formatInspectDraw);
         }
      }
   }

   public static List<C2Cues.Contact> getLengthCues(C2Knife c2Knife, Clip clip) {
      if (c2Knife != null && clip != null && isBadPack()) {
         var packBadMap2 = packBadMap;
         if (!c2Knife.isClassics()) {
            return getTimeEffect(clip.cues(), clip.length(), lengthCuesText -> formatText(packBadMap2, c2Knife.classics, lengthCuesText));
         } else {
            Clip modelUnmodifiable = getModelUnmodifiable(c2Knife, clip);
            if (modelUnmodifiable == null) {
               return null;
            } else {
               String classics = getButterflyEmerald(c2Knife).classics;
               return getTimeEffect(getEffectTime(modelUnmodifiable.cues(), modelUnmodifiable.length(), clip.length()), clip.length(), all -> formatText(packBadMap2, classics, all));
            }
         }
      } else {
         return null;
      }
   }

   public static boolean isBadPack() {
      var packBadMap2 = packBadMap;
      if (packBadMap2 == null) {
         updateAsyncRun();
      }

      return packBadMap2 != null && !packBadMap2.isEmpty();
   }

   static String formatCompileForString(String compileText) {
      if (compileText == null) {
         return null;
      } else {
         Matcher matcher = compile.matcher(compileText);
         return matcher.matches() ? matcher.group(1) : null;
      }
   }

   static Map<String, String> getMap(ZipFile zipFile) {
      var arrayList = new ArrayList();
      var entries = zipFile.entries();
      int c2PackCounter = 0;

      while (entries.hasMoreElements() && c2PackCounter++ < 8192) {
         ZipEntry randomElement = (ZipEntry)entries.nextElement();
         if (!randomElement.isDirectory()) {
            arrayList.add(randomElement.getName());
         }
      }

      return getUnmodifiableMap(arrayList);
   }

   public static synchronized Identifier getIdentifier(String identifierText) {
      Integer corzclient_ogg_map = CORZCLIENT_OGG_MAP.get(identifierText);
      if (corzclient_ogg_map == null) {
         if (CORZCLIENT_OGG_ITEMS.size() >= 16384) {
            return null;
         }

         corzclient_ogg_map = CORZCLIENT_OGG_ITEMS.size();
         CORZCLIENT_OGG_ITEMS.add(identifierText);
         CORZCLIENT_OGG_MAP.put(identifierText, corzclient_ogg_map);
      }

      return Identifier.of("corzclient", "c2pack/" + corzclient_ogg_map);
   }

   public static synchronized void updatePlayed() {
      played++;
   }

   public static Map<String, C2PackAudio.Pack> getUnmodifiableMap(Path path) {
      var linkedHashMap = new LinkedHashMap();

      for (Path path2 : getLinksNofollow(path)) {
         String formatCompile = formatCompileForString(path2.getFileName().toString());

         try (ZipFile zipFile = new ZipFile(path2.toFile())) {
            var map = getMap(zipFile);
            if (!map.isEmpty()) {
               linkedHashMap.put(formatCompile, new C2PackAudio.Pack(formatCompile, path2, map));
            }
         } catch (RuntimeException | IOException e) {
            System.err.println("[C" + path2.getFileName() + ": " + e);
         }
      }

      return Collections.unmodifiableMap(linkedHashMap);
   }

   public static boolean canUnmodifiableClassics(C2Knife c2Knife, C2Model c2Model, String unmodifiableClassicsText) {
      if (c2Knife == null || unmodifiableClassicsText == null || !isBadPack()) {
         return false;
      } else if (c2Model == null) {
         return !c2Knife.isClassics() && packBadMap.get(c2Knife.classics) != null;
      } else if (unmodifiableClassicsText.endsWith("*")) {
         String substring = unmodifiableClassicsText.substring(0, unmodifiableClassicsText.length() - 1);

         for (Clip clip : c2Model.unmodifiableMap.values()) {
            if (clip.name().startsWith(substring) && c2Knife.isEndsWith(clip.name())) {
               var lengthCues = getLengthCues(c2Knife, clip);
               if (lengthCues != null && !lengthCues.isEmpty()) {
                  return true;
               }
            }
         }

         return false;
      } else {
         var lengthCues2 = getLengthCues(c2Knife, c2Model.unmodifiableMap.get(unmodifiableClassicsText));
         return lengthCues2 != null && !lengthCues2.isEmpty();
      }
   }

   static synchronized long getMuchPack() {
      return packMuchTime;
   }

   public static float getAmount() {
      float c2PackLimit = KnifeSoundCues.getAmount(0.8);
      return c2PackLimit > 0.0F ? 0.7F / c2PackLimit : 0.7F;
   }

   public record Pack(String name, Path zip, Map<String, String> entries) {
   }

   record Pcm(ByteBuffer data, AudioFormat format) {
   }
}
