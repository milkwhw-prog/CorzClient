package com.corz.client.inventory;

import com.corz.client.config.FriendNameStore;
import com.corz.client.entity.ClientThreadUtils;
import com.corz.client.entity.DeflectZone;
import com.corz.client.entity.EntityPlayerUtilsLiving;
import com.corz.client.entity.FloorMathUtilsDirection;
import com.corz.client.world.AbsorptionPosData;
import com.corz.client.world.BlockStateUtilsDirections;
import com.corz.client.world.BoxLengthUtils;
import com.corz.client.world.CenterVecData;
import com.corz.client.world.HuntDeflect;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.IntFunction;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResult.Success;
import net.minecraft.util.ActionResult.SwingSource;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import net.minecraft.world.World;
import com.corz.client.utils.HeightDeltaMath;
import com.corz.client.utils.BotActionType;
import com.corz.client.utils.HotbarSlotSwitcher;
import com.corz.client.utils.HandswapGate;
import com.corz.client.utils.BlockHazardFlags;
import com.corz.client.utils.TrackFlick;
import com.corz.client.utils.AmountConstTime;
import com.corz.client.utils.CaseMaxData;
import com.corz.client.utils.DefensiveAggressiveType;
import com.corz.client.utils.DeflectAmountsUtils;
import com.corz.client.utils.PearlsPlaystyleData;
import com.corz.client.utils.SelfPopData;
import com.corz.client.utils.SurroundKitType;
import com.corz.client.utils.VecShieldUtilsAmounts;
import com.corz.client.utils.ZoneEscapeUtils2;
import com.corz.client.utils.ZoneEscapeUtilsFlag;

public final class CooldownEscape {
   private String landingCooldownText;
   private double escapeDistance;
   private long pearlLandingTime;
   private final TrackFlick trackFlick;
   private int targetId;
   private double acceptDeflectAmount;
   private double cooldownAimAmount;
   public static final String DASH_PLACEHOLDER_TEXT = "-";
   public static final String CRYSTAL = "crystal";
   private int aimStuckValue;
   private long lastUpdateTick;
   private String landingEscapeText;
   public static final String ESCAPE = "escape";
   private final HotbarSlotSwitcher inventorySelected;
   private boolean pearlLandingActive;
   private PearlsPlaystyleData pearlLandingPearlsPlaystyleD;
   private String cooldownEscapingText;
   private double escapeLandingAmount;
   private int functionIntervalCount;
   public static final String DEFLECT = "deflect";
   private CooldownEscape.Plan vecData;
   private long aimStuckTime;
   private long pearlTime;
   private double functionIntervalAmount;
   public static final String LANDING = "no landing";
   private int crystalSelectValue;
   private long aimDeadlineTick;
   private CooldownEscape.State cooldown = CooldownEscape.State.IDLE;
   private String cooldownAimText;
   private int functionIntervalValue;
   private boolean accept;
   private long crystalDeflectTime;
   private int aimTimeoutValue;
   private int escapeLandingCount;
   private long cooldownEscapingTime;
   private int vecMovedCount;
   private final HandswapGate cooldownCooldownAim;
   private long cooldownTime;
   private IntFunction<AmountConstTime> intFunction;
   private Vec3d entityPos;
   private long landingCooldownValue;
   private CooldownEscape.Plan escapeLanding;
   private Vec3d entityPosCooldownAim;
   private AmountConstTime.Spot functionIntervalSpot;
   public static final double[] escapeLandingTable = new double[]{16.0, 22.0, 28.0};
   private AbsorptionPosData absorptionPosData;
   public static final String ZONE = "zone";
   private int cooldownEscapeCount;

   private static List<Vec3d> getPosCreative(ClientWorld world, ClientPlayerEntity player, SelfPopData selfPopData) {
      ArrayList arrayList = new ArrayList();

      for (PlayerEntity playerEntity : world.getPlayers()) {
         if (playerEntity != player
            && playerEntity.isAlive()
            && !playerEntity.isRemoved()
            && !playerEntity.isSpectator()
            && !playerEntity.isCreative()
            && !FriendNameStore.canFlag(playerEntity)
            && !EntityPlayerUtilsLiving.canScoreboardTeam(playerEntity)
            && !(playerEntity.squaredDistanceTo(player) > 4096.0)) {
            arrayList.add(playerEntity.getEntityPos());
         }
      }

      if (arrayList.isEmpty() && selfPopData != null && selfPopData.target() != null && selfPopData.target().pos() != null) {
         arrayList.add(selfPopData.target().pos());
      }

      return arrayList;
   }

   private CooldownEscape.State getCooldownEscaping(long time, String text) {
      this.cooldownEscapingText = text;
      this.lastUpdateTick = time;
      this.cooldownEscapingTime = time + 4L;
      this.updateAimStuck();
      this.aimStuckValue = 0;
      this.cooldown = CooldownEscape.State.FAILED;
      return CooldownEscape.State.FAILED;
   }

   private boolean canVecMoved(ClientPlayerEntity player, World world, long vecMovedValue, float cosSinFactor, float vecMovedValue3) {
      this.handleFunctionInterval(vecMovedValue, this.targetId);
      if (this.functionIntervalSpot != null && player != null) {
         Vec3d eye = FloorMathUtilsDirection.getEye(player);
         Vec3d cosSin = getCosSin(cosSinFactor, vecMovedValue3, getGround(player));
         FloorMathUtilsDirection.Result vecDirection = FloorMathUtilsDirection.getVecDirection(eye, cosSin, 160);
         return vecDirection != null && vecDirection.movedFlag && vecDirection.vec3d3VecMoved != null ? this.canAmount(world, eye, cosSin, vecDirection.vec3d3VecMoved, vecDirection.vec3d3VecMoved.y, Math.max(1, vecDirection.flightTicks)) : false;
      } else {
         return false;
      }
   }

   private boolean canAcceptVec(CooldownEscape.Plan plan) {
      return this.cooldown == CooldownEscape.State.AIM && this.vecData != null && !this.accept && canLandingWhy(this.vecData, plan, this.acceptDeflectAmount);
   }

   public boolean isCooldownCooling() {
      ClientPlayerEntity player = MinecraftClient.getInstance().player;
      if (player == null) {
         return false;
      } else if (ClientThreadUtils.getTime() < this.cooldownEscapingTime) {
         return false;
      } else if (this.cooldown != CooldownEscape.State.IDLE && this.cooldown != CooldownEscape.State.AIM) {
         return false;
      } else {
         int selectedSlot = this.inventorySelected.findSlotMatching(stack -> stack.isOf(Items.ENDER_PEARL), false);
         if (selectedSlot < 0) {
            return false;
         } else {
            ItemStack stack2 = player.getInventory().getStack(selectedSlot);
            return !player.getItemCooldownManager().isCoolingDown(stack2);
         }
      }
   }

   private static Vec3d getGround(ClientPlayerEntity player) {
      return new Vec3d(player.getX() - player.lastX, player.isOnGround() ? 0.0 : player.getY() - player.lastY, player.getZ() - player.lastZ);
   }

   static boolean canFlag(Box box, Vec3d vec) {
      return box != null && vec != null && box.contains(vec);
   }

   public CooldownEscape(TrackFlick trackFlick, HandswapGate cooldown, HotbarSlotSwitcher inventorySelected) {
      this.acceptDeflectAmount = 2.5;
      this.aimDeadlineTick = Long.MAX_VALUE;
      this.aimStuckTime = Long.MIN_VALUE;
      this.aimTimeoutValue = 8;
      this.cooldownEscapingTime = Long.MIN_VALUE;
      this.cooldownTime = Long.MIN_VALUE;
      this.lastUpdateTick = Long.MIN_VALUE;
      this.crystalDeflectTime = Long.MIN_VALUE;
      this.cooldownAimText = "-";
      this.landingCooldownText = "-";
      this.landingCooldownValue = Long.MIN_VALUE;
      this.pearlLandingTime = Long.MIN_VALUE;
      this.pearlTime = Long.MIN_VALUE;
      this.targetId = -1;
      this.trackFlick = trackFlick;
      this.cooldownCooldownAim = cooldown;
      this.inventorySelected = inventorySelected;
   }

   private void handleTextTime(long time, String text) {
      this.landingCooldownText = text;
      this.landingCooldownValue = time;
   }

   public void updateCooldownThrown() {
      this.updateAimStuck();
      this.escapeLanding = null;
      this.pearlLandingTime = Long.MIN_VALUE;
      if (this.cooldown != CooldownEscape.State.THROWN) {
         this.cooldown = CooldownEscape.State.IDLE;
      }
   }

   public CooldownEscape.Plan getEscapeLanding(SelfPopData selfPopData) {
      MinecraftClient instance = MinecraftClient.getInstance();
      ClientPlayerEntity player = instance.player;
      ClientWorld world = instance.world;
      if (player != null && world != null && selfPopData != null && selfPopData.self() != null) {
         AbsorptionPosData self = selfPopData.self();
         if (!(self.getAbsorptionHealth() > 6.0) && !self.totemConfirmed()) {
            return null;
         } else {
            List posCreative = getPosCreative(world, player, selfPopData);
            Vec3d entityPos = player.getEntityPos();
            double escapeLandingTotal = 0.0;
            double escapeLandingTotal2 = 0.0;

            for (Vec3d vec3d : (Iterable<net.minecraft.util.math.Vec3d>) (posCreative)) {
               escapeLandingTotal += vec3d.x;
               escapeLandingTotal2 += vec3d.z;
            }

            float escapeLandingValue;
            if (posCreative.isEmpty()) {
               escapeLandingValue = player.getYaw();
            } else {
               double escapeLandingValue2 = escapeLandingTotal / posCreative.size();
               double escapeLandingValue3 = escapeLandingTotal2 / posCreative.size();
               escapeLandingValue = VecShieldUtilsAmounts.getAmount(entityPos.x - escapeLandingValue2, entityPos.z - escapeLandingValue3);
            }

            Vec3d eye = FloorMathUtilsDirection.getEye(player);
            Vec3d ground = getGround(player);
            List list = getItems(escapeLandingValue);
            this.vecMovedCount = 0;
            this.handleFunctionInterval(selfPopData.tick(), selfPopData.target() == null ? -1 : selfPopData.target().id());
            CooldownEscape.Plan escapeLanding = null;
            double amount = 0.0;
            double negativeInfinity = Double.NEGATIVE_INFINITY;
            int escapeLandingLimit = 0;

            for (float degrees : (Iterable<Float>) (list)) {
               if (this.vecMovedCount >= 72 || escapeLandingLimit >= 6) {
                  break;
               }

               for (double escapeLandingTableItem : escapeLandingTable) {
                  if (this.vecMovedCount >= 72 || escapeLandingLimit >= 6) {
                     break;
                  }

                  CooldownEscape.Candidate vecMoved = this.getVecMoved(world, eye, ground, degrees, escapeLandingTableItem);
                  if (vecMoved != null) {
                     double flooredBottom = getFlooredBottom(world, vecMoved.landing, vecMoved.stand, posCreative);
                     if (!Double.isNaN(flooredBottom)
                        && !this.canAmount(world, eye, getCosSin(degrees, vecMoved.pitch, ground), vecMoved.landing, vecMoved.stand.getY(), vecMoved.flight)) {
                        escapeLandingLimit++;
                        if (flooredBottom > negativeInfinity) {
                           negativeInfinity = flooredBottom;
                           escapeLanding = new CooldownEscape.Plan(degrees, vecMoved.pitch, vecMoved.landing, "escape");
                           amount = escapeLandingTableItem;
                        }
                     }
                  }
               }
            }

            this.escapeLandingAmount = escapeLanding == null ? 0.0 : negativeInfinity;
            this.cooldownAimAmount = amount;
            this.landingEscapeText = escapeLanding != null ? null : formatZoneDeflect(this.cooldownEscapeCount, this.escapeLandingCount);
            return escapeLanding;
         }
      } else {
         return null;
      }
   }

   public String getCooldownEscapingText() {
      return this.cooldownEscapingText;
   }

   private static Vec3d getVecMoved(ClientPlayerEntity player, float vecMovedValue, float vecMovedValue2, int vecDirectionValue) {
      FloorMathUtilsDirection.Result vecDirection = FloorMathUtilsDirection.getVecDirection(
         FloorMathUtilsDirection.getEye(player), getCosSin(vecMovedValue, vecMovedValue2, getGround(player)), vecDirectionValue
      );
      return vecDirection != null && vecDirection.movedFlag && vecDirection.vec3d3VecMoved != null ? vecDirection.vec3d3VecMoved : null;
   }

   private boolean isCoolingDown() {
      ClientPlayerEntity player = MinecraftClient.getInstance().player;
      if (player == null) {
         return true;
      } else {
         int selectedSlot = this.inventorySelected.findSlotMatching(stack -> stack.isOf(Items.ENDER_PEARL), false);
         return selectedSlot < 0 ? true : player.getItemCooldownManager().isCoolingDown(player.getInventory().getStack(selectedSlot));
      }
   }

   private int getPitchYaw(CooldownEscape.Plan plan) {
      return this.trackFlick != null && plan != null
         ? getMax(BoxLengthUtils.getAmount(this.trackFlick.getCurrentYaw(), this.trackFlick.getCurrentPitch(), plan.yaw(), plan.pitch()))
         : 8;
   }

   private static boolean canExpandEntities(ClientWorld world, ClientPlayerEntity player, Vec3d vec) {
      if (world != null && player != null && vec != null) {
         Box expand = new Box(vec, vec).expand(1.0E-4);
         return !world.getOtherEntities(player, expand, entity -> entity instanceof EndCrystalEntity && canFlag(entity.getBoundingBox(), vec)).isEmpty();
      } else {
         return false;
      }
   }

   static String formatZoneDeflect(int zoneDeflectLimit, int zoneDeflectLimit2) {
      if (zoneDeflectLimit > 0) {
         return "deflect";
      } else {
         return zoneDeflectLimit2 > 0 ? "zone" : null;
      }
   }

   public String getCooldownAimText() {
      return this.cooldownAimText;
   }

   private boolean isAbsorptionPearls() {
      return HuntDeflect.canPearlsTotems(this.pearlLandingPearlsPlaystyleD == null || this.pearlLandingPearlsPlaystyleD.escapePearls(), this.absorptionPosData, this.pearlLandingActive);
   }

   static int getValue(int cooldownEscapeValue, long cooldownEscapeValue2, long cooldownEscapeValue3) {
      return cooldownEscapeValue2 != Long.MIN_VALUE && cooldownEscapeValue2 == cooldownEscapeValue3 - 1L ? cooldownEscapeValue + 1 : 1;
   }

   public boolean isTime() {
      return this.lastUpdateTick == ClientThreadUtils.getTime();
   }

   private static double getFlooredBottom(ClientWorld world, Vec3d vec, BlockPos pos, List<Vec3d> list) {
      double flooredBottomBound = Double.POSITIVE_INFINITY;

      for (Vec3d vec3d : list) {
         flooredBottomBound = Math.min(flooredBottomBound, vec3d.distanceTo(vec));
      }

      if (flooredBottomBound < 20.0) {
         return Double.NaN;
      } else {
         if (Double.isInfinite(flooredBottomBound)) {
            flooredBottomBound = 40.0;
         }

         int downState = BlockHazardFlags.countOpenBlocksBelow(world, BlockPos.ofFloored(vec), 4);
         boolean flooredBottomFlag = false;
         BlockStateUtilsDirections.Hole bedrockState = BlockStateUtilsDirections.getBedrockState(world, pos);
         if (bedrockState != null && bedrockState.type() != BlockStateUtilsDirections.Type.NONE && bedrockState.openSides() <= 1) {
            flooredBottomFlag = true;
         }

         if (!flooredBottomFlag && !list.isEmpty()) {
            Vec3d add = Vec3d.ofBottomCenter(pos).add(0.0, 1.6, 0.0);
            boolean flooredBottomFlag2 = true;

            for (Vec3d vec3d2 : list) {
               if (BlockEntityUtils.canSubtractRaycast(vec3d2.add(0.0, 1.62, 0.0), add)) {
                  flooredBottomFlag2 = false;
                  break;
               }
            }

            flooredBottomFlag = flooredBottomFlag2;
         }

         return flooredBottomBound + (flooredBottomFlag ? 2.0 : 0.0) - 0.5 * downState;
      }
   }

   private ZoneEscapeUtilsFlag.Clear getInstance(World world) {
      return world == null
         ? ZoneEscapeUtilsFlag.Clear.INSTANCE
         : (x, y, z, x2, y2, z2) -> {
            if (this.functionIntervalCount <= 0) {
               return true;
            } else {
               this.functionIntervalCount--;
               Vec3d vec3d = new Vec3d(x, y, z);
               Vec3d vec3d2 = new Vec3d(x2, y2, z2);
               return vec3d.squaredDistanceTo(vec3d2) < 1.0E-8
                  ? true
                  : world.raycast(new RaycastContext(vec3d, vec3d2, ShapeType.COLLIDER, FluidHandling.NONE, ShapeContext.absent())).getType() == Type.MISS;
            }
         };
   }

   static double getAmount(
      Vec3d vec, Vec3d vec2, double cooldownEscapeValue, int cooldownEscapeLimit, AmountConstTime.Spot spot, int zoneEscapeValue, double zoneEscapeBound, ZoneEscapeUtilsFlag.Clear clear
   ) {
      if (vec != null && vec2 != null && spot != null && cooldownEscapeLimit >= 1 && !Double.isNaN(cooldownEscapeValue)) {
         int[] cooldownEscapeValues = ZoneEscapeUtilsFlag.getValues(zoneEscapeValue, zoneEscapeBound, cooldownEscapeLimit);
         return cooldownEscapeValues.length == 0
            ? 0.0
            : ZoneEscapeUtilsFlag.getInstance(
               vec.x, vec.y, vec.z, vec2.x, vec2.y, vec2.z, cooldownEscapeValue, cooldownEscapeValues, spot.getX(), spot.getY(), spot.getZ(), clear
            );
      } else {
         return 0.0;
      }
   }

   public boolean isAimCooldown() {
      return this.cooldown == CooldownEscape.State.AIM && this.vecData != null;
   }

   public void handleCooldownAim(CooldownEscape.Plan vecData, long max, double cooldownAimLimit) {
      if (vecData != null && vecData.landing() != null && Float.isFinite(vecData.yaw()) && Float.isFinite(vecData.pitch())) {
         long aimStuckTime = ClientThreadUtils.getTime();
         if (this.cooldown != CooldownEscape.State.THROWN || this.cooldownTime != aimStuckTime) {
            if (aimStuckTime < this.cooldownEscapingTime) {
               this.handleTextTime(aimStuckTime, "cooldown");
            } else if (this.cooldown != CooldownEscape.State.AIM || !this.accept) {
               if (aimStuckTime <= max) {
                  if (!this.isAbsorptionPearls()) {
                     this.handleTextTime(aimStuckTime, "gates");
                  } else if (this.isCoolingDown()) {
                     this.handleTextTime(aimStuckTime, "cooldown");
                  } else if (this.canAcceptVec(vecData)) {
                     this.aimDeadlineTick = Math.max(this.aimDeadlineTick, max);
                  } else {
                     if (this.cooldown != CooldownEscape.State.AIM || this.vecData == null || this.accept || canPitchYaw(this.vecData, vecData)) {
                        this.aimStuckTime = aimStuckTime;
                        this.aimTimeoutValue = this.getPitchYaw(vecData);
                        this.crystalSelectValue = 0;
                     }

                     this.vecData = vecData;
                     this.accept = false;
                     this.acceptDeflectAmount = cooldownAimLimit > 0.0 && Double.isFinite(cooldownAimLimit) ? cooldownAimLimit : 1.5;
                     this.aimDeadlineTick = max;
                     this.cooldown = CooldownEscape.State.AIM;
                  }
               }
            }
         }
      }
   }

   public static boolean canDistance(Vec3d vec, Vec3d vec2, double distanceLimit) {
      if (vec == null || vec2 == null || !(distanceLimit > 0.0) || Double.isInfinite(distanceLimit)) {
         return false;
      } else {
         return canFlag(vec) && canFlag(vec2) ? vec.distanceTo(vec2) <= distanceLimit : false;
      }
   }

   private static boolean canEntityInstance(ClientPlayerEntity player, float entityValue, float entityValue2, int boundingBoxLimit) {
      Vec3d eye = FloorMathUtilsDirection.getEye(player);
      ClientWorld world = MinecraftClient.getInstance().world;
      if (canExpandEntities(world, player, eye)) {
         return true;
      } else {
         FloorMathUtilsDirection.Result boundingBox = FloorMathUtilsDirection.getBoundingBox(
            eye, getCosSin(entityValue, entityValue2, getGround(player)), false, 0, null, player, true, boundingBoxLimit
         );
         return boundingBox != null && boundingBox.entityValue >= 0 && world != null && world.getEntityById(boundingBox.entityValue) instanceof EndCrystalEntity;
      }
   }

   static int getMax(double cooldownEscapeValue) {
      return !Double.isFinite(cooldownEscapeValue) ? 8 : Math.max(8, (int)Math.ceil(cooldownEscapeValue / 20.0) + 3);
   }

   private CooldownEscape.Plan getVecLanding(ClientPlayerEntity player, long vecLandingValue) {
      if (this.vecData != null && this.vecData.landing() != null) {
         Vec3d eye = FloorMathUtilsDirection.getEye(player);
         Vec3d ground = getGround(player);
         double[] vecLandingValues = DeflectAmountsUtils.getAmounts(ground.x, ground.y, ground.z, player.isOnGround(), 1);
         Vec3d vec3d = new Vec3d(vecLandingValues[0], vecLandingValues[1], vecLandingValues[2]);
         FloorMathUtilsDirection.Result vecDirection = FloorMathUtilsDirection.getVecDirection(eye, getCosSin(this.vecData.yaw(), this.vecData.pitch(), vec3d), 200);
         if (vecDirection != null && vecDirection.movedFlag && vecDirection.vec3d3VecMoved != null && canDistance(vecDirection.vec3d3VecMoved, this.vecData.landing(), this.acceptDeflectAmount * 0.5)) {
            return null;
         } else {
            CenterVecData.PearlShot vecDistance = DeflectZone.getVecDistance(
               eye,
               vecLandingValues,
               this.vecData.landing(),
               Integer.MAX_VALUE,
               this.acceptDeflectAmount,
               vecLandingValue,
               this.vecData.why(),
               Math.max(-89.0, this.vecData.pitch() - 25.0),
               Math.min(89.0, this.vecData.pitch() + 25.0),
               this.vecData.pitch()
            );
            return vecDistance != null && canDistance(vecDistance.landing(), this.vecData.landing(), this.acceptDeflectAmount)
               ? new CooldownEscape.Plan(vecDistance.yaw(), vecDistance.pitch(), this.vecData.landing(), this.vecData.why())
               : null;
         }
      } else {
         return null;
      }
   }

   private static BlockPos getFlooredCollision(ClientWorld world, Vec3d vec) {
      BlockPos floored = BlockPos.ofFloored(vec);
      if (!world.getBlockState(floored).getCollisionShape(world, floored).isEmpty()) {
         floored = floored.up();
      }

      int downState = BlockHazardFlags.countOpenBlocksBelow(world, floored, 4);
      if (downState > 3) {
         return null;
      } else {
         BlockPos down = floored.down(downState);
         if (!BlockHazardFlags.isSafeToStand(world, down)) {
            return null;
         } else {
            int stateBlock = BlockHazardFlags.getExtendedHazardFlags(world, down);
            return (stateBlock & 351) != 0 ? null : down;
         }
      }
   }

   private void updateAimStuck() {
      this.vecData = null;
      this.aimStuckValue = 0;
      this.crystalSelectValue = 0;
      this.crystalDeflectTime = Long.MIN_VALUE;
      this.accept = false;
      this.aimDeadlineTick = Long.MAX_VALUE;
      this.aimStuckTime = Long.MIN_VALUE;
      this.aimTimeoutValue = 8;
      this.escapeDistance = 0.0;
      this.entityPosCooldownAim = null;
      this.pearlTime = Long.MIN_VALUE;
   }

   public CooldownEscape.State getCooldownAim() {
      MinecraftClient instance = MinecraftClient.getInstance();
      ClientPlayerEntity player = instance.player;
      long aimStuckTime = ClientThreadUtils.getTime();
      this.cooldownAimText = this.landingCooldownValue == aimStuckTime ? this.landingCooldownText : "-";
      if (player != null && instance.world != null && instance.interactionManager != null) {
         if (this.cooldown == CooldownEscape.State.THROWN || this.cooldown == CooldownEscape.State.FAILED) {
            this.cooldown = CooldownEscape.State.IDLE;
         }

         if (this.cooldown == CooldownEscape.State.IDLE
            && this.vecData == null
            && this.escapeLanding != null
            && aimStuckTime - this.pearlLandingTime <= 4L
            && aimStuckTime >= this.pearlLandingTime
            && aimStuckTime >= this.cooldownEscapingTime) {
            this.vecData = this.escapeLanding;
            this.accept = true;
            this.acceptDeflectAmount = 2.5;
            this.aimDeadlineTick = this.pearlLandingTime + 4L + 8L;
            this.escapeDistance = this.cooldownAimAmount;
            this.entityPosCooldownAim = player.getEntityPos();
            this.aimStuckTime = aimStuckTime;
            this.aimTimeoutValue = 8;
            this.cooldown = CooldownEscape.State.AIM;
         }

         if (this.cooldown != CooldownEscape.State.AIM || this.vecData == null) {
            this.cooldown = CooldownEscape.State.IDLE;
            return CooldownEscape.State.IDLE;
         } else if (aimStuckTime > this.aimDeadlineTick) {
            this.updateAimStuck();
            return CooldownEscape.State.IDLE;
         } else if (++this.aimStuckValue > 12) {
            return this.getCooldownEscaping(aimStuckTime, "aim stuck");
         } else if (aimStuckTime - this.aimStuckTime > this.aimTimeoutValue) {
            return this.getCooldownEscaping(aimStuckTime, "aim timeout");
         } else {
            int selectedSlot = this.inventorySelected.findSlotMatching(stack -> stack.isOf(Items.ENDER_PEARL), false);
            if (selectedSlot < 0) {
               return this.getCooldownEscaping(aimStuckTime, "no pearl");
            } else {
               if (this.accept
                  && this.entityPosCooldownAim != null
                  && player.getEntityPos().distanceTo(this.entityPosCooldownAim) > 0.6
                  && (this.pearlTime == Long.MIN_VALUE || aimStuckTime - this.pearlTime >= 2L)) {
                  this.pearlTime = aimStuckTime;
                  CooldownEscape.Plan vecData = this.getVecEscape(instance.world, player, aimStuckTime);
                  if (vecData != null) {
                     this.vecData = vecData;
                     this.entityPosCooldownAim = player.getEntityPos();
                  }
               }

               if (!this.accept) {
                  CooldownEscape.Plan vecLanding = this.getVecLanding(player, aimStuckTime);
                  if (vecLanding != null) {
                     this.vecData = vecLanding;
                  }
               }

               this.trackFlick.requestSmoothedRotation(BotActionType.PEARL, this.vecData.yaw(), this.vecData.pitch(), TrackFlick.Speed.FLICK, 90);
               ItemStack stack2 = player.getInventory().getStack(selectedSlot);
               if (!stack2.isOf(Items.ENDER_PEARL) || player.getItemCooldownManager().isCoolingDown(stack2)) {
                  return this.getAimText("cooldown");
               } else if (!this.cooldownCooldownAim.isSwitchAllowed() || !this.cooldownCooldownAim.isRotationSynced()) {
                  return this.getAimText("gates");
               } else if (!this.accept && !this.isAbsorptionPearls()) {
                  this.updateAimStuck();
                  return CooldownEscape.State.IDLE;
               } else {
                  Vec3d vecMoved = getVecMoved(player, this.trackFlick.getCurrentYaw(), this.trackFlick.getCurrentPitch(), this.accept ? 160 : 200);
                  if (!canDistance(vecMoved, this.vecData.landing(), this.acceptDeflectAmount)) {
                     return this.getAimText("accept");
                  } else if (this.accept && this.canVecMoved(player, instance.world, aimStuckTime, this.trackFlick.getCurrentYaw(), this.trackFlick.getCurrentPitch())) {
                     return this.getAimText("deflect");
                  } else if (canEntityInstance(player, this.trackFlick.getCurrentYaw(), this.trackFlick.getCurrentPitch(), this.accept ? 160 : 200)) {
                     this.crystalSelectValue = getValue(this.crystalSelectValue, this.crystalDeflectTime, aimStuckTime);
                     this.crystalDeflectTime = aimStuckTime;
                     return this.crystalSelectValue >= 2 ? this.getCooldownEscaping(aimStuckTime, "crystal") : this.getAimText("crystal");
                  } else {
                     this.crystalSelectValue = 0;
                     if (!this.inventorySelected.trySelectSlot(selectedSlot, aimStuckTime)) {
                        return this.getAimText("select");
                     } else {
                        ActionResult actionResult;
                        try (HandswapGate.SwitchHandle handler = this.cooldownCooldownAim.acquireHandle()) {
                           actionResult = instance.interactionManager.interactItem(player, Hand.MAIN_HAND);
                        }

                        if (actionResult instanceof Success success && success.swingSource() == SwingSource.CLIENT) {
                           player.swingHand(Hand.MAIN_HAND);
                        }

                        this.cooldownTime = aimStuckTime;
                        this.updateAimStuck();
                        this.aimStuckValue = 0;
                        this.cooldownAimText = "-";
                        this.escapeLanding = null;
                        this.pearlLandingTime = Long.MIN_VALUE;
                        this.cooldown = CooldownEscape.State.THROWN;
                        return CooldownEscape.State.THROWN;
                     }
                  }
               }
            }
         }
      } else {
         this.updateAimStuck();
         return CooldownEscape.State.IDLE;
      }
   }

   public void setIntFunction(IntFunction<AmountConstTime> intFunction) {
      this.intFunction = intFunction;
   }

   private CooldownEscape.Candidate getVecMoved(ClientWorld world, Vec3d vec, Vec3d vec2, float degrees, double vecMovedFactor) {
      double angle = Math.toRadians(degrees);
      double sin = -Math.sin(angle);
      double cos = Math.cos(angle);
      Vec3d vec3d = new Vec3d(vec.x + sin * vecMovedFactor, vec.y - 1.52, vec.z + cos * vecMovedFactor);
      float vecMovedValue = FloorMathUtilsDirection.getAmount(vec, vec3d, 1.5, 0.03);
      double zoneEscapeValue = Float.isFinite(vecMovedValue) ? Math.max(-38.0, Math.min(30.0, (double)vecMovedValue)) : 0.0;
      Vec3d[] vec3ds = new Vec3d[1];
      double[] doubles = new double[]{Double.POSITIVE_INFINITY};
      double[] doubles2 = new double[]{Double.NaN};
      int[] ints = new int[]{0};
      ZoneEscapeUtils2.Miss miss = yawx -> {
         if (this.vecMovedCount >= 72) {
            return Double.POSITIVE_INFINITY;
         } else {
            this.vecMovedCount++;
            FloorMathUtilsDirection.Result vecDirection = FloorMathUtilsDirection.getVecDirection(vec, getCosSin(degrees, (float)yawx, vec2), 160);
            if (vecDirection != null && vecDirection.movedFlag && vecDirection.vec3d3VecMoved != null) {
               Vec3d var17x = vecDirection.vec3d3VecMoved.subtract(vec);
               double var18x = var17x.x * sin + var17x.z * cos - vecMovedFactor;
               if (Math.abs(var18x) < doubles[0]) {
                  doubles[0] = Math.abs(var18x);
                  vec3ds[0] = vecDirection.vec3d3VecMoved;
                  doubles2[0] = yawx;
                  ints[0] = vecDirection.flightTicks;
               }

               return var18x;
            } else {
               return Double.POSITIVE_INFINITY;
            }
         }
      };
      double vecHashValue = ZoneEscapeUtils2.getAmount(miss, zoneEscapeValue, -38.0, 30.0, 1.0, 4);
      if (Double.isNaN(vecHashValue)) {
         vecHashValue = doubles2[0];
      }

      if (!Double.isNaN(vecHashValue) && vec3ds[0] != null) {
         Vec3d vec3d2 = vec3ds[0];
         int vecMovedBound = ints[0];
         if (Double.isNaN(doubles2[0]) || Math.abs(doubles2[0] - vecHashValue) > 1.0E-6) {
            if (this.vecMovedCount >= 72) {
               return null;
            }

            this.vecMovedCount++;
            FloorMathUtilsDirection.Result vecDirection2 = FloorMathUtilsDirection.getVecDirection(vec, getCosSin(degrees, (float)vecHashValue, vec2), 160);
            if (vecDirection2 == null || !vecDirection2.movedFlag || vecDirection2.vec3d3VecMoved == null) {
               return null;
            }

            vec3d2 = vecDirection2.vec3d3VecMoved;
            vecMovedBound = vecDirection2.flightTicks;
         }

         BlockPos flooredCollision = getFlooredCollision(world, vec3d2);
         return flooredCollision == null ? null : new CooldownEscape.Candidate((float)vecHashValue, vec3d2, flooredCollision, Math.max(1, vecMovedBound));
      } else {
         return null;
      }
   }

   public CaseMaxData getPearlLanding(SelfPopData selfPopData) {
      if (selfPopData != null) {
         this.absorptionPosData = selfPopData.self();
         this.pearlLandingPearlsPlaystyleD = selfPopData.cfg();
         this.pearlLandingActive = selfPopData.getSelfPop().ref() && selfPopData.enemiesWithin8() < 2;
         this.targetId = selfPopData.target() == null ? -1 : selfPopData.target().id();
      }

      long pearlLandingTime = ClientThreadUtils.getTime();
      if (this.cooldown == CooldownEscape.State.AIM && this.vecData != null) {
         return new CaseMaxData(SurroundKitType.PEARL, true, this.accept ? this.escapeLandingAmount : 0.0, 0, false, false, this.vecData.why());
      } else if (pearlLandingTime < this.cooldownEscapingTime) {
         return CaseMaxData.getCaseMaxData(SurroundKitType.PEARL, "cooldown");
      } else if (selfPopData == null || selfPopData.lastPosture() != DefensiveAggressiveType.ESCAPE) {
         return CaseMaxData.getCaseMaxData(SurroundKitType.PEARL, "not escaping");
      } else if (selfPopData.cfg() != null && !selfPopData.cfg().escapePearls()) {
         return CaseMaxData.getCaseMaxData(SurroundKitType.PEARL, "off");
      } else if (!this.isCooldownCooling()) {
         return CaseMaxData.getCaseMaxData(SurroundKitType.PEARL, "no pearl");
      } else {
         ClientPlayerEntity player = MinecraftClient.getInstance().player;
         Vec3d entityPos = player == null ? null : player.getEntityPos();
         boolean pearlLandingFlag = this.pearlLandingTime != Long.MIN_VALUE
            && pearlLandingTime - this.pearlLandingTime < 4L
            && pearlLandingTime >= this.pearlLandingTime
            && this.entityPos != null
            && entityPos != null
            && this.entityPos.distanceTo(entityPos) < 1.0;
         if (!pearlLandingFlag) {
            this.escapeLanding = this.getEscapeLanding(selfPopData);
            this.pearlLandingTime = pearlLandingTime;
            this.entityPos = entityPos;
         }

         return this.escapeLanding == null
            ? CaseMaxData.getCaseMaxData(SurroundKitType.PEARL, this.landingEscapeText == null ? "no landing" : this.landingEscapeText)
            : new CaseMaxData(SurroundKitType.PEARL, true, this.escapeLandingAmount, 2, false, false, "escape");
      }
   }

   public CooldownEscape.State getCooldown() {
      return this.cooldown;
   }

   static boolean canFlag(AmountConstTime.Spot spot, double cooldownEscapeValue, double constDeflectValue, double cooldownEscapeValue3) {
      if (spot == null) {
         return false;
      } else {
         double cooldownEscapeFactor = cooldownEscapeValue - spot.getX();
         double cooldownEscapeFactor2 = constDeflectValue - spot.getY();
         double cooldownEscapeFactor3 = cooldownEscapeValue3 - spot.getZ();
         return HeightDeltaMath.getDistanceWeightedReachFraction(constDeflectValue, Math.sqrt(cooldownEscapeFactor * cooldownEscapeFactor + cooldownEscapeFactor2 * cooldownEscapeFactor2 + cooldownEscapeFactor3 * cooldownEscapeFactor3), spot.getAmount()) >= 0.25;
      }
   }

   private boolean canAmount(World world, Vec3d vec, Vec3d vec2, Vec3d vec3, double cooldownEscapeValue, int cooldownEscapeLimit) {
      AmountConstTime.Spot spot = this.functionIntervalSpot;
      if (spot == null || vec3 == null) {
         return false;
      } else if (canFlag(spot, vec3.x, cooldownEscapeValue, vec3.z)) {
         this.escapeLandingCount++;
         return true;
      } else if (ZoneEscapeUtilsFlag.canFlag(getAmount(vec, vec2, vec3.y, cooldownEscapeLimit, spot, this.functionIntervalValue, this.functionIntervalAmount, this.getInstance(world)))) {
         this.cooldownEscapeCount++;
         return true;
      } else {
         return false;
      }
   }

   public boolean isCooldownTime() {
      return this.cooldownTime == ClientThreadUtils.getTime();
   }

   private void handleFunctionInterval(long functionIntervalValue, int functionIntervalLimit) {
      this.escapeLandingCount = 0;
      this.cooldownEscapeCount = 0;
      this.functionIntervalCount = 16;
      IntFunction intFunction = this.intFunction;
      AmountConstTime amountConstTime = intFunction != null && functionIntervalLimit >= 0 ? (AmountConstTime)intFunction.apply(functionIntervalLimit) : null;
      AmountConstTime.Spot data = amountConstTime == null ? null : amountConstTime.getTimeAmount(functionIntervalValue);
      this.functionIntervalSpot = data;
      this.functionIntervalValue = data == null ? 0 : amountConstTime.getMin(functionIntervalValue) - 2;
      this.functionIntervalAmount = data == null ? 0.0 : data.interval();
   }

   static boolean canPitchYaw(CooldownEscape.Plan plan, CooldownEscape.Plan plan2) {
      return plan != null && plan2 != null ? !(BoxLengthUtils.getAmount(plan.yaw(), plan.pitch(), plan2.yaw(), plan2.pitch()) <= 30.0) : true;
   }

   private static boolean canFlag(Vec3d vec) {
      return Double.isFinite(vec.x) && Double.isFinite(vec.y) && Double.isFinite(vec.z);
   }

   public void updateLandingCooldown() {
      this.updateAimStuck();
      this.cooldown = CooldownEscape.State.IDLE;
      this.cooldownEscapingTime = Long.MIN_VALUE;
      this.cooldownTime = Long.MIN_VALUE;
      this.lastUpdateTick = Long.MIN_VALUE;
      this.cooldownEscapingText = null;
      this.escapeLanding = null;
      this.pearlLandingTime = Long.MIN_VALUE;
      this.entityPos = null;
      this.escapeLandingAmount = 0.0;
      this.absorptionPosData = null;
      this.pearlLandingPearlsPlaystyleD = null;
      this.targetId = -1;
      this.functionIntervalSpot = null;
      this.landingEscapeText = null;
      this.cooldownAimText = "-";
      this.landingCooldownText = "-";
      this.landingCooldownValue = Long.MIN_VALUE;
   }

   private CooldownEscape.Plan getVecEscape(ClientWorld world, ClientPlayerEntity player, long vecEscapeValue) {
      if (this.vecData != null && world != null && this.escapeDistance > 0.0) {
         this.vecMovedCount = 0;
         Vec3d eye = FloorMathUtilsDirection.getEye(player);
         Vec3d ground = getGround(player);
         CooldownEscape.Candidate vecMoved = this.getVecMoved(world, eye, ground, this.vecData.yaw(), this.escapeDistance);
         if (vecMoved == null) {
            return null;
         } else {
            double flooredBottom = getFlooredBottom(world, vecMoved.landing, vecMoved.stand, getPosCreative(world, player, null));
            if (Double.isNaN(flooredBottom)) {
               return null;
            } else {
               this.handleFunctionInterval(vecEscapeValue, this.targetId);
               return this.canAmount(world, eye, getCosSin(this.vecData.yaw(), vecMoved.pitch, ground), vecMoved.landing, vecMoved.stand.getY(), vecMoved.flight)
                  ? null
                  : new CooldownEscape.Plan(this.vecData.yaw(), vecMoved.pitch, vecMoved.landing, "escape");
            }
         }
      } else {
         return null;
      }
   }

   public CooldownEscape.Plan getVecDataPlan() {
      return this.vecData;
   }

   static Vec3d getCosSin(float cosSinFactor, float cosSinFactor2, Vec3d vec) {
      float x = -MathHelper.sin(cosSinFactor * (float) (Math.PI / 180.0)) * MathHelper.cos(cosSinFactor2 * (float) (Math.PI / 180.0));
      float sin = -MathHelper.sin(cosSinFactor2 * (float) (Math.PI / 180.0));
      float z = MathHelper.cos(cosSinFactor * (float) (Math.PI / 180.0)) * MathHelper.cos(cosSinFactor2 * (float) (Math.PI / 180.0));
      Vec3d multiply = new Vec3d(x, sin, z).normalize().multiply(1.5);
      return vec == null ? multiply : multiply.add(vec);
   }

   private CooldownEscape.State getAimText(String text) {
      this.cooldownAimText = text;
      return CooldownEscape.State.AIM;
   }

   static boolean canLandingWhy(CooldownEscape.Plan plan, CooldownEscape.Plan plan2, double landingWhyLimit) {
      if (plan != null && plan2 != null && plan.landing() != null && plan2.landing() != null) {
         return !Objects.equals(plan2.why(), plan.why()) ? false : plan2.landing().distanceTo(plan.landing()) <= landingWhyLimit;
      } else {
         return false;
      }
   }

   private static List<Float> getItems(float cooldownEscapeValue) {
      ArrayList arrayList = new ArrayList();
      ArrayList supplier = new ArrayList();
      double cooldownEscapeFactor = 22.5;
      ArrayList arrayList3 = new ArrayList();

      for (int i = 0; i < 16; i++) {
         double cooldownEscapeLimit = i * cooldownEscapeFactor;
         double spotValue = cooldownEscapeLimit > 180.0 ? cooldownEscapeLimit - 360.0 : cooldownEscapeLimit;
         arrayList3.add(new double[]{spotValue, Math.abs(spotValue)});
      }

      arrayList3.sort((obj, obj2) -> Double.compare(((double[]) obj)[1], ((double[]) obj2)[1]));

      for (double[] cooldownEscapeValues : (Iterable<double[]>) (arrayList3)) {
         float totalPriceData = (float)(cooldownEscapeValue + cooldownEscapeValues[0]);
         if (cooldownEscapeValues[1] <= 70.0) {
            arrayList.add(totalPriceData);
         } else {
            supplier.add(totalPriceData);
         }
      }

      arrayList.addAll(supplier);
      return arrayList;
   }

   private record Candidate(float pitch, Vec3d landing, BlockPos stand, int flight) {
   }

   public record Plan(float yaw, float pitch, Vec3d landing, String why) {
   }

   public static enum State {
      IDLE,
      AIM,
      THROWN,
      FAILED;
   }
}
