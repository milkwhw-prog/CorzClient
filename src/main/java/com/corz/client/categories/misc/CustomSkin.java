package com.corz.client.categories.misc;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.EnumSetting;
import com.corz.client.gui.ButtonBase;
import com.corz.client.gui.RotateQuaternionfApi;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import com.corz.client.utils.BrainrotTurnUtils;
import com.corz.client.utils.QuaternionfRotateQuaternionf;
import com.corz.client.utils.RenderMatrixUtilsWarm;
import com.corz.client.utils.WarmBrainrotBase;
import com.corz.client.utils.InventoryPlayer;
import com.corz.client.utils.PlayerCrystalUtils;
import ai.auth.Module;
import ai.auth.ModuleRegistry;
import com.corz.client.utils.IconsPngType;

public class CustomSkin extends Module implements RotateQuaternionfApi {
   private final EnumSetting enumHelper = new EnumSetting(
      "Brainrot", "Which brainrot you turn into", BrainrotTurnUtils.BRAINROT_TURN_ITEMS.get(0).formatTungDinString(), BrainrotTurnUtils.getBrainrotTurn()
   );
   private final ButtonBase buttonBase = new ButtonBase("Shuffle", "Turn into a random brainrot", "Shuffle", this::updateBrainrotTurn);
   private final BooleanSetting booleanBase;
   private volatile Set<Integer> brainrotYouSet;
   private final QuaternionfRotateQuaternionf quaternionfRotateQuaternionf;
   private final AtomicInteger atomicInteger;
   private int updateCooldownTicks;
   public static int scaleValue = -1;
   private int iIiCount;
   private final BooleanSetting brainrotEveryone = new BooleanSetting(
      "Brainrot Everyone", "Other players turn into random brainrots too (only you see it; their armor and held items are hidden)", false
   );

   @Override
   public void renderQuaternionfRotate(DrawContext context, int quaternionfRotateValue, int editValue, int releaseRightLimit, int n4) {
      this.quaternionfRotateQuaternionf.renderAmountScale(context, quaternionfRotateValue, editValue, releaseRightLimit, n4, this.getFloorMod());
   }

   @Override
   public void updateQuaternionfRotate() {
      this.quaternionfRotateQuaternionf.updateFlag();
   }

   public WarmBrainrotBase getBrainrotTurn() {
      return BrainrotTurnUtils.BRAINROT_TURN_ITEMS.get(this.getFloorMod());
   }

   private void updateBrainrotTurn() {
      int brainrot_turn_itemsSize = BrainrotTurnUtils.BRAINROT_TURN_ITEMS.size();
      if (brainrot_turn_itemsSize > 1) {
         int randomInt = ThreadLocalRandom.current().nextInt(brainrot_turn_itemsSize - 1);
         if (randomInt >= this.getFloorMod()) {
            randomInt++;
         }

         this.enumHelper.handle(randomInt);
      }
   }

   public int getFloorMod() {
      return Math.floorMod(this.enumHelper.getCurrentIndex(), BrainrotTurnUtils.BRAINROT_TURN_ITEMS.size());
   }

   @Override
   public void handleQuaternionfRotate(double quaternionfRotateValue, double quaternionfRotateValue2) {
      this.quaternionfRotateQuaternionf.handleAmountFlag(quaternionfRotateValue, quaternionfRotateValue2);
   }

   @Override
   protected void onEnable() {
      RenderMatrixUtilsWarm.updateCustomSkin();
      this.getBrainrotTurn().updateThrowableThread();
   }

   private void updateBrainrotYou() {
      if (this.brainrotEveryone.isEnabled() && mc.world != null && mc.player != null) {
         var arrayList = new ArrayList();

         for (AbstractClientPlayerEntity abstractClientPlayerEntity : mc.world.getPlayers()) {
            if (abstractClientPlayerEntity != mc.player) {
               arrayList.add(abstractClientPlayerEntity);
            }
         }

         arrayList.sort(Comparator.comparingDouble(obj -> ((net.minecraft.client.network.AbstractClientPlayerEntity) obj).squaredDistanceTo(mc.player)));
         var brainrotYouSet = new HashSet();

         for (int i = 0; i < Math.min(16, arrayList.size()); i++) {
            AbstractClientPlayerEntity abstractClientPlayerEntity2 = (AbstractClientPlayerEntity)arrayList.get(i);
            brainrotYouSet.add(abstractClientPlayerEntity2.getId());
            BrainrotTurnUtils.BRAINROT_TURN_ITEMS
               .get(Math.floorMod(abstractClientPlayerEntity2.getUuid().hashCode(), BrainrotTurnUtils.BRAINROT_TURN_ITEMS.size()))
               .updateThrowableThread();
         }

         this.brainrotYouSet = brainrotYouSet;
      } else {
         this.brainrotYouSet = Set.of();
      }
   }

   public CustomSkin() {
      super("CustomSkin", IconsPngType.MISC);
      this.booleanBase = new BooleanSetting("Funny Sounds", "Plays your brainrot's chant when you hit something (only you hear it)", true);
      this.quaternionfRotateQuaternionf = new QuaternionfRotateQuaternionf();
      this.atomicInteger = new AtomicInteger();
      this.brainrotYouSet = Set.of();
      this.addSettings(new ConfigSetting[]{this.enumHelper, this.buttonBase, this.brainrotEveryone, this.booleanBase});
   }

   @Override
   protected void onReset() {
      this.getBrainrotTurn().updateThrowableThread();
      if (this.updateCooldownTicks-- <= 0) {
         this.updateCooldownTicks = 4;
         this.updateBrainrotYou();
      }

      if (this.atomicInteger.get() > 0) {
         if (this.iIiCount > 0) {
            this.iIiCount--;
         } else {
            this.atomicInteger.decrementAndGet();
            this.iIiCount = 3;
            WarmBrainrotBase brainrotTurn = this.getBrainrotTurn();
            String formatNoteBlock = brainrotTurn.formatNoteBlock();
            if (formatNoteBlock != null && mc.player != null) {
               try {
                  mc.getSoundManager().play(PositionedSoundInstance.ui(SoundEvent.of(Identifier.ofVanilla(formatNoteBlock)), brainrotTurn.getAmountFloat(), 0.7F));
               } catch (Throwable e) {
                  this.atomicInteger.set(0);
               }
            } else {
               this.atomicInteger.set(0);
            }
         }
      }
   }

   @Override
   protected void onDisable() {
      this.atomicInteger.set(0);
      this.brainrotYouSet = Set.of();
   }

   public static WarmBrainrotBase getBrainrotTurn(AbstractClientPlayerEntity abstractClientPlayerEntity) {
      if (abstractClientPlayerEntity != null && !BrainrotTurnUtils.BRAINROT_TURN_ITEMS.isEmpty()) {
         if (ModuleRegistry.getModule(CustomSkin.class) instanceof CustomSkin customSkin) {
            boolean brainrotTurnFlag = abstractClientPlayerEntity == MinecraftClient.getInstance().player;
            if (brainrotTurnFlag && scaleValue >= 0) {
               return BrainrotTurnUtils.BRAINROT_TURN_ITEMS.get(Math.floorMod(scaleValue, BrainrotTurnUtils.BRAINROT_TURN_ITEMS.size()));
            } else if (brainrotTurnFlag && scaleValue == -2) {
               return null;
            } else if (!customSkin.isEnabled()) {
               return null;
            } else if (brainrotTurnFlag) {
               return customSkin.getBrainrotTurn();
            } else {
               return customSkin.brainrotEveryone.isEnabled() && customSkin.brainrotYouSet.contains(abstractClientPlayerEntity.getId())
                  ? BrainrotTurnUtils.BRAINROT_TURN_ITEMS.get(Math.floorMod(abstractClientPlayerEntity.getUuid().hashCode(), BrainrotTurnUtils.BRAINROT_TURN_ITEMS.size()))
                  : null;
            }
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   @Override
   protected void onPacketSend(InventoryPlayer inventoryPlayer) {
      if (this.booleanBase.isEnabled()) {
         if (inventoryPlayer.packet instanceof PlayerInteractEntityC2SPacket playerInteractEntityC2SPacke && PlayerCrystalUtils.canHandleFlag(playerInteractEntityC2SPacke)) {
            if (this.getBrainrotTurn().formatNoteBlock() == null) {
               return;
            }

            if (this.atomicInteger.get() == 0) {
               this.atomicInteger.set(Math.max(1, this.getBrainrotTurn().getValue()));
            }
         }
      }
   }

   @Override
   public boolean canQuaternionfRotate(double quaternionfRotateValue, double quaternionfRotateValue2, int quaternionfRotateValue3, int quaternionfRotateValue4, int quaternionfRotateValue5, int quaternionfRotateValue6, int quaternionfRotateValue7) {
      return this.quaternionfRotateQuaternionf.canAmountFlag(quaternionfRotateValue, quaternionfRotateValue2, quaternionfRotateValue3, quaternionfRotateValue4, quaternionfRotateValue5, quaternionfRotateValue6, quaternionfRotateValue7);
   }
}
