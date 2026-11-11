package com.corz.client.utils;

import com.corz.client.mixins.AccessorClientConnection;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.OffThreadException;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.BundlePacket;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.KeepAliveC2SPacket;
import net.minecraft.network.packet.c2s.play.AcknowledgeReconfigurationC2SPacket;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.network.packet.s2c.common.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.play.EnterReconfigurationS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.text.Text;
import ai.auth.Module;
import ai.auth.ModuleRegistry;
import com.corz.client.utils.EntityDamageUtils;
import com.corz.client.utils.DisconnectMultiplayerHandler;
import com.corz.client.utils.InventoryPlayer;

public final class ConnectionTimeUtils {
   private static final long timeMillis = System.currentTimeMillis();
   private static final long connectionValue = System.nanoTime();
   private static volatile long lastClientboundTimeMs = -1L;

   static boolean canListenerPacket(ClientConnection clientConnection) {
      return clientConnection != null && clientConnection.getPacketListener() instanceof ClientPlayNetworkHandler;
   }

   public static boolean canPackets(Packet<?> packet, Predicate<Packet<?>> predicate) {
      if (packet instanceof BundlePacket bundlePacket) {
         for (Packet center : (Iterable<net.minecraft.network.packet.Packet>) (bundlePacket.getPackets())) {
            if (predicate.test(center)) {
               return true;
            }
         }

         return false;
      } else {
         return packet != null && predicate.test(packet);
      }
   }

   static Channel getCorzChannel(ClientConnection clientConnection) {
      return clientConnection == null ? null : ((AccessorClientConnection)clientConnection).corzChannel();
   }

   public static void update() {
      ConnectionTimeUtils.Outbound.updateConnectionClientQUEUE_LOCK();
      ConnectionTimeUtils.Inbound.updateConnectionClientQUEUE_LOCK();
   }

   public static void update2() {
      ConnectionTimeUtils.Outbound.update();
      ConnectionTimeUtils.Inbound.updateQUEUE_LOCK();
   }

   static long getTime(long connectionValue, long connectionValue2, long connectionValue3) {
      return connectionValue + (connectionValue3 - connectionValue2) / 1000000L;
   }

   public static long getTime() {
      return lastClientboundTimeMs;
   }

   static boolean canOpen(ClientConnection clientConnection) {
      return canClientboundSide(clientConnection) && clientConnection.isOpen() && canListenerPacket(clientConnection);
   }

   public static long getTimeTimeMillis() {
      return getTime(timeMillis, connectionValue, System.nanoTime());
   }

   static boolean canClientboundSide(ClientConnection clientConnection) {
      return clientConnection != null && clientConnection.getSide() == NetworkSide.CLIENTBOUND;
   }

   private ConnectionTimeUtils() {
   }

   static final class ClaimFences {
      private final Map<Object, long[]> claimFencesLongArrayMap = new IdentityHashMap<>();
      private long claimSequenceCounter;

      long getTime() {
         return this.claimSequenceCounter;
      }

      void handleTime(Object obj) {
         if (obj != null) {
            this.claimFencesLongArrayMap.computeIfAbsent(obj, obj2 -> new long[3])[0] = ++this.claimSequenceCounter;
         }
      }

      void handleTime(Object obj, long claimFencesValue) {
         if (obj != null) {
            long[] ifAbsent = this.claimFencesLongArrayMap.computeIfAbsent(obj, obj2 -> new long[3]);
            ifAbsent[1] = ++this.claimSequenceCounter;
            ifAbsent[2] = claimFencesValue;
         }
      }

      long getTime(Object obj, long claimFencesValue, long claimFencesBound) {
         long[] claimFencesValues = this.claimFencesLongArrayMap.get(obj);
         if (claimFencesValues == null) {
            return claimFencesBound;
         } else if (claimFencesValues[0] > claimFencesValue) {
            return Long.MIN_VALUE;
         } else {
            return claimFencesValues[1] > claimFencesValue ? Math.min(claimFencesBound, claimFencesValues[2]) : claimFencesBound;
         }
      }
   }

   public static final class Inbound {
      private static final Object QUEUE_LOCK = new Object();
      private static final DequeArray<Packet<?>> INBOUND_PACKET_QUEUE = new DequeArray<>(1000L);
      private static final AtomicBoolean ATOMIC_BOOLEAN = new AtomicBoolean();
      private static final ConnectionTimeUtils.ClaimFences FENCE_TRACKER = new ConnectionTimeUtils.ClaimFences();
      private static ClientConnection clientConnection;

      private static boolean canFlag(Packet<?> packet) {
         return packet instanceof PlayerPositionLookS2CPacket
            || packet instanceof EnterReconfigurationS2CPacket
            || packet instanceof DisconnectS2CPacket
            || packet instanceof GameJoinS2CPacket
            || packet instanceof PlayerRespawnS2CPacket;
      }

      public static void update() {
         handleAtomicEvent(true);
      }

      private static void handleAtomicEvent(boolean atomicEventFlag) {
         ClientConnection clientConnection2;
         synchronized (QUEUE_LOCK) {
            if (INBOUND_PACKET_QUEUE.isEmpty()) {
               return;
            }

            clientConnection2 = clientConnection;
         }

         Channel corzChannel = ConnectionTimeUtils.getCorzChannel(clientConnection2);
         if (corzChannel != null) {
            if (atomicEventFlag || ATOMIC_BOOLEAN.compareAndSet(false, true)) {
               try {
                  corzChannel.eventLoop().execute(() -> {
                     if (!atomicEventFlag) {
                        ATOMIC_BOOLEAN.set(false);
                     }

                     List var2x;
                     synchronized (QUEUE_LOCK) {
                        if (clientConnection != clientConnection2) {
                           return;
                        }

                        var2x = atomicEventFlag ? INBOUND_PACKET_QUEUE.getDequeArrayArrayDeque() : INBOUND_PACKET_QUEUE.getDequeArray(ConnectionTimeUtils.getTimeTimeMillis());
                        updateConnectionClient();
                     }

                     handleDisconnectMultiplayer(clientConnection2, var2x);
                  });
               } catch (RejectedExecutionException e) {
                  if (!atomicEventFlag) {
                     ATOMIC_BOOLEAN.set(false);
                  }
               }
            }
         }
      }

      private static void handleDisconnectMultiplayer(ClientConnection clientConnection, List<Packet<?>> list) {
         for (Packet packet : list) {
            if (!clientConnection.isOpen()) {
               return;
            }

            PacketListener packetListener = clientConnection.getPacketListener();
            if (packetListener instanceof ClientPlayNetworkHandler && packetListener.accepts(packet)) {
               try {
                  AccessorClientConnection.corzHandlePacket(packet, packetListener);
               } catch (OffThreadException e) {
               } catch (RejectedExecutionException e2) {
                  clientConnection.disconnect(Text.translatable("multiplayer.disconnect.server_shutdown"));
                  return;
               } catch (ClassCastException e3) {
                  clientConnection.disconnect(Text.translatable("multiplayer.disconnect.invalid_packet"));
                  return;
               }
            }
         }
      }

      public static void updateQUEUE_LOCK() {
         synchronized (QUEUE_LOCK) {
            updateConnectionClientINBOUND_PACKET_QUEUE();
         }
      }

      public static boolean canConnectionClient(ClientConnection clientConnection2, Packet<?> packet) {
         if (!ConnectionTimeUtils.canClientboundSide(clientConnection2)) {
            return false;
         } else {
            PacketListener packetListener = clientConnection2.getPacketListener();
            if (!(packetListener instanceof ClientPlayNetworkHandler)) {
               updateQUEUE_LOCK();
               return false;
            } else {
               long time = ConnectionTimeUtils.getTimeTimeMillis();
               EntityDamageUtils.handleSourceEntity(packet, time);
               long connectionClientValue;
               List list;
               synchronized (QUEUE_LOCK) {
                  connectionClientValue = FENCE_TRACKER.getTime();
                  if (clientConnection != clientConnection2) {
                     updateConnectionClientINBOUND_PACKET_QUEUE();
                  }

                  list = INBOUND_PACKET_QUEUE.isEmpty() ? List.of() : INBOUND_PACKET_QUEUE.getDequeArray(time);
                  updateConnectionClient();
               }

               handleDisconnectMultiplayer(clientConnection2, list);
               if (packet instanceof PlayerPositionLookS2CPacket) {
                  ConnectionTimeUtils.lastClientboundTimeMs = time;
               }

               if (!canFlag(packet) && !DirExtractionUtils.isFlag()) {
                  boolean connectionClientFlag;
                  synchronized (QUEUE_LOCK) {
                     connectionClientFlag = !INBOUND_PACKET_QUEUE.isEmpty();
                  }

                  ArrayList arrayList = null;
                  ArrayList arrayList2 = null;

                  for (Module iilliliilIIiItem : ModuleRegistry.getModules()) {
                     if (iilliliilIIiItem instanceof DisconnectMultiplayerHandler disconnectMultiplayerHandler && iilliliilIIiItem.isEnabled()) {
                        long connectionClientLimit;
                        try {
                           connectionClientLimit = disconnectMultiplayerHandler.getKnockbackWater(packet, connectionClientFlag, time);
                        } catch (RuntimeException e) {
                           continue;
                        }

                        if (connectionClientLimit > time) {
                           if (arrayList == null) {
                              arrayList = new ArrayList(2);
                              arrayList2 = new ArrayList(2);
                           }

                           arrayList.add(iilliliilIIiItem);
                           arrayList2.add(Math.min(connectionClientLimit, time + 1000L));
                        }
                     }
                  }

                  if (!connectionClientFlag && arrayList == null) {
                     return false;
                  } else {
                     synchronized (QUEUE_LOCK) {
                        if (clientConnection != clientConnection2) {
                           updateConnectionClientINBOUND_PACKET_QUEUE();
                        }

                        DequeArray.Entry entry = null;
                        if (arrayList != null) {
                           for (int i = 0; i < arrayList.size(); i++) {
                              Module Module = (Module)arrayList.get(i);
                              long entryBound = FENCE_TRACKER.getTime(Module, connectionClientValue, (Long)arrayList2.get(i));
                              if (entryBound > time && Module.isEnabled()) {
                                 if (entry == null) {
                                    entry = INBOUND_PACKET_QUEUE.getDequeArray(packet, time);
                                 }

                                 entry.handleForObjectLong(Module, entryBound);
                              }
                           }
                        }

                        if (entry == null) {
                           if (INBOUND_PACKET_QUEUE.isEmpty()) {
                              return false;
                           }

                           INBOUND_PACKET_QUEUE.getDequeArray(packet, time);
                        }

                        clientConnection = clientConnection2;
                        return true;
                     }
                  }
               } else {
                  List dequeArrayArrayDeque2;
                  synchronized (QUEUE_LOCK) {
                     dequeArrayArrayDeque2 = INBOUND_PACKET_QUEUE.getDequeArrayArrayDeque();
                     updateConnectionClient();
                  }

                  handleDisconnectMultiplayer(clientConnection2, dequeArrayArrayDeque2);
                  if (packet instanceof PlayerPositionLookS2CPacket || packet instanceof EnterReconfigurationS2CPacket || packet instanceof DisconnectS2CPacket) {
                     ConnectionTimeUtils.Outbound.update3();
                  }

                  return false;
               }
            }
         }
      }

      public static long getTime() {
         synchronized (QUEUE_LOCK) {
            return INBOUND_PACKET_QUEUE.getMax(ConnectionTimeUtils.getTimeTimeMillis());
         }
      }

      private static void updateConnectionClient() {
         if (INBOUND_PACKET_QUEUE.isEmpty()) {
            clientConnection = null;
         }
      }

      static void updateConnectionClientQUEUE_LOCK() {
         synchronized (QUEUE_LOCK) {
            if (INBOUND_PACKET_QUEUE.isEmpty()) {
               return;
            }

            if (!ConnectionTimeUtils.canOpen(clientConnection)) {
               updateConnectionClientINBOUND_PACKET_QUEUE();
               return;
            }
         }

         handleAtomicEvent(false);
      }

      public static void handle(Object obj) {
         synchronized (QUEUE_LOCK) {
            FENCE_TRACKER.handleTime(obj);
            INBOUND_PACKET_QUEUE.handleDequeArray(obj);
         }

         handleAtomicEvent(false);
      }

      private Inbound() {
      }

      private static void updateConnectionClientINBOUND_PACKET_QUEUE() {
         INBOUND_PACKET_QUEUE.updateDequeArray();
         clientConnection = null;
      }

      public static int getValue() {
         synchronized (QUEUE_LOCK) {
            return INBOUND_PACKET_QUEUE.getDequeArray();
         }
      }

      public static boolean isFlag() {
         synchronized (QUEUE_LOCK) {
            return !INBOUND_PACKET_QUEUE.isEmpty();
         }
      }

      public static boolean canFlag(Object obj) {
         synchronized (QUEUE_LOCK) {
            return INBOUND_PACKET_QUEUE.canDequeArray(obj);
         }
      }

      public static void handle(Object obj, long inboundValue) {
         synchronized (QUEUE_LOCK) {
            FENCE_TRACKER.handleTime(obj, inboundValue);
            INBOUND_PACKET_QUEUE.handleDequeArray(obj, inboundValue);
         }

         handleAtomicEvent(false);
      }

      public static void update3() {
         handleAtomicEvent(false);
      }
   }

   public static final class Outbound {
      private static final Object QUEUE_LOCK = new Object();
      private static ClientConnection clientConnection;
      private static final DequeArray<ConnectionTimeUtils.Outbound.Held> HELD_PACKET_QUEUE = new DequeArray<>(60000L);
      private static final ThreadLocal<Boolean> withInitial = ThreadLocal.withInitial(() -> Boolean.FALSE);
      private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger();

      public static int getValue() {
         synchronized (QUEUE_LOCK) {
            return HELD_PACKET_QUEUE.getDequeArray();
         }
      }

      private static void updateConnectionClient() {
         if (HELD_PACKET_QUEUE.isEmpty()) {
            clientConnection = null;
         }
      }

      public static void update() {
         synchronized (QUEUE_LOCK) {
            updateConnectionClientHELD_PACKET_QUEUE();
         }
      }

      private static void updateConnectionClientHELD_PACKET_QUEUE() {
         HELD_PACKET_QUEUE.updateDequeArray();
         clientConnection = null;
      }

      private static void handleConnectionClient(long connectionClientValue) {
         if (!HELD_PACKET_QUEUE.isEmpty()) {
            ClientConnection clientConnection2 = clientConnection;
            var dequeArray = HELD_PACKET_QUEUE.getDequeArray(connectionClientValue);
            updateConnectionClient();

            for (ConnectionTimeUtils.Outbound.Held held : dequeArray) {
               handleAtomicEvent(clientConnection2, held);
            }
         }
      }

      public static void updateQUEUE_LOCK() {
         synchronized (QUEUE_LOCK) {
            handleConnectionClient(ConnectionTimeUtils.getTimeTimeMillis());
         }
      }

      private Outbound() {
      }

      public static void handlePacket(InventoryPlayer inventoryPlayer) {
         if (inventoryPlayer.isInventoryPlayerActive() && inventoryPlayer.isOpenedByClient()) {
            var packet = inventoryPlayer.packet;
            synchronized (QUEUE_LOCK) {
               DequeArray.Entry next = HELD_PACKET_QUEUE.getNext(held -> held.packet() == packet);
               if (next != null) {
                  HELD_PACKET_QUEUE.isLastOccurrence(next);
               }

               updateConnectionClient();
            }
         }
      }

      private static void handleAtomicEvent(ClientConnection clientConnection, ConnectionTimeUtils.Outbound.Held held) {
         Channel corzChannel = ConnectionTimeUtils.getCorzChannel(clientConnection);
         if (corzChannel != null && clientConnection.isOpen()) {
            ATOMIC_INTEGER.incrementAndGet();

            try {
               corzChannel.eventLoop().execute(() -> {
                  try {
                     withInitial.set(Boolean.TRUE);

                     try {
                        clientConnection.send(held.packet(), held.listener(), held.flush());
                     } finally {
                        withInitial.set(Boolean.FALSE);
                     }
                  } finally {
                     ATOMIC_INTEGER.decrementAndGet();
                  }
               });
            } catch (RejectedExecutionException e) {
               ATOMIC_INTEGER.decrementAndGet();
            }
         }
      }

      public static long getTime() {
         synchronized (QUEUE_LOCK) {
            return HELD_PACKET_QUEUE.getMax(ConnectionTimeUtils.getTimeTimeMillis());
         }
      }

      public static boolean canConnectionSend(Packet<?> packet) {
         ClientPlayNetworkHandler networkHandler = MinecraftClient.getInstance().getNetworkHandler();
         if (packet != null && networkHandler != null) {
            ClientConnection connection = networkHandler.getConnection();
            if (!ConnectionTimeUtils.canOpen(connection)) {
               return false;
            } else {
               connection.send(packet, null, true);
               return true;
            }
         } else {
            return false;
         }
      }

      public static void handle(Object obj) {
         synchronized (QUEUE_LOCK) {
            HELD_PACKET_QUEUE.handleDequeArray(obj);
            handleConnectionClient(ConnectionTimeUtils.getTimeTimeMillis());
         }
      }

      private static void handleConnectionClient(ClientConnection clientConnection2) {
         if (clientConnection != clientConnection2) {
            HELD_PACKET_QUEUE.updateDequeArray();
            clientConnection = clientConnection2;
         }
      }

      public static void update3() {
         synchronized (QUEUE_LOCK) {
            updateConnectionClient4();
         }
      }

      public static boolean canEventLoop(ClientConnection clientConnection2, Packet<?> packet, ChannelFutureListener channelFutureListener, boolean eventLoopFlag) {
         if (!withInitial.get() && ConnectionTimeUtils.canClientboundSide(clientConnection2)) {
            Channel corzChannel = ConnectionTimeUtils.getCorzChannel(clientConnection2);
            boolean eventLoopFlag2 = corzChannel != null && corzChannel.eventLoop().inEventLoop();
            boolean isKeepAliveC2SPacket = packet instanceof KeepAliveC2SPacket;
            synchronized (QUEUE_LOCK) {
               if (clientConnection != clientConnection2 && !HELD_PACKET_QUEUE.isEmpty()) {
                  updateConnectionClientHELD_PACKET_QUEUE();
               }

               if (DirExtractionUtils.isFlag() || packet instanceof TeleportConfirmC2SPacket || packet instanceof AcknowledgeReconfigurationC2SPacket) {
                  updateConnectionClient4();
               } else if (!isKeepAliveC2SPacket && !HELD_PACKET_QUEUE.isEmpty()) {
                  if (!ConnectionTimeUtils.canListenerPacket(clientConnection2)) {
                     updateConnectionClientHELD_PACKET_QUEUE();
                  } else {
                     long eventLoopValue = ConnectionTimeUtils.getTimeTimeMillis();
                     handleConnectionClient(eventLoopValue);
                     if (!HELD_PACKET_QUEUE.isEmpty()) {
                        HELD_PACKET_QUEUE.getDequeArray(new ConnectionTimeUtils.Outbound.Held(packet, channelFutureListener, eventLoopFlag), eventLoopValue);
                        return true;
                     }
                  }
               }

               if (eventLoopFlag2 && !isKeepAliveC2SPacket && ATOMIC_INTEGER.get() > 0) {
                  handleAtomicEvent(clientConnection2, new ConnectionTimeUtils.Outbound.Held(packet, channelFutureListener, eventLoopFlag));
                  return true;
               } else {
                  return false;
               }
            }
         } else {
            return false;
         }
      }

      public static void handle(Object obj, long outboundValue) {
         synchronized (QUEUE_LOCK) {
            HELD_PACKET_QUEUE.handleDequeArray(obj, outboundValue);
            handleConnectionClient(ConnectionTimeUtils.getTimeTimeMillis());
         }
      }

      public static boolean canPacket(Object obj, InventoryPlayer inventoryPlayer, long packetValue) {
         if (obj == null || inventoryPlayer == null || inventoryPlayer.packet == null || inventoryPlayer.isOpenedByClient()) {
            return false;
         } else if (DirExtractionUtils.isFlag()) {
            return false;
         } else {
            ClientConnection clientConnection = inventoryPlayer.getClientConnection();
            if (!ConnectionTimeUtils.canOpen(clientConnection)) {
               return false;
            } else {
               var packet = inventoryPlayer.packet;
               if (!(packet instanceof KeepAliveC2SPacket)
                  && !(packet instanceof TeleportConfirmC2SPacket)
                  && !(packet instanceof AcknowledgeReconfigurationC2SPacket)) {
                  synchronized (QUEUE_LOCK) {
                     if (obj instanceof Module ilIllIiIll2 && !ilIllIiIll2.isEnabled()) {
                        return false;
                     } else if (inventoryPlayer.isInventoryPlayerActive()) {
                        DequeArray.Entry next = HELD_PACKET_QUEUE.getNext(held -> held.packet() == packet);
                        if (next == null) {
                           return false;
                        } else {
                           next.handleForObjectLong(obj, packetValue);
                           return true;
                        }
                     } else {
                        handleConnectionClient(clientConnection);
                        HELD_PACKET_QUEUE.getDequeArray(new ConnectionTimeUtils.Outbound.Held(packet, null, true), ConnectionTimeUtils.getTimeTimeMillis()).handleForObjectLong(obj, packetValue);
                        inventoryPlayer.updateFlagInventoryPlayerActive();
                        return true;
                     }
                  }
               } else {
                  return false;
               }
            }
         }
      }

      public static boolean isFlag() {
         synchronized (QUEUE_LOCK) {
            return !HELD_PACKET_QUEUE.isEmpty();
         }
      }

      static void updateConnectionClientQUEUE_LOCK() {
         synchronized (QUEUE_LOCK) {
            if (!HELD_PACKET_QUEUE.isEmpty()) {
               if (!ConnectionTimeUtils.canOpen(clientConnection)) {
                  updateConnectionClientHELD_PACKET_QUEUE();
               } else {
                  handleConnectionClient(ConnectionTimeUtils.getTimeTimeMillis());
               }
            }
         }
      }

      private static void updateConnectionClient4() {
         if (!HELD_PACKET_QUEUE.isEmpty()) {
            ClientConnection clientConnection2 = clientConnection;
            var dequeArray = HELD_PACKET_QUEUE.getDequeArrayArrayDeque();
            updateConnectionClient();

            for (ConnectionTimeUtils.Outbound.Held held : dequeArray) {
               handleAtomicEvent(clientConnection2, held);
            }
         }
      }

      public static boolean canFlag(Object obj) {
         synchronized (QUEUE_LOCK) {
            return HELD_PACKET_QUEUE.canDequeArray(obj);
         }
      }

      private record Held(Packet<?> packet, ChannelFutureListener listener, boolean flush) {
      }
   }
}
