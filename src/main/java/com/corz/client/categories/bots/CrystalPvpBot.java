package com.corz.client.categories.bots;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.EnumSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.gui.Button;
import com.corz.client.gui.ZoomHud;
import com.corz.client.render.QueueCommand;
import com.corz.client.utils.ScissorTgtHandler.Snapshot;
import com.corz.client.world.ThreadClientHandlerHandle;
import net.minecraft.util.math.Vec3d;
import com.corz.client.utils.CrystalPvpTickController;
import com.corz.client.utils.AutoVanish;
import com.corz.client.utils.InventoryPlayer;
import ai.auth.Module;
import ai.auth.ModuleRegistry;
import com.corz.client.utils.CarefulBalancedType;
import com.corz.client.utils.DeltaAngle;
import com.corz.client.utils.IconsPngType;
import com.corz.client.utils.PearlsPlaystyleData;
import com.corz.client.utils.PlayerScaffold;
import com.corz.client.utils.ScissorTgtHandler;
import com.corz.client.utils.SwordBot;
import com.corz.client.utils.WeaknessGodApi;

public class CrystalPvpBot extends Module implements WeaknessGodApi, ThreadClientHandlerHandle, ScissorTgtHandler {
   private final BooleanSetting combosSetting;
   private final CrystalPvpTickController waitingPlayer;
   private final BooleanSetting aggroPearls;
   private final BooleanSetting protectLoot;
   private final BooleanSetting useAnchors;
   private final BooleanSetting learnOpponents;
   private final EnumSetting turnSpeedSetting;
   private final BooleanSetting buildObsidian;
   private final BooleanSetting escapePearls;
   private final EnumSetting playstyle = new EnumSetting(
      "Playstyle",
      "Terminator never lets go: it chases, pearls after the enemy and keeps them in the air. Balanced picks its moments. Careful backs off and heals sooner. Every style keeps the same safety checks.",
      "Terminator",
      "Terminator",
      "Balanced",
      "Careful"
   );
   private final IntegerSetting fightRangeSetting = new IntegerSetting(
      "Fight Range", "How far away the bot looks for a player to fight. It keeps chasing a player it is already fighting.", 16, 8, 32, 1
   );
   private final BooleanSetting eatGapples;

   @Override
   protected void onPacketSend(InventoryPlayer inventoryPlayer) {
      this.waitingPlayer.onSendPacket(inventoryPlayer);
   }

   public CrystalPvpBot() {
      super("Crystal PvP Bot", IconsPngType.BOTS);
      this.turnSpeedSetting = new EnumSetting("Turn Speed", "How fast the bot turns your view. Smooth looks the most human.", "Normal", "Smooth", "Normal", "Fast");
      this.combosSetting = new BooleanSetting("Combos", "Knocks players up with your sword, then keeps them in the air with obsidian and crystals.", true);
      this.aggroPearls = new BooleanSetting(
         "Aggro Pearls", "Throws ender pearls after the enemy so they cannot run from a combo. Keeps a pearl for escaping.", true
      );
      this.useAnchors = new BooleanSetting(
         "Use Anchors", "Uses respawn anchors from one block below the enemy so the blast misses you. Never in the Nether.", true
      );
      this.buildObsidian = new BooleanSetting("Build Obsidian", "Places obsidian to make crystal spots under and around the enemy.", true);
      this.eatGapples = new BooleanSetting("Eat Gapples", "Eats golden apples only when your health is low and no enemy is close.", true);
      this.escapePearls = new BooleanSetting("Escape Pearls", "Throws an ender pearl to get away when you are about to lose.", true);
      this.learnOpponents = new BooleanSetting(
         "Learn Opponents", "Remembers how each player moves and fights (saved on this PC only) and adapts to them. Never takes more risk.", true
      );
      this.protectLoot = new BooleanSetting("Protect Loot", "Never blows up dropped totems, armor or tools.", true);
      this.addSettings(
         new ConfigSetting[]{
            this.playstyle,
            this.fightRangeSetting,
            this.turnSpeedSetting,
            this.combosSetting,
            this.aggroPearls,
            this.useAnchors,
            this.buildObsidian,
            this.eatGapples,
            this.escapePearls,
            this.learnOpponents,
            this.protectLoot
         }
      );
      this.waitingPlayer = new CrystalPvpTickController(this);
   }

   @Override
   protected void onPacketReceive(AutoVanish autoVanish) {
      this.waitingPlayer.onReceivePacket(autoVanish);
   }

   @Override
   protected void onZoomHud(ZoomHud zoomHud) {
      this.waitingPlayer.onZoomHudInput(zoomHud);
   }

   public PearlsPlaystyleData getPlaystyleConfig() {
      return new PearlsPlaystyleData(
         CarefulBalancedType.getBalancedCareful(this.playstyle.getCurrentOption()),
         this.fightRangeSetting.getValue(),
         getTurnSpeedMultiplier(this.turnSpeedSetting.getCurrentOption()),
         this.combosSetting.isEnabled(),
         this.aggroPearls.isEnabled(),
         this.useAnchors.isEnabled(),
         this.buildObsidian.isEnabled(),
         this.eatGapples.isEnabled(),
         this.escapePearls.isEnabled(),
         this.learnOpponents.isEnabled(),
         this.protectLoot.isEnabled()
      );
   }

   @Override
   protected void onButtonClick(Button button) {
      this.waitingPlayer.onMouseButtonInput(button);
   }

   @Override
   public boolean isReductionCritical() {
      return this.isEnabled() && this.waitingPlayer.isEngaged();
   }

   public static void ignoreValues(double crystalPvpValue, double crystalPvpValue2) {
   }

   public static boolean isWaitingPlayer() {
      return ModuleRegistry.getModule(CrystalPvpBot.class) instanceof CrystalPvpBot crystalPvpBot && crystalPvpBot.isEnabled() && crystalPvpBot.waitingPlayer.isEngagedWithoutRecentInput();
   }

   @Override
   protected void onEnable() {
      this.waitingPlayer.onInactiveTick();
   }

   @Override
   protected void onSwordBot(SwordBot swordBot) {
      this.waitingPlayer.onKeyInput(swordBot);
   }

   @Override
   protected void onReset() {
      this.waitingPlayer.onClientTick();
   }

   @Override
   public Snapshot getStatusSnapshot() {
      if (!this.isEnabled()) {
         return Snapshot.INSTANCE;
      } else {
         Snapshot menuTurnData = this.waitingPlayer.getStatusSnapshot();
         return menuTurnData == null ? Snapshot.INSTANCE : menuTurnData;
      }
   }

   @Override
   protected void onDisable() {
      this.waitingPlayer.onDisable();
   }

   @Override
   public boolean isGodWeakness() {
      return this.isEnabled() && this.waitingPlayer.isEngagedNearTarget();
   }

   @Override
   public void handlePlayerReduction(Vec3d vec, boolean playerReductionFlag) {
      if (this.isEnabled()) {
         this.waitingPlayer.updateMovementTarget(vec, playerReductionFlag);
      }
   }

   @Override
   public boolean isAgePlayer() {
      return this.isEnabled() && this.waitingPlayer.isEngaged();
   }

   @Override
   protected void onRender3D(QueueCommand queueCommand) {
      this.waitingPlayer.onQueueCommand(queueCommand);
   }

   public static float getTurnSpeedMultiplier(String fastSmoothText) {
      if ("Smooth".equalsIgnoreCase(fastSmoothText)) {
         return 0.75F;
      } else {
         return "Fast".equalsIgnoreCase(fastSmoothText) ? 1.15F : 1.0F;
      }
   }

   @Override
   public boolean isGodWeaknessWaitingPlayer() {
      return this.isEnabled() && this.waitingPlayer.isEngaged();
   }

   @Override
   protected void onCameraAngle(DeltaAngle deltaAngle) {
      this.waitingPlayer.onMouseDelta(deltaAngle);
   }

   @Override
   protected void onPlayerScaffold(PlayerScaffold playerScaffold) {
      this.waitingPlayer.onMouseMove(playerScaffold);
   }
}
