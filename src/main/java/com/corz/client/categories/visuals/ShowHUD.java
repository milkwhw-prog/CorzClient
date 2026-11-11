package com.corz.client.categories.visuals;

import com.corz.client.CorzClient;
import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.ModuleConfigAutosave;
import com.corz.client.config.EnumSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.config.FriendNameStore;
import com.corz.client.gui.ButtonBase;
import com.corz.client.gui.DisabledEnabled;
import com.corz.client.gui.MatrixPeekUtilsRender;
import com.corz.client.gui.NativeHud;
import com.corz.client.gui.NativeHud.Caps;
import com.corz.client.gui.NativeHud.Field;
import com.corz.client.gui.NativeHud.ListRow;
import com.corz.client.gui.NativeHud.Pop;
import com.corz.client.gui.NativeHud.Pulse;
import com.corz.client.gui.NativeHud.Row;
import com.corz.client.gui.NativeHud.Smooth;
import com.corz.client.gui.ScreenSoundsUtils;
import com.corz.client.gui.ZoomHud;
import com.corz.client.mixins.AccessorItemCooldownEntry;
import com.corz.client.mixins.AccessorItemCooldownManager;
import com.corz.client.render.CardMatrixUtils;
import com.corz.client.render.CorzRoundedUtils;
import com.corz.client.render.IcoGenUtils;
import com.corz.client.render.MatrixPeekUtilsIdentifier;
import com.corz.client.render.VertexTextureFont;
import com.corz.client.render.VertexTexturePeek;
import com.corz.client.utils.StaffRoster.Member;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Monitor;
import net.minecraft.client.util.VideoMode;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix3x2fStack;
import org.lwjgl.glfw.GLFW;
import com.corz.client.categories.bots.AutoElytraPilot;
import com.corz.client.categories.bots.AutoTpaHere;
import com.corz.client.categories.bots.AnchorHunter;
import com.corz.client.categories.bots.AutoElytraFinder;
import com.corz.client.categories.donutsmp.Bloodhound;
import com.corz.client.categories.donutsmp.RegionMap;
import com.corz.client.categories.donutsmp.StaffList;
import com.corz.client.categories.misc.FakeCoordinates;
import com.corz.client.categories.misc.NameProtect;
import ai.auth.Module;
import ai.auth.ModuleRegistry;
import com.corz.client.utils.FpsHistory;
import com.corz.client.categories.visuals.TotemCounter;
import com.corz.client.categories.visuals.SpotifyHUD;
import com.corz.client.utils.HudFontUtils;
import com.corz.client.utils.HotbarHud;
import com.corz.client.utils.HudShowUtils;
import com.corz.client.utils.CardStatusUtilsActive;
import com.corz.client.utils.DirExtractionUtils;
import com.corz.client.utils.DoneMaxUtils;
import com.corz.client.utils.IconsPngType;
import com.corz.client.utils.PageRaltUtils;
import com.corz.client.utils.QuintBounceType;
import com.corz.client.utils.QuintScissor;
import com.corz.client.utils.StaffBadge;
import com.corz.client.utils.StaffRoster;

public class ShowHUD extends Module {
   private float anchorCenterX;
   private static final String RMB = "RMB";
   private final Caps[] capss;
   private final float[] shieldTable;
   private float keybindsModulesAmount;
   private final float[] hudStyleAmounts;
   public static int round;
   private final float[] hotbarTable;
   private VertexTexturePeek vertexTexture;
   private final BooleanSetting booleanBase;
   private static final float VALUE_2_5_F = 2.5F;
   public static int potionsHudOffset;
   private float potionIconSize;
   private static final String[] potionsStaffTexts = new String[]{"topbar", "perf", "modules", "resources", "perfgraph", "coords", "staff", "potions"};
   private final float[] modulesKeybindsAmounts;
   private float leftRightAmount;
   private static float[] potionsStaffAmounts = new float[16];
   private static final float HUD_PADDING_LARGE = 8.0F;
   private float hudAmount;
   private boolean resizeScrollFlag;
   private static final String COOLDOWNS = "Cooldowns";
   private List<Module> alphabeticalCategory;
   private String resizeHandleText;
   private long lastNanoTime;
   private final BooleanSetting coordinates;
   private float coordsAmount;
   private final QuintScissor holdKeyAnimation;
   private float personAmount;
   private float buildAmount;
   private final Map<String, float[]> hudOffsetsMap;
   private static long potionsSsaTime = -1L;
   private String dragReleaseText;
   private final ShowHUD.NList nList;
   private final Field field;
   private String releaseText;
   private final float[] scaledTable;
   public static int radarSsaFactorRadarSsa;
   private float scrollAmount;
   private boolean scaledMatricesActive;
   private float anchorWidth;
   private final Field keyboard;
   private final List<ShowHUD.HudBox> hudResetItems;
   private int enumCount;
   private long lastNanoTimeTimerCooldowns;
   private int clockCloudValue;
   private static final String POTIONS = "Potions";
   private static long ssaPerfgraphCountPotionsSsa = -1L;
   private final float[] hudTable;
   private float backpackTopbarMinWidth;
   private final Pop lerpUuidPop;
   private float anchorHeight;
   private final ArrayList<ShowHUD.NCd> cooldownsTimerItems;
   private final Pop shieldPop;
   private final Pop staffPop;
   private float coordsRadarAmount;
   private final Smooth smooth;
   private static long ssaPerfgraphCount = 0L;
   private static final String DRAG_MOVE_CORNER = "Drag to move  \u00b7  corner or scroll to resize";
   private final BooleanSetting customHotbar;
   private final IntegerSetting integerHelper;
   private static final String BPS = "BPS";
   private final String[] clone;
   private float defaultsHud;
   private final float[] defaultsLayoutTable;
   private float defaultsAmount;
   private static final float HUD_PADDING_SMALL = 4.0F;
   private final float[] table;
   private float savedBaseX;
   private boolean defaultsLayoutFlag;
   private VertexTextureFont vertexTextureOnlineStaff;
   private final QuintScissor quart;
   private final Smooth modulesKeybindsSmooth;
   public static int radarSsaFactorRegionmapRegio;
   private final BooleanSetting moduleList;
   private final ShowHUD.NFlow nFlow;
   private static VertexTexturePeek vertexTextureVertexTexturePe;
   private final QuintScissor quartStaff;
   private float hotbarAmount;
   private final Pop scrollResizePop;
   private float buildOnlineAmount;
   private static float vertexTextureAmount;
   private final Map<String, float[]> hudModulesMap;
   private final EnumSetting enumHelper;
   private final float[] cloudPersonTable;
   private final BooleanSetting dragEditor;
   private float baseY;
   private long lastNanoTimeRelease;
   private float displayedBuildProgress;
   private float topbarBackpackAmount;
   private final float[] corzStaffAmounts;
   private float tpsSignalAmount;
   private float bottomLeftAmount;
   private final QuintScissor scaledMatricesQuintScissor;
   private final Pop topbarCloudPop;
   private float anchorTop;
   private final QuintScissor quartLinked;
   private int scaleFactor;
   private final Map<StatusEffect, ShowHUD.NPot> effectAmplifierNPotMap;
   private final ItemStack[] itemStacks;
   private final QuintScissor quartScaledMatrices;
   private static long ssaPotionsTime = -1L;
   private final BooleanSetting inventoryCard;
   private boolean buildprogressFlag;
   private final BooleanSetting performance;
   private float defaultsHudCoords;
   private float onlineNoneAmount;
   private boolean person;
   private final Smooth keybindsKeyboardSmooth;
   private LivingEntity livingEntity;
   private final QuintScissor hudQuintScissor;
   private static long ssaPerfgraphCountRoundSsa = -1L;
   private final Pop barInventoryPop;
   private final Smooth fieldsTotemsSmooth;
   private float[] coordsTable;
   private boolean hudActive;
   private final BooleanSetting metricIcons;
   private final ButtonBase buttonBase;
   private final Field timer;
   private static float totemsAmount;
   private float hudDefaultsAmount;
   private final int[] inventoryResourcesValues;
   private final Pop flaskPotionsPop;
   private int modulesKeybindsValue;
   private final BooleanSetting clockScaledMatrices;
   private final ShowHUD.NList modulesKeybindsNList;
   private float layoutDefaultsAmount;
   private StaffList staffList;
   private final Field scrollField;
   private final BooleanSetting radarLerpUuid;
   private float hotbarMinOffset;
   private final BooleanSetting performanceCard;
   private long tpsSignalTime;
   private final String[] inventoryTexts;
   private final int[] hotbarSortedIndices;
   private long tpsSignalValue;
   private float offsetX;
   private int hotbarCount;
   private boolean releaseActive;
   private final Set<UUID> lerpUuidUuidSet;
   private float popupPointerY;
   private final Field smoothField;
   private final long[] hsvRgbTable;
   private final Smooth smoothSmooth;
   private final float[] hotbarWidths;
   private final BooleanSetting vitalsCard;
   private final Smooth backpackTopbarSmooth;
   private final Smooth hudSmooth;
   private final ArrayList<String> hudOrderItems;
   private final Map<String, float[]> modulesHudMap;
   private final ItemStack[] smoothItemStacks;
   private final Pop performancePerfgraphPop;
   private int releaseValue;
   public static int regionmapRegionValue;
   private float resizeScrollAmount;
   public static int ssaPotionsValue;
   private final QuintScissor quintScissor;
   private boolean resizeHandleFlag;
   private static final String CORZ = "Discord.gg/leakestan";
   private String cloudPersonText;
   private int coordsRadarCount;
   private boolean keybindsKeyboardActive;
   private DrawContext drawContext;
   private long lastNanoTimeBuild;
   private float elementY;
   private final float[] hotbarHeights;
   private final Smooth styleHudSmooth;
   private static final String INF = "inf";
   private static final String RESOURCES = "Resources";
   private final float[] onlineBuildAmounts;
   private long lastNanoTimeSmooth;
   private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("h:mm:ssa", Locale.US);
   private float smoothAmount;
   public static int hudValue;
   private String coordsText;
   private float shieldAmount;
   private int scaledWidth;
   private final long[] eventNanoTimes;
   private float corzStaffAmount;
   private static final EquipmentSlot[] ssaClientEquipmentSlots = new EquipmentSlot[]{
      EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
   };
   private final Smooth corzBrandSmooth;
   private float elementX;
   private int coordsRadarValue;
   private final Pop fieldsTotemsPop;
   private float onlineBuildAmount;
   private float cooldownsPotionsAmount;
   private double scaledMatricesAmount;
   private int scaledValue;
   private long lastNanoTimeCloudPerson;
   private static int hotbarTotemsCount;
   private float backpackTopbarAmount;
   private int roundModulesValue;
   private VertexTexturePeek vertexTextureTpsSignal;
   private int rosterStaff;
   private final Field staffField;
   private final Pop modulesKeybindsPop;
   private final Pop backpackTopbarPop;
   private final Field hudField;
   private int hotbarVisibleCount;
   private double linkedAmount;
   private final Smooth performancePerfgraphSmooth;
   private final Smooth topbarCloudSmooth;
   private final QuintScissor potionsAnimation;
   private boolean modulesFlag;
   private final Map<String, ShowHUD.HudScale> hotbarHudMap;
   private String resizeScrollText;
   private final Smooth defaultsLayoutSmooth;
   private final Smooth styleHudWidthSmooth;
   private double x;
   private final Map<String, ShowHUD.NStaff> onlineNoneNStaffMap;
   private float elementWidth;
   private final float[] coordsRadarTable;
   private final Smooth lowHealthSmooth;
   private String scrollText;
   private final Smooth[] smooths;
   private final Smooth fieldsCheckedSmooth;
   private final Field staffNameField;
   private final float[] defaultsTable;
   private float onlineStaffAmount;
   private final ShowHUD.NFlow coordsRadarNFlow;
   private static final String[] clientCorzTexts = new String[]{"X", "Y", "Z"};
   private final Field onlineNoneField;
   private final BooleanSetting keystrokesScaledMatrices;
   private final QuintScissor serverInfoAnimation;
   private long timeMillis;
   private final QuintScissor keystrokesAnimation;
   private static final ShowHUD.H2Seg H2_SEG = new ShowHUD.H2Seg(null, "Corz Client", -1, false, true);
   private float radarMaxWidth;
   private boolean topbarPerfFlag;
   private final LinkedList<Long> linkedList;
   private final QuintScissor smoothQuintScissor;
   private long cloudPersonValue;
   public static int ssaPotionsExtraSize;
   private long enumValue;
   private final float[] radarEntryWidths;
   private final ShowHUD.NFlow hudNFlow;
   private String hudText;
   private float cooldownsStaffAmount;
   private float savedBaseY;
   private final float[] shieldDurabilityFractions;
   private final float[] styleHudTable;
   private final Pop timerCooldownsPop;
   private final QuintScissor staffQuintScissor;
   private float potionsResourcesAmount;
   private int minusValue;
   private ShowHUD.HudScale hudScale;
   private final Field buildField;
   private String popupText;
   private final float[] styleHudCurve;
   private float bottomAmount;
   private String cloudPersonCachedSubText;
   private final Map<String, float[]> hudDefaultsMap;
   private final Field distanceField;
   private static final String[] onlineBuildTexts = new String[]{"N", "E", "S", "W"};
   private static final String[] healthFoodTexts = new String[]{"W", "A", "S", "D"};
   private final QuintScissor scaledFadeAnimation;
   private int personOnlineValue;
   private final BooleanSetting serverInfo;
   private final BooleanSetting targetHud;
   private final ShowHUD.H2Seg[] cloudPersonH2Segs;
   private final Smooth keybindsVisibilitySmooth;
   private int scaledResultCount;
   private float anchorLeft;
   private final BooleanSetting potionsScaledMatrices;
   private float bottomProjectionEnd;
   private long lastNanoTimePerformancePerfg;
   private double dragMouseY;
   private float modulesAmount;
   private final float[] hotbarTargetXs;
   private final Pop smoothPop;
   private int layoutDefaultsValue;
   private final BooleanSetting buildProgress;
   private final Pulse pulse;
   private final float[] scaledScratchValues;
   private final Field flask;
   private boolean hudScales;
   private final QuintScissor linkedQuintScissor;
   private final int[] hotbarSortPriorities;
   private float scaledAmount;
   private float popupTargetX;
   private final Pop keybindsKeyboardPop;
   private float defaultX;
   private final Pop fieldsCheckedPop;
   private double tpsServerAmount;
   private final ShowHUD.NFlow timerCooldownsFlow;
   private int currentFps;
   private final Field modulesKeybindsField;
   private final Smooth absorptionSmooth;
   public static int ssaPotionsValue4SsaPotions;
   private final Smooth healthBarSmooth;
   private float cloudPersonAmount;
   private final float[] radarEntryYs;
   private long lastNanoTimeCoordsRadar;
   private final Smooth[] coordsRadarSmooths;
   private final ArrayList<Module> sortedKeybindModules;
   private long smoothValue;
   private final BooleanSetting brandScaledMatrices = new BooleanSetting("Brand", "Client name pill", true);
   private final Smooth flaskPotionsSmooth;
   private final ShowHUD.NFlow keybindsFlow;
   private final String[] hudTexts;
   private int scaledHeight;
   private final float[] hotbarCurrentYs;
   private boolean linkedActive;
   private final ShowHUD.NFlow flaskPotionsFlow;
   private int cloudPersonCachedValue;
   private Module toggle;
   private float defaultY;
   private final QuintScissor keySPressAnimation;
   private int tpsValue;
   private long timeMillisLinked;
   private float percentLabelWidth;
   private final Smooth buildSmooth;
   private final Map<Identifier, ShowHUD.NCd> timerCooldownsNCdMap;
   private float popupTargetY;
   private static final String[] levelArmorTexts = new String[]{"HEALTH", "FOOD", "ARMOR", "LEVEL", "AIR"};
   private final int[] hsvColorCache;
   private float bottomDirectionX;
   private float hudCount;
   private final BooleanSetting cpsScaledMatrices;
   private float staffAmount;
   private ItemStack[] backpackTopbarItemStacks;
   private final Pop personOnlinePop;
   private final float[] scrollTable;
   private final Smooth lowFoodSmooth;
   private float maxCoordsRadarBound;
   private final boolean[] coordsRadarFlags;
   private static final int SINGLE_STEP = 1;
   private final Smooth personOnlineSmooth;
   private float popupCurrentX;
   private boolean linkedWasActive;
   private final Smooth totemsSpacingSmooth;
   private static final String[] levelKeybindsTexts = getTextsStringArray();
   private final float[] layoutDefaultsTable;
   private LivingEntity entity;
   private float popupWidth;
   private final ArrayList<ShowHUD.NPot> effectAmplifierNPotList;
   private final BooleanSetting effectIcons;
   private final ArrayDeque<long[]> arrayDeque;
   private long lastLayoutNanoTime;
   private final float[] backpackTopbarTable;
   private final QuintScissor keyWPressAnimation;
   private final ArrayList<ShowHUD.NStaff> onlineNoneItems;
   private final Pop buildPop;
   private long lastTimeMillis;
   private final Pulse timerCooldownsPulse;
   private float radarCoordsAmount;
   private int hotbarValue;
   private final Pulse keybindsKeyboardPulse;
   private static final String KEYBINDS = "Keybinds";
   private final Pop popPop;
   private long lastFrameTimeMs;
   private int scaledMatricesValue;
   private float elementHeight;
   private final Field buildPercentField;
   private final Field[] fields;
   private final QuintScissor moduleListAnimation;
   private final Field performancePerfgraphField;
   private final Smooth barInventorySmooth;
   private final Smooth staffSmooth;
   private final float[] hotbarTargetYs;
   private final String[] hotbarTexts;
   private final EnumSetting sortEnum;
   private double dragStartMouseX;
   private final Pulse onlineNonePulse;
   private final ShowHUD.H2Seg[] h2Segs;
   private final int[] hotbarGroupCounts;
   private int onlineNoneValue;
   private float lerpUuidAmount;
   private float bottomDirectionY;
   private float lastRenderedAmount;
   private static int keybindsLevelValue = -9;
   private static int keybindsResourcesValue = -9;
   private int cachedEnumCount;
   private final Pop scrollPop;
   private float bottomOriginX;
   private final float[] scaledHudTable;
   private long keybindsKeyboardValue;
   private final float[] tpsSignalTable;
   private static float keybindsResourcesAmount = 1.0F;
   private final float[] onlineStaffTable;
   private final Smooth resourcesVisibilitySmooth;
   private final int[] inventoryTable;
   private float staffCorzAmount;
   private final Smooth airBarSmooth;
   private final BooleanSetting radarPlayers;
   public static int ssaPotionsOffsetY;
   private static final String[] hudFlowTexts = new String[]{"keybinds", "resources", "inventory", "keystrokes", "perfgraph", "potions"};
   private final float[] radarEntryXs;
   private final Pop corzBrandPop;
   private long lastNanoTimeScrollResize;
   private String releaseResetText;
   private final Smooth totemsOffsetSmooth;
   private static final Color COLOR = new Color(CardMatrixUtils.getValue(-1750963, -7630437, 0.3F), true);
   private final Field moreCountField;
   private final Smooth timerCooldownsSmooth;
   private static float cachedVertexTextureMax;
   private float radarCoordsAmount2CoordsRada;
   private boolean defaultsFlag;
   private String dragRightText;
   private int roundedModulesWidth;
   public static int ssaPotionsOffsetX;
   private int gaugeSignalValue;
   private final Smooth secondaryBarSmooth;
   private final QuintScissor cpsAnimation;
   private static final String NONE_ONLINE = "None online";
   private final BooleanSetting keybindsCard;
   private final Smooth coordsRadarSmooth;
   private double dragStartMouseY;
   private final Field[] fieldsTotemsFields;
   private float coordsOffsetY;
   private static final String STAFF = "Staff";
   private float defaultsLayoutAmount;
   private double z;
   private final LinkedList<Long> linkedLinkedList;
   private double tickAccumulator;
   private final float[] coordsAmounts;
   private float bottomOriginY;
   private final Pulse modulesKeybindsPulse;
   private final Smooth scrollSmooth;
   private final Pulse scrollPulse;
   public static int radarSsaTotal2RadarSsa;
   private long lastNanoTimeBackpackTopbar;
   private static float fieldsTotemsAmount;
   private final float[] styleHudColorBuffer;
   private float radarArmorAmount;
   private static final String LMB = "LMB";
   private float frameDeltaSeconds;
   private float popupCurrentY;
   public static int roundSsaTop;
   private static final String[] onlineStaffTexts = new String[]{"-5s", "-4s", "-3s", "-2s", "-1s", "now"};
   private long lastNanoTimeLerpUuid;
   private final BooleanSetting targetPlayersOnly;
   private final IntegerSetting radarSize;
   private final Smooth personOnlineWidthSmooth;
   private int timerCooldownsValue;
   private final ShowHUD.NFlow performanceGraphFlow;
   private boolean scaledFadeInPending;
   private final float[] shieldDisplayedFractions;
   private final Smooth[] fieldsTotemsSmooths;
   private float coordsOffsetX;
   private final QuintScissor quartBuild;
   private final long[] inventoryChangeTimes;
   private static final String MODULES = "Modules";
   private int defaultsLayoutValue;
   private final int[] inventoryCounts;
   private boolean scaledMatricesWasActive;
   private int effectAmplifierValue;
   private static final String[] barInventoryTexts = getTexts();
   private float scaledBestPosition;
   private int hotbarItemCount;
   private final BooleanSetting armorHud;
   private static final String BUILD = "Build";
   private long lastNanoTimeFlaskPotions;
   private final boolean[] hotbarFlags;
   private VertexTextureFont hudVertexTextureFont;
   private static final String CORZ_CLIENT = "Corz Client";
   private final Smooth radarPanelHeightSmooth;
   private final Smooth flaskPotionsHeightSmooth;
   private final boolean[] hudFlags;
   public static int radarSsaTotalRadarSsa;
   private static ShowHUD singleton;
   private static final String PERFORMANCE = "Performance";

   private List<Module> getTimeEnum() {
      long time = (this.sortEnum.getCurrentIndex() + 1L) * -7046029254386353131L;

      for (Module iilliliilIIiItem : ModuleRegistry.getModules()) {
         if (iilliliilIIiItem != null && iilliliilIIiItem.isEnabled() && iilliliilIIiItem.isVisible()) {
            time = (time ^ System.identityHashCode(iilliliilIIiItem) * -7046029254386353131L + iilliliilIIiItem.getBindKey()) * -4658895280553007687L;
         }
      }

      if (time != this.enumValue || this.enumCount == 0) {
         this.enumValue = time;
         this.alphabeticalCategory = this.getAlphabeticalCategory();
         this.enumCount++;
      }

      return this.alphabeticalCategory;
   }

   private void renderLerpUuid(DrawContext context, int lerpUuidValue, int fieldsCheckedValue, long nanoTime) {
      boolean lerpUuidFlag = this.radarLerpUuid.isEnabled() && mc.world != null;
      if (this.lerpUuidPop.canTimeAmount(lerpUuidFlag, ssaPerfgraphCount) && mc.world != null) {
         int maxRadarSize = this.getMaxRadarSize();
         float lerpUuidValue4 = maxRadarSize / 2.0F;
         this.handleLeftRight(lerpUuidValue, fieldsCheckedValue, maxRadarSize);
         float lerpUuidValue5 = this.lerpUuidAmount + this.coordsOffsetX;
         float hudScaleValue = this.leftRightAmount + this.coordsOffsetY;
         float translateMatricesValue = lerpUuidValue5 + lerpUuidValue4;
         float lerpUuidValue8 = hudScaleValue + lerpUuidValue4;
         this.handleTextHud(lerpUuidValue5, hudScaleValue, maxRadarSize, maxRadarSize);
         this.handleRadarSsa(this.lerpUuidAmount, this.leftRightAmount, maxRadarSize);
         float tickProgress = mc.getRenderTickCounter().getTickProgress(true);
         float lerpUuidValue9 = mc.player.lastYaw + (mc.player.getYaw() - mc.player.lastYaw) * tickProgress;
         double radians = Math.toRadians((lerpUuidValue9 % 360.0 + 180.0 + 360.0) % 360.0);
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(this.lerpUuidPop.getPopProgress());
         boolean translateMatrices = NativeHud.canTranslateMatrices(context, translateMatricesValue, lerpUuidValue8, this.lerpUuidPop.getAmount());

         try {
            NativeHud.handle(NativeHud.lerpUuidValue, lerpUuidValue5, hudScaleValue, (float)maxRadarSize, (float)maxRadarSize, 1.0F);
            float max10 = Math.max(4.0F, lerpUuidValue4 - 16.0F);
            float lerpUuidFactor = Math.max(6.0F, lerpUuidValue4 - 14.0F);
            float max2 = Math.max(8.0F, lerpUuidValue4 - 8.0F);
            NativeHud.renderForDrawContextFloatInt(context, translateMatricesValue - max10, lerpUuidValue8, max10 * 2.0F, 268435455);
            NativeHud.renderForDrawContextFloat(context, translateMatricesValue, lerpUuidValue8 - max10, max10 * 2.0F, 268435455);
            NativeHud.handlePeekPosition(translateMatricesValue - lerpUuidFactor, lerpUuidValue8 - lerpUuidFactor, lerpUuidFactor * 2.0F, lerpUuidFactor * 2.0F, lerpUuidFactor, MatrixPeekUtilsRender.getAmount(), 536870911);
            if (this.radarPlayers.isEnabled()) {
               double lerpUuidDivisor = Math.max(1, this.integerHelper.getValue());
               ClientPlayerEntity player = mc.player;
               double lerp = MathHelper.lerp((double)tickProgress, player.lastRenderX, player.getX());
               double lerp2 = MathHelper.lerp((double)tickProgress, player.lastRenderZ, player.getZ());
               var players = mc.world.getPlayers();
               int varSize = players.size();
               if (this.lastNanoTimeLerpUuid == 0L || nanoTime - this.lastNanoTimeLerpUuid >= 500000000L) {
                  this.lastNanoTimeLerpUuid = nanoTime;
                  this.lerpUuidUuidSet.clear();

                  for (int i = 0; i < varSize; i++) {
                     PlayerEntity playerEntity = (PlayerEntity)players.get(i);
                     if (playerEntity != player && FriendNameStore.canFlag(playerEntity)) {
                        this.lerpUuidUuidSet.add(playerEntity.getUuid());
                     }
                  }
               }

               for (int j = 0; j < varSize; j++) {
                  PlayerEntity playerEntity2 = (PlayerEntity)players.get(j);
                  if (playerEntity2 != player) {
                     double y = MathHelper.lerp((double)tickProgress, playerEntity2.lastRenderX, playerEntity2.getX()) - lerp;
                     double lerpUuidFactor3 = MathHelper.lerp((double)tickProgress, playerEntity2.lastRenderZ, playerEntity2.getZ()) - lerp2;
                     double distance = Math.sqrt(y * y + lerpUuidFactor3 * lerpUuidFactor3);
                     if (!(distance > lerpUuidDivisor)) {
                        double angle = Math.atan2(y, -lerpUuidFactor3) - radians;
                        double lerpUuidFactor4 = distance / lerpUuidDivisor * lerpUuidFactor;
                        NativeHud.render(
                           context,
                           translateMatricesValue + (float)(Math.sin(angle) * lerpUuidFactor4),
                           lerpUuidValue8 - (float)(Math.cos(angle) * lerpUuidFactor4),
                           3.0F,
                           this.lerpUuidUuidSet.contains(playerEntity2.getUuid()) ? -8393375 : -1750963
                        );
                     }
                  }
               }
            }

            NativeHud.render(context, translateMatricesValue, lerpUuidValue8, 3.0F, -920587);
            float lerpUuidValue11 = 10.0F;

            for (int k = 0; k < 4; k++) {
               double angle2 = k * (Math.PI / 2) - radians;
               float lerpUuidValue12 = translateMatricesValue + (float)(Math.sin(angle2) * max2);
               float lerpUuidValue13 = lerpUuidValue8 - (float)(Math.cos(angle2) * max2);
               NativeHud.handlePushScale(
                  onlineBuildTexts[k],
                  NativeHud.getAmount4(lerpUuidValue12 - this.onlineBuildAmounts[k] / 2.0F),
                  NativeHud.getAmount2(lerpUuidValue13 - lerpUuidValue11 / 2.0F, lerpUuidValue11, 0.78F),
                  0.78F,
                  k == 0 ? NativeHud.OFF_WHITE : NativeHud.GRAY
               );
            }
         } finally {
            NativeHud.renderPopMatrices(context, translateMatrices);
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
         }
      }
   }

   private void handleSsaPotions(int value, int value2, int ceil3, int ceil4) {
      ShowHUD.HudScale hudScale = this.hudScale;
      if (hudScale != null && ceil3 > 0) {
         ceil3 = (int)Math.ceil(ceil3 * hudScale.coordsPerf);
         ceil4 = (int)Math.ceil(ceil4 * hudScale.coordsPerf);
      }

      ssaPotionsOffsetY = value;
      ssaPotionsValue = value2;
      hudValue = ceil3;
      ssaPotionsExtraSize = ceil4;
      ssaPotionsTime = ssaPerfgraphCount;
   }

   private int getHotbarTotems(String hotbarTotemsText) {
      if (this.hotbarValue != hotbarTotemsCount) {
         return -1;
      } else {
         for (int i = 0; i < this.hotbarItemCount; i++) {
            if (this.hotbarTexts[i].equals(hotbarTotemsText)) {
               return i;
            }
         }

         return -1;
      }
   }

   private String getString(StatusEffectInstance statusEffectInstance) {
      try {
         return statusEffectInstance.getEffectType().value().getName().getString();
      } catch (Exception e2) {
         return "?";
      }
   }

   private static boolean canDuration(ShowHUD.NPot nPot, ShowHUD.NPot nPot2) {
      return nPot.durationActive != nPot2.durationActive ? !nPot.durationActive : nPot.duration > nPot2.duration;
   }

   private void renderFieldsChecked(DrawContext context, int fieldsCheckedValue) {
      if (this.fieldsCheckedPop.canTimeAmount(this.coordinates.isEnabled(), ssaPerfgraphCount)) {
         int pilotElytraValue = (int)Math.floor(FakeCoordinates.getX(mc.player.getX()));
         int floor3 = (int)Math.floor(mc.player.getY());
         int floor4 = (int)Math.floor(FakeCoordinates.getZ(mc.player.getZ()));
         if (this.fields[0].canMax(pilotElytraValue)) {
            this.fields[0].handleAmountTime(String.valueOf(pilotElytraValue), 0.9F, true, true);
         }

         if (this.fields[1].canMax(floor3)) {
            this.fields[1].handleAmountTime(String.valueOf(floor3), 0.9F, true, true);
         }

         if (this.fields[2].canMax(floor4)) {
            this.fields[2].handleAmountTime(String.valueOf(floor4), 0.9F, true, true);
         }

         long maxRound = Math.max(0L, Math.round(this.linkedAmount * 10.0));
         if (this.field.canMax(maxRound)) {
            this.field.handleAmountTime(maxRound / 10L + "." + maxRound % 10L, 0.9F, true, false);
         }

         float fieldsCheckedTotal = this.field.checkedRocketsAmount + 4.0F + this.modulesAmount;

         for (int i = 0; i < 3; i++) {
            fieldsCheckedTotal += this.modulesKeybindsAmounts[i] + 4.0F + this.fields[i].checkedRocketsAmount + 8.0F;
         }

         float maxRadarSize = 23.0F;
         float fieldsCheckedValue6 = 6.0F;
         float hudFactor = this.fieldsCheckedSmooth.getAmount((float)Math.ceil(fieldsCheckedValue6 + 12.0F + 5.0F + fieldsCheckedTotal + 7.0F), this.frameDeltaSeconds, 0.08F);
         float fieldsCheckedValue8 = 6.0F + this.coordsOffsetX;
         float hudScaleValue = fieldsCheckedValue - 6.0F - maxRadarSize + this.coordsOffsetY;
         this.handleTextHud(fieldsCheckedValue8, hudScaleValue, hudFactor, maxRadarSize);
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(this.fieldsCheckedPop.getPopProgress());
         boolean translateMatrices = NativeHud.canTranslateMatrices(context, fieldsCheckedValue8 + hudFactor / 2.0F, hudScaleValue + maxRadarSize / 2.0F, this.fieldsCheckedPop.getAmount());

         try {
            NativeHud.handle(NativeHud.pinValue, fieldsCheckedValue8, hudScaleValue, hudFactor, maxRadarSize, 1.0F);
            NativeHud.handle(fieldsCheckedValue8 + fieldsCheckedValue6, hudScaleValue + NativeHud.getAmount4((maxRadarSize - 12.0F) / 2.0F), IcoGenUtils.getIdentifier("map-pin"), NativeHud.WHITE, 1.0F);
            float scaleValue = fieldsCheckedValue8 + fieldsCheckedValue6 + 12.0F + 5.0F;
            float f13 = NativeHud.getAmount2(hudScaleValue, maxRadarSize, 0.78F);
            float roundOffset = NativeHud.getAmount2(hudScaleValue, maxRadarSize, 0.9F);

            for (int j = 0; j < 3; j++) {
               NativeHud.handlePushScale(clientCorzTexts[j], scaleValue, f13, 0.78F, NativeHud.GRAY);
               float nativeHudValue = scaleValue + (this.modulesKeybindsAmounts[j] + 4.0F);
               NativeHud.handle(this.fields[j], nativeHudValue, roundOffset, 0.9F, NativeHud.OFF_WHITE);
               scaleValue = nativeHudValue + (this.fields[j].checkedRocketsAmount + 8.0F);
            }

            NativeHud.handleAmount(this.field, scaleValue + this.field.checkedRocketsAmount, roundOffset, 0.9F, NativeHud.OFF_WHITE);
            NativeHud.handlePushScale("BPS", scaleValue + this.field.checkedRocketsAmount + 4.0F, f13, 0.78F, NativeHud.GRAY);
         } finally {
            NativeHud.renderPopMatrices(context, translateMatrices);
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
         }
      }
   }

   @Override
   protected void onZoomHud(ZoomHud zoomHud) {
      String ilIlText = this.releaseText;
      if (ilIlText != null && this.scaledMatricesActive && mc.currentScreen instanceof ChatScreen) {
         this.tickAccumulator = this.tickAccumulator + zoomHud.getAmount();
         int hudHotbarValue = (int)this.tickAccumulator;
         zoomHud.updateFlag();
         if (hudHotbarValue != 0) {
            this.tickAccumulator -= hudHotbarValue;
            this.handleHudHotbar(ilIlText, hudHotbarValue);
            this.eventNanoTimes[hudHotbarValue > 0 ? 4 : 2] = System.nanoTime();
         }
      }
   }

   @Override
   public boolean isVisible() {
      return false;
   }

   private static float getIcon(VertexTexturePeek vertexTexturePeek, ShowHUD.H2Seg h2Seg, float iconFactor) {
      return (8.0F + (h2Seg.icon != null ? 14.0F : 0.0F) + 8.0F) * iconFactor + getTextMono(vertexTexturePeek, h2Seg, iconFactor);
   }

   private void handleStyleHud(float styleHudValue, float styleHudValue2, float styleHudValue3, float textureColorBound) {
      float styleHudValue5 = 5.0F;
      float styleHudValue6 = 0.1F;
      int styleHudLimit = FpsHistory.getValue();
      float time7 = FpsHistory.getTime();
      float max8 = this.styleHudSmooth.getMax(FpsHistory.getTimeAmount(), this.frameDeltaSeconds, 0.12F, 0.01F);
      int index = 0;
      this.styleHudCurve[index] = 0.0F;
      this.styleHudTable[index] = max8;
      index++;
      float styleHudBound = max8;

      for (int i = 0; i < styleHudLimit && index < 161; i++) {
         float styleHudValue9 = -(i + 0.5F + time7) * 0.1F;
         float amounts10 = FpsHistory.getAmounts(Math.max(0, i - 1));
         float amounts11 = FpsHistory.getAmounts(i);
         float amounts12 = FpsHistory.getAmounts(Math.min(styleHudLimit - 1, i + 1));
         float styleHudBound2 = (amounts10 + 2.0F * amounts11 + amounts12) / 4.0F;
         this.styleHudCurve[index] = styleHudValue9;
         this.styleHudTable[index] = styleHudBound2;
         index++;
         styleHudBound = Math.max(styleHudBound, styleHudBound2);
         if (styleHudValue9 < -5.0F) {
            break;
         }
      }

      if (index >= 2) {
         float rocketsElytraBound = this.styleHudWidthSmooth.getMax(Math.max(30.0F, styleHudBound * 1.18F), this.frameDeltaSeconds, 0.45F, 0.5F);
         float styleHudFactor = (styleHudValue2 - styleHudValue) / 5.0F;
         float styleHudFactor2 = (textureColorBound - styleHudValue3) / Math.max(1.0F, rocketsElytraBound);
         int textureColorLimit = 0;

         for (int j = index - 1; j >= 0 && textureColorLimit < this.hudStyleAmounts.length; j--) {
            float styleHudFactor3 = this.styleHudCurve[j];
            float styleHudTotal = this.styleHudTable[j];
            if (j == index - 1 && styleHudFactor3 < -5.0F && index >= 2) {
               float styleHudValue13 = this.styleHudCurve[j - 1];
               float styleHudValue14 = this.styleHudTable[j - 1];
               float styleHudFactor4 = (-5.0F - styleHudFactor3) / (styleHudValue13 - styleHudFactor3);
               styleHudTotal += (styleHudValue14 - styleHudTotal) * styleHudFactor4;
               styleHudFactor3 = -5.0F;
            }

            this.hudStyleAmounts[textureColorLimit] = styleHudValue2 + styleHudFactor3 * styleHudFactor;
            this.styleHudColorBuffer[textureColorLimit] = textureColorBound - Math.min(rocketsElytraBound, styleHudTotal) * styleHudFactor2;
            textureColorLimit++;
            if (j == 0) {
               break;
            }

            float styleHudValue15 = this.styleHudCurve[j - 1];
            float styleHudValue16 = this.styleHudTable[Math.min(index - 1, j + 1)];
            float styleHudTotal17 = styleHudTotal;
            float styleHudValue18 = this.styleHudTable[j - 1];
            float styleHudValue19 = this.styleHudTable[Math.max(0, j - 2)];

            for (int k = 1; k < 3 && textureColorLimit < this.hudStyleAmounts.length; k++) {
               float styleHudFactor5 = k / 3.0F;
               float styleHudFactor6 = styleHudFactor5 * styleHudFactor5;
               float styleHudFactor7 = styleHudFactor6 * styleHudFactor5;
               float hashHuntBound = 0.5F
                  * (
                     2.0F * styleHudTotal17
                        + (-styleHudValue16 + styleHudValue18) * styleHudFactor5
                        + (2.0F * styleHudValue16 - 5.0F * styleHudTotal17 + 4.0F * styleHudValue18 - styleHudValue19) * styleHudFactor6
                        + (-styleHudValue16 + 3.0F * styleHudTotal17 - 3.0F * styleHudValue18 + styleHudValue19) * styleHudFactor7
                  );
               this.hudStyleAmounts[textureColorLimit] = styleHudValue2 + (styleHudFactor3 + (styleHudValue15 - styleHudFactor3) * styleHudFactor5) * styleHudFactor;
               this.styleHudColorBuffer[textureColorLimit] = textureColorBound - Math.max(0.0F, Math.min(rocketsElytraBound, hashHuntBound)) * styleHudFactor2;
               textureColorLimit++;
            }
         }

         int releaseRight = NativeHud.getReleaseRight();
         float nativeHudDivisor = (styleHudValue2 - styleHudValue) * 0.14F;
         NativeHud.handleTextureColor(this.hudStyleAmounts, this.styleHudColorBuffer, textureColorLimit, styleHudValue3, textureColorBound, releaseRight, 0.42F, styleHudValue, nativeHudDivisor);
         NativeHud.handleTextureColor(this.hudStyleAmounts, this.styleHudColorBuffer, textureColorLimit, 1.4F, releaseRight, styleHudValue, nativeHudDivisor);
         float nativeHudValue = this.hudStyleAmounts[textureColorLimit - 1];
         float y = this.styleHudColorBuffer[textureColorLimit - 1];
         float pilotStatusBound = 3.4F;
         float styleHudValue24 = 4.0F;
         NativeHud.handlePeekPosition(nativeHudValue - pilotStatusBound / 2.0F, y - pilotStatusBound / 2.0F, pilotStatusBound, pilotStatusBound, pilotStatusBound / 2.0F, NativeHud.getValue(releaseRight, 0.55F), styleHudValue24, true);
         NativeHud.render(null, nativeHudValue, y, pilotStatusBound, -1);
      }
   }

   public void renderScaledMatrices(DrawContext drawContext) {
      this.scaledMatricesActive = this.dragEditor.isEnabled() && this.isEnabled() && mc.currentScreen instanceof ChatScreen;
      this.drawContext = drawContext;
      if (!this.scaledMatricesActive && this.scaledMatricesWasActive) {
         this.updateTextRelease();
      }

      this.scaledMatricesWasActive = this.scaledMatricesActive;
      boolean scaledMatricesFlag = !ScreenSoundsUtils.isNoScreenOpen();
      if (scaledMatricesFlag && !this.scaledMatricesActive) {
         this.scaledFadeInPending = true;
      } else {
         if (this.scaledFadeInPending && !this.scaledMatricesActive) {
            this.scaledFadeInPending = false;
            this.scaledFadeAnimation.handleAmountFlag(0.0);
            this.scaledFadeAnimation.handleAmountTime(1.0);
         }

         boolean iliIlllilIi = this.isEnabled();
         if (Math.abs(this.scaledMatricesQuintScissor.getTargetAmount() - (iliIlllilIi ? 1.0 : 0.0)) > 0.01) {
            this.scaledMatricesQuintScissor.handleAmountTime(iliIlllilIi ? 1.0 : 0.0);
         }

         float scaledMatricesValue = (float)(this.scaledMatricesQuintScissor.getAmount() * this.scaledFadeAnimation.getAmount());
         if (!iliIlllilIi && scaledMatricesValue < 0.004F) {
            updatePotionsSsa();
         } else {
            if (this.scaledMatricesActive) {
               this.hudResetItems.clear();
               this.render(drawContext);
            }

            double scaleFactor = mc.getWindow().getScaleFactor();
            int calculateScaleFactor = mc.getWindow().calculateScaleFactor(getMax(mc), mc.forcesUnicodeFont());
            float scaledMatricesDivisor = (float)(calculateScaleFactor / scaleFactor);
            boolean scaledMatricesFlag3 = Math.abs(scaledMatricesDivisor - 1.0F) > 1.0E-4F;
            int scaledWidth = scaledMatricesFlag3 ? Math.round(mc.getWindow().getScaledWidth() / scaledMatricesDivisor) : mc.getWindow().getScaledWidth();
            int scaledHeight = scaledMatricesFlag3 ? Math.round(mc.getWindow().getScaledHeight() / scaledMatricesDivisor) : mc.getWindow().getScaledHeight();
            int scaledMatricesValue2 = scaledWidth;
            int fieldsCheckedValue = scaledHeight;
            if (scaledMatricesFlag3) {
               CorzClient.fontManager.setMatrixStack(null);
               MatrixPeekUtilsRender.handleMatrixStack(new MatrixStack());
               CorzRoundedUtils.scaledWidth = scaledWidth;
               CorzRoundedUtils.scaledHeight = scaledHeight;
               drawContext.getMatrices().pushMatrix();
               drawContext.getMatrices().scale(scaledMatricesDivisor, scaledMatricesDivisor);
               int value = (int)Math.round(scaleFactor * 100.0);
               if (calculateScaleFactor != this.scaleFactor || value != this.scaledMatricesValue) {
                  this.scaleFactor = calculateScaleFactor;
                  this.scaledMatricesValue = value;
               }
            } else {
               CorzClient.fontManager.setMatrixStack(null);
            }

            this.scaledWidth = scaledWidth;
            this.scaledHeight = scaledHeight;
            ssaPerfgraphCount++;
            this.updateLinkedTime();
            boolean hasPlayer = mc.player != null;
            this.handle(this.quartScaledMatrices, this.brandScaledMatrices.isEnabled() || (this.performance.isEnabled() || this.serverInfo.isEnabled()) && hasPlayer);
            this.handle(this.quart, (this.coordinates.isEnabled() || this.radarLerpUuid.isEnabled()) && hasPlayer);
            this.handle(this.quintScissor, this.clockScaledMatrices.isEnabled());
            this.handle(this.quartBuild, this.buildProgress.isEnabled() && CardStatusUtilsActive.getCardStatusSnapshot().isLoadedTotal());
            this.handle(this.potionsAnimation, this.potionsScaledMatrices.isEnabled() && hasPlayer);
            this.handle(this.moduleListAnimation, this.moduleList.isEnabled());
            boolean scaledMatricesFlag4 = this.targetHud.isEnabled()
               && hasPlayer
               && this.entity != null
               && !this.entity.isRemoved()
               && (!this.targetPlayersOnly.isEnabled() || this.entity instanceof PlayerEntity)
               && System.currentTimeMillis() - this.lastTimeMillis < 1500L;
            if (Math.abs(this.smoothQuintScissor.getTargetAmount() - (scaledMatricesFlag4 ? 1.0 : 0.0)) > 0.01) {
               this.smoothQuintScissor.setValue(scaledMatricesFlag4 ? 360 : 200);
               this.smoothQuintScissor.handleAmountTime(scaledMatricesFlag4 ? 1.0 : 0.0);
            }

            this.handle(this.keystrokesAnimation, this.keystrokesScaledMatrices.isEnabled() && hasPlayer);
            this.handle(this.cpsAnimation, this.cpsScaledMatrices.isEnabled() && hasPlayer);
            this.handle(this.serverInfoAnimation, this.serverInfo.isEnabled() && hasPlayer);
            this.handle(this.hudQuintScissor, this.armorHud.isEnabled() && hasPlayer);
            boolean scaledMatricesFlag5 = scaledMatricesValue < 0.999F;
            MatrixStack matrixStack = CorzClient.fontManager.getMatrixStack();
            MatrixStack matrixStack2 = MatrixPeekUtilsRender.getMatrixStack();
            float panelOverlay = 1.0F;
            if (scaledMatricesFlag5) {
               float y = -3.0F * (1.0F - scaledMatricesValue);
               panelOverlay = CorzRoundedUtils.getPanelOverlay(scaledMatricesValue);
               matrixStack.push();
               matrixStack.translate(0.0F, y, 0.0F);
               if (matrixStack2 != matrixStack) {
                  matrixStack2.push();
                  matrixStack2.translate(0.0F, y, 0.0F);
               }

               drawContext.getMatrices().pushMatrix();
               drawContext.getMatrices().translate(0.0F, y);
            }

            try {
               this.handleAmountCoords("buildprogress");
               this.renderAmountTime(drawContext, scaledMatricesValue2, fieldsCheckedValue);
               this.updateTextAmounts();
               this.renderCoordsRadar(drawContext, scaledMatricesValue2, fieldsCheckedValue);
               this.handleAmountsHotbar(false, true);
            } finally {
               if (this.hudScale != null) {
                  NativeHud.renderPopMatrices(drawContext, this.buildprogressFlag);
                  this.hudScale = null;
                  this.buildprogressFlag = false;
               }

               if (scaledMatricesFlag5) {
                  drawContext.getMatrices().popMatrix();
                  if (matrixStack2 != matrixStack) {
                     matrixStack2.pop();
                  }

                  matrixStack.pop();
                  CorzRoundedUtils.handlePanelOverlay(panelOverlay);
               }
            }

            if (this.scaledMatricesActive) {
               double[] scaledWidth2 = this.getScaledWidth(scaledMatricesDivisor);
               this.handleAmountText(scaledWidth2[0], scaledWidth2[1]);
               this.renderDragReset(drawContext, this.scaledMatricesAmount, this.dragMouseY);
            }

            if (scaledMatricesFlag3) {
               drawContext.getMatrices().popMatrix();
               CorzRoundedUtils.scaledWidth = 0.0F;
               CorzRoundedUtils.scaledHeight = 0.0F;
               CorzClient.fontManager.setMatrixStack(null);
               MatrixPeekUtilsRender.handleMatrixStack(new MatrixStack());
            }
         }
      }
   }

   private void handleBottomLeft(String bottomLeftText, ShowHUD.HudScale hudScale, float bottomLeftValue, float hudScaleValue, float bottomFactor, float radarSsaFactor) {
      float[] hudmodulesmap = this.hudModulesMap.get(bottomLeftText);
      if (hudmodulesmap != null) {
         this.bottomAmount = getAmountForFloat(hudmodulesmap[0]);
         this.bottomLeftAmount = getAmountForFloat(hudmodulesmap[1]);
         if (hudScale != null) {
            hudScale.bottomLeftActive = false;
         }
      } else {
         this.handleBottomLeftForStringHudScale(bottomLeftText, hudScale, bottomLeftValue, hudScaleValue, bottomFactor, radarSsaFactor);
      }
   }

   private static void handleTranslatePush(MatrixStack matrices, float x, float y, float x2) {
      matrices.push();
      matrices.translate(x, y, 0.0F);
      matrices.scale(x2, x2, 1.0F);
      matrices.translate(-x, -y, 0.0F);
   }

   private void updateTextAmounts() {
      ShowHUD.HudScale hudScale = this.hudScale;
      if (hudScale != null) {
         NativeHud.renderPopMatrices(this.drawContext, this.buildprogressFlag);
         this.hudScale = null;
         this.buildprogressFlag = false;
      }

      if (this.coordsText != null && this.coordsTable != null) {
         this.modulesHudMap.put(this.coordsText, this.coordsTable);
         float hudValue = this.coordsTable[0] + this.coordsOffsetX;
         float hudScaleValue = this.coordsTable[1] + this.coordsOffsetY;
         float hudValue3 = this.coordsTable[2];
         float maxRadarSize = this.coordsTable[3];
         if (hudScale != null) {
            float hudValue5 = hudScale.getAmount(hudValue + hudValue3);
            float amountForFloat = hudScale.getAmountForFloat(hudScaleValue + maxRadarSize);
            hudValue = hudScale.getAmount(hudValue);
            hudScaleValue = hudScale.getAmountForFloat(hudScaleValue);
            hudValue3 = hudValue5 - hudValue;
            maxRadarSize = amountForFloat - hudScaleValue;
         }

         if (this.defaultsLayoutFlag && this.coordsText.equals(this.dragReleaseText)) {
            this.handleHotbarAmounts(this.coordsText, this.coordsTable[0], this.coordsTable[1], hudValue3, maxRadarSize);
         } else {
            this.handleHotbarAmounts(this.coordsText, hudValue - this.defaultsHud, hudScaleValue - this.defaultsHudCoords, hudValue3, maxRadarSize);
         }

         if (!this.hudModulesMap.containsKey(this.coordsText) && this.hudOffsetsMap.containsKey(this.coordsText) && !this.coordsText.equals(this.dragReleaseText)) {
            this.handleScaledWidth(this.coordsText, hudValue - this.defaultsHud, hudScaleValue - this.defaultsHudCoords, hudValue3, maxRadarSize);
         }
      }

      this.coordsText = null;
      this.coordsOffsetX = 0.0F;
      this.coordsOffsetY = 0.0F;
      this.defaultsHud = 0.0F;
      this.defaultsHudCoords = 0.0F;
      this.coordsTable = null;
   }

   private void handleAmount(float hudValue, float... hudValues) {
      float abs2 = Math.abs(this.hotbarAmount - hudValue);
      if (abs2 <= 5.0F) {
         this.handleAmountMin(hudValue, abs2, hudValues);
      }
   }

   private float getAmount(String hudText) {
      return this.getScaledHud(hudText)[1] + this.getDefaultsHudForString(hudText);
   }

   private void handleHudHotbar(String hudHotbarText, int hudHotbarValue) {
      ShowHUD.HudScale hotbarhudmap = this.hotbarHudMap.get(hudHotbarText);
      float hudHotbarValue2 = hotbarhudmap == null ? getCoordsPerf(hudHotbarText) : hotbarhudmap.hudScaleFactor;
      this.handleHudHotbar(hudHotbarText, Math.round(hudHotbarValue2 / 0.05F + hudHotbarValue) * 0.05F);
   }

   private boolean canAmountPop(DrawContext context, float popValue, float popValue2, long nanoTime) {
      VertexTexturePeek vertexTexture = getVertexTexture();
      boolean corzclientFlag = vertexTexture.isCorzclientFlag();
      if (this.popPop.canTimeAmount(this.performance.isEnabled() && corzclientFlag, ssaPerfgraphCount) && corzclientFlag) {
         this.handleTpsSignal(vertexTexture, nanoTime);
         float glfwLimit = popValue + this.coordsOffsetX;
         float hudScaleValue = popValue2 + this.coordsOffsetY;
         render(context, vertexTexture, NativeHud.TPS_SIGNAL_BAR_COUNT, glfwLimit, hudScaleValue, this.h2Segs, this.tpsSignalTable, 3, this.tpsSignalAmount, this.popPop);
         this.handleTextHud(glfwLimit, hudScaleValue, this.tpsSignalAmount, 20.0F);
         return true;
      } else {
         return false;
      }
   }

   private void handleBottomAmount(String releaseResetText, double bottomValue, double bottomValue2) {
      float[] moduleshudmap = this.modulesHudMap.get(releaseResetText);
      if (moduleshudmap != null) {
         ShowHUD.HudScale hotbarhudmap = this.hotbarHudMap.get(releaseResetText);
         if (hotbarhudmap == null) {
            hotbarhudmap = new ShowHUD.HudScale();
            this.hotbarHudMap.put(releaseResetText, hotbarhudmap);
         }

         float bottomLeftValue = moduleshudmap[0] + this.getAmount5(releaseResetText);
         float hudScaleValue = moduleshudmap[1] + this.getAmount(releaseResetText);
         this.handleBottomLeft(releaseResetText, hotbarhudmap, bottomLeftValue, hudScaleValue, moduleshudmap[2], moduleshudmap[3]);
         this.bottomOriginX = bottomLeftValue + this.bottomAmount * moduleshudmap[2];
         this.bottomOriginY = hudScaleValue + this.bottomLeftAmount * moduleshudmap[3];
         this.bottomDirectionX = ((this.bottomAmount >= 0.75F ? 0.0F : 1.0F) - this.bottomAmount) * moduleshudmap[2];
         this.bottomDirectionY = ((this.bottomLeftAmount >= 0.75F ? 0.0F : 1.0F) - this.bottomLeftAmount) * moduleshudmap[3];
         this.releaseResetText = releaseResetText;
         this.bottomProjectionEnd = hotbarhudmap.hudScaleFactor - this.getAmount(bottomValue, bottomValue2);
      }
   }

   private void handleCapssFields(int index, float capssFieldsValue, float nativeHudValue, float nativeHudFactor, Color color) {
      float maxRadarSize = 9.5F;
      NativeHud.handleTextsAmounts(this.capss[index], capssFieldsValue, NativeHud.getAmount2(nativeHudValue, maxRadarSize, 0.66F), 0.66F, NativeHud.GRAY);
      NativeHud.handleAmount(this.fieldsTotemsFields[index], capssFieldsValue + nativeHudFactor, NativeHud.getAmount2(nativeHudValue, maxRadarSize, 0.8F), 0.8F, color);
   }

   private void handleHudHotbar(String hudHotbarText, float max) {
      if (hudHotbarText != null) {
         max = Math.max(0.6F, Math.min(1.8F, Math.round(max * 100.0F) / 100.0F));
         ShowHUD.HudScale hotbarhudmap = this.hotbarHudMap.get(hudHotbarText);
         if (hotbarhudmap == null) {
            if (max == getCoordsPerf(hudHotbarText)) {
               return;
            }

            hotbarhudmap = new ShowHUD.HudScale();
            this.hotbarHudMap.put(hudHotbarText, hotbarhudmap);
         }

         if (hotbarhudmap.hudScaleFactor != max) {
            hotbarhudmap.hudScaleFactor = max;
            ModuleConfigAutosave.updateFlag2();
         }
      }
   }

   public static void updateScalesHud() {
      ShowHUD showHUD = getShowHUD();
      if (showHUD != null) {
         showHUD.hudScales = false;
      }
   }

   private boolean canAmountsScaled(float scaledValue, float hashHuntBound, float scaledValue3, float scaledValue4) {
      if (this.canAmountsHotbar(scaledValue, hashHuntBound, scaledValue3, scaledValue4)) {
         return false;
      } else {
         float maxValue = Float.MAX_VALUE;
         float scaledValue6 = 6.0F;

         for (int i = 0; i < this.hotbarItemCount; i++) {
            if (this.canHotbarTexts(i) && canFlag(scaledValue, hashHuntBound, scaledValue3, scaledValue4, this.hotbarTable[i], this.hotbarCurrentYs[i], this.hotbarWidths[i], this.hotbarHeights[i])) {
               float scaledValue7 = this.hotbarTable[i];
               float scaledValue8 = this.hotbarCurrentYs[i];
               float scaledValue9 = this.hotbarWidths[i];
               float scaledValue10 = this.hotbarHeights[i];

               for (int j = 1; j < 5; j++) {
                  float amount = j == 1 ? scaledValue7 - scaledValue6 - scaledValue3 : (j == 2 ? scaledValue7 + scaledValue9 + scaledValue6 : scaledValue);
                  float amount2 = j == 3 ? scaledValue8 - scaledValue6 - scaledValue4 : (j == 4 ? scaledValue8 + scaledValue10 + scaledValue6 : hashHuntBound);
                  float scaledLimit = Math.abs(amount - scaledValue) + Math.abs(amount2 - hashHuntBound);
                  if (!(scaledLimit > 20.0F)
                     && !(scaledLimit >= maxValue)
                     && !(amount < 0.0F)
                     && !(amount2 < 0.0F)
                     && !(amount > this.scaledWidth - scaledValue3)
                     && !(amount2 > this.scaledHeight - scaledValue4)
                     && this.canAmountsHotbar(amount, amount2, scaledValue3, scaledValue4)) {
                     maxValue = scaledLimit;
                     this.scaledBestPosition = amount;
                     this.scaledAmount = amount2;
                  }
               }
            }
         }

         return maxValue != Float.MAX_VALUE;
      }
   }

   private static VertexTexturePeek getVertexTexture() {
      VertexTexturePeek regular = VertexTexturePeek.getRegular();
      return regular.isCorzclientFlag() ? regular : VertexTexturePeek.getSuisse();
   }

   private void handle(QuintScissor quintScissor, boolean hudFlag) {
      double hudValue = hudFlag ? 1.0 : 0.0;
      if (Math.abs(quintScissor.getTargetAmount() - hudValue) > 0.01) {
         quintScissor.handleAmountTime(hudValue);
      }
   }

   private void renderModulesKeybinds(DrawContext context, int modulesKeybindsValue, int fieldsCheckedValue) {
      this.modulesFlag = false;
      var timeEnum = this.getTimeEnum();
      int max = Math.max(4, (int)((fieldsCheckedValue - 6.0F - 30.0F - 5.0F - 64.0F) / 13.5F));
      if (this.cachedEnumCount != this.enumCount || max != this.modulesKeybindsValue) {
         this.cachedEnumCount = this.enumCount;
         this.modulesKeybindsValue = max;
         int varSize = timeEnum.size();
         int modulesKeybindsLimit = varSize > max ? max - 1 : varSize;
         this.modulesKeybindsNList.update();

         for (int i = 0; i < modulesKeybindsLimit; i++) {
            this.modulesKeybindsNList.handleShowHud((Module)timeEnum.get(i));
         }

         this.modulesKeybindsNList.updateFlag();
         if (varSize > 0 && this.modulesKeybindsField.canMax(varSize)) {
            this.modulesKeybindsField.handleAmountTime(String.valueOf(varSize), 0.8F, true, false);
            this.modulesKeybindsPulse.updateTime();
         }

         int deltaVar = varSize - modulesKeybindsLimit;
         if (this.moreCountField.canMax(deltaVar)) {
            this.moreCountField.handleAmountTime(deltaVar > 0 ? "+" + deltaVar + " more" : "", 0.78F, false, false);
         }
      }

      this.modulesKeybindsNList.handleKeybindsKeyboard(1.0F - (float)Math.exp(-this.frameDeltaSeconds / 0.06F));
      float maxRadarSize = this.hudSmooth.getMax(this.moreCountField.isEmpty() ? 0.0F : 13.5F, this.frameDeltaSeconds, 0.06F, 0.01F);
      if (!this.modulesKeybindsPop.canTimeAmount(this.moduleList.isEnabled() && this.modulesKeybindsNList.keybindsKeystrokesFlag, ssaPerfgraphCount)) {
         this.modulesKeybindsSmooth.updateMax();
      } else {
         float glfwLimit = this.modulesKeybindsSmooth
            .getAmount(
               (float)Math.ceil(
                  Math.max(
                     NativeHud.getAmount2(this.keybindsModulesAmount, this.modulesKeybindsField.checkedRocketsAmount),
                     Math.max(this.modulesKeybindsNList.maxRowWidth, this.moreCountField.textWidth) + 14.0F
                  )
               ),
               this.frameDeltaSeconds,
               0.08F
            );
         float modulesKeybindsValue4 = 30.0F + this.modulesKeybindsNList.keybindsKeyboardAmount + maxRadarSize + 5.0F;
         float roundSsaValue = modulesKeybindsValue - 6.0F - glfwLimit;
         float configsLikeValue = 6.0F;
         this.roundedModulesWidth = Math.round(roundSsaValue);
         this.personOnlineValue = Math.round(configsLikeValue);
         this.roundModulesValue = Math.round(glfwLimit);
         this.modulesFlag = true;
         float[] hudmodulesmap = this.hudModulesMap.get("modules");
         float[] moduleshudmap = this.modulesHudMap.get("modules");
         float modulesKeybindsValue7 = (hudmodulesmap != null && hudmodulesmap[0] <= 0.25F && moduleshudmap != null ? moduleshudmap[0] : roundSsaValue) + this.coordsOffsetX;
         float hudScaleValue = configsLikeValue + this.coordsOffsetY;
         this.handleTextHud(modulesKeybindsValue7, hudScaleValue, glfwLimit, modulesKeybindsValue4);
         this.handleRoundSsa(roundSsaValue, configsLikeValue, glfwLimit);
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(this.modulesKeybindsPop.getPopProgress());
         boolean translateMatrices = NativeHud.canTranslateMatrices(context, modulesKeybindsValue7 + glfwLimit / 2.0F, hudScaleValue + modulesKeybindsValue4 / 2.0F, this.modulesKeybindsPop.getAmount());

         try {
            NativeHud.handle(NativeHud.gridLayoutValue, modulesKeybindsValue7, hudScaleValue, glfwLimit, modulesKeybindsValue4, 1.0F);
            float colorAmount = NativeHud.getColorAmount(
               context,
               modulesKeybindsValue7,
               hudScaleValue,
               glfwLimit,
               IcoGenUtils.getIdentifier("layout-grid"),
               NativeHud.WHITE,
               "Modules",
               this.modulesKeybindsField,
               this.modulesKeybindsPulse,
               getExperienceProgress(this.modulesKeybindsNList.keybindsKeyboardAmount / 8.0F)
            );
            this.modulesKeybindsNList.handleAmountText(colorAmount, modulesKeybindsValue7 + 7.0F, modulesKeybindsValue7 + glfwLimit - 7.0F);
            if (maxRadarSize > 0.5F && !this.moreCountField.isEmpty()) {
               float panelOverlay2 = CorzRoundedUtils.getPanelOverlay(maxRadarSize / 13.5F);

               try {
                  NativeHud.handlePushScale(
                     this.moreCountField.bloodhoundBestText, modulesKeybindsValue7 + 7.0F, NativeHud.getAmount2(colorAmount + this.modulesKeybindsNList.keybindsKeyboardAmount, maxRadarSize, 0.78F), 0.78F, NativeHud.GRAY
                  );
               } finally {
                  CorzRoundedUtils.handlePanelOverlay(panelOverlay2);
               }
            }
         } finally {
            NativeHud.renderPopMatrices(context, translateMatrices);
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
         }
      }
   }

   private void handleAmountCoords(String text) {
      this.coordsText = text;
      this.coordsTable = null;
      float[] scaledHud = this.getScaledHud(text);
      this.defaultsHud = this.getDefaultsHud(text);
      this.defaultsHudCoords = this.getDefaultsHudForString(text);
      this.coordsOffsetX = scaledHud[0] + this.defaultsHud;
      this.coordsOffsetY = scaledHud[1] + this.defaultsHudCoords;
      if (this.defaultsFlag && text.equals(this.dragReleaseText) && getFlowHud(text) >= 0) {
         this.savedBaseX = this.radarCoordsAmount;
         this.savedBaseY = this.radarCoordsAmount2CoordsRada;
         this.coordsOffsetX = this.elementX - this.radarCoordsAmount;
         this.coordsOffsetY = this.elementY - this.radarCoordsAmount2CoordsRada;
      }

      if (!this.hotbarHudMap.isEmpty()) {
         ShowHUD.HudScale hudScale = this.hotbarHudMap.get(text);
         float[] coordsValues = hudScale == null ? null : this.modulesHudMap.get(text);
         if (coordsValues != null && this.canAmountBottom(text, hudScale, coordsValues[0] + this.coordsOffsetX, coordsValues[1] + this.coordsOffsetY, coordsValues[2], coordsValues[3])) {
            this.hudScale = hudScale;
            this.buildprogressFlag = NativeHud.canTranslateMatrices(this.drawContext, hudScale.translateX, hudScale.translateAmount, hudScale.coordsPerf);
         }
      }
   }

   private static void updatePotionsSsa() {
      potionsSsaTime = -1L;
      ssaPotionsTime = -1L;
      ssaPerfgraphCountRoundSsa = -1L;
      ssaPerfgraphCountPotionsSsa = -1L;
   }

   private static String formatPath(Identifier id2) {
      String path = id2.getPath();
      StringBuilder stringBuilder = new StringBuilder(path.length() + 4);
      boolean pathFlag = true;

      for (int i = 0; i < path.length(); i++) {
         char charAt = path.charAt(i);
         if (charAt == '_' || charAt == '-' || charAt == ' ') {
            stringBuilder.append(' ');
            pathFlag = true;
         } else if (pathFlag) {
            stringBuilder.append(Character.toUpperCase(charAt));
            pathFlag = false;
         } else {
            stringBuilder.append(charAt);
         }
      }

      String trim = stringBuilder.toString().trim();
      return trim.isEmpty() ? id2.toString() : trim;
   }

   public static float getKeybindsResources() {
      return canScreenTotems(keybindsResourcesValue) ? fieldsTotemsAmount : 0.0F;
   }

   private boolean isEquals() {
      return Arrays.equals((Object[])this.clone, (Object[])hudFlowTexts);
   }

   private void handleOnlineNone(StaffList staffList, long onlineNoneValue) {
      for (int i = 0; i < this.onlineNoneItems.size(); i++) {
         this.onlineNoneItems.get(i).updateAmountFlag();
      }

      ClientPlayNetworkHandler networkHandler = mc.getNetworkHandler();
      var rosterStaff = staffList.getStaffMembers();
      this.rosterStaff = staffList.getOnlineStaffCount();
      int value = 0;

      for (int j = 0; j < rosterStaff.size(); j++) {
         Member member = (Member)rosterStaff.get(j);
         String onlineNoneText = member.memberText;
         ShowHUD.NStaff nStaff = this.onlineNoneNStaffMap.get(onlineNoneText);
         if (nStaff == null) {
            nStaff = new ShowHUD.NStaff(onlineNoneText);
            nStaff.onlineNoneAmount = NativeHud.getAmount3(onlineNoneText, 0.9F);
            nStaff.handleAmount(this.onlineNoneItems.size() * 15.0F);
            this.onlineNoneNStaffMap.put(onlineNoneText, nStaff);
            this.onlineNoneItems.add(nStaff);
         }

         nStaff.updateAmountFlagDurationActive();
         nStaff.handlePersonTime(member.displayColorRgb, onlineNoneValue);
         PlayerListEntry playerListEntry = member.uUID != null && networkHandler != null ? networkHandler.getPlayerListEntry(member.uUID) : null;

         try {
            nStaff.skinTextures = playerListEntry == null ? null : playerListEntry.getSkinTextures();
         } catch (Throwable e) {
            nStaff.skinTextures = null;
         }

         value++;
      }

      for (int k = 1; k < this.onlineNoneItems.size(); k++) {
         ShowHUD.NStaff onlinenoneitem = this.onlineNoneItems.get(k);

         int onlineNoneCounter;
         for (onlineNoneCounter = k - 1; onlineNoneCounter >= 0 && canPersonInsensitive(onlinenoneitem, this.onlineNoneItems.get(onlineNoneCounter)); onlineNoneCounter--) {
            this.onlineNoneItems.set(onlineNoneCounter + 1, this.onlineNoneItems.get(onlineNoneCounter));
         }

         this.onlineNoneItems.set(onlineNoneCounter + 1, onlinenoneitem);
      }

      int onlineNoneCounter2 = 0;

      for (int m = 0; m < this.onlineNoneItems.size(); m++) {
         ShowHUD.NStaff onlinenoneitem2 = this.onlineNoneItems.get(m);
         if (!onlinenoneitem2.durationActive) {
            onlinenoneitem2.onlineNoneAmount = onlineNoneCounter2++ * 15.0F;
         }
      }

      this.onlineNoneValue = value;
      this.person = staffList.isShowCount();
      if (this.onlineNoneField.canMax(value)) {
         this.onlineNoneField.handleAmountTime(String.valueOf(value), 0.8F, true, false);
         this.onlineNonePulse.updateTime();
      }
   }

   private static String getFormat(double hudValue) {
      return String.format("%.0f", hudValue);
   }

   private void renderFieldsTotems(DrawContext context, int fieldsTotemsValue) {
      ClientPlayerEntity player = mc.player;
      boolean fieldsTotemsFlag = this.vitalsCard.isEnabled()
         && mc.interactionManager != null
         && mc.interactionManager.hasStatusBars()
         && !(player.getVehicle() instanceof LivingEntity);
      int air = player.getAir();
      int maxMaxAir = Math.max(1, player.getMaxAir());
      float fieldsTotemsFactor = this.airBarSmooth.getMax(fieldsTotemsFlag && air < maxMaxAir ? 1.0F : 0.0F, this.frameDeltaSeconds, 0.08F, 0.002F);
      float fieldsTotemsValue2 = 15.0F;
      float fieldsTotemsValue3 = 5.0F;
      float maxRadarSize = NativeHud.getAmount4(13.0F + fieldsTotemsValue2 * 2.0F + fieldsTotemsValue3 + (fieldsTotemsValue2 + fieldsTotemsValue3) * fieldsTotemsFactor);
      double scaleFactor = mc.getWindow().getScaleFactor();
      float keybindsResourcesAmount2 = (float)(mc.getWindow().calculateScaleFactor(getMax(mc), mc.forcesUnicodeFont()) / scaleFactor);
      ClientPlayNetworkHandler networkHandler = mc.getNetworkHandler();
      boolean fieldsTotemsFlag2 = networkHandler != null && networkHandler.getWaypointHandler().hasWaypoint();
      float amountForFloat = this.totemsSpacingSmooth.getAmountForFloat(fieldsTotemsFlag2 ? NativeHud.getAmount4(17.0F / keybindsResourcesAmount2) : 6.0F, this.frameDeltaSeconds, 0.08F);
      float fieldsTotemsLimit = fieldsTotemsFlag && !this.canHudOffsets("vitals") ? amountForFloat + maxRadarSize * this.getHudScale() : 0.0F;
      if (this.toggle == null) {
         this.toggle = ModuleRegistry.getModule(TotemCounter.class);
      }

      boolean fieldsTotemsFlag3 = fieldsTotemsLimit > 0.0F && this.toggle != null && this.toggle.isEnabled() && !this.canHudOffsets("totems");
      fieldsTotemsAmount = this.fieldsTotemsSmooth.getAmountForFloat(fieldsTotemsLimit, this.frameDeltaSeconds, 0.08F);
      totemsAmount = this.totemsOffsetSmooth.getAmountForFloat(fieldsTotemsFlag3 ? fieldsTotemsLimit + 6.0F + 16.0F * this.getHotbarHud("totems") : fieldsTotemsLimit, this.frameDeltaSeconds, 0.08F);
      keybindsResourcesAmount = keybindsResourcesAmount2;
      keybindsResourcesValue = hotbarTotemsCount;
      if (this.fieldsTotemsPop.canTimeAmount(fieldsTotemsFlag, ssaPerfgraphCount)) {
         keybindsLevelValue = hotbarTotemsCount;
         float health = player.getHealth();
         float maxMaxHealth = Math.max(1.0F, player.getMaxHealth());
         int ceil5 = MathHelper.ceil(health);
         int ceilAbsorptionAmount = MathHelper.ceil(player.getAbsorptionAmount());
         if (this.fieldsTotemsFields[0].canMax(ceil5 * 4096L + ceilAbsorptionAmount)) {
            this.fieldsTotemsFields[0].handleAmountTime(ceilAbsorptionAmount > 0 ? ceil5 + "+" + ceilAbsorptionAmount : String.valueOf(ceil5), 0.8F, true, true);
         }

         int foodLevel = player.getHungerManager().getFoodLevel();
         if (this.fieldsTotemsFields[1].canMax(foodLevel)) {
            this.fieldsTotemsFields[1].handleAmountTime(String.valueOf(foodLevel), 0.8F, true, true);
         }

         int armor = player.getArmor();
         if (this.fieldsTotemsFields[2].canMax(armor)) {
            this.fieldsTotemsFields[2].handleAmountTime(String.valueOf(armor), 0.8F, true, true);
         }

         int experienceLevel = player.experienceLevel;
         if (this.fieldsTotemsFields[3].canMax(experienceLevel)) {
            this.fieldsTotemsFields[3].handleAmountTime(String.valueOf(experienceLevel), 0.8F, true, true);
         }

         int pilotElytraValue = MathHelper.ceil(Math.max(0, air) * 10.0F / maxMaxAir);
         if (this.fieldsTotemsFields[4].canMax(pilotElytraValue)) {
            this.fieldsTotemsFields[4].handleAmountTime(String.valueOf(pilotElytraValue), 0.8F, true, true);
         }

         float max7 = this.fieldsTotemsSmooths[0].getMax(getExperienceProgress(health / maxMaxHealth), this.frameDeltaSeconds, 0.12F, 0.001F);
         float nativeHudLimit = this.fieldsTotemsSmooths[1].getMax(getExperienceProgress(foodLevel / 20.0F), this.frameDeltaSeconds, 0.12F, 0.001F);
         float max9 = this.fieldsTotemsSmooths[2].getMax(getExperienceProgress(armor / 20.0F), this.frameDeltaSeconds, 0.12F, 0.001F);
         float max10 = this.fieldsTotemsSmooths[3].getMax(getExperienceProgress(player.experienceProgress), this.frameDeltaSeconds, 0.12F, 0.001F);
         float max11 = this.fieldsTotemsSmooths[4].getMax(getExperienceProgress((float)air / maxMaxAir), this.frameDeltaSeconds, 0.12F, 0.001F);
         float cardMatrixFactor = this.lowHealthSmooth.getMax(health < 6.0F ? 1.0F : 0.0F, this.frameDeltaSeconds, 0.12F, 0.002F);
         float max3 = this.lowFoodSmooth.getMax(foodLevel <= 6.0F ? 1.0F : 0.0F, this.frameDeltaSeconds, 0.12F, 0.002F);
         int releaseRight = NativeHud.getReleaseRight();
         float hudFactor = 184.0F;
         float fieldsTotemsValue13 = 79.0F;
         float fieldsTotemsValue14 = (mc.getWindow().getScaledHeight() - 22.0F) / keybindsResourcesAmount2 - getHotbarOffsets();
         float fieldsTotemsValue15 = NativeHud.getAmount4((fieldsTotemsValue - hudFactor) / 2.0F) + this.coordsOffsetX;
         float hudScaleValue = NativeHud.getAmount4(fieldsTotemsValue14 - amountForFloat - maxRadarSize) + this.coordsOffsetY;
         this.handleTextHud(fieldsTotemsValue15, hudScaleValue, hudFactor, maxRadarSize);
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(this.fieldsTotemsPop.getPopProgress());
         boolean translateMatrices = NativeHud.canTranslateMatrices(context, fieldsTotemsValue15 + hudFactor / 2.0F, hudScaleValue + maxRadarSize / 2.0F, this.fieldsTotemsPop.getAmount());

         try {
            NativeHud.handle(NativeHud.fieldsTotemsValue, fieldsTotemsValue15, hudScaleValue, hudFactor, maxRadarSize, 1.0F);
            float fieldsTotemsValue17 = fieldsTotemsValue15 + 8.0F;
            float capssFieldsValue = fieldsTotemsValue17 + fieldsTotemsValue13 + 10.0F;
            float nativeHudValue = hudScaleValue + maxRadarSize - 7.0F - fieldsTotemsValue2;
            float fieldsTotemsValue20 = nativeHudValue - fieldsTotemsValue3 - fieldsTotemsValue2;
            float fieldsTotemsValue21 = 12.5F;
            float nativeHudBound = 2.5F;
            if (fieldsTotemsFactor > 0.01F) {
               float nativeHudFactor = hudFactor - 16.0F;
               float fieldsTotemsValue24 = hudScaleValue + 6.0F;
               float panelOverlay2 = CorzRoundedUtils.getPanelOverlay(fieldsTotemsFactor * fieldsTotemsFactor);

               try {
                  this.handleCapssFields(4, fieldsTotemsValue17, fieldsTotemsValue24, nativeHudFactor, NativeHud.OFF_WHITE);
                  NativeHud.handle(fieldsTotemsValue17, fieldsTotemsValue24 + fieldsTotemsValue21, nativeHudFactor, nativeHudBound, max11, releaseRight);
               } finally {
                  CorzRoundedUtils.handlePanelOverlay(panelOverlay2);
               }
            }

            this.handleCapssFields(0, fieldsTotemsValue17, fieldsTotemsValue20, fieldsTotemsValue13, cardMatrixFactor > 0.5F ? NativeHud.RED : NativeHud.OFF_WHITE);
            NativeHud.handle(fieldsTotemsValue17, fieldsTotemsValue20 + fieldsTotemsValue21, fieldsTotemsValue13, nativeHudBound, max7, CardMatrixUtils.getValue(releaseRight, -1750963, cardMatrixFactor));
            this.handleCapssFields(1, capssFieldsValue, fieldsTotemsValue20, fieldsTotemsValue13, max3 > 0.5F ? NativeHud.RED : NativeHud.OFF_WHITE);
            NativeHud.handle(capssFieldsValue, fieldsTotemsValue20 + fieldsTotemsValue21, fieldsTotemsValue13, nativeHudBound, nativeHudLimit, CardMatrixUtils.getValue(releaseRight, -1750963, max3));
            this.handleCapssFields(2, fieldsTotemsValue17, nativeHudValue, fieldsTotemsValue13, NativeHud.OFF_WHITE);
            NativeHud.handle(fieldsTotemsValue17, nativeHudValue + fieldsTotemsValue21, fieldsTotemsValue13, nativeHudBound, max9, releaseRight);
            this.handleCapssFields(3, capssFieldsValue, nativeHudValue, fieldsTotemsValue13, NativeHud.OFF_WHITE);
            NativeHud.handle(capssFieldsValue, nativeHudValue + fieldsTotemsValue21, fieldsTotemsValue13, nativeHudBound, max10, releaseRight);
         } finally {
            NativeHud.renderPopMatrices(context, translateMatrices);
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
         }
      }
   }

   private static void render(DrawContext context, float hudValue, float configsLikeValue, float glfwLimit, float experienceProgressLimit) {
      float hudValue5 = MatrixPeekUtilsRender.getAmount();
      MatrixPeekUtilsRender.renderMatrixPeek(context, getAmount5(hudValue), Math.round(configsLikeValue) + hudValue5, 0.5F, Math.round(glfwLimit) - hudValue5 * 2.0F, getValue(721420287, experienceProgressLimit));
   }

   private float getTextsStaff(DrawContext context, float staffValue, float nativeHudValue) {
      float staffValue3 = 18.0F;
      float staffValue4 = 4.0F;
      float staffValue5 = 6.0F;
      float staffValue6 = staffValue3 * 3.0F + staffValue4 * 2.0F;
      float panelOverlayLimit = this.staffSmooth.getMax(this.cpsScaledMatrices.isEnabled() ? 1.0F : 0.0F, this.frameDeltaSeconds, 0.08F, 0.002F);
      float amount4 = NativeHud.getAmount4(staffValue3 + 8.0F * panelOverlayLimit);
      float hudFactor = staffValue6 + 2.0F * staffValue5;
      float textsStaff = 2.0F * staffValue5 + staffValue3 * 2.0F + staffValue4 * 3.0F + amount4 + 12.0F;
      if (!this.staffPop.canTimeAmount(this.keystrokesScaledMatrices.isEnabled(), ssaPerfgraphCount)) {
         return textsStaff;
      } else {
         float staffValue10 = staffValue + this.coordsOffsetX;
         float hudScaleValue = nativeHudValue + this.coordsOffsetY;
         this.handleTextHud(staffValue10, hudScaleValue, hudFactor, textsStaff);
         staffValue = NativeHud.getAmount4(staffValue10);
         nativeHudValue = NativeHud.getAmount4(hudScaleValue);
         int index = Math.min(40, this.linkedLinkedList.size());
         int min2 = Math.min(40, this.linkedList.size());
         if (this.staffField.canMax(index)) {
            this.staffField.handleAmountTime(levelKeybindsTexts[index], 0.64F, true, false);
         }

         if (this.staffNameField.canMax(min2)) {
            this.staffNameField.handleAmountTime(levelKeybindsTexts[min2], 0.64F, true, false);
         }

         float panelOverlay = CorzRoundedUtils.getPanelOverlay(this.staffPop.getPopProgress());
         boolean translateMatrices = NativeHud.canTranslateMatrices(context, staffValue + hudFactor / 2.0F, nativeHudValue + textsStaff / 2.0F, this.staffPop.getAmount());

         try {
            NativeHud.handle(NativeHud.staffValue, staffValue, nativeHudValue, hudFactor, textsStaff, 1.0F);
            float staffValue12 = staffValue + staffValue5;
            float staffValue13 = nativeHudValue + staffValue5;
            float staffValue14 = staffValue13 + staffValue3 + staffValue4;
            float staffValue15 = staffValue14 + staffValue3 + staffValue4;
            float matrixStackValue = staffValue15 + amount4 + staffValue4;
            float nativeHudBound = NativeHud.getAmount4((staffValue6 - staffValue4) / 2.0F);
            handleColor(healthFoodTexts[0], this.corzStaffAmounts[0], staffValue12 + staffValue3 + staffValue4, staffValue13, staffValue3, staffValue3, (float)this.keyWPressAnimation.getAmount());
            handleColor(healthFoodTexts[1], this.corzStaffAmounts[1], staffValue12, staffValue14, staffValue3, staffValue3, (float)this.quartLinked.getAmount());
            handleColor(healthFoodTexts[2], this.corzStaffAmounts[2], staffValue12 + staffValue3 + staffValue4, staffValue14, staffValue3, staffValue3, (float)this.keySPressAnimation.getAmount());
            handleColor(healthFoodTexts[3], this.corzStaffAmounts[3], staffValue12 + (staffValue3 + staffValue4) * 2.0F, staffValue14, staffValue3, staffValue3, (float)this.holdKeyAnimation.getAmount());
            handleColor("LMB", this.onlineNoneAmount, this.staffField, staffValue12, staffValue15, nativeHudBound, amount4, (float)this.staffQuintScissor.getAmount(), panelOverlayLimit);
            handleColor(
               "RMB", this.staffAmount, this.staffNameField, staffValue12 + nativeHudBound + staffValue4, staffValue15, staffValue6 - nativeHudBound - staffValue4, amount4, (float)this.linkedQuintScissor.getAmount(), panelOverlayLimit
            );
            float cardMatrixFactor = (float)this.quartStaff.getAmount();
            float hashHuntBound = 12.0F;
            float crossLimit = 16.0F;
            float staffValue21 = 2.0F;
            NativeHud.handle(staffValue12, matrixStackValue, staffValue6, hashHuntBound, cardMatrixFactor);
            NativeHud.handlePeekPosition(
               NativeHud.getAmount4(staffValue12 + (staffValue6 - crossLimit) / 2.0F),
               NativeHud.getAmount4(matrixStackValue + (hashHuntBound - staffValue21) / 2.0F),
               crossLimit,
               staffValue21,
               staffValue21 / 2.0F,
               0.0F,
               CardMatrixUtils.getValue(-7630437, -920587, cardMatrixFactor)
            );
         } finally {
            NativeHud.renderPopMatrices(context, translateMatrices);
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
         }

         return textsStaff;
      }
   }

   private static float getPotionsStaff(VertexTexturePeek vertexTexturePeek, String potionsStaffText, float potionsStaffValue, float peekEndValue, int potionsStaffValue3, float vertexTextureValue) {
      float vertexTexture = getVertexTexture(vertexTexturePeek, vertexTextureValue);
      float potionsStaffTotal = potionsStaffValue;
      int potionsStaffLimit = potionsStaffText.length();
      if (potionsStaffAmounts.length < potionsStaffLimit) {
         potionsStaffAmounts = new float[potionsStaffLimit];
      }

      float[] peekEndValues = potionsStaffAmounts;

      for (int i = 0; i < potionsStaffLimit; i++) {
         char charAt = potionsStaffText.charAt(i);
         if (charAt >= '0' && charAt <= '9') {
            float amountFlag = vertexTexturePeek.getAmountFlag(charAt, vertexTextureValue);
            peekEndValues[i] = potionsStaffTotal + (vertexTexture - amountFlag) / 2.0F;
            potionsStaffTotal += vertexTexture;
         } else {
            peekEndValues[i] = potionsStaffTotal;
            potionsStaffTotal += vertexTexturePeek.getAmountFlag(charAt, vertexTextureValue);
         }
      }

      vertexTexturePeek.handlePeekEnd(potionsStaffText, peekEndValues, potionsStaffLimit, peekEndValue, potionsStaffValue3, vertexTextureValue);
      return potionsStaffTotal - potionsStaffValue;
   }

   private static void render(DrawContext context, float hudValue, float configsLikeValue, float glfwLimit, float hudValue4, float hudValue5, int cardMatrixValue) {
      MatrixPeekUtilsRender.renderMatrixStack(
         context, Math.round(hudValue), Math.round(configsLikeValue), Math.max(0, Math.round(glfwLimit)), Math.max(0, Math.round(hudValue4)), Math.max(0, Math.round(hudValue5)), cardMatrixValue
      );
   }

   private static float getCoordsPerf(String coordsPerfText) {
      return switch (coordsPerfText) {
         case "topbar" -> 0.9F;
         case "modules" -> 0.85F;
         case "perf", "resources", "coords", "staff", "perfgraph", "potions" -> 0.8F;
         default -> 1.0F;
      };
   }

   public static boolean isPotionsSsa() {
      return isInstance() && canPerfgraphSsa(potionsSsaTime);
   }

   private void handleHotbarAmounts(String hotbarText, float hotbarBound, float hashHuntBound, float hotbarValue, float maxRadarSize) {
      if (this.hotbarValue != hotbarTotemsCount) {
         this.hotbarItemCount = 0;
         this.hotbarValue = hotbarTotemsCount;
      }

      if (!(hotbarValue <= 0.0F) && !(maxRadarSize <= 0.0F) && (!"hotbar".equals(hotbarText) || this.canHudOffsets(hotbarText))) {
         for (int i = 0; i < this.hotbarItemCount; i++) {
            if (this.hotbarTexts[i].equals(hotbarText)) {
               float max3 = Math.max(this.hotbarTargetXs[i] + this.hotbarWidths[i], hotbarBound + hotbarValue);
               float max4 = Math.max(this.hotbarTargetYs[i] + this.hotbarHeights[i], hashHuntBound + maxRadarSize);
               this.hotbarTargetXs[i] = Math.min(this.hotbarTargetXs[i], hotbarBound);
               this.hotbarTargetYs[i] = Math.min(this.hotbarTargetYs[i], hashHuntBound);
               this.hotbarWidths[i] = max3 - this.hotbarTargetXs[i];
               this.hotbarHeights[i] = max4 - this.hotbarTargetYs[i];
               return;
            }
         }

         if (this.hotbarItemCount < 64) {
            this.hotbarTexts[this.hotbarItemCount] = hotbarText;
            this.hotbarTargetXs[this.hotbarItemCount] = hotbarBound;
            this.hotbarTargetYs[this.hotbarItemCount] = hashHuntBound;
            this.hotbarWidths[this.hotbarItemCount] = hotbarValue;
            this.hotbarHeights[this.hotbarItemCount] = maxRadarSize;
            this.hotbarItemCount++;
         }
      }
   }

   private float getDefaultsHud(String defaultsHudText) {
      if (!this.hudDefaultsMap.isEmpty() && (!this.defaultsFlag || !defaultsHudText.equals(this.dragReleaseText))) {
         float[] huddefaultsmap = this.hudDefaultsMap.get(defaultsHudText);
         return huddefaultsmap == null ? 0.0F : huddefaultsmap[2];
      } else {
         return 0.0F;
      }
   }

   private float getMax() {
      return 30.0F + this.backpackTopbarAmount + 5.0F;
   }

   private void updateTextRelease() {
      if (this.dragReleaseText != null) {
         this.updateHudDefaults();
      }

      this.releaseActive = true;
      this.defaultsLayoutValue = 0;
      this.releaseResetText = null;
      this.popupText = null;
      this.releaseText = null;
      this.releaseValue = 0;
      this.lastNanoTimeRelease = 0L;
      this.tickAccumulator = 0.0;
   }

   private float getHotbarHud(String hotbarHudText) {
      if (this.hotbarHudMap.isEmpty()) {
         return 1.0F;
      } else {
         ShowHUD.HudScale hotbarhudmap = this.hotbarHudMap.get(hotbarHudText);
         return hotbarhudmap == null ? 1.0F : hotbarhudmap.hotbarOffsetsAmount;
      }
   }

   private void renderSmoothAmount(DrawContext context, int smoothValue, int fieldsCheckedValue, long nanoTime) {
      LivingEntity livingEntity = this.entity;
      boolean smoothFlag = this.smoothQuintScissor.getTargetAmount() > 0.5 && livingEntity != null;
      if (livingEntity != null && livingEntity != this.livingEntity) {
         this.livingEntity = livingEntity;

         String smoothText;
         try {
            smoothText = livingEntity.getName().getString();
         } catch (Throwable e) {
            smoothText = "?";
         }

         if (livingEntity instanceof PlayerEntity) {
            smoothText = NameProtect.getString(smoothText);
         }

         this.hudField.handleAmountTime(smoothText, 0.9F, false, false);
         this.healthBarSmooth.updateMax();
         this.smooth.updateMax();
         this.absorptionSmooth.updateMax();
         this.secondaryBarSmooth.updateMax();
         this.smoothSmooth.updateMax();
         this.lastRenderedAmount = -1.0F;
         this.lastNanoTimeSmooth = 0L;
         this.smoothValue = 0L;
      }

      if (this.smoothPop.canTimeAmount(smoothFlag, ssaPerfgraphCount) && livingEntity != null) {
         float health = livingEntity.getHealth();
         float maxHealth = livingEntity.getMaxHealth();
         float absorptionAmount = livingEntity.getAbsorptionAmount();
         float amount = maxHealth > 0.0F ? getExperienceProgress(health / maxHealth) : 0.0F;
         if (this.lastRenderedAmount >= 0.0F && amount < this.lastRenderedAmount - 0.001F) {
            this.lastNanoTimeSmooth = nanoTime;
         }

         this.lastRenderedAmount = amount;
         float smoothBound = this.healthBarSmooth.getMax(amount, this.frameDeltaSeconds, 0.12F, 0.001F);
         float smoothLimit = Math.max(smoothBound, this.smooth.getMax(amount, this.frameDeltaSeconds, 0.35F, 0.001F));
         float textureVertexLimit = this.absorptionSmooth.getMax(maxHealth > 0.0F ? getExperienceProgress(absorptionAmount / maxHealth) : 0.0F, this.frameDeltaSeconds, 0.12F, 0.001F);
         int pilotElytraValue = MathHelper.ceil(Math.max(0.0F, health + absorptionAmount));
         if (this.smoothField.canMax(pilotElytraValue)) {
            this.smoothField.handleAmountTime(String.valueOf(pilotElytraValue), 0.78F, true, true);
         }

         if (this.smoothValue == 0L || nanoTime - this.smoothValue >= 100000000L) {
            this.smoothValue = nanoTime;
            int round4 = Math.round(mc.player.distanceTo(livingEntity) * 10.0F);
            if (this.distanceField.canMax(round4)) {
               this.distanceField.handleAmountTime(round4 / 10 + "." + round4 % 10 + " m", 0.76F, true, false);
            }
         }

         int smoothCounter = 0;

         for (EquipmentSlot equipmentSlot : ssaClientEquipmentSlots) {
            ItemStack equippedStack = livingEntity.getEquippedStack(equipmentSlot);
            if (equippedStack != null && !equippedStack.isEmpty()) {
               this.smoothItemStacks[smoothCounter++] = equippedStack;
            }
         }

         int smoothCounter3 = smoothCounter;
         ItemStack mainHandStack = livingEntity.getMainHandStack();
         ItemStack offHandStack = livingEntity.getOffHandStack();
         if (mainHandStack != null && !mainHandStack.isEmpty()) {
            this.smoothItemStacks[smoothCounter++] = mainHandStack;
         }

         if (offHandStack != null && !offHandStack.isEmpty()) {
            this.smoothItemStacks[smoothCounter++] = offHandStack;
         }

         float nativeHudFactor = this.smoothAmount;
         float smoothValue5 = 6.0F;
         float smoothValue6 = 5.0F;
         float smoothDivisor = 4.0F + nativeHudFactor + 2.0F + 2.0F + 4.0F;
         float max4 = this.smoothSmooth.getMax(smoothCounter > 0 ? smoothDivisor : 0.0F, this.frameDeltaSeconds, 0.08F, 0.01F);
         float smoothValue7 = smoothCounter * nativeHudFactor + Math.max(0, smoothCounter - 1) * 3.0F + (smoothCounter3 > 0 && smoothCounter > smoothCounter3 ? 3.0F : 0.0F);
         SkinTextures skinTextures = null;
         if (livingEntity instanceof AbstractClientPlayerEntity abstractClientPlayerEntity) {
            try {
               skinTextures = abstractClientPlayerEntity.getSkin();
            } catch (Throwable e2) {
            }
         }

         if (skinTextures == null && livingEntity instanceof PlayerEntity playerEntity && mc.getNetworkHandler() != null) {
            try {
               PlayerListEntry playerListEntry = mc.getNetworkHandler().getPlayerListEntry(playerEntity.getUuid());
               if (playerListEntry != null) {
                  skinTextures = playerListEntry.getSkinTextures();
               }
            } catch (Throwable e3) {
            }
         }

         float crossLimit = 24.0F;
         float smoothValue9 = 6.0F;
         float smoothValue10 = 8.0F;
         float smoothValue11 = 0.9F;
         float peekPositionFactor = 0.76F;
         float x2 = 0.78F;
         float smoothValue14 = smoothValue10 + 1.0F;
         float max15 = Math.max(54.0F, Math.max(this.hudField.textWidth, smoothValue14 + this.distanceField.checkedRocketsAmount));
         float hudFactor = this.secondaryBarSmooth
            .getAmount((float)Math.ceil(Math.max(smoothValue5 + crossLimit + smoothValue9 + max15 + smoothValue9 + 22.0F + smoothValue5, smoothValue7 + 2.0F * smoothValue5)), this.frameDeltaSeconds, 0.08F);
         float smoothValue17 = smoothValue6 * 2.0F + crossLimit;
         float maxRadarSize = smoothValue17 + max4;
         float smoothValue19 = NativeHud.getAmount4((smoothValue - hudFactor) / 2.0F) + this.coordsOffsetX;
         float hudScaleValue = NativeHud.getAmount4(fieldsCheckedValue / 2.0F + 48.0F) + this.coordsOffsetY;
         this.handleTextHud(smoothValue19, hudScaleValue, hudFactor, maxRadarSize);
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(this.smoothPop.getPopProgress());
         boolean translateMatrices = NativeHud.canTranslateMatrices(context, smoothValue19 + hudFactor / 2.0F, hudScaleValue + maxRadarSize / 2.0F, this.smoothPop.getAmount());

         try {
            NativeHud.handle(NativeHud.smoothValue, smoothValue19, hudScaleValue, hudFactor, maxRadarSize, 1.0F);
            float peekBodyValue = smoothValue19 + smoothValue5;
            float randomValue = hudScaleValue + smoothValue6;
            float smoothValue23 = smoothValue19 + hudFactor - smoothValue5;
            if (!NativeHud.canPeekBody(skinTextures, peekBodyValue, randomValue, crossLimit, 4.0F)) {
               NativeHud.handleColor(peekBodyValue, randomValue, crossLimit, 4.0F, IcoGenUtils.getIdentifierForString("person"), 12.0F);
            }

            float scaleValue = peekBodyValue + crossLimit + smoothValue9;
            float smoothValue25 = randomValue + 12.5F;
            float smoothValue26 = 9.0F;
            NativeHud.handlePushScale(this.hudField.bloodhoundBestText, scaleValue, NativeHud.getAmount2(randomValue + 1.5F, 11.0F, smoothValue11), smoothValue11, NativeHud.OFF_WHITE);
            NativeHud.handle(IcoGenUtils.getIdentifierForString("target"), scaleValue + 0.5F, NativeHud.getAmount4(smoothValue25 + (smoothValue26 - smoothValue10) / 2.0F), smoothValue10, NativeHud.GRAY);
            NativeHud.handle(this.distanceField, scaleValue + smoothValue14, NativeHud.getAmount2(smoothValue25, smoothValue26, peekPositionFactor), peekPositionFactor, NativeHud.GRAY);
            float lerpUuidFactor = 9.0F;
            float hotbarMatrixBound = NativeHud.getAmount4(2.0F);
            float textureVertexValue = smoothValue23 - 11.0F;
            float smoothValue30 = randomValue + crossLimit / 2.0F;
            float smoothLimit5 = 0.0F;
            if (this.lastNanoTimeSmooth != 0L) {
               float smoothLimit6 = (float)(nanoTime - this.lastNanoTimeSmooth) / 2.6E8F;
               if (smoothLimit6 >= 1.0F) {
                  this.lastNanoTimeSmooth = 0L;
               } else {
                  smoothLimit5 = (1.0F - smoothLimit6) * (1.0F - smoothLimit6);
               }
            }

            NativeHud.handleTextureVertex(textureVertexValue, smoothValue30, lerpUuidFactor, hotbarMatrixBound, 1.0F, 318767103);
            if (smoothLimit > smoothBound + 0.002F) {
               NativeHud.handleTextureVertex(textureVertexValue, smoothValue30, lerpUuidFactor, hotbarMatrixBound, smoothLimit, NativeHud.getValue(-920587, 0.35F + 0.45F * smoothLimit5));
            }

            NativeHud.handleTextureVertex(textureVertexValue, smoothValue30, lerpUuidFactor, hotbarMatrixBound, smoothBound, NativeHud.getFlags(smoothBound));
            if (textureVertexLimit > 0.001F) {
               NativeHud.handleTextureVertex(textureVertexValue, smoothValue30, 11.0F, NativeHud.getAmount4(1.0F), textureVertexLimit, -1535700);
            }

            if (smoothLimit5 > 0.01F) {
               double angle = smoothBound * (Math.PI * 2) - (Math.PI / 2);
               float smoothFactor = lerpUuidFactor - hotbarMatrixBound / 2.0F;
               NativeHud.render(context, textureVertexValue + (float)Math.cos(angle) * smoothFactor, smoothValue30 + (float)Math.sin(angle) * smoothFactor, 3.0F, NativeHud.getValue(-920587, smoothLimit5));
            }

            float smoothValue31 = 10.0F;
            NativeHud.handleAmountForFieldFloat(this.smoothField, textureVertexValue, NativeHud.getAmount2(smoothValue30 - smoothValue31 / 2.0F, smoothValue31, x2), x2, NativeHud.OFF_WHITE);
            if (max4 > 0.5F) {
               float smoothValue32 = max4 / smoothDivisor;
               NativeHud.renderForDrawContextFloatInt(context, smoothValue19 + smoothValue5, hudScaleValue + smoothValue17, hudFactor - 2.0F * smoothValue5, NativeHud.getValue(268435455, smoothValue32));
               float panelOverlay2 = CorzRoundedUtils.getPanelOverlay(smoothValue32);

               try {
                  float smoothTotal = smoothValue19 + smoothValue5;
                  float smoothValue33 = hudScaleValue + smoothValue17 + 4.0F;
                  float nativeHudValue = smoothValue33 + nativeHudFactor + 2.0F;

                  for (int i = 0; i < smoothCounter; i++) {
                     if (i == smoothCounter3 && smoothCounter3 > 0) {
                        smoothTotal += 3.0F;
                     }

                     ItemStack itemStack = this.smoothItemStacks[i];
                     NativeHud.renderMatrixMatrices(context, itemStack, smoothTotal, smoothValue33, nativeHudFactor);
                     if (itemStack.isDamageable() && itemStack.getMaxDamage() > 0) {
                        float nativeHudLimit = 1.0F - (float)itemStack.getDamage() / itemStack.getMaxDamage();
                        if (nativeHudLimit < 0.999F) {
                           NativeHud.handle(smoothTotal, nativeHudValue, nativeHudFactor, 2.0F, nativeHudLimit, NativeHud.getFlags(nativeHudLimit));
                        }
                     }

                     smoothTotal += nativeHudFactor + 3.0F;
                  }
               } finally {
                  CorzRoundedUtils.handlePanelOverlay(panelOverlay2);
               }
            }
         } finally {
            NativeHud.renderPopMatrices(context, translateMatrices);
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
         }
      }
   }

   private int getIndexOf(String indexOfText) {
      int indexOf = this.hudOrderItems.indexOf(indexOfText);
      return indexOf < 0 ? -1 : indexOf;
   }

   private void handleAmountMin(float amount, float amount2, float... hudValues) {
      if (!(amount2 >= this.hotbarMinOffset)) {
         this.hotbarMinOffset = amount2;
         this.layoutDefaultsAmount = amount;
         this.layoutDefaultsValue = Math.min(3, hudValues.length / 4);
         System.arraycopy(hudValues, 0, this.layoutDefaultsTable, 0, this.layoutDefaultsValue * 4);
      }
   }

   private static boolean canPersonInsensitive(ShowHUD.NStaff nStaff, ShowHUD.NStaff nStaff2) {
      return String.CASE_INSENSITIVE_ORDER.compare(nStaff.personText, nStaff2.personText) < 0;
   }

   private static float[] getAmounts(float[] hudValues, float[] floats) {
      if (hudValues == null) {
         return floats;
      } else {
         float hudValue = Math.min(hudValues[0], floats[0]);
         float min2 = Math.min(hudValues[1], floats[1]);
         float max3 = Math.max(hudValues[0] + hudValues[2], floats[0] + floats[2]);
         float max4 = Math.max(hudValues[1] + hudValues[3], floats[1] + floats[3]);
         return new float[]{hudValue, min2, max3 - hudValue, max4 - min2};
      }
   }

   private static void renderIconText(DrawContext context, VertexTexturePeek vertexTexturePeek, ShowHUD.H2Seg h2Seg, float iconValue, float hudScaleValue, float iconValue3, float experienceProgressLimit, float iconValue5) {
      float iconValue6 = 8.0F * iconValue5;
      float peekPositionFactor = 9.0F * iconValue5;
      float iconValue8 = 5.0F * iconValue5;
      float iconValue9 = 12.0F * iconValue5;
      float iconTotal = iconValue + iconValue6;
      if (h2Seg.icon != null) {
         render(context, h2Seg.icon, iconTotal, hudScaleValue + (iconValue3 - peekPositionFactor) / 2.0F, peekPositionFactor, getValue(-1, experienceProgressLimit));
         iconTotal += peekPositionFactor + iconValue8;
      }

      int vertexTextureValue = getValue(h2Seg.color, experienceProgressLimit);
      if (h2Seg.brand) {
         VertexTexturePeek wave = VertexTexturePeek.getWave();
         if (wave.isCorzclientFlag()) {
            wave.handleAmountPosition(h2Seg.text, iconTotal, hudScaleValue + (iconValue3 - wave.getAmountForFloat(13.0F * iconValue5)) / 2.0F, getValue(-4205835, experienceProgressLimit), getValue(-1, experienceProgressLimit), 13.0F * iconValue5);
            return;
         }
      }

      float peekEndValue = hudScaleValue + (iconValue3 - vertexTexturePeek.getAmountForFloat(iconValue9)) / 2.0F;
      if (h2Seg.mono) {
         getPotionsStaff(vertexTexturePeek, h2Seg.text, iconTotal, peekEndValue, vertexTextureValue, iconValue9);
      } else {
         vertexTexturePeek.handlePeekEnd(h2Seg.text, iconTotal, peekEndValue, vertexTextureValue, iconValue9);
      }
   }

   private void handleSsaPotions(float ssaPotionsValue, float nativeHudValue, float ssaPotionsValue3, float pilotStatusBound) {
      regionmapRegionValue = (int)NativeHud.getAmount4(ssaPotionsValue);
      ssaPotionsOffsetX = (int)NativeHud.getAmount4(nativeHudValue);
      potionsHudOffset = (int)ssaPotionsValue3;
      ssaPotionsValue4SsaPotions = (int)pilotStatusBound;
      potionsSsaTime = ssaPerfgraphCount;
   }

   private static boolean isInstance() {
      ShowHUD singleton2 = singleton;
      return singleton2 != null && singleton2.isEnabled();
   }

   private void handleAmountDefaults(double defaultsValue, double defaultsValue2) {
      float defaultsValue3 = (float)(defaultsValue - this.dragStartMouseX);
      float planValuesItem = (float)(defaultsValue2 - this.dragStartMouseY);
      String dragReleaseText = this.dragReleaseText;
      boolean defaultsFlag = getFlowHud(dragReleaseText) >= 0;
      float[] hudValues = this.defaultsTable;
      if (!this.defaultsFlag) {
         if (Math.abs(defaultsValue3) < 1.5F && Math.abs(planValuesItem) < 1.5F) {
            return;
         }

         this.defaultsFlag = true;
         this.hudDefaultsMap.remove(dragReleaseText);
         this.savedBaseX = this.defaultsAmount;
         this.savedBaseY = this.baseY;
         float[] hudmodulesmap = this.hudModulesMap.remove(dragReleaseText);
         float[] moduleshudmap = this.modulesHudMap.get(dragReleaseText);
         float hotbarHud = this.getHotbarHud(dragReleaseText);
         if (hudmodulesmap != null && moduleshudmap != null && hotbarHud != 1.0F) {
            this.handleBottomLeftForStringHudScale(dragReleaseText, this.hotbarHudMap.get(dragReleaseText), moduleshudmap[0] + this.offsetX, moduleshudmap[1] + this.hudDefaultsAmount, moduleshudmap[2], moduleshudmap[3]);
            this.defaultsAmount = this.defaultsAmount + (this.bottomAmount - getAmountForFloat(hudmodulesmap[0])) * moduleshudmap[2] * (1.0F - hotbarHud);
            this.baseY = this.baseY + (this.bottomLeftAmount - getAmountForFloat(hudmodulesmap[1])) * moduleshudmap[3] * (1.0F - hotbarHud);
         }
      } else if (defaultsFlag) {
         this.defaultsAmount = this.savedBaseX;
         this.baseY = this.savedBaseY;
         if (this.canAmountHud(dragReleaseText, hudValues)) {
            this.elementWidth = hudValues[2];
            this.elementHeight = hudValues[3];
         }
      } else if (this.canAmountHud(dragReleaseText, hudValues)) {
         this.defaultsAmount = hudValues[0] - this.offsetX;
         this.baseY = hudValues[1] - this.hudDefaultsAmount;
         this.elementWidth = hudValues[2];
         this.elementHeight = hudValues[3];
      }

      float elementWidth25 = this.elementWidth;
      float elementHeight39 = this.elementHeight;
      float coordsValue = this.defaultX + defaultsValue3;
      float coordsLimit = this.defaultY + planValuesItem;
      this.defaultsLayoutFlag = defaultsFlag && this.canAmountsCoords(dragReleaseText, coordsValue, coordsLimit, elementWidth25, elementHeight39);
      float defaultsBound;
      float hashHuntBound;
      if (this.defaultsLayoutFlag) {
         this.defaultsLayoutValue = 0;
         defaultsBound = coordsValue;
         hashHuntBound = coordsLimit;
      } else {
         defaultsBound = this.getHotbarAmounts(coordsValue, coordsLimit, elementWidth25, elementHeight39, !defaultsFlag);
         hashHuntBound = this.getHotbarAmountsForFloatFlag(defaultsBound, coordsLimit, elementWidth25, elementHeight39, !defaultsFlag);
         if (this.canAmountsScaled(defaultsBound, hashHuntBound, elementWidth25, elementHeight39)) {
            defaultsBound = this.scaledBestPosition;
            hashHuntBound = this.scaledAmount;
         }
      }

      this.elementX = Math.max(0.0F, Math.min(this.scaledWidth - elementWidth25, defaultsBound));
      this.elementY = Math.max(0.0F, Math.min(this.scaledHeight - elementHeight39, hashHuntBound));
      this.offsetX = this.elementX - this.defaultsAmount;
      this.hudDefaultsAmount = this.elementY - this.baseY;
      this.hudOffsetsMap.put(dragReleaseText, new float[]{this.offsetX, this.hudDefaultsAmount});
   }

   public static boolean isKeybindsLevel() {
      return canScreenTotems(keybindsLevelValue);
   }

   private boolean canFlagsResources(String resourcesText) {
      if (this.canHudOffsets(resourcesText)) {
         return false;
      } else {
         for (int i = 0; i < this.clone.length; i++) {
            if (this.clone[i].equals(resourcesText)) {
               int index = this.inventoryResourcesValues[i];
               return index >= 0 && index < this.coordsRadarFlags.length && this.coordsRadarFlags[index];
            }
         }

         return false;
      }
   }

   public static int getMax(MinecraftClient minecraftClient) {
      int heightScaled = getHeight(minecraftClient) / 480;
      return Math.max(2, Math.min(3, heightScaled));
   }

   public static float getAmountForString(String hudText) {
      ShowHUD showHUD = getShowHUD();
      return showHUD == null ? 1.0F : showHUD.getHotbarHud(hudText);
   }

   private void handleRadarSsa(float radarSsaTotal, float hudScaleValue, float radarSsaFactor) {
      float hotbarHud = this.getHotbarHud("radar");
      if (hotbarHud != 1.0F) {
         this.handleBottomLeftForStringHudScale("radar", null, radarSsaTotal, hudScaleValue, radarSsaFactor, radarSsaFactor);
         radarSsaTotal += this.bottomAmount * radarSsaFactor * (1.0F - hotbarHud);
         hudScaleValue += this.bottomLeftAmount * radarSsaFactor * (1.0F - hotbarHud);
         radarSsaFactor *= hotbarHud;
      }

      radarSsaTotalRadarSsa = (int)radarSsaTotal;
      radarSsaTotal2RadarSsa = (int)hudScaleValue;
      radarSsaFactorRegionmapRegio = (int)radarSsaFactor;
      radarSsaFactorRadarSsa = (int)radarSsaFactor;
      ssaPerfgraphCountPotionsSsa = ssaPerfgraphCount;
   }

   private boolean canCloudPerson(VertexTexturePeek vertexTexture, long nanoTime) {
      boolean booleanValue = this.brandScaledMatrices.isEnabled();
      boolean enabled = this.serverInfo.isEnabled();
      boolean enabled3 = this.clockScaledMatrices.isEnabled();
      long time = enabled3 ? System.currentTimeMillis() / 1000L : -1L;
      int value = (booleanValue ? 1 : 0) | (enabled ? 2 : 0) | (VertexTexturePeek.getWave().isCorzclientFlag() ? 4 : 0);
      boolean cloudPersonFlag = time == this.cloudPersonValue && value == this.cloudPersonCachedValue && vertexTexture == this.vertexTexture;
      if (cloudPersonFlag && this.lastNanoTimeCloudPerson != 0L && nanoTime - this.lastNanoTimeCloudPerson < 250000000L) {
         return this.topbarPerfFlag;
      } else {
         this.lastNanoTimeCloudPerson = nanoTime;
         String cloudPersonText = "";

         try {
            cloudPersonText = mc.getSession().getUsername();
         } catch (Throwable e) {
         }

         if (cloudPersonText == null) {
            cloudPersonText = "";
         }

         String text = NameProtect.getString(cloudPersonText);
         String text2 = enabled ? this.getAddress() : "";
         if (cloudPersonFlag && text.equals(this.cloudPersonText) && text2.equals(this.cloudPersonCachedSubText)) {
            return this.topbarPerfFlag;
         } else {
            this.cloudPersonValue = time;
            this.cloudPersonCachedValue = value;
            this.vertexTexture = vertexTexture;
            this.cloudPersonText = text;
            this.cloudPersonCachedSubText = text2;
            if (!booleanValue && !enabled3 && text.isEmpty() && text2.isEmpty()) {
               return false;
            } else {
               int clockCloudValue = 0;
               if (booleanValue) {
                  this.cloudPersonH2Segs[clockCloudValue++] = H2_SEG;
               }

               if (!text.isEmpty()) {
                  this.cloudPersonH2Segs[clockCloudValue++] = new ShowHUD.H2Seg(IcoGenUtils.getIdentifierGENERATED_IDENTIFIER_CACHE("person"), text);
               }

               if (!text2.isEmpty()) {
                  this.cloudPersonH2Segs[clockCloudValue++] = new ShowHUD.H2Seg(IcoGenUtils.getIdentifierGENERATED_IDENTIFIER_CACHE("cloud"), text2);
               }

               if (enabled3) {
                  this.cloudPersonH2Segs[clockCloudValue++] = new ShowHUD.H2Seg(
                     IcoGenUtils.getIdentifierGENERATED_IDENTIFIER_CACHE("clock"), SIMPLE_DATE_FORMAT.format(new Date()), -1, true, false
                  );
               }

               this.clockCloudValue = clockCloudValue;
               float amount = 0.0F;

               for (int i = 0; i < clockCloudValue; i++) {
                  this.cloudPersonTable[i] = getAmount(vertexTexture, this.cloudPersonH2Segs[i]);
                  amount += this.cloudPersonTable[i];
               }

               this.cloudPersonAmount = amount;
               return true;
            }
         }
      }
   }

   private float getHudScale() {
      return this.hudScale != null ? this.hudScale.coordsPerf : 1.0F;
   }

   private boolean canHotbarTexts(int index) {
      return !this.hotbarTexts[index].equals(this.dragReleaseText) && !this.canFlagsResources(this.hotbarTexts[index]);
   }

   private static void handleColor(String colorText, float colorValue, Field field, float nativeHudValue, float matrixStackValue, float nativeHudBound, float hashHuntBound, float cardMatrixFactor, float panelOverlayLimit) {
      NativeHud.handle(nativeHudValue, matrixStackValue, nativeHudBound, hashHuntBound, cardMatrixFactor);
      NativeHud.handlePushScale(
         colorText, NativeHud.getAmount4(nativeHudValue + (nativeHudBound - colorValue) / 2.0F), NativeHud.getAmount2(matrixStackValue + panelOverlayLimit, 18.0F - 4.0F * panelOverlayLimit, 0.7F), 0.7F, NativeHud.OFF_WHITE
      );
      if (!(panelOverlayLimit <= 0.01F)) {
         float maxRadarSize = 9.0F;
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(panelOverlayLimit);

         try {
            NativeHud.handleAmountForFieldFloat(field, nativeHudValue + nativeHudBound / 2.0F, NativeHud.getAmount2(matrixStackValue + hashHuntBound - 3.0F - maxRadarSize, maxRadarSize, 0.64F), 0.64F, NativeHud.GRAY);
         } finally {
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
         }
      }
   }

   private float getCorzBrand(DrawContext context, int corzBrandValue, int fieldsCheckedValue) {
      boolean booleanValue = this.brandScaledMatrices.isEnabled();
      float corzBrandFactor = 21.0F;
      float corzBrand = this.corzBrandSmooth.getAmountForFloat(booleanValue && !this.canHudOffsets("brand") ? corzBrandFactor * this.getHudScale() + 6.0F : 0.0F, this.frameDeltaSeconds, 0.08F);
      if (!this.corzBrandPop.canTimeAmount(booleanValue, ssaPerfgraphCount)) {
         return corzBrand;
      } else {
         float hudFactor = (float)Math.ceil(this.corzStaffAmount + 14.0F);
         float corzBrandValue4 = corzBrandValue - 6.0F - hudFactor + this.coordsOffsetX;
         float hudScaleValue = fieldsCheckedValue - 6.0F - corzBrandFactor + this.coordsOffsetY;
         this.handleTextHud(corzBrandValue4, hudScaleValue, hudFactor, corzBrandFactor);
         this.handleMaxHud(corzBrandValue4, hudScaleValue, hudFactor, corzBrandValue, fieldsCheckedValue);
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(this.corzBrandPop.getPopProgress());
         boolean translateMatrices = NativeHud.canTranslateMatrices(context, corzBrandValue4 + hudFactor / 2.0F, hudScaleValue + corzBrandFactor / 2.0F, this.corzBrandPop.getAmount());

         try {
            NativeHud.handle(NativeHud.corzValue, corzBrandValue4, hudScaleValue, hudFactor, corzBrandFactor, 1.0F);
            NativeHud.handlePushScale(
               "Discord.gg/leakestan", corzBrandValue4 + (hudFactor - this.corzStaffAmount) / 2.0F, NativeHud.getAmount2(hudScaleValue, corzBrandFactor, 0.8F), 0.8F, NativeHud.GRAY
            );
         } finally {
            NativeHud.renderPopMatrices(context, translateMatrices);
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
         }

         return corzBrand;
      }
   }

   private static void render(DrawContext context, Identifier id, float hudValue, float roundOffset, float peekPositionFactor, int hudValue4) {
      MatrixPeekUtilsIdentifier.handle(hudValue, roundOffset, peekPositionFactor, peekPositionFactor, id, new Color(hudValue4, true), MatrixPeekUtilsRender.getMatrixStack());
   }

   public static boolean canHudHotbar(String hudHotbarText) {
      ShowHUD singleton2 = singleton;
      if (singleton2 == null) {
         return false;
      } else if (!singleton2.scaledMatricesActive && !singleton2.hudOffsetsMap.containsKey(hudHotbarText) && !singleton2.hudModulesMap.containsKey(hudHotbarText)) {
         ShowHUD.HudScale hudScale = singleton2.hotbarHudMap.isEmpty() ? null : singleton2.hotbarHudMap.get(hudHotbarText);
         return hudScale != null && (hudScale.hudScaleFactor != 1.0F || hudScale.hotbarOffsetsAmount != 1.0F);
      } else {
         return true;
      }
   }

   private int getValue(ShowHUD.Anchor anchor, double hudValue) {
      double hudFactor = 1.0 - hudValue;

      return switch (anchor) {
         case TOP_LEFT, TOP_CENTER, TOP_RIGHT -> (int)(-6.0 * hudFactor);
         case MID_LEFT, MID_RIGHT -> 0;
         case BOT_LEFT, BOT_CENTER, BOT_RIGHT -> (int)(6.0 * hudFactor);
      };
   }

   private void handleScaledWidth(String scaledWidthText, float scaledWidthValue, float hudLimit, float scaledWidthValue3, float maxRadarSize) {
      if (this.scaledWidth > 0 && this.scaledHeight > 0) {
         scaledWidthValue = getAmount(scaledWidthValue, 0.0F, Math.max(0.0F, this.scaledWidth - scaledWidthValue3));
         hudLimit = getAmount(hudLimit, 0.0F, Math.max(0.0F, this.scaledHeight - maxRadarSize));
         this.hudModulesMap.put(scaledWidthText, getAmounts(scaledWidthValue, hudLimit, scaledWidthValue3, maxRadarSize, this.scaledWidth, this.scaledHeight));
         ModuleConfigAutosave.updateFlag2();
      }
   }

   private void renderPersonOnline(DrawContext context, int personOnlineValue, long nanoTime) {
      if (this.staffList == null) {
         this.staffList = (StaffList)ModuleRegistry.getModule(StaffList.class);
      }

      StaffList staffList = this.staffList;
      boolean personOnlineFlag = staffList != null && staffList.isEnabled() && (!staffList.isDonutsmpOnly() || StaffList.isDonutsmpAddress()) && mc.getNetworkHandler() != null;
      if (!personOnlineFlag) {
         this.lastNanoTime = 0L;
      } else if (this.lastNanoTime == 0L || staffList.getOnlineStaffCount() != this.rosterStaff || nanoTime - this.lastNanoTime >= 500000000L) {
         this.lastNanoTime = nanoTime;
         this.handleOnlineNone(staffList, nanoTime);
      }

      float rowFactor = 1.0F - (float)Math.exp(-this.frameDeltaSeconds / 0.06F);
      float amount = 0.0F;
      float personOnlineBound = 0.0F;

      for (int i = 0; i < this.onlineNoneItems.size(); i++) {
         ShowHUD.NStaff onlinenoneitem = this.onlineNoneItems.get(i);
         if (onlinenoneitem.canAmountFlag(rowFactor)) {
            this.onlineNoneNStaffMap.remove(onlinenoneitem.personText);
            this.onlineNoneItems.remove(i--);
         } else {
            onlinenoneitem.rowHeight = amount;
            amount += 15.0F * onlinenoneitem.personOnlineAmount;
            if (!onlinenoneitem.durationActive) {
               personOnlineBound = Math.max(personOnlineBound, this.personAmount + 5.0F + onlinenoneitem.onlineNoneAmount + 8.0F + 4.0F);
            }
         }
      }

      boolean personOnlineFlag2 = this.onlineNoneValue == 0;
      float personOnlineLimit = this.personOnlineSmooth.getMax(personOnlineFlag2 ? 15.0F : 0.0F, this.frameDeltaSeconds, 0.06F, 0.01F);
      if (!this.personOnlinePop.canTimeAmount(personOnlineFlag, ssaPerfgraphCount)) {
         this.personOnlineWidthSmooth.updateMax();
      } else {
         if (personOnlineFlag2) {
            personOnlineBound = Math.max(personOnlineBound, this.onlineBuildAmount);
         }

         float hudFactor = this.personOnlineWidthSmooth
            .getAmount(
               (float)Math.ceil(Math.max(NativeHud.getAmount2(this.staffCorzAmount, this.person ? this.onlineNoneField.checkedRocketsAmount : 0.0F), personOnlineBound + 14.0F)),
               this.frameDeltaSeconds,
               0.08F
            );
         float maxRadarSize = 30.0F + amount + personOnlineLimit + 5.0F;
         float roundSsaValue = Math.max(6.0F, this.modulesFlag ? this.roundedModulesWidth - 6.0F - hudFactor : personOnlineValue - 6.0F - hudFactor);
         float glfwLimit = this.modulesFlag ? this.personOnlineValue : 6.0F;
         float personOnlineValue7 = roundSsaValue + this.coordsOffsetX;
         float hudScaleValue = glfwLimit + this.coordsOffsetY;
         this.handleTextHud(personOnlineValue7, hudScaleValue, hudFactor, maxRadarSize);
         this.handleRoundSsa(roundSsaValue, glfwLimit, hudFactor);
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(this.personOnlinePop.getPopProgress());
         boolean translateMatrices = NativeHud.canTranslateMatrices(context, personOnlineValue7 + hudFactor / 2.0F, hudScaleValue + maxRadarSize / 2.0F, this.personOnlinePop.getAmount());

         try {
            NativeHud.handle(NativeHud.personStaffValue, personOnlineValue7, hudScaleValue, hudFactor, maxRadarSize, 1.0F);
            float personOnlineValue9 = NativeHud.getAmount(
               context,
               personOnlineValue7,
               hudScaleValue,
               hudFactor,
               IcoGenUtils.getIdentifierForString("person"),
               "Staff",
               this.person ? this.onlineNoneField : null,
               this.onlineNonePulse,
               getExperienceProgress((amount + personOnlineLimit) / 8.0F)
            );
            float personOnlineValue10 = personOnlineValue7 + 7.0F;
            float personOnlineFactor = getExperienceProgress(personOnlineLimit / 15.0F) * (1.0F - getExperienceProgress(amount / 4.5F));
            if (personOnlineLimit > 0.5F && personOnlineFactor > 0.004F) {
               float panelOverlay2 = CorzRoundedUtils.getPanelOverlay(personOnlineFactor * personOnlineFactor);

               try {
                  NativeHud.handle("None online", personOnlineValue10, personOnlineValue9 + amount, personOnlineLimit, NativeHud.GRAY);
               } finally {
                  CorzRoundedUtils.handlePanelOverlay(panelOverlay2);
               }
            }

            for (int j = 0; j < this.onlineNoneItems.size(); j++) {
               ShowHUD.NStaff onlinenoneitem2 = this.onlineNoneItems.get(j);
               float personOnlineAmount = onlinenoneitem2.personOnlineAmount;
               if (!(personOnlineAmount <= 0.002F)) {
                  float peekPositionFactor = 15.0F * personOnlineAmount;
                  float personOnlineValue12 = personOnlineValue9 + onlinenoneitem2.rowHeight;
                  float panelOverlay3 = CorzRoundedUtils.getPanelOverlay(personOnlineAmount * personOnlineAmount);

                  try {
                     float roundOffset = personOnlineValue12 + NativeHud.getAmount4((peekPositionFactor - this.personAmount) / 2.0F);
                     if (!NativeHud.canFlag(onlinenoneitem2.skinTextures, personOnlineValue10, roundOffset, this.personAmount)) {
                        NativeHud.handle(IcoGenUtils.getIdentifierForString("person"), personOnlineValue10, roundOffset, this.personAmount, NativeHud.GRAY);
                     }

                     NativeHud.handle(onlinenoneitem2.personText, personOnlineValue10 + this.personAmount + 5.0F, personOnlineValue12, peekPositionFactor, NativeHud.OFF_WHITE);
                     if (onlinenoneitem2.personValue >= 0) {
                        int nativeHudValue = 0xFF000000 | onlinenoneitem2.getPersonTime(nanoTime);
                        float pilotStatusBound = 4.0F;
                        float peekPositionValue = NativeHud.getAmount4(personOnlineValue7 + hudFactor - 7.0F - pilotStatusBound);
                        float matrixStackValue = personOnlineValue12 + NativeHud.getAmount4((peekPositionFactor - pilotStatusBound) / 2.0F);
                        NativeHud.handlePeekPosition(peekPositionValue, matrixStackValue, pilotStatusBound, pilotStatusBound, pilotStatusBound / 2.0F, NativeHud.getValue(nativeHudValue, 0.22F), 2.5F, true);
                        NativeHud.handlePeekPosition(peekPositionValue, matrixStackValue, pilotStatusBound, pilotStatusBound, pilotStatusBound / 2.0F, 0.0F, nativeHudValue);
                     }
                  } finally {
                     CorzRoundedUtils.handlePanelOverlay(panelOverlay3);
                  }
               }
            }
         } finally {
            NativeHud.renderPopMatrices(context, translateMatrices);
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
         }
      }
   }

   private void handleLeftRight(int leftRightValue, int fieldsCheckedValue, int maxRadarSize) {
      float leftRightBound = 6.0F;
      String formatText = this.enumHelper.getCurrentOption();
      float leftRightBound2;
      float wrapDegreesBound;
      switch (formatText) {
         case "Top Left":
            leftRightBound2 = leftRightBound;
            wrapDegreesBound = leftRightBound;
            break;
         case "Top Right":
            leftRightBound2 = leftRightValue - leftRightBound - maxRadarSize;
            wrapDegreesBound = leftRightBound;
            break;
         case "Bottom Left":
            leftRightBound2 = leftRightBound;
            wrapDegreesBound = fieldsCheckedValue - leftRightBound - maxRadarSize - (this.coordinates.isEnabled() ? 29.0F : 0.0F);
            break;
         case "Bottom Right":
            leftRightBound2 = leftRightValue - leftRightBound - maxRadarSize;
            wrapDegreesBound = fieldsCheckedValue - leftRightBound - maxRadarSize - this.radarArmorAmount;
            break;
         case "Left":
            leftRightBound2 = leftRightBound;
            wrapDegreesBound = (fieldsCheckedValue - maxRadarSize) / 2.0F;
            break;
         case "Right":
            leftRightBound2 = leftRightValue - leftRightBound - maxRadarSize;
            wrapDegreesBound = (fieldsCheckedValue - maxRadarSize) / 2.0F;
            break;
         default:
            if (canPerfgraphSsa(potionsSsaTime)) {
               leftRightBound2 = regionmapRegionValue + potionsHudOffset + leftRightBound;
               wrapDegreesBound = ssaPotionsOffsetX;
            } else {
               leftRightBound2 = leftRightValue - leftRightBound - maxRadarSize;
               wrapDegreesBound = (fieldsCheckedValue - maxRadarSize) / 2.0F;
            }
      }

      this.lerpUuidAmount = NativeHud.getAmount4(Math.max(leftRightBound, Math.min(leftRightBound2, leftRightValue - maxRadarSize - leftRightBound)));
      this.leftRightAmount = NativeHud.getAmount4(Math.max(leftRightBound, Math.min(wrapDegreesBound, fieldsCheckedValue - maxRadarSize - leftRightBound)));
   }

   private void renderShieldAmounts(DrawContext context, int shieldValue, int fieldsCheckedValue, float corzBrand) {
      float shieldFactor = 1.0F - (float)Math.exp(-this.frameDeltaSeconds / 0.06F);
      float shieldFactor2 = 1.0F - (float)Math.exp(-this.frameDeltaSeconds / 0.12F);
      float shieldTotal = 0.0F;
      boolean shieldFlag = false;

      for (int i = 0; i < 4; i++) {
         ItemStack equippedStack = mc.player.getEquippedStack(ssaClientEquipmentSlots[i]);
         boolean shieldFlag2 = equippedStack != null && !equippedStack.isEmpty();
         if (shieldFlag2) {
            shieldFlag = true;
            this.itemStacks[i] = equippedStack;
            int maxDamage = equippedStack.getMaxDamage();
            this.shieldDurabilityFractions[i] = maxDamage > 0 ? (float)(maxDamage - equippedStack.getDamage()) / maxDamage : 1.0F;
         }

         float shieldValue4 = shieldFlag2 ? 1.0F : 0.0F;
         this.shieldTable[i] = this.shieldTable[i] + (shieldValue4 - this.shieldTable[i]) * shieldFactor;
         if (Math.abs(shieldValue4 - this.shieldTable[i]) < 0.002F) {
            this.shieldTable[i] = shieldValue4;
         }

         this.shieldDisplayedFractions[i] = this.shieldDisplayedFractions[i] + (this.shieldDurabilityFractions[i] - this.shieldDisplayedFractions[i]) * shieldFactor2;
         if (Math.abs(this.shieldDurabilityFractions[i] - this.shieldDisplayedFractions[i]) < 0.001F) {
            this.shieldDisplayedFractions[i] = this.shieldDurabilityFractions[i];
         }

         shieldTotal += (this.shieldAmount + 4.0F) * this.shieldTable[i];
      }

      if (this.shieldPop.canTimeAmount(this.armorHud.isEnabled() && shieldFlag, ssaPerfgraphCount)) {
         float maxRadarSize = 23.0F;
         float shieldValue6 = 6.0F;
         float hudFactor = NativeHud.getAmount(shieldValue6 + 12.0F + 5.0F + Math.max(0.0F, shieldTotal - 4.0F) + 7.0F);
         float shieldValue8 = shieldValue - 6.0F - hudFactor + this.coordsOffsetX;
         float hudScaleValue = fieldsCheckedValue - 6.0F - maxRadarSize - corzBrand + this.coordsOffsetY;
         this.handleTextHud(shieldValue8, hudScaleValue, hudFactor, maxRadarSize);
         this.handleMaxHud(shieldValue8, hudScaleValue, hudFactor, shieldValue, fieldsCheckedValue);
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(this.shieldPop.getPopProgress());
         boolean translateMatrices = NativeHud.canTranslateMatrices(context, shieldValue8 + hudFactor / 2.0F, hudScaleValue + maxRadarSize / 2.0F, this.shieldPop.getAmount());

         try {
            NativeHud.handle(NativeHud.shieldValue, shieldValue8, hudScaleValue, hudFactor, maxRadarSize, 1.0F);
            NativeHud.handle(shieldValue8 + shieldValue6, hudScaleValue + NativeHud.getAmount4((maxRadarSize - 12.0F) / 2.0F), IcoGenUtils.getIdentifierForString("shield"), 1.0F);
            float matrixMatricesValue = shieldValue8 + shieldValue6 + 12.0F + 5.0F;
            float shieldValue10 = hudScaleValue + NativeHud.getAmount4((maxRadarSize - this.shieldAmount - 2.0F - 2.0F) / 2.0F);
            float nativeHudValue = shieldValue10 + this.shieldAmount + 2.0F;

            for (int j = 0; j < 4; j++) {
               float panelOverlayLimit = this.shieldTable[j];
               if (!(panelOverlayLimit <= 0.002F) && this.itemStacks[j] != null) {
                  float panelOverlay2 = CorzRoundedUtils.getPanelOverlay(panelOverlayLimit);

                  try {
                     NativeHud.renderMatrixMatrices(context, this.itemStacks[j], matrixMatricesValue, shieldValue10, this.shieldAmount);
                     NativeHud.handle(matrixMatricesValue, nativeHudValue, this.shieldAmount, 2.0F, this.shieldDisplayedFractions[j], this.getHsvRgb(j));
                  } finally {
                     CorzRoundedUtils.handlePanelOverlay(panelOverlay2);
                  }

                  matrixMatricesValue += (this.shieldAmount + 4.0F) * panelOverlayLimit;
               }
            }
         } finally {
            NativeHud.renderPopMatrices(context, translateMatrices);
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
         }
      }
   }

   private boolean canAmountsScaled(int index, int scaledLimit, boolean scaledFlag) {
      float scaledValue = this.hotbarWidths[index];
      float scaledValue2 = this.hotbarHeights[index];
      float scaledValue3 = this.hotbarTargetXs[index];
      float scaledValue4 = this.hotbarTargetYs[index];
      float scaledValue5 = 6.0F;
      float[] huddefaultsmap = this.hudDefaultsMap.get(this.hotbarTexts[index]);
      int scaledValue6 = huddefaultsmap == null ? 0 : (int)huddefaultsmap[4];
      float maxValue = Float.MAX_VALUE;
      int deltaN3 = 1;
      int scaledLimit4 = scaledFlag ? this.scaledValue : scaledLimit;
      this.scaledScratchValues[0] = scaledValue3;
      this.hudTable[0] = scaledValue4;
      this.scaledResultCount = 0;

      for (int j = 0; j < 3 && deltaN3 > 0; j++) {
         int deltaN = 0;

         for (int m = 0; m < deltaN3; m++) {
            float amount = this.scaledScratchValues[m];
            float hashHuntBound = this.hudTable[m];
            boolean scaledFlag2 = true;

            for (int p = 0; p < scaledLimit4; p++) {
               if (p != scaledLimit) {
                  int index3 = this.hotbarSortedIndices[p];
                  float scaledValue7 = p < scaledLimit ? this.hotbarTable[index3] : this.hotbarTargetXs[index3];
                  float scaledValue8 = p < scaledLimit ? this.hotbarCurrentYs[index3] : this.hotbarTargetYs[index3];
                  float scaledValue9 = this.hotbarWidths[index3];
                  float scaledValue10 = this.hotbarHeights[index3];
                  if (canFlag(amount, hashHuntBound, scaledValue, scaledValue2, scaledValue7, scaledValue8, scaledValue9, scaledValue10)) {
                     scaledFlag2 = false;
                     if (j == 2) {
                        break;
                     }

                     for (int q = 1; q < 5 && deltaN < this.scaledTable.length; q++) {
                        float scaledLimit5 = q == 1 ? scaledValue7 - scaledValue5 - scaledValue : (q == 2 ? scaledValue7 + scaledValue9 + scaledValue5 : amount);
                        float scaledLimit6 = q == 3 ? scaledValue8 - scaledValue5 - scaledValue2 : (q == 4 ? scaledValue8 + scaledValue10 + scaledValue5 : hashHuntBound);
                        if (!(scaledLimit5 < 0.0F)
                           && !(scaledLimit6 < 0.0F)
                           && !(scaledLimit5 > this.scaledWidth - scaledValue)
                           && !(scaledLimit6 > this.scaledHeight - scaledValue2)
                           && !(Math.abs(scaledLimit5 - scaledValue3) + Math.abs(scaledLimit6 - scaledValue4) >= maxValue + 10.0F)) {
                           this.scaledTable[deltaN] = scaledLimit5;
                           this.table[deltaN] = scaledLimit6;
                           deltaN++;
                        }
                     }
                  }
               }
            }

            if (scaledFlag2 && j != 0) {
               float deltaVar = amount - scaledValue3;
               float planValuesItem = hashHuntBound - scaledValue4;
               int value = Math.abs(deltaVar) >= Math.abs(planValuesItem) ? (deltaVar < 0.0F ? 1 : 2) : (planValuesItem < 0.0F ? 3 : 4);
               float scaledValue11 = Math.abs(deltaVar) + Math.abs(planValuesItem) - (value == scaledValue6 ? 10.0F : 0.0F);
               if (scaledValue11 < maxValue) {
                  maxValue = scaledValue11;
                  this.scaledBestPosition = amount;
                  this.scaledAmount = hashHuntBound;
                  this.scaledResultCount = value;
               }
            }
         }

         System.arraycopy(this.scaledTable, 0, this.scaledScratchValues, 0, deltaN);
         System.arraycopy(this.table, 0, this.hudTable, 0, deltaN);
         deltaN3 = deltaN;
      }

      return maxValue != Float.MAX_VALUE;
   }

   private String getUpperCase(int upperCaseValue) {
      if (upperCaseValue < 0) {
         return "";
      } else if (PageRaltUtils.canFlag(upperCaseValue)) {
         return "M" + (PageRaltUtils.getValue(upperCaseValue) + 1);
      } else {
         String formatNone = PageRaltUtils.formatNone(upperCaseValue);
         if (formatNone != null && !formatNone.equals("None")) {
            return formatNone.toUpperCase(Locale.ROOT);
         } else {
            try {
               String glfwGetKeyName = GLFW.glfwGetKeyName(upperCaseValue, 0);
               if (glfwGetKeyName != null && !glfwGetKeyName.isBlank()) {
                  return glfwGetKeyName.toUpperCase(Locale.ROOT);
               }
            } catch (Throwable e) {
            }

            return PageRaltUtils.formatText(upperCaseValue);
         }
      }
   }

   public static boolean isSpectatorHidden() {
      ShowHUD singleton2 = singleton;
      return singleton2 != null && singleton2.isEnabled() && singleton2.customHotbar.isEnabled()
         ? mc.player != null && !mc.player.isSpectator() && !mc.options.hudHidden && !DirExtractionUtils.isFlag()
         : false;
   }

   public static float getBeneficialEffects() {
      ShowHUD singleton2 = singleton;
      if ((singleton2 == null || !singleton2.isEnabled()) && mc.player != null && !mc.options.hudHidden) {
         boolean beneficialEffectsFlag = false;
         boolean beneficialEffectsFlag2 = false;

         for (StatusEffectInstance statusEffectInstance : mc.player.getStatusEffects()) {
            if (statusEffectInstance.shouldShowIcon()) {
               if (statusEffectInstance.getEffectType().value().isBeneficial()) {
                  beneficialEffectsFlag = true;
               } else {
                  beneficialEffectsFlag2 = true;
               }
            }
         }

         return beneficialEffectsFlag2 ? 51.0F : (beneficialEffectsFlag ? 25.0F : 0.0F);
      } else {
         return 0.0F;
      }
   }

   public static boolean isFlag() {
      return true;
   }

   private float getAmount(double hudValue, double hudValue2) {
      float hudDivisor = this.bottomDirectionX * this.bottomDirectionX + this.bottomDirectionY * this.bottomDirectionY;
      return hudDivisor < 1.0F ? 1.0F : (float)((hudValue - this.bottomOriginX) * this.bottomDirectionX + (hudValue2 - this.bottomOriginY) * this.bottomDirectionY) / hudDivisor;
   }

   private void handleOnlineStaff(VertexTextureFont vertexTexture) {
      this.vertexTextureOnlineStaff = vertexTexture;

      for (int i = 0; i < 5; i++) {
         this.capss[i] = new Caps(levelArmorTexts[i], 0.66F, 0.6F);
      }

      this.modulesAmount = NativeHud.getAmount3("BPS", 0.78F);

      for (int j = 0; j < 3; j++) {
         this.modulesKeybindsAmounts[j] = NativeHud.getAmount3(clientCorzTexts[j], 0.78F);
      }

      this.keybindsModulesAmount = NativeHud.getAmount3("Modules", 0.95F);
      this.onlineStaffAmount = NativeHud.getAmount3("Keybinds", 0.95F);
      this.potionsResourcesAmount = NativeHud.getAmount3("Resources", 0.95F);
      this.cooldownsPotionsAmount = NativeHud.getAmount3("Potions", 0.95F);
      this.cooldownsStaffAmount = NativeHud.getAmount3("Cooldowns", 0.95F);
      this.staffCorzAmount = NativeHud.getAmount3("Staff", 0.95F);
      this.corzStaffAmount = NativeHud.getAmount3("Discord.gg/leakestan", 0.8F);

      for (int k = 0; k < 4; k++) {
         this.corzStaffAmounts[k] = NativeHud.getAmount3(healthFoodTexts[k], 0.82F);
      }

      this.onlineNoneAmount = NativeHud.getAmount3("LMB", 0.7F);
      this.staffAmount = NativeHud.getAmount3("RMB", 0.7F);
      this.onlineBuildAmount = NativeHud.getAmount3("None online", 0.9F);
      this.buildOnlineAmount = NativeHud.getAmount3("Build", 0.78F);

      for (int m = 0; m < 4; m++) {
         this.onlineBuildAmounts[m] = NativeHud.getAmount3(onlineBuildTexts[m], 0.78F);
      }

      for (int p = 0; p < 6; p++) {
         this.onlineStaffTable[p] = NativeHud.getAmount3(onlineStaffTexts[p], 0.62F);
      }

      for (int q = 0; q < 4; q++) {
         this.backpackTopbarTable[q] = NativeHud.getAmount3(this.inventoryTexts[q], 0.82F);
      }
   }

   private static float getAmount(float hudLimit) {
      hudLimit = hudLimit < 0.0F ? 0.0F : (hudLimit > 1.0F ? 1.0F : hudLimit);
      float hudFactor = 1.0F - hudLimit;
      return 1.0F - hudFactor * hudFactor * hudFactor;
   }

   private void updateHudDefaults() {
      String dragReleaseText = this.dragReleaseText;
      boolean defaultsLayoutFlag = this.defaultsLayoutFlag;
      this.dragReleaseText = null;
      this.defaultsLayoutFlag = false;
      this.defaultsLayoutValue = 0;
      if (dragReleaseText != null && this.defaultsFlag) {
         this.defaultsFlag = false;
         if (defaultsLayoutFlag) {
            this.hudOffsetsMap.remove(dragReleaseText);
            this.hudModulesMap.remove(dragReleaseText);
            this.hudOrderItems.remove(dragReleaseText);
            float hudDefaultsValue = this.elementX - this.savedBaseX;
            float planValuesItem = this.elementY - this.savedBaseY;
            if (!(Math.abs(hudDefaultsValue) >= 0.05F) && !(Math.abs(planValuesItem) >= 0.05F)) {
               this.hudDefaultsMap.remove(dragReleaseText);
            } else {
               this.hudDefaultsMap.put(dragReleaseText, new float[]{0.0F, 0.0F, hudDefaultsValue, planValuesItem, 0.0F});
            }

            ModuleConfigAutosave.updateFlag2();
         } else {
            if (Math.abs(this.offsetX) < 0.5F && Math.abs(this.hudDefaultsAmount) < 0.5F) {
               this.hudOffsetsMap.remove(dragReleaseText);
               this.hudModulesMap.remove(dragReleaseText);
               this.hudOrderItems.remove(dragReleaseText);
            } else {
               this.handleScaledWidth(dragReleaseText, this.elementX, this.elementY, this.elementWidth, this.elementHeight);
               this.hudOrderItems.remove(dragReleaseText);
               this.hudOrderItems.add(dragReleaseText);
            }

            this.hudDefaultsMap.remove(dragReleaseText);
            ModuleConfigAutosave.updateFlag2();
         }
      } else {
         this.defaultsFlag = false;
      }
   }

   private void handleMaxHud(float hudValue, float hudScaleValue, float hudFactor, int corzBrandValue, int fieldsCheckedValue) {
      ShowHUD.HudScale hudScale = this.hudScale;
      if (hudScale != null) {
         float hudValue6 = hudScale.getAmount(hudValue + hudFactor);
         hudValue = hudScale.getAmount(hudValue);
         hudScaleValue = hudScale.getAmountForFloat(hudScaleValue);
         hudFactor = hudValue6 - hudValue;
      }

      if (!(hudValue + hudFactor < corzBrandValue - 220.0F) && !(hudScaleValue < fieldsCheckedValue - 96.0F)) {
         this.radarMaxWidth = Math.max(this.radarMaxWidth, fieldsCheckedValue - hudScaleValue + 4.0F - 8.0F);
      }
   }

   private String getAddress() {
      try {
         ServerInfo currentServerEntry = mc.getCurrentServerEntry();
         if (currentServerEntry != null && currentServerEntry.address != null && !currentServerEntry.address.isEmpty()) {
            return currentServerEntry.address;
         }

         if (mc.isInSingleplayer()) {
            return "Singleplayer";
         }
      } catch (Throwable e) {
      }

      return "";
   }

   public static float getAmountFloat(String hudText) {
      ShowHUD showHUD = getShowHUD();
      return showHUD != null ? showHUD.getAmount(hudText) : 0.0F;
   }

   private float getTimes(int index, long hudValue) {
      long hudValue2 = this.inventoryChangeTimes[index];
      if (hudValue2 == 0L) {
         return 1.0F;
      } else {
         float hudLimit = (float)(hudValue - hudValue2) / 2.4E8F;
         if (hudLimit >= 1.0F) {
            this.inventoryChangeTimes[index] = 0L;
            return 1.0F;
         } else {
            return 1.0F + 0.18F * NativeHud.getAmountForFloat(hudLimit);
         }
      }
   }

   private float getLayoutDefaults() {
      if (this.hotbarMinOffset == Float.MAX_VALUE) {
         return this.hotbarAmount;
      } else {
         for (int i = 0; i < this.layoutDefaultsValue && this.defaultsLayoutValue < this.defaultsLayoutTable.length / 4; this.defaultsLayoutValue++) {
            System.arraycopy(this.layoutDefaultsTable, i * 4, this.defaultsLayoutTable, this.defaultsLayoutValue * 4, 4);
            i++;
         }

         return this.layoutDefaultsAmount;
      }
   }

   private boolean canHudHotbar(String hudHotbarText, float[] hudHotbarValues) {
      float[] moduleshudmap = this.modulesHudMap.get(hudHotbarText);
      if (moduleshudmap == null) {
         return false;
      } else {
         float hudHotbarTotal = moduleshudmap[0];
         float hudScaleValue = moduleshudmap[1];
         float hudHotbarFactor = moduleshudmap[2];
         float radarSsaFactor = moduleshudmap[3];
         ShowHUD.HudScale hudScale = this.hotbarHudMap.isEmpty() ? null : this.hotbarHudMap.get(hudHotbarText);
         float hudHotbarValue = hudScale == null ? 1.0F : hudScale.hotbarOffsetsAmount;
         if (hudHotbarValue != 1.0F) {
            this.handleBottomLeftForStringHudScale(hudHotbarText, null, hudHotbarTotal, hudScaleValue, hudHotbarFactor, radarSsaFactor);
            hudHotbarTotal += this.bottomAmount * hudHotbarFactor * (1.0F - hudHotbarValue);
            hudScaleValue += this.bottomLeftAmount * radarSsaFactor * (1.0F - hudHotbarValue);
            hudHotbarFactor *= hudHotbarValue;
            radarSsaFactor *= hudHotbarValue;
         }

         hudHotbarValues[0] = hudHotbarTotal;
         hudHotbarValues[1] = hudScaleValue;
         hudHotbarValues[2] = hudHotbarFactor;
         hudHotbarValues[3] = radarSsaFactor;
         return true;
      }
   }

   @Override
   public boolean hasExtraData() {
      return true;
   }

   public static boolean isSsaPerfgraphCountRoundSsa() {
      return isInstance() && canPerfgraphSsa(ssaPerfgraphCountRoundSsa);
   }

   private void handle(int index, int subSequenceLimit) {
      if (subSequenceLimit > index) {
         subSequenceLimit--;
      }

      if (subSequenceLimit != index && subSequenceLimit >= 0 && subSequenceLimit < this.clone.length) {
         String hudText = this.clone[index];
         if (subSequenceLimit < index) {
            System.arraycopy(this.clone, subSequenceLimit, this.clone, subSequenceLimit + 1, index - subSequenceLimit);
         } else {
            System.arraycopy(this.clone, index + 1, this.clone, index, subSequenceLimit - index);
         }

         this.clone[subSequenceLimit] = hudText;
      }
   }

   private void renderCoordsRadar(DrawContext context, int coordsRadarValue, int fieldsCheckedValue) {
      if (mc.player != null) {
         long nanoTime = System.nanoTime();
         this.frameDeltaSeconds = this.lastNanoTimeCoordsRadar == 0L ? 0.016F : Math.min(0.05F, (float)(nanoTime - this.lastNanoTimeCoordsRadar) / 1.0E9F);
         this.lastNanoTimeCoordsRadar = nanoTime;
         VertexTextureFont fontRenderer = NativeHud.getFontRenderer();
         if (fontRenderer != this.vertexTextureOnlineStaff) {
            this.handleOnlineStaff(fontRenderer);
         }

         int maxRound = Math.max(1, Math.round(1.0F / MatrixPeekUtilsRender.getAmount()));
         this.shieldAmount = 16.0F * Math.max(1, Math.round(10.0F * maxRound / 16.0F)) / maxRound;
         this.smoothAmount = 16.0F * Math.max(1, Math.round(8.0F * maxRound / 16.0F)) / maxRound;
         this.potionIconSize = Math.min(12.0F, 18.0F * Math.max(1, Math.round(9.0F * maxRound / 18.0F)) / maxRound);
         this.personAmount = 8.0F * Math.max(1, Math.round(10.0F * maxRound / 8.0F)) / maxRound;
         this.topbarBackpackAmount = 16.0F * Math.max(1, Math.round(12.0F * maxRound / 16.0F)) / maxRound;
         this.backpackTopbarMinWidth = Math.max(27.0F, NativeHud.getAmount(this.topbarBackpackAmount * 2.1F));
         this.backpackTopbarAmount = Math.max(22.0F, NativeHud.getAmount(this.topbarBackpackAmount * 1.6F));
         this.radarMaxWidth = 0.0F;
         float radarCoordsAmount = 6.0F;
         float coordsRadarValue3 = 6.0F;
         this.handleAmountCoords("topbar");
         float topbarCloud = this.getTopbarCloud(context, radarCoordsAmount, coordsRadarValue3, nanoTime);
         this.updateTextAmounts();
         this.handleAmountCoords("perf");
         boolean amountPop = this.canAmountPop(context, radarCoordsAmount, coordsRadarValue3 + topbarCloud, nanoTime);
         this.updateTextAmounts();
         boolean coordsRadarFlag = this.topbarPerfFlag && !this.canHudOffsets("topbar");
         boolean coordsRadarFlag2 = amountPop && this.performance.isEnabled() && !this.canHudOffsets("perf");
         float coordsRadarValue4 = coordsRadarFlag ? 20.0F * this.getHotbarHud("topbar") : 0.0F;
         float coordsRadarValue5 = coordsRadarFlag2 ? 20.0F * this.getHotbarHud("perf") : 0.0F;
         float coordsRadarValue6 = coordsRadarValue3 + this.radarPanelHeightSmooth.getAmountForFloat(!coordsRadarFlag && !coordsRadarFlag2 ? 0.0F : coordsRadarValue4 + coordsRadarValue5 + (coordsRadarFlag && coordsRadarFlag2 ? 4.0F : 0.0F) + 6.0F, this.frameDeltaSeconds, 0.08F);
         float amounts2 = this.getAmounts("spotify", ModuleRegistry.getModule(SpotifyHUD.class), radarCoordsAmount, coordsRadarValue6);
         boolean coordsRadarFlag3 = this.radarLerpUuid.isEnabled() && !this.canHudOffsets("radar");
         boolean coordsRadarFlag4 = coordsRadarFlag3 && "Bottom Left".equals(this.enumHelper.getCurrentOption());
         float coordsRadarBound = this.getMaxRadarSize() * this.getHotbarHud("radar");
         this.radarCoordsAmount = radarCoordsAmount;
         this.radarCoordsAmount2CoordsRada = amounts2;
         this.coordsRadarAmount = amounts2;
         this.maxCoordsRadarBound = 0.0F;
         this.coordsRadarCount = 0;
         this.coordsAmount = fieldsCheckedValue
            - 6.0F
            - (!this.coordinates.isEnabled() || !coordsRadarFlag4 && this.canHudOffsets("coords") ? 0.0F : 23.0F * this.getHotbarHud("coords") + 6.0F)
            - (coordsRadarFlag4 ? coordsRadarBound + 6.0F : 0.0F);
         this.coordsRadarValue = 1;
         this.coordsAmounts[0] = this.radarCoordsAmount;
         Arrays.fill(this.coordsRadarFlags, false);

         for (int i = 0; i < this.clone.length; i++) {
            this.renderCoordsRadar(context, i, nanoTime, coordsRadarFlag3, coordsRadarBound);
         }

         this.handleAmountCoords("modules");
         this.renderModulesKeybinds(context, coordsRadarValue, fieldsCheckedValue);
         this.updateRoundModules();
         this.updateTextAmounts();
         this.handleAmountCoords("target");
         this.renderSmoothAmount(context, coordsRadarValue, fieldsCheckedValue, nanoTime);
         this.updateTextAmounts();
         this.handleAmountCoords("staff");
         this.renderPersonOnline(context, coordsRadarValue, nanoTime);
         this.updateTextAmounts();
         this.handleAmountCoords("coords");
         this.renderFieldsChecked(context, fieldsCheckedValue);
         this.updateTextAmounts();
         this.handleAmountCoords("vitals");
         this.renderFieldsTotems(context, coordsRadarValue);
         this.updateTextAmounts();
         this.handleAmountCoords("brand");
         float corzBrand = this.getCorzBrand(context, coordsRadarValue, fieldsCheckedValue);
         this.updateTextAmounts();
         this.handleAmountCoords("armor");
         this.renderShieldAmounts(context, coordsRadarValue, fieldsCheckedValue, corzBrand);
         this.updateTextAmounts();
         this.radarArmorAmount = corzBrand + (this.shieldPop.getPopProgress() > 0.01F ? 23.0F * this.getHotbarHud("armor") + 6.0F : 0.0F);
         this.handleAmountCoords("radar");
         this.renderLerpUuid(context, coordsRadarValue, fieldsCheckedValue, nanoTime);
         this.updateTextAmounts();
         NativeHud.handleMaxTime(this.radarMaxWidth);
      }
   }

   public static void handle(String hudText, float hudValue) {
      ShowHUD showHUD = getShowHUD();
      if (showHUD != null) {
         showHUD.handleHudHotbar(hudText, hudValue);
      }
   }

   private static boolean canAmount(ShowHUD.NCd nCd, ShowHUD.NCd nCd2) {
      return nCd.durationActive != nCd2.durationActive ? !nCd.durationActive : nCd.timerCooldownsAmount > nCd2.timerCooldownsAmount;
   }

   private void updateHudDefaultsHudOffsetsMap() {
      boolean hudDefaultsFlag = !this.hudOffsetsMap.isEmpty() || !this.hudModulesMap.isEmpty() || !this.isEquals();
      this.hudOffsetsMap.clear();
      this.hudModulesMap.clear();
      this.hudOrderItems.clear();

      for (Entry entry : this.hotbarHudMap.entrySet()) {
         ShowHUD.HudScale hudScale = (ShowHUD.HudScale)entry.getValue();
         float max = getCoordsPerf((String)entry.getKey());
         hudDefaultsFlag |= hudScale.hudScaleFactor != max;
         hudScale.hudScaleFactor = max;
         hudScale.bottomLeftActive = false;
      }

      this.dragReleaseText = null;
      this.defaultsFlag = false;
      this.defaultsLayoutFlag = false;
      this.defaultsLayoutValue = 0;
      this.releaseResetText = null;
      System.arraycopy(hudFlowTexts, 0, this.clone, 0, hudFlowTexts.length);
      this.hudDefaultsMap.clear();
      if (hudDefaultsFlag) {
         ModuleConfigAutosave.updateFlag2();
      }

      DisabledEnabled.getInstance().handleDisabledEnabled("HUD Layout", "Reset to defaults", true);
   }

   public static void handleScrollResize(boolean resizeScrollFlag) {
      ShowHUD showHUD = getShowHUD();
      if (showHUD != null) {
         showHUD.resizeScrollFlag = resizeScrollFlag;
      }
   }

   public static float getAmount4(String hudText) {
      ShowHUD showHUD = getShowHUD();
      return showHUD != null ? showHUD.getAmount5(hudText) : 0.0F;
   }

   private void handleTextHud(float hudValue, float hudScaleValue, float hudFactor, float maxRadarSize) {
      if (this.coordsText != null) {
         this.handleHudAmount(this.coordsText, hudValue, hudScaleValue, hudFactor, maxRadarSize, this.hudScale);
      }
   }

   public static float getHudHotbar(String hudHotbarText) {
      ShowHUD showHUD = getShowHUD();
      ShowHUD.HudScale hudScale = showHUD == null ? null : showHUD.hotbarHudMap.get(hudHotbarText);
      return hudScale == null ? 1.0F : hudScale.hudScaleFactor;
   }

   private boolean canDuration(StatusEffectInstance statusEffectInstance) {
      int duration = statusEffectInstance.getDuration();
      return duration < 0 || duration > 72000;
   }

   private void renderAmountBuild(DrawContext context, int buildValue) {
      if (this.buildPop.canTimeAmount(this.quartBuild.getTargetAmount() > 0.5, ssaPerfgraphCount)) {
         VertexTextureFont fontRenderer = NativeHud.getFontRenderer();
         if (fontRenderer != this.vertexTextureOnlineStaff) {
            this.handleOnlineStaff(fontRenderer);
         }

         boolean buildFlag = this.buildAmount > 0.5F;
         int round2 = Math.round(this.displayedBuildProgress * 100.0F);
         if (this.buildPercentField.canMax(round2)) {
            this.buildPercentField.handleAmountTime(round2 + "%", 0.9F, true, true);
         }

         long nanoTime = System.nanoTime();
         if (this.lastNanoTimeBuild == 0L || nanoTime - this.lastNanoTimeBuild >= 500000000L) {
            this.lastNanoTimeBuild = nanoTime;
            String fieldText = formatText(buildFlag);
            if (this.buildField.canMax(fieldText.hashCode())) {
               this.buildField.handleAmountTime(fieldText, 0.78F, false, true);
            }
         }

         boolean empty2 = !this.buildField.isEmpty();
         float buildValue3 = 25.0F;
         float hudFactor = this.buildSmooth
            .getAmount(
               Math.max(
                  132.0F,
                  (float)Math.ceil(
                     buildValue3 + this.buildOnlineAmount + 4.0F + this.buildPercentField.checkedRocketsAmount + (empty2 ? 10.0F + this.buildField.textWidth : 0.0F) + 7.0F
                  )
               ),
               this.frameDeltaSeconds,
               0.08F
            );
         float maxRadarSize = 26.0F;
         int spotifySong = SpotifyHUD.getSpotifySong();
         float buildValue6 = NativeHud.getAmount4((buildValue - hudFactor) / 2.0F) + this.coordsOffsetX;
         float hudScaleValue = (spotifySong > 0 ? spotifySong + 5.0F : 6.0F) + this.coordsOffsetY;
         this.handleTextHud(buildValue6, hudScaleValue, hudFactor, maxRadarSize);
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(this.buildPop.getPopProgress());
         boolean translateMatrices = NativeHud.canTranslateMatrices(context, buildValue6 + hudFactor / 2.0F, hudScaleValue + maxRadarSize / 2.0F, this.buildPop.getAmount());

         try {
            NativeHud.handle(NativeHud.cubeValue, buildValue6, hudScaleValue, hudFactor, maxRadarSize, 1.0F);
            NativeHud.handleForFloatIdentifier(buildValue6 + 6.0F, hudScaleValue + 6.0F, IcoGenUtils.getIdentifierForString("cube"), 1.0F);
            float scaleValue = buildValue6 + buildValue3;
            float nativeHudValue = buildValue6 + hudFactor - 7.0F;
            float buildValue10 = hudScaleValue + 4.0F;
            float buildValue11 = 12.0F;
            NativeHud.handlePushScale("Build", scaleValue, NativeHud.getAmount2(buildValue10, buildValue11, 0.78F), 0.78F, NativeHud.GRAY);
            NativeHud.handleAmount(
               this.buildPercentField,
               scaleValue + this.buildOnlineAmount + 4.0F + this.buildPercentField.checkedRocketsAmount,
               NativeHud.getAmount2(buildValue10, buildValue11, 0.9F),
               0.9F,
               NativeHud.OFF_WHITE
            );
            if (empty2) {
               NativeHud.handleAmount(this.buildField, nativeHudValue, NativeHud.getAmount2(buildValue10, buildValue11, 0.78F), 0.78F, NativeHud.GRAY);
            }

            NativeHud.handle(
               scaleValue,
               hudScaleValue + 18.5F,
               nativeHudValue - scaleValue,
               2.5F,
               this.displayedBuildProgress,
               CardMatrixUtils.getValue(NativeHud.getReleaseRight(), -8393375, getExperienceProgress(this.buildAmount))
            );
         } finally {
            NativeHud.renderPopMatrices(context, translateMatrices);
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
         }
      }
   }

   private void renderAmountScroll(DrawContext context) {
      boolean hasText = this.popupText != null;
      if (hasText) {
         this.scrollText = this.popupText;
      }

      String text = this.scrollText;
      if (this.scrollPop.canTimeAmount(hasText, ssaPerfgraphCount) && text != null) {
         ShowHUD.HudScale hotbarhudmap = this.hotbarHudMap.get(text);
         float scrollLimit = hotbarhudmap == null ? 1.0F : hotbarhudmap.hudScaleFactor;
         boolean scrollFlag = scrollLimit != 1.0F;
         this.scrollSmooth.getMax(scrollFlag ? 1.0F : 0.0F, this.defaultsLayoutAmount, 0.05F, 0.002F);
         if (this.canAmountHud(text)) {
            int scrollValue = Math.round(scrollLimit * 100.0F);
            boolean equals = text.equals(this.hudText);
            if (this.scrollField.canMax(scrollValue) || !equals) {
               this.scrollField.handleAmountTime(scrollValue + "%", 0.8F, true, equals);
               if (equals) {
                  this.scrollPulse.updateTime();
               }

               this.hudText = text;
            }

            long nanoTime = System.nanoTime();
            float scrollFactor = 1.0F - (float)Math.exp(-this.defaultsLayoutAmount / 0.05F);

            for (int i = 1; i <= 5; i++) {
               float scrollValue2 = this.releaseValue == i ? 1.0F : 0.0F;
               this.scrollTable[i] = this.scrollTable[i] + (scrollValue2 - this.scrollTable[i]) * scrollFactor;
               if (Math.abs(scrollValue2 - this.scrollTable[i]) < 0.004F) {
                  this.scrollTable[i] = scrollValue2;
               }
            }

            float x2 = this.scrollPop.getAmount();
            float popupCurrentX37 = this.popupCurrentX;
            float nativeHudValue = this.popupCurrentY;
            float panelOverlayBound = this.popupWidth;
            float y = nativeHudValue + 8.5F;
            float panelOverlay = CorzRoundedUtils.getPanelOverlay(this.scrollPop.getPopProgress());

            try {
               boolean translateMatrices = NativeHud.canTranslateMatrices(context, popupCurrentX37 + panelOverlayBound / 2.0F, y, x2);

               try {
                  NativeHud.handle(NativeHud.scrollValue, popupCurrentX37, nativeHudValue, panelOverlayBound, 17.0F, 8.5F, 1.0F);
                  this.renderColorAmounts(context, 2, popupCurrentX37, nativeHudValue, 17.0F, nanoTime, scrollLimit > 0.601F);
                  float peekPositionValue = popupCurrentX37 + 17.0F;
                  float translateMatricesValue = peekPositionValue + this.percentLabelWidth / 2.0F;
                  float nativeHudLimit = scrollFlag ? this.scrollTable[3] : 0.0F;
                  if (nativeHudLimit > 0.01F) {
                     NativeHud.handlePeekPosition(peekPositionValue, nativeHudValue + 2.0F, this.percentLabelWidth, 13.0F, 6.5F, 0.0F, NativeHud.getValue(385875967, nativeHudLimit));
                  }

                  boolean canTranslateMatrices = NativeHud.canTranslateMatrices(
                     context, translateMatricesValue, y, (1.0F + 0.5F * (this.scrollPulse.getTime() - 1.0F)) * (1.0F - 0.12F * this.getTimesForIntLong(3, nanoTime))
                  );

                  try {
                     NativeHud.handleAmountForFieldFloat(this.scrollField, translateMatricesValue, NativeHud.getAmount2(nativeHudValue, 17.0F, 0.8F), 0.8F, scrollFlag ? NativeHud.getWhite() : NativeHud.OFF_WHITE);
                  } finally {
                     NativeHud.renderPopMatrices(context, canTranslateMatrices);
                  }

                  float scrollValue6 = peekPositionValue + this.percentLabelWidth + 17.0F;
                  float max3 = this.scrollSmooth.getMax();
                  this.renderColorAmounts(context, 4, peekPositionValue + this.percentLabelWidth, nativeHudValue, 17.0F, nanoTime, scrollLimit < 1.7989999F);
                  if (max3 > 0.01F) {
                     float panelOverlay2 = CorzRoundedUtils.getPanelOverlay(max3 * max3);

                     try {
                        NativeHud.renderForDrawContextFloat(context, scrollValue6, nativeHudValue + 5.0F, 7.0F, 536870911);
                        this.renderColorAmounts(context, 5, scrollValue6 - 1.0F, nativeHudValue, 16.0F, nanoTime, true);
                     } finally {
                        CorzRoundedUtils.handlePanelOverlay(panelOverlay2);
                     }
                  }
               } finally {
                  NativeHud.renderPopMatrices(context, translateMatrices);
               }

               float cardMatrixFactor = this.scrollTable[1];
               float pilotStatusBound = 8.0F;
               float scrollValue9 = this.scrollAmount - pilotStatusBound / 2.0F;
               float matrixStackValue = this.popupPointerY - pilotStatusBound / 2.0F;
               int releaseRight = NativeHud.getReleaseRight();
               boolean matricesFlag = NativeHud.canTranslateMatrices(context, this.scrollAmount, this.popupPointerY, x2 * (1.0F + 0.25F * cardMatrixFactor));

               try {
                  NativeHud.handlePeekPosition(scrollValue9, matrixStackValue + 1.0F, pilotStatusBound, pilotStatusBound, pilotStatusBound / 2.0F, 1291845632, 4.0F, true);
                  NativeHud.handlePeekPosition(scrollValue9, matrixStackValue, pilotStatusBound, pilotStatusBound, pilotStatusBound / 2.0F, 0.0F, -16184562);
                  NativeHud.handlePeekPosition(
                     scrollValue9, matrixStackValue, pilotStatusBound, pilotStatusBound, pilotStatusBound / 2.0F, MatrixPeekUtilsRender.getAmount(), CardMatrixUtils.getValue(-1929379841, releaseRight, cardMatrixFactor)
                  );
                  NativeHud.render(context, this.scrollAmount, this.popupPointerY, 3.0F, CardMatrixUtils.getValue(-920587, releaseRight, cardMatrixFactor));
               } finally {
                  NativeHud.renderPopMatrices(context, matricesFlag);
               }
            } finally {
               CorzRoundedUtils.handlePanelOverlay(panelOverlay);
            }
         }
      } else {
         if (!hasText) {
            this.scrollText = null;
         }
      }
   }

   private float getHotbarAmounts(float amount, float hotbarBound, float hotbarValue, float hotbarValue2, boolean hotbarFlag) {
      this.defaultsLayoutValue = 0;
      this.handleAmountsHotbar(true, false);
      this.hotbarAmount = amount;
      this.hotbarMinOffset = Float.MAX_VALUE;
      float scaledWidth = this.scaledWidth;
      float scaledHeight = this.scaledHeight;
      float hudValues = scaledWidth / 2.0F;
      float hotbarValue4 = 6.0F;
      this.handleAmount(hudValues - hotbarValue / 2.0F, hudValues, 0.0F, hudValues, scaledHeight);
      this.handleAmount(6.0F, 6.0F, hotbarBound, 6.0F, hotbarBound + hotbarValue2);
      this.handleAmount(scaledWidth - 6.0F - hotbarValue, scaledWidth - 6.0F, hotbarBound, scaledWidth - 6.0F, hotbarBound + hotbarValue2);
      if (hotbarFlag) {
         this.handleAmount(this.defaultsAmount);
      }

      for (int i = 0; i < this.hotbarItemCount; i++) {
         if (!this.hotbarTexts[i].equals(this.dragReleaseText) && !this.canFlagsResources(this.hotbarTexts[i])) {
            float hotbarValue5 = this.hotbarTable[i];
            float wrapDegreesBound = this.hotbarCurrentYs[i];
            float hotbarValue6 = this.hotbarWidths[i];
            float hotbarValue7 = this.hotbarHeights[i];
            float minHome = Math.min(wrapDegreesBound, hotbarBound);
            float max8 = Math.max(wrapDegreesBound + hotbarValue7, hotbarBound + hotbarValue2);
            if (hotbarBound < wrapDegreesBound + hotbarValue7 + hotbarValue4 && wrapDegreesBound < hotbarBound + hotbarValue2 + hotbarValue4) {
               float maxHome = Math.max(wrapDegreesBound, hotbarBound);
               float min9 = Math.min(wrapDegreesBound + hotbarValue7, hotbarBound + hotbarValue2);
               this.handleAmount(hotbarValue5 + hotbarValue6 + hotbarValue4, true, hotbarValue5 + hotbarValue6 + hotbarValue4 / 2.0F, maxHome, hotbarValue5 + hotbarValue6 + hotbarValue4 / 2.0F, min9);
               this.handleAmount(hotbarValue5 - hotbarValue4 - hotbarValue, false, hotbarValue5 - hotbarValue4 / 2.0F, maxHome, hotbarValue5 - hotbarValue4 / 2.0F, min9);
            }

            if (!(amount < hotbarValue5 + hotbarValue6) || !(hotbarValue5 < amount + hotbarValue) || !(hotbarBound < wrapDegreesBound + hotbarValue7) || !(wrapDegreesBound < hotbarBound + hotbarValue2)) {
               this.handleAmount(hotbarValue5, hotbarValue5, minHome, hotbarValue5, max8);
               this.handleAmount(hotbarValue5 + hotbarValue6 - hotbarValue, hotbarValue5 + hotbarValue6, minHome, hotbarValue5 + hotbarValue6, max8);
               this.handleAmount(hotbarValue5 + (hotbarValue6 - hotbarValue) / 2.0F, hotbarValue5 + hotbarValue6 / 2.0F, minHome, hotbarValue5 + hotbarValue6 / 2.0F, max8);
               this.handleAmount(
                  scaledWidth - hotbarValue5 - hotbarValue6,
                  hudValues,
                  0.0F,
                  hudValues,
                  scaledHeight,
                  scaledWidth - hotbarValue5 - hotbarValue6,
                  hotbarBound,
                  scaledWidth - hotbarValue5 - hotbarValue6,
                  hotbarBound + hotbarValue2,
                  hotbarValue5 + hotbarValue6,
                  wrapDegreesBound,
                  hotbarValue5 + hotbarValue6,
                  wrapDegreesBound + hotbarValue7
               );
               this.handleAmount(scaledWidth - hotbarValue5 - hotbarValue, hudValues, 0.0F, hudValues, scaledHeight, scaledWidth - hotbarValue5, hotbarBound, scaledWidth - hotbarValue5, hotbarBound + hotbarValue2, hotbarValue5, wrapDegreesBound, hotbarValue5, wrapDegreesBound + hotbarValue7);
            }
         }
      }

      return this.getLayoutDefaults();
   }

   private float getAmount5(String hudText) {
      return this.getScaledHud(hudText)[0] + this.getDefaultsHud(hudText);
   }

   private int getMaxRadarSize() {
      float hudValue = Math.max(1, this.radarSize.getValue()) / 100.0F;
      return Math.max(40, Math.round(80.0F * hudValue));
   }

   private void handleAmountText(double amount, double hudCount2) {
      long handle = mc.getWindow().getHandle();
      boolean flag = GLFW.glfwGetMouseButton(handle, 0) == 1;
      boolean flag2 = GLFW.glfwGetMouseButton(handle, 1) == 1;
      long nanoTime = System.nanoTime();
      this.defaultsLayoutAmount = this.lastLayoutNanoTime == 0L ? 0.016F : Math.min(0.05F, (float)(nanoTime - this.lastLayoutNanoTime) / 1.0E9F);
      this.lastLayoutNanoTime = nanoTime;
      VertexTextureFont vertexTexture = NativeHud.getFontRenderer();
      if (vertexTexture != this.hudVertexTextureFont) {
         this.hudVertexTextureFont = vertexTexture;
         this.percentLabelWidth = (float)Math.ceil(NativeHud.getAmountForStringFloat("100%", 0.8F)) + 4.0F;
         this.resizeScrollAmount = NativeHud.getAmount3("Drag to move  \u00b7  corner or scroll to resize", 0.86F);
      }

      if (this.resizeHandleFlag) {
         flag = this.resizeScrollFlag;
         flag2 = false;
         if (this.resizeHandleText != null && this.canAmountHud(this.resizeHandleText)) {
            boolean equals = this.resizeHandleText.equals(this.resizeScrollText);
            float hudValue = equals ? this.popupCurrentX : this.popupTargetX;
            float amount3 = (equals ? this.popupCurrentY : this.popupTargetY) + 8.5F;
            switch (this.minusValue) {
               case 1:
                  this.anchorCenterX = this.scrollAmount;
                  this.hudCount = this.popupPointerY;
                  break;
               case 2:
                  this.anchorCenterX = hudValue + 8.5F;
                  this.hudCount = amount3;
                  break;
               case 3:
                  this.anchorCenterX = hudValue + 17.0F + this.percentLabelWidth / 2.0F;
                  this.hudCount = amount3;
                  break;
               case 4:
                  this.anchorCenterX = hudValue + 17.0F + this.percentLabelWidth + 8.5F;
                  this.hudCount = amount3;
                  break;
               case 5:
                  this.anchorCenterX = hudValue + 34.0F + this.percentLabelWidth + 8.0F;
                  this.hudCount = amount3;
                  break;
               default:
                  this.anchorCenterX = this.anchorLeft + this.anchorWidth / 2.0F;
                  this.hudCount = this.anchorTop + this.anchorHeight / 2.0F;
            }

            this.resizeHandleText = null;
         }

         amount = this.anchorCenterX;
         hudCount2 = this.hudCount;
      }

      this.scaledMatricesAmount = amount;
      this.dragMouseY = hudCount2;
      String text = null;

      for (int i = this.hudResetItems.size() - 1; i >= 0; i--) {
         ShowHUD.HudBox hudresetitem = this.hudResetItems.get(i);
         if (amount >= hudresetitem.dragResetAmount && amount <= hudresetitem.dragResetAmount + hudresetitem.hudBoxAmount && hudCount2 >= hudresetitem.hudAmount && hudCount2 <= hudresetitem.hudAmount + hudresetitem.boxHeight) {
            text = hudresetitem.dragResetText;
            break;
         }
      }

      boolean hudFlag = this.dragReleaseText != null || this.releaseResetText != null;
      boolean canAmountHud2 = this.popupText != null && this.canAmountHud(this.popupText);
      if (this.popupText != null && !canAmountHud2) {
         this.popupText = null;
         this.releaseResetText = null;
      }

      int index = canAmountHud2 && !hudFlag && this.scrollPop.getPopProgress() > 0.35F ? this.getAmountScroll(amount, hudCount2) : 0;
      if (!hudFlag) {
         boolean hudFlag3 = canAmountHud2
            && text == null
            && amount >= this.popupCurrentX - 6.0F
            && amount <= this.popupCurrentX + this.popupWidth + 6.0F
            && hudCount2 >= Math.min(this.popupCurrentY, this.anchorTop + this.anchorHeight)
            && hudCount2 <= Math.max(this.popupCurrentY + 17.0F, this.anchorTop);
         if (index != 0 || hudFlag3) {
            this.lastNanoTimeRelease = 0L;
         } else if (text != null) {
            if (!text.equals(this.popupText)) {
               this.popupText = text;
               this.scrollPop.updateTime();
               canAmountHud2 = this.canAmountHud(text);
            }

            this.lastNanoTimeRelease = 0L;
         } else if (this.popupText != null) {
            if (this.lastNanoTimeRelease == 0L) {
               this.lastNanoTimeRelease = nanoTime;
            } else if (nanoTime - this.lastNanoTimeRelease > 180000000L) {
               this.popupText = null;
            }
         }
      }

      if (this.popupText != null && canAmountHud2) {
         if (!this.popupText.equals(this.resizeScrollText) || this.dragReleaseText != null) {
            this.resizeScrollText = this.popupText;
            this.popupCurrentX = this.popupTargetX;
            this.popupCurrentY = this.popupTargetY;
         } else if (index < 2) {
            float hudFactor = 1.0F - (float)Math.exp(-this.defaultsLayoutAmount / 0.05F);
            this.popupCurrentX = this.popupCurrentX + (this.popupTargetX - this.popupCurrentX) * hudFactor;
            this.popupCurrentY = this.popupCurrentY + (this.popupTargetY - this.popupCurrentY) * hudFactor;
            if (Math.abs(this.popupTargetX - this.popupCurrentX) < 0.1F) {
               this.popupCurrentX = this.popupTargetX;
            }

            if (Math.abs(this.popupTargetY - this.popupCurrentY) < 0.1F) {
               this.popupCurrentY = this.popupTargetY;
            }
         }
      }

      String hudText = index != 0 ? this.popupText : text;
      this.dragRightText = this.releaseResetText != null ? this.releaseResetText : hudText;
      this.releaseText = hudFlag ? null : hudText;
      this.releaseValue = this.releaseResetText != null ? 1 : index;
      if (flag && !this.releaseActive) {
         if (index == 1) {
            this.handleBottomAmount(this.popupText, amount, hudCount2);
         } else if (index == 2 || index == 4) {
            this.handleHudHotbar(this.popupText, index == 4 ? 1 : -1);
            this.eventNanoTimes[index] = nanoTime;
         } else if (index == 3 || index == 5) {
            this.handleHudHotbar(this.popupText, getCoordsPerf(this.popupText));
            this.eventNanoTimes[index] = nanoTime;
         } else if (index == 0 && text != null && this.canAmountHud(text, this.defaultsTable)) {
            this.dragReleaseText = text;
            this.defaultsFlag = false;
            float[] scaledHud = this.getScaledHud(text);
            this.offsetX = scaledHud[0] + this.getDefaultsHud(text);
            this.hudDefaultsAmount = scaledHud[1] + this.getDefaultsHudForString(text);
            this.defaultX = this.elementX = this.defaultsTable[0];
            this.defaultY = this.elementY = this.defaultsTable[1];
            this.elementWidth = this.defaultsTable[2];
            this.elementHeight = this.defaultsTable[3];
            this.defaultsAmount = this.defaultX - this.offsetX;
            this.baseY = this.defaultY - this.hudDefaultsAmount;
            this.dragStartMouseX = amount;
            this.dragStartMouseY = hudCount2;
         }
      } else if (flag && this.releaseResetText != null) {
         this.handleHudHotbar(this.releaseResetText, getAmount3(this.getAmount(amount, hudCount2) + this.bottomProjectionEnd));
      } else if (flag && this.dragReleaseText != null) {
         this.handleAmountDefaults(amount, hudCount2);
      } else if (!flag && this.releaseActive) {
         if (this.dragReleaseText != null) {
            this.updateHudDefaults();
         }

         this.releaseResetText = null;
      }

      if (flag2 && !this.hudActive && hudText != null) {
         boolean hasRemove = this.hudOffsetsMap.remove(hudText) != null;
         boolean hudFlag4 = hasRemove | this.hudModulesMap.remove(hudText) != null;
         boolean hudFlag5 = hudFlag4 | this.hudOrderItems.remove(hudText);
         ShowHUD.HudScale hotbarhudmap = this.hotbarHudMap.get(hudText);
         float max = getCoordsPerf(hudText);
         if (hotbarhudmap != null) {
            hudFlag5 |= hotbarhudmap.hudScaleFactor != max;
            hotbarhudmap.hudScaleFactor = max;
            hotbarhudmap.bottomLeftActive = false;
         }

         if (hudFlag5) {
            ModuleConfigAutosave.updateFlag2();
         }

         if (hudText.equals(this.dragReleaseText)) {
            this.dragReleaseText = null;
            this.defaultsFlag = false;
            this.defaultsLayoutFlag = false;
            this.defaultsLayoutValue = 0;
         }

         if (hudText.equals(this.releaseResetText)) {
            this.releaseResetText = null;
         }
      }

      this.releaseActive = flag;
      this.hudActive = flag2;
   }

   public static boolean isSsaPerfgraphCountPotionsSsa() {
      return isInstance() && canPerfgraphSsa(ssaPerfgraphCountPotionsSsa);
   }

   private void updateTimerCooldowns() {
      for (int i = 0; i < this.cooldownsTimerItems.size(); i++) {
         this.cooldownsTimerItems.get(i).updateAmountFlag();
      }

      try {
         AccessorItemCooldownManager itemCooldownManager = (AccessorItemCooldownManager)mc.player.getItemCooldownManager();
         int corzGetTick = itemCooldownManager.corzGetTick();
         var corzGetEntries = itemCooldownManager.corzGetEntries();
         if (corzGetEntries != null) {
            for (Entry entry : corzGetEntries.entrySet()) {
               if (entry.getValue() instanceof AccessorItemCooldownEntry accessorItemCooldownEntry) {
                  int timerCooldownsLimit = accessorItemCooldownEntry.corzEndTick() - corzGetTick;
                  if (timerCooldownsLimit > 0) {
                     Identifier identifier = (Identifier)entry.getKey();
                     ShowHUD.NCd nCd = this.timerCooldownsNCdMap.get(identifier);
                     if (nCd == null) {
                        nCd = new ShowHUD.NCd(identifier);
                        Item item = Registries.ITEM.get(identifier);
                        boolean timerCooldownsFlag = item != null && item != Items.AIR;
                        nCd.itemStack = new ItemStack(timerCooldownsFlag ? item : Items.PAPER);
                        nCd.timerCooldownsText = timerCooldownsFlag ? nCd.itemStack.getName().getString() : formatPath(identifier);
                        nCd.nCdAmount = NativeHud.getAmount3(nCd.timerCooldownsText, 0.9F);
                        nCd.handleAmount(this.cooldownsTimerItems.size() * 15.0F);
                        this.timerCooldownsNCdMap.put(identifier, nCd);
                        this.cooldownsTimerItems.add(nCd);
                     }

                     nCd.updateAmountFlagDurationActive();
                     nCd.timerCooldownsAmount = timerCooldownsLimit / 20.0F;
                     if (nCd.field.canMax(Math.round(nCd.timerCooldownsAmount * 10.0F))) {
                        nCd.field.handleAmountTime(formatText(nCd.timerCooldownsAmount), 0.9F, true, false);
                     }
                  }
               }
            }
         }
      } catch (Throwable e) {
      }

      for (int j = 1; j < this.cooldownsTimerItems.size(); j++) {
         ShowHUD.NCd cooldownstimeritem = this.cooldownsTimerItems.get(j);

         int timerCooldownsCounter;
         for (timerCooldownsCounter = j - 1; timerCooldownsCounter >= 0 && canAmount(cooldownstimeritem, this.cooldownsTimerItems.get(timerCooldownsCounter)); timerCooldownsCounter--) {
            this.cooldownsTimerItems.set(timerCooldownsCounter + 1, this.cooldownsTimerItems.get(timerCooldownsCounter));
         }

         this.cooldownsTimerItems.set(timerCooldownsCounter + 1, cooldownstimeritem);
      }

      int value = 0;

      for (int k = 0; k < this.cooldownsTimerItems.size(); k++) {
         ShowHUD.NCd cooldownstimeritem2 = this.cooldownsTimerItems.get(k);
         if (!cooldownstimeritem2.durationActive) {
            cooldownstimeritem2.onlineNoneAmount = value++ * 15.0F;
         }
      }

      this.timerCooldownsValue = value;
      if (value > 0 && this.timer.canMax(value)) {
         this.timer.handleAmountTime(String.valueOf(value), 0.8F, true, false);
         this.timerCooldownsPulse.updateTime();
      }
   }

   private void updateRoundModules() {
      ShowHUD.HudScale hudScale = this.hudScale;
      if (hudScale != null && this.modulesFlag && !this.canHudOffsets("modules")) {
         int roundModulesValue = this.roundedModulesWidth + this.roundModulesValue;
         this.roundedModulesWidth = Math.round(hudScale.getAmount(this.roundedModulesWidth));
         this.roundModulesValue = roundModulesValue - this.roundedModulesWidth;
      }
   }

   public static float getAmount(DrawContext context, float hudValue, float glfwLimit, float regionmapRegionBound, float regionmapRegionTotal, String hudText) {
      render(context, hudValue, glfwLimit, regionmapRegionBound, regionmapRegionTotal, 7.0F, -671088640);
      if (hudText == null) {
         return glfwLimit + 14.5F;
      } else {
         VertexTexturePeek suisse = VertexTexturePeek.getSuisse();
         if (suisse.isCorzclientFlag()) {
            float hudValue5 = 13.0F;
            float ctxOffset = hudValue + 6.0F;
            float peekEndValue = glfwLimit + 5.3F;
            suisse.handlePeekEnd("|", ctxOffset, peekEndValue, -1, 13.0F);
            float hudValue7 = ctxOffset + (suisse.getCorzclientAmounts("|", 13.0F) + 3.0F);
            suisse.handlePeekEnd(hudText, hudValue7, peekEndValue, -1, 13.0F);
         } else {
            VertexTextureFont font = HudFontUtils.getSmallFont();
            font.handle("| " + hudText, hudValue + 6.0F, font.getAbcdefghijklmnopqrstuvwxyzBrown(Math.round(glfwLimit), 15), new Color(-1, true));
         }

         return glfwLimit + 14.5F;
      }
   }

   private static String formatText(float hudValue) {
      int round2 = Math.round(hudValue * 10.0F);
      return round2 / 10 + "." + Math.abs(round2 % 10) + "s";
   }

   private static float getTextMono(VertexTexturePeek vertexTexturePeek, ShowHUD.H2Seg h2Seg, float monoValue) {
      if (h2Seg.brand) {
         VertexTexturePeek wave = VertexTexturePeek.getWave();
         if (wave.isCorzclientFlag()) {
            return wave.getCorzclientAmounts(h2Seg.text, 13.0F * monoValue);
         }
      }

      return h2Seg.mono ? getAmount(vertexTexturePeek, h2Seg.text, 12.0F * monoValue) : vertexTexturePeek.getCorzclientAmounts(h2Seg.text, 12.0F * monoValue);
   }

   private void handleTpsSignal(VertexTexturePeek vertexTexture, long time) {
      boolean booleanValue = this.metricIcons.isEnabled();
      int tpsValue = booleanValue ? 1 : 0;
      int currentFps = mc.getCurrentFps();
      int gaugeSignalValue = this.gaugeSignalValue;
      long tpsSignalTime = Math.round(this.tpsServerAmount * 10.0);
      boolean tpsSignalFlag = tpsValue != this.tpsValue || vertexTexture != this.vertexTextureTpsSignal;
      if (tpsSignalFlag || this.tpsSignalValue == 0L || time - this.tpsSignalValue >= 500000000L) {
         this.tpsSignalValue = time;
         gaugeSignalValue = this.getMaxInt();
      }

      if (tpsSignalFlag || currentFps != this.currentFps || gaugeSignalValue != this.gaugeSignalValue || tpsSignalTime != this.tpsSignalTime) {
         if (tpsSignalFlag || currentFps != this.currentFps) {
            this.handleSegsAmounts(vertexTexture, 0, booleanValue ? IcoGenUtils.getIdentifierGENERATED_IDENTIFIER_CACHE("gauge") : null, currentFps + " FPS");
         }

         if (tpsSignalFlag || gaugeSignalValue != this.gaugeSignalValue) {
            this.handleSegsAmounts(vertexTexture, 1, booleanValue ? IcoGenUtils.getIdentifierGENERATED_IDENTIFIER_CACHE("signal") : null, gaugeSignalValue + " ms");
         }

         if (tpsSignalFlag || tpsSignalTime != this.tpsSignalTime) {
            this.handleSegsAmounts(vertexTexture, 2, booleanValue ? IcoGenUtils.getIdentifierGENERATED_IDENTIFIER_CACHE("server") : null, String.format(Locale.ROOT, "%.1f TPS", this.tpsServerAmount));
         }

         this.tpsValue = tpsValue;
         this.vertexTextureTpsSignal = vertexTexture;
         this.currentFps = currentFps;
         this.gaugeSignalValue = gaugeSignalValue;
         this.tpsSignalTime = tpsSignalTime;
         this.tpsSignalAmount = this.tpsSignalTable[0] + this.tpsSignalTable[1] + this.tpsSignalTable[2];
      }
   }

   private boolean canAmounts(int index, int index2) {
      int hudLimit = this.hotbarSortPriorities[index];
      int hudValue = this.hotbarSortPriorities[index2];
      if (hudLimit != hudValue) {
         return hudLimit > hudValue;
      } else if (hudLimit != Integer.MIN_VALUE) {
         return false;
      } else {
         return this.hotbarTargetYs[index] != this.hotbarTargetYs[index2] ? this.hotbarTargetYs[index] < this.hotbarTargetYs[index2] : this.hotbarTargetXs[index] < this.hotbarTargetXs[index2];
      }
   }

   public ShowHUD() {
      super("Show HUD", IconsPngType.VISUAL);
      this.performance = new BooleanSetting("Performance", "FPS & Ping", true);
      this.clockScaledMatrices = new BooleanSetting("Clock", "Subtle clock", true);
      this.buildProgress = new BooleanSetting("Build Progress", "Auto Builder completion bar under the clock", true);
      this.coordinates = new BooleanSetting("Coordinates", "XYZ display", true);
      this.potionsScaledMatrices = new BooleanSetting("Potions", "Status-effect widget", true);
      this.effectIcons = new BooleanSetting("Effect Icons", "Show effect icons", true);
      this.moduleList = new BooleanSetting("Module List", "Active modules", true);
      this.radarLerpUuid = new BooleanSetting("Radar", "Nearby-player radar", true);
      this.targetHud = new BooleanSetting("Target HUD", "Crosshair target info", true);
      this.targetPlayersOnly = new BooleanSetting("Target Players Only", "Only show the Target HUD for players, never mobs", true);
      this.keystrokesScaledMatrices = new BooleanSetting("Keystrokes", "WASD + clicks", true);
      this.cpsScaledMatrices = new BooleanSetting("CPS", "Clicks per second", true);
      this.serverInfo = new BooleanSetting("Server Info", "IP / TPS", true);
      this.armorHud = new BooleanSetting("Armor HUD", "Armor durability", true);
      this.metricIcons = new BooleanSetting("Metric Icons", "Small icons before status-bar metrics", true);
      this.keybindsCard = new BooleanSetting("Keybinds Card", "Enabled modules listed with their keybinds", true);
      this.booleanBase = new BooleanSetting("Resources Card", "Gapple, totem, crystal and exp bottle counts", true);
      this.inventoryCard = new BooleanSetting("Inventory Card", "Your 27 inventory slots at a glance", true);
      this.performanceCard = new BooleanSetting("Performance Card", "FPS graph of the last 5 seconds", true);
      this.vitalsCard = new BooleanSetting("Vitals Card", "Health, food, armor and XP card above the hotbar, in place of the vanilla bars", true);
      this.customHotbar = new BooleanSetting("Custom Hotbar", "Replace the vanilla hotbar with a smooth custom one", false);
      this.sortEnum = new EnumSetting("Sort", "Module sort", "Length", "Length", "Alphabetical", "Category");
      this.enumHelper = new EnumSetting(
         "Radar Placement",
         "Where the compass radar sits. Auto docks it on the right edge, clear of the other widgets.",
         "Auto",
         "Auto",
         "Top Left",
         "Top Right",
         "Bottom Left",
         "Bottom Right",
         "Left",
         "Right"
      );
      this.radarSize = new IntegerSetting("Radar Size", "Compass radar diameter, as a percentage. Lower = smaller.", 100, 60, 220, 10);
      this.radarPlayers = new BooleanSetting(
         "Radar Players", "Plot nearby players as contact dots (friends green, others red), scaled by distance within Range.", true
      );
      this.integerHelper = new IntegerSetting("Radar Range", "How far (blocks) the radar reaches when plotting players.", 64, 16, 256, 16);
      this.dragEditor = new BooleanSetting(
         "Drag Editor",
         "Open chat (press T) to drag HUD elements anywhere with the mouse. Right-click a widget to reset it. Positions are saved to your config.",
         true
      );
      this.buttonBase = new ButtonBase("Reset HUD Layout", "Move every HUD element back to its default position.", "Reset", this::updateHudDefaultsHudOffsetsMap)
         .getFlagButtonActive();
      this.hudOffsetsMap = new HashMap<>();
      this.hudModulesMap = new HashMap<>();
      this.modulesHudMap = new HashMap<>();
      this.hudResetItems = new ArrayList<>();
      this.scaledHudTable = new float[2];
      this.defaultsTable = new float[4];
      this.hudDefaultsMap = new HashMap<>();
      this.hudOrderItems = new ArrayList<>();
      this.hotbarTexts = new String[64];
      this.hudTexts = new String[64];
      this.hotbarTargetXs = new float[64];
      this.hotbarTargetYs = new float[64];
      this.hotbarWidths = new float[64];
      this.hotbarHeights = new float[64];
      this.hotbarTable = new float[64];
      this.hotbarCurrentYs = new float[64];
      this.hotbarSortedIndices = new int[64];
      this.hotbarSortPriorities = new int[64];
      this.hotbarGroupCounts = new int[64];
      this.hudFlags = new boolean[64];
      this.scaledScratchValues = new float[256];
      this.hudTable = new float[256];
      this.scaledTable = new float[256];
      this.table = new float[256];
      this.hotbarValue = Integer.MIN_VALUE;
      this.radarEntryXs = new float[64];
      this.radarEntryYs = new float[64];
      this.radarEntryWidths = new float[64];
      this.coordsRadarTable = new float[64];
      this.hotbarFlags = new boolean[64];
      this.defaultsLayoutTable = new float[32];
      this.defaultsLayoutSmooth = new Smooth();
      this.hotbarHudMap = new HashMap<>();
      this.quartScaledMatrices = getQuart();
      this.quintScissor = getQuart();
      this.quart = getQuart();
      this.potionsAnimation = getQuart();
      this.moduleListAnimation = getQuart();
      this.quartBuild = getQuart();
      this.displayedBuildProgress = 0.0F;
      this.lastFrameTimeMs = 0L;
      this.buildAmount = 0.0F;
      this.smoothQuintScissor = new QuintScissor(QuintBounceType.LINEAR, 320, 0.0);
      this.scaledFadeAnimation = new QuintScissor(QuintBounceType.CUBIC_OUT, 220, 1.0);
      this.scaledMatricesQuintScissor = new QuintScissor(QuintBounceType.CUBIC_OUT, 240, 0.0);
      this.scaledFadeInPending = false;
      this.keystrokesAnimation = getQuart();
      this.cpsAnimation = getQuart();
      this.serverInfoAnimation = getQuart();
      this.hudQuintScissor = getQuart();
      this.arrayDeque = new ArrayDeque<>();
      this.tpsServerAmount = 20.0;
      this.keyWPressAnimation = getQuart();
      this.quartLinked = getQuart();
      this.keySPressAnimation = getQuart();
      this.holdKeyAnimation = getQuart();
      this.staffQuintScissor = getQuart();
      this.linkedQuintScissor = getQuart();
      this.quartStaff = getQuart();
      this.linkedLinkedList = new LinkedList<>();
      this.linkedList = new LinkedList<>();
      this.linkedWasActive = false;
      this.linkedActive = false;
      this.entity = null;
      this.lastTimeMillis = 0L;
      this.scaleFactor = Integer.MIN_VALUE;
      this.scaledMatricesValue = Integer.MIN_VALUE;
      this.defaultsLayoutAmount = 0.016F;
      this.scrollPop = new Pop();
      this.scrollResizePop = new Pop();
      this.scrollField = new Field();
      this.scrollPulse = new Pulse();
      this.scrollSmooth = new Smooth();
      this.scrollTable = new float[7];
      this.eventNanoTimes = new long[7];
      this.layoutDefaultsTable = new float[12];
      this.alphabeticalCategory = List.of();
      this.enumValue = 0L;
      this.enumCount = 0;
      this.staffPop = new Pop();
      this.flaskPotionsPop = new Pop();
      this.timerCooldownsPop = new Pop();
      this.personOnlinePop = new Pop();
      this.smoothPop = new Pop();
      this.lerpUuidPop = new Pop();
      this.buildPop = new Pop();
      this.fieldsTotemsPop = new Pop();
      this.staffSmooth = new Smooth();
      this.flaskPotionsHeightSmooth = new Smooth();
      this.flaskPotionsSmooth = new Smooth();
      this.timerCooldownsSmooth = new Smooth();
      this.personOnlineWidthSmooth = new Smooth();
      this.personOnlineSmooth = new Smooth();
      this.secondaryBarSmooth = new Smooth();
      this.smoothSmooth = new Smooth();
      this.healthBarSmooth = new Smooth();
      this.smooth = new Smooth();
      this.absorptionSmooth = new Smooth();
      this.buildSmooth = new Smooth();
      this.airBarSmooth = new Smooth();
      this.lowHealthSmooth = new Smooth();
      this.lowFoodSmooth = new Smooth();
      this.fieldsTotemsSmooth = new Smooth();
      this.totemsOffsetSmooth = new Smooth();
      this.fieldsTotemsSmooths = new Smooth[]{new Smooth(), new Smooth(), new Smooth(), new Smooth(), new Smooth()};
      this.staffField = new Field();
      this.staffNameField = new Field();
      this.flask = new Field();
      this.timer = new Field();
      this.onlineNoneField = new Field();
      this.hudField = new Field();
      this.smoothField = new Field();
      this.distanceField = new Field();
      this.buildPercentField = new Field();
      this.buildField = new Field();
      this.fieldsTotemsFields = new Field[]{new Field(), new Field(), new Field(), new Field(), new Field()};
      this.pulse = new Pulse();
      this.timerCooldownsPulse = new Pulse();
      this.onlineNonePulse = new Pulse();
      this.corzStaffAmounts = new float[4];
      this.onlineBuildAmounts = new float[4];
      this.shieldAmount = 8.0F;
      this.smoothAmount = 8.0F;
      this.potionIconSize = 9.0F;
      this.personAmount = 8.0F;
      this.lastNanoTimeBuild = 0L;
      this.lastNanoTime = 0L;
      this.lastNanoTimeFlaskPotions = 0L;
      this.lastNanoTimeTimerCooldowns = 0L;
      this.smoothValue = 0L;
      this.lastNanoTimeSmooth = 0L;
      this.onlineNoneValue = 0;
      this.effectAmplifierValue = 0;
      this.timerCooldownsValue = 0;
      this.effectAmplifierNPotMap = new HashMap<>();
      this.effectAmplifierNPotList = new ArrayList<>();
      this.timerCooldownsNCdMap = new HashMap<>();
      this.cooldownsTimerItems = new ArrayList<>();
      this.onlineNoneNStaffMap = new HashMap<>();
      this.onlineNoneItems = new ArrayList<>();
      this.lastRenderedAmount = -1.0F;
      this.smoothItemStacks = new ItemStack[6];
      this.lerpUuidUuidSet = new HashSet<>();
      this.lastNanoTimeLerpUuid = 0L;
      this.topbarCloudPop = new Pop();
      this.popPop = new Pop();
      this.keybindsKeyboardPop = new Pop();
      this.backpackTopbarPop = new Pop();
      this.fieldsCheckedPop = new Pop();
      this.shieldPop = new Pop();
      this.modulesKeybindsPop = new Pop();
      this.corzBrandPop = new Pop();
      this.field = new Field();
      this.modulesKeybindsField = new Field();
      this.keyboard = new Field();
      this.moreCountField = new Field();
      this.fields = new Field[]{new Field(), new Field(), new Field()};
      this.cloudPersonH2Segs = new ShowHUD.H2Seg[4];
      this.h2Segs = new ShowHUD.H2Seg[3];
      this.cloudPersonTable = new float[4];
      this.tpsSignalTable = new float[3];
      this.cloudPersonText = "";
      this.cloudPersonCachedSubText = "";
      this.cloudPersonCachedValue = -1;
      this.tpsValue = -1;
      this.currentFps = -1;
      this.cloudPersonValue = Long.MIN_VALUE;
      this.lastNanoTimeCloudPerson = 0L;
      this.tpsSignalTime = Long.MIN_VALUE;
      this.radarPanelHeightSmooth = new Smooth();
      this.topbarCloudSmooth = new Smooth();
      this.keybindsVisibilitySmooth = new Smooth();
      this.keybindsKeyboardSmooth = new Smooth();
      this.resourcesVisibilitySmooth = new Smooth();
      this.backpackTopbarSmooth = new Smooth();
      this.coordsRadarSmooth = new Smooth();
      this.fieldsCheckedSmooth = new Smooth();
      this.corzBrandSmooth = new Smooth();
      this.modulesKeybindsSmooth = new Smooth();
      this.hudSmooth = new Smooth();
      this.totemsSpacingSmooth = new Smooth();
      this.coordsRadarNFlow = new ShowHUD.NFlow();
      this.hudNFlow = new ShowHUD.NFlow();
      this.flaskPotionsFlow = new ShowHUD.NFlow();
      this.timerCooldownsFlow = new ShowHUD.NFlow();
      this.modulesKeybindsPulse = new Pulse();
      this.keybindsKeyboardPulse = new Pulse();
      this.modulesKeybindsNList = new ShowHUD.NList(13.5F, 0.88F, false);
      this.nList = new ShowHUD.NList(15.0F, 0.9F, true);
      this.sortedKeybindModules = new ArrayList<>();
      this.cachedEnumCount = 0;
      this.modulesKeybindsValue = -1;
      this.keybindsKeyboardValue = 0L;
      this.lastNanoTimeCoordsRadar = 0L;
      this.tpsSignalValue = 0L;
      this.lastNanoTimeBackpackTopbar = 0L;
      this.frameDeltaSeconds = 0.016F;
      this.inventoryCounts = new int[4];
      this.inventoryTable = new int[4];
      this.inventoryChangeTimes = new long[4];
      this.inventoryTexts = new String[]{"", "", "", ""};
      this.smooths = new Smooth[]{new Smooth(), new Smooth(), new Smooth(), new Smooth()};
      this.topbarBackpackAmount = 12.0F;
      this.backpackTopbarMinWidth = 27.0F;
      this.backpackTopbarAmount = 22.0F;
      this.itemStacks = new ItemStack[4];
      this.shieldTable = new float[4];
      this.shieldDurabilityFractions = new float[4];
      this.shieldDisplayedFractions = new float[4];
      this.hsvColorCache = new int[4];
      this.hsvRgbTable = new long[]{-1L, -1L, -1L, -1L};
      this.modulesKeybindsAmounts = new float[3];
      this.capss = new Caps[5];
      this.clone = (String[])hudFlowTexts.clone();
      this.inventoryResourcesValues = new int[hudFlowTexts.length];
      this.coordsAmounts = new float[8];
      this.keybindsFlow = new ShowHUD.NFlow();
      this.coordsRadarSmooths = new Smooth[]{new Smooth(), new Smooth(), new Smooth(), new Smooth(), new Smooth(), new Smooth()};
      this.coordsRadarFlags = new boolean[8];
      this.rosterStaff = -1;
      this.backpackTopbarTable = new float[4];
      this.performancePerfgraphPop = new Pop();
      this.barInventoryPop = new Pop();
      this.performancePerfgraphSmooth = new Smooth();
      this.barInventorySmooth = new Smooth();
      this.styleHudWidthSmooth = new Smooth();
      this.styleHudSmooth = new Smooth();
      this.performancePerfgraphField = new Field();
      this.performanceGraphFlow = new ShowHUD.NFlow();
      this.nFlow = new ShowHUD.NFlow();
      this.styleHudCurve = new float[161];
      this.styleHudTable = new float[161];
      this.hudStyleAmounts = new float[644];
      this.styleHudColorBuffer = new float[644];
      this.onlineStaffTable = new float[6];
      this.metricIcons.withGroupName("HUD Style");
      this.keybindsCard.withGroupName("HUD Style");
      this.booleanBase.withGroupName("HUD Style");
      this.inventoryCard.withGroupName("HUD Style");
      this.performanceCard.withGroupName("HUD Style");
      this.vitalsCard.withGroupName("HUD Style");
      this.customHotbar.withGroupName("HUD Style");
      this.enumHelper.withGroupName("Radar");
      this.radarSize.withGroupName("Radar");
      this.radarPlayers.withGroupName("Radar");
      this.integerHelper.withGroupName("Radar");
      this.dragEditor.withGroupName("Drag Editor");
      this.buttonBase.withGroupName("Drag Editor");
      this.addSettings(
         new ConfigSetting[]{
            this.brandScaledMatrices,
            this.performance,
            this.clockScaledMatrices,
            this.buildProgress,
            this.coordinates,
            this.potionsScaledMatrices,
            this.effectIcons,
            this.moduleList,
            this.radarLerpUuid,
            this.enumHelper,
            this.radarSize,
            this.radarPlayers,
            this.integerHelper,
            this.targetHud,
            this.targetPlayersOnly,
            this.keystrokesScaledMatrices,
            this.cpsScaledMatrices,
            this.serverInfo,
            this.armorHud,
            this.metricIcons,
            this.keybindsCard,
            this.booleanBase,
            this.inventoryCard,
            this.performanceCard,
            this.vitalsCard,
            this.customHotbar,
            this.sortEnum,
            this.dragEditor,
            this.buttonBase
         }
      );
      this.updateHotbarHud();
      singleton = this;
      this.setEnabled(true);
   }

   public static void updateTotemsHotbar() {
      hotbarTotemsCount++;
   }

   private void renderColorAmounts(DrawContext context, int index, float colorValue, float colorValue2, float colorValue3, long nanoTime, boolean colorFlag) {
      float colorBound = colorFlag ? this.scrollTable[index] : 0.0F;
      float times = this.getTimesForIntLong(index, nanoTime);
      float rocketsElytraBound = Math.max(0.0F, times);
      float translateMatricesValue = colorValue + colorValue3 / 2.0F;
      float enabledOffset = colorValue2 + 8.5F;
      float colorLimit = Math.max(colorBound, rocketsElytraBound);
      if (colorLimit > 0.01F) {
         NativeHud.handlePeekPosition(colorValue + 2.0F, colorValue2 + 2.0F, colorValue3 - 4.0F, 13.0F, 6.5F, 0.0F, NativeHud.getValue(385875967, colorLimit));
      }

      boolean translateMatrices = NativeHud.canTranslateMatrices(context, translateMatricesValue, enabledOffset, 1.0F - 0.18F * times);

      try {
         if (index != 5) {
            int nativeHudValue = colorFlag
               ? CardMatrixUtils.getValue(CardMatrixUtils.getValue(-7630437, -920587, colorBound), NativeHud.getReleaseRight(), rocketsElytraBound)
               : NativeHud.getValue(-11117467, 0.6F);
            float crossLimit = Math.max(MatrixPeekUtilsRender.getAmount(), NativeHud.getAmount4(0.75F));
            float colorValue8 = 3.0F;
            NativeHud.handlePeekPosition(
               NativeHud.getAmount4(translateMatricesValue - colorValue8), NativeHud.getAmount4(enabledOffset - crossLimit / 2.0F), 6.0F, crossLimit, crossLimit / 2.0F, 0.0F, nativeHudValue
            );
            if (index == 4) {
               NativeHud.handlePeekPosition(
                  NativeHud.getAmount4(translateMatricesValue - crossLimit / 2.0F), NativeHud.getAmount4(enabledOffset - colorValue8), crossLimit, 6.0F, crossLimit / 2.0F, 0.0F, nativeHudValue
               );
            }

            return;
         }

         NativeHud.handle(
            IcoGenUtils.getIcoReset(),
            NativeHud.getAmount4(translateMatricesValue - 4.0F),
            NativeHud.getAmount4(enabledOffset - 4.0F),
            8.0F,
            colorLimit > 0.5F ? NativeHud.OFF_WHITE : NativeHud.GRAY
         );
      } finally {
         NativeHud.renderPopMatrices(context, translateMatrices);
      }
   }

   public static void handleResizeMinus(String resizeHandleText, String resizeMinusText) {
      ShowHUD showHUD = getShowHUD();
      if (showHUD != null) {
         if ("off".equals(resizeHandleText)) {
            showHUD.resizeHandleFlag = false;
            showHUD.resizeScrollFlag = false;
            showHUD.resizeHandleText = null;
         } else {
            showHUD.resizeHandleFlag = true;
            showHUD.resizeHandleText = resizeHandleText;

            showHUD.minusValue = switch (resizeMinusText) {
               case "handle" -> 1;
               case "minus" -> 2;
               case "label" -> 3;
               case "plus" -> 4;
               case "reset" -> 5;
               default -> 0;
            };
         }
      }
   }

   private static float getAmountForFloat(float hudLimit) {
      return hudLimit <= 0.25F ? 0.0F : (hudLimit >= 0.75F ? 1.0F : 0.5F);
   }

   @Override
   public void loadExtraData(JsonElement json) {
      if (json != null && json.isJsonObject()) {
         JsonObject asJsonObject = json.getAsJsonObject();
         this.hudOffsetsMap.clear();
         this.hudModulesMap.clear();
         this.hudOrderItems.clear();
         this.hudDefaultsMap.clear();
         this.hotbarHudMap.clear();
         System.arraycopy(hudFlowTexts, 0, this.clone, 0, hudFlowTexts.length);
         if (asJsonObject.has("hudScaleHint")) {
            this.hudScales = true;
         }

         if (asJsonObject.has("hudFlow") && asJsonObject.get("hudFlow").isJsonArray()) {
            String[] strings = new String[hudFlowTexts.length];
            int iIiCounter = 0;

            for (JsonElement jsonElement : asJsonObject.getAsJsonArray("hudFlow")) {
               try {
                  String asString = jsonElement.getAsString();
                  boolean iIiFlag = false;

                  for (int i = 0; i < iIiCounter; i++) {
                     iIiFlag |= strings[i].equals(asString);
                  }

                  if (!iIiFlag && getFlowHud(asString) >= 0 && iIiCounter < strings.length) {
                     strings[iIiCounter++] = asString;
                  }
               } catch (Exception e) {
               }
            }

            for (String iIiText : hudFlowTexts) {
               boolean iIiFlag2 = false;

               for (int j = 0; j < iIiCounter; j++) {
                  iIiFlag2 |= strings[j].equals(iIiText);
               }

               if (!iIiFlag2) {
                  strings[iIiCounter++] = iIiText;
               }
            }

            System.arraycopy(strings, 0, this.clone, 0, strings.length);
         }

         if (asJsonObject.has("hudOrder") && asJsonObject.get("hudOrder").isJsonArray()) {
            for (JsonElement getAsJsonItem : asJsonObject.getAsJsonArray("hudOrder")) {
               try {
                  String downOffset = getAsJsonItem.getAsString();
                  if (!this.hudOrderItems.contains(downOffset)) {
                     this.hudOrderItems.add(downOffset);
                  }
               } catch (Exception e2) {
               }
            }
         }

         boolean has3 = !asJsonObject.has("hudScaleRev");
         if (asJsonObject.has("hudScales") && asJsonObject.get("hudScales").isJsonObject()) {
            for (Entry entry : asJsonObject.getAsJsonObject("hudScales").entrySet()) {
               try {
                  float max = Math.round(((JsonElement)entry.getValue()).getAsFloat() * 100.0F) / 100.0F;
                  if (!(max < 0.6F) && !(max > 1.8F) && (!has3 || max != getPotionsPerfgraph((String)entry.getKey()))) {
                     ShowHUD.HudScale hudScale = new ShowHUD.HudScale();
                     hudScale.hudScaleFactor = max;
                     hudScale.hotbarOffsetsAmount = max;
                     this.hotbarHudMap.put((String)entry.getKey(), hudScale);
                  }
               } catch (Exception e3) {
               }
            }
         }

         this.updateHotbarHud();
         if (asJsonObject.has("hudOffsets") && asJsonObject.get("hudOffsets").isJsonObject()) {
            for (Entry entryItem : asJsonObject.getAsJsonObject("hudOffsets").entrySet()) {
               try {
                  JsonArray asJsonArray = ((JsonElement)entryItem.getValue()).getAsJsonArray();
                  this.hudOffsetsMap.put((String)entryItem.getKey(), new float[]{asJsonArray.get(0).getAsFloat(), asJsonArray.get(1).getAsFloat()});
               } catch (Exception e4) {
               }
            }
         }

         if (asJsonObject.has("hudPins") && asJsonObject.get("hudPins").isJsonObject()) {
            for (Entry entry3 : asJsonObject.getAsJsonObject("hudPins").entrySet()) {
               try {
                  JsonArray asJsonArray2 = ((JsonElement)entry3.getValue()).getAsJsonArray();
                  this.hudModulesMap
                     .put(
                        (String)entry3.getKey(),
                        new float[]{asJsonArray2.get(0).getAsFloat(), asJsonArray2.get(1).getAsFloat(), asJsonArray2.get(2).getAsFloat(), asJsonArray2.get(3).getAsFloat()}
                     );
               } catch (Exception e5) {
               }
            }
         }
      }
   }

   private static float getAmount3(float hudValue) {
      return Math.abs(hudValue - 1.0F) < 0.065F ? 1.0F : Math.round(hudValue / 0.05F) * 0.05F;
   }

   private float getBackpackTopbar(DrawContext context, float backpackTopbarValue, float backpackTopbarValue2, long nanoTime) {
      boolean booleanValue = this.booleanBase.isEnabled();
      float backpackTopbarFactor = this.getMax();
      float backpackTopbar = (backpackTopbarFactor * this.getHudScale() + 6.0F) * this.resourcesVisibilitySmooth.getMax(booleanValue && !this.canHudOffsets("resources") ? 1.0F : 0.0F, this.frameDeltaSeconds, 0.08F, 0.002F);
      if (!this.backpackTopbarPop.canTimeAmount(booleanValue, ssaPerfgraphCount)) {
         return backpackTopbar;
      } else {
         if (this.backpackTopbarItemStacks == null) {
            this.backpackTopbarItemStacks = new ItemStack[]{
               new ItemStack(Items.GOLDEN_APPLE),
               new ItemStack(Items.TOTEM_OF_UNDYING),
               new ItemStack(Items.END_CRYSTAL),
               new ItemStack(Items.EXPERIENCE_BOTTLE)
            };
         }

         boolean backpackTopbarFlag = this.lastNanoTimeBackpackTopbar == 0L || nanoTime - this.lastNanoTimeBackpackTopbar > 1000000000L;
         if (backpackTopbarFlag || nanoTime - this.lastNanoTimeBackpackTopbar >= 250000000L) {
            this.lastNanoTimeBackpackTopbar = nanoTime;
            this.handleTextsInventory(nanoTime, backpackTopbarFlag);
         }

         float backpackTopbarValue3 = 6.0F;
         float backpackTopbarBound = 0.0F;

         for (int i = 0; i < 4; i++) {
            backpackTopbarBound = Math.max(backpackTopbarBound, this.backpackTopbarTable[i]);
         }

         float max4 = Math.max(this.backpackTopbarMinWidth, (float)Math.ceil(this.topbarBackpackAmount + 3.0F + backpackTopbarBound + 9.0F));
         float hudFactor = this.backpackTopbarSmooth
            .getAmount((float)Math.ceil(Math.max(NativeHud.getAmount2(this.potionsResourcesAmount, 0.0F), 2.0F * backpackTopbarValue3 + 4.0F * max4)), this.frameDeltaSeconds, 0.08F);
         float backpackTopbarFactor2 = (hudFactor - 2.0F * backpackTopbarValue3) / 4.0F;
         float backpackTopbarValue6 = backpackTopbarValue + this.coordsOffsetX;
         float hudScaleValue = backpackTopbarValue2 + this.coordsOffsetY;
         this.handleTextHud(backpackTopbarValue6, hudScaleValue, hudFactor, backpackTopbarFactor);
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(this.backpackTopbarPop.getPopProgress());
         boolean translateMatrices = NativeHud.canTranslateMatrices(context, backpackTopbarValue6 + hudFactor / 2.0F, hudScaleValue + backpackTopbarFactor / 2.0F, this.backpackTopbarPop.getAmount());

         try {
            NativeHud.handle(NativeHud.backpackResourcesValue, backpackTopbarValue6, hudScaleValue, hudFactor, backpackTopbarFactor, 1.0F);
            float colorAmount = NativeHud.getColorAmount(
               context, backpackTopbarValue6, hudScaleValue, hudFactor, IcoGenUtils.getIdentifier("backpack"), NativeHud.WHITE, "Resources", null, null, 1.0F
            );
            float backpackTopbarValue8 = colorAmount + this.backpackTopbarAmount / 2.0F;
            float backpackTopbarValue9 = this.topbarBackpackAmount + 2.0F;
            float nativeHudValue = NativeHud.getAmount4(backpackTopbarValue8 - this.topbarBackpackAmount / 2.0F);
            float backpackTopbarValue11 = NativeHud.getAmount4(backpackTopbarValue8 - backpackTopbarValue9 / 2.0F);

            for (int j = 1; j < 4; j++) {
               NativeHud.renderForDrawContextFloat(context, backpackTopbarValue6 + backpackTopbarValue3 + j * backpackTopbarFactor2, backpackTopbarValue11, backpackTopbarValue9, 536870911);
            }

            for (int k = 0; k < 4; k++) {
               float backpackTopbarValue12 = backpackTopbarValue6 + backpackTopbarValue3 + (k + 0.5F) * backpackTopbarFactor2;
               float backpackTopbarLimit = this.smooths[k].getMax(this.inventoryCounts[k] > 0 ? 1.0F : 0.0F, this.frameDeltaSeconds, 0.06F, 0.002F);
               float backpackTopbarValue13 = this.backpackTopbarTable[k];
               float matrixMatricesValue = NativeHud.getAmount4(backpackTopbarValue12 - (this.topbarBackpackAmount + 3.0F + backpackTopbarValue13) / 2.0F);
               boolean canTranslateMatrices = NativeHud.canTranslateMatrices(
                  context, matrixMatricesValue + this.topbarBackpackAmount / 2.0F, nativeHudValue + this.topbarBackpackAmount / 2.0F, this.getTimes(k, nanoTime)
               );

               try {
                  NativeHud.renderMatrixMatrices(context, this.backpackTopbarItemStacks[k], matrixMatricesValue, nativeHudValue, this.topbarBackpackAmount);
               } finally {
                  NativeHud.renderPopMatrices(context, canTranslateMatrices);
               }

               Color color = backpackTopbarLimit >= 0.998F
                  ? NativeHud.OFF_WHITE
                  : (backpackTopbarLimit <= 0.002F ? COLOR : new Color(CardMatrixUtils.getValue(COLOR.getRGB(), NativeHud.OFF_WHITE.getRGB(), backpackTopbarLimit), true));
               NativeHud.handlePushScale(
                  this.inventoryTexts[k], matrixMatricesValue + this.topbarBackpackAmount + 3.0F, NativeHud.getAmount2(nativeHudValue, this.topbarBackpackAmount, 0.82F), 0.82F, color
               );
            }
         } finally {
            NativeHud.renderPopMatrices(context, translateMatrices);
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
         }

         return backpackTopbar;
      }
   }

   private boolean canAmountsHotbar(float hotbarValue, float hashHuntBound, float hotbarValue3, float hotbarValue4) {
      for (int i = 0; i < this.hotbarItemCount; i++) {
         if (this.canHotbarTexts(i) && canFlag(hotbarValue, hashHuntBound, hotbarValue3, hotbarValue4, this.hotbarTable[i], this.hotbarCurrentYs[i], this.hotbarWidths[i], this.hotbarHeights[i])) {
            return false;
         }
      }

      return true;
   }

   @Override
   public JsonElement saveExtraData() {
      JsonObject jsonObject = new JsonObject();

      for (Entry entry : this.hotbarHudMap.entrySet()) {
         if (((ShowHUD.HudScale)entry.getValue()).hudScaleFactor != getCoordsPerf((String)entry.getKey())) {
            jsonObject.addProperty((String)entry.getKey(), ((ShowHUD.HudScale)entry.getValue()).hudScaleFactor);
         }
      }

      boolean iIiFlag = !this.isEquals();
      if (this.hudOffsetsMap.isEmpty() && this.hudModulesMap.isEmpty() && jsonObject.size() == 0 && !this.hudScales && !iIiFlag) {
         return null;
      } else {
         JsonObject jsonObject2 = new JsonObject();
         JsonObject jsonObject3 = new JsonObject();

         for (Entry entryItem : this.hudOffsetsMap.entrySet()) {
            float[] iIiValues = (float[])entryItem.getValue();
            if (!(Math.abs(iIiValues[0]) < 0.5F) || !(Math.abs(iIiValues[1]) < 0.5F)) {
               JsonArray jsonArray = new JsonArray();
               jsonArray.add(iIiValues[0]);
               jsonArray.add(iIiValues[1]);
               jsonObject3.add((String)entryItem.getKey(), jsonArray);
            }
         }

         JsonObject jsonObject4 = new JsonObject();

         for (Entry entry3 : this.hudModulesMap.entrySet()) {
            float[] iIiValues2 = (float[])entry3.getValue();
            JsonArray jsonArray2 = new JsonArray();
            jsonArray2.add(iIiValues2[0]);
            jsonArray2.add(iIiValues2[1]);
            jsonArray2.add(iIiValues2[2]);
            jsonArray2.add(iIiValues2[3]);
            jsonObject4.add((String)entry3.getKey(), jsonArray2);
         }

         if (jsonObject3.size() == 0 && jsonObject4.size() == 0 && jsonObject.size() == 0 && !this.hudScales && !iIiFlag) {
            return null;
         } else {
            if (iIiFlag) {
               JsonArray jsonArray3 = new JsonArray();

               for (String iIiText : this.clone) {
                  jsonArray3.add(iIiText);
               }

               jsonObject2.add("hudFlow", jsonArray3);
            }

            if (jsonObject3.size() > 0) {
               jsonObject2.add("hudOffsets", jsonObject3);
            }

            if (jsonObject4.size() > 0) {
               jsonObject2.add("hudPins", jsonObject4);
            }

            JsonArray jsonArray4 = new JsonArray();

            for (String obsidianIdleValue : this.hudOrderItems) {
               if (this.hudModulesMap.containsKey(obsidianIdleValue)) {
                  jsonArray4.add(obsidianIdleValue);
               }
            }

            if (jsonArray4.size() > 0) {
               jsonObject2.add("hudOrder", jsonArray4);
            }

            if (jsonObject.size() > 0) {
               jsonObject2.add("hudScales", jsonObject);
            }

            if (this.hudScales) {
               jsonObject2.addProperty("hudScaleHint", true);
            }

            jsonObject2.addProperty("hudScaleRev", 2);
            return jsonObject2;
         }
      }
   }

   private float getHotbarAmountsForFloatFlag(float hotbarBound, float amount, float hotbarValue, float hotbarValue2, boolean hotbarFlag) {
      this.hotbarAmount = amount;
      this.hotbarMinOffset = Float.MAX_VALUE;
      float scaledWidth = this.scaledWidth;
      float scaledHeight = this.scaledHeight;
      float hotbarValue3 = scaledHeight / 2.0F;
      float hotbarValue4 = 6.0F;
      this.handleAmount(hotbarValue3 - hotbarValue2 / 2.0F, 0.0F, hotbarValue3, scaledWidth, hotbarValue3);
      this.handleAmount(6.0F, hotbarBound, 6.0F, hotbarBound + hotbarValue, 6.0F);
      this.handleAmount(scaledHeight - 6.0F - hotbarValue2, hotbarBound, scaledHeight - 6.0F, hotbarBound + hotbarValue, scaledHeight - 6.0F);
      if (hotbarFlag) {
         this.handleAmount(this.baseY);
      }

      for (int i = 0; i < this.hotbarItemCount; i++) {
         if (!this.hotbarTexts[i].equals(this.dragReleaseText) && !this.canFlagsResources(this.hotbarTexts[i])) {
            float wrapDegreesBound = this.hotbarTable[i];
            float hotbarValue5 = this.hotbarCurrentYs[i];
            float hotbarValue6 = this.hotbarWidths[i];
            float hotbarValue7 = this.hotbarHeights[i];
            float minHome = Math.min(wrapDegreesBound, hotbarBound);
            float max8 = Math.max(wrapDegreesBound + hotbarValue6, hotbarBound + hotbarValue);
            if (hotbarBound < wrapDegreesBound + hotbarValue6 + hotbarValue4 && wrapDegreesBound < hotbarBound + hotbarValue + hotbarValue4) {
               float maxHome = Math.max(wrapDegreesBound, hotbarBound);
               float min9 = Math.min(wrapDegreesBound + hotbarValue6, hotbarBound + hotbarValue);
               this.handleAmount(hotbarValue5 + hotbarValue7 + hotbarValue4, true, maxHome, hotbarValue5 + hotbarValue7 + hotbarValue4 / 2.0F, min9, hotbarValue5 + hotbarValue7 + hotbarValue4 / 2.0F);
               this.handleAmount(hotbarValue5 - hotbarValue4 - hotbarValue2, false, maxHome, hotbarValue5 - hotbarValue4 / 2.0F, min9, hotbarValue5 - hotbarValue4 / 2.0F);
            }

            if (!(hotbarBound < wrapDegreesBound + hotbarValue6) || !(wrapDegreesBound < hotbarBound + hotbarValue) || !(amount < hotbarValue5 + hotbarValue7) || !(hotbarValue5 < amount + hotbarValue2)) {
               this.handleAmount(hotbarValue5, minHome, hotbarValue5, max8, hotbarValue5);
               this.handleAmount(hotbarValue5 + hotbarValue7 - hotbarValue2, minHome, hotbarValue5 + hotbarValue7, max8, hotbarValue5 + hotbarValue7);
               this.handleAmount(hotbarValue5 + (hotbarValue7 - hotbarValue2) / 2.0F, minHome, hotbarValue5 + hotbarValue7 / 2.0F, max8, hotbarValue5 + hotbarValue7 / 2.0F);
               this.handleAmount(
                  scaledHeight - hotbarValue5 - hotbarValue7,
                  0.0F,
                  hotbarValue3,
                  scaledWidth,
                  hotbarValue3,
                  hotbarBound,
                  scaledHeight - hotbarValue5 - hotbarValue7,
                  hotbarBound + hotbarValue,
                  scaledHeight - hotbarValue5 - hotbarValue7,
                  wrapDegreesBound,
                  hotbarValue5 + hotbarValue7,
                  wrapDegreesBound + hotbarValue6,
                  hotbarValue5 + hotbarValue7
               );
               this.handleAmount(scaledHeight - hotbarValue5 - hotbarValue2, 0.0F, hotbarValue3, scaledWidth, hotbarValue3, hotbarBound, scaledHeight - hotbarValue5, hotbarBound + hotbarValue, scaledHeight - hotbarValue5, wrapDegreesBound, hotbarValue5, wrapDegreesBound + hotbarValue6, hotbarValue5);
            }
         }
      }

      return this.getLayoutDefaults();
   }

   private double[] getScaledWidth(float scaledWidthDivisor) {
      Window window = mc.getWindow();
      double scaledWidthValue = mc.mouse.getX() * window.getScaledWidth() / Math.max(1, window.getWidth());
      double scaledWidthValue2 = mc.mouse.getY() * window.getScaledHeight() / Math.max(1, window.getHeight());
      return new double[]{scaledWidthValue / scaledWidthDivisor, scaledWidthValue2 / scaledWidthDivisor};
   }

   private void handleCoordsRadar(float coordsRadarLimit, float coordsRadarBound) {
      this.radarCoordsAmount2CoordsRada += coordsRadarLimit;
      if (coordsRadarLimit > 0.5F) {
         this.maxCoordsRadarBound = Math.max(this.maxCoordsRadarBound, coordsRadarBound);
      }
   }

   private static QuintScissor getQuart() {
      return new QuintScissor(QuintBounceType.QUART_OUT, 260, 0.0);
   }

   private void renderCoordsRadar(DrawContext context, int index, long coordsRadarValue, boolean coordsRadarFlag, float coordsRadarBound) {
      String coordsRadarText = this.clone[index];
      this.inventoryResourcesValues[index] = -1;
      switch (coordsRadarText) {
         case "keybinds":
            float coordsRadarValue2 = this.keybindsCard.isEnabled() && this.nList.keybindsKeystrokesFlag
               ? (30.0F + this.nList.keybindsKeyboardAmount + 5.0F) * this.getHotbarHud(coordsRadarText)
               : 0.0F;
            this.handleCoordsRadar(this.keybindsFlow, this.keybindsKeyboardPop, !this.canHudOffsets(coordsRadarText), coordsRadarValue2);
            this.inventoryResourcesValues[index] = this.coordsRadarCount;
            this.handleCoordsRadar(coordsRadarText, coordsRadarValue2, 0.0F, !this.canHudOffsets(coordsRadarText));
            this.handleAmountCoords(coordsRadarText);
            this.handleCoordsRadar(this.getKeybindsKeyboard(context, this.radarCoordsAmount, this.radarCoordsAmount2CoordsRada), this.keybindsKeyboardSmooth.getMax() * this.getHudScale());
            this.updateTextAmounts();
            break;
         case "resources":
            float coordsRadarValue3 = this.booleanBase.isEnabled() ? this.getMax() * this.getHotbarHud(coordsRadarText) : 0.0F;
            this.handleCoordsRadar(this.coordsRadarNFlow, this.backpackTopbarPop, !this.canHudOffsets(coordsRadarText), coordsRadarValue3);
            this.inventoryResourcesValues[index] = this.coordsRadarCount;
            this.handleCoordsRadar(coordsRadarText, coordsRadarValue3, 0.0F, !this.canHudOffsets(coordsRadarText));
            this.handleAmountCoords(coordsRadarText);
            this.handleCoordsRadar(this.getBackpackTopbar(context, this.radarCoordsAmount, this.radarCoordsAmount2CoordsRada, coordsRadarValue), this.backpackTopbarSmooth.getMax() * this.getHudScale());
            this.updateTextAmounts();
            break;
         case "inventory":
            float coordsRadarValue4 = this.inventoryCard.isEnabled() ? this.getAmount2() * this.getHotbarHud(coordsRadarText) : 0.0F;
            this.handleCoordsRadar(this.nFlow, this.barInventoryPop, !this.canHudOffsets(coordsRadarText), coordsRadarValue4);
            this.inventoryResourcesValues[index] = this.coordsRadarCount;
            this.handleCoordsRadar(coordsRadarText, coordsRadarValue4, 0.0F, !this.canHudOffsets(coordsRadarText));
            this.handleAmountCoords(coordsRadarText);
            this.handleCoordsRadar(this.getBarInventory(context, this.radarCoordsAmount, this.radarCoordsAmount2CoordsRada), this.getAmount3() * this.getHudScale());
            this.updateTextAmounts();
            break;
         case "keystrokes":
            boolean canHudOffsets2 = !this.canHudOffsets(coordsRadarText);
            boolean coordsRadarFlag3 = coordsRadarFlag && "Auto".equals(this.enumHelper.getCurrentOption());
            boolean coordsRadarFlag4 = this.keystrokesScaledMatrices.isEnabled() && (canHudOffsets2 || coordsRadarFlag3);
            boolean coordsRadarFlag5 = coordsRadarFlag4 && coordsRadarFlag3;
            this.handleCoordsRadar(this.hudNFlow, canHudOffsets2 ? this.staffPop : this.lerpUuidPop, coordsRadarFlag4, this.hudAmount);
            this.inventoryResourcesValues[index] = this.coordsRadarCount;
            this.handleCoordsRadar(coordsRadarText, coordsRadarFlag4 ? this.hudAmount : 0.0F, coordsRadarFlag5 ? 6.0F + coordsRadarBound : 0.0F, coordsRadarFlag4);
            float radarCoordsAmount = this.radarCoordsAmount;
            float nativeHudValue = this.radarCoordsAmount2CoordsRada;
            this.handleAmountCoords(coordsRadarText);
            float hudScale = this.getHudScale();
            float pilotStatusBound = this.getTextsStaff(context, this.radarCoordsAmount, this.radarCoordsAmount2CoordsRada) * hudScale;
            this.updateTextAmounts();
            if (coordsRadarFlag4 && (!canHudOffsets2 || this.staffPop.getPopProgress() > 0.01F)) {
               this.handleSsaPotions(radarCoordsAmount, nativeHudValue, 74.0F * hudScale, pilotStatusBound);
            }

            this.hudAmount = coordsRadarFlag4 ? (coordsRadarFlag5 ? Math.max(pilotStatusBound, coordsRadarBound) : pilotStatusBound) : 0.0F;
            this.handleCoordsRadar(this.coordsRadarSmooth.getAmountForFloat(coordsRadarFlag4 ? this.hudAmount + 6.0F : 0.0F, this.frameDeltaSeconds, 0.08F), 74.0F * hudScale + (coordsRadarFlag5 ? 6.0F + coordsRadarBound : 0.0F));
            break;
         case "perfgraph":
            float coordsRadarLimit = this.performanceCard.isEnabled() ? 68.0F * this.getHotbarHud(coordsRadarText) : 0.0F;
            this.handleCoordsRadar(this.performanceGraphFlow, this.performancePerfgraphPop, !this.canHudOffsets(coordsRadarText), coordsRadarLimit);
            this.inventoryResourcesValues[index] = this.coordsRadarCount;
            this.handleCoordsRadar(coordsRadarText, coordsRadarLimit, 0.0F, !this.canHudOffsets(coordsRadarText));
            this.handleAmountCoords(coordsRadarText);
            this.handleCoordsRadar(this.getPerformancePerfgraph(context, this.radarCoordsAmount, this.radarCoordsAmount2CoordsRada, coordsRadarValue), 156.0F * this.getHudScale());
            this.updateTextAmounts();
            break;
         case "potions":
            boolean canHudOffsets6 = !this.canHudOffsets(coordsRadarText);
            float hotbarHud = this.getHotbarHud(coordsRadarText);
            float coordsRadarValue6 = this.effectAmplifierValue > 0 ? (30.0F + this.effectAmplifierValue * 15.0F + 5.0F) * hotbarHud : 0.0F;
            this.handleCoordsRadar(this.flaskPotionsFlow, this.flaskPotionsPop, canHudOffsets6, coordsRadarValue6);
            this.inventoryResourcesValues[index] = this.coordsRadarCount;
            this.handleCoordsRadar(coordsRadarText, coordsRadarValue6, 0.0F, canHudOffsets6);
            this.handleAmountCoords(coordsRadarText);
            this.handleCoordsRadar(this.getFlaskPotions(context, this.radarCoordsAmount, this.radarCoordsAmount2CoordsRada, coordsRadarValue), this.flaskPotionsHeightSmooth.getMax());
            this.handleCoordsRadar(this.timerCooldownsFlow, this.timerCooldownsPop, canHudOffsets6, this.timerCooldownsValue > 0 ? (30.0F + this.timerCooldownsValue * 15.0F + 5.0F) * hotbarHud : 0.0F);
            this.renderTimerCooldowns(context, this.radarCoordsAmount, this.radarCoordsAmount2CoordsRada, coordsRadarValue);
            this.updateTextAmounts();
      }
   }

   private String formatMmbLmb(int mmbLmbLimit) {
      if (mmbLmbLimit >= 0 && PageRaltUtils.canFlag(mmbLmbLimit)) {
         int mmbLmbValue = PageRaltUtils.getValue(mmbLmbLimit);
         return mmbLmbValue == 0 ? "LMB" : (mmbLmbValue == 1 ? "RMB" : (mmbLmbValue == 2 ? "MMB" : "MB" + (mmbLmbValue + 1)));
      } else {
         return this.getUpperCase(mmbLmbLimit);
      }
   }

   private void renderDragReset(DrawContext context, double dragResetValue, double dragResetValue2) {
      int releaseRight = NativeHud.getReleaseRight();
      float dragResetValue3 = MatrixPeekUtilsRender.getAmount();
      float dragResetBound = this.getAmount();

      for (int i = 0; i < this.hudResetItems.size(); i++) {
         ShowHUD.HudBox hudresetitem = this.hudResetItems.get(i);
         boolean dragResetFlag = hudresetitem.dragResetText.equals(this.dragReleaseText) || this.dragReleaseText == null && hudresetitem.dragResetText.equals(this.dragRightText);
         float matrixPeekValue = NativeHud.getAmount4(hudresetitem.dragResetAmount) - 2.0F;
         float configsLikeValue = NativeHud.getAmount4(hudresetitem.hudAmount) - 2.0F;
         float wrapDegreesBound = NativeHud.getAmount4(hudresetitem.hudBoxAmount) + 4.0F;
         float hashHuntBound = NativeHud.getAmount4(hudresetitem.boxHeight) + 4.0F;
         float corzLicenseLimit = Math.min(dragResetBound, Math.min(wrapDegreesBound, hashHuntBound) / 2.0F);
         if (dragResetFlag) {
            MatrixPeekUtilsRender.renderMatrixPeek(context, matrixPeekValue, configsLikeValue, wrapDegreesBound, hashHuntBound, corzLicenseLimit, getValue(releaseRight, 20));
         }

         MatrixPeekUtilsRender.renderMatrixPeek(context, matrixPeekValue, configsLikeValue, wrapDegreesBound, hashHuntBound, corzLicenseLimit, dragResetValue3, dragResetFlag ? getValue(releaseRight, 235) : getValue(-1, 64));
      }

      this.updateDefaultsLayout();
      this.renderAmountScroll(context);
      this.renderScrollResize(context);
      VertexTexturePeek suisse = VertexTexturePeek.getSuisse();
      if (suisse.isCorzclientFlag()) {
         String dragResetText = this.releaseResetText != null
            ? "Release to set size"
            : (
               this.dragReleaseText != null
                  ? "Drop to place"
                  : (
                     this.dragRightText != null
                        ? "Drag to move  \u00b7  scroll to resize  \u00b7  right-click to reset"
                        : "Drag any widget  \u00b7  right-click to reset  \u00b7  close chat to finish"
                  )
            );
         float dragResetValue7 = 11.0F;
         float dragResetValue8 = 9.0F;
         float dragResetValue9 = 17.0F;
         float corzclientAmounts = suisse.getCorzclientAmounts(dragResetText, 11.0F);
         float dragResetValue10 = 9.0F + corzclientAmounts + 9.0F;
         float dragResetValue11 = (this.scaledWidth - dragResetValue10) / 2.0F;
         float glfwLimit = 3.0F;
         render(context, dragResetValue11, glfwLimit, dragResetValue10, 17.0F, 7.0F, getValue(-15724524, 232));
         MatrixPeekUtilsRender.renderMatrixPeek(context, dragResetValue11, glfwLimit, dragResetValue10, 17.0F, 7.0F, dragResetValue3, getValue(-1, 56));
         suisse.handlePeekEnd(dragResetText, dragResetValue11 + 9.0F, glfwLimit + (17.0F - suisse.getAmountForFloat(11.0F)) / 2.0F, -1, 11.0F);
      }
   }

   public static float getAmounts(float hudLimit, float hudValue, float bottomFactor, float maxRadarSize, String... texts) {
      ShowHUD showHUD = getShowHUD();
      if (showHUD != null && showHUD.isEnabled()) {
         float hudValue2 = hudValue;
         float[] hudHotbarValues = showHUD.defaultsTable;

         for (String hudText : texts) {
            if (showHUD.canHudHotbar(hudText, hudHotbarValues)) {
               float hudLimit2 = hudHotbarValues[0];
               float hudLimit3 = hudHotbarValues[1];
               float hudValue4 = hudHotbarValues[2];
               float hudValue5 = hudHotbarValues[3];
               if (!(hudLimit2 + hudValue4 <= hudLimit) && !(hudLimit2 >= hudLimit + bottomFactor) && !(hudLimit3 >= hudValue2 + maxRadarSize) && !(hudLimit3 + hudValue5 <= hudValue2)) {
                  hudValue2 = hudLimit3 + hudValue5 + 6.0F;
               }
            }
         }

         return hudValue2;
      } else {
         return hudValue;
      }
   }

   private float getAmounts(String hudText, Module Module, float hudLimit, float hudLimit2) {
      if (Module != null && Module.isEnabled()) {
         float[] hudHotbarValues = this.defaultsTable;
         if (!this.canHudHotbar(hudText, hudHotbarValues)) {
            return hudLimit2;
         } else {
            float hudLimit3 = hudHotbarValues[0];
            float hudLimit4 = hudHotbarValues[1];
            float hudValue = hudHotbarValues[2];
            float hudValue2 = hudHotbarValues[3];
            return !(hudLimit3 > hudLimit + 120.0F) && !(hudLimit3 + hudValue < hudLimit) && !(hudLimit4 + hudValue2 < hudLimit2) && !(hudLimit4 > hudLimit2 + 64.0F) ? hudLimit4 + hudValue2 + 6.0F : hudLimit2;
         }
      } else {
         return hudLimit2;
      }
   }

   private static void render(DrawContext context, VertexTexturePeek vertexTexturePeek, ShowHUD.H2Seg h2Seg, float hudValue, float hudScaleValue, float hudValue3, float experienceProgressLimit) {
      renderIconText(context, vertexTexturePeek, h2Seg, hudValue, hudScaleValue, hudValue3, experienceProgressLimit, 1.0F);
   }

   private boolean canAmountHud(String hudText) {
      float min = Float.MAX_VALUE;
      float wrapDegreesBound = Float.MAX_VALUE;
      float hudBound = -Float.MAX_VALUE;
      float pilotStatusBound = -Float.MAX_VALUE;

      for (int i = 0; i < this.hudResetItems.size(); i++) {
         ShowHUD.HudBox hudresetitem = this.hudResetItems.get(i);
         if (hudresetitem.dragResetText.equals(hudText)) {
            min = Math.min(min, hudresetitem.dragResetAmount);
            wrapDegreesBound = Math.min(wrapDegreesBound, hudresetitem.hudAmount);
            hudBound = Math.max(hudBound, hudresetitem.dragResetAmount + hudresetitem.hudBoxAmount);
            pilotStatusBound = Math.max(pilotStatusBound, hudresetitem.hudAmount + hudresetitem.boxHeight);
         }
      }

      if (hudBound < min) {
         return false;
      } else {
         this.anchorLeft = min;
         this.anchorTop = wrapDegreesBound;
         this.anchorWidth = hudBound - min;
         this.anchorHeight = pilotStatusBound - wrapDegreesBound;
         this.popupWidth = NativeHud.getAmount4(17.0F + this.percentLabelWidth + 17.0F + 16.0F * this.scrollSmooth.getMax());
         this.popupTargetX = NativeHud.getAmount4(Math.max(3.0F, Math.min(this.scaledWidth - 3.0F - this.popupWidth, min + (this.anchorWidth - this.popupWidth) / 2.0F)));
         float hudValue = pilotStatusBound + 6.0F;
         float hudLimit = wrapDegreesBound - 6.0F - 17.0F;
         this.popupTargetY = NativeHud.getAmount4(hudValue + 17.0F <= this.scaledHeight - 22.0F ? hudValue : (hudLimit >= 3.0F ? hudLimit : pilotStatusBound - 17.0F - 4.0F));
         float[] moduleshudmap = this.modulesHudMap.get(hudText);
         float[] hudoffsetsmap = this.hudOffsetsMap.get(hudText);
         if (moduleshudmap != null) {
            this.handleBottomLeft(
               hudText, this.hotbarHudMap.get(hudText), moduleshudmap[0] + (hudoffsetsmap != null ? hudoffsetsmap[0] : 0.0F), moduleshudmap[1] + (hudoffsetsmap != null ? hudoffsetsmap[1] : 0.0F), moduleshudmap[2], moduleshudmap[3]
            );
         } else {
            this.bottomAmount = 0.0F;
            this.bottomLeftAmount = 0.0F;
         }

         float hudValue2 = 2.0F - Math.min(this.getAmount(), Math.min(this.anchorWidth, this.anchorHeight) / 2.0F + 2.0F) * 0.2929F;
         this.scrollAmount = NativeHud.getAmount4(this.bottomAmount >= 0.75F ? min - hudValue2 : hudBound + hudValue2);
         this.popupPointerY = NativeHud.getAmount4(this.bottomLeftAmount >= 0.75F ? wrapDegreesBound - hudValue2 : pilotStatusBound + hudValue2);
         return true;
      }
   }

   private boolean canHudOffsets(String hudOffsetsText) {
      if (this.defaultsLayoutFlag && hudOffsetsText.equals(this.dragReleaseText)) {
         return false;
      } else if (this.hudModulesMap.containsKey(hudOffsetsText)) {
         return true;
      } else {
         float[] hudoffsetsmap = this.hudOffsetsMap.get(hudOffsetsText);
         return hudoffsetsmap != null && (Math.abs(hudoffsetsmap[0]) >= 0.5F || Math.abs(hudoffsetsmap[1]) >= 0.5F);
      }
   }

   private boolean canAmountsCoords(String coordsText, float coordsValue, float coordsLimit, float coordsValue2, float coordsValue3) {
      int coordsLimit2 = -1;

      for (int i = 0; i < this.clone.length; i++) {
         if (this.clone[i].equals(coordsText)) {
            coordsLimit2 = i;
         }
      }

      if (coordsLimit2 < 0) {
         return false;
      } else {
         float coordsValue4 = coordsLimit + coordsValue3 / 2.0F;

         for (int j = 0; j < this.coordsRadarValue; j++) {
            if (!(Math.abs(coordsValue - this.coordsAmounts[j]) > 24.0F)) {
               float coordsBound = Float.MAX_VALUE;
               float pilotStatusBound = -Float.MAX_VALUE;
               int coordsLimit3 = -1;
               int coordsValue5 = -1;

               for (int k = 0; k < this.clone.length; k++) {
                  if (k != coordsLimit2 && this.inventoryResourcesValues[k] == j) {
                     String coordsText2 = this.clone[k];
                     int index = this.canHudOffsets(coordsText2) ? -1 : this.getHotbarTotems(coordsText2);
                     if (index >= 0) {
                        coordsBound = Math.min(coordsBound, this.hotbarTargetYs[index]);
                        pilotStatusBound = Math.max(pilotStatusBound, this.hotbarTargetYs[index] + this.hotbarHeights[index]);
                        coordsValue5 = k;
                        if (coordsLimit3 < 0 && this.hotbarTargetYs[index] + this.hotbarHeights[index] / 2.0F > coordsValue4) {
                           coordsLimit3 = k;
                        }
                     }
                  }
               }

               if (coordsValue5 < 0) {
                  if (j != 0) {
                     continue;
                  }

                  pilotStatusBound = this.coordsRadarAmount;
                  coordsBound = this.coordsRadarAmount;
               }

               if (!(coordsLimit + coordsValue3 < coordsBound - 16.0F) && !(coordsLimit > pilotStatusBound + 30.0F)) {
                  int subSequenceLimit = coordsLimit3 >= 0 ? coordsLimit3 : coordsValue5 + 1;
                  int hotbarTotems = this.getHotbarTotems(coordsText);
                  if (this.defaultsLayoutFlag && hotbarTotems >= 0 && subSequenceLimit != coordsLimit2 && subSequenceLimit != coordsLimit2 + 1) {
                     float coordsValue7 = this.hotbarHeights[hotbarTotems] + 6.0F;
                     int coordsValue8 = subSequenceLimit < coordsLimit2 ? subSequenceLimit : coordsLimit2 + 1;
                     int coordsLimit4 = subSequenceLimit < coordsLimit2 ? coordsLimit2 - 1 : subSequenceLimit - 1;

                     for (int m = coordsValue8; m <= coordsLimit4; m++) {
                        String hudOffsetsText = this.clone[m];
                        if (this.inventoryResourcesValues[m] == j && !this.canHudOffsets(hudOffsetsText) && this.getHotbarTotems(hudOffsetsText) >= 0) {
                           this.handleDefaultsHud(hudOffsetsText, 0.0F, subSequenceLimit < coordsLimit2 ? -coordsValue7 : coordsValue7);
                        }
                     }
                  }

                  this.handle(coordsLimit2, subSequenceLimit);
                  return true;
               }
            }
         }

         return false;
      }
   }

   private static float getAmount(VertexTexturePeek vertexTexturePeek, String hudText, float hudValue) {
      float vertexTexture = getVertexTexture(vertexTexturePeek, hudValue);
      float hudTotal = 0.0F;

      for (int i = 0; i < hudText.length(); i++) {
         char charAt = hudText.charAt(i);
         hudTotal += charAt >= '0' && charAt <= '9' ? vertexTexture : vertexTexturePeek.getAmountFlag(charAt, hudValue);
      }

      return hudTotal;
   }

   private static float getAmount(float hudLimit, float hudLimit2, float hudValue) {
      return hudLimit < hudLimit2 ? hudLimit2 : (hudLimit > hudValue ? hudValue : hudLimit);
   }

   private static boolean canPerfgraphSsa(long perfgraphSsaLimit) {
      return perfgraphSsaLimit >= 0L && perfgraphSsaLimit >= ssaPerfgraphCount - 1L;
   }

   private static void render(DrawContext context, VertexTexturePeek vertexTexturePeek, int hudValue, float glfwLimit, float hudScaleValue, ShowHUD.H2Seg[] h2Segs, float[] hudValues, int hudLimit, float configsLikeValue, Pop pop2) {
      float panelOverlay = CorzRoundedUtils.getPanelOverlay(pop2.getPopProgress());
      boolean translateMatrices = NativeHud.canTranslateMatrices(context, glfwLimit + configsLikeValue / 2.0F, hudScaleValue + 10.0F, pop2.getAmount());

      try {
         NativeHud.handle(hudValue, Math.round(glfwLimit), Math.round(hudScaleValue), Math.max(0, Math.round(configsLikeValue)), Math.round(20.0F), Math.round(6.0F), 1.0F);
         float glfwLimit2 = glfwLimit;

         for (int i = 0; i < hudLimit; i++) {
            if (i > 0) {
               render(context, glfwLimit2, hudScaleValue, 20.0F, 1.0F);
            }

            render(context, vertexTexturePeek, h2Segs[i], glfwLimit2, hudScaleValue, 20.0F, 1.0F);
            glfwLimit2 += hudValues[i];
         }
      } finally {
         NativeHud.renderPopMatrices(context, translateMatrices);
         CorzRoundedUtils.handlePanelOverlay(panelOverlay);
      }
   }

   private void handleCoordsRadar(String coordsRadarText, float coordsRadarLimit, float coordsRadarValue, boolean coordsRadarFlag) {
      int flowHud = getFlowHud(coordsRadarText);
      float radarCoordsAmount2 = this.radarCoordsAmount2CoordsRada;
      float coordsRadarLimit2 = radarCoordsAmount2;
      if (coordsRadarFlag && coordsRadarLimit > 0.0F && this.hotbarCount > 0) {
         float[] moduleshudmap = this.modulesHudMap.get(coordsRadarText);
         float coordsRadarValue2 = (moduleshudmap == null ? 120.0F : moduleshudmap[2] * this.getHotbarHud(coordsRadarText)) + coordsRadarValue;
         float coordsRadarValue3 = 5.5F;
         boolean coordsRadarFlag2 = false;

         for (int i = 0; i < 8; i++) {
            boolean coordsRadarFlag3 = false;

            for (int j = 0; j < this.hotbarCount; j++) {
               if (!(this.radarEntryXs[j] >= this.radarCoordsAmount + coordsRadarValue2 + coordsRadarValue3)
                  && !(this.radarEntryXs[j] + this.radarEntryWidths[j] + coordsRadarValue3 <= this.radarCoordsAmount)
                  && !(this.radarEntryYs[j] >= coordsRadarLimit2 + coordsRadarLimit + coordsRadarValue3)
                  && !(this.radarEntryYs[j] + this.coordsRadarTable[j] + coordsRadarValue3 <= coordsRadarLimit2)) {
                  coordsRadarLimit2 = this.radarEntryYs[j] + this.coordsRadarTable[j] + 6.0F;
                  coordsRadarFlag3 = true;
                  coordsRadarFlag2 |= this.hotbarFlags[j];
               }
            }

            if (!coordsRadarFlag3) {
               break;
            }
         }

         if (coordsRadarFlag2 && this.coordsRadarCount < this.coordsRadarFlags.length) {
            this.coordsRadarFlags[this.coordsRadarCount] = true;
         }
      }

      this.radarCoordsAmount2CoordsRada = radarCoordsAmount2 + this.coordsRadarSmooths[flowHud].getMax(coordsRadarLimit2 - radarCoordsAmount2, this.frameDeltaSeconds, 0.07F, 0.05F);
   }

   private void updateLinkedTime() {
      if (mc.player != null) {
         long timeMillis = System.currentTimeMillis();
         Window window = mc.getWindow();
         long handle = window.getHandle();
         this.handleForQuintScissorFlag(this.keyWPressAnimation, InputUtil.isKeyPressed(window, 87));
         this.handleForQuintScissorFlag(this.quartLinked, InputUtil.isKeyPressed(window, 65));
         this.handleForQuintScissorFlag(this.keySPressAnimation, InputUtil.isKeyPressed(window, 83));
         this.handleForQuintScissorFlag(this.holdKeyAnimation, InputUtil.isKeyPressed(window, 68));
         this.handleForQuintScissorFlag(this.quartStaff, InputUtil.isKeyPressed(window, 32));
         boolean flag = GLFW.glfwGetMouseButton(handle, 0) == 1;
         boolean hudFlag = GLFW.glfwGetMouseButton(handle, 1) == 1;
         this.handleForQuintScissorFlag(this.staffQuintScissor, flag);
         this.handleForQuintScissorFlag(this.linkedQuintScissor, hudFlag);
         if (flag && !this.linkedWasActive) {
            this.linkedLinkedList.add(timeMillis);
         }

         if (hudFlag && !this.linkedActive) {
            this.linkedList.add(timeMillis);
         }

         this.linkedWasActive = flag;
         this.linkedActive = hudFlag;

         while (!this.linkedLinkedList.isEmpty() && timeMillis - this.linkedLinkedList.peek() > 1000L) {
            this.linkedLinkedList.poll();
         }

         while (!this.linkedList.isEmpty() && timeMillis - this.linkedList.peek() > 1000L) {
            this.linkedList.poll();
         }

         if (mc.crosshairTarget != null && mc.crosshairTarget.getType() == Type.ENTITY) {
            Entity entity = ((EntityHitResult)mc.crosshairTarget).getEntity();
            boolean linkedFlag = entity instanceof LivingEntity && (!this.targetPlayersOnly.isEnabled() || entity instanceof PlayerEntity);
            if (linkedFlag) {
               this.entity = (LivingEntity)entity;
               this.lastTimeMillis = timeMillis;
            }
         }

         double x = mc.player.getX();
         double z = mc.player.getZ();
         if (this.timeMillis == 0L) {
            this.x = x;
            this.z = z;
            this.timeMillis = timeMillis;
         } else {
            double seconds = (timeMillis - this.timeMillis) / 1000.0;
            if (seconds >= 0.05) {
               double hypot = Math.hypot(x - this.x, z - this.z);
               this.linkedAmount = this.linkedAmount + (hypot / seconds - this.linkedAmount) * 0.3;
               this.x = x;
               this.z = z;
               this.timeMillis = timeMillis;
            }
         }

         if (mc.world != null && timeMillis - this.timeMillisLinked >= 100L) {
            long linkedValue = mc.world.getTime();
            if (!this.arrayDeque.isEmpty() && linkedValue < this.arrayDeque.peekLast()[1]) {
               this.arrayDeque.clear();
            }

            this.arrayDeque.addLast(new long[]{timeMillis, linkedValue});

            while (this.arrayDeque.size() > 1 && timeMillis - ((long[])this.arrayDeque.peekFirst())[0] > 4000L) {
               this.arrayDeque.pollFirst();
            }

            long[] peekFirst = this.arrayDeque.peekFirst();
            double seconds2 = (timeMillis - peekFirst[0]) / 1000.0;
            if (seconds2 >= 1.0) {
               double max2 = Math.max(0.0, Math.min(20.0, (linkedValue - peekFirst[1]) / seconds2));
               this.tpsServerAmount = this.tpsServerAmount + (max2 - this.tpsServerAmount) * 0.5;
            }

            this.timeMillisLinked = timeMillis;
         }
      }
   }

   private float getTimesForIntLong(int index, long hudValue) {
      long hudValue2 = this.eventNanoTimes[index];
      if (hudValue2 == 0L) {
         return 0.0F;
      } else {
         float hudLimit = (float)(hudValue - hudValue2) / 2.0E8F;
         if (hudLimit >= 1.0F) {
            this.eventNanoTimes[index] = 0L;
            return 0.0F;
         } else {
            return NativeHud.getAmountForFloat(hudLimit);
         }
      }
   }

   public static boolean isPotionsSsaHudValue() {
      return isInstance() && hudValue > 0 && canPerfgraphSsa(ssaPotionsTime);
   }

   private float getDefaultsHudForString(String defaultsHudText) {
      if (!this.hudDefaultsMap.isEmpty() && (!this.defaultsFlag || !defaultsHudText.equals(this.dragReleaseText))) {
         float[] huddefaultsmap = this.hudDefaultsMap.get(defaultsHudText);
         return huddefaultsmap == null ? 0.0F : huddefaultsmap[3];
      } else {
         return 0.0F;
      }
   }

   private float getAmount() {
      return 10.0F;
   }

   private void renderScrollResize(DrawContext context) {
      long nanoTime = System.nanoTime();
      if (!this.hudScales) {
         this.hudScales = true;
         this.lastNanoTimeScrollResize = nanoTime;
         ModuleConfigAutosave.updateFlag2();
      }

      boolean scrollResizeFlag = this.lastNanoTimeScrollResize != 0L && nanoTime - this.lastNanoTimeScrollResize < 5200000000L;
      if (!this.scrollResizePop.canTimeAmount(scrollResizeFlag, ssaPerfgraphCount)) {
         if (!scrollResizeFlag) {
            this.lastNanoTimeScrollResize = 0L;
         }
      } else {
         float scrollResizeValue = NativeHud.getAmount4((float)Math.ceil(this.resizeScrollAmount + 22.0F));
         float maxRadarSize = 21.0F;
         float peekPositionValue = NativeHud.getAmount4((this.scaledWidth - scrollResizeValue) / 2.0F);
         float nativeHudValue = NativeHud.getAmount4(this.scaledHeight * 0.26F);
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(this.scrollResizePop.getPopProgress());
         boolean translateMatrices = NativeHud.canTranslateMatrices(context, peekPositionValue + scrollResizeValue / 2.0F, nativeHudValue + maxRadarSize / 2.0F, this.scrollResizePop.getAmount());

         try {
            NativeHud.handle(NativeHud.resizeScrollValue, peekPositionValue, nativeHudValue, scrollResizeValue, maxRadarSize, maxRadarSize / 2.0F, 1.0F);
            NativeHud.handlePushScale(
               "Drag to move  \u00b7  corner or scroll to resize",
               peekPositionValue + (scrollResizeValue - this.resizeScrollAmount) / 2.0F,
               NativeHud.getAmount2(nativeHudValue, maxRadarSize, 0.86F),
               0.86F,
               NativeHud.OFF_WHITE
            );
         } finally {
            NativeHud.renderPopMatrices(context, translateMatrices);
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
         }
      }
   }

   private static float getExperienceProgress(float experienceProgressLimit) {
      return experienceProgressLimit < 0.0F ? 0.0F : (experienceProgressLimit > 1.0F ? 1.0F : experienceProgressLimit);
   }

   private String formatText(int hudLimit) {
      if (hudLimit >= 0 && hudLimit <= 72000) {
         int seconds = hudLimit / 20;
         return seconds / 60 + ":" + String.format("%02d", seconds % 60);
      } else {
         return "";
      }
   }

   private void updateHotbarHud() {
      for (String hotbarHudText : potionsStaffTexts) {
         if (!this.hotbarHudMap.containsKey(hotbarHudText)) {
            ShowHUD.HudScale hudScale = new ShowHUD.HudScale();
            hudScale.hudScaleFactor = hudScale.hotbarOffsetsAmount = hudScale.coordsPerf = getCoordsPerf(hotbarHudText);
            this.hotbarHudMap.put(hotbarHudText, hudScale);
         }
      }
   }

   private void renderAmountTime(DrawContext context, int hudValue, int fieldsCheckedValue) {
      double hudValue3 = this.quartBuild.getAmount();
      if (!(hudValue3 < 0.005)) {
         float amount = CardStatusUtilsActive.getAmount();
         boolean hudFlag = amount >= 0.999F && CardStatusUtilsActive.getTotal() > 0;
         long timeMillis = System.currentTimeMillis();
         float hudValue4 = this.lastFrameTimeMs == 0L ? 0.016F : Math.min(0.1F, (float)(timeMillis - this.lastFrameTimeMs) / 1000.0F);
         this.lastFrameTimeMs = timeMillis;
         if (amount <= 0.0F && this.displayedBuildProgress > 0.5F) {
            this.displayedBuildProgress = 0.0F;
         } else {
            this.displayedBuildProgress = this.displayedBuildProgress + (amount - this.displayedBuildProgress) * (1.0F - (float)Math.exp(-hudValue4 * 6.0));
         }

         if (Math.abs(amount - this.displayedBuildProgress) < 8.0E-4F) {
            this.displayedBuildProgress = amount;
         }

         this.buildAmount = this.buildAmount + ((hudFlag ? 1.0F : 0.0F) - this.buildAmount) * (1.0F - (float)Math.exp(-hudValue4 * 3.0));
         this.renderAmountBuild(context, hudValue);
      }
   }

   private float getBarInventory(DrawContext context, float barInventoryValue, float barInventoryValue2) {
      boolean booleanValue = this.inventoryCard.isEnabled();
      float hudFactor = this.getAmount3();
      float barInventoryFactor = this.getAmount2();
      float barInventory = (barInventoryFactor * this.getHudScale() + 6.0F) * this.barInventorySmooth.getMax(booleanValue && !this.canHudOffsets("inventory") ? 1.0F : 0.0F, this.frameDeltaSeconds, 0.08F, 0.002F);
      if (!this.barInventoryPop.canTimeAmount(booleanValue, ssaPerfgraphCount)) {
         return barInventory;
      } else {
         float barInventoryValue4 = barInventoryValue + this.coordsOffsetX;
         float hudScaleValue = barInventoryValue2 + this.coordsOffsetY;
         this.handleTextHud(barInventoryValue4, hudScaleValue, hudFactor, barInventoryFactor);
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(this.barInventoryPop.getPopProgress());
         boolean translateMatrices = NativeHud.canTranslateMatrices(context, barInventoryValue4 + hudFactor / 2.0F, hudScaleValue + barInventoryFactor / 2.0F, this.barInventoryPop.getAmount());

         try {
            NativeHud.handle(NativeHud.barInventoryValue, barInventoryValue4, hudScaleValue, hudFactor, barInventoryFactor, 1.0F);
            PlayerInventory inventory = mc.player.getInventory();
            float barInventoryFactor2 = 18.0F;
            float barInventoryFactor3 = 18.0F;
            float topbarBackpackAmount = this.topbarBackpackAmount;
            float peekPositionFactor = 6.0F;
            float barInventoryValue7 = barInventoryValue4 + 6.0F;
            float barInventoryValue8 = hudScaleValue + 6.0F;
            float barInventoryValue9 = barInventoryFactor3 * 0.42F;

            for (int i = 0; i < 3; i++) {
               float nativeHudValue = barInventoryValue8 + i * barInventoryFactor3;
               if (i > 0) {
                  NativeHud.renderForDrawContextFloatInt(context, barInventoryValue7, nativeHudValue, 9.0F * barInventoryFactor2, 536870911);
               }

               for (int j = 0; j < 9; j++) {
                  float barInventoryValue11 = barInventoryValue7 + j * barInventoryFactor2;
                  if (j > 0) {
                     NativeHud.renderForDrawContextFloat(context, barInventoryValue11, nativeHudValue + (barInventoryFactor3 - barInventoryValue9) / 2.0F, barInventoryValue9, 536870911);
                  }

                  ItemStack stack = inventory.getStack(9 + i * 9 + j);
                  float barInventoryValue12 = barInventoryValue11 + barInventoryFactor2 / 2.0F;
                  float barInventoryValue13 = nativeHudValue + barInventoryFactor3 / 2.0F;
                  if (stack.isEmpty()) {
                     NativeHud.handle(
                        IcoGenUtils.getIdentifier("x"),
                        NativeHud.getAmount4(barInventoryValue12 - peekPositionFactor / 2.0F),
                        NativeHud.getAmount4(barInventoryValue13 - peekPositionFactor / 2.0F),
                        peekPositionFactor,
                        NativeHud.DARK_BLUE
                     );
                  } else {
                     float matrixMatricesValue = NativeHud.getAmount4(barInventoryValue12 - topbarBackpackAmount / 2.0F);
                     float barInventoryValue15 = NativeHud.getAmount4(barInventoryValue13 - topbarBackpackAmount / 2.0F);
                     NativeHud.renderMatrixMatrices(context, stack, matrixMatricesValue, barInventoryValue15, topbarBackpackAmount);
                     int index = stack.getCount();
                     if (index > 1) {
                        NativeHud.renderMatrixMatrices(context, index < 100 ? barInventoryTexts[index] : String.valueOf(index), matrixMatricesValue, barInventoryValue15, topbarBackpackAmount);
                     }

                     if (stack.isItemBarVisible()) {
                        float crossLimit = topbarBackpackAmount - 2.0F;
                        float barInventoryValue16 = 1.2F;
                        float matrixStackValue = NativeHud.getAmount4(barInventoryValue15 + topbarBackpackAmount + 0.6F);
                        NativeHud.handlePeekPosition(matrixMatricesValue + 1.0F, matrixStackValue, crossLimit, barInventoryValue16, barInventoryValue16 / 2.0F, 0.0F, 318767103);
                        NativeHud.handlePeekPosition(
                           matrixMatricesValue + 1.0F,
                           matrixStackValue,
                           NativeHud.getAmount4(crossLimit * stack.getItemBarStep() / 13.0F),
                           barInventoryValue16,
                           barInventoryValue16 / 2.0F,
                           0.0F,
                           0xFF000000 | stack.getItemBarColor()
                        );
                     }
                  }
               }
            }
         } finally {
            NativeHud.renderPopMatrices(context, translateMatrices);
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
         }

         return barInventory;
      }
   }

   public static float getMax(boolean hudFlag) {
      if (!canScreenTotems(keybindsResourcesValue)) {
         return 0.0F;
      } else {
         float hudValue = hudFlag ? 28.0F : 14.0F;
         return Math.max(0, Math.round((totemsAmount + getHotbarOffsets()) * keybindsResourcesAmount + 3.0F - hudValue));
      }
   }

   private float getTopbarCloud(DrawContext context, float topbarCloudValue, float topbarCloudValue2, long nanoTime) {
      VertexTexturePeek vertexTexture = getVertexTexture();
      boolean corzclientFlag = vertexTexture.isCorzclientFlag();
      this.topbarPerfFlag = corzclientFlag && this.canCloudPerson(vertexTexture, nanoTime);
      float topbarCloud = (20.0F * this.getHudScale() + 4.0F)
         * this.topbarCloudSmooth.getMax(this.topbarPerfFlag && !this.canHudOffsets("topbar") ? 1.0F : 0.0F, this.frameDeltaSeconds, 0.08F, 0.002F);
      if (this.topbarCloudPop.canTimeAmount(this.topbarPerfFlag, ssaPerfgraphCount) && corzclientFlag && this.clockCloudValue != 0) {
         float glfwLimit = topbarCloudValue + this.coordsOffsetX;
         float hudScaleValue = topbarCloudValue2 + this.coordsOffsetY;
         render(context, vertexTexture, NativeHud.topbarCloudValue, glfwLimit, hudScaleValue, this.cloudPersonH2Segs, this.cloudPersonTable, this.clockCloudValue, this.cloudPersonAmount, this.topbarCloudPop);
         this.handleTextHud(glfwLimit, hudScaleValue, this.cloudPersonAmount, 20.0F);
         this.handleSsaPotions((int)topbarCloudValue, (int)topbarCloudValue2, (int)this.cloudPersonAmount, 20);
         return topbarCloud;
      } else {
         this.handleSsaPotions(0, 0, 0, 0);
         return topbarCloud;
      }
   }

   private static ShowHUD getShowHUD() {
      return (ShowHUD)ModuleRegistry.getModule(ShowHUD.class);
   }

   private static int getHeight(Window window) {
      try {
         Monitor monitor = window.getMonitor();
         if (monitor != null) {
            VideoMode currentVideoMode = monitor.getCurrentVideoMode();
            if (currentVideoMode != null) {
               return currentVideoMode.getHeight();
            }
         }
      } catch (Throwable e) {
      }

      return 0;
   }

   public static int getTotemsHotbar() {
      return hotbarTotemsCount;
   }

   private void handleEffectAmplifier(boolean effectAmplifierFlag) {
      for (int i = 0; i < this.effectAmplifierNPotList.size(); i++) {
         this.effectAmplifierNPotList.get(i).updateAmountFlag();
      }

      if (effectAmplifierFlag) {
         for (StatusEffectInstance statusEffectInstance : mc.player.getStatusEffects()) {
            StatusEffect statusEffect = statusEffectInstance.getEffectType().value();
            ShowHUD.NPot nPot = this.effectAmplifierNPotMap.get(statusEffect);
            if (nPot == null) {
               nPot = new ShowHUD.NPot(statusEffect);
               nPot.flaskPotionsText = this.getString(statusEffectInstance);
               nPot.effectAmplifierAmount = NativeHud.getAmount3(nPot.flaskPotionsText, 0.9F);

               try {
                  nPot.effectTexture = InGameHud.getEffectTexture(statusEffectInstance.getEffectType());
               } catch (Throwable e) {
               }

               nPot.handleAmount(this.effectAmplifierNPotList.size() * 15.0F);
               this.effectAmplifierNPotMap.put(statusEffect, nPot);
               this.effectAmplifierNPotList.add(nPot);
            }

            nPot.updateAmountFlagDurationActive();
            int amplifier = statusEffectInstance.getAmplifier();
            if (amplifier != nPot.amplifier) {
               nPot.amplifier = amplifier;
               nPot.effectAmplifierText = amplifier > 0 ? this.formatViiiVii(amplifier + 1) : "";
               nPot.flaskPotionsAmount = NativeHud.getAmount3(nPot.effectAmplifierText, 0.8F);
            }

            int duration = statusEffectInstance.getDuration();
            boolean canDuration2 = this.canDuration(statusEffectInstance);
            nPot.duration = canDuration2 ? Integer.MAX_VALUE : duration;
            nPot.effectAmplifierActive = !canDuration2 && duration < 200;
            if (canDuration2) {
               if (nPot.field.canMax(-1L)) {
                  nPot.field.handleAmountTime("inf", 0.9F, true, false);
               }
            } else if (nPot.field.canMax(duration / 20)) {
               nPot.field.handleAmountTime(this.formatText(duration), 0.9F, true, false);
            }
         }
      }

      for (int j = 1; j < this.effectAmplifierNPotList.size(); j++) {
         ShowHUD.NPot nPot2 = this.effectAmplifierNPotList.get(j);

         int effectAmplifierCounter;
         for (effectAmplifierCounter = j - 1; effectAmplifierCounter >= 0 && canDuration(nPot2, this.effectAmplifierNPotList.get(effectAmplifierCounter)); effectAmplifierCounter--) {
            this.effectAmplifierNPotList.set(effectAmplifierCounter + 1, this.effectAmplifierNPotList.get(effectAmplifierCounter));
         }

         this.effectAmplifierNPotList.set(effectAmplifierCounter + 1, nPot2);
      }

      int value = 0;

      for (int k = 0; k < this.effectAmplifierNPotList.size(); k++) {
         ShowHUD.NPot nPot3 = this.effectAmplifierNPotList.get(k);
         if (!nPot3.durationActive) {
            nPot3.onlineNoneAmount = value++ * 15.0F;
         }
      }

      this.effectAmplifierValue = value;
      if (value > 0 && this.flask.canMax(value)) {
         this.flask.handleAmountTime(String.valueOf(value), 0.8F, true, false);
         this.pulse.updateTime();
      }
   }

   private int getHsvRgb(int index) {
      long hsvRgbValue = Math.round(getExperienceProgress(this.shieldDisplayedFractions[index]) * 600.0F);
      if (hsvRgbValue != this.hsvRgbTable[index]) {
         this.hsvRgbTable[index] = hsvRgbValue;
         this.hsvColorCache[index] = 0xFF000000 | MathHelper.hsvToRgb((float)hsvRgbValue / 1800.0F, 1.0F, 1.0F);
      }

      return this.hsvColorCache[index];
   }

   private void updateDefaultsLayout() {
      float defaultsLayoutValue = this.defaultsLayoutSmooth
         .getMax(this.dragReleaseText != null && this.defaultsFlag && this.defaultsLayoutValue > 0 ? 1.0F : 0.0F, this.defaultsLayoutAmount, 0.05F, 0.01F);
      if (!(defaultsLayoutValue <= 0.01F) && this.defaultsLayoutValue != 0) {
         int defaultsLayoutValue2 = NativeHud.getValue(NativeHud.getReleaseRight(), 0.85F * defaultsLayoutValue);
         float crossLimit = MatrixPeekUtilsRender.getAmount();

         for (int i = 0; i < this.defaultsLayoutValue; i++) {
            float defaultsLayoutBound = this.defaultsLayoutTable[i * 4];
            float nativeHudValue = this.defaultsLayoutTable[i * 4 + 1];
            float defaultsLayoutBound3 = this.defaultsLayoutTable[i * 4 + 2];
            float hashHuntBound = this.defaultsLayoutTable[i * 4 + 3];
            if (defaultsLayoutBound == defaultsLayoutBound3) {
               NativeHud.handlePeekPosition(NativeHud.getAmount4(defaultsLayoutBound), Math.min(nativeHudValue, hashHuntBound), crossLimit, Math.abs(hashHuntBound - nativeHudValue), 0.0F, 0.0F, defaultsLayoutValue2);
            } else {
               NativeHud.handlePeekPosition(Math.min(defaultsLayoutBound, defaultsLayoutBound3), NativeHud.getAmount4(nativeHudValue), Math.abs(defaultsLayoutBound3 - defaultsLayoutBound), crossLimit, 0.0F, 0.0F, defaultsLayoutValue2);
            }
         }
      }
   }

   private static float getPotionsPerfgraph(String potionsPerfgraphText) {
      return switch (potionsPerfgraphText) {
         case "perfgraph" -> 0.9F;
         case "potions" -> 1.0F;
         default -> -1.0F;
      };
   }

   private boolean canAmountHud(String hudText, float[] hudValues) {
      float hudBound = Float.MAX_VALUE;
      float maxValue = Float.MAX_VALUE;
      float pilotStatusBound = -Float.MAX_VALUE;
      float maxValue4 = -Float.MAX_VALUE;

      for (int i = 0; i < this.hudResetItems.size(); i++) {
         ShowHUD.HudBox hudresetitem = this.hudResetItems.get(i);
         if (hudresetitem.dragResetText.equals(hudText)) {
            hudBound = Math.min(hudBound, hudresetitem.dragResetAmount);
            maxValue = Math.min(maxValue, hudresetitem.hudAmount);
            pilotStatusBound = Math.max(pilotStatusBound, hudresetitem.dragResetAmount + hudresetitem.hudBoxAmount);
            maxValue4 = Math.max(maxValue4, hudresetitem.hudAmount + hudresetitem.boxHeight);
         }
      }

      if (pilotStatusBound < hudBound) {
         return false;
      } else {
         hudValues[0] = hudBound;
         hudValues[1] = maxValue;
         hudValues[2] = pilotStatusBound - hudBound;
         hudValues[3] = maxValue4 - maxValue;
         return true;
      }
   }

   private boolean canFlag(Module Module) {
      return Module != null && Module.isEnabled() && Module.getBindKey() >= 0 && Module.isVisible();
   }

   private void handleCoordsRadar(ShowHUD.NFlow nFlow, Pop pop2, boolean coordsRadarFlag, float coordsRadarLimit) {
      if (!coordsRadarFlag) {
         nFlow.coordsRadarActive = false;
         nFlow.lastRadarCount = -1;
      } else {
         boolean coordsRadarFlag2 = pop2.getPopProgress() > 0.01F;
         if (coordsRadarLimit > 0.0F || !coordsRadarFlag2) {
            nFlow.coordsRadarActive = coordsRadarLimit > 0.0F
               && this.radarCoordsAmount2CoordsRada > this.coordsRadarAmount + 0.5F
               && this.radarCoordsAmount2CoordsRada + coordsRadarLimit + (nFlow.coordsRadarActive && coordsRadarFlag2 ? 32.0F : 0.0F) > this.coordsAmount;
         }

         if (nFlow.coordsRadarActive) {
            this.radarCoordsAmount = this.radarCoordsAmount + (this.maxCoordsRadarBound + 6.0F);
            this.radarCoordsAmount2CoordsRada = this.coordsRadarAmount;
            this.maxCoordsRadarBound = 0.0F;
            this.coordsRadarCount++;
            if (this.coordsRadarCount < this.coordsAmounts.length) {
               this.coordsAmounts[this.coordsRadarCount] = this.radarCoordsAmount;
               this.coordsRadarValue = Math.max(this.coordsRadarValue, this.coordsRadarCount + 1);
            }
         }

         if (coordsRadarFlag2 && nFlow.lastRadarCount >= 0 && nFlow.lastRadarCount != this.coordsRadarCount) {
            pop2.updateTime();
         }

         nFlow.lastRadarCount = this.coordsRadarCount;
      }
   }

   private void handleAmount(float hudValue, boolean hudFlag, float... hudValues) {
      float hudLimit = this.hotbarAmount - hudValue;
      float hudLimit2 = 18.0F;
      boolean hudFlag2 = hudFlag ? hudLimit < 0.0F : hudLimit > 0.0F;
      float abs2 = Math.abs(hudLimit);
      if (hudFlag2 ? abs2 <= hudLimit2 : abs2 <= 5.0F) {
         this.handleAmountMin(hudValue, hudFlag2 ? abs2 * 5.0F / hudLimit2 : abs2, hudValues);
      }
   }

   private float getPerformancePerfgraph(DrawContext context, float performancePerfgraphValue, float performancePerfgraphValue2, long nanoTime) {
      boolean booleanValue = this.performanceCard.isEnabled();
      float hudFactor = 156.0F;
      float performancePerfgraphFactor = 68.0F;
      float performancePerfgraph = (performancePerfgraphFactor * this.getHudScale() + 6.0F) * this.performancePerfgraphSmooth.getMax(booleanValue && !this.canHudOffsets("perfgraph") ? 1.0F : 0.0F, this.frameDeltaSeconds, 0.08F, 0.002F);
      if (!this.performancePerfgraphPop.canTimeAmount(booleanValue, ssaPerfgraphCount)) {
         return performancePerfgraph;
      } else {
         if (this.lastNanoTimePerformancePerfg == 0L || nanoTime - this.lastNanoTimePerformancePerfg >= 250000000L) {
            this.lastNanoTimePerformancePerfg = nanoTime;
            int currentFps = mc.getCurrentFps();
            if (this.performancePerfgraphField.canMax(currentFps)) {
               this.performancePerfgraphField.handleAmountTime(currentFps + " FPS", 0.9F, true, false);
            }
         }

         float performancePerfgraphValue4 = performancePerfgraphValue + this.coordsOffsetX;
         float hudScaleValue = performancePerfgraphValue2 + this.coordsOffsetY;
         this.handleTextHud(performancePerfgraphValue4, hudScaleValue, hudFactor, performancePerfgraphFactor);
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(this.performancePerfgraphPop.getPopProgress());
         boolean translateMatrices = NativeHud.canTranslateMatrices(context, performancePerfgraphValue4 + hudFactor / 2.0F, hudScaleValue + performancePerfgraphFactor / 2.0F, this.performancePerfgraphPop.getAmount());

         try {
            NativeHud.handle(NativeHud.performanceValue, performancePerfgraphValue4, hudScaleValue, hudFactor, performancePerfgraphFactor, 1.0F);
            float performancePerfgraphValue6 = 8.0F;
            float maxRadarSize = 20.0F;
            NativeHud.handlePushScale("Performance", performancePerfgraphValue4 + performancePerfgraphValue6, NativeHud.getAmount2(hudScaleValue + 4.0F, maxRadarSize, 0.95F), 0.95F, NativeHud.OFF_WHITE);
            NativeHud.handleAmount(this.performancePerfgraphField, performancePerfgraphValue4 + hudFactor - performancePerfgraphValue6, NativeHud.getAmount2(hudScaleValue + 4.0F, maxRadarSize, 0.9F), 0.9F, NativeHud.getWhite());
            float performancePerfgraphBound = performancePerfgraphValue4 + performancePerfgraphValue6;
            float performancePerfgraphValue8 = performancePerfgraphValue4 + hudFactor - performancePerfgraphValue6;
            float performancePerfgraphValue9 = hudScaleValue + 26.0F;
            float textureColorBound = hudScaleValue + performancePerfgraphFactor - 15.0F;
            this.handleStyleHud(performancePerfgraphBound, performancePerfgraphValue8, performancePerfgraphValue9, textureColorBound);
            float f13 = NativeHud.getAmount2(textureColorBound + 2.0F, 11.0F, 0.62F);

            for (int i = 0; i < 6; i++) {
               float performancePerfgraphValue12 = this.onlineStaffTable[i];
               float hashHuntBound = performancePerfgraphBound + (performancePerfgraphValue8 - performancePerfgraphBound) * i / 5.0F - performancePerfgraphValue12 / 2.0F;
               float nativeHudValue = Math.max(performancePerfgraphBound, Math.min(performancePerfgraphValue8 - performancePerfgraphValue12, hashHuntBound));
               NativeHud.handlePushScale(onlineStaffTexts[i], NativeHud.getAmount4(nativeHudValue), f13, 0.62F, NativeHud.DARK_BLUE);
            }
         } finally {
            NativeHud.renderPopMatrices(context, translateMatrices);
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
         }

         return performancePerfgraph;
      }
   }

   private static float[] getAmounts(float hudLimit, float hudLimit2, float hudValue, float maxRadarSize, float hudValue3, float hudValue4) {
      float hudLimit3 = hudValue3 - (hudLimit + hudValue);
      float abs5 = Math.abs(hudLimit + hudValue / 2.0F - hudValue3 / 2.0F);
      float hudValue6;
      float hudValue7;
      if (abs5 < hudLimit && abs5 < hudLimit3) {
         hudValue6 = 0.5F;
         hudValue7 = hudLimit + hudValue / 2.0F - hudValue3 / 2.0F;
      } else if (hudLimit <= hudLimit3) {
         hudValue6 = 0.0F;
         hudValue7 = hudLimit;
      } else {
         hudValue6 = 1.0F;
         hudValue7 = hudLimit3;
      }

      float hudLimit4 = hudValue4 - (hudLimit2 + maxRadarSize);
      float abs8 = Math.abs(hudLimit2 + maxRadarSize / 2.0F - hudValue4 / 2.0F);
      float hudValue9;
      float hudValue10;
      if (abs8 < hudLimit2 && abs8 < hudLimit4) {
         hudValue9 = 0.5F;
         hudValue10 = hudLimit2 + maxRadarSize / 2.0F - hudValue4 / 2.0F;
      } else if (hudLimit2 <= hudLimit4) {
         hudValue9 = 0.0F;
         hudValue10 = hudLimit2;
      } else {
         hudValue9 = 1.0F;
         hudValue10 = hudLimit4;
      }

      return new float[]{hudValue6, hudValue9, hudValue7, hudValue10};
   }

   private void handleRoundSsa(float roundSsaValue, float glfwLimit, float roundSsaFactor) {
      roundSsaTop = Math.round(roundSsaValue + roundSsaFactor * (1.0F - this.getHotbarHud(this.coordsText)));
      round = Math.round(glfwLimit);
      ssaPerfgraphCountRoundSsa = ssaPerfgraphCount;
   }

   private static float getAmount(float hudLimit, float hudValue, float hudValue2, float scaledHudFactor) {
      if (hudLimit <= 0.25F) {
         return hudValue;
      } else {
         return hudLimit >= 0.75F ? hudValue2 - hudValue - scaledHudFactor : hudValue2 / 2.0F + hudValue - scaledHudFactor / 2.0F;
      }
   }

   private int getAmountScroll(double scrollLimit, double scrollLimit2) {
      if (Math.abs(scrollLimit - this.scrollAmount) <= 6.0 && Math.abs(scrollLimit2 - this.popupPointerY) <= 6.0) {
         return 1;
      } else if (!(scrollLimit < this.popupCurrentX) && !(scrollLimit > this.popupCurrentX + this.popupWidth) && !(scrollLimit2 < this.popupCurrentY) && !(scrollLimit2 > this.popupCurrentY + 17.0F)) {
         float scrollValue = (float)scrollLimit - this.popupCurrentX;
         if (scrollValue < 17.0F) {
            return 2;
         } else if (scrollValue < 17.0F + this.percentLabelWidth) {
            return 3;
         } else if (scrollValue < 17.0F + this.percentLabelWidth + 17.0F) {
            return 4;
         } else {
            return this.scrollSmooth.getMax() > 0.5F ? 5 : 6;
         }
      } else {
         return 0;
      }
   }

   private String formatViiiVii(int viiiViiValue) {
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

   private static boolean canScreenTotems(int screenTotemsValue) {
      return hotbarTotemsCount - screenTotemsValue <= 1 && (mc.currentScreen == null || mc.currentScreen instanceof ChatScreen);
   }

   private static String formatTextForInt(int hudLimit) {
      if (hudLimit < 1000) {
         return String.valueOf(hudLimit);
      } else if (hudLimit >= 10000) {
         return hudLimit / 1000 + "k";
      } else {
         int hudValue = hudLimit / 100 % 10;
         return hudValue == 0 ? hudLimit / 1000 + "k" : hudLimit / 1000 + "." + hudValue + "k";
      }
   }

   private static float getVertexTexture(VertexTexturePeek vertexTexture, float amount) {
      if (vertexTexture == vertexTextureVertexTexturePe && Float.floatToRawIntBits(amount) == Float.floatToRawIntBits(vertexTextureAmount)) {
         return cachedVertexTextureMax;
      } else {
         float max = 0.0F;

         for (char vertexTextureCounter = '0'; vertexTextureCounter <= '9'; vertexTextureCounter++) {
            max = Math.max(max, vertexTexture.getAmountFlag(vertexTextureCounter, amount));
         }

         vertexTextureVertexTexturePe = vertexTexture;
         vertexTextureAmount = amount;
         cachedVertexTextureMax = max;
         return max;
      }
   }

   private float getFlaskPotions(DrawContext context, float flaskPotionsValue, float flaskPotionsValue2, long nanoTime) {
      if (this.lastNanoTimeFlaskPotions == 0L || nanoTime - this.lastNanoTimeFlaskPotions >= 50000000L) {
         this.lastNanoTimeFlaskPotions = nanoTime;
         this.handleEffectAmplifier(this.potionsScaledMatrices.isEnabled());
      }

      boolean booleanValue = this.effectIcons.isEnabled();
      float flaskPotionsValue3 = booleanValue ? this.potionIconSize + 5.0F : 0.0F;
      float rowFactor = 1.0F - (float)Math.exp(-this.frameDeltaSeconds / 0.06F);
      float flaskPotionsTotal = 0.0F;
      float flaskPotionsBound = 0.0F;

      for (int i = 0; i < this.effectAmplifierNPotList.size(); i++) {
         ShowHUD.NPot nPot = this.effectAmplifierNPotList.get(i);
         if (nPot.canAmountFlag(rowFactor)) {
            this.effectAmplifierNPotMap.remove(nPot.statusEffect);
            this.effectAmplifierNPotList.remove(i--);
         } else {
            flaskPotionsTotal += 15.0F * nPot.personOnlineAmount;
            if (!nPot.durationActive) {
               flaskPotionsBound = Math.max(flaskPotionsBound, flaskPotionsValue3 + nPot.effectAmplifierAmount + (nPot.effectAmplifierText.isEmpty() ? 0.0F : 4.0F + nPot.flaskPotionsAmount) + 10.0F + nPot.field.checkedRocketsAmount);
            }
         }
      }

      boolean flaskPotionsFlag = this.effectAmplifierValue > 0;
      float maxRadarSize = 30.0F + flaskPotionsTotal + 5.0F;
      float flaskPotions = (maxRadarSize + 6.0F) * this.flaskPotionsSmooth.getMax(flaskPotionsFlag ? 1.0F : 0.0F, this.frameDeltaSeconds, 0.08F, 0.002F);
      if (!this.flaskPotionsPop.canTimeAmount(flaskPotionsFlag, ssaPerfgraphCount)) {
         this.flaskPotionsHeightSmooth.updateMax();
         return flaskPotions;
      } else {
         float hudFactor = this.flaskPotionsHeightSmooth
            .getAmount(
               (float)Math.ceil(Math.max(120.0F, Math.max(NativeHud.getAmount2(this.cooldownsPotionsAmount, this.flask.checkedRocketsAmount), flaskPotionsBound + 14.0F))),
               this.frameDeltaSeconds,
               0.08F
            );
         float flaskPotionsValue7 = flaskPotionsValue + this.coordsOffsetX;
         float hudScaleValue = flaskPotionsValue2 + this.coordsOffsetY;
         this.handleTextHud(flaskPotionsValue7, hudScaleValue, hudFactor, maxRadarSize);
         float flaskPotionsValue9 = 0.55F + 0.45F * (0.5F + 0.5F * (float)Math.cos(nanoTime % 900000000L * 6.981317007977318E-9));
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(this.flaskPotionsPop.getPopProgress());
         boolean translateMatrices = NativeHud.canTranslateMatrices(context, flaskPotionsValue7 + hudFactor / 2.0F, hudScaleValue + maxRadarSize / 2.0F, this.flaskPotionsPop.getAmount());

         try {
            NativeHud.handle(NativeHud.flaskPotionsValue, flaskPotionsValue7, hudScaleValue, hudFactor, maxRadarSize, 1.0F);
            float flaskPotionsValue10 = NativeHud.getAmount(
               context, flaskPotionsValue7, hudScaleValue, hudFactor, IcoGenUtils.getIdentifierForString("flask"), "Potions", this.flask, this.pulse, getExperienceProgress(flaskPotionsTotal / 8.0F)
            );
            float guiMatrixValue = flaskPotionsValue7 + 7.0F;
            float flaskPotionsValue12 = flaskPotionsValue7 + hudFactor - 7.0F;

            for (int j = 0; j < this.effectAmplifierNPotList.size(); j++) {
               ShowHUD.NPot nPot2 = this.effectAmplifierNPotList.get(j);
               float personOnlineAmount = nPot2.personOnlineAmount;
               if (!(personOnlineAmount <= 0.002F)) {
                  float peekPositionFactor = 15.0F * personOnlineAmount;
                  float roundOffset = flaskPotionsValue10 + nPot2.rowHeight;
                  float nativeHudValue = guiMatrixValue + flaskPotionsValue3;
                  float panelOverlay2 = CorzRoundedUtils.getPanelOverlay(personOnlineAmount * personOnlineAmount);

                  try {
                     if (booleanValue) {
                        NativeHud.renderGuiMatrix(context, nPot2.effectTexture, guiMatrixValue, roundOffset + NativeHud.getAmount4((peekPositionFactor - this.potionIconSize) / 2.0F), this.potionIconSize);
                     }

                     NativeHud.handle(nPot2.flaskPotionsText, nativeHudValue, roundOffset, peekPositionFactor, NativeHud.OFF_WHITE);
                     if (!nPot2.effectAmplifierText.isEmpty()) {
                        NativeHud.handleColor(nPot2.effectAmplifierText, nativeHudValue + nPot2.effectAmplifierAmount + 4.0F, roundOffset, peekPositionFactor);
                     }

                     float panelOverlay3 = CorzRoundedUtils.getPanelOverlay(nPot2.effectAmplifierActive ? flaskPotionsValue9 : 1.0F);

                     try {
                        NativeHud.handleForFieldFloat(nPot2.field, flaskPotionsValue12, roundOffset, peekPositionFactor, nPot2.effectAmplifierActive ? NativeHud.RED : NativeHud.OFF_WHITE);
                     } finally {
                        CorzRoundedUtils.handlePanelOverlay(panelOverlay3);
                     }
                  } finally {
                     CorzRoundedUtils.handlePanelOverlay(panelOverlay2);
                  }
               }
            }
         } finally {
            NativeHud.renderPopMatrices(context, translateMatrices);
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
         }

         return flaskPotions;
      }
   }

   private boolean canAmountBottom(String bottomText, ShowHUD.HudScale hudScale, float bottomValue, float hudScaleValue, float bottomFactor, float radarSsaFactor) {
      float amount = hudScale.getOffsetsHotbar();
      if (amount == 1.0F) {
         hudScale.coordsPerf = 1.0F;
         return false;
      } else {
         this.handleBottomLeft(bottomText, hudScale, bottomValue, hudScaleValue, bottomFactor, radarSsaFactor);
         hudScale.coordsPerf = amount;
         hudScale.translateX = bottomValue + this.bottomAmount * bottomFactor;
         hudScale.translateAmount = hudScaleValue + this.bottomLeftAmount * radarSsaFactor;
         return true;
      }
   }

   private float getKeybindsKeyboard(DrawContext context, float keybindsKeyboardValue, float keybindsKeyboardValue2) {
      boolean booleanValue = this.keybindsCard.isEnabled();
      var list = ModuleRegistry.getModules();
      long time = booleanValue ? -7046029254386353131L : 0L;
      if (booleanValue) {
         int index = 0;

         for (int i = list.size(); index < i; index++) {
            Module Module = (Module)list.get(index);
            if (this.canFlag(Module)) {
               time = (time ^ System.identityHashCode(Module) * -7046029254386353131L + Module.getBindKey()) * -4658895280553007687L;
            }
         }
      }

      if (!this.keybindsKeyboardActive || time != this.keybindsKeyboardValue) {
         this.keybindsKeyboardActive = true;
         this.keybindsKeyboardValue = time;
         this.sortedKeybindModules.clear();
         if (booleanValue) {
            int index2 = 0;

            for (int j = list.size(); index2 < j; index2++) {
               Module ilIllIiIll3 = (Module)list.get(index2);
               if (this.canFlag(ilIllIiIll3)) {
                  int itemsSize = this.sortedKeybindModules.size();
                  this.sortedKeybindModules.add(ilIllIiIll3);

                  while (itemsSize > 0 && String.CASE_INSENSITIVE_ORDER.compare(this.sortedKeybindModules.get(itemsSize - 1).getCategory(), ilIllIiIll3.getCategory()) > 0) {
                     this.sortedKeybindModules.set(itemsSize, this.sortedKeybindModules.get(itemsSize - 1));
                     itemsSize--;
                  }

                  this.sortedKeybindModules.set(itemsSize, ilIllIiIll3);
               }
            }
         }

         this.nList.update();

         for (int k = 0; k < this.sortedKeybindModules.size(); k++) {
            this.nList.handleShowHud(this.sortedKeybindModules.get(k));
         }

         this.nList.updateFlag();
         int pilotElytraValue = this.sortedKeybindModules.size();
         if (pilotElytraValue > 0 && this.keyboard.canMax(pilotElytraValue)) {
            this.keyboard.handleAmountTime(String.valueOf(pilotElytraValue), 0.8F, true, false);
            this.keybindsKeyboardPulse.updateTime();
         }
      }

      this.nList.handleKeybindsKeyboard(1.0F - (float)Math.exp(-this.frameDeltaSeconds / 0.06F));
      boolean keybindsKeyboardFlag = booleanValue && this.nList.keybindsKeystrokesFlag;
      float keybindsKeyboardFactor = 30.0F + this.nList.keybindsKeyboardAmount + 5.0F;
      float keybindsKeyboard = (keybindsKeyboardFactor * this.getHudScale() + 6.0F)
         * this.keybindsVisibilitySmooth.getMax(keybindsKeyboardFlag && !this.canHudOffsets("keybinds") ? 1.0F : 0.0F, this.frameDeltaSeconds, 0.08F, 0.002F);
      if (!this.keybindsKeyboardPop.canTimeAmount(keybindsKeyboardFlag, ssaPerfgraphCount)) {
         this.keybindsKeyboardSmooth.updateMax();
         return keybindsKeyboard;
      } else {
         float hudFactor = this.keybindsKeyboardSmooth
            .getAmount(
               (float)Math.ceil(
                  Math.max(120.0F, Math.max(NativeHud.getAmount2(this.onlineStaffAmount, this.keyboard.checkedRocketsAmount), this.nList.maxRowWidth + 14.0F))
               ),
               this.frameDeltaSeconds,
               0.08F
            );
         float keybindsKeyboardValue4 = keybindsKeyboardValue + this.coordsOffsetX;
         float hudScaleValue = keybindsKeyboardValue2 + this.coordsOffsetY;
         this.handleTextHud(keybindsKeyboardValue4, hudScaleValue, hudFactor, keybindsKeyboardFactor);
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(this.keybindsKeyboardPop.getPopProgress());
         boolean translateMatrices = NativeHud.canTranslateMatrices(context, keybindsKeyboardValue4 + hudFactor / 2.0F, hudScaleValue + keybindsKeyboardFactor / 2.0F, this.keybindsKeyboardPop.getAmount());

         try {
            NativeHud.handle(NativeHud.keyboardKeybindsValue, keybindsKeyboardValue4, hudScaleValue, hudFactor, keybindsKeyboardFactor, 1.0F);
            float keybindsKeyboardValue6 = NativeHud.getAmount(
               context,
               keybindsKeyboardValue4,
               hudScaleValue,
               hudFactor,
               IcoGenUtils.getIdentifierForString("keyboard"),
               "Keybinds",
               this.keyboard,
               this.keybindsKeyboardPulse,
               getExperienceProgress(this.nList.keybindsKeyboardAmount / 8.0F)
            );
            this.nList.handleAmountText(keybindsKeyboardValue6, keybindsKeyboardValue4 + 7.0F, keybindsKeyboardValue4 + hudFactor - 7.0F);
         } finally {
            NativeHud.renderPopMatrices(context, translateMatrices);
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
         }

         return keybindsKeyboard;
      }
   }

   public static int getHeight(MinecraftClient minecraftClient) {
      Window window = minecraftClient.getWindow();
      int height = window.getHeight();
      int height2 = getHeight(window);
      return height2 > height && height >= height2 * 0.8F ? height2 : height;
   }

   private static int getValue(int hudValue, float experienceProgressLimit) {
      return getValue(hudValue, Math.round((hudValue >>> 24 & 0xFF) * getExperienceProgress(experienceProgressLimit)));
   }

   public static ShowHUD.HudScale getHudHotbar(String hudHotbarText, float hudHotbarValue, float hudScaleValue, float bottomFactor, float maxRadarSize) {
      ShowHUD showHUD = getShowHUD();
      if (showHUD != null && hudHotbarText != null && !(bottomFactor <= 0.0F) && !(maxRadarSize <= 0.0F)) {
         ShowHUD.HudScale hudHotbar = showHUD.hotbarHudMap.isEmpty() ? null : showHUD.hotbarHudMap.get(hudHotbarText);
         if (hudHotbar != null && !showHUD.canAmountBottom(hudHotbarText, hudHotbar, hudHotbarValue, hudScaleValue, bottomFactor, maxRadarSize)) {
            hudHotbar = null;
         }

         showHUD.handleHudAmount(hudHotbarText, hudHotbarValue, hudScaleValue, bottomFactor, maxRadarSize, hudHotbar);
         return hudHotbar;
      } else {
         return null;
      }
   }

   private static void handleColor(String colorText, float colorValue, float nativeHudValue, float matrixStackValue, float nativeHudBound, float hashHuntBound, float cardMatrixFactor) {
      NativeHud.handle(nativeHudValue, matrixStackValue, nativeHudBound, hashHuntBound, cardMatrixFactor);
      NativeHud.handlePushScale(colorText, NativeHud.getAmount4(nativeHudValue + (nativeHudBound - colorValue) / 2.0F), NativeHud.getAmount2(matrixStackValue, hashHuntBound, 0.82F), 0.82F, NativeHud.OFF_WHITE);
   }

   private static int getFlowHud(String flowHudText) {
      for (int i = 0; i < hudFlowTexts.length; i++) {
         if (hudFlowTexts[i].equals(flowHudText)) {
            return i;
         }
      }

      return -1;
   }

   public static float getTime() {
      ShowHUD showHUD = (ShowHUD)ModuleRegistry.getModule(ShowHUD.class);
      return showHUD != null && showHUD.isEnabled() ? getExperienceProgress((float)(showHUD.scaledMatricesQuintScissor.getAmount() * showHUD.scaledFadeAnimation.getAmount())) : 1.0F;
   }

   private float getAmount2() {
      return 66.0F;
   }

   private void handleHudAmount(String hudText, float hudValue, float hudScaleValue, float hudFactor, float maxRadarSize, ShowHUD.HudScale hudScale) {
      if (hudText != null && !(hudFactor <= 0.0F) && !(maxRadarSize <= 0.0F)) {
         boolean equals = hudText.equals(this.coordsText);
         float hudValue3 = equals ? this.coordsOffsetX : this.getAmount5(hudText);
         float hudValue4 = equals ? this.coordsOffsetY : this.getAmount(hudText);
         float[] floats = new float[]{hudValue - hudValue3, hudScaleValue - hudValue4, hudFactor, maxRadarSize};
         if (equals) {
            this.coordsTable = getAmounts(this.coordsTable, floats);
         } else {
            this.modulesHudMap.put(hudText, floats);
            if (hudScale != null) {
               this.handleHotbarAmounts(hudText, hudScale.getAmount(hudValue) - this.getDefaultsHud(hudText), hudScale.getAmountForFloat(hudScaleValue) - this.getDefaultsHudForString(hudText), hudFactor * hudScale.coordsPerf, maxRadarSize * hudScale.coordsPerf);
            } else {
               this.handleHotbarAmounts(hudText, hudValue - this.getDefaultsHud(hudText), hudScaleValue - this.getDefaultsHudForString(hudText), hudFactor, maxRadarSize);
            }
         }

         if (this.scaledMatricesActive) {
            if (hudScale != null) {
               this.hudResetItems.add(new ShowHUD.HudBox(hudText, hudScale.getAmount(hudValue), hudScale.getAmountForFloat(hudScaleValue), hudFactor * hudScale.coordsPerf, maxRadarSize * hudScale.coordsPerf));
            } else {
               this.hudResetItems.add(new ShowHUD.HudBox(hudText, hudValue, hudScaleValue, hudFactor, maxRadarSize));
            }
         }
      }
   }

   private void handleAmountsHotbar(boolean hotbarFlag, boolean hotbarFlag2) {
      if (this.hotbarValue != hotbarTotemsCount) {
         this.hotbarItemCount = 0;
      }

      int value = this.hotbarItemCount;
      String hotbarText = this.defaultsFlag ? this.dragReleaseText : null;
      boolean hasVar = hotbarText != null;

      for (int i = 0; i < value; i++) {
         this.hotbarTable[i] = this.hotbarTargetXs[i];
         this.hotbarCurrentYs[i] = this.hotbarTargetYs[i];
         this.hudFlags[i] = false;
         String hudOffsetsText = this.hotbarTexts[i];
         boolean hotbarFlag3 = hudOffsetsText.equals(hotbarText) && !this.defaultsLayoutFlag;
         this.hotbarSortPriorities[i] = hotbarFlag3 ? Integer.MAX_VALUE : (this.canHudOffsets(hudOffsetsText) ? this.getIndexOf(hudOffsetsText) : Integer.MIN_VALUE);
         if (!hotbarFlag3 && this.hotbarSortPriorities[i] != Integer.MIN_VALUE) {
            hasVar = true;
         }
      }

      if (hasVar) {
         int value2 = 0;

         for (int j = 0; j < value; j++) {
            if (!hotbarFlag || !this.hotbarTexts[j].equals(hotbarText)) {
               this.hotbarSortedIndices[value2++] = j;
            }
         }

         for (int k = 1; k < value2; k++) {
            int hotbarValue = this.hotbarSortedIndices[k];

            int index;
            for (index = k - 1; index >= 0 && this.canAmounts(hotbarValue, this.hotbarSortedIndices[index]); index--) {
               this.hotbarSortedIndices[index + 1] = this.hotbarSortedIndices[index];
            }

            this.hotbarSortedIndices[index + 1] = hotbarValue;
         }

         this.scaledValue = value2;

         for (int m = 0; m < value2; m++) {
            int index2 = this.hotbarSortedIndices[m];
            if (this.hotbarSortPriorities[index2] == Integer.MAX_VALUE) {
               this.hudFlags[index2] = true;
            } else {
               boolean hotbarFlag4 = this.hotbarSortPriorities[index2] == Integer.MIN_VALUE;
               boolean hotbarFlag5 = false;
               if (!hotbarFlag4 || getFlowHud(this.hotbarTexts[index2]) < 0) {
                  for (int p = 0; p < m && !hotbarFlag5; p++) {
                     int index3 = this.hotbarSortedIndices[p];
                     if ((this.hudFlags[index3] || !hotbarFlag4)
                        && canFlag(
                           this.hotbarTargetXs[index2],
                           this.hotbarTargetYs[index2],
                           this.hotbarWidths[index2],
                           this.hotbarHeights[index2],
                           this.hotbarTable[index3],
                           this.hotbarCurrentYs[index3],
                           this.hotbarWidths[index3],
                           this.hotbarHeights[index3]
                        )) {
                        hotbarFlag5 = true;
                     }
                  }

                  if (!hotbarFlag4) {
                     this.hudFlags[index2] = true;
                  }

                  if (hotbarFlag5 && (this.canAmountsScaled(index2, m, true) || this.canAmountsScaled(index2, m, false))) {
                     this.hotbarTable[index2] = this.scaledBestPosition;
                     this.hotbarCurrentYs[index2] = this.scaledAmount;
                     this.hotbarGroupCounts[index2] = this.scaledResultCount;
                     this.hudFlags[index2] = true;
                  }
               }
            }
         }
      }

      if (hotbarFlag2) {
         this.hotbarCount = 0;

         for (int q = 0; q < value && this.hotbarCount < 64; q++) {
            if (this.hotbarSortPriorities[q] != Integer.MIN_VALUE && (this.hotbarSortPriorities[q] != Integer.MAX_VALUE || getFlowHud(this.hotbarTexts[q]) < 0)) {
               this.radarEntryXs[this.hotbarCount] = this.hotbarTable[q];
               this.radarEntryYs[this.hotbarCount] = this.hotbarCurrentYs[q];
               this.radarEntryWidths[this.hotbarCount] = this.hotbarWidths[q];
               this.coordsRadarTable[this.hotbarCount] = this.hotbarHeights[q];
               this.hotbarFlags[this.hotbarCount] = this.hotbarSortPriorities[q] == Integer.MAX_VALUE;
               this.hotbarCount++;
            }
         }
      }

      if (hotbarFlag2) {
         float hotbarFactor = 1.0F - (float)Math.exp(-this.frameDeltaSeconds / 0.07F);

         for (int r = 0; r < value; r++) {
            String botCrystalText = this.hotbarTexts[r];
            float hotbarValue2 = this.hotbarTable[r] - this.hotbarTargetXs[r];
            float planValuesItem = this.hotbarCurrentYs[r] - this.hotbarTargetYs[r];
            float[] hotbarValues = this.hudDefaultsMap.isEmpty() ? null : this.hudDefaultsMap.get(botCrystalText);
            if (hotbarValues == null) {
               if (Math.abs(hotbarValue2) < 0.05F && Math.abs(planValuesItem) < 0.05F) {
                  continue;
               }

               boolean hotbarFlag6 = false;

               for (int s = 0; s < this.hotbarVisibleCount && !hotbarFlag6; s++) {
                  hotbarFlag6 = botCrystalText.equals(this.hudTexts[s]);
               }

               hotbarValues = new float[]{hotbarValue2, planValuesItem, hotbarFlag6 ? 0.0F : hotbarValue2, hotbarFlag6 ? 0.0F : planValuesItem, 0.0F};
               this.hudDefaultsMap.put(botCrystalText, hotbarValues);
            }

            hotbarValues[0] = hotbarValue2;
            hotbarValues[1] = planValuesItem;
            if (Math.abs(hotbarValue2) >= 0.05F || Math.abs(planValuesItem) >= 0.05F) {
               hotbarValues[4] = this.hotbarGroupCounts[r];
            }

            hotbarValues[2] += (hotbarValue2 - hotbarValues[2]) * hotbarFactor;
            hotbarValues[3] += (planValuesItem - hotbarValues[3]) * hotbarFactor;
            if (Math.abs(hotbarValue2 - hotbarValues[2]) < 0.05F) {
               hotbarValues[2] = hotbarValue2;
            }

            if (Math.abs(planValuesItem - hotbarValues[3]) < 0.05F) {
               hotbarValues[3] = planValuesItem;
            }

            if (hotbarValues[2] == 0.0F && hotbarValues[3] == 0.0F && hotbarValue2 == 0.0F && planValuesItem == 0.0F) {
               this.hudDefaultsMap.remove(botCrystalText);
            }
         }

         if (!this.hudDefaultsMap.isEmpty()) {
            var iterator = this.hudDefaultsMap.entrySet().iterator();

            while (iterator.hasNext()) {
               String hotbarText4 = (String)((Entry)iterator.next()).getKey();
               boolean hotbarFlag7 = false;

               for (int t = 0; t < value && !hotbarFlag7; t++) {
                  hotbarFlag7 = hotbarText4.equals(this.hotbarTexts[t]);
               }

               if (!hotbarFlag7) {
                  iterator.remove();
               }
            }
         }

         System.arraycopy(this.hotbarTexts, 0, this.hudTexts, 0, value);
         this.hotbarVisibleCount = value;
      }
   }

   private float getAmount3() {
      return 174.0F;
   }

   private void handleSegsAmounts(VertexTexturePeek vertexTexturePeek, int index, Identifier id, String segsText) {
      this.h2Segs[index] = new ShowHUD.H2Seg(id, segsText, -1, true, false);
      this.tpsSignalTable[index] = getAmount(vertexTexturePeek, this.h2Segs[index]);
   }

   private static boolean canFlag(float hudValue, float hashHuntBound, float hudValue3, float hudValue4, float hudValue5, float hudValue6, float hudValue7, float hudValue8) {
      float hudValue9 = 5.5F;
      return hudValue < hudValue5 + hudValue7 + hudValue9 && hudValue5 < hudValue + hudValue3 + hudValue9 && hashHuntBound < hudValue6 + hudValue8 + hudValue9 && hudValue6 < hashHuntBound + hudValue4 + hudValue9;
   }

   public static void update() {
      ShowHUD showHUD = getShowHUD();
      if (showHUD != null) {
         showHUD.updateHudDefaultsHudOffsetsMap();
      }
   }

   private void render(DrawContext context) {
      Module iliIiiliiiii = ModuleRegistry.getModule(RegionMap.class);
      if (iliIiiliiiii instanceof RegionMap regionMap && iliIiiliiiii.isEnabled()) {
         regionMap.renderScaledWindow(context);
      }

      Module module3 = ModuleRegistry.getModule(SpotifyHUD.class);
      if (module3 instanceof SpotifyHUD spotifyHUD && module3.isEnabled()) {
         spotifyHUD.renderScaledWidth(context);
      }

      Module module4 = ModuleRegistry.getModule(TotemCounter.class);
      if (module4 instanceof TotemCounter totemCounter && module4.isEnabled()) {
         totemCounter.renderScaledClassic(context);
      }

      Module module5 = ModuleRegistry.getModule(AutoElytraFinder.class);
      if (module5 instanceof AutoElytraFinder autoElytraFinder && module5.isEnabled()) {
         autoElytraFinder.renderStatusElytra(context);
      }

      Module module6 = ModuleRegistry.getModule(AutoTpaHere.class);
      if (module6 instanceof AutoTpaHere autoTpaHere && module6.isEnabled()) {
         autoTpaHere.renderStatusCard(context);
      }

      Module module7 = ModuleRegistry.getModule(AutoElytraPilot.class);
      if (module7 instanceof AutoElytraPilot autoElytraPilot && module7.isEnabled()) {
         autoElytraPilot.renderPilotStatus(context);
      }

      Module module8 = ModuleRegistry.getModule(AnchorHunter.class);
      if (module8 instanceof AnchorHunter anchorHunter && module8.isEnabled()) {
         anchorHunter.renderStatusCard(context);
      }

      Module module9 = ModuleRegistry.getModule(Bloodhound.class);
      if (module9 instanceof Bloodhound bloodhound && module9.isEnabled()) {
         bloodhound.renderStatusCard(context);
      }

      HotbarHud.updateHotbarAmount();
   }

   private int getMaxInt() {
      try {
         ClientPlayNetworkHandler networkHandler = mc.getNetworkHandler();
         if (networkHandler != null && mc.player != null) {
            PlayerListEntry playerListEntry = networkHandler.getPlayerListEntry(mc.player.getUuid());
            int hudLimit = playerListEntry != null ? playerListEntry.getLatency() : -1;
            if (hudLimit > 0) {
               return hudLimit;
            } else {
               String hudText = mc.player.getGameProfile() != null ? mc.player.getGameProfile().name() : null;
               if (hudText == null || hudText.isEmpty()) {
                  try {
                     hudText = mc.getSession().getUsername();
                  } catch (Throwable e) {
                  }
               }

               if (hudText != null) {
                  for (PlayerListEntry getPlayerItem : networkHandler.getPlayerList()) {
                     GameProfile profile = getPlayerItem.getProfile();
                     if (profile != null && hudText.equalsIgnoreCase(profile.name())) {
                        return Math.max(0, getPlayerItem.getLatency());
                     }
                  }
               }

               return Math.max(0, hudLimit);
            }
         } else {
            return 0;
         }
      } catch (Throwable e2) {
         return 0;
      }
   }

   private static String[] getTexts() {
      String[] strings = new String[100];

      for (int i = 2; i < 100; i++) {
         strings[i] = String.valueOf(i);
      }

      return strings;
   }

   private void handleForQuintScissorFlag(QuintScissor quintScissor, boolean hudFlag) {
      double hudValue = hudFlag ? 1.0 : 0.0;
      if (Math.abs(quintScissor.getTargetAmount() - hudValue) > 0.01) {
         quintScissor.handleAmountTime(hudValue);
      }
   }

   private static int getAppleGolden(ItemStack stack) {
      if (stack == null || stack.isEmpty()) {
         return -1;
      } else if (stack.isOf(Items.GOLDEN_APPLE) || stack.isOf(Items.ENCHANTED_GOLDEN_APPLE)) {
         return 0;
      } else if (stack.isOf(Items.TOTEM_OF_UNDYING)) {
         return 1;
      } else if (stack.isOf(Items.END_CRYSTAL)) {
         return 2;
      } else {
         return stack.isOf(Items.EXPERIENCE_BOTTLE) ? 3 : -1;
      }
   }

   private static String formatText(boolean hudFlag) {
      if (hudFlag) {
         return "";
      } else {
         double hudLimit = DoneMaxUtils.getAmount();
         return hudLimit > 0.5 ? "~" + DoneMaxUtils.formatDone(hudLimit) : "";
      }
   }

   public static float getHotbarOffsets() {
      ShowHUD singleton2 = singleton;
      if (singleton2 != null && !singleton2.hotbarHudMap.isEmpty()) {
         ShowHUD.HudScale hotbarhudmap = singleton2.hotbarHudMap.get("hotbar");
         return hotbarhudmap != null && hotbarhudmap.hotbarOffsetsAmount != 1.0F && !singleton2.canHudOffsets("hotbar")
            ? HotbarHud.getHotbarAmount() * (hotbarhudmap.hotbarOffsetsAmount - 1.0F)
            : 0.0F;
      } else {
         return 0.0F;
      }
   }

   private static String[] getTextsStringArray() {
      String[] strings = new String[41];

      for (int i = 0; i <= 40; i++) {
         strings[i] = i + " CPS";
      }

      return strings;
   }

   private List<Module> getAlphabeticalCategory() {
      var list = HudShowUtils.getItems(ModuleRegistry.getModules());
      String formatText = this.sortEnum.getCurrentOption();

      return switch (formatText) {
         case "Alphabetical" -> list.stream().sorted(Comparator.comparing(Module::getCategory)).toList();
         case "Length" -> list.stream()
            .sorted(
               (ilIllIiIll2, ilIllIiIll3) -> Integer.compare(
                  (int)HudFontUtils.getLargeFont().getAbcdefghijklmnopqrstuvwxyzCorzfont(ilIllIiIll3.getCategory()),
                  (int)HudFontUtils.getLargeFont().getAbcdefghijklmnopqrstuvwxyzCorzfont(ilIllIiIll2.getCategory())
               )
            )
            .toList();
         case "Category" -> list.stream().sorted(Comparator.comparing(Module::getPageType).thenComparing(Module::getCategory)).toList();
         default -> list;
      };
   }

   private void renderTimerCooldowns(DrawContext context, float timerCooldownsValue, float timerCooldownsValue2, long nanoTime) {
      if (this.lastNanoTimeTimerCooldowns == 0L || nanoTime - this.lastNanoTimeTimerCooldowns >= 50000000L) {
         this.lastNanoTimeTimerCooldowns = nanoTime;
         this.updateTimerCooldowns();
      }

      float shieldAmountOffset = this.shieldAmount + 5.0F;
      float rowFactor = 1.0F - (float)Math.exp(-this.frameDeltaSeconds / 0.06F);
      float timerCooldownsTotal = 0.0F;
      float timerCooldownsBound = 0.0F;

      for (int i = 0; i < this.cooldownsTimerItems.size(); i++) {
         ShowHUD.NCd cooldownstimeritem = this.cooldownsTimerItems.get(i);
         if (cooldownstimeritem.canAmountFlag(rowFactor)) {
            this.timerCooldownsNCdMap.remove(cooldownstimeritem.itemIdentifier);
            this.cooldownsTimerItems.remove(i--);
         } else {
            timerCooldownsTotal += 15.0F * cooldownstimeritem.personOnlineAmount;
            if (!cooldownstimeritem.durationActive) {
               timerCooldownsBound = Math.max(timerCooldownsBound, shieldAmountOffset + cooldownstimeritem.nCdAmount + 10.0F + cooldownstimeritem.field.checkedRocketsAmount);
            }
         }
      }

      if (!this.timerCooldownsPop.canTimeAmount(this.timerCooldownsValue > 0, ssaPerfgraphCount)) {
         this.timerCooldownsSmooth.updateMax();
      } else {
         float hudFactor = this.timerCooldownsSmooth
            .getAmount(
               (float)Math.ceil(Math.max(120.0F, Math.max(NativeHud.getAmount2(this.cooldownsStaffAmount, this.timer.checkedRocketsAmount), timerCooldownsBound + 14.0F))),
               this.frameDeltaSeconds,
               0.08F
            );
         float maxRadarSize = 30.0F + timerCooldownsTotal + 5.0F;
         float timerCooldownsValue6 = timerCooldownsValue + this.coordsOffsetX;
         float hudScaleValue = timerCooldownsValue2 + this.coordsOffsetY;
         this.handleTextHud(timerCooldownsValue6, hudScaleValue, hudFactor, maxRadarSize);
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(this.timerCooldownsPop.getPopProgress());
         boolean translateMatrices = NativeHud.canTranslateMatrices(context, timerCooldownsValue6 + hudFactor / 2.0F, hudScaleValue + maxRadarSize / 2.0F, this.timerCooldownsPop.getAmount());

         try {
            NativeHud.handle(NativeHud.timerCooldownsValue, timerCooldownsValue6, hudScaleValue, hudFactor, maxRadarSize, 1.0F);
            float timerCooldownsValue8 = NativeHud.getAmount(
               context, timerCooldownsValue6, hudScaleValue, hudFactor, IcoGenUtils.getIdentifierForString("timer"), "Cooldowns", this.timer, this.timerCooldownsPulse, getExperienceProgress(timerCooldownsTotal / 8.0F)
            );
            float matrixMatricesValue = timerCooldownsValue6 + 7.0F;
            float nativeHudValue = timerCooldownsValue6 + hudFactor - 7.0F;

            for (int j = 0; j < this.cooldownsTimerItems.size(); j++) {
               ShowHUD.NCd cooldownstimeritem2 = this.cooldownsTimerItems.get(j);
               float personOnlineAmount = cooldownstimeritem2.personOnlineAmount;
               if (!(personOnlineAmount <= 0.002F)) {
                  float peekPositionFactor = 15.0F * personOnlineAmount;
                  float roundOffset = timerCooldownsValue8 + cooldownstimeritem2.rowHeight;
                  float panelOverlay2 = CorzRoundedUtils.getPanelOverlay(personOnlineAmount * personOnlineAmount);

                  try {
                     NativeHud.renderMatrixMatrices(
                        context, cooldownstimeritem2.itemStack, matrixMatricesValue, roundOffset + NativeHud.getAmount4((peekPositionFactor - this.shieldAmount) / 2.0F), this.shieldAmount
                     );
                     NativeHud.handle(cooldownstimeritem2.timerCooldownsText, matrixMatricesValue + shieldAmountOffset, roundOffset, peekPositionFactor, NativeHud.OFF_WHITE);
                     NativeHud.handleForFieldFloat(cooldownstimeritem2.field, nativeHudValue, roundOffset, peekPositionFactor, NativeHud.OFF_WHITE);
                  } finally {
                     CorzRoundedUtils.handlePanelOverlay(panelOverlay2);
                  }
               }
            }
         } finally {
            NativeHud.renderPopMatrices(context, translateMatrices);
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
         }
      }
   }

   private static float getAmount(VertexTexturePeek vertexTexturePeek, ShowHUD.H2Seg h2Seg) {
      return getIcon(vertexTexturePeek, h2Seg, 1.0F);
   }

   public static float getAmount4(float hudValue) {
      return 20.0F * hudValue;
   }

   public static void handleAmount(float hudValue, float hudValue2) {
      ShowHUD showHUD = getShowHUD();
      if (showHUD != null) {
         showHUD.anchorCenterX += hudValue;
         showHUD.hudCount += hudValue2;
      }
   }

   private static float getAmount5(float hudValue) {
      return Math.round(hudValue * 2.0F) / 2.0F;
   }

   private static int getValue(int hudValue, int hudBound) {
      return Math.max(0, Math.min(255, hudBound)) << 24 | hudValue & 16777215;
   }

   private void handleDefaultsHud(String defaultsHudText, float defaultsHudValue, float defaultsHudValue2) {
      float[] huddefaultsmap = this.hudDefaultsMap.get(defaultsHudText);
      if (huddefaultsmap == null) {
         huddefaultsmap = new float[5];
         this.hudDefaultsMap.put(defaultsHudText, huddefaultsmap);
      }

      huddefaultsmap[2] += defaultsHudValue;
      huddefaultsmap[3] += defaultsHudValue2;
   }

   private float[] getScaledHud(String scaledHudText) {
      float[] hudoffsetsmap = this.hudOffsetsMap.get(scaledHudText);
      if (!scaledHudText.equals(this.dragReleaseText)) {
         float[] hudmodulesmap = this.hudModulesMap.get(scaledHudText);
         float[] moduleshudmap = this.modulesHudMap.get(scaledHudText);
         if (hudmodulesmap != null && moduleshudmap != null && this.scaledWidth > 0 && this.scaledHeight > 0) {
            float scaledHudFactor = moduleshudmap[2];
            float scaledHudFactor2 = moduleshudmap[3];
            ShowHUD.HudScale hudScale = this.hotbarHudMap.isEmpty() ? null : this.hotbarHudMap.get(scaledHudText);
            float scaledHudFactor3 = hudScale == null ? 1.0F : hudScale.getOffsetsHotbar();
            float scaledHudValue;
            float scaledHudValue2;
            if (scaledHudFactor3 == 1.0F) {
               scaledHudValue = getAmount(getAmount(hudmodulesmap[0], hudmodulesmap[2], this.scaledWidth, scaledHudFactor), 0.0F, Math.max(0.0F, this.scaledWidth - scaledHudFactor));
               scaledHudValue2 = getAmount(getAmount(hudmodulesmap[1], hudmodulesmap[3], this.scaledHeight, scaledHudFactor2), 0.0F, Math.max(0.0F, this.scaledHeight - scaledHudFactor2));
            } else {
               float scaledHudValue3 = scaledHudFactor * scaledHudFactor3;
               float scaledHudValue4 = scaledHudFactor2 * scaledHudFactor3;
               scaledHudValue = getAmount(getAmount(hudmodulesmap[0], hudmodulesmap[2], this.scaledWidth, scaledHudValue3), 0.0F, Math.max(0.0F, this.scaledWidth - scaledHudValue3))
                  + getAmountForFloat(hudmodulesmap[0]) * (scaledHudValue3 - scaledHudFactor);
               scaledHudValue2 = getAmount(getAmount(hudmodulesmap[1], hudmodulesmap[3], this.scaledHeight, scaledHudValue4), 0.0F, Math.max(0.0F, this.scaledHeight - scaledHudValue4))
                  + getAmountForFloat(hudmodulesmap[1]) * (scaledHudValue4 - scaledHudFactor2);
            }

            float scaledHudValue5 = scaledHudValue - moduleshudmap[0];
            float scaledHudValue6 = scaledHudValue2 - moduleshudmap[1];
            if (hudoffsetsmap == null || hudoffsetsmap[0] != scaledHudValue5 || hudoffsetsmap[1] != scaledHudValue6) {
               this.hudOffsetsMap.put(scaledHudText, new float[]{scaledHudValue5, scaledHudValue6});
            }

            this.scaledHudTable[0] = scaledHudValue5;
            this.scaledHudTable[1] = scaledHudValue6;
            return this.scaledHudTable;
         }
      }

      if (hudoffsetsmap != null) {
         this.scaledHudTable[0] = hudoffsetsmap[0];
         this.scaledHudTable[1] = hudoffsetsmap[1];
      } else {
         this.scaledHudTable[0] = 0.0F;
         this.scaledHudTable[1] = 0.0F;
      }

      return this.scaledHudTable;
   }

   private void handleTextsInventory(long inventoryValue, boolean inventoryFlag) {
      Arrays.fill(this.inventoryTable, 0);

      try {
         PlayerInventory inventory = mc.player.getInventory();

         for (int i = 0; i <= 40; i++) {
            if (i < 36 || i >= 40) {
               ItemStack stack = inventory.getStack(i);
               int appleGolden = getAppleGolden(stack);
               if (appleGolden >= 0) {
                  this.inventoryTable[appleGolden] = this.inventoryTable[appleGolden] + stack.getCount();
               }
            }
         }
      } catch (Throwable e) {
      }

      for (int j = 0; j < 4; j++) {
         int inventoryLimit = this.inventoryTable[j];
         if (inventoryLimit > this.inventoryCounts[j] && !inventoryFlag) {
            this.inventoryChangeTimes[j] = inventoryValue;
         }

         if (inventoryLimit != this.inventoryCounts[j] || this.inventoryTexts[j].isEmpty()) {
            this.inventoryTexts[j] = formatTextForInt(inventoryLimit);
            this.backpackTopbarTable[j] = NativeHud.getAmount3(this.inventoryTexts[j], 0.82F);
         }

         this.inventoryCounts[j] = inventoryLimit;
      }
   }

   private void handleBottomLeftForStringHudScale(String bottomLeftText, ShowHUD.HudScale hudScale, float bottomLeftValue, float hudScaleValue, float bottomFactor, float radarSsaFactor) {
      switch (bottomLeftText) {
         case "modules":
         case "staff":
            this.bottomAmount = 1.0F;
            this.bottomLeftAmount = 0.0F;
            break;
         case "coords":
            this.bottomAmount = 0.0F;
            this.bottomLeftAmount = 1.0F;
            break;
         case "armor":
         case "brand":
            this.bottomAmount = 1.0F;
            this.bottomLeftAmount = 1.0F;
            break;
         case "vitals":
         case "totems":
         case "hotbar":
            this.bottomAmount = 0.5F;
            this.bottomLeftAmount = 1.0F;
            break;
         case "target":
         case "buildprogress":
         case "clock":
         case "spotify":
         case "elytrafinder":
         case "elytrapilot":
         case "autotpahere":
         case "anchorhunter":
            this.bottomAmount = 0.5F;
            this.bottomLeftAmount = 0.0F;
            break;
         case "topbar":
         case "perf":
         case "keybinds":
         case "resources":
         case "inventory":
         case "keystrokes":
         case "perfgraph":
         case "potions":
            this.bottomAmount = 0.0F;
            this.bottomLeftAmount = 0.0F;
            break;
         case "radar":
            boolean perfgraphSsa = canPerfgraphSsa(potionsSsaTime);
            String formatText = this.enumHelper.getCurrentOption();
            switch (formatText) {
               case "Top Left":
                  this.bottomAmount = 0.0F;
                  this.bottomLeftAmount = 0.0F;
                  return;
               case "Top Right":
                  this.bottomAmount = 1.0F;
                  this.bottomLeftAmount = 0.0F;
                  return;
               case "Bottom Left":
                  this.bottomAmount = 0.0F;
                  this.bottomLeftAmount = 1.0F;
                  return;
               case "Bottom Right":
                  this.bottomAmount = 1.0F;
                  this.bottomLeftAmount = 1.0F;
                  return;
               case "Left":
                  this.bottomAmount = 0.0F;
                  this.bottomLeftAmount = 0.5F;
                  return;
               case "Right":
                  this.bottomAmount = 1.0F;
                  this.bottomLeftAmount = 0.5F;
                  return;
               default:
                  this.bottomAmount = perfgraphSsa ? 0.0F : 1.0F;
                  this.bottomLeftAmount = perfgraphSsa ? 0.0F : 0.5F;
                  return;
            }
         default:
            if (hudScale != null && hudScale.bottomLeftActive) {
               this.bottomAmount = hudScale.hudScaleAmount;
               this.bottomLeftAmount = hudScale.bottomLeftAmount;
               return;
            }

            this.bottomAmount = bottomLeftValue + bottomFactor / 2.0F > this.scaledWidth / 2.0F ? 1.0F : 0.0F;
            this.bottomLeftAmount = hudScaleValue + radarSsaFactor / 2.0F > this.scaledHeight / 2.0F ? 1.0F : 0.0F;
            if (hudScale != null) {
               hudScale.bottomLeftActive = true;
               hudScale.hudScaleAmount = this.bottomAmount;
               hudScale.bottomLeftAmount = this.bottomLeftAmount;
            }
      }
   }

   public static float getHotbarHud(String hotbarHudText, float hotbarHudValue) {
      ShowHUD showHUD = getShowHUD();
      ShowHUD.HudScale hudScale = showHUD != null && !showHUD.hotbarHudMap.isEmpty() ? showHUD.hotbarHudMap.get(hotbarHudText) : null;
      return hudScale != null && hudScale.coordsPerf != 1.0F && hudScale.hotbarOffsetsAmount != 1.0F ? hudScale.getAmountForFloat(hotbarHudValue) : hotbarHudValue;
   }

   private static enum Anchor {
      TOP_LEFT,
      TOP_CENTER,
      TOP_RIGHT,
      MID_LEFT,
      MID_RIGHT,
      BOT_LEFT,
      BOT_CENTER,
      BOT_RIGHT;
   }

   private record H2Seg(Identifier icon, String text, int color, boolean mono, boolean brand) {
      H2Seg(Identifier text, String icon) {
         this(text, icon, -1, false, false);
      }
   }

   private static final class HudBox {
      final float boxHeight;
      final float hudAmount;
      final String dragResetText;
      final float dragResetAmount;
      final float hudBoxAmount;

      HudBox(String text, float amount, float amount2, float amount3, float amount4) {
         this.dragResetText = text;
         this.dragResetAmount = amount;
         this.hudAmount = amount2;
         this.hudBoxAmount = amount3;
         this.boxHeight = amount4;
      }
   }

   public static final class HudScale {
      private float hotbarOffsetsAmount;
      public float translateX;
      public float translateAmount;
      private long lastNanoTime;
      private int lastHotbarTotemsCount;
      private boolean bottomLeftActive;
      private float hudScaleFactor = 1.0F;
      private float bottomLeftAmount;
      public float coordsPerf;
      private float hudScaleAmount;

      public void handleAmountTranslate(MatrixStack matrices, float translateFactor) {
         matrices.push();
         matrices.peek().getPositionMatrix().identity();
         matrices.translate(this.translateX * translateFactor, this.translateAmount * translateFactor, 0.0F);
         matrices.scale(this.coordsPerf, this.coordsPerf, 1.0F);
         matrices.translate(-this.translateX * translateFactor, -this.translateAmount * translateFactor, 0.0F);
      }

      public void handleAmountTranslate(Matrix3x2fStack matrix3x2fStack) {
         matrix3x2fStack.translate(this.translateX, this.translateAmount);
         matrix3x2fStack.scale(this.coordsPerf, this.coordsPerf);
         matrix3x2fStack.translate(-this.translateX, -this.translateAmount);
      }

      public HudScale() {
         this.hotbarOffsetsAmount = 1.0F;
         this.lastHotbarTotemsCount = -1;
         this.coordsPerf = 1.0F;
      }

      public void handleAmountTranslate(MatrixStack matrices) {
         matrices.push();
         matrices.translate(this.translateX, this.translateAmount, 0.0F);
         matrices.scale(this.coordsPerf, this.coordsPerf, 1.0F);
         matrices.translate(-this.translateX, -this.translateAmount, 0.0F);
      }

      private float getOffsetsHotbar() {
         if (this.lastHotbarTotemsCount == ShowHUD.hotbarTotemsCount) {
            return this.hotbarOffsetsAmount;
         } else {
            this.lastHotbarTotemsCount = ShowHUD.hotbarTotemsCount;
            long nanoTime = System.nanoTime();
            if (this.hotbarOffsetsAmount != this.hudScaleFactor) {
               float offsetsHotbarLimit = this.lastNanoTime == 0L ? 0.0F : (float)(nanoTime - this.lastNanoTime) / 1.0E9F;
               if (offsetsHotbarLimit > 0.25F) {
                  this.hotbarOffsetsAmount = this.hudScaleFactor;
               } else {
                  this.hotbarOffsetsAmount = this.hotbarOffsetsAmount + (this.hudScaleFactor - this.hotbarOffsetsAmount) * (1.0F - (float)Math.exp(-offsetsHotbarLimit / 0.045F));
                  if (Math.abs(this.hudScaleFactor - this.hotbarOffsetsAmount) < 0.0015F) {
                     this.hotbarOffsetsAmount = this.hudScaleFactor;
                  }
               }
            }

            this.lastNanoTime = nanoTime;
            return this.hotbarOffsetsAmount;
         }
      }

      public float getAmount(float hudScaleValue) {
         return this.translateX + (hudScaleValue - this.translateX) * this.coordsPerf;
      }

      public void handleAmountTranslateForMatrixStackFloat(MatrixStack matrices, float translateFactor) {
         matrices.push();
         matrices.translate(this.translateX * translateFactor, this.translateAmount * translateFactor, 0.0F);
         matrices.scale(this.coordsPerf, this.coordsPerf, 1.0F);
         matrices.translate(-this.translateX * translateFactor, -this.translateAmount * translateFactor, 0.0F);
      }

      public float getAmountForFloat(float hudScaleValue) {
         return this.translateAmount + (hudScaleValue - this.translateAmount) * this.coordsPerf;
      }
   }

   private static final class NCd extends Row {
      float timerCooldownsAmount;
      ItemStack itemStack;
      float nCdAmount;
      final Field field;
      String timerCooldownsText = "";
      final Identifier itemIdentifier;

      NCd(Identifier key) {
         this.field = new Field();
         this.itemIdentifier = key;
      }
   }

   private static final class NFlow {
      boolean coordsRadarActive;
      int lastRadarCount = -1;
   }

   private final class NList {
      ArrayList<ListRow> hudListRowList;
      final float textScale;
      final Map<String, ListRow> hudListRowMap = new HashMap<>();
      boolean keybindsKeystrokesFlag;
      float maxRowWidth;
      final float hudAmount;
      final boolean hudActive;
      float keybindsKeyboardAmount;
      ArrayList<ListRow> keybindsKeyboardListRowList = new ArrayList<>();

      NList(final float amount, float amount2, boolean flag) {
         this.hudListRowList = new ArrayList<>();
         this.hudAmount = amount;
         this.textScale = amount2;
         this.hudActive = flag;
      }

      void handleKeybindsKeyboard(float keybindsKeyboardValue) {
         this.keybindsKeyboardAmount = 0.0F;
         this.maxRowWidth = 0.0F;
         this.keybindsKeystrokesFlag = false;

         for (int i = 0; i < this.keybindsKeyboardListRowList.size(); i++) {
            ListRow listRow = this.keybindsKeyboardListRowList.get(i);
            if (listRow.canAmountFlag(this.keybindsKeyboardAmount, keybindsKeyboardValue)) {
               this.hudListRowMap.remove(listRow.keybindsKeyboardText);
               this.keybindsKeyboardListRowList.remove(i--);
            } else {
               this.keybindsKeyboardAmount = this.keybindsKeyboardAmount + listRow.getAmount();
               if (!listRow.keybindsKeyboardActive) {
                  this.keybindsKeystrokesFlag = true;
                  this.maxRowWidth = Math.max(this.maxRowWidth, listRow.keybindsKeyboardAmount);
               }
            }
         }
      }

      void handleShowHud(Module Module) {
         String hudText = Module.getCategory();
         ListRow listRow = this.hudListRowMap.get(hudText);
         if (listRow == null) {
            listRow = new ListRow(hudText, NativeHud.getAmount3(hudText, this.textScale), this.hudAmount);
            this.hudListRowMap.put(hudText, listRow);
         }

         listRow.updateAmountFlagKeybindsKeyboardActive();
         int value = Module.getBindKey();
         if (this.hudActive && value != listRow.lastShownValue) {
            listRow.lastShownValue = value;
            String formatMmbLmb = ShowHUD.this.formatMmbLmb(value);
            listRow.handleAmountText(formatMmbLmb, NativeHud.getAmount3(formatMmbLmb, 0.72F));
         }

         this.hudListRowList.add(listRow);
      }

      void updateFlag() {
         for (int i = 0; i < this.keybindsKeyboardListRowList.size(); i++) {
            ListRow listRow = this.keybindsKeyboardListRowList.get(i);
            if (listRow.keybindsKeyboardActive) {
               this.hudListRowList.add(Math.min(i, this.hudListRowList.size()), listRow);
            }
         }

         float hudTotal = 0.0F;

         for (int j = 0; j < this.hudListRowList.size(); j++) {
            ListRow listRow2 = this.hudListRowList.get(j);
            listRow2.handleAmountFlag(hudTotal);
            hudTotal += listRow2.getAmount();
         }

         var items = this.keybindsKeyboardListRowList;
         this.keybindsKeyboardListRowList = this.hudListRowList;
         this.hudListRowList = items;
      }

      void handleAmountText(float hudValue, float nativeHudValue, float whiteValue) {
         for (int i = 0; i < this.keybindsKeyboardListRowList.size(); i++) {
            ListRow listRow = this.keybindsKeyboardListRowList.get(i);
            float rowAmount = listRow.rowAmount;
            if (!(rowAmount <= 0.002F)) {
               float maxRadarSize = listRow.getAmount();
               float hudValue5 = hudValue + listRow.targetY + listRow.slideOffsetY;
               float panelOverlay = CorzRoundedUtils.getPanelOverlay(rowAmount * rowAmount);

               try {
                  NativeHud.handle(listRow.keybindsKeyboardText, nativeHudValue, hudValue5, maxRadarSize, this.textScale, NativeHud.OFF_WHITE);
                  if (listRow.badgeWidth > 0.0F) {
                     NativeHud.handleWhite(whiteValue, hudValue5, maxRadarSize, listRow.rowText, listRow.textAlpha, listRow.badgeWidth);
                  }
               } finally {
                  CorzRoundedUtils.handlePanelOverlay(panelOverlay);
               }
            }
         }
      }

      void update() {
         for (int i = 0; i < this.keybindsKeyboardListRowList.size(); i++) {
            this.keybindsKeyboardListRowList.get(i).updateAmountFlag();
         }

         this.hudListRowList.clear();
      }
   }

   private static final class NPot extends Row {
      String effectAmplifierText;
      float effectAmplifierAmount;
      String flaskPotionsText = "";
      int amplifier;
      int duration;
      final StatusEffect statusEffect;
      Identifier effectTexture;
      boolean effectAmplifierActive;
      float flaskPotionsAmount;
      final Field field;

      NPot(StatusEffect value) {
         this.effectAmplifierText = "";
         this.amplifier = -1;
         this.field = new Field();
         this.statusEffect = value;
      }
   }

   private static final class NStaff extends Row {
      float onlineNoneAmount;
      int personValue = -1;
      static final float PADDING_SMALL = 4.0F;
      static final float PADDING_LARGE = 8.0F;
      SkinTextures skinTextures;
      long startNanos;
      final String personText;
      static final float VALUE_2_5_F = 2.5F;
      int badgeState;

      NStaff(String personText) {
         this.personText = personText;
      }

      int getPersonTime(long personValue) {
         if (this.personValue < 0) {
            return 16777215;
         } else {
            float nativeHudValue = (float)(personValue - this.startNanos) / 2.2E8F;
            return StaffBadge.getValue(this.badgeState, this.personValue, NativeHud.getAmount5(nativeHudValue));
         }
      }

      void handlePersonTime(int personValue, long time) {
         if (this.personValue != personValue) {
            this.badgeState = this.personValue < 0 ? personValue : this.getPersonTime(time);
            this.personValue = personValue;
            this.startNanos = time;
         }
      }
   }
}
