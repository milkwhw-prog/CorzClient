package com.corz.client.categories.visuals;

import com.corz.client.CorzClient;
import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.gui.MatrixPeekUtilsRender;
import com.corz.client.gui.NativeHud;
import com.corz.client.gui.TickProgress;
import com.corz.client.render.CorzRoundedUtils;
import com.corz.client.render.IcoGenUtils;
import com.corz.client.render.MatrixPeekUtilsIdentifier;
import com.corz.client.render.VertexTextureFont;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import ai.auth.Module;
import ai.auth.ModuleRegistry;
import com.corz.client.categories.visuals.ShowHUD;
import com.corz.client.utils.HudFontUtils;
import com.corz.client.utils.IconsPngType;

public class SpotifyHUD extends Module {
   private static final HttpClient build;
   private static volatile Identifier identifier = null;
   private static volatile SpotifyHUD.Track track = new SpotifyHUD.Track(
      "No song yet", "No song yet", SpotifyHUD.Lyrics.formatRoot("No song yet", "No song yet"), 226, 152000L, System.currentTimeMillis(), true
   );
   private static final Identifier identifierTextureIdentifier = Identifier.of("corzclient", "spotify/cover");
   private long lyricsCardTime;
   private final BooleanSetting booleanBase;
   private long lastNanoTime;
   private static volatile float spotifySongAmount = 0.0F;
   private SpotifyHUD.Lyrics.Doc doc;
   private static final ExecutorService threadExecutor;
   private long timeMillis;
   private final BooleanSetting lyrics;
   private static final Color OFF_WHITE = new Color(240, 243, 248, 255);
   private static final int[] songValues = new int[256];
   private static final AtomicReference<SpotifyHUD.Cover> ATOMIC_REFERENCE = new AtomicReference<>();
   private VertexTextureFont vertexTexture;
   private static final Object SONG_SPOTIFY_OBJECT = new Object();
   private long lastTimeMillis;
   private int lyricsHighlightingValue;
   private static final Color[] colors = new Color[17];
   private final BooleanSetting progressBar;
   private static final Color[] songColors = new Color[256];
   private ShowHUD.HudScale hudScale;
   private float lyricsHighlightingAmount;
   private final HashMap<Integer, SpotifyHUD.Wrap> rowsWordsWrapMap;
   private float lyricsCardAmount;
   private String scissorVertexText;
   private static volatile SpotifyHUD.CoverFail coverFail;
   private static final long LYRICS_LOOKAHEAD_MS = 120L;
   private final BooleanSetting autoDetect = new BooleanSetting("Auto Detect", "Follow the Spotify playing on this PC (Windows media session).", true);
   private int spotifyHudValue;
   private long lyricsHighlightTimeMs;
   private static final AtomicReference<String> CURRENT_COVER_KEY;
   private String[] colorsTexts;
   private static final MatrixStack MATRIX_STACK = new MatrixStack();
   private float[] colorsTable;

   private static String formatText(VertexTextureFont vertexTextureFont, String formatText, int spotifyHudLimit) {
      if (!formatText.isEmpty() && !(vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont(formatText) <= spotifyHudLimit)) {
         int spotifyHudValue = (int)vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont("...");
         StringBuilder stringBuilder = new StringBuilder();

         for (int i = 0; i < formatText.length() && !(vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont(stringBuilder.toString() + formatText.charAt(i)) + spotifyHudValue > spotifyHudLimit); i++) {
            stringBuilder.append(formatText.charAt(i));
         }

         return stringBuilder.append("...").toString();
      } else {
         return formatText;
      }
   }

   @Override
   protected void onRender2D(TickProgress tickProgress) {
      this.renderScaledWidth(tickProgress.drawContext);
   }

   public static void handleTrackSec(int secValue) {
      synchronized (SONG_SPOTIFY_OBJECT) {
         SpotifyHUD.Track track2 = track;
         track = new SpotifyHUD.Track(
            track2.title(),
            track2.artist(),
            track2.lyricKey(),
            track2.durationSec(),
            getTime(secValue * 1000L, track2.durationSec()),
            System.currentTimeMillis(),
            track2.playing()
         );
      }
   }

   @Override
   protected void onDisable() {
      SpotifyHUD.SpotifyDetector.updateStart();
   }

   private void handle(MatrixStack matrices, Identifier id2, int spotifyHudValue, int spotifyHudValue2, int peekPositionFactor, Color color) {
      MatrixPeekUtilsIdentifier.handle(spotifyHudValue - peekPositionFactor / 2.0F, spotifyHudValue2 - peekPositionFactor / 2.0F, peekPositionFactor, peekPositionFactor, id2, color, matrices);
   }

   private int getWordsLines(SpotifyHUD.Lyrics.Doc doc2, int index, long wordsLinesLimit, float wordsLinesValue, VertexTextureFont vertexTextureFont) {
      if (index < 0) {
         return index;
      } else {
         SpotifyHUD.Lyrics.Line line2 = doc2.lines()[index];
         SpotifyHUD.Wrap rowsWords = this.getRowsWords(doc2, index, wordsLinesValue, vertexTextureFont);
         int wordsLinesBound = 0;

         for (int i = 0; i < line2.words().length && line2.ws()[i] <= wordsLinesLimit; i++) {
            wordsLinesBound = rowsWords.rowOf()[i];
         }

         return index << 3 | Math.min(7, wordsLinesBound);
      }
   }

   private static void handleTrackSong(String songText, String referenceAtomicText, long songValue, int songBound, boolean songFlag, long songValue2) {
      String songText3 = songText != null ? songText : "";
      String rootText = referenceAtomicText != null ? referenceAtomicText : "";
      int maxResyncMs = Math.max(1, songBound);
      long currentMillis = System.currentTimeMillis();
      synchronized (SONG_SPOTIFY_OBJECT) {
         SpotifyHUD.Track track2 = track;
         boolean songFlag2 = songText3.equals(track2.title()) && rootText.equals(track2.artist());
         boolean songFlag3 = songFlag2 && maxResyncMs == track2.durationSec() && songFlag == track2.playing();
         if (!songFlag3 || Math.abs(songValue - track2.getMin(currentMillis)) >= songValue2) {
            track = new SpotifyHUD.Track(
               songText3, rootText, songFlag2 ? track2.lyricKey() : SpotifyHUD.Lyrics.formatRoot(rootText, songText3), maxResyncMs, getTime(songValue, maxResyncMs), currentMillis, songFlag
            );
         }
      }
   }

   public SpotifyHUD() {
      super("Spotify HUD", IconsPngType.VISUAL);
      this.booleanBase = new BooleanSetting("Album Art", "Show the album cover tile on the left.", true);
      this.progressBar = new BooleanSetting("Progress Bar", "Show the progress / time bar below the card.", true);
      this.lyrics = new BooleanSetting("Lyrics", "Show synced lyrics under the card, highlighting each word as it is sung.", true);
      this.lyricsCardAmount = 0.0F;
      this.lastNanoTime = 0L;
      this.lastTimeMillis = 0L;
      this.lyricsHighlightTimeMs = 0L;
      this.lyricsCardTime = 1000L;
      this.lyricsHighlightingValue = Integer.MIN_VALUE;
      this.spotifyHudValue = Integer.MIN_VALUE;
      this.lyricsHighlightingAmount = -1.0F;
      this.rowsWordsWrapMap = new HashMap<>();
      this.scissorVertexText = "";
      this.colorsTexts = new String[0];
      this.colorsTable = new float[0];
      this.addSettings(new ConfigSetting[]{this.autoDetect, this.booleanBase, this.progressBar, this.lyrics});
   }

   public static int getSpotifySong() {
      SpotifyHUD spotifyHUD = (SpotifyHUD)ModuleRegistry.getModule(SpotifyHUD.class);
      if (spotifyHUD != null && spotifyHUD.isEnabled()) {
         byte spotifySongValue = 5;
         int spotifySongBound = spotifyHUD.booleanBase.isEnabled() ? 20 : 0;
         int spotifySongValue2 = spotifySongValue
            + Math.max(
               spotifySongBound, (int)HudFontUtils.getSmallFont().getAbcdefghijklmnopqrstuvwxyzBrown() + 1 + (int)HudFontUtils.getLargeFont().getAbcdefghijklmnopqrstuvwxyzBrown()
            )
            + spotifySongValue;
         int spotifySongFactor = spotifySongValue2 + (spotifyHUD.progressBar.isEnabled() ? 13 : 0) + Math.round(spotifySongAmount);
         return 22 + Math.round(spotifySongFactor * ShowHUD.getAmountForString("spotify"));
      } else {
         return -1;
      }
   }

   private static String getReplace(String replaceText) {
      int indexOf = replaceText.indexOf("\"artworkUrl100\"");
      if (indexOf < 0) {
         return null;
      } else {
         int indexOf2 = replaceText.indexOf(34, replaceText.indexOf(58, indexOf) + 1);
         int end = indexOf2 < 0 ? -1 : replaceText.indexOf(34, indexOf2 + 1);
         return indexOf2 >= 0 && end >= 0 ? replaceText.substring(indexOf2 + 1, end).replace("\\/", "/") : null;
      }
   }

   static {
      for (int i = 0; i < colors.length; i++) {
         colors[i] = new Color(240, 243, 248, Math.round(255.0F * i / (colors.length - 1)));
      }

      build = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(6L)).followRedirects(Redirect.NORMAL).build();
      threadExecutor = Executors.newSingleThreadExecutor(runnable -> {
         Thread thread = new Thread(runnable, "corz-spotify-cover");
         thread.setDaemon(true);
         return thread;
      });
      CURRENT_COVER_KEY = new AtomicReference<>("");
      coverFail = null;
   }

   private static long getTime(long spotifyHudLimit, int spotifyHudValue) {
      long spotifyHudValue2 = spotifyHudValue * 1000L;
      return spotifyHudLimit < 0L ? 0L : (spotifyHudLimit > spotifyHudValue2 ? spotifyHudValue2 : spotifyHudLimit);
   }

   private static SpotifyHUD.Wrap getWrap(float[] wrapValues, float wrapValue, float rowsWordsValue) {
      int wrapSize = wrapValues.length;
      int[] ints = new int[wrapSize];
      float[] floats = new float[wrapSize];
      var arrayList = new ArrayList();
      float wrapTotal = 0.0F;
      int wrapCounter = 0;

      for (int i = 0; i < wrapSize; i++) {
         if (wrapTotal > 0.0F && wrapTotal + wrapValue + wrapValues[i] > rowsWordsValue) {
            arrayList.add(wrapTotal);
            wrapCounter++;
            wrapTotal = 0.0F;
         }

         if (wrapTotal > 0.0F) {
            wrapTotal += wrapValue;
         }

         ints[i] = wrapCounter;
         floats[i] = wrapTotal;
         wrapTotal += wrapValues[i];
      }

      arrayList.add(wrapTotal);
      float[] floats2 = new float[arrayList.size()];

      for (int j = 0; j < floats2.length; j++) {
         floats2[j] = (Float)arrayList.get(j);
      }

      return new SpotifyHUD.Wrap(ints, floats, floats2, floats2.length);
   }

   private static Color getSongColors(int songColorsValue, int songColorsValue2) {
      int songColorsValue3 = songColorsValue2 << 24 | songColorsValue << 16 | songColorsValue << 8 | Math.min(255, songColorsValue + 3);
      int index = songColorsValue3 * -1640531535 >>> 24;
      Color color2 = songColors[index];
      if (color2 == null || songValues[index] != songColorsValue3) {
         color2 = new Color(songColorsValue3, true);
         songColors[index] = color2;
         songValues[index] = songColorsValue3;
      }

      return color2;
   }

   private SpotifyHUD.Wrap getRowsWords(SpotifyHUD.Lyrics.Doc doc2, int index, float rowsWordsValue, VertexTextureFont vertexTextureFont) {
      SpotifyHUD.Wrap rowsWords = this.rowsWordsWrapMap.get(index);
      if (rowsWords != null) {
         return rowsWords;
      } else {
         String[] words = doc2.lines()[index].words();
         float[] floats = new float[words.length];

         for (int i = 0; i < words.length; i++) {
            floats[i] = vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont(words[i]);
         }

         float wrapValue = vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont(" ");
         SpotifyHUD.Wrap wrap = getWrap(floats, wrapValue, rowsWordsValue);

         for (float rowsWordsTotal = rowsWordsValue - 4.0F; wrap.rows() > 1 && rowsWordsTotal > rowsWordsValue * 0.5F; rowsWordsTotal -= 4.0F) {
            SpotifyHUD.Wrap wrap2 = getWrap(floats, wrapValue, rowsWordsTotal);
            if (wrap2.rows() != wrap.rows()) {
               break;
            }

            wrap = wrap2;
         }

         this.rowsWordsWrapMap.put(index, wrap);
         return wrap;
      }
   }

   public static void handleTrackSong(boolean songFlag) {
      long currentMillis = System.currentTimeMillis();
      synchronized (SONG_SPOTIFY_OBJECT) {
         SpotifyHUD.Track track2 = track;
         track = new SpotifyHUD.Track(track2.title(), track2.artist(), track2.lyricKey(), track2.durationSec(), track2.getMin(currentMillis), currentMillis, songFlag);
      }
   }

   private static long getStartLines(SpotifyHUD.Lyrics.Doc doc2, int linesValue, long linesValue2) {
      SpotifyHUD.Lyrics.Line[] lines = doc2.lines();
      if (linesValue == -1) {
         return lines.length > 0 ? lines[0].start() : 1L;
      } else {
         int docOffset = -(linesValue + 2);
         return docOffset + 1 < lines.length ? lines[docOffset + 1].start() : linesValue2;
      }
   }

   private void renderRowLyrics(
      DrawContext context, SpotifyHUD.Lyrics.Doc doc2, int rowLyricsLimit, long rowLyricsValue, float rowLyricsValue2, float f13, float rowsWordsValue, float rowLyricsValue5, boolean rowLyricsFlag, VertexTextureFont vertexTextureFont
   ) {
      if (!(rowLyricsValue5 < 0.01F) && rowLyricsLimit != Integer.MIN_VALUE) {
         float rowLyricsValue6 = vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzBrown() + 2.0F;
         if (rowLyricsLimit >= 0) {
            int index = rowLyricsLimit >>> 3;
            int index2 = rowLyricsLimit & 7;
            SpotifyHUD.Lyrics.Line line = doc2.lines()[index];
            SpotifyHUD.Wrap rowsWords = this.getRowsWords(doc2, index, rowsWordsValue, vertexTextureFont);
            if (index2 < rowsWords.rows()) {
               String[] words = line.words();
               float rowLyricsValue7 = rowLyricsValue2 + (rowsWordsValue - rowsWords.rowW()[index2]) / 2.0F;

               for (int i = 0; i < words.length; i++) {
                  if (rowsWords.rowOf()[i] == index2) {
                     float spotifyHudFactor = rowLyricsFlag ? 1.0F : getMin((float)(rowLyricsValue - line.ws()[i]) / (float)Math.max(160L, line.we()[i] - line.ws()[i]));
                     float amountForFloat = getAmountForFloat(spotifyHudFactor);
                     int round = Math.round(255.0F * rowLyricsValue5 * (0.34F + 0.66F * amountForFloat));
                     if (round >= 3) {
                        int songColorsValue = Math.round(212.0F + 41.0F * amountForFloat);
                        vertexTextureFont.handleForStringFloat(words[i], rowLyricsValue7 + rowsWords.xOf()[i], f13, getSongColors(songColorsValue, Math.min(255, round)));
                     }
                  }
               }
            }
         } else {
            long linesEnd = getLinesEnd(doc2, rowLyricsLimit);
            long startLines = getStartLines(doc2, rowLyricsLimit, this.lyricsCardTime);
            float rowLyricsValue11 = rowLyricsFlag ? 1.0F : getMin((float)(rowLyricsValue - linesEnd) / (float)Math.max(1L, startLines - linesEnd));
            float rowLyricsFactor = 0.88F + 0.12F * (float)Math.sin(System.currentTimeMillis() / 420.0);
            byte releaseRightLimit = 3;
            byte rowLyricsValue13 = 4;
            int rowLyricsValue14 = releaseRightLimit * 3 + rowLyricsValue13 * 2;
            int round15 = Math.round(rowLyricsValue2 + (rowsWordsValue - rowLyricsValue14) / 2.0F);
            int editValue = Math.round(f13 + (rowLyricsValue6 - releaseRightLimit) / 2.0F);

            for (int j = 0; j < 3; j++) {
               float rowLyricsValue17 = getAmount(getMin(rowLyricsValue11 * 3.0F - j));
               int round18 = Math.round(255.0F * rowLyricsValue5 * (0.28F + 0.72F * rowLyricsValue17) * rowLyricsFactor);
               MatrixPeekUtilsRender.renderMatrixStack(context, round15 + j * (releaseRightLimit + rowLyricsValue13), editValue, releaseRightLimit, releaseRightLimit, 1, round18 << 24 | 16251388);
            }
         }
      }
   }

   private static int getStartEnd(SpotifyHUD.Lyrics.Doc doc2, long spotifyHudLimit) {
      SpotifyHUD.Lyrics.Line[] lines = doc2.lines();
      int spotifyHudValue = 0;
      int spotifyHudLimit2 = lines.length - 1;
      int index = -1;

      while (spotifyHudValue <= spotifyHudLimit2) {
         int index2 = spotifyHudValue + spotifyHudLimit2 >>> 1;
         if (lines[index2].start() <= spotifyHudLimit) {
            index = index2;
            spotifyHudValue = index2 + 1;
         } else {
            spotifyHudLimit2 = index2 - 1;
         }
      }

      if (index < 0) {
         return !lines[0].isWords() && lines[0].start() - spotifyHudLimit < 1800L ? 0 : -1;
      } else {
         SpotifyHUD.Lyrics.Line line = lines[index];
         SpotifyHUD.Lyrics.Line line2 = index + 1 < lines.length ? lines[index + 1] : null;
         if (!line.isWords()) {
            return line2 != null && !line2.isWords() && spotifyHudLimit > line.end() + 1000L && line2.start() - line.end() > 3500L && line2.start() - spotifyHudLimit > 900L
               ? -(index + 2)
               : index;
         } else {
            return line2 == null || line2.isWords() || line2.start() - spotifyHudLimit >= 900L && line2.start() - line.start() >= 2500L ? -(index + 2) : index + 1;
         }
      }
   }

   private static long getLinesEnd(SpotifyHUD.Lyrics.Doc doc2, int linesEndValue) {
      if (linesEndValue == -1) {
         return 0L;
      } else {
         SpotifyHUD.Lyrics.Line line = doc2.lines()[-(linesEndValue + 2)];
         return line.isWords() ? line.start() : line.end();
      }
   }

   private static void handleTrackSong(String songText, String referenceAtomicText, boolean songFlag) {
      String songText3 = songText != null ? songText : "";
      String rootText = referenceAtomicText != null ? referenceAtomicText : "";
      long currentMillis = System.currentTimeMillis();
      synchronized (SONG_SPOTIFY_OBJECT) {
         SpotifyHUD.Track track2 = track;
         boolean songFlag2 = songText3.equals(track2.title()) && rootText.equals(track2.artist());
         if (!songFlag2 || songFlag != track2.playing()) {
            track = new SpotifyHUD.Track(
               songText3, rootText, songFlag2 ? track2.lyricKey() : SpotifyHUD.Lyrics.formatRoot(rootText, songText3), track2.durationSec(), track2.getMin(currentMillis), currentMillis, songFlag
            );
         }
      }
   }

   private static String getFormat(int spotifyHudValue) {
      if (spotifyHudValue < 0) {
         spotifyHudValue = 0;
      }

      return String.format(Locale.ROOT, "%d:%02d", spotifyHudValue / 60, spotifyHudValue % 60);
   }

   private void renderSpotifyIdentifier(DrawContext context, MatrixStack matrices, int spotifyIdentifierValue, float scaledWidthDivisor, boolean spotifyIdentifierFlag) {
      VertexTextureFont font = HudFontUtils.getLargeFont();
      VertexTextureFont smallFont = HudFontUtils.getSmallFont();
      byte spotifyIdentifierValue3 = 5;
      boolean booleanValue = this.booleanBase.isEnabled();
      int spotifyIdentifierBound = booleanValue ? 20 : 0;
      int spotifyIdentifierValue4 = booleanValue ? 6 : 0;
      byte spotifyHudLimit = 70;
      byte spotifyIdentifierValue6 = 6;
      byte peekPositionFactor = 10;
      byte spotifyIdentifierValue8 = 13;
      byte spotifyIdentifierValue9 = 5;
      int spotifyIdentifierValue10 = peekPositionFactor + spotifyIdentifierValue9 + spotifyIdentifierValue8 + spotifyIdentifierValue9 + peekPositionFactor;
      int spotifyIdentifierValue11 = (int)font.getAbcdefghijklmnopqrstuvwxyzBrown();
      int spotifyIdentifierValue12 = (int)smallFont.getAbcdefghijklmnopqrstuvwxyzBrown();
      int bottomFactor = spotifyIdentifierValue3 + spotifyIdentifierBound + spotifyIdentifierValue4 + spotifyHudLimit + spotifyIdentifierValue6 + spotifyIdentifierValue10 + spotifyIdentifierValue3;
      int spotifyIdentifierValue14 = spotifyIdentifierValue3 + Math.max(spotifyIdentifierBound, spotifyIdentifierValue12 + 1 + spotifyIdentifierValue11) + spotifyIdentifierValue3;
      SpotifyHUD.Track track2 = track;
      long min15 = track2.getMin(System.currentTimeMillis());
      boolean enabled = this.progressBar.isEnabled();
      byte spotifyIdentifierValue16 = 0;
      byte boxHeight = 13;
      int spotifyIdentifierValue18 = spotifyIdentifierValue14 + (enabled ? spotifyIdentifierValue16 + boxHeight : 0);
      float lyricsHighlighting = this.getLyricsHighlighting(bottomFactor - spotifyIdentifierValue3 * 2.0F, font, track2, min15);
      spotifySongAmount = lyricsHighlighting;
      int maxRadarSize = spotifyIdentifierValue18 + (int)Math.ceil(lyricsHighlighting);
      float glfwLimit = ShowHUD.getAmount4("spotify");
      float amountFloat = ShowHUD.getAmountFloat("spotify");
      int spotifyIdentifierValue22 = (spotifyIdentifierValue - bottomFactor) / 2;
      byte spotifyIdentifierValue23 = 22;
      int spotifyIdentifierValue24 = spotifyIdentifierValue22 + Math.round(glfwLimit);
      int spotifyIdentifierValue25 = spotifyIdentifierValue23 + Math.round(amountFloat);
      ShowHUD.HudScale hudScale = ShowHUD.getHudHotbar("spotify", spotifyIdentifierValue22 + glfwLimit, spotifyIdentifierValue23 + amountFloat, bottomFactor, maxRadarSize);
      this.hudScale = hudScale;
      if (hudScale != null) {
         hudScale.handleAmountTranslate(matrices);
      }

      this.render(context, spotifyIdentifierValue24, spotifyIdentifierValue25, bottomFactor, maxRadarSize, 7);
      int spotifyIdentifierTotal = spotifyIdentifierValue24 + spotifyIdentifierValue3;
      if (booleanValue) {
         int roundOffset = spotifyIdentifierValue25 + (spotifyIdentifierValue14 - spotifyIdentifierBound) / 2;
         if (identifier != null) {
            MatrixPeekUtilsIdentifier.handleWhite(spotifyIdentifierTotal, roundOffset, spotifyIdentifierBound, spotifyIdentifierBound, identifier, matrices);
         } else {
            MatrixPeekUtilsRender.renderMatrixStack(context, spotifyIdentifierTotal, roundOffset, spotifyIdentifierBound, spotifyIdentifierBound, 4, new Color(46, 50, 62, 235).getRGB());
            MatrixPeekUtilsRender.renderMatrixStack(context, spotifyIdentifierTotal, roundOffset + spotifyIdentifierBound / 2, spotifyIdentifierBound, spotifyIdentifierBound / 2, 4, new Color(32, 35, 45, 200).getRGB());
            byte spotifyIdentifierValue27 = 11;
            MatrixPeekUtilsIdentifier.handle(
               spotifyIdentifierTotal + (spotifyIdentifierBound - spotifyIdentifierValue27) / 2.0F, roundOffset + (spotifyIdentifierBound - spotifyIdentifierValue27) / 2.0F, spotifyIdentifierValue27, spotifyIdentifierValue27, IcoGenUtils.getNoteIco(), new Color(200, 205, 215, 220), matrices
            );
         }

         spotifyIdentifierTotal += spotifyIdentifierBound + spotifyIdentifierValue4;
      }

      int spotifyIdentifierValue28 = spotifyIdentifierValue12 + 1 + spotifyIdentifierValue11;
      int f13 = spotifyIdentifierValue25 + (spotifyIdentifierValue14 - spotifyIdentifierValue28) / 2;
      smallFont.handleForStringFloat(formatText(smallFont, track2.artist(), spotifyHudLimit), spotifyIdentifierTotal, f13, new Color(178, 184, 196, 235));
      this.renderScissorVertex(context, font, track2.title(), spotifyIdentifierTotal, f13 + spotifyIdentifierValue12 + 1, spotifyHudLimit, spotifyIdentifierValue14, spotifyIdentifierValue25, scaledWidthDivisor, spotifyIdentifierFlag);
      int spotifyIdentifierValue30 = spotifyIdentifierValue25 + spotifyIdentifierValue14 / 2;
      int spotifyIdentifierValue31 = spotifyIdentifierValue24 + bottomFactor - spotifyIdentifierValue3 - spotifyIdentifierValue10;
      Color color = new Color(212, 217, 227, 240);
      this.handle(matrices, IcoGenUtils.getMprevIco(), spotifyIdentifierValue31 + peekPositionFactor / 2, spotifyIdentifierValue30, peekPositionFactor, color);
      int spotifyIdentifierValue32 = spotifyIdentifierValue31 + peekPositionFactor + spotifyIdentifierValue9;
      this.handle(
         matrices, track2.playing() ? IcoGenUtils.getMpauseIco() : IcoGenUtils.getMplayIco(), spotifyIdentifierValue32 + spotifyIdentifierValue8 / 2, spotifyIdentifierValue30, spotifyIdentifierValue8, new Color(247, 249, 252, 255)
      );
      int spotifyIdentifierValue33 = spotifyIdentifierValue32 + spotifyIdentifierValue8 + spotifyIdentifierValue9;
      this.handle(matrices, IcoGenUtils.getMnextIco(), spotifyIdentifierValue33 + peekPositionFactor / 2, spotifyIdentifierValue30, peekPositionFactor, color);
      if (enabled) {
         int brownValue = spotifyIdentifierValue25 + spotifyIdentifierValue14 + spotifyIdentifierValue16;
         MatrixPeekUtilsRender.renderMatrixPeek(context, spotifyIdentifierValue24 + spotifyIdentifierValue3, brownValue, bottomFactor - spotifyIdentifierValue3 * 2, 0.5F, 486539263);
         int durationSec = track2.durationSec();
         int spotifyIdentifierValue35 = (int)(min15 / 1000L);
         int max36 = Math.max(0, durationSec - spotifyIdentifierValue35);
         float hashHuntBound = (float)(min15 / (durationSec * 1000.0));
         String format = getFormat(spotifyIdentifierValue35);
         String spotifyIdentifierText = "-" + getFormat(max36);
         int spotifyIdentifierValue37 = (int)smallFont.getAbcdefghijklmnopqrstuvwxyzCorzfont(spotifyIdentifierText);
         int abcdefghijklmnopqrstuvwxyzBrown = smallFont.getAbcdefghijklmnopqrstuvwxyzBrown(brownValue, boxHeight);
         smallFont.handleForStringFloat(format, spotifyIdentifierValue24 + spotifyIdentifierValue3, abcdefghijklmnopqrstuvwxyzBrown, new Color(216, 221, 231, 240));
         smallFont.handleForStringFloat(spotifyIdentifierText, spotifyIdentifierValue24 + bottomFactor - spotifyIdentifierValue3 - spotifyIdentifierValue37, abcdefghijklmnopqrstuvwxyzBrown, new Color(150, 156, 168, 225));
         int matrixStackValue = spotifyIdentifierValue24 + spotifyIdentifierValue3 + (int)smallFont.getAbcdefghijklmnopqrstuvwxyzCorzfont(format) + 7;
         int spotifyIdentifierLimit = spotifyIdentifierValue24 + bottomFactor - spotifyIdentifierValue3 - spotifyIdentifierValue37 - 7 - matrixStackValue;
         byte n4 = 3;
         int editValue = brownValue + (boxHeight - n4) / 2;
         if (spotifyIdentifierLimit > 4) {
            MatrixPeekUtilsRender.renderMatrixStack(context, matrixStackValue, editValue, spotifyIdentifierLimit, n4, 1, new Color(255, 255, 255, 40).getRGB());
            int round = Math.round(spotifyIdentifierLimit * Math.max(0.0F, Math.min(1.0F, hashHuntBound)));
            if (round > 0) {
               MatrixPeekUtilsRender.renderMatrixStack(context, matrixStackValue, editValue, round, n4, 1, new Color(245, 247, 250, 245).getRGB());
            }
         }
      }

      if (lyricsHighlighting > 0.5F) {
         this.renderLyricsHighlighting(context, spotifyIdentifierValue24, spotifyIdentifierValue25 + spotifyIdentifierValue18, bottomFactor, spotifyIdentifierValue3, font);
      }

      if (hudScale != null) {
         matrices.pop();
      }
   }

   private void renderLyricsHighlighting(DrawContext context, int lyricsHighlightingValue, int f2, int bottomFactor, int lyricsHighlightingValue4, VertexTextureFont vertexTextureFont) {
      SpotifyHUD.Lyrics.Doc doc = this.doc;
      if (doc != null) {
         float lyricsHighlightingFactor = getAmount(this.lyricsCardAmount);
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(lyricsHighlightingFactor * lyricsHighlightingFactor);

         try {
            MatrixPeekUtilsRender.renderMatrixPeek(context, lyricsHighlightingValue + lyricsHighlightingValue4, f2, bottomFactor - lyricsHighlightingValue4 * 2, 0.5F, 486539263);
            long rowLyricsValue = this.lyricsHighlightTimeMs;
            float min5 = Math.min(1.0F, (float)(System.currentTimeMillis() - this.lastTimeMillis) / 340.0F);
            float lyricsHighlightingValue6 = this.spotifyHudValue == Integer.MIN_VALUE ? 1.0F : getAmount(min5);
            float lyricsHighlightingValue7 = lyricsHighlightingValue + lyricsHighlightingValue4;
            float rowsWordsValue = bottomFactor - lyricsHighlightingValue4 * 2.0F;
            float lyricsHighlightingValue9 = f2 + 6.0F;
            if (lyricsHighlightingValue6 < 1.0F) {
               this.renderRowLyrics(
                  context, doc, this.spotifyHudValue, rowLyricsValue, lyricsHighlightingValue7, lyricsHighlightingValue9 - 3.0F * lyricsHighlightingValue6, rowsWordsValue, (1.0F - lyricsHighlightingValue6) * (1.0F - lyricsHighlightingValue6), true, vertexTextureFont
               );
            }

            this.renderRowLyrics(context, doc, this.lyricsHighlightingValue, rowLyricsValue, lyricsHighlightingValue7, lyricsHighlightingValue9 + 3.0F * (1.0F - lyricsHighlightingValue6), rowsWordsValue, lyricsHighlightingValue6, false, vertexTextureFont);
         } finally {
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
         }
      }
   }

   public static void handle(String spotifyHudText, String referenceAtomicText, int spotifyHudValue, int songBound, boolean spotifyHudFlag) {
      handleTrackSong(spotifyHudText, referenceAtomicText, spotifyHudValue * 1000L, songBound, spotifyHudFlag, 5000L);
   }

   private static void handleReferenceAtomic(String referenceAtomicText, String songText) {
      if (songText != null && !songText.isEmpty()) {
         String lowerCase = (referenceAtomicText + "\u001f" + songText).toLowerCase(Locale.ROOT);
         if (!lowerCase.equals(CURRENT_COVER_KEY.get())) {
            identifier = null;
            ATOMIC_REFERENCE.set(null);
            SpotifyHUD.CoverFail coverFail2 = coverFail;
            if (coverFail2 == null || !coverFail2.key().equals(lowerCase) || System.currentTimeMillis() - coverFail2.ms() >= 15000L) {
               CURRENT_COVER_KEY.set(lowerCase);
               threadExecutor.submit(
                  () -> {
                     if (lowerCase.equals(CURRENT_COVER_KEY.get())) {
                        try {
                           String var3x = URLEncoder.encode((referenceAtomicText + " " + songText).trim(), StandardCharsets.UTF_8);
                           URI create = URI.create("https://itunes.apple.com/search?term=" + var3x + "&entity=song&limit=1");
                           HttpRequest build2 = HttpRequest.newBuilder(create).timeout(Duration.ofSeconds(8L)).header("User-Agent", "CorzClient").GET().build();
                           var send = build.send(build2, BodyHandlers.ofString());
                           if (send.statusCode() != 200) {
                              throw new IOException("iTunes search HTTP " + send.statusCode());
                           }

                           String replace = getReplace((String)send.body());
                           if (replace == null) {
                              return;
                           }

                           if (!lowerCase.equals(CURRENT_COVER_KEY.get())) {
                              return;
                           }

                           String bytesText = replace.replace("100x100bb", "300x300bb");
                           var send2 = build.send(
                              HttpRequest.newBuilder(URI.create(bytesText)).timeout(Duration.ofSeconds(8L)).GET().build(), BodyHandlers.ofByteArray()
                           );
                           if (send2.statusCode() != 200) {
                              throw new IOException("iTunes artwork HTTP " + send2.statusCode());
                           }

                           BufferedImage read = ImageIO.read(new ByteArrayInputStream((byte[])send2.body()));
                           if (read == null) {
                              return;
                           }

                           if (!lowerCase.equals(CURRENT_COVER_KEY.get())) {
                              return;
                           }

                           int width = read.getWidth();
                           int height = read.getHeight();
                           ATOMIC_REFERENCE.set(new SpotifyHUD.Cover(lowerCase, width, height, read.getRGB(0, 0, width, height, null, 0, width)));
                        } catch (Throwable e) {
                           coverFail = new SpotifyHUD.CoverFail(lowerCase, System.currentTimeMillis());
                           CURRENT_COVER_KEY.compareAndSet(lowerCase, "");
                        }
                     }
                  }
               );
            }
         }
      }
   }

   private void handleAmountsTexts(VertexTextureFont vertexTextureFont, String spotifyHudText) {
      int index = spotifyHudText.length();
      this.colorsTexts = new String[index];
      this.colorsTable = new float[index + 1];

      for (int i = 0; i < index; i++) {
         this.colorsTexts[i] = String.valueOf(spotifyHudText.charAt(i));
         this.colorsTable[i] = vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont(spotifyHudText.substring(0, i));
      }

      this.colorsTable[index] = vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont(spotifyHudText);
   }

   @Override
   protected void onReset() {
      if (this.autoDetect.isEnabled()) {
         SpotifyHUD.SpotifyDetector.updateSpotifyDetect();
      } else {
         SpotifyHUD.SpotifyDetector.updateStart();
      }
   }

   public void renderScaledWidth(DrawContext context) {
      if (context != null) {
         SpotifyHUD.Cover cover = ATOMIC_REFERENCE.get() == null ? null : ATOMIC_REFERENCE.getAndSet(null);
         if (cover != null) {
            handleIdentifierTexture(cover);
         }

         MatrixStack matrixStack = new MatrixStack();
         CorzClient.fontManager.setMatrixStack(matrixStack);
         MatrixPeekUtilsRender.handleMatrixStack(matrixStack);
         double scaleFactor = mc.getWindow().getScaleFactor();
         int calculateScaleFactor = mc.getWindow().calculateScaleFactor(ShowHUD.getMax(mc), mc.forcesUnicodeFont());
         float scaledWidthDivisor = (float)(calculateScaleFactor / scaleFactor);
         boolean scaledWidthFlag = Math.abs(scaledWidthDivisor - 1.0F) > 1.0E-4F;
         int scaledWidth;
         if (scaledWidthFlag) {
            scaledWidth = Math.round(mc.getWindow().getScaledWidth() / scaledWidthDivisor);
            CorzRoundedUtils.scaledWidth = scaledWidth;
            CorzRoundedUtils.scaledHeight = Math.round(mc.getWindow().getScaledHeight() / scaledWidthDivisor);
         } else {
            scaledWidth = mc.getWindow().getScaledWidth();
         }

         float scaledWidthValue = ShowHUD.getTime();
         if (scaledWidthValue < 0.004F) {
            if (scaledWidthFlag) {
               CorzRoundedUtils.scaledWidth = 0.0F;
               CorzRoundedUtils.scaledHeight = 0.0F;
            }
         } else {
            float panelOverlay = CorzRoundedUtils.getPanelOverlay(scaledWidthValue);

            try {
               this.renderSpotifyIdentifier(context, matrixStack, scaledWidth, scaledWidthDivisor, scaledWidthFlag);
            } finally {
               CorzRoundedUtils.handlePanelOverlay(panelOverlay);
               if (scaledWidthFlag) {
                  CorzRoundedUtils.scaledWidth = 0.0F;
                  CorzRoundedUtils.scaledHeight = 0.0F;
               }
            }
         }
      }
   }

   private float getLyricsHighlighting(float lyricsHighlightingAmount, VertexTextureFont vertexTextureFont, SpotifyHUD.Track track, long lyricsHighlightingValue) {
      long nanoTime = System.nanoTime();
      float lyricsHighlightingValue2 = this.lastNanoTime == 0L ? 0.0F : Math.min(0.1F, (float)(nanoTime - this.lastNanoTime) / 1.0E9F);
      this.lastNanoTime = nanoTime;
      SpotifyHUD.Lyrics.Doc doc = this.lyrics.isEnabled() ? SpotifyHUD.Lyrics.getDoc(track.lyricKey()) : null;
      if (doc != null && doc.lines().length == 0) {
         doc = null;
      }

      if (doc != null && doc != this.doc) {
         this.doc = doc;
         this.rowsWordsWrapMap.clear();
         this.lyricsHighlightingValue = Integer.MIN_VALUE;
         this.spotifyHudValue = Integer.MIN_VALUE;
      }

      if (lyricsHighlightingAmount != this.lyricsHighlightingAmount) {
         this.rowsWordsWrapMap.clear();
         this.lyricsHighlightingAmount = lyricsHighlightingAmount;
      }

      this.lyricsCardAmount = getAmount(this.lyricsCardAmount, doc != null ? 1.0F : 0.0F, lyricsHighlightingValue2, 0.16F);
      if (this.doc != null && !(this.lyricsCardAmount < 0.003F)) {
         if (doc != null) {
            this.lyricsHighlightTimeMs = lyricsHighlightingValue + 120L;
            this.lyricsCardTime = track.durationSec() * 1000L;
            int wordsLines = this.getWordsLines(this.doc, getStartEnd(this.doc, this.lyricsHighlightTimeMs), this.lyricsHighlightTimeMs, lyricsHighlightingAmount, vertexTextureFont);
            if (wordsLines != this.lyricsHighlightingValue) {
               this.spotifyHudValue = this.lyricsHighlightingValue;
               this.lyricsHighlightingValue = wordsLines;
               this.lastTimeMillis = System.currentTimeMillis();
            }
         }

         return (6.0F + vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzBrown() + 2.0F + 6.0F) * getAmount(this.lyricsCardAmount);
      } else {
         if (doc == null) {
            this.doc = null;
            this.lyricsCardAmount = 0.0F;
         }

         return 0.0F;
      }
   }

   private void renderScissorVertex(DrawContext context, VertexTextureFont vertexTexture, String text, int scissorVertexValue, int f13, int scissorVertexLimit, int scissorVertexValue3, int editValue, float scaledWidthDivisor, boolean scissorVertexFlag) {
      float scissorVertexValue6 = vertexTexture.getAbcdefghijklmnopqrstuvwxyzCorzfont(text);
      if (scissorVertexValue6 <= scissorVertexLimit) {
         vertexTexture.handleForStringFloat(text, scissorVertexValue, f13, OFF_WHITE);
      } else {
         long timeMillis = System.currentTimeMillis();
         if (!text.equals(this.scissorVertexText) || vertexTexture != this.vertexTexture) {
            this.scissorVertexText = text;
            this.vertexTexture = vertexTexture;
            this.timeMillis = timeMillis;
            this.handleAmountsTexts(vertexTexture, text);
         }

         float scissorVertexBound = scissorVertexValue6 + 28.0F;
         long ceil7 = (long)Math.ceil(scissorVertexBound * 1000.0F / 28.0F);
         long scissorVertexValue8 = (timeMillis - this.timeMillis) % (2600L + ceil7);
         float scissorVertexLimit2 = scissorVertexValue8 < 2600L ? 0.0F : Math.min(scissorVertexBound, (float)(scissorVertexValue8 - 2600L) * 28.0F / 1000.0F);
         ShowHUD.HudScale hudScale = this.hudScale;
         float scissorVertexValue2 = scissorVertexValue;
         float editValue2 = editValue;
         float scissorVertexFactor = scissorVertexValue + scissorVertexLimit;
         float hudScaleValue = editValue + scissorVertexValue3;
         if (hudScale != null) {
            scissorVertexValue2 = hudScale.getAmount(scissorVertexValue2);
            editValue2 = hudScale.getAmountForFloat(editValue2);
            scissorVertexFactor = hudScale.getAmount(scissorVertexFactor);
            hudScaleValue = hudScale.getAmountForFloat(hudScaleValue);
         }

         float scissorVertexFactor3 = scissorVertexFlag ? scaledWidthDivisor : 1.0F;
         MatrixStack matrixStack = MatrixPeekUtilsRender.getMatrixStack();
         MatrixPeekUtilsRender.handleMatrixStack(MATRIX_STACK);

         try {
            context.enableScissor((int)Math.floor(scissorVertexValue2 * scissorVertexFactor3), (int)Math.floor(editValue2 * scissorVertexFactor3), (int)Math.ceil(scissorVertexFactor * scissorVertexFactor3), (int)Math.ceil(hudScaleValue * scissorVertexFactor3));
         } finally {
            MatrixPeekUtilsRender.handleMatrixStack(matrixStack);
         }

         float scissorVertexValue9 = Math.min(1.0F, scissorVertexLimit2 / 12.0F) * Math.min(1.0F, (scissorVertexBound - scissorVertexLimit2) / 12.0F);
         this.handleTextsColors(vertexTexture, scissorVertexValue - scissorVertexLimit2, f13, scissorVertexValue, scissorVertexValue + scissorVertexLimit, scissorVertexValue9);
         if (scissorVertexLimit2 > 0.0F) {
            this.handleTextsColors(vertexTexture, scissorVertexValue - scissorVertexLimit2 + scissorVertexBound, f13, scissorVertexValue, scissorVertexValue + scissorVertexLimit, scissorVertexValue9);
         }

         context.disableScissor();
      }
   }

   private static float getAmount(float spotifyHudValue) {
      float spotifyHudFactor = 1.0F - getMin(spotifyHudValue);
      return 1.0F - spotifyHudFactor * spotifyHudFactor * spotifyHudFactor;
   }

   private static float getAmountForFloat(float spotifyHudFactor) {
      spotifyHudFactor = getMin(spotifyHudFactor);
      return spotifyHudFactor * spotifyHudFactor * (3.0F - 2.0F * spotifyHudFactor);
   }

   private void handleTextsColors(VertexTextureFont vertexTextureFont, float colorsValue, int f13, int colorsLimit, int colorsValue3, float colorsLimit2) {
      for (int i = 0; i < this.colorsTexts.length; i++) {
         float vertexTextureValue = colorsValue + this.colorsTable[i];
         float colorsValue4 = this.colorsTable[i + 1] - this.colorsTable[i];
         float colorsValue5 = vertexTextureValue + colorsValue4 / 2.0F;
         if (!(vertexTextureValue + colorsValue4 < colorsLimit) && !(vertexTextureValue > colorsValue3) && !this.colorsTexts[i].isBlank()) {
            float colorsBound = Math.min(1.0F, (colorsValue3 - colorsValue5) / 12.0F);
            if (colorsLimit2 > 0.0F) {
               colorsBound = Math.min(colorsBound, 1.0F - colorsLimit2 + colorsLimit2 * Math.min(1.0F, (colorsValue5 - colorsLimit) / 12.0F));
            }

            if (!(colorsBound <= 0.02F)) {
               vertexTextureFont.handleForStringFloat(this.colorsTexts[i], vertexTextureValue, f13, colors[Math.round(getMin(colorsBound) * (colors.length - 1))]);
            }
         }
      }
   }

   private static float getAmount(float spotifyHudValue, float spotifyHudValue2, float spotifyHudValue3, float spotifyHudDivisor) {
      if (spotifyHudValue3 <= 0.0F) {
         return spotifyHudValue;
      } else {
         float spotifyHudValue4 = spotifyHudValue + (spotifyHudValue2 - spotifyHudValue) * (1.0F - (float)Math.exp(-spotifyHudValue3 / spotifyHudDivisor));
         return Math.abs(spotifyHudValue4 - spotifyHudValue2) < 0.002F ? spotifyHudValue2 : spotifyHudValue4;
      }
   }

   private void render(DrawContext context, int spotifyHudValue, int editValue, int bottomFactor, int maxRadarSize, int iconSize) {
      if (ShowHUD.isFlag()) {
         NativeHud.handle(NativeHud.SPOTIFY_HUD_ID, (float)spotifyHudValue, (float)editValue, (float)bottomFactor, (float)maxRadarSize, 1.0F);
      } else {
         MatrixPeekUtilsRender.renderMatrixStack(context, spotifyHudValue, editValue, bottomFactor, maxRadarSize, iconSize, -671088640);
      }
   }

   private static float getMin(float spotifyHudLimit) {
      return spotifyHudLimit < 0.0F ? 0.0F : (spotifyHudLimit > 1.0F ? 1.0F : spotifyHudLimit);
   }

   public static void handleIdentifier(Identifier identifier2) {
      identifier = identifier2;
   }

   private static void handleIdentifierTexture(SpotifyHUD.Cover cover) {
      if (cover.key.equals(CURRENT_COVER_KEY.get())) {
         try {
            NativeImage nativeImage = new NativeImage(cover.w, cover.h, false);

            for (int i = 0; i < cover.px.length; i++) {
               nativeImage.setColorArgb(i % cover.w, i / cover.w, cover.px[i]);
            }

            mc.getTextureManager().registerTexture(identifierTextureIdentifier, new NativeImageBackedTexture(() -> "corz_spotify_cover", nativeImage));
            identifier = identifierTextureIdentifier;
            if (!cover.key.equals(CURRENT_COVER_KEY.get())) {
               identifier = null;
            }
         } catch (Throwable e) {
         }
      }
   }

   private record Cover(String key, int w, int h, int[] px) {
   }

   private record CoverFail(String key, long ms) {
   }

   private static final class Lyrics {
      private static final Pattern compile = Pattern.compile("\\[(\\d{1,3}):(\\d{1,2}(?:[.:]\\d{1,3})?)]");
      private static final Pattern lyricsPattern = Pattern.compile("<(\\d{1,3}):(\\d{1,2}(?:[.:]\\d{1,3})?)>");
      private static final Map<String, SpotifyHUD.Lyrics.Doc> DOC_CACHE = new LinkedHashMap<String, SpotifyHUD.Lyrics.Doc>(16, 0.75F, true) {
         @Override
         protected boolean removeEldestEntry(Entry<String, SpotifyHUD.Lyrics.Doc> entry) {
            return this.size() > 48;
         }
      };
      private static final long LOOKAHEAD_MS = 120L;
      private static final ExecutorService threadPool = Executors.newCachedThreadPool(runnable -> {
         Thread thread = new Thread(runnable, "corz-spotify-lyrics");
         thread.setDaemon(true);
         return thread;
      });
      private static volatile String latestRequestKey = "";
      private static volatile SpotifyHUD.Lyrics.Doc doc;
      private static volatile long timeMillis = 0L;
      private static final Map<String, Long> REQUEST_START_TIMES = new ConcurrentHashMap<>();

      private static long getTime(String lyricsText, String lyricsText2) {
         return Long.parseLong(lyricsText) * 60000L + Math.round(Double.parseDouble(lyricsText2.replace(':', '.')) * 1000.0);
      }

      static SpotifyHUD.Lyrics.Doc getDoc(String docText) {
         SpotifyHUD.Lyrics.Doc doc2 = doc;
         return doc2 != null && doc2.key().equals(docText) ? doc2 : null;
      }

      private static String formatDurationSynced(JsonObject json, int durationSyncedLimit, double durationSyncedValue) {
         JsonElement dursec = json.get("syncedLyrics");
         if (dursec != null && dursec.isJsonPrimitive()) {
            String asString = dursec.getAsString();
            if (asString.isBlank()) {
               return null;
            } else {
               return durationSyncedLimit > 5 && json.has("duration") && json.get("duration").isJsonPrimitive() && Math.abs(json.get("duration").getAsDouble() - durationSyncedLimit) > durationSyncedValue
                  ? null
                  : asString;
            }
         } else {
            return null;
         }
      }

      private static String getSubstring(String substringText) {
         String substring2 = substringText == null ? "" : substringText;

         for (String lowerCase : new String[]{", ", " & ", " feat", " ft.", " x ", " X ", ";"}) {
            int indexOf = substring2.indexOf(lowerCase);
            if (indexOf > 0) {
               substring2 = substring2.substring(0, indexOf);
            }
         }

         return substring2.trim();
      }

      private static SpotifyHUD.Lyrics.Line getLine(long lineValue, List<String> list, List<long[]> list2, String lineText) {
         int spansSize = list.size();
         long[] longs = new long[spansSize];
         long[] longs2 = new long[spansSize];

         for (int i = 0; i < spansSize; i++) {
            longs[i] = ((long[])list2.get(i))[0];
            longs2[i] = ((long[])list2.get(i))[1];
         }

         long lineValue2 = spansSize == 0 ? lineValue : longs2[spansSize - 1];
         return new SpotifyHUD.Lyrics.Line(lineValue, lineValue2, lineText, list.toArray(new String[0]), longs, longs2);
      }

      private static String formatLrcMilliseconds(String lrcMillisecondsText, List<String> list, int lrcMillisecondsValue, String lyricsText, int[] lrcMillisecondsValues)  throws java.lang.InterruptedException {
         int reqSize = list.size();
         var linkedBlockingQueue = new LinkedBlockingQueue();
         var arrayList = new ArrayList(reqSize);
         String[] strings = new String[reqSize];
         boolean[] booleans = new boolean[reqSize];
         int lrcMillisecondsCounter = reqSize;
         long currentTimeMillis2 = 0L;
         long currentTimeMillisOffset = System.currentTimeMillis() + 8000L;

         try {
            for (int i = 0; i < reqSize; i++) {
               int lyricsValue = i;
               var sendAsync = getSendAsync((String)list.get(i));
               arrayList.add(sendAsync);
               sendAsync.whenComplete((res, throwable) -> linkedBlockingQueue.offer(getParse(lyricsValue, (HttpResponse<String>)res, throwable, lrcMillisecondsValue, lyricsText)));
            }

            while (true) {
               int index = -1;
               boolean lrcMillisecondsFlag = true;

               for (int j = 0; j < reqSize && index < 0; j++) {
                  if (!booleans[j]) {
                     lrcMillisecondsFlag = false;
                  } else if (strings[j] != null) {
                     index = j;
                  }
               }

               long currentMillis = System.currentTimeMillis();
               if (index >= 0 && (lrcMillisecondsFlag || currentMillis - currentTimeMillis2 >= 250L)) {
                  return strings[index];
               }

               if (lrcMillisecondsCounter == 0) {
                  return null;
               }

               if (!lrcMillisecondsText.equals(latestRequestKey)) {
                  return null;
               }

               if (currentMillis >= currentTimeMillisOffset) {
                  lrcMillisecondsValues[0] += lrcMillisecondsCounter;
                  return index >= 0 ? strings[index] : null;
               }

               long lrcMillisecondsBound = Math.min(120L, currentTimeMillisOffset - currentMillis);
               if (index >= 0) {
                  lrcMillisecondsBound = Math.min(lrcMillisecondsBound, 250L - (currentMillis - currentTimeMillis2));
               }

               for (SpotifyHUD.Lyrics.Hit hit = (SpotifyHUD.Lyrics.Hit)linkedBlockingQueue.poll(Math.max(1L, lrcMillisecondsBound), TimeUnit.MILLISECONDS);
                  hit != null;
                  hit = (SpotifyHUD.Lyrics.Hit)linkedBlockingQueue.poll()
               ) {
                  if (!booleans[hit.i()]) {
                     booleans[hit.i()] = true;
                     lrcMillisecondsCounter--;
                     if (hit.err()) {
                        lrcMillisecondsValues[0]++;
                     }

                     if (hit.lrc() != null) {
                        strings[hit.i()] = hit.lrc();
                        if (currentTimeMillis2 == 0L) {
                           currentTimeMillis2 = System.currentTimeMillis();
                        }
                     }
                  }
               }
            }
         } finally {
            for (CompletableFuture completableFuture : (Iterable<java.util.concurrent.CompletableFuture>) (arrayList)) {
               completableFuture.cancel(true);
            }
         }
      }

      private static CompletableFuture<HttpResponse<String>> getSendAsync(String asyncText) {
         HttpRequest build = HttpRequest.newBuilder(URI.create(asyncText)).timeout(Duration.ofSeconds(7L)).header("User-Agent", "CorzClient/1.0").GET().build();
         return SpotifyHUD.build.sendAsync(build, BodyHandlers.ofString(StandardCharsets.UTF_8));
      }

      private static void handle(String lyricsText, long lyricsValue, long lyricsValue2, List<String> list, List<long[]> list2) {
         String[] split = lyricsText.trim().split("\\s+");
         int lyricsCounter = 0;
         double lyricsTotal = 0.0;

         for (String lyricsText2 : split) {
            if (!lyricsText2.isEmpty()) {
               lyricsCounter++;
               lyricsTotal += lyricsText2.length() + 2.0;
            }
         }

         if (lyricsCounter != 0) {
            double lyricsTotal2 = 0.0;

            for (String groupParseData : split) {
               if (!groupParseData.isEmpty()) {
                  long lyricsValue3 = lyricsValue + Math.round((lyricsValue2 - lyricsValue) * (lyricsTotal2 / lyricsTotal));
                  lyricsTotal2 += groupParseData.length() + 2.0;
                  long lyricsValue4 = lyricsValue + Math.round((lyricsValue2 - lyricsValue) * (lyricsTotal2 / lyricsTotal));
                  list.add(groupParseData);
                  list2.add(new long[]{lyricsValue3, lyricsValue4});
               }
            }
         }
      }

      static void handleDocLines(String docLinesText, String songText, int docLinesLimit) {
         if (songText != null && !songText.isBlank() && docLinesLimit > 1) {
            String moneyEnoughText = formatRoot(docLinesText, songText);
            String lyricsText = moneyEnoughText + "\u001f" + docLinesLimit;
            long currentMillis = System.currentTimeMillis();
            synchronized (DOC_CACHE) {
               label121: {
                  SpotifyHUD.Lyrics.Doc doc2 = doc;
                  boolean docLinesFlag = doc2 != null && doc2.key().equals(moneyEnoughText);
                  if (docLinesFlag && doc2.lines().length > 0) {
                     latestRequestKey = lyricsText;
                     return;
                  }

                  SpotifyHUD.Lyrics.Doc doc3 = DOC_CACHE.get(lyricsText);
                  Long long2 = REQUEST_START_TIMES.get(lyricsText);
                  boolean docLinesFlag2 = long2 != null && currentMillis - long2 < 60000L;
                  if (!lyricsText.equals(latestRequestKey) || long2 != null && !docLinesFlag2 || timeMillis != 0L && currentMillis - timeMillis >= 20000L) {
                     latestRequestKey = lyricsText;
                     timeMillis = 0L;
                     if (doc3 == null || doc3.lines().length <= 0 && !docLinesFlag2) {
                        if (long2 != null) {
                           REQUEST_START_TIMES.put(lyricsText, currentMillis);
                        }

                        if (doc2 != null && !docLinesFlag) {
                           doc = null;
                        }
                        break label121;
                     }

                     doc = doc3;
                     return;
                  }

                  if (!docLinesFlag && doc3 != null && doc3.lines().length > 0) {
                     doc = doc3;
                  }

                  return;
               }
            }

            threadPool.submit(() -> {
               try {
                  if (!lyricsText.equals(latestRequestKey)) {
                     return;
                  }

                  SpotifyHUD.Lyrics.Line[] var5x = getText(lyricsText, docLinesText == null ? "" : docLinesText, songText, docLinesLimit);
                  if (var5x == null) {
                     return;
                  }

                  SpotifyHUD.Lyrics.Doc botCrystalValue = new SpotifyHUD.Lyrics.Doc(moneyEnoughText, var5x);
                  synchronized (DOC_CACHE) {
                     DOC_CACHE.put(lyricsText, botCrystalValue);
                     if (var5x.length == 0) {
                        REQUEST_START_TIMES.put(lyricsText, System.currentTimeMillis());
                     } else {
                        REQUEST_START_TIMES.remove(lyricsText);
                     }

                     if (lyricsText.equals(latestRequestKey)) {
                        doc = botCrystalValue;
                     }
                  }
               } catch (Throwable e) {
                  synchronized (DOC_CACHE) {
                     if (lyricsText.equals(latestRequestKey)) {
                        timeMillis = System.currentTimeMillis();
                     }
                  }
               }
            });
         }
      }

      private static String getEncode(String encodeText) {
         return URLEncoder.encode(encodeText.trim(), StandardCharsets.UTF_8);
      }

      static String formatRoot(String rootText, String songText) {
         return ((rootText == null ? "" : rootText) + "\u001f" + (songText == null ? "" : songText)).toLowerCase(Locale.ROOT);
      }

      private static String getSubstringForString(String substringText) {
         String replaceAll = substringText.replaceAll("\\s*[(\\[][^)\\]]*(?i:feat|ft\\.|with |remaster|version|edit|live|mono|stereo|explicit)[^)\\]]*[)\\]]", "");
         int indexOf = replaceAll.indexOf(" - ");
         if (indexOf > 0) {
            replaceAll = replaceAll.substring(0, indexOf);
         }

         String trim = replaceAll.trim();
         return trim.isEmpty() ? substringText.trim() : trim;
      }

      private static SpotifyHUD.Lyrics.Hit getParse(int lyricsValue, HttpResponse<String> httpResponse, Throwable throwable, int durationJsonValue, String lyricsText) {
         if (throwable == null && httpResponse != null) {
            if (httpResponse.statusCode() == 404) {
               return new SpotifyHUD.Lyrics.Hit(lyricsValue, null, false);
            } else if (httpResponse.statusCode() != 200) {
               return new SpotifyHUD.Lyrics.Hit(lyricsValue, null, true);
            } else {
               try {
                  return new SpotifyHUD.Lyrics.Hit(lyricsValue, formatDurationJson(JsonParser.parseString((String)httpResponse.body()), durationJsonValue, lyricsText), false);
               } catch (RuntimeException e) {
                  return new SpotifyHUD.Lyrics.Hit(lyricsValue, null, true);
               }
            }
         } else {
            return new SpotifyHUD.Lyrics.Hit(lyricsValue, null, true);
         }
      }

      private static SpotifyHUD.Lyrics.Line getCompile(long compileValue, long compileBound, String compileText) {
         var arrayList = new ArrayList();
         var arrayList2 = new ArrayList();
         Matcher matcher = lyricsPattern.matcher(compileText);
         var arrayList3 = new ArrayList();
         var arrayList4 = new ArrayList();

         while (matcher.find()) {
            arrayList3.add(new int[]{matcher.start(), matcher.end()});
            arrayList4.add(getTime(matcher.group(1), matcher.group(2)));
         }

         if (!arrayList3.isEmpty()) {
            var arrayList5 = new ArrayList();
            var arrayList6 = new ArrayList();
            String trim = compileText.substring(0, ((int[])arrayList3.get(0))[0]).trim();
            if (!trim.isEmpty()) {
               arrayList5.add(compileValue);
               arrayList6.add(trim);
            }

            for (int i = 0; i < arrayList3.size(); i++) {
               arrayList5.add((Long)arrayList4.get(i));
               arrayList6.add(compileText.substring(((int[])arrayList3.get(i))[1], i + 1 < arrayList3.size() ? ((int[])arrayList3.get(i + 1))[0] : compileText.length()));
            }

            long minStart = Math.min(compileBound, (Long)arrayList5.get(arrayList5.size() - 1) + 1200L);

            for (int j = 0; j < arrayList6.size(); j++) {
               long lyricsValue = (Long)arrayList5.get(j);
               long compileValue3 = j + 1 < arrayList5.size() ? (Long)arrayList5.get(j + 1) : Math.max(lyricsValue + 200L, minStart);
               handle((String)arrayList6.get(j), lyricsValue, compileValue3, arrayList, arrayList2);
            }

            String join = String.join(" ", arrayList);
            return getLine(compileValue, arrayList, arrayList2, join);
         } else {
            String lyricsText = compileText.replaceAll("\\s+", " ").trim();
            if (!lyricsText.isEmpty() && !lyricsText.equals("\u266a")) {
               int replaceLength = lyricsText.replace(" ", "").length();
               long etaSeconds = Math.max(250L, compileBound - compileValue - 120L);
               long min4 = Math.min(etaSeconds, Math.max(700L, replaceLength * 75L + 350L));
               handle(lyricsText, compileValue, compileValue + min4, arrayList, arrayList2);
               return getLine(compileValue, arrayList, arrayList2, lyricsText);
            } else {
               return new SpotifyHUD.Lyrics.Line(compileValue, compileValue, "", new String[0], new long[0], new long[0]);
            }
         }
      }

      private static String formatDurationJson(JsonElement json, int durationJsonValue, String durationJsonText) {
         if (json == null) {
            return null;
         } else if (json.isJsonObject()) {
            return formatDurationSynced(json.getAsJsonObject(), durationJsonValue, 3.5);
         } else if (!json.isJsonArray()) {
            return null;
         } else {
            String formatDurationJson = null;
            double durationJsonLimit = Double.MAX_VALUE;

            for (JsonElement jsonElement : json.getAsJsonArray()) {
               if (jsonElement.isJsonObject()) {
                  JsonObject asJsonObject = jsonElement.getAsJsonObject();
                  if (durationJsonText != null && !durationJsonText.isEmpty()) {
                     JsonElement jsonElement2 = asJsonObject.get("artistName");
                     if (jsonElement2 == null || !jsonElement2.isJsonPrimitive() || !jsonElement2.getAsString().toLowerCase(Locale.ROOT).contains(durationJsonText.toLowerCase(Locale.ROOT))) {
                        continue;
                     }
                  }

                  String formatDurationSynced2 = formatDurationSynced(asJsonObject, durationJsonValue, 6.0);
                  if (formatDurationSynced2 != null) {
                     double durationJsonValue2 = asJsonObject.has("duration") && asJsonObject.get("duration").isJsonPrimitive()
                        ? Math.abs(asJsonObject.get("duration").getAsDouble() - durationJsonValue)
                        : 5.0;
                     if (durationJsonValue2 < durationJsonLimit) {
                        durationJsonLimit = durationJsonValue2;
                        formatDurationJson = formatDurationSynced2;
                     }
                  }
               }
            }

            return formatDurationJson;
         }
      }

      static SpotifyHUD.Lyrics.Line[] getCompileBody(String compileBodyText, long compileBodyBound) {
         var arrayList = new ArrayList();

         record Raw(long t, String body) {
         }

         for (String compileCommandText : compileBodyText.split("\\r?\\n")) {
            Matcher matcher = compile.matcher(compileCommandText);
            var arrayList2 = new ArrayList();

            int start;
            for (start = 0; matcher.find() && matcher.start() == start; start = matcher.end()) {
               arrayList2.add(getTime(matcher.group(1), matcher.group(2)));
            }

            if (!arrayList2.isEmpty()) {
               String trim = compileCommandText.substring(start).trim();

               for (long compileBodyValue : (Iterable<Long>) (arrayList2)) {
                  arrayList.add(new Raw(compileBodyValue, trim));
               }
            }
         }

         arrayList.sort(Comparator.comparingLong(Raw::t));
         SpotifyHUD.Lyrics.Line[] lines = new SpotifyHUD.Lyrics.Line[arrayList.size()];

         for (int i = 0; i < arrayList.size(); i++) {
            long compileValue = ((Raw)arrayList.get(i)).t();
            long compileBound = i + 1 < arrayList.size() ? ((Raw)arrayList.get(i + 1)).t() : Math.max(compileValue + 4000L, compileBodyBound);
            lines[i] = getCompile(compileValue, compileBound, ((Raw)arrayList.get(i)).body());
         }

         return lines;
      }

      private static SpotifyHUD.Lyrics.Line[] getText(String lyricsText, String substringText, String songText, int lyricsValue)  throws java.io.IOException, java.lang.InterruptedException {
         String substring = getSubstring(substringText);
         String substringForString = getSubstringForString(songText);
         String totalPriceData = "https://lrclib.net/api/get?artist_name=" + getEncode(substringText) + "&track_name=" + getEncode(songText) + "&duration=" + lyricsValue;
         String lyricsText5 = "https://lrclib.net/api/get?artist_name=" + getEncode(substring) + "&track_name=" + getEncode(substringForString) + "&duration=" + lyricsValue;
         String lyricsText6 = "https://lrclib.net/api/search?track_name=" + getEncode(songText) + "&artist_name=" + getEncode(substring);
         String lyricsText7 = "https://lrclib.net/api/search?track_name=" + getEncode(substringForString) + "&artist_name=" + getEncode(substring);
         var arrayList = new ArrayList(5);
         arrayList.add("https://lrclib.net/api/get-cached?artist_name=" + getEncode(substringText) + "&track_name=" + getEncode(songText) + "&duration=" + lyricsValue);
         arrayList.add(totalPriceData);
         if (!lyricsText5.equals(totalPriceData)) {
            arrayList.add(lyricsText5);
         }

         arrayList.add(lyricsText6);
         if (!lyricsText7.equals(lyricsText6)) {
            arrayList.add(lyricsText7);
         }

         int[] ints = new int[]{0};
         int varSize = arrayList.size();
         String compileBodyText = formatLrcMilliseconds(lyricsText, arrayList, lyricsValue, null, ints);
         String[][] strings = new String[][]{
            {"https://lrclib.net/api/search?q=" + getEncode(substring + " " + substringForString), null}, {"https://lrclib.net/api/search?track_name=" + getEncode(substringForString), substring}
         };

         for (int i = 0; i < strings.length && compileBodyText == null; i++) {
            if (!lyricsText.equals(latestRequestKey)) {
               return null;
            }

            varSize++;
            compileBodyText = formatLrcMilliseconds(lyricsText, List.of(strings[i][0]), lyricsValue, strings[i][1], ints);
         }

         if (compileBodyText == null && !lyricsText.equals(latestRequestKey)) {
            return null;
         } else if (compileBodyText == null && ints[0] > 0) {
            throw new IOException("lrclib: " + ints[0] + " of " + varSize + " lookups failed");
         } else {
            return compileBodyText == null ? new SpotifyHUD.Lyrics.Line[0] : getCompileBody(compileBodyText, lyricsValue * 1000L);
         }
      }

      record Doc(String key, SpotifyHUD.Lyrics.Line[] lines) {
      }

      private record Hit(int i, String lrc, boolean err) {
      }

      record Line(long start, long end, String text, String[] words, long[] ws, long[] we) {
         boolean isWords() {
            return this.words.length == 0;
         }
      }
   }

   private static final class SpotifyDetector {
      private static volatile Process start;
      private static volatile boolean spotifyDetectFlag;
      private static final String ERROR_ACTION_PREFERENCE = "$ErrorActionPreference = 'SilentlyContinue'\n[Console]::OutputEncoding = [System.Text.Encoding]::UTF8\nAdd-Type -AssemblyName System.Runtime.WindowsRuntime\n$asTaskGeneric = ([System.WindowsRuntimeSystemExtensions].GetMethods() | Where-Object { $_.Name -eq 'AsTask' -and $_.GetParameters().Count -eq 1 -and $_.GetParameters()[0].ParameterType.Name -eq 'IAsyncOperation`1' })[0]\nfunction Await($op, $type) { $t = $asTaskGeneric.MakeGenericMethod($type).Invoke($null, @($op)); $t.Wait(-1) | Out-Null; $t.Result }\n$mgrType = [Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager, Windows.Media.Control, ContentType=WindowsRuntime]\n$mgr = Await ($mgrType::RequestAsync()) $mgrType\n$sep = [char]31\nwhile ($true) {\n  try {\n    $sessions = $mgr.GetSessions()\n    $s = $sessions | Where-Object { $_.SourceAppUserModelId -like '*Spotify*' } | Select-Object -First 1\n    if (-not $s) { $s = $mgr.GetCurrentSession() }\n    if ($s) {\n      $m = Await ($s.TryGetMediaPropertiesAsync()) ([Windows.Media.Control.GlobalSystemMediaTransportControlsSessionMediaProperties])\n      $tl = $s.GetTimelineProperties()\n      $pb = $s.GetPlaybackInfo()\n      $posMs = $tl.Position.TotalMilliseconds\n      if ([int]$pb.PlaybackStatus -eq 4) { $age = ([DateTimeOffset]::Now - $tl.LastUpdatedTime).TotalMilliseconds; if ($age -gt 0 -and $age -lt 900000) { $posMs += $age } }\n      Write-Output (\"T\" + $sep + $m.Title + $sep + $m.Artist + $sep + ([int]$tl.Position.TotalSeconds) + $sep + ([int]$tl.EndTime.TotalSeconds) + $sep + ([int]$pb.PlaybackStatus) + $sep + ([long]$posMs))\n    } else { Write-Output \"NONE\" }\n  } catch { Write-Output \"NONE\" }\n  [Console]::Out.Flush()\n  Start-Sleep -Milliseconds 350\n}\n";
      private static volatile long timeMillis;

      static synchronized void updateSpotifyDetect() {
         if (start == null || !start.isAlive()) {
            if (System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win")) {
               long currentTimeMillis = System.currentTimeMillis();
               if (currentTimeMillis - timeMillis >= 5000L) {
                  timeMillis = currentTimeMillis;

                  try {
                     File tempFile = File.createTempFile("corz_spotify", ".ps1");
                     tempFile.deleteOnExit();
                     Files.writeString(
                        tempFile.toPath(),
                        "$ErrorActionPreference = 'SilentlyContinue'\n[Console]::OutputEncoding = [System.Text.Encoding]::UTF8\nAdd-Type -AssemblyName System.Runtime.WindowsRuntime\n$asTaskGeneric = ([System.WindowsRuntimeSystemExtensions].GetMethods() | Where-Object { $_.Name -eq 'AsTask' -and $_.GetParameters().Count -eq 1 -and $_.GetParameters()[0].ParameterType.Name -eq 'IAsyncOperation`1' })[0]\nfunction Await($op, $type) { $t = $asTaskGeneric.MakeGenericMethod($type).Invoke($null, @($op)); $t.Wait(-1) | Out-Null; $t.Result }\n$mgrType = [Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager, Windows.Media.Control, ContentType=WindowsRuntime]\n$mgr = Await ($mgrType::RequestAsync()) $mgrType\n$sep = [char]31\nwhile ($true) {\n  try {\n    $sessions = $mgr.GetSessions()\n    $s = $sessions | Where-Object { $_.SourceAppUserModelId -like '*Spotify*' } | Select-Object -First 1\n    if (-not $s) { $s = $mgr.GetCurrentSession() }\n    if ($s) {\n      $m = Await ($s.TryGetMediaPropertiesAsync()) ([Windows.Media.Control.GlobalSystemMediaTransportControlsSessionMediaProperties])\n      $tl = $s.GetTimelineProperties()\n      $pb = $s.GetPlaybackInfo()\n      $posMs = $tl.Position.TotalMilliseconds\n      if ([int]$pb.PlaybackStatus -eq 4) { $age = ([DateTimeOffset]::Now - $tl.LastUpdatedTime).TotalMilliseconds; if ($age -gt 0 -and $age -lt 900000) { $posMs += $age } }\n      Write-Output (\"T\" + $sep + $m.Title + $sep + $m.Artist + $sep + ([int]$tl.Position.TotalSeconds) + $sep + ([int]$tl.EndTime.TotalSeconds) + $sep + ([int]$pb.PlaybackStatus) + $sep + ([long]$posMs))\n    } else { Write-Output \"NONE\" }\n  } catch { Write-Output \"NONE\" }\n  [Console]::Out.Flush()\n  Start-Sleep -Milliseconds 350\n}\n"
                     );
                     Process process = new ProcessBuilder(
                           "powershell", "-NoProfile", "-ExecutionPolicy", "Bypass", "-WindowStyle", "Hidden", "-File", tempFile.getAbsolutePath()
                        )
                        .start();
                     start = process;
                     Thread thread = new Thread(() -> handleNoneUtf(process), "corz-spotify-detect");
                     thread.setDaemon(true);
                     thread.start();
                     if (!spotifyDetectFlag) {
                        spotifyDetectFlag = true;
                        Runtime.getRuntime().addShutdownHook(new Thread(SpotifyHUD.SpotifyDetector::updateStart));
                     }
                  } catch (Exception e) {
                  }
               }
            }
         }
      }

      static synchronized void updateStart() {
         Process start2 = start;
         start = null;
         if (start2 != null) {
            start2.destroyForcibly();
         }
      }

      private static void handleNoneUtf(Process process) {
         String noneUtfText = null;
         String referenceAtomicText2 = null;
         String noneUtfText3 = null;
         String moneyEnoughText = null;
         int noneUtfValue = 0;
         int noneUtfCounter = 0;

         String readLine5;
         try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            while ((readLine5 = bufferedReader.readLine()) != null) {
               if (!readLine5.isEmpty() && !readLine5.startsWith("NONE")) {
                  String[] split = readLine5.split("\u001f", -1);
                  if (split.length >= 6 && "T".equals(split[0])) {
                     String songText = split[1];
                     String referenceAtomicText = split[2];
                     int maxParseInt = Math.max(1, getParseInt(split[4]));
                     boolean noneUtfFlag = getParseInt(split[5]) == 4;
                     long noneUtfLimit = split.length >= 7 ? getTime(split[6], -1L) : -1L;
                     SpotifyHUD.handleReferenceAtomic(referenceAtomicText, songText);
                     if (maxParseInt <= 1) {
                        if (++noneUtfCounter < 4) {
                           SpotifyHUD.handleTrackSong(songText, referenceAtomicText, noneUtfFlag);
                           continue;
                        }
                     } else {
                        noneUtfCounter = 0;
                     }

                     long noneUtfValue2 = noneUtfLimit >= 0L ? noneUtfLimit : getParseInt(split[3]) * 1000L;
                     boolean noneUtfFlag2 = !songText.equals(noneUtfText) || !referenceAtomicText.equals(referenceAtomicText2);
                     if (!noneUtfFlag2 || maxParseInt <= 1 || maxParseInt != noneUtfValue || noneUtfValue2 <= 3000L || songText.equals(noneUtfText3) && referenceAtomicText.equals(moneyEnoughText)) {
                        noneUtfText3 = null;
                        moneyEnoughText = null;
                        if (noneUtfLimit >= 0L) {
                           SpotifyHUD.handleTrackSong(songText, referenceAtomicText, noneUtfLimit, maxParseInt, noneUtfFlag, 900L);
                        } else {
                           SpotifyHUD.handle(songText, referenceAtomicText, getParseInt(split[3]), maxParseInt, noneUtfFlag);
                        }

                        if (maxParseInt > 1) {
                           noneUtfText = songText;
                           referenceAtomicText2 = referenceAtomicText;
                           noneUtfValue = maxParseInt;
                           SpotifyHUD.Lyrics.handleDocLines(referenceAtomicText, songText, maxParseInt);
                        }
                     } else {
                        noneUtfText3 = songText;
                        moneyEnoughText = referenceAtomicText;
                        SpotifyHUD.handleTrackSong(songText, referenceAtomicText, noneUtfFlag);
                     }
                  }
               }
            }
         } catch (Exception e) {
         }
      }

      private static long getTime(String spotifyDetectorText, long spotifyDetectorValue) {
         try {
            return Long.parseLong(spotifyDetectorText.trim());
         } catch (Exception e) {
            return spotifyDetectorValue;
         }
      }

      private static int getParseInt(String intText) {
         try {
            return Integer.parseInt(intText.trim());
         } catch (Exception e) {
            return 0;
         }
      }
   }

   private record Track(String title, String artist, String lyricKey, int durationSec, long baseElapsedMs, long stateMs, boolean playing) {
      long getMin(long now) {
         long baseElapsedMs = this.baseElapsedMs;
         if (this.playing) {
            baseElapsedMs += now - this.stateMs;
         }

         return baseElapsedMs < 0L ? 0L : Math.min(baseElapsedMs, this.durationSec * 1000L);
      }
   }

   private record Wrap(int[] rowOf, float[] xOf, float[] rowW, int rows) {
   }
}
