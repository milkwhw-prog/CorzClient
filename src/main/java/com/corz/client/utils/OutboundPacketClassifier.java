package com.corz.client.utils;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.network.packet.c2s.play.ClientTickEndC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.PlayerInput;
import com.corz.client.utils.PlayerCrystalUtils;
import com.corz.client.utils.OutboundPacketKind;

public final class OutboundPacketClassifier {
   public static OutboundPacketKind classifyPacket(Packet<?> packet) {
      if (packet == null) {
         return OutboundPacketKind.OTHER;
      } else if (packet instanceof PlayerMoveC2SPacket playerMoveC2SPacket) {
         return playerMoveC2SPacket.changesLook() ? OutboundPacketKind.MOVE_LOOK : OutboundPacketKind.MOVE;
      } else if (packet instanceof ClientTickEndC2SPacket) {
         return OutboundPacketKind.TICK_END;
      } else if (packet instanceof PlayerInputC2SPacket) {
         return OutboundPacketKind.INPUT;
      } else if (packet instanceof PlayerInteractEntityC2SPacket playerInteractEntityC2SPacke) {
         return PlayerCrystalUtils.canHandleFlag(playerInteractEntityC2SPacke) ? OutboundPacketKind.ATTACK : OutboundPacketKind.INTERACT_ENTITY;
      } else if (packet instanceof PlayerInteractBlockC2SPacket) {
         return OutboundPacketKind.INTERACT_BLOCK;
      } else if (packet instanceof PlayerInteractItemC2SPacket) {
         return OutboundPacketKind.USE_ITEM;
      } else if (packet instanceof PlayerActionC2SPacket playerActionC2SPacket) {
         return classifyPlayerAction(playerActionC2SPacket.getAction());
      } else if (packet instanceof UpdateSelectedSlotC2SPacket) {
         return OutboundPacketKind.SLOT;
      } else if (packet instanceof HandSwingC2SPacket) {
         return OutboundPacketKind.SWING;
      } else if (packet instanceof ClientCommandC2SPacket clientCommandC2SPacket) {
         return classifyClientCommand(clientCommandC2SPacket.getMode());
      } else if (packet instanceof ClickSlotC2SPacket) {
         return OutboundPacketKind.CLICK_SLOT;
      } else if (packet instanceof CloseHandledScreenC2SPacket) {
         return OutboundPacketKind.CLOSE_SCREEN;
      } else {
         return packet instanceof TeleportConfirmC2SPacket ? OutboundPacketKind.TELEPORT_CONFIRM : OutboundPacketKind.OTHER;
      }
   }

   private OutboundPacketClassifier() {
   }

   public static float[] getMoveLookAngles(Packet<?> packet) {
      return packet instanceof PlayerMoveC2SPacket playerMoveC2SPacket && playerMoveC2SPacket.changesLook() ? new float[]{playerMoveC2SPacket.getYaw(0.0F), playerMoveC2SPacket.getPitch(0.0F)} : null;
   }

   public static int getSelectedSlot(Packet<?> packet) {
      return packet instanceof UpdateSelectedSlotC2SPacket updateSelectedSlotC2SPacket ? updateSelectedSlotC2SPacket.getSelectedSlot() : -1;
   }

   private static OutboundPacketKind classifyClientCommand(Mode mode) {
      if (mode == Mode.START_SPRINTING) {
         return OutboundPacketKind.SPRINT_START;
      } else {
         return mode == Mode.STOP_SPRINTING ? OutboundPacketKind.SPRINT_STOP : OutboundPacketKind.OTHER;
      }
   }

   private static OutboundPacketKind classifyPlayerAction(Action action) {
      if (action == null) {
         return OutboundPacketKind.OTHER;
      } else {
         return switch (action) {
            case SWAP_ITEM_WITH_OFFHAND -> OutboundPacketKind.SWAP_OFFHAND;
            case RELEASE_USE_ITEM -> OutboundPacketKind.RELEASE_USE;
            case DROP_ITEM, DROP_ALL_ITEMS -> OutboundPacketKind.DROP;
            case START_DESTROY_BLOCK, STOP_DESTROY_BLOCK, ABORT_DESTROY_BLOCK -> OutboundPacketKind.DIG;
            case STAB -> OutboundPacketKind.ATTACK;
            default -> OutboundPacketKind.OTHER;
         };
      }
   }

   public static PlayerInput getInput(Packet<?> packet) {
      return packet instanceof PlayerInputC2SPacket playerInputC2SPacket ? playerInputC2SPacket.input() : null;
   }

   public static float[] getUseItemYawPitch(Packet<?> packet) {
      return packet instanceof PlayerInteractItemC2SPacket playerInteractItemC2SPacket ? new float[]{playerInteractItemC2SPacket.getYaw(), playerInteractItemC2SPacket.getPitch()} : null;
   }
}
