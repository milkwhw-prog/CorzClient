package com.corz.client.categories.bots;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.EnumSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.gui.Button;
import com.corz.client.gui.ZoomHud;
import com.corz.client.utils.ScissorTgtHandler.Snapshot;
import com.corz.client.world.ThreadClientHandlerHandle;
import net.minecraft.util.math.Vec3d;
import com.corz.client.utils.SwordBotConfig;
import com.corz.client.utils.SwordPlayer;
import com.corz.client.utils.AutoVanish;
import com.corz.client.utils.InventoryPlayer;
import ai.auth.Module;
import ai.auth.ModuleRegistry;
import com.corz.client.utils.DeltaAngle;
import com.corz.client.utils.IconsPngType;
import com.corz.client.utils.PlayerScaffold;
import com.corz.client.utils.ScissorTgtHandler;
import com.corz.client.utils.SwordBot;
import com.corz.client.utils.WeaknessGodApi;

public class SwordPvpBot extends Module implements WeaknessGodApi, ThreadClientHandlerHandle, ScissorTgtHandler {
   private final SwordPlayer swordPlayer;
   private final BooleanSetting strafeSetting;
   private final BooleanSetting jumpResetSetting;
   private final IntegerSetting fightRangeSetting;
   private final BooleanSetting critHitsSetting;
   private final EnumSetting playstyleSetting = new EnumSetting(
      "Playstyle",
      "Untouchable keeps just outside the enemy's reach and only steps in to hit first. Aggressive stands closer and crits more.",
      "Untouchable",
      "Untouchable",
      "Aggressive"
   );
   private final EnumSetting turnSpeedSetting;

   public SwordPvpBot() {
      super("Sword PvP Bot", IconsPngType.BOTS);
      this.fightRangeSetting = new IntegerSetting(
         "Fight Range", "How far away the bot looks for a player to fight. It keeps chasing a player it is already fighting.", 16, 6, 32, 1
      );
      this.turnSpeedSetting = new EnumSetting("Turn Speed", "How fast the bot turns your view. Smooth looks the most human.", "Normal", "Smooth", "Normal", "Fast");
      this.strafeSetting = new BooleanSetting("Strafe", "Circles left and right around the enemy so their hits miss.", true);
      this.jumpResetSetting = new BooleanSetting("Jump Reset", "Jumps the moment you get hit so you take less knockback.", true);
      this.critHitsSetting = new BooleanSetting("Crit Hits", "Jump-crits an enemy who runs away or turns their back. Never when they can hit you.", true);
      this.addSettings(new ConfigSetting[]{this.playstyleSetting, this.fightRangeSetting, this.turnSpeedSetting, this.strafeSetting, this.jumpResetSetting, this.critHitsSetting});
      this.swordPlayer = new SwordPlayer(this);
   }

   @Override
   protected void onPlayerScaffold(PlayerScaffold playerScaffold) {
      this.swordPlayer.onMouseMove(playerScaffold);
   }

   public static float getTurnSpeedMultiplier(String fastSmoothText) {
      if ("Smooth".equalsIgnoreCase(fastSmoothText)) {
         return 0.75F;
      } else {
         return "Fast".equalsIgnoreCase(fastSmoothText) ? 1.15F : 1.0F;
      }
   }

   @Override
   protected void onSwordBot(SwordBot swordBot) {
      this.swordPlayer.onKeyInput(swordBot);
   }

   public static boolean isSwordBotActive() {
      return ModuleRegistry.getModule(SwordPvpBot.class) instanceof SwordPvpBot swordPvpBot && swordPvpBot.isEnabled() && swordPvpBot.swordPlayer.isBotControlling();
   }

   @Override
   protected void onPacketReceive(AutoVanish autoVanish) {
      this.swordPlayer.onIncomingPacket(autoVanish);
   }

   @Override
   protected void onCameraAngle(DeltaAngle deltaAngle) {
      this.swordPlayer.onMouseDelta(deltaAngle);
   }

   public SwordBotConfig buildSettings() {
      return new SwordBotConfig(
         "Aggressive".equalsIgnoreCase(this.playstyleSetting.getCurrentOption()),
         this.fightRangeSetting.getValue(),
         getTurnSpeedMultiplier(this.turnSpeedSetting.getCurrentOption()),
         this.strafeSetting.isEnabled(),
         this.jumpResetSetting.isEnabled(),
         this.critHitsSetting.isEnabled()
      );
   }

   @Override
   protected void onEnable() {
      this.swordPlayer.resetAndShowWaiting();
   }

   @Override
   public boolean isGodWeaknessWaitingPlayer() {
      return this.isEnabled() && this.swordPlayer.isTargetLocked();
   }

   @Override
   protected void onZoomHud(ZoomHud zoomHud) {
      this.swordPlayer.onZoomInput(zoomHud);
   }

   @Override
   protected void onReset() {
      this.swordPlayer.onClientTick();
   }

   @Override
   protected void onButtonClick(Button button) {
      this.swordPlayer.onMouseButtonInput(button);
   }

   @Override
   protected void onPacketSend(InventoryPlayer inventoryPlayer) {
      this.swordPlayer.trackOutgoingPacket(inventoryPlayer);
   }

   @Override
   public Snapshot getStatusSnapshot() {
      if (!this.isEnabled()) {
         return Snapshot.INSTANCE;
      } else {
         Snapshot menuTurnData = this.swordPlayer.getUiSnapshot();
         return menuTurnData == null ? Snapshot.INSTANCE : menuTurnData;
      }
   }

   @Override
   protected void onDisable() {
      this.swordPlayer.onBotDisabled();
   }

   @Override
   public void handlePlayerReduction(Vec3d vec, boolean playerReductionFlag) {
      if (this.isEnabled()) {
         this.swordPlayer.recordHookTimeFromVec(vec, playerReductionFlag);
      }
   }
}
