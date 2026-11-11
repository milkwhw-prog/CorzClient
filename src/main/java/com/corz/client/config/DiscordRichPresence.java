package com.corz.client.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.UnixDomainSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class DiscordRichPresence {
   private static final String DISCORD_INVITE_URL = "https://discord.gg/corzgg";
   private static long startTimestampSeconds;
   private static final String LARGE_IMAGE_KEY = "corz_logo_v2";
   private static final String ACTIVITY_DETAILS = "Playing CorzClient";
   private static InputStream ipcInputStream;
   private static final String WEBSITE_URL = "https://www.corz.gg/";
   private static final String ACTIVITY_STATE = "corz.gg";
   private static final AtomicInteger NONCE_COUNTER = new AtomicInteger(1);
   private static final String LARGE_IMAGE_TEXT = "CorzClient";
   private static Closeable ipcConnection;
   private static ScheduledExecutorService presenceExecutor;
   private static OutputStream ipcOutputStream;
   private static final int IPC_VERSION = 1;
   private static volatile boolean ipcReady = false;

   public static void shutdownPresence() {
      ipcReady = false;
      if (presenceExecutor != null) {
         presenceExecutor.shutdownNow();
      }

      try {
         if (ipcOutputStream != null) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("v", 1);
            jsonObject.addProperty("client_id", String.valueOf(1476045767755174113L));
            writeIpcFrame(2, jsonObject.toString());
         }
      } catch (Exception e) {
      }

      closeIpcConnection();
   }

   private static byte[] readIpcBytes(int streamInputSize)  throws java.io.IOException {
      byte[] bytes = new byte[streamInputSize];
      int streamInputTotal = 0;

      while (streamInputTotal < streamInputSize) {
         int read = ipcInputStream.read(bytes, streamInputTotal, streamInputSize - streamInputTotal);
         if (read < 0) {
            return null;
         }

         streamInputTotal += read;
      }

      return bytes;
   }

   public static void startPresenceUpdates() {
      startTimestampSeconds = System.currentTimeMillis() / 1000L;
      presenceExecutor = Executors.newSingleThreadScheduledExecutor(runnable -> {
         Thread thread = new Thread(runnable, "CorzClient-DiscordRPC");
         thread.setDaemon(true);
         return thread;
      });
      presenceExecutor.scheduleAtFixedRate(DiscordRichPresence::reconnectIfNotReady, 0L, 15L, TimeUnit.SECONDS);
   }

   private static void openIpcConnection() {
      boolean contains = System.getProperty("os.name", "").toLowerCase().contains("win");
      if (contains) {
         for (int i = 0; i < 10; i++) {
            try {
               String tmpdirStreamText = "\\\\.\\pipe\\discord-ipc-" + i;
               RandomAccessFile randomAccessFile = new RandomAccessFile(tmpdirStreamText, "rw");
               ipcConnection = randomAccessFile;
               ipcInputStream = new FileInputStream(randomAccessFile.getFD());
               ipcOutputStream = new FileOutputStream(randomAccessFile.getFD());
               return;
            } catch (IOException e) {
            }
         }
      } else {
         String[] strings = new String[]{System.getenv("XDG_RUNTIME_DIR"), System.getenv("TMPDIR"), System.getProperty("java.io.tmpdir"), "/tmp"};

         for (String stringsItem : strings) {
            if (stringsItem != null) {
               for (int j = 0; j < 10; j++) {
                  try {
                     UnixDomainSocketAddress of = UnixDomainSocketAddress.of(stringsItem + "/discord-ipc-" + j);
                     SocketChannel open3 = SocketChannel.open(of);
                     open3.configureBlocking(true);
                     ipcConnection = open3;
                     ipcInputStream = open3.socket().getInputStream();
                     ipcOutputStream = open3.socket().getOutputStream();
                     return;
                  } catch (Exception e2) {
                  }
               }
            }
         }
      }
   }

   private static void sendActivityUpdate() {
      if (ipcReady && ipcOutputStream != null) {
         try {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("details", "Playing CorzClient");
            jsonObject.addProperty("state", "corz.gg");
            JsonObject jsonObject2 = new JsonObject();
            jsonObject2.addProperty("start", startTimestampSeconds);
            jsonObject.add("timestamps", jsonObject2);
            JsonObject jsonObject3 = new JsonObject();
            jsonObject3.addProperty("large_image", "corz_logo_v2");
            jsonObject3.addProperty("large_text", "CorzClient");
            jsonObject.add("assets", jsonObject3);
            JsonArray jsonArray = new JsonArray();
            JsonObject jsonObject4 = new JsonObject();
            jsonObject4.addProperty("label", "Website");
            jsonObject4.addProperty("url", "https://www.corz.gg/");
            jsonArray.add(jsonObject4);
            JsonObject jsonObject5 = new JsonObject();
            jsonObject5.addProperty("label", "Discord");
            jsonObject5.addProperty("url", "https://discord.gg/corzgg");
            jsonArray.add(jsonObject5);
            jsonObject.add("buttons", jsonArray);
            JsonObject jsonObject6 = new JsonObject();
            jsonObject6.addProperty("pid", (int)(ProcessHandle.current().pid() & 2147483647L));
            jsonObject6.add("activity", jsonObject);
            JsonObject jsonObject7 = new JsonObject();
            jsonObject7.addProperty("cmd", "SET_ACTIVITY");
            jsonObject7.addProperty("nonce", String.valueOf(NONCE_COUNTER.getAndIncrement()));
            jsonObject7.add("args", jsonObject6);
            writeIpcFrame(1, jsonObject7.toString());
         } catch (Exception e) {
            closeIpcConnection();
         }
      }
   }

   private static void writeIpcFrame(int streamOutputValue, String streamOutputText)  throws java.io.IOException {
      byte[] bytes = streamOutputText.getBytes(StandardCharsets.UTF_8);
      ByteBuffer order = ByteBuffer.allocate(8 + bytes.length).order(ByteOrder.LITTLE_ENDIAN);
      order.putInt(streamOutputValue);
      order.putInt(bytes.length);
      order.put(bytes);
      ipcOutputStream.write(order.array());
      ipcOutputStream.flush();
   }

   private static String readIpcFrame() {
      try {
         byte[] streamInput = readIpcBytes(8);
         if (streamInput == null) {
            return null;
         } else {
            ByteBuffer order = ByteBuffer.wrap(streamInput).order(ByteOrder.LITTLE_ENDIAN);
            order.getInt();
            int int2 = order.getInt();
            byte[] ipcBytes = readIpcBytes(int2);
            return ipcBytes == null ? null : new String(ipcBytes, StandardCharsets.UTF_8);
         }
      } catch (Exception e) {
         return null;
      }
   }

   private static void connectAndHandshake() {
      closeIpcConnection();

      try {
         openIpcConnection();
         if (ipcOutputStream == null) {
            return;
         }

         JsonObject jsonObject = new JsonObject();
         jsonObject.addProperty("v", 1);
         jsonObject.addProperty("client_id", String.valueOf(1476045767755174113L));
         writeIpcFrame(0, jsonObject.toString());
         Future submit = Executors.newSingleThreadExecutor().submit(() -> readIpcFrame());
         String readyClientText = (String)submit.get(3L, TimeUnit.SECONDS);
         if (readyClientText == null || !readyClientText.contains("READY")) {
            closeIpcConnection();
            return;
         }

         ipcReady = true;
         Thread.sleep(200L);
         sendActivityUpdate();
      } catch (Exception e) {
         closeIpcConnection();
      }
   }

   private static void closeIpcConnection() {
      ipcReady = false;

      try {
         if (ipcOutputStream != null) {
            ipcOutputStream.close();
         }
      } catch (Exception e) {
      }

      try {
         if (ipcInputStream != null) {
            ipcInputStream.close();
         }
      } catch (Exception e2) {
      }

      try {
         if (ipcConnection != null) {
            ipcConnection.close();
         }
      } catch (Exception e3) {
      }

      ipcOutputStream = null;
      ipcInputStream = null;
      ipcConnection = null;
   }

   private static void reconnectIfNotReady() {
      if (!ipcReady) {
         connectAndHandshake();
      }
   }
}
