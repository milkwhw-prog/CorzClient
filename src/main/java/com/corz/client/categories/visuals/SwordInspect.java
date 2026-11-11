package com.corz.client.categories.visuals;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.DoubleSetting;
import com.corz.client.config.EnumSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.config.KeybindSetting;
import com.corz.client.gui.ButtonBase;
import com.corz.client.utils.KnifeCueClock.Sink;
import com.corz.client.utils.KnifeSoundCues.Cue;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.IntToDoubleFunction;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.lwjgl.glfw.GLFW;
import com.corz.client.utils.C2Butterfly;
import com.corz.client.utils.C2Cues;
import com.corz.client.utils.C2Knife;
import com.corz.client.utils.KnifeCueClock;
import com.corz.client.utils.KnifeFinish;
import com.corz.client.utils.KnifeInspectClip;
import com.corz.client.utils.KnifeMesh;
import com.corz.client.utils.KnifePreview;
import com.corz.client.utils.KnifeRig;
import com.corz.client.utils.KnifeSoundCues;
import com.corz.client.utils.KnifeSounds;
import ai.auth.Module;
import ai.auth.ModuleRegistry;
import com.corz.client.utils.IconsPngType;
import com.corz.client.utils.PageRaltUtils;
import com.corz.client.utils.SwordBot;

public class SwordInspect extends Module {
   private long deployButterflyTime;
   private final EnumSetting enumHelper;
   private static final String[] randomInspectTexts = new String[]{
      "Random", "Spin", "Flip", "Barrel Roll", "Twirl", "Toss", "Butterfly", "M9 Bayonet", "Stiletto"
   };
   private final BooleanSetting booleanBase;
   private float swishAmount;
   private final KeybindSetting keybindBase;
   private final IntegerSetting integerHelper;
   private long swishTime;
   private final KnifeCueClock knifeCueClock;
   private long swishTimeSwishDeploy;
   private String deployButterflyText;
   private static final Map<String, Identifier> PROBE_KNIFE_MAP = new HashMap<>();
   private long previewKnifeTime;
   private static final Identifier identifier = Identifier.of("corzclient", "knife_missing_probe");
   private long deployButterflyValue;
   private List<Cue> knifePreviewCueList;
   private final KnifeCueClock cueClockKnifeCueClock;
   private String lastKnife;
   private final DoubleSetting doubleHelper;
   private int previewKnifeValue;
   private final BooleanSetting swordsOnly;
   private static final String[] knifeInspectTexts = getArray();
   private final EnumSetting knifeFinish;
   static final SwordInspect.Anim ANIM = new SwordInspect.Anim(900, 2, 0.38F, 0.2F, -0.02F, -20.0F, 12.0F, -6.0F, 28.0F, 44.0F, 34.0F, 20.0F, 74.0F);
   private long previewSpinTime;
   private SwordInspect.Anim ANIMMultiplyAnim;
   private static final Map<String, SwordInspect.Anim> STILETTO_MAP = Map.of(
      "Flip",
      new SwordInspect.Anim(2450, 5, 0.1F, 0.0F, -0.37F, -25.0F, 34.0F, -6.0F, 28.0F, 44.0F, 26.0F, 20.0F, 74.0F),
      "Toss",
      new SwordInspect.Anim(1750, 5, 0.18F, 0.1F, -0.33F, -33.0F, 32.0F, -31.0F, 69.0F, 75.0F, 5.0F, 20.0F, 60.0F),
      "Butterfly",
      new SwordInspect.Anim(3800, 1, 0.12F, 0.06F, -0.3F, -22.0F, 28.0F, -6.0F, 24.0F, 14.0F, 30.0F, 14.0F, 86.0F),
      "M9 Bayonet",
      new SwordInspect.Anim(3400, 1, 0.1F, 0.06F, -0.3F, -24.0F, 30.0F, -6.0F, 22.0F, 16.0F, 28.0F, 18.0F, 84.0F),
      "Stiletto",
      new SwordInspect.Anim(3200, 2, 0.1F, 0.06F, -0.3F, -24.0F, 30.0F, -6.0F, 26.0F, 18.0F, 30.0F, 14.0F, 86.0F)
   );
   private boolean swappedSlotFlag;
   private final Sink swishRoundSink;
   private final BooleanSetting holdToLoop;
   private List<C2Cues.Contact> cueClockContactList;
   private final EnumSetting animationEnum = new EnumSetting(
      "Animation", "Which inspect trick to play. Random rolls a fresh one each press.", "Random", randomInspectTexts
   );
   private static final List<String> STILETTO_ITEMS = getCopyOf();
   private float[] swishAmounts;
   private final ButtonBase buttonBase;
   private final Sink cueClockSink;
   private final EnumSetting knifeSounds;
   private int spinCount;
   private static final String BUTTERFLY = "Butterfly";
   private KnifeInspectClip knifeInspectClip;
   private int hashCode;
   private int round;
   private float[] cueClockTable;

   public static KnifeInspectClip getAnticipationSpins(String anticipationSpinsText) {
      if (!KnifeInspectClip.canBayonetStiletto(anticipationSpinsText)) {
         return null;
      } else {
         SwordInspect.Anim animStiletto = getAnimStiletto(anticipationSpinsText);
         return KnifeInspectClip.getKnifeInspectClip(anticipationSpinsText, animStiletto.anticipation(), animStiletto.overshoot(), animStiletto.spins());
      }
   }

   private void handleSwishDeploy(LivingEntity livingEntity, Hand hand) {
      if (this.swappedSlotFlag && C2Butterfly.canFlag(livingEntity, hand)) {
         KnifeSounds.handleSoundInstance(this.deployButterflyValue);
         this.deployButterflyValue = 0L;
         long swishTime = getTime();
         if (swishTime - this.swishTimeSwishDeploy >= 300L) {
            this.swishTimeSwishDeploy = swishTime;
            this.handle("swish", 0.26F, 1.0F);
         }
      }
   }

   static float getRiseSettle(float riseSettleValue, SwordInspect.Anim anim) {
      float riseScaled = anim.rise() / 100.0F;
      float settleScaled = anim.settle() / 100.0F;
      float riseSettleBound = getAmount(getAmountFloat(riseSettleValue / riseScaled));
      float hashHuntBound = getAmount(getAmountFloat((1.0F - riseSettleValue) / (1.0F - settleScaled)));
      return Math.min(riseSettleBound, hashHuntBound);
   }

   private void updateSwappedSlot() {
      boolean swappedSlotFlag = this.isEnabled();
      if (swappedSlotFlag != this.swappedSlotFlag) {
         this.swappedSlotFlag = swappedSlotFlag;
         C2Butterfly.handleFlag(swappedSlotFlag);
      }
   }

   @Override
   public void loadExtraData(JsonElement json) {
      if (json != null && json.isJsonObject()) {
         JsonObject asJsonObject = json.getAsJsonObject();
         boolean iIiFlag = asJsonObject.has("soundVersion") && asJsonObject.get("soundVersion").isJsonPrimitive() && asJsonObject.get("soundVersion").getAsJsonPrimitive().isNumber();
         this.doubleHelper.handleMaxAmount(KnifeSoundCues.getAmount(iIiFlag ? asJsonObject.get("soundVersion").getAsInt() : 0, this.doubleHelper.getValue()));
         if (asJsonObject.has("lastKnife") && asJsonObject.get("lastKnife").isJsonPrimitive() && KnifeRig.getBayonetStiletto(asJsonObject.get("lastKnife").getAsString()) != null) {
            this.lastKnife = asJsonObject.get("lastKnife").getAsString();
         }
      }
   }

   private boolean isKeyPressed() {
      int pressedValue = this.keybindBase.getKeyCode();
      if (pressedValue < 0 || mc.getWindow() == null) {
         return false;
      } else {
         return PageRaltUtils.canFlag(pressedValue)
            ? GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), PageRaltUtils.getValue(pressedValue)) == 1
            : InputUtil.isKeyPressed(mc.getWindow(), pressedValue);
      }
   }

   @Override
   protected void onSwordBot(SwordBot swordBot) {
      if (this.isEnabled() && swordBot.getValue() == 1) {
         if (mc.player != null && mc.currentScreen == null) {
            if (this.keybindBase.isBoundTo(swordBot.getKey())) {
               if (this.knifeInspectClip == null || !this.isPreviewKnife()) {
                  if (!this.swappedSlotFlag || !C2Butterfly.isFlag()) {
                     this.updateKnifePreview();
                  }
               }
            }
         }
      }
   }

   public void handleMultiplyTranslate(MatrixStack matrices, int multiplyTranslateValue, Arm arm) {
      float roundPreview = this.getRoundPreview();
      float anim = this.getAnim(roundPreview);
      float flickScaled = this.ANIMMultiplyAnim.flick() / 34.0F;
      float multiplyTranslateFactor = (-multiplyTranslateValue * 16.0F * getAmount(roundPreview, 0.0F, 0.24F) + multiplyTranslateValue * 34.0F * getAmount(roundPreview, 0.12F, 0.6F) - multiplyTranslateValue * 14.0F * getAmount(roundPreview, 0.6F, 1.0F)) * flickScaled;
      float multiplyTranslateFactor2 = (-9.0F * getAmount(roundPreview, 0.0F, 0.32F) + 7.0F * getAmount(roundPreview, 0.3F, 1.0F)) * flickScaled;
      float x = multiplyTranslateValue * 0.56F;
      float y = -0.52F;
      float z = -0.72F;
      matrices.translate(x, y, z);
      matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(multiplyTranslateFactor * anim));
      matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(multiplyTranslateFactor2 * anim));
      matrices.translate(-x, -y, -z);
   }

   public String formatEnum() {
      return this.enumHelper.getCurrentOption();
   }

   private static boolean isContains(ItemStack stack) {
      return stack.getComponents().contains(DataComponentTypes.WEAPON);
   }

   @Override
   protected void onDisable() {
      this.updateSwappedSlot();
      this.cueClockKnifeCueClock.handle(this.cueClockSink);
      this.knifeCueClock.handle(this.swishRoundSink);
   }

   public ItemStack getModelProbe(ItemStack itemStack, KnifeRig knifeRig, int modelProbeValue) {
      ItemStack copy = itemStack.copy();
      copy.set(DataComponentTypes.ITEM_MODEL, PROBE_KNIFE_MAP.computeIfAbsent(knifeRig.getParentParts(modelProbeValue).model(), modelProbeText -> Identifier.of("corzclient", modelProbeText)));
      return copy;
   }

   public boolean canSwappedSlot(AbstractClientPlayerEntity abstractClientPlayerEntity, Hand hand, ItemStack itemStack) {
      return this.swappedSlotFlag && C2Butterfly.canFlag(abstractClientPlayerEntity, hand, itemStack);
   }

   public boolean isPreviewKnife() {
      if (this.previewKnifeTime < 0L) {
         return false;
      } else {
         long previewKnifeLimit = getTime() - this.previewKnifeTime;
         if (previewKnifeLimit >= 0L && previewKnifeLimit < this.round) {
            return true;
         } else {
            this.previewKnifeTime = -1L;
            return false;
         }
      }
   }

   private static long getTime() {
      return System.nanoTime() / 1000000L;
   }

   @Override
   public JsonElement saveExtraData() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("soundVersion", 4);
      if (this.lastKnife != null) {
         jsonObject.addProperty("lastKnife", this.lastKnife);
      }

      return jsonObject;
   }

   public boolean isEnum() {
      return !"Off".equals(this.enumHelper.getCurrentOption());
   }

   static float[] getAmounts(List<Cue> list, float swordInspectFactor) {
      float[] floats = new float[list.size()];

      for (int i = 0; i < floats.length; i++) {
         floats[i] = ((Cue)list.get(i)).p() * swordInspectFactor;
      }

      return floats;
   }

   private KnifeRig getInspectKnife(LivingEntity livingEntity, ItemStack itemStack) {
      if (mc.player != null && livingEntity == mc.player && this.canSwordsEmpty(itemStack)) {
         KnifeInspectClip knifeInspectClip = this.knifeInspectClip;
         return knifeInspectClip != null && this.isPreviewKnife()
            ? KnifeRig.getBayonetStiletto(randomInspectTexts[this.previewKnifeValue])
            : KnifeRig.getBayonetStiletto(this.formatButterflyRandom(null));
      } else {
         return null;
      }
   }

   public String getKnifeFinish() {
      return this.knifeFinish.getCurrentOption();
   }

   public String getAnimationEnum() {
      return this.animationEnum.getCurrentOption();
   }

   static float getAmount(float swordInspectLimit, float swordInspectLimit2, float swordInspectValue) {
      return !(swordInspectLimit <= swordInspectLimit2) && !(swordInspectLimit >= swordInspectValue) ? (float)Math.sin(Math.PI * ((swordInspectLimit - swordInspectLimit2) / (swordInspectValue - swordInspectLimit2))) : 0.0F;
   }

   public void handleAnimInspect(MatrixStack matrices, int animInspectValue, Arm arm) {
      handleMultiplyDegrees(matrices, randomInspectTexts[this.previewKnifeValue], this.getRoundPreview(), this.spinCount, this.ANIMMultiplyAnim);
   }

   private C2Knife getButterflyPlayer(LivingEntity livingEntity, ItemStack itemStack) {
      if (mc.player != null && livingEntity == mc.player && this.canSwordsEmpty(itemStack)) {
         C2Knife c2Knife = C2Knife.getC2Knife(this.enumHelper.getCurrentOption());
         if (c2Knife != null) {
            return c2Knife;
         } else {
            return this.getInspectKnife(livingEntity, itemStack) == KnifeRig.BUTTERFLY_TP ? C2Knife.BUTTERFLY : null;
         }
      } else {
         return null;
      }
   }

   private void updateScreenInstance() {
      MinecraftClient instance = MinecraftClient.getInstance();
      instance.setScreen(new KnifePreview(instance.currentScreen, this));
   }

   static float getAmount(float swordInspectValue) {
      return 1.0F - (float)Math.pow(1.0F - swordInspectValue, 3.0);
   }

   @Override
   protected void onReset() {
      this.updateSwappedSlot();
      if (this.swappedSlotFlag) {
         C2Butterfly.handleInstanceFlag(this.holdToLoop.isEnabled() && mc.currentScreen == null && this.isKeyPressed());
      }

      this.updateCueClock();
      this.updateKnifeSwish();
      if (this.isEnabled() && mc.player != null && mc.currentScreen == null) {
         this.updateDeployButterfly();
         if (!mc.isPaused()) {
            long previewSpinTime = getTime();
            boolean iIiFlag = mc.player.handSwinging || mc.player.isUsingItem() || this.swappedSlotFlag && C2Butterfly.isFlag();
            if (iIiFlag && this.isPreviewKnife()) {
               this.previewKnifeTime = -1L;
               this.previewSpinTime = previewSpinTime;
            } else if (this.holdToLoop.isEnabled() && !iIiFlag && !this.isPreviewKnife() && this.isKeyPressed()) {
               this.updateKnifePreview();
            } else if (this.booleanBase.isEnabled()) {
               if (!this.isPreviewKnife() && !iIiFlag && !(mc.player.getVelocity().horizontalLengthSquared() > 1.0E-4)) {
                  if (previewSpinTime - this.previewSpinTime >= this.integerHelper.getValue() * 1000L) {
                     this.updateKnifePreview();
                  }
               } else {
                  this.previewSpinTime = previewSpinTime;
               }
            }
         }
      }
   }

   public SwordInspect() {
      super("Sword Inspect", IconsPngType.VISUAL);
      this.enumHelper = new EnumSetting(
         "Knife Model",
         "Hold a knife instead of your sword (swords only). Auto = the knife picked in Animation, or your last knife (Butterfly at first) when Animation is Random. The C2 knives (Bayonet to Treasure Knife, by dc) bring their own draw, inspects and slashes. Knives always play their own inspect.",
         "Auto",
         knifeInspectTexts
      );
      this.knifeFinish = new EnumSetting(
         "Knife Finish",
         "CS-style knife skins for the M9 Bayonet, Stiletto and Butterfly in your hand (first person). The C2 knives keep their own skin.",
         "Vanilla",
         KnifeFinish.getClone()
      );
      this.keybindBase = new KeybindSetting("Inspect Key", "Press this to inspect your held sword. Tap for one trick; hold (with Hold To Loop) to keep going.", 82);
      this.holdToLoop = new BooleanSetting(
         "Hold To Loop", "Keep looping the inspect for as long as the Inspect Key is held; release to let the last one settle.", true
      );
      this.swordsOnly = new BooleanSetting("Swords Only", "Only inspect melee weapons (swords, axes, tridents, maces). Off = any held item.", true);
      this.booleanBase = new BooleanSetting("Auto Inspect", "Occasionally play a random inspect on its own while you hold still.", false);
      this.integerHelper = new IntegerSetting("Auto Every", "Auto-inspect interval, in seconds.", 25, 5, 180, 5);
      this.knifeSounds = new EnumSetting(
         "Knife Sounds",
         "Subtle knife sounds in first person (draw, slash, a jingle of keys while the butterfly flips). Synth = Corz knife sounds, Minecraft = game sounds.",
         "Synth",
         "Synth",
         "Minecraft",
         "Off"
      );
      this.doubleHelper = new DoubleSetting(
         "Knife Volume", "How loud the knife sounds are. 0.8 is the quiet level they were made for; 1.0 is about twice as loud (+10 dB).", 0.8, 0.0, 1.0, 0.05
      );
      this.buttonBase = new ButtonBase(
         "Preview", "Open the live preview: every knife and inspect playing with its sounds; pick one to use it.", "Preview", this::updateScreenInstance
      );
      this.previewKnifeTime = -1L;
      this.previewKnifeValue = 1;
      this.round = 900;
      this.spinCount = 1;
      this.ANIMMultiplyAnim = ANIM;
      this.previewSpinTime = 0L;
      this.knifeInspectClip = null;
      this.lastKnife = null;
      this.knifePreviewCueList = List.of();
      this.swishAmounts = new float[0];
      this.swishTime = 0L;
      this.deployButterflyValue = 0L;
      this.knifeCueClock = new KnifeCueClock();
      this.swishRoundSink = new Sink() {
         @Override
         public void handle(long swordInspectValue) {
            KnifeSounds.handleSoundInstance(swordInspectValue);
         }

         @Override
         public void handleInspectSword(int index) {
            var list = SwordInspect.this.knifePreviewCueList;
            if (index >= 0 && index < list.size()) {
               Cue cue = (Cue)list.get(index);
               SwordInspect.this.handleEnum(cue.sound(), cue.volume(), cue.pitch(), SwordInspect.this.knifeCueClock.getTime());
            }
         }
      };
      this.swishAmount = 0.0F;
      this.deployButterflyText = null;
      this.hashCode = 0;
      this.deployButterflyTime = 0L;
      this.swishTimeSwishDeploy = 0L;
      this.swappedSlotFlag = false;
      this.cueClockContactList = List.of();
      this.cueClockTable = new float[0];
      this.cueClockKnifeCueClock = new KnifeCueClock();
      this.cueClockSink = new Sink() {
         @Override
         public void handle(long soundValue) {
            KnifeSounds.handleSoundInstance(soundValue);
         }

         @Override
         public void handleInspectSword(int i) {
            var cueClockContactList = SwordInspect.this.cueClockContactList;
            if (i >= 0 && i < cueClockContactList.size()) {
               C2Cues.Contact contact = (C2Cues.Contact)cueClockContactList.get(i);
               SwordInspect.this.handleEnum(contact.sound(), contact.volume(), contact.pitch(), SwordInspect.this.cueClockKnifeCueClock.getTime());
            }
         }
      };
      this.addSettings(
         new ConfigSetting[]{
            this.animationEnum,
            this.enumHelper,
            this.knifeFinish,
            this.buttonBase,
            this.knifeSounds,
            this.doubleHelper,
            this.keybindBase,
            this.holdToLoop,
            this.swordsOnly,
            this.booleanBase,
            this.integerHelper
         }
      );
      KnifeMesh.updateInstanceModel();
      C2Butterfly.handleFunction(this::getButterflyPlayer);
   }

   static float getAnticipationOvershoot(float anticipationOvershootValue, int maxTurns, SwordInspect.Anim anim) {
      float anticipationOvershootValue3 = 360.0F * maxTurns * getAmountForFloat(anticipationOvershootValue);
      float anticipationOvershootValue4 = -anim.anticipation() * getAmount(anticipationOvershootValue, 0.0F, 0.26F);
      float anticipationOvershootValue5 = anim.overshoot() * getAmount(anticipationOvershootValue, 0.6F, 1.0F);
      return anticipationOvershootValue3 + anticipationOvershootValue4 + anticipationOvershootValue5;
   }

   public boolean isSwappedButterfly() {
      return this.swappedSlotFlag && C2Butterfly.canFlag(C2Knife.BUTTERFLY);
   }

   static SwordInspect.Anim getAnimStiletto(String animStilettoText) {
      SwordInspect.Anim anim = animStilettoText == null ? null : STILETTO_MAP.get(animStilettoText);
      return anim != null ? anim : ANIM;
   }

   public String getKnifeSounds() {
      return this.knifeSounds.getCurrentOption();
   }

   public C2Knife getEnum() {
      return C2Knife.getC2Knife(this.enumHelper.getCurrentOption());
   }

   public void handleAutoEnum(String lastKnife) {
      if (lastKnife != null && C2Knife.getC2Knife(lastKnife) != null) {
         this.enumHelper.handle(lastKnife);
      } else if (lastKnife != null && Arrays.asList(randomInspectTexts).contains(lastKnife)) {
         String formatText = this.enumHelper.getCurrentOption();
         if (C2Knife.getC2Knife(formatText) != null) {
            this.enumHelper.handle("Auto");
            formatText = "Auto";
         }

         if (KnifeInspectClip.canBayonetStiletto(lastKnife)) {
            if (!"Auto".equals(formatText) && !lastKnife.equals(formatText)) {
               this.enumHelper.handle("Auto");
            }

            this.lastKnife = lastKnife;
         } else if (!"Random".equals(lastKnife) && KnifeInspectClip.canBayonetStiletto(formatText)) {
            this.enumHelper.handle("Auto");
         }

         this.animationEnum.handle(lastKnife);
      }
   }

   private float getAnim(float animValue) {
      return getRiseSettle(animValue, this.ANIMMultiplyAnim);
   }

   public ItemStack getModelThird(LivingEntity livingEntity, ItemStack modelThird, ItemDisplayContext itemDisplayContext) {
      if (itemDisplayContext != ItemDisplayContext.THIRD_PERSON_RIGHT_HAND && itemDisplayContext != ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
         return modelThird;
      } else if (mc.player != null && livingEntity == mc.player && modelThird == mc.player.getMainHandStack()) {
         KnifeRig inspectKnife = this.getInspectKnife(livingEntity, modelThird);
         if (inspectKnife == null) {
            return modelThird;
         } else {
            Identifier ifAbsent = PROBE_KNIFE_MAP.computeIfAbsent(inspectKnife.getChildParentTextChildParent(), modelThirdText -> Identifier.of("corzclient", modelThirdText));
            BakedModelManager bakedModelManager = mc.getBakedModelManager();
            if (bakedModelManager != null && bakedModelManager.getItemModel(ifAbsent) != bakedModelManager.getItemModel(identifier)) {
               ItemStack copy = modelThird.copy();
               copy.set(DataComponentTypes.ITEM_MODEL, ifAbsent);
               return copy;
            } else {
               return modelThird;
            }
         }
      } else {
         return modelThird;
      }
   }

   private boolean canSwordsEmpty(ItemStack itemStack) {
      return this.isEnabled() && !itemStack.isEmpty() && itemStack.isIn(ItemTags.SWORDS) && !"Off".equals(this.enumHelper.getCurrentOption());
   }

   public String formatSlotSwapped(String slotSwappedText) {
      if ("slot".equals(slotSwappedText) && mc.player != null) {
         int selectedSlot = mc.player.getInventory().getSelectedSlot();
         mc.player.getInventory().setSelectedSlot(selectedSlot == 0 ? 1 : 0);
         return "slot swapped";
      } else {
         if (!mc.isWindowFocused()) {
            mc.onWindowFocusChanged(true);
         }

         this.updateKnifePreview();
         return "inspect c2="
            + this.swappedSlotFlag
            + " status="
            + C2Butterfly.formatTextString()
            + " inspecting="
            + C2Butterfly.isFlag()
            + " clip="
            + C2Butterfly.formatText()
            + " focused="
            + mc.isWindowFocused();
      }
   }

   public void handleMultiplyAnim(MatrixStack matrices, int multiplyAnimFactor, Arm arm) {
      float roundPreview = this.getRoundPreview();
      float anim = this.getAnim(roundPreview);
      float multiplyAnimValue = getAmount(roundPreview, 0.0F, 0.26F);
      matrices.translate(multiplyAnimFactor * (-0.22F * anim + 0.05F * multiplyAnimValue), 0.16F * anim - 0.05F * multiplyAnimValue, -0.34F * anim + 0.09F * multiplyAnimValue);
      float sideScaled = multiplyAnimFactor * 0.56F;
      float y = -0.52F;
      float z = -0.72F;
      matrices.translate(sideScaled, y, z);
      matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(multiplyAnimFactor * this.ANIMMultiplyAnim.roll() * anim));
      matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(multiplyAnimFactor * this.ANIMMultiplyAnim.yaw() * anim));
      matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(this.ANIMMultiplyAnim.pitch() * anim));
      matrices.translate(-sideScaled, -y, -z);
   }

   static float getAmountForFloat(float swordInspectFactor) {
      return swordInspectFactor < 0.5F ? 4.0F * swordInspectFactor * swordInspectFactor * swordInspectFactor : 1.0F - (float)Math.pow(-2.0F * swordInspectFactor + 2.0F, 3.0) / 2.0F;
   }

   public static List<String> getStiletto() {
      return STILETTO_ITEMS;
   }

   public static void handle(LivingEntity livingEntity, Hand hand) {
      SwordInspect swordInspect = (SwordInspect)ModuleRegistry.getModule(SwordInspect.class);
      if (swordInspect != null) {
         swordInspect.handleSwishDeploy(livingEntity, hand);
      }
   }

   @Override
   protected void onEnable() {
      this.updateSwappedSlot();
   }

   private void updateKnifePreview() {
      ItemStack itemStack = mc.player != null ? mc.player.getMainHandStack() : ItemStack.EMPTY;
      if (itemStack != null && !itemStack.isEmpty()) {
         if (!this.swordsOnly.isEnabled() || isContains(itemStack)) {
            boolean swordsEmpty = this.canSwordsEmpty(itemStack);
            if (swordsEmpty && this.swappedSlotFlag && C2Knife.getC2Knife(this.enumHelper.getCurrentOption()) != null) {
               if (C2Butterfly.canFlag(mc.player, Hand.MAIN_HAND, itemStack)) {
                  this.knifeInspectClip = null;
                  this.previewKnifeTime = -1L;
                  this.previewSpinTime = getTime();
                  C2Butterfly.update();
               }
            } else {
               String formatRandomInspect = this.formatRandomInspect(swordsEmpty && "Auto".equals(this.enumHelper.getCurrentOption()));
               String lastKnife = swordsEmpty ? this.formatButterflyRandom(formatRandomInspect) : null;
               if (lastKnife != null && this.swappedSlotFlag && KnifeRig.getBayonetStiletto(lastKnife) == KnifeRig.BUTTERFLY_TP) {
                  String lastKnife2 = this.lastKnife;
                  this.lastKnife = lastKnife;
                  if (C2Butterfly.canFlag(mc.player, Hand.MAIN_HAND, itemStack)) {
                     this.knifeInspectClip = null;
                     this.previewKnifeTime = -1L;
                     this.previewSpinTime = getTime();
                     C2Butterfly.update();
                     return;
                  }

                  this.lastKnife = lastKnife2;
               }

               String knifePreviewText = lastKnife != null ? lastKnife : (KnifeInspectClip.canBayonetStiletto(formatRandomInspect) ? "Spin" : formatRandomInspect);
               this.previewKnifeValue = getInspectRandom(knifePreviewText);
               String formatText = this.animationEnum.getCurrentOption();
               SwordInspect.Anim animStiletto = getAnimStiletto(knifePreviewText);
               this.ANIMMultiplyAnim = "Random".equals(formatText) ? animStiletto : getAnimStiletto(formatText);
               this.round = animStiletto.ms();
               int spins = animStiletto.spins();
               this.spinCount = Math.max(1, spins);
               this.knifeInspectClip = lastKnife != null ? KnifeInspectClip.getKnifeInspectClip(lastKnife, animStiletto.anticipation(), animStiletto.overshoot(), spins) : null;
               if (this.knifeInspectClip != null) {
                  this.round = Math.round(this.round * KnifeInspectClip.getAmount(lastKnife, spins));
               }

               if (this.knifeInspectClip != null) {
                  this.lastKnife = lastKnife;
               }

               this.knifePreviewCueList = this.knifeInspectClip != null ? KnifeSoundCues.getMinecraftBlock(lastKnife, this.knifeInspectClip, this.round / 1000.0) : List.of();
               this.swishAmounts = getAmounts(this.knifePreviewCueList, this.round / 1000.0F);
               this.previewKnifeTime = getTime();
               this.swishTime = KnifeSounds.getTime();
               this.previewSpinTime = this.previewKnifeTime;
               this.updateKnifeSwish();
            }
         }
      }
   }

   private static String[] getArray() {
      var arrayList = new ArrayList<>(List.of("Off", "Auto", "M9 Bayonet", "Stiletto", "Butterfly"));
      arrayList.addAll(C2Knife.getUnmodifiable());
      return arrayList.toArray(String[]::new);
   }

   private void handle(String swordInspectText, float swordInspectValue, float enumValue) {
      this.handleEnum(swordInspectText, swordInspectValue, enumValue, 0L);
   }

   public KnifeRig getHand(LivingEntity livingEntity, Hand hand, ItemStack itemStack) {
      return hand != Hand.MAIN_HAND ? null : this.getInspectKnife(livingEntity, itemStack);
   }

   public void handleEnum(int enumValue) {
      String[] clone = KnifeFinish.getClone();
      if (clone.length != 0 && enumValue != 0) {
         int enumValue2 = 0;

         for (int i = 0; i < clone.length; i++) {
            if (clone[i].equals(this.knifeFinish.getCurrentOption())) {
               enumValue2 = i;
            }
         }

         this.knifeFinish.handle(clone[Math.floorMod(enumValue2 + enumValue, clone.length)]);
      }
   }

   private void updateDeployButterfly() {
      int hashCode = System.identityHashCode(mc.player);
      if (hashCode != this.hashCode) {
         this.hashCode = hashCode;
         this.deployButterflyText = null;
      }

      ItemStack mainHandStack = mc.player.getMainHandStack();
      String text = mc.player.getInventory().getSelectedSlot() + "|" + mainHandStack.getItem();
      if (!text.equals(this.deployButterflyText)) {
         boolean noText = this.deployButterflyText == null;
         this.deployButterflyText = text;
         KnifeSounds.handleSoundInstance(this.deployButterflyValue);
         this.deployButterflyValue = 0L;
         boolean deployButterflyFlag = !noText && mc.options.getPerspective().isFirstPerson();
         KnifeRig knifeRig = deployButterflyFlag ? this.getInspectKnife(mc.player, mainHandStack) : null;
         C2Knife c2Knife = deployButterflyFlag && knifeRig == null ? this.getButterflyPlayer(mc.player, mainHandStack) : null;
         boolean hasVar = c2Knife != null;
         long deployButterflyTime = getTime();
         if ((knifeRig != null || hasVar) && deployButterflyTime - this.deployButterflyTime >= 400L) {
            this.deployButterflyTime = deployButterflyTime;
            boolean deployButterflyFlag2 = knifeRig == KnifeRig.BUTTERFLY_TP || hasVar && c2Knife.isButterflySnowfall();
            this.deployButterflyValue = KnifeSounds.getTime();
            this.handleEnum(deployButterflyFlag2 ? "deploy_butterfly" : "deploy", deployButterflyFlag2 ? 0.5F : 0.45F, 1.0F, this.deployButterflyValue);
         }
      }
   }

   public void handleGlintEnum(AbstractClientPlayerEntity abstractClientPlayerEntity, ItemStack itemStack, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int glintEnumValue) {
      C2Butterfly.handle(abstractClientPlayerEntity, matrixStack, orderedRenderCommandQueue, glintEnumValue, KnifeFinish.getFinish(this.knifeFinish.getCurrentOption()), itemStack != null && itemStack.hasGlint());
      this.updateCueClock();
   }

   static float[] getTime(List<C2Cues.Contact> list) {
      float[] floats = new float[list.size()];

      for (int i = 0; i < floats.length; i++) {
         floats[i] = ((C2Cues.Contact)list.get(i)).time();
      }

      return floats;
   }

   public IntToDoubleFunction getSwishRound(float swishAmount) {
      boolean swishRoundFlag = swishAmount > 0.0F && (this.swishAmount <= 0.0F || swishAmount < this.swishAmount - 0.2F);
      this.swishAmount = swishAmount;
      if (swishRoundFlag && getTime() - this.swishTimeSwishDeploy >= 300L) {
         this.swishTimeSwishDeploy = getTime();
         this.handle("swish", 0.26F, 1.0F);
      }

      KnifeInspectClip knifeInspectClip = this.knifeInspectClip;
      if (knifeInspectClip != null && this.isPreviewKnife()) {
         float roundPreview = this.getRoundPreview();
         this.knifeCueClock
            .handleAmountAmounts(this.swishTime, this.swishAmounts, this.round / 1000.0F, roundPreview * this.round / 1000.0F, this.isPausedEnum(), this.swishRoundSink);
         return swishRoundValue -> knifeInspectClip.getTracks(swishRoundValue, roundPreview);
      } else {
         this.updateKnifeSwish();
         return swishRoundValue2 -> 0.0;
      }
   }

   private boolean isPausedEnum() {
      return "Off".equals(this.knifeSounds.getCurrentOption()) || mc.isPaused();
   }

   private static int getInspectRandom(String inspectRandomText) {
      for (int i = 1; i < randomInspectTexts.length; i++) {
         if (randomInspectTexts[i].equals(inspectRandomText)) {
            return i;
         }
      }

      return 1;
   }

   public boolean canModelBaked(KnifeRig knifeRig) {
      BakedModelManager bakedModelManager = mc.getBakedModelManager();
      if (bakedModelManager == null) {
         return false;
      } else {
         ItemModel itemModel = bakedModelManager.getItemModel(identifier);

         for (int i = 0; i < knifeRig.getParentParts(); i++) {
            Identifier ifAbsent = PROBE_KNIFE_MAP.computeIfAbsent(knifeRig.getParentParts(i).model(), modelBakedText -> Identifier.of("corzclient", modelBakedText));
            if (bakedModelManager.getItemModel(ifAbsent) == itemModel) {
               return false;
            }
         }

         return true;
      }
   }

   static void handleMultiplyDegrees(MatrixStack matrices, String multiplyDegreesText, float multiplyDegreesFactor, int multiplyDegreesBound, SwordInspect.Anim anim) {
      float riseSettle = getRiseSettle(multiplyDegreesFactor, anim);
      int maxTurns = Math.max(1, multiplyDegreesBound);
      float multiplyDegreesFactor2 = (float) (Math.PI * 2);
      switch (multiplyDegreesText) {
         case "Spin":
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(getAnticipationOvershoot(multiplyDegreesFactor, maxTurns, anim)));
            break;
         case "Flip":
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(getAnticipationOvershoot(multiplyDegreesFactor, maxTurns, anim)));
            break;
         case "Barrel Roll":
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(getAnticipationOvershoot(multiplyDegreesFactor, maxTurns, anim)));
            break;
         case "Twirl":
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(getAnticipationOvershoot(multiplyDegreesFactor, maxTurns, anim)));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(16.0F * MathHelper.sin(multiplyDegreesFactor2 * multiplyDegreesFactor) * riseSettle));
            break;
         case "Toss":
            float multiplyDegreesFactor3 = 4.0F * multiplyDegreesFactor * (1.0F - multiplyDegreesFactor);
            matrices.translate(0.0F, 0.62F * multiplyDegreesFactor3, -0.12F * multiplyDegreesFactor3);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(getAnticipationOvershoot(multiplyDegreesFactor, maxTurns, anim)));
            break;
         default:
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(getAnticipationOvershoot(multiplyDegreesFactor, maxTurns, anim)));
      }
   }

   public String formatEnumEnumHelper() {
      String formatText = this.enumHelper.getCurrentOption();
      return C2Knife.getC2Knife(formatText) != null ? formatText : this.animationEnum.getCurrentOption();
   }

   public void handleAnimTranslate(MatrixStack matrices, int animTranslateFactor, Arm arm) {
      float anim = this.getAnim(this.getRoundPreview());
      matrices.translate(animTranslateFactor * this.ANIMMultiplyAnim.side() * anim, this.ANIMMultiplyAnim.lift() * anim, this.ANIMMultiplyAnim.forward() * anim);
   }

   public boolean canEmptyHand(Hand hand, ItemStack stack) {
      if (!this.isEnabled() || hand != Hand.MAIN_HAND) {
         return false;
      } else if (stack == null || stack.isEmpty()) {
         return false;
      } else {
         return this.swordsOnly.isEnabled() && !isContains(stack) ? false : this.isPreviewKnife();
      }
   }

   private float getRoundPreview() {
      return getAmountFloat((float)(getTime() - this.previewKnifeTime) / Math.max(1, this.round));
   }

   public String formatEnumKnifeFinish() {
      return this.knifeFinish.getCurrentOption();
   }

   private void handleEnum(String enumText, float enumFactor, float enumValue, long deployKnifeValue) {
      KnifeSounds.handleMinecraftPlay(this.knifeSounds.getCurrentOption(), enumText, enumFactor * KnifeSoundCues.getAmount(this.doubleHelper.getValue()), enumValue, deployKnifeValue);
   }

   private void updateCueClock() {
      long cueClockValue = this.swappedSlotFlag ? C2Butterfly.getTime() : 0L;
      if (cueClockValue != this.cueClockKnifeCueClock.getTime()) {
         this.cueClockContactList = cueClockValue != 0L ? C2Butterfly.getItems() : List.of();
         this.cueClockTable = getTime(this.cueClockContactList);
      }

      this.cueClockKnifeCueClock
         .handleAmountAmounts(cueClockValue, this.cueClockTable, C2Butterfly.getAmount(), cueClockValue != 0L ? C2Butterfly.getAmountFloat() : 0.0F, this.isPausedEnum(), this.cueClockSink);
   }

   public String formatAutoEnum(String autoEnumText) {
      String formatText = this.enumHelper.getCurrentOption();
      if ("Off".equals(formatText)) {
         return null;
      } else if (!"Auto".equals(formatText)) {
         return KnifeInspectClip.canBayonetStiletto(formatText) ? formatText : null;
      } else {
         return KnifeInspectClip.canBayonetStiletto(autoEnumText) ? autoEnumText : null;
      }
   }

   static float getAmountFloat(float swordInspectLimit) {
      return swordInspectLimit < 0.0F ? 0.0F : (swordInspectLimit > 1.0F ? 1.0F : swordInspectLimit);
   }

   private String formatButterflyRandom(String formatButterflyRandom) {
      String formatText = this.enumHelper.getCurrentOption();
      if (!"Auto".equals(formatText)) {
         return KnifeInspectClip.canBayonetStiletto(formatText) ? formatText : null;
      } else {
         String currentOption = this.animationEnum.getCurrentOption();
         if (KnifeInspectClip.canBayonetStiletto(currentOption)) {
            return currentOption;
         } else if (!"Random".equals(currentOption)) {
            return null;
         } else if (KnifeInspectClip.canBayonetStiletto(formatButterflyRandom)) {
            return formatButterflyRandom;
         } else {
            return this.lastKnife != null ? this.lastKnife : "Butterfly";
         }
      }
   }

   private String formatRandomInspect(boolean randomInspectFlag) {
      String formatText = this.animationEnum.getCurrentOption();
      if (!"Random".equals(formatText)) {
         return formatText;
      } else {
         var arrayList = new ArrayList();

         for (int i = 1; i < randomInspectTexts.length; i++) {
            if (KnifeInspectClip.canBayonetStiletto(randomInspectTexts[i]) == randomInspectFlag) {
               arrayList.add(randomInspectTexts[i]);
            }
         }

         return arrayList.isEmpty() ? randomInspectTexts[1] : (String)arrayList.get(ThreadLocalRandom.current().nextInt(arrayList.size()));
      }
   }

   public static void handleSpins(MatrixStack matrices, String spinsText, float spinsValue) {
      SwordInspect.Anim animStiletto = getAnimStiletto(spinsText);
      handleMultiplyDegrees(matrices, spinsText, getAmountFloat(spinsValue), animStiletto.spins(), animStiletto);
   }

   private void updateKnifeSwish() {
      boolean knifeSwishFlag = this.knifeInspectClip != null && this.isPreviewKnife();
      long knifeSwishValue = knifeSwishFlag ? this.swishTime : 0L;
      float knifeSwishValue2 = knifeSwishFlag ? (float)(getTime() - this.previewKnifeTime) / 1000.0F : 0.0F;
      this.knifeCueClock.handleAmountAmounts(knifeSwishValue, this.swishAmounts, this.round / 1000.0F, knifeSwishValue2, this.isPausedEnum(), this.swishRoundSink);
   }

   public static float getSpins(String spinsText) {
      SwordInspect.Anim animStiletto = getAnimStiletto(spinsText);
      float ms = animStiletto.ms();
      if (KnifeInspectClip.canBayonetStiletto(spinsText)) {
         ms *= KnifeInspectClip.getAmount(spinsText, animStiletto.spins());
      }

      return ms / 1000.0F;
   }

   @Override
   public boolean hasExtraData() {
      return true;
   }

   public float getAmount() {
      return KnifeSoundCues.getAmount(this.doubleHelper.getValue());
   }

   private static List<String> getCopyOf() {
      var arrayList = new ArrayList<>(List.of("Butterfly", "M9 Bayonet", "Stiletto"));
      arrayList.addAll(C2Knife.getUnmodifiable());
      arrayList.addAll(List.of("Random", "Spin", "Flip", "Barrel Roll", "Twirl", "Toss"));
      return List.copyOf(arrayList);
   }

   record Anim(
      int ms,
      int spins,
      float lift,
      float forward,
      float side,
      float roll,
      float yaw,
      float pitch,
      float anticipation,
      float overshoot,
      float flick,
      float rise,
      float settle
   ) {
   }
}
