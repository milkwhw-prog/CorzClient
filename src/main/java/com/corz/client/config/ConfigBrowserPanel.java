package com.corz.client.config;

import com.corz.client.config.ConfigJsonValidator.Result;
import com.corz.client.config.PresetFileStore.Entry;
import com.corz.client.config.PresetFileStore.Preset;
import com.corz.client.config.PresetFileStore.Source;
import com.corz.client.config.PresetFileStore.SourceMeta;
import com.corz.client.gui.ClearEdit;
import com.corz.client.gui.ClearIcons;
import com.corz.client.gui.DisabledEnabled;
import com.corz.client.gui.MatrixPeekUtilsRender;
import com.corz.client.render.CardMatrixUtils;
import com.corz.client.render.CorzRoundedUtils;
import com.corz.client.render.IcoGenUtils;
import com.corz.client.render.MatrixClearUtils;
import com.corz.client.render.MatrixEspUtils;
import com.corz.client.render.MatrixPeekUtilsIdentifier;
import com.corz.client.render.MatrixStackUtilsAmount;
import com.corz.client.render.MatrixStackUtilsAmount.Anims;
import com.corz.client.render.MatrixStackUtilsAmount.Entrance;
import com.corz.client.render.MatrixStackUtilsAmount.Hits;
import com.corz.client.render.VertexTextureFont;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Window;
import org.joml.Matrix3x2fStack;
import org.joml.Vector2f;
import com.corz.client.categories.configs.Configs;
import ai.auth.ModuleRegistry;
import com.corz.client.utils.AutoRiggerUtils;

public class ConfigBrowserPanel {
   private static final ConfigBrowserPanel.Tab[] TABS_IN_ORDER = new ConfigBrowserPanel.Tab[]{
      ConfigBrowserPanel.Tab.MINE, ConfigBrowserPanel.Tab.COMMUNITY, ConfigBrowserPanel.Tab.DOWNLOADS
   };
   private volatile String communityLoadError;
   private List<CommunityConfigClient.ShareCode> renderedShareCodes;
   private ConfigsPage renderedCommunityPage;
   private List<ConfigBrowserPanel.ConfigListEntry> localEntries;
   private String copiedShareCode;
   private static final String STRIP_ANIM_KEY = "strip";
   private final Hits contentHits;
   private String sharedConfigId;
   private final Anims like;
   private String inputBarText;
   private String hoverTooltipText;
   private boolean inputIsConfigName;
   private static final String CLICK_DELETE = "Click again to delete";
   private static final String[] TAB_LABELS = new String[]{"Local", "Community", "Downloads"};
   private String redeemCodeInput;
   private float tooltipCenterX;
   private float tooltipY;
   private static final String[] SORT_API_KEYS = new String[]{"popular", "most_downloaded", "newest", "trending"};
   private String search;
   private String localSearchText;
   private static final String[] SORT_LABELS = new String[]{"Popular", "Most Downloaded", "Newest", "Trending"};
   private long confirmExpiryMs;
   private volatile String notTurnedOnMessage;
   private boolean redeemInputOpen;
   private static final Integer[] SHARE_CODE_MAX_USES = new Integer[]{1, 5, 25, null};
   private int plus;
   private String sharedConfigName;
   private boolean shareLoading;
   private long tooltipChangeNanos;
   private int searchLayoutOffset;
   private List<ConfigBrowserPanel.ConfigListEntry> trackedLocalEntries;
   private final Hits inputBarHits;
   private String apply;
   private int panelWidth;
   private List<String> communityDateLabels;
   private final ClearEdit nameInputEdit;
   private boolean tooltipBelow;
   private String sharingConfigKey;
   private float tooltipMaxX;
   private String shareErrorMessage;
   private String renamingConfigKey;
   private ConfigBrowserPanel.Tab tabs = ConfigBrowserPanel.Tab.MINE;
   private int lastEntriesHash;
   private float tooltipMinX;
   private static final String ANIM_KEY_PREFIX = "i:";
   private static final String[] SHARE_USES_LABELS = new String[]{"1 use", "5 uses", "25 uses", "Unlimited"};
   private int unfilteredConfigCount;
   private static final String[] SORT_ANIM_KEYS = new String[]{"sort:p0", "sort:p1", "sort:p2", "sort:p3"};
   private final Entrance cardEntrance;
   private final List<ConfigBrowserPanel.ConfigListEntry> visibleEntries;
   private volatile ConfigsPage communityPage;
   private int tabsTopY;
   private long lastRenderNanos;
   private long copiedCodeDeadlineMs;
   private boolean nameInputOpen;
   private List<Entry> cachedEntryList;
   private String lastTooltipText;
   private final Vector2f scratchVec;
   private final ConfigListSetting configListSetting;
   private int lastEntryCount;
   private List<Entry> allEntries = new ArrayList<>();
   private final ClearIcons searchField;
   private volatile String keptForSafetyMessage;
   private long tabSwitchTimeMs;
   private long appliedBadgeDeadlineMs;
   private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
   private String appliedConfigKey;
   private volatile boolean communityLoading;
   private String nameInput;
   private final ClearEdit codeInputEdit;
   private boolean searchVisible;
   private long searchDebounceStartMs;
   private volatile long communityRequestId;
   private String newestShareCode;
   private boolean inputFocused;
   private int entranceTabKey;
   private volatile List<CommunityConfigClient.ShareCode> shareCodes;
   private int sort;
   private static boolean accountSyncStarted = false;
   private String pendingConfirmKey;
   private String activeConfigKey;

   private void renderShareCodeCard(DrawContext context, Hits hits, CommunityConfigClient.ShareCode shareCode, int revokeRevokedValue, int vectorTransformValue, int identifierText, int matrixStackLimit, int scaledY) {
      VertexTextureFont fontRenderer = MatrixStackUtilsAmount.getFontRenderer();
      String revokeRevokedText = shareCode.code;
      boolean revokeRevokedFlag = MatrixStackUtilsAmount.canFlag(matrixStackLimit, scaledY, revokeRevokedValue, vectorTransformValue, identifierText, 36.0F);
      MatrixStackUtilsAmount.render(context, revokeRevokedValue, vectorTransformValue, identifierText, 36.0F, this.like.getAmount(buildAnimKey(revokeRevokedText, "card"), revokeRevokedFlag), 0.0F, 0.0F);
      float ctxOffset = revokeRevokedValue + 8;
      float revokeRevokedValue6 = revokeRevokedValue + identifierText - 8;
      float targetOffset = vectorTransformValue + 10.0F;
      float matrixStackValue = revokeRevokedValue6 - 16.0F;
      MatrixStackUtilsAmount.render(
         context,
         hits,
         this.like,
         buildAnimKey(revokeRevokedText, "revoke"),
         IcoGenUtils.getCrossIco(),
         matrixStackValue,
         targetOffset,
         16.0F,
         10.0F,
         matrixStackLimit,
         scaledY,
         true,
         () -> this.revokeShareCode(revokeRevokedText)
      );
      this.setHoverTooltip("Revoke", matrixStackValue, targetOffset, 16.0F, 16.0F, matrixStackLimit, scaledY, false);
      float revokeRevokedValue8 = matrixStackValue - 6.0F;
      if (!shareCode.revoked) {
         boolean revokeRevokedFlag2 = revokeRevokedText.equals(this.copiedShareCode) && System.currentTimeMillis() < this.copiedCodeDeadlineMs;
         revokeRevokedValue8 = MatrixStackUtilsAmount.getAmount(
               context,
               hits,
               this.like,
               buildAnimKey(revokeRevokedText, "copy"),
               revokeRevokedFlag2 ? "Copied" : "Copy",
               matrixStackValue - 3.0F,
               targetOffset,
               16.0F,
               matrixStackLimit,
               scaledY,
               false,
               () -> this.copyShareCode(revokeRevokedText)
            )
            - 6.0F;
      }

      int revokeRevokedValue9 = shareCode.revoked
         ? -7630437
         : (revokeRevokedText.equals(this.newestShareCode) ? CardMatrixUtils.getValue(MatrixEspUtils.releaseRightValue, -1, 0.3) : -920587);
      MatrixStackUtilsAmount.handle(revokeRevokedText, ctxOffset, MatrixStackUtilsAmount.getAmount(vectorTransformValue + 7.0F, 11.0F, 1.0F), revokeRevokedValue9);
      if (shareCode.revoked) {
         MatrixStackUtilsAmount.getAmount(context, "Revoked", ctxOffset + MatrixStackUtilsAmount.getAmount3(revokeRevokedText) + 5.0F, vectorTransformValue + 7.0F, -1750963);
      }

      MatrixStackUtilsAmount.handlePushScale(
         fontRenderer.formatText("usage: " + shareCode.getUsageLabel(), (revokeRevokedValue8 - ctxOffset) / 0.82F),
         ctxOffset,
         MatrixStackUtilsAmount.getAmount(vectorTransformValue + 18.0F, 11.0F, 0.82F),
         0.82F,
         -7630437
      );
   }

   private float renderLikeButton(DrawContext context, Hits hits, SharedConfigEntry versionJsonUtils, float likeUnlikeValue, float matrixStackValue, int matrixStackLimit, int scaledY) {
      String valueOf = String.valueOf(Math.max(0L, versionJsonUtils.likeCount));
      float identifierText = 18.0F + (float)Math.ceil(MatrixStackUtilsAmount.getAmount(valueOf, 0.82F)) + 6.0F;
      float likeUnlike = likeUnlikeValue - identifierText;
      String animsText = buildAnimKey(versionJsonUtils.id, "like");
      float cardMatrixFactor = this.like.getAmount(animsText, MatrixStackUtilsAmount.canFlag(matrixStackLimit, scaledY, likeUnlike, matrixStackValue, identifierText, 16.0F));
      float likeUnlikeValue7 = this.like.getAmount(animsText + ":on", versionJsonUtils.liked ? 1.0F : 0.0F, 16.0F);
      boolean translatePush = MatrixStackUtilsAmount.canTranslatePush(context, likeUnlike + identifierText / 2.0F, matrixStackValue + 8.0F, this.like.getTime(animsText, 0.05F));
      MatrixPeekUtilsRender.renderForDrawContextFloat(
         context,
         likeUnlike,
         matrixStackValue,
         identifierText,
         16.0F,
         5.0F,
         CardMatrixUtils.getValue(CardMatrixUtils.getValue(CardMatrixUtils.getValue4(), -1, 0.03 + 0.06 * cardMatrixFactor), -45715, 0.12 * likeUnlikeValue7),
         CardMatrixUtils.getValue(
            CardMatrixUtils.getValue(CardMatrixUtils.getValueInt(), 1040187391, cardMatrixFactor), MatrixStackUtilsAmount.getValue(-45715, 0.5F), likeUnlikeValue7
         )
      );
      int likeUnlikeValue8 = CardMatrixUtils.getValue(CardMatrixUtils.getValue(-7630437, -45715, cardMatrixFactor), -45715, likeUnlikeValue7);
      MatrixPeekUtilsIdentifier.handle(
         likeUnlike + 5.0F, matrixStackValue + 3.0F, 10.0F, 10.0F, IcoGenUtils.getHeartIco(), new Color(likeUnlikeValue8, true), MatrixPeekUtilsRender.getMatrixStack()
      );
      MatrixStackUtilsAmount.handlePushScale(
         valueOf,
         likeUnlike + 5.0F + 10.0F + 3.0F,
         MatrixStackUtilsAmount.getAmount(matrixStackValue, 16.0F, 0.82F),
         0.82F,
         CardMatrixUtils.getValue(CardMatrixUtils.getValue(-7630437, -920587, cardMatrixFactor), CardMatrixUtils.getValue(-45715, -1, 0.3), likeUnlikeValue7)
      );
      MatrixStackUtilsAmount.renderPopMatrices(context, translatePush);
      hits.handle(likeUnlike - 1.0F, matrixStackValue - 1.0F, identifierText + 2.0F, 18.0F, () -> {
         if (versionJsonUtils.liked) {
            this.like.handleTimeForString(animsText);
         } else {
            this.like.handleTime(animsText);
         }

         this.toggleLike(versionJsonUtils);
      });
      this.setHoverTooltip(versionJsonUtils.liked ? "Unlike" : "Like", likeUnlike, matrixStackValue, identifierText, 16.0F, matrixStackLimit, scaledY, false);
      return likeUnlike;
   }

   public boolean onKeyPressed(int appendStripValue, int scancode, int modifiers) {
      boolean appendStripFlag = appendStripValue == 257 || appendStripValue == 335;
      if ((this.nameInputOpen || this.redeemInputOpen) && this.inputFocused) {
         if (appendStripValue == 256) {
            this.closeInputBar();
            return true;
         } else if (this.nameInputOpen) {
            if (appendStripFlag) {
               this.submitConfigName();
               return true;
            } else {
               if (appendStripValue == 259 && !this.nameInput.isEmpty()) {
                  this.nameInput = this.nameInput.substring(0, this.nameInput.length() - 1);
                  this.nameInputEdit.updateTime();
               }

               return true;
            }
         } else if (appendStripFlag) {
            this.redeemEnteredCode();
            return true;
         } else {
            if (appendStripValue == 259 && !this.redeemCodeInput.isEmpty()) {
               this.redeemCodeInput = this.redeemCodeInput.substring(0, this.redeemCodeInput.length() - 1);
               this.codeInputEdit.updateTime();
            }

            return true;
         }
      } else if (this.sharingConfigKey != null) {
         if (appendStripValue == 256) {
            this.closeShareView();
         }

         return true;
      } else if (this.searchField.isClearModulesFlag()) {
         if (appendStripFlag) {
            if (this.tabs == ConfigBrowserPanel.Tab.COMMUNITY) {
               this.searchDebounceStartMs = 0L;
               this.reloadCommunityConfigs();
            }

            return true;
         } else {
            this.searchField.canClearSubstring(appendStripValue, scancode, modifiers);
            this.onSearchTextChanged();
            return true;
         }
      } else if ((this.nameInputOpen || this.redeemInputOpen) && appendStripValue == 256) {
         this.closeInputBar();
         return true;
      } else {
         return false;
      }
   }

   private void deleteCommunityConfig(SharedConfigEntry versionJsonUtils) {
      this.showNotification("Deleting\u2026", nullToEmpty(versionJsonUtils.name), true);
      CommunityConfigClient.deleteConfig(versionJsonUtils.id, result -> {
         if (result.ok) {
            this.showNotification("Config Deleted", nullToEmpty(versionJsonUtils.name) + " removed from community", true);
            this.reloadCommunityConfigs();
         } else {
            this.showNotification("Delete Failed", result.errorMessage != null ? result.errorMessage : result.errorCode, false);
         }
      });
   }

   private void syncAccountConfigsOnce() {
      if (!accountSyncStarted && ConfigSyncUtils.isSignedIn()) {
         accountSyncStarted = true;
         ConfigSyncUtils.downloadMissingServerConfigs(added -> {
            if (added != null && added > 0) {
               this.reloadLocalEntries();
               this.showNotification("Configs Synced", added + (added == 1 ? " config" : " configs") + " from your account", true);
            }
         });
      }
   }

   private float renderConfigCardHeader(DrawContext context, String loadedFolderfillText, String loadedFolderfillText2, float loadedFolderfillFactor, int loadedFolderfillValue, int vectorTransformValue, int loadedValue, int matrixStackLimit, int scaledY) {
      byte segmentSlider = 16;
      float loadedFolderfillValue7 = 30.0F;
      float loadedFolderfillValue8 = (MatrixStackUtilsAmount.getAmount("Loaded") + 5.0F) * loadedFolderfillFactor;
      String formatText = MatrixStackUtilsAmount.getFontRenderer().formatText(loadedFolderfillText2, loadedValue - 48 - loadedFolderfillValue7 - loadedFolderfillValue8);
      float amount3 = MatrixStackUtilsAmount.getAmount3(formatText);
      float releaseRightBound = Math.max(44.0F, (float)Math.ceil((loadedFolderfillValue7 + amount3 + loadedFolderfillValue8) * 2.0F) / 2.0F);
      boolean loadedFolderfillFlag = MatrixStackUtilsAmount.canFlag(matrixStackLimit, scaledY, loadedFolderfillValue, vectorTransformValue + segmentSlider, loadedValue, 60 - segmentSlider)
         || MatrixStackUtilsAmount.canFlag(matrixStackLimit, scaledY, loadedFolderfillValue, vectorTransformValue, releaseRightBound, segmentSlider);
      float releaseRight = MatrixStackUtilsAmount.getReleaseRight(context, loadedFolderfillValue, vectorTransformValue, loadedValue, 60.0F, releaseRightBound, this.like.getAmount(buildAnimKey(loadedFolderfillText, "card"), loadedFolderfillFlag), loadedFolderfillFactor);
      float cardYOffset = loadedFolderfillValue + 8;
      float matrixStackValue = cardYOffset + 10.0F + 4.0F;
      MatrixPeekUtilsIdentifier.handle(
         cardYOffset,
         vectorTransformValue + 3 + 0.5F,
         10.0F,
         10.0F,
         IcoGenUtils.getIdentifierGEN_CORZCLIENT_MAP("folderfill"),
         new Color(CardMatrixUtils.getValue(-7630437, MatrixEspUtils.releaseRightValue, loadedFolderfillFactor), true),
         MatrixPeekUtilsRender.getMatrixStack()
      );
      MatrixStackUtilsAmount.handle(formatText, matrixStackValue, MatrixStackUtilsAmount.getAmount(vectorTransformValue + 3, 11.0F, 1.0F), -920587);
      float loadedFolderfillLimit = clampForFloat((loadedFolderfillFactor - 0.8F) / 0.2F);
      if (loadedFolderfillLimit > 0.01F) {
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(loadedFolderfillLimit);

         try {
            MatrixStackUtilsAmount.getReleaseRight(context, "Loaded", matrixStackValue + amount3 + 5.0F, vectorTransformValue + 3);
         } finally {
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
         }
      }

      return releaseRight;
   }

   private Configs getConfigsModule() {
      return ModuleRegistry.getModule(Configs.class) instanceof Configs configs ? configs : null;
   }

   public boolean onMouseScrolled(double configShareValue, double heightAmount, double keybindBindValue) {
      return false;
   }

   private static String formatEpochSeconds(long configShareValue) {
      if (configShareValue <= 0L) {
         return null;
      } else {
         try {
            return Instant.ofEpochSecond(configShareValue).atZone(ZoneId.systemDefault()).format(DATE_FORMAT);
         } catch (RuntimeException e) {
            return null;
         }
      }
   }

   public boolean onMouseClicked(double stripIconsValue, double mouseY, int modulesValue) {
      if (modulesValue != 0) {
         return false;
      } else {
         boolean stripIconsFlag = this.searchVisible && this.searchField.canMin(stripIconsValue, mouseY);
         this.searchField.canClearModules(stripIconsValue, mouseY, modulesValue);
         if (!this.searchVisible) {
            this.searchField.update();
         }

         this.inputFocused = false;
         if (stripIconsFlag) {
            return true;
         } else {
            boolean animsPress = this.contentHits.canAnimsPress(stripIconsValue, mouseY);
            if (this.nameInputOpen || this.redeemInputOpen) {
               this.focusInput();
            }

            return animsPress || this.nameInputOpen || this.redeemInputOpen || this.sharingConfigKey != null;
         }
      }
   }

   private void renderCommunityTab(DrawContext context, int resultsConfigsValue, int matrixStackLimit, int mouseY, long currentMillis) {
      if (this.communityPage == null && this.communityLoadError == null && !this.communityLoading) {
         this.reloadCommunityConfigs();
      }

      if (this.searchDebounceStartMs != 0L && currentMillis - this.searchDebounceStartMs > 400L) {
         this.searchDebounceStartMs = 0L;
         this.reloadCommunityConfigs();
      }

      boolean sortConfigsFlag = this.communityLoading;
      String configsLoadText = this.communityLoadError;
      ConfigsPage configsJsonUtils = this.communityPage;
      if (configsJsonUtils != this.renderedCommunityPage) {
         this.renderedCommunityPage = configsJsonUtils;
         var hashSet = new HashSet();
         var items = new ArrayList();
         if (configsJsonUtils != null) {
            for (SharedConfigEntry versionJsonUtils : configsJsonUtils.configs) {
               hashSet.add(versionJsonUtils.id);
               String format = formatEpochSeconds(versionJsonUtils.updatedAt);
               items.add(format != null ? format : formatEpochSeconds(versionJsonUtils.createdAt));
            }
         }

         this.communityDateLabels = items;
         this.pruneStaleAnimations(hashSet);
      }

      VertexTextureFont fontRenderer = MatrixStackUtilsAmount.getFontRenderer();
      this.renderSortBar(context, resultsConfigsValue, matrixStackLimit, mouseY, !sortConfigsFlag && configsLoadText == null ? configsJsonUtils : null);
      int downloadCommunityValue = resultsConfigsValue + 20;
      if (!sortConfigsFlag) {
         if (configsLoadText != null) {
            MatrixStackUtilsAmount.renderMatricesPop(
               context,
               this.contentHits,
               this.like,
               "empty:offline",
               this.plus,
               downloadCommunityValue,
               this.panelWidth,
               120.0F,
               IcoGenUtils.getCloudIco(),
               "Offline",
               fontRenderer.formatText(configsLoadText, this.panelWidth / 0.82F),
               "Retry",
               matrixStackLimit,
               mouseY,
               this::reloadCommunityConfigs
            );
         } else if (configsJsonUtils != null && !configsJsonUtils.configs.isEmpty()) {
            this.updateEntranceAnimation(ConfigBrowserPanel.Tab.COMMUNITY.ordinal(), System.identityHashCode(configsJsonUtils), 0);
            float resultsConfigsValue6 = MatrixStackUtilsAmount.getAmount("Verified");
            float resultsConfigsValue7 = MatrixStackUtilsAmount.getAmount("Yours");
            boolean resultsConfigsFlag = true;

            for (int i = 0; i < configsJsonUtils.configs.size(); i++) {
               int resultsConfigsValue8 = i % 2;
               int valueForInt = MatrixStackUtilsAmount.getValueForInt(this.plus, this.panelWidth, 2, 4, resultsConfigsValue8);
               int loadedValue = MatrixStackUtilsAmount.getValue(this.plus, this.panelWidth, 2, 4, resultsConfigsValue8);
               int vectorTransformValue = downloadCommunityValue + i / 2 * 64;
               if (resultsConfigsValue8 == 0) {
                  resultsConfigsFlag = this.isRowVisibleInScissor(context, vectorTransformValue, 60);
               }

               if (resultsConfigsFlag) {
                  Entrance amountFlag = this.cardEntrance.getAmountFlag(i);
                  if (!amountFlag.isAmount()) {
                     float tabsSettingsAmount = amountFlag.tabsSettingsAmount;
                     float resultsConfigsLimit = amountFlag.entranceProgress;
                     Hits hits = amountFlag.matrixScissorActive ? this.contentHits : Hits.INSTANCE;
                     int resultsConfigsValue12 = amountFlag.activeFlag ? matrixStackLimit : this.plus - 4000;
                     SharedConfigEntry resultsconfigsitem = configsJsonUtils.configs.get(i);
                     boolean translatePush = MatrixStackUtilsAmount.canTranslatePush(context, valueForInt + loadedValue / 2.0F, vectorTransformValue + 30.0F, amountFlag.matrixScissorAmount);

                     try {
                        this.renderCardBackground(context, nullToEmpty(resultsconfigsitem.name), 0.0F, tabsSettingsAmount, valueForInt, vectorTransformValue, loadedValue);
                        if (resultsConfigsLimit > 0.002F) {
                           float panelOverlay = CorzRoundedUtils.getPanelOverlay(resultsConfigsLimit);

                           try {
                              this.renderCommunityConfigCard(
                                 context, hits, resultsconfigsitem, i < this.communityDateLabels.size() ? this.communityDateLabels.get(i) : null, valueForInt, vectorTransformValue, loadedValue, resultsConfigsValue12, mouseY, resultsConfigsValue6, resultsConfigsValue7
                              );
                           } finally {
                              CorzRoundedUtils.handlePanelOverlay(panelOverlay);
                           }
                        }
                     } finally {
                        MatrixStackUtilsAmount.renderPopMatrices(context, translatePush);
                     }
                  }
               }
            }
         } else {
            MatrixStackUtilsAmount.renderMatricesPop(
               context,
               this.contentHits,
               this.like,
               "empty:results",
               this.plus,
               downloadCommunityValue,
               this.panelWidth,
               120.0F,
               IcoGenUtils.getIdentifierGEN_CORZCLIENT_MAP("search"),
               "No results",
               "Try a different sort or search.",
               null,
               matrixStackLimit,
               mouseY,
               null
            );
         }
      } else {
         for (int j = 0; j < 6; j++) {
            int matrixStackFactor = j % 2;
            MatrixStackUtilsAmount.render(
               context,
               (float)MatrixStackUtilsAmount.getValueForInt(this.plus, this.panelWidth, 2, 4, matrixStackFactor),
               (float)(downloadCommunityValue + j / 2 * 64),
               (float)MatrixStackUtilsAmount.getValue(this.plus, this.panelWidth, 2, 4, matrixStackFactor),
               60.0F,
               64.0F + j % 3 * 14.0F,
               j
            );
         }
      }
   }

   public void onMouseDragged(double configShareValue, double minmaxValue, int button) {
   }

   public boolean onCharTyped(char ch, int appendStripValue) {
      if ((this.nameInputOpen || this.redeemInputOpen) && this.inputFocused) {
         if (this.nameInputOpen) {
            if (isValidNameChar(ch) && this.nameInput.length() < 64) {
               this.nameInput = this.nameInput + ch;
               this.nameInputEdit.updateTime();
            }
         } else if (isAlphanumeric(ch) && this.redeemCodeInput.length() < 12) {
            this.redeemCodeInput = this.redeemCodeInput + Character.toUpperCase(ch);
            this.codeInputEdit.updateTime();
         }

         return true;
      } else if (this.sharingConfigKey == null && this.searchField.isClearModulesFlag()) {
         this.searchField.canClearSubstring(ch, appendStripValue);
         this.onSearchTextChanged();
         return true;
      } else {
         return false;
      }
   }

   private void setHoverTooltip(String deleteClickText, float deleteConfigsValue, float targetOffset, float identifierText, float segmentSlider, int matrixStackLimit, int scaledY, boolean configsFlag) {
      if (MatrixStackUtilsAmount.canFlag(matrixStackLimit, scaledY, deleteConfigsValue, targetOffset, identifierText, segmentSlider)) {
         this.hoverTooltipText = deleteClickText;
         this.tooltipCenterX = deleteConfigsValue + identifierText / 2.0F;
         this.tooltipY = configsFlag ? targetOffset + segmentSlider + 4.0F : targetOffset - 4.0F;
         this.tooltipBelow = configsFlag;
      }
   }

   private int getNoticeHeight() {
      int keptturnedSize = this.getWrappedNoticeLines().size();
      return keptturnedSize == 0 ? 0 : keptturnedSize * 10 + 6;
   }

   private void closeInputBar() {
      this.nameInputOpen = false;
      this.nameInput = "";
      this.renamingConfigKey = null;
      this.redeemInputOpen = false;
      this.redeemCodeInput = "";
      this.inputFocused = false;
   }

   private void importConfigFromFile() {
      Thread thread = new Thread(() -> {
         try {
            FileDialog var1x = new FileDialog((Frame)null, "Import Corz config", 0);
            var1x.setFile("*.corz.json;*.json");
            var1x.setVisible(true);
            String directory = var1x.getDirectory();
            String file = var1x.getFile();
            if (directory == null || file == null) {
               return;
            }

            byte[] allBytes = Files.readAllBytes(Path.of(directory, file));
            Result[] results = new Result[1];
            String[] strings = new String[1];
            ConfigPresetFile jsonVersion = PresetFileStore.parsePresetBytes(allBytes, results, strings);
            if (jsonVersion == null) {
               this.showNotification("Import Failed", results[0] != null && results[0].getFirstError() != null ? results[0].getFirstError() : "Invalid config", false);
               return;
            }

            String importConfigText = strings[0];
            SourceMeta sourceMeta = new SourceMeta();
            sourceMeta.source = Source.IMPORTED;

            try {
               PresetFileStore.saveSourceMeta(importConfigText, sourceMeta);
            } catch (IOException e) {
            }

            this.showNotification("Config Imported", jsonVersion.name, true);
            this.reloadLocalEntries();
         } catch (Throwable e2) {
            this.showNotification("Import Failed", "Couldn't open the file picker", false);
         }
      }, "corz-config-import");
      thread.setDaemon(true);
      thread.start();
   }

   public void setLayout(int plus, int configsTabsValue, int shareConfigValue, int configsSearchValue) {
      this.plus = plus;
      this.tabsTopY = configsTabsValue;
      this.panelWidth = shareConfigValue;
      this.searchLayoutOffset = configsSearchValue;
   }

   private void rebuildLocalEntries(List<Entry> items) {
      var arrayList = new ArrayList(items.size());

      for (Entry entry : items) {
         Source source = PresetFileStore.readSourceMeta(entry.fileName).source;
         String versionImportedText = entry.jsonVersion.name != null ? entry.jsonVersion.name : entry.fileName;
         int modulesJson = countEnabledModules(entry.jsonVersion.content);
         String nullToEmpty2 = nullToEmpty(entry.jsonVersion.author);
         String versionImportedText2 = null;
         if (source == Source.IMPORTED) {
            versionImportedText2 = "Imported";
         } else if (source == Source.COMMUNITY && !nullToEmpty2.isEmpty()) {
            versionImportedText2 = "by " + nullToEmpty2;
         }

         String format = formatIsoTimestamp(entry.jsonVersion.updatedAt);
         if (format == null) {
            format = formatIsoTimestamp(entry.jsonVersion.createdAt);
         }

         arrayList.add(
            new ConfigBrowserPanel.ConfigListEntry(
               entry.fileName,
               versionImportedText,
               (versionImportedText + " " + nullToEmpty2).toLowerCase(Locale.ROOT),
               modulesJson + (modulesJson == 1 ? " module on" : " modules on"),
               versionImportedText2,
               format,
               source == Source.LOCAL,
               source == Source.COMMUNITY
            )
         );
      }

      this.localEntries = arrayList;
      this.cachedEntryList = items;
      this.activeConfigKey = PresetFileStore.readActivePresetName();
   }

   private void closeShareView() {
      this.sharingConfigKey = null;
      this.sharedConfigName = null;
      this.sharedConfigId = null;
      this.shareErrorMessage = null;
      this.shareLoading = false;
      this.shareCodes = new ArrayList<>();
      this.newestShareCode = null;
      this.entranceTabKey = -1;
      this.reloadLocalEntries();
   }

   private void submitConfigName() {
      String trim = this.nameInput.trim();
      if (trim.length() < 3) {
         this.showNotification("Configs", "Name must be at least 3 characters", false);
      } else {
         try {
            if (this.renamingConfigKey == null) {
               String formatText = ModuleConfigSerializer.exportConfigJson(trim, "Snapshot of current setup", getUsername());
               PresetFileStore.writeActivePresetName(formatText);
               this.showNotification("Config Created", trim + " saved", true);
               if (ConfigSyncUtils.isSignedIn()) {
                  ConfigSyncUtils.uploadPrivateConfig(formatText, result -> {});
               }
            } else {
               PresetFileStore.renamePreset(this.renamingConfigKey, trim);
               this.showNotification("Config Renamed", trim, true);
            }

            this.reloadLocalEntries();
         } catch (IOException e) {
            this.showNotification("Configs", "Save failed: " + e.getMessage(), false);
         }

         this.nameInputOpen = false;
         this.nameInput = "";
         this.renamingConfigKey = null;
      }
   }

   private void revokeShareCode(String preparingShareText) {
      if (this.sharedConfigId != null) {
         CommunityConfigClient.revokeShareCode(this.sharedConfigId, preparingShareText, res -> {
            if (res.ok) {
               this.showNotification("Share Code", preparingShareText + " revoked", false);
               this.loadShareCodes();
            } else {
               this.showNotification("Share", res.errorMessage != null ? res.errorMessage : "Couldn't revoke", false);
            }
         });
      }
   }

   private void loadShareCodes() {
      if (this.sharedConfigId != null) {
         this.shareLoading = true;
         CommunityConfigClient.fetchShareCodes(this.sharedConfigId, res -> {
            this.shareLoading = false;
            if (res.ok) {
               this.shareCodes = res.value;
               this.shareErrorMessage = null;
            } else {
               this.shareErrorMessage = res.errorMessage != null ? res.errorMessage : "Couldn't load codes.";
            }
         });
      }
   }

   private boolean isRowVisibleInScissor(DrawContext context, int vectorTransformValue, int vectorTransformValue2) {
      Matrix3x2fStack matrices = context.getMatrices();
      float vectorTransformValue3 = this.plus + this.panelWidth / 2.0F;
      int rowHOffset = vectorTransformValue2 + 16;

      for (int i = -16; i < rowHOffset + 16; i += 16) {
         matrices.transformPosition(vectorTransformValue3, vectorTransformValue + Math.min(i, rowHOffset), this.scratchVec);
         if (context.scissorContains(Math.round(this.scratchVec.x), Math.round(this.scratchVec.y))) {
            return true;
         }
      }

      return false;
   }

   private void openNameInput() {
      if (!this.nameInputOpen) {
         this.nameInput = "";
      }

      this.nameInputOpen = true;
      this.renamingConfigKey = null;
      this.redeemInputOpen = false;
      this.redeemCodeInput = "";
      this.focusInput();
   }

   private static String formatIsoTimestamp(String configShareText) {
      if (configShareText != null && !configShareText.isEmpty()) {
         try {
            return OffsetDateTime.parse(configShareText).atZoneSameInstant(ZoneId.systemDefault()).format(DATE_FORMAT);
         } catch (RuntimeException e) {
            return null;
         }
      } else {
         return null;
      }
   }

   private void setCommunitySort(int sort) {
      this.searchField.update();
      if (this.sort != sort) {
         this.sort = sort;
         this.reloadCommunityConfigs();
      }
   }

   private boolean isShareViewOpen() {
      return this.tabs == ConfigBrowserPanel.Tab.MINE && this.sharingConfigKey != null;
   }

   private static String truncateStartToWidth(String configShareText, float configShareValue) {
      String matrixStackText = configShareText;

      while (matrixStackText.length() > 1 && MatrixStackUtilsAmount.getAmount3(matrixStackText) > configShareValue) {
         matrixStackText = matrixStackText.substring(1);
      }

      return matrixStackText;
   }

   private static float computeCardWidth(String loadedText, float loadedFactor, int loadedValue) {
      float loadedValue2 = 30.0F;
      float loadedValue3 = (MatrixStackUtilsAmount.getAmount("Loaded") + 5.0F) * loadedFactor;
      String formatText = MatrixStackUtilsAmount.getFontRenderer().formatText(loadedText, loadedValue - 48 - loadedValue2 - loadedValue3);
      return Math.max(44.0F, (float)Math.ceil((loadedValue2 + MatrixStackUtilsAmount.getAmount3(formatText) + loadedValue3) * 2.0F) / 2.0F);
   }

   private void switchTab(ConfigBrowserPanel.Tab tabs) {
      if (this.tabs != tabs) {
         if (this.tabs != ConfigBrowserPanel.Tab.COMMUNITY) {
            this.localSearchText = this.searchField.getSubstring();
         }

         this.tabs = tabs;
         this.tabSwitchTimeMs = System.currentTimeMillis();
         this.searchField.update();
         this.searchField.handleSubstringClear(tabs == ConfigBrowserPanel.Tab.COMMUNITY ? this.search : this.localSearchText);
         this.nameInputOpen = false;
         this.nameInput = "";
         this.renamingConfigKey = null;
         this.redeemInputOpen = false;
         this.redeemCodeInput = "";
         this.inputFocused = false;
         this.sharingConfigKey = null;
         this.shareCodes = new ArrayList<>();
         this.sharedConfigId = null;
         this.shareErrorMessage = null;
         this.shareLoading = false;
         this.like.handle("strip", 0.0F);
         this.pruneStaleAnimations(Set.of());
         this.trackedLocalEntries = null;
         this.renderedCommunityPage = null;
         this.renderedShareCodes = null;
         this.entranceTabKey = -1;
         this.cardEntrance.updateTimeFlag();
         if (tabs != ConfigBrowserPanel.Tab.COMMUNITY) {
            this.reloadLocalEntries();
         }
      }
   }

   public void clearInputFocus() {
      this.searchField.update();
      this.inputFocused = false;
   }

   public boolean isOverlayOpen() {
      return false;
   }

   private static boolean isValidNameChar(char configShareLimit) {
      return configShareLimit >= 'a' && configShareLimit <= 'z' || configShareLimit >= 'A' && configShareLimit <= 'Z' || configShareLimit >= '0' && configShareLimit <= '9' || configShareLimit == ' ' || configShareLimit == '_' || configShareLimit == '-';
   }

   private void renderTabBar(DrawContext context, int tabsPlusValue, int mouseY) {
      float amountForDrawContextString = MatrixStackUtilsAmount.getAmountForDrawContextString(context, "Configs", this.plus, this.tabsTopY, 22.0F);
      float tabsPlusValue4 = MatrixStackUtilsAmount.getAmount(
         context,
         this.contentHits,
         this.like,
         "tabs",
         TAB_LABELS,
         this.tabs.ordinal(),
         amountForDrawContextString + 10.0F,
         this.tabsTopY,
         22.0F,
         tabsPlusValue,
         mouseY,
         index -> this.switchTab(TABS_IN_ORDER[index])
      );
      boolean tabsStrip = this.isShareViewOpen();
      boolean tabsPlusFlag = this.tabs == ConfigBrowserPanel.Tab.MINE && !tabsStrip;
      float tabsPlusBound = this.plus + this.panelWidth;
      if (tabsPlusFlag) {
         tabsPlusBound = MatrixStackUtilsAmount.getAmount(
               context,
               this.contentHits,
               this.like,
               "new",
               "New",
               IcoGenUtils.getIdentifierGEN_CORZCLIENT_MAP("plus"),
               (float)(this.plus + this.panelWidth),
               this.tabsTopY + 2.0F,
               18.0F,
               tabsPlusValue,
               mouseY,
               true,
               this::openNameInput
            )
            - 8.0F
            - 62.0F;
      }

      int ceil5 = (int)Math.ceil(tabsPlusValue4 + 8.0F);
      int tabsPlusValue6 = tabsPlusFlag ? (int)Math.floor(tabsPlusBound - 6.0F) : this.plus + this.panelWidth;
      int tabsPlusLimit = Math.min(124, tabsPlusValue6 - ceil5);
      this.searchVisible = !tabsStrip && tabsPlusLimit >= 56;
      if (this.searchVisible) {
         this.searchField.handleMin(ceil5, this.tabsTopY + 2, tabsPlusLimit, 18);
         this.searchField.renderModulesClear(context, tabsPlusValue, mouseY, 0.0F);
      } else {
         this.searchField.update();
      }

      if (tabsPlusFlag) {
         this.renderToolbarButtons(context, Math.min(tabsPlusBound, this.searchVisible ? ceil5 + tabsPlusLimit + 6.0F : tabsPlusValue4 + 8.0F), tabsPlusValue, mouseY);
      }
   }

   private void renderLocalEntryGrid(DrawContext context, Hits hits, int downloadCommunityValue, int matrixStackLimit, int scaledY, long currentMillis, boolean downloadCommunityFlag) {
      int downloadCommunityValue5 = 1;

      for (int i = 0; i < this.visibleEntries.size(); i++) {
         downloadCommunityValue5 = downloadCommunityValue5 * 31 + this.visibleEntries.get(i).slug().hashCode();
      }

      this.updateEntranceAnimation(this.tabs.ordinal(), downloadCommunityValue5, this.visibleEntries.size());
      boolean rowVisibleInScissor2 = true;

      for (int j = 0; j < this.visibleEntries.size(); j++) {
         int matrixStackFactor = j % 2;
         int valueForInt = MatrixStackUtilsAmount.getValueForInt(this.plus, this.panelWidth, 2, 4, matrixStackFactor);
         int loadedValue = MatrixStackUtilsAmount.getValue(this.plus, this.panelWidth, 2, 4, matrixStackFactor);
         int vectorTransformValue = downloadCommunityValue + j / 2 * 64;
         if (matrixStackFactor == 0) {
            rowVisibleInScissor2 = this.isRowVisibleInScissor(context, vectorTransformValue, 60);
         }

         if (rowVisibleInScissor2) {
            Entrance amountFlag = this.cardEntrance.getAmountFlag(j);
            if (!amountFlag.isAmount()) {
               float tabsSettingsAmount = amountFlag.tabsSettingsAmount;
               float downloadCommunityLimit = amountFlag.entranceProgress;
               Hits hits2 = amountFlag.matrixScissorActive ? hits : Hits.INSTANCE;
               int downloadCommunityValue10 = amountFlag.activeFlag ? matrixStackLimit : this.plus - 4000;
               ConfigBrowserPanel.ConfigListEntry communitydownloaditem = this.visibleEntries.get(j);
               float configShareValue = this.like.getAmount(buildAnimKey(communitydownloaditem.slug(), "active"), communitydownloaditem.slug().equals(this.activeConfigKey) ? 1.0F : 0.0F, 14.0F);
               boolean translatePush = MatrixStackUtilsAmount.canTranslatePush(context, valueForInt + loadedValue / 2.0F, vectorTransformValue + 30.0F, amountFlag.matrixScissorAmount);

               try {
                  this.renderCardBackground(context, communitydownloaditem.name(), configShareValue, tabsSettingsAmount, valueForInt, vectorTransformValue, loadedValue);
                  if (downloadCommunityLimit > 0.002F) {
                     float panelOverlay = CorzRoundedUtils.getPanelOverlay(downloadCommunityLimit);

                     try {
                        this.renderLocalConfigCard(context, hits2, communitydownloaditem, configShareValue, valueForInt, vectorTransformValue, loadedValue, downloadCommunityValue10, scaledY, currentMillis, downloadCommunityFlag);
                     } finally {
                        CorzRoundedUtils.handlePanelOverlay(panelOverlay);
                     }
                  }
               } finally {
                  MatrixStackUtilsAmount.renderPopMatrices(context, translatePush);
               }
            }
         }
      }
   }

   private void submitInput() {
      if (this.nameInputOpen) {
         this.submitConfigName();
      } else {
         this.redeemEnteredCode();
      }

      if (this.nameInputOpen || this.redeemInputOpen) {
         this.focusInput();
      }
   }

   public int getContentHeight() {
      return 28 + this.getTabContentHeight();
   }

   private int getInputBarHeight() {
      return Math.round(this.like.getAmount("strip") * 24.0F);
   }

   private static float easeOutCubic(float configShareValue) {
      configShareValue = clampForFloat(configShareValue);
      float configShareFactor = 1.0F - configShareValue;
      return 1.0F - configShareFactor * configShareFactor * configShareFactor;
   }

   private void reloadLocalEntries() {
      this.allEntries = PresetFileStore.listPresets();
   }

   private void renderCommunityConfigCard(
      DrawContext context, Hits hits, SharedConfigEntry versionJsonUtils, String downloadVerifiedText, int downloadVerifiedValue, int vectorTransformValue, int loadedValue, int matrixStackLimit, int mouseY, float downloadVerifiedValue6, float downloadVerifiedValue7
   ) {
      float loadedFolderfill = this.renderConfigCardHeader(context, versionJsonUtils.id, nullToEmpty(versionJsonUtils.name), 0.0F, downloadVerifiedValue, vectorTransformValue, loadedValue, matrixStackLimit, mouseY);
      float verifiedWOffset = downloadVerifiedValue + 8;
      float downloadVerifiedValue8 = downloadVerifiedValue + loadedValue - 8;
      float verifiedWOffset9 = verifiedWOffset;
      float cardOffset = loadedFolderfill + 6.0F;
      String matrixStackText = versionJsonUtils.uniqueDownloadCount + (versionJsonUtils.uniqueDownloadCount == 1L ? " download" : " downloads");
      float downloadVerifiedValue11 = MatrixStackUtilsAmount.getAmount(matrixStackText) + (versionJsonUtils.verified ? 4.0F + downloadVerifiedValue6 : 0.0F) + (versionJsonUtils.owner ? 4.0F + downloadVerifiedValue7 : 0.0F);
      String nullToEmpty2 = nullToEmpty(versionJsonUtils.author);
      if (!nullToEmpty2.isEmpty()) {
         verifiedWOffset9 = verifiedWOffset + (MatrixStackUtilsAmount.getAmount(context, "by " + nullToEmpty2, verifiedWOffset, cardOffset, downloadVerifiedValue8 - verifiedWOffset - downloadVerifiedValue11 - 4.0F) + 4.0F);
      }

      float downloadVerifiedTotal = verifiedWOffset9 + (MatrixStackUtilsAmount.getAmount(context, matrixStackText, verifiedWOffset9, cardOffset, downloadVerifiedValue8 - verifiedWOffset9) + 4.0F);
      if (versionJsonUtils.verified) {
         downloadVerifiedTotal += MatrixStackUtilsAmount.getAmount(context, "Verified", downloadVerifiedTotal, cardOffset, -11755009) + 4.0F;
      }

      if (versionJsonUtils.owner) {
         MatrixStackUtilsAmount.getAmount(context, "Yours", downloadVerifiedTotal, cardOffset, MatrixEspUtils.releaseRightValue);
      }

      float matrixStackValue = vectorTransformValue + 60 - 6.0F - 16.0F;
      float deleteTrashValue = MatrixStackUtilsAmount.getAmount(
         context,
         hits,
         this.like,
         buildAnimKey(versionJsonUtils.id, "get"),
         "Get",
         IcoGenUtils.getIdentifierGEN_CORZCLIENT_MAP("download"),
         downloadVerifiedValue8,
         matrixStackValue,
         16.0F,
         matrixStackLimit,
         mouseY,
         true,
         () -> this.downloadCommunityConfig(versionJsonUtils)
      );
      if (versionJsonUtils.owner) {
         deleteTrashValue -= 19.0F;
         this.renderDeleteButton(
            context,
            hits,
            buildAnimKey(versionJsonUtils.id, "delete"),
            deleteTrashValue,
            matrixStackValue,
            versionJsonUtils.id != null && versionJsonUtils.id.equals(this.pendingConfirmKey) && "cdelete".equals(this.apply),
            matrixStackLimit,
            mouseY,
            () -> this.armOrConfirmAction(versionJsonUtils.id, "cdelete", () -> this.deleteCommunityConfig(versionJsonUtils))
         );
      }

      float likeUnlike = this.renderLikeButton(context, hits, versionJsonUtils, deleteTrashValue - 3.0F, matrixStackValue, matrixStackLimit, mouseY);
      this.renderTimestampLabel(context, downloadVerifiedText, verifiedWOffset, matrixStackValue, likeUnlike - 6.0F);
   }

   private void openShareView(String stripText) {
      ConfigPresetFile jsonVersion;
      try {
         jsonVersion = PresetFileStore.readPresetByName(stripText);
      } catch (IOException e) {
         jsonVersion = null;
      }

      this.sharingConfigKey = stripText;
      this.sharedConfigName = jsonVersion != null && jsonVersion.name != null ? jsonVersion.name : stripText;
      this.entranceTabKey = -1;
      this.like.handle("strip", 0.0F);
      this.sharedConfigId = null;
      this.shareErrorMessage = null;
      this.shareLoading = true;
      this.newestShareCode = null;
      this.shareCodes = new ArrayList<>();
      if (!ConfigSyncUtils.isSignedIn()) {
         this.shareLoading = false;
         this.shareErrorMessage = "Sign in to share configs.";
      } else {
         ConfigSyncUtils.uploadPrivateConfig(stripText, res -> {
            if (!res.ok) {
               this.shareLoading = false;
               this.shareErrorMessage = res.errorMessage != null ? res.errorMessage : "Couldn't prepare this config.";
            } else {
               this.sharedConfigId = res.configId;
               this.loadShareCodes();
            }
         });
      }
   }

   private void showNotification(String configShareText, String substringText, boolean configShareFlag) {
      DisabledEnabled.getInstance().handleDisabledEnabled(configShareText, substringText, configShareFlag);
   }

   private void reloadCommunityConfigs() {
      this.communityLoading = true;
      this.communityLoadError = null;
      this.communityPage = null;
      long configShareValue = ++this.communityRequestId;
      var linkedHashMap = new LinkedHashMap();
      linkedHashMap.put("sort", SORT_API_KEYS[this.sort]);
      if (!this.search.isBlank()) {
         linkedHashMap.put("search", this.search.trim());
      }

      linkedHashMap.put("pageSize", "20");
      CommunityConfigClient.searchConfigs(linkedHashMap, req -> {
         if (configShareValue == this.communityRequestId) {
            this.communityLoading = false;
            if (req.ok) {
               this.communityPage = req.value;
               this.communityLoadError = null;
            } else {
               this.communityLoadError = req.errorMessage != null ? req.errorMessage : "Couldn't load configs.";
            }
         }
      });
   }

   private void updateEntranceAnimation(int stripMineValue, int hashCode, int value) {
      if (stripMineValue != this.entranceTabKey || hashCode != this.lastEntriesHash) {
         if (stripMineValue == this.entranceTabKey && value > 0 && value <= this.lastEntryCount) {
            this.cardEntrance.updateTimeFlagEntranceValue();
         } else {
            this.cardEntrance.updateTimeFlag();
         }

         this.entranceTabKey = stripMineValue;
         this.lastEntriesHash = hashCode;
         this.lastEntryCount = value;
      }
   }

   private static String buildAnimKey(String configShareText, String configShareText2) {
      return "i:" + configShareText + '|' + configShareText2;
   }

   public void renderTooltip(DrawContext context) {
      long nanoTime = System.nanoTime();
      if (this.hoverTooltipText != null && nanoTime - this.lastRenderNanos <= 400000000L) {
         if (!this.hoverTooltipText.equals(this.lastTooltipText)) {
            this.lastTooltipText = this.hoverTooltipText;
            this.tooltipChangeNanos = "Click again to delete".equals(this.hoverTooltipText) ? nanoTime - 350000000L : nanoTime;
         }

         float deleteClickValue = clampForFloat((float)(nanoTime - this.tooltipChangeNanos - 350000000L) / 1.2E8F);
         if (!(deleteClickValue <= 0.0F)) {
            float deleteClickValue2 = (float)Math.ceil(MatrixStackUtilsAmount.getAmount(this.hoverTooltipText, 0.82F)) + 12.0F;
            float materialFactor = 15.0F;
            float matrixPeekValue = Math.max(this.tooltipMinX, Math.min(this.tooltipMaxX - deleteClickValue2, this.tooltipCenterX - deleteClickValue2 / 2.0F));
            float matrixStackValue = this.tooltipBelow ? this.tooltipY : this.tooltipY - materialFactor;
            float panelOverlay = CorzRoundedUtils.getPanelOverlay(deleteClickValue);

            try {
               MatrixPeekUtilsRender.renderForDrawContextFloat(context, matrixPeekValue, matrixStackValue, deleteClickValue2, materialFactor, 5.0F, -65990120, 620756991);
               MatrixStackUtilsAmount.handlePushScale(this.hoverTooltipText, matrixPeekValue + 6.0F, MatrixStackUtilsAmount.getAmount(matrixStackValue, materialFactor, 0.82F), 0.82F, -920587);
            } finally {
               CorzRoundedUtils.handlePanelOverlay(panelOverlay);
            }
         }
      } else {
         this.lastTooltipText = null;
      }
   }

   private void onSearchTextChanged() {
      if (this.tabs == ConfigBrowserPanel.Tab.COMMUNITY) {
         String search = this.searchField.getSubstring();
         if (search.length() > 64) {
            search = search.substring(0, 64);
            this.searchField.handleSubstringClear(search);
         }

         if (!search.equals(this.search)) {
            this.search = search;
            this.searchDebounceStartMs = System.currentTimeMillis();
         }
      }
   }

   private void renderToolbarButtons(DrawContext context, float autosaveImportValue, int matrixStackLimit, int scaledY) {
      float identifierText = 18.0F;
      float targetOffset = this.tabsTopY + (22.0F - identifierText) / 2.0F;
      float autosaveImportValue6 = identifierText + 4.0F;
      MatrixStackUtilsAmount.render(
         context, this.contentHits, this.like, "import", IcoGenUtils.getIdentifierGEN_CORZCLIENT_MAP("download"), autosaveImportValue, targetOffset, identifierText, 12.0F, matrixStackLimit, scaledY, false, this::importConfigFromFile
      );
      this.setHoverTooltip("Import from file", autosaveImportValue, targetOffset, identifierText, identifierText, matrixStackLimit, scaledY, true);
      float matrixStackValue = autosaveImportValue + autosaveImportValue6;
      MatrixStackUtilsAmount.render(
         context, this.contentHits, this.like, "redeem", IcoGenUtils.getIdentifierGEN_CORZCLIENT_MAP("ticket"), matrixStackValue, targetOffset, identifierText, 12.0F, matrixStackLimit, scaledY, false, this::openRedeemInput
      );
      this.setHoverTooltip("Redeem share code", matrixStackValue, targetOffset, identifierText, identifierText, matrixStackLimit, scaledY, true);
      boolean base = this.isAutosaveEnabled();
      float releaseRightValue = matrixStackValue + autosaveImportValue6;
      MatrixStackUtilsAmount.renderReleaseRight(
         context,
         this.contentHits,
         this.like,
         "autosave",
         IcoGenUtils.getIdentifierGEN_CORZCLIENT_MAP("save"),
         releaseRightValue,
         targetOffset,
         identifierText,
         12.0F,
         matrixStackLimit,
         scaledY,
         false,
         this.like.getAmount("autosave:on", base ? 1.0F : 0.0F, 16.0F),
         this::toggleAutosave
      );
      this.setHoverTooltip(base ? "Autosave is on" : "Autosave is off", releaseRightValue, targetOffset, identifierText, identifierText, matrixStackLimit, scaledY, true);
   }

   public void onMouseReleased(double configShareValue, double mouseReleasedValue, int button) {
   }

   private void toggleLike(SharedConfigEntry versionJsonUtils) {
      boolean liked = !versionJsonUtils.liked;
      versionJsonUtils.liked = liked;
      versionJsonUtils.likeCount = Math.max(0L, versionJsonUtils.likeCount + (liked ? 1 : -1));
      CommunityConfigClient.setConfigLiked(versionJsonUtils.id, liked, result -> {
         if (result.ok) {
            versionJsonUtils.liked = liked;
            versionJsonUtils.likeCount = (Long)result.value;
         } else {
            versionJsonUtils.liked = !liked;
            versionJsonUtils.likeCount = Math.max(0L, versionJsonUtils.likeCount + (liked ? -1 : 1));
            this.showNotification("Like", result.errorMessage != null ? result.errorMessage : "Couldn't update like", false);
         }
      });
   }

   private void publishToCommunity(String publishPublishingText) {
      if (!ConfigSyncUtils.isSignedIn()) {
         this.showNotification("Publish", "A valid license is required to publish", false);
      } else {
         ConfigPresetFile jsonVersion;
         try {
            jsonVersion = PresetFileStore.readPresetByName(publishPublishingText);
         } catch (IOException e) {
            jsonVersion = null;
         }

         if (jsonVersion == null) {
            this.showNotification("Publish", "Config not found", false);
         } else {
            ConfigPresetFile jsonVersion2 = jsonVersion;
            this.showNotification("Publishing\u2026", jsonVersion2.name, true);
            ConfigSyncUtils.uploadPrivateConfig(publishPublishingText, local -> {
               if (!local.ok) {
                  this.showNotification("Publish Failed", local.errorMessage != null ? local.errorMessage : "Couldn't reach the server", false);
               } else {
                  String couldnPrepareText = local.configId;
                  Runnable var4x = () -> CommunityConfigClient.setConfigVisibility(couldnPrepareText, "PUBLIC", localxx -> {
                     if (localxx.ok) {
                        this.showNotification("Submitted", jsonVersion2.name + " \u2014 public once staff verify it", true);
                     } else {
                        this.showNotification("Publish Failed", localxx.errorMessage != null ? localxx.errorMessage : localxx.errorCode, false);
                     }
                  });
                  CommunityConfigClient.fetchConfig(couldnPrepareText, got -> {
                     String var5x = got.ok && got.value != null ? got.value.sha256 : null;
                     ConfigPresetFile applyUploadSafetyRules = CommunityConfigClient.applyUploadSafetyRules(jsonVersion2);
                     if (var5x != null && applyUploadSafetyRules != null && var5x.equalsIgnoreCase(applyUploadSafetyRules.computeShaString())) {
                        var4x.run();
                     } else {
                        CommunityConfigClient.uploadConfigVersion(couldnPrepareText, jsonVersion2, "Publish", promotex -> {
                           if (!promotex.ok) {
                              this.showNotification("Publish Failed", promotex.errorMessage != null ? promotex.errorMessage : promotex.errorCode, false);
                           } else {
                              var4x.run();
                           }
                        });
                     }
                  });
               }
            });
         }
      }
   }

   private void renderNoticeBlock(DrawContext context, Hits hits, int plusDismissValue, int matrixStackLimit, int scaledY) {
      var keptTurned = this.getWrappedNoticeLines();
      if (!keptTurned.isEmpty()) {
         float amountForString = MatrixStackUtilsAmount.getAmountForString("");

         for (int i = 0; i < keptTurned.size(); i++) {
            float priceValue = plusDismissValue + i * 10;
            if (i == 0) {
               MatrixStackUtilsAmount.getAmount(context, -2054070, -5854026, (String)keptTurned.get(i), this.plus, priceValue);
            } else {
               MatrixStackUtilsAmount.handlePushScale((String)keptTurned.get(i), this.plus + amountForString, priceValue, 0.82F, -5854026);
            }
         }

         float segmentSlider = keptTurned.size() * 10;
         hits.handle(this.plus, plusDismissValue, this.panelWidth, segmentSlider, () -> {
            this.keptForSafetyMessage = null;
            this.notTurnedOnMessage = null;
         });
         this.setHoverTooltip("Click to dismiss", this.plus, plusDismissValue, this.panelWidth, segmentSlider, matrixStackLimit, scaledY, true);
      }
   }

   private void renderLocalConfigCard(
      DrawContext context, Hits hits, ConfigBrowserPanel.ConfigListEntry localView, float shareLoadValue, int loadedFolderfillValue, int vectorTransformValue, int loadedValue, int matrixStackLimit, int scaledY, long currentMillis, boolean shareLoadFlag
   ) {
      String slug = localView.slug();
      float loadedFolderfill = this.renderConfigCardHeader(context, slug, localView.name(), shareLoadValue, loadedFolderfillValue, vectorTransformValue, loadedValue, matrixStackLimit, scaledY);
      float targetOffset = loadedFolderfillValue + 8;
      float releaseRightValue = loadedFolderfillValue + loadedValue - 8;
      float matrixStackValue = targetOffset + MatrixStackUtilsAmount.getAmount(context, localView.modules(), targetOffset, loadedFolderfill + 6.0F, releaseRightValue - targetOffset) + 4.0F;
      if (localView.origin() != null) {
         MatrixStackUtilsAmount.getAmount(context, localView.origin(), matrixStackValue, loadedFolderfill + 6.0F, releaseRightValue - matrixStackValue);
      }

      float shareLoadValue10 = vectorTransformValue + 60 - 6.0F - 16.0F;
      boolean equals = slug.equals(this.pendingConfirmKey);
      boolean shareLoadFlag2 = equals && "apply".equals(this.apply);
      boolean shareLoadFlag3 = slug.equals(this.appliedConfigKey) && currentMillis < this.appliedBadgeDeadlineMs;
      float releaseRight = MatrixStackUtilsAmount.getReleaseRight(
         context,
         hits,
         this.like,
         buildAnimKey(slug, "load"),
         shareLoadFlag2 ? "Confirm" : (shareLoadFlag3 ? "Applied" : "Load"),
         !shareLoadFlag2 && !shareLoadFlag3 ? IcoGenUtils.getMplayIco() : null,
         releaseRightValue,
         shareLoadValue10,
         16.0F,
         50.0F,
         matrixStackLimit,
         scaledY,
         true,
         this.like.getAmount(buildAnimKey(slug, "armed"), shareLoadFlag2 ? 1.0F : 0.0F, 16.0F),
         () -> this.armOrConfirmAction(slug, "apply", () -> this.applyLocalConfig(slug))
      );
      float shareLoadTotal = releaseRight - 19.0F;
      this.renderDeleteButton(
         context,
         hits,
         buildAnimKey(slug, "delete"),
         shareLoadTotal,
         shareLoadValue10,
         equals && "delete".equals(this.apply),
         matrixStackLimit,
         scaledY,
         () -> this.armOrConfirmAction(slug, "delete", () -> this.deleteLocalConfig(slug))
      );
      if (shareLoadFlag && localView.local()) {
         shareLoadTotal -= 19.0F;
         MatrixStackUtilsAmount.render(
            context,
            hits,
            this.like,
            buildAnimKey(slug, "publish"),
            IcoGenUtils.getIdentifierGEN_CORZCLIENT_MAP("globe"),
            shareLoadTotal,
            shareLoadValue10,
            16.0F,
            10.0F,
            matrixStackLimit,
            scaledY,
            false,
            () -> this.publishToCommunity(slug)
         );
         this.setHoverTooltip("Publish to community", shareLoadTotal, shareLoadValue10, 16.0F, 16.0F, matrixStackLimit, scaledY, false);
      }

      if (shareLoadFlag) {
         shareLoadTotal -= 19.0F;
         MatrixStackUtilsAmount.render(
            context,
            hits,
            this.like,
            buildAnimKey(slug, "share"),
            IcoGenUtils.getIdentifierGEN_CORZCLIENT_MAP("share"),
            shareLoadTotal,
            shareLoadValue10,
            16.0F,
            10.0F,
            matrixStackLimit,
            scaledY,
            false,
            () -> this.openShareView(slug)
         );
         this.setHoverTooltip("Share with a code", shareLoadTotal, shareLoadValue10, 16.0F, 16.0F, matrixStackLimit, scaledY, false);
      }

      this.renderTimestampLabel(context, localView.stamp(), targetOffset, shareLoadValue10, shareLoadTotal - 6.0F);
   }

   private static boolean isSearchShortcutPressed() {
      Window window = MinecraftClient.getInstance().getWindow();
      return !InputUtil.isKeyPressed(window, 75)
         ? false
         : InputUtil.isKeyPressed(window, 341) || InputUtil.isKeyPressed(window, 345) || InputUtil.isKeyPressed(window, 343) || InputUtil.isKeyPressed(window, 347);
   }

   private void toggleAutosave() {
      Configs configs = this.getConfigsModule();
      if (configs != null) {
         configs.booleanBase.handle(!configs.booleanBase.isEnabled());
         this.showNotification("Autosave", configs.booleanBase.isEnabled() ? "On \u2014 active config stays in sync" : "Off", configs.booleanBase.isEnabled());
      }
   }

   private static String getUsername() {
      try {
         return MinecraftClient.getInstance().getSession().getUsername();
      } catch (Throwable e) {
         return "You";
      }
   }

   private List<String> getWrappedNoticeLines() {
      var arrayList = new ArrayList();
      if (this.tabs != ConfigBrowserPanel.Tab.COMMUNITY && !this.isShareViewOpen()) {
         float keptTurnedValue = Math.max(40.0F, this.panelWidth - MatrixStackUtilsAmount.getAmountForString("") - 2.0F);

         for (String keptTurnedText : new String[]{this.keptForSafetyMessage, this.notTurnedOnMessage}) {
            if (keptTurnedText != null) {
               StringBuilder stringBuilder = new StringBuilder();

               for (String keptTurnedText2 : keptTurnedText.split(" ")) {
                  String matrixStackText = stringBuilder.length() == 0 ? keptTurnedText2 : stringBuilder + " " + keptTurnedText2;
                  if (stringBuilder.length() > 0 && MatrixStackUtilsAmount.getAmount(matrixStackText, 0.82F) > keptTurnedValue) {
                     arrayList.add(stringBuilder.toString());
                     stringBuilder = new StringBuilder(keptTurnedText2);
                  } else {
                     stringBuilder = new StringBuilder(matrixStackText);
                  }
               }

               if (stringBuilder.length() > 0) {
                  arrayList.add(stringBuilder.toString());
               }
            }
         }

         return arrayList;
      } else {
         return arrayList;
      }
   }

   private void filterLocalEntries() {
      var allEntries4 = this.allEntries;
      if (allEntries4 != this.cachedEntryList) {
         this.rebuildLocalEntries(allEntries4);
      }

      String lowerCase = this.searchField.getSubstring().trim().toLowerCase(Locale.ROOT);
      this.visibleEntries.clear();
      this.unfilteredConfigCount = 0;

      for (ConfigBrowserPanel.ConfigListEntry localView : this.localEntries) {
         if (this.tabs != ConfigBrowserPanel.Tab.DOWNLOADS || localView.community()) {
            this.unfilteredConfigCount++;
            if (lowerCase.isEmpty() || localView.key().contains(lowerCase)) {
               this.visibleEntries.add(localView);
            }
         }
      }
   }

   private void renderCardBackground(DrawContext context, String configShareText, float configShareValue, float panelOverlayLimit, int releaseRightValue, int vectorTransformValue, int loadedValue) {
      if (!(panelOverlayLimit <= 0.004F)) {
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(panelOverlayLimit);

         try {
            MatrixStackUtilsAmount.getReleaseRight(context, releaseRightValue, vectorTransformValue, loadedValue, 60.0F, computeCardWidth(configShareText, configShareValue, loadedValue), 0.0F, 0.0F);
         } finally {
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
         }
      }
   }

   private boolean isSharePlaceholderShown(List<CommunityConfigClient.ShareCode> list) {
      return this.shareLoading && this.sharedConfigId == null || this.shareErrorMessage != null || list.isEmpty();
   }

   private void copyShareCode(String shareCodeText) {
      this.copyToClipboard(shareCodeText);
      this.copiedShareCode = shareCodeText;
      this.copiedCodeDeadlineMs = System.currentTimeMillis() + 1400L;
      this.showNotification("Share Code", shareCodeText + " copied", true);
   }

   private void pruneStaleAnimations(Set<String> set) {
      this.like.handle(livex -> {
         if (!livex.startsWith("i:")) {
            return true;
         } else {
            int indexOf = livex.indexOf(124);
            return indexOf > 0 && set.contains(livex.substring("i:".length(), indexOf));
         }
      });
   }

   private void renderDeleteButton(DrawContext context, Hits hits, String deleteTrashText, float deleteTrashValue, float targetOffset, boolean deleteTrashFlag, int matrixStackLimit, int mouseY, Runnable runnable) {
      MatrixStackUtilsAmount.renderReleaseRight(
         context,
         hits,
         this.like,
         deleteTrashText,
         IcoGenUtils.getIdentifierGEN_CORZCLIENT_MAP("trash"),
         deleteTrashValue,
         targetOffset,
         16.0F,
         10.0F,
         matrixStackLimit,
         mouseY,
         true,
         this.like.getAmount(deleteTrashText + ":arm", deleteTrashFlag ? 1.0F : 0.0F, 18.0F),
         runnable
      );
      this.setHoverTooltip(deleteTrashFlag ? "Click again to delete" : "Delete", deleteTrashValue, targetOffset, 16.0F, 16.0F, matrixStackLimit, mouseY, false);
   }

   private void renderSortBar(DrawContext context, int configsLikeValue, int matrixStackLimit, int scaledY, ConfigsPage configsJsonUtils) {
      float plus = this.plus;
      int releaseRightValue = CardMatrixUtils.getValue(MatrixEspUtils.releaseRightValue, -1, 0.3);

      for (int i = 0; i < SORT_LABELS.length; i++) {
         String configsLikeText = SORT_LABELS[i];
         float identifierText = (float)Math.ceil(MatrixStackUtilsAmount.getAmount(configsLikeText, 0.82F)) + 14.0F;
         boolean configsLikeFlag = this.sort == i;
         float configsLikeLimit = this.like.getAmount("sort:h" + i, !configsLikeFlag && MatrixStackUtilsAmount.canFlag(matrixStackLimit, scaledY, plus, configsLikeValue, identifierText, 14.0F));
         float cardMatrixFactor = this.like.getAmount("sort:on" + i, configsLikeFlag ? 1.0F : 0.0F, 16.0F);
         boolean translatePush = MatrixStackUtilsAmount.canTranslatePush(context, plus + identifierText / 2.0F, configsLikeValue + 7.0F, this.like.getTime(SORT_ANIM_KEYS[i], 0.07F));
         if (cardMatrixFactor > 0.01F) {
            MatrixPeekUtilsRender.renderForDrawContextFloat(
               context,
               plus,
               configsLikeValue,
               identifierText,
               14.0F,
               5.0F,
               MatrixStackUtilsAmount.getValue(MatrixEspUtils.releaseRightValue, 0.16F * cardMatrixFactor),
               MatrixStackUtilsAmount.getValue(MatrixEspUtils.releaseRightValue, 0.42F * cardMatrixFactor)
            );
         } else if (configsLikeLimit > 0.01F) {
            MatrixPeekUtilsRender.renderMatrixPeek(context, plus, configsLikeValue, identifierText, 14.0F, 5.0F, MatrixStackUtilsAmount.getValue(-1, 0.06F * configsLikeLimit));
         }

         MatrixStackUtilsAmount.handlePushScale(
            configsLikeText,
            plus + 7.0F,
            MatrixStackUtilsAmount.getAmount(configsLikeValue, 14.0F, 0.82F),
            0.82F,
            CardMatrixUtils.getValue(CardMatrixUtils.getValue(-7630437, -920587, configsLikeLimit), releaseRightValue, cardMatrixFactor)
         );
         MatrixStackUtilsAmount.renderPopMatrices(context, translatePush);
         int configsLikeValue6 = i;
         this.contentHits.handleFlag(plus, configsLikeValue - 1.0F, identifierText, 16.0F, () -> this.setCommunitySort(configsLikeValue6), this.like, SORT_ANIM_KEYS[i]);
         plus += identifierText + 3.0F;
      }

      if (configsJsonUtils != null && !configsJsonUtils.configs.isEmpty()) {
         int resultsconfigsitemsSize = configsJsonUtils.configs.size();
         String scaleText = configsJsonUtils.total > resultsconfigsitemsSize ? "Showing " + resultsconfigsitemsSize + " of " + configsJsonUtils.total : resultsconfigsitemsSize + (resultsconfigsitemsSize == 1 ? " config" : " configs");
         MatrixStackUtilsAmount.handlePushScale(
            scaleText,
            this.plus + this.panelWidth - MatrixStackUtilsAmount.getAmount(scaleText, 0.82F),
            MatrixStackUtilsAmount.getAmount(configsLikeValue, 14.0F, 0.82F),
            0.82F,
            -11117467
         );
      }
   }

   private void armOrConfirmAction(String cdeleteApplyText, String apply, Runnable runnable) {
      long currentMillis = System.currentTimeMillis();
      if (cdeleteApplyText.equals(this.pendingConfirmKey) && apply.equals(this.apply) && currentMillis <= this.confirmExpiryMs) {
         this.pendingConfirmKey = null;
         this.apply = null;
         runnable.run();
      } else {
         this.pendingConfirmKey = cdeleteApplyText;
         this.apply = apply;
         this.confirmExpiryMs = currentMillis + 2500L;
      }
   }

   private void redeemEnteredCode() {
      String trim = this.redeemCodeInput.trim();
      if (trim.length() < 4) {
         this.showNotification("Redeem", "Enter a valid share code", false);
      } else {
         this.redeemInputOpen = false;
         this.redeemCodeInput = "";
         this.showNotification("Redeeming\u2026", trim, true);
         CommunityConfigClient.redeemShareCode(trim, res -> {
            if (!res.ok) {
               this.showNotification("Redeem Failed", res.errorMessage != null ? res.errorMessage : res.errorCode, false);
            } else {
               try {
                  Result[] results = new Result[1];
                  ConfigPresetFile jsonVersion = ConfigPresetFile.parseBytes(res.value.bytes, results);
                  if (jsonVersion == null) {
                     this.showNotification("Redeem Failed", "Config failed validation", false);
                     return;
                  }

                  String saveAsUnsealedPreset = PresetFileStore.saveAsUnsealedPreset(jsonVersion);
                  SourceMeta sourceMeta = new SourceMeta();
                  sourceMeta.source = Source.IMPORTED;

                  try {
                     PresetFileStore.saveSourceMeta(saveAsUnsealedPreset, sourceMeta);
                  } catch (IOException e) {
                  }

                  this.showNotification("Config Added", nullToEmpty(jsonVersion.name) + " \u2014 imported from code", true);
                  this.reloadLocalEntries();
               } catch (IOException e2) {
                  this.showNotification("Redeem Failed", e2.getMessage(), false);
               }
            }
         });
      }
   }

   private static int countEnabledModules(JsonObject json) {
      if (json != null && json.has("modules") && json.get("modules").isJsonObject()) {
         ConfigSharePolicy instance = ConfigSharePolicy.getInstance();
         int modulesJson = 0;

         for (java.util.Map.Entry entry : json.getAsJsonObject("modules").entrySet()) {
            if (((JsonElement)entry.getValue()).isJsonObject() && !AutoRiggerUtils.isContains((String)entry.getKey())) {
               String lowerCase = AutoRiggerUtils.getLowerCase((String)entry.getKey());
               if (!instance.isInAnySection(lowerCase) && instance.isRoleAllowed(instance.getModuleRole(lowerCase, false), true)) {
                  JsonElement asjsonobject = ((JsonElement)entry.getValue()).getAsJsonObject().get("enabled");
                  if (asjsonobject != null && asjsonobject.isJsonPrimitive() && asjsonobject.getAsJsonPrimitive().isBoolean() && asjsonobject.getAsBoolean()) {
                     modulesJson++;
                  }
               }
            }
         }

         return modulesJson;
      } else {
         return 0;
      }
   }

   private boolean isAutosaveEnabled() {
      Configs configs = this.getConfigsModule();
      return configs != null && configs.booleanBase.isEnabled();
   }

   private static int computeGridHeight(int configShareValue, int configShareDivisor, int configShareFactor) {
      int configShareFactor2 = (configShareValue + configShareDivisor - 1) / configShareDivisor;
      return configShareFactor2 * configShareFactor + Math.max(0, configShareFactor2 - 1) * 4;
   }

   private void createShareCode(Integer integer) {
      if (this.sharedConfigId == null) {
         this.shareErrorMessage = "Still preparing\u2026";
      } else {
         this.shareLoading = true;
         CommunityConfigClient.createShareCode(this.sharedConfigId, integer, res -> {
            this.shareLoading = false;
            if (res.ok) {
               this.newestShareCode = res.value.code;
               this.copyToClipboard(res.value.code);
               this.showNotification("Share Code", res.value.code + " copied \u2014 usage " + res.value.getUsageLabel(), true);
               this.loadShareCodes();
            } else {
               this.showNotification("Share", res.errorMessage != null ? res.errorMessage : "Couldn't mint a code", false);
            }
         });
      }
   }

   private void renderLocalTab(DrawContext context, int stripDownloadValue, int stripValue, int mouseY, long currentMillis) {
      this.filterLocalEntries();
      if (this.localEntries != this.trackedLocalEntries) {
         this.trackedLocalEntries = this.localEntries;
         var hashSet = new HashSet();

         for (ConfigBrowserPanel.ConfigListEntry localView : this.localEntries) {
            hashSet.add(localView.slug());
         }

         this.pruneStaleAnimations(hashSet);
      }

      boolean stripDownloadFlag = this.tabs == ConfigBrowserPanel.Tab.MINE;
      boolean focusPlusFlag = stripDownloadFlag && (this.nameInputOpen || this.redeemInputOpen);
      float stripDownloadLimit = stripDownloadFlag ? this.like.getAmount("strip", focusPlusFlag ? 1.0F : 0.0F, 20.0F) : 0.0F;
      if (focusPlusFlag) {
         this.inputIsConfigName = this.nameInputOpen;
         this.inputBarText = this.nameInputOpen ? this.nameInput : this.redeemCodeInput;
      }

      if (focusPlusFlag || stripDownloadLimit > 0.01F) {
         this.renderInputBar(context, stripDownloadValue, stripValue, mouseY, stripDownloadLimit, focusPlusFlag);
      }

      int plusDismissValue = stripDownloadValue + (stripDownloadFlag ? this.getInputBarHeight() : 0);
      this.renderNoticeBlock(context, focusPlusFlag ? this.inputBarHits : this.contentHits, plusDismissValue, focusPlusFlag ? this.plus - 4000 : stripValue, mouseY);
      int downloadCommunityValue = plusDismissValue + this.getNoticeHeight();
      Hits hits = focusPlusFlag ? this.inputBarHits : this.contentHits;
      int matrixStackLimit = focusPlusFlag ? this.plus - 4000 : stripValue;
      float panelOverlay = CorzRoundedUtils.getPanelOverlay(1.0F - 0.45F * stripDownloadLimit);

      try {
         if (this.visibleEntries.isEmpty()) {
            this.entranceTabKey = -1;
         }

         if (this.unfilteredConfigCount == 0) {
            if (stripDownloadFlag) {
               MatrixStackUtilsAmount.renderMatricesPop(
                  context,
                  hits,
                  this.like,
                  "empty:mine",
                  this.plus,
                  downloadCommunityValue,
                  this.panelWidth,
                  120.0F,
                  IcoGenUtils.getIdentifierGEN_CORZCLIENT_MAP("folder"),
                  "No saved configs",
                  "Use New to snapshot your current setup.",
                  "New config",
                  matrixStackLimit,
                  mouseY,
                  this::openNameInput
               );
            } else {
               MatrixStackUtilsAmount.renderMatricesPop(
                  context,
                  hits,
                  this.like,
                  "empty:downloads",
                  this.plus,
                  downloadCommunityValue,
                  this.panelWidth,
                  120.0F,
                  IcoGenUtils.getIdentifierGEN_CORZCLIENT_MAP("download"),
                  "No downloads yet",
                  "Download a config from the Community tab.",
                  "Browse community",
                  matrixStackLimit,
                  mouseY,
                  () -> this.switchTab(ConfigBrowserPanel.Tab.COMMUNITY)
               );
            }
         } else if (this.visibleEntries.isEmpty()) {
            MatrixStackUtilsAmount.renderMatricesPop(
               context,
               hits,
               this.like,
               "empty:none",
               this.plus,
               downloadCommunityValue,
               this.panelWidth,
               88.0F,
               IcoGenUtils.getIdentifierGEN_CORZCLIENT_MAP("search"),
               "No configs match",
               "Try a different name.",
               null,
               matrixStackLimit,
               mouseY,
               null
            );
         } else {
            this.renderLocalEntryGrid(context, hits, downloadCommunityValue, matrixStackLimit, mouseY, currentMillis, stripDownloadFlag);
         }
      } finally {
         CorzRoundedUtils.handlePanelOverlay(panelOverlay);
      }
   }

   private void deleteLocalConfig(String deletedDeleteText) {
      try {
         ConfigSyncUtils.deleteServerCopyOfLocalConfig(deletedDeleteText);
         PresetFileStore.deletePreset(deletedDeleteText);
         this.showNotification("Config Deleted", deletedDeleteText, false);
         this.reloadLocalEntries();
      } catch (IOException e) {
         this.showNotification("Configs", "Delete failed", false);
      }
   }

   private int getTabContentHeight() {
      if (this.tabs == ConfigBrowserPanel.Tab.COMMUNITY) {
         ConfigsPage communityPage2 = this.communityPage;
         if (this.communityLoading) {
            return 20 + computeGridHeight(6, 2, 60);
         } else {
            return this.communityLoadError == null && communityPage2 != null && !communityPage2.configs.isEmpty()
               ? 20 + computeGridHeight(communityPage2.configs.size(), 2, 60)
               : 140;
         }
      } else if (this.isShareViewOpen()) {
         var shareStripItems = this.shareCodes;
         return 24 + (this.isSharePlaceholderShown(shareStripItems) ? 88 : computeGridHeight(shareStripItems.size(), 3, 36));
      } else {
         this.filterLocalEntries();
         int configsCommunityValue = (this.tabs == ConfigBrowserPanel.Tab.MINE ? this.getInputBarHeight() : 0) + this.getNoticeHeight();
         if (this.unfilteredConfigCount == 0) {
            return configsCommunityValue + 120;
         } else {
            return this.visibleEntries.isEmpty() ? configsCommunityValue + 88 : configsCommunityValue + computeGridHeight(this.visibleEntries.size(), 2, 60);
         }
      }
   }

   private void renderShareView(DrawContext context, int shareConfigValue, int matrixStackLimit, int scaledY) {
      VertexTextureFont fontRenderer = MatrixStackUtilsAmount.getFontRenderer();
      float shareConfigValue4 = MatrixStackUtilsAmount.getAmount(
            context, this.contentHits, this.like, "share:back", "Back", this.plus + 40.0F, shareConfigValue, 18.0F, matrixStackLimit, scaledY, false, this::closeShareView
         )
         + 48.0F;
      float matrixStackValue = this.plus + this.panelWidth;

      for (int i = SHARE_USES_LABELS.length - 1; i >= 0; i--) {
         Integer integer = SHARE_CODE_MAX_USES[i];
         matrixStackValue = MatrixStackUtilsAmount.getAmount(
               context,
               this.contentHits,
               this.like,
               "share:mint" + i,
               SHARE_USES_LABELS[i],
               matrixStackValue,
               shareConfigValue,
               18.0F,
               matrixStackLimit,
               scaledY,
               false,
               () -> this.createShareCode(integer)
            )
            - 4.0F;
      }

      String shareConfigText = "New code";
      float scaleValue = matrixStackValue - 4.0F - MatrixStackUtilsAmount.getAmount(shareConfigText, 0.82F);
      MatrixStackUtilsAmount.handlePushScale(shareConfigText, scaleValue, MatrixStackUtilsAmount.getAmount(shareConfigValue, 18.0F, 0.82F), 0.82F, -7630437);
      MatrixStackUtilsAmount.handle(
         fontRenderer.formatText(nullToEmpty(this.sharedConfigName), scaleValue - 8.0F - shareConfigValue4), shareConfigValue4, fontRenderer.getAbcdefghijklmnopqrstuvwxyzBrown(shareConfigValue, 18), -920587
      );
      int topOffset = shareConfigValue + 24;
      var mintAnyoneItems = this.shareCodes;
      if (this.shareLoading && this.sharedConfigId == null) {
         MatrixStackUtilsAmount.renderMatricesPop(
            context,
            this.contentHits,
            this.like,
            "share:wait",
            this.plus,
            topOffset,
            this.panelWidth,
            88.0F,
            IcoGenUtils.getIdentifierGEN_CORZCLIENT_MAP("share"),
            "Preparing this config to share",
            "This only takes a moment.",
            null,
            matrixStackLimit,
            scaledY,
            null
         );
      } else if (this.shareErrorMessage != null) {
         MatrixStackUtilsAmount.renderMatricesPop(
            context,
            this.contentHits,
            this.like,
            "share:error",
            this.plus,
            topOffset,
            this.panelWidth,
            88.0F,
            IcoGenUtils.getCloudIco(),
            "Sharing unavailable",
            fontRenderer.formatText(this.shareErrorMessage, this.panelWidth / 0.82F),
            null,
            matrixStackLimit,
            scaledY,
            null
         );
      } else if (mintAnyoneItems.isEmpty()) {
         MatrixStackUtilsAmount.renderMatricesPop(
            context,
            this.contentHits,
            this.like,
            "share:none",
            this.plus,
            topOffset,
            this.panelWidth,
            88.0F,
            IcoGenUtils.getIdentifierGEN_CORZCLIENT_MAP("ticket"),
            "No codes yet",
            "Mint one above. Anyone who enters the code gets this config.",
            null,
            matrixStackLimit,
            scaledY,
            null
         );
      } else {
         if (mintAnyoneItems != this.renderedShareCodes) {
            this.renderedShareCodes = mintAnyoneItems;
            var hashSet = new HashSet();

            for (CommunityConfigClient.ShareCode shareCode : mintAnyoneItems) {
               hashSet.add(shareCode.code);
            }

            this.pruneStaleAnimations(hashSet);
         }

         int shareConfigValue7 = 1;

         for (int j = 0; j < mintAnyoneItems.size(); j++) {
            shareConfigValue7 = shareConfigValue7 * 31 + String.valueOf(((CommunityConfigClient.ShareCode)mintAnyoneItems.get(j)).code).hashCode();
         }

         this.updateEntranceAnimation(TABS_IN_ORDER.length, shareConfigValue7, mintAnyoneItems.size());
         boolean shareConfigFlag = true;

         for (int k = 0; k < mintAnyoneItems.size(); k++) {
            int matrixStackFactor = k % 3;
            int valueForInt = MatrixStackUtilsAmount.getValueForInt(this.plus, this.panelWidth, 3, 4, matrixStackFactor);
            int materialFactor = MatrixStackUtilsAmount.getValue(this.plus, this.panelWidth, 3, 4, matrixStackFactor);
            int vectorTransformValue = topOffset + k / 3 * 40;
            if (matrixStackFactor == 0) {
               shareConfigFlag = this.isRowVisibleInScissor(context, vectorTransformValue, 36);
            }

            if (shareConfigFlag) {
               Entrance amountFlag = this.cardEntrance.getAmountFlag(k);
               if (!amountFlag.isAmount()) {
                  float tabsSettingsAmount = amountFlag.tabsSettingsAmount;
                  float shareConfigLimit = amountFlag.entranceProgress;
                  Hits hits = amountFlag.matrixScissorActive ? this.contentHits : Hits.INSTANCE;
                  int shareConfigValue12 = amountFlag.activeFlag ? matrixStackLimit : this.plus - 4000;
                  boolean translatePush = MatrixStackUtilsAmount.canTranslatePush(context, valueForInt + materialFactor / 2.0F, vectorTransformValue + 18.0F, amountFlag.matrixScissorAmount);

                  try {
                     if (tabsSettingsAmount > 0.004F) {
                        float panelOverlay = CorzRoundedUtils.getPanelOverlay(tabsSettingsAmount);

                        try {
                           MatrixStackUtilsAmount.render(context, valueForInt, vectorTransformValue, materialFactor, 36.0F, 0.0F, 0.0F, 0.0F);
                        } finally {
                           CorzRoundedUtils.handlePanelOverlay(panelOverlay);
                        }
                     }

                     if (shareConfigLimit > 0.002F) {
                        float panelOverlay2 = CorzRoundedUtils.getPanelOverlay(shareConfigLimit);

                        try {
                           this.renderShareCodeCard(context, hits, (CommunityConfigClient.ShareCode)mintAnyoneItems.get(k), valueForInt, vectorTransformValue, materialFactor, shareConfigValue12, scaledY);
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

   private void renderInputBar(DrawContext context, int focusPlusValue, int stripValue, int mouseY, float panelOverlayLimit, boolean focusPlusFlag) {
      VertexTextureFont fontRenderer = MatrixStackUtilsAmount.getFontRenderer();
      boolean stripFlag = this.inputIsConfigName;
      String focusPlusText = this.inputBarText;
      boolean matrixStackFlag = focusPlusText.trim().length() >= (stripFlag ? 3 : 4);
      Runnable runnable = this::submitInput;
      String focusPlusText2 = stripFlag ? (this.renamingConfigKey == null ? "Name this config..." : "New name...") : "Enter share code...";
      ClearEdit clearEdit = stripFlag ? this.nameInputEdit : this.codeInputEdit;
      Hits hits = focusPlusFlag ? this.contentHits : this.inputBarHits;
      int matrixStackLimit = focusPlusFlag ? stripValue : this.plus - 4000;
      float panelOverlay = CorzRoundedUtils.getPanelOverlay(panelOverlayLimit);

      try {
         float matrixStackValue = this.plus + this.panelWidth;
         float hitsValue = MatrixStackUtilsAmount.getAmount(
            context, hits, this.like, "entry:ok", stripFlag ? "Save" : "Redeem", null, matrixStackValue, (float)focusPlusValue, 18.0F, matrixStackLimit, mouseY, matrixStackFlag, runnable
         );
         if (!matrixStackFlag) {
            hits.handle(hitsValue, focusPlusValue - 1.0F, matrixStackValue - hitsValue, 20.0F, runnable);
         }

         float focusPlusValue8 = MatrixStackUtilsAmount.getAmount(
            context, hits, this.like, "entry:cancel", "Cancel", hitsValue - 4.0F, focusPlusValue, 18.0F, matrixStackLimit, mouseY, false, this::closeInputBar
         );
         float materialFactor = focusPlusValue8 - 6.0F - this.plus;
         float releaseRightFactor = focusPlusFlag ? this.like.getAmount("entry:focus", this.inputFocused ? 1.0F : 0.0F, 16.0F) : this.like.getAmount("entry:focus");
         MatrixPeekUtilsRender.renderForDrawContextFloat(context, this.plus, focusPlusValue, materialFactor, 18.0F, 5.0F, CardMatrixUtils.getValue7(), MatrixPeekUtilsRender.getReleaseRight(releaseRightFactor));
         MatrixPeekUtilsIdentifier.handle(
            this.plus + 6.0F,
            focusPlusValue + 3.0F,
            12.0F,
            12.0F,
            IcoGenUtils.getIdentifierGEN_CORZCLIENT_MAP(stripFlag ? "pencil" : "ticket"),
            new Color(CardMatrixUtils.getValue(-10722709, -920587, releaseRightFactor), true),
            MatrixPeekUtilsRender.getMatrixStack()
         );
         float plusOffset = this.plus + 24.0F;
         float configShareValue = materialFactor - 24.0F - 8.0F;
         int abcdefghijklmnopqrstuvwxyzBrown = fontRenderer.getAbcdefghijklmnopqrstuvwxyzBrown(focusPlusValue, 18);
         String matrixStackText = truncateStartToWidth(focusPlusText, configShareValue);
         if (focusPlusText.isEmpty()) {
            MatrixStackUtilsAmount.handle(focusPlusText2, plusOffset, abcdefghijklmnopqrstuvwxyzBrown, -7630437);
         } else {
            MatrixStackUtilsAmount.handle(matrixStackText, plusOffset, abcdefghijklmnopqrstuvwxyzBrown, -920587);
         }

         if (focusPlusFlag && this.inputFocused) {
            int n4 = (int)fontRenderer.getAbcdefghijklmnopqrstuvwxyzBrown() - 2;
            clearEdit.renderAmountTime(
               context,
               plusOffset + MatrixStackUtilsAmount.getAmount3(matrixStackText) + 1.0F,
               focusPlusValue + (18 - n4) / 2,
               n4,
               MatrixEspUtils.releaseRightValue,
               MatrixClearUtils.getAmount()
            );
         }

         hits.handle(this.plus, focusPlusValue - 1.0F, materialFactor, 20.0F, this::focusInput);
      } finally {
         CorzRoundedUtils.handlePanelOverlay(panelOverlay);
      }
   }

   private void downloadCommunityConfig(SharedConfigEntry versionJsonUtils) {
      this.showNotification("Downloading\u2026", nullToEmpty(versionJsonUtils.name), true);
      CommunityConfigClient.downloadConfig(
         versionJsonUtils.id,
         null,
         result -> {
            if (!result.ok) {
               this.showNotification("Download Failed", result.errorMessage != null ? result.errorMessage : result.errorCode, false);
            } else {
               try {
                  Result[] results = new Result[1];
                  ConfigPresetFile jsonVersion = ConfigPresetFile.parseBytes(((CommunityConfigClient.Download)result.value).bytes, results);
                  if (jsonVersion == null) {
                     this.showNotification("Download Failed", "Config failed validation", false);
                     return;
                  }

                  String savePresetForCommunityId = PresetFileStore.savePresetForCommunityId(jsonVersion, versionJsonUtils.id);
                  SourceMeta sourceMeta = new SourceMeta();
                  sourceMeta.source = Source.COMMUNITY;
                  sourceMeta.communityId = versionJsonUtils.id;
                  sourceMeta.communitySha256 = ((CommunityConfigClient.Download)result.value).meta.has("sha256")
                     ? ((CommunityConfigClient.Download)result.value).meta.get("sha256").getAsString()
                     : null;
                  sourceMeta.communityVersionId = ((CommunityConfigClient.Download)result.value).meta.has("versionId")
                     ? ((CommunityConfigClient.Download)result.value).meta.get("versionId").getAsString()
                     : null;
                  sourceMeta.downloadedAt = OffsetDateTime.now(ZoneOffset.UTC).toString();
                  PresetFileStore.saveSourceMeta(savePresetForCommunityId, sourceMeta);
                  this.showNotification("Config Downloaded", nullToEmpty(versionJsonUtils.name) + " \u2014 saved to Downloads", true);
                  this.reloadLocalEntries();
               } catch (IOException e) {
                  this.showNotification("Download Failed", e.getMessage(), false);
               }
            }
         }
      );
   }

   private void openRedeemInput() {
      if (!this.redeemInputOpen) {
         this.redeemCodeInput = "";
      }

      this.redeemInputOpen = true;
      this.nameInputOpen = false;
      this.nameInput = "";
      this.renamingConfigKey = null;
      this.focusInput();
   }

   private static float clampForFloat(float configShareLimit) {
      return configShareLimit < 0.0F ? 0.0F : (configShareLimit > 1.0F ? 1.0F : configShareLimit);
   }

   private void renderTimestampLabel(DrawContext context, String configShareText, float configShareValue, float matrixStackValue, float configShareValue3) {
      if (configShareText != null) {
         float limitOffset = configShareValue + 12.0F;
         if (!(limitOffset + MatrixStackUtilsAmount.getAmount(configShareText, 0.82F) > configShareValue3)) {
            MatrixPeekUtilsIdentifier.handle(
               configShareValue, matrixStackValue + 3.5F, 9.0F, 9.0F, IcoGenUtils.getIcoClockIcoClockActive(), new Color(-10722709, true), MatrixPeekUtilsRender.getMatrixStack()
            );
            MatrixStackUtilsAmount.handlePushScale(configShareText, limitOffset, MatrixStackUtilsAmount.getAmount(matrixStackValue, 16.0F, 0.82F), 0.82F, -7630437);
         }
      }
   }

   public ConfigBrowserPanel(ConfigListSetting configListBase, int plus, int configsTabsValue, int shareConfigValue, int configsSearchValue) {
      this.cachedEntryList = null;
      this.localEntries = new ArrayList<>();
      this.trackedLocalEntries = null;
      this.visibleEntries = new ArrayList<>();
      this.unfilteredConfigCount = 0;
      this.activeConfigKey = null;
      this.pendingConfirmKey = null;
      this.apply = null;
      this.confirmExpiryMs = 0L;
      this.appliedConfigKey = null;
      this.appliedBadgeDeadlineMs = 0L;
      this.keptForSafetyMessage = null;
      this.notTurnedOnMessage = null;
      this.nameInputOpen = false;
      this.renamingConfigKey = null;
      this.nameInput = "";
      this.nameInputEdit = new ClearEdit();
      this.redeemInputOpen = false;
      this.redeemCodeInput = "";
      this.codeInputEdit = new ClearEdit();
      this.inputFocused = false;
      this.inputIsConfigName = true;
      this.inputBarText = "";
      this.sharingConfigKey = null;
      this.sharedConfigName = null;
      this.sharedConfigId = null;
      this.shareLoading = false;
      this.shareErrorMessage = null;
      this.shareCodes = new ArrayList<>();
      this.newestShareCode = null;
      this.renderedShareCodes = null;
      this.sort = 0;
      this.search = "";
      this.localSearchText = "";
      this.searchDebounceStartMs = 0L;
      this.searchField = new ClearIcons(0, 0, 0, 18);
      this.searchVisible = false;
      this.communityPage = null;
      this.communityLoadError = null;
      this.communityLoading = false;
      this.communityRequestId = 0L;
      this.renderedCommunityPage = null;
      this.communityDateLabels = new ArrayList<>();
      this.contentHits = new Hits();
      this.inputBarHits = new Hits();
      this.like = new Anims();
      this.cardEntrance = new Entrance();
      this.entranceTabKey = -1;
      this.lastEntriesHash = 0;
      this.lastEntryCount = 0;
      this.copiedShareCode = null;
      this.copiedCodeDeadlineMs = 0L;
      this.scratchVec = new Vector2f();
      this.tabSwitchTimeMs = 0L;
      this.lastRenderNanos = 0L;
      this.hoverTooltipText = null;
      this.lastTooltipText = null;
      this.tooltipBelow = false;
      this.tooltipChangeNanos = 0L;
      this.configListSetting = configListBase;
      this.plus = plus;
      this.tabsTopY = configsTabsValue;
      this.panelWidth = shareConfigValue;
      this.searchLayoutOffset = configsSearchValue;
      this.searchField.handleModulesSearch("Search configs...");
      this.searchField.setModulesSearchFlag(false);
      this.searchField.setModulesSearchValue(6);
      this.reloadLocalEntries();
      this.syncAccountConfigsOnce();
   }

   private void copyToClipboard(String clipboard) {
      try {
         MinecraftClient.getInstance().keyboard.setClipboard(clipboard);
      } catch (Throwable e) {
      }
   }

   private static boolean isAlphanumeric(char configShareLimit) {
      return configShareLimit >= 'a' && configShareLimit <= 'z' || configShareLimit >= 'A' && configShareLimit <= 'Z' || configShareLimit >= '0' && configShareLimit <= '9';
   }

   private static String nullToEmpty(String configShareText) {
      return configShareText == null ? "" : configShareText;
   }

   private void applyLocalConfig(String appliedLoadText) {
      try {
         Preset loadPreset = PresetFileStore.loadPreset(appliedLoadText);
         if (loadPreset == null) {
            this.showNotification("Configs", "Config not found", false);
            return;
         }

         ConfigPresetFile jsonVersion = loadPreset.jsonVersion;
         ModuleConfigSerializer.Result settingSetup = ModuleConfigSerializer.getSettingSetup(loadPreset);
         PresetFileStore.writeActivePresetName(appliedLoadText);
         if (settingSetup.applied) {
            this.appliedConfigKey = appliedLoadText;
            this.appliedBadgeDeadlineMs = System.currentTimeMillis() + 1800L;
            String keptConfigText = settingSetup.appliedSettingCount + " settings, " + (settingSetup.clampedSettingCount > 0 ? settingSetup.clampedSettingCount + " clamped, " : "") + "applied";
            this.showNotification("Config Applied", jsonVersion.name + " \u2014 " + keptConfigText, true);
            this.keptForSafetyMessage = settingSetup.getKeptLocalNote();
            this.notTurnedOnMessage = settingSetup.getNotEnabledNote();
            if (this.keptForSafetyMessage != null) {
               this.showNotification("Kept For Safety", this.keptForSafetyMessage, false);
            }

            if (this.notTurnedOnMessage != null) {
               this.showNotification("Not Turned On", this.notTurnedOnMessage, false);
            }

            if (this.keptForSafetyMessage == null && this.notTurnedOnMessage == null && !settingSetup.notes.isEmpty()) {
               this.showNotification("Config Notes", settingSetup.notes.get(0), false);
            }
         } else if (settingSetup.rolledBack) {
            this.showNotification("Apply Failed", "Rolled back safely", false);
         } else {
            this.showNotification("Apply Failed", settingSetup.errorMessage != null ? settingSetup.errorMessage : "Unknown error", false);
         }

         this.reloadLocalEntries();
      } catch (IOException e) {
         this.showNotification("Configs", "Apply failed: " + e.getMessage(), false);
      }
   }

   public void renderPanel(DrawContext context, int stripValue, int mouseY) {
      long nanoTime = System.nanoTime();
      if (this.lastRenderNanos != 0L && nanoTime - this.lastRenderNanos > 400000000L) {
         this.clearInputFocus();
         if (!this.nameInputOpen && !this.redeemInputOpen) {
            this.like.handle("strip", 0.0F);
         }
      }

      this.lastRenderNanos = nanoTime;
      if (this.cardEntrance.isTimeFlag()) {
         this.entranceTabKey = -1;
         this.cardEntrance.updateTimeFlag();
      }

      if ((this.searchField.isClearModulesFlag() || this.inputFocused) && isSearchShortcutPressed()) {
         this.clearInputFocus();
      }

      long currentMillis = System.currentTimeMillis();
      if (this.pendingConfirmKey != null && currentMillis > this.confirmExpiryMs) {
         this.pendingConfirmKey = null;
         this.apply = null;
      }

      this.contentHits.update();
      this.inputBarHits.update();
      this.hoverTooltipText = null;
      this.tooltipMinX = this.plus + 1.0F;
      this.tooltipMaxX = this.plus + this.panelWidth - 1.0F;
      this.renderTabBar(context, stripValue, mouseY);
      int resultsConfigsValue = this.tabsTopY + 22 + 6;
      float stripBound = easeOutCubic((float)(currentMillis - this.tabSwitchTimeMs) / 190.0F);
      float panelOverlay = CorzRoundedUtils.getPanelOverlay(Math.max(1.0E-4F, stripBound));

      try {
         if (this.tabs == ConfigBrowserPanel.Tab.COMMUNITY) {
            this.renderCommunityTab(context, resultsConfigsValue, stripValue, mouseY, currentMillis);
         } else if (this.isShareViewOpen()) {
            this.renderShareView(context, resultsConfigsValue, stripValue, mouseY);
         } else {
            this.renderLocalTab(context, resultsConfigsValue, stripValue, mouseY, currentMillis);
         }
      } finally {
         CorzRoundedUtils.handlePanelOverlay(panelOverlay);
      }
   }

   private void focusInput() {
      this.inputFocused = true;
      this.searchField.update();
      (this.nameInputOpen ? this.nameInputEdit : this.codeInputEdit).updateTime();
   }

   private record ConfigListEntry(String slug, String name, String key, String modules, String origin, String stamp, boolean local, boolean community) {
   }

   private static enum Tab {
      MINE,
      COMMUNITY,
      DOWNLOADS;
   }
}
