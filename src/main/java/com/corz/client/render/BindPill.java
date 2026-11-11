package com.corz.client.render;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.ModuleConfigAutosave;
import com.corz.client.config.DoubleSetting;
import com.corz.client.config.EnumSetting;
import com.corz.client.gui.ButtonBase;
import com.corz.client.gui.ClearIcons;
import com.corz.client.gui.RenderPillUtils;
import com.corz.client.render.MatrixStackUtilsAmount.Anims;
import com.corz.client.render.MatrixStackUtilsAmount.Entrance;
import com.corz.client.render.MatrixStackUtilsAmount.Hits;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;
import com.corz.client.categories.settings.ClientSounds;
import com.corz.client.categories.settings.GUISettings;
import com.corz.client.categories.settings.License;
import com.corz.client.categories.settings.MiddleClickFriend;
import com.corz.client.categories.settings.Notifications;
import com.corz.client.categories.settings.Panic;
import com.corz.client.categories.settings.SelfDestruct;
import ai.auth.LicenseRuntime;
import ai.auth.Module;
import ai.auth.ModuleRegistry;
import com.corz.client.utils.PageRaltUtils;

public class BindPill {
   private final String[] tabs;
   private final Anims listenSettingAnims;
   private DoubleSetting doubleHelper;
   private double targetAmount;
   private final int[] settingsSearchValues;
   private final ClearIcons clearIcons;
   private int animationDurationMs;
   private final GUISettings gui3d;
   private static final int UNIT_VALUE = 1;
   private int bindPillValue;
   private int settingsTabsValue;
   private final Hits settingsTabsHits;
   private final List<BindPill.Row> matchDifferentItems;
   private final Entrance settingsTabsEntrance;
   private int settingsSearchValue;
   private static final String PILL_BIND = "pill:bind";
   private boolean listen;
   private static final String[] systemLicenseTexts = new String[]{"Interface", "Sounds", "License", "System"};
   private int settingsTabsCount;
   private final List<List<BindPill.Row>> clickguiHotkeyItems = new ArrayList<>();
   private boolean settingsTabsActive;
   private int tabsPanelWidth;
   private static int systemLicenseValue = -1;
   private final MiddleClickFriend friend;
   private int lastTabsLayoutWidth;

   private void handleSettingsSearch(int settingsSearchValue) {
      this.settingsSearchValue = settingsSearchValue;
      systemLicenseValue = this.settingsSearchValues[settingsSearchValue];
      this.clearIcons.handleSubstringClear("");
   }

   public int getTabsPanelWidth() {
      return this.tabsPanelWidth;
   }

   private void updateListenIcons() {
      this.clearIcons.update();
      this.listen = true;
   }

   public boolean canIconsClear(double amount, double iconsClearValue, int modulesValue) {
      boolean iconsClearFlag = this.settingsTabsActive && this.clearIcons.canMin(amount, iconsClearValue);
      this.clearIcons.canClearModules(amount, iconsClearValue, modulesValue);
      if (!this.settingsTabsActive) {
         this.clearIcons.update();
      }

      if (iconsClearFlag) {
         return true;
      } else if (modulesValue != 0) {
         return false;
      } else {
         this.targetAmount = amount;
         return this.settingsTabsHits.canAnimsPress(amount, iconsClearValue);
      }
   }

   private void renderTabsSettings(DrawContext context, BindPill.Row row, int tabsSettingsValue, int vectorTransformValue, int w, int modulesClearLimit, int scaledY) {
      Entrance amountFlag = this.settingsTabsEntrance.getAmountFlag(this.settingsTabsCount++);
      if (!amountFlag.isAmount()) {
         float tabsSettingsAmount = amountFlag.tabsSettingsAmount;
         float tabsSettingsLimit = amountFlag.entranceProgress;
         Hits hits = amountFlag.matrixScissorActive ? this.settingsTabsHits : Hits.INSTANCE;
         int matrixStackLimit = amountFlag.activeFlag ? modulesClearLimit : tabsSettingsValue - 4000;
         String tabsSettingsText = "card:" + row.key();
         boolean translatePush = MatrixStackUtilsAmount.canTranslatePush(context, tabsSettingsValue + w / 2.0F, vectorTransformValue + 18.0F, amountFlag.matrixScissorAmount * this.listenSettingAnims.getTime(tabsSettingsText, 0.025F));

         try {
            if (tabsSettingsAmount > 0.004F) {
               float panelOverlay = CorzRoundedUtils.getPanelOverlay(tabsSettingsAmount);

               try {
                  MatrixStackUtilsAmount.render(context, tabsSettingsValue, vectorTransformValue, w, 36.0F, 0.0F, 0.0F, 0.0F);
               } finally {
                  CorzRoundedUtils.handlePanelOverlay(panelOverlay);
               }
            }

            if (tabsSettingsLimit > 0.002F) {
               float panelOverlay2 = CorzRoundedUtils.getPanelOverlay(tabsSettingsLimit);

               try {
                  this.renderListenSetting(context, row, tabsSettingsText, hits, tabsSettingsValue, vectorTransformValue, w, matrixStackLimit, scaledY);
               } finally {
                  CorzRoundedUtils.handlePanelOverlay(panelOverlay2);
               }
            }
         } finally {
            MatrixStackUtilsAmount.renderPopMatrices(context, translatePush);
         }
      }
   }

   public boolean isListen() {
      return this.listen;
   }

   private static <T extends ConfigSetting> T getBaseForIlIllIiIllClass(Module Module, Class<T> class2) {
      if (Module == null) {
         return null;
      } else {
         for (ConfigSetting autoEspBase : Module.getSettings()) {
            if (class2.isInstance(autoEspBase)) {
               return (T)class2.cast(autoEspBase);
            }
         }

         return null;
      }
   }

   public void handlePillBind(int pillBindValue) {
      if (this.gui3d != null) {
         this.gui3d.setBindKey(PageRaltUtils.getValueForInt(pillBindValue));
      }

      this.listen = false;
      this.listenSettingAnims.handleTime("pill:bind");
   }

   private String formatGuiGlfw() {
      int guiGlfwValue = this.gui3d != null ? this.gui3d.getBindKey() : 344;
      String formatNone = PageRaltUtils.formatNone(guiGlfwValue);
      if (formatNone != null) {
         return formatNone;
      } else {
         String glfwGetKeyName = GLFW.glfwGetKeyName(guiGlfwValue, 0);
         return glfwGetKeyName != null ? glfwGetKeyName.toUpperCase() : PageRaltUtils.formatText(guiGlfwValue);
      }
   }

   public void handlePillBindForInt(int pillBindValue) {
      if (this.gui3d != null) {
         this.gui3d.setBindKey(PageRaltUtils.canFlagForInt(pillBindValue) ? -1 : pillBindValue);
      }

      this.listen = false;
      this.listenSettingAnims.handleTime("pill:bind");
   }

   public BindPill() {
      this.matchDifferentItems = new ArrayList<>();
      this.clearIcons = new ClearIcons(0, 0, 0, 0);
      this.settingsTabsActive = false;
      this.settingsTabsHits = new Hits();
      this.listenSettingAnims = new Anims();
      this.settingsTabsEntrance = new Entrance();
      this.settingsTabsValue = 0;
      this.lastTabsLayoutWidth = -1;
      this.settingsTabsCount = 0;
      this.doubleHelper = null;
      this.listen = false;
      this.tabsPanelWidth = 200;
      this.gui3d = (GUISettings)ModuleRegistry.getModule(GUISettings.class);
      this.friend = (MiddleClickFriend)ModuleRegistry.getModule(MiddleClickFriend.class);
      ButtonBase base = getBaseForIlIllIiIllClass(ModuleRegistry.getModule(License.class), ButtonBase.class);
      ButtonBase base2 = getBaseForIlIllIiIllClass(ModuleRegistry.getModule(Panic.class), ButtonBase.class);
      ButtonBase base3 = getBaseForIlIllIiIllClass(ModuleRegistry.getModule(SelfDestruct.class), ButtonBase.class);
      Module iliIiiliiiii = ModuleRegistry.getModule(ClientSounds.class);
      Module Module = ModuleRegistry.getModule(Notifications.class);

      for (int i = 0; i < systemLicenseTexts.length; i++) {
         this.clickguiHotkeyItems.add(new ArrayList<>());
      }

      this.handleClickguiHotkey(0, BindPill.Kind.BIND, "bind", "Menu Bind", "Key that opens this menu", null);
      this.handle(0, BindPill.Kind.TOGGLE, "gui3d", (BooleanSetting)getBaseForIlIllIiIllString(this.gui3d, "3D GUI"));
      if (this.friend != null) {
         this.handleClickguiHotkey(0, BindPill.Kind.TOGGLE, "friend", this.friend.getCategory(), "Middle-click a player to add or remove a friend", null);
      }

      this.handle(0, BindPill.Kind.TOGGLE, "toasts", (BooleanSetting)getBaseForIlIllIiIllString(Module, "Toast Notifications"));
      this.handle(0, BindPill.Kind.TOGGLE, "toasts_mod", (BooleanSetting)getBaseForIlIllIiIllString(Module, "Module Toggle Toasts"));
      this.handle(1, BindPill.Kind.TOGGLE, "cs_mod", (BooleanSetting)getBaseForIlIllIiIllString(iliIiiliiiii, "Module Sound"));
      this.handle(1, BindPill.Kind.TOGGLE, "cs_hov", (BooleanSetting)getBaseForIlIllIiIllString(iliIiiliiiii, "Hover Sound"));
      this.handle(1, BindPill.Kind.TOGGLE, "cs_cat", (BooleanSetting)getBaseForIlIllIiIllString(iliIiiliiiii, "Category Click Sound"));
      this.handle(1, BindPill.Kind.TOGGLE, "cs_card", (BooleanSetting)getBaseForIlIllIiIllString(iliIiiliiiii, "Module Card Sound"));
      this.handle(1, BindPill.Kind.CYCLE, "cs_pack", (EnumSetting)getBaseForIlIllIiIllString(iliIiiliiiii, "Sound Pack"));
      this.handle(1, BindPill.Kind.CYCLE, "cs_hstyle", (EnumSetting)getBaseForIlIllIiIllString(iliIiiliiiii, "Hover Style"));
      this.handle(1, BindPill.Kind.SLIDER, "cs_vol", (DoubleSetting)getBaseForIlIllIiIllString(iliIiiliiiii, "Volume"));
      this.handle(1, BindPill.Kind.SLIDER, "cs_hvol", (DoubleSetting)getBaseForIlIllIiIllString(iliIiiliiiii, "Hover Volume"));
      if (base != null) {
         this.handleClickguiHotkey(2, BindPill.Kind.LICENSE, "lic", "License", null, base);
      }

      if (base2 != null) {
         this.handleClickguiHotkey(3, BindPill.Kind.ACTION, "panic", "Panic", base2.getSettingName(), base2);
      }

      if (base3 != null) {
         this.handleClickguiHotkey(3, BindPill.Kind.ACTION, "destruct", base3.getSettingName(), "Hide the client for a screenshare", base3);
      }

      int bindPillCounter = 0;

      for (List list : this.clickguiHotkeyItems) {
         if (!list.isEmpty()) {
            bindPillCounter++;
         }
      }

      this.tabs = new String[bindPillCounter + 1];
      this.settingsSearchValues = new int[bindPillCounter + 1];
      this.tabs[0] = "All";
      this.settingsSearchValues[0] = -1;
      int settingsSearchValue = 1;

      for (int j = 0; j < systemLicenseTexts.length; j++) {
         if (!this.clickguiHotkeyItems.get(j).isEmpty()) {
            this.tabs[settingsSearchValue] = systemLicenseTexts[j];
            this.settingsSearchValues[settingsSearchValue] = j;
            if (j == systemLicenseValue) {
               this.settingsSearchValue = settingsSearchValue;
            }

            settingsSearchValue++;
         }
      }

      this.clearIcons.handleModulesSearch("Search settings...");
      this.clearIcons.setModulesSearchFlag(false);
      this.clearIcons.setModulesSearchValue(6);
   }

   public void updateListen() {
      this.listen = false;
   }

   public boolean canIconsClear(char ch, int iconsClearValue) {
      if (!this.clearIcons.isClearModulesFlag()) {
         return false;
      } else {
         this.clearIcons.canClearSubstring(ch, iconsClearValue);
         return true;
      }
   }

   private static ConfigSetting getBaseForIlIllIiIllString(Module Module, String bindPillText) {
      if (Module == null) {
         return null;
      } else {
         for (ConfigSetting base : Module.getSettings()) {
            if (base.getSettingName().equalsIgnoreCase(bindPillText)) {
               return base;
            }
         }

         return null;
      }
   }

   public void renderSettingsTabs(DrawContext context, int settingsTabsValue, int ctxOffset, int w, int modulesClearLimit, int mouseY) {
      this.settingsTabsHits.update();
      boolean timeFlag = this.settingsTabsEntrance.isTimeFlag();
      String lowerCase = this.clearIcons.getSubstring().trim().toLowerCase(Locale.ROOT);
      boolean settingsTabsFlag = !lowerCase.isEmpty();
      int settingsTabsValue6 = -1 - this.settingsSearchValue;
      int size7 = -1;
      if (settingsTabsFlag) {
         this.matchDifferentItems.clear();
         settingsTabsValue6 = 1;

         for (List list : this.clickguiHotkeyItems) {
            for (BindPill.Row row : (Iterable<com.corz.client.render.BindPill.Row>) (list)) {
               if (row.haystack().contains(lowerCase)) {
                  this.matchDifferentItems.add(row);
                  settingsTabsValue6 = settingsTabsValue6 * 31 + row.key().hashCode();
               }
            }
         }

         size7 = this.matchDifferentItems.size();
         if (settingsTabsValue6 < 0) {
            settingsTabsValue6 = ~settingsTabsValue6;
         }
      }

      if (timeFlag) {
         this.settingsTabsEntrance.updateTimeFlag();
      } else if (settingsTabsValue6 != this.settingsTabsValue) {
         if (size7 > 0 && this.lastTabsLayoutWidth > 0 && size7 <= this.lastTabsLayoutWidth) {
            this.settingsTabsEntrance.updateTimeFlagEntranceValue();
         } else {
            this.settingsTabsEntrance.updateTimeFlag();
         }
      }

      this.settingsTabsValue = settingsTabsValue6;
      this.lastTabsLayoutWidth = size7;
      this.settingsTabsCount = 0;
      float amountForDrawContextString = MatrixStackUtilsAmount.getAmountForDrawContextString(context, "Settings", settingsTabsValue, ctxOffset, 22.0F);
      float settingsTabsValue9 = MatrixStackUtilsAmount.getAmount(
         context, this.settingsTabsHits, this.listenSettingAnims, "tabs", this.tabs, settingsTabsFlag ? 0 : this.settingsSearchValue, amountForDrawContextString + 10.0F, ctxOffset, 22.0F, modulesClearLimit, mouseY, this::handleSettingsSearch
      );
      int ceil10 = (int)Math.ceil(settingsTabsValue9 + 8.0F);
      int settingsTabsLimit = Math.min(130, settingsTabsValue + w - ceil10);
      this.settingsTabsActive = settingsTabsLimit >= 56;
      if (this.settingsTabsActive) {
         this.clearIcons.handleMin(ceil10, ctxOffset + 2, settingsTabsLimit, 18);
         this.clearIcons.renderModulesClear(context, modulesClearLimit, mouseY, 0.0F);
      } else {
         this.clearIcons.update();
      }

      int settingsTabsTotal = ctxOffset + 22 + 6;
      if (settingsTabsFlag) {
         if (this.matchDifferentItems.isEmpty()) {
            MatrixStackUtilsAmount.renderMatricesPop(
               context,
               this.settingsTabsHits,
               this.listenSettingAnims,
               "none",
               settingsTabsValue,
               settingsTabsTotal,
               w,
               88.0F,
               IcoGenUtils.getIdentifierGEN_CORZCLIENT_MAP("search"),
               "No settings match",
               "Try a different name.",
               null,
               modulesClearLimit,
               mouseY,
               null
            );
            settingsTabsTotal += 88;
         } else {
            settingsTabsTotal += this.getLicenseKind(context, this.matchDifferentItems, settingsTabsValue, settingsTabsTotal, w, modulesClearLimit, mouseY);
         }
      } else if (this.settingsSearchValues[this.settingsSearchValue] < 0) {
         boolean settingsTabsFlag2 = true;

         for (int i = 0; i < systemLicenseTexts.length; i++) {
            var clickguihotkeyitem = this.clickguiHotkeyItems.get(i);
            if (!clickguihotkeyitem.isEmpty()) {
               if (!settingsTabsFlag2) {
                  settingsTabsTotal += 9;
               }

               settingsTabsFlag2 = false;
               float panelOverlay = CorzRoundedUtils.getPanelOverlay(this.settingsTabsEntrance.getAmount(this.settingsTabsCount));

               try {
                  MatrixStackUtilsAmount.renderRoot(context, systemLicenseTexts[i], settingsTabsValue, settingsTabsTotal, w);
               } finally {
                  CorzRoundedUtils.handlePanelOverlay(panelOverlay);
               }

               settingsTabsTotal += 12;
               settingsTabsTotal += this.getLicenseKind(context, clickguihotkeyitem, settingsTabsValue, settingsTabsTotal, w, modulesClearLimit, mouseY);
            }
         }
      } else {
         settingsTabsTotal += this.getLicenseKind(context, this.clickguiHotkeyItems.get(this.settingsSearchValues[this.settingsSearchValue]), settingsTabsValue, settingsTabsTotal, w, modulesClearLimit, mouseY);
      }

      this.tabsPanelWidth = settingsTabsTotal - ctxOffset;
   }

   public boolean isFlag() {
      return false;
   }

   public void render(DrawContext context) {
   }

   public boolean isIconsClear() {
      return this.clearIcons.isClearModulesFlag();
   }

   private void handle(double bindPillValue) {
      if (this.doubleHelper != null && this.animationDurationMs > 0) {
         double bindPillFactor = Math.max(0.0, Math.min(1.0, (bindPillValue - this.bindPillValue) / this.animationDurationMs));
         double doubleBound = this.doubleHelper.getMinValue() + bindPillFactor * (this.doubleHelper.getMaxValue() - this.doubleHelper.getMinValue());
         double bindPillLimit = this.doubleHelper.getStepSize();
         if (bindPillLimit > 0.0) {
            doubleBound = Math.round(doubleBound / bindPillLimit) * bindPillLimit;
         }

         this.doubleHelper.handleMaxAmount(doubleBound);
         ModuleConfigAutosave.updateFlag2();
      }
   }

   public void handle(double bindPillValue, double bindPillValue2, int button) {
      if (this.doubleHelper != null) {
         this.handle(bindPillValue);
      }
   }

   private float getAmount(DrawContext context, Hits hits, DoubleSetting doubleHelper, String bindPillText, int bindPillValue, int vectorTransformValue, int materialFactor, float abcdefghijklmnopqrstuvwxyzBrown, int matrixStackLimit, int scaledY) {
      int value = bindPillValue + 8;
      int releaseRightFactor = materialFactor - 16;
      int matrixStackValue = value - 6;
      int plusDismissValue = vectorTransformValue + 16;
      int identifierText = releaseRightFactor + 12;
      byte segmentSlider = 20;
      float cardMatrixFactor = this.listenSettingAnims.getAmount("slide:" + bindPillText, this.doubleHelper == doubleHelper || MatrixStackUtilsAmount.canFlag(matrixStackLimit, scaledY, matrixStackValue, plusDismissValue, identifierText, segmentSlider));
      double deltaAmount = doubleHelper.getMaxValue() - doubleHelper.getMinValue();
      float bindPillValue12 = this.listenSettingAnims.getAmount("val:" + bindPillText, (float)doubleHelper.getValue(), 12.0F);
      float bindPillValue13 = deltaAmount <= 0.0 ? 0.0F : (float)((bindPillValue12 - doubleHelper.getMinValue()) / deltaAmount);
      MatrixStackUtilsAmount.renderReleaseRight(context, value, vectorTransformValue + 24, releaseRightFactor, bindPillValue13, cardMatrixFactor);
      String roundOffset = Math.round(bindPillValue12 * 100.0F) + "%";
      float bindPillValue14 = bindPillValue + materialFactor - 8 - MatrixStackUtilsAmount.getAmount3(roundOffset);
      MatrixStackUtilsAmount.handle(roundOffset, bindPillValue14, abcdefghijklmnopqrstuvwxyzBrown, CardMatrixUtils.getValue(-7630437, -920587, cardMatrixFactor));
      hits.handle(matrixStackValue, plusDismissValue, identifierText, segmentSlider, () -> {
         this.doubleHelper = doubleHelper;
         this.bindPillValue = value;
         this.animationDurationMs = releaseRightFactor;
         this.handle(this.targetAmount);
      });
      return bindPillValue14;
   }

   private void handleClickguiHotkey(int index, BindPill.Kind kind, String clickguiHotkeyText, String clickguiHotkeyText2, String builderText, ConfigSetting autoEspBase) {
      StringBuilder append = new StringBuilder(clickguiHotkeyText2).append(' ').append(systemLicenseTexts[index]);
      if (builderText != null) {
         append.append(' ').append(builderText);
      }

      if (autoEspBase != null && autoEspBase.getDescription() != null) {
         append.append(' ').append(autoEspBase.getDescription());
      }

      if (autoEspBase instanceof ButtonBase buttonBase) {
         append.append(' ').append(buttonBase.getText());
      }

      if (kind == BindPill.Kind.BIND) {
         append.append(" keybind hotkey gui clickgui open");
      }

      this.clickguiHotkeyItems.get(index).add(new BindPill.Row(clickguiHotkeyText, kind, clickguiHotkeyText2, builderText, autoEspBase, append.toString().toLowerCase(Locale.ROOT)));
   }

   public boolean canIconsClear(int iconsClearValue, int scancode, int modifiers) {
      if (!this.clearIcons.isClearModulesFlag()) {
         return false;
      } else {
         this.clearIcons.canClearSubstring(iconsClearValue, scancode, modifiers);
         return true;
      }
   }

   public void updateListenIcons2() {
      this.listen = false;
      this.clearIcons.update();
      this.settingsTabsActive = false;
      this.settingsTabsHits.update();
   }

   private void renderListenSetting(DrawContext context, BindPill.Row row, String listenSettingText, Hits hits, int listenSettingValue, int vectorTransformValue, int materialFactor, int matrixStackLimit, int scaledY) {
      VertexTextureFont fontRenderer = MatrixStackUtilsAmount.getFontRenderer();
      String bindPillText = row.key();
      float lockFactor = this.listenSettingAnims.getAmount(listenSettingText, MatrixStackUtilsAmount.canFlag(matrixStackLimit, scaledY, listenSettingValue, vectorTransformValue, materialFactor, 36.0F));
      float matrixStackValue = listenSettingValue + materialFactor - 8;
      float targetOffset = vectorTransformValue + 10.0F;
      float listenSettingValue9 = listenSettingValue + 8;
      float abcdefghijklmnopqrstuvwxyzBrown = MatrixStackUtilsAmount.getAmount(vectorTransformValue + 7.0F, 11.0F, 1.0F);

      float listenSettingValue11 = switch (row.kind()) {
         case BIND -> {
            float listenSettingValue12 = this.listenSettingAnims.getAmount("listen", this.listen ? 1.0F : 0.0F, 16.0F);
            MatrixStackUtilsAmount.render(context, listenSettingValue, vectorTransformValue, materialFactor, 36.0F, lockFactor * 0.5F, 0.0F, listenSettingValue12);
            yield MatrixStackUtilsAmount.getAmount(
               context,
               hits,
               this.listenSettingAnims,
               "pill:bind",
               this.listen ? "Press a key..." : this.formatGuiGlfw(),
               matrixStackValue,
               targetOffset,
               16.0F,
               44.0F,
               matrixStackLimit,
               scaledY,
               listenSettingValue12,
               this::updateListenIcons
            );
         }
         case TOGGLE -> {
            boolean listenSettingFlag = row.setting() instanceof BooleanSetting booleanBase ? booleanBase.isEnabled() : this.friend.isEnabled();
            Runnable runnable;
            Runnable runnable2 = runnable = row.setting() instanceof BooleanSetting booleanBase2 ? booleanBase2::toggle : this.friend::toggle;
            float releaseRightLimit = this.listenSettingAnims.getAmount("on:" + bindPillText, listenSettingFlag ? 1.0F : 0.0F, 16.0F);
            String animsText = "press:" + bindPillText;
            MatrixStackUtilsAmount.render(context, listenSettingValue, vectorTransformValue, materialFactor, 36.0F, lockFactor, this.listenSettingAnims.getAmount(animsText, 0.0F, 10.0F), releaseRightLimit);
            RenderPillUtils.render(context, listenSettingValue + materialFactor, vectorTransformValue, 36, 8, releaseRightLimit, lockFactor);
            hits.handleFlag(listenSettingValue, vectorTransformValue, materialFactor, 36.0F, () -> {
               this.listenSettingAnims.handle(animsText, 1.0F);
               runnable.run();
            }, this.listenSettingAnims, listenSettingText);
            yield matrixStackValue - 22.0F;
         }
         case CYCLE -> {
            EnumSetting setting = (EnumSetting)row.setting();
            MatrixStackUtilsAmount.render(context, listenSettingValue, vectorTransformValue, materialFactor, 36.0F, lockFactor * 0.5F, 0.0F, 0.0F);
            yield MatrixStackUtilsAmount.getAmount(
               context, hits, this.listenSettingAnims, "pill:" + bindPillText, setting.getCurrentOption(), IcoGenUtils.getChevrondnIco(), matrixStackValue, targetOffset, 16.0F, 66.0F, matrixStackLimit, scaledY, 0.0F, () -> {
                  int listSize = setting.getOptions().size();
                  if (listSize > 0) {
                     setting.handle((setting.getCurrentIndex() + 1) % listSize);
                  }
               }
            );
         }
         case SLIDER -> {
            MatrixStackUtilsAmount.render(context, listenSettingValue, vectorTransformValue, materialFactor, 36.0F, lockFactor * 0.5F, 0.0F, 0.0F);
            yield this.getAmount(context, hits, (DoubleSetting)row.setting(), bindPillText, listenSettingValue, vectorTransformValue, materialFactor, abcdefghijklmnopqrstuvwxyzBrown, matrixStackLimit, scaledY);
         }
         case LICENSE, ACTION -> {
            ButtonBase buttonBase = (ButtonBase)row.setting();
            MatrixStackUtilsAmount.render(context, listenSettingValue, vectorTransformValue, materialFactor, 36.0F, lockFactor * 0.5F, 0.0F, 0.0F);
            yield MatrixStackUtilsAmount.getAmount(
               context, hits, this.listenSettingAnims, "btn:" + bindPillText, buttonBase.getText(), matrixStackValue, targetOffset, 16.0F, matrixStackLimit, scaledY, buttonBase.isButtonActive(), buttonBase::updateAction
            );
         }
      };
      float vertexTextureLimit = listenSettingValue11 - 8.0F - listenSettingValue9;
      MatrixStackUtilsAmount.handle(fontRenderer.formatText(row.label(), vertexTextureLimit), listenSettingValue9, abcdefghijklmnopqrstuvwxyzBrown, -920587);
      float priceValue = MatrixStackUtilsAmount.getAmount(vectorTransformValue + 18.0F, 11.0F, 0.82F);
      if (row.kind() == BindPill.Kind.LICENSE) {
         ButtonBase setting3 = (ButtonBase)row.setting();
         MatrixStackUtilsAmount.getAmount(context, true, fontRenderer.formatText(setting3.formatSupplier(), (vertexTextureLimit - 8.0F) / 0.82F), listenSettingValue9, priceValue);
      } else if (row.kind() != BindPill.Kind.SLIDER && row.desc() != null) {
         MatrixStackUtilsAmount.handlePushScale(fontRenderer.formatText(row.desc(), vertexTextureLimit / 0.82F), listenSettingValue9, priceValue, 0.82F, -7630437);
      }
   }

   public void handleForDoubleInt(double bindPillValue, double bindPillValue2, int button) {
      this.doubleHelper = null;
   }

   private int getLicenseKind(DrawContext context, List<BindPill.Row> clickguihotkeyitem, int licenseKindValue, int settingsTabsTotal, int w, int modulesClearLimit, int mouseY) {
      if (clickguihotkeyitem.size() == 1 && ((BindPill.Row)clickguihotkeyitem.get(0)).kind() == BindPill.Kind.LICENSE) {
         this.renderTabsSettings(context, (BindPill.Row)clickguihotkeyitem.get(0), licenseKindValue, settingsTabsTotal, w, modulesClearLimit, mouseY);
         return 36;
      } else {
         for (int i = 0; i < clickguihotkeyitem.size(); i++) {
            int matrixStackFactor = i % 2;
            this.renderTabsSettings(
               context,
               (BindPill.Row)clickguihotkeyitem.get(i),
               MatrixStackUtilsAmount.getValueForInt(licenseKindValue, w, 2, 4, matrixStackFactor),
               settingsTabsTotal + i / 2 * 40,
               MatrixStackUtilsAmount.getValue(licenseKindValue, w, 2, 4, matrixStackFactor),
               modulesClearLimit,
               mouseY
            );
         }

         int licenseKindValue7 = (clickguihotkeyitem.size() + 2 - 1) / 2;
         return licenseKindValue7 == 0 ? 0 : licenseKindValue7 * 40 - 4;
      }
   }

   private void handle(int bindPillValue, BindPill.Kind kind, String bindPillText, ConfigSetting autoEspBase) {
      if (autoEspBase != null) {
         this.handleClickguiHotkey(bindPillValue, kind, bindPillText, autoEspBase.getSettingName(), autoEspBase.getDescription(), autoEspBase);
      }
   }

   private static enum Kind {
      BIND,
      TOGGLE,
      CYCLE,
      SLIDER,
      LICENSE,
      ACTION;
   }

   private record Row(String key, BindPill.Kind kind, String label, String desc, ConfigSetting setting, String haystack) {
   }
}
