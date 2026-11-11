package com.corz.client.utils;

import com.corz.client.config.PlayerSkinResolver;
import com.corz.client.config.RadioStationRegistry;
import com.corz.client.config.RadioSetting;
import com.corz.client.gui.DisabledEnabled;
import com.corz.client.gui.MatrixPeekUtilsRender;
import com.corz.client.render.CardMatrixUtils;
import com.corz.client.render.CorzRoundedUtils;
import com.corz.client.render.IcoGenUtils;
import com.corz.client.render.MatrixClearUtils;
import com.corz.client.render.MatrixEspUtils;
import com.corz.client.render.MatrixIdentifierUtils;
import com.corz.client.render.MatrixPeekUtilsIdentifier;
import com.corz.client.render.MatrixStackUtilsAmount;
import com.corz.client.render.VertexTextureFont;
import java.awt.Color;
import java.net.URI;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix3x2fStack;

public class RadioCom {
   private int empty;
   private static final String CORZ_RADIO = "Corz Radio";
   private RadioCom.Card[] reloadPlaylistsFounderPlaylists;
   private static final String AIR = "On air";
   private static boolean playlistsReloadActive;
   private static final String INVALID_LINK = "Invalid link";
   private float stationsPlaylistsAmount;
   private static final double[] airAmounts = new double[]{7.1, 5.3, 8.6};
   private boolean stationsPlaylistsFlag;
   private static final String COMING = "Coming soon";
   private final MatrixStackUtilsAmount.Anims playlistsReloadAnims;
   private final Runnable reloadPlaylistsAction;
   private final RadioSetting radioBase;
   private static final String RELOAD = "reload";
   private static final double[] airTable = new double[]{0.0, 1.9, 4.1};
   private int stationIndex;
   private final MatrixStackUtilsAmount.Hits playlistsReloadHits = new MatrixStackUtilsAmount.Hits();
   private int playlistsReloadValue;
   private static final float[] reloadTable = new float[48];
   private static final String ON_PREFIX = "on:";
   private static final String PLAYLIST = "No playlist yet";
   private int playlistsFounderValue;
   private static final String PLAY = "play:";
   private static final float[] reloadAmounts = new float[2];
   private static final String CARD = "card:";
   private final MatrixStackUtilsAmount.Entrance playlistsReloadEntrance;
   private static int emptyBroadcastValue;
   private static final String STAFF_PLAYLISTS = "Staff playlists";
   private static final String STATIONS = "Stations";
   private int playlistsStaffValue;

   private static float getAmount() {
      return 37.5F;
   }

   private void renderPlayRadio(DrawContext context, RadioCom.Card card, float playRadioValue, float amountMax, float linkComingBound, float playRadioLimit, boolean playRadioFlag, int matrixStackLimit, int scaledY) {
      float playRadioBound = this.playlistsReloadAnims.getAmount(card.radioCorzText, playRadioFlag && MatrixStackUtilsAmount.canFlag(matrixStackLimit, scaledY, playRadioValue, amountMax, 18.0F, 18.0F));
      float cardMatrixFactor = Math.max(linkComingBound * 0.55F, playRadioBound);
      int cardMatrixValue = MatrixStackUtilsAmount.getValue(MatrixEspUtils.releaseRightValue, 0.92F);
      int playRadioValue8 = CardMatrixUtils.getValue(
         CardMatrixUtils.getValue(CardMatrixUtils.getValue4(), -1, 0.04 + 0.08 * cardMatrixFactor), CardMatrixUtils.getValue(cardMatrixValue, -1, 0.1 * playRadioBound), playRadioLimit
      );
      int matrixStackValue = CardMatrixUtils.getValue(CardMatrixUtils.getValue(CardMatrixUtils.getValueInt(), 1040187391, cardMatrixFactor), cardMatrixValue, playRadioLimit);
      int playRadioValue10 = CardMatrixUtils.getValue(CardMatrixUtils.getValue(-7630437, -920587, cardMatrixFactor), MatrixStackUtilsAmount.getReleaseRight(), playRadioLimit);
      boolean translatePush = MatrixStackUtilsAmount.canTranslatePush(context, playRadioValue + 9.0F, amountMax + 9.0F, this.playlistsReloadAnims.getTime(card.radioCorzText, 0.07F));

      try {
         if (playRadioLimit > 0.01F) {
            MatrixPeekUtilsRender.renderMatrixPeekForDrawContextFloat(context, playRadioValue, amountMax, 18.0F, 18.0F, 9.0F, CardMatrixUtils.getReleaseRight(0.2 * playRadioLimit), 4.0F, 0.5F);
         }

         MatrixPeekUtilsRender.renderForDrawContextFloat(context, playRadioValue, amountMax, 18.0F, 18.0F, 9.0F, playRadioValue8, matrixStackValue);
         float playRadioValue11 = 3.0F;
         MatrixPeekUtilsIdentifier.handle(
            playRadioValue + playRadioValue11, amountMax + playRadioValue11, 12.0F, 12.0F, IcoGenUtils.getIdentifierForString("play"), new Color(playRadioValue10, true), MatrixPeekUtilsRender.getMatrixStack()
         );
      } finally {
         MatrixStackUtilsAmount.renderPopMatrices(context, translatePush);
      }
   }

   private float getPlaylistsFounder() {
      if (this.reloadPlaylistsFounderPlaylists.length == 0) {
         return 116.0F;
      } else {
         int playlistsFounderValue = (this.reloadPlaylistsFounderPlaylists.length + 2 - 1) / 2;
         return playlistsFounderValue * 64 + (playlistsFounderValue - 1) * 4;
      }
   }

   public int getPlaylistsStations() {
      this.updatePlaylistsFounder();
      if (this.stationsPlaylistsAmount < 0.0F) {
         this.stationsPlaylistsAmount = this.getPlaylistsFounder();
      }

      return 28 + Math.round(this.stationsPlaylistsAmount);
   }

   private static void renderReleaseRight(DrawContext context, VertexTextureFont vertexTextureFont, RadioCom.Card card, float releaseRightValue, float matrixStackValue) {
      String mc = card.playlistStation.mc();
      if (!mc.isEmpty() && PlayerSkinResolver.getSkinTextures(mc) != null) {
         MatrixStackUtilsAmount.renderReleaseRight(context, mc, releaseRightValue, matrixStackValue, 24.0F);
      } else {
         MatrixPeekUtilsRender.renderForDrawContextFloat(
            context,
            releaseRightValue,
            matrixStackValue,
            24.0F,
            24.0F,
            6.0F,
            CardMatrixUtils.getValue(CardMatrixUtils.getValue4(), MatrixEspUtils.releaseRightValue, 0.16),
            CardMatrixUtils.getReleaseRight(0.3)
         );
         MatrixStackUtilsAmount.handle(
            card.upperCase,
            releaseRightValue + (24.0F - card.playlistAmount) / 2.0F - 1.3F,
            vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzBrown(Math.round(matrixStackValue), 24),
            CardMatrixUtils.getValue(MatrixEspUtils.releaseRightValue, -1, 0.25)
         );
      }
   }

   private static void renderPopMatrices(DrawContext context) {
      context.getMatrices().popMatrix();
      MatrixPeekUtilsRender.getMatrixStack().pop();
   }

   private static void renderTranslatePush(DrawContext context, float x, float translatePushValue, float x2, float translatePushValue2) {
      MatrixStack matrixStack = MatrixPeekUtilsRender.getMatrixStack();
      matrixStack.push();
      matrixStack.translate(x, translatePushValue + translatePushValue2, 0.0F);
      matrixStack.scale(x2, x2, 1.0F);
      matrixStack.translate(-x, -translatePushValue, 0.0F);
      Matrix3x2fStack matrices = context.getMatrices();
      matrices.pushMatrix();
      matrices.translate(x, translatePushValue + translatePushValue2);
      matrices.scale(x2, x2);
      matrices.translate(-x, -translatePushValue);
   }

   private void renderLinkComing(
      DrawContext context, VertexTextureFont vertexTextureFont, RadioCom.Card card, int linkComingValue, int linkComingValue2, int materialFactor, float linkComingBound, int matrixStackLimit, int scaledY, double airFactor
   ) {
      RadioStationRegistry.Station playlistStation = card.playlistStation;
      boolean url = playlistStation.hasUrl();
      float releaseRightLimit = this.playlistsReloadAnims.getAmount(card.founderOwnerText, this.radioBase.isSelected(playlistStation) ? 1.0F : 0.0F, 11.0F);
      renderReleaseRight(context, linkComingValue, linkComingValue2, materialFactor, linkComingBound, releaseRightLimit);
      float releaseRightValue = linkComingValue + 6;
      float linkComingValue8 = linkComingValue + materialFactor - 6;
      float matrixStackValue = releaseRightValue + 24.0F + 7.0F;
      float playRadioValue = linkComingValue8 - 18.0F;
      float amountMax = linkComingValue2 + getAmount();
      float linkComingValue12 = playRadioValue - 5.0F - releaseRightValue;
      card.handlePlaylistText(vertexTextureFont, linkComingValue8 - matrixStackValue, linkComingValue12);
      float panelOverlay = CorzRoundedUtils.getPanelOverlay(url ? 1.0F : 0.46F);

      try {
         renderReleaseRight(context, vertexTextureFont, card, releaseRightValue, linkComingValue2 + 6);
         MatrixStackUtilsAmount.handle(card.linkComingText, matrixStackValue, MatrixStackUtilsAmount.getAmount(linkComingValue2 + 6.0F, 11.0F, 1.0F), -920587);
         if (!card.playlistText.isEmpty()) {
            MatrixStackUtilsAmount.getAmount(
               context, card.playlistText, matrixStackValue, linkComingValue2 + 19.0F, card.founderTrackFlag ? MatrixEspUtils.releaseRightValue : -5854026
            );
         }

         MatrixStackUtilsAmount.handlePushScale(
            card.cardText,
            releaseRightValue,
            MatrixStackUtilsAmount.getAmount(linkComingValue2 + 35.0F, 10.0F, 0.82F),
            0.82F,
            CardMatrixUtils.getValue(-7630437, -5854026, Math.max(linkComingBound, releaseRightLimit))
         );
         this.renderPlayRadio(context, card, playRadioValue, amountMax, linkComingBound, releaseRightLimit, url, matrixStackLimit, scaledY);
      } finally {
         CorzRoundedUtils.handlePanelOverlay(panelOverlay);
      }

      float cardOffset = linkComingValue2 + 47.0F;
      if (!url) {
         if (playlistStation.badUrl()) {
            MatrixStackUtilsAmount.getAmount(context, "Invalid link", releaseRightValue, cardOffset, -1535700);
         } else {
            MatrixStackUtilsAmount.getAmount(context, "Coming soon", releaseRightValue, cardOffset, linkComingValue12);
         }
      } else {
         if (releaseRightLimit < 0.998F && !card.comingLinkText.isEmpty()) {
            float panelOverlay2 = CorzRoundedUtils.getPanelOverlay(1.0F - releaseRightLimit);

            try {
               MatrixStackUtilsAmount.getAmount(context, card.comingLinkText, releaseRightValue, cardOffset - 2.0F * releaseRightLimit, linkComingValue12);
            } finally {
               CorzRoundedUtils.handlePanelOverlay(panelOverlay2);
            }
         }

         if (releaseRightLimit > 0.002F) {
            float panelOverlay3 = CorzRoundedUtils.getPanelOverlay(releaseRightLimit);

            try {
               renderAirAmounts(context, releaseRightValue, cardOffset + 2.0F * (1.0F - releaseRightLimit), airFactor);
            } finally {
               CorzRoundedUtils.handlePanelOverlay(panelOverlay3);
            }
         }
      }
   }

   private static String getSubstring(String substringText) {
      if (substringText.isEmpty()) {
         return "";
      } else if (substringText.startsWith("spotify:")) {
         return "Spotify";
      } else {
         try {
            String host = new URI(substringText).getHost();
            if (host == null) {
               return "";
            } else {
               String lowerCase = host.toLowerCase(Locale.ROOT);
               if (lowerCase.startsWith("www.")) {
                  lowerCase = lowerCase.substring(4);
               }

               if (lowerCase.endsWith("spotify.com")) {
                  return "Spotify";
               } else if (lowerCase.equals("music.youtube.com")) {
                  return "YouTube Music";
               } else if (lowerCase.endsWith("youtube.com") || lowerCase.equals("youtu.be")) {
                  return "YouTube";
               } else if (lowerCase.endsWith("soundcloud.com")) {
                  return "SoundCloud";
               } else if (lowerCase.equals("music.apple.com")) {
                  return "Apple Music";
               } else if (lowerCase.endsWith("deezer.com")) {
                  return "Deezer";
               } else {
                  return lowerCase.endsWith("tidal.com") ? "Tidal" : lowerCase;
               }
            }
         } catch (Exception e) {
            return "";
         }
      }
   }

   private static void renderAirAmounts(DrawContext context, float airValue, float matrixStackValue, double airFactor) {
      int airValue3 = CardMatrixUtils.getValue(MatrixEspUtils.releaseRightValue, -1, 0.22);
      float airValue4 = matrixStackValue + 11.0F - 1.5F;
      float airFactor2 = MatrixStackUtilsAmount.getAmount(3.5F);
      float airValue5 = MatrixStackUtilsAmount.getAmount(airValue + 1.0F);

      for (int i = 0; i < airAmounts.length; i++) {
         double airFactor3 = 0.5 + 0.5 * Math.sin(airFactor * airAmounts[i] + airTable[i]);
         double airFactor4 = 0.72 + 0.28 * Math.sin(airFactor * airAmounts[i] * 0.37 + airTable[i] * 1.7);
         float materialFactor = 2.5F + 5.5F * (float)(airFactor3 * airFactor4);
         MatrixPeekUtilsRender.renderMatrixPeek(context, airValue5 + i * airFactor2, airValue4 - materialFactor, 2.0F, materialFactor, 1.0F, airValue3);
      }

      MatrixStackUtilsAmount.handlePushScale(
         "On air", airValue5 + (airAmounts.length - 1) * airFactor2 + 2.0F + 5.0F, MatrixStackUtilsAmount.getAmount(matrixStackValue, 11.0F, 0.82F), 0.82F, airValue3
      );
   }

   public static boolean canReloadAmounts(int reloadLimit, float[] reloadValues) {
      if (reloadValues == null || reloadValues.length < 2) {
         return false;
      } else if (reloadLimit == -1) {
         if (!playlistsReloadActive) {
            return false;
         } else {
            reloadValues[0] = reloadAmounts[0];
            reloadValues[1] = reloadAmounts[1];
            return true;
         }
      } else if (reloadLimit >= 0 && reloadLimit < emptyBroadcastValue) {
         reloadValues[0] = reloadTable[reloadLimit * 2];
         reloadValues[1] = reloadTable[reloadLimit * 2 + 1];
         return true;
      } else {
         return false;
      }
   }

   public RadioCom(RadioSetting radioBase, int empty, int playlistsStaffValue, int playlistsReloadValue, int value) {
      this.playlistsReloadAnims = new MatrixStackUtilsAmount.Anims();
      this.playlistsReloadEntrance = new MatrixStackUtilsAmount.Entrance();
      this.reloadPlaylistsAction = this::updateRadioCorz;
      this.reloadPlaylistsFounderPlaylists = new RadioCom.Card[0];
      this.playlistsFounderValue = -1;
      this.stationsPlaylistsFlag = true;
      this.stationsPlaylistsAmount = -1.0F;
      this.radioBase = radioBase;
      this.empty = empty;
      this.playlistsStaffValue = playlistsStaffValue;
      this.playlistsReloadValue = playlistsReloadValue;
      this.stationIndex = value;
      this.updatePlaylistsFounder();
   }

   private void updatePlaylistsFounder() {
      int value = RadioStationRegistry.getReloadCount();
      if (value != this.playlistsFounderValue) {
         this.playlistsFounderValue = value;
         List list = this.radioBase.getStations();
         RadioCom.Card[] cards = new RadioCom.Card[list.size()];

         for (int i = 0; i < cards.length; i++) {
            RadioCom.Card card = new RadioCom.Card((RadioStationRegistry.Station)list.get(i), i);
            card.action = () -> this.handleRadioCorz(card);
            cards[i] = card;
         }

         this.reloadPlaylistsFounderPlaylists = cards;
         this.playlistsReloadAnims.handle(playlistsFounderText -> !playlistsFounderText.startsWith("card:") && !playlistsFounderText.startsWith("play:") && !playlistsFounderText.startsWith("on:"));
         this.stationsPlaylistsFlag = true;
      }
   }

   public void handlePlaylistsReload(int empty, int playlistsStaffValue, int playlistsReloadValue, int value) {
      this.empty = empty;
      this.playlistsStaffValue = playlistsStaffValue;
      this.playlistsReloadValue = playlistsReloadValue;
      this.stationIndex = value;
   }

   private void updateRadioCorz() {
      RadioStationRegistry.reloadStations();
      this.playlistsReloadAnims.handleTime("reload");
      int flagSize = RadioStationRegistry.getStations().size();
      if (RadioStationRegistry.isOverrideFileInvalid()) {
         DisabledEnabled.getInstance().handleDisabledEnabled("Corz Radio", "radio.json is invalid", false);
      } else if (flagSize == 0) {
         DisabledEnabled.getInstance().handleDisabledEnabled("Corz Radio", "No stations found", false);
      } else {
         DisabledEnabled.getInstance().handleDisabledEnabled("Corz Radio", flagSize == 1 ? "1 station loaded" : flagSize + " stations loaded", true);
      }
   }

   private void handleRadioCorz(RadioCom.Card card) {
      RadioStationRegistry.Station playlistStation = card.playlistStation;
      if (playlistStation.hasUrl()) {
         if (!RadioStationRegistry.openStationUrl(playlistStation)) {
            DisabledEnabled.getInstance().handleDisabledEnabled("Corz Radio", "Couldn't open " + playlistStation.staff(), false);
         } else {
            this.radioBase.setSelectedStationKey(playlistStation.key());
            this.playlistsReloadAnims.handleTime(card.radioCorzText);
            DisabledEnabled.getInstance().handleDisabledEnabled("Corz Radio", "Tuned to " + playlistStation.staff(), true);
         }
      }
   }

   private static void renderReleaseRight(DrawContext context, float releaseRightValue, float releaseRightValue2, float materialFactor, float linkComingBound, float releaseRightLimit) {
      int matrixStackValue = CardMatrixUtils.getValue(
         CardMatrixUtils.getValue(CardMatrixUtils.getValue4(), -1, 0.025 + 0.04 * linkComingBound), MatrixEspUtils.releaseRightValue, 0.07 * releaseRightLimit
      );
      int releaseRightValue6 = CardMatrixUtils.getValue(CardMatrixUtils.getValue(CardMatrixUtils.getValueInt(), 1040187391, 0.6 * linkComingBound), CardMatrixUtils.getReleaseRight(0.62), releaseRightLimit);
      if (releaseRightLimit > 0.02F && !CardMatrixUtils.isModernFlag()) {
         MatrixPeekUtilsRender.renderMatrixPeek(context, releaseRightValue, releaseRightValue2, materialFactor, 64.0F, 7.0F, CardMatrixUtils.getReleaseRight(0.2 * releaseRightLimit), 3.0F, 0.85F);
      }

      MatrixPeekUtilsRender.renderForDrawContextFloat(context, releaseRightValue, releaseRightValue2, materialFactor, 64.0F, 7.0F, matrixStackValue, releaseRightValue6);
   }

   public void renderPlaylistsReload(DrawContext context, int playlistsReloadValue, int mouseY) {
      this.updatePlaylistsFounder();
      this.playlistsReloadHits.update();
      float stationsPlaylistsAmount = this.getPlaylistsFounder();
      this.stationsPlaylistsAmount = this.stationsPlaylistsAmount < 0.0F
         ? stationsPlaylistsAmount
         : (float)MatrixIdentifierUtils.getAmount(this.stationsPlaylistsAmount, stationsPlaylistsAmount, 16.0, MatrixClearUtils.getAmount());
      if (Math.abs(this.stationsPlaylistsAmount - stationsPlaylistsAmount) < 0.5F) {
         this.stationsPlaylistsAmount = stationsPlaylistsAmount;
      }

      boolean timeFlag = this.playlistsReloadEntrance.isTimeFlag();
      if (timeFlag || this.stationsPlaylistsFlag) {
         this.playlistsReloadEntrance.updateTimeFlag();
      }

      this.stationsPlaylistsFlag = false;
      float amountForDrawContextString = MatrixStackUtilsAmount.getAmountForDrawContextString(context, "Stations", this.empty, this.playlistsStaffValue, 22.0F);
      MatrixStackUtilsAmount.handlePushScale(
         "Staff playlists", amountForDrawContextString + 6.0F, MatrixStackUtilsAmount.getAmount(this.playlistsStaffValue, 22.0F, 0.82F) + 1.25F, 0.82F, -7630437
      );
      float identifierText = 18.0F;
      float playlistsReloadValue5 = this.empty + this.playlistsReloadValue - identifierText;
      float targetOffset = this.playlistsStaffValue + (22.0F - identifierText) / 2.0F;
      MatrixStackUtilsAmount.render(
         context, this.playlistsReloadHits, this.playlistsReloadAnims, "reload", IcoGenUtils.getIcoReset(), playlistsReloadValue5, targetOffset, identifierText, 12.0F, playlistsReloadValue, mouseY, false, this.reloadPlaylistsAction
      );
      reloadAmounts[0] = playlistsReloadValue5 + identifierText / 2.0F;
      reloadAmounts[1] = targetOffset + identifierText / 2.0F;
      playlistsReloadActive = true;
      int downloadCommunityValue = this.playlistsStaffValue + 22 + 6;
      emptyBroadcastValue = 0;
      if (this.reloadPlaylistsFounderPlaylists.length == 0) {
         MatrixStackUtilsAmount.renderMatricesPop(
            context,
            this.playlistsReloadHits,
            this.playlistsReloadAnims,
            "empty",
            this.empty,
            downloadCommunityValue,
            this.playlistsReloadValue,
            116.0F,
            IcoGenUtils.getIdentifierForString("broadcast"),
            "No stations yet",
            "Add playlists to corzclient/radio.json",
            "Reload",
            playlistsReloadValue,
            mouseY,
            this.reloadPlaylistsAction
         );
      } else {
         VertexTextureFont fontRenderer = MatrixStackUtilsAmount.getFontRenderer();
         double seconds = System.nanoTime() / 1.0E9;

         for (int i = 0; i < this.reloadPlaylistsFounderPlaylists.length; i++) {
            RadioCom.Card card = this.reloadPlaylistsFounderPlaylists[i];
            int matrixStackFactor = i % 2;
            int valueForInt = MatrixStackUtilsAmount.getValueForInt(this.empty, this.playlistsReloadValue, 2, 4, matrixStackFactor);
            int materialFactor = MatrixStackUtilsAmount.getValue(this.empty, this.playlistsReloadValue, 2, 4, matrixStackFactor);
            int matrixStackValue = downloadCommunityValue + i / 2 * 68;
            if (i < 24) {
               reloadTable[i * 2] = valueForInt + materialFactor - 6 - 9.0F;
               reloadTable[i * 2 + 1] = matrixStackValue + getAmount() + 9.0F;
               emptyBroadcastValue = i + 1;
            }

            MatrixStackUtilsAmount.Entrance amountFlag = this.playlistsReloadEntrance.getAmountFlag(i);
            if (!amountFlag.isAmount()) {
               float tabsSettingsAmount = amountFlag.tabsSettingsAmount;
               float playlistsReloadLimit = amountFlag.entranceProgress;
               boolean url = card.playlistStation.hasUrl();
               boolean playlistsReloadFlag = url && amountFlag.matrixScissorActive;
               int matrixStackLimit = url && amountFlag.activeFlag ? playlistsReloadValue : -4000;
               float playlistsReloadFactor = this.playlistsReloadAnims.getAmount(card.playlistsReloadText, MatrixStackUtilsAmount.canFlag(matrixStackLimit, mouseY, valueForInt, matrixStackValue, materialFactor, 64.0F));
               renderTranslatePush(
                  context, valueForInt + materialFactor / 2.0F, matrixStackValue + 32.0F, amountFlag.matrixScissorAmount * this.playlistsReloadAnims.getTime(card.playlistsReloadText, 0.025F), -1.0F * playlistsReloadFactor
               );

               try {
                  if (tabsSettingsAmount > 0.004F) {
                     float panelOverlay = CorzRoundedUtils.getPanelOverlay(tabsSettingsAmount);

                     try {
                        renderReleaseRight(context, valueForInt, matrixStackValue, materialFactor, 0.0F, 0.0F);
                     } finally {
                        CorzRoundedUtils.handlePanelOverlay(panelOverlay);
                     }
                  }

                  if (playlistsReloadLimit > 0.002F) {
                     float panelOverlay2 = CorzRoundedUtils.getPanelOverlay(playlistsReloadLimit);

                     try {
                        this.renderLinkComing(context, fontRenderer, card, valueForInt, matrixStackValue, materialFactor, playlistsReloadFactor, matrixStackLimit, mouseY, seconds);
                     } finally {
                        CorzRoundedUtils.handlePanelOverlay(panelOverlay2);
                     }
                  }
               } finally {
                  renderPopMatrices(context);
               }

               if (playlistsReloadFlag) {
                  this.playlistsReloadHits.handleFlag(valueForInt, matrixStackValue, materialFactor, 64.0F, card.action, this.playlistsReloadAnims, card.playlistsReloadText);
               }
            }
         }
      }
   }

   public boolean canFlag(double radioComValue, double mouseY, int button) {
      return button == 0 && this.playlistsReloadHits.canAnimsPress(radioComValue, mouseY);
   }

   private static final class Card {
      String playlistText;
      final RadioStationRegistry.Station playlistStation;
      Runnable action;
      String linkComingText = "";
      final String founderOwnerText;
      final String playlistTrackText;
      float playlistAmount;
      float lastLayoutWidth;
      final String radioCorzText;
      final boolean founderTrackFlag;
      String cardText;
      String comingLinkText;
      final String playlistsReloadText;
      float cardAmount;
      final String upperCase;
      final String playlistTrackTextPlaylist;

      void handlePlaylistText(VertexTextureFont vertexTextureFont, float amount, float amount2) {
         if (amount != this.lastLayoutWidth || amount2 != this.cardAmount) {
            this.lastLayoutWidth = amount;
            this.cardAmount = amount2;
            this.linkComingText = vertexTextureFont.formatText(this.playlistStation.staff(), amount);
            this.playlistText = this.playlistStation.role().isEmpty() ? "" : vertexTextureFont.formatText(this.playlistStation.role(), (amount - 10.0F) / 0.78F);
            this.cardText = vertexTextureFont.formatText(this.playlistStation.title().isEmpty() ? "No playlist yet" : this.playlistStation.title(), amount2 / 0.82F);
            this.comingLinkText = MatrixStackUtilsAmount.getAmount(this.playlistTrackText) <= amount2 ? this.playlistTrackText : this.playlistTrackTextPlaylist;
            this.playlistAmount = vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont(this.upperCase);
         }
      }

      Card(RadioStationRegistry.Station data, int cardValue) {
         this.playlistText = "";
         this.cardText = "";
         this.comingLinkText = "";
         this.lastLayoutWidth = -1.0F;
         this.cardAmount = -1.0F;
         this.playlistStation = data;
         this.playlistsReloadText = "card:" + cardValue;
         this.radioCorzText = "play:" + cardValue;
         this.founderOwnerText = "on:" + cardValue;
         this.upperCase = data.staff().substring(0, 1).toUpperCase(Locale.ROOT);
         this.founderTrackFlag = data.role().equalsIgnoreCase("owner") || data.role().equalsIgnoreCase("founder");
         String playlistTrackText = data.tracks() <= 0 ? "" : (data.tracks() == 1 ? "1 track" : data.tracks() + " tracks");
         String tag = data.tag();
         if (!playlistTrackText.isEmpty() && !tag.isEmpty()) {
            this.playlistTrackText = playlistTrackText + " \u00b7 " + tag;
            this.playlistTrackTextPlaylist = playlistTrackText;
         } else if (playlistTrackText.isEmpty() && tag.isEmpty()) {
            this.playlistTrackText = RadioCom.getSubstring(data.url());
            this.playlistTrackTextPlaylist = this.playlistTrackText;
         } else {
            this.playlistTrackText = playlistTrackText.isEmpty() ? tag : playlistTrackText;
            this.playlistTrackTextPlaylist = this.playlistTrackText;
         }
      }
   }
}
