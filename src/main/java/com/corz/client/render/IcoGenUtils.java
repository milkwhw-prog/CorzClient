package com.corz.client.render;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

public final class IcoGenUtils {
   private static final Identifier chevronIcoIdentifier = Identifier.of("corzclient", "gen/chevron");
   private static boolean mnextIcoFlag;
   private static final Identifier icoCheckIdentifier = Identifier.of("corzclient", "gen/check");
   private static boolean icoCorzFlag;
   private static final Identifier crossIcoIdentifier = Identifier.of("corzclient", "gen/cross");
   private static boolean icoResetFlag;
   private static boolean gaugeIcoFlag;
   private static boolean mplayIcoFlag;
   private static final Identifier gaugeIcoIdentifier = Identifier.of("corzclient", "gen/gauge");
   private static boolean noteIcoFlag;
   private static final Identifier signalIcoIdentifier = Identifier.of("corzclient", "gen/signal");
   private static final Identifier icoSpeedIdentifier = Identifier.of("corzclient", "gen/speed");
   private static boolean icoCubeFlag;
   private static final Identifier pinIcoIdentifier = Identifier.of("corzclient", "gen/pin");
   private static final Identifier navIcoIdentifier = Identifier.of("corzclient", "gen/nav");
   private static final Identifier cloudIcoIdentifier = Identifier.of("corzclient", "gen/cloud");
   private static boolean chevrondnIcoFlag;
   private static boolean icoShieldFlag;
   private static final Identifier icoCorzIdentifier = Identifier.of("corzclient", "gen/server");
   private static final Identifier mprevIcoIdentifier = Identifier.of("corzclient", "gen/mprev");
   private static final Identifier mnextIcoIdentifier = Identifier.of("corzclient", "gen/mnext");
   private static final Identifier mplayIcoIdentifier = Identifier.of("corzclient", "gen/mplay");
   private static final Identifier mpauseIcoIdentifier = Identifier.of("corzclient", "gen/mpause");
   private static final Identifier noteIcoIdentifier = Identifier.of("corzclient", "gen/note");
   private static boolean mpauseIcoFlag;
   private static final Identifier icoResetIdentifier = Identifier.of("corzclient", "gen/reset");
   private static boolean navIcoFlag;
   private static boolean icoClockFlag;
   private static boolean cloudIcoFlag;
   private static final Identifier identifier = Identifier.of("corzclient", "gen/shield");
   private static boolean signalIcoFlag;
   private static final Identifier icoCubeIdentifier = Identifier.of("corzclient", "gen/cube");
   private static final Identifier chevrondnIcoIdentifier = Identifier.of("corzclient", "gen/chevrondn");
   private static boolean icoCheckFlag;
   private static boolean mprevIcoFlag;
   private static final Identifier starIcoIdentifier = Identifier.of("corzclient", "gen/star");
   private static boolean icoClockActive;
   private static final Identifier icoClockIdentifier = Identifier.of("corzclient", "gen/clock");
   private static boolean starIcoFlag;
   private static boolean chevronIcoFlag;
   private static final Identifier heartIcoIdentifier = Identifier.of("corzclient", "gen/heart");
   private static final Identifier icoGenIdentifier = Identifier.of("corzclient", "gen/clockhud");
   private static final Map<String, Identifier> GEN_CORZCLIENT_MAP = new HashMap<>();
   private static boolean heartIcoFlag;
   private static final Map<String, Identifier> GENERATED_IDENTIFIER_CACHE = new HashMap<>();
   private static boolean icoSpeedFlag;
   private static final Map<String, Identifier> CORZCLIENT_GEN_MAP = new HashMap<>();
   private static final Map<String, Identifier> ICON_IDENTIFIER_CACHE = new HashMap<>();
   private static boolean crossIcoFlag;
   private static boolean pinIcoFlag;

   private static boolean canStrokeTexture(Identifier id2, String strokeTextureText, IcoGenUtils.Paint paint, float strokeTextureLimit) {
      try {
         BufferedImage bufferedImage = new BufferedImage(64, 64, 2);
         Graphics2D graphics = bufferedImage.createGraphics();
         graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
         graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
         graphics.setColor(Color.WHITE);
         if (strokeTextureLimit > 0.0F) {
            graphics.setStroke(new BasicStroke(strokeTextureLimit, 1, 1));
         }

         paint.paint(graphics);
         graphics.dispose();
         NativeImage nativeImage = new NativeImage(64, 64, false);
         int[] rgb = bufferedImage.getRGB(0, 0, 64, 64, null, 0, 64);

         for (int i = 0; i < rgb.length; i++) {
            nativeImage.setColorArgb(i % 64, i / 64, rgb[i]);
         }

         NativeImageBackedTexture nativeImageBackedTexture = new NativeImageBackedTexture(() -> strokeTextureText, nativeImage);
         MinecraftClient.getInstance().getTextureManager().registerTexture(id2, nativeImageBackedTexture);
         return true;
      } catch (Throwable e) {
         return false;
      }
   }

   private static double getAmount(String icoGenText, int[] icoGenValues) {
      handle(icoGenText, icoGenValues);
      int posLength = icoGenText.length();
      int index = icoGenValues[0];
      int index2 = index;
      if (index < posLength && (icoGenText.charAt(index) == '-' || icoGenText.charAt(index) == '+')) {
         index2 = index + 1;
      }

      boolean icoGenFlag = false;
      boolean icoGenFlag2 = false;

      while (index2 < posLength) {
         char charAt = icoGenText.charAt(index2);
         if (charAt >= '0' && charAt <= '9') {
            icoGenFlag2 = true;
            index2++;
         } else {
            if (charAt != '.' || icoGenFlag) {
               break;
            }

            icoGenFlag = true;
            index2++;
         }
      }

      if (icoGenFlag2 && index2 < posLength && (icoGenText.charAt(index2) == 'e' || icoGenText.charAt(index2) == 'E')) {
         int i = index2 + 1;
         if (i < posLength && (icoGenText.charAt(i) == '-' || icoGenText.charAt(i) == '+')) {
            i++;
         }

         if (i < posLength && Character.isDigit(icoGenText.charAt(i))) {
            index2 = i;

            while (index2 < posLength && Character.isDigit(icoGenText.charAt(index2))) {
               index2++;
            }
         }
      }

      if (!icoGenFlag2) {
         throw new IllegalArgumentException("svg: number expected at " + index);
      } else {
         icoGenValues[0] = index2;
         return Double.parseDouble(icoGenText.substring(index, index2));
      }
   }

   private static boolean canFlag(Identifier id2, String icoGenText, IcoGenUtils.Paint paint) {
      return canStrokeTexture(id2, icoGenText, paint, 7.36F);
   }

   public static Identifier getIdentifier(String identifierText) {
      Identifier corzclient_gen_map = ICON_IDENTIFIER_CACHE.get(identifierText);
      if (corzclient_gen_map != null) {
         return corzclient_gen_map;
      } else {
         Identifier identifier = Identifier.of("corzclient", "gen/lucide_" + identifierText.replace('-', '_'));

         String[] texts = switch (identifierText) {
            case "backpack" -> new String[]{
               "M4 10a4 4 0 0 1 4-4h8a4 4 0 0 1 4 4v10a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2z",
               "M8 10h8",
               "M8 18h8",
               "M8 22v-6a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v6",
               "M9 6V4a2 2 0 0 1 2-2h2a2 2 0 0 1 2 2v2"
            };
            case "map-pin" -> new String[]{
               "M20 10c0 4.993-5.539 10.193-7.399 11.799a1 1 0 0 1-1.202 0C9.539 20.193 4 14.993 4 10a8 8 0 0 1 16 0", "M15 10a3 3 0 1 1-6 0a3 3 0 1 1 6 0"
            };
            case "layout-grid" -> new String[]{
               "M4 3h5a1 1 0 0 1 1 1v5a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1V4a1 1 0 0 1 1-1z",
               "M15 3h5a1 1 0 0 1 1 1v5a1 1 0 0 1-1 1h-5a1 1 0 0 1-1-1V4a1 1 0 0 1 1-1z",
               "M15 14h5a1 1 0 0 1 1 1v5a1 1 0 0 1-1 1h-5a1 1 0 0 1-1-1v-5a1 1 0 0 1 1-1z",
               "M4 14h5a1 1 0 0 1 1 1v5a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1v-5a1 1 0 0 1 1-1z"
            };
            case "x" -> new String[]{"M18 6 6 18", "m6 6 12 12"};
            case "crosshair" -> new String[]{"M22 12a10 10 0 1 1-20 0a10 10 0 1 1 20 0", "M22 12h-4", "M6 12H2", "M12 6V2", "M12 22v-4"};
            case "rotate-cw" -> new String[]{"M21 12a9 9 0 1 1-9-9c2.52 0 4.93 1 6.74 2.74L21 8", "M21 3v5h-5"};
            case "flip-horizontal-2" -> new String[]{"m3 7 5 5-5 5V7", "m21 7-5 5 5 5V7", "M12 20v2", "M12 14v2", "M12 8v2", "M12 2v2"};
            case "package" -> new String[]{
               "M11 21.73a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73z",
               "M12 22V12",
               "m3.3 7 7.703 4.734a2 2 0 0 0 1.994 0L20.7 7",
               "m7.5 4.27 9 5.15"
            };
            case "chevron-down" -> new String[]{"m6 9 6 6 6-6"};
            case "chevron-up" -> new String[]{"m18 15-6-6-6 6"};
            case "minus" -> new String[]{"M5 12h14"};
            case "plus" -> new String[]{"M5 12h14", "M12 5v14"};
            case "chevron-left" -> new String[]{"m15 18-6-6 6-6"};
            case "chevron-right" -> new String[]{"m9 18 6-6-6-6"};
            case "rotate-ccw" -> new String[]{"M3 12a9 9 0 1 0 9-9 9.75 9.75 0 0 0-6.74 2.74L3 8", "M3 3v5h5"};
            case "layers" -> new String[]{
               "m12.83 2.18a2 2 0 0 0-1.66 0L2.6 6.08a1 1 0 0 0 0 1.83l8.58 3.91a2 2 0 0 0 1.66 0l8.58-3.9a1 1 0 0 0 0-1.83Z",
               "m22 17.65-9.17 4.16a2 2 0 0 1-1.66 0L2 17.65",
               "m22 12.65-9.17 4.16a2 2 0 0 1-1.66 0L2 12.65"
            };
            case "shopping-cart" -> new String[]{
               "M9 21a1 1 0 1 1-2 0a1 1 0 1 1 2 0",
               "M20 21a1 1 0 1 1-2 0a1 1 0 1 1 2 0",
               "M2.05 2.05h2l2.66 12.42a2 2 0 0 0 2 1.58h9.78a2 2 0 0 0 1.95-1.57l1.65-7.43H5.12"
            };
            case "swords" -> new String[]{
               "M14.5 17.5 3 6V3h3l11.5 11.5", "M13 19l6-6", "M16 16l4 4", "M19 21l2-2", "M14.5 6.5 18 3h3v3l-3.5 3.5", "M5 14l4 4", "M7 17l-3 3", "M3 19l2 2"
            };
            case "bot" -> new String[]{
               "M12 8V4H8", "M6 8h12a2 2 0 0 1 2 2v8a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2v-8a2 2 0 0 1 2-2z", "M2 14h2", "M20 14h2", "M15 13v2", "M9 13v2"
            };
            default -> null;
         };
         if (texts != null) {
            canStrokeTexture(identifier, "corz_ico_lucide_" + identifierText, 96, 24.0F, 2.0F, paths -> {
               for (String var5x : texts) {
                  paths.draw(getGeneralPath(var5x));
               }
            });
         }

         ICON_IDENTIFIER_CACHE.put(identifierText, identifier);
         return identifier;
      }
   }

   public static Identifier getChevrondnIco() {
      if (!chevrondnIcoFlag) {
         chevrondnIcoFlag = canFlagForIdentifierString(chevrondnIcoIdentifier, "corz_ico_chevrondn", graphics2D -> {
            GeneralPath generalPath = new GeneralPath();
            generalPath.moveTo(17.92, 25.6);
            generalPath.lineTo(32.0, 39.68);
            generalPath.lineTo(46.08, 25.6);
            graphics2D.draw(generalPath);
         });
      }

      return chevrondnIcoIdentifier;
   }

   public static Identifier getNavIco() {
      if (!navIcoFlag) {
         navIcoFlag = canFlag4(navIcoIdentifier, "corz_ico_nav", graphics2D -> {
            GeneralPath generalPath = new GeneralPath();
            generalPath.moveTo(32.0, 11.52);
            generalPath.lineTo(47.36, 51.2);
            generalPath.lineTo(32.0, 40.96);
            generalPath.lineTo(16.64, 51.2);
            generalPath.closePath();
            graphics2D.fill(generalPath);
         });
      }

      return navIcoIdentifier;
   }

   private static void handle(String icoGenText, int[] icoGenValues) {
      while (icoGenValues[0] < icoGenText.length() && (Character.isWhitespace(icoGenText.charAt(icoGenValues[0])) || icoGenText.charAt(icoGenValues[0]) == ',')) {
         icoGenValues[0]++;
      }
   }

   private static boolean canFlagForIdentifierString(Identifier id2, String icoGenText, IcoGenUtils.Paint paint) {
      return canStrokeTexture(id2, icoGenText, paint, 4.608F);
   }

   private static boolean canFlagForIdentifierStringPaint(Identifier id2, String icoGenText, IcoGenUtils.Paint paint) {
      return canStrokeTexture(id2, icoGenText, 48, 16.0F, 1.5F, paint);
   }

   public static Identifier getNoteIco() {
      if (!noteIcoFlag) {
         noteIcoFlag = canFlag4(noteIcoIdentifier, "corz_ico_note", graphics2D -> {
            graphics2D.fill(new java.awt.geom.Ellipse2D.Double(17.92, 35.84, 14.08, 10.88));
            graphics2D.fill(new java.awt.geom.RoundRectangle2D.Double(30.08, 15.36, 3.2, 26.88, 1.28, 1.28));
            GeneralPath generalPath = new GeneralPath();
            generalPath.moveTo(33.28, 15.36);
            generalPath.lineTo(43.52, 19.84);
            generalPath.lineTo(43.52, 28.16);
            generalPath.lineTo(33.28, 23.68);
            generalPath.closePath();
            graphics2D.fill(generalPath);
         });
      }

      return noteIcoIdentifier;
   }

   public static Identifier getIdentifierForString(String identifierText) {
      Identifier corzclient_gen_map = CORZCLIENT_GEN_MAP.get(identifierText);
      if (corzclient_gen_map != null) {
         return corzclient_gen_map;
      } else {
         Identifier identifier = Identifier.of("corzclient", "gen/line_" + identifierText);
         String icoGenText = "corz_ico_line_" + identifierText;
         switch (identifierText) {
            case "clock":
               canFlagForIdentifierStringPaint(identifier, icoGenText, graphics2D -> {
                  render(graphics2D, 7.5, 7.5, 5.0);
                  GeneralPath var1x = new GeneralPath();
                  var1x.moveTo(7.5, 4.5);
                  var1x.lineTo(7.5, 7.5);
                  var1x.lineTo(9.5, 9.0);
                  graphics2D.draw(var1x);
               });
               break;
            case "timer":
               canFlagForIdentifierStringPaint(identifier, icoGenText, graphics2D2 -> {
                  render(graphics2D2, 7.5, 8.5, 4.0);
                  render(graphics2D2, 6.5, 2.5, 8.5, 2.5);
                  render(graphics2D2, 7.5, 8.5, 7.5, 6.5);
               });
               break;
            case "monitor":
               canFlagForIdentifierStringPaint(identifier, icoGenText, graphics2D3 -> {
                  render(graphics2D3, 2.5, 2.5, 10.0, 7.0, 1.5);
                  render(graphics2D3, 7.5, 9.5, 7.5, 12.5);
                  render(graphics2D3, 5.5, 12.5, 9.5, 12.5);
               });
               break;
            case "signal":
               canFlagForIdentifierStringPaint(identifier, icoGenText, graphics2D4 -> {
                  render(graphics2D4, 3.5, 12.5, 3.5, 10.5);
                  render(graphics2D4, 7.5, 12.5, 7.5, 6.5);
                  render(graphics2D4, 11.5, 12.5, 11.5, 2.5);
               });
               break;
            case "server":
               canFlagForIdentifierStringPaint(identifier, icoGenText, graphics2D5 -> {
                  render(graphics2D5, 2.5, 2.5, 11.0, 4.0, 1.25);
                  render(graphics2D5, 2.5, 9.5, 11.0, 4.0, 1.25);
                  renderForGraphics2DDouble(graphics2D5, 5.5, 4.5, 1.5);
                  renderForGraphics2DDouble(graphics2D5, 5.5, 11.5, 1.5);
               });
               break;
            case "keyboard":
               canFlagForIdentifierStringPaint(identifier, icoGenText, graphics2D6 -> {
                  render(graphics2D6, 2.5, 4.5, 10.0, 6.0, 1.5);
                  renderForGraphics2DDouble(graphics2D6, 5.5, 6.5, 1.5);
                  renderForGraphics2DDouble(graphics2D6, 7.5, 6.5, 1.5);
                  renderForGraphics2DDouble(graphics2D6, 9.5, 6.5, 1.5);
                  render(graphics2D6, 5.5, 8.5, 9.5, 8.5);
               });
               break;
            case "flask":
               canFlagForIdentifierStringPaint(identifier, icoGenText, graphics2D7 -> {
                  render(graphics2D7, 5.5, 2.5, 10.5, 2.5);
                  GeneralPath var1x = new GeneralPath();
                  var1x.moveTo(6.5, 2.5);
                  var1x.lineTo(6.5, 6.0);
                  var1x.lineTo(3.3, 11.0);
                  var1x.quadTo(2.4, 12.5, 4.2, 12.5);
                  var1x.lineTo(11.8, 12.5);
                  var1x.quadTo(13.6, 12.5, 12.7, 11.0);
                  var1x.lineTo(9.5, 6.0);
                  var1x.lineTo(9.5, 2.5);
                  graphics2D7.draw(var1x);
                  render(graphics2D7, 5.5, 9.5, 10.5, 9.5);
               });
               break;
            case "pin":
               canFlagForIdentifierStringPaint(identifier, icoGenText, graphics2D8 -> {
                  GeneralPath var1x = new GeneralPath();
                  var1x.moveTo(7.5, 12.6);
                  var1x.curveTo(5.5, 10.9, 3.5, 8.9, 3.5, 6.5);
                  var1x.curveTo(3.5, 4.29, 5.29, 2.5, 7.5, 2.5);
                  var1x.curveTo(9.71, 2.5, 11.5, 4.29, 11.5, 6.5);
                  var1x.curveTo(11.5, 8.9, 9.5, 10.9, 7.5, 12.6);
                  var1x.closePath();
                  graphics2D8.draw(var1x);
                  renderForGraphics2DDouble(graphics2D8, 7.5, 6.5, 2.5);
               });
               break;
            case "shield":
               canFlagForIdentifierStringPaint(identifier, icoGenText, graphics2D9 -> {
                  GeneralPath var1x = new GeneralPath();
                  var1x.moveTo(7.5, 2.5);
                  var1x.lineTo(11.5, 4.0);
                  var1x.lineTo(11.5, 7.5);
                  var1x.curveTo(11.5, 10.2, 9.8, 11.8, 7.5, 12.6);
                  var1x.curveTo(5.2, 11.8, 3.5, 10.2, 3.5, 7.5);
                  var1x.lineTo(3.5, 4.0);
                  var1x.closePath();
                  graphics2D9.draw(var1x);
               });
               break;
            case "target":
               canFlagForIdentifierStringPaint(identifier, icoGenText, graphics2D10 -> {
                  render(graphics2D10, 7.5, 7.5, 5.0);
                  renderForGraphics2DDouble(graphics2D10, 7.5, 7.5, 3.0);
               });
               break;
            case "person":
               canFlagForIdentifierStringPaint(identifier, icoGenText, graphics2D11 -> {
                  render(graphics2D11, 7.5, 4.5, 2.0);
                  GeneralPath var1x = new GeneralPath();
                  var1x.moveTo(3.5, 12.5);
                  var1x.lineTo(3.5, 12.0);
                  var1x.quadTo(3.5, 9.5, 6.0, 9.5);
                  var1x.lineTo(9.0, 9.5);
                  var1x.quadTo(11.5, 9.5, 11.5, 12.0);
                  var1x.lineTo(11.5, 12.5);
                  graphics2D11.draw(var1x);
               });
               break;
            case "cube":
               canFlagForIdentifierStringPaint(identifier, icoGenText, graphics2D12 -> {
                  GeneralPath var1x = new GeneralPath();
                  var1x.moveTo(7.5, 2.5);
                  var1x.lineTo(11.5, 4.75);
                  var1x.lineTo(11.5, 10.25);
                  var1x.lineTo(7.5, 12.5);
                  var1x.lineTo(3.5, 10.25);
                  var1x.lineTo(3.5, 4.75);
                  var1x.closePath();
                  graphics2D12.draw(var1x);
                  GeneralPath var2x = new GeneralPath();
                  var2x.moveTo(3.5, 4.75);
                  var2x.lineTo(7.5, 7.0);
                  var2x.lineTo(11.5, 4.75);
                  graphics2D12.draw(var2x);
                  render(graphics2D12, 7.5, 7.0, 7.5, 12.5);
               });
               break;
            case "check":
               canFlagForIdentifierStringPaint(identifier, icoGenText, graphics2D13 -> {
                  GeneralPath var1x = new GeneralPath();
                  var1x.moveTo(3.5, 7.5);
                  var1x.lineTo(6.5, 10.5);
                  var1x.lineTo(12.5, 4.5);
                  graphics2D13.draw(var1x);
               });
               break;
            case "cross":
               canFlagForIdentifierStringPaint(identifier, icoGenText, graphics2D14 -> {
                  render(graphics2D14, 4.5, 4.5, 11.5, 11.5);
                  render(graphics2D14, 11.5, 4.5, 4.5, 11.5);
               });
               break;
            case "play":
               canFlagForIdentifierStringPaint(identifier, icoGenText, graphics2D15 -> {
                  GeneralPath var1x = new GeneralPath();
                  var1x.moveTo(6.0, 4.0);
                  var1x.lineTo(12.0, 8.0);
                  var1x.lineTo(6.0, 12.0);
                  var1x.closePath();
                  graphics2D15.draw(var1x);
               });
               break;
            case "broadcast":
               canFlagForIdentifierStringPaint(identifier, icoGenText, graphics2D16 -> {
                  renderForGraphics2DDouble(graphics2D16, 8.0, 8.0, 2.5);
                  graphics2D16.draw(new java.awt.geom.Arc2D.Double(4.75, 4.75, 6.5, 6.5, -45.0, 90.0, 0));
                  graphics2D16.draw(new java.awt.geom.Arc2D.Double(4.75, 4.75, 6.5, 6.5, 135.0, 90.0, 0));
                  graphics2D16.draw(new java.awt.geom.Arc2D.Double(2.0, 2.0, 12.0, 12.0, -45.0, 90.0, 0));
                  graphics2D16.draw(new java.awt.geom.Arc2D.Double(2.0, 2.0, 12.0, 12.0, 135.0, 90.0, 0));
               });
               break;
            case "wing":
               canFlagForIdentifierStringPaint(identifier, icoGenText, graphics2D17 -> {
                  GeneralPath var1x = new GeneralPath();
                  var1x.moveTo(6.6, 3.0);
                  var1x.curveTo(5.2, 2.5, 3.6, 2.9, 2.9, 4.8);
                  var1x.curveTo(2.3, 7.4, 2.4, 10.6, 3.2, 13.2);
                  var1x.quadTo(4.6, 11.4, 6.3, 10.2);
                  var1x.curveTo(6.2, 8.2, 6.6, 5.6, 6.6, 3.0);
                  var1x.closePath();
                  graphics2D17.draw(var1x);
                  graphics2D17.draw(new AffineTransform(-1.0F, 0.0F, 0.0F, 1.0F, 16.0F, 0.0F).createTransformedShape(var1x));
               });
         }

         CORZCLIENT_GEN_MAP.put(identifierText, identifier);
         return identifier;
      }
   }

   private static void render(Graphics2D graphics2D, double icoGenValue, double icoGenValue2, double icoGenValue3, double icoGenValue4, double icoGenValue5) {
      graphics2D.draw(new java.awt.geom.RoundRectangle2D.Double(icoGenValue, icoGenValue2, icoGenValue3, icoGenValue4, icoGenValue5 * 2.0, icoGenValue5 * 2.0));
   }

   public static Identifier getGaugeIco() {
      if (!gaugeIcoFlag) {
         gaugeIcoFlag = canFlag(gaugeIcoIdentifier, "corz_ico_gauge", graphics2D -> {
            GeneralPath generalPath = new GeneralPath();
            generalPath.moveTo(10.24, 33.28);
            generalPath.lineTo(23.04, 33.28);
            generalPath.lineTo(29.44, 19.2);
            generalPath.lineTo(37.12, 46.08);
            generalPath.lineTo(42.24, 33.28);
            generalPath.lineTo(55.04, 33.28);
            graphics2D.draw(generalPath);
         });
      }

      return gaugeIcoIdentifier;
   }

   static GeneralPath getGeneralPath(String generalPathText) {
      GeneralPath generalPath = new GeneralPath();
      int[] ints = new int[]{0};
      int generalPathValue = generalPathText.length();
      double icoGenValue = 0.0;
      double generalPathValue3 = 0.0;
      double icoGenValue4 = 0.0;
      double generalPathValue5 = 0.0;
      double generalPathValue6 = 0.0;
      double generalPathValue7 = 0.0;
      char ch = 0;
      char upperCase2 = 0;

      while (true) {
         handle(generalPathText, ints);
         if (ints[0] >= generalPathValue) {
            return generalPath;
         }

         char charAt = generalPathText.charAt(ints[0]);
         if (Character.isLetter(charAt)) {
            ch = charAt;
            ints[0]++;
         } else if (ch == 'M') {
            ch = 'L';
         } else if (ch == 'm') {
            ch = 'l';
         } else if (ch == 0 || ch == 'Z' || ch == 'z') {
            throw new IllegalArgumentException("svg: stray number at " + ints[0]);
         }

         boolean lowerCase = Character.isLowerCase(ch);
         double generalPathValue8 = lowerCase ? icoGenValue : 0.0;
         double generalPathValue9 = lowerCase ? generalPathValue3 : 0.0;
         char upperCase = Character.toUpperCase(ch);
         switch (upperCase) {
            case 'A':
               double icoGenFactor = getAmount(generalPathText, ints);
               double generalPathValue11 = getAmount(generalPathText, ints);
               double degrees = getAmount(generalPathText, ints);
               boolean generalPathFlag = canFlag(generalPathText, ints);
               boolean canFlag2 = canFlag(generalPathText, ints);
               double generalPathValue13 = generalPathValue8 + getAmount(generalPathText, ints);
               double generalPathValue14 = generalPathValue9 + getAmount(generalPathText, ints);
               handle(generalPath, icoGenValue, generalPathValue3, icoGenFactor, generalPathValue11, degrees, generalPathFlag, canFlag2, generalPathValue13, generalPathValue14);
               icoGenValue = generalPathValue13;
               generalPathValue3 = generalPathValue14;
               break;
            case 'B':
            case 'D':
            case 'E':
            case 'F':
            case 'G':
            case 'I':
            case 'J':
            case 'K':
            case 'N':
            case 'O':
            case 'P':
            case 'R':
            case 'U':
            case 'W':
            case 'X':
            case 'Y':
            default:
               throw new IllegalArgumentException("svg: unknown command " + ch);
            case 'C':
               double generalPathValue15 = generalPathValue8 + getAmount(generalPathText, ints);
               double generalPathValue16 = generalPathValue9 + getAmount(generalPathText, ints);
               double generalPathValue17 = generalPathValue8 + getAmount(generalPathText, ints);
               double generalPathValue18 = generalPathValue9 + getAmount(generalPathText, ints);
               icoGenValue = generalPathValue8 + getAmount(generalPathText, ints);
               generalPathValue3 = generalPathValue9 + getAmount(generalPathText, ints);
               generalPath.curveTo(generalPathValue15, generalPathValue16, generalPathValue17, generalPathValue18, icoGenValue, generalPathValue3);
               generalPathValue6 = generalPathValue17;
               generalPathValue7 = generalPathValue18;
               break;
            case 'H':
               icoGenValue = generalPathValue8 + getAmount(generalPathText, ints);
               generalPath.lineTo(icoGenValue, generalPathValue3);
               break;
            case 'L':
               icoGenValue = generalPathValue8 + getAmount(generalPathText, ints);
               generalPathValue3 = generalPathValue9 + getAmount(generalPathText, ints);
               generalPath.lineTo(icoGenValue, generalPathValue3);
               break;
            case 'M':
               icoGenValue = generalPathValue8 + getAmount(generalPathText, ints);
               generalPathValue3 = generalPathValue9 + getAmount(generalPathText, ints);
               generalPath.moveTo(icoGenValue, generalPathValue3);
               icoGenValue4 = icoGenValue;
               generalPathValue5 = generalPathValue3;
               break;
            case 'Q':
               double generalPathValue19 = generalPathValue8 + getAmount(generalPathText, ints);
               double generalPathValue20 = generalPathValue9 + getAmount(generalPathText, ints);
               icoGenValue = generalPathValue8 + getAmount(generalPathText, ints);
               generalPathValue3 = generalPathValue9 + getAmount(generalPathText, ints);
               generalPath.quadTo(generalPathValue19, generalPathValue20, icoGenValue, generalPathValue3);
               generalPathValue6 = generalPathValue19;
               generalPathValue7 = generalPathValue20;
               break;
            case 'S':
               boolean generalPathFlag3 = upperCase2 == 'C' || upperCase2 == 'S';
               double generalPathValue21 = generalPathFlag3 ? 2.0 * icoGenValue - generalPathValue6 : icoGenValue;
               double generalPathValue22 = generalPathFlag3 ? 2.0 * generalPathValue3 - generalPathValue7 : generalPathValue3;
               double generalPathValue23 = generalPathValue8 + getAmount(generalPathText, ints);
               double generalPathValue24 = generalPathValue9 + getAmount(generalPathText, ints);
               icoGenValue = generalPathValue8 + getAmount(generalPathText, ints);
               generalPathValue3 = generalPathValue9 + getAmount(generalPathText, ints);
               generalPath.curveTo(generalPathValue21, generalPathValue22, generalPathValue23, generalPathValue24, icoGenValue, generalPathValue3);
               generalPathValue6 = generalPathValue23;
               generalPathValue7 = generalPathValue24;
               break;
            case 'T':
               boolean generalPathFlag4 = upperCase2 == 'Q' || upperCase2 == 'T';
               double generalPathValue25 = generalPathFlag4 ? 2.0 * icoGenValue - generalPathValue6 : icoGenValue;
               double generalPathValue26 = generalPathFlag4 ? 2.0 * generalPathValue3 - generalPathValue7 : generalPathValue3;
               icoGenValue = generalPathValue8 + getAmount(generalPathText, ints);
               generalPathValue3 = generalPathValue9 + getAmount(generalPathText, ints);
               generalPath.quadTo(generalPathValue25, generalPathValue26, icoGenValue, generalPathValue3);
               generalPathValue6 = generalPathValue25;
               generalPathValue7 = generalPathValue26;
               break;
            case 'V':
               generalPathValue3 = generalPathValue9 + getAmount(generalPathText, ints);
               generalPath.lineTo(icoGenValue, generalPathValue3);
               break;
            case 'Z':
               generalPath.closePath();
               icoGenValue = icoGenValue4;
               generalPathValue3 = generalPathValue5;
         }

         upperCase2 = upperCase;
      }
   }

   private IcoGenUtils() {
   }

   public static Identifier getIcoShield() {
      if (!icoShieldFlag) {
         icoShieldFlag = canFlagForIdentifierString(identifier, "corz_ico_shield", graphics2D -> {
            GeneralPath generalPath = new GeneralPath();
            generalPath.moveTo(32.0, 10.24);
            generalPath.lineTo(51.2, 17.28);
            generalPath.curveTo(51.2, 38.4, 46.08, 48.64, 32.0, 55.04);
            generalPath.curveTo(17.92, 48.64, 12.8, 38.4, 12.8, 17.28);
            generalPath.closePath();
            graphics2D.draw(generalPath);
         });
      }

      return identifier;
   }

   public static Identifier getIcoCorz() {
      if (!icoCorzFlag) {
         icoCorzFlag = canFlag4(icoCorzIdentifier, "corz_ico_server", graphics2D -> {
            double icoCorzValue = 12.8;
            double icoCorzValue2 = 38.4;
            double icoCorzValue3 = 9.92;
            double icoCorzValue4 = 3.52;
            graphics2D.fill(new java.awt.geom.RoundRectangle2D.Double(icoCorzValue, 15.36, icoCorzValue2, icoCorzValue3, icoCorzValue4, icoCorzValue4));
            graphics2D.fill(new java.awt.geom.RoundRectangle2D.Double(icoCorzValue, 27.04, icoCorzValue2, icoCorzValue3, icoCorzValue4, icoCorzValue4));
            graphics2D.fill(new java.awt.geom.RoundRectangle2D.Double(icoCorzValue, 38.72, icoCorzValue2, icoCorzValue3, icoCorzValue4, icoCorzValue4));
         });
      }

      return icoCorzIdentifier;
   }

   private static boolean canStrokeTexture(Identifier id2, String strokeTextureText, IcoGenUtils.Paint paint, float strokeTextureLimit, int strokeTextureFactor) {
      try {
         BufferedImage bufferedImage = new BufferedImage(64, 64, 2);
         Graphics2D graphics = bufferedImage.createGraphics();
         graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
         graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
         graphics.setColor(Color.WHITE);
         if (strokeTextureLimit > 0.0F) {
            graphics.setStroke(new BasicStroke(strokeTextureLimit, 1, 1));
         }

         paint.paint(graphics);
         graphics.dispose();
         int strokeTextureLimit2 = 64 / strokeTextureFactor;
         int[] rgb = bufferedImage.getRGB(0, 0, 64, 64, null, 0, 64);
         NativeImage nativeImage = new NativeImage(strokeTextureLimit2, strokeTextureLimit2, false);

         for (int i = 0; i < strokeTextureLimit2; i++) {
            for (int j = 0; j < strokeTextureLimit2; j++) {
               int strokeTextureTotal = 0;

               for (int k = 0; k < strokeTextureFactor; k++) {
                  for (int m = 0; m < strokeTextureFactor; m++) {
                     strokeTextureTotal += rgb[(i * strokeTextureFactor + k) * 64 + j * strokeTextureFactor + m] >>> 24 & 0xFF;
                  }
               }

               nativeImage.setColorArgb(j, i, strokeTextureTotal / (strokeTextureFactor * strokeTextureFactor) << 24 | 16777215);
            }
         }

         NativeImageBackedTexture nativeImageBackedTexture = new NativeImageBackedTexture(() -> strokeTextureText, nativeImage);
         MinecraftClient.getInstance().getTextureManager().registerTexture(id2, nativeImageBackedTexture);
         return true;
      } catch (Throwable e) {
         return false;
      }
   }

   private static boolean canFlag(String icoGenText, int[] icoGenValues) {
      handle(icoGenText, icoGenValues);
      char ch = icoGenValues[0] < icoGenText.length() ? icoGenText.charAt(icoGenValues[0]) : 32;
      if (ch != '0' && ch != '1') {
         throw new IllegalArgumentException("svg: arc flag expected at " + icoGenValues[0]);
      } else {
         icoGenValues[0]++;
         return ch == '1';
      }
   }

   private static GeneralPath getGeneralPath(double generalPathValue, double generalPathValue2, double generalPathValue3) {
      double generalPathValue4 = generalPathValue3 * 0.28;
      GeneralPath generalPath = new GeneralPath();

      for (int i = 0; i < 8; i++) {
         double angle = (-Math.PI / 2) + i * Math.PI / 4.0;
         double generalPathFactor = i % 2 == 0 ? generalPathValue3 : generalPathValue4;
         double icoGenValue = generalPathValue + Math.cos(angle) * generalPathFactor;
         double generalPathValue6 = generalPathValue2 + Math.sin(angle) * generalPathFactor;
         if (i == 0) {
            generalPath.moveTo(icoGenValue, generalPathValue6);
         } else {
            generalPath.lineTo(icoGenValue, generalPathValue6);
         }
      }

      generalPath.closePath();
      return generalPath;
   }

   public static Identifier getIcoClock() {
      if (!icoClockFlag) {
         icoClockFlag = canStrokeTexture(icoGenIdentifier, "corz_ico_clock_hud", graphics2D -> {
            graphics2D.draw(new java.awt.geom.Ellipse2D.Double(10.88, 10.88, 42.24, 42.24));
            graphics2D.draw(new java.awt.geom.Line2D.Double(32.0, 32.0, 32.0, 20.48));
            graphics2D.draw(new java.awt.geom.Line2D.Double(32.0, 32.0, 44.8, 32.0));
         }, 7.36F, 2);
      }

      return icoGenIdentifier;
   }

   public static Identifier getCloudIco() {
      if (!cloudIcoFlag) {
         cloudIcoFlag = canFlag4(cloudIcoIdentifier, "corz_ico_cloud", graphics2D -> {
            Area area = new Area();
            area.add(new Area(new java.awt.geom.Ellipse2D.Double(8.96, 26.88, 21.76, 21.76)));
            area.add(new Area(new java.awt.geom.Ellipse2D.Double(21.12, 17.92, 25.6, 25.6)));
            area.add(new Area(new java.awt.geom.Ellipse2D.Double(33.28, 26.88, 21.76, 21.76)));
            area.add(new Area(new java.awt.geom.RoundRectangle2D.Double(11.52, 35.84, 40.96, 12.8, 12.8, 12.8)));
            graphics2D.fill(area);
         });
      }

      return cloudIcoIdentifier;
   }

   public static Identifier getIcoReset() {
      if (!icoResetFlag) {
         icoResetFlag = canFlagForIdentifierString(icoResetIdentifier, "corz_ico_reset", graphics2D -> {
            double icoResetValue = 32.0;
            double icoResetValue2 = 32.0;
            double icoResetFactor = 16.0;
            graphics2D.draw(new java.awt.geom.Arc2D.Double(icoResetValue - icoResetFactor, icoResetValue2 - icoResetFactor, icoResetFactor * 2.0, icoResetFactor * 2.0, 75.0, 285.0, 0));
            double angle = Math.toRadians(75.0);
            double icoResetValue3 = icoResetValue + icoResetFactor * Math.cos(angle);
            double icoResetValue4 = icoResetValue2 - icoResetFactor * Math.sin(angle);
            graphics2D.draw(new java.awt.geom.Line2D.Double(icoResetValue3, icoResetValue4, icoResetValue3 - 7.68, icoResetValue4 + 1.92));
            graphics2D.draw(new java.awt.geom.Line2D.Double(icoResetValue3, icoResetValue4, icoResetValue3 + 1.28, icoResetValue4 + 8.32));
         });
      }

      return icoResetIdentifier;
   }

   private static void render(Graphics2D graphics2D, double icoGenValue, double icoGenValue2, double icoGenValue3) {
      graphics2D.draw(new java.awt.geom.Ellipse2D.Double(icoGenValue - icoGenValue3, icoGenValue2 - icoGenValue3, icoGenValue3 * 2.0, icoGenValue3 * 2.0));
   }

   public static Identifier getMnextIco() {
      if (!mnextIcoFlag) {
         mnextIcoFlag = canFlag4(mnextIcoIdentifier, "corz_ico_mnext", graphics2D -> {
            GeneralPath generalPath = new GeneralPath();
            generalPath.moveTo(16.64, 17.92);
            generalPath.lineTo(38.4, 32.0);
            generalPath.lineTo(16.64, 46.08);
            generalPath.closePath();
            graphics2D.fill(generalPath);
            graphics2D.fill(new java.awt.geom.RoundRectangle2D.Double(43.2, 19.2, 5.44, 25.6, 3.2, 3.2));
         });
      }

      return mnextIcoIdentifier;
   }

   private static void handle(GeneralPath generalPath, double icoGenValue, double icoGenValue2, double icoGenFactor, double abs2, double degrees, boolean icoGenFlag, boolean icoGenFlag2, double icoGenValue3, double icoGenValue4) {
      if (icoGenValue != icoGenValue3 || icoGenValue2 != icoGenValue4) {
         icoGenFactor = Math.abs(icoGenFactor);
         abs2 = Math.abs(abs2);
         if (icoGenFactor != 0.0 && abs2 != 0.0) {
            double angle = Math.toRadians(degrees);
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            double icoGenFactor3 = (icoGenValue - icoGenValue3) / 2.0;
            double icoGenFactor4 = (icoGenValue2 - icoGenValue4) / 2.0;
            double icoGenFactor5 = cos * icoGenFactor3 + sin * icoGenFactor4;
            double icoGenFactor6 = -sin * icoGenFactor3 + cos * icoGenFactor4;
            double icoGenLimit = icoGenFactor5 * icoGenFactor5 / (icoGenFactor * icoGenFactor) + icoGenFactor6 * icoGenFactor6 / (abs2 * abs2);
            if (icoGenLimit > 1.0) {
               double sqrt = Math.sqrt(icoGenLimit);
               icoGenFactor *= sqrt;
               abs2 *= sqrt;
            }

            double icoGenFactor7 = icoGenFactor * icoGenFactor;
            double icoGenFactor8 = abs2 * abs2;
            double icoGenDivisor = icoGenFactor7 * icoGenFactor6 * icoGenFactor6 + icoGenFactor8 * icoGenFactor5 * icoGenFactor5;
            double icoGenFactor9 = (icoGenFlag == icoGenFlag2 ? -1 : 1) * Math.sqrt(Math.max(0.0, (icoGenFactor7 * icoGenFactor8 - icoGenDivisor) / icoGenDivisor));
            double icoGenFactor10 = icoGenFactor9 * icoGenFactor * icoGenFactor6 / abs2;
            double icoGenFactor11 = -icoGenFactor9 * abs2 * icoGenFactor5 / icoGenFactor;
            double icoGenValue5 = cos * icoGenFactor10 - sin * icoGenFactor11 + (icoGenValue + icoGenValue3) / 2.0;
            double icoGenValue6 = sin * icoGenFactor10 + cos * icoGenFactor11 + (icoGenValue2 + icoGenValue4) / 2.0;
            double x2 = (icoGenFactor5 - icoGenFactor10) / icoGenFactor;
            double y2 = (icoGenFactor6 - icoGenFactor11) / abs2;
            double icoGenFactor12 = (-icoGenFactor5 - icoGenFactor10) / icoGenFactor;
            double icoGenFactor13 = (-icoGenFactor6 - icoGenFactor11) / abs2;
            double atan2 = Math.atan2(y2, x2);
            double spotValue = Math.atan2(x2 * icoGenFactor13 - y2 * icoGenFactor12, x2 * icoGenFactor12 + y2 * icoGenFactor13);
            if (!icoGenFlag2 && spotValue > 0.0) {
               spotValue -= Math.PI * 2;
            } else if (icoGenFlag2 && spotValue < 0.0) {
               spotValue += Math.PI * 2;
            }

            int max2 = Math.max(1, (int)Math.ceil(Math.abs(spotValue) / (Math.PI / 2) - 1.0E-9));
            double icoGenValue7 = spotValue / max2;
            double icoGenFactor14 = 1.3333333333333333 * Math.tan(icoGenValue7 / 4.0);

            for (int i = 0; i < max2; i++) {
               double cos2 = Math.cos(atan2);
               double sin2 = Math.sin(atan2);
               double cos3 = Math.cos(atan2 + icoGenValue7);
               double sin3 = Math.sin(atan2 + icoGenValue7);
               double icoGenFactor15 = cos2 - icoGenFactor14 * sin2;
               double icoGenFactor16 = sin2 + icoGenFactor14 * cos2;
               double icoGenFactor17 = cos3 + icoGenFactor14 * sin3;
               double icoGenFactor18 = sin3 - icoGenFactor14 * cos3;
               generalPath.curveTo(
                  icoGenValue5 + icoGenFactor * icoGenFactor15 * cos - abs2 * icoGenFactor16 * sin,
                  icoGenValue6 + icoGenFactor * icoGenFactor15 * sin + abs2 * icoGenFactor16 * cos,
                  icoGenValue5 + icoGenFactor * icoGenFactor17 * cos - abs2 * icoGenFactor18 * sin,
                  icoGenValue6 + icoGenFactor * icoGenFactor17 * sin + abs2 * icoGenFactor18 * cos,
                  icoGenValue5 + icoGenFactor * cos3 * cos - abs2 * sin3 * sin,
                  icoGenValue6 + icoGenFactor * cos3 * sin + abs2 * sin3 * cos
               );
               atan2 += icoGenValue7;
            }
         } else {
            generalPath.lineTo(icoGenValue3, icoGenValue4);
         }
      }
   }

   public static Identifier getIcoClockIcoClockActive() {
      if (!icoClockActive) {
         icoClockActive = canFlag(icoClockIdentifier, "corz_ico_clock", graphics2D -> {
            graphics2D.draw(new java.awt.geom.Ellipse2D.Double(10.88, 10.88, 42.24, 42.24));
            graphics2D.draw(new java.awt.geom.Line2D.Double(32.0, 32.0, 32.0, 20.48));
            graphics2D.draw(new java.awt.geom.Line2D.Double(32.0, 32.0, 44.8, 32.0));
         });
      }

      return icoClockIdentifier;
   }

   public static Identifier getSignalIco() {
      if (!signalIcoFlag) {
         signalIcoFlag = canFlag(signalIcoIdentifier, "corz_ico_signal", graphics2D -> {
            double signalIcoValue = 46.08;
            graphics2D.draw(new java.awt.geom.Line2D.Double(17.92, signalIcoValue, 17.92, 38.4));
            graphics2D.draw(new java.awt.geom.Line2D.Double(32.0, signalIcoValue, 32.0, 28.16));
            graphics2D.draw(new java.awt.geom.Line2D.Double(46.08, signalIcoValue, 46.08, 17.92));
         });
      }

      return signalIcoIdentifier;
   }

   public static Identifier getPinIco() {
      if (!pinIcoFlag) {
         pinIcoFlag = canFlag4(pinIcoIdentifier, "corz_ico_pin", graphics2D -> {
            GeneralPath generalPath = new GeneralPath();
            generalPath.moveTo(32.0, 52.48);
            generalPath.curveTo(32.0, 52.48, 15.36, 33.28, 15.36, 24.32);
            generalPath.curveTo(15.36, 12.16, 48.64, 12.16, 48.64, 24.32);
            generalPath.curveTo(48.64, 33.28, 32.0, 52.48, 32.0, 52.48);
            generalPath.closePath();
            graphics2D.fill(generalPath);
         });
      }

      return pinIcoIdentifier;
   }

   public static Identifier getIcoSpeed() {
      if (!icoSpeedFlag) {
         icoSpeedFlag = canFlag(icoSpeedIdentifier, "corz_ico_speed", graphics2D -> {
            GeneralPath generalPath = new GeneralPath();
            generalPath.moveTo(21.76, 17.92);
            generalPath.lineTo(33.28, 32.0);
            generalPath.lineTo(21.76, 46.08);
            GeneralPath generalPath2 = new GeneralPath();
            generalPath2.moveTo(34.56, 17.92);
            generalPath2.lineTo(46.08, 32.0);
            generalPath2.lineTo(34.56, 46.08);
            graphics2D.draw(generalPath);
            graphics2D.draw(generalPath2);
         });
      }

      return icoSpeedIdentifier;
   }

   public static Identifier getChevronIco() {
      if (!chevronIcoFlag) {
         chevronIcoFlag = canFlag(chevronIcoIdentifier, "corz_ico_chevron", graphics2D -> {
            GeneralPath generalPath = new GeneralPath();
            generalPath.moveTo(23.04, 14.08);
            generalPath.lineTo(42.24, 32.0);
            generalPath.lineTo(23.04, 49.92);
            graphics2D.draw(generalPath);
         });
      }

      return chevronIcoIdentifier;
   }

   private static GeneralPath getGeneralPath(double generalPathValue, double generalPathValue2, double generalPathValue3, double generalPathValue4) {
      GeneralPath generalPath = new GeneralPath();

      for (int i = 0; i < 10; i++) {
         double angle = (-Math.PI / 2) + i * Math.PI / 5.0;
         double generalPathFactor = i % 2 == 0 ? generalPathValue3 : generalPathValue4;
         double icoGenValue = generalPathValue + Math.cos(angle) * generalPathFactor;
         double generalPathValue6 = generalPathValue2 + Math.sin(angle) * generalPathFactor;
         if (i == 0) {
            generalPath.moveTo(icoGenValue, generalPathValue6);
         } else {
            generalPath.lineTo(icoGenValue, generalPathValue6);
         }
      }

      generalPath.closePath();
      return generalPath;
   }

   public static Identifier getIcoCheck() {
      if (!icoCheckFlag) {
         icoCheckFlag = canFlag(icoCheckIdentifier, "corz_ico_check", graphics2D -> {
            GeneralPath generalPath = new GeneralPath();
            generalPath.moveTo(15.36, 32.64);
            generalPath.lineTo(26.88, 44.8);
            generalPath.lineTo(48.64, 19.2);
            graphics2D.draw(generalPath);
         });
      }

      return icoCheckIdentifier;
   }

   public static Identifier getMpauseIco() {
      if (!mpauseIcoFlag) {
         mpauseIcoFlag = canFlag4(mpauseIcoIdentifier, "corz_ico_mpause", graphics2D -> {
            double mpauseIcoValue = 9.6;
            double mpauseIcoValue2 = 17.92;
            double mpauseIcoValue3 = 28.16;
            double mpauseIcoValue4 = 3.2;
            graphics2D.fill(new java.awt.geom.RoundRectangle2D.Double(21.12, mpauseIcoValue2, mpauseIcoValue, mpauseIcoValue3, mpauseIcoValue4, mpauseIcoValue4));
            graphics2D.fill(new java.awt.geom.RoundRectangle2D.Double(33.28, mpauseIcoValue2, mpauseIcoValue, mpauseIcoValue3, mpauseIcoValue4, mpauseIcoValue4));
         });
      }

      return mpauseIcoIdentifier;
   }

   public static Identifier getStarIco() {
      if (!starIcoFlag) {
         starIcoFlag = canFlag4(starIcoIdentifier, "corz_ico_star", graphics2D -> graphics2D.fill(getGeneralPath(32.0, 33.28, 26.88, 11.2)));
      }

      return starIcoIdentifier;
   }

   private static void render(Graphics2D graphics2D, double icoGenValue, double icoGenValue2, double icoGenValue3, double icoGenValue4) {
      graphics2D.draw(new java.awt.geom.Line2D.Double(icoGenValue, icoGenValue2, icoGenValue3, icoGenValue4));
   }

   public static Identifier getCrossIco() {
      if (!crossIcoFlag) {
         crossIcoFlag = canFlag(crossIcoIdentifier, "corz_ico_cross", graphics2D -> {
            GeneralPath generalPath = new GeneralPath();
            generalPath.moveTo(17.28, 17.28);
            generalPath.lineTo(46.72, 46.72);
            GeneralPath generalPath2 = new GeneralPath();
            generalPath2.moveTo(46.72, 17.28);
            generalPath2.lineTo(17.28, 46.72);
            graphics2D.draw(generalPath);
            graphics2D.draw(generalPath2);
         });
      }

      return crossIcoIdentifier;
   }

   public static Identifier getHeartIco() {
      if (!heartIcoFlag) {
         heartIcoFlag = canFlag4(heartIcoIdentifier, "corz_ico_heart", graphics2D -> {
            double heartIcoValue = 11.84;
            graphics2D.fill(new java.awt.geom.Ellipse2D.Double(21.44 - heartIcoValue, 24.0 - heartIcoValue, heartIcoValue * 2.0, heartIcoValue * 2.0));
            graphics2D.fill(new java.awt.geom.Ellipse2D.Double(42.56 - heartIcoValue, 24.0 - heartIcoValue, heartIcoValue * 2.0, heartIcoValue * 2.0));
            GeneralPath generalPath = new GeneralPath();
            generalPath.moveTo(10.24, 28.16);
            generalPath.lineTo(53.76, 28.16);
            generalPath.lineTo(32.0, 53.76);
            generalPath.closePath();
            graphics2D.fill(generalPath);
         });
      }

      return heartIcoIdentifier;
   }

   public static Identifier getMplayIco() {
      if (!mplayIcoFlag) {
         mplayIcoFlag = canFlag4(mplayIcoIdentifier, "corz_ico_mplay", graphics2D -> {
            GeneralPath generalPath = new GeneralPath();
            generalPath.moveTo(21.76, 16.64);
            generalPath.lineTo(47.36, 32.0);
            generalPath.lineTo(21.76, 47.36);
            generalPath.closePath();
            graphics2D.fill(generalPath);
         });
      }

      return mplayIcoIdentifier;
   }

   private static void renderForGraphics2DDouble(Graphics2D graphics2D, double icoGenValue, double icoGenValue2, double icoGenValue3) {
      graphics2D.fill(new java.awt.geom.Ellipse2D.Double(icoGenValue - icoGenValue3 / 2.0, icoGenValue2 - icoGenValue3 / 2.0, icoGenValue3, icoGenValue3));
   }

   public static Identifier getIdentifierGEN_CORZCLIENT_MAP(String identifierText) {
      Identifier gen_corzclient_map = GEN_CORZCLIENT_MAP.get(identifierText);
      if (gen_corzclient_map != null) {
         return gen_corzclient_map;
      } else {
         Identifier identifier = Identifier.of("corzclient", "gen/ui_" + identifierText);
         String icoGenText = "corz_ico_ui_" + identifierText;
         double identifierValue = 64.0;

         boolean identifierFlag = switch (identifierText) {
            case "plus" -> canFlagForIdentifierString(identifier, icoGenText, graphics2D -> {
               graphics2D.draw(new java.awt.geom.Line2D.Double(identifierValue * 0.5, identifierValue * 0.22, identifierValue * 0.5, identifierValue * 0.78));
               graphics2D.draw(new java.awt.geom.Line2D.Double(identifierValue * 0.22, identifierValue * 0.5, identifierValue * 0.78, identifierValue * 0.5));
            });
            case "trash" -> canFlagForIdentifierString(identifier, icoGenText, graphics2D2 -> {
               graphics2D2.draw(new java.awt.geom.Line2D.Double(identifierValue * 0.2, identifierValue * 0.28, identifierValue * 0.8, identifierValue * 0.28));
               GeneralPath var3x = new GeneralPath();
               var3x.moveTo(identifierValue * 0.4, identifierValue * 0.28);
               var3x.lineTo(identifierValue * 0.4, identifierValue * 0.18);
               var3x.lineTo(identifierValue * 0.6, identifierValue * 0.18);
               var3x.lineTo(identifierValue * 0.6, identifierValue * 0.28);
               graphics2D2.draw(var3x);
               GeneralPath var4x = new GeneralPath();
               var4x.moveTo(identifierValue * 0.27, identifierValue * 0.28);
               var4x.lineTo(identifierValue * 0.31, identifierValue * 0.82);
               var4x.lineTo(identifierValue * 0.69, identifierValue * 0.82);
               var4x.lineTo(identifierValue * 0.73, identifierValue * 0.28);
               graphics2D2.draw(var4x);
               graphics2D2.draw(new java.awt.geom.Line2D.Double(identifierValue * 0.43, identifierValue * 0.42, identifierValue * 0.43, identifierValue * 0.68));
               graphics2D2.draw(new java.awt.geom.Line2D.Double(identifierValue * 0.57, identifierValue * 0.42, identifierValue * 0.57, identifierValue * 0.68));
            });
            case "pencil" -> canFlagForIdentifierString(identifier, icoGenText, graphics2D3 -> {
               GeneralPath var3x = new GeneralPath();
               var3x.moveTo(identifierValue * 0.22, identifierValue * 0.78);
               var3x.lineTo(identifierValue * 0.26, identifierValue * 0.62);
               var3x.lineTo(identifierValue * 0.62, identifierValue * 0.26);
               var3x.lineTo(identifierValue * 0.74, identifierValue * 0.38);
               var3x.lineTo(identifierValue * 0.38, identifierValue * 0.74);
               var3x.closePath();
               graphics2D3.draw(var3x);
               graphics2D3.draw(new java.awt.geom.Line2D.Double(identifierValue * 0.53, identifierValue * 0.35, identifierValue * 0.65, identifierValue * 0.47));
            });
            case "download" -> canFlagForIdentifierString(identifier, icoGenText, graphics2D4 -> {
               graphics2D4.draw(new java.awt.geom.Line2D.Double(identifierValue * 0.5, identifierValue * 0.18, identifierValue * 0.5, identifierValue * 0.6));
               GeneralPath var3x = new GeneralPath();
               var3x.moveTo(identifierValue * 0.32, identifierValue * 0.44);
               var3x.lineTo(identifierValue * 0.5, identifierValue * 0.62);
               var3x.lineTo(identifierValue * 0.68, identifierValue * 0.44);
               graphics2D4.draw(var3x);
               GeneralPath var4x = new GeneralPath();
               var4x.moveTo(identifierValue * 0.2, identifierValue * 0.66);
               var4x.lineTo(identifierValue * 0.2, identifierValue * 0.8);
               var4x.lineTo(identifierValue * 0.8, identifierValue * 0.8);
               var4x.lineTo(identifierValue * 0.8, identifierValue * 0.66);
               graphics2D4.draw(var4x);
            });
            case "upload" -> canFlagForIdentifierString(identifier, icoGenText, graphics2D5 -> {
               graphics2D5.draw(new java.awt.geom.Line2D.Double(identifierValue * 0.5, identifierValue * 0.62, identifierValue * 0.5, identifierValue * 0.2));
               GeneralPath var3x = new GeneralPath();
               var3x.moveTo(identifierValue * 0.32, identifierValue * 0.38);
               var3x.lineTo(identifierValue * 0.5, identifierValue * 0.2);
               var3x.lineTo(identifierValue * 0.68, identifierValue * 0.38);
               graphics2D5.draw(var3x);
               GeneralPath var4x = new GeneralPath();
               var4x.moveTo(identifierValue * 0.2, identifierValue * 0.66);
               var4x.lineTo(identifierValue * 0.2, identifierValue * 0.8);
               var4x.lineTo(identifierValue * 0.8, identifierValue * 0.8);
               var4x.lineTo(identifierValue * 0.8, identifierValue * 0.66);
               graphics2D5.draw(var4x);
            });
            case "share" -> canFlagForIdentifierString(identifier, icoGenText, graphics2D6 -> {
               double var3x = identifierValue * 0.16;
               graphics2D6.draw(new java.awt.geom.Ellipse2D.Double(identifierValue * 0.72 - var3x / 2.0, identifierValue * 0.24 - var3x / 2.0, var3x, var3x));
               graphics2D6.draw(new java.awt.geom.Ellipse2D.Double(identifierValue * 0.28 - var3x / 2.0, identifierValue * 0.5 - var3x / 2.0, var3x, var3x));
               graphics2D6.draw(new java.awt.geom.Ellipse2D.Double(identifierValue * 0.72 - var3x / 2.0, identifierValue * 0.76 - var3x / 2.0, var3x, var3x));
               graphics2D6.draw(new java.awt.geom.Line2D.Double(identifierValue * 0.36, identifierValue * 0.45, identifierValue * 0.64, identifierValue * 0.29));
               graphics2D6.draw(new java.awt.geom.Line2D.Double(identifierValue * 0.36, identifierValue * 0.55, identifierValue * 0.64, identifierValue * 0.71));
            });
            case "key" -> canFlagForIdentifierString(identifier, icoGenText, graphics2D7 -> {
               graphics2D7.draw(new java.awt.geom.Ellipse2D.Double(identifierValue * 0.2, identifierValue * 0.36, identifierValue * 0.28, identifierValue * 0.28));
               graphics2D7.draw(new java.awt.geom.Line2D.Double(identifierValue * 0.48, identifierValue * 0.5, identifierValue * 0.82, identifierValue * 0.5));
               graphics2D7.draw(new java.awt.geom.Line2D.Double(identifierValue * 0.68, identifierValue * 0.5, identifierValue * 0.68, identifierValue * 0.62));
               graphics2D7.draw(new java.awt.geom.Line2D.Double(identifierValue * 0.8, identifierValue * 0.5, identifierValue * 0.8, identifierValue * 0.6));
            });
            case "user" -> canFlagForIdentifierString(identifier, icoGenText, graphics2D8 -> {
               graphics2D8.draw(new java.awt.geom.Ellipse2D.Double(identifierValue * 0.36, identifierValue * 0.18, identifierValue * 0.28, identifierValue * 0.28));
               graphics2D8.draw(new java.awt.geom.Arc2D.Double(identifierValue * 0.22, identifierValue * 0.6, identifierValue * 0.56, identifierValue * 0.46, 0.0, 180.0, 0));
            });
            case "globe" -> canFlagForIdentifierString(identifier, icoGenText, graphics2D9 -> {
               graphics2D9.draw(new java.awt.geom.Ellipse2D.Double(identifierValue * 0.2, identifierValue * 0.2, identifierValue * 0.6, identifierValue * 0.6));
               graphics2D9.draw(new java.awt.geom.Ellipse2D.Double(identifierValue * 0.37, identifierValue * 0.2, identifierValue * 0.26, identifierValue * 0.6));
               graphics2D9.draw(new java.awt.geom.Line2D.Double(identifierValue * 0.2, identifierValue * 0.5, identifierValue * 0.8, identifierValue * 0.5));
            });
            case "folder" -> canFlagForIdentifierString(identifier, icoGenText, graphics2D10 -> {
               GeneralPath var3x = new GeneralPath();
               var3x.moveTo(identifierValue * 0.18, identifierValue * 0.3);
               var3x.lineTo(identifierValue * 0.42, identifierValue * 0.3);
               var3x.lineTo(identifierValue * 0.5, identifierValue * 0.38);
               var3x.lineTo(identifierValue * 0.82, identifierValue * 0.38);
               var3x.lineTo(identifierValue * 0.82, identifierValue * 0.76);
               var3x.lineTo(identifierValue * 0.18, identifierValue * 0.76);
               var3x.closePath();
               graphics2D10.draw(var3x);
            });
            case "folderfill" -> canFlag4(identifier, icoGenText, graphics2D11 -> {
               Area var3x = new Area(new java.awt.geom.RoundRectangle2D.Double(identifierValue * 0.14, identifierValue * 0.2, identifierValue * 0.36, identifierValue * 0.3, identifierValue * 0.12, identifierValue * 0.12));
               var3x.add(new Area(new java.awt.geom.RoundRectangle2D.Double(identifierValue * 0.14, identifierValue * 0.32, identifierValue * 0.72, identifierValue * 0.48, identifierValue * 0.14, identifierValue * 0.14)));
               graphics2D11.fill(var3x);
            });
            case "save" -> canFlagForIdentifierString(identifier, icoGenText, graphics2D12 -> {
               GeneralPath var3x = new GeneralPath();
               var3x.moveTo(identifierValue * 0.2, identifierValue * 0.2);
               var3x.lineTo(identifierValue * 0.68, identifierValue * 0.2);
               var3x.lineTo(identifierValue * 0.8, identifierValue * 0.32);
               var3x.lineTo(identifierValue * 0.8, identifierValue * 0.8);
               var3x.lineTo(identifierValue * 0.2, identifierValue * 0.8);
               var3x.closePath();
               graphics2D12.draw(var3x);
               graphics2D12.draw(new java.awt.geom.Rectangle2D.Double(identifierValue * 0.34, identifierValue * 0.54, identifierValue * 0.32, identifierValue * 0.26));
               graphics2D12.draw(new java.awt.geom.Line2D.Double(identifierValue * 0.34, identifierValue * 0.2, identifierValue * 0.34, identifierValue * 0.36));
               graphics2D12.draw(new java.awt.geom.Line2D.Double(identifierValue * 0.34, identifierValue * 0.36, identifierValue * 0.6, identifierValue * 0.36));
               graphics2D12.draw(new java.awt.geom.Line2D.Double(identifierValue * 0.6, identifierValue * 0.36, identifierValue * 0.6, identifierValue * 0.2));
            });
            case "ticket" -> canFlagForIdentifierString(identifier, icoGenText, graphics2D13 -> {
               graphics2D13.draw(new java.awt.geom.RoundRectangle2D.Double(identifierValue * 0.16, identifierValue * 0.3, identifierValue * 0.68, identifierValue * 0.4, identifierValue * 0.1, identifierValue * 0.1));
               graphics2D13.draw(new java.awt.geom.Line2D.Double(identifierValue * 0.6, identifierValue * 0.36, identifierValue * 0.6, identifierValue * 0.44));
               graphics2D13.draw(new java.awt.geom.Line2D.Double(identifierValue * 0.6, identifierValue * 0.56, identifierValue * 0.6, identifierValue * 0.64));
            });
            case "search" -> canFlagForIdentifierString(identifier, icoGenText, graphics2D14 -> {
               graphics2D14.draw(new java.awt.geom.Ellipse2D.Double(identifierValue * 0.2, identifierValue * 0.2, identifierValue * 0.42, identifierValue * 0.42));
               graphics2D14.draw(new java.awt.geom.Line2D.Double(identifierValue * 0.57, identifierValue * 0.57, identifierValue * 0.8, identifierValue * 0.8));
            });
            case "close" -> canFlagForIdentifierString(identifier, icoGenText, graphics2D15 -> {
               graphics2D15.draw(new java.awt.geom.Line2D.Double(identifierValue * 0.28, identifierValue * 0.28, identifierValue * 0.72, identifierValue * 0.72));
               graphics2D15.draw(new java.awt.geom.Line2D.Double(identifierValue * 0.72, identifierValue * 0.28, identifierValue * 0.28, identifierValue * 0.72));
            });
            case "dots" -> canFlag4(identifier, icoGenText, graphics2D16 -> {
               double var3x = identifierValue * 0.13;
               graphics2D16.fill(new java.awt.geom.Ellipse2D.Double(identifierValue * 0.24 - var3x / 2.0, identifierValue * 0.5 - var3x / 2.0, var3x, var3x));
               graphics2D16.fill(new java.awt.geom.Ellipse2D.Double(identifierValue * 0.5 - var3x / 2.0, identifierValue * 0.5 - var3x / 2.0, var3x, var3x));
               graphics2D16.fill(new java.awt.geom.Ellipse2D.Double(identifierValue * 0.76 - var3x / 2.0, identifierValue * 0.5 - var3x / 2.0, var3x, var3x));
            });
            default -> false;
         };
         if (!identifierFlag) {
            return getCrossIco();
         } else {
            GEN_CORZCLIENT_MAP.put(identifierText, identifier);
            return identifier;
         }
      }
   }

   public static Identifier getIcoCube() {
      if (!icoCubeFlag) {
         icoCubeFlag = canFlagForIdentifierString(icoCubeIdentifier, "corz_ico_cube", graphics2D -> {
            double icoCubeValue = 32.0;
            double icoCubeValue2 = 10.88;
            double icoCubeValue3 = 52.48;
            double icoCubeValue4 = 21.44;
            double icoGenValue = 11.52;
            double icoCubeValue6 = 42.56;
            double icoCubeValue7 = 53.12;
            GeneralPath generalPath = new GeneralPath();
            generalPath.moveTo(icoCubeValue, icoCubeValue2);
            generalPath.lineTo(icoCubeValue3, icoCubeValue4);
            generalPath.lineTo(icoCubeValue3, icoCubeValue6);
            generalPath.lineTo(icoCubeValue, icoCubeValue7);
            generalPath.lineTo(icoGenValue, icoCubeValue6);
            generalPath.lineTo(icoGenValue, icoCubeValue4);
            generalPath.closePath();
            graphics2D.draw(generalPath);
            double icoCubeValue8 = 32.0;
            graphics2D.draw(new java.awt.geom.Line2D.Double(icoCubeValue, icoCubeValue2, icoCubeValue, icoCubeValue8));
            graphics2D.draw(new java.awt.geom.Line2D.Double(icoGenValue, icoCubeValue6, icoCubeValue, icoCubeValue8));
            graphics2D.draw(new java.awt.geom.Line2D.Double(icoCubeValue3, icoCubeValue6, icoCubeValue, icoCubeValue8));
         });
      }

      return icoCubeIdentifier;
   }

   public static Identifier getMprevIco() {
      if (!mprevIcoFlag) {
         mprevIcoFlag = canFlag4(mprevIcoIdentifier, "corz_ico_mprev", graphics2D -> {
            graphics2D.fill(new java.awt.geom.RoundRectangle2D.Double(15.36, 19.2, 5.44, 25.6, 3.2, 3.2));
            GeneralPath generalPath = new GeneralPath();
            generalPath.moveTo(47.36, 17.92);
            generalPath.lineTo(25.6, 32.0);
            generalPath.lineTo(47.36, 46.08);
            generalPath.closePath();
            graphics2D.fill(generalPath);
         });
      }

      return mprevIcoIdentifier;
   }

   public static Identifier getIdentifierGENERATED_IDENTIFIER_CACHE(String identifierText) {
      Identifier gen_corzclient_map = GENERATED_IDENTIFIER_CACHE.get(identifierText);
      if (gen_corzclient_map != null) {
         return gen_corzclient_map;
      } else {
         Identifier identifier = Identifier.of("corzclient", "gen/hud_" + identifierText);
         String strokeTextureText = "corz_ico_hud_" + identifierText;
         switch (identifierText) {
            case "person":
               canStrokeTexture(identifier, strokeTextureText, graphics2D -> {
                  graphics2D.fill(new java.awt.geom.Ellipse2D.Double(22.4, 8.96, 19.2, 19.2));
                  Area var1x = new Area(new java.awt.geom.Ellipse2D.Double(11.52, 32.0, 40.96, 35.84));
                  var1x.intersect(new Area(new java.awt.geom.Rectangle2D.Double(0.0, 0.0, 64.0, 52.48)));
                  graphics2D.fill(var1x);
               }, 0.0F, 2);
               break;
            case "pin":
               canStrokeTexture(identifier, strokeTextureText, graphics2D2 -> {
                  GeneralPath var1x = new GeneralPath();
                  var1x.moveTo(32.0, 52.48);
                  var1x.curveTo(32.0, 52.48, 15.36, 33.28, 15.36, 24.32);
                  var1x.curveTo(15.36, 12.16, 48.64, 12.16, 48.64, 24.32);
                  var1x.curveTo(48.64, 33.28, 32.0, 52.48, 32.0, 52.48);
                  var1x.closePath();
                  graphics2D2.fill(var1x);
               }, 0.0F, 2);
               break;
            case "cloud":
               canStrokeTexture(identifier, strokeTextureText, graphics2D3 -> {
                  Area var1x = new Area();
                  var1x.add(new Area(new java.awt.geom.Ellipse2D.Double(8.96, 26.88, 21.76, 21.76)));
                  var1x.add(new Area(new java.awt.geom.Ellipse2D.Double(21.12, 17.92, 25.6, 25.6)));
                  var1x.add(new Area(new java.awt.geom.Ellipse2D.Double(33.28, 26.88, 21.76, 21.76)));
                  var1x.add(new Area(new java.awt.geom.RoundRectangle2D.Double(11.52, 35.84, 40.96, 12.8, 12.8, 12.8)));
                  graphics2D3.fill(var1x);
               }, 0.0F, 2);
               break;
            case "gauge":
               canStrokeTexture(identifier, strokeTextureText, graphics2D4 -> {
                  GeneralPath var1x = new GeneralPath();
                  var1x.moveTo(10.24, 33.28);
                  var1x.lineTo(23.04, 33.28);
                  var1x.lineTo(29.44, 19.2);
                  var1x.lineTo(37.12, 46.08);
                  var1x.lineTo(42.24, 33.28);
                  var1x.lineTo(55.04, 33.28);
                  graphics2D4.draw(var1x);
               }, 7.36F, 2);
               break;
            case "signal":
               canStrokeTexture(identifier, strokeTextureText, graphics2D5 -> {
                  double var1x = 46.08;
                  graphics2D5.draw(new java.awt.geom.Line2D.Double(17.92, var1x, 17.92, 38.4));
                  graphics2D5.draw(new java.awt.geom.Line2D.Double(32.0, var1x, 32.0, 28.16));
                  graphics2D5.draw(new java.awt.geom.Line2D.Double(46.08, var1x, 46.08, 17.92));
               }, 7.36F, 2);
               break;
            case "speed":
               canStrokeTexture(identifier, strokeTextureText, graphics2D6 -> {
                  GeneralPath var1x = new GeneralPath();
                  var1x.moveTo(21.76, 17.92);
                  var1x.lineTo(33.28, 32.0);
                  var1x.lineTo(21.76, 46.08);
                  GeneralPath var2x = new GeneralPath();
                  var2x.moveTo(34.56, 17.92);
                  var2x.lineTo(46.08, 32.0);
                  var2x.lineTo(34.56, 46.08);
                  graphics2D6.draw(var1x);
                  graphics2D6.draw(var2x);
               }, 7.36F, 2);
               break;
            case "server":
               canStrokeTexture(identifier, strokeTextureText, graphics2D7 -> {
                  double var1x = 12.8;
                  double var3x = 38.4;
                  double identifierValue = 9.92;
                  double identifierValue2 = 3.52;
                  graphics2D7.fill(new java.awt.geom.RoundRectangle2D.Double(var1x, 15.36, var3x, identifierValue, identifierValue2, identifierValue2));
                  graphics2D7.fill(new java.awt.geom.RoundRectangle2D.Double(var1x, 27.04, var3x, identifierValue, identifierValue2, identifierValue2));
                  graphics2D7.fill(new java.awt.geom.RoundRectangle2D.Double(var1x, 38.72, var3x, identifierValue, identifierValue2, identifierValue2));
               }, 0.0F, 2);
               break;
            case "cube":
               canStrokeTexture(identifier, strokeTextureText, graphics2D8 -> {
                  double var1x = 32.0;
                  double var3x = 10.88;
                  double icoGenValue = 52.48;
                  double identifierValue4 = 21.44;
                  double identifierValue5 = 11.52;
                  double identifierValue6 = 42.56;
                  double identifierValue7 = 53.12;
                  GeneralPath generalPath = new GeneralPath();
                  generalPath.moveTo(var1x, var3x);
                  generalPath.lineTo(icoGenValue, identifierValue4);
                  generalPath.lineTo(icoGenValue, identifierValue6);
                  generalPath.lineTo(var1x, identifierValue7);
                  generalPath.lineTo(identifierValue5, identifierValue6);
                  generalPath.lineTo(identifierValue5, identifierValue4);
                  generalPath.closePath();
                  graphics2D8.draw(generalPath);
                  graphics2D8.draw(new java.awt.geom.Line2D.Double(identifierValue5, identifierValue4, var1x, 32.0));
                  graphics2D8.draw(new java.awt.geom.Line2D.Double(icoGenValue, identifierValue4, var1x, 32.0));
                  graphics2D8.draw(new java.awt.geom.Line2D.Double(var1x, 32.0, var1x, identifierValue7));
               }, 6.4511995F, 2);
               break;
            case "star":
               canStrokeTexture(identifier, strokeTextureText, graphics2D9 -> graphics2D9.fill(getGeneralPath(32.0, 33.28, 26.88, 11.2)), 0.0F, 2);
               break;
            case "clock":
               canStrokeTexture(identifier, strokeTextureText, graphics2D10 -> {
                  graphics2D10.draw(new java.awt.geom.Ellipse2D.Double(10.88, 10.88, 42.24, 42.24));
                  graphics2D10.draw(new java.awt.geom.Line2D.Double(32.0, 32.0, 32.0, 20.48));
                  graphics2D10.draw(new java.awt.geom.Line2D.Double(32.0, 32.0, 44.8, 32.0));
               }, 7.36F, 2);
               break;
            case "sparkles":
               canStrokeTexture(identifier, strokeTextureText, graphics2D11 -> {
                  graphics2D11.fill(getGeneralPath(26.88, 34.56, 19.2));
                  graphics2D11.fill(getGeneralPath(47.36, 16.64, 8.96));
               }, 0.0F, 2);
               break;
            case "shield":
               canStrokeTexture(identifier, strokeTextureText, graphics2D12 -> {
                  GeneralPath var1x = new GeneralPath();
                  var1x.moveTo(32.0, 10.24);
                  var1x.lineTo(51.2, 17.28);
                  var1x.curveTo(51.2, 38.4, 46.08, 48.64, 32.0, 55.04);
                  var1x.curveTo(17.92, 48.64, 12.8, 38.4, 12.8, 17.28);
                  var1x.closePath();
                  graphics2D12.fill(var1x);
               }, 0.0F, 2);
               break;
            case "eye":
               canStrokeTexture(identifier, strokeTextureText, graphics2D13 -> {
                  GeneralPath var1x = new GeneralPath();
                  var1x.moveTo(7.68, 32.0);
                  var1x.quadTo(32.0, 6.4, 56.32, 32.0);
                  var1x.quadTo(32.0, 57.6, 7.68, 32.0);
                  var1x.closePath();
                  graphics2D13.draw(var1x);
                  double var2x = 14.08;
                  graphics2D13.fill(new java.awt.geom.Ellipse2D.Double(32.0 - var2x / 2.0, 32.0 - var2x / 2.0, var2x, var2x));
               }, 6.4511995F, 2);
               break;
            case "target":
               canStrokeTexture(identifier, strokeTextureText, graphics2D14 -> {
                  graphics2D14.draw(new java.awt.geom.Ellipse2D.Double(10.88, 10.88, 42.24, 42.24));
                  double var1x = 15.36;
                  graphics2D14.fill(new java.awt.geom.Ellipse2D.Double(32.0 - var1x / 2.0, 32.0 - var1x / 2.0, var1x, var1x));
               }, 7.36F, 2);
         }

         GENERATED_IDENTIFIER_CACHE.put(identifierText, identifier);
         return identifier;
      }
   }

   private static boolean canStrokeTexture(Identifier id2, String strokeTextureText, int strokeTextureDivisor, float strokeTextureDivisor2, float strokeTextureValue, IcoGenUtils.Paint paint) {
      try {
         BufferedImage bufferedImage = new BufferedImage(strokeTextureDivisor, strokeTextureDivisor, 2);
         Graphics2D graphics = bufferedImage.createGraphics();
         graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
         graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
         graphics.setColor(Color.WHITE);
         graphics.scale(strokeTextureDivisor / strokeTextureDivisor2, strokeTextureDivisor / strokeTextureDivisor2);
         graphics.setStroke(new BasicStroke(strokeTextureValue, 1, 1));
         paint.paint(graphics);
         graphics.dispose();
         NativeImage nativeImage = new NativeImage(strokeTextureDivisor, strokeTextureDivisor, false);
         int[] rgb = bufferedImage.getRGB(0, 0, strokeTextureDivisor, strokeTextureDivisor, null, 0, strokeTextureDivisor);

         for (int i = 0; i < rgb.length; i++) {
            nativeImage.setColorArgb(i % strokeTextureDivisor, i / strokeTextureDivisor, rgb[i] & 0xFF000000 | 16777215);
         }

         NativeImageBackedTexture nativeImageBackedTexture = new NativeImageBackedTexture(() -> strokeTextureText, nativeImage);
         MinecraftClient.getInstance().getTextureManager().registerTexture(id2, nativeImageBackedTexture);
         return true;
      } catch (Throwable e) {
         return false;
      }
   }

   private static boolean canFlag4(Identifier id2, String icoGenText, IcoGenUtils.Paint paint) {
      return canStrokeTexture(id2, icoGenText, paint, 0.0F);
   }

   private interface Paint {
      void paint(Graphics2D graphics2D);
   }
}
