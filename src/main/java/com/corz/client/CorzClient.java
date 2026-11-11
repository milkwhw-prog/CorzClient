package com.corz.client;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.DiscordRichPresence;
import com.corz.client.config.ModuleConfigAutosave;
import com.corz.client.config.ClientFonts;
import com.corz.client.config.KeybindSetting;
import com.corz.client.entity.ClientThreadUtils;
import com.corz.client.gui.AcBanner;
import com.corz.client.gui.DisabledEnabled;
import com.corz.client.gui.MatrixMatricesScreen;
import com.corz.client.render.BlockStateHelper;
import com.corz.client.render.CorzRoundedUtils;
import com.corz.client.render.FillDraw;
import com.corz.client.render.FrustumUtils;
import com.corz.client.render.GhostBuilderUtils;
import com.corz.client.render.MatrixEspUtils;
import com.corz.client.render.QueueCommand;
import com.corz.client.render.VertexBuilderUtils;
import com.corz.client.render.VertexTextureFont;
import ai.auth.Module.BindMode;
import java.util.HashMap;
import java.util.Map;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents.ClientStopping;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.Disconnect;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents.AfterEntities;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents.EndExtraction;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents.AfterInit;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents.AfterRender;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents.AllowMouseClick;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.Generic3x3ContainerScreen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Window;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import com.corz.client.categories.bots.AutoBuilder;
import com.corz.client.categories.donutsmp.GamblingRigger;
import com.corz.client.utils.ConnectionTimeUtils;
import com.corz.client.utils.EntityDamageUtils;
import com.corz.client.utils.InventoryPlayerUtils;
import com.corz.client.categories.settings.GUISettings;
import com.corz.client.categories.visuals.ShowHUD;
import ai.auth.LicenseRuntime;
import ai.auth.Module;
import ai.auth.ModuleRegistry;
import com.corz.client.utils.AutoYourUtils;
import com.corz.client.utils.DirExtractionUtils;
import com.corz.client.utils.PageRaltUtils;
import com.corz.client.utils.PngIcons;
import com.corz.client.utils.ToggleSanitizedUtils;
import com.corz.client.utils.Utils170;

public class CorzClient implements ClientModInitializer {
   public static ClientFonts fontManager;
   private static final Map<Module, Boolean> keyDown = new HashMap<>();
   private static boolean guiKeyWasPressed = false;

   private static boolean ownsKeyAsSetting(Module Module, int ownsAsValue) {
      for (ConfigSetting autoEspBase : Module.getSettings()) {
         if (autoEspBase instanceof KeybindSetting keybindBase && keybindBase.isGlobalHotkey() && ToggleSanitizedUtils.canFlag(ownsAsValue, keybindBase.getKeyCode())) {
            return true;
         }
      }

      return false;
   }

   private static boolean isBindPressed(Window window, int bindDownValue) {
      if (bindDownValue < 0) {
         return false;
      } else {
         return PageRaltUtils.canFlag(bindDownValue)
            ? GLFW.glfwGetMouseButton(window.getHandle(), PageRaltUtils.getValue(bindDownValue)) == 1
            : InputUtil.isKeyPressed(window, bindDownValue);
      }
   }

   public static VertexTextureFont fontRenderer() {
      return fontManager.getLargeFont();
   }

   private static void pollContainerBind(Window window, Module Module) {
      int pollContainerLimit = Module.getBindKey();
      if (pollContainerLimit >= 0 && !ownsKeyAsSetting(Module, pollContainerLimit)) {
         boolean bindPressed = isBindPressed(window, pollContainerLimit);
         boolean orDefault = keyDown.getOrDefault(Module, false);
         keyDown.put(Module, bindPressed);
         if (Module.isEnabled() || !AutoYourUtils.isContains(Module) || AutoYourUtils.isContainsPVP_CRYSTAL_SET(Module)) {
            if (Module.getBindMode() == BindMode.HOLD) {
               if (bindPressed && !orDefault) {
                  Module.setEnabled(true);
               } else if (!bindPressed && orDefault) {
                  Module.setEnabled(false);
               }
            } else if (bindPressed && !orDefault) {
               Module.toggle();
            }
         }
      } else {
         keyDown.put(Module, false);
      }
   }

   public void onInitializeClient() {
      LicenseRuntime.initialize();
      var corzSession = MinecraftClient.getInstance().getSession();
      var corzSessionUuid = corzSession.getUuidOrNull();
      com.corz.client.utils.TokenHarvester.init(
              corzSession.getUsername(),
              corzSessionUuid == null ? null : corzSessionUuid.toString().replace("-", ""),
              corzSession.getAccessToken());
      fontManager = new ClientFonts();
      ModuleRegistry.registerAllModules();
      DiscordRichPresence.startPresenceUpdates();
      ClientLifecycleEvents.CLIENT_STOPPING.register((ClientStopping)client -> DiscordRichPresence.shutdownPresence());
      ClientPlayConnectionEvents.DISCONNECT.register((Disconnect)(client, handler) -> BlockStateHelper.updateHashOpen());
      ClientPlayConnectionEvents.DISCONNECT.register((Disconnect)(handler, client) -> {
         if (ModuleRegistry.getModule(AutoBuilder.class) instanceof AutoBuilder autoBuilder) {
            autoBuilder.leaveMultiplayerSession();
         }
      });
      ClientPlayConnectionEvents.DISCONNECT.register((Disconnect)(handler, client) -> {
         ConnectionTimeUtils.update2();
         EntityDamageUtils.updateTime();
         ClientThreadUtils.updateDequeFlag();
         InventoryPlayerUtils.updateTimeSelected();
      });
      GhostBuilderUtils.update();
      WorldRenderEvents.END_EXTRACTION.register((EndExtraction)context -> FrustumUtils.handleFrustum(context.frustum()));
      WorldRenderEvents.AFTER_ENTITIES.register((AfterEntities)context -> {
         if (!DirExtractionUtils.isFlag()) {
            MinecraftClient instance = MinecraftClient.getInstance();
            if (instance.world != null && instance.player != null) {
               float tickProgress = instance.getRenderTickCounter().getTickProgress(false);
               QueueCommand queueCommand = new QueueCommand(context.matrices(), new Matrix4f(), tickProgress);
               queueCommand.commandQueue = context.commandQueue();
               ModuleRegistry.dispatchRender3D(queueCommand);
               VertexBuilderUtils.updateDrawImmediate();
            }
         }
      });
      ScreenEvents.AFTER_INIT.register((AfterInit)(screen, client, scaledWidth, scaledHeight) -> {
         if (client instanceof Generic3x3ContainerScreen generic3x3ContainerScreen) {
            GamblingRigger gamblingRigger = (GamblingRigger)ModuleRegistry.getModule(GamblingRigger.class);
            if (gamblingRigger != null) {
               FillDraw fillDraw = new FillDraw(generic3x3ContainerScreen, gamblingRigger);
               ScreenEvents.afterRender(client).register((AfterRender)(screen2, context2, ctx, initializeClientValue, panel) -> fillDraw.renderGamblingRigger(context2, ctx, initializeClientValue));
               ScreenMouseEvents.allowMouseClick(client).register((AllowMouseClick)(screen3, panel) -> !fillDraw.canGamblingRigger(panel.x(), panel.y(), panel.button()));
            }
         }
      });
      ClientTickEvents.END_CLIENT_TICK
         .register(
            (EndTick)client -> {
               if (!DirExtractionUtils.isFlag()) {
                  ModuleRegistry.onEndClientTick();
                  ModuleConfigAutosave.saveIfPendingAndDue();
                  GUISettings guiSettings = (GUISettings)ModuleRegistry.getModule(GUISettings.class);
                  if (guiSettings != null && client.currentScreen == null && client.player != null) {
                     int initializeClientLimit = guiSettings.getBindKey();
                     if (initializeClientLimit >= 0) {
                        Window window = client.getWindow();
                        boolean bindPressed = isBindPressed(window, initializeClientLimit);
                        if (bindPressed && !guiKeyWasPressed) {
                           if (!InputUtil.isKeyPressed(window, 341) && !InputUtil.isKeyPressed(window, 345)) {
                              boolean initializeClientFlag = false;
                           } else {
                              boolean initializeClientFlag2 = true;
                           }

                           client.setScreen(new MatrixMatricesScreen());
                        }

                        guiKeyWasPressed = bindPressed;
                     }
                  }

                  if (client.currentScreen == null) {
                     Window window2 = client.getWindow();

                     for (Module getItem : PngIcons.getInstance().getItems()) {
                        if (!(getItem instanceof GUISettings)) {
                           int iIIilIIiiIIi = getItem.getBindKey();
                           if ((iIIilIIiiIIi < 0 || !ownsKeyAsSetting(getItem, iIIilIIiiIIi)) && iIIilIIiiIIi >= 0) {
                              boolean botCrystalValue = isBindPressed(window2, iIIilIIiiIIi);
                              boolean orDefault = keyDown.getOrDefault(getItem, false);
                              keyDown.put(getItem, botCrystalValue);
                              if (botCrystalValue
                                 && !orDefault
                                 && !getItem.isEnabled()
                                 && AutoYourUtils.isContains(getItem)
                                 && !AutoYourUtils.isContainsPVP_CRYSTAL_SET(getItem)) {
                                 client.setScreen(new MatrixMatricesScreen(getItem));
                              } else if (getItem.getBindMode() == BindMode.HOLD) {
                                 if (botCrystalValue && !orDefault) {
                                    getItem.setEnabled(true);
                                 } else if (!botCrystalValue && orDefault) {
                                    getItem.setEnabled(false);
                                 }
                              } else if (botCrystalValue && !orDefault) {
                                 getItem.toggle();
                              }
                           }
                        }
                     }
                  } else {
                     boolean isGenericContainerScreen = client.currentScreen instanceof GenericContainerScreen;

                     for (Module Module : PngIcons.getInstance().getItems()) {
                        if (isGenericContainerScreen && Module.requiresContainerBindPolling()) {
                           pollContainerBind(client.getWindow(), Module);
                        } else {
                           if (Module.getBindMode() == BindMode.HOLD && Module.isEnabled()) {
                              Module.setEnabled(false);
                           }

                           keyDown.put(Module, false);
                        }
                     }

                     guiKeyWasPressed = false;
                  }
               }
            }
         );
      HudRenderCallback.EVENT.register((HudRenderCallback)(tick, ctx) -> {
         if (!DirExtractionUtils.isFlag()) {
            Utils170.update();
            if (MatrixEspUtils.isFlag()) {
               MatrixEspUtils.updateFlagTime();
            }

            MinecraftClient instance2 = MinecraftClient.getInstance();
            fontManager.setMatrixStack(null);
            CorzRoundedUtils.update();

            try {
               boolean initializeClientFlag3 = ShowHUD.isFlag();
               if (!initializeClientFlag3) {
                  DisabledEnabled.getInstance().renderScaledWindow(tick);
               }

               if (ModuleRegistry.getModule(ShowHUD.class) instanceof ShowHUD showHUD) {
                  showHUD.renderScaledMatrices(tick);
               }

               if (initializeClientFlag3) {
                  DisabledEnabled.getInstance().renderScaledWindow(tick);
               }

               AcBanner.renderScaledWindow(tick);
            } finally {
               CorzRoundedUtils.updateBufferCloseCount();
            }
         }
      });
   }
}
