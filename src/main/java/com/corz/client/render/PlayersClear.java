package com.corz.client.render;

import com.corz.client.config.PlayerSkinResolver;
import com.corz.client.gui.ClearEdit;
import com.corz.client.gui.ClearIcons;
import com.corz.client.gui.DisabledEnabled;
import com.corz.client.gui.MatrixPeekUtilsRender;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Window;
import org.joml.Matrix3x2fStack;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import com.corz.client.categories.misc.NameProtect;
import com.corz.client.utils.FriendListBase;

public class PlayersClear {
   private int friendsValue;
   private final List<PlayersClear.RowM> focusModeItems;
   private final MatrixStackUtilsAmount.Hits hintClearHits;
   private int friendsEmptyValue;
   private final ClearIcons clearIcons;
   private static final String CLEAR_ALL = "Clear all?";
   private static final String ROW = "row:";
   private final FriendListBase friendListBase;
   private long nanoTimeIconsClear;
   private String playersClearText;
   private String copiedFlashText;
   private float thumbDragStartAmount;
   private int empty;
   private long nanoTimeFlashArm;
   private long flashCopiedDeadline;
   private long lastNanoTime;
   private int friendsSearchValue;
   private boolean barOfflineFlag;
   private long clearArmDeadlineMs;
   private boolean thumbHintFlag;
   private String append;
   private boolean barFocusActive;
   private boolean thumbActive;
   private long removedFriendTime;
   private final MatrixStackUtilsAmount.Anims friendsPlayersAnims;
   private static final String MORE_LABEL = "more:";
   private final List<PlayersClear.Shown> playersMatchItems;
   private String hintText;
   private int scaledY;
   private final MatrixStackUtilsAmount.Entrance friendsPlayersEntrance;
   private float thumbAmount;
   private static final String PLAYERS_IGNORED_SELECTED = "Players ignored by selected modules";
   private int rebuildGeneration;
   private String clipboard;
   private float maxScrollOffset;
   private boolean friendsFlag;
   private final MatrixStackUtilsAmount.Anims hintClearAnims;
   private final Vector2f vectorTransformVector2f;
   private long hintTime;
   private int playersUserValue;
   private final MatrixStackUtilsAmount.Hits friendsPlayersHits = new MatrixStackUtilsAmount.Hits();
   private final MatrixStackUtilsAmount.Hits flashArmHits;
   private float barThumbAmount;
   private String hintIgnoredText;
   private final MatrixStackUtilsAmount.Entrance focusModeEntrance;
   private boolean playersClearActive;
   private static final String[] tabsOnlineTexts = new String[]{"All", "Online"};
   private long clearArmTime;
   private long lastNanoTimeFriendsPlayers;
   private int friendsValueFriendsPlayers;
   private double iconsClearAmount;
   private int scaledX;
   private boolean friendsPlayersActive;
   private final ClearEdit focusHintClearEdit;
   private boolean lastFriendsPlayersFlag;
   private long nanoTimeFocusHint;
   private static final String CARD = "card:";
   private static final String SEARCH_ENTER_PLAYER = "Search or enter player name...";
   private float barAddedAmount;
   private static final String ROWBTN = "rowbtn:";
   private int friendsTabsValue;
   private boolean modePlayersFlag;
   private static final int UNUSED_ONE = 1;
   private int friendsPlayersValue;
   private static final String CLEAR_ARM = "clear:arm";
   private double playersClearAmount;
   private int tabs;
   private boolean flashArmFlag;
   private boolean iconsClearActive;
   private boolean clearArmFlag;
   private boolean armConfirmFlag;
   private boolean focusEnterFlag;

   public void handleThumbAmount(double thumbValue, double minmaxValue, int button) {
      if (this.clipboard == null) {
         if (this.barFocusActive && !this.thumbActive && this.thumbHintFlag) {
            float thumbDivisor = 150.0F - this.thumbAmount;
            if (!(thumbDivisor <= 0.0F) && !(this.maxScrollOffset <= 0.0F)) {
               float thumbFactor = (float)(minmaxValue - this.playersClearAmount) / thumbDivisor;
               this.barAddedAmount = MatrixStackUtilsAmount.getAmount(getAmount(this.thumbDragStartAmount + thumbFactor * this.maxScrollOffset, 0.0F, this.maxScrollOffset));
            }
         }
      }
   }

   public void handleThumbHint(double thumbHintValue, double mouseReleasedValue, int button) {
      this.thumbHintFlag = false;
   }

   private static boolean isPressedWindow() {
      Window window = MinecraftClient.getInstance().getWindow();
      return !InputUtil.isKeyPressed(window, 75)
         ? false
         : InputUtil.isKeyPressed(window, 341) || InputUtil.isKeyPressed(window, 345) || InputUtil.isKeyPressed(window, 343) || InputUtil.isKeyPressed(window, 347);
   }

   private void handleFriendBase(String friendText) {
      this.handleFocusRow(friendText);
      this.friendListBase.handleForString(friendText);
   }

   private void renderOfflineTrash(DrawContext context, PlayersClear.RowM rowM, String offlineTrashText, float offlineTrashValue, float configsLikeValue, float materialFactor, float offlineTrashLimit, int matrixStackLimit, int mouseY) {
      VertexTextureFont fontRenderer = MatrixStackUtilsAmount.getFontRenderer();
      if (offlineTrashLimit > 0.004F) {
         MatrixPeekUtilsRender.renderMatrixPeek(
            context, offlineTrashValue, configsLikeValue, materialFactor, 28.0F, 6.0F, rowM.offlineTrashActive ? CardMatrixUtils.getReleaseRight(0.1 * offlineTrashLimit) : MatrixStackUtilsAmount.getValue(-1, 0.05F * offlineTrashLimit)
         );
      }

      MatrixStackUtilsAmount.renderReleaseRight(context, rowM.friendName, offlineTrashValue + 4.0F, configsLikeValue + 4.0F, 20.0F);
      float matrixStackValue = offlineTrashValue + 4.0F + 20.0F + 8.0F;
      float releaseRightValue = offlineTrashValue + materialFactor - 5.0F - 16.0F;
      float btnKeyOffset = configsLikeValue + 6.0F;
      MatrixStackUtilsAmount.handle(
         fontRenderer.formatText(NameProtect.getString(rowM.friendName), releaseRightValue - 6.0F - matrixStackValue), matrixStackValue, MatrixStackUtilsAmount.getAmount(configsLikeValue + 3.0F, 11.0F, 1.0F), -920587
      );
      boolean offlineTrashFlag = PlayerSkinResolver.isPlayerListed(rowM.friendName);
      MatrixStackUtilsAmount.getAmount(
         context, offlineTrashFlag, offlineTrashFlag ? "Online" : "Offline", matrixStackValue, MatrixStackUtilsAmount.getAmount(configsLikeValue + 14.0F, 11.0F, 0.82F), this.barOfflineFlag
      );
      if (rowM.offlineTrashActive) {
         MatrixStackUtilsAmount.renderReleaseRight(
            context,
            MatrixStackUtilsAmount.Hits.INSTANCE,
            this.hintClearAnims,
            offlineTrashText,
            IcoGenUtils.getIdentifierGEN_CORZCLIENT_MAP("plus"),
            releaseRightValue,
            btnKeyOffset,
            16.0F,
            12.0F,
            matrixStackLimit,
            mouseY,
            false,
            offlineTrashLimit,
            null
         );
      } else {
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(0.34F + 0.66F * offlineTrashLimit);
         boolean translatePush = MatrixStackUtilsAmount.canTranslatePush(context, releaseRightValue + 8.0F, btnKeyOffset + 8.0F, 0.86F + 0.14F * offlineTrashLimit);

         try {
            MatrixStackUtilsAmount.render(
               context, MatrixStackUtilsAmount.Hits.INSTANCE, this.hintClearAnims, offlineTrashText, IcoGenUtils.getIdentifierGEN_CORZCLIENT_MAP("trash"), releaseRightValue, btnKeyOffset, 16.0F, 12.0F, matrixStackLimit, mouseY, true, null
            );
         } finally {
            MatrixStackUtilsAmount.renderPopMatrices(context, translatePush);
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
         }
      }
   }

   private void updateBarFocus() {
      this.barFocusActive = false;
      this.thumbActive = false;
      this.append = "";
      this.focusEnterFlag = false;
      this.barThumbAmount = 0.0F;
      this.barAddedAmount = 0.0F;
      this.focusModeItems.clear();
      this.hintClearHits.update();
      this.hintClearAnims.handle(barFocusText -> !barFocusText.startsWith("row:") && !barFocusText.startsWith("rowbtn:"));
   }

   private void updateFriendsIcons() {
      this.friendsFlag = true;
      this.clearIcons.update();
      this.friendsPlayersHits.update();
      this.updateArmClearClearArmTime();
   }

   private void updateScaledMouse() {
      MinecraftClient instance = MinecraftClient.getInstance();
      Window window = instance.getWindow();
      this.scaledX = (int)instance.mouse.getScaledX(window);
      this.scaledY = (int)instance.mouse.getScaledY(window);
   }

   private void updateTextTime() {
      this.playersClearText = null;
      this.playersClearActive = true;
      this.nanoTimeIconsClear = System.nanoTime();
   }

   private static String formatText(String playersClearText, float playersClearValue) {
      VertexTextureFont fontRenderer = MatrixStackUtilsAmount.getFontRenderer();
      if (playersClearValue <= 0.0F) {
         return "";
      } else {
         String corzfontText = playersClearText;

         while (corzfontText.length() > 1 && fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont(corzfontText) > playersClearValue) {
            corzfontText = corzfontText.substring(1);
         }

         return corzfontText;
      }
   }

   public void render(DrawContext context, int playersClearValue, int mouseY) {
      this.renderFriendsPlayers(context, playersClearValue, mouseY);
      this.updateTimeText();
   }

   private void updateTimeText() {
      if (this.nanoTimeIconsClear != 0L) {
         long deltaNanotime = System.nanoTime() - this.nanoTimeIconsClear;
         if (deltaNanotime >= 70000000L) {
            String playersClearText = this.playersClearText;
            boolean playersClearActive3 = this.playersClearActive;
            this.playersClearText = null;
            this.playersClearActive = false;
            this.nanoTimeIconsClear = 0L;
            if (deltaNanotime <= 400000000L) {
               if (playersClearActive3) {
                  this.updateFocusHint();
               } else {
                  this.handleFlashArm(playersClearText);
               }
            }
         }
      }
   }

   private void updateFlashCopied() {
      this.clipboard = null;
      this.flashArmFlag = false;
      this.armConfirmFlag = false;
      this.copiedFlashText = null;
      this.flashArmHits.update();
   }

   private void updateFocusHint() {
      this.updateFriendsIcons();
      this.updateFlashCopied();
      this.barFocusActive = true;
      this.thumbActive = false;
      this.nanoTimeFocusHint = System.nanoTime();
      this.append = "";
      this.focusEnterFlag = true;
      this.focusHintClearEdit.updateTime();
      this.barThumbAmount = 0.0F;
      this.barAddedAmount = 0.0F;
      this.thumbHintFlag = false;
      this.clearArmFlag = false;
      this.updateHintText();
      this.hintClearAnims.handle("m:hint", 0.0F);
      this.hintClearAnims.handle("m:arm", 0.0F);
      this.hintClearAnims.handle("m:mode", 0.0F);
      this.hintClearAnims.handle("m:focus", 0.0F);
      this.focusModeItems.clear();
      this.modePlayersFlag = false;
      this.updateFocusMode();
   }

   private boolean canFlashArm(double flashArmValue, double iconsClearValue) {
      if (this.flashArmFlag) {
         return true;
      } else if (!MatrixStackUtilsAmount.canFlag(flashArmValue, iconsClearValue, getScaledWidth(220), getScaledHeight(124), 220.0F, 124.0F)) {
         this.updateFlashArm();
         return true;
      } else {
         this.flashArmHits.canAnimsPress(flashArmValue, iconsClearValue);
         return true;
      }
   }

   private void updateFriendsSaved() {
      long currentMillis = System.currentTimeMillis();
      if (currentMillis < this.clearArmTime) {
         if (currentMillis < this.clearArmTime - 2500L + 300L) {
            return;
         }

         this.updateArmClearClearArmTime();
         this.friendListBase.update();
         this.updateRootFriends();
         DisabledEnabled.getInstance().handleDisabledEnabled("Friends Cleared", "All saved players removed", false);
      } else {
         this.clearArmTime = currentMillis + 2500L;
      }
   }

   private void renderFlagArm(DrawContext context) {
      this.updateScaledMouse();
      float armLimit = getAmount(this.nanoTimeFocusHint, this.thumbActive);
      if (this.thumbActive && armLimit >= 1.0F) {
         this.updateBarFocus();
      } else {
         if (this.clearArmFlag && System.currentTimeMillis() > this.clearArmDeadlineMs) {
            this.clearArmFlag = false;
         }

         this.hintClearHits.update();
         int scaledWidth = getScaledWidth(280);
         int scaledHeight = getScaledHeight(277);
         this.renderScaledWidth(context, scaledWidth, scaledHeight, 280, 277, armLimit, this.thumbActive, () -> this.renderHintClear(context, scaledWidth, scaledHeight));
      }
   }

   public boolean canIconsClear(double iconsClearValue, double amount, int modulesValue) {
      if (modulesValue != 0) {
         return this.isFlashCopied();
      } else {
         this.friendsPlayersActive = true;
         if (this.clipboard != null) {
            return this.canFlashArm(iconsClearValue, amount);
         } else if (!this.barFocusActive) {
            if (this.nanoTimeIconsClear != 0L) {
               return true;
            } else {
               boolean iconsClearFlag = this.iconsClearActive && this.clearIcons.canMin(iconsClearValue, amount);
               this.clearIcons.canClearModules(iconsClearValue, amount, modulesValue);
               if (!this.iconsClearActive) {
                  this.clearIcons.update();
               }

               return iconsClearFlag ? true : this.friendsPlayersHits.canAnimsPress(iconsClearValue, amount);
            }
         } else if (this.thumbActive) {
            return true;
         } else if (!MatrixStackUtilsAmount.canFlag(iconsClearValue, amount, getScaledWidth(280), getScaledHeight(277), 280.0F, 277.0F)) {
            this.updateFlagThumb();
            return true;
         } else {
            this.iconsClearAmount = amount;
            this.hintClearHits.canAnimsPress(iconsClearValue, amount);
            return true;
         }
      }
   }

   private void updateFocusMode() {
      String lowerCase = this.append.trim().toLowerCase(Locale.ROOT);
      boolean modePlayersFlag = !lowerCase.isEmpty();
      LinkedHashMap linkedHashMap = new LinkedHashMap();
      if (modePlayersFlag) {
         MinecraftClient instance = MinecraftClient.getInstance();
         ClientPlayNetworkHandler networkHandler = instance.getNetworkHandler();
         if (networkHandler != null) {
            for (PlayerListEntry playerListEntry : networkHandler.getPlayerList()) {
               String focusModeText = playerListEntry.getProfile() != null ? playerListEntry.getProfile().name() : null;
               if (focusModeText != null && !focusModeText.isEmpty() && !this.friendListBase.canFlag(focusModeText) && focusModeText.toLowerCase(Locale.ROOT).contains(lowerCase)) {
                  linkedHashMap.putIfAbsent(focusModeText, true);
               }
            }
         }
      } else {
         for (String getItem : this.friendListBase.getItems()) {
            linkedHashMap.put(getItem, false);
         }
      }

      if (modePlayersFlag == this.modePlayersFlag && !this.focusModeItems.isEmpty()) {
         HashSet hashSet = new HashSet();

         for (String focusModeText3 : (Iterable<java.lang.String>) (linkedHashMap.keySet())) {
            hashSet.add(focusModeText3.toLowerCase(Locale.ROOT));
         }

         Iterator iterator = this.focusModeItems.iterator();

         while (iterator.hasNext()) {
            PlayersClear.RowM rowM = (PlayersClear.RowM)iterator.next();
            if (!rowM.focusModeActive && !hashSet.contains(rowM.lowerCase)) {
               if (!modePlayersFlag) {
                  rowM.focusModeActive = true;
                  rowM.lastNanoTime = System.nanoTime();
               } else {
                  iterator.remove();
               }
            }
         }

         HashSet hashSet2 = new HashSet();

         for (PlayersClear.RowM rowM2 : this.focusModeItems) {
            if (!rowM2.focusModeActive) {
               hashSet2.add(rowM2.lowerCase);
            }
         }

         for (Entry entry : (Iterable<java.util.Map.Entry>) (linkedHashMap.entrySet())) {
            String tickx = ((String)entry.getKey()).toLowerCase(Locale.ROOT);
            if (!hashSet2.contains(tickx)) {
               this.focusModeItems.add(new PlayersClear.RowM((String)entry.getKey(), (Boolean)entry.getValue(), -1, 0));
               hashSet2.add(tickx);
            }
         }
      } else {
         this.focusModeItems.clear();
         this.rebuildGeneration++;

         for (Entry entry2 : (Iterable<java.util.Map.Entry>) (linkedHashMap.entrySet())) {
            this.focusModeItems.add(new PlayersClear.RowM((String)entry2.getKey(), (Boolean)entry2.getValue(), this.rebuildGeneration, this.focusModeItems.size()));
         }

         this.focusModeEntrance.updateTimeFlag();
         this.modePlayersFlag = modePlayersFlag;
      }
   }

   public PlayersClear(FriendListBase friendListBase, int empty, int friendsTabsValue, int playersUserValue, int friendsSearchValue) {
      this.friendsPlayersAnims = new MatrixStackUtilsAmount.Anims();
      this.friendsPlayersEntrance = new MatrixStackUtilsAmount.Entrance();
      this.friendsValueFriendsPlayers = 0;
      this.friendsValue = -1;
      this.friendsPlayersValue = -1;
      this.friendsFlag = false;
      this.clearIcons = new ClearIcons(0, 0, 0, 18);
      this.playersMatchItems = new ArrayList<>();
      this.vectorTransformVector2f = new Vector2f();
      this.tabs = 0;
      this.friendsEmptyValue = 0;
      this.lastNanoTime = 0L;
      this.lastNanoTimeFriendsPlayers = 0L;
      this.iconsClearActive = false;
      this.lastFriendsPlayersFlag = false;
      this.friendsPlayersActive = false;
      this.clearArmTime = 0L;
      this.playersClearText = null;
      this.playersClearActive = false;
      this.nanoTimeIconsClear = 0L;
      this.flashArmHits = new MatrixStackUtilsAmount.Hits();
      this.clipboard = null;
      this.flashArmFlag = false;
      this.nanoTimeFlashArm = 0L;
      this.armConfirmFlag = false;
      this.removedFriendTime = 0L;
      this.copiedFlashText = null;
      this.flashCopiedDeadline = 0L;
      this.hintClearHits = new MatrixStackUtilsAmount.Hits();
      this.hintClearAnims = new MatrixStackUtilsAmount.Anims();
      this.focusModeEntrance = new MatrixStackUtilsAmount.Entrance();
      this.rebuildGeneration = 0;
      this.barFocusActive = false;
      this.thumbActive = false;
      this.nanoTimeFocusHint = 0L;
      this.append = "";
      this.focusEnterFlag = true;
      this.modePlayersFlag = false;
      this.focusHintClearEdit = new ClearEdit();
      this.hintText = null;
      this.hintIgnoredText = null;
      this.hintTime = 0L;
      this.clearArmFlag = false;
      this.clearArmDeadlineMs = 0L;
      this.barThumbAmount = 0.0F;
      this.barAddedAmount = 0.0F;
      this.thumbHintFlag = false;
      this.playersClearAmount = 0.0;
      this.thumbDragStartAmount = 0.0F;
      this.maxScrollOffset = 0.0F;
      this.thumbAmount = 0.0F;
      this.barOfflineFlag = true;
      this.iconsClearAmount = 0.0;
      this.focusModeItems = new ArrayList<>();
      this.friendListBase = friendListBase;
      this.empty = empty;
      this.friendsTabsValue = friendsTabsValue;
      this.playersUserValue = playersUserValue;
      this.friendsSearchValue = friendsSearchValue;
      this.clearIcons.handleModulesSearch("Search friends...");
      this.clearIcons.setModulesSearchFlag(false);
      this.clearIcons.setModulesSearchValue(6);
      this.updateFocusMode();
      this.updateRootFriends();
   }

   public void updateIconsClear() {
      this.clearIcons.update();
   }

   private void handleHintText(String hintText) {
      this.hintText = hintText;
      this.hintTime = System.currentTimeMillis() + 1600L;
   }

   private static boolean isHandleGlfw() {
      return GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), 0) == 1;
   }

   private void renderArmFlash(DrawContext context) {
      this.updateScaledMouse();
      float armFlashLimit = getAmount(this.nanoTimeFlashArm, this.flashArmFlag);
      if (this.flashArmFlag && armFlashLimit >= 1.0F) {
         this.updateFlashCopied();
      } else {
         if (this.armConfirmFlag && System.currentTimeMillis() > this.removedFriendTime) {
            this.armConfirmFlag = false;
         }

         String flashArmText = this.clipboard;
         this.flashArmHits.update();
         int scaledWidth = getScaledWidth(220);
         int scaledHeight = getScaledHeight(124);
         this.renderScaledWidth(context, scaledWidth, scaledHeight, 220, 124, armFlashLimit, this.flashArmFlag, () -> this.renderFlashArm(context, scaledWidth, scaledHeight, flashArmText));
      }
   }

   private void handleFlashArm(String copiedFlashText) {
      if (copiedFlashText != null) {
         this.updateFriendsIcons();
         this.clipboard = copiedFlashText;
         this.flashArmFlag = false;
         this.nanoTimeFlashArm = System.nanoTime();
         this.armConfirmFlag = false;
         this.copiedFlashText = null;
         this.hintClearAnims.handle("fp:flash", 0.0F);
         this.hintClearAnims.handle("fp:arm", 0.0F);
      }
   }

   public int getTime() {
      if (System.nanoTime() - this.lastNanoTime > 100000000L) {
         this.updateRootFriends();
      }

      return 28 + this.getMatchPlayers();
   }

   private void updateHintText() {
      this.hintText = null;
      this.hintTime = 0L;
   }

   public boolean isFlashCopied() {
      return this.barFocusActive || this.clipboard != null;
   }

   private void updateFlashArm() {
      if (this.clipboard != null && !this.flashArmFlag) {
         this.flashArmFlag = true;
         this.nanoTimeFlashArm = System.nanoTime();
         this.armConfirmFlag = false;
      }
   }

   private void updateArmClear() {
      long currentMillis = System.currentTimeMillis();
      if (!this.friendListBase.getItems().isEmpty()) {
         if (this.clearArmFlag && currentMillis <= this.clearArmDeadlineMs) {
            if (currentMillis < this.clearArmDeadlineMs - 2500L + 300L) {
               return;
            }

            this.friendListBase.update();
            this.clearArmFlag = false;
            this.updateFocusMode();
         } else {
            this.clearArmFlag = true;
            this.clearArmDeadlineMs = currentMillis + 2500L;
         }
      }
   }

   private void renderBarThumb(DrawContext context, int barThumbValue, int barThumbLimit, int w, int matrixStackLimit, int mouseY) {
      long nanoTime = System.nanoTime();
      this.focusModeEntrance.isTimeFlag();
      int focusmodeitemsSize = this.focusModeItems.size();
      float[] floats = new float[focusmodeitemsSize];
      float barThumbTotal = 0.0F;
      int barThumbCounter = 0;

      for (int i = 0; i < focusmodeitemsSize; i++) {
         PlayersClear.RowM focusmodeitem = this.focusModeItems.get(i);
         float barThumbValue4 = 1.0F;
         if (focusmodeitem.focusModeActive) {
            barThumbValue4 = 1.0F - MatrixStackUtilsAmount.getAmount3((float)(nanoTime - focusmodeitem.lastNanoTime) / 2.0E8F);
         } else {
            barThumbCounter++;
         }

         floats[i] = 30.0F * barThumbValue4;
         barThumbTotal += floats[i];
      }

      float max = Math.max(0.0F, barThumbTotal - 150.0F);
      this.barAddedAmount = getAmount(this.barAddedAmount, 0.0F, max);
      this.barThumbAmount = (float)MatrixIdentifierUtils.getAmount(
         this.barThumbAmount, this.barAddedAmount, this.thumbHintFlag ? 40.0 : 18.0, MatrixClearUtils.getAmount()
      );
      if (Math.abs(this.barThumbAmount - this.barAddedAmount) < 0.05F) {
         this.barThumbAmount = this.barAddedAmount;
      }

      this.barThumbAmount = getAmount(this.barThumbAmount, 0.0F, max);
      this.barOfflineFlag = this.barThumbAmount == this.barAddedAmount && barThumbCounter == focusmodeitemsSize;
      this.maxScrollOffset = max;
      float panelOverlayLimit = this.hintClearAnims.getAmount("m:bar", max > 0.0F ? 1.0F : 0.0F, 14.0F);
      float identifierText = w - 6.0F * panelOverlayLimit;
      if (barThumbCounter == 0) {
         if (this.modePlayersFlag) {
            MatrixStackUtilsAmount.renderMatricesPop(
               context,
               MatrixStackUtilsAmount.Hits.INSTANCE,
               this.hintClearAnims,
               "m:none",
               barThumbValue,
               barThumbLimit,
               w,
               150.0F,
               IcoGenUtils.getIdentifierGEN_CORZCLIENT_MAP("search"),
               "No online players match",
               "Press Enter to add the name anyway.",
               null,
               matrixStackLimit,
               mouseY,
               null
            );
         } else {
            MatrixStackUtilsAmount.renderMatricesPop(
               context,
               MatrixStackUtilsAmount.Hits.INSTANCE,
               this.hintClearAnims,
               "m:empty",
               barThumbValue,
               barThumbLimit,
               w,
               150.0F,
               IcoGenUtils.getIdentifierGEN_CORZCLIENT_MAP("user"),
               "No saved players yet",
               "Type a name above to add one.",
               null,
               matrixStackLimit,
               mouseY,
               null
            );
         }
      }

      int barThumbValue7 = mouseY >= barThumbLimit && mouseY <= barThumbLimit + 150 ? matrixStackLimit : -4000;
      context.enableScissor(barThumbValue - 2, barThumbLimit, barThumbValue + w + 2, barThumbLimit + 150);

      try {
         float barThumbTotal2 = barThumbLimit - this.barThumbAmount;

         for (int j = 0; j < focusmodeitemsSize; j++) {
            float rowBound = barThumbTotal2;
            barThumbTotal2 += floats[j];
            if (!(floats[j] <= 0.0F) && !(rowBound + 28.0F < barThumbLimit) && !(rowBound > barThumbLimit + 150)) {
               this.renderRowAmount(context, this.focusModeItems.get(j), barThumbValue, rowBound, identifierText, barThumbLimit, barThumbValue7, mouseY, nanoTime);
            }
         }
      } finally {
         context.disableScissor();
      }

      this.focusModeItems.removeIf(nowNs -> nowNs.focusModeActive && (float)(nanoTime - nowNs.lastNanoTime) >= 2.0E8F);
      if (max <= 0.0F) {
         this.thumbAmount = 0.0F;
      } else {
         float thumbAmount = MatrixStackUtilsAmount.getAmount(Math.max(18.0F, 22500.0F / barThumbTotal));
         float matrixStackValue = barThumbLimit + (150.0F - thumbAmount) * (this.barThumbAmount / max);
         if (this.barThumbAmount == this.barAddedAmount) {
            matrixStackValue = MatrixStackUtilsAmount.getAmount(matrixStackValue);
         }

         this.thumbAmount = thumbAmount;
         float matrixPeekValue = barThumbValue + w - 2;
         float cardMatrixFactor = this.hintClearAnims.getAmount("m:thumb", this.thumbHintFlag || MatrixStackUtilsAmount.canFlag(matrixStackLimit, mouseY, matrixPeekValue - 2.0F, barThumbLimit, 6.0F, 150.0F));
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(panelOverlayLimit);

         try {
            MatrixPeekUtilsRender.renderMatrixPeek(context, matrixPeekValue, barThumbLimit, 2.0F, 150.0F, 1.0F, MatrixStackUtilsAmount.getValue(-1, 0.05F));
            MatrixPeekUtilsRender.renderMatrixPeek(
               context,
               matrixPeekValue,
               matrixStackValue,
               2.0F,
               thumbAmount,
               1.0F,
               CardMatrixUtils.getValue(
                  MatrixStackUtilsAmount.getValue(-1, 0.24F), MatrixStackUtilsAmount.getValue(MatrixEspUtils.releaseRightValue, 0.92F), cardMatrixFactor
               )
            );
         } finally {
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
         }

         this.hintClearHits.handle(matrixPeekValue - 2.0F, barThumbLimit, 6.0F, 150.0F, this::updateThumbAmount);
      }
   }

   private static float getAmount(long playersClearValue, boolean playersClearFlag) {
      float playersClearDivisor = (playersClearFlag ? 130.0F : 240.0F) * 1000000.0F;
      return (float)(System.nanoTime() - playersClearValue) / playersClearDivisor;
   }

   private void updateCopiedFlash() {
      try {
         MinecraftClient.getInstance().keyboard.setClipboard(this.clipboard);
         this.copiedFlashText = "Copied";
      } catch (Exception e) {
         this.copiedFlashText = "Copy failed";
      }

      this.flashCopiedDeadline = System.currentTimeMillis() + 1600L;
   }

   private void updateArmClearClearArmTime() {
      this.clearArmTime = 0L;
      this.friendsPlayersAnims.handle("clear:arm", 0.0F);
   }

   private static void render(DrawContext context, String playersClearText, float playersClearValue, float sideBuildingValue, float sideWalkAmount, float panelOverlayLimit) {
      if (!(panelOverlayLimit <= 0.002F)) {
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(panelOverlayLimit);

         try {
            MatrixStackUtilsAmount.renderRoot(context, playersClearText, playersClearValue, sideBuildingValue, sideWalkAmount);
         } finally {
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
         }
      }
   }

   public boolean canBarAdded(double barAddedValue, double heightAmount, double keybindBindValue) {
      if (this.clipboard != null) {
         return true;
      } else if (this.barFocusActive && !this.thumbActive) {
         this.barAddedAmount = MatrixStackUtilsAmount.getAmount(getAmount(this.barAddedAmount - (float)keybindBindValue * 30.0F, 0.0F, this.maxScrollOffset));
         return true;
      } else {
         return false;
      }
   }

   private void renderOnlineDots(
      DrawContext context, MatrixStackUtilsAmount.Hits hits, PlayersClear.Shown shown, String onlineDotsText, int onlineDotsValue, int vectorTransformValue, int identifierText, int matrixStackLimit, int mouseY
   ) {
      VertexTextureFont fontRenderer = MatrixStackUtilsAmount.getFontRenderer();
      Runnable runnable = () -> this.handleTextTime(shown.name());
      boolean onlineDotsFlag = MatrixStackUtilsAmount.canFlag(matrixStackLimit, mouseY, onlineDotsValue, vectorTransformValue, identifierText, 36.0F);
      MatrixStackUtilsAmount.render(context, onlineDotsValue, vectorTransformValue, identifierText, 36.0F, this.friendsPlayersAnims.getAmount(onlineDotsText, onlineDotsFlag), 0.0F, 0.0F);
      hits.handleFlag(onlineDotsValue, vectorTransformValue, identifierText, 36.0F, runnable, this.friendsPlayersAnims, onlineDotsText);
      MatrixStackUtilsAmount.renderReleaseRight(context, shown.name(), onlineDotsValue + 6, vectorTransformValue + 6.0F, 24.0F);
      float matrixStackValue = onlineDotsValue + 6 + 24 + 8.0F;
      float onlineDotsValue7 = onlineDotsValue + identifierText - 6 - 16;
      String formatText = fontRenderer.formatText(NameProtect.getString(shown.name()), onlineDotsValue7 - 3.0F - matrixStackValue);
      MatrixStackUtilsAmount.handle(formatText, matrixStackValue, MatrixStackUtilsAmount.getAmount(vectorTransformValue + 7.0F, 11.0F, 1.0F), -920587);
      MatrixStackUtilsAmount.getAmount(
         context, shown.online(), shown.online() ? "Online" : "Offline", matrixStackValue, MatrixStackUtilsAmount.getAmount(vectorTransformValue + 18.0F, 11.0F, 0.82F)
      );
      MatrixStackUtilsAmount.render(
         context, hits, this.friendsPlayersAnims, "more:" + shown.key(), IcoGenUtils.getIdentifierGEN_CORZCLIENT_MAP("dots"), onlineDotsValue7, vectorTransformValue + 10.0F, 16.0F, 12.0F, matrixStackLimit, mouseY, false, runnable
      );
   }

   private void renderRowAmount(DrawContext context, PlayersClear.RowM rowM, float rowValue, float rowBound, float identifierText, int barThumbLimit, int rowValue4, int mouseY, long nanoTime) {
      float rowLimit = 0.0F;
      boolean rowFlag = true;
      boolean matrixScissorActive2 = true;
      float panelOverlayLimit;
      float rowFactor;
      if (rowM.rowGeneration == this.rebuildGeneration) {
         MatrixStackUtilsAmount.Entrance amountFlag = this.focusModeEntrance.getAmountFlag(rowM.rowValue);
         panelOverlayLimit = amountFlag.entranceProgress;
         rowFactor = amountFlag.matrixScissorAmount;
         rowLimit = amountFlag.tabsSettingsAmount;
         rowFlag = amountFlag.activeFlag;
         matrixScissorActive2 = amountFlag.matrixScissorActive;
      } else {
         float matrixStackValue = MatrixStackUtilsAmount.getMax((float)(nanoTime - rowM.nanoTimeRow) / 2.0E8F);
         panelOverlayLimit = MatrixStackUtilsAmount.getAmountForFloat(matrixStackValue / 0.7F);
         rowFactor = 0.965F + 0.035F * MatrixStackUtilsAmount.getAmount(matrixStackValue, 1.1F);
      }

      if (rowM.focusModeActive) {
         float max8 = MatrixStackUtilsAmount.getMax((float)(nanoTime - rowM.lastNanoTime) / 2.0E8F);
         panelOverlayLimit *= 1.0F - MatrixStackUtilsAmount.getMax(max8 / 0.5F);
         rowFactor *= 1.0F - 0.04F * max8;
         rowLimit = 0.0F;
         rowFlag = false;
         matrixScissorActive2 = false;
      }

      if (!(panelOverlayLimit <= 0.002F) || !(rowLimit <= 0.004F)) {
         String rowText = "row:" + rowM.lowerCase;
         String offlineTrashText = "rowbtn:" + rowM.lowerCase;
         int matrixStackLimit = rowFlag ? rowValue4 : -4000;
         float offlineTrashLimit = this.hintClearAnims.getAmount(rowText, MatrixStackUtilsAmount.canFlag(matrixStackLimit, mouseY, rowValue, rowBound, identifierText, 28.0F));
         boolean translatePush = MatrixStackUtilsAmount.canTranslatePush(context, rowValue + identifierText / 2.0F, rowBound + 14.0F, rowFactor * this.hintClearAnims.getTime(rowText, 0.025F));

         try {
            if (rowLimit > 0.004F) {
               MatrixPeekUtilsRender.renderMatrixPeek(context, rowValue, rowBound, identifierText, 28.0F, 6.0F, MatrixStackUtilsAmount.getValue(-1, 0.1F * rowLimit));
            }

            if (panelOverlayLimit > 0.002F) {
               float panelOverlay = CorzRoundedUtils.getPanelOverlay(panelOverlayLimit);

               try {
                  this.renderOfflineTrash(context, rowM, offlineTrashText, rowValue, rowBound, identifierText, offlineTrashLimit, matrixStackLimit, mouseY);
               } finally {
                  CorzRoundedUtils.handlePanelOverlay(panelOverlay);
               }
            }
         } finally {
            MatrixStackUtilsAmount.renderPopMatrices(context, translatePush);
         }

         float maxTop = Math.max((float)barThumbLimit, rowBound);
         float min11 = Math.min((float)(barThumbLimit + 150), rowBound + 28.0F);
         if (matrixScissorActive2 && !(min11 <= maxTop)) {
            if (rowM.offlineTrashActive) {
               this.hintClearHits.handleFlag(rowValue, maxTop, identifierText, min11 - maxTop, () -> this.handleFriendBar(rowM.friendName), this.hintClearAnims, rowText);
            } else {
               this.hintClearHits.handleFlag(rowValue + identifierText - 16.0F - 9.0F, maxTop, 25.0F, min11 - maxTop, () -> this.handleFriendBase(rowM.friendName), this.hintClearAnims, offlineTrashText);
            }
         }
      }
   }

   private void updateAddedEnter() {
      String trim = this.append.trim();
      if (trim.isEmpty()) {
         this.handleHintText("Enter a player name");
      } else if (this.friendListBase.canFlag(trim)) {
         this.handleHintText("Already added");
      } else {
         this.friendListBase.handle3(trim);
         this.append = "";
         this.barAddedAmount = 0.0F;
         this.updateHintText();
         this.updateFocusMode();
         this.clearIcons.handleSubstringClear("");
         if (!PlayerSkinResolver.isPlayerListed(trim)) {
            this.tabs = 0;
         }
      }
   }

   public boolean canAppendIcons(int appendIconsValue, int scancode, int modifiers) {
      if (this.clipboard != null) {
         if (appendIconsValue == 256) {
            this.updateFlashArm();
         }

         return true;
      } else if (!this.barFocusActive) {
         if (!this.clearIcons.isClearModulesFlag()) {
            return false;
         } else {
            this.clearIcons.canClearSubstring(appendIconsValue, scancode, modifiers);
            this.updateRootFriends();
            return true;
         }
      } else if (this.thumbActive) {
         return true;
      } else if (appendIconsValue == 256) {
         this.updateFlagThumb();
         return true;
      } else if (appendIconsValue != 257 && appendIconsValue != 335) {
         if (this.focusEnterFlag && appendIconsValue == 259 && !this.append.isEmpty()) {
            this.append = this.append.substring(0, this.append.length() - 1);
            this.barAddedAmount = 0.0F;
            this.updateHintText();
            this.updateFocusMode();
            this.focusHintClearEdit.updateTime();
         }

         return true;
      } else {
         this.updateAddedEnter();
         return true;
      }
   }

   private void renderFriendsPlayers(DrawContext context, int friendsPlayersValue, int mouseY) {
      long nanoTime = System.nanoTime();
      if (this.lastNanoTimeFriendsPlayers != 0L && nanoTime - this.lastNanoTimeFriendsPlayers > 400000000L) {
         this.clearIcons.update();
      }

      this.lastNanoTimeFriendsPlayers = nanoTime;
      if (this.clearIcons.isClearModulesFlag() && isPressedWindow()) {
         this.clearIcons.update();
      }

      boolean flag = isHandleGlfw();
      if (flag && !this.lastFriendsPlayersFlag && !this.friendsPlayersActive) {
         this.clearIcons.update();
      }

      if (!flag) {
         this.friendsPlayersActive = false;
      }

      this.lastFriendsPlayersFlag = flag;
      this.updateRootFriends();
      this.friendsPlayersHits.update();
      boolean timeFlag = this.friendsPlayersEntrance.isTimeFlag();
      int friendsValue = this.tabs + 1;

      for (int i = 0; i < this.playersMatchItems.size(); i++) {
         friendsValue = friendsValue * 31 + this.playersMatchItems.get(i).key().hashCode();
      }

      if (timeFlag) {
         if (this.friendsFlag && friendsValue == this.friendsValueFriendsPlayers) {
            this.friendsPlayersEntrance.updateTimeFlagEntranceValue();
         } else {
            this.friendsPlayersEntrance.updateTimeFlag();
         }
      } else if (friendsValue != this.friendsValueFriendsPlayers) {
         if (this.tabs == this.friendsPlayersValue && !this.playersMatchItems.isEmpty() && this.playersMatchItems.size() <= this.friendsValue) {
            this.friendsPlayersEntrance.updateTimeFlagEntranceValue();
         } else {
            this.friendsPlayersEntrance.updateTimeFlag();
         }
      }

      this.friendsFlag = false;
      this.friendsValueFriendsPlayers = friendsValue;
      this.friendsValue = this.playersMatchItems.size();
      this.friendsPlayersValue = this.tabs;
      float amountForDrawContextString = MatrixStackUtilsAmount.getAmountForDrawContextString(context, "Friends", this.empty, this.friendsTabsValue, 22.0F);
      if (this.friendsEmptyValue > 0) {
         String valueOf = String.valueOf(this.friendsEmptyValue);
         float scaleValue = amountForDrawContextString + 5.0F;
         MatrixStackUtilsAmount.handlePushScale(valueOf, scaleValue, MatrixStackUtilsAmount.getAmount(this.friendsTabsValue, 22.0F, 0.82F) + 1.25F, 0.82F, -7630437);
         amountForDrawContextString = scaleValue + MatrixStackUtilsAmount.getAmount(valueOf, 0.82F);
      }

      float friendsPlayersValue5 = MatrixStackUtilsAmount.getAmount(
         context, this.friendsPlayersHits, this.friendsPlayersAnims, "tabs", tabsOnlineTexts, this.tabs, amountForDrawContextString + 10.0F, this.friendsTabsValue, 22.0F, friendsPlayersValue, mouseY, this::handleTabs
      );
      float friendsTabsValueOffset = this.friendsTabsValue + 2.0F;
      float friendsPlayersValue6 = MatrixStackUtilsAmount.getAmount(
         context,
         this.friendsPlayersHits,
         this.friendsPlayersAnims,
         "add",
         "Add",
         IcoGenUtils.getIdentifierGEN_CORZCLIENT_MAP("plus"),
         (float)(this.empty + this.playersUserValue),
         friendsTabsValueOffset,
         18.0F,
         friendsPlayersValue,
         mouseY,
         true,
         this::updateTextTime
      );
      if (this.friendsEmptyValue > 0) {
         friendsPlayersValue6 = this.getClearAll(context, friendsPlayersValue6 - 4.0F, friendsPlayersValue, mouseY);
      }

      int ceil7 = (int)Math.ceil(friendsPlayersValue5 + 8.0F);
      int friendsPlayersLimit = Math.min(130, (int)Math.floor(friendsPlayersValue6 - 8.0F) - ceil7);
      this.iconsClearActive = friendsPlayersLimit >= 56;
      if (this.iconsClearActive) {
         this.clearIcons.handleMin(ceil7, Math.round(friendsTabsValueOffset), friendsPlayersLimit, 18);
         this.clearIcons.renderModulesClear(context, friendsPlayersValue, mouseY, 0.0F);
      } else {
         this.clearIcons.update();
      }

      int downloadCommunityValue = this.friendsTabsValue + 22 + 6;
      if (this.friendsEmptyValue == 0) {
         MatrixStackUtilsAmount.renderMatricesPop(
            context,
            this.friendsPlayersHits,
            this.friendsPlayersAnims,
            "empty",
            this.empty,
            downloadCommunityValue,
            this.playersUserValue,
            120.0F,
            IcoGenUtils.getIdentifierGEN_CORZCLIENT_MAP("user"),
            "No saved players",
            "Add a player and your modules will never target them.",
            "Add player",
            friendsPlayersValue,
            mouseY,
            this::updateTextTime
         );
      } else if (this.playersMatchItems.isEmpty()) {
         MatrixStackUtilsAmount.renderMatricesPop(
            context,
            this.friendsPlayersHits,
            this.friendsPlayersAnims,
            "none",
            this.empty,
            downloadCommunityValue,
            this.playersUserValue,
            88.0F,
            IcoGenUtils.getIdentifierGEN_CORZCLIENT_MAP("search"),
            "No players match",
            this.formatTryDifferent(),
            null,
            friendsPlayersValue,
            mouseY,
            null
         );
      } else {
         boolean friendsPlayersFlag = true;

         for (int j = 0; j < this.playersMatchItems.size(); j++) {
            PlayersClear.Shown playersmatchitem = this.playersMatchItems.get(j);
            int matrixStackFactor = j % 3;
            int valueForInt = MatrixStackUtilsAmount.getValueForInt(this.empty, this.playersUserValue, 3, 4, matrixStackFactor);
            int materialFactor = MatrixStackUtilsAmount.getValue(this.empty, this.playersUserValue, 3, 4, matrixStackFactor);
            int vectorTransformValue = downloadCommunityValue + j / 3 * 40;
            if (matrixStackFactor == 0) {
               friendsPlayersFlag = this.canVectorTransform(context, vectorTransformValue);
            }

            if (friendsPlayersFlag) {
               MatrixStackUtilsAmount.Entrance amountFlag = this.friendsPlayersEntrance.getAmountFlag(j);
               if (!amountFlag.isAmount()) {
                  float tabsSettingsAmount = amountFlag.tabsSettingsAmount;
                  float entranceProgress = amountFlag.entranceProgress;
                  MatrixStackUtilsAmount.Hits hits = amountFlag.matrixScissorActive ? this.friendsPlayersHits : MatrixStackUtilsAmount.Hits.INSTANCE;
                  int matrixStackLimit = amountFlag.activeFlag ? friendsPlayersValue : this.empty - 4000;
                  String friendsPlayersText = "card:" + playersmatchitem.key();
                  boolean translatePush = MatrixStackUtilsAmount.canTranslatePush(
                     context, valueForInt + materialFactor / 2.0F, vectorTransformValue + 18.0F, amountFlag.matrixScissorAmount * this.friendsPlayersAnims.getTime(friendsPlayersText, 0.025F)
                  );

                  try {
                     if (tabsSettingsAmount > 0.004F) {
                        float panelOverlay = CorzRoundedUtils.getPanelOverlay(tabsSettingsAmount);

                        try {
                           MatrixStackUtilsAmount.render(context, valueForInt, vectorTransformValue, materialFactor, 36.0F, 0.0F, 0.0F, 0.0F);
                        } finally {
                           CorzRoundedUtils.handlePanelOverlay(panelOverlay);
                        }
                     }

                     if (entranceProgress > 0.002F) {
                        float panelOverlay2 = CorzRoundedUtils.getPanelOverlay(entranceProgress);

                        try {
                           this.renderOnlineDots(context, hits, playersmatchitem, friendsPlayersText, valueForInt, vectorTransformValue, materialFactor, matrixStackLimit, mouseY);
                        } finally {
                           CorzRoundedUtils.handlePanelOverlay(panelOverlay2);
                        }
                     }
                  } finally {
                     MatrixStackUtilsAmount.renderPopMatrices(context, translatePush);
                  }
               }
            }
         }
      }
   }

   private String formatTryDifferent() {
      boolean differentFlag = !this.clearIcons.getSubstring().trim().isEmpty();
      if (this.tabs == 1) {
         return differentFlag ? "Try a different name or switch to All." : "None of your saved players are on this server.";
      } else {
         return "Try a different name.";
      }
   }

   public boolean canIconsAppend(char ch, int iconsAppendValue) {
      if (this.clipboard != null) {
         return true;
      } else if (!this.barFocusActive) {
         if (!this.clearIcons.isClearModulesFlag()) {
            return false;
         } else {
            this.clearIcons.canClearSubstring(ch, iconsAppendValue);
            this.updateRootFriends();
            return true;
         }
      } else if (this.thumbActive) {
         return true;
      } else {
         if (this.focusEnterFlag && canFlag(ch)) {
            this.append = this.append + ch;
            this.barAddedAmount = 0.0F;
            this.updateHintText();
            this.updateFocusMode();
            this.focusHintClearEdit.updateTime();
         }

         return true;
      }
   }

   private void handleFriendBar(String friendBarText) {
      this.friendListBase.handle3(friendBarText);
      this.append = "";
      this.barAddedAmount = 0.0F;
      this.updateFocusMode();
      this.clearIcons.handleSubstringClear("");
   }

   private void handleTextTime(String text) {
      this.playersClearText = text;
      this.playersClearActive = false;
      this.nanoTimeIconsClear = System.nanoTime();
   }

   private static int getScaledWidth(int scaledWidthValue) {
      return (MinecraftClient.getInstance().getWindow().getScaledWidth() - scaledWidthValue) / 2;
   }

   private void renderHintClear(DrawContext context, int hintClearValue, int scaledHeight) {
      VertexTextureFont fontRenderer = MatrixStackUtilsAmount.getFontRenderer();
      int matrixStackLimit = this.thumbActive ? -4000 : this.scaledX;
      int scaledY = this.scaledY;
      int topOffset = hintClearValue + 12;
      short sideWalkAmount = 256;
      int matrixStackValue = topOffset + sideWalkAmount;
      int itemsSize = this.friendListBase.getItems().size();
      int leftOffset = scaledHeight + 12;
      float amountForDrawContextString = MatrixStackUtilsAmount.getAmountForDrawContextString(context, "Friends", topOffset, leftOffset, 16.0F);
      MatrixStackUtilsAmount.handlePushScale(itemsSize + " saved", amountForDrawContextString + 6.0F, MatrixStackUtilsAmount.getAmount(leftOffset, 16.0F, 0.82F) + 1.25F, 0.82F, -7630437);
      MatrixStackUtilsAmount.render(
         context, this.hintClearHits, this.hintClearAnims, "m:x", IcoGenUtils.getCrossIco(), matrixStackValue - 16, leftOffset + 0.0F, 16.0F, 10.0F, matrixStackLimit, scaledY, false, this::updateFlagThumb
      );
      boolean hintClearFlag = this.hintText != null && System.currentTimeMillis() < this.hintTime;
      if (hintClearFlag) {
         this.hintIgnoredText = this.hintText;
      }

      float hintClearLimit = this.hintClearAnims.getAmount("m:hint", hintClearFlag ? 1.0F : 0.0F, 16.0F);
      float hintClearValue7 = MatrixStackUtilsAmount.getAmount(leftOffset + 16, 10.0F, 0.82F);
      if (hintClearLimit < 0.998F) {
         MatrixStackUtilsAmount.handlePushScale(
            "Players ignored by selected modules", topOffset, hintClearValue7 - 3.0F * hintClearLimit, 0.82F, MatrixStackUtilsAmount.getValue(-7630437, 1.0F - hintClearLimit)
         );
      }

      if (hintClearLimit > 0.002F && this.hintIgnoredText != null) {
         MatrixStackUtilsAmount.handlePushScale(
            this.hintIgnoredText, topOffset, hintClearValue7 + 3.0F * (1.0F - hintClearLimit), 0.82F, MatrixStackUtilsAmount.getValue(-1750963, hintClearLimit)
         );
      }

      int brownValue = scaledHeight + 46;
      boolean matrixStackFlag = !this.append.trim().isEmpty();
      float hitsValue = MatrixStackUtilsAmount.getAmount(
         context,
         this.hintClearHits,
         this.hintClearAnims,
         "m:add",
         "Add",
         IcoGenUtils.getIdentifierGEN_CORZCLIENT_MAP("plus"),
         matrixStackValue,
         brownValue,
         22.0F,
         54.0F,
         matrixStackLimit,
         scaledY,
         matrixStackFlag,
         this::updateAddedEnter
      );
      if (!matrixStackFlag) {
         this.hintClearHits.handle(hitsValue, brownValue - 1.0F, matrixStackValue - hitsValue, 24.0F, this::updateAddedEnter);
      }

      float identifierText = hitsValue - 6.0F - topOffset;
      float releaseRightBound = this.hintClearAnims.getAmount("m:focus", this.focusEnterFlag ? 1.0F : 0.0F, 16.0F);
      float hintClearValue11 = this.hintClearAnims.getAmount("m:field", MatrixStackUtilsAmount.canFlag(matrixStackLimit, scaledY, topOffset, brownValue, identifierText, 22.0F));
      float releaseRight = MatrixStackUtilsAmount.getReleaseRight(context, topOffset, brownValue, identifierText, 22.0F, IcoGenUtils.getIdentifierGEN_CORZCLIENT_MAP("search"), releaseRightBound, hintClearValue11, hintClearLimit);
      int abcdefghijklmnopqrstuvwxyzBrown = fontRenderer.getAbcdefghijklmnopqrstuvwxyzBrown(brownValue, 22);
      float playersClearValue = topOffset + identifierText - 8.0F - releaseRight;
      String matrixStackText = formatText(this.append, playersClearValue);
      if (this.append.isEmpty()) {
         MatrixStackUtilsAmount.handle(fontRenderer.formatText("Search or enter player name...", playersClearValue), releaseRight, abcdefghijklmnopqrstuvwxyzBrown, MatrixStackUtilsAmount.getValue(-7630437, 0.72F));
      } else {
         MatrixStackUtilsAmount.handle(matrixStackText, releaseRight, abcdefghijklmnopqrstuvwxyzBrown, -920587);
      }

      if (this.focusEnterFlag) {
         int n4 = (int)fontRenderer.getAbcdefghijklmnopqrstuvwxyzBrown() - 2;
         this.focusHintClearEdit
            .renderAmountTime(
               context,
               releaseRight + (this.append.isEmpty() ? 0.0F : MatrixStackUtilsAmount.getAmount3(matrixStackText) + 1.0F),
               brownValue + (22 - n4) / 2,
               n4,
               MatrixEspUtils.releaseRightValue,
               MatrixClearUtils.getAmount()
            );
      }

      this.hintClearHits.handle(topOffset, brownValue - 1.0F, identifierText, 24.0F, this::updateEnterFocus);
      float panelOverlayLimit = this.hintClearAnims.getAmount("m:mode", this.modePlayersFlag ? 1.0F : 0.0F, 14.0F);
      render(context, "Saved players", topOffset, scaledHeight + 77, sideWalkAmount, 1.0F - panelOverlayLimit);
      render(context, "On this server", topOffset, scaledHeight + 77, sideWalkAmount, panelOverlayLimit);
      this.renderBarThumb(context, topOffset, scaledHeight + 89, sideWalkAmount, matrixStackLimit, scaledY);
      int targetOffset = scaledHeight + 247;
      boolean hintClearFlag3 = itemsSize > 0;
      float hintClearValue16 = this.hintClearAnims.getAmount("m:arm", this.clearArmFlag ? 1.0F : 0.0F, 16.0F);
      float panelOverlay = CorzRoundedUtils.getPanelOverlay(0.42F + 0.58F * this.hintClearAnims.getAmount("m:clearable", hintClearFlag3 ? 1.0F : 0.0F, 12.0F));

      try {
         MatrixStackUtilsAmount.getAmount(
            context,
            hintClearFlag3 ? this.hintClearHits : MatrixStackUtilsAmount.Hits.INSTANCE,
            this.hintClearAnims,
            "m:clear",
            this.clearArmFlag ? "Confirm clear?" : "Clear all",
            topOffset,
            targetOffset,
            18.0F,
            hintClearFlag3 ? matrixStackLimit : -4000,
            scaledY,
            true,
            hintClearValue16,
            this::updateArmClear
         );
      } finally {
         CorzRoundedUtils.handlePanelOverlay(panelOverlay);
      }

      MatrixStackUtilsAmount.getAmount(context, this.hintClearHits, this.hintClearAnims, "m:close", "Close", matrixStackValue, targetOffset, 18.0F, matrixStackLimit, scaledY, false, this::updateFlagThumb);
   }

   private static boolean canFlag(char playersClearLimit) {
      return playersClearLimit >= 'a' && playersClearLimit <= 'z' || playersClearLimit >= 'A' && playersClearLimit <= 'Z' || playersClearLimit >= '0' && playersClearLimit <= '9' || playersClearLimit == '_';
   }

   private int getMatchPlayers() {
      if (this.friendsEmptyValue == 0) {
         return 120;
      } else if (this.playersMatchItems.isEmpty()) {
         return 88;
      } else {
         int matchPlayersValue = (this.playersMatchItems.size() + 3 - 1) / 3;
         return matchPlayersValue * 36 + (matchPlayersValue - 1) * 4;
      }
   }

   private void updateFlagThumb() {
      if (!this.thumbActive) {
         this.thumbActive = true;
         this.nanoTimeFocusHint = System.nanoTime();
         this.focusEnterFlag = false;
         this.thumbHintFlag = false;
         this.clearArmFlag = false;
      }
   }

   private void handleFocusRow(String focusRowText) {
      String lowerCase = focusRowText.toLowerCase(Locale.ROOT);

      for (PlayersClear.RowM rowM : this.focusModeItems) {
         if (!rowM.focusModeActive && rowM.lowerCase.equals(lowerCase)) {
            rowM.focusModeActive = true;
            rowM.lastNanoTime = System.nanoTime();
            return;
         }
      }
   }

   private static float getAmount(float playersClearLimit, float playersClearLimit2, float playersClearValue) {
      return playersClearLimit < playersClearLimit2 ? playersClearLimit2 : (playersClearLimit > playersClearValue ? playersClearValue : playersClearLimit);
   }

   private boolean canVectorTransform(DrawContext context, int vectorTransformValue) {
      Matrix3x2fStack matrices = context.getMatrices();
      float vectorTransformValue2 = this.empty + this.playersUserValue / 2.0F;

      for (int i = -16; i <= 52; i += 17) {
         matrices.transformPosition(vectorTransformValue2, vectorTransformValue + i, this.vectorTransformVector2f);
         if (context.scissorContains(Math.round(this.vectorTransformVector2f.x), Math.round(this.vectorTransformVector2f.y))) {
            return true;
         }
      }

      return false;
   }

   private static int getScaledHeight(int scaledHeightValue) {
      return (MinecraftClient.getInstance().getWindow().getScaledHeight() - scaledHeightValue) / 2;
   }

   private void updateFriendRemoved() {
      long currentMillis = System.currentTimeMillis();
      if (this.armConfirmFlag && currentMillis <= this.removedFriendTime) {
         if (currentMillis < this.removedFriendTime - 2500L + 300L) {
            return;
         }

         String friendText = this.clipboard;
         this.friendListBase.handleForString(friendText);
         this.handleFocusRow(friendText);
         this.updateFocusMode();
         this.updateRootFriends();
         this.updateFlashArm();
         DisabledEnabled.getInstance().handleDisabledEnabled("Friend Removed", friendText + " is no longer saved", false);
      } else {
         this.armConfirmFlag = true;
         this.removedFriendTime = currentMillis + 2500L;
      }
   }

   public void handleFriendsUser(int empty, int friendsTabsValue, int playersUserValue, int friendsSearchValue) {
      this.empty = empty;
      this.friendsTabsValue = friendsTabsValue;
      this.playersUserValue = playersUserValue;
      this.friendsSearchValue = friendsSearchValue;
   }

   public void renderFlashCopied(DrawContext context) {
      if (this.clipboard != null) {
         this.renderArmFlash(context);
      }

      if (this.barFocusActive) {
         this.renderFlagArm(context);
      }
   }

   private void updateThumbAmount() {
      this.thumbHintFlag = true;
      this.playersClearAmount = this.iconsClearAmount;
      this.thumbDragStartAmount = this.barThumbAmount;
   }

   private void handleTabs(int tabs) {
      this.tabs = tabs;
      this.updateRootFriends();
   }

   private void updateRootFriends() {
      List list = this.friendListBase.getItems();
      if (list.size() != this.friendsEmptyValue) {
         HashSet hashSet = new HashSet();

         for (String rootFriendsText : (Iterable<java.lang.String>) (list)) {
            hashSet.add(rootFriendsText.toLowerCase(Locale.ROOT));
         }

         this.friendsPlayersAnims.handle(rootFriendsText2 -> !rootFriendsText2.startsWith("card:") && !rootFriendsText2.startsWith("more:") || hashSet.contains(rootFriendsText2.substring("card:".length())));
      }

      String lowerCase = this.clearIcons.getSubstring().trim().toLowerCase(Locale.ROOT);
      this.playersMatchItems.clear();

      for (String jsonMetadataText : (Iterable<java.lang.String>) (list)) {
         boolean rootFriendsFlag = PlayerSkinResolver.isPlayerListed(jsonMetadataText);
         if (this.tabs != 1 || rootFriendsFlag) {
            String lowerCase2 = jsonMetadataText.toLowerCase(Locale.ROOT);
            if (lowerCase.isEmpty() || lowerCase2.contains(lowerCase) || NameProtect.getString(jsonMetadataText).toLowerCase(Locale.ROOT).contains(lowerCase)) {
               this.playersMatchItems.add(new PlayersClear.Shown(jsonMetadataText, lowerCase2, rootFriendsFlag));
            }
         }
      }

      this.friendsEmptyValue = list.size();
      this.lastNanoTime = System.nanoTime();
   }

   private void renderScaledWidth(DrawContext context, int scaledWidthValue, int scaledHeight, int materialFactor, int scaledWidthValue4, float matrixStackValue, boolean scaledWidthFlag, Runnable runnable) {
      Window window = MinecraftClient.getInstance().getWindow();
      MatrixStackUtilsAmount.render(context, window.getScaledWidth(), window.getScaledHeight(), 0.56F * MatrixStackUtilsAmount.getAmountFloat(matrixStackValue, scaledWidthFlag));
      boolean translatePush = MatrixStackUtilsAmount.canTranslatePush(context, scaledWidthValue + materialFactor / 2.0F, scaledHeight + scaledWidthValue4 / 2.0F, MatrixStackUtilsAmount.getAmount(matrixStackValue, scaledWidthFlag));
      float panelOverlay = CorzRoundedUtils.getPanelOverlay(MatrixStackUtilsAmount.getAmountForFloatFlag(matrixStackValue, scaledWidthFlag));

      try {
         MatrixStackUtilsAmount.render(context, scaledWidthValue, scaledHeight, materialFactor, scaledWidthValue4);
         runnable.run();
      } finally {
         CorzRoundedUtils.handlePanelOverlay(panelOverlay);
         MatrixStackUtilsAmount.renderPopMatrices(context, translatePush);
      }
   }

   private void updateEnterFocus() {
      this.focusEnterFlag = true;
      this.focusHintClearEdit.updateTime();
   }

   private void renderFlashArm(DrawContext context, int flashArmValue, int scaledHeight, String flashArmText) {
      VertexTextureFont fontRenderer = MatrixStackUtilsAmount.getFontRenderer();
      int flashArmValue3 = this.flashArmFlag ? -4000 : this.scaledX;
      int scaledY = this.scaledY;
      int ctxOffset = flashArmValue + 12;
      short releaseRightBound = 196;
      int flashArmValue5 = ctxOffset + releaseRightBound;
      MatrixStackUtilsAmount.renderReleaseRight(context, flashArmText, ctxOffset, scaledHeight + 12, 36.0F);
      float matrixStackValue = flashArmValue5 - 16;
      MatrixStackUtilsAmount.render(
         context, this.flashArmHits, this.hintClearAnims, "fp:x", IcoGenUtils.getCrossIco(), matrixStackValue, scaledHeight + 12, 16.0F, 10.0F, flashArmValue3, scaledY, false, this::updateFlashArm
      );
      float scaleValue = ctxOffset + 36 + 10.0F;
      boolean flashArmFlag = PlayerSkinResolver.isPlayerListed(flashArmText);
      String formatText = fontRenderer.formatText(NameProtect.getString(flashArmText), (matrixStackValue - 6.0F - scaleValue) / 1.1F);
      MatrixStackUtilsAmount.handlePushScale(formatText, scaleValue, MatrixStackUtilsAmount.getAmount(scaledHeight + 12 + 5.0F, 12.0F, 1.1F), 1.1F, -920587);
      MatrixStackUtilsAmount.getAmount(context, flashArmFlag, flashArmFlag ? "Online" : "Offline", scaleValue, MatrixStackUtilsAmount.getAmount(scaledHeight + 12 + 19.0F, 11.0F, 0.82F));
      MatrixPeekUtilsRender.renderMatrixPeek(context, ctxOffset, scaledHeight + 58, releaseRightBound, MatrixPeekUtilsRender.getAmount(), CardMatrixUtils.getValueInt());
      boolean flashArmFlag2 = this.copiedFlashText != null && System.currentTimeMillis() < this.flashCopiedDeadline;
      float flashArmValue8 = this.hintClearAnims.getAmount("fp:flash", flashArmFlag2 ? 1.0F : 0.0F, 16.0F);
      int leftOffset = scaledHeight + 68;
      MatrixStackUtilsAmount.getAmount(
         context,
         this.flashArmHits,
         this.hintClearAnims,
         "fp:copy",
         flashArmFlag2 ? this.copiedFlashText : "Copy name",
         flashArmValue5,
         leftOffset,
         20.0F,
         releaseRightBound,
         flashArmValue3,
         scaledY,
         false,
         flashArmValue8,
         this::updateCopiedFlash
      );
      float matrixStackLimit = this.hintClearAnims.getAmount("fp:arm", this.armConfirmFlag ? 1.0F : 0.0F, 16.0F);
      MatrixStackUtilsAmount.getAmount(
         context,
         this.flashArmHits,
         this.hintClearAnims,
         "fp:remove",
         this.armConfirmFlag ? "Confirm remove?" : "Remove from friends",
         flashArmValue5,
         leftOffset + 20 + 4,
         20.0F,
         releaseRightBound,
         flashArmValue3,
         scaledY,
         true,
         matrixStackLimit,
         this::updateFriendRemoved
      );
   }

   private float getClearAll(DrawContext context, float playersClearValue, int matrixStackLimit, int mouseY) {
      float materialFactor = 18.0F;
      float releaseRightValue = playersClearValue - materialFactor;
      float matrixStackValue = this.friendsTabsValue + (22.0F - materialFactor) / 2.0F;
      float playersClearValue7 = releaseRightValue - 2.0F;
      float playersClearFactor = (float)Math.ceil(MatrixStackUtilsAmount.getAmount("Clear all?", 0.82F)) + 14.0F;
      float deltaVar = playersClearValue7 - playersClearFactor;
      boolean playersClearFlag = System.currentTimeMillis() < this.clearArmTime;
      float playersClearLimit = this.friendsPlayersAnims.getAmount("clear:arm", playersClearFlag ? 1.0F : 0.0F, 18.0F);
      if (playersClearLimit > 0.01F) {
         float max8 = Math.max(10.0F, playersClearFactor * playersClearLimit);
         float matrixPeekValue = playersClearLimit >= 1.0F ? deltaVar : playersClearValue7 - max8;
         float max10 = MatrixStackUtilsAmount.getMax(playersClearLimit / 0.35F);
         float amountForFloat = MatrixStackUtilsAmount.getAmountForFloat((playersClearLimit - 0.86F) / 0.14F);
         MatrixPeekUtilsRender.renderForDrawContextFloat(
            context,
            matrixPeekValue,
            matrixStackValue,
            playersClearValue7 - matrixPeekValue,
            materialFactor,
            5.0F,
            MatrixStackUtilsAmount.getValue(-1750963, 0.13F * max10),
            MatrixStackUtilsAmount.getValue(-1750963, 0.42F * max10)
         );
         if (amountForFloat > 0.01F) {
            MatrixStackUtilsAmount.handlePushScale(
               "Clear all?",
               deltaVar + 7.0F,
               MatrixStackUtilsAmount.getAmount(matrixStackValue, materialFactor, 0.82F) + 3.0F * (1.0F - amountForFloat),
               0.82F,
               MatrixStackUtilsAmount.getValue(CardMatrixUtils.getValue(-1750963, -1, 0.18), amountForFloat)
            );
         }
      }

      if (playersClearFlag) {
         this.friendsPlayersHits.handle(deltaVar, matrixStackValue - 1.0F, playersClearValue - deltaVar, materialFactor + 2.0F, this::updateFriendsSaved);
      }

      MatrixStackUtilsAmount.renderReleaseRight(
         context,
         this.friendsPlayersHits,
         this.friendsPlayersAnims,
         "clear",
         IcoGenUtils.getIdentifierGEN_CORZCLIENT_MAP("trash"),
         releaseRightValue,
         matrixStackValue,
         materialFactor,
         12.0F,
         matrixStackLimit,
         mouseY,
         true,
         playersClearLimit,
         this::updateFriendsSaved
      );
      return deltaVar;
   }

   private static final class RowM {
      boolean focusModeActive;
      final int rowGeneration;
      long lastNanoTime;
      final String lowerCase;
      final long nanoTimeRow = System.nanoTime();
      final int rowValue;
      final boolean offlineTrashActive;
      final String friendName;

      RowM(String key, boolean value, int value2, int value3) {
         this.focusModeActive = false;
         this.lastNanoTime = 0L;
         this.friendName = key;
         this.lowerCase = key.toLowerCase(Locale.ROOT);
         this.offlineTrashActive = value;
         this.rowGeneration = value2;
         this.rowValue = value3;
      }
   }

   private record Shown(String name, String key, boolean online) {
   }
}
