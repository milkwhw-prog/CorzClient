package com.corz.client.categories.visuals;

import com.corz.client.CorzClient;
import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.DoubleSetting;
import com.corz.client.config.EnumSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.config.FriendNameStore;
import com.corz.client.gui.TickProgress;
import com.corz.client.render.IcoGenUtils;
import com.corz.client.render.MatrixEspUtils;
import com.corz.client.render.MatrixPeekUtilsIdentifier;
import com.corz.client.render.VertexConsumerUtilsVector;
import com.corz.client.render.VertexTextureFont;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.awt.Color;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import org.joml.Matrix3x2fStack;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import com.corz.client.categories.misc.NameProtect;
import com.corz.client.utils.AutoVanish;
import ai.auth.Module;
import com.corz.client.utils.IconsPngType;

public class NameTags extends Module {
   private final int[] vectorWindowTable;
   private float jitterOffset;
   private final ConcurrentLinkedQueue<EntityStatusS2CPacket> concurrentLinkedQueue;
   private static final Color[] colors;
   private final BooleanSetting booleanBase;
   private final Matrix4f matrixFontMatrix4f;
   private static final int UNUSED_ONE = 1;
   private final BooleanSetting showRankTag;
   private final BooleanSetting throughWalls = new BooleanSetting("Through Walls", "Show tags for players hidden behind blocks", true);
   private static final String[] luckSeaTexts;
   private final BooleanSetting showFriendMarker;
   private final EnumSetting enumHelper;
   private final DoubleSetting doubleHelper;
   private final Map<Object, String[]> luckSeaStringArrayMap;
   private final EnumSetting healthBarPosition;
   private float matrixFontAmount;
   private boolean matrixFontActive;
   private final BooleanSetting showArmor;
   private static final String[] signalTexts;
   private final BooleanSetting showSelfInThirdPerson;
   private final BooleanSetting ignoreNpcs;
   private long timeMillis;
   private final BooleanSetting showEnchantments;
   public static final String TOP = "Top";
   private final BooleanSetting showPops;
   public static final String LEFT = "Left";
   private boolean effectDurationActive;
   private final BooleanSetting showDurability;
   private static final int[] colorsTable;
   public static final String RIGHT = "Right";
   private final List<NameTags.Tag> nameTagsTagList;
   public static final Matrix4f MATRIX4F = new Matrix4f();
   private final MatrixStack matrixStack;
   private static final Map<String, Integer> LUCK_SEA_MAP = new HashMap<>();
   public static final Matrix4f MATRIX4_FMATRIX4F = new Matrix4f();
   private final IntegerSetting integerHelper = new IntegerSetting("Max Distance", "Only draw tags within this many blocks", 96, 8, 256);
   private static final Map<String, String> CURSE_PROTECTION_MAP = new HashMap<>();
   private float vectorWindowAmount;
   private final Int2ObjectOpenHashMap<String> int2ObjectOpenHashMap;
   private final Map<UUID, NameTags.Pops> barHealthMap;
   private ItemStack itemStack;
   private final BooleanSetting ignoreBots;
   private final List<NameTags.Tag> matrixFontTagList;
   private final NameTags.Tag tag;
   private final Vector3f vectorWindowVector3f;
   private final ArrayList<Object[]> luckSeaObjectArrayList;
   private final BooleanSetting showPing;
   private float signalAmount;
   public static final Matrix4f MATRIX4F3 = new Matrix4f();
   private final MatrixStack matrixFontMatrixStack;
   private final BooleanSetting showDistance;
   private final BooleanSetting showHeldItems;
   private static final String[] sidePositionTexts = new String[]{"Top", "Bottom", "Left", "Right"};
   private final Map<UUID, NameTags.Anim> barSideMap;
   private final EnumSetting setPosition;
   private WeakReference<Object> weakReference;
   private final BooleanSetting showHealth = new BooleanSetting("Show Health", "Health bar next to the player", true);
   private static final String[] damageTexts;
   private long nameTagsValue;
   private final EnumSetting namePosition;
   public static final String BOTTOM = "Bottom";

   private float getSignalText(NameTags.Tag tag, VertexTextureFont vertexTextureFont) {
      NameTags.Anim ifAbsent = tag.ifAbsent;
      float textAbsent = this.getTextAbsent(tag, vertexTextureFont);
      float signalValue = vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont(ifAbsent.animText) * 0.8F;
      float signalLimit = ifAbsent.deltaText.isEmpty() ? 0.0F : vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont(ifAbsent.deltaText) * 0.8F;
      float signalLimit2 = ifAbsent.cachedSecondaryText.isEmpty() ? 0.0F : ifAbsent.textWidth + 6.0F;
      float signalLimit3 = ifAbsent.signalAmount > 0.0F ? (tag.signal >= 0 ? 8.5F : 0.0F) + ifAbsent.signalAmount : 0.0F;
      return 10.0F
         + (textAbsent > 0.0F ? textAbsent + 5.0F : 0.0F)
         + (tag.tagActive ? 7.0F : 0.0F)
         + ifAbsent.mainTextWidth
         + (signalLimit2 > 0.0F ? 5.0F + signalLimit2 : 0.0F)
         + (signalLimit3 > 0.0F ? 5.0F + signalLimit3 : 0.0F)
         + 5.0F
         + signalValue
         + (signalLimit > 0.0F ? 1.5F + signalLimit : 0.0F)
         + 1.5F
         + 7.0F;
   }

   private void renderEffectDuration(NameTags.Tag tag, MatrixStack matrices, DrawContext context, VertexTextureFont vertexTextureFont, boolean effectDurationFlag) {
      float effectDurationValue = tag.stacksTable[1];
      float effectDurationValue2 = tag.effectDurationTable[1];
      float effectDurationAmount = tag.effectDurationAmount;
      boolean effectDurationFlag2 = tag.effectDurationFlags[1];
      this.handle(matrices, effectDurationValue, effectDurationValue2, tag.signalValue, effectDurationAmount);
      float effectDurationValue3 = Math.max(8.0F, tag.textScale * 0.8F) + 2.0F;
      float effectDurationTotal = 5.0F;
      float effectDurationTotal2 = 3.0F;

      for (int i = 0; i < tag.effectDurationCount; i++) {
         StatusEffectInstance statusEffectInstance = tag.statusEffectInstances[i];
         if (effectDurationFlag) {
            Identifier identifier = null;

            try {
               identifier = InGameHud.getEffectTexture(statusEffectInstance.getEffectType());
            } catch (Throwable e) {
            }

            if (identifier != null) {
               context.drawGuiTexture(
                  RenderPipelines.GUI_TEXTURED, identifier, Math.round(effectDurationTotal), Math.round(effectDurationTotal2 + (effectDurationValue3 - 8.0F) / 2.0F), 8, 8, getColors(16777215, effectDurationAmount).getRGB()
               );
               this.effectDurationActive = true;
            }
         }

         boolean effectDurationFlag3 = statusEffectInstance.getDuration() >= 0 && statusEffectInstance.getDuration() < 200 && !statusEffectInstance.isInfinite();
         handlePopTranslate(
            vertexTextureFont, matrices, tag.effectDurationTexts[i], effectDurationTotal + 8.0F + 2.0F, this.getAmount(effectDurationTotal2, effectDurationValue3, 0.8F), 0.8F, getColors(effectDurationFlag3 ? 14699339 : 13225430, effectDurationAmount)
         );
         if (effectDurationFlag2) {
            effectDurationTotal2 += effectDurationValue3;
         } else {
            effectDurationTotal += 10.0F + tag.signalTable[i] + 6.0F;
         }
      }
   }

   private float getInteger(float integerValue) {
      float integerFactor = getAmountForFloat(integerValue / this.integerHelper.getValue(), 0.0F, 1.0F);
      return 1.0F + -0.27999997F * integerFactor;
   }

   private static float getAmount(float nameTagsValue, float nameTagsValue2, float nameTagsFactor, float nameTagsFactor2) {
      if (nameTagsFactor <= 0.0F) {
         return nameTagsValue;
      } else {
         float min3 = Math.min(1.0F, nameTagsFactor * nameTagsFactor2);
         return nameTagsValue + (nameTagsValue2 - nameTagsValue) * min3;
      }
   }

   private static boolean canUuid(PlayerEntity playerEntity) {
      return playerEntity.getUuid().version() == 2;
   }

   @Override
   protected void onPacketReceive(AutoVanish autoVanish) {
      if (autoVanish.packet instanceof EntityStatusS2CPacket entityStatusS2CPacket && this.showPops.isEnabled()) {
         byte status = entityStatusS2CPacket.getStatus();
         if (status == 35 || status == 3) {
            if (this.concurrentLinkedQueue.size() >= 512) {
               this.concurrentLinkedQueue.poll();
            }

            this.concurrentLinkedQueue.add(entityStatusS2CPacket);
         }
      }
   }

   private boolean canAmountsFlags(int nameTagsValue, NameTags.Tag tag, int index2) {
      float nameTagsValue2 = tag.matricesTable[index2];
      float nameTagsValue3 = tag.table[index2];
      float nameTagsLimit = nameTagsValue2 + tag.tagTable[index2];
      float nameTagsLimit2 = nameTagsValue3 + tag.tagHeights[index2];

      for (int i = nameTagsValue + 1; i < this.matrixFontTagList.size(); i++) {
         NameTags.Tag tag2 = this.matrixFontTagList.get(i);

         for (int j = 0; j < 3; j++) {
            if (tag2.signalFlags[j]) {
               float nameTagsValue4 = tag2.matricesTable[j];
               float nameTagsValue5 = tag2.table[j];
               if (nameTagsValue4 < nameTagsLimit && nameTagsValue4 + tag2.tagTable[j] > nameTagsValue2 && nameTagsValue5 < nameTagsLimit2 && nameTagsValue5 + tag2.tagHeights[j] > nameTagsValue3) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   private static void handlePopTranslate(VertexTextureFont vertexTextureFont, MatrixStack matrices, String translateText, float x2, float y2, float x3, Color color) {
      if (translateText != null && !translateText.isEmpty()) {
         if (Math.abs(x3 - 1.0F) < 0.001F) {
            vertexTextureFont.handleForStringFloat(translateText, x2, y2, color);
         } else {
            matrices.push();

            try {
               matrices.translate(x2, y2, 0.0F);
               matrices.scale(x3, x3, 1.0F);
               vertexTextureFont.handleForStringFloat(translateText, 0.0F, 0.0F, color);
            } finally {
               matrices.pop();
            }
         }
      }
   }

   private static int getBottomLeft(EnumSetting enumHelper) {
      String formatText = enumHelper.getCurrentOption();

      return switch (formatText) {
         case "Bottom" -> 1;
         case "Left" -> 2;
         case "Right" -> 3;
         default -> 0;
      };
   }

   private String formatDamageMax(ItemStack stack2) {
      if (this.showDurability.isEnabled() && stack2.isDamaged() && stack2.getMaxDamage() > 0) {
         int damageBound = Math.round(100.0F * (stack2.getMaxDamage() - stack2.getDamage()) / stack2.getMaxDamage());
         return damageTexts[Math.max(0, Math.min(100, damageBound))];
      } else {
         return "";
      }
   }

   private void handleStacksEmpty(NameTags.Tag tag, ItemStack stack2) {
      if (stack2 != null && !stack2.isEmpty() && tag.signalCount < 6) {
         tag.itemStacks[tag.signalCount++] = stack2;
      }
   }

   private void handle(MatrixStack matrices, float nameTagsValue, float nameTagsValue2, int colorsValue, float effectDurationAmount) {
      VertexConsumerUtilsVector.update4();

      try {
         VertexConsumerUtilsVector.handle(matrices, getColors(0, 0.3F * effectDurationAmount), -1.5, -1.0, nameTagsValue + 1.5F, nameTagsValue2 + 2.0F, 5.5, 12.0);
         VertexConsumerUtilsVector.handle(matrices, getColors(colorsValue, 0.2F * effectDurationAmount), -1.0, -1.0, nameTagsValue + 1.0F, nameTagsValue2 + 1.0F, 5.0, 12.0);
         VertexConsumerUtilsVector.handle(matrices, getColors(658190, 0.86F * effectDurationAmount), 0.0, 0.0, nameTagsValue, nameTagsValue2, 4.0, 12.0);
      } finally {
         VertexConsumerUtilsVector.update3();
      }
   }

   private boolean canSignalAmount(NameTags.Tag tag, PlayerEntity next, float signalValue, float nameTagsFactor) {
      UUID uuid = next.getUuid();
      NameTags.Anim ifAbsent = this.barSideMap.computeIfAbsent(uuid, uuid2 -> new NameTags.Anim());
      ifAbsent.animActive = true;
      tag.next = next;
      tag.ifAbsent = ifAbsent;
      float distanceTo = mc.player.distanceTo(next);
      tag.distanceTo = distanceTo;
      boolean signalFlag = distanceTo <= this.integerHelper.getValue();
      double vectorWindowValue = getAmount(next.lastX, next.getX(), signalValue);
      double signalValue4 = getAmount(next.lastY, next.getY(), signalValue) + next.getHeight() + 0.5;
      double signalValue5 = getAmount(next.lastZ, next.getZ(), signalValue);
      boolean vectorWindow = this.canVectorWindow(vectorWindowValue, signalValue4, signalValue5);
      float max = this.signalAmount;
      float vectorWindowAmount2 = this.vectorWindowAmount;
      boolean cameraPos2 = false;
      if (signalFlag && vectorWindow && !this.throughWalls.isEnabled()) {
         long time = mc.world.getTime();
         if (ifAbsent.signalValue != time) {
            ifAbsent.signalValue = time;
            ifAbsent.cameraPos = this.canCameraPos(next);
         }

         cameraPos2 = ifAbsent.cameraPos;
      }

      float maxMaxHealth = Math.max(1.0F, next.getMaxHealth());
      float amount = getAmountForFloat(next.getHealth(), 0.0F, maxMaxHealth);
      if (!ifAbsent.signalActive) {
         ifAbsent.signalActive = true;
         ifAbsent.fadeProgress = 0.0F;
         ifAbsent.animAmount = amount;
      }

      boolean signalFlag3 = signalFlag && !cameraPos2 && vectorWindow;
      float playerScaled = nameTagsFactor / 0.18F;
      ifAbsent.fadeProgress = signalFlag3 ? Math.min(1.0F, ifAbsent.fadeProgress + playerScaled) : Math.max(0.0F, ifAbsent.fadeProgress - playerScaled);
      ifAbsent.animAmount = getAmount(ifAbsent.animAmount, amount, nameTagsFactor, 10.0F);
      if (!(ifAbsent.fadeProgress <= 0.001F) && vectorWindow) {
         float amount2 = getAmount(ifAbsent.fadeProgress);
         tag.effectDurationAmount = amount2;
         tag.signalActive = !this.matrixFontActive && amount2 >= 0.55F;
         float amount3 = this.getInteger(distanceTo) * (float)this.doubleHelper.getValue() * (0.92F + 0.07999998F * amount2);
         tag.screenX = amount3;
         if (!this.canMinMax(tag, next, signalValue)) {
            tag.targetSignalValue = tag.tagAmount = max;
            tag.minHeight = tag.maxHeight = vectorWindowAmount2;
         }

         VertexTextureFont vertexTexture = CorzClient.fontManager.getLargeFont();
         float abcdefghijklmnopqrstuvwxyzBrown = vertexTexture.getAbcdefghijklmnopqrstuvwxyzBrown();
         tag.textScale = abcdefghijklmnopqrstuvwxyzBrown;
         this.jitterOffset = vertexTexture.getAbcdefghijklmnopqrstuvwxyzBrown(0, 100) - 50.0F;
         tag.tagActive = this.showFriendMarker.isEnabled() && FriendNameStore.canFlag(next);
         tag.signalValue = tag.tagActive ? 5562506 : MatrixEspUtils.releaseRightValue & 16777215;
         tag.signalAmount = getAmountForFloat(ifAbsent.animAmount / maxMaxHealth, 0.0F, 1.0F);
         float maxAbsorptionAmount = Math.max(0.0F, next.getAbsorptionAmount());
         tag.matricesAmount = maxAbsorptionAmount / maxMaxHealth;
         tag.matricesValue = getValue(tag.signalAmount);
         long time2 = mc.world.getTime();
         if (ifAbsent.lastSourceTime != time2 || ifAbsent.vertexTexture != vertexTexture) {
            ifAbsent.lastSourceTime = time2;
            ifAbsent.vertexTexture = vertexTexture;
            String text = formatText(vertexTexture, NameProtect.getString(next.getName().getString()), 130.0F);
            if (!text.equals(ifAbsent.cachedMainText)) {
               ifAbsent.cachedMainText = text;
               ifAbsent.mainTextWidth = vertexTexture.getAbcdefghijklmnopqrstuvwxyzCorzfont(text);
            }

            String corzfontText = this.showRankTag.isEnabled() ? getSubstring(next) : "";
            if (!corzfontText.equals(ifAbsent.cachedSecondaryText)) {
               ifAbsent.cachedSecondaryText = corzfontText;
               ifAbsent.textWidth = corzfontText.isEmpty() ? 0.0F : vertexTexture.getAbcdefghijklmnopqrstuvwxyzCorzfont(corzfontText) * 0.7F;
            }

            ifAbsent.accentColor = getColorScoreboard(next);
            ifAbsent.animValue = Long.MIN_VALUE;
         }

         tag.signal = this.showPing.isEnabled() ? this.getLatencyUuid(next) : -1;
         int signalLimit = this.showDistance.isEnabled() ? (int)distanceTo : -1;
         long time3 = (long)tag.signal << 32 | signalLimit & 4294967295L;
         if (time3 != ifAbsent.animValue) {
            ifAbsent.animValue = time3;
            String signalText = tag.signal >= 0 ? tag.signal + "ms" : "";
            String signalText2 = signalLimit >= 0 ? signalLimit + "m" : "";
            ifAbsent.signalText = formatText(signalText, signalText2);
            ifAbsent.signalAmount = ifAbsent.signalText.isEmpty() ? 0.0F : vertexTexture.getAbcdefghijklmnopqrstuvwxyzCorzfont(ifAbsent.signalText) * 0.82F;
         }

         int value = Math.round(ifAbsent.animAmount * 10.0F);
         int value2 = maxAbsorptionAmount > 0.05F ? Math.round(maxAbsorptionAmount) : -1;
         if (value != ifAbsent.lastBaseValue) {
            ifAbsent.lastBaseValue = value;
            ifAbsent.animText = value / 10 + "." + Math.abs(value % 10);
         }

         if (value2 != ifAbsent.lastDeltaValue) {
            ifAbsent.lastDeltaValue = value2;
            ifAbsent.deltaText = value2 < 0 ? "" : "+" + value2;
         }

         tag.tagPopCount = this.showPops.isEnabled() ? this.getUuidBar(next) : -1;
         if (tag.tagPopCount != ifAbsent.lastTagValue) {
            ifAbsent.lastTagValue = tag.tagPopCount;
            ifAbsent.absentText = tag.tagPopCount < 0 ? "" : (tag.tagPopCount < 100 ? signalTexts[tag.tagPopCount] : String.valueOf(tag.tagPopCount));
         }

         tag.signalCount = 0;
         if (this.showArmor.isEnabled() && !this.matrixFontActive) {
            this.handleStacksEmpty(tag, next.getEquippedStack(EquipmentSlot.HEAD));
            this.handleStacksEmpty(tag, next.getEquippedStack(EquipmentSlot.CHEST));
            this.handleStacksEmpty(tag, next.getEquippedStack(EquipmentSlot.LEGS));
            this.handleStacksEmpty(tag, next.getEquippedStack(EquipmentSlot.FEET));
            if (this.showHeldItems.isEnabled()) {
               this.handleStacksEmpty(tag, next.getMainHandStack());
               this.handleStacksEmpty(tag, next.getOffHandStack());
            }
         }

         for (int i = 0; i < tag.signalCount; i++) {
            tag.signalTexts[i] = this.formatDamageMax(tag.itemStacks[i]);
            tag.stacksTexts[i] = this.getLuckSea(tag.itemStacks[i]);
         }

         tag.effectDurationCount = 0;
         if (this.booleanBase.isEnabled()) {
            for (StatusEffectInstance statusEffectInstance : next.getStatusEffects()) {
               if (tag.effectDurationCount >= 6) {
                  break;
               }

               if (statusEffectInstance.shouldShowIcon()) {
                  tag.statusEffectInstances[tag.effectDurationCount] = statusEffectInstance;
                  tag.effectDurationTexts[tag.effectDurationCount] = this.getAppend(statusEffectInstance);
                  tag.signalTable[tag.effectDurationCount] = vertexTexture.getAbcdefghijklmnopqrstuvwxyzCorzfont(tag.effectDurationTexts[tag.effectDurationCount]) * 0.8F;
                  tag.effectDurationCount++;
               }
            }
         }

         float targetSignalValue = tag.targetSignalValue;
         float minHeight = tag.minHeight;
         float tagAmount = tag.tagAmount;
         float signalBound = tag.maxHeight;
         float signalValue7 = (targetSignalValue + tagAmount) / 2.0F;
         float signalTotal = minHeight - 3.0F;
         float signalTotal2 = signalBound + 3.0F;
         float signalValue8 = targetSignalValue - 4.0F;
         float signalValue9 = tagAmount + 4.0F;
         float minHeight3 = minHeight;
         float minHeight4 = minHeight;

         for (int j = 0; j < 4; j++) {
            tag.signalFlags[j] = false;
         }

         if (this.showHealth.isEnabled()) {
            int bottomLeft = getBottomLeft(this.healthBarPosition);
            tag.signalFlags[3] = true;
            if (bottomLeft != 2 && bottomLeft != 3) {
               float amountForFloat10 = getAmountForFloat((tagAmount - targetSignalValue) * 0.9F, 34.0F * amount3, 80.0F * amount3);
               float max11 = Math.max(2.0F, 2.5F * amount3);
               float signalValue12 = bottomLeft == 0 ? signalTotal - max11 : signalTotal2;
               handleAmountsFlags(tag, 3, signalValue7 - amountForFloat10 / 2.0F, signalValue12, amountForFloat10, max11, amountForFloat10, max11, false, false);
               if (bottomLeft == 0) {
                  signalTotal = signalValue12 - 3.0F * amount3;
               } else {
                  signalTotal2 = signalValue12 + max11 + 3.0F * amount3;
               }
            } else {
               float max13 = Math.max(2.0F, 2.5F * amount3);
               float max14 = Math.max(minHeight + 8.0F, signalBound);
               float signalValue15 = bottomLeft == 2 ? signalValue8 - max13 : signalValue9;
               handleAmountsFlags(tag, 3, signalValue15, minHeight, max13, max14 - minHeight, max13, max14 - minHeight, true, bottomLeft == 2);
               if (bottomLeft == 2) {
                  signalValue8 = signalValue15 - 4.0F;
               } else {
                  signalValue9 = signalValue15 + max13 + 4.0F;
               }
            }
         }

         for (int m = 0; m < 3; m++) {
            int bottomLeft2 = getBottomLeft(m == 0 ? this.namePosition : (m == 1 ? this.enumHelper : this.setPosition));
            boolean nameTagsFlag = bottomLeft2 == 2 || bottomLeft2 == 3;
            float signalFactor;
            float signalFactor2;
            if (m == 0) {
               signalFactor = this.getSignalText(tag, vertexTexture);
               signalFactor2 = abcdefghijklmnopqrstuvwxyzBrown + 6.0F;
            } else if (m == 1) {
               if (tag.effectDurationCount == 0) {
                  continue;
               }

               signalFactor = this.getAmounts(tag, nameTagsFlag);
               signalFactor2 = this.getAmount(tag, abcdefghijklmnopqrstuvwxyzBrown, nameTagsFlag);
            } else {
               if (tag.signalCount == 0) {
                  continue;
               }

               signalFactor = this.getTexts(tag, vertexTexture, nameTagsFlag);
               signalFactor2 = this.getTexts(tag, abcdefghijklmnopqrstuvwxyzBrown, nameTagsFlag);
            }

            float amountForFloat = signalFactor * amount3;
            float signalValue17 = signalFactor2 * amount3;
            float signalValue18;
            float signalValue19;
            switch (bottomLeft2) {
               case 1:
                  signalValue18 = signalValue7 - amountForFloat / 2.0F;
                  signalValue19 = signalTotal2;
                  signalTotal2 += signalValue17 + 3.0F * amount3;
                  break;
               case 2:
                  signalValue18 = signalValue8 - amountForFloat;
                  signalValue19 = minHeight3;
                  minHeight3 += signalValue17 + 3.0F * amount3;
                  break;
               case 3:
                  signalValue18 = signalValue9;
                  signalValue19 = minHeight4;
                  minHeight4 += signalValue17 + 3.0F * amount3;
                  break;
               default:
                  signalValue18 = signalValue7 - amountForFloat / 2.0F;
                  signalValue19 = signalTotal - signalValue17;
                  signalTotal -= signalValue17 + 3.0F * amount3;
            }

            tag.signalFlags[m] = true;
            handleAmountsFlags(tag, m, signalValue18, signalValue19, amountForFloat, signalValue17, signalFactor, signalFactor2, nameTagsFlag, bottomLeft2 == 2);
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public void onRender2D(TickProgress tickProgress) {
      this.updateBarHealth();
      if (mc.world != null && mc.player != null) {
         if (mc.currentScreen == null) {
            long timeMillis = System.currentTimeMillis();
            float nameTagsValue = this.nameTagsValue == 0L ? 0.0F : Math.min(0.1F, (float)(timeMillis - this.nameTagsValue) / 1000.0F);
            this.nameTagsValue = timeMillis;

            for (NameTags.Anim anim : this.barSideMap.values()) {
               anim.animActive = false;
            }

            boolean nameTagsFlag = !mc.gameRenderer.getCamera().isThirdPerson();
            MatrixStack matrixStack = this.matrixStack;
            matrixStack.loadIdentity();
            CorzClient.fontManager.setMatrixStack(matrixStack);
            this.matrixFontMatrix4f.set(MATRIX4F).mul(MATRIX4_FMATRIX4F).mul(MATRIX4F3);
            this.matrixFontTagList.clear();
            int index = 0;

            for (AbstractClientPlayerEntity abstractClientPlayerEntity : mc.world.getPlayers()) {
               if ((abstractClientPlayerEntity != mc.player || this.showSelfInThirdPerson.isEnabled() && !nameTagsFlag)
                  && !abstractClientPlayerEntity.isInvisible()
                  && (!this.ignoreNpcs.isEnabled() || !canUuid(abstractClientPlayerEntity))
                  && (!this.ignoreBots.isEnabled() || !this.canUuidNetwork(abstractClientPlayerEntity))) {
                  if (index == this.nameTagsTagList.size()) {
                     this.nameTagsTagList.add(new NameTags.Tag());
                  }

                  NameTags.Tag tag = this.nameTagsTagList.get(index);
                  if (this.canSignalAmount(tag, abstractClientPlayerEntity, tickProgress.tickProgress, nameTagsValue)) {
                     this.matrixFontTagList.add(tag);
                     index++;
                  }
               }
            }

            this.matrixFontTagList.sort((tag2, tag3) -> Float.compare(tag3.distanceTo, tag2.distanceTo));

            for (int i = 0; i < this.matrixFontTagList.size(); i++) {
               this.renderAmountsMatrices(this.matrixFontTagList.get(i), i, matrixStack, tickProgress.drawContext);
            }

            for (int j = 0; j < index; j++) {
               this.nameTagsTagList.get(j).next = null;
            }

            this.matrixFontTagList.clear();
            this.barSideMap.entrySet().removeIf(entry -> !entry.getValue().animActive);
            if (this.luckSeaStringArrayMap.size() > 256) {
               this.luckSeaStringArrayMap.clear();
            }
         }
      }
   }

   public float getAmountTime(float nameTagsValue) {
      return !Float.isNaN(this.matrixFontAmount) && System.currentTimeMillis() - this.timeMillis <= 400L ? this.matrixFontAmount : nameTagsValue;
   }

   @Override
   public void onDisable() {
      this.barSideMap.clear();
      this.barHealthMap.clear();
      this.concurrentLinkedQueue.clear();
      this.weakReference = new WeakReference<>(null);
      this.nameTagsValue = 0L;
      this.timeMillis = 0L;
   }

   private boolean canCameraPos(PlayerEntity playerEntity) {
      try {
         Vec3d cameraPos = mc.gameRenderer.getCamera().getCameraPos();
         Vec3d eyePos = playerEntity.getEyePos();
         BlockHitResult raycast = mc.world.raycast(new RaycastContext(cameraPos, eyePos, ShapeType.COLLIDER, FluidHandling.NONE, playerEntity));
         return raycast != null && raycast.getType() != Type.MISS ? raycast.getPos().squaredDistanceTo(cameraPos) < eyePos.squaredDistanceTo(cameraPos) - 0.01 : false;
      } catch (Throwable e) {
         return false;
      }
   }

   private static double getAmount(double nameTagsValue, double nameTagsValue2, float nameTagsFactor) {
      return nameTagsValue + (nameTagsValue2 - nameTagsValue) * nameTagsFactor;
   }

   private static void handleAmountsFlags(NameTags.Tag tag, int index, float nameTagsValue, float minHeight, float amountForFloat, float nameTagsValue4, float nameTagsValue5, float nameTagsValue6, boolean nameTagsFlag, boolean nameTagsFlag2) {
      tag.matricesTable[index] = nameTagsValue;
      tag.table[index] = minHeight;
      tag.tagTable[index] = amountForFloat;
      tag.tagHeights[index] = nameTagsValue4;
      tag.stacksTable[index] = nameTagsValue5;
      tag.effectDurationTable[index] = nameTagsValue6;
      tag.effectDurationFlags[index] = nameTagsFlag;
      tag.stacksFlags[index] = nameTagsFlag2;
   }

   private static int getColorScoreboard(PlayerEntity playerEntity) {
      try {
         Team scoreboardTeam = playerEntity.getScoreboardTeam();
         if (scoreboardTeam != null) {
            Integer colorValue = scoreboardTeam.getColor().getColorValue();
            if (colorValue != null && (colorValue & 16777215) != 16777215) {
               return colorValue & 16777215;
            }
         }
      } catch (Throwable e) {
      }

      return 13225430;
   }

   private static int getValue(float nameTagsLimit) {
      if (nameTagsLimit > 0.6F) {
         return 6282350;
      } else {
         return nameTagsLimit > 0.3F ? 14731304 : 14699339;
      }
   }

   private void renderMatrixPop(DrawContext context, ItemStack stack2, float matrixPopValue, float matrixPopValue2, float matrixPopValue3) {
      if (!this.matrixFontActive && stack2 != null && !stack2.isEmpty()) {
         this.effectDurationActive = true;
         Matrix3x2fStack matrices = context.getMatrices();
         matrices.pushMatrix();

         try {
            matrices.translate(matrixPopValue, matrixPopValue2);
            matrices.scale(matrixPopValue3 / 16.0F, matrixPopValue3 / 16.0F);
            context.drawItem(stack2, 0, 0);
         } finally {
            matrices.popMatrix();
         }
      }
   }

   private float getAmount(float nameTagsValue, float nameTagsValue2, float nameTagsFactor) {
      return nameTagsValue + nameTagsValue2 / 2.0F + this.jitterOffset * nameTagsFactor;
   }

   public boolean isFlag() {
      return this.isEnabled();
   }

   private float getAmounts(NameTags.Tag tag, boolean nameTagsFlag) {
      float nameTagsTotal = 0.0F;
      float nameTagsBound = 0.0F;

      for (int i = 0; i < tag.effectDurationCount; i++) {
         float rocketsElytraBound = 10.0F + tag.signalTable[i];
         nameTagsTotal += rocketsElytraBound;
         nameTagsBound = Math.max(nameTagsBound, rocketsElytraBound);
      }

      return nameTagsFlag ? 10.0F + nameTagsBound : 10.0F + nameTagsTotal + 6.0F * Math.max(0, tag.effectDurationCount - 1);
   }

   private static String formatText(String nameTagsText, String formatText) {
      if (nameTagsText.isEmpty() && formatText.isEmpty()) {
         return "";
      } else if (nameTagsText.isEmpty()) {
         return formatText;
      } else {
         return formatText.isEmpty() ? nameTagsText : nameTagsText + " \u00b7 " + formatText;
      }
   }

   public boolean canMatrixFont(DrawContext context, float matrixFontValue, boolean matrixFontFlag) {
      if (mc.world != null && mc.player != null && this.isEnabled()) {
         long timeMillis = System.currentTimeMillis();
         float nameTagsFactor = this.timeMillis != 0L && timeMillis - this.timeMillis <= 400L ? Math.min(0.1F, (float)(timeMillis - this.timeMillis) / 1000.0F) : 0.0F;
         this.timeMillis = timeMillis;
         MatrixStack matrixStack = CorzClient.fontManager.getMatrixStack();
         CorzClient.fontManager.setMatrixStack(this.matrixFontMatrixStack);
         this.matrixFontActive = !matrixFontFlag;
         this.effectDurationActive = false;
         this.matrixFontMatrix4f.set(MATRIX4F).mul(MATRIX4_FMATRIX4F).mul(MATRIX4F3);

         boolean matrixFont;
         try {
            this.updateBarHealth();
            NameTags.Tag tag = this.tag;
            this.matrixFontAmount = Float.NaN;
            if (this.canSignalAmount(tag, mc.player, matrixFontValue, nameTagsFactor)) {
               this.matrixFontAmount = this.getMax(tag);
               this.matrixFontTagList.clear();
               this.matrixFontTagList.add(tag);
               this.renderAmountsMatrices(tag, 0, this.matrixFontMatrixStack, context);
               this.matrixFontTagList.clear();
            }

            matrixFont = this.effectDurationActive;
         } finally {
            this.matrixFontActive = false;
            CorzClient.fontManager.setMatrixStack(matrixStack);
         }

         return matrixFont;
      } else {
         return false;
      }
   }

   private float getAmount(NameTags.Tag tag, float nameTagsValue, boolean nameTagsFlag) {
      float nameTagsFactor = Math.max(8.0F, nameTagsValue * 0.8F) + 2.0F;
      return nameTagsFlag ? 6.0F + nameTagsFactor * tag.effectDurationCount : 6.0F + nameTagsFactor;
   }

   private static String formatText(VertexTextureFont vertexTextureFont, String formatText, float nameTagsLimit) {
      if (formatText == null) {
         return "";
      } else if (vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont(formatText) <= nameTagsLimit) {
         return formatText;
      } else {
         String nameTagsText = "...";
         float nameTagsValue = vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont(nameTagsText);
         StringBuilder stringBuilder = new StringBuilder();
         float nameTagsTotal = 0.0F;

         for (int i = 0; i < formatText.length(); i++) {
            float nameTagsValue2 = vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont(String.valueOf(formatText.charAt(i)));
            if (nameTagsTotal + nameTagsValue2 + nameTagsValue > nameTagsLimit) {
               break;
            }

            stringBuilder.append(formatText.charAt(i));
            nameTagsTotal += nameTagsValue2;
         }

         return stringBuilder.append(nameTagsText).toString();
      }
   }

   private void renderStacksTexts(NameTags.Tag tag, MatrixStack matrices, DrawContext context, VertexTextureFont vertexTextureFont, boolean stacksFlag) {
      float stacksValue = tag.stacksTable[2];
      float stacksValue2 = tag.effectDurationTable[2];
      float effectDurationAmount = tag.effectDurationAmount;
      int signalValue = tag.signalValue;
      this.handle(matrices, stacksValue, stacksValue2, signalValue, effectDurationAmount);
      float stacksFactor = tag.textScale * 0.55F + 0.5F;
      if (!tag.effectDurationFlags[2]) {
         int stacksBound = 0;

         for (int i = 0; i < tag.signalCount; i++) {
            stacksBound = Math.max(stacksBound, tag.stacksTexts[i].length);
         }

         float stacksTotal = 5.0F;
         float stacksValue3 = 3.0F + stacksBound * stacksFactor;

         for (int j = 0; j < tag.signalCount; j++) {
            float texts = this.getTexts(tag, vertexTextureFont, j);
            String[] texts2 = tag.stacksTexts[j];

            for (int k = 0; k < texts2.length; k++) {
               float stacksValue4 = vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont(texts2[k]) * 0.55F;
               float y2 = stacksValue3 - (texts2.length - k) * stacksFactor;
               handlePopTranslate(vertexTextureFont, matrices, texts2[k], stacksTotal + (texts - stacksValue4) / 2.0F, y2, 0.55F, getColors(k == 0 ? signalValue : 13225430, effectDurationAmount));
            }

            if (stacksFlag) {
               this.renderMatrixPop(context, tag.itemStacks[j], stacksTotal + (texts - 12.0F) / 2.0F, stacksValue3, 12.0F);
            }

            String stacksText = tag.signalTexts[j];
            if (!stacksText.isEmpty()) {
               float stacksValue6 = vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont(stacksText) * 0.52F;
               handlePopTranslate(
                  vertexTextureFont, matrices, stacksText, stacksTotal + (texts - stacksValue6) / 2.0F, stacksValue3 + 12.0F + 0.5F, 0.52F, getColors(getDamageMax(tag.itemStacks[j]), effectDurationAmount)
               );
            }

            stacksTotal += texts + 3.0F;
         }
      } else {
         boolean stacksFlag2 = tag.stacksFlags[2];
         float stacksTotal2 = 3.0F;

         for (int p = 0; p < tag.signalCount; p++) {
            float matrixPopValue = stacksFlag2 ? stacksValue - 5.0F - 12.0F : 5.0F;
            if (stacksFlag) {
               this.renderMatrixPop(context, tag.itemStacks[p], matrixPopValue, stacksTotal2, 12.0F);
            }

            float x2 = stacksFlag2 ? matrixPopValue - 2.0F : matrixPopValue + 12.0F + 2.0F;
            float stacksValue8 = this.getAmount(stacksTotal2, 12.0F, 0.55F);
            String[] texts3 = tag.stacksTexts[p];

            for (int q = 0; q < texts3.length; q++) {
               float stacksValue9 = vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont(texts3[q]) * 0.55F;
               if (stacksFlag2) {
                  x2 -= stacksValue9;
               }

               handlePopTranslate(vertexTextureFont, matrices, texts3[q], x2, stacksValue8, 0.55F, getColors(q == 0 ? signalValue : 13225430, effectDurationAmount));
               x2 = stacksFlag2 ? x2 - 2.0F : x2 + stacksValue9 + 2.0F;
            }

            String corzfontText = tag.signalTexts[p];
            if (!corzfontText.isEmpty()) {
               float stacksValue10 = vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont(corzfontText) * 0.52F;
               if (stacksFlag2) {
                  x2 -= stacksValue10;
               }

               handlePopTranslate(vertexTextureFont, matrices, corzfontText, x2, this.getAmount(stacksTotal2, 12.0F, 0.52F), 0.52F, getColors(getDamageMax(tag.itemStacks[p]), effectDurationAmount));
            }

            stacksTotal2 += 13.0F;
         }
      }
   }

   private static int getValue(int nameTagsValue) {
      if (nameTagsValue < 80) {
         return 6282350;
      } else {
         return nameTagsValue < 180 ? 14731304 : 14699339;
      }
   }

   private void handle(MatrixStack matrices, float nameTagsValue, float nameTagsValue2, float nameTagsValue3, float nameTagsFactor, float nameTagsFactor2, float nameTagsBound, int colorsValue, float effectDurationAmount) {
      VertexConsumerUtilsVector.update4();

      try {
         VertexConsumerUtilsVector.handle(
            matrices, getColors(0, 0.55F * effectDurationAmount), nameTagsValue - 0.5F, nameTagsValue2 - 0.5F, nameTagsValue + nameTagsValue3 + 0.5F, nameTagsValue2 + nameTagsFactor + 0.5F, nameTagsValue3 / 2.0F + 0.5F, 6.0
         );
         float nameTagsLimit = Math.max(0.0F, nameTagsFactor * nameTagsFactor2);
         if (nameTagsLimit > 0.5F) {
            VertexConsumerUtilsVector.handle(matrices, getColors(colorsValue, 0.95F * effectDurationAmount), nameTagsValue, nameTagsValue2 + nameTagsFactor - nameTagsLimit, nameTagsValue + nameTagsValue3, nameTagsValue2 + nameTagsFactor, nameTagsValue3 / 2.0F, 6.0);
         }

         float min2 = Math.min(nameTagsFactor, nameTagsFactor * getAmountForFloat(nameTagsBound, 0.0F, 1.0F));
         if (min2 > 0.5F) {
            VertexConsumerUtilsVector.handle(matrices, getColors(15909198, 0.85F * effectDurationAmount), nameTagsValue, nameTagsValue2 + nameTagsFactor - min2, nameTagsValue + nameTagsValue3 * 0.45F, nameTagsValue2 + nameTagsFactor, nameTagsValue3 / 4.0F, 6.0);
         }
      } finally {
         VertexConsumerUtilsVector.update3();
      }
   }

   private static String getSubstring(PlayerEntity playerEntity) {
      try {
         Team scoreboardTeam = playerEntity.getScoreboardTeam();
         if (scoreboardTeam == null) {
            return "";
         } else {
            String strip = Formatting.strip(scoreboardTeam.getPrefix().getString());
            if (strip == null) {
               return "";
            } else {
               String trim = strip.replace("[", "").replace("]", "").replace("|", "").trim();
               if (trim.length() > 14) {
                  trim = trim.substring(0, 14).trim();
               }

               return trim;
            }
         }
      } catch (Throwable e) {
         return "";
      }
   }

   private float getTexts(NameTags.Tag tag, VertexTextureFont vertexTextureFont, int index) {
      float texts = 12.0F;

      for (String nameTagsText : tag.stacksTexts[index]) {
         texts = Math.max(texts, vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont(nameTagsText) * 0.55F);
      }

      String corzfontText = tag.signalTexts[index];
      if (!corzfontText.isEmpty()) {
         texts = Math.max(texts, vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont(corzfontText) * 0.52F);
      }

      return texts;
   }

   private static float getAmount(float nameTagsLimit) {
      nameTagsLimit = nameTagsLimit < 0.0F ? 0.0F : (nameTagsLimit > 1.0F ? 1.0F : nameTagsLimit);
      float nameTagsFactor = 1.0F - nameTagsLimit;
      return 1.0F - nameTagsFactor * nameTagsFactor * nameTagsFactor;
   }

   private boolean canVectorWindow(double vectorWindowValue, double vectorWindowValue2, double vectorWindowValue3) {
      int framebufferWidth = mc.getWindow().getFramebufferWidth();
      int framebufferHeight = mc.getWindow().getFramebufferHeight();
      this.vectorWindowTable[2] = framebufferWidth;
      this.vectorWindowTable[3] = framebufferHeight;
      Vec3d cameraPos = mc.gameRenderer.getCamera().getCameraPos();
      this.matrixFontMatrix4f.project((float)(vectorWindowValue - cameraPos.x), (float)(vectorWindowValue2 - cameraPos.y), (float)(vectorWindowValue3 - cameraPos.z), this.vectorWindowTable, this.vectorWindowVector3f);
      if (!(this.vectorWindowVector3f.z <= 0.0F) && !(this.vectorWindowVector3f.z >= 1.0F)) {
         float scaleFactor = mc.getWindow().getScaleFactor();
         this.signalAmount = this.vectorWindowVector3f.x / scaleFactor;
         this.vectorWindowAmount = (framebufferHeight - this.vectorWindowVector3f.y) / scaleFactor;
         return true;
      } else {
         return false;
      }
   }

   private String[] getLuckSea(ItemStack stack2) {
      if (!this.showEnchantments.isEnabled()) {
         return luckSeaTexts;
      } else {
         ItemEnchantmentsComponent enchantments = stack2.getEnchantments();
         if (enchantments != null && !enchantments.isEmpty()) {
            String[] luckSea = this.luckSeaStringArrayMap.get(enchantments);
            if (luckSea != null) {
               return luckSea;
            } else {
               var luckSeaObjectArrayList3 = this.luckSeaObjectArrayList;
               luckSeaObjectArrayList3.clear();

               for (Entry entry : enchantments.getEnchantmentEntries()) {
                  var registryEntry = (RegistryEntry<Enchantment>)entry.getKey();
                  String orElse = ((java.lang.String) (registryEntry.getKey().map(registryKey -> registryKey.getValue().getPath()).orElse("")));
                  String curse_protection_map = CURSE_PROTECTION_MAP.get(orElse);
                  if (curse_protection_map == null) {
                     curse_protection_map = orElse.isEmpty() ? "?" : orElse.substring(0, 1).toUpperCase(Locale.ROOT);
                  }

                  int intValue = entry.getIntValue();
                  int luckSeaValue = 1;

                  try {
                     luckSeaValue = ((Enchantment)registryEntry.value()).getMaxLevel();
                  } catch (Throwable e) {
                  }

                  int orDefault = LUCK_SEA_MAP.getOrDefault(orElse, LUCK_SEA_MAP.get("luck_of_the_sea"));
                  luckSeaObjectArrayList3.add(new Object[]{orDefault, intValue, luckSeaValue <= 1 && intValue <= 1 ? curse_protection_map : curse_protection_map + intValue});
               }

               luckSeaObjectArrayList3.sort((objs, objs2) -> {
                  int var2x = Integer.compare((Integer)objs[0], (Integer)objs2[0]);
                  return var2x != 0 ? var2x : Integer.compare((Integer)objs2[1], (Integer)objs[1]);
               });
               int luckSeaSize = Math.min(4, luckSeaObjectArrayList3.size());
               String[] strings = new String[luckSeaSize];

               for (int i = 0; i < luckSeaSize; i++) {
                  strings[i] = (String)((Object[])luckSeaObjectArrayList3.get(i))[2];
               }

               luckSeaObjectArrayList3.clear();
               this.luckSeaStringArrayMap.put(enchantments, strings);
               return strings;
            }
         } else {
            return luckSeaTexts;
         }
      }
   }

   private float getMax(NameTags.Tag tag) {
      float naN = Float.NaN;

      for (int i = 0; i < 3; i++) {
         if (tag.signalFlags[i] && tag.effectDurationFlags[i] && tag.stacksFlags[i]) {
            float nameTagsBound = tag.table[i] + tag.tagHeights[i];
            naN = Float.isNaN(naN) ? nameTagsBound : Math.max(naN, nameTagsBound);
         }
      }

      return naN;
   }

   private void handleForMatrixStackFloat(MatrixStack matrices, float nameTagsValue, float f12, float nameTagsFactor, float nameTagsValue3, float nameTagsFactor2, float nameTagsBound, int colorsValue, float effectDurationAmount) {
      VertexConsumerUtilsVector.update4();

      try {
         VertexConsumerUtilsVector.handle(matrices, getColors(0, 0.55F * effectDurationAmount), nameTagsValue - 0.5F, f12 - 0.5F, nameTagsValue + nameTagsFactor + 0.5F, f12 + nameTagsValue3 + 0.5F, nameTagsValue3 / 2.0F + 0.5F, 6.0);
         float nameTagsLimit = Math.max(0.0F, nameTagsFactor * nameTagsFactor2);
         if (nameTagsLimit > 0.5F) {
            VertexConsumerUtilsVector.handle(matrices, getColors(colorsValue, 0.95F * effectDurationAmount), nameTagsValue, f12, nameTagsValue + nameTagsLimit, f12 + nameTagsValue3, nameTagsValue3 / 2.0F, 6.0);
         }

         float min2 = Math.min(nameTagsFactor, nameTagsFactor * getAmountForFloat(nameTagsBound, 0.0F, 1.0F));
         if (min2 > 0.5F) {
            VertexConsumerUtilsVector.handle(matrices, getColors(15909198, 0.85F * effectDurationAmount), nameTagsValue, f12, nameTagsValue + min2, f12 + nameTagsValue3 * 0.45F, nameTagsValue3 / 4.0F, 6.0);
         }
      } finally {
         VertexConsumerUtilsVector.update3();
      }
   }

   private float getTexts(NameTags.Tag tag, VertexTextureFont vertexTextureFont, boolean nameTagsFlag) {
      if (!nameTagsFlag) {
         float nameTagsTotal = 0.0F;

         for (int i = 0; i < tag.signalCount; i++) {
            nameTagsTotal += this.getTexts(tag, vertexTextureFont, i);
         }

         return 10.0F + nameTagsTotal + 3.0F * Math.max(0, tag.signalCount - 1);
      } else {
         float nameTagsBound = 0.0F;

         for (int j = 0; j < tag.signalCount; j++) {
            float rocketsElytraBound = 12.0F;

            for (String nameTagsText : tag.stacksTexts[j]) {
               rocketsElytraBound += 2.0F + vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont(nameTagsText) * 0.55F;
            }

            String corzfontText = tag.signalTexts[j];
            if (!corzfontText.isEmpty()) {
               rocketsElytraBound += 2.0F + vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont(corzfontText) * 0.52F;
            }

            nameTagsBound = Math.max(nameTagsBound, rocketsElytraBound);
         }

         return 10.0F + nameTagsBound;
      }
   }

   private static int getDamageMax(ItemStack stack) {
      float damageValue = stack.getMaxDamage() <= 0 ? 1.0F : 1.0F - (float)stack.getDamage() / stack.getMaxDamage();
      return getValue(damageValue);
   }

   public NameTags() {
      super("Name Tags", IconsPngType.VISUAL);
      this.showDistance = new BooleanSetting("Show Distance", "Display distance in blocks", true);
      this.showPing = new BooleanSetting("Show Ping", "Display connection latency", true);
      this.showArmor = new BooleanSetting("Show Armor", "Armor set with enchantments and durability", true);
      this.showHeldItems = new BooleanSetting("Show Held Items", "Add main-hand and off-hand items to the set", true);
      this.showDurability = new BooleanSetting("Show Durability", "Durability percent under damaged gear", true);
      this.showEnchantments = new BooleanSetting("Show Enchantments", "Short enchantment labels above each item, like P4 or U3", true);
      this.booleanBase = new BooleanSetting("Show Effects", "Active potion effects with the time left (most servers only send your own)", true);
      this.showPops = new BooleanSetting("Show Pops", "Totem pops since the player last died", true);
      this.showRankTag = new BooleanSetting("Show Rank Tag", "Rank or prefix tag from the scoreboard next to the name", true);
      this.showFriendMarker = new BooleanSetting("Show Friend Marker", "Mark friends with an accent dot and name tint", true);
      this.showSelfInThirdPerson = new BooleanSetting("Show Self In Third Person", "Show your own tag in third person / freecam", false);
      this.ignoreBots = new BooleanSetting("Ignore Bots", "Hide tags for fake players with no entry in the server tab list (bots / fake connections).", false);
      this.ignoreNpcs = new BooleanSetting(
         "Ignore NPCs", "Hide tags for server NPCs \u2014 Citizens-style version-2 UUIDs (shopkeepers, warps, holograms).", false
      );
      this.doubleHelper = new DoubleSetting("Scale", "Overall size of the name tag", 1.0, 0.5, 2.0, 0.05);
      this.namePosition = new EnumSetting("Name Position", "Which side of the player the name sits on", "Top", sidePositionTexts);
      this.setPosition = new EnumSetting("Set Position", "Which side of the player the armor set sits on", "Top", sidePositionTexts);
      this.enumHelper = new EnumSetting("Effects Position", "Which side of the player the effects sit on", "Top", sidePositionTexts);
      this.healthBarPosition = new EnumSetting("Health Bar Position", "Which side of the player the health bar sits on", "Left", sidePositionTexts);
      this.barSideMap = new HashMap<>();
      this.barHealthMap = new HashMap<>();
      this.concurrentLinkedQueue = new ConcurrentLinkedQueue<>();
      this.weakReference = new WeakReference<>(null);
      this.luckSeaStringArrayMap = new IdentityHashMap<>();
      this.int2ObjectOpenHashMap = new Int2ObjectOpenHashMap();
      this.nameTagsValue = 0L;
      this.matrixStack = new MatrixStack();
      this.matrixFontMatrixStack = new MatrixStack();
      this.timeMillis = 0L;
      this.matrixFontMatrix4f = new Matrix4f();
      this.vectorWindowTable = new int[4];
      this.vectorWindowVector3f = new Vector3f();
      this.nameTagsTagList = new ArrayList<>();
      this.matrixFontTagList = new ArrayList<>();
      this.tag = new NameTags.Tag();
      this.luckSeaObjectArrayList = new ArrayList<>();
      this.matrixFontAmount = Float.NaN;
      this.addSettings(
         new ConfigSetting[]{
            this.throughWalls,
            this.integerHelper,
            this.showHealth,
            this.showDistance,
            this.showPing,
            this.showArmor,
            this.showHeldItems,
            this.showDurability,
            this.showEnchantments,
            this.booleanBase,
            this.showPops,
            this.showRankTag,
            this.showFriendMarker,
            this.showSelfInThirdPerson,
            this.ignoreBots,
            this.ignoreNpcs,
            this.doubleHelper,
            this.namePosition,
            this.setPosition,
            this.enumHelper,
            this.healthBarPosition
         }
      );
   }

   private boolean canUuidNetwork(PlayerEntity playerEntity) {
      if (canUuid(playerEntity)) {
         return false;
      } else {
         ClientPlayNetworkHandler networkHandler = mc.getNetworkHandler();
         return networkHandler != null && networkHandler.getPlayerListEntry(playerEntity.getUuid()) == null;
      }
   }

   private static float getAmountForFloat(float nameTagsBound, float nameTagsLimit, float maxMaxHealth) {
      return nameTagsBound < nameTagsLimit ? nameTagsLimit : Math.min(nameTagsBound, maxMaxHealth);
   }

   private static String formatViiiVii(int viiiViiValue) {
      return switch (viiiViiValue) {
         case 1 -> "I";
         case 2 -> "II";
         case 3 -> "III";
         case 4 -> "IV";
         case 5 -> "V";
         case 6 -> "VI";
         case 7 -> "VII";
         case 8 -> "VIII";
         case 9 -> "IX";
         case 10 -> "X";
         default -> String.valueOf(viiiViiValue);
      };
   }

   private int getUuidBar(PlayerEntity player) {
      NameTags.Pops barhealthmap = this.barHealthMap.get(player.getUuid());
      if (barhealthmap == null) {
         return 0;
      } else if (barhealthmap.playerId == player.getId() && !player.isDead()) {
         return barhealthmap.popCount;
      } else {
         this.barHealthMap.remove(player.getUuid());
         return 0;
      }
   }

   @Override
   protected void onReset() {
      this.updateBarHealth();
   }

   private float getTexts(NameTags.Tag tag, float nameTagsValue, boolean nameTagsFlag) {
      if (nameTagsFlag) {
         return 6.0F + tag.signalCount * 13.0F;
      } else {
         int nameTagsBound = 0;
         boolean nameTagsFlag2 = false;

         for (int i = 0; i < tag.signalCount; i++) {
            nameTagsBound = Math.max(nameTagsBound, tag.stacksTexts[i].length);
            if (!tag.signalTexts[i].isEmpty()) {
               nameTagsFlag2 = true;
            }
         }

         return 6.0F + nameTagsBound * (nameTagsValue * 0.55F + 0.5F) + 12.0F + (nameTagsFlag2 ? nameTagsValue * 0.52F + 1.0F : 0.0F);
      }
   }

   private int getLatencyUuid(PlayerEntity playerEntity) {
      try {
         ClientPlayNetworkHandler networkHandler = mc.getNetworkHandler();
         if (networkHandler == null) {
            return -1;
         } else {
            PlayerListEntry playerListEntry = networkHandler.getPlayerListEntry(playerEntity.getUuid());
            return playerListEntry != null ? Math.max(0, playerListEntry.getLatency()) : -1;
         }
      } catch (Throwable e) {
         return -1;
      }
   }

   private void renderSignalText(NameTags.Tag tag, MatrixStack matrices, DrawContext context, VertexTextureFont vertexTextureFont, boolean signalFlag) {
      NameTags.Anim ifAbsent = tag.ifAbsent;
      float signalValue = tag.stacksTable[0];
      float signalValue2 = tag.effectDurationTable[0];
      float effectDurationAmount = tag.effectDurationAmount;
      this.handle(matrices, signalValue, signalValue2, tag.signalValue, effectDurationAmount);
      float signalTotal = 5.0F;
      if (!ifAbsent.absentText.isEmpty()) {
         if (signalFlag) {
            if (this.itemStack == null) {
               this.itemStack = new ItemStack(Items.TOTEM_OF_UNDYING);
            }

            this.renderMatrixPop(context, this.itemStack, signalTotal, (signalValue2 - 9.0F) / 2.0F, 9.0F);
         }

         float x2 = signalTotal + 10.5F;
         handlePopTranslate(vertexTextureFont, matrices, ifAbsent.absentText, x2, this.getAmount(0.0F, signalValue2, 0.8F), 0.8F, getColors(tag.tagPopCount > 0 ? 15909198 : 9080728, effectDurationAmount));
         signalTotal = x2 + (vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont(ifAbsent.absentText) * 0.8F + 5.0F);
      }

      if (tag.tagActive) {
         float f12 = (signalValue2 - 4.0F) / 2.0F;
         VertexConsumerUtilsVector.update4();

         try {
            VertexConsumerUtilsVector.handle(matrices, getColors(5562506, effectDurationAmount), signalTotal, f12, signalTotal + 4.0F, f12 + 4.0F, 2.0, 8.0);
         } finally {
            VertexConsumerUtilsVector.update3();
         }

         signalTotal += 7.0F;
      }

      vertexTextureFont.handleForStringFloat(ifAbsent.cachedMainText, signalTotal, this.getAmount(0.0F, signalValue2, 1.0F), getColors(tag.tagActive ? 5562506 : 15922423, effectDurationAmount));
      float signalValue5 = signalTotal + ifAbsent.mainTextWidth;
      if (!ifAbsent.cachedSecondaryText.isEmpty()) {
         float vertexConsumerValue = signalValue5 + 5.0F;
         float signalValue7 = ifAbsent.textWidth + 6.0F;
         float signalValue8 = (signalValue2 - 9.0F) / 2.0F;
         VertexConsumerUtilsVector.update4();

         try {
            VertexConsumerUtilsVector.handle(matrices, getColors(ifAbsent.accentColor, 0.22F * effectDurationAmount), vertexConsumerValue, signalValue8, vertexConsumerValue + signalValue7, signalValue8 + 9.0F, 4.5, 8.0);
         } finally {
            VertexConsumerUtilsVector.update3();
         }

         handlePopTranslate(vertexTextureFont, matrices, ifAbsent.cachedSecondaryText, vertexConsumerValue + 3.0F, this.getAmount(signalValue8, 9.0F, 0.7F), 0.7F, getColors(ifAbsent.accentColor, effectDurationAmount));
         signalValue5 = vertexConsumerValue + signalValue7;
      }

      if (ifAbsent.signalAmount > 0.0F) {
         float matrixPeekValue = signalValue5 + 5.0F;
         if (tag.signal >= 0) {
            MatrixPeekUtilsIdentifier.handle(
               matrixPeekValue, (signalValue2 - 7.0F) / 2.0F, 7.0F, 7.0F, IcoGenUtils.getIdentifierGENERATED_IDENTIFIER_CACHE("signal"), getColors(getValue(tag.signal), effectDurationAmount), matrices
            );
            matrixPeekValue += 8.5F;
         }

         handlePopTranslate(vertexTextureFont, matrices, ifAbsent.signalText, matrixPeekValue, this.getAmount(0.0F, signalValue2, 0.82F), 0.82F, getColors(9080728, effectDurationAmount));
      }

      float signalValue9 = signalValue - 5.0F;
      MatrixPeekUtilsIdentifier.handle(signalValue9 - 7.0F, (signalValue2 - 7.0F) / 2.0F, 7.0F, 7.0F, IcoGenUtils.getHeartIco(), getColors(tag.matricesValue, effectDurationAmount), matrices);
      float signalTotal3 = signalValue9 - 8.5F;
      if (!ifAbsent.deltaText.isEmpty()) {
         float signalValue10 = vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont(ifAbsent.deltaText) * 0.8F;
         handlePopTranslate(vertexTextureFont, matrices, ifAbsent.deltaText, signalTotal3 - signalValue10, this.getAmount(0.0F, signalValue2, 0.8F), 0.8F, getColors(15909198, effectDurationAmount));
         signalTotal3 -= signalValue10 + 1.5F;
      }

      float signalValue11 = vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont(ifAbsent.animText) * 0.8F;
      handlePopTranslate(vertexTextureFont, matrices, ifAbsent.animText, signalTotal3 - signalValue11, this.getAmount(0.0F, signalValue2, 0.8F), 0.8F, getColors(tag.matricesValue, effectDurationAmount));
   }

   private String getAppend(StatusEffectInstance statusEffectInstance) {
      int appendLimit = Math.max(0, Math.min(255, statusEffectInstance.getAmplifier()));
      boolean appendFlag = statusEffectInstance.isInfinite() || statusEffectInstance.getDuration() < 0;
      int appendValue = appendFlag ? 0 : Math.min(1048575, statusEffectInstance.getDuration() / 20);
      int appendValue2 = appendFlag ? -1 - appendLimit : appendLimit << 20 | appendValue;
      String int2objectopenhashmap = (String)this.int2ObjectOpenHashMap.get(appendValue2);
      if (int2objectopenhashmap != null) {
         return int2objectopenhashmap;
      } else {
         String appendText = appendLimit > 0 ? formatViiiVii(appendLimit + 1) + " " : "";
         String append = appendFlag ? appendText + "**" : appendText + appendValue / 60 + ":" + (appendValue % 60 < 10 ? "0" : "") + appendValue % 60;
         if (this.int2ObjectOpenHashMap.size() > 512) {
            this.int2ObjectOpenHashMap.clear();
         }

         this.int2ObjectOpenHashMap.put(appendValue2, append);
         return append;
      }
   }

   private void renderAmountsMatrices(NameTags.Tag tag, int matricesValue, MatrixStack matrices, DrawContext context) {
      float effectDurationAmount = tag.effectDurationAmount;
      float x = tag.screenX;
      VertexTextureFont vertexTexture = CorzClient.fontManager.getLargeFont();
      this.jitterOffset = vertexTexture.getAbcdefghijklmnopqrstuvwxyzBrown(0, 100) - 50.0F;
      if (tag.signalFlags[3]) {
         if (tag.effectDurationFlags[3]) {
            this.handle(matrices, tag.matricesTable[3], tag.table[3], tag.tagTable[3], tag.tagHeights[3], tag.signalAmount, tag.matricesAmount, tag.matricesValue, effectDurationAmount);
         } else {
            this.handleForMatrixStackFloat(matrices, tag.matricesTable[3], tag.table[3], tag.tagTable[3], tag.tagHeights[3], tag.signalAmount, tag.matricesAmount, tag.matricesValue, effectDurationAmount);
         }
      }

      for (int i = 0; i < 3; i++) {
         if (tag.signalFlags[i]) {
            boolean matricesFlag = tag.signalActive && !this.canAmountsFlags(matricesValue, tag, i);
            matrices.push();
            context.getMatrices().pushMatrix();

            try {
               matrices.translate(tag.matricesTable[i], tag.table[i], 0.0F);
               matrices.scale(x, x, 1.0F);
               context.getMatrices().translate(tag.matricesTable[i], tag.table[i]);
               context.getMatrices().scale(x, x);
               if (i == 0) {
                  this.renderSignalText(tag, matrices, context, vertexTexture, matricesFlag);
               } else if (i == 1) {
                  this.renderEffectDuration(tag, matrices, context, vertexTexture, matricesFlag);
               } else {
                  this.renderStacksTexts(tag, matrices, context, vertexTexture, matricesFlag);
               }
            } finally {
               context.getMatrices().popMatrix();
               matrices.pop();
            }
         }
      }
   }

   private void updateBarHealth() {
      if (mc.world != this.weakReference.get()) {
         this.weakReference = new WeakReference<>(mc.world);
         this.barHealthMap.clear();
         this.concurrentLinkedQueue.clear();
      }

      if (mc.world == null) {
         this.concurrentLinkedQueue.clear();
      } else {
         EntityStatusS2CPacket entityStatusS2CPacket;
         while ((entityStatusS2CPacket = this.concurrentLinkedQueue.poll()) != null) {
            if (entityStatusS2CPacket.getEntity(mc.world) instanceof PlayerEntity playerEntity) {
               if (entityStatusS2CPacket.getStatus() == 3) {
                  this.barHealthMap.remove(playerEntity.getUuid());
               } else {
                  NameTags.Pops ifAbsent = this.barHealthMap.computeIfAbsent(playerEntity.getUuid(), uuid -> new NameTags.Pops());
                  if (ifAbsent.playerId != playerEntity.getId()) {
                     ifAbsent.playerId = playerEntity.getId();
                     ifAbsent.popCount = 0;
                  }

                  ifAbsent.popCount++;
               }
            }
         }

         if (!this.barHealthMap.isEmpty()) {
            this.barHealthMap.entrySet().removeIf(entry -> {
               PlayerEntity var1x = mc.world.getPlayerByUuid(entry.getKey());
               return var1x != null && (var1x.isDead() || var1x.getHealth() <= 0.0F);
            });
            if (this.barHealthMap.size() > 64) {
               this.barHealthMap.keySet().removeIf(uuid2 -> mc.world.getPlayerByUuid(uuid2) == null);
            }
         }
      }
   }

   static {
      String[][] strings = new String[][]{
         {"protection", "P"},
         {"blast_protection", "BP"},
         {"fire_protection", "FP"},
         {"projectile_protection", "PP"},
         {"sharpness", "S"},
         {"smite", "SM"},
         {"bane_of_arthropods", "BA"},
         {"power", "PW"},
         {"efficiency", "E"},
         {"density", "D"},
         {"breach", "B"},
         {"impaling", "IM"},
         {"riptide", "RT"},
         {"loyalty", "LO"},
         {"feather_falling", "FF"},
         {"depth_strider", "DS"},
         {"frost_walker", "FW"},
         {"soul_speed", "SS"},
         {"swift_sneak", "SN"},
         {"respiration", "R"},
         {"aqua_affinity", "AA"},
         {"thorns", "T"},
         {"fire_aspect", "FA"},
         {"knockback", "K"},
         {"looting", "L"},
         {"sweeping_edge", "SE"},
         {"wind_burst", "WB"},
         {"punch", "PU"},
         {"flame", "FL"},
         {"infinity", "I"},
         {"multishot", "MS"},
         {"quick_charge", "QC"},
         {"piercing", "PI"},
         {"channeling", "CH"},
         {"fortune", "F"},
         {"silk_touch", "ST"},
         {"lure", "LU"},
         {"luck_of_the_sea", "LS"},
         {"unbreaking", "U"},
         {"mending", "M"},
         {"binding_curse", "CB"},
         {"vanishing_curse", "CV"}
      };

      for (int i = 0; i < strings.length; i++) {
         CURSE_PROTECTION_MAP.put(strings[i][0], strings[i][1]);
         LUCK_SEA_MAP.put(strings[i][0], i);
      }

      damageTexts = new String[101];
      signalTexts = new String[100];

      for (int j = 0; j <= 100; j++) {
         damageTexts[j] = j + "%";
      }

      for (int k = 0; k < 100; k++) {
         signalTexts[k] = String.valueOf(k);
      }

      colors = new Color[512];
      colorsTable = new int[512];
      luckSeaTexts = new String[0];
   }

   private static Color getColors(int colorsValue, float effectDurationAmount) {
      int max3 = Math.max(0, Math.min(255, Math.round(effectDurationAmount * 255.0F)));
      int colorsValue4 = max3 << 24 | colorsValue & 16777215;
      int index = colorsValue4 * -1640531535 >>> 23;
      Color color2 = colors[index];
      if (color2 == null || colorsTable[index] != colorsValue4) {
         color2 = new Color(colorsValue4, true);
         colors[index] = color2;
         colorsTable[index] = colorsValue4;
      }

      return color2;
   }

   private float getTextAbsent(NameTags.Tag tag, VertexTextureFont vertexTextureFont) {
      NameTags.Anim ifAbsent = tag.ifAbsent;
      return ifAbsent.absentText.isEmpty() ? 0.0F : 10.5F + vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont(ifAbsent.absentText) * 0.8F;
   }

   private boolean canMinMax(NameTags.Tag tag, PlayerEntity playerEntity, float nameTagsValue) {
      double nameTagsValue2 = getAmount(playerEntity.lastX, playerEntity.getX(), nameTagsValue);
      double nameTagsValue3 = getAmount(playerEntity.lastY, playerEntity.getY(), nameTagsValue);
      double nameTagsValue4 = getAmount(playerEntity.lastZ, playerEntity.getZ(), nameTagsValue);
      Box boundingBox = playerEntity.getBoundingBox();
      double nameTagsValue5 = (boundingBox.maxX - boundingBox.minX) / 2.0;
      double nameTagsValue6 = (boundingBox.maxZ - boundingBox.minZ) / 2.0;
      double nameTagsValue7 = boundingBox.maxY - boundingBox.minY;
      float min = Float.MAX_VALUE;
      float maxValue = Float.MAX_VALUE;
      float max = -Float.MAX_VALUE;
      float pilotStatusBound = -Float.MAX_VALUE;
      int nameTagsCounter = 0;

      for (int i = 0; i < 8; i++) {
         double vectorWindowValue = nameTagsValue2 + ((i & 1) == 0 ? -nameTagsValue5 : nameTagsValue5);
         double nameTagsValue9 = nameTagsValue3 + ((i & 2) == 0 ? 0.0 : nameTagsValue7);
         double nameTagsValue10 = nameTagsValue4 + ((i & 4) == 0 ? -nameTagsValue6 : nameTagsValue6);
         if (this.canVectorWindow(vectorWindowValue, nameTagsValue9, nameTagsValue10)) {
            nameTagsCounter++;
            min = Math.min(min, this.signalAmount);
            max = Math.max(max, this.signalAmount);
            maxValue = Math.min(maxValue, this.vectorWindowAmount);
            pilotStatusBound = Math.max(pilotStatusBound, this.vectorWindowAmount);
         }
      }

      if (nameTagsCounter < 2) {
         return false;
      } else {
         tag.targetSignalValue = min;
         tag.minHeight = maxValue;
         tag.tagAmount = max;
         tag.maxHeight = pilotStatusBound;
         return true;
      }
   }

   private static final class Anim {
      float signalAmount;
      float textWidth;
      int accentColor;
      boolean cameraPos;
      VertexTextureFont vertexTexture;
      long lastSourceTime;
      long signalValue = Long.MIN_VALUE;
      boolean signalActive;
      String signalText;
      long animValue;
      float animAmount;
      int lastTagValue;
      String animText;
      boolean animActive;
      String cachedSecondaryText;
      String cachedMainText;
      int lastDeltaValue;
      float mainTextWidth;
      int lastBaseValue;
      String deltaText;
      String absentText;
      float fadeProgress;

      private Anim() {
         this.lastSourceTime = Long.MIN_VALUE;
         this.lastBaseValue = Integer.MIN_VALUE;
         this.lastDeltaValue = Integer.MIN_VALUE;
         this.lastTagValue = Integer.MIN_VALUE;
         this.animValue = Long.MIN_VALUE;
         this.animText = "";
         this.deltaText = "";
         this.absentText = "";
         this.signalText = "";
         this.cachedMainText = "";
         this.cachedSecondaryText = "";
      }
   }

   private static final class Pops {
      int playerId;
      int popCount;
   }

   private static final class Tag {
      final boolean[] signalFlags;
      float screenX;
      final boolean[] effectDurationFlags;
      float signalAmount;
      int tagPopCount;
      final String[][] stacksTexts;
      float distanceTo;
      int signal;
      final String[] signalTexts;
      float maxHeight;
      float matricesAmount;
      PlayerEntity next;
      float effectDurationAmount;
      boolean signalActive;
      final float[] matricesTable;
      final StatusEffectInstance[] statusEffectInstances;
      final ItemStack[] itemStacks = new ItemStack[6];
      final boolean[] stacksFlags;
      int signalValue;
      boolean tagActive;
      float targetSignalValue;
      final float[] effectDurationTable;
      final String[] effectDurationTexts;
      float tagAmount;
      int matricesValue;
      final float[] signalTable;
      final float[] tagTable;
      float minHeight;
      final float[] table;
      final float[] stacksTable;
      int signalCount;
      int effectDurationCount;
      final float[] tagHeights;
      NameTags.Anim ifAbsent;
      float textScale;

      private Tag() {
         this.signalTexts = new String[6];
         this.stacksTexts = new String[6][];
         this.statusEffectInstances = new StatusEffectInstance[6];
         this.effectDurationTexts = new String[6];
         this.signalTable = new float[6];
         this.signalFlags = new boolean[4];
         this.effectDurationFlags = new boolean[4];
         this.stacksFlags = new boolean[4];
         this.matricesTable = new float[4];
         this.table = new float[4];
         this.tagTable = new float[4];
         this.tagHeights = new float[4];
         this.stacksTable = new float[4];
         this.effectDurationTable = new float[4];
      }
   }
}
