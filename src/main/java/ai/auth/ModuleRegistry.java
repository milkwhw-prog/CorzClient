package ai.auth;

import com.corz.client.entity.ClientThreadUtils;
import com.corz.client.gui.Button;
import com.corz.client.gui.TickProgress;
import com.corz.client.gui.ZoomHud;
import com.corz.client.render.QueueCommand;
import com.corz.client.render.VertexBuilderUtils;
import com.corz.client.world.MathClampUtils;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.util.math.MatrixStack;
import com.corz.client.categories.bots.AutoElytraPilot;
import com.corz.client.categories.bots.AutoTpaHere;
import com.corz.client.categories.bots.CrystalPvpBot;
import com.corz.client.utils.PlayerInputOverride;
import com.corz.client.categories.bots.SwordPvpBot;
import com.corz.client.categories.combat.AimAssist;
import com.corz.client.categories.combat.AnchorMacro;
import com.corz.client.categories.combat.AutoAggroPearl;
import com.corz.client.categories.combat.AutoCart;
import com.corz.client.categories.combat.AutoClicker;
import com.corz.client.categories.combat.AutoHitCrystal;
import com.corz.client.categories.combat.AutoHotbar;
import com.corz.client.categories.combat.AutoInventoryTotem;
import com.corz.client.categories.combat.AutoJumpReset;
import com.corz.client.categories.combat.AutoShieldBreaker;
import com.corz.client.categories.combat.AutoTotem;
import com.corz.client.categories.combat.AutoTrap;
import com.corz.client.categories.combat.AutoXP;
import com.corz.client.categories.combat.Criticals;
import com.corz.client.categories.combat.CrystalAura;
import com.corz.client.categories.combat.CrystalOptimizer;
import com.corz.client.categories.combat.HitBoxes;
import com.corz.client.categories.combat.HitSelect;
import com.corz.client.categories.combat.HitSwap;
import com.corz.client.categories.combat.KeyPearl;
import com.corz.client.categories.combat.MaceBomber;
import com.corz.client.categories.combat.ObsidianOptimizer;
import com.corz.client.categories.combat.PerfectWindCharge;
import com.corz.client.categories.combat.Reach;
import com.corz.client.categories.combat.SafeAnchor;
import com.corz.client.categories.combat.SilentAim;
import com.corz.client.categories.combat.TridentAssist;
import com.corz.client.categories.combat.TriggerBot;
import com.corz.client.categories.combat.WTap;
import com.corz.client.categories.bots.AnchorHunter;
import com.corz.client.categories.donutsmp.AntiVanish;
import com.corz.client.categories.bots.AutoAreaMiner;
import com.corz.client.categories.bots.AutoAuctionFlipper;
import com.corz.client.categories.bots.AutoBuilder;
import com.corz.client.categories.bots.AutoElytraFinder;
import com.corz.client.categories.donutsmp.AutoMine;
import com.corz.client.categories.donutsmp.AutoSell;
import com.corz.client.categories.donutsmp.AutoSellChest;
import com.corz.client.categories.donutsmp.Bloodhound;
import com.corz.client.categories.donutsmp.BoneDropper;
import com.corz.client.categories.donutsmp.ChunkFinder;
import com.corz.client.categories.donutsmp.ChunkFinderV2;
import com.corz.client.categories.donutsmp.DeepPlayerFinder;
import com.corz.client.categories.donutsmp.FakePay;
import com.corz.client.categories.donutsmp.FakeStats;
import com.corz.client.categories.donutsmp.GamblingRigger;
import com.corz.client.categories.donutsmp.NetheriteFinder;
import com.corz.client.categories.donutsmp.RegionMap;
import com.corz.client.categories.donutsmp.Relog;
import com.corz.client.categories.bots.RtpBaseAlert;
import com.corz.client.categories.donutsmp.SeedAnalyzer;
import com.corz.client.categories.donutsmp.SilentHome;
import com.corz.client.categories.bots.SpawnerProtect;
import com.corz.client.categories.donutsmp.SpecialItemBuyer;
import com.corz.client.categories.donutsmp.StaffList;
import com.corz.client.categories.donutsmp.SusChunkFinder;
import com.corz.client.categories.donutsmp.SusChunkFinderV2;
import com.corz.client.categories.bots.VillagerTradeBot;
import com.corz.client.categories.donutsmp.WanderingTraderFinder;
import com.corz.client.categories.misc.ACAlert;
import com.corz.client.categories.misc.AutoPot;
import com.corz.client.categories.misc.AutoPromo;
import com.corz.client.categories.misc.AutoTool;
import com.corz.client.categories.misc.BoatFly;
import com.corz.client.categories.misc.Capes;
import com.corz.client.utils.Cooldown;
import com.corz.client.categories.misc.CorzRadio;
import com.corz.client.categories.misc.CreamyKeys;
import com.corz.client.categories.misc.CustomCrosshair;
import com.corz.client.categories.misc.CustomSkin;
import com.corz.client.categories.misc.FOVSlider;
import com.corz.client.categories.misc.FakeCoordinates;
import com.corz.client.categories.misc.FakePlayer;
import com.corz.client.categories.misc.FastPlace;
import com.corz.client.categories.misc.Fastbreak;
import com.corz.client.categories.misc.InventoryCleaner;
import com.corz.client.categories.misc.NameProtect;
import com.corz.client.categories.misc.PotionRefill;
import com.corz.client.categories.misc.SkinProtect;
import com.corz.client.categories.misc.TimeChanger;
import com.corz.client.categories.misc.WeatherChanger;
import com.corz.client.categories.misc.WeatherNotifier;
import com.corz.client.categories.misc.Zoom;
import com.corz.client.categories.movement.AutoSpear;
import com.corz.client.categories.movement.AutoWalk;
import com.corz.client.categories.movement.Flight;
import com.corz.client.categories.movement.Freecam;
import com.corz.client.categories.movement.KeepSprint;
import com.corz.client.categories.movement.PearlCatchAssist;
import com.corz.client.categories.movement.Scaffold;
import com.corz.client.categories.movement.SpeedHack;
import com.corz.client.categories.movement.Sprint;
import com.corz.client.utils.AutoVanish;
import com.corz.client.categories.network.BackTrack;
import com.corz.client.categories.network.Blink;
import com.corz.client.utils.ConnectionTimeUtils;
import com.corz.client.utils.EntityDamageUtils;
import com.corz.client.categories.network.FakeLag;
import com.corz.client.utils.InventoryPlayer;
import com.corz.client.categories.network.KnockbackDelay;
import com.corz.client.categories.settings.ClientSounds;
import com.corz.client.categories.configs.Configs;
import com.corz.client.categories.socials.Friends;
import com.corz.client.categories.settings.GUISettings;
import com.corz.client.categories.settings.License;
import com.corz.client.categories.settings.MiddleClickFriend;
import com.corz.client.categories.settings.Notifications;
import com.corz.client.categories.settings.Panic;
import com.corz.client.categories.settings.SelfDestruct;
import com.corz.client.categories.theme.ThemeCustomizer;
import com.corz.client.categories.visuals.AtmosphericParticles;
import com.corz.client.categories.visuals.BlockESP;
import com.corz.client.categories.visuals.BlockHighlight;
import com.corz.client.categories.visuals.BlockOverlay;
import com.corz.client.categories.visuals.CustomFlame;
import com.corz.client.categories.visuals.CustomSky;
import com.corz.client.categories.visuals.CustomWorld;
import com.corz.client.categories.visuals.FireFly;
import com.corz.client.categories.visuals.Freelook;
import com.corz.client.categories.visuals.Fullbright;
import com.corz.client.categories.visuals.HandCustomer;
import com.corz.client.categories.visuals.HandShader;
import com.corz.client.categories.visuals.HeatColor;
import com.corz.client.categories.visuals.HoleESP;
import com.corz.client.categories.visuals.JumpCircles;
import com.corz.client.categories.visuals.LootESP;
import com.corz.client.categories.visuals.MobESP;
import com.corz.client.categories.visuals.MotionBlur;
import com.corz.client.categories.visuals.NameTags;
import com.corz.client.categories.visuals.NoExplosions;
import com.corz.client.categories.visuals.NoFarmLag;
import com.corz.client.categories.visuals.NoFlame;
import com.corz.client.categories.visuals.Particles;
import com.corz.client.categories.visuals.PearlCatch;
import com.corz.client.categories.visuals.PearlPrediction;
import com.corz.client.categories.visuals.PlayerESP;
import com.corz.client.categories.visuals.RealisticWater;
import com.corz.client.categories.visuals.ShowHUD;
import com.corz.client.categories.visuals.SpawnerFinder;
import com.corz.client.categories.visuals.SpotifyHUD;
import com.corz.client.categories.visuals.StorageESP;
import com.corz.client.categories.visuals.SwordInspect;
import com.corz.client.categories.visuals.TargetESP;
import com.corz.client.categories.visuals.TotemCounter;
import com.corz.client.categories.visuals.Trails;
import com.corz.client.categories.visuals.Waypoints;
import ai.auth.Module;
import com.corz.client.utils.DeltaAngle;
import com.corz.client.utils.DirExtractionUtils;
import com.corz.client.utils.ExtUtils;
import com.corz.client.utils.IconsPngType;
import com.corz.client.utils.InstanceTimeUtils;
import com.corz.client.utils.PlayerScaffold;
import com.corz.client.utils.ScaffoldTrade;
import com.corz.client.utils.SwordBot;

public class ModuleRegistry {
    private static final Map<Class<? extends Module>, Module> modulesByClass;
    private static final List<Module> modules;
    private static final Map<IconsPngType, List<Module>> modulesByCategory;

    static {
        modules = new ArrayList<>();
        modulesByCategory = new EnumMap<>(IconsPngType.class);
        modulesByClass = new IdentityHashMap<>();
    }

    private static void registerModule(Module module) {
        modules.add(module);
        modulesByClass.putIfAbsent(module.getClass(), module);
        modulesByCategory.get(module.getPageType()).add(module);
    }

    public static Module getModule(Class<? extends Module> clazz) {
        return modulesByClass.get(clazz);
    }

    public static List<Module> getModules() {
        return modules;
    }

    public static List<Module> getModulesByCategory(IconsPngType category) {
        return modulesByCategory.getOrDefault(category, new ArrayList<>());
    }

    public static void registerAllModules() {
        IconsPngType[] categories = IconsPngType.values();
        for (int i = 0; i < categories.length; ++i) {
            modulesByCategory.put(categories[i], new ArrayList<>());
        }
        registerModule(new ACAlert());
        registerModule(new AimAssist());
        registerModule(new AnchorHunter());
        registerModule(new AnchorMacro());
        registerModule(new AntiVanish());
        registerModule(new AtmosphericParticles());
        registerModule(new AutoAggroPearl());
        registerModule(new AutoAreaMiner());
        registerModule(new AutoAuctionFlipper());
        registerModule(new AutoBuilder());
        registerModule(new AutoCart());
        registerModule(new AutoClicker());
        registerModule(new AutoElytraFinder());
        registerModule(new AutoElytraPilot());
        registerModule(new AutoHitCrystal());
        registerModule(new AutoHotbar());
        registerModule(new AutoInventoryTotem());
        registerModule(new AutoJumpReset());
        registerModule(new AutoMine());
        registerModule(new AutoPot());
        registerModule(new AutoPromo());
        registerModule(new AutoSell());
        registerModule(new AutoSellChest());
        registerModule(new AutoShieldBreaker());
        registerModule(new AutoSpear());
        registerModule(new AutoTool());
        registerModule(new AutoTotem());
        registerModule(new AutoTpaHere());
        registerModule(new AutoTrap());
        registerModule(new AutoWalk());
        registerModule(new AutoXP());
        registerModule(new BackTrack());
        registerModule(new Blink());
        registerModule(new BlockESP());
        registerModule(new BlockHighlight());
        registerModule(new BlockOverlay());
        registerModule(new Bloodhound());
        registerModule(new BoatFly());
        registerModule(new BoneDropper());
        registerModule(new Capes());
        registerModule(new ChunkFinder());
        registerModule(new ChunkFinderV2());
        registerModule(new ClientSounds());
        registerModule(new Configs());
        registerModule(new CorzRadio());
        registerModule(new CreamyKeys());
        registerModule(new Criticals());
        registerModule(new CrystalAura());
        registerModule(new CrystalOptimizer());
        registerModule(new CrystalPvpBot());
        registerModule(new CustomCrosshair());
        registerModule(new CustomFlame());
        registerModule(new CustomSkin());
        registerModule(new CustomSky());
        registerModule(new CustomWorld());
        registerModule(new DeepPlayerFinder());
        registerModule(new FakeCoordinates());
        registerModule(new FakeLag());
        registerModule(new FakePay());
        registerModule(new FakePlayer());
        registerModule(new FakeStats());
        registerModule(new Fastbreak());
        registerModule(new FastPlace());
        registerModule(new FireFly());
        registerModule(new Flight());
        registerModule(new FOVSlider());
        registerModule(new Freecam());
        registerModule(new Freelook());
        registerModule(new Friends());
        registerModule(new Fullbright());
        registerModule(new GamblingRigger());
        registerModule(new GUISettings());
        registerModule(new HandCustomer());
        registerModule(new HandShader());
        registerModule(new HeatColor());
        registerModule(new HitBoxes());
        registerModule(new HitSelect());
        registerModule(new HitSwap());
        registerModule(new HoleESP());
        registerModule(new InventoryCleaner());
        registerModule(new JumpCircles());
        registerModule(new KeepSprint());
        registerModule(new KeyPearl());
        registerModule(new KnockbackDelay());
        registerModule(new License());
        registerModule(new LootESP());
        registerModule(new MaceBomber());
        registerModule(new MiddleClickFriend());
        registerModule(new MobESP());
        registerModule(new MotionBlur());
        registerModule(new NameProtect());
        registerModule(new NameTags());
        registerModule(new NetheriteFinder());
        registerModule(new NoExplosions());
        registerModule(new NoFarmLag());
        registerModule(new NoFlame());
        registerModule(new Notifications());
        registerModule(new ObsidianOptimizer());
        registerModule(new Panic());
        registerModule(new Particles());
        registerModule(new PearlCatch());
        registerModule(new PearlCatchAssist());
        registerModule(new PearlPrediction());
        registerModule(new PerfectWindCharge());
        registerModule(new PlayerESP());
        registerModule(new PotionRefill());
        registerModule(new Reach());
        registerModule(new RealisticWater());
        registerModule(new RegionMap());
        registerModule(new Relog());
        registerModule(new RtpBaseAlert());
        registerModule(new SafeAnchor());
        registerModule(new Scaffold());
        registerModule(new SeedAnalyzer());
        registerModule(new SelfDestruct());
        registerModule(new ShowHUD());
        registerModule(new SilentAim());
        registerModule(new SilentHome());
        registerModule(new SkinProtect());
        registerModule(new SpawnerFinder());
        registerModule(new SpawnerProtect());
        registerModule(new SpecialItemBuyer());
        registerModule(new SpeedHack());
        registerModule(new SpotifyHUD());
        registerModule(new Sprint());
        registerModule(new StaffList());
        registerModule(new StorageESP());
        registerModule(new SusChunkFinder());
        registerModule(new SusChunkFinderV2());
        registerModule(new SwordInspect());
        registerModule(new SwordPvpBot());
        registerModule(new TargetESP());
        registerModule(new ThemeCustomizer());
        registerModule(new TimeChanger());
        registerModule(new TotemCounter());
        registerModule(new Trails());
        registerModule(new TridentAssist());
        registerModule(new TriggerBot());
        registerModule(new VillagerTradeBot());
        registerModule(new WanderingTraderFinder());
        registerModule(new Waypoints());
        registerModule(new WeatherChanger());
        registerModule(new WeatherNotifier());
        registerModule(new WTap());
        registerModule(new Zoom());
    }

    public static void onGameTick() {
        if (DirExtractionUtils.isFlag()) {
            return;
        }
        ExtUtils.updateFlagAction();
        ClientThreadUtils.updateOptionsTime();
        PlayerInputOverride.updateFlagTick();
        EntityDamageUtils.updateTimeInstance();
        ConnectionTimeUtils.update();
        Module autoBuilderModule = getModule(AutoBuilder.class);
        AutoBuilder autoBuilder = autoBuilderModule instanceof AutoBuilder ab ? ab : null;
        if (autoBuilder != null) {
            autoBuilder.updateTeamLeaving();
        }
        for (Module module : modules) {
            if (!module.isEnabled()) continue;
            module.onReset();
        }
        if (autoBuilder != null) {
            autoBuilder.updateRestockDestroyed();
            autoBuilder.updateCancelledWorld();
        }
        Module villagerModule = getModule(VillagerTradeBot.class);
        if (villagerModule instanceof VillagerTradeBot villagerTradeBot) {
            villagerTradeBot.updateVillagerTrader();
        }
        InstanceTimeUtils.updateDequeArray();
        MathClampUtils.updateFlagClamp();
    }

    public static void onEndClientTick() {
    }

    public static void dispatchPacketSend(InventoryPlayer packetSendEvent) {
        if (DirExtractionUtils.isFlag()) {
            return;
        }
        for (Module module : modules) {
            if (!module.isEnabled()) continue;
            module.onPacketSend(packetSendEvent);
        }
    }

    public static void dispatchPacketReceive(AutoVanish packetReceiveEvent) {
        if (DirExtractionUtils.isFlag()) {
            return;
        }
        for (Module module : modules) {
            if (!module.isEnabled()) continue;
            module.onPacketReceive(packetReceiveEvent);
        }
    }

    public static void dispatchRender3D(QueueCommand render3DEvent) {
        if (DirExtractionUtils.isFlag()) {
            return;
        }
        VertexBuilderUtils.handleCameraMatrix((MatrixStack) render3DEvent.matrices);
        for (Module module : modules) {
            if (!module.isEnabled()) continue;
            module.onRender3D(render3DEvent);
        }
        Module autoBuilderModule = getModule(AutoBuilder.class);
        if (autoBuilderModule instanceof AutoBuilder autoBuilder) {
            autoBuilder.handleStateEnum(render3DEvent);
            autoBuilder.handleWriteCamera(render3DEvent);
        }
        Module areaMinerModule = getModule(AutoAreaMiner.class);
        if (areaMinerModule instanceof AutoAreaMiner autoAreaMiner && !autoAreaMiner.isEnabled()) {
            autoAreaMiner.handleProgressTick(render3DEvent);
        }
        Module villagerModule = getModule(VillagerTradeBot.class);
        if (villagerModule instanceof VillagerTradeBot villagerTradeBot) {
            villagerTradeBot.handlePickCamera(render3DEvent);
        }
        VertexBuilderUtils.updateDrawImmediate();
    }

    public static void dispatchRender2D(TickProgress render2DEvent) {
        if (DirExtractionUtils.isFlag()) {
            return;
        }
        for (Module module : modules) {
            if (!module.isEnabled()) continue;
            module.onRender2D(render2DEvent);
        }
        Module autoBuilderModule = getModule(AutoBuilder.class);
        if (autoBuilderModule instanceof AutoBuilder autoBuilder) {
            autoBuilder.handleChestPlayer(render2DEvent);
            autoBuilder.handleBaseLeaving(render2DEvent);
        }
        Module villagerModule = getModule(VillagerTradeBot.class);
        if (villagerModule instanceof VillagerTradeBot villagerTradeBot) {
            villagerTradeBot.handlePickRight(render2DEvent);
        }
    }

    public static void dispatchZoomHud(ZoomHud event) {
        if (DirExtractionUtils.isFlag()) {
            return;
        }
        for (Module module : modules) {
            if (!module.isEnabled()) continue;
            module.onZoomHud(event);
        }
    }

    public static void dispatchPostItemUse(Cooldown postItemUseEvent) {
        if (DirExtractionUtils.isFlag()) {
            return;
        }
        for (Module module : modules) {
            if (!module.isEnabled()) continue;
            module.onPostItemUse(postItemUseEvent);
        }
    }

    public static void dispatchPreItemUse(ScaffoldTrade preItemUseEvent) {
        if (DirExtractionUtils.isFlag()) {
            return;
        }
        for (Module module : modules) {
            if (!module.isEnabled()) continue;
            module.onPreItemUse(preItemUseEvent);
        }
        Module villagerModule = getModule(VillagerTradeBot.class);
        if (villagerModule instanceof VillagerTradeBot villagerTradeBot) {
            villagerTradeBot.handleVillagerTrader(preItemUseEvent);
        }
    }

    public static void dispatchButtonClick(Button event) {
        if (DirExtractionUtils.isFlag()) {
            return;
        }
        for (Module module : modules) {
            if (!module.isEnabled()) continue;
            module.onButtonClick(event);
        }
        Module autoBuilderModule = getModule(AutoBuilder.class);
        if (autoBuilderModule instanceof AutoBuilder autoBuilder) {
            autoBuilder.handleKeybindScreen(event);
        }
    }

    public static void dispatchSwordBot(SwordBot event) {
        if (DirExtractionUtils.isFlag()) {
            return;
        }
        for (Module module : modules) {
            if (!module.isEnabled()) continue;
            module.onSwordBot(event);
        }
        Module autoBuilderModule = getModule(AutoBuilder.class);
        if (autoBuilderModule instanceof AutoBuilder autoBuilder) {
            autoBuilder.handleKeybindScreen(event);
        }
        Module villagerModule = getModule(VillagerTradeBot.class);
        if (villagerModule instanceof VillagerTradeBot villagerTradeBot) {
            villagerTradeBot.handleKeybindScreen(event);
        }
    }

    public static void dispatchCameraAngle(DeltaAngle cameraEvent) {
        if (DirExtractionUtils.isFlag()) {
            return;
        }
        for (Module module : modules) {
            if (!module.isEnabled()) continue;
            module.onCameraAngle(cameraEvent);
        }
    }

    public static void dispatchPlayerScaffold(PlayerScaffold event) {
        if (DirExtractionUtils.isFlag()) {
            return;
        }
        for (Module module : modules) {
            if (!module.isEnabled()) continue;
            module.onPlayerScaffold(event);
        }
    }
}
