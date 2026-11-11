package com.corz.client.categories.bots;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.ModuleConfigAutosave;
import com.corz.client.config.DoubleSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.config.StringBase;
import com.corz.client.utils.BuildGuideOverlay.Frame;
import com.corz.client.utils.AreaMinerController.State;
import com.corz.client.gui.ButtonBase;
import com.corz.client.gui.DisabledEnabled;
import com.corz.client.render.QueueCommand;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import com.corz.client.utils.BuildGuideOverlay;
import com.corz.client.utils.AreaMinerController;
import com.corz.client.utils.MinerPathNavigator;
import com.corz.client.utils.Drill;
import com.corz.client.utils.DelegatingAutoEspBase;
import com.corz.client.utils.AreaLayerCursor;
import com.corz.client.utils.BlockRegionSelection;
import ai.auth.Module;
import ai.auth.ModuleRegistry;
import com.corz.client.utils.ElytraAutoUtils;
import com.corz.client.utils.IconsPngType;

public class AutoAreaMiner extends Module {
   private final BooleanSetting xpRepair;
   private final ButtonBase corner1Looking;
   private final BooleanSetting pauseInGui;
   private BlockPos previewPathEnd;
   private final ButtonBase corner2Feet;
   private final IntegerSetting layerHeight;
   private boolean blockSetFailed;
   private int areaMaxY;
   private final ButtonBase corner1Feet;
   private final IntegerSetting passWidth;
   private final BooleanSetting notifyWhenDone;
   private final BooleanSetting autoEat;
   private int areaMaxZ;
   private final DoubleSetting reach;
   private final StringBase corner2Text;
   private final BuildGuideOverlay overlayFrameTracker;
   private static boolean previewPathEnabled;
   private int areaMinZ;
   private boolean wasMining;
   private boolean blockSetFinished;
   private boolean blockSetMode;
   private final StringBase corner1Text = new StringBase("Corner 1", "First corner as \"x y z\" (or use the buttons below)", "");
   private List<BlockPos> previewPathBlocks;
   private int areaMinY;
   private final BlockRegionSelection miningRegion;
   private final List<BlockPos> blockSetPositions;
   private long nearCornerKey;
   private AreaMinerController engine;
   private AreaLayerCursor nearCorner;
   private final BooleanSetting sweepLeftovers;
   private int focusLayerY;
   private final BooleanSetting sprint;
   private final Drill drill;
   private final BooleanSetting showArea;
   private Object startWorld;
   private final IntegerSetting repairAt;
   private final IntegerSetting gappleAfterLosing;
   private final BooleanSetting showPath;
   private Object startPlayer;
   private final BooleanSetting placeBlocks;
   private final ButtonBase corner2Looking;
   private final StringBase drillName;
   private boolean areaDefined;
   private int areaMaxX;
   private String blockSetTag;
   private int areaMinX;

   private void fillOverlayFrame(Frame frame) {
      frame.clear();
      frame.guidesEnabled = this.showArea.isEnabled();
      frame.trailEnabled = this.showPath.isEnabled();
      if (this.areaDefined) {
         frame.setArea(this.areaMinX, this.areaMinY, this.areaMinZ, this.areaMaxX, this.areaMaxY, this.areaMaxZ);
      }

      if (this.engine != null && this.engine.isActive()) {
         AreaLayerCursor nearCorner = this.engine.getLayerCursor();
         if (nearCorner != null && !nearCorner.isCheckingLeftovers() && this.miningRegion.hasSelection()) {
            this.fillNearCornerFrame(frame, nearCorner);
         } else {
            BlockPos immutable = this.engine.getCurrentBreakTarget();
            if (immutable != null) {
               frame.setCutBox(immutable.getX(), immutable.getY(), immutable.getZ(), immutable.getX(), immutable.getY(), immutable.getZ());
            }
         }

         frame.cellBlock = this.engine.getAimTarget();
         MinerPathNavigator blockPosHelper = this.engine.getNavigator();
         if (blockPosHelper != null && blockPosHelper.getGoalPos() != null && System.nanoTime() - blockPosHelper.getLastMoveNanos() < 160000000L) {
            frame.targetBlock = blockPosHelper.getGoalPos();
            frame.targetActive = true;
            var items = blockPosHelper.getCurrentPath();
            if (items != null && frame.targetBlock.equals(blockPosHelper.getPathGoal())) {
               frame.pathPositions = items;
            }
         }
      }

      if (previewPathEnabled) {
         this.fillPreviewPathFrame(frame);
      } else {
         this.previewPathBlocks = null;
      }
   }

   public void startBlockSetMining(Collection<BlockPos> collection, String text) {
      this.abortEngineRun();
      this.getOrCreateEngine().resetIfInactive();
      this.blockSetTag = text;
      this.blockSetMode = true;
      this.blockSetPositions.clear();
      if (collection != null) {
         for (BlockPos blockPos : collection) {
            this.blockSetPositions.add(blockPos.toImmutable());
         }
      }

      this.blockSetFinished = this.blockSetPositions.isEmpty();
      this.blockSetFailed = false;
      this.updateAreaFromBlockSet();
   }

   private void abortEngineRun() {
      if (this.engine != null && this.engine.isActive()) {
         this.engine.stopMining();
      }

      this.wasMining = false;
   }

   private void runMinerTick() {
      this.syncDrillSettings();
      AreaMinerController posBlock = this.getOrCreateEngine();
      if (!posBlock.isActive() || mc.world == this.startWorld && mc.player == this.startPlayer && mc.player.isAlive()) {
         if (!posBlock.isActive()) {
            if (this.wasMining) {
               this.wasMining = false;
               boolean minerAreaFlag = posBlock.getState() == State.DONE;
               if (this.blockSetMode) {
                  this.markBlockSetFinished(!minerAreaFlag);
               } else if (minerAreaFlag && this.notifyWhenDone.isEnabled()) {
                  DisabledEnabled.getInstance().handleDisabledEnabled("Auto Area Miner", "Area cleared", true);
               }

               this.setEnabled(false);
            } else if (this.areaDefined) {
               this.releaseMovementKeys();
               this.miningRegion.setCorners(new BlockPos(this.areaMinX, this.areaMinY, this.areaMinZ), new BlockPos(this.areaMaxX, this.areaMaxY, this.areaMaxZ));
               if (this.blockSetMode) {
                  this.miningRegion.setAllowedPositions(this.blockSetPositions);
               } else {
                  this.miningRegion.clearAllowedPositions();
               }

               String formatPosBlock = posBlock.startMining();
               if (formatPosBlock != null) {
                  this.sendMinerMessage("\u00a7b[Miner]\u00a7r \u00a7c" + formatPosBlock);
                  this.markBlockSetFinished(true);
                  this.setEnabled(false);
               } else {
                  this.wasMining = true;
                  this.startWorld = mc.world;
                  this.startPlayer = mc.player;
               }
            }
         } else if (this.pauseInGui.isEnabled() && mc.currentScreen != null) {
            posBlock.neutralizeInput();
         } else {
            posBlock.onTick();
         }
      } else {
         posBlock.stopMining();
         this.wasMining = false;
         this.sendMinerMessage("\u00a7b[Miner]\u00a7r stopped \u2014 the world or player changed (disconnect, death or dimension change).");
         this.markBlockSetFinished(true);
         this.setEnabled(false);
      }
   }

   public void clearBlockSet(String minerAreaText) {
      if (this.blockSetMode && (this.blockSetTag == null || this.blockSetTag.equals(minerAreaText))) {
         this.abortEngineRun();
         this.miningRegion.clearAllowedPositions();
         this.getOrCreateEngine().resetIfInactive();
         this.blockSetMode = false;
         this.blockSetFinished = false;
         this.blockSetFailed = false;
         this.blockSetTag = null;
         this.blockSetPositions.clear();
         this.updateAreaFromCorners();
      }
   }

   private static int[] parseCornerCoordinates(String autoAreaText) {
      if (autoAreaText == null) {
         return null;
      } else {
         String[] split = autoAreaText.trim().replace(",", " ").split("\\s+");
         if (split.length != 3) {
            return null;
         } else {
            try {
               return new int[]{Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2])};
            } catch (NumberFormatException e) {
               return null;
            }
         }
      }
   }

   private void updateAreaFromCorners() {
      int[] minerAreaValues = parseCornerCoordinates(this.corner1Text.getCurrentText());
      int[] parseCornerCoordinates2 = parseCornerCoordinates(this.corner2Text.getCurrentText());
      this.areaDefined = minerAreaValues != null && parseCornerCoordinates2 != null;
      if (this.areaDefined) {
         this.areaMinX = Math.min(minerAreaValues[0], parseCornerCoordinates2[0]);
         this.areaMaxX = Math.max(minerAreaValues[0], parseCornerCoordinates2[0]);
         this.areaMinY = Math.min(minerAreaValues[1], parseCornerCoordinates2[1]);
         this.areaMaxY = Math.max(minerAreaValues[1], parseCornerCoordinates2[1]);
         this.areaMinZ = Math.min(minerAreaValues[2], parseCornerCoordinates2[2]);
         this.areaMaxZ = Math.max(minerAreaValues[2], parseCornerCoordinates2[2]);
      }
   }

   private void fillNearCornerFrame(Frame frame, AreaLayerCursor nearCorner) {
      int face = nearCorner.getFaceCoord();
      int miningCornerValue = nearCorner.getTargetRowCoord();
      long miningTime = ((nearCorner.getLayerIndex() * 4099L + nearCorner.getRowIndex()) * 1000003L + face) * 1000033L + miningCornerValue;
      if (nearCorner != this.nearCorner || miningTime != this.nearCornerKey) {
         this.nearCorner = nearCorner;
         this.nearCornerKey = miningTime;
         this.focusLayerY = nearCorner.getSliceCenterPos().getY();
      }

      int frameValue = Math.max(miningCornerValue - 1, nearCorner.getRowAxisMin());
      int miningCornerLimit = Math.min(miningCornerValue + 1, nearCorner.getRowAxisMax());
      int max3 = Math.max(this.focusLayerY - 1, this.miningRegion.getMinY());
      int min2 = Math.min(this.focusLayerY + 1, this.miningRegion.getMaxY());
      if (frameValue <= miningCornerLimit && max3 <= min2) {
         boolean miningCornerFlag = nearCorner.faceAxis == Axis.X;
         if (miningCornerFlag) {
            frame.setCutBox(face, max3, frameValue, face, min2, miningCornerLimit);
         } else {
            frame.setCutBox(frameValue, max3, face, miningCornerLimit, min2, face);
         }

         frame.setExtrude(miningCornerFlag ? 0 : 2, nearCorner.getFaceDirection(), 4, nearCorner.getFaceAxisStart(), nearCorner.getFaceAxisEnd());
      }
   }

   public AutoAreaMiner() {
      super("Auto Area Miner", IconsPngType.DONUTSMP);
      this.corner2Text = new StringBase("Corner 2", "Second corner as \"x y z\" (or use the buttons below)", "");
      this.corner1Feet = new ButtonBase("Corner 1 \u2192 Feet", "Set corner 1 to the block you're standing on", "Set", () -> this.setCornerFromPlayer(1, false));
      this.corner1Looking = new ButtonBase("Corner 1 \u2192 Looking", "Set corner 1 to the block you're looking at", "Set", () -> this.setCornerFromPlayer(1, true));
      this.corner2Feet = new ButtonBase("Corner 2 \u2192 Feet", "Set corner 2 to the block you're standing on", "Set", () -> this.setCornerFromPlayer(2, false));
      this.corner2Looking = new ButtonBase("Corner 2 \u2192 Looking", "Set corner 2 to the block you're looking at", "Set", () -> this.setCornerFromPlayer(2, true));
      this.drillName = new StringBase(
         "Drill Name", "Part of your 3x3 drill's name (not case-sensitive). The drill cuts the 3x3 faces, a plain pickaxe cleans up the leftovers", "Drill"
      );
      this.layerHeight = new IntegerSetting("Layer Height", "How tall each layer is. The area is cleared from the top down, one layer at a time", 3, 1, 6);
      this.passWidth = new IntegerSetting("Pass Width", "How wide each row of a layer is", 3, 1, 5);
      this.reach = new DoubleSetting("Reach", "How far the miner plans to reach. It never uses less than your real reach", 4.5, 3.0, 6.0, 0.5);
      this.sweepLeftovers = new BooleanSetting("Sweep Leftovers", "After each layer, mine every block that is still standing before moving down", true);
      this.sprint = new BooleanSetting("Sprint", "Sprint while walking", true);
      this.placeBlocks = new BooleanSetting("Place Blocks", "Pillar up, bridge gaps and plug lava or water with solid blocks from your hotbar", true);
      this.autoEat = new BooleanSetting("Auto Eat", "Eat food at 10 hunger or less, and a golden apple after losing the amount below", true);
      this.gappleAfterLosing = new IntegerSetting("Gapple After Losing", "Hunger bars lost before a golden apple from your hotbar is eaten", 2, 1, 10);
      this.xpRepair = new BooleanSetting("XP Repair", "When your tool runs low, throw XP bottles from your inventory to mend it", true);
      this.repairAt = new IntegerSetting("Repair At", "Durability left on the tool that starts XP repair", 50, 1, 1000);
      this.showArea = new BooleanSetting("Show Area", "Draw the selected area and the cut being mined", true);
      this.showPath = new BooleanSetting("Show Path", "Draw the route the miner is walking and where it is heading", true);
      this.pauseInGui = new BooleanSetting("Pause In GUI", "Stop while a screen is open", true);
      this.notifyWhenDone = new BooleanSetting("Notify When Done", "Toast and stop when the area is cleared", true);
      this.drill = new Drill();
      this.miningRegion = new BlockRegionSelection();
      this.blockSetPositions = new ArrayList<>();
      this.overlayFrameTracker = new BuildGuideOverlay();
      this.nearCornerKey = Long.MIN_VALUE;
      this.drillName.withGroupName("Mining");
      this.layerHeight.withGroupName("Mining");
      this.passWidth.withGroupName("Mining");
      this.reach.withGroupName("Mining");
      this.sweepLeftovers.withGroupName("Mining");
      this.sprint.withGroupName("Movement");
      this.placeBlocks.withGroupName("Movement");
      this.autoEat.withGroupName("Survival");
      this.gappleAfterLosing.withGroupName("Survival");
      this.xpRepair.withGroupName("Survival");
      this.repairAt.withGroupName("Survival");
      this.showArea.withGroupName("General");
      this.showPath.withGroupName("General");
      this.pauseInGui.withGroupName("General");
      this.notifyWhenDone.withGroupName("General");
      this.addSettings(
         new ConfigSetting[]{
            this.corner1Text,
            this.corner2Text,
            this.corner1Feet,
            this.corner1Looking,
            this.corner2Feet,
            this.corner2Looking,
            this.drillName,
            this.layerHeight,
            this.passWidth,
            this.reach,
            this.sweepLeftovers,
            this.sprint,
            this.placeBlocks,
            this.autoEat,
            this.gappleAfterLosing,
            this.xpRepair,
            this.repairAt,
            this.showArea,
            this.showPath,
            this.pauseInGui,
            this.notifyWhenDone,
            new DelegatingAutoEspBase("V2 Layer Height", this.layerHeight),
            new DelegatingAutoEspBase("V2 Pass Width", this.passWidth),
            new DelegatingAutoEspBase("V2 Reach", this.reach),
            new DelegatingAutoEspBase("V2 Sprint", this.sprint),
            new DelegatingAutoEspBase("V2 Place Blocks", this.placeBlocks),
            new DelegatingAutoEspBase("V2 Sweep Leftovers", this.sweepLeftovers),
            new DelegatingAutoEspBase("V2 Auto Eat", this.autoEat),
            new DelegatingAutoEspBase("V2 Gapple After Losing", this.gappleAfterLosing),
            new DelegatingAutoEspBase("V2 XP Repair", this.xpRepair),
            new DelegatingAutoEspBase("V2 Repair At", this.repairAt)
         }
      );
   }

   @Override
   protected void onRender3D(QueueCommand queueCommand) {
      this.fillOverlayFrame(this.overlayFrameTracker.getFrame());
      this.overlayFrameTracker.renderGuidesSafely(true, queueCommand.tickProgress);
   }

   public boolean isBlockSetFailed() {
      return this.blockSetMode && this.blockSetFinished && this.blockSetFailed;
   }

   public boolean isBlockSetMode() {
      return this.blockSetMode;
   }

   private void syncDrillSettings() {
      this.drill.layerHeight = this.layerHeight.getValue();
      this.drill.passWidth = this.passWidth.getValue();
      this.drill.reachDistance = this.reach.getValue();
      this.drill.sprintToTarget = this.sprint.isEnabled();
      this.drill.placeBlocks = this.placeBlocks.isEnabled();
      this.drill.scanLeftovers = this.sweepLeftovers.isEnabled();
      this.drill.autoEatEnabled = this.autoEat.isEnabled();
      this.drill.hungerDropToEat = this.gappleAfterLosing.getValue();
      this.drill.replaceDamagedTool = this.xpRepair.isEnabled();
      this.drill.minToolDurability = this.repairAt.getValue();
      this.drill.drillToolName = this.drillName.getCurrentText();
      this.drill.drillActive = this.blockSetMode;
   }

   @Override
   protected void onEnable() {
      if (this.blockSetMode) {
         this.updateAreaFromBlockSet();
      } else {
         this.updateAreaFromCorners();
      }

      if (!this.areaDefined && !this.blockSetMode) {
         DisabledEnabled.getInstance().handleDisabledEnabled("Auto Area Miner", "Set both corners first", false);
      }

      this.wasMining = false;
   }

   private void releaseMovementKeys() {
      mc.options.forwardKey.setPressed(false);
      ElytraAutoUtils.handleRawPressed(mc.options.sprintKey, false);
      ElytraAutoUtils.handleRawPressed(mc.options.sneakKey, false);
      mc.options.jumpKey.setPressed(false);
      mc.options.useKey.setPressed(false);
   }

   @Override
   protected void onReset() {
      if (mc.player != null && mc.world != null && mc.interactionManager != null) {
         if (!this.blockSetMode || !this.blockSetFinished) {
            this.runMinerTick();
         }
      }
   }

   private void setCornerFromPlayer(int minerAreaValue, boolean minerAreaFlag) {
      if (mc.player != null) {
         BlockPos blockPos = mc.player.getBlockPos();
         if (minerAreaFlag) {
            if (!(mc.crosshairTarget instanceof BlockHitResult blockHitResult) || blockHitResult.getType() != Type.BLOCK) {
               DisabledEnabled.getInstance().handleDisabledEnabled("Auto Area Miner", "Look at a block first", false);
               return;
            }

            blockPos = blockHitResult.getBlockPos();
         }

         String minerAreaText = blockPos.getX() + " " + blockPos.getY() + " " + blockPos.getZ();
         if (minerAreaValue == 1) {
            this.corner1Text.handleText(minerAreaText);
         } else {
            this.corner2Text.handleText(minerAreaText);
         }

         ModuleConfigAutosave.updateFlag();
         if (!this.blockSetMode) {
            this.updateAreaFromCorners();
         }

         if (this.areaDefined && !this.blockSetMode) {
            DisabledEnabled.getInstance().handleDisabledEnabled("Auto Area Miner", "Area set: " + (long)this.getAreaBlockCount() + " blocks", true);
         } else {
            DisabledEnabled.getInstance().handleDisabledEnabled("Auto Area Miner", "Corner " + minerAreaValue + " set", true);
         }
      }
   }

   private void updateAreaFromBlockSet() {
      this.areaDefined = !this.blockSetPositions.isEmpty();
      if (this.areaDefined) {
         this.areaMinX = this.areaMinY = this.areaMinZ = Integer.MAX_VALUE;
         this.areaMaxX = this.areaMaxY = this.areaMaxZ = Integer.MIN_VALUE;

         for (BlockPos blockPos : this.blockSetPositions) {
            this.areaMinX = Math.min(this.areaMinX, blockPos.getX());
            this.areaMinY = Math.min(this.areaMinY, blockPos.getY());
            this.areaMinZ = Math.min(this.areaMinZ, blockPos.getZ());
            this.areaMaxX = Math.max(this.areaMaxX, blockPos.getX());
            this.areaMaxY = Math.max(this.areaMaxY, blockPos.getY());
            this.areaMaxZ = Math.max(this.areaMaxZ, blockPos.getZ());
         }
      }
   }

   private void markBlockSetFinished(boolean flag) {
      if (this.blockSetMode) {
         this.blockSetFinished = true;
         this.blockSetFailed = flag;
      }
   }

   public boolean isBlockSetFinishedFor(String minerText) {
      return this.blockSetMode && this.blockSetFinished && (this.blockSetTag == null || this.blockSetTag.equals(minerText));
   }

   @Override
   protected void onDisable() {
      this.abortEngineRun();
      this.releaseMovementKeys();
      if (mc.interactionManager != null) {
         mc.interactionManager.cancelBlockBreaking();
      }
   }

   private void fillPreviewPathFrame(Frame frame) {
      if (mc.player != null) {
         if (this.previewPathBlocks == null) {
            BlockPos blockPos = mc.player.getBlockPos();
            Direction horizontalFacing = mc.player.getHorizontalFacing();
            Direction rotateYClockwise = horizontalFacing.rotateYClockwise();
            var items = new ArrayList();
            BlockPos blockPos2 = blockPos;

            for (int i = 0; i < 6; i++) {
               items.add(blockPos2 = blockPos2.offset(horizontalFacing));
            }

            for (int j = 0; j < 4; j++) {
               items.add(blockPos2 = blockPos2.offset(rotateYClockwise));
            }

            this.previewPathBlocks = items;
            this.previewPathEnd = blockPos2;
         }

         frame.trailEnabled = true;
         frame.pathPositions = this.previewPathBlocks;
         frame.targetBlock = this.previewPathEnd;
         frame.targetActive = true;
      }
   }

   private AreaMinerController getOrCreateEngine() {
      if (this.engine == null) {
         this.engine = new AreaMinerController(mc, this.drill, this.miningRegion);
      }

      return this.engine;
   }

   public String formatEngineSummary() {
      return this.engine == null ? "no engine" : this.engine.describeFluidAndPlugStats();
   }

   public void handleProgressTick(QueueCommand queueCommand) {
      this.overlayFrameTracker.renderGuidesSafely(false, queueCommand.tickProgress);
   }

   private double getAreaBlockCount() {
      return (double)(this.areaMaxX - this.areaMinX + 1) * (this.areaMaxY - this.areaMinY + 1) * (this.areaMaxZ - this.areaMinZ + 1);
   }

   public String getStateDescription() {
      return this.engine == null
         ? "state=IDLE"
         : "state="
            + this.engine.getState()
            + " "
            + this.engine.formatStatusLine()
            + (this.engine.isActive() ? " | " + this.engine.buildDebugSummary() : "");
   }

   private void sendMinerMessage(String messageLiteralText) {
      if (mc.player != null) {
         mc.player.sendMessage(Text.literal(messageLiteralText), false);
      }
   }

   public boolean isEngineActive() {
      return this.isEnabled() && this.engine != null && this.engine.isCrosshairOnAimTarget();
   }

   public static void setPreviewPathEnabled(boolean flag) {
      previewPathEnabled = flag;
   }

   public static void setAreaFromCommand(String minerText) {
      if (ModuleRegistry.getModule(AutoAreaMiner.class) instanceof AutoAreaMiner autoAreaMiner && mc.player != null) {
         String[] split = minerText.trim().split("\\s+");
         if (split.length == 6) {
            BlockPos blockPos = mc.player.getBlockPos();
            int[] ints = new int[]{blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX(), blockPos.getY(), blockPos.getZ()};
            int[] ints2 = new int[6];

            for (int i = 0; i < 6; i++) {
               String minerText2 = split[i];
               boolean startsWith = minerText2.startsWith("~");
               String minerText3 = startsWith ? minerText2.substring(1) : minerText2;
               int minerValue = minerText3.isEmpty() ? 0 : Integer.parseInt(minerText3);
               ints2[i] = startsWith ? ints[i] + minerValue : minerValue;
            }

            autoAreaMiner.corner1Text.handleText(ints2[0] + " " + ints2[1] + " " + ints2[2]);
            autoAreaMiner.corner2Text.handleText(ints2[3] + " " + ints2[4] + " " + ints2[5]);
            if (!autoAreaMiner.blockSetMode) {
               autoAreaMiner.updateAreaFromCorners();
            }
         }
      }
   }
}
