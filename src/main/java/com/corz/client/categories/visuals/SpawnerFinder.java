package com.corz.client.categories.visuals;

import com.corz.client.CorzClient;
import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.ColorSetting;
import com.corz.client.config.ModuleConfigAutosave;
import com.corz.client.config.DoubleSetting;
import com.corz.client.config.EnumSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.gui.ButtonBase;
import com.corz.client.gui.DisabledEnabled;
import com.corz.client.gui.MatrixPeekUtilsRender;
import com.corz.client.gui.TickProgress;
import com.corz.client.render.CorzRoundedUtils;
import com.corz.client.render.QueueCommand;
import com.corz.client.render.VectorDotUtils;
import com.corz.client.render.VertexBuilderUtils;
import com.corz.client.render.VertexConsumerUtilsVector;
import com.corz.client.render.VertexTexturePeek;
import com.corz.client.utils.ChunkBlockUtils.Result;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents.Load;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents.Unload;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import org.joml.Matrix4f;
import com.corz.client.utils.ChunkBlockUtils;
import com.corz.client.utils.MinecraftSpiderUtils;
import com.corz.client.utils.PriorityQueueHelper;
import com.corz.client.utils.RememberedSpawner;
import com.corz.client.utils.Spawner;
import com.corz.client.utils.SpawnerTimeUtils;
import ai.auth.Module;
import com.corz.client.categories.visuals.ShowHUD;
import com.corz.client.utils.IconsPngType;

public class SpawnerFinder extends Module {
   private int labelsValue;
   private final BooleanSetting booleanBase;
   private final BooleanSetting showDistance;
   private final int[] labelsScanValues;
   private int scanTickCounter;
   private final RememberedSpawner iIiRememberedSpawner;
   private final ColorSetting colorBase;
   private final DoubleSetting doubleHelper;
   private final ColorSetting skeletonColor;
   private final BooleanSetting labels;
   private final BooleanSetting tracers;
   private final IntegerSetting integerHelper;
   private final IntegerSetting maxDistance;
   private final IntegerSetting chunkRadius;
   private final BooleanSetting showCoordinates;
   private final String[] labelsTexts;
   private final Result iIiResult;
   private volatile List<Spawner> labelsItems;
   private final IntegerSetting scanBudget;
   private final ColorSetting blazeColor;
   private final DoubleSetting beamHeight;
   private final BooleanSetting rememberSpawners = new BooleanSetting(
      "Remember Spawners",
      "Keep spawners you found visible after their chunk unloads. Scoped per server and dimension, saved locally, re-checked when the chunk loads again.",
      true
   );
   private final Map<Long, Integer> spawnerIntegerMap;
   private final IntegerSetting fillAlpha;
   private final ButtonBase buttonBase;
   private final PriorityQueueHelper chunkClampedPriorityQueueHel;
   private int cachedMaxY;
   public static volatile SpawnerFinder singleton;
   private int cachedMinY;
   private final Map<String, String> unknownMap;
   private final ColorSetting zombieColor;
   private final ColorSetting spiderColor;
   private final BooleanSetting spawnerFinderBooleanBase;
   private final DoubleSetting lineWidth;
   private ClientWorld world;
   private final float[] labelsAmounts;
   private final ColorSetting unknownColor;
   private long timeMillis;
   private final EnumSetting enumHelper = new EnumSetting("Box", "How each spawner block is boxed", "Outline", "Off", "Outline", "Filled", "Both");
   private final Set<Long> forgetRememberedSet;
   private boolean iIiActive;
   private final IntegerSetting minY;
   private final ColorSetting caveSpider;
   private final float[] labelsScanAmounts;

   @Override
   protected void onDisable() {
      this.iIiRememberedSpawner.updateRememberedFlag();
      this.chunkClampedPriorityQueueHel.updatePriorityQueue();
      this.forgetRememberedSet.clear();
      this.spawnerIntegerMap.clear();
      this.world = null;
      this.labelsItems = List.of();
      this.labelsValue = -1;
      this.iIiActive = false;
      ModuleConfigAutosave.updateFlag2();
   }

   private void handleIntegerYinclusive(ClientWorld clientWorld) {
      this.iIiRememberedSpawner.updateRememberedFlag();
      this.chunkClampedPriorityQueueHel.updatePriorityQueue();
      this.forgetRememberedSet.clear();
      this.spawnerIntegerMap.clear();
      this.world = clientWorld;
      this.cachedMinY = this.cachedMaxY = Integer.MIN_VALUE;
      if (clientWorld != null) {
         this.iIiRememberedSpawner.handleTextRemembered(this.formatServerAddress(clientWorld));
         this.cachedMinY = Math.max(clientWorld.getBottomY(), Math.min(this.minY.getValue(), this.integerHelper.getValue()));
         this.cachedMaxY = Math.min(clientWorld.getTopYInclusive(), Math.max(this.minY.getValue(), this.integerHelper.getValue()));
         this.handleChunkClamped(clientWorld);
      }

      this.iIiActive = false;
      ModuleConfigAutosave.updateFlag2();
   }

   private void updateForgottenSpawners() {
      this.iIiRememberedSpawner.updateRememberedSpawnerSpawnerMap();
      ModuleConfigAutosave.updateFlag2();
      DisabledEnabled.getInstance().handleDisabledEnabled("Spawner Finder", "Remembered spawners forgotten", false);
   }

   private void renderSpawnerRemembered(DrawContext context, VertexTexturePeek vertexTexturePeek, float spawnerRememberedDivisor) {
      var labelsItems = this.labelsItems;
      if (!labelsItems.isEmpty()) {
         Vec3d cameraPos = mc.gameRenderer.getCamera().getCameraPos();
         double spawnerRememberedFactor = this.maxDistance.getValue();
         double spawnerRememberedValue = spawnerRememberedFactor * spawnerRememberedFactor;
         var arrayList = new ArrayList();

         for (Spawner next : labelsItems) {
            double spawnerRememberedFactor2 = next.spawnerRememberedValueSpawne + 0.5 - cameraPos.x;
            double spawnerRememberedFactor3 = next.spawnerRememberedValue + 0.5 - cameraPos.y;
            double spawnerRememberedFactor4 = next.rememberedSpawnerValue + 0.5 - cameraPos.z;
            double spawnerRememberedLimit = spawnerRememberedFactor2 * spawnerRememberedFactor2 + spawnerRememberedFactor3 * spawnerRememberedFactor3 + spawnerRememberedFactor4 * spawnerRememberedFactor4;
            if (!(spawnerRememberedLimit > spawnerRememberedValue)) {
               Vec3d windowFramebuffer = VertexConsumerUtilsVector.getWindowFramebuffer(
                  new Vec3d(next.spawnerRememberedValueSpawne + 0.5, next.spawnerRememberedValue + 1.18, next.rememberedSpawnerValue + 0.5)
               );
               if (windowFramebuffer != null && !(windowFramebuffer.z <= 0.0) && !(windowFramebuffer.z >= 1.0)) {
                  SpawnerFinder.Label label = new SpawnerFinder.Label();
                  label.next = next;
                  label.spawnerAmount = Math.sqrt(spawnerRememberedLimit);
                  label.centerX = (float)(windowFramebuffer.x / spawnerRememberedDivisor);
                  label.spawnerRememberedAmount = (float)(windowFramebuffer.y / spawnerRememberedDivisor);
                  arrayList.add(label);
               }
            }
         }

         if (!arrayList.isEmpty()) {
            arrayList.sort((obj, obj2) -> Double.compare(((com.corz.client.categories.visuals.SpawnerFinder.Label) obj).spawnerAmount, ((com.corz.client.categories.visuals.SpawnerFinder.Label) obj2).spawnerAmount));
            int min2 = Math.min(48, arrayList.size());
            float labelsSpawnerValue = CorzRoundedUtils.scaledWidth > 0.0F
               ? CorzRoundedUtils.scaledWidth
               : (float)mc.getWindow().getFramebufferWidth() / mc.getWindow().getScaleFactor();
            float spawnerRememberedValue4 = CorzRoundedUtils.scaledHeight > 0.0F
               ? CorzRoundedUtils.scaledHeight
               : (float)mc.getWindow().getFramebufferHeight() / mc.getWindow().getScaleFactor();

            for (int i = min2 - 1; i >= 0; i--) {
               this.renderLabelsSpawner(context, vertexTexturePeek, (SpawnerFinder.Label)arrayList.get(i), labelsSpawnerValue, spawnerRememberedValue4);
            }
         }
      }
   }

   public static void handleInstance(ClientWorld clientWorld, BlockPos pos2, BlockState state, BlockState blockState) {
      SpawnerFinder singleton2 = singleton;
      if (singleton2 != null && singleton2.isEnabled()) {
         singleton2.handleSpawnerMin(clientWorld, pos2, state, blockState);
      }
   }

   private String formatUnknown(String unknownText) {
      if (unknownText == null) {
         return "Unknown";
      } else {
         String unknownmap = this.unknownMap.get(unknownText);
         if (unknownmap != null) {
            return unknownmap;
         } else {
            String formatUnknown;
            try {
               formatUnknown = EntityType.get(unknownText).map(entityType -> entityType.getName().getString()).orElse(MinecraftSpiderUtils.formatUnknown(unknownText));
            } catch (Throwable e) {
               formatUnknown = MinecraftSpiderUtils.formatUnknown(unknownText);
            }

            this.unknownMap.put(unknownText, formatUnknown);
            return formatUnknown;
         }
      }
   }

   @Override
   public void loadExtraData(JsonElement json) {
      if (json != null && json.isJsonObject()) {
         JsonObject asJsonObject = json.getAsJsonObject();
         if (asJsonObject.has("remembered") && asJsonObject.get("remembered").isJsonObject()) {
            for (Entry entry : asJsonObject.getAsJsonObject("remembered").entrySet()) {
               if (((JsonElement)entry.getValue()).isJsonArray()) {
                  var arrayList = new ArrayList();
                  var arrayList2 = new ArrayList();

                  for (JsonElement jsonElement : ((JsonElement)entry.getValue()).getAsJsonArray()) {
                     if (jsonElement.isJsonArray()) {
                        JsonArray asJsonArray = jsonElement.getAsJsonArray();
                        if (asJsonArray.size() >= 3) {
                           try {
                              arrayList.add(new int[]{asJsonArray.get(0).getAsInt(), asJsonArray.get(1).getAsInt(), asJsonArray.get(2).getAsInt()});
                              String iIiText = asJsonArray.size() > 3 ? asJsonArray.get(3).getAsString() : "";
                              arrayList2.add(iIiText.isEmpty() ? null : iIiText);
                           } catch (Exception e) {
                           }

                           if (arrayList.size() >= 4096) {
                              break;
                           }
                        }
                     }
                  }

                  this.iIiRememberedSpawner.handleRememberedTime((String)entry.getKey(), arrayList, arrayList2);
               }
            }

            this.labelsValue = -1;
         }
      }
   }

   public int getLabels() {
      return this.labelsItems.size();
   }

   private void handleSpawnerMin(ClientWorld clientWorld, BlockPos pos2, BlockState state, BlockState blockState) {
      boolean of = state.isOf(Blocks.SPAWNER);
      boolean of2 = blockState.isOf(Blocks.SPAWNER);
      if (of || of2) {
         if (clientWorld != this.world) {
            this.handleIntegerYinclusive(clientWorld);
         }

         int x = pos2.getX();
         int y = pos2.getY();
         int z = pos2.getZ();
         if (of2) {
            if (y < this.cachedMinY || y > this.cachedMaxY) {
               return;
            }

            this.iIiRememberedSpawner.canLoadedTime(x, y, z, null);
            this.spawnerIntegerMap.put(SpawnerTimeUtils.getTime(x, y, z), 100);
         } else {
            this.spawnerIntegerMap.remove(SpawnerTimeUtils.getTime(x, y, z));
            this.iIiRememberedSpawner.canFlag(x, y, z);
         }
      }
   }

   private int getColorBase(Spawner spawner) {
      return switch (MinecraftSpiderUtils.getMinecraftSpider(spawner.getSpawnerText())) {
         case ZOMBIE -> this.zombieColor.getColor();
         case SKELETON -> this.skeletonColor.getColor();
         case SPIDER -> this.spiderColor.getColor();
         case CAVE_SPIDER -> this.caveSpider.getColor();
         case BLAZE -> this.blazeColor.getColor();
         case OTHER -> this.colorBase.getColor();
         case UNKNOWN -> this.unknownColor.getColor();
      };
   }

   private static int getClamp(int clampValue, float glfwLimit) {
      int clamp = MathHelper.clamp(Math.round(glfwLimit), 0, 255);
      return clamp << 24 | clampValue & 16777215;
   }

   private void handleRememberedSpawner(ClientWorld clientWorld) {
      if (!this.spawnerIntegerMap.isEmpty()) {
         int rememberedSpawnerCounter = 0;

         for (Iterator iterator = this.spawnerIntegerMap.entrySet().iterator(); iterator.hasNext() && rememberedSpawnerCounter < 16; rememberedSpawnerCounter++) {
            var entry = (Entry)iterator.next();
            long rememberedSpawnerValue = (Long)entry.getKey();
            Spawner spawner = this.iIiRememberedSpawner.getSpawner(rememberedSpawnerValue);
            if (spawner != null && !spawner.isSpawnerText() && spawner.isLoadedRemembered()) {
               String formatLogicSpawn = ChunkBlockUtils.formatLogicSpawn(
                  clientWorld.getBlockEntity(new BlockPos(spawner.spawnerRememberedValueSpawne, spawner.spawnerRememberedValue, spawner.rememberedSpawnerValue))
               );
               if (formatLogicSpawn != null) {
                  this.iIiRememberedSpawner.canFlag(rememberedSpawnerValue, formatLogicSpawn);
                  iterator.remove();
               } else if ((Integer)entry.getValue() <= 1) {
                  iterator.remove();
               } else {
                  entry.setValue((Integer)entry.getValue() - 1);
               }
            } else {
               iterator.remove();
            }
         }
      }
   }

   public static void handleInstance(BlockPos pos2) {
      SpawnerFinder singleton2 = singleton;
      if (singleton2 != null && singleton2.isEnabled() && pos2 != null) {
         singleton2.handleThreadWorld(pos2);
      }
   }

   private void handleChunkClamped(ClientWorld clientWorld) {
      if (mc.player != null) {
         int chunkClampedLimit = Math.min(this.chunkRadius.getValue(), mc.options.getClampedViewDistance() + 2);
         ChunkPos chunkPos = mc.player.getChunkPos();
         this.chunkClampedPriorityQueueHel.handlePriorityQueue(chunkPos.x, chunkPos.z);

         for (int i = -chunkClampedLimit; i <= chunkClampedLimit; i++) {
            for (int j = -chunkClampedLimit; j <= chunkClampedLimit; j++) {
               int chunkClampedValue = chunkPos.x + i;
               int chunkClampedValue2 = chunkPos.z + j;
               if (clientWorld.isChunkLoaded(chunkClampedValue, chunkClampedValue2)) {
                  long timeForInt = SpawnerTimeUtils.getTimeForInt(chunkClampedValue, chunkClampedValue2);
                  if (!this.forgetRememberedSet.contains(timeForInt)) {
                     this.chunkClampedPriorityQueueHel.canPriorityQueue(timeForInt);
                  }
               }
            }
         }
      }
   }

   @Override
   protected void onEnable() {
      this.world = null;
      this.cachedMinY = this.cachedMaxY = Integer.MIN_VALUE;
      this.scanTickCounter = 0;
      this.labelsValue = -1;
      this.labelsItems = List.of();
   }

   private void handleThreadWorld(BlockPos pos2) {
      ClientWorld world = mc.world;
      if (world != null && world == this.world && mc.isOnThread()) {
         long threadWorldValue = SpawnerTimeUtils.getTime(pos2.getX(), pos2.getY(), pos2.getZ());
         if (this.iIiRememberedSpawner.getSpawner(threadWorldValue) != null) {
            String formatLogicSpawn = ChunkBlockUtils.formatLogicSpawn(world.getBlockEntity(pos2));
            if (formatLogicSpawn != null) {
               this.iIiRememberedSpawner.canFlag(threadWorldValue, formatLogicSpawn);
               this.spawnerIntegerMap.remove(threadWorldValue);
            }
         }
      }
   }

   private String formatServerAddress(ClientWorld clientWorld) {
      ServerInfo currentServerEntry = mc.getCurrentServerEntry();
      String serverAddressText;
      if (currentServerEntry != null && currentServerEntry.address != null && !currentServerEntry.address.isBlank()) {
         serverAddressText = "mp:" + currentServerEntry.address.trim().toLowerCase(Locale.ROOT);
      } else if (mc.isInSingleplayer() && mc.getServer() != null) {
         serverAddressText = "sp:" + mc.getServer().getSaveProperties().getLevelName();
      } else {
         serverAddressText = "unknown";
      }

      return serverAddressText + "|" + clientWorld.getRegistryKey().getValue();
   }

   @Override
   public JsonElement saveExtraData() {
      var map = this.iIiRememberedSpawner.getText();
      if (map.isEmpty()) {
         return null;
      } else {
         JsonObject jsonObject = new JsonObject();

         for (Entry entry : map.entrySet()) {
            JsonArray jsonArray = new JsonArray();

            for (Spawner spawner : (Iterable<com.corz.client.utils.Spawner>) ((List)entry.getValue())) {
               JsonArray jsonArray2 = new JsonArray();
               jsonArray2.add(spawner.spawnerRememberedValueSpawne);
               jsonArray2.add(spawner.spawnerRememberedValue);
               jsonArray2.add(spawner.rememberedSpawnerValue);
               jsonArray2.add(spawner.getSpawnerText() == null ? "" : spawner.getSpawnerText());
               jsonArray.add(jsonArray2);
            }

            jsonObject.add((String)entry.getKey(), jsonArray);
         }

         JsonObject jsonObject2 = new JsonObject();
         jsonObject2.add("remembered", jsonObject);
         return jsonObject2;
      }
   }

   public SpawnerFinder() {
      super("Spawner Finder", IconsPngType.VISUAL);
      this.tracers = new BooleanSetting("Tracers", "Line from your crosshair to each spawner", false);
      this.spawnerFinderBooleanBase = new BooleanSetting("Beam", "Vertical beacon beam rising from each spawner", true);
      this.labels = new BooleanSetting("Labels", "Screen-space label over each spawner", true);
      this.booleanBase = new BooleanSetting("Show Mob Type", "Spawned mob on the label (Unknown when the server did not send it)", true);
      this.showDistance = new BooleanSetting("Show Distance", "Distance in blocks on the label", true);
      this.showCoordinates = new BooleanSetting("Show Coordinates", "Block coordinates on the label", false);
      this.chunkRadius = new IntegerSetting(
         "Chunk Radius", "How far around you (in chunks) loaded chunks are queued for scanning. Chunks the server never sent can't be scanned.", 12, 2, 32
      );
      this.minY = new IntegerSetting("Min Y", "Lowest Y scanned (clamped to the world floor, -64 in the Overworld)", -64, -64, 320);
      this.integerHelper = new IntegerSetting("Max Y", "Highest Y scanned (clamped to the world ceiling)", 320, -64, 320);
      this.scanBudget = new IntegerSetting(
         "Scan Budget", "Chunk columns scanned per tick while catching up. Higher = faster first sweep, more work per tick.", 16, 1, 64
      );
      this.maxDistance = new IntegerSetting("Max Distance", "Spawners farther than this (blocks) are not drawn", 256, 16, 1024);
      this.fillAlpha = new IntegerSetting("Fill Alpha", "Transparency of the filled box", 60, 1, 255);
      this.lineWidth = new DoubleSetting("Line Width", "Outline / tracer line width (px)", 2.0, 1.0, 4.0, 0.5);
      this.beamHeight = new DoubleSetting("Beam Height", "Height of the beacon beam (blocks)", 24.0, 4.0, 128.0, 1.0);
      this.doubleHelper = new DoubleSetting("Remembered Fade", "Opacity of remembered (unloaded) spawners relative to loaded ones", 0.45, 0.1, 1.0, 0.05);
      this.zombieColor = new ColorSetting("Zombie", "Zombie spawners", -11218092);
      this.skeletonColor = new ColorSetting("Skeleton", "Skeleton spawners", -1644826);
      this.spiderColor = new ColorSetting("Spider", "Spider spawners", -4169668);
      this.caveSpider = new ColorSetting("Cave Spider", "Cave spider spawners", -13649736);
      this.blazeColor = new ColorSetting("Blaze", "Blaze spawners", -22746);
      this.colorBase = new ColorSetting("Other Mob", "Any other known mob (silverfish, magma cube, custom\u2026)", -4819969);
      this.unknownColor = new ColorSetting("Unknown", "Spawners whose mob the server did not send", -6516796);
      this.buttonBase = new ButtonBase(
            "Forget Remembered", "Delete every remembered spawner (all servers and dimensions)", "Forget", this::updateForgottenSpawners
         )
         .getFlagButtonActive();
      this.iIiRememberedSpawner = new RememberedSpawner();
      this.chunkClampedPriorityQueueHel = new PriorityQueueHelper();
      this.forgetRememberedSet = new HashSet<>();
      this.iIiResult = new Result();
      this.spawnerIntegerMap = new HashMap<>();
      this.unknownMap = new HashMap<>();
      this.cachedMinY = Integer.MIN_VALUE;
      this.cachedMaxY = Integer.MIN_VALUE;
      this.labelsValue = -1;
      this.labelsItems = List.of();
      this.labelsTexts = new String[4];
      this.labelsAmounts = new float[4];
      this.labelsScanAmounts = new float[4];
      this.labelsScanValues = new int[4];
      this.booleanBase.withGroupName("Labels");
      this.showDistance.withGroupName("Labels");
      this.showCoordinates.withGroupName("Labels");
      this.chunkRadius.withGroupName("Scan");
      this.minY.withGroupName("Scan");
      this.integerHelper.withGroupName("Scan");
      this.scanBudget.withGroupName("Scan");
      this.maxDistance.withGroupName("Render");
      this.fillAlpha.withGroupName("Render");
      this.lineWidth.withGroupName("Render");
      this.beamHeight.withGroupName("Render");
      this.doubleHelper.withGroupName("Render");
      this.zombieColor.withGroupName("Colors");
      this.skeletonColor.withGroupName("Colors");
      this.spiderColor.withGroupName("Colors");
      this.caveSpider.withGroupName("Colors");
      this.blazeColor.withGroupName("Colors");
      this.colorBase.withGroupName("Colors");
      this.unknownColor.withGroupName("Colors");
      this.addSettings(
         new ConfigSetting[]{
            this.rememberSpawners,
            this.enumHelper,
            this.tracers,
            this.spawnerFinderBooleanBase,
            this.labels,
            this.booleanBase,
            this.showDistance,
            this.showCoordinates,
            this.chunkRadius,
            this.minY,
            this.integerHelper,
            this.scanBudget,
            this.maxDistance,
            this.fillAlpha,
            this.lineWidth,
            this.beamHeight,
            this.doubleHelper,
            this.zombieColor,
            this.skeletonColor,
            this.spiderColor,
            this.caveSpider,
            this.blazeColor,
            this.colorBase,
            this.unknownColor,
            this.buttonBase
         }
      );
      singleton = this;
      ClientChunkEvents.CHUNK_LOAD.register((Load)(chunk, world) -> {
         if (this.isEnabled()) {
            if (chunk != this.world) {
               this.handleIntegerYinclusive(chunk);
            }

            long spawnerFinderValue = SpawnerTimeUtils.getTimeForInt(world.getPos().x, world.getPos().z);
            this.forgetRememberedSet.remove(spawnerFinderValue);
            this.chunkClampedPriorityQueueHel.canPriorityQueue(spawnerFinderValue);
         }
      });
      ClientChunkEvents.CHUNK_UNLOAD.register((Unload)(chunk, world) -> {
         if (this.isEnabled() && chunk == this.world) {
            long timeForInt = SpawnerTimeUtils.getTimeForInt(world.getPos().x, world.getPos().z);
            this.chunkClampedPriorityQueueHel.canPriorityQueueForLong(timeForInt);
            this.forgetRememberedSet.remove(timeForInt);
            this.iIiRememberedSpawner.handleRememberedFlag(timeForInt);
            this.iIiActive = true;
         }
      });
   }

   @Override
   protected void onReset() {
      ClientWorld world = mc.world;
      if (world != this.world) {
         this.handleIntegerYinclusive(world);
      }

      if (world != null && mc.player != null) {
         this.scanTickCounter++;
         this.iIiRememberedSpawner.handleFlag(this.rememberSpawners.isEnabled());
         int max = Math.max(world.getBottomY(), Math.min(this.minY.getValue(), this.integerHelper.getValue()));
         int min = Math.min(world.getTopYInclusive(), Math.max(this.minY.getValue(), this.integerHelper.getValue()));
         if (max != this.cachedMinY || min != this.cachedMaxY) {
            this.cachedMinY = max;
            this.cachedMaxY = min;
            this.forgetRememberedSet.clear();
            this.handleChunkClamped(world);
         }

         ChunkPos chunkPos = mc.player.getChunkPos();
         this.chunkClampedPriorityQueueHel.handlePriorityQueue(chunkPos.x, chunkPos.z);
         if (this.scanTickCounter % 100 == 0) {
            this.handleChunkClamped(world);
         }

         int iIiLimit = this.scanBudget.getValue();
         int iIiCounter = 0;
         int iIiTotal = 0;

         Long long2;
         while (iIiCounter < iIiLimit && iIiTotal < 64 && (long2 = this.chunkClampedPriorityQueueHel.getPoll()) != null) {
            WorldChunk chunk = world.getChunkManager().getChunk(SpawnerTimeUtils.getValueForLong(long2), SpawnerTimeUtils.getValue(long2), ChunkStatus.FULL, false);
            if (chunk != null) {
               ChunkBlockUtils.getBlockStart(chunk, max, min, this.iIiResult);
               this.iIiRememberedSpawner.handle(long2, this.iIiResult.blockStartStringMap);
               this.forgetRememberedSet.add(long2);
               iIiCounter++;
               iIiTotal += this.iIiResult.iIiCount;
            }
         }

         this.handleRememberedSpawner(world);
         if (this.scanTickCounter % 100 == 0) {
            for (Spawner spawner : this.iIiRememberedSpawner.getItems(32)) {
               String formatLogicSpawn = ChunkBlockUtils.formatLogicSpawn(
                  world.getBlockEntity(new BlockPos(spawner.spawnerRememberedValueSpawne, spawner.spawnerRememberedValue, spawner.rememberedSpawnerValue))
               );
               if (formatLogicSpawn != null) {
                  this.iIiRememberedSpawner.canFlag(spawner.spawnerPositionKey, formatLogicSpawn);
               }
            }
         }

         this.updateLabels();
         if (this.iIiActive && System.currentTimeMillis() - this.timeMillis > 30000L) {
            this.iIiActive = false;
            this.timeMillis = System.currentTimeMillis();
            ModuleConfigAutosave.updateFlag2();
         }
      } else {
         this.updateLabels();
      }
   }

   private void updateLabels() {
      if (this.iIiRememberedSpawner.getUpdateRevision() != this.labelsValue) {
         this.labelsValue = this.iIiRememberedSpawner.getUpdateRevision();
         this.labelsItems = this.iIiRememberedSpawner.getFlag();
      }
   }

   @Override
   protected void onRender2D(TickProgress tickProgress) {
      if (this.labels.isEnabled()) {
         if (mc.world != null && mc.player != null && mc.currentScreen == null) {
            DrawContext drawContext = tickProgress.drawContext;
            MatrixStack matrixStack = new MatrixStack();
            CorzClient.fontManager.setMatrixStack(matrixStack);
            MatrixPeekUtilsRender.handleMatrixStack(matrixStack);
            double scaleFactor = mc.getWindow().getScaleFactor();
            int calculateScaleFactor = mc.getWindow().calculateScaleFactor(ShowHUD.getMax(mc), mc.forcesUnicodeFont());
            float spawnerFinderDivisor = (float)(calculateScaleFactor / scaleFactor);
            boolean spawnerFinderFlag = Math.abs(spawnerFinderDivisor - 1.0F) > 1.0E-4F;
            if (spawnerFinderFlag) {
               CorzRoundedUtils.scaledWidth = Math.round(mc.getWindow().getScaledWidth() / spawnerFinderDivisor);
               CorzRoundedUtils.scaledHeight = Math.round(mc.getWindow().getScaledHeight() / spawnerFinderDivisor);
            }

            VertexTexturePeek regular = VertexTexturePeek.getRegular();
            VertexTexturePeek vertexTexturePeek = regular.isCorzclientFlag() ? regular : VertexTexturePeek.getSuisse();

            try {
               if (vertexTexturePeek.isCorzclientFlag()) {
                  this.renderSpawnerRemembered(drawContext, vertexTexturePeek, spawnerFinderDivisor);
                  return;
               }
            } finally {
               if (spawnerFinderFlag) {
                  CorzRoundedUtils.scaledWidth = 0.0F;
                  CorzRoundedUtils.scaledHeight = 0.0F;
               }
            }
         }
      }
   }

   private void renderLabelsSpawner(DrawContext context, VertexTexturePeek vertexTexturePeek, SpawnerFinder.Label label, float labelsSpawnerValue, float labelsSpawnerValue2) {
      Spawner next = label.next;
      float clamp = MathHelper.clamp(1.12F - (float)label.spawnerAmount / 96.0F, 0.72F, 1.0F);
      float materialFactor = 18.0F * clamp;
      float labelsSpawnerValue4 = 7.0F * clamp;
      float corzclientValue = 11.0F * clamp;
      float corzLicenseLimit = 5.0F * clamp;
      String labelsSpawnerText = this.booleanBase.isEnabled() ? this.formatUnknown(next.getSpawnerText()) : "Spawner";
      String labelsSpawnerText2 = this.showDistance.isEnabled() ? "  " + Math.round(label.spawnerAmount) + "m" : "";
      String corzclientText = this.showCoordinates.isEnabled()
         ? "  " + next.spawnerRememberedValueSpawne + " " + next.spawnerRememberedValue + " " + next.rememberedSpawnerValue
         : "";
      String labelsSpawnerText4 = next.isLoadedRemembered() ? "" : "  remembered";
      float corzclientAmounts = vertexTexturePeek.getCorzclientAmounts(labelsSpawnerText, corzclientValue);
      float corzclientAmounts2 = vertexTexturePeek.getCorzclientAmounts(labelsSpawnerText2, corzclientValue);
      float corzclientAmounts3 = vertexTexturePeek.getCorzclientAmounts(corzclientText, corzclientValue);
      float corzclientAmounts4 = vertexTexturePeek.getCorzclientAmounts(labelsSpawnerText4, corzclientValue);
      float labelsSpawnerValue7 = labelsSpawnerValue4 + corzclientAmounts + corzclientAmounts2 + corzclientAmounts3 + corzclientAmounts4 + labelsSpawnerValue4;
      float matrixPeekValue = label.centerX - labelsSpawnerValue7 / 2.0F;
      float matrixStackValue = label.spawnerRememberedAmount - materialFactor;
      if (!(matrixPeekValue + labelsSpawnerValue7 + 16.0F < 0.0F) && !(matrixPeekValue - 16.0F > labelsSpawnerValue) && !(matrixStackValue + materialFactor + 16.0F < 0.0F) && !(matrixStackValue - 16.0F > labelsSpawnerValue2)) {
         float panelOverlayLimit = next.isLoadedRemembered() ? 1.0F : (float)this.doubleHelper.getValue();
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(panelOverlayLimit);

         try {
            MatrixPeekUtilsRender.renderForDrawContextFloat(context, matrixPeekValue, matrixStackValue, labelsSpawnerValue7, materialFactor, corzLicenseLimit, -233499369, 620756991);
            float labelsSpawnerValue11 = matrixPeekValue + labelsSpawnerValue4;
            float labelsSpawnerValue12 = matrixStackValue + (materialFactor - vertexTexturePeek.getAmountForFloat(corzclientValue)) / 2.0F;
            int index = 0;
            this.labelsTexts[index] = labelsSpawnerText;
            this.labelsAmounts[index] = labelsSpawnerValue11;
            this.labelsScanValues[index++] = this.getColorBase(next) | 0xFF000000;
            float labelsSpawnerTotal = labelsSpawnerValue11 + corzclientAmounts;
            if (!labelsSpawnerText2.isEmpty()) {
               this.labelsTexts[index] = labelsSpawnerText2;
               this.labelsAmounts[index] = labelsSpawnerTotal;
               this.labelsScanValues[index++] = -6645087;
               labelsSpawnerTotal += corzclientAmounts2;
            }

            if (!corzclientText.isEmpty()) {
               this.labelsTexts[index] = corzclientText;
               this.labelsAmounts[index] = labelsSpawnerTotal;
               this.labelsScanValues[index++] = -6645087;
               labelsSpawnerTotal += corzclientAmounts3;
            }

            if (!labelsSpawnerText4.isEmpty()) {
               this.labelsTexts[index] = labelsSpawnerText4;
               this.labelsAmounts[index] = labelsSpawnerTotal;
               this.labelsScanValues[index++] = -6645087;
            }

            for (int i = 0; i < index; i++) {
               this.labelsScanAmounts[i] = labelsSpawnerValue12;
            }

            vertexTexturePeek.handlePeekEnd(this.labelsTexts, this.labelsAmounts, this.labelsScanAmounts, this.labelsScanValues, index, corzclientValue);
         } finally {
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
         }
      }
   }

   @Override
   protected void onRender3D(QueueCommand queueCommand) {
      var labelsItems = this.labelsItems;
      if (!labelsItems.isEmpty() && mc.world != null && mc.player != null) {
         int indexOf = this.enumHelper.getCurrentIndex();
         boolean spawnerFinderFlag = indexOf == 1 || indexOf == 3;
         boolean spawnerFinderFlag2 = indexOf == 2 || indexOf == 3;
         boolean booleanValue = this.tracers.isEnabled();
         boolean enabled = this.spawnerFinderBooleanBase.isEnabled();
         if (spawnerFinderFlag || spawnerFinderFlag2 || booleanValue || enabled) {
            Camera camera = mc.gameRenderer.getCamera();
            Vec3d cameraPos = camera.getCameraPos();
            double spawnerFinderFactor = this.maxDistance.getValue();
            double spawnerFinderValue = spawnerFinderFactor * spawnerFinderFactor;
            float blockOverlayValue = (float)this.lineWidth.getValue();
            int spawnerFinderValue3 = this.fillAlpha.getValue();
            float spawnerFinderValue4 = (float)this.doubleHelper.getValue();
            float spawnerFinderValue5 = (float)this.beamHeight.getValue();
            float spawnerFinderFactor2 = 0.85F + 0.15F * (float)Math.sin((float)(System.currentTimeMillis() % 10000L) / 1000.0F * 3.0F);
            MatrixStack matrices = queueCommand.matrices;
            matrices.push();
            matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
            Matrix4f positionMatrix = matrices.peek().getPositionMatrix();
            VertexConsumer vertexConsumer = enabled ? VertexBuilderUtils.getBufferIMMEDIATE() : null;

            for (Spawner spawner : labelsItems) {
               double vectorDotValue = spawner.spawnerRememberedValueSpawne + 0.5;
               double spawnerFinderValue7 = spawner.spawnerRememberedValue + 0.5;
               double spawnerFinderValue8 = spawner.rememberedSpawnerValue + 0.5;
               double spawnerFinderFactor3 = vectorDotValue - cameraPos.x;
               double spawnerFinderFactor4 = spawnerFinderValue7 - cameraPos.y;
               double spawnerFinderFactor5 = spawnerFinderValue8 - cameraPos.z;
               if (!(spawnerFinderFactor3 * spawnerFinderFactor3 + spawnerFinderFactor4 * spawnerFinderFactor4 + spawnerFinderFactor5 * spawnerFinderFactor5 > spawnerFinderValue)) {
                  int clampValue = this.getColorBase(spawner) & 16777215;
                  float spawnerFinderFactor6 = spawner.isLoadedRemembered() ? 1.0F : spawnerFinderValue4;
                  float vertexBuilderValue = spawner.spawnerRememberedValueSpawne + 0.002F;
                  float y = spawner.spawnerRememberedValue + 0.002F;
                  float spawnerFinderValue12 = spawner.rememberedSpawnerValue + 0.002F;
                  float vertexColorValue = spawner.spawnerRememberedValueSpawne + 0.998F;
                  float f5 = spawner.spawnerRememberedValue + 0.998F;
                  float amountForDouble = spawner.rememberedSpawnerValue + 0.998F;
                  if (spawnerFinderFlag2) {
                     VertexBuilderUtils.handle(
                        positionMatrix,
                        vertexBuilderValue,
                        y,
                        spawnerFinderValue12,
                        vertexColorValue,
                        f5,
                        amountForDouble,
                        (clampValue >> 16 & 0xFF) / 255.0F,
                        (clampValue >> 8 & 0xFF) / 255.0F,
                        (clampValue & 0xFF) / 255.0F,
                        spawnerFinderValue3 / 255.0F * spawnerFinderFactor6
                     );
                  }

                  if (spawnerFinderFlag) {
                     VertexBuilderUtils.handle(positionMatrix, vertexBuilderValue, y, spawnerFinderValue12, vertexColorValue, f5, amountForDouble, getClamp(clampValue, 255.0F * spawnerFinderFactor6), blockOverlayValue);
                  }

                  if (booleanValue) {
                     VectorDotUtils.handle(vectorDotValue, spawnerFinderValue7, spawnerFinderValue8, getClamp(clampValue, 200.0F * spawnerFinderFactor6), Math.max(1.0F, blockOverlayValue * 0.6F));
                  }

                  if (vertexConsumer != null) {
                     double distance = Math.sqrt(spawnerFinderFactor3 * spawnerFinderFactor3 + spawnerFinderFactor5 * spawnerFinderFactor5);
                     float spawnerFinderFactor7 = distance > 0.001 ? (float)(spawnerFinderFactor5 / distance) : 1.0F;
                     float spawnerFinderFactor8 = distance > 0.001 ? (float)(-spawnerFinderFactor3 / distance) : 0.0F;
                     float spawnerFinderFactor9 = 0.14F;
                     float deltaY = spawner.spawnerRememberedValue + 1.0F;
                     int clamp = getClamp(clampValue, 140.0F * spawnerFinderFactor6 * spawnerFinderFactor2);
                     int valueInt = getClamp(clampValue, 0.0F);
                     VertexBuilderUtils.handleVertexColor(
                        vertexConsumer,
                        positionMatrix,
                        (float)vectorDotValue + spawnerFinderFactor7 * spawnerFinderFactor9,
                        deltaY,
                        (float)spawnerFinderValue8 + spawnerFinderFactor8 * spawnerFinderFactor9,
                        clamp,
                        (float)vectorDotValue - spawnerFinderFactor7 * spawnerFinderFactor9,
                        deltaY,
                        (float)spawnerFinderValue8 - spawnerFinderFactor8 * spawnerFinderFactor9,
                        clamp,
                        (float)vectorDotValue - spawnerFinderFactor7 * spawnerFinderFactor9,
                        deltaY + spawnerFinderValue5,
                        (float)spawnerFinderValue8 - spawnerFinderFactor8 * spawnerFinderFactor9,
                        valueInt,
                        (float)vectorDotValue + spawnerFinderFactor7 * spawnerFinderFactor9,
                        deltaY + spawnerFinderValue5,
                        (float)spawnerFinderValue8 + spawnerFinderFactor8 * spawnerFinderFactor9,
                        valueInt
                     );
                  }
               }
            }

            matrices.pop();
         }
      }
   }

   private static final class Label {
      float centerX;
      float spawnerRememberedAmount;
      double spawnerAmount;
      Spawner next;
   }
}
