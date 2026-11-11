package com.corz.client.categories.bots;

import com.corz.client.CorzClient;
import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.ModuleConfigAutosave;
import com.corz.client.config.DoubleSetting;
import com.corz.client.config.VillagerTradeRule;
import com.corz.client.config.VillagerTradeRule.Action;
import com.corz.client.config.VillagerTradeRule.OutputKind;
import com.corz.client.config.EnumSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.config.KeybindSetting;
import com.corz.client.utils.BlockWalker.Status;
import com.corz.client.utils.MerchantTrader.Result;
import com.corz.client.utils.TradeEnchantUtils.Candidate;
import com.corz.client.utils.TradeEnchantUtils.SelectionMode;
import com.corz.client.utils.VillagerTradeUtils.Due;
import com.corz.client.gui.DisabledEnabled;
import com.corz.client.gui.TickProgress;
import com.corz.client.inventory.IngotBallUtils;
import com.corz.client.inventory.InventoryStackUtilsHandle;
import com.corz.client.inventory.OfferTrade;
import com.corz.client.inventory.ProtectionCurseUtils;
import com.corz.client.inventory.VillagerTrader;
import com.corz.client.render.ChestStorageUtils;
import com.corz.client.render.MatrixEspUtils;
import com.corz.client.render.QueueCommand;
import com.corz.client.render.VertexBuilderUtils;
import com.corz.client.render.VertexConsumerUtilsVector;
import com.corz.client.render.VertexTextureFont;
import com.corz.client.world.MathClampUtils;
import com.corz.client.world.RoleDimension;
import com.corz.client.world.RoleDimension.Role;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.awt.Color;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.PlaySoundFromEntityS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResult.Success;
import net.minecraft.util.ActionResult.SwingSource;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.TradeOfferList;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import org.joml.Matrix4f;
import com.corz.client.categories.bots.AutoElytraPilot;
import com.corz.client.categories.combat.AutoXP;
import com.corz.client.utils.AmountTradeUtils;
import com.corz.client.utils.EnchantJsonKeep;
import com.corz.client.utils.BlockWalker;
import com.corz.client.utils.MerchantTrader;
import com.corz.client.utils.TradeEnchantUtils;
import com.corz.client.utils.VillagerTracker;
import com.corz.client.utils.TrackedVillager;
import com.corz.client.utils.VillagerTradeUtils;
import com.corz.client.utils.AutoVanish;
import com.corz.client.categories.visuals.ShowHUD;
import com.corz.client.categories.visuals.SpotifyHUD;
import ai.auth.Module;
import ai.auth.ModuleRegistry;
import com.corz.client.utils.ClampPitch;
import com.corz.client.categories.bots.AutoBuilder;
import com.corz.client.categories.bots.AutoElytraFinder;
import com.corz.client.categories.bots.AutoAreaMiner;
import com.corz.client.utils.WalkRouteFinder;
import com.corz.client.utils.CardStatusUtilsFlag;
import com.corz.client.utils.DeltaAngle;
import com.corz.client.utils.ElytraAutoUtils;
import com.corz.client.utils.IconsPngType;
import com.corz.client.utils.NoneCartographerUtils;
import com.corz.client.utils.ScaffoldTrade;
import com.corz.client.utils.SwordBot;

public class VillagerTradeBot extends Module {
   private final IntegerSetting enchantLevelSetting;
   private final BooleanSetting pauseOnGuiSetting;
   private final List<VillagerTradeRule> tradeTargets;
   private VillagerTradeBot.Pick pick;
   private int lastWithdrawCount;
   private long pickStartMillis;
   private final BooleanSetting autoDropOutput;
   private int lastOutputStackCount;
   private final List<VillagerTradeBot.Owned> carriedItems;
   private long lastOutputWaitMessageTick;
   private final KeybindSetting addLookedAtTradeKey;
   private boolean pendingCarriedSnapshot;
   private long lastInteractTick;
   private long lastOutputChangeTick;
   private final ClampPitch aimController;
   private final Map<VillagerTradeRule, Long> targetPausedUntilTick;
   private int roundRobinCounter;
   private long statusTextCacheMillis;
   private final KeybindSetting clearAddedKey;
   private final Map<String, Long> messageLastTickByKey;
   private boolean openTraderScreenPending;
   private boolean awaitUseKeyRelease;
   private Object lastNetworkHandler;
   private final BooleanSetting villagerEsp;
   private final List<RoleDimension> markedStorages;
   private int standStillTicks;
   private long screenOpenedTick;
   private long carriedSnapshotMillis;
   private final EnumSetting storageRoleSetting;
   private boolean hasStatusMessage;
   private String cachedStatusText;
   private final Set<Long> labeledStorageKeys;
   private final DoubleSetting interactionRange;
   private final EnumSetting targetActionSetting;
   private String tradeSummaryText;
   private boolean carriedSnapshotActive;
   private final EnumSetting sellItemSetting;
   private final IntegerSetting targetAmountSetting;
   private int strayPickupAttempts;
   private final VillagerTradeRule acquireEmeraldsTarget;
   private final EnumSetting automationMode = new EnumSetting(
      "Automation Mode",
      "FULL_AUTO does everything; BUY_ONLY skips selling; SELL_ONLY only sells for emeralds; ASSISTED only trades a merchant screen you open yourself (no walking).",
      "Full Auto",
      "Full Auto",
      "Buy Only",
      "Sell Only",
      "Assisted"
   );
   private final EnumSetting targetItemSetting;
   private final EnumSetting enchantmentSetting;
   private Role pickStorageRole;
   private final EnumSetting outputKindSetting;
   private long statusMessageMillis;
   private final KeybindSetting markStorageKey;
   private final BooleanSetting pauseWhenMoving = new BooleanSetting(
      "Pause When Moving", "Stand down while you manually move, so you can take over without fighting the bot.", true
   );
   private final IntegerSetting interactionDelay;
   private String tradeSummaryKey;
   private long pickConfirmedMillis;
   private long lastVillagerInteractTick;
   private final BooleanSetting statusOverlay;
   private int movedItemCount;
   private String statusMessageText;
   private long lastVillagerScanTick;
   private final ConcurrentLinkedQueue<VillagerTradeBot.WorkSound> workSoundQueue;
   private final Map<VillagerTradeRule, String> pauseReasonByTarget;
   private long stateEnteredTick;
   private final BooleanSetting navigation;
   private final BooleanSetting materials;
   private final IntegerSetting tradeDelay;
   private long lastContainerOpenTick;
   private int lastDepositSlotCount;
   private boolean tradeSessionActive;
   private final EnumSetting targetProfessionSetting;
   private final IntegerSetting retryDelay;
   private final MerchantTrader stackMerchant;
   private RoleDimension strayDropStorage;
   private final EnumSetting emeraldSource;
   private final Deque<VillagerTradeBot.Goal> goalStack;
   private long nextDepositTick;
   private final List<VillagerTradeBot.Owned> knownCarriedItems;
   private final IntegerSetting restockCheckDelay;
   private InventoryScreen dropInventoryScreen;
   private String idleStatusText;
   private long strayPickupTick;
   private String pausedReasonText;
   private final Map<Item, Long> materialUnavailableUntilTick;
   private final VillagerTracker villagerEntityHelper;
   private VillagerTradeBot.State botState;
   private final IntegerSetting targetPrioritySetting;
   private final IntegerSetting emeraldReserve;
   private final IntegerSetting maxEmeraldCostSetting;
   private final EnumSetting selectionMode;
   private final IntegerSetting guiTimeout;
   private long idleUntilTick;
   private long lastScreenOutputTick;
   private String carriedSnapshotServerKey;
   private final BooleanSetting autoAcquireEmeralds;
   private long pickScreenOpenedMillis;
   private final BooleanSetting autoDepositOutputs;
   private final IntegerSetting searchRange;
   private long tickCounter;
   private long lastPickHintMillis;
   private final IntegerSetting turnSpeed;
   private final BlockWalker pathWalker;
   private boolean walkedThisTick;

   public VillagerTradeBot.Pick getPick() {
      return this.pick;
   }

   public Integer getStatusColor() {
      return !this.isEnabled() ? 0 : this.botState == VillagerTradeBot.State.PAUSED ? -1535700 : -8393375;
   }

   private void updateTargetAutoDrop() {
      boolean tradesDropFlag = this.autoDropOutput.isEnabled() && !this.isDropSpotActive();

      for (VillagerTradeRule enchantJsonProfession : this.tradeTargets) {
         enchantJsonProfession.autoDrop = tradesDropFlag && enchantJsonProfession.action == Action.BUY;
      }
   }

   public int moveTarget(int tradesTrade, int tradesTradeValue) {
      int presetsSelectedLimit = tradesTrade + tradesTradeValue;
      if (tradesTrade >= 0 && tradesTrade < this.tradeTargets.size() && presetsSelectedLimit >= 0 && presetsSelectedLimit < this.tradeTargets.size()) {
         Collections.swap(this.tradeTargets, tradesTrade, presetsSelectedLimit);
         this.reassignPriorities();
         ModuleConfigAutosave.updateFlag2();
         return presetsSelectedLimit;
      } else {
         return tradesTrade;
      }
   }

   private void tickPickupStrayItem() {
      VillagerTradeBot.Goal deque = this.goalStack.peek();
      if (deque == null) {
         this.setBotState(VillagerTradeBot.State.PLAN);
      } else if (mc.world.getEntityById(deque.itemEntityId) instanceof ItemEntity itemEntity && itemEntity.isAlive()) {
         this.pathWalker.setTarget(itemEntity.getBlockPos(), 0.35);
         Status posTime = this.pathWalker.tickWalk();
         this.walkedThisTick = true;
         if (posTime == Status.STUCK || posTime == Status.NO_PATH || this.getTicksInState() > 300L) {
            this.pathWalker.clearTarget();
            this.sendThrottledMessage(
               "stray:" + (deque.storage == null ? "" : deque.storage.formatRole()),
               "An item landed off the drop spot and the bot can't reach it @ " + itemEntity.getBlockPos().toShortString() + ".",
               false,
               6000L
            );
            this.popGoal();
            this.setBotState(VillagerTradeBot.State.PLAN);
         }
      } else {
         this.pathWalker.clearTarget();
         this.popGoal();
         this.setBotState(VillagerTradeBot.State.PLAN);
      }
   }

   private void closeScreenIfBotOwned() {
      if (this.isBotScreenOpen()) {
         this.closeOpenScreens();
      }
   }

   private void renderStorageLabels(MatrixStack matrixStack) {
      String droppingCheckingText = this.getDimensionId();
      RoleDimension storageState = this.getActiveStorage();
      this.labeledStorageKeys.clear();

      for (RoleDimension roleDimension : this.markedStorages) {
         if (roleDimension.canDimension(droppingCheckingText)) {
            BlockPos blockPos = roleDimension.getBlockPos();
            boolean droppingCheckingFlag = roleDimension.role == Role.DROP;
            if (droppingCheckingFlag || this.labeledStorageKeys.add(this.getChestKey(blockPos))) {
               boolean droppingCheckingFlag2 = roleDimension == storageState;
               boolean droppingCheckingFlag3 = this.isEnabled() && this.isStorageFull(roleDimension);
               double sqrt = Math.sqrt(roleDimension.getVec3d().squaredDistanceTo(mc.player.getEyePos()));
               if (!(sqrt > 16.0) || droppingCheckingFlag2 || droppingCheckingFlag3) {
                  float droppingCheckingValue = ChestStorageUtils.getAmount(sqrt);
                  if (!(droppingCheckingValue <= 0.0F)) {
                     Box box = droppingCheckingFlag ? new Box(blockPos) : ChestStorageUtils.getUnion(blockPos);
                     Vec3d vec3d = droppingCheckingFlag ? new Vec3d(blockPos.getX() + 0.5, blockPos.getY() + 1.6, blockPos.getZ() + 0.5) : ChestStorageUtils.getMaxMin(box);
                     Vec3d windowFramebuffer = VertexConsumerUtilsVector.getWindowFramebuffer(vec3d);
                     if (windowFramebuffer != null && !(windowFramebuffer.z < 0.0) && !(windowFramebuffer.z > 1.0)) {
                        String roundOffset = Math.round(sqrt) + "m";
                        String corzfontText;
                        if (droppingCheckingFlag3) {
                           long droppingCheckingLimit = Math.max(0L, 200L - (this.tickCounter - roleDimension.overworldMinecraftTimeOverwo)) / 20L;
                           corzfontText = droppingCheckingLimit > 0L ? "Full \u00b7 retry " + droppingCheckingLimit + "s" : "Full \u00b7 checking";
                        } else if (droppingCheckingFlag) {
                           corzfontText = droppingCheckingFlag2 && this.botState == VillagerTradeBot.State.DROP_OP ? "Dropping..." : (droppingCheckingFlag2 ? "Heading here \u00b7 " + roundOffset : roundOffset);
                        } else {
                           boolean hasOffset = ChestStorageUtils.getOffsetForBlockPos(blockPos) != null;
                           corzfontText = droppingCheckingFlag2 ? "Heading here \u00b7 " + roundOffset : (hasOffset ? "Double \u00b7 " : "") + roundOffset;
                        }

                        ChestStorageUtils.handleTranslatePush(
                           matrixStack,
                           ChestStorageUtils.formatChestStorage(roleDimension.role),
                           corzfontText,
                           droppingCheckingFlag3 ? -42406 : ChestStorageUtils.getStorageChest(roleDimension.role),
                           (float)windowFramebuffer.x,
                           (float)windowFramebuffer.y,
                           droppingCheckingValue
                        );
                     }
                  }
               }
            }
         }
      }
   }

   public int getTotalTradedCount() {
      int soldTrades = 0;

      for (VillagerTradeRule enchantJsonProfession : this.tradeTargets) {
         soldTrades += enchantJsonProfession.tradedAmount;
      }

      return soldTrades;
   }

   public void handlePickCamera(QueueCommand queueCommand) {
      if (mc.world != null && mc.player != null && this.shouldRenderPickUi()) {
         if (!this.markedStorages.isEmpty() || this.pick != VillagerTradeBot.Pick.NONE) {
            Vec3d cameraPos = mc.gameRenderer.getCamera().getCameraPos();
            MatrixStack matrices = queueCommand.matrices;
            matrices.push();
            matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
            Matrix4f positionMatrix = matrices.peek().getPositionMatrix();
            String cameraText = this.getDimensionId();
            RoleDimension storageState = this.getActiveStorage();
            float cameraValue = ChestStorageUtils.getAmountFloat();
            this.labeledStorageKeys.clear();

            for (RoleDimension roleDimension : this.markedStorages) {
               if (roleDimension.canDimension(cameraText) && !(roleDimension.getVec3d().squaredDistanceTo(cameraPos) > 9216.0)) {
                  BlockPos blockPos = roleDimension.getBlockPos();
                  boolean cameraFlag = roleDimension == storageState;
                  if (roleDimension.role == Role.DROP) {
                     ChestStorageUtils.handle(positionMatrix, blockPos, -13375745, cameraFlag ? 1.0F : cameraValue, cameraFlag);
                  } else if (this.labeledStorageKeys.add(this.getChestKey(blockPos))) {
                     Box union = ChestStorageUtils.getUnion(blockPos);
                     if (this.isEnabled() && this.isStorageFull(roleDimension)) {
                        float cameraValue2 = ChestStorageUtils.getAmount();
                        ChestStorageUtils.handleMinMax(positionMatrix, union, -42406, 0.8F + 0.2F * cameraValue2, 0.1F + 0.08F * cameraValue2, 2.0F);
                     } else {
                        ChestStorageUtils.handleMinMax(
                           positionMatrix, union, ChestStorageUtils.getStorageChest(roleDimension.role), cameraFlag ? 1.0F : cameraValue, cameraFlag ? 0.12F : 0.06F, cameraFlag ? 2.5F : 2.0F
                        );
                     }
                  }
               }
            }

            if (this.pick == VillagerTradeBot.Pick.STORAGE
               && mc.currentScreen == null
               && mc.crosshairTarget instanceof BlockHitResult blockHitResult
               && blockHitResult.getType() == Type.BLOCK) {
               BlockPos pos = blockHitResult.getBlockPos();
               if (this.pickStorageRole == Role.DROP) {
                  ChestStorageUtils.handle(positionMatrix, pos, -13375745, 1.0F, true);
               } else {
                  boolean entityBlock = this.isContainerBlock(pos);
                  ChestStorageUtils.handleMinMax(
                     positionMatrix,
                     ChestStorageUtils.getUnion(pos),
                     entityBlock ? ChestStorageUtils.getStorageChest(this.pickStorageRole) : -38037,
                     1.0F,
                     entityBlock ? 0.16F : 0.1F,
                     2.5F
                  );
               }
            }

            if (this.pick == VillagerTradeBot.Pick.TRADE
               && mc.currentScreen == null
               && mc.crosshairTarget instanceof EntityHitResult entityHitResult
               && entityHitResult.getEntity() instanceof VillagerEntity villagerEntity) {
               ChestStorageUtils.handleMinMax(positionMatrix, villagerEntity.getBoundingBox(), MatrixEspUtils.releaseRightValue, cameraValue, 0.08F, 2.0F);
            }

            matrices.pop();
            VertexBuilderUtils.updateDrawImmediate();
         }
      }
   }

   private boolean isConflictingModuleActive() {
      return this.isModuleActive(AutoBuilder.class)
         || this.isModuleActive(AutoElytraFinder.class)
         || this.isModuleActive(AutoElytraPilot.class)
         || this.isModuleActive(AutoAreaMiner.class);
   }

   private boolean queueShortageGoal(VillagerTradeBot.Goal goal) {
      TrackedVillager villagerExperience = this.villagerEntityHelper.findByUuid(goal.villagerUuid);
      OfferTrade offerTrade = villagerExperience == null ? null : TradeEnchantUtils.findBestTrade(villagerExperience, goal.target);
      if (offerTrade == null) {
         return false;
      } else if (offerTrade.count3EnchantJson > this.countItem(Items.EMERALD)) {
         this.handleMissingEmeralds(goal, offerTrade.count3EnchantJson);
         return true;
      } else {
         Item item = null;
         if (offerTrade.primaryCostItem != null && offerTrade.primaryCostItem != Items.EMERALD && this.countItem(offerTrade.primaryCostItem) < offerTrade.primaryCostCount) {
            item = offerTrade.primaryCostItem;
         } else if (offerTrade.isFlag() && offerTrade.secondaryCostItem != Items.EMERALD && this.countItem(offerTrade.secondaryCostItem) < offerTrade.count2UnconfirmedFull) {
            item = offerTrade.secondaryCostItem;
         }

         if (item == null) {
            return false;
         } else {
            this.queueMaterialWithdraw(goal, item);
            return true;
         }
      }
   }

   private boolean areAllBuyItemsStackable() {
      boolean buyAction = false;

      for (VillagerTradeRule enchantJsonProfession : this.getRunnableTargets()) {
         if (enchantJsonProfession.action == Action.BUY) {
            Item buyItem = enchantJsonProfession.getBuyItem();
            if (buyItem == null || buyItem.getMaxCount() <= 1) {
               return false;
            }

            buyAction = true;
         }
      }

      return buyAction;
   }

   private Box getVillagerBox(TrackedVillager villagerExperience) {
      VillagerEntity byId = this.villagerEntityHelper.findVillagerEntity(villagerExperience.uuid);
      if (byId == null) {
         return null;
      } else {
         double widthScaled = byId.getWidth() / 2.0;
         double height = byId.getHeight();
         Vec3d entityPos = byId.getEntityPos();
         return new Box(entityPos.x - widthScaled, entityPos.y, entityPos.z - widthScaled, entityPos.x + widthScaled, entityPos.y + height, entityPos.z + widthScaled);
      }
   }

   public boolean beginTradePick() {
      if (!this.isEnabled() && mc.player != null && mc.world != null) {
         this.pick = VillagerTradeBot.Pick.TRADE;
         this.pickStartMillis = System.currentTimeMillis();
         this.pickScreenOpenedMillis = 0L;
         return true;
      } else {
         return false;
      }
   }

   public static VillagerTradeRule createTargetFromOffer(OfferTrade offerTrade, String enchantJsonText) {
      return VillagerTradeRule.createFromOffer(offerTrade, enchantJsonText);
   }

   public int indexOfTarget(VillagerTradeRule enchantJsonProfession) {
      if (enchantJsonProfession == null) {
         return -1;
      } else {
         String formatProfessionEmerald = enchantJsonProfession.formatDescription();

         for (int i = 0; i < this.tradeTargets.size(); i++) {
            if (this.tradeTargets.get(i).formatDescription().equals(formatProfessionEmerald)) {
               return i;
            }
         }

         return -1;
      }
   }

   private void beginTradeSession() {
      if (!this.tradeSessionActive && InventoryStackUtilsHandle.canFlagForObject(this)) {
         this.tradeSessionActive = true;
      }
   }

   @Override
   public void onReset() {
      if (mc.player != null
         && mc.world != null
         && mc.interactionManager != null
         && mc.player.networkHandler != null) {
         if (!mc.player.isAlive()) {
            this.stopWithMessage("Died \u2014 stopping.");
         } else {
            if (this.lastNetworkHandler != null && mc.getNetworkHandler() != this.lastNetworkHandler) {
               this.pendingCarriedSnapshot = true;
            }

            if (this.pendingCarriedSnapshot) {
               if (mc.player.age < 20) {
                  this.releaseMovementKeys();
                  this.aimController.clearTargetAndOverride();
                  return;
               }

               this.snapshotCarriedItems();
            }

            this.tickCounter++;
            if (this.tickCounter % 20L == 0L) {
               this.refreshCarriedSnapshot();
            }

            if (this.isConflictingModuleActive()) {
               this.pauseWithReason("Paused: another automation is running.");
            } else if (this.isAutoXpConflict()) {
               this.pauseWithReason("Paused: Auto XP is on \u2014 it would throw the XP bottles you buy.");
            } else {
               this.aimController.setMaxStep(6.0F + this.turnSpeed.getValue() * 4.2F);
               this.aimController.clearTarget();
               boolean iIiFlag = this.botState == VillagerTradeBot.State.TO_VILLAGER
                  || this.botState == VillagerTradeBot.State.TO_STORAGE
                  || this.botState == VillagerTradeBot.State.PICKUP;
               boolean iIiFlag2 = !iIiFlag && !this.walkedThisTick && this.isMovementKeyPressed();
               boolean assistedState = this.isUserScreenOpen();
               boolean iIiFlag3 = this.pauseOnGuiSetting.isEnabled() && this.shouldPauseForGui();
               this.releaseMovementKeys();
               this.walkedThisTick = false;
               if (assistedState) {
                  this.closeOpenScreens();
               }

               this.updateTargetAutoDrop();
               this.scanVillagers();
               this.processWorkSounds();
               if ((!this.pauseWhenMoving.isEnabled() || !iIiFlag2) && !iIiFlag3) {
                  if (this.botState == VillagerTradeBot.State.PAUSED) {
                     this.setBotState(VillagerTradeBot.State.SCAN);
                  }

                  try {
                     switch (this.botState) {
                        case IDLE:
                           this.setBotState(VillagerTradeBot.State.SCAN);
                           break;
                        case SCAN:
                           this.setBotState(VillagerTradeBot.State.PLAN);
                           break;
                        case PLAN:
                           this.planNextGoal();
                           break;
                        case TO_VILLAGER:
                           this.tickWalkToVillager();
                           break;
                        case OPEN_VILLAGER:
                           this.tickOpenVillager();
                           break;
                        case TRADE:
                           this.tickTrade();
                           break;
                        case TO_STORAGE:
                           this.tickWalkToStorage();
                           break;
                        case OPEN_STORAGE:
                           this.tickOpenStorage();
                           break;
                        case STORAGE_OP:
                           this.tickStorageTransfer();
                           break;
                        case DROP_OP:
                           this.tickDropOutputs();
                           break;
                        case PICKUP:
                           this.tickPickupStrayItem();
                           break;
                        case WAIT_RESTOCK:
                           this.tickWaitRestock();
                           break;
                        case RECOVER:
                           this.tickRecover();
                        case PAUSED:
                     }
                  } catch (Exception e) {
                     this.failCurrentGoal("internal error (" + e.getClass().getSimpleName() + ")");
                  }

                  this.aimController.updateSmoothedRotation();
               } else {
                  this.pauseWithReason(iIiFlag3 ? "Paused: your GUI is open." : "Paused: you are moving.");
                  this.aimController.updateSmoothedRotation();
               }
            }
         }
      }
   }

   private void walkToDropSpot(VillagerTradeBot.Goal goal, RoleDimension roleDimension) {
      this.walkedThisTick = true;
      boolean posBlockFlag = goal.standPos != null
         ? this.isNearBlockCenter(goal.standPos, 0.25)
            || this.isNearBlockCenter(goal.standPos, 0.6) && mc.player.isOnGround() && this.getAimClearance(roleDimension) >= 0.0
         : this.isInRangeOfStorage(roleDimension);
      if (posBlockFlag) {
         this.pathWalker.clearTarget();
         this.setBotState(VillagerTradeBot.State.OPEN_STORAGE);
      } else {
         if (goal.standPos != null && this.isNearBlockCenter(goal.standPos, 0.9)) {
            this.pathWalker.clearTarget();
            this.nudgeTowardBlock(goal.standPos);
            String posBlockText = "nudge";
         } else {
            this.pathWalker.setTarget(goal.standPos != null ? goal.standPos : this.getApproachPos(roleDimension.getBlockPos()), goal.standPos != null ? 0.6 : 1.2);
            Status posTime = this.pathWalker.tickWalk();
            String name = posTime.name();
            if (posTime == Status.STUCK) {
               this.failCurrentGoal("couldn't walk to it");
               return;
            }

            if (posTime == Status.ARRIVED && goal.standPos == null) {
               this.pathWalker.clearTarget();
               this.setBotState(VillagerTradeBot.State.OPEN_STORAGE);
               return;
            }
         }

         if (this.getTicksInState() > 400L) {
            this.failCurrentGoal("couldn't walk to it");
         }
      }
   }

   private String describeGoalVillager(VillagerTradeBot.Goal goal) {
      TrackedVillager villagerExperience = goal == null ? null : this.villagerEntityHelper.findByUuid(goal.villagerUuid);
      return villagerExperience == null ? "a villager" : "the " + villagerExperience.professionName;
   }

   private VillagerTradeRule findMatchingTarget(TrackedVillager villagerExperience, List<VillagerTradeRule> list) {
      VillagerTradeRule tradeOpen = null;

      for (VillagerTradeRule enchantJsonProfession : list) {
         if (TradeEnchantUtils.matchesProfession(villagerExperience, enchantJsonProfession)) {
            if (villagerExperience.offers.isEmpty() || TradeEnchantUtils.findBestTrade(villagerExperience, enchantJsonProfession) != null || villagerExperience.isBlockedByStockOrPrice(enchantJsonProfession)) {
               return enchantJsonProfession;
            }

            if (tradeOpen == null) {
               tradeOpen = enchantJsonProfession;
            }
         }
      }

      return tradeOpen;
   }

   private VillagerTradeBot.Goal createDepositGoal(String emeraldsRestockText) {
      RoleDimension roleDimension = this.findDepositStorage();
      if (roleDimension == null) {
         return null;
      } else {
         VillagerTradeBot.Goal goal = new VillagerTradeBot.Goal();
         goal.goalKind = VillagerTradeBot.GoalKind.DEPOSIT;
         goal.storageRole = roleDimension.role;
         goal.storage = roleDimension;
         goal.description = emeraldsRestockText;
         return goal;
      }
   }

   private List<VillagerTradeRule> getRunnableTargets() {
      var arrayList = new ArrayList();

      for (VillagerTradeRule enchantJsonProfession : this.tradeTargets) {
         if (enchantJsonProfession.isEnabled() && this.isAllowedByMode(enchantJsonProfession) && !this.isTargetPaused(enchantJsonProfession)) {
            arrayList.add(enchantJsonProfession);
         }
      }

      return arrayList;
   }

   public VillagerTradeBot.TradeFacts getTradeFacts(VillagerTradeRule enchantJsonProfession) {
      int villagerEntityLimit = -1;
      int villagerEntityCounter = 0;
      int villagerEntityCounter2 = 0;
      int villagerEntityCounter3 = 0;
      String villagerEntityText = "";
      int villagerEntityCounter4 = 0;
      if (enchantJsonProfession != null && this.isEnabled()) {
         Vec3d zero = this.getPlayerPos();

         for (TrackedVillager villagerExperience : this.villagerEntityHelper.getAllVillagers()) {
            if (villagerExperience.hasProfession && TradeEnchantUtils.matchesProfession(villagerExperience, enchantJsonProfession)) {
               if (villagerExperience.needsLevelUp(enchantJsonProfession)) {
                  villagerEntityCounter4++;
               } else if (villagerExperience.offers.isEmpty()) {
                  villagerEntityCounter3++;
               } else {
                  OfferTrade tradeBuy = TradeEnchantUtils.findBestTrade(villagerExperience, enchantJsonProfession);
                  if (tradeBuy != null) {
                     villagerEntityCounter++;
                     if (villagerEntityLimit < 0 || tradeBuy.count3EnchantJson < villagerEntityLimit) {
                        villagerEntityLimit = tradeBuy.count3EnchantJson;
                        villagerEntityText = villagerExperience.professionName + " \u00b7 " + Math.round(Math.sqrt(villagerExperience.entityPos.squaredDistanceTo(zero))) + "m";
                     }
                  } else if (villagerExperience.isBlockedByStockOrPrice(enchantJsonProfession)) {
                     villagerEntityCounter2++;
                  }
               }
            }
         }

         return new VillagerTradeBot.TradeFacts(villagerEntityLimit, villagerEntityText, villagerEntityCounter, villagerEntityCounter2, villagerEntityCounter3, villagerEntityCounter4);
      } else {
         return new VillagerTradeBot.TradeFacts(villagerEntityLimit, villagerEntityText, 0, 0, 0, 0);
      }
   }

   private void renderSeeTradesHint(MatrixStack matrixStack) {
      if (mc.crosshairTarget instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof VillagerEntity villagerEntity) {
         Vec3d windowFramebuffer = VertexConsumerUtilsVector.getWindowFramebuffer(villagerEntity.getEntityPos().add(0.0, villagerEntity.getHeight() + 0.55, 0.0));
         if (windowFramebuffer != null && !(windowFramebuffer.z < 0.0) && !(windowFramebuffer.z > 1.0)) {
            String seeTradesText = NoneCartographerUtils.getKey(villagerEntity.getVillagerData().profession());
            ChestStorageUtils.handleTranslatePush(
               matrixStack, seeTradesText, "Right-click to see its trades", MatrixEspUtils.releaseRightValue, (float)windowFramebuffer.x, (float)windowFramebuffer.y, 1.0F
            );
         }
      }
   }

   private static boolean isStorageAt(RoleDimension roleDimension, BlockPos pos) {
      return roleDimension.asIntRoleDimension == pos.getX() && roleDimension.intValue == pos.getY() && roleDimension.asInt == pos.getZ();
   }

   private static boolean storageMatchesRole(RoleDimension roleDimension, Role role) {
      return switch (role) {
         case MATERIALS -> roleDimension.isRoleMaterials();
         case OUTPUTS -> roleDimension.isRoleOutputs();
         case EMERALDS -> roleDimension.isRoleEmeralds();
         case DROP -> roleDimension.role == Role.DROP;
         default -> roleDimension.role != Role.DROP;
      };
   }

   private void planNextGoal() {
      if (this.automationMode.getCurrentOption().equals("Assisted")) {
         this.tickAssistedMode();
      } else {
         VillagerTradeBot.Goal deque = this.goalStack.peek();
         if (deque == null) {
            deque = this.createStrayPickupGoal();
            if (deque == null) {
               deque = this.createAutoDepositGoal();
            }

            if (deque == null && this.hasDepositableStacks() && this.countEmptySlots() == 0 && this.isDepositBlocked()) {
               this.waitForOutputRoom();
               return;
            }

            if (deque == null) {
               deque = this.findNextVillagerGoal();
            }

            if (deque == null) {
               this.enterIdleWait();
               return;
            }

            this.goalStack.push(deque);
         }

         this.startGoal(deque);
      }
   }

   private void renderStatusOverlay(MatrixStack matrices, VertexTextureFont vertexTextureFont) {
      float traderVillagerValue = 230.0F;
      String traderVillagerText = "Villager Trader";
      String formatText = vertexTextureFont.formatText(this.getCachedStatusText(), traderVillagerValue);
      String corzfontText = "Emeralds " + this.getEmeraldCount() + "  \u00b7  Bought " + this.getTotalTradedCount();
      String formatTraderVillager = this.getStatusMessage();
      if (!formatTraderVillager.isEmpty()) {
         formatTraderVillager = vertexTextureFont.formatText(formatTraderVillager, traderVillagerValue);
      }

      var arrayList = new ArrayList();
      float traderVillagerFactor = vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzBrown();
      float traderVillagerFactor2 = 2.0F;
      float traderVillagerValue2 = 6.0F;
      float traderVillagerValue3 = traderVillagerFactor * 0.42F;
      float traderVillagerValue4 = 4.0F;
      float traderVillagerBound = vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont(traderVillagerText) + traderVillagerValue3 + traderVillagerValue4;
      float pilotStatusBound = Math.max(traderVillagerBound, vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont(formatText));
      float max3 = Math.max(pilotStatusBound, vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont(corzfontText));
      if (!formatTraderVillager.isEmpty()) {
         max3 = Math.max(max3, vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont(formatTraderVillager));
      }

      for (int i = 0; i < arrayList.size(); i++) {
         arrayList.set(i, vertexTextureFont.formatText(CardStatusUtilsFlag.getOrDefault((String)arrayList.get(i)), traderVillagerValue));
         max3 = Math.max(max3, vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont((String)arrayList.get(i)));
      }

      int traderVillagerFactor3 = 3 + (formatTraderVillager.isEmpty() ? 0 : 1) + arrayList.size();
      float traderVillagerValue5 = traderVillagerValue2 * 2.0F + traderVillagerFactor3 * traderVillagerFactor + (traderVillagerFactor3 - 1) * traderVillagerFactor2 + 2.0F;
      float[] perfTopbar = this.getOverlayPosition(max3 + traderVillagerValue2 * 2.0F, traderVillagerValue5);
      float chestStorageValue = perfTopbar[0];
      float traderVillagerValue7 = perfTopbar[1];
      ChestStorageUtils.handle(chestStorageValue, traderVillagerValue7, max3 + traderVillagerValue2 * 2.0F, traderVillagerValue5);
      int traderVillagerValue8 = -16184564;
      VertexConsumerUtilsVector.handle(
         matrices, new Color(traderVillagerValue8 >> 16 & 0xFF, traderVillagerValue8 >> 8 & 0xFF, traderVillagerValue8 & 0xFF, 215), chestStorageValue, traderVillagerValue7, chestStorageValue + max3 + traderVillagerValue2 * 2.0F, traderVillagerValue7 + traderVillagerValue5, 4.0, 6.0
      );
      int colorValue = !this.isEnabled() ? -4669236 : (this.botState == VillagerTradeBot.State.PAUSED ? -1535700 : -11870592);
      float f13 = traderVillagerValue7 + traderVillagerValue2;
      float f12 = f13 + (traderVillagerFactor - traderVillagerValue3) / 2.0F;
      VertexConsumerUtilsVector.handle(matrices, toColor(colorValue), chestStorageValue + traderVillagerValue2, f12, chestStorageValue + traderVillagerValue2 + traderVillagerValue3, f12 + traderVillagerValue3, traderVillagerValue3 / 2.0F, 6.0);
      vertexTextureFont.handleForStringFloat(traderVillagerText, chestStorageValue + traderVillagerValue2 + traderVillagerValue3 + traderVillagerValue4, f13, new Color(-1));
      float traderVillagerValue12 = f13 + (traderVillagerFactor + traderVillagerFactor2 + 2.0F);
      vertexTextureFont.handleForStringFloat(formatText, chestStorageValue + traderVillagerValue2, traderVillagerValue12, new Color(-3947581));
      float traderVillagerTotal = traderVillagerValue12 + (traderVillagerFactor + traderVillagerFactor2);
      vertexTextureFont.handleForStringFloat(corzfontText, chestStorageValue + traderVillagerValue2, traderVillagerTotal, new Color(-8421505));
      if (!formatTraderVillager.isEmpty()) {
         traderVillagerTotal += traderVillagerFactor + traderVillagerFactor2;
         vertexTextureFont.handleForStringFloat(formatTraderVillager, chestStorageValue + traderVillagerValue2, traderVillagerTotal, toColor(-1537398));
      }

      for (String vertexTextureText : (Iterable<java.lang.String>) (arrayList)) {
         traderVillagerTotal += traderVillagerFactor + traderVillagerFactor2;
         vertexTextureFont.handleForStringFloat(vertexTextureText, chestStorageValue + traderVillagerValue2, traderVillagerTotal, new Color(-8421505));
      }
   }

   private void sendThrottledMessage(String outputWaitingText, String villagerTradeText, boolean outputWaitingFlag, long outputWaitingValue) {
      Long long2 = this.messageLastTickByKey.get(outputWaitingText);
      if (long2 == null || this.tickCounter - long2 >= outputWaitingValue) {
         this.messageLastTickByKey.put(outputWaitingText, this.tickCounter);
         this.notifyUser(villagerTradeText, outputWaitingFlag);
      }
   }

   private void stopWithMessage(String villagerTradeText) {
      this.notifyUser(villagerTradeText, false);
      this.setEnabled(false);
   }

   private int getCurrentOwnedCount(VillagerTradeBot.Owned owned) {
      if (owned.trackedCount <= 0) {
         return 0;
      } else {
         int count = this.countMatchingStacks(owned.stackTemplate);
         if (count < owned.trackedCount) {
            owned.trackedCount = count;
         }

         return owned.trackedCount;
      }
   }

   public String getCachedStatusText() {
      long timeMillis = System.currentTimeMillis();
      if (timeMillis - this.statusTextCacheMillis < 150L && this.cachedStatusText != null) {
         return this.cachedStatusText;
      } else {
         this.statusTextCacheMillis = timeMillis;
         this.cachedStatusText = CardStatusUtilsFlag.getOrDefault(this.buildStatusText());
         return this.cachedStatusText;
      }
   }

   private String buildStatusText() {
      if (this.pick == VillagerTradeBot.Pick.TRADE) {
         return "Right-click a villager to pick a trade";
      } else if (this.pick == VillagerTradeBot.Pick.STORAGE) {
         return "Right-click to mark the " + ChestStorageUtils.formatChestStorage(this.pickStorageRole).toLowerCase(Locale.ROOT);
      } else if (!this.isEnabled()) {
         return this.tradeTargets.isEmpty() ? "Add a trade to begin" : "Stopped";
      } else if (mc.player == null) {
         return "Waiting for the world";
      } else {
         VillagerTradeBot.Goal deque = this.goalStack.peek();

         return switch (this.botState) {
            case TO_VILLAGER -> "Walking to " + this.describeGoalVillager(deque) + this.describeVillagerDistance(deque);
            case OPEN_VILLAGER -> "Opening " + this.describeGoalVillager(deque);
            case TRADE -> this.describeTradeProgress(deque);
            case TO_STORAGE, OPEN_STORAGE, STORAGE_OP -> this.describeStorageAction(deque);
            case DROP_OP -> "Dropping items at the drop spot";
            case PICKUP -> "Picking up an item that missed the drop spot";
            case WAIT_RESTOCK -> this.idleStatusText.isEmpty() ? "Waiting for villagers to restock" : this.idleStatusText;
            case RECOVER -> deque != null && deque.failureReason != null && !deque.failureReason.isEmpty()
               ? "Retrying \u2014 " + deque.failureReason
               : "Retrying";
            case PAUSED -> this.pausedReasonText.isEmpty() ? "Paused" : stripTrailingPeriod(this.pausedReasonText);
            default -> "Looking for the next trade";
         };
      }
   }

   private void queueMaterialWithdraw(VillagerTradeBot.Goal goal, Item emerald) {
      String materialsEmeraldsText = emerald != null ? IngotBallUtils.getKey(emerald) : "materials";
      if (this.materials.isEnabled() && emerald != null && this.findNearestStorage(Role.MATERIALS) != null) {
         if (this.isMaterialUnavailable(emerald)) {
            this.pauseTarget(goal.target, "no " + materialsEmeraldsText + " left in the MATERIALS chest \u2014 refill it", 1200L);
            this.popGoal();
            this.setBotState(VillagerTradeBot.State.PLAN);
         } else {
            VillagerTradeBot.Goal deque = new VillagerTradeBot.Goal();
            deque.goalKind = VillagerTradeBot.GoalKind.WITHDRAW;
            deque.storageRole = Role.MATERIALS;
            deque.withdrawItem = emerald;
            deque.withdrawAmount = 64;
            deque.description = "withdraw " + materialsEmeraldsText;
            this.goalStack.push(deque);
            this.setBotState(VillagerTradeBot.State.PLAN);
         }
      } else {
         String emeraldsOutputText = !this.materials.isEnabled()
            ? "out of " + materialsEmeraldsText + " (Auto Withdraw Materials is off)"
            : (
               this.hasStorageForRole(Role.MATERIALS)
                  ? "out of " + materialsEmeraldsText + " \u2014 the MATERIALS chest can't be reached right now"
                  : "out of " + materialsEmeraldsText + " \u2014 put some in your inventory or mark a MATERIALS chest"
            );
         this.pauseTarget(goal.target, emeraldsOutputText, 1200L);
         this.popGoal();
         this.setBotState(VillagerTradeBot.State.PLAN);
      }
   }

   private void processWorkSounds() {
      VillagerTradeBot.WorkSound workSound;
      while ((workSound = this.workSoundQueue.poll()) != null) {
         TrackedVillager villagerExperience = null;
         double entitySquaredLimit = 4.0;

         for (TrackedVillager getAllVillagersItem : this.villagerEntityHelper.getAllVillagers()) {
            if (workSound.entityId() >= 0) {
               if (getAllVillagersItem.entityId == workSound.entityId()) {
                  villagerExperience = getAllVillagersItem;
                  break;
               }
            } else {
               double squaredDistanceTo = getAllVillagersItem.entityPos.squaredDistanceTo(workSound.x(), workSound.y(), workSound.z());
               if (squaredDistanceTo <= entitySquaredLimit) {
                  entitySquaredLimit = squaredDistanceTo;
                  villagerExperience = getAllVillagersItem;
               }
            }
         }

         if (villagerExperience != null) {
            villagerExperience.soldOutTick = this.tickCounter;
         }
      }
   }

   private boolean isTargetPaused(VillagerTradeRule enchantJsonProfession) {
      Long long2 = this.targetPausedUntilTick.get(enchantJsonProfession);
      return long2 != null && this.tickCounter < long2;
   }

   private boolean isNearBlockCenter(BlockPos pos, double playerCenterLimit) {
      Vec3d center = Vec3d.ofCenter(pos);
      double deltaX = mc.player.getX() - center.x;
      double deltaZ = mc.player.getZ() - center.z;
      return Math.hypot(deltaX, deltaZ) <= playerCenterLimit && Math.abs(mc.player.getY() - pos.getY()) <= 1.0;
   }

   private void showPickHint(String traderVillagerText) {
      long timeMillis = System.currentTimeMillis();
      if (timeMillis - this.lastPickHintMillis >= 1500L) {
         this.lastPickHintMillis = timeMillis;
         DisabledEnabled.getInstance().handleDisabledEnabled("Villager Trader", traderVillagerText, false);
      }
   }

   private int getVillagerBoxColor(TrackedVillager villagerExperience, VillagerTradeRule enchantJsonProfession, boolean tradeReleaseFlag) {
      if (tradeReleaseFlag) {
         return -11870592;
      } else if (enchantJsonProfession == null || villagerExperience.offers.isEmpty()) {
         return -4669236;
      } else if (TradeEnchantUtils.findBestTrade(villagerExperience, enchantJsonProfession) != null) {
         return MatrixEspUtils.releaseRightValue;
      } else {
         return villagerExperience.isBlockedByStockOrPrice(enchantJsonProfession) ? -871589 : -4669236;
      }
   }

   private String describeVillagerTrade(TrackedVillager villagerExperience, VillagerTradeRule enchantJsonProfession) {
      if (enchantJsonProfession != null && villagerExperience.needsLevelUp(enchantJsonProfession)) {
         return "Not " + NoneCartographerUtils.formatExpertMaster(enchantJsonProfession.getBuyItemValue()) + " yet \u00b7 " + NoneCartographerUtils.formatExpertMaster(villagerExperience.level);
      } else if (villagerExperience.offers.isEmpty()) {
         return "Not checked yet";
      } else if (enchantJsonProfession == null) {
         return "No matching trade";
      } else {
         OfferTrade tradeBuy = TradeEnchantUtils.findBestTrade(villagerExperience, enchantJsonProfession);
         if (tradeBuy != null) {
            return tradeBuy.formatLevelMax() + " \u00b7 " + tradeBuy.count3EnchantJson + "e \u00b7 " + tradeBuy.getMax() + " left";
         } else {
            OfferTrade next = villagerExperience.findTooExpensiveOffer(enchantJsonProfession);
            if (next != null) {
               return "Too pricey \u00b7 " + next.count3EnchantJson + "e";
            } else {
               return villagerExperience.isBlockedByStockOrPrice(enchantJsonProfession) ? "Sold out \u00b7 restocking" : "No matching trade";
            }
         }
      }
   }

   public void handlePickRight(TickProgress tickProgress) {
      if (mc.world != null && mc.player != null && this.shouldRenderPickUi()) {
         MatrixStack matrixStack = new MatrixStack();
         CorzClient.fontManager.setMatrixStack(matrixStack);
         if (!this.isEnabled()) {
            ChestStorageUtils.update();
         }

         boolean rightFlag = this.pick != VillagerTradeBot.Pick.NONE && mc.currentScreen == null;
         if (rightFlag && this.pick == VillagerTradeBot.Pick.STORAGE) {
            this.renderStorageHint(matrixStack);
         } else if (rightFlag) {
            this.renderSeeTradesHint(matrixStack);
         }

         if (!this.markedStorages.isEmpty()) {
            this.renderStorageLabels(matrixStack);
         }

         if (rightFlag) {
            String rightText = this.pick == VillagerTradeBot.Pick.TRADE
               ? "Right-click a villager to pick a trade"
               : (
                  this.pickStorageRole == Role.DROP
                     ? "Right-click the block where items should land"
                     : "Right-click your " + ChestStorageUtils.formatChestStorage(this.pickStorageRole).toLowerCase(Locale.ROOT)
               );
            ChestStorageUtils.handleScaledWindow(
               matrixStack,
               rightText,
               "Esc to go back",
               this.pick == VillagerTradeBot.Pick.TRADE ? MatrixEspUtils.releaseRightValue : ChestStorageUtils.getStorageChest(this.pickStorageRole)
            );
         }
      }
   }

   private VillagerEntity getTargetedVillager() {
      if (mc.crosshairTarget instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof VillagerEntity entity) {
         return entity;
      } else if (!this.isMerchantScreenOpen()) {
         return null;
      } else {
         VillagerEntity villagerEntity2 = null;
         double entityLimit = 36.0;

         for (Entity getEntitiesItem : mc.world.getEntities()) {
            if (getEntitiesItem instanceof VillagerEntity villagerEntity) {
               double squaredDistanceTo = villagerEntity.squaredDistanceTo(mc.player);
               if (squaredDistanceTo < entityLimit) {
                  entityLimit = squaredDistanceTo;
                  villagerEntity2 = villagerEntity;
               }
            }
         }

         return villagerEntity2;
      }
   }

   private void markStorageFull(RoleDimension roleDimension) {
      roleDimension.handleOverworldOutput(true, this.tickCounter);
      BlockPos offset = this.getOtherChestHalf(roleDimension.getBlockPos());
      if (offset != null) {
         for (RoleDimension roleDimension2 : this.markedStorages) {
            if (roleDimension2.getBlockPos().equals(offset) && roleDimension2.canDimension(roleDimension.dimension)) {
               roleDimension2.handleOverworldOutput(true, this.tickCounter);
            }
         }
      }
   }

   private boolean isVillagerReachable(TrackedVillager villagerExperience) {
      if (villagerExperience == null || !villagerExperience.hasProfession || villagerExperience.isOnCooldown(this.tickCounter) || this.tickCounter - villagerExperience.lastSeenTick > 40L) {
         return false;
      } else {
         return !this.navigation.isEnabled() && mc.player != null
            ? villagerExperience.entityPos.add(0.0, 1.5, 0.0).distanceTo(mc.player.getEyePos()) <= 3.5
            : true;
      }
   }

   private void quickMoveSlot(int quickClickLimit) {
      ScreenHandler currentScreenHandler = mc.player.currentScreenHandler;
      if (currentScreenHandler != null && quickClickLimit >= 0) {
         mc.interactionManager.clickSlot(currentScreenHandler.syncId, quickClickLimit, 0, SlotActionType.QUICK_MOVE, mc.player);
      }
   }

   private boolean hasLineOfSightToStorage(Vec3d vec, RoleDimension roleDimension) {
      Vec3d add = this.getStorageTopCenter(roleDimension).add(0.0, 0.05, 0.0);
      BlockHitResult raycast = mc.world.raycast(new RaycastContext(vec, add, ShapeType.COLLIDER, FluidHandling.NONE, mc.player));
      if (raycast.getType() == Type.MISS) {
         return true;
      } else {
         return raycast instanceof BlockHitResult blockHitResult && blockHitResult.getBlockPos().equals(roleDimension.getBlockPos())
            ? true
            : raycast.getPos().squaredDistanceTo(vec) >= add.squaredDistanceTo(vec) - 0.3;
      }
   }

   private RoleDimension findNearestStorage(Role role) {
      String villagerTradeText = mc.world.getRegistryKey().getValue().toString();
      RoleDimension next = null;
      double villagerTradeLimit = Double.MAX_VALUE;

      for (RoleDimension roleDimension : this.markedStorages) {
         if (roleDimension.canDimension(villagerTradeText) && storageMatchesRole(roleDimension, role) && this.isStorageUsable(roleDimension)) {
            double squaredDistanceTo = roleDimension.getVec3d().squaredDistanceTo(mc.player.getEyePos());
            if (squaredDistanceTo < villagerTradeLimit) {
               villagerTradeLimit = squaredDistanceTo;
               next = roleDimension;
            }
         }
      }

      return next;
   }

   private boolean isTradeOutputStack(ItemStack stack) {
      if (stack != null && !stack.isEmpty()) {
         if (!stack.isOf(Items.EMERALD) && !stack.isOf(Items.BOOK)) {
            if (this.isSellItemStack(stack)) {
               return false;
            } else {
               for (VillagerTradeRule enchantJsonProfession : this.tradeTargets) {
                  if (this.matchesBuyTarget(enchantJsonProfession, stack)) {
                     return true;
                  }
               }

               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public void handleVillagerTrader(ScaffoldTrade scaffoldTrade) {
      if (this.awaitUseKeyRelease) {
         if (mc.options.useKey.isPressed()) {
            scaffoldTrade.updateFlag();
            return;
         }

         this.awaitUseKeyRelease = false;
      }

      if (this.pick != VillagerTradeBot.Pick.NONE && mc.player != null && mc.world != null && mc.currentScreen == null) {
         if (this.pick == VillagerTradeBot.Pick.TRADE) {
            if (!(mc.crosshairTarget instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof MerchantEntity merchantEntity)) {
               scaffoldTrade.updateFlag();
               this.showPickHint("Right-click a villager to see its trades.");
            } else if (!mc.player.getMainHandStack().isEmpty() || !mc.player.getOffHandStack().isEmpty()) {
               scaffoldTrade.updateFlag();
               this.awaitUseKeyRelease = true;
               if (mc.interactionManager != null) {
                  this.interactWithEntity(merchantEntity, entityHitResult);
               }
            }
         } else {
            scaffoldTrade.updateFlag();
            if (mc.crosshairTarget instanceof BlockHitResult blockHitResult && blockHitResult.getType() == Type.BLOCK) {
               BlockPos blockPos = blockHitResult.getBlockPos();
               if (this.pickStorageRole != Role.DROP && !this.isContainerBlock(blockPos)) {
                  this.showPickHint("That's not a chest \u2014 pick a chest, barrel or shulker box.");
               } else {
                  String formatTraderVillager = this.markStorageBlock(blockPos, this.pickStorageRole);
                  this.pick = VillagerTradeBot.Pick.NONE;
                  this.awaitUseKeyRelease = true;
                  DisabledEnabled.getInstance().handleDisabledEnabled("Villager Trader", formatTraderVillager, true);
                  this.openTraderScreenPending = true;
                  this.pickConfirmedMillis = System.currentTimeMillis();
               }
            } else {
               this.showPickHint("Look at a block, then right-click it.");
            }
         }
      }
   }

   public boolean isStorageFullWhileRunning(RoleDimension roleDimension) {
      return this.isEnabled() && this.isStorageFull(roleDimension);
   }

   private ItemStack getStackAtIndex(int cursorStackValue) {
      if (cursorStackValue <= 35) {
         return mc.player.getInventory().getStack(cursorStackValue);
      } else if (cursorStackValue == 36) {
         return mc.player.getOffHandStack();
      } else {
         ScreenHandler currentScreenHandler = mc.player.currentScreenHandler;
         return currentScreenHandler == null ? ItemStack.EMPTY : currentScreenHandler.getCursorStack();
      }
   }

   private void handleMissingEmeralds(VillagerTradeBot.Goal goal, int emeraldsEmeraldBound) {
      VillagerTradeBot.Goal deque = this.countEmptySlots() < 3 && this.hasDepositableStacks() ? this.createDepositGoal("make room for emeralds") : null;
      if (deque != null) {
         this.goalStack.push(deque);
         this.setBotState(VillagerTradeBot.State.PLAN);
      } else {
         int emeraldsChestValue = Math.max(emeraldsEmeraldBound, 1) + this.emeraldReserve.getValue();
         RoleDimension next = this.findNearestStorage(Role.EMERALDS);
         if (this.materials.isEnabled() && next != null && !this.isMaterialUnavailable(Items.EMERALD)) {
            VillagerTradeBot.Goal goal3 = new VillagerTradeBot.Goal();
            goal3.goalKind = VillagerTradeBot.GoalKind.WITHDRAW;
            goal3.storageRole = Role.EMERALDS;
            goal3.withdrawItem = Items.EMERALD;
            goal3.withdrawAmount = emeraldsChestValue;
            goal3.description = "restock emeralds from chest";
            this.goalStack.push(goal3);
            this.setBotState(VillagerTradeBot.State.PLAN);
         } else if (!this.canAcquireEmeralds()) {
            String emeraldsEmeraldText = !this.hasStorageForRole(Role.EMERALDS)
               ? "out of emeralds \u2014 no emerald chest marked (Villager Trader \u2192 Emerald chest \u2192 Mark) and Auto Acquire Emeralds is off"
               : (
                  !this.materials.isEnabled()
                     ? "out of emeralds \u2014 Auto Withdraw Materials is off and Auto Acquire Emeralds is off"
                     : (
                        next == null
                           ? "out of emeralds \u2014 the emerald chest can't be reached right now"
                           : "out of emeralds \u2014 the emerald chest is empty (refill it) and Auto Acquire Emeralds is off"
                     )
               );
            this.pauseTarget(goal.target, emeraldsEmeraldText, 1200L);
            this.popGoal();
            this.setBotState(VillagerTradeBot.State.PLAN);
         } else {
            VillagerTradeRule enchantJsonProfession = this.getAcquireEmeraldsTarget();
            if (this.isTargetPaused(enchantJsonProfession)) {
               this.pauseTarget(goal.target, this.pauseReasonByTarget.getOrDefault(enchantJsonProfession, "can't get emeralds right now"), 1200L);
               this.popGoal();
               this.setBotState(VillagerTradeBot.State.PLAN);
            } else {
               Item defaultedRegistry = IngotBallUtils.getDefaultedRegistry(this.sellItemSetting.getCurrentOption());
               boolean emeraldsEmeraldFlag = defaultedRegistry != null && this.countItem(defaultedRegistry) > 0;
               boolean emeraldsEmeraldFlag2 = defaultedRegistry != null && this.materials.isEnabled() && this.findNearestStorage(Role.MATERIALS) != null && !this.isMaterialUnavailable(defaultedRegistry);
               if (!emeraldsEmeraldFlag && !emeraldsEmeraldFlag2) {
                  this.pauseTarget(goal.target, "out of emeralds and no " + this.sellItemSetting.getCurrentOption() + " to sell for more", 1200L);
                  this.popGoal();
                  this.setBotState(VillagerTradeBot.State.PLAN);
               } else {
                  UUID uUID = this.pickVillagerForTarget(enchantJsonProfession);
                  boolean restockCheckFlag = false;
                  String emeraldsEmeraldText2 = "selling " + this.sellItemSetting.getCurrentOption();
                  if (uUID == null) {
                     TrackedVillager outputTrade = this.findVillagerToInspect(enchantJsonProfession, this.villagerEntityHelper.getSortedByDistance());
                     if (outputTrade != null) {
                        outputTrade.levelResetTick = this.tickCounter;
                        uUID = outputTrade.uuid;
                        String emeraldsEmeraldText3 = "checking a " + outputTrade.professionName + " for a " + this.sellItemSetting.getCurrentOption() + " trade";
                     }
                  }

                  if (uUID == null) {
                     Due timeExpensive = VillagerTradeUtils.findNextDueVillager(
                        this.villagerEntityHelper.getAllVillagers(),
                        List.of(enchantJsonProfession),
                        this.tickCounter,
                        this.restockCheckDelay.getValue(),
                        this.getPlayerPos(),
                        this::isVillagerReachable
                     );
                     if (timeExpensive != null) {
                        uUID = timeExpensive.villager().uuid;
                        restockCheckFlag = timeExpensive.hinted();
                        String emeraldsEmeraldText4 = "re-checking a sold-out " + timeExpensive.villager().professionName;
                     }
                  }

                  if (uUID == null) {
                     String emeraldsEmeraldText5 = "Any".equals(enchantJsonProfession.profession) ? "villager" : enchantJsonProfession.profession;
                     this.pauseTarget(
                        goal.target,
                        "out of emeralds \u2014 no "
                           + emeraldsEmeraldText5
                           + " in range is buying "
                           + this.sellItemSetting.getCurrentOption()
                           + " right now (re-checking after their restock)",
                        this.restockCheckDelay.getValue()
                     );
                     this.popGoal();
                     this.setBotState(VillagerTradeBot.State.PLAN);
                  } else {
                     VillagerTradeBot.Goal goal4 = new VillagerTradeBot.Goal();
                     goal4.goalKind = VillagerTradeBot.GoalKind.SELL_FOR_EMERALDS;
                     goal4.target = enchantJsonProfession;
                     goal4.villagerUuid = uUID;
                     goal4.isRestockCheck = restockCheckFlag;
                     goal4.targetEmeraldCount = emeraldsChestValue;
                     goal4.description = "acquire emeralds \u2192 " + emeraldsChestValue;
                     this.goalStack.push(goal4);
                     this.setBotState(VillagerTradeBot.State.PLAN);
                  }
               }
            }
         }
      }
   }

   private String getServerKey() {
      ServerInfo currentServerEntry = mc.getCurrentServerEntry();
      if (currentServerEntry != null) {
         return "mp:" + currentServerEntry.address;
      } else {
         IntegratedServer server = mc.getServer();
         return server != null ? "sp:" + server.getSaveProperties().getLevelName() : "";
      }
   }

   private void tickDropOutputs() {
      VillagerTradeBot.Goal deque = this.goalStack.peek();
      if (deque != null && deque.storage != null) {
         if (this.getTicksInState() > 600L) {
            this.closeOpenScreens();
            this.endTradeSession();
            this.popGoal();
            this.setBotState(VillagerTradeBot.State.PLAN);
         } else {
            float[] playerEye = this.computeAimToStorage(deque.storage);
            this.aimController.setTargetAngles(playerEye[0], playerEye[1], 1);
            if (this.dropInventoryScreen != null && mc.currentScreen == this.dropInventoryScreen) {
               if (this.tickCounter - this.screenOpenedTick >= 2L && this.tickCounter - this.lastOutputChangeTick >= 8L) {
                  if (this.aimController.isAngleWithinTolerance(playerEye[0], playerEye[1], 2.5F) && this.isPlayerStandingStill()) {
                     int value = this.countDepositableStacks();
                     if (this.lastOutputStackCount != Integer.MAX_VALUE && value < this.lastOutputStackCount) {
                        this.movedItemCount = this.movedItemCount + (this.lastOutputStackCount - value);
                        this.lastScreenOutputTick = this.tickCounter;
                     }

                     if (value != 0 && this.tickCounter - this.lastScreenOutputTick <= 40L) {
                        this.lastOutputStackCount = value;
                        int stackInventory = this.findInventorySlot(this::isDepositableStack);
                        if (stackInventory >= 0) {
                           mc.interactionManager
                              .clickSlot(mc.player.playerScreenHandler.syncId, stackInventory, 1, SlotActionType.THROW, mc.player);
                           this.lastOutputChangeTick = this.tickCounter;
                        }
                     } else {
                        mc.player.closeHandledScreen();
                        this.endTradeSession();
                        if (value == 0) {
                           this.strayDropStorage = deque.storage;
                           this.strayPickupTick = this.tickCounter + 50L;
                        }

                        this.popGoal();
                        this.setBotState(VillagerTradeBot.State.PLAN);
                     }
                  } else {
                     this.standStillTicks = 0;
                     mc.player.closeHandledScreen();
                  }
               }
            } else {
               this.dropInventoryScreen = null;
               Vec3d vec3d = deque.storage.getVec3d();
               if (Math.hypot(vec3d.x - mc.player.getX(), vec3d.z - mc.player.getZ()) > 4.0 && this.navigation.isEnabled()) {
                  this.setBotState(VillagerTradeBot.State.TO_STORAGE);
               } else if (!this.hasDepositableStacks()) {
                  this.popGoal();
                  this.setBotState(VillagerTradeBot.State.PLAN);
               } else if (!this.aimController.isAngleWithinTolerance(playerEye[0], playerEye[1], 2.0F)) {
                  this.standStillTicks = 0;
               } else {
                  this.standStillTicks = this.isPlayerStandingStill() ? this.standStillTicks + 1 : 0;
                  if (this.standStillTicks >= 3 && mc.currentScreen == null) {
                     this.beginTradeSession();
                     this.dropInventoryScreen = new InventoryScreen(mc.player);
                     mc.setScreen(this.dropInventoryScreen);
                     this.screenOpenedTick = this.tickCounter;
                     this.lastOutputChangeTick = -4611686018427387904L;
                     this.lastScreenOutputTick = this.tickCounter;
                  }
               }
            }
         }
      } else {
         this.closeOpenScreens();
         this.endTradeSession();
         this.setBotState(VillagerTradeBot.State.PLAN);
      }
   }

   private void tickOpenStorage() {
      VillagerTradeBot.Goal deque = this.goalStack.peek();
      if (deque == null) {
         this.setBotState(VillagerTradeBot.State.PLAN);
      } else {
         RoleDimension roleDimension = deque.storage;
         if (roleDimension == null) {
            this.goalStack.pop();
            this.setBotState(VillagerTradeBot.State.PLAN);
         } else if (roleDimension.role == Role.DROP) {
            this.movedItemCount = 0;
            this.lastOutputStackCount = Integer.MAX_VALUE;
            this.lastOutputChangeTick = -4611686018427387904L;
            this.setBotState(VillagerTradeBot.State.DROP_OP);
         } else if (this.isStorageScreenOpen()) {
            this.beginTradeSession();
            this.screenOpenedTick = this.tickCounter;
            this.lastOutputChangeTick = -4611686018427387904L;
            this.movedItemCount = 0;
            this.lastScreenOutputTick = this.tickCounter;
            this.lastDepositSlotCount = Integer.MAX_VALUE;
            this.lastWithdrawCount = Integer.MIN_VALUE;
            this.setBotState(VillagerTradeBot.State.STORAGE_OP);
         } else {
            Vec3d vec3d = roleDimension.getVec3d();
            double distanceTo = vec3d.distanceTo(mc.player.getEyePos());
            if (distanceTo > 4.3 && this.navigation.isEnabled()) {
               this.setBotState(VillagerTradeBot.State.TO_STORAGE);
            } else {
               this.aimController.setTargetFromPoint(vec3d, 1);
               if (this.aimController.isPointWithinAngle(vec3d, 16.0F) && this.tickCounter - this.lastInteractTick >= this.interactionDelay.getValue()) {
                  BlockHitResult raycast = this.getContainerHitResult(roleDimension.getBlockPos());
                  if (raycast != null) {
                     mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, raycast);
                     mc.player.swingHand(Hand.MAIN_HAND);
                     this.lastContainerOpenTick = this.tickCounter;
                  }

                  this.lastInteractTick = this.tickCounter;
               }

               if (this.getTicksInState() > this.guiTimeout.getValue()) {
                  this.failCurrentGoal("it didn't open (blocked lid, or not a container?)");
               }
            }
         }
      }
   }

   private RoleDimension getActiveStorage() {
      VillagerTradeBot.Goal deque = this.goalStack.peek();
      if (deque != null && this.isEnabled()) {
         return this.botState != VillagerTradeBot.State.TO_STORAGE
               && this.botState != VillagerTradeBot.State.OPEN_STORAGE
               && this.botState != VillagerTradeBot.State.STORAGE_OP
               && this.botState != VillagerTradeBot.State.DROP_OP
            ? null
            : deque.storage;
      } else {
         return null;
      }
   }

   private void endTradeSession() {
      if (this.tradeSessionActive) {
         InventoryStackUtilsHandle.handle(this);
         this.tradeSessionActive = false;
      }
   }

   public VillagerTradeBot() {
      super("Auto Villager Trade", IconsPngType.DONUTSMP);
      this.pauseOnGuiSetting = new BooleanSetting("Pause On Your GUI", "Stand down while you have a GUI open that the bot did not open itself.", true);
      this.statusOverlay = new BooleanSetting("Status Overlay", "Show a small card on screen with what the bot is doing right now.", true);
      this.searchRange = new IntegerSetting("Search Range", "How far (blocks) to scan for and use villagers.", 32, 4, 96);
      this.selectionMode = new EnumSetting(
         "Selection Mode",
         "How to pick when several villagers offer a matching trade.",
         "Best Score",
         "Cheapest",
         "Closest",
         "Most Stock",
         "Round Robin",
         "User Priority",
         "Best Score"
      );
      this.villagerEsp = new BooleanSetting("Villager ESP", "Draw boxes and labels on tracked villagers (target / resource / exhausted).", true);
      this.interactionRange = new DoubleSetting("Interaction Range", "How close to stand to a villager before opening the trade.", 3.0, 1.5, 4.5, 0.1);
      this.targetProfessionSetting = new EnumSetting("Profession", "Which villager profession this target uses.", "Librarian", NoneCartographerUtils.getMasonCartographer());
      this.targetActionSetting = new EnumSetting("Action", "Buy the output, or sell the item for emeralds.", "Buy", "Buy", "Sell");
      this.outputKindSetting = new EnumSetting(
         "Output", "What the trade produces: an enchanted book, an item, or emeralds (for a sell).", "Enchanted Book", "Enchanted Book", "Item", "Emerald"
      );
      this.enchantmentSetting = new EnumSetting(
         "Enchantment", "For an enchanted-book target: which enchantment to buy.", "Mending", ProtectionCurseUtils.getProtectionFire()
      );
      this.enchantLevelSetting = new IntegerSetting("Enchant Level", "Minimum stored enchant level to accept.", 1, 1, 5);
      this.targetItemSetting = new EnumSetting("Item", "The item to buy (Item output) or to sell (Sell action).", "Pumpkin", IngotBallUtils.getBookIngot());
      this.maxEmeraldCostSetting = new IntegerSetting("Max Emerald Cost", "Skip trades that cost more emeralds than this (0 = any price).", 0, 0, 64);
      this.targetAmountSetting = new IntegerSetting("Target Amount", "Stop this target after producing this many outputs (0 = unlimited).", 0, 0, 4608);
      this.targetPrioritySetting = new IntegerSetting("Priority", "Higher-priority targets are pursued first.", 100, 0, 100);
      this.autoDropOutput = new BooleanSetting(
         "Auto Drop Output",
         "Throw the bought items away instead of keeping them. With a Drop Spot marked (Villager Trader \u2192 Drop spot \u2192 Mark) the bot collects them and throws them all onto that spot; with no Drop Spot it drops each one the instant it arrives (validated \u2014 only what the bot bought is dropped; items you carried before Start are kept).",
         false
      );
      this.autoAcquireEmeralds = new BooleanSetting(
         "Auto Acquire Emeralds", "When a purchase runs short on emeralds, sell goods to a villager to make more, then resume.", true
      );
      this.emeraldSource = new EnumSetting(
         "Emerald Source", "Which profession to sell to for emeralds.", "Farmer", "Farmer", "Fletcher", "Cleric", "Armorer", "Toolsmith", "Weaponsmith", "Any"
      );
      this.sellItemSetting = new EnumSetting("Sell Item", "Which item to sell for emeralds.", "Pumpkin", IngotBallUtils.getIngotLeather());
      this.emeraldReserve = new IntegerSetting("Emerald Reserve", "Keep at least this many emeralds on hand after acquiring.", 64, 0, 4608);
      this.materials = new BooleanSetting("Auto Withdraw Materials", "Pull missing trade materials (e.g. books) from a MATERIALS storage.", true);
      this.autoDepositOutputs = new BooleanSetting(
         "Auto Deposit Outputs", "Deposit outputs into an OUTPUTS storage when the inventory fills or a STORE rule applies.", true
      );
      this.navigation = new BooleanSetting("Navigation", "Walk to villagers and storage automatically (off = only trade what is already in reach).", true);
      this.turnSpeed = new IntegerSetting("Turn Speed", "Aim speed (1 = gentle/slow, 10 = snappy).", 6, 1, 10);
      this.tradeDelay = new IntegerSetting("Trade Delay", "Ticks between merchant actions (lower = faster, higher = safer).", 4, 1, 20);
      this.interactionDelay = new IntegerSetting("Interaction Delay", "Ticks between villager interactions.", 6, 1, 40);
      this.retryDelay = new IntegerSetting("Retry Delay", "Ticks to wait before recovering from a failure.", 20, 5, 100);
      this.guiTimeout = new IntegerSetting("GUI Timeout", "Ticks to wait for a trade/container GUI to open before recovering.", 60, 20, 200);
      this.restockCheckDelay = new IntegerSetting(
         "Restock Check Delay",
         "Ticks before the bot walks back to a sold-out villager to see if it restocked (each villager is checked in turn, oldest first; a villager working at its job block is checked right away).",
         600,
         200,
         6000,
         20
      );
      this.addLookedAtTradeKey = new KeybindSetting("Add Looked-At Trade", "Look at a villager and press this to add its matching trade as a target.");
      this.markStorageKey = new KeybindSetting("Mark Storage", "Look at a container and press this to register it as a storage site.");
      this.clearAddedKey = new KeybindSetting("Clear Added Targets/Storage", "Remove every target and storage added via the keybinds.");
      this.storageRoleSetting = new EnumSetting(
         "Storage Role",
         "Role assigned to the next block you mark. Drop Spot = where the bot walks to and throws its outputs when Auto Drop Output is on (any block: hopper, water, lava\u2026).",
         "Any",
         "Any",
         "Materials",
         "Outputs",
         "Emeralds",
         "Drop Spot"
      );
      this.aimController = new ClampPitch(0.4F, 3.0F);
      this.pathWalker = new BlockWalker(this.aimController);
      this.villagerEntityHelper = new VillagerTracker();
      this.stackMerchant = new MerchantTrader();
      this.acquireEmeraldsTarget = new VillagerTradeRule();
      this.tradeTargets = new ArrayList<>();
      this.markedStorages = new ArrayList<>();
      this.targetPausedUntilTick = new IdentityHashMap<>();
      this.pauseReasonByTarget = new IdentityHashMap<>();
      this.carriedItems = new ArrayList<>();
      this.knownCarriedItems = new ArrayList<>();
      this.materialUnavailableUntilTick = new HashMap<>();
      this.messageLastTickByKey = new HashMap<>();
      this.workSoundQueue = new ConcurrentLinkedQueue<>();
      this.pick = VillagerTradeBot.Pick.NONE;
      this.pickStorageRole = Role.OUTPUTS;
      this.pausedReasonText = "";
      this.statusMessageText = "";
      this.cachedStatusText = "";
      this.idleStatusText = "";
      this.tradeSummaryText = "";
      this.labeledStorageKeys = new HashSet<>();
      this.botState = VillagerTradeBot.State.IDLE;
      this.lastVillagerScanTick = -4611686018427387904L;
      this.lastInteractTick = -4611686018427387904L;
      this.lastVillagerInteractTick = -4611686018427387904L;
      this.lastContainerOpenTick = -4611686018427387904L;
      this.idleUntilTick = -4611686018427387904L;
      this.nextDepositTick = -4611686018427387904L;
      this.screenOpenedTick = -4611686018427387904L;
      this.lastOutputChangeTick = -4611686018427387904L;
      this.lastDepositSlotCount = Integer.MAX_VALUE;
      this.lastWithdrawCount = Integer.MIN_VALUE;
      this.lastScreenOutputTick = -4611686018427387904L;
      this.lastOutputStackCount = Integer.MAX_VALUE;
      this.strayPickupTick = -4611686018427387904L;
      this.lastOutputWaitMessageTick = -4611686018427387904L;
      this.goalStack = new ArrayDeque<>();

      for (ConfigSetting autoEspBase : new ConfigSetting[]{this.searchRange, this.selectionMode, this.villagerEsp, this.interactionRange}) {
         autoEspBase.withGroupName("Villager Selection");
      }

      for (ConfigSetting autoEspBase2 : new ConfigSetting[]{
         this.targetProfessionSetting,
         this.targetActionSetting,
         this.outputKindSetting,
         this.enchantmentSetting,
         this.enchantLevelSetting,
         this.targetItemSetting,
         this.maxEmeraldCostSetting,
         this.targetAmountSetting,
         this.targetPrioritySetting,
         this.autoDropOutput
      }) {
         autoEspBase2.withGroupName("Primary Trade");
      }

      for (ConfigSetting autoEspBase3 : new ConfigSetting[]{this.autoAcquireEmeralds, this.emeraldSource, this.sellItemSetting, this.emeraldReserve, this.materials, this.autoDepositOutputs}) {
         autoEspBase3.withGroupName("Resources");
      }

      for (ConfigSetting autoEspBase4 : new ConfigSetting[]{
         this.navigation, this.turnSpeed, this.tradeDelay, this.interactionDelay, this.retryDelay, this.guiTimeout, this.restockCheckDelay
      }) {
         autoEspBase4.withGroupName("Movement & Timing");
      }

      for (ConfigSetting autoEspBase5 : new ConfigSetting[]{this.addLookedAtTradeKey, this.markStorageKey, this.clearAddedKey, this.storageRoleSetting}) {
         autoEspBase5.withGroupName("Storage & Keybinds");
      }

      this.addSettings(
         new ConfigSetting[]{
            this.automationMode,
            this.pauseWhenMoving,
            this.pauseOnGuiSetting,
            this.statusOverlay,
            this.searchRange,
            this.selectionMode,
            this.villagerEsp,
            this.interactionRange,
            this.targetProfessionSetting,
            this.targetActionSetting,
            this.outputKindSetting,
            this.enchantmentSetting,
            this.enchantLevelSetting,
            this.targetItemSetting,
            this.maxEmeraldCostSetting,
            this.targetAmountSetting,
            this.targetPrioritySetting,
            this.autoDropOutput,
            this.autoAcquireEmeralds,
            this.emeraldSource,
            this.sellItemSetting,
            this.emeraldReserve,
            this.materials,
            this.autoDepositOutputs,
            this.navigation,
            this.turnSpeed,
            this.tradeDelay,
            this.interactionDelay,
            this.retryDelay,
            this.guiTimeout,
            this.restockCheckDelay,
            this.addLookedAtTradeKey,
            this.markStorageKey,
            this.clearAddedKey,
            this.storageRoleSetting
         }
      );

      for (ConfigSetting autoEspBase6 : new ConfigSetting[]{
         this.targetProfessionSetting,
         this.targetActionSetting,
         this.outputKindSetting,
         this.enchantmentSetting,
         this.enchantLevelSetting,
         this.targetItemSetting,
         this.maxEmeraldCostSetting,
         this.targetAmountSetting,
         this.targetPrioritySetting,
         this.addLookedAtTradeKey,
         this.markStorageKey,
         this.clearAddedKey,
         this.storageRoleSetting
      }) {
         autoEspBase6.hideFromList();
      }

      this.stackMerchant.setDropFilter(this::isDepositableStack);
   }

   private VillagerTradeBot.Goal createStrayPickupGoal() {
      if (this.strayDropStorage != null && this.tickCounter >= this.strayPickupTick && mc.world != null) {
         RoleDimension roleDimension = this.strayDropStorage;
         this.strayDropStorage = null;
         ItemEntity next = this.findStrayItem(roleDimension);
         if (next == null) {
            this.strayPickupAttempts = 0;
            return null;
         } else if (this.navigation.isEnabled() && ++this.strayPickupAttempts <= 2) {
            VillagerTradeBot.Goal goal = new VillagerTradeBot.Goal();
            goal.goalKind = VillagerTradeBot.GoalKind.PICKUP;
            goal.itemEntityId = next.getId();
            goal.storage = roleDimension;
            goal.description = "pick up a missed item";
            return goal;
         } else {
            this.strayPickupAttempts = 0;
            this.sendThrottledMessage(
               "stray:" + roleDimension.formatRole(),
               "Some items land next to the drop spot @ "
                  + roleDimension.getBlockPos().toShortString()
                  + " instead of in it \u2014 clear the blocks around it so the bot can toss from right beside it.",
               false,
               6000L
            );
            return null;
         }
      } else {
         return null;
      }
   }

   private VillagerTradeRule createTargetFromVillager(TrackedVillager villagerExperience) {
      String profession = NoneCartographerUtils.getKey(villagerExperience.profession);

      for (OfferTrade offerTrade : villagerExperience.offers) {
         if (offerTrade.emeraldStackActive && offerTrade.next != null) {
            String tradeProfessionText = ProtectionCurseUtils.getKey(offerTrade.next);
            if (ProtectionCurseUtils.getProtectionFire(tradeProfessionText) != null) {
               VillagerTradeRule librarianEnchant = VillagerTradeRule.createBuyEnchantedBookRule(tradeProfessionText, Math.max(1, offerTrade.level), 80, false);
               librarianEnchant.profession = profession;
               return librarianEnchant;
            }
         }
      }

      for (OfferTrade offerTrade2 : villagerExperience.offers) {
         if (IngotBallUtils.canExperienceBottle(offerTrade2.emeraldStackItem)) {
            return createTargetFromOffer(offerTrade2, profession);
         }
      }

      for (OfferTrade offerTrade3 : villagerExperience.offers) {
         if (offerTrade3.itemStack && offerTrade3.primaryCostItem != null) {
            String defaultedRegistryText = IngotBallUtils.getKey(offerTrade3.primaryCostItem);
            if (IngotBallUtils.getDefaultedRegistry(defaultedRegistryText) != null) {
               return VillagerTradeRule.createSellItemRule(profession, defaultedRegistryText, 60);
            }
         }
      }

      return null;
   }

   private VillagerTradeRule getAcquireEmeraldsTarget() {
      this.acquireEmeraldsTarget.enabled = true;
      this.acquireEmeraldsTarget.action = Action.SELL;
      this.acquireEmeraldsTarget.kind = OutputKind.EMERALD;
      this.acquireEmeraldsTarget.profession = this.emeraldSource.getCurrentOption();
      this.acquireEmeraldsTarget.item = this.sellItemSetting.getCurrentOption();
      this.acquireEmeraldsTarget.priority = 60;
      this.acquireEmeraldsTarget.targetAmount = 0;
      return this.acquireEmeraldsTarget;
   }

   private boolean isMerchantScreenOpen() {
      return mc.currentScreen instanceof MerchantScreen && mc.player.currentScreenHandler instanceof MerchantScreenHandler;
   }

   private boolean isMovementKeyPressed() {
      return mc.options.forwardKey.isPressed()
         || mc.options.backKey.isPressed()
         || mc.options.leftKey.isPressed()
         || mc.options.rightKey.isPressed()
         || mc.options.jumpKey.isPressed();
   }

   private boolean matchesBuyTarget(VillagerTradeRule enchantJsonProfession, ItemStack stack) {
      if (enchantJsonProfession != null && enchantJsonProfession.enabled && enchantJsonProfession.action == Action.BUY) {
         return switch (enchantJsonProfession.kind) {
            case ENCHANTED_BOOK -> EnchantJsonKeep.hasStoredEnchantmentAtLeast(stack, enchantJsonProfession.enchant, Math.max(1, enchantJsonProfession.enchantLevel));
            case ITEM -> {
               Item defaultedRegistry = IngotBallUtils.getDefaultedRegistry(enchantJsonProfession.item);
               yield defaultedRegistry != null && stack.isOf(defaultedRegistry);
            }
            case EMERALD -> false;
         };
      } else {
         return false;
      }
   }

   private boolean isDepositableStack(ItemStack stack) {
      if (!this.isTradeOutputStack(stack)) {
         return false;
      } else {
         VillagerTradeBot.Owned next = this.findCarriedEntry(stack);
         if (next == null) {
            return true;
         } else {
            int villagerTradeValue = this.getCurrentOwnedCount(next);
            return villagerTradeValue <= 0 || stack.getCount() <= this.countMatchingStacks(next.stackTemplate) - villagerTradeValue;
         }
      }
   }

   private void tickStorageTransfer() {
      VillagerTradeBot.Goal deque = this.goalStack.peek();
      if (deque != null && this.isStorageScreenOpen()) {
         boolean outputMakeFlag = deque.goalKind == VillagerTradeBot.GoalKind.DEPOSIT && this.tickCounter - this.lastScreenOutputTick <= 40L;
         if (this.getTicksInState() > (outputMakeFlag ? 2400 : 300)) {
            this.closeOpenScreens();
            this.endTradeSession();
            this.popGoal();
            this.setBotState(VillagerTradeBot.State.PLAN);
         } else if (this.tickCounter - this.screenOpenedTick >= 4L) {
            if (this.tickCounter - this.lastOutputChangeTick >= 2L) {
               if (deque.goalKind == VillagerTradeBot.GoalKind.DEPOSIT) {
                  int value = this.countDepositableStacks();
                  if (this.lastDepositSlotCount != Integer.MAX_VALUE && value < this.lastDepositSlotCount) {
                     this.movedItemCount = this.movedItemCount + (this.lastDepositSlotCount - value);
                     this.lastScreenOutputTick = this.tickCounter;
                     if (deque.storage != null) {
                        deque.storage.handleOverworldOutput(false, this.tickCounter);
                     }
                  }

                  if (value == 0) {
                     if (deque.storage != null) {
                        deque.storage.handleOverworldOutput(false, this.tickCounter);
                     }

                     this.closeOpenScreens();
                     this.endTradeSession();
                     this.popGoal();
                     this.setBotState(VillagerTradeBot.State.PLAN);
                  } else if (value >= this.lastDepositSlotCount) {
                     if (this.tickCounter - this.lastScreenOutputTick < 40L) {
                        if (this.tickCounter - this.lastOutputChangeTick >= 4L) {
                           int slotsSlot = this.findPlayerSlotInScreen(this::isDepositableStack);
                           if (slotsSlot >= 0) {
                              this.quickMoveSlot(slotsSlot);
                              this.lastOutputChangeTick = this.tickCounter;
                           }
                        }
                     } else {
                        if (deque.storage != null) {
                           this.markStorageFull(deque.storage);
                        }

                        this.closeOpenScreens();
                        this.endTradeSession();
                        this.popGoal();
                        this.setBotState(VillagerTradeBot.State.PLAN);
                     }
                  } else {
                     this.lastDepositSlotCount = value;
                     int playerSlotInScreen = this.findPlayerSlotInScreen(this::isDepositableStack);
                     if (playerSlotInScreen >= 0) {
                        this.quickMoveSlot(playerSlotInScreen);
                        this.lastOutputChangeTick = this.tickCounter;
                     }
                  }
               } else if (deque.goalKind == VillagerTradeBot.GoalKind.WITHDRAW) {
                  int makeRoomValue = this.countItem(deque.withdrawItem);
                  if (makeRoomValue >= deque.withdrawAmount) {
                     this.materialUnavailableUntilTick.remove(deque.withdrawItem);
                     this.closeOpenScreens();
                     this.endTradeSession();
                     this.popGoal();
                     this.setBotState(VillagerTradeBot.State.PLAN);
                  } else if (makeRoomValue <= this.lastWithdrawCount) {
                     this.materialUnavailableUntilTick.remove(deque.withdrawItem);
                     this.closeOpenScreens();
                     this.endTradeSession();
                     VillagerTradeBot.Goal goal = this.hasDepositableStacks() ? this.createDepositGoal("make room") : null;
                     if (goal != null) {
                        this.goalStack.push(goal);
                     } else {
                        this.popGoal();
                     }

                     this.setBotState(VillagerTradeBot.State.PLAN);
                  } else {
                     this.lastWithdrawCount = makeRoomValue;
                     int slotStack = this.findContainerSlot(stack -> stack.isOf(deque.withdrawItem));
                     if (slotStack >= 0) {
                        this.quickMoveSlot(slotStack);
                        this.movedItemCount++;
                        this.lastOutputChangeTick = this.tickCounter;
                     } else {
                        if (this.movedItemCount > 0) {
                           this.materialUnavailableUntilTick.remove(deque.withdrawItem);
                        } else {
                           this.materialUnavailableUntilTick.put(deque.withdrawItem, this.tickCounter + (deque.withdrawItem == Items.EMERALD ? 200 : 600));
                        }

                        this.closeOpenScreens();
                        this.endTradeSession();
                        this.popGoal();
                        this.setBotState(VillagerTradeBot.State.PLAN);
                     }
                  }
               } else {
                  this.closeOpenScreens();
                  this.endTradeSession();
                  this.popGoal();
                  this.setBotState(VillagerTradeBot.State.PLAN);
               }
            }
         }
      } else {
         this.closeOpenScreens();
         this.endTradeSession();
         this.setBotState(VillagerTradeBot.State.PLAN);
      }
   }

   private boolean canAcquireEmeralds() {
      return this.autoAcquireEmeralds.isEnabled() && !this.automationMode.getCurrentOption().equals("Buy Only");
   }

   @Override
   public void loadExtraData(JsonElement json) {
      this.tradeTargets.clear();
      this.markedStorages.clear();
      if (json instanceof JsonObject jsonObject) {
         try {
            if (jsonObject.has("targets")) {
               for (JsonElement jsonElement : jsonObject.getAsJsonArray("targets")) {
                  if (jsonElement.isJsonObject()) {
                     this.tradeTargets.add(VillagerTradeRule.fromJson(jsonElement.getAsJsonObject()));
                  }
               }
            }

            if (jsonObject.has("storages")) {
               for (JsonElement getAsJsonItem : jsonObject.getAsJsonArray("storages")) {
                  if (getAsJsonItem.isJsonObject()) {
                     this.markedStorages.add(RoleDimension.getRoleDimension(getAsJsonItem.getAsJsonObject()));
                  }
               }
            }
         } catch (Exception e) {
         }

         if (!jsonObject.has("v")) {
            VillagerTradeRule emeraldSell = this.buildTargetFromSettings();
            if (this.indexOfTarget(emeraldSell) < 0) {
               this.tradeTargets.add(0, emeraldSell);
            }

            this.tradeTargets.sort((enchantJsonProfession, enchantJsonProfession2) -> Integer.compare(enchantJsonProfession2.priority, enchantJsonProfession.priority));
            if (this.restockCheckDelay.getValue() < 600) {
               this.restockCheckDelay.handleMax(600);
            }
         }

         this.reassignPriorities();
      }
   }

   private boolean isFullCubeAt(BlockPos pos) {
      BlockState blockState = mc.world.getBlockState(pos);
      return !blockState.isAir() && blockState.isFullCube(mc.world, pos);
   }

   private boolean isContainerBlock(BlockPos pos2) {
      BlockEntity blockEntity = mc.world.getBlockEntity(pos2);
      return blockEntity instanceof LootableContainerBlockEntity || blockEntity instanceof EnderChestBlockEntity;
   }

   private boolean hasProtectedOutputStacks() {
      if (mc.player == null) {
         return false;
      } else {
         PlayerInventory inventory = mc.player.getInventory();

         for (int i = 0; i <= 35; i++) {
            ItemStack stack = inventory.getStack(i);
            if (this.isTradeOutputStack(stack) && !this.isDepositableStack(stack)) {
               return true;
            }
         }

         return false;
      }
   }

   private void onNeedMaterials(VillagerTradeBot.Goal goal) {
      this.closeOpenScreens();
      this.stackMerchant.reset();
      this.endTradeSession();
      this.queueMaterialWithdraw(goal, this.stackMerchant.getMissingItem());
   }

   private boolean isMaterialUnavailable(Item emerald) {
      Long makeroommap = this.materialUnavailableUntilTick.get(emerald);
      return makeroommap != null && this.tickCounter < makeroommap;
   }

   private void tickRecover() {
      this.closeOpenScreens();
      this.stackMerchant.reset();
      this.pathWalker.clearTarget();
      this.endTradeSession();
      if (this.getTicksInState() >= this.retryDelay.getValue()) {
         VillagerTradeBot.Goal deque = this.goalStack.peek();
         if (deque != null && deque.failureCount >= 2) {
            this.blacklistFailedGoal(deque);
         }

         this.setBotState(VillagerTradeBot.State.SCAN);
      }
   }

   private RoleDimension findNearestOutputStorage() {
      if (mc.world != null && mc.player != null) {
         String villagerTradeText = mc.world.getRegistryKey().getValue().toString();
         RoleDimension next = null;
         double villagerTradeLimit = Double.MAX_VALUE;

         for (RoleDimension roleDimension : this.markedStorages) {
            if (roleDimension.canDimension(villagerTradeText)
               && roleDimension.isRoleOutputs()
               && this.isStorageUsable(roleDimension)
               && (!roleDimension.outputWaitingFlag || this.tickCounter - roleDimension.overworldMinecraftTimeOverwo >= 200L)) {
               double squaredDistanceTo = roleDimension.getVec3d().squaredDistanceTo(mc.player.getEyePos());
               if (squaredDistanceTo < villagerTradeLimit) {
                  villagerTradeLimit = squaredDistanceTo;
                  next = roleDimension;
               }
            }
         }

         return next;
      } else {
         return null;
      }
   }

   public void updateVillagerTrader() {
      if (this.awaitUseKeyRelease && !mc.options.useKey.isPressed()) {
         this.awaitUseKeyRelease = false;
      }

      if (this.openTraderScreenPending && System.currentTimeMillis() - this.pickConfirmedMillis > 5000L) {
         this.openTraderScreenPending = false;
      }

      if (this.openTraderScreenPending && mc.player != null && mc.currentScreen == null) {
         this.openTraderScreenPending = false;
         VillagerTrader.handleScreenInstance(this);
      } else if (this.pick != VillagerTradeBot.Pick.NONE) {
         if (mc.player != null && mc.world != null) {
            long timeMillis = System.currentTimeMillis();
            boolean villagerTraderFlag = mc.currentScreen instanceof MerchantScreen
               && mc.player.currentScreenHandler instanceof MerchantScreenHandler;
            if (!villagerTraderFlag && timeMillis - this.pickStartMillis > 120000L) {
               this.cancelPick("Picking timed out \u2014 open the Villager Trader to try again.");
            } else if (this.pick == VillagerTradeBot.Pick.TRADE) {
               if (!villagerTraderFlag) {
                  this.pickScreenOpenedMillis = 0L;
               } else if (mc.crosshairTarget instanceof EntityHitResult entityHitResult
                  && entityHitResult.getEntity() instanceof MerchantEntity merchantEntity
                  && !(merchantEntity instanceof VillagerEntity)) {
                  mc.player.closeHandledScreen();
                  this.showPickHint("Wandering traders walk away \u2014 pick a villager instead.");
                  this.pickScreenOpenedMillis = 0L;
               } else {
                  if (this.pickScreenOpenedMillis == 0L) {
                     this.pickScreenOpenedMillis = timeMillis;
                  }

                  TradeOfferList recipes = ((MerchantScreenHandler)mc.player.currentScreenHandler).getRecipes();
                  if (!recipes.isEmpty() || timeMillis - this.pickScreenOpenedMillis >= 2500L) {
                     var arrayList = new ArrayList();

                     for (int i = 0; i < recipes.size(); i++) {
                        if (recipes.get(i) != null) {
                           arrayList.add(OfferTrade.getDisplayedUses(recipes.get(i), i));
                        }
                     }

                     VillagerEntity entity = this.getTargetedVillager();
                     String villagerTraderText = entity == null ? "Any" : NoneCartographerUtils.getKey(entity.getVillagerData().profession());
                     int villagerTraderValue = entity == null ? 0 : entity.getVillagerData().level();
                     this.pick = VillagerTradeBot.Pick.NONE;
                     this.pickScreenOpenedMillis = 0L;
                     mc.player.closeHandledScreen();
                     VillagerTrader.handleScreenInstance(this, arrayList, villagerTraderText, villagerTraderValue);
                  }
               }
            }
         } else {
            this.cancelPick(null);
         }
      }
   }

   private void tickWalkToStorage() {
      VillagerTradeBot.Goal deque = this.goalStack.peek();
      if (deque == null) {
         this.setBotState(VillagerTradeBot.State.PLAN);
      } else {
         RoleDimension roleDimension = deque.storage;
         if (roleDimension == null) {
            this.goalStack.pop();
            this.setBotState(VillagerTradeBot.State.PLAN);
         } else if (roleDimension.role == Role.DROP) {
            this.walkToDropSpot(deque, roleDimension);
         } else {
            Vec3d vec3d = roleDimension.getVec3d();
            if (vec3d.distanceTo(mc.player.getEyePos()) <= 3.4) {
               this.pathWalker.clearTarget();
               this.setBotState(VillagerTradeBot.State.OPEN_STORAGE);
            } else {
               this.pathWalker.setTarget(this.getApproachPos(roleDimension.getBlockPos()), 1.6);
               Status posTime = this.pathWalker.tickWalk();
               this.walkedThisTick = true;
               if (posTime == Status.ARRIVED) {
                  this.setBotState(VillagerTradeBot.State.OPEN_STORAGE);
               } else if (posTime == Status.STUCK || this.getTicksInState() > 400L) {
                  this.failCurrentGoal("couldn't walk to it");
               }
            }
         }
      }
   }

   private float[] getOverlayPosition(float perfTopbarValue, float perfTopbarValue2) {
      Window window = mc.getWindow();
      float scaledWidth = window.getScaledWidth();
      double scaleFactor = window.getScaleFactor();
      int calculateScaleFactor = window.calculateScaleFactor(ShowHUD.getMax(mc), mc.forcesUnicodeFont());
      float perfTopbarDivisor = scaleFactor > 0.0 ? (float)(calculateScaleFactor / scaleFactor) : 1.0F;
      if (!(perfTopbarDivisor > 0.0F)) {
         perfTopbarDivisor = 1.0F;
      }

      int spotifySong = SpotifyHUD.getSpotifySong();
      float hudValue = spotifySong > 0 ? spotifySong + 5.0F : 6.0F;
      float perfTopbarFactor = (scaledWidth / perfTopbarDivisor - perfTopbarValue / perfTopbarDivisor) / 2.0F;
      float amounts2 = ShowHUD.getAmounts(perfTopbarFactor, hudValue, perfTopbarValue / perfTopbarDivisor, perfTopbarValue2 / perfTopbarDivisor, "topbar", "perf", "clock", "potions", "modules");
      return new float[]{Math.max(4.0F, perfTopbarFactor * perfTopbarDivisor), Math.max(4.0F, amounts2 * perfTopbarDivisor)};
   }

   private double getAimClearance(RoleDimension roleDimension) {
      Vec3d vec3d = this.getStorageTopCenter(roleDimension);
      double hypot = Math.hypot(vec3d.x - mc.player.getX(), vec3d.z - mc.player.getZ());
      double eyePlayerValue = vec3d.y - (mc.player.getEyeY() - 0.3);
      return !this.hasLineOfSightToStorage(mc.player.getEyePos(), roleDimension) ? -1.0 : AmountTradeUtils.getAmountForDouble(hypot, eyePlayerValue);
   }

   private boolean isStandable(BlockPos pos) {
      try {
         return WalkRouteFinder.isStandable(mc.world, pos);
      } catch (Exception e) {
         return false;
      }
   }

   private void tickAssistedMode() {
      if (this.isMerchantScreenOpen()) {
         if (!this.stackMerchant.hasTradeRule()) {
            VillagerTradeRule next = this.findTargetInOpenScreen();
            if (next == null) {
               return;
            }

            this.beginTradeSession();
            this.stackMerchant.startWithCursorPolicy(next, this.tradeDelay.getValue(), true);
         }

         Result unconfirmedFull = this.stackMerchant.tickTrading();
         if (unconfirmedFull != Result.RUNNING && unconfirmedFull != Result.TRADED) {
            if (unconfirmedFull == Result.NEED_EMERALDS || unconfirmedFull == Result.NEED_MATERIALS || unconfirmedFull == Result.INV_FULL || unconfirmedFull == Result.FAILED) {
               this.sendThrottledMessage("assist:" + unconfirmedFull, "Can't trade here \u2014 " + this.stackMerchant.getStatusMessage() + ".", false, 600L);
            }

            this.stackMerchant.reset();
            this.endTradeSession();
         }
      }
   }

   private void nudgeTowardBlock(BlockPos pos) {
      double optionsPlayerValue = pos.getX() + 0.5 - mc.player.getX();
      double x = pos.getZ() + 0.5 - mc.player.getZ();
      float degrees = (float)Math.toDegrees(Math.atan2(-optionsPlayerValue, x));
      this.aimController.setTargetAngles(degrees, 25.0F, 0);
      if (Math.abs(MathHelper.wrapDegrees(degrees - mc.player.getYaw())) < 30.0F) {
         ElytraAutoUtils.handleRawPressed(mc.options.sneakKey, true);
         mc.options.forwardKey.setPressed(true);
      }
   }

   private boolean isDropSpotActive() {
      return this.autoDropOutput.isEnabled() && mc.world != null && this.hasStorageForRole(Role.DROP);
   }

   private String describeVillagerDistance(VillagerTradeBot.Goal goal) {
      VillagerEntity villagerEntity = goal == null ? null : this.villagerEntityHelper.findVillagerEntity(goal.villagerUuid);
      return villagerEntity != null && mc.player != null ? " \u00b7 " + Math.round(Math.sqrt(villagerEntity.squaredDistanceTo(mc.player))) + "m" : "";
   }

   private boolean isStorageUsable(RoleDimension roleDimension) {
      if (this.tickCounter < roleDimension.overworldMinecraftTime) {
         return false;
      } else {
         return !this.navigation.isEnabled() && mc.player != null ? roleDimension.getVec3d().distanceTo(mc.player.getEyePos()) <= 4.5 : true;
      }
   }

   private VillagerTradeBot.Goal createAutoDepositGoal() {
      if ((this.autoDepositOutputs.isEnabled() || this.isDropSpotActive()) && this.tickCounter >= this.nextDepositTick) {
         if (this.hasDepositableStacks() && this.countEmptySlots() <= (this.areAllBuyItemsStackable() ? 1 : 6)) {
            VillagerTradeBot.Goal roleEmeralds = this.createDepositGoal("deposit outputs");
            if (roleEmeralds == null) {
               return null;
            } else {
               this.nextDepositTick = this.tickCounter + 100L;
               return roleEmeralds;
            }
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private void tickWalkToVillager() {
      VillagerTradeBot.Goal deque = this.goalStack.peek();
      if (deque == null) {
         this.setBotState(VillagerTradeBot.State.PLAN);
      } else {
         VillagerEntity byId = this.villagerEntityHelper.findVillagerEntity(deque.villagerUuid);
         if (byId == null) {
            this.abandonGoal(deque);
         } else {
            Vec3d eyePos = byId.getEyePos();
            double distanceTo = eyePos.distanceTo(mc.player.getEyePos());
            BlockPos offset = this.findVillagerStandPos(byId);
            if (offset == null) {
               if (distanceTo <= 3.0) {
                  this.pathWalker.clearTarget();
                  this.setBotState(VillagerTradeBot.State.OPEN_VILLAGER);
                  return;
               }
            } else if (this.isNearBlockCenter(offset, 0.65) && distanceTo <= 3.0) {
               this.pathWalker.clearTarget();
               this.setBotState(VillagerTradeBot.State.OPEN_VILLAGER);
               return;
            }

            BlockPos blockPos = offset != null ? offset : this.getApproachPos(byId.getBlockPos());
            this.pathWalker.setTarget(blockPos, offset != null ? 0.6 : 1.2);
            Status posTime = this.pathWalker.tickWalk();
            this.walkedThisTick = true;
            if (posTime != Status.ARRIVED && (offset != null || !(distanceTo <= 3.0))) {
               if (posTime == Status.STUCK || this.getTicksInState() > 400L) {
                  this.failCurrentGoal("can't walk to it");
               }
            } else {
               this.pathWalker.clearTarget();
               this.setBotState(VillagerTradeBot.State.OPEN_VILLAGER);
            }
         }
      }
   }

   private String describeStorageAction(VillagerTradeBot.Goal goal) {
      if (goal == null) {
         return "Using storage";
      } else if (goal.goalKind != VillagerTradeBot.GoalKind.DEPOSIT) {
         if (goal.goalKind == VillagerTradeBot.GoalKind.WITHDRAW) {
            return goal.withdrawItem == Items.EMERALD
               ? "Taking emeralds from the emerald chest"
               : "Taking " + IngotBallUtils.getKey(goal.withdrawItem) + " from the material chest";
         } else {
            return "Using storage";
         }
      } else {
         return goal.storage != null && goal.storage.role == Role.DROP ? "Taking items to the drop spot" : "Storing items in the output chest";
      }
   }

   private boolean isStorageFull(RoleDimension roleDimension) {
      return roleDimension.outputWaitingFlag && this.tickCounter - roleDimension.overworldMinecraftTimeOverwo < 200L;
   }

   private String markStorageBlock(BlockPos pos2, Role role) {
      String traderVillagerText = this.getDimensionId();
      BlockPos offset = ChestStorageUtils.getOffsetForBlockPos(pos2);
      RoleDimension roleDimension = null;

      for (RoleDimension roleDimension2 : this.markedStorages) {
         if (roleDimension2.canDimension(traderVillagerText) && (isStorageAt(roleDimension2, pos2) || offset != null && isStorageAt(roleDimension2, offset))) {
            roleDimension = roleDimension2;
            break;
         }
      }

      String formatChestStorage = ChestStorageUtils.formatChestStorage(role);
      if (roleDimension != null && roleDimension.role == role) {
         return "That's already your " + formatChestStorage.toLowerCase(Locale.ROOT) + ".";
      } else {
         if (role == Role.DROP) {
            this.markedStorages.removeIf(roleDimension3 -> roleDimension3.role == Role.DROP && roleDimension3.canDimension(traderVillagerText));
         }

         if (roleDimension != null) {
            this.markedStorages.remove(roleDimension);
         }

         this.markedStorages.add(new RoleDimension(pos2, traderVillagerText, role));
         if (roleDimension != null && roleDimension.role == Role.DROP) {
            this.disableDropIfNoSpot();
         }

         ModuleConfigAutosave.updateFlag();
         return roleDimension != null ? "Changed it to your " + formatChestStorage.toLowerCase(Locale.ROOT) + "." : "Marked your " + formatChestStorage.toLowerCase(Locale.ROOT) + ".";
      }
   }

   private int countDepositableStacks() {
      if (mc.player == null) {
         return 0;
      } else {
         PlayerInventory inventory = mc.player.getInventory();
         int inventoryPlayer = 0;

         for (int i = 0; i <= 35; i++) {
            if (this.isDepositableStack(inventory.getStack(i))) {
               inventoryPlayer++;
            }
         }

         return inventoryPlayer;
      }
   }

   private void closeOpenScreens() {
      if (mc.player != null) {
         if (mc.currentScreen instanceof InventoryScreen) {
            if (this.botState == VillagerTradeBot.State.DROP_OP && this.dropInventoryScreen != null && mc.currentScreen == this.dropInventoryScreen) {
               mc.player.closeHandledScreen();
            }
         } else {
            if (mc.currentScreen instanceof HandledScreen) {
               mc.player.closeHandledScreen();
            }
         }
      }
   }

   private String getDimensionId() {
      return mc.world == null ? "" : mc.world.getRegistryKey().getValue().toString();
   }

   private void markLookedAtStorage() {
      if (mc.player != null && mc.crosshairTarget instanceof BlockHitResult blockHitResult && blockHitResult.getType() == Type.BLOCK) {
         String formatText = this.storageRoleSetting.getCurrentOption();

         Role role = switch (formatText) {
            case "Materials" -> Role.MATERIALS;
            case "Outputs" -> Role.OUTPUTS;
            case "Emeralds" -> Role.EMERALDS;
            case "Drop Spot" -> Role.DROP;
            default -> Role.ANY;
         };
         if (role != Role.DROP && !this.isContainerBlock(blockHitResult.getBlockPos())) {
            this.notifyUser("That's not a container \u2014 look at a chest, barrel or shulker box.", false);
         } else {
            this.notifyUser(this.markStorageBlock(blockHitResult.getBlockPos(), role), true);
         }
      } else {
         this.notifyUser("Look at a chest (or the block for the drop spot) to mark it.", false);
      }
   }

   private static String stripTrailingPeriod(String villagerTradeText) {
      return villagerTradeText.endsWith(".") ? villagerTradeText.substring(0, villagerTradeText.length() - 1) : villagerTradeText;
   }

   private BlockHitResult getContainerHitResult(BlockPos pos) {
      Vec3d eyePos = mc.player.getEyePos();
      Vec3d cosSin = MathClampUtils.getCosSin(mc.player.getYaw(), mc.player.getPitch());
      Vec3d add = eyePos.add(cosSin.multiply(5.0));
      BlockHitResult raycast = mc.world.raycast(new RaycastContext(eyePos, add, ShapeType.OUTLINE, FluidHandling.NONE, mc.player));
      if (raycast instanceof BlockHitResult raycast2 && raycast.getType() == Type.BLOCK && raycast2.getBlockPos().getManhattanDistance(pos) <= 1) {
         return raycast2;
      } else {
         Vec3d center = Vec3d.ofCenter(pos);
         Direction facing = Direction.getFacing(eyePos.x - center.x, eyePos.y - center.y, eyePos.z - center.z);
         Vec3d add2 = center.add(new Vec3d(facing.getOffsetX() * 0.5, facing.getOffsetY() * 0.5, facing.getOffsetZ() * 0.5));
         return new BlockHitResult(add2, facing, pos, false);
      }
   }

   private VillagerTradeBot.Goal findNextVillagerGoal() {
      var instanceZero = this.villagerEntityHelper.getSortedByDistance();
      var tradesTrade = this.getRunnableTargets();

      for (VillagerTradeRule next : tradesTrade) {
         List list = TradeEnchantUtils.collectCandidates(instanceZero, next, this::isVillagerReachable);
         if (!list.isEmpty()) {
            Candidate candidate = TradeEnchantUtils.selectCandidate(list, this.getVillagerSelectionMode(), this.getPlayerPos(), this.roundRobinCounter++);
            if (candidate != null) {
               VillagerTradeBot.Goal goal = new VillagerTradeBot.Goal();
               goal.goalKind = next.action == Action.SELL ? VillagerTradeBot.GoalKind.SELL : VillagerTradeBot.GoalKind.BUY;
               goal.target = next;
               goal.villagerUuid = candidate.villager().uuid;
               goal.description = next.formatDescription();
               return goal;
            }
         }
      }

      for (VillagerTradeRule tradesTradeItem : tradesTrade) {
         TrackedVillager outputTrade = this.findVillagerToInspect(tradesTradeItem, instanceZero);
         if (outputTrade != null) {
            outputTrade.levelResetTick = this.tickCounter;
            VillagerTradeBot.Goal goal2 = new VillagerTradeBot.Goal();
            goal2.goalKind = tradesTradeItem.action == Action.SELL ? VillagerTradeBot.GoalKind.SELL : VillagerTradeBot.GoalKind.BUY;
            goal2.target = tradesTradeItem;
            goal2.villagerUuid = outputTrade.uuid;
            goal2.description = "inspect " + outputTrade.professionName;
            return goal2;
         }
      }

      Due timeExpensive = VillagerTradeUtils.findNextDueVillager(instanceZero, tradesTrade, this.tickCounter, this.restockCheckDelay.getValue(), this.getPlayerPos(), this::isVillagerReachable);
      if (timeExpensive != null) {
         TrackedVillager villager = timeExpensive.villager();
         VillagerTradeBot.Goal goal3 = new VillagerTradeBot.Goal();
         goal3.goalKind = timeExpensive.target().action == Action.SELL ? VillagerTradeBot.GoalKind.SELL : VillagerTradeBot.GoalKind.BUY;
         goal3.target = timeExpensive.target();
         goal3.villagerUuid = villager.uuid;
         goal3.isRestockCheck = timeExpensive.hinted();
         goal3.description = "restock check";
         return goal3;
      } else {
         return null;
      }
   }

   public String getPauseReason(VillagerTradeRule enchantJsonProfession) {
      return this.isEnabled() && this.isTargetPaused(enchantJsonProfession) ? CardStatusUtilsFlag.getOrDefault(this.pauseReasonByTarget.getOrDefault(enchantJsonProfession, "")) : "";
   }

   private void reassignPriorities() {
      for (int i = 0; i < this.tradeTargets.size(); i++) {
         this.tradeTargets.get(i).priority = Math.max(0, 100 - i);
      }
   }

   private int findContainerSlot(Predicate<ItemStack> predicate) {
      ScreenHandler currentScreenHandler = mc.player.currentScreenHandler;
      if (currentScreenHandler == null) {
         return -1;
      } else {
         int slotStackLimit = currentScreenHandler.slots.size() - 36;
         if (slotStackLimit <= 0) {
            return -1;
         } else {
            for (int i = 0; i < slotStackLimit; i++) {
               if (predicate.test(currentScreenHandler.getSlot(i).getStack())) {
                  return i;
               }
            }

            return -1;
         }
      }
   }

   public void cancelPick(String traderVillagerText) {
      this.pick = VillagerTradeBot.Pick.NONE;
      this.pickScreenOpenedMillis = 0L;
      if (traderVillagerText != null) {
         DisabledEnabled.getInstance().handleDisabledEnabled("Villager Trader", traderVillagerText, false);
      }
   }

   private void snapshotCarriedItems() {
      this.pendingCarriedSnapshot = false;
      this.carriedSnapshotActive = true;
      ClientPlayNetworkHandler networkHandler = mc.getNetworkHandler();
      String formatServerSave = this.getServerKey();
      boolean youStoresFlag = !formatServerSave.isEmpty() && formatServerSave.equals(this.carriedSnapshotServerKey) && System.currentTimeMillis() - this.carriedSnapshotMillis < 600000L;
      this.carriedItems.clear();

      for (VillagerTradeBot.Owned owned : this.collectCarriedItems()) {
         int totalCount = owned.totalCount;
         if (youStoresFlag) {
            VillagerTradeBot.Owned owned2 = null;

            for (VillagerTradeBot.Owned owned3 : this.knownCarriedItems) {
               if (ItemStack.areItemsAndComponentsEqual(owned3.stackTemplate, owned.stackTemplate)) {
                  owned2 = owned3;
                  break;
               }
            }

            if (owned2 != null) {
               totalCount = owned.totalCount >= owned2.totalCount ? owned2.trackedCount + (owned.totalCount - owned2.totalCount) : Math.min(owned2.trackedCount, owned.totalCount);
            }
         }

         if (totalCount > 0) {
            this.carriedItems.add(new VillagerTradeBot.Owned(owned.stackTemplate, totalCount, owned.totalCount));
         }
      }

      this.lastNetworkHandler = networkHandler;
      this.knownCarriedItems.clear();
      this.carriedSnapshotServerKey = null;
      int youStoresTotal = 0;

      for (VillagerTradeBot.Owned owned4 : this.carriedItems) {
         if (IngotBallUtils.canExperienceBottle(owned4.stackTemplate.getItem())) {
            youStoresTotal += owned4.trackedCount;
         }
      }

      boolean youStoresFlag2 = false;

      for (VillagerTradeRule enchantJsonProfession : this.tradeTargets) {
         youStoresFlag2 |= enchantJsonProfession.enabled && enchantJsonProfession.isExperienceBottleRule();
      }

      if (youStoresTotal > 0 && youStoresFlag2) {
         this.notifyUser(
            "You carry " + youStoresTotal + " XP bottle" + (youStoresTotal == 1 ? "" : "s") + " \u2014 the bot leaves those with you and only stores or drops ones it buys.",
            true
         );
      }
   }

   private int findPlayerSlotInScreen(Predicate<ItemStack> predicate) {
      ScreenHandler currentScreenHandler = mc.player.currentScreenHandler;
      if (currentScreenHandler == null) {
         return -1;
      } else {
         int slotsSlotValue = currentScreenHandler.slots.size() - 36;
         if (slotsSlotValue < 0) {
            return -1;
         } else {
            for (int i = slotsSlotValue; i < currentScreenHandler.slots.size(); i++) {
               if (predicate.test(currentScreenHandler.getSlot(i).getStack())) {
                  return i;
               }
            }

            return -1;
         }
      }
   }

   private String describeMarkedStorages() {
      if (this.markedStorages.isEmpty()) {
         return "No chests marked \u2014 open the Villager Trader and press Mark next to a chest.";
      } else {
         StringBuilder stringBuilder = new StringBuilder("Storage: ");

         for (int i = 0; i < this.markedStorages.size(); i++) {
            if (i > 0) {
               stringBuilder.append(", ");
            }

            RoleDimension villagertraderitem = this.markedStorages.get(i);
            stringBuilder.append(ChestStorageUtils.formatChestStorage(villagertraderitem.role))
               .append(" @ ")
               .append(villagertraderitem.asIntRoleDimension)
               .append(' ')
               .append(villagertraderitem.intValue)
               .append(' ')
               .append(villagertraderitem.asInt);
         }

         return stringBuilder.toString();
      }
   }

   private boolean isSellItemStack(ItemStack stack) {
      if (this.canAcquireEmeralds()) {
         Item defaultedRegistry = IngotBallUtils.getDefaultedRegistry(this.sellItemSetting.getCurrentOption());
         if (defaultedRegistry != null && stack.isOf(defaultedRegistry)) {
            return true;
         }
      }

      for (VillagerTradeRule enchantJsonProfession : this.tradeTargets) {
         if (enchantJsonProfession.enabled && enchantJsonProfession.action == Action.SELL) {
            Item defaultedRegistry2 = IngotBallUtils.getDefaultedRegistry(enchantJsonProfession.item);
            if (defaultedRegistry2 != null && stack.isOf(defaultedRegistry2)) {
               return true;
            }
         }
      }

      return false;
   }

   @Override
   protected void onRender3D(QueueCommand queueCommand) {
      if (mc.world != null && mc.player != null && this.villagerEsp.isEnabled()) {
         Vec3d cameraPos = mc.gameRenderer.getCamera().getCameraPos();
         MatrixStack matrices = queueCommand.matrices;
         matrices.push();
         matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
         Matrix4f positionMatrix = matrices.peek().getPositionMatrix();
         UUID uuid = this.goalStack.peek() != null ? this.goalStack.peek().villagerUuid : null;
         var tradesTrade = this.getRunnableTargets();
         float villagerTradeValue = ChestStorageUtils.getAmountFloat();

         for (TrackedVillager villagerExperience : this.villagerEntityHelper.getAllVillagers()) {
            boolean villagerTradeFlag = uuid != null && uuid.equals(villagerExperience.uuid);
            VillagerTradeRule tradeOpen = this.findMatchingTarget(villagerExperience, tradesTrade);
            if (tradeOpen != null || villagerTradeFlag) {
               Box entityWidth = this.getVillagerBox(villagerExperience);
               if (entityWidth != null) {
                  ChestStorageUtils.handleMinMax(
                     positionMatrix, entityWidth, this.getVillagerBoxColor(villagerExperience, tradeOpen, villagerTradeFlag), villagerTradeFlag ? 1.0F : villagerTradeValue * 0.8F, villagerTradeFlag ? 0.1F : 0.05F, villagerTradeFlag ? 2.0F : 1.5F
                  );
               }
            }
         }

         matrices.pop();
         VertexBuilderUtils.updateDrawImmediate();
      }
   }

   private boolean isAllowedByMode(VillagerTradeRule enchantJsonProfession) {
      String formatText = this.automationMode.getCurrentOption();

      return switch (formatText) {
         case "Buy Only" -> enchantJsonProfession.action == Action.BUY;
         case "Sell Only" -> enchantJsonProfession.action == Action.SELL;
         default -> true;
      };
   }

   private void waitForOutputRoom() {
      if (this.tickCounter - this.lastOutputWaitMessageTick > 600L) {
         boolean outputWaitingFlag = false;

         for (RoleDimension roleDimension : this.markedStorages) {
            if (roleDimension.isRoleOutputs() && roleDimension.outputWaitingFlag && this.tickCounter - roleDimension.overworldMinecraftTimeOverwo < 200L) {
               outputWaitingFlag = true;
            }
         }

         this.notifyUser(
            this.isDropSpotActive()
               ? "Can't reach the drop spot right now \u2014 waiting, then trying again."
               : (
                  outputWaitingFlag
                     ? "All output chests are full \u2014 waiting for room. Empty them, add a hopper, or mark another output chest."
                     : "Can't reach an output chest right now \u2014 waiting, then trying again."
               ),
            false
         );
         this.lastOutputWaitMessageTick = this.tickCounter;
      }

      this.idleStatusText = this.isDropSpotActive() ? "Waiting to reach the drop spot" : "Waiting for room in the output chest";
      this.idleUntilTick = this.tickCounter + 100L;
      this.setBotState(VillagerTradeBot.State.WAIT_RESTOCK);
   }

   @Override
   protected void onPacketReceive(AutoVanish autoVanish) {
      if (this.workSoundQueue.size() < 64) {
         if (autoVanish.packet instanceof PlaySoundS2CPacket playSoundS2CPacket && isVillagerWorkSound(playSoundS2CPacket.getSound())) {
            this.workSoundQueue.add(new VillagerTradeBot.WorkSound(playSoundS2CPacket.getX(), playSoundS2CPacket.getY(), playSoundS2CPacket.getZ(), -1));
         } else if (autoVanish.packet instanceof PlaySoundFromEntityS2CPacket playSoundFromEntityS2CPacket && isVillagerWorkSound(playSoundFromEntityS2CPacket.getSound())) {
            this.workSoundQueue.add(new VillagerTradeBot.WorkSound(0.0, 0.0, 0.0, playSoundFromEntityS2CPacket.getEntityId()));
         }
      }
   }

   private String describeTradeProgress(VillagerTradeBot.Goal goal) {
      if (goal == null || goal.target == null) {
         return "Trading";
      } else if (goal.goalKind == VillagerTradeBot.GoalKind.SELL_FOR_EMERALDS) {
         return "Selling " + IngotBallUtils.formatBookIngot(goal.target.item) + " for emeralds";
      } else {
         return goal.target.action == Action.SELL
            ? "Selling " + IngotBallUtils.formatBookIngot(goal.target.item) + " \u00b7 " + goal.target.tradedAmount + " sold"
            : "Buying " + goal.target.formatTargetName() + " \u00b7 " + goal.target.tradedAmount + " bought";
      }
   }

   private void enterIdleWait() {
      var tradesTrade = this.getRunnableTargets();
      this.idleStatusText = "Waiting for villagers to restock";
      if (tradesTrade.isEmpty() && this.areAllTargetsComplete()) {
         if (!this.hasDepositableStacks() || this.findDepositStorage() == null) {
            this.notifyUser("All trades done \u2014 every trade reached its Stop after amount.", true);
            this.setEnabled(false);
            return;
         }

         this.idleStatusText = "All trades done \u2014 storing the last items";
      } else if (tradesTrade.isEmpty()) {
         for (VillagerTradeRule enchantJsonProfession : this.tradeTargets) {
            String villagersIdleText = this.isTargetPaused(enchantJsonProfession) ? this.pauseReasonByTarget.getOrDefault(enchantJsonProfession, "") : "";
            if (!villagersIdleText.isEmpty()) {
               this.idleStatusText = "Paused \u2014 " + villagersIdleText;
               break;
            }
         }
      } else {
         VillagerTradeRule villagerTradeRule = (VillagerTradeRule)tradesTrade.get(0);
         String villagersIdleText2 = "Any".equals(villagerTradeRule.profession) ? "villager" : villagerTradeRule.profession;
         int villagersIdleCounter = 0;
         int villagersIdleCounter2 = 0;
         int villagersIdleCounter3 = 0;
         int villagersIdleCounter4 = 0;
         int villagersIdleCounter5 = 0;
         int villagersIdleCounter6 = 0;

         for (TrackedVillager villagerExperience : this.villagerEntityHelper.getAllVillagers()) {
            if (villagerExperience.hasProfession && TradeEnchantUtils.matchesProfession(villagerExperience, villagerTradeRule)) {
               villagersIdleCounter++;
               if (villagerExperience.needsLevelUp(villagerTradeRule)) {
                  villagersIdleCounter5++;
                  if (villagerExperience.offers.isEmpty() && this.isVillagerReachable(villagerExperience)) {
                     villagersIdleCounter6++;
                  }
               } else if (villagerExperience.isOnCooldown(this.tickCounter)) {
                  villagersIdleCounter3++;
               } else if (villagerExperience.offers.isEmpty()) {
                  villagersIdleCounter4++;
               } else if (villagerExperience.isBlockedByStockOrPrice(villagerTradeRule)) {
                  villagersIdleCounter2++;
               }
            }
         }

         int deltaVar = villagersIdleCounter - villagersIdleCounter5;
         String formatExpertMaster = NoneCartographerUtils.formatExpertMaster(villagerTradeRule.getBuyItemValue());
         if (deltaVar == 0 && villagersIdleCounter6 > 0) {
            this.idleStatusText = "Checking the villagers' trades";
         } else if (deltaVar == 0 && villagersIdleCounter5 > 0) {
            this.idleStatusText = "No " + formatExpertMaster + " " + villagersIdleText2 + " within " + this.searchRange.getValue() + " blocks";
            this.sendThrottledMessage(
               "idle:level",
               "Only "
                  + formatExpertMaster
                  + " "
                  + villagersIdleText2
                  + "s sell "
                  + villagerTradeRule.formatTargetName()
                  + "s and none is within "
                  + this.searchRange.getValue()
                  + " blocks \u2014 trade with a "
                  + villagersIdleText2
                  + " until it reaches "
                  + formatExpertMaster
                  + ".",
               false,
               6000L
            );
         } else if (villagersIdleCounter4 > 0 && villagersIdleCounter2 == 0 && deltaVar > villagersIdleCounter3) {
            this.idleStatusText = "Checking the villagers' trades";
         } else if (villagersIdleCounter == 0) {
            this.idleStatusText = "No " + villagersIdleText2 + " within " + this.searchRange.getValue() + " blocks";
            this.sendThrottledMessage(
               "idle:none", "No " + villagersIdleText2 + " within " + this.searchRange.getValue() + " blocks for " + villagerTradeRule.formatDescription() + ".", false, 6000L
            );
         } else if (villagersIdleCounter2 > 0) {
            this.idleStatusText = "Sold out \u2014 waiting for a restock";
            this.sendThrottledMessage(
               "idle:soldout",
               villagerTradeRule.formatDescription()
                  + ": every matching "
                  + villagersIdleText2
                  + " is sold out. Re-checking each one every "
                  + this.restockCheckDelay.getValue() / 20
                  + "s, and right away when it works at its job block.",
               true,
               6000L
            );
         } else if (villagersIdleCounter3 == deltaVar) {
            this.idleStatusText = "Villagers out of reach \u2014 retrying soon";
            this.sendThrottledMessage("idle:blocked", "Every " + villagersIdleText2 + " is out of reach right now \u2014 trying again in a minute.", false, 6000L);
         } else if (!this.navigation.isEnabled() && !this.hasReachableVillager()) {
            this.idleStatusText = "Stand next to a " + villagersIdleText2 + " (walking is off)";
            this.sendThrottledMessage("idle:reach", "Navigation is off and no " + villagersIdleText2 + " is within reach \u2014 stand next to one.", false, 6000L);
         } else {
            this.idleStatusText = "No " + villagersIdleText2 + " nearby offers " + villagerTradeRule.formatTargetName();
            this.sendThrottledMessage(
               "idle:nomatch",
               "No " + villagersIdleText2 + " nearby offers " + villagerTradeRule.formatDescription() + " \u2014 check your trade list in the Villager Trader.",
               false,
               6000L
            );
         }
      }

      this.idleUntilTick = this.tickCounter + 20L;
      this.setBotState(VillagerTradeBot.State.WAIT_RESTOCK);
   }

   private void failCurrentGoal(String failingKeptText) {
      if (this.isBotScreenOpen()) {
         this.closeOpenScreens();
      }

      VillagerTradeBot.Goal deque = this.goalStack.peek();
      if (deque != null) {
         deque.failureCount++;
         deque.failureReason = failingKeptText;
      }

      this.setBotState(VillagerTradeBot.State.RECOVER);
   }

   private int countItem(Item defaultedRegistry) {
      if (defaultedRegistry != null && mc.player != null) {
         PlayerInventory inventory = mc.player.getInventory();
         int inventoryPlayer = 0;

         for (int i = 0; i <= 35; i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.isOf(defaultedRegistry)) {
               inventoryPlayer += stack.getCount();
            }
         }

         return inventoryPlayer;
      } else {
         return 0;
      }
   }

   private void scanVillagers() {
      if (this.tickCounter - this.lastVillagerScanTick >= 10L) {
         this.villagerEntityHelper.scanNearbyVillagers(this.tickCounter, this.searchRange.getValue());
         this.lastVillagerScanTick = this.tickCounter;
      }
   }

   private void notifyUser(String villagerTradeText, boolean villagerTradeFlag) {
      DisabledEnabled.getInstance().handleDisabledEnabled("Auto Villager Trade", villagerTradeText, villagerTradeFlag);
      if (mc.player != null) {
         mc.player.sendMessage(Text.literal((villagerTradeFlag ? "\u00a7a[Villager]\u00a7r " : "\u00a7c[Villager]\u00a7r ") + villagerTradeText), false);
      }

      if (!villagerTradeFlag) {
         this.statusMessageText = CardStatusUtilsFlag.getOrDefault(villagerTradeText);
         this.hasStatusMessage = true;
         this.statusMessageMillis = System.currentTimeMillis();
      }
   }

   public int getEmeraldCount() {
      return this.countItem(Items.EMERALD);
   }

   private void disableDropIfNoSpot() {
      if (this.autoDropOutput.isEnabled() && !this.hasStorageForRole(Role.DROP)) {
         this.autoDropOutput.handle(false);
      }
   }

   private boolean isBotScreenOpen() {
      return mc.currentScreen != null && mc.player != null
         ? (this.botState == VillagerTradeBot.State.TRADE || this.botState == VillagerTradeBot.State.OPEN_VILLAGER) && this.isMerchantScreenOpen()
            || (this.botState == VillagerTradeBot.State.STORAGE_OP || this.botState == VillagerTradeBot.State.OPEN_STORAGE) && this.isStorageScreenOpen()
            || this.botState == VillagerTradeBot.State.DROP_OP && this.dropInventoryScreen != null && mc.currentScreen == this.dropInventoryScreen
            || this.isUserScreenOpen()
         : false;
   }

   private int findInventorySlot(Predicate<ItemStack> predicate) {
      PlayerInventory inventory = mc.player.getInventory();

      for (int i = 9; i <= 35; i++) {
         if (predicate.test(inventory.getStack(i))) {
            return i;
         }
      }

      for (int j = 0; j <= 8; j++) {
         if (predicate.test(inventory.getStack(j))) {
            return 36 + j;
         }
      }

      return -1;
   }

   private BlockPos findDropStandPos(RoleDimension roleDimension) {
      if (mc.world != null && mc.player != null) {
         BlockPos blockPos = roleDimension.getBlockPos();
         if (!mc.world.getBlockState(blockPos.up()).getCollisionShape(mc.world, blockPos.up()).isEmpty()) {
            return null;
         } else {
            int[] ints = new int[]{1, 0, 2, -1};
            BlockPos add = null;
            double villagerTradeValue = -1.0E9;

            for (int i = 1; i <= 3; i++) {
               for (int j = -i; j <= i; j++) {
                  for (int k = -i; k <= i; k++) {
                     if (Math.max(Math.abs(j), Math.abs(k)) == i) {
                        int minAbs = Math.min(Math.abs(j), Math.abs(k));
                        if (i != 3 || minAbs <= 1) {
                           for (int intsItem : ints) {
                              BlockPos pos = blockPos.add(j, intsItem, k);
                              if (this.isStandable(pos)) {
                                 Vec3d vec3d = new Vec3d(pos.getX() + 0.5, pos.getY() + 1.62, pos.getZ() + 0.5);
                                 double hypot = Math.hypot(j, k);
                                 double tradeLimit = roleDimension.intValue + 1.0 - (vec3d.y - 0.3);
                                 double villagerTradeBound = AmountTradeUtils.getAmountForDouble(hypot, tradeLimit);
                                 if (!(villagerTradeBound < -0.35) && this.hasLineOfSightToStorage(vec3d, roleDimension)) {
                                    double villagerTradeLimit = Math.min(villagerTradeBound, 0.2) * 20.0
                                       - minAbs * 0.5
                                       - Math.abs(intsItem - 1) * 0.5
                                       - 0.05 * Math.sqrt(pos.getSquaredDistance(mc.player.getBlockPos()));
                                    if (villagerTradeLimit > villagerTradeValue) {
                                       villagerTradeValue = villagerTradeLimit;
                                       add = pos;
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }

            return add;
         }
      } else {
         return null;
      }
   }

   private void popGoal() {
      if (!this.goalStack.isEmpty()) {
         this.goalStack.pop();
      }
   }

   private Vec3d getPlayerPos() {
      return mc.player != null ? mc.player.getEntityPos() : Vec3d.ZERO;
   }

   public Role getPickStorageRole() {
      return this.pickStorageRole;
   }

   private boolean shouldRenderPickUi() {
      return this.isEnabled() || this.pick != VillagerTradeBot.Pick.NONE || mc.currentScreen instanceof VillagerTrader;
   }

   private VillagerTradeBot.Owned findCarriedEntry(ItemStack stack) {
      for (VillagerTradeBot.Owned next : this.carriedItems) {
         if (ItemStack.areItemsAndComponentsEqual(next.stackTemplate, stack)) {
            return next;
         }
      }

      return null;
   }

   private boolean hasReachableVillager() {
      for (TrackedVillager villagerExperience : this.villagerEntityHelper.getAllVillagers()) {
         if (this.isVillagerReachable(villagerExperience)) {
            return true;
         }
      }

      return false;
   }

   private UUID pickVillagerForTarget(VillagerTradeRule enchantJsonProfession) {
      List list = TradeEnchantUtils.collectCandidates(this.villagerEntityHelper.getSortedByDistance(), enchantJsonProfession, this::isVillagerReachable);
      Candidate candidate = TradeEnchantUtils.selectCandidate(list, this.getVillagerSelectionMode(), this.getPlayerPos(), this.roundRobinCounter);
      return candidate == null ? null : candidate.villager().uuid;
   }

   private RoleDimension findDepositStorage() {
      if (mc.world == null || mc.player == null) {
         return null;
      } else if (this.isDropSpotActive()) {
         return this.findNearestStorage(Role.DROP);
      } else {
         return this.autoDepositOutputs.isEnabled() ? this.findNearestOutputStorage() : null;
      }
   }

   public void removeTarget(int tradesTradeLimit) {
      if (tradesTradeLimit >= 0 && tradesTradeLimit < this.tradeTargets.size()) {
         VillagerTradeRule tradestradeitem = this.tradeTargets.remove(tradesTradeLimit);
         this.targetPausedUntilTick.remove(tradestradeitem);
         this.pauseReasonByTarget.remove(tradestradeitem);
         this.reassignPriorities();
         ModuleConfigAutosave.updateFlag();
      }
   }

   private void blacklistFailedGoal(VillagerTradeBot.Goal goal) {
      String roleFailingText = goal.failureReason != null && !goal.failureReason.isEmpty() ? goal.failureReason : "it kept failing";
      if (goal.storage != null && (goal.goalKind == VillagerTradeBot.GoalKind.DEPOSIT || goal.goalKind == VillagerTradeBot.GoalKind.WITHDRAW)) {
         goal.storage.overworldMinecraftTime = this.tickCounter + 1200L;
         String roleFailingText2 = goal.storage.role == Role.DROP ? "the drop spot" : "the " + goal.storage.role + " chest";
         this.sendThrottledMessage(
            "site:" + goal.storage.formatRole(),
            "Skipping " + roleFailingText2 + " @ " + goal.storage.getBlockPos().toShortString() + " for a minute \u2014 " + roleFailingText + ".",
            false,
            1200L
         );
      } else if (goal.villagerUuid != null) {
         TrackedVillager villagerExperience = this.villagerEntityHelper.findByUuid(goal.villagerUuid);
         if (villagerExperience != null) {
            villagerExperience.cooldownUntilTick = this.tickCounter + 1200L;
            this.sendThrottledMessage(
               "vil:" + villagerExperience.uuid,
               "Skipping the " + villagerExperience.professionName + " @ " + villagerExperience.blockPos.toShortString() + " for a minute \u2014 " + roleFailingText + ".",
               false,
               1200L
            );
         }
      }

      this.popGoal();
   }

   private long getTicksInState() {
      return this.tickCounter - this.stateEnteredTick;
   }

   public int removeStorages(Role role) {
      int villagertraderitemsSize = this.markedStorages.size();
      String traderVillagerText = this.getDimensionId();
      this.markedStorages.removeIf(roleDimension -> (traderVillagerText.isEmpty() || roleDimension.canDimension(traderVillagerText)) && storageIsRole(roleDimension, role));
      int traderVillager = villagertraderitemsSize - this.markedStorages.size();
      if (traderVillager > 0) {
         if (role == Role.DROP) {
            this.disableDropIfNoSpot();
         }

         ModuleConfigAutosave.updateFlag();
      }

      return traderVillager;
   }

   private boolean isModuleActive(Class<? extends Module> class2) {
      Module iliIiiliiiii = ModuleRegistry.getModule(class2);
      return iliIiiliiiii != null && iliIiiliiiii != this && iliIiiliiiii.isEnabled();
   }

   @Override
   protected void onCameraAngle(DeltaAngle deltaAngle) {
      this.aimController.applyInterpolatedAngles(deltaAngle);
   }

   public List<RoleDimension> getStoragesForRole(Role role) {
      var arrayList = new ArrayList();
      String traderVillagerText = this.getDimensionId();

      for (RoleDimension roleDimension : this.markedStorages) {
         if ((traderVillagerText.isEmpty() || roleDimension.canDimension(traderVillagerText)) && storageIsRole(roleDimension, role)) {
            arrayList.add(roleDimension);
         }
      }

      return arrayList;
   }

   private void abandonGoal(VillagerTradeBot.Goal goal) {
      this.popGoal();
      this.setBotState(VillagerTradeBot.State.PLAN);
   }

   private boolean isUserScreenOpen() {
      if (mc.currentScreen == null || mc.player == null) {
         return false;
      } else if (this.isMerchantScreenOpen()) {
         return this.botState != VillagerTradeBot.State.TRADE
            && this.botState != VillagerTradeBot.State.OPEN_VILLAGER
            && !this.automationMode.getCurrentOption().equals("Assisted")
            && this.tickCounter - this.lastVillagerInteractTick <= 100L;
      } else {
         return !this.isStorageScreenOpen()
            ? false
            : this.botState != VillagerTradeBot.State.STORAGE_OP
               && this.botState != VillagerTradeBot.State.OPEN_STORAGE
               && this.tickCounter - this.lastContainerOpenTick <= 100L;
      }
   }

   public static boolean storageIsRole(RoleDimension roleDimension, Role role) {
      return roleDimension.role == role || roleDimension.role == Role.ANY && role != Role.DROP;
   }

   public boolean beginStoragePick(Role outputsEmeraldsType) {
      if (!this.isEnabled() && mc.player != null && mc.world != null && outputsEmeraldsType != null) {
         this.pick = VillagerTradeBot.Pick.STORAGE;
         this.pickStorageRole = outputsEmeraldsType;
         this.pickStartMillis = System.currentTimeMillis();
         return true;
      } else {
         return false;
      }
   }

   private void recordVillagerOffers(UUID uUID) {
      if (uUID != null) {
         MerchantScreen merchantScreen = this.isMerchantScreenOpen() ? (MerchantScreen)mc.currentScreen : null;
         if (merchantScreen != null) {
            TradeOfferList tradeOfferList = mc.player.currentScreenHandler instanceof MerchantScreenHandler merchantScreenHandler ? merchantScreenHandler.getRecipes() : null;
            if (tradeOfferList != null) {
               var arrayList = new ArrayList();

               for (int i = 0; i < tradeOfferList.size(); i++) {
                  if (tradeOfferList.get(i) != null) {
                     arrayList.add(OfferTrade.getDisplayedUses(tradeOfferList.get(i), i));
                  }
               }

               TrackedVillager ifAbsent = this.villagerEntityHelper.getOrCreate(uUID);
               ifAbsent.setOffersAndCheckChanged(arrayList);
               ifAbsent.lastTradeScreenTick = this.tickCounter;
            }
         }
      }
   }

   public int addTargetIfAbsent(VillagerTradeRule enchantJsonProfession) {
      if (enchantJsonProfession == null) {
         return -1;
      } else {
         int tradesTrade = this.indexOfTarget(enchantJsonProfession);
         if (tradesTrade >= 0) {
            return tradesTrade;
         } else {
            this.tradeTargets.add(enchantJsonProfession);
            this.reassignPriorities();
            ModuleConfigAutosave.updateFlag();
            return this.tradeTargets.size() - 1;
         }
      }
   }

   private int countMatchingStacks(ItemStack stack) {
      if (mc.player == null) {
         return 0;
      } else {
         int equalComponents = 0;

         for (int i = 0; i <= 37; i++) {
            ItemStack cursorStack = this.getStackAtIndex(i);
            if (!cursorStack.isEmpty() && ItemStack.areItemsAndComponentsEqual(cursorStack, stack)) {
               equalComponents += cursorStack.getCount();
            }
         }

         return equalComponents;
      }
   }

   private boolean isDepositBlocked() {
      if (mc.world == null || mc.player == null) {
         return false;
      } else {
         return this.isDropSpotActive()
            ? this.findNearestStorage(Role.DROP) == null
            : this.autoDepositOutputs.isEnabled() && this.hasStorageForRole(Role.OUTPUTS) && this.findNearestOutputStorage() == null;
      }
   }

   private void refreshCarriedSnapshot() {
      if (this.carriedSnapshotActive && !this.pendingCarriedSnapshot && mc.player != null && mc.getNetworkHandler() == this.lastNetworkHandler) {
         this.knownCarriedItems.clear();

         for (VillagerTradeBot.Owned owned : this.collectCarriedItems()) {
            VillagerTradeBot.Owned next = this.findCarriedEntry(owned.stackTemplate);
            this.knownCarriedItems.add(new VillagerTradeBot.Owned(owned.stackTemplate, next == null ? 0 : this.getCurrentOwnedCount(next), owned.totalCount));
         }

         this.carriedSnapshotServerKey = this.getServerKey();
         this.carriedSnapshotMillis = System.currentTimeMillis();
      }
   }

   private boolean isInRangeOfStorage(RoleDimension roleDimension) {
      if (!mc.player.isOnGround()) {
         return false;
      } else {
         Vec3d vec3d = roleDimension.getVec3d();
         double playerGroundValue = Math.abs(mc.player.getX() - vec3d.x);
         double deltaZ = Math.abs(mc.player.getZ() - vec3d.z);
         if (Math.hypot(playerGroundValue, deltaZ) > 3.4) {
            return false;
         } else {
            return Math.abs(mc.player.getY() - (roleDimension.intValue + 1)) > 2.5 ? false : this.hasLineOfSightToStorage(mc.player.getEyePos(), roleDimension);
         }
      }
   }

   private SelectionMode getVillagerSelectionMode() {
      String formatText = this.selectionMode.getCurrentOption();

      return switch (formatText) {
         case "Cheapest" -> SelectionMode.CHEAPEST;
         case "Closest" -> SelectionMode.CLOSEST;
         case "Most Stock" -> SelectionMode.MOST_STOCK;
         case "Round Robin" -> SelectionMode.ROUND_ROBIN;
         case "User Priority" -> SelectionMode.USER_PRIORITY;
         default -> SelectionMode.BEST_SCORE;
      };
   }

   private List<VillagerTradeBot.Owned> collectCarriedItems() {
      var arrayList = new ArrayList();
      if (mc.player == null) {
         return arrayList;
      } else {
         for (int i = 0; i <= 37; i++) {
            ItemStack cursorStack = this.getStackAtIndex(i);
            if (!cursorStack.isEmpty()) {
               VillagerTradeBot.Owned owned = null;

               for (VillagerTradeBot.Owned owned2 : (Iterable<com.corz.client.categories.bots.VillagerTradeBot.Owned>) (arrayList)) {
                  if (ItemStack.areItemsAndComponentsEqual(owned2.stackTemplate, cursorStack)) {
                     owned = owned2;
                     break;
                  }
               }

               if (owned == null) {
                  arrayList.add(new VillagerTradeBot.Owned(cursorStack.copyWithCount(1), cursorStack.getCount(), cursorStack.getCount()));
               } else {
                  owned.trackedCount = owned.trackedCount + cursorStack.getCount();
                  owned.totalCount = owned.totalCount + cursorStack.getCount();
               }
            }
         }

         return arrayList;
      }
   }

   private BlockPos findVillagerStandPos(VillagerEntity villagerEntity) {
      if (mc.world != null && mc.player != null) {
         BlockPos blockPos = villagerEntity.getBlockPos();
         Vec3d eyePos = villagerEntity.getEyePos();
         BlockPos offset = null;
         double offsetValue = -1.0E9;

         for (Direction direction : net.minecraft.util.math.Direction.Type.HORIZONTAL) {
            BlockPos blockOffsetPos = blockPos.offset(direction);
            if ((!this.isFullCubeAt(blockOffsetPos) || !this.isFullCubeAt(blockOffsetPos.up())) && !this.isFullCubeAt(blockOffsetPos.up())) {
               int offsetCounter = 0;

               for (Direction direction2 : net.minecraft.util.math.Direction.Type.HORIZONTAL) {
                  if (direction2 != direction && (this.isFullCubeAt(blockPos.offset(direction2)) || this.isFullCubeAt(blockPos.offset(direction2).up()))) {
                     offsetCounter++;
                  }
               }

               for (int i = 1; i <= 2; i++) {
                  BlockPos pos = blockPos.offset(direction, i);
                  if (this.isStandable(pos) && (i != 2 || !this.isFullCubeAt(blockOffsetPos))) {
                     double offsetLimit = offsetCounter * 10.0
                        + (this.hasLineOfSightFromBlock(pos, eyePos) ? 5.0 : 0.0)
                        + (i == 2 ? 1.0 : 0.0)
                        - 0.15 * Math.sqrt(pos.getSquaredDistance(mc.player.getBlockPos()));
                     if (offsetLimit > offsetValue) {
                        offsetValue = offsetLimit;
                        offset = pos;
                     }
                  }
               }
            }
         }

         return offset;
      } else {
         return null;
      }
   }

   @Override
   protected void onRender2D(TickProgress tickProgress) {
      if (mc.world != null && mc.player != null) {
         MatrixStack matrixStack = new MatrixStack();
         CorzClient.fontManager.setMatrixStack(matrixStack);
         VertexTextureFont vertexTexture = CorzClient.fontManager.getLargeFont();
         ChestStorageUtils.update();
         if (this.statusOverlay.isEnabled()) {
            this.renderStatusOverlay(matrixStack, vertexTexture);
         }

         if (this.villagerEsp.isEnabled()) {
            this.renderVillagerLabels(matrixStack);
         }
      }
   }

   private TrackedVillager findVillagerToInspect(VillagerTradeRule enchantJsonProfession, List<TrackedVillager> list) {
      TrackedVillager outputTrade = null;

      for (TrackedVillager outputTrade2 : list) {
         if (this.isVillagerReachable(outputTrade2)
            && outputTrade2.offers.isEmpty()
            && this.tickCounter - outputTrade2.levelResetTick >= this.guiTimeout.getValue() * 3L
            && TradeEnchantUtils.matchesProfession(outputTrade2, enchantJsonProfession)) {
            if (!outputTrade2.needsLevelUp(enchantJsonProfession)) {
               return outputTrade2;
            }

            if (outputTrade == null) {
               outputTrade = outputTrade2;
            }
         }
      }

      if (outputTrade == null) {
         return null;
      } else {
         for (TrackedVillager villagerExperience : list) {
            if (villagerExperience.hasProfession && TradeEnchantUtils.matchesProfession(villagerExperience, enchantJsonProfession) && !villagerExperience.needsLevelUp(enchantJsonProfession)) {
               return null;
            }
         }

         return outputTrade;
      }
   }

   private void interactWithEntity(Entity entity, EntityHitResult entityHitResult) {
      ActionResult interactEntityAtLocation = mc.interactionManager.interactEntityAtLocation(mc.player, entity, entityHitResult, Hand.MAIN_HAND);
      if (!interactEntityAtLocation.isAccepted()) {
         interactEntityAtLocation = mc.interactionManager.interactEntity(mc.player, entity, Hand.MAIN_HAND);
      }

      if (interactEntityAtLocation instanceof Success success && success.swingSource() == SwingSource.CLIENT) {
         mc.player.swingHand(Hand.MAIN_HAND);
      }
   }

   private boolean hasLineOfSightFromBlock(BlockPos pos, Vec3d vec) {
      Vec3d vec3d = new Vec3d(pos.getX() + 0.5, pos.getY() + 1.62, pos.getZ() + 0.5);
      BlockHitResult raycast = mc.world.raycast(new RaycastContext(vec3d, vec, ShapeType.COLLIDER, FluidHandling.NONE, mc.player));
      return raycast.getType() == Type.MISS || raycast.getPos().squaredDistanceTo(vec3d) >= vec.squaredDistanceTo(vec3d) - 0.3;
   }

   public void clearPause(VillagerTradeRule enchantJsonProfession) {
      if (enchantJsonProfession != null) {
         this.targetPausedUntilTick.remove(enchantJsonProfession);
         this.pauseReasonByTarget.remove(enchantJsonProfession);
      }

      ModuleConfigAutosave.updateFlag2();
   }

   private boolean isStorageScreenOpen() {
      return mc.currentScreen instanceof HandledScreen
         && !(mc.currentScreen instanceof InventoryScreen)
         && !(mc.currentScreen instanceof MerchantScreen)
         && mc.player.currentScreenHandler != null
         && mc.player.currentScreenHandler != mc.player.playerScreenHandler;
   }

   private void renderVillagerLabels(MatrixStack matrixStack) {
      UUID uuid = this.goalStack.peek() != null ? this.goalStack.peek().villagerUuid : null;
      var tradesTrade = this.getRunnableTargets();

      for (TrackedVillager villagerExperience : this.villagerEntityHelper.getAllVillagers()) {
         boolean villagerEntityFlag = uuid != null && uuid.equals(villagerExperience.uuid);
         VillagerTradeRule tradeOpen = this.findMatchingTarget(villagerExperience, tradesTrade);
         if (tradeOpen != null || villagerEntityFlag) {
            VillagerEntity byId = this.villagerEntityHelper.findVillagerEntity(villagerExperience.uuid);
            if (byId != null) {
               double sqrt = Math.sqrt(byId.squaredDistanceTo(mc.player));
               if (!(sqrt > 32.0)) {
                  Vec3d windowFramebuffer = VertexConsumerUtilsVector.getWindowFramebuffer(byId.getEntityPos().add(0.0, byId.getHeight() + 0.55, 0.0));
                  if (windowFramebuffer != null && !(windowFramebuffer.z < 0.0) && !(windowFramebuffer.z > 1.0)) {
                     String villagerEntityText = villagerEntityFlag ? villagerExperience.professionName + " \u00b7 Target" : villagerExperience.professionName;
                     ChestStorageUtils.handleTranslatePush(
                        matrixStack,
                        villagerEntityText,
                        this.describeVillagerTrade(villagerExperience, tradeOpen),
                        this.getVillagerBoxColor(villagerExperience, tradeOpen, villagerEntityFlag),
                        (float)windowFramebuffer.x,
                        (float)windowFramebuffer.y,
                        sqrt > 16.0 ? 0.75F : 1.0F
                     );
                  }
               }
            }
         }
      }
   }

   public String getTradeSummaryText() {
      if (!this.isEnabled() && this.pick == VillagerTradeBot.Pick.NONE) {
         int tradestradeitemsSize = this.tradeTargets.size();
         String tradesText = tradestradeitemsSize == 0 ? "" : this.tradeTargets.get(0).formatTargetName();
         String tradesText2 = tradestradeitemsSize + "|" + tradesText;
         if (!tradesText2.equals(this.tradeSummaryKey)) {
            this.tradeSummaryKey = tradesText2;
            this.tradeSummaryText = tradestradeitemsSize == 0 ? "No trades yet" : (tradestradeitemsSize == 1 ? tradesText : tradesText + " + " + (tradestradeitemsSize - 1) + " more");
         }

         return this.tradeSummaryText;
      } else {
         return this.getCachedStatusText();
      }
   }

   private void pauseWithReason(String retryingPausedText) {
      this.releaseMovementKeys();
      this.pathWalker.clearTarget();
      this.aimController.clearTargetAndOverride();
      this.stackMerchant.reset();
      this.closeScreenIfBotOwned();
      this.endTradeSession();
      this.pausedReasonText = retryingPausedText;
      if (this.botState != VillagerTradeBot.State.PAUSED) {
         this.setBotState(VillagerTradeBot.State.PAUSED);
      }
   }

   private static boolean isVillagerWorkSound(RegistryEntry<SoundEvent> registryEntry) {
      try {
         return registryEntry != null && ((SoundEvent)registryEntry.value()).id().getPath().startsWith("entity.villager.work_");
      } catch (Exception e) {
         return false;
      }
   }

   private void tickTrade() {
      VillagerTradeBot.Goal deque = this.goalStack.peek();
      if (deque == null) {
         this.closeOpenScreens();
         this.endTradeSession();
         this.setBotState(VillagerTradeBot.State.PLAN);
      } else if (!this.isMerchantScreenOpen()) {
         this.stackMerchant.reset();
         this.endTradeSession();
         this.failCurrentGoal("the trade window closed");
      } else {
         Result unconfirmedFull = this.stackMerchant.tickTrading();
         this.recordVillagerOffers(deque.villagerUuid);
         if (unconfirmedFull != Result.RUNNING && unconfirmedFull != Result.CLOSED && unconfirmedFull != Result.FAILED) {
            this.recordRestockResult(deque, unconfirmedFull == Result.STOCK_OUT);
         }

         switch (unconfirmedFull) {
            case RUNNING:
            default:
               break;
            case TRADED:
               deque.failureCount = 0;
               this.messageLastTickByKey.keySet().removeIf(merchantTradeText -> merchantTradeText.startsWith("idle:"));
               if (deque.goalKind == VillagerTradeBot.GoalKind.SELL_FOR_EMERALDS && this.countItem(Items.EMERALD) >= deque.targetEmeraldCount) {
                  this.closeOpenScreens();
                  this.stackMerchant.reset();
                  this.endTradeSession();
                  this.popGoal();
                  this.setBotState(VillagerTradeBot.State.PLAN);
               } else if (this.tickCounter % 20L == 0L) {
               }
               break;
            case DONE_TARGET:
               this.notifyUser(deque.target.formatDescription() + " target reached.", true);
               this.closeOpenScreens();
               this.stackMerchant.reset();
               this.endTradeSession();
               this.popGoal();
               this.setBotState(VillagerTradeBot.State.PLAN);
               break;
            case STOCK_OUT:
               this.onTradeUnavailable(deque);
               break;
            case NEED_EMERALDS:
               this.onNeedEmeralds(deque);
               break;
            case NEED_MATERIALS:
               this.onNeedMaterials(deque);
               break;
            case INV_FULL:
               this.onInventoryFull(deque);
               break;
            case CLOSED:
               this.closeOpenScreens();
               this.stackMerchant.reset();
               this.endTradeSession();
               this.failCurrentGoal("the trade window closed");
               break;
            case FAILED:
               String append = this.stackMerchant.getStatusMessage();
               this.closeOpenScreens();
               this.stackMerchant.reset();
               this.endTradeSession();
               this.failCurrentGoal(append.isEmpty() ? "the trade failed" : append);
         }
      }
   }

   private boolean areAllTargetsComplete() {
      boolean tradesEnabled = false;

      for (VillagerTradeRule enchantJsonProfession : this.tradeTargets) {
         if (enchantJsonProfession.enabled && this.isAllowedByMode(enchantJsonProfession)) {
            if (!enchantJsonProfession.isTargetSold()) {
               return false;
            }

            tradesEnabled = true;
         }
      }

      return tradesEnabled;
   }

   private void setBotState(VillagerTradeBot.State state) {
      if (state != this.botState) {
         this.botState = state;
         this.stateEnteredTick = this.tickCounter;
      }
   }

   private Vec3d getStorageTopCenter(RoleDimension roleDimension) {
      return new Vec3d(roleDimension.asIntRoleDimension + 0.5, roleDimension.intValue + 1.0, roleDimension.asInt + 0.5);
   }

   private void renderStorageHint(MatrixStack matrixStack) {
      if (mc.crosshairTarget instanceof BlockHitResult blockHitResult && blockHitResult.getType() == Type.BLOCK) {
         BlockPos blockPos = blockHitResult.getBlockPos();
         boolean chestOutputsFlag = this.pickStorageRole == Role.DROP;
         boolean chestOutputsFlag2 = chestOutputsFlag || this.isContainerBlock(blockPos);
         Box box = chestOutputsFlag ? new Box(blockPos) : ChestStorageUtils.getUnion(blockPos);
         Vec3d vec3d = chestOutputsFlag ? new Vec3d(blockPos.getX() + 0.5, blockPos.getY() + 1.6, blockPos.getZ() + 0.5) : ChestStorageUtils.getMaxMin(box);
         Vec3d windowFramebuffer = VertexConsumerUtilsVector.getWindowFramebuffer(vec3d);
         if (windowFramebuffer != null && !(windowFramebuffer.z < 0.0) && !(windowFramebuffer.z > 1.0)) {
            String formatChestStorage = ChestStorageUtils.formatChestStorage(this.pickStorageRole);
            String chestOutputsText = chestOutputsFlag2 ? "Right-click to mark" : "Not a chest";
            String corzfontText = chestOutputsFlag2 ? formatChestStorage : "Pick a chest, barrel or shulker box";
            ChestStorageUtils.handleTranslatePush(
               matrixStack, chestOutputsText, corzfontText, chestOutputsFlag2 ? ChestStorageUtils.getStorageChest(this.pickStorageRole) : -38037, (float)windowFramebuffer.x, (float)windowFramebuffer.y, 1.0F
            );
         }
      }
   }

   private void releaseMovementKeys() {
      mc.options.forwardKey.setPressed(false);
      mc.options.backKey.setPressed(false);
      mc.options.leftKey.setPressed(false);
      mc.options.rightKey.setPressed(false);
      mc.options.jumpKey.setPressed(false);
      ElytraAutoUtils.handleRawPressed(mc.options.sneakKey, false);
      ElytraAutoUtils.handleRawPressed(mc.options.sprintKey, false);
   }

   private boolean shouldPauseForGui() {
      if (!(mc.currentScreen instanceof HandledScreen)) {
         return false;
      } else {
         return this.isBotScreenOpen() ? false : !this.automationMode.getCurrentOption().equals("Assisted") || !this.isMerchantScreenOpen();
      }
   }

   @Override
   protected void onDisable() {
      this.refreshCarriedSnapshot();
      this.releaseMovementKeys();
      this.pathWalker.clearTarget();
      this.aimController.clearTargetAndOverride();
      this.stackMerchant.reset();
      this.closeOpenScreens();
      this.endTradeSession();
      this.resetState();
   }

   @Override
   protected void onEnable() {
      if (this.isConflictingModuleActive()) {
         this.notifyUser("Disable Auto Builder / Elytra / Area Miner first \u2014 they share movement.", false);
         this.setEnabled(false);
      } else {
         if (this.pick != VillagerTradeBot.Pick.NONE) {
            this.cancelPick(null);
         }

         this.resetState();
         this.acquireEmeraldsTarget.tradedAmount = 0;

         for (VillagerTradeRule enchantJsonProfession : this.tradeTargets) {
            enchantJsonProfession.tradedAmount = 0;
         }

         this.statusMessageText = "";
         this.hasStatusMessage = false;
         if (this.getRunnableTargets().isEmpty()) {
            this.notifyUser(
               this.tradeTargets.isEmpty()
                  ? "No trades yet \u2014 open the Villager Trader and press Add trade."
                  : "None of your trades can run \u2014 turn one on in the Villager Trader (or check Automation Mode).",
               false
            );
            this.setEnabled(false);
         } else {
            this.notifyUser("Auto Villager Trade started.", true);
            if (this.autoDepositOutputs.isEnabled() || this.materials.isEnabled()) {
               this.notifyUser(this.describeMarkedStorages(), !this.markedStorages.isEmpty());
            }

            this.pendingCarriedSnapshot = true;
            if (this.autoDropOutput.isEnabled() && !this.hasStorageForRole(Role.DROP)) {
               this.notifyUser(
                  "Throw bought items away is on and no drop spot is marked here \u2014 every item bought is thrown on the ground next to the villager.", false
               );
            }
         }
      }
   }

   public boolean isPaused() {
      return this.isEnabled() && this.botState == VillagerTradeBot.State.PAUSED;
   }

   private boolean hasStorageForRole(Role role) {
      if (mc.world == null) {
         return false;
      } else {
         String registryTraderText = mc.world.getRegistryKey().getValue().toString();

         for (RoleDimension roleDimension : this.markedStorages) {
            if (roleDimension.canDimension(registryTraderText) && storageMatchesRole(roleDimension, role)) {
               return true;
            }
         }

         return false;
      }
   }

   private float[] computeAimToStorage(RoleDimension roleDimension) {
      Vec3d vec3d = this.getStorageTopCenter(roleDimension);
      double deltaX = vec3d.x - mc.player.getX();
      double deltaZ = vec3d.z - mc.player.getZ();
      float degrees = (float)Math.toDegrees(Math.atan2(-deltaX, deltaZ));
      double playerEyeValue = vec3d.y - (mc.player.getEyeY() - 0.3);
      return new float[]{degrees, AmountTradeUtils.getAmount(Math.hypot(deltaX, deltaZ), playerEyeValue)};
   }

   @Override
   public JsonElement saveExtraData() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("v", 2);
      JsonArray jsonArray = new JsonArray();

      for (VillagerTradeRule enchantJsonProfession : this.tradeTargets) {
         jsonArray.add(enchantJsonProfession.toJson());
      }

      jsonObject.add("targets", jsonArray);
      JsonArray jsonArray2 = new JsonArray();

      for (RoleDimension roleDimension : this.markedStorages) {
         jsonArray2.add(roleDimension.getPropertyRole());
      }

      jsonObject.add("storages", jsonArray2);
      return jsonObject;
   }

   public void handleKeybindScreen(SwordBot swordBot) {
      if (swordBot.getValue() == 1) {
         if (mc.player != null && mc.world != null) {
            int keybindScreenValue = swordBot.getKey();
            if (this.pick != VillagerTradeBot.Pick.NONE && mc.currentScreen == null && keybindScreenValue == 256) {
               swordBot.updateFlag();
               this.cancelPick(null);
               this.openTraderScreenPending = true;
               this.pickConfirmedMillis = System.currentTimeMillis();
            } else if (mc.currentScreen != null) {
               if (mc.currentScreen instanceof MerchantScreen && this.addLookedAtTradeKey.isBoundTo(keybindScreenValue)) {
                  this.addLookedAtTrade();
               }
            } else {
               if (this.markStorageKey.isBoundTo(keybindScreenValue)) {
                  this.markLookedAtStorage();
               } else if (this.addLookedAtTradeKey.isBoundTo(keybindScreenValue)) {
                  this.addLookedAtTrade();
               }
            }
         }
      }
   }

   private long getChestKey(BlockPos pos) {
      BlockPos offset = ChestStorageUtils.getOffsetForBlockPos(pos);
      return offset == null ? pos.asLong() : Math.min(pos.asLong(), offset.asLong());
   }

   private EntityHitResult getVillagerHitResult(VillagerEntity villagerEntity) {
      if (mc.crosshairTarget instanceof EntityHitResult crosshairTarget && crosshairTarget.getEntity() == villagerEntity) {
         return crosshairTarget;
      } else {
         Vec3d eyePos = mc.player.getEyePos();
         Vec3d cosSin = MathClampUtils.getCosSin(mc.player.getYaw(), mc.player.getPitch());
         Vec3d add = eyePos.add(cosSin.multiply(mc.player.getEntityInteractionRange()));
         Box expand = villagerEntity.getBoundingBox().expand(villagerEntity.getTargetingMargin());
         return expand.raycast(eyePos, add).map(vec -> new EntityHitResult(villagerEntity, vec)).orElse(null);
      }
   }

   private BlockPos getApproachPos(BlockPos blockPos) {
      double blockPosValue = Math.min(this.interactionRange.getValue(), 2.6);

      try {
         BlockPos losCenter = WalkRouteFinder.findStandPositionWithOffsets(blockPos, blockPosValue, null, null, null, WalkRouteFinder.STAND_Y_OFFSETS_NARROW);
         if (losCenter != null) {
            return losCenter;
         }
      } catch (Exception e) {
      }

      return blockPos;
   }

   private BlockPos getOtherChestHalf(BlockPos pos2) {
      if (mc.world == null) {
         return null;
      } else {
         BlockState blockState = mc.world.getBlockState(pos2);
         if (blockState.getBlock() instanceof ChestBlock && blockState.contains(ChestBlock.CHEST_TYPE)) {
            return blockState.get(ChestBlock.CHEST_TYPE) == ChestType.SINGLE ? null : pos2.offset(ChestBlock.getFacing(blockState));
         } else {
            return null;
         }
      }
   }

   private void tickOpenVillager() {
      VillagerTradeBot.Goal deque = this.goalStack.peek();
      if (deque == null) {
         this.setBotState(VillagerTradeBot.State.PLAN);
      } else if (this.isMerchantScreenOpen()) {
         this.beginTradeSession();
         this.recordVillagerOffers(deque.villagerUuid);
         this.stackMerchant.start(deque.target, this.tradeDelay.getValue());
         this.setBotState(VillagerTradeBot.State.TRADE);
      } else {
         VillagerEntity byId = this.villagerEntityHelper.findVillagerEntity(deque.villagerUuid);
         if (byId == null) {
            this.abandonGoal(deque);
         } else {
            Vec3d eyePos = byId.getEyePos();
            double distanceTo = eyePos.distanceTo(mc.player.getEyePos());
            if (distanceTo > 4.0) {
               if (this.navigation.isEnabled()) {
                  this.setBotState(VillagerTradeBot.State.TO_VILLAGER);
               } else {
                  this.failCurrentGoal("it's out of reach and Navigation is off");
               }
            } else {
               this.aimController.setTargetFromPoint(eyePos, 1);
               if (this.tickCounter - this.lastInteractTick >= this.interactionDelay.getValue()) {
                  EntityHitResult crosshairTarget = this.getVillagerHitResult(byId);
                  if (crosshairTarget != null) {
                     this.interactWithEntity(byId, crosshairTarget);
                     this.lastInteractTick = this.tickCounter;
                     this.lastVillagerInteractTick = this.tickCounter;
                  }
               }

               if (this.getTicksInState() > this.guiTimeout.getValue()) {
                  this.failCurrentGoal("its trade window didn't open (asleep, busy with another player, or blocked)");
               }
            }
         }
      }
   }

   public String getStatusMessage() {
      return this.hasStatusMessage && System.currentTimeMillis() - this.statusMessageMillis < 90000L ? this.statusMessageText : "";
   }

   private int countEmptySlots() {
      if (mc.player == null) {
         return 0;
      } else {
         PlayerInventory inventory = mc.player.getInventory();
         int inventoryPlayer = 0;

         for (int i = 0; i <= 35; i++) {
            if (inventory.getStack(i).isEmpty()) {
               inventoryPlayer++;
            }
         }

         return inventoryPlayer;
      }
   }

   private void onInventoryFull(VillagerTradeBot.Goal goal) {
      this.closeOpenScreens();
      this.stackMerchant.reset();
      this.endTradeSession();
      if (!this.hasDepositableStacks()) {
         this.pauseTarget(
            goal.target,
            this.hasProtectedOutputStacks()
               ? "inventory full \u2014 the items you carried at Start stay with you; store some yourself or free a few slots"
               : "inventory full of items that aren't trade outputs \u2014 free a few slots",
            1200L
         );
         this.popGoal();
         this.setBotState(VillagerTradeBot.State.PLAN);
      } else {
         VillagerTradeBot.Goal roleEmeralds = this.createDepositGoal("deposit outputs");
         if (roleEmeralds != null) {
            this.goalStack.push(roleEmeralds);
            this.setBotState(VillagerTradeBot.State.PLAN);
         } else if (this.isDepositBlocked()) {
            this.popGoal();
            this.waitForOutputRoom();
         } else {
            this.pauseTarget(
               goal.target,
               !this.autoDepositOutputs.isEnabled() && !this.isDropSpotActive()
                  ? "inventory full \u2014 Auto Deposit Outputs is off"
                  : "inventory full \u2014 no output chest marked (Villager Trader \u2192 Output chest \u2192 Mark)",
               1200L
            );
            this.popGoal();
            this.setBotState(VillagerTradeBot.State.PLAN);
         }
      }
   }

   private boolean isAutoXpConflict() {
      if (!this.isModuleActive(AutoXP.class)) {
         return false;
      } else {
         for (VillagerTradeRule enchantJsonProfession : this.tradeTargets) {
            if (enchantJsonProfession.isEnabled() && this.isAllowedByMode(enchantJsonProfession) && enchantJsonProfession.isExperienceBottleRule()) {
               return true;
            }
         }

         return false;
      }
   }

   private void resetState() {
      this.goalStack.clear();
      this.carriedItems.clear();
      this.pendingCarriedSnapshot = false;
      this.carriedSnapshotActive = false;
      this.targetPausedUntilTick.clear();
      this.pauseReasonByTarget.clear();
      this.materialUnavailableUntilTick.clear();
      this.messageLastTickByKey.clear();
      this.workSoundQueue.clear();
      this.villagerEntityHelper.clear();
      this.stackMerchant.reset();
      this.botState = VillagerTradeBot.State.IDLE;
      this.stateEnteredTick = this.tickCounter;
      this.idleUntilTick = -4611686018427387904L;
      this.walkedThisTick = false;
      this.strayDropStorage = null;
      this.strayPickupAttempts = 0;
      this.standStillTicks = 0;
      this.dropInventoryScreen = null;
   }

   private void tickWaitRestock() {
      if (this.tickCounter >= this.idleUntilTick) {
         if (this.goalStack.isEmpty() && this.hasDepositableStacks()) {
            VillagerTradeBot.Goal roleEmeralds = this.createDepositGoal("deposit outputs (idle)");
            if (roleEmeralds != null) {
               this.goalStack.push(roleEmeralds);
               this.setBotState(VillagerTradeBot.State.PLAN);
               return;
            }

            if (this.countEmptySlots() == 0 && this.isDepositBlocked()) {
               this.waitForOutputRoom();
               return;
            }
         }

         this.setBotState(VillagerTradeBot.State.SCAN);
      }
   }

   public List<VillagerTradeRule> getTargets() {
      return Collections.unmodifiableList(this.tradeTargets);
   }

   private static Color toColor(int colorValue) {
      int alpha = colorValue >>> 24 == 0 ? 255 : colorValue >>> 24;
      return new Color(colorValue >> 16 & 0xFF, colorValue >> 8 & 0xFF, colorValue & 0xFF, alpha);
   }

   private VillagerTradeRule findTargetInOpenScreen() {
      if (!this.isMerchantScreenOpen()) {
         return null;
      } else {
         TradeOfferList recipes = ((MerchantScreenHandler)mc.player.currentScreenHandler).getRecipes();
         var arrayList = new ArrayList();

         for (int i = 0; i < recipes.size(); i++) {
            if (recipes.get(i) != null) {
               arrayList.add(OfferTrade.getDisplayedUses(recipes.get(i), i));
            }
         }

         for (VillagerTradeRule next : this.getRunnableTargets()) {
            for (OfferTrade offerTrade : (Iterable<com.corz.client.inventory.OfferTrade>) (arrayList)) {
               if (TradeEnchantUtils.isAvailableMatch(next, offerTrade)) {
                  return next;
               }
            }
         }

         return null;
      }
   }

   private void startGoal(VillagerTradeBot.Goal goal) {
      switch (goal.goalKind) {
         case BUY:
         case SELL:
         case SELL_FOR_EMERALDS:
            if (goal.villagerUuid == null) {
               goal.villagerUuid = this.pickVillagerForTarget(goal.target);
            }

            if (goal.villagerUuid == null) {
               this.abandonGoal(goal);
               return;
            }

            if (goal.goalKind == VillagerTradeBot.GoalKind.BUY && this.queueShortageGoal(goal)) {
               return;
            }

            this.setBotState(this.navigation.isEnabled() ? VillagerTradeBot.State.TO_VILLAGER : VillagerTradeBot.State.OPEN_VILLAGER);
            break;
         case DEPOSIT:
            RoleDimension roleDimension = this.findDepositStorage();
            if (roleDimension == null) {
               this.goalStack.pop();
               this.setBotState(VillagerTradeBot.State.PLAN);
               return;
            }

            goal.storage = roleDimension;
            goal.storageRole = roleDimension.role;
            goal.standPos = roleDimension.role == Role.DROP && this.navigation.isEnabled() ? this.findDropStandPos(roleDimension) : null;
            this.setBotState(this.navigation.isEnabled() ? VillagerTradeBot.State.TO_STORAGE : VillagerTradeBot.State.OPEN_STORAGE);
            break;
         case WITHDRAW:
            RoleDimension nearestStorage = this.findNearestStorage(goal.storageRole);
            if (nearestStorage == null) {
               this.goalStack.pop();
               this.setBotState(VillagerTradeBot.State.PLAN);
               return;
            }

            goal.storage = nearestStorage;
            this.setBotState(this.navigation.isEnabled() ? VillagerTradeBot.State.TO_STORAGE : VillagerTradeBot.State.OPEN_STORAGE);
            break;
         case PICKUP:
            this.setBotState(VillagerTradeBot.State.PICKUP);
      }
   }

   private void onNeedEmeralds(VillagerTradeBot.Goal goal) {
      this.closeOpenScreens();
      this.stackMerchant.reset();
      this.endTradeSession();
      this.handleMissingEmeralds(goal, this.stackMerchant.getEmeraldsNeeded());
   }

   private void addLookedAtTrade() {
      VillagerEntity entity = this.getTargetedVillager();
      if (entity == null) {
         this.notifyUser("Look at a villager to add its trade.", false);
      } else {
         TrackedVillager ifAbsent = this.villagerEntityHelper.getOrCreate(entity.getUuid());
         ifAbsent.updateFromEntity(entity, this.tickCounter);
         if (this.isMerchantScreenOpen()) {
            this.recordVillagerOffers(entity.getUuid());
         }

         if (ifAbsent.offers.isEmpty()) {
            this.notifyUser("Right-click the villager to open its trades, then press Add Looked-At Trade while the trade window is open.", false);
         } else {
            VillagerTradeRule tradeProfession = this.createTargetFromVillager(ifAbsent);
            if (tradeProfession == null) {
               this.notifyUser(
                  "This " + ifAbsent.professionName + " has no trade the bot can add (an enchanted book, XP bottles, or selling a known item for emeralds).",
                  false
               );
            } else if (this.indexOfTarget(tradeProfession) >= 0) {
               this.notifyUser("Already in your list: " + tradeProfession.formatTargetName(), false);
            } else {
               this.addTargetIfAbsent(tradeProfession);
               this.notifyUser("Added trade: " + tradeProfession.formatTargetName(), true);
            }
         }
      }
   }

   private void onTradeUnavailable(VillagerTradeBot.Goal goal) {
      String append = this.stackMerchant.getStatusMessage();
      TrackedVillager villagerExperience = this.villagerEntityHelper.findByUuid(goal.villagerUuid);
      OfferTrade offerTrade = villagerExperience != null ? villagerExperience.findTooExpensiveOffer(goal.target) : null;
      if (offerTrade != null) {
         this.sendThrottledMessage(
            "price:" + villagerExperience.uuid,
            goal.target.formatDescription()
               + " costs "
               + offerTrade.count3EnchantJson
               + " emeralds at the "
               + villagerExperience.professionName
               + " @ "
               + villagerExperience.blockPos.toShortString()
               + " \u2014 above your Max Emerald Cost ("
               + goal.target.maxEmeraldCost
               + "). It's re-checked after its next restock.",
            false,
            6000L
         );
      }

      this.closeOpenScreens();
      this.stackMerchant.reset();
      this.endTradeSession();
      UUID uUID = this.pickVillagerForTarget(goal.target);
      if (uUID != null && !uUID.equals(goal.villagerUuid)) {
         goal.villagerUuid = uUID;
         goal.failureCount = 0;
         this.setBotState(VillagerTradeBot.State.PLAN);
      } else {
         this.popGoal();
         this.setBotState(VillagerTradeBot.State.PLAN);
      }
   }

   private void recordRestockResult(VillagerTradeBot.Goal goal, boolean restockFlag) {
      TrackedVillager villagerExperience = this.villagerEntityHelper.findByUuid(goal.villagerUuid);
      if (villagerExperience != null) {
         villagerExperience.updateFailStreak(goal.isRestockCheck, restockFlag);
      }

      goal.isRestockCheck = false;
   }

   private VillagerTradeRule buildTargetFromSettings() {
      VillagerTradeRule enchantJsonProfession = new VillagerTradeRule();
      enchantJsonProfession.enabled = true;
      enchantJsonProfession.action = this.targetActionSetting.getCurrentOption().equals("Sell") ? Action.SELL : Action.BUY;
      String formatText = this.outputKindSetting.getCurrentOption();

      enchantJsonProfession.kind = switch (formatText) {
         case "Item" -> OutputKind.ITEM;
         case "Emerald" -> OutputKind.EMERALD;
         default -> OutputKind.ENCHANTED_BOOK;
      };
      if (enchantJsonProfession.action == Action.SELL) {
         enchantJsonProfession.kind = OutputKind.EMERALD;
      }

      enchantJsonProfession.profession = this.targetProfessionSetting.getCurrentOption();
      enchantJsonProfession.enchant = this.enchantmentSetting.getCurrentOption();
      enchantJsonProfession.enchantLevel = this.enchantLevelSetting.getValue();
      enchantJsonProfession.item = this.targetItemSetting.getCurrentOption();
      enchantJsonProfession.maxEmeraldCost = this.maxEmeraldCostSetting.getValue();
      enchantJsonProfession.priority = this.targetPrioritySetting.getValue();
      enchantJsonProfession.targetAmount = this.targetAmountSetting.getValue();
      return enchantJsonProfession;
   }

   private ItemEntity findStrayItem(RoleDimension roleDimension) {
      Vec3d vec3d = roleDimension.getVec3d();

      for (ItemEntity next : mc.world
         .getEntitiesByClass(
            ItemEntity.class, new Box(vec3d, vec3d).expand(5.0, 3.0, 5.0), itemEntity -> itemEntity.isAlive() && itemEntity.isOnGround() && this.isTradeOutputStack(itemEntity.getStack())
         )) {
         boolean villagerTradeFlag = Math.abs(next.getX() - vec3d.x) < 0.7
            && Math.abs(next.getZ() - vec3d.z) < 0.7
            && next.getY() >= roleDimension.intValue - 0.5
            && next.getY() <= roleDimension.intValue + 2.0;
         if (!villagerTradeFlag) {
            return next;
         }
      }

      return null;
   }

   private boolean isPlayerStandingStill() {
      return mc.player.isOnGround()
         && !mc.player.isSneaking()
         && mc.player.getPose() == EntityPose.STANDING
         && mc.player.getVelocity().horizontalLengthSquared() < 1.0E-4;
   }

   private void pauseTarget(VillagerTradeRule enchantJsonProfession, String emeraldsOutputText, long emeraldsOutputBound) {
      if (enchantJsonProfession != null) {
         this.targetPausedUntilTick.put(enchantJsonProfession, this.tickCounter + Math.max(emeraldsOutputBound, (long)this.retryDelay.getValue()));
         this.pauseReasonByTarget.put(enchantJsonProfession, emeraldsOutputText);
         this.sendThrottledMessage(
            "pause:" + System.identityHashCode(enchantJsonProfession) + ":" + emeraldsOutputText, "Pausing " + enchantJsonProfession.formatDescription() + " \u2014 " + emeraldsOutputText + ".", false, 6000L
         );
      }
   }

   private boolean hasDepositableStacks() {
      if (mc.player == null) {
         return false;
      } else {
         PlayerInventory inventory = mc.player.getInventory();

         for (int i = 0; i <= 35; i++) {
            if (this.isDepositableStack(inventory.getStack(i))) {
               return true;
            }
         }

         return false;
      }
   }

   private static final class Goal {
      Role storageRole;
      String failureReason = "";
      VillagerTradeRule target;
      boolean isRestockCheck;
      int targetEmeraldCount;
      BlockPos standPos;
      VillagerTradeBot.GoalKind goalKind;
      int itemEntityId;
      int withdrawAmount;
      UUID villagerUuid;
      RoleDimension storage;
      int failureCount;
      String description = "";
      Item withdrawItem;
   }

   private static enum GoalKind {
      BUY,
      SELL,
      SELL_FOR_EMERALDS,
      DEPOSIT,
      WITHDRAW,
      PICKUP;
   }

   private static final class Owned {
      final ItemStack stackTemplate;
      int trackedCount;
      int totalCount;

      Owned(ItemStack withCount, int count, int count2) {
         this.stackTemplate = withCount;
         this.trackedCount = count;
         this.totalCount = count2;
      }
   }

   public static enum Pick {
      NONE,
      TRADE,
      STORAGE;
   }

   private static enum State {
      IDLE,
      SCAN,
      PLAN,
      TO_VILLAGER,
      OPEN_VILLAGER,
      TRADE,
      TO_STORAGE,
      OPEN_STORAGE,
      STORAGE_OP,
      DROP_OP,
      PICKUP,
      WAIT_RESTOCK,
      RECOVER,
      PAUSED;
   }

   public record TradeFacts(int bestCost, String bestWhere, int inStock, int restocking, int unchecked, int belowLevel) {
   }

   private record WorkSound(double x, double y, double z, int entityId) {
   }
}
