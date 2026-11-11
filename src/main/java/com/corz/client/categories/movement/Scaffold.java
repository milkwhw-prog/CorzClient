package com.corz.client.categories.movement;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.EnumSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.config.RangeSetting;
import com.corz.client.entity.ClientThreadUtils;
import com.corz.client.gui.TickProgress;
import com.corz.client.inventory.SelectedInventoryUtils;
import com.corz.client.render.QueueCommand;
import com.corz.client.world.ClampMath;
import com.corz.client.world.MathClampUtils;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShapes;
import com.corz.client.categories.combat.AutoInventoryTotem;
import com.corz.client.utils.EntityDamageUtils;
import com.corz.client.categories.visuals.Freelook;
import ai.auth.Module;
import ai.auth.ModuleRegistry;
import com.corz.client.utils.IliIilType;
import com.corz.client.utils.WindowCode;
import com.corz.client.utils.MaxTarget;
import com.corz.client.utils.AmountConstUtilsAmounts;
import com.corz.client.utils.MinecraftOreUtils;
import com.corz.client.categories.movement.Freecam;
import com.corz.client.utils.AmountsScaffold;
import com.corz.client.utils.IconsPngType;
import com.corz.client.utils.PlayerScaffold;
import com.corz.client.utils.ScaffoldTrade;
import com.corz.client.utils.WeaknessGodApi;

public class Scaffold extends Module implements WeaknessGodApi {
   private double nextTargetX;
   private final IntegerSetting integerHelper;
   private boolean scaffoldActive;
   private BlockPos add;
   private float scaffoldAmount;
   private float randomOffset;
   private IliIilType pendingDirection;
   private final EnumSetting enumHelper = new EnumSetting(
      "Mode", "Legit crouches for you at block edges. GodBridge bridges for you after a few blocks while you hold S", "Legit", "Legit", "GodBridge"
   );
   private long lastNanoTime;
   private final RangeSetting sneakDelay = new RangeSetting(
      "Sneak Delay", "How long to keep crouching after leaving an edge in ms (higher is safer, lower is faster)", 100.0, 200.0, 0.0, 500.0, 10.0
   );
   private IliIilType iliIilType;
   private final BooleanSetting booleanBase;
   private double floorAmount;
   private long lastRaycastTick;
   private final WindowCode windowCode;
   private BlockPos offset;
   private Object player;
   private int randomDelayTicks;
   private long iiIlValue;
   private static final String LEGIT = "Legit";
   private int codeWindowValue;
   private int rangeRaycastValue;
   private final BooleanSetting pitchCheck;
   private Object world;
   private boolean alignedWithYaw;
   private final AmountsScaffold glidingClimbingAmountsScaffo;
   private int scaffoldCount;
   private Scaffold.MoveTask moveTask;
   private boolean offsetBlockActive;
   private long codeValue;
   private String iIiText;
   private boolean codeWindowActive;
   private boolean taskMoveActive;
   private final BooleanSetting blockCount;
   private boolean codeActive;
   private IliIilType floorIliIilType;
   private long showsTime;
   private Item lockedBlockItem;
   private double floorTargetX;
   private double nextTargetZ;
   private boolean godBridgeFlag;
   private static final String GOD_BRIDGE = "GodBridge";
   private boolean reverseDirection;
   private boolean leftKeyHeld;
   private final BooleanSetting requireSneak = new BooleanSetting("Require Sneak", "Legit only helps while you hold sneak", false);
   private int y;
   private final MaxTarget aimSmoother;
   private final IntegerSetting activationBlocks = new IntegerSetting("Activation Blocks", "Blocks you place by hand before GodBridge takes over", 2, 1, 4);
   private long codeCount;
   private int glidingClimbingCount;

   private void updateMinmax() {
      double minmaxValue = this.sneakDelay.getMin();
      double minmaxLimit = this.sneakDelay.getMax();
      this.randomDelayTicks = (int)Math.round(minmaxLimit > minmaxValue ? minmaxValue + ThreadLocalRandom.current().nextDouble() * (minmaxLimit - minmaxValue) : minmaxValue);
   }

   private void handleTimeCode(ClientPlayerEntity player, boolean codeFlag) {
      GameOptions options = mc.options;
      double codeLimit = this.windowCode.getAmount(options.forwardKey, options.backKey);
      boolean onGround = player.isOnGround();
      boolean codeFlag2 = codeLimit <= 0.0 && onGround && this.canMinCollisions(player, 0.2);
      long codeLimit2 = (this.codeCount - this.showsTime) * 50L;
      if (!codeFlag) {
         if (codeFlag2) {
            this.codeActive = true;
         } else if (codeLimit > 0.0 || codeLimit2 >= 500L) {
            this.codeActive = false;
         }
      }

      boolean codeFlag3 = codeFlag2;
      boolean codeFlag4 = false;
      if (!codeFlag2 && codeLimit2 < this.randomDelayTicks && this.randomDelayTicks > 30) {
         codeFlag3 = true;
         codeFlag4 = true;
      }

      if (onGround) {
         if (codeFlag3) {
            if (!player.isSneaking()) {
               this.updateMinmax();
            }

            this.windowCode.handle(options.sneakKey, true);
            this.codeValue = this.codeCount;
            if (!codeFlag4) {
               this.showsTime = this.codeCount;
            }
         } else if (codeFlag && this.requireSneak.isEnabled() && (this.codeCount - this.codeValue) * 50L < 1000L && codeLimit < 0.0) {
            this.windowCode.handle(options.sneakKey, false);
         }
      }
   }

   private void handleRangeRaycast(ClientPlayerEntity player) {
      if (this.codeCount - this.lastRaycastTick >= this.rangeRaycastValue && !this.taskMoveActive) {
         if (!player.isUsingItem() && !ClientThreadUtils.isUSE_KEY_PRESSES()) {
            if (!this.aimSmoother.isFlag() || !(this.aimSmoother.getAmount(player.getYaw()) > 8.0)) {
               if (player.raycast(player.getBlockInteractionRange(), 1.0F, false) instanceof BlockHitResult blockHitResult && this.canOffsetBlock(player, blockHitResult)) {
                  if (this.canSelectedSlot(player)) {
                     ClientThreadUtils.canOptionsInstanceForObject(this);
                  }
               }
            }
         }
      }
   }

   @Override
   public boolean isGodWeaknessWaitingPlayer() {
      return this.godBridgeFlag;
   }

   private boolean canGlidingClimbing(ClientPlayerEntity player) {
      if (!player.getAbilities().flying && !player.isGliding() && !player.isClimbing()) {
         if (player.getY() < this.y + 1.0 - 0.6) {
            return true;
         } else if (!this.canInventoryStack(player) || mc.interactionManager.isBreakingBlock()) {
            return true;
         } else if (!Freelook.isFlag() && this.glidingClimbingAmountsScaffo.isFlag()) {
            return true;
         } else if (this.windowCode.canFlag(mc.options.backKey)) {
            this.glidingClimbingCount = 0;
            return false;
         } else {
            return ++this.glidingClimbingCount > 10 || !this.canFloorIliForClientPlayerEntity(player);
         }
      } else {
         return true;
      }
   }

   private void updateAmount() {
      ThreadLocalRandom current = ThreadLocalRandom.current();
      this.scaffoldAmount = (float)(current.nextDouble() * 2.0 - 1.0);
      this.randomOffset = (float)((current.nextDouble() * 2.0 - 1.0) * 0.5);
   }

   @Override
   protected void onRender2D(TickProgress tickProgress) {
      if (this.blockCount.isEnabled() && mc.player != null && tickProgress.drawContext != null) {
         int stackGod = this.getStackGod(mc.player);
         int scaffoldValue = stackGod >= 32 ? -16597446 : (stackGod >= 16 ? -1774 : -60396);
         DrawContext drawContext = tickProgress.drawContext;
         drawContext.drawCenteredTextWithShadow(
            mc.textRenderer, Integer.toString(stackGod), drawContext.getScaledWindowWidth() / 2, drawContext.getScaledWindowHeight() / 2 + 10, scaffoldValue
         );
      }
   }

   private static double getAmount(double scaffoldValue) {
      return scaffoldValue - Math.floor(scaffoldValue);
   }

   private void handleTaskCode(ClientPlayerEntity player) {
      if (AmountConstUtilsAmounts.canFlag(player.getX(), player.getZ(), this.moveTask.taskCodeAmount, this.moveTask.targetZ, 0.2)) {
         this.moveTask.taskCodeActive = true;
      } else {
         Vec3d velocity = player.getVelocity();
         double taskCodeValue = player.isOnGround() ? 0.06 : 0.004;
         boolean[] flags = AmountConstUtilsAmounts.getFlags(player.getYaw(), player.getX(), player.getZ(), velocity.x, velocity.z, this.moveTask.taskCodeAmount, this.moveTask.targetZ, taskCodeValue);
         GameOptions options = mc.options;
         this.windowCode.handle(options.forwardKey, flags[0]);
         this.windowCode.handle(options.rightKey, flags[1]);
         this.windowCode.handle(options.backKey, flags[2]);
         this.windowCode.handle(options.leftKey, flags[3]);
         this.windowCode.handle(options.sneakKey, true);
      }
   }

   private boolean canIliIil(ClientPlayerEntity player) {
      if (this.codeWindowValue < 16) {
         return false;
      } else {
         int y = player.getVelocity().y > 0.0 ? this.y + 1 : this.y;
         IliIilType iliIilType = this.floorIliIilType.getIilIliType(2);
         BlockPos blockPos = new BlockPos(MathHelper.floor(player.getX()) - this.floorIliIilType.directionX, y, MathHelper.floor(player.getZ()) - this.floorIliIilType.degreesValue);
         if (!this.canCollisionShape(blockPos)) {
            blockPos = blockPos.add(iliIilType.directionX, 0, iliIilType.degreesValue);
            if (!this.canCollisionShape(blockPos)) {
               blockPos = blockPos.add(-2 * iliIilType.directionX, 0, -2 * iliIilType.degreesValue);
            }
         }

         if (!this.canCollisionShape(blockPos)) {
            this.handleCodeWindowForClientPlayerEntity(player);
            return true;
         } else {
            double[] scaffoldValues = AmountConstUtilsAmounts.getAmounts(this.floorIliIilType, this.codeWindowActive);
            this.floorTargetX = blockPos.getX() + scaffoldValues[0];
            this.floorAmount = blockPos.getZ() + scaffoldValues[1];
            this.codeWindowValue = 0;
            this.updateAmount();
            this.handleIliIil(player, 15.0, 0.5, false);
            this.moveTask = new Scaffold.MoveTask(this.floorTargetX, this.floorAmount, true);
            return true;
         }
      }
   }

   @Override
   public boolean isGodWeakness() {
      return this.godBridgeFlag;
   }

   private void handleClimbingFlag(ClientPlayerEntity player) {
      if (!player.isClimbing() && this.canPitchInteger(player)) {
         this.handleTimeCode(player, false);
      } else {
         this.codeActive = false;
      }

      this.handleIliIilAdd(player);
   }

   private boolean canPitchInteger(ClientPlayerEntity player) {
      return !this.pitchCheck.isEnabled() || player.getPitch() >= this.integerHelper.getValue();
   }

   private boolean canCollisionShape(BlockPos pos2) {
      return !mc.world.getBlockState(pos2).getCollisionShape(mc.world, pos2).isEmpty();
   }

   public static boolean isGodBridge() {
      Scaffold scaffold = getScaffold();
      return scaffold != null && (scaffold.godBridgeFlag || scaffold.windowCode.isFlag());
   }

   private static Scaffold getScaffold() {
      return ModuleRegistry.getModule(Scaffold.class) instanceof Scaffold scaffold && scaffold.isEnabled() ? scaffold : null;
   }

   @Override
   protected void onPlayerScaffold(PlayerScaffold playerScaffold) {
      if (this.godBridgeFlag && mc.currentScreen == null && mc.mouse != null && mc.mouse.isCursorLocked()) {
         this.glidingClimbingAmountsScaffo.handleAmount(playerScaffold.getAmount(), playerScaffold.getPlayerScaffoldAmount());
      }
   }

   private void handleIliIil(ClientPlayerEntity player) {
      this.floorIliIilType = this.pendingDirection;
      this.codeWindowActive = this.alignedWithYaw;
      this.floorTargetX = this.nextTargetX;
      this.floorAmount = this.nextTargetZ;
      this.offsetBlockActive = false;
      this.pendingDirection = null;
      this.moveTask = null;
      this.codeWindowValue = 0;
      this.updateAmount();
      this.handleIliIil(player, this.floorIliIilType.isFlag() ? 12.0 : 15.0, 0.0, true);
      this.taskMoveActive = true;
   }

   private void handleHandStack(ClientPlayerEntity player) {
      if (!this.requireSneak.isEnabled() || this.windowCode.canFlag(mc.options.sneakKey)) {
         if (!player.isClimbing() && this.canPitchInteger(player)) {
            if (!this.booleanBase.isEnabled() || MinecraftOreUtils.canEmpty(player.getMainHandStack()) || MinecraftOreUtils.canEmpty(player.getOffHandStack())) {
               this.handleTimeCode(player, true);
            }
         }
      }
   }

   @Override
   protected void onEnable() {
      this.world = null;
      this.player = null;
      this.iIiText = null;
      this.lastNanoTime = 0L;
      this.aimSmoother.updateAmountFlag();
      this.updateFlagIli();
   }

   private void handleGodGround(ClientPlayerEntity player) {
      if (!this.godBridgeFlag) {
         this.handleClimbingFlag(player);
      } else if (this.canGlidingClimbing(player)) {
         this.handleCodeWindowForClientPlayerEntity(player);
      } else {
         this.updateCodeWindowWindowCode();
         if (MathClampUtils.isClampApplied()) {
            if (player.isOnGround()) {
               this.windowCode.handle(mc.options.sneakKey, true);
            }
         } else {
            this.handleRangeRaycast(player);
            if (!this.canTaskMove(player)) {
               if (!this.canIliIilForClientPlayerEntity(player)) {
                  if (!this.canIliIil(player)) {
                     this.handleIliIil(player, 15.0, 0.5, false);
                     this.handleCodeWindow(player);
                  }
               }
            }
         }
      }
   }

   private void updateCodeWindow() {
      if (mc != null && mc.currentScreen == null) {
         this.windowCode.updateWindowCodeBooleanMap();
      } else {
         this.windowCode.updateRestoreClose();
      }
   }

   private boolean canOffsetBlock(ClientPlayerEntity player, BlockHitResult blockHitResult) {
      if (blockHitResult.getType() == Type.BLOCK && !blockHitResult.isInsideBlock()) {
         Direction side = blockHitResult.getSide();
         boolean offsetBlockFlag = Math.abs(player.getVelocity().y) > 0.1;
         if (!AmountConstUtilsAmounts.canFlag(
            side.getOffsetX(), side.getOffsetY(), side.getOffsetZ(), this.floorIliIilType, this.offsetBlockActive ? this.pendingDirection : null, offsetBlockFlag || this.offsetBlockActive
         )) {
            return false;
         } else {
            BlockPos blockPos = blockHitResult.getBlockPos();
            if (!MinecraftOreUtils.canCubeTable(mc.world.getBlockState(blockPos), mc.world, blockPos)) {
               return false;
            } else {
               BlockPos offset = blockPos.offset(side);
               if (offset.getY() == this.y && this.isReplaceable(offset)) {
                  Box box = new Box(offset);
                  if (box.intersects(player.getBoundingBox()) || !mc.world.doesNotIntersectEntities(null, VoxelShapes.cuboid(box))) {
                     return false;
                  } else {
                     return !(Math.abs(offset.getX() + 0.5 - player.getX()) > 1.5) && !(Math.abs(offset.getZ() + 0.5 - player.getZ()) > 1.5)
                        ? !offset.equals(this.offset) || this.codeCount - this.iiIlValue >= this.getMax()
                        : false;
                  }
               } else {
                  return false;
               }
            }
         }
      } else {
         return false;
      }
   }

   public static boolean isGodBridgeGodBridgeFlag() {
      Scaffold scaffold = getScaffold();
      return scaffold != null && scaffold.godBridgeFlag && scaffold.aimSmoother.isFlag();
   }

   @Override
   protected void onReset() {
      if (mc.player != null && mc.world != null && mc.options != null && mc.interactionManager != null) {
         this.codeCount++;
         String text = this.enumHelper.getCurrentOption();
         if (mc.world != this.world || mc.player != this.player || !text.equals(this.iIiText)) {
            this.world = mc.world;
            this.player = mc.player;
            this.iIiText = text;
            this.updateCodeWindow();
            this.aimSmoother.updateAmountFlag();
            this.updateFlagIli();
         }

         this.glidingClimbingAmountsScaffo.updateAmountAmounts();
         if (mc.currentScreen != null) {
            if (this.godBridgeFlag) {
               this.updateIliIil();
            }

            this.windowCode.updateRestoreClose();
         } else {
            ClientPlayerEntity player = mc.player;
            if (!player.isDead() && !player.isSpectator() && !player.hasVehicle() && !Freecam.isInstanceFlag()) {
               if ("GodBridge".equals(text)) {
                  this.handleGodGround(player);
               } else {
                  this.handleHandStack(player);
               }

               this.windowCode.update3();
            } else {
               if (this.godBridgeFlag) {
                  this.updateIliIil();
               }

               this.windowCode.update();
               this.windowCode.update3();
            }
         }
      }
   }

   @Override
   protected void onRender3D(QueueCommand queueCommand) {
      long nanoTime = System.nanoTime();
      double scaffoldValue = this.lastNanoTime == 0L ? 0.0 : Math.min(100.0, (nanoTime - this.lastNanoTime) / 1000000.0);
      this.lastNanoTime = nanoTime;
      ClientPlayerEntity player = mc.player;
      if (this.godBridgeFlag
         && this.aimSmoother.isFlag()
         && player != null
         && mc.currentScreen == null
         && !Freecam.isInstanceFlag()
         && !MathClampUtils.isClampApplied()) {
         double bound = 0.85 + ThreadLocalRandom.current().nextDouble() * 0.3;
         float[] amountMax = this.aimSmoother.getAmountMax(player.getYaw(), player.getPitch(), scaffoldValue, ClampMath.getSensitivityMouse(), bound);
         if (amountMax[0] != 0.0F || amountMax[1] != 0.0F) {
            float yaw = player.getYaw();
            float pitch = player.getPitch();
            float clamp = MathHelper.clamp(pitch + amountMax[1], -90.0F, 90.0F);
            player.setYaw(yaw + amountMax[0]);
            player.setPitch(clamp);
            player.lastYaw = player.lastYaw + amountMax[0];
            player.lastPitch += clamp - pitch;
         }
      }
   }

   private boolean canTaskMove(ClientPlayerEntity player) {
      boolean taskMove = false;
      if (this.taskMoveActive) {
         if (this.aimSmoother.getAmount(player.getYaw()) > 4.0) {
            taskMove = true;
         } else {
            this.taskMoveActive = false;
            this.aimSmoother.updateAmountFlag();
         }
      }

      if (this.moveTask != null && !this.moveTask.taskCodeActive) {
         if (this.moveTask.elapsedTicks >= 40) {
            this.moveTask = null;
            return true;
         }

         this.moveTask.elapsedTicks++;
         this.handleTaskCode(player);
         if (!this.moveTask.taskCodeActive && this.moveTask.taskMoveActive) {
            taskMove = true;
         }
      }

      return taskMove;
   }

   private void handleCodeWindow(ClientPlayerEntity player) {
      boolean floorIli = this.canFloorIli(player);
      this.codeWindowValue = floorIli ? this.codeWindowValue + 1 : 0;
      if (!floorIli) {
         GameOptions options = mc.options;
         this.windowCode.handle(options.backKey, true);
         if (this.floorIliIilType.isFlag()) {
            this.windowCode.handle(options.rightKey, this.codeWindowActive);
            this.windowCode.handle(options.leftKey, !this.codeWindowActive);
         }
      }
   }

   @Override
   public boolean isReductionCritical() {
      return this.godBridgeFlag;
   }

   private void handleIliIilForClientPlayerEntity(ClientPlayerEntity player) {
      this.floorIliIilType = this.iliIilType;
      this.codeWindowActive = AmountConstUtilsAmounts.canFlag(this.floorIliIilType, player.getYaw(), false);
      double[] scaffoldValues = AmountConstUtilsAmounts.getAmounts(this.floorIliIilType, this.codeWindowActive);
      this.floorTargetX = this.add.getX() + scaffoldValues[0];
      this.floorAmount = this.add.getZ() + scaffoldValues[1];
      this.y = this.add.getY();
      this.lockedBlockItem = player.getMainHandStack().getItem();
      this.godBridgeFlag = true;
      this.moveTask = new Scaffold.MoveTask(this.floorTargetX, this.floorAmount, true);
      this.taskMoveActive = false;
      this.offsetBlockActive = false;
      this.pendingDirection = null;
      this.reverseDirection = true;
      this.leftKeyHeld = this.windowCode.canFlag(mc.options.leftKey);
      this.scaffoldActive = this.windowCode.canFlag(mc.options.rightKey);
      this.codeWindowValue = 0;
      this.glidingClimbingCount = 0;
      this.add = null;
      this.scaffoldCount = 0;
      this.glidingClimbingAmountsScaffo.updateAmountsAmount();
      this.updateAmount();
      this.handleIliIil(player, 15.0, 0.5, false);
   }

   public Scaffold() {
      super("Scaffold", IconsPngType.MOVEMENT);
      this.pitchCheck = new BooleanSetting("Pitch Check", "Only help while you look down far enough", false);
      this.integerHelper = new IntegerSetting("Pitch", "How far down you must look in degrees when Pitch Check is on", 45, 0, 90);
      this.booleanBase = new BooleanSetting("Only With Blocks", "Legit only helps while you hold a block", false);
      this.blockCount = new BooleanSetting("Block Count", "Shows how many blocks you have under your crosshair", true);
      this.windowCode = new WindowCode();
      this.aimSmoother = new MaxTarget();
      this.glidingClimbingAmountsScaffo = new AmountsScaffold();
      this.showsTime = -1000L;
      this.codeValue = -1000L;
      this.floorIliIilType = IliIilType.N;
      this.reverseDirection = true;
      this.lastRaycastTick = -1000L;
      this.rangeRaycastValue = 3;
      this.iiIlValue = -1000L;
      this.addSettings(
         new ConfigSetting[]{
            this.enumHelper, this.sneakDelay, this.requireSneak, this.activationBlocks, this.pitchCheck, this.integerHelper, this.booleanBase, this.blockCount
         }
      );
   }

   private boolean canCodeWindow(ClientPlayerEntity player, BlockPos pos) {
      if (this.codeActive && player.isOnGround() && !player.getAbilities().flying) {
         GameOptions options = mc.options;
         if (!this.windowCode.canFlag(options.backKey) || this.windowCode.canFlag(options.forwardKey) || this.windowCode.canFlag(options.jumpKey)) {
            return false;
         } else {
            return !this.windowCode.canFlag(options.attackKey) && !mc.interactionManager.isBreakingBlock() && !MathClampUtils.isClampApplied()
               ? Math.abs(pos.getX() - this.add.getX()) <= 1 && Math.abs(pos.getZ() - this.add.getZ()) <= 1
               : false;
         }
      } else {
         return false;
      }
   }

   private int getStackGod(ClientPlayerEntity player) {
      int stackGod = 0;
      if ("GodBridge".equals(this.enumHelper.getCurrentOption())) {
         PlayerInventory inventory = player.getInventory();

         for (int i = 0; i < 9; i++) {
            ItemStack stack = inventory.getStack(i);
            if (MinecraftOreUtils.isFullCube(stack)) {
               stackGod += stack.getCount();
            }
         }

         ItemStack offHandStack = player.getOffHandStack();
         if (MinecraftOreUtils.isFullCube(offHandStack)) {
            stackGod += offHandStack.getCount();
         }

         return stackGod;
      } else {
         ItemStack mainHandStack = player.getMainHandStack();
         if (MinecraftOreUtils.canEmpty(mainHandStack)) {
            stackGod += mainHandStack.getCount();
         }

         ItemStack itemStack = player.getOffHandStack();
         if (MinecraftOreUtils.canEmpty(itemStack)) {
            stackGod += itemStack.getCount();
         }

         return stackGod;
      }
   }

   private void handleIliIilAdd(ClientPlayerEntity player) {
      if (!MinecraftOreUtils.isFullCube(player.getMainHandStack())) {
         this.add = null;
         this.scaffoldCount = 0;
      } else {
         IliIilType iliIilType = IliIilType.getIilIliTypeForDouble(player.getYaw());
         if (this.iliIilType != null && iliIilType != this.iliIilType) {
            this.add = null;
            this.scaffoldCount = 0;
         }

         this.iliIilType = iliIilType;
         BlockPos blockPos = new BlockPos(MathHelper.floor(player.getX()), AmountConstUtilsAmounts.getY(player.getY()), MathHelper.floor(player.getZ()));
         if (this.add != null) {
            int scaffoldLimit = this.activationBlocks.getValue();
            if (AmountConstUtilsAmounts.canFlag(
               this.add.getX(), this.add.getY(), this.add.getZ(), blockPos.getX(), blockPos.getY(), blockPos.getZ(), iliIilType, this.scaffoldCount, scaffoldLimit
            )) {
               this.add = null;
               this.scaffoldCount = 0;
            } else if (this.scaffoldCount >= scaffoldLimit) {
               if (this.canCodeWindow(player, blockPos)) {
                  this.handleIliIilForClientPlayerEntity(player);
               }
            } else if (!this.isReplaceable(this.add)) {
               this.scaffoldCount++;
               BlockPos add = this.add.add(iliIilType.directionX, 0, iliIilType.degreesValue);
               if (!this.isReplaceable(add)) {
                  this.add = null;
                  this.scaffoldCount = 0;
               } else if (this.scaffoldCount < scaffoldLimit) {
                  this.add = add;
               }
            }
         } else if (player.isOnGround()) {
            for (int i = 0; i <= 2; i++) {
               BlockPos pos2 = blockPos.add(iliIilType.directionX * i, 0, iliIilType.degreesValue * i);
               if (this.isReplaceable(pos2)) {
                  this.add = pos2;
                  break;
               }
            }
         }
      }
   }

   private boolean isReplaceable(BlockPos pos2) {
      return mc.world.getBlockState(pos2).isReplaceable();
   }

   private boolean canInventoryStack(ClientPlayerEntity player) {
      PlayerInventory inventory = player.getInventory();

      for (int i = 0; i < 9; i++) {
         if (MinecraftOreUtils.isFullCube(inventory.getStack(i))) {
            return true;
         }
      }

      return false;
   }

   public static boolean isGodBridgeFlag() {
      Scaffold scaffold = getScaffold();
      return scaffold != null && scaffold.godBridgeFlag;
   }

   private boolean canMinCollisions(ClientPlayerEntity player, double collisionsValue) {
      Vec3d velocity = player.getVelocity();
      Box offset = player.getBoundingBox().contract(collisionsValue, 0.0, collisionsValue).offset(velocity.x, -1.0, velocity.z);
      Box box = new Box(offset.minX, offset.minY, offset.minZ, offset.maxX, offset.minY + 1.0, offset.maxZ);
      return !mc.world.getBlockCollisions(player, box).iterator().hasNext();
   }

   @Override
   protected void onDisable() {
      this.updateCodeWindow();
      this.aimSmoother.updateAmountFlag();
      this.updateFlagIli();
   }

   private void updateIliIil() {
      this.godBridgeFlag = false;
      this.aimSmoother.updateAmountFlag();
      this.windowCode.update();
      this.moveTask = null;
      this.taskMoveActive = false;
      this.offsetBlockActive = false;
      this.pendingDirection = null;
      this.reverseDirection = true;
      this.lockedBlockItem = null;
      this.codeActive = false;
      this.add = null;
      this.scaffoldCount = 0;
      this.iliIilType = null;
      this.codeWindowValue = 0;
      this.glidingClimbingCount = 0;
      this.showsTime = this.codeCount;
      this.updateMinmax();
      this.glidingClimbingAmountsScaffo.updateAmountsAmount();
   }

   private void handleIliIil(ClientPlayerEntity player, double scaffoldValue, double apiPutBound, boolean scaffoldFlag) {
      float scaffoldValue3 = (float)(
         AmountConstUtilsAmounts.getAmount(this.floorIliIilType, this.codeWindowActive)
            + AmountConstUtilsAmounts.getAmount(this.floorIliIilType, this.floorTargetX - player.getX(), this.floorAmount - player.getZ())
            + this.scaffoldAmount
      );
      float bound = AmountConstUtilsAmounts.getAmount(this.floorIliIilType.isFlag(), this.codeWindowValue) + this.randomOffset;
      this.aimSmoother.handleAmountFlag(scaffoldValue3, bound, AmountConstUtilsAmounts.getAmount(player.getYaw(), scaffoldValue3, scaffoldValue), apiPutBound, scaffoldFlag);
   }

   private boolean canIliIilForClientPlayerEntity(ClientPlayerEntity player) {
      if (this.offsetBlockActive && this.moveTask != null && this.moveTask.taskCodeActive) {
         this.handleIliIil(player);
         return true;
      } else {
         if (!this.offsetBlockActive) {
            boolean flag = this.windowCode.canFlag(mc.options.leftKey);
            boolean canFlag = this.windowCode.canFlag(mc.options.rightKey);
            boolean scaffoldFlag = this.reverseDirection ? flag && !this.leftKeyHeld : !flag && this.leftKeyHeld;
            boolean scaffoldFlag2 = this.reverseDirection ? canFlag && !this.scaffoldActive : !canFlag && this.scaffoldActive;
            this.leftKeyHeld = flag;
            this.scaffoldActive = canFlag;
            if (scaffoldFlag) {
               this.pendingDirection = this.floorIliIilType.getIilIliType(this.reverseDirection ? 1 : -1);
            }

            if (scaffoldFlag2) {
               this.pendingDirection = this.floorIliIilType.getIilIliType(this.reverseDirection ? -1 : 1);
            }

            if (scaffoldFlag || scaffoldFlag2) {
               this.reverseDirection = !this.reverseDirection;
            }

            this.offsetBlockActive = scaffoldFlag || scaffoldFlag2;
            if (!this.offsetBlockActive) {
               return false;
            }
         }

         this.moveTask = null;
         if (player.isOnGround() && !this.canMinCollisions(player, 0.2) && !AmountConstUtilsAmounts.canFlag(this.floorIliIilType, getAmount(player.getX()), getAmount(player.getZ()))) {
            this.alignedWithYaw = AmountConstUtilsAmounts.canFlag(this.pendingDirection, player.getYaw(), this.codeWindowActive);
            double[] scaffoldValues = AmountConstUtilsAmounts.getAmounts(this.pendingDirection, this.alignedWithYaw);
            this.nextTargetX = MathHelper.floor(player.getX()) + scaffoldValues[0];
            this.nextTargetZ = MathHelper.floor(player.getZ()) + scaffoldValues[1];
            this.aimSmoother.updateAmountFlag();
            boolean scaffoldFlag3 = this.floorIliIilType.isFlag()
               && (!this.codeWindowActive && this.pendingDirection == this.floorIliIilType.getIilIliType(-1) || this.codeWindowActive && this.pendingDirection == this.floorIliIilType.getIilIliType(1));
            boolean scaffoldFlag4 = Math.abs(player.getX() - this.nextTargetX) <= 0.15 && Math.abs(player.getZ() - this.nextTargetZ) <= 0.15;
            this.moveTask = !scaffoldFlag3 && !scaffoldFlag4 ? new Scaffold.MoveTask(this.nextTargetX, this.nextTargetZ, true) : Scaffold.MoveTask.getFlag();
            return false;
         } else {
            return false;
         }
      }
   }

   private int getMax() {
      return Math.max(4, (int)Math.ceil(EntityDamageUtils.getCachedHurtTimeLimit() / 50.0) + 2);
   }

   private boolean canSelectedSlot(ClientPlayerEntity player) {
      PlayerInventory inventory = player.getInventory();
      ItemStack mainHandStack = player.getMainHandStack();
      if (!MinecraftOreUtils.isFullCube(mainHandStack) || this.lockedBlockItem != null && !mainHandStack.isOf(this.lockedBlockItem)) {
         int selectedSlot = -1;
         if (this.lockedBlockItem != null) {
            Item item = this.lockedBlockItem;
            selectedSlot = SelectedInventoryUtils.getInstanceInventory(want -> want.isOf(item) && MinecraftOreUtils.isFullCube(want));
         }

         if (selectedSlot < 0 && MinecraftOreUtils.isFullCube(mainHandStack)) {
            this.lockedBlockItem = mainHandStack.getItem();
            return true;
         } else {
            if (selectedSlot < 0) {
               selectedSlot = SelectedInventoryUtils.getInstanceInventory(MinecraftOreUtils::isFullCube);
            }

            if (selectedSlot < 0) {
               return false;
            } else if (selectedSlot == inventory.getSelectedSlot()) {
               return true;
            } else if (!AutoInventoryTotem.isHandSwapSlotActive() && !SelectedInventoryUtils.isSelectedSlot()) {
               inventory.setSelectedSlot(selectedSlot);
               this.lockedBlockItem = inventory.getStack(selectedSlot).getItem();
               return true;
            } else {
               return false;
            }
         }
      } else {
         return true;
      }
   }

   private boolean canFloorIli(ClientPlayerEntity player) {
      if (!this.floorIliIilType.isFlag()) {
         return this.canMinCollisions(player, 0.16);
      } else {
         double[] floorValues = AmountConstUtilsAmounts.getAmounts(this.floorIliIilType, player.getX(), player.getZ(), this.floorTargetX, this.floorAmount);
         return !this.canCollisionShape(new BlockPos(MathHelper.floor(floorValues[0]), this.y, MathHelper.floor(floorValues[1])));
      }
   }

   private boolean canFloorIliForClientPlayerEntity(ClientPlayerEntity player) {
      double[] floorValues = AmountConstUtilsAmounts.getAmountsForIliIilTypeDouble(this.floorIliIilType, player.getX(), player.getZ(), this.floorTargetX, this.floorAmount);
      return !this.canCollisionShape(new BlockPos(MathHelper.floor(floorValues[0]), this.y, MathHelper.floor(floorValues[1])));
   }

   @Override
   protected void onPreItemUse(ScaffoldTrade scaffoldTrade) {
      if (this.godBridgeFlag && mc.player != null && mc.world != null) {
         if (ClientThreadUtils.getObject() == this
            && !MathClampUtils.isClampApplied()
            && mc.interactionManager != null
            && !mc.interactionManager.isBreakingBlock()) {
            ClientPlayerEntity player = mc.player;
            if (mc.crosshairTarget instanceof BlockHitResult blockHitResult
               && this.canOffsetBlock(player, blockHitResult)
               && MinecraftOreUtils.isFullCube(player.getMainHandStack())) {
               this.lastRaycastTick = this.codeCount;
               this.rangeRaycastValue = AmountConstUtilsAmounts.getMin(ThreadLocalRandom.current().nextDouble());
               this.offset = blockHitResult.getBlockPos().offset(blockHitResult.getSide());
               this.iiIlValue = this.codeCount;
               this.updateAmount();
            } else {
               scaffoldTrade.updateFlag();
            }
         } else {
            scaffoldTrade.updateFlag();
         }
      }
   }

   private void updateCodeWindowWindowCode() {
      GameOptions options = mc.options;
      this.windowCode.handle(options.forwardKey, false);
      this.windowCode.handle(options.backKey, false);
      this.windowCode.handle(options.leftKey, false);
      this.windowCode.handle(options.rightKey, false);
      this.windowCode.handle(options.sprintKey, false);
   }

   private void updateFlagIli() {
      this.godBridgeFlag = false;
      this.moveTask = null;
      this.taskMoveActive = false;
      this.offsetBlockActive = false;
      this.reverseDirection = true;
      this.leftKeyHeld = false;
      this.scaffoldActive = false;
      this.pendingDirection = null;
      this.lockedBlockItem = null;
      this.codeActive = false;
      this.add = null;
      this.scaffoldCount = 0;
      this.iliIilType = null;
      this.codeWindowValue = 0;
      this.glidingClimbingCount = 0;
      this.showsTime = -1000L;
      this.codeValue = -1000L;
      this.lastRaycastTick = -1000L;
      this.offset = null;
      this.iiIlValue = -1000L;
      this.rangeRaycastValue = 2;
      this.scaffoldAmount = 0.0F;
      this.randomOffset = 0.0F;
      this.glidingClimbingAmountsScaffo.updateAmountsAmount();
      this.windowCode.update();
      this.updateMinmax();
   }

   private void handleCodeWindowForClientPlayerEntity(ClientPlayerEntity player) {
      boolean codeWindowFlag = player.isOnGround() && this.canMinCollisions(player, 0.2);
      this.updateIliIil();
      if (codeWindowFlag) {
         GameOptions options = mc.options;
         this.windowCode.handle(options.backKey, false);
         this.windowCode.handle(options.leftKey, false);
         this.windowCode.handle(options.rightKey, false);
      }

      if (!player.isClimbing()) {
         this.handleTimeCode(player, false);
      }
   }

   private static final class MoveTask {
      final double targetZ;
      int elapsedTicks;
      final boolean taskMoveActive;
      boolean taskCodeActive;
      final double taskCodeAmount;

      MoveTask(double amount, double amount2, boolean flag) {
         this.taskCodeAmount = amount;
         this.targetZ = amount2;
         this.taskMoveActive = flag;
      }

      static Scaffold.MoveTask getFlag() {
         Scaffold.MoveTask moveTask = new Scaffold.MoveTask(0.0, 0.0, false);
         moveTask.taskCodeActive = true;
         return moveTask;
      }
   }
}
