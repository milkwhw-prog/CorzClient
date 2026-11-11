package com.corz.client.inventory;

import com.corz.client.gui.ClearEdit;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;

public class SearchEdit {
   private float thumbScrollOffset;
   private long itemFadeStartNanos;
   private static final int DEFAULT_STEP = 1;
   private int scaledMouseY;
   private int editValue;
   private boolean clearbtnSearchActive;
   private boolean switchSelectedFlag;
   private int editBoxX;
   private int editClusterValue;
   private boolean saveSavedFlag;
   private final MatrixStackUtilsAmount.Entrance thumbEntrance;
   private int selectedEditValue;
   private String selectedEditText;
   private int listSize;
   private Item displayedItem;
   private int thumbValue;
   private String editText;
   private long nanoTimeThumb;
   private final MatrixStackUtilsAmount.Hits savedHits;
   private long nanoTimeClick;
   private Item thumbItem;
   private final ClearEdit clearbtnSearchClearEdit;
   private String append;
   private double clusterAmount;
   private int thumbX;
   private String replace;
   private int editBoxY;
   private long clickValue;
   private final MatrixStackUtilsAmount.Anims savedAnims;
   private boolean clickActive;
   private boolean focusFlag;
   private int editSelectedValue;
   private long editFadeStartNanos;
   private String clickText;
   private String savedText;
   private final ItemList itemList;
   private int thumbY;
   private static final String OPEN = "open";
   private static final String EDIT = "Edit";
   private int savedValue;
   private int search;
   private final MatrixStackUtilsAmount.Anims editSelectedAnims = new MatrixStackUtilsAmount.Anims();
   private float editAmount;
   private int editSelectedIndex;
   private float clickAmount;
   private int selectedValue;
   private boolean editActive;
   private float thumbAmount;
   private int switchSelectedCount;
   private final Map<Item, SearchEdit.Fx> thumbFxMap;
   private boolean thumbActive;
   private static final String[] viewSelectedTexts = new String[]{"All", "Selected"};
   private final List<Item> thumbItemList;
   private long open;
   private float thumbBarAmount;
   private final Set<Item> thumbItemSet;
   private float thumbStartScroll;
   private static final String CLICK = "Click an item to add or remove it.";
   private double thumbStartTarget;
   private final Map<Item, ItemStack> stackItemStackMap;
   private float editSelectedAmount = -1.0F;
   private double clusterMouseX;
   private String clickHintText;
   private float replaceThumbAmount;
   private float thumbMaxScroll;
   private boolean openClusterFlag;
   private int scaledMouseValue;
   private long editHoverStartNanos;

   private void updateFocusTime() {
      this.focusFlag = true;
      this.clearbtnSearchClearEdit.updateTime();
   }

   private void renderMatrices(DrawContext context, ItemStack itemStack, int matricesValue, int configsLikeValue, float matricesValue3) {
      int editLimit = matricesValue + 3;
      int floorCtx = configsLikeValue + 3;
      int matricesBound = editLimit - 1;
      int matricesValue6 = floorCtx - 1;
      int matricesValue7 = editLimit + 16 + 1;
      int intersectsCenterBound = floorCtx + 16 + 1;
      int scissorDisableLimit = (int)Math.floor(matricesValue + 22 - 1 - 3.5F - matricesValue3 + 0.01F);
      int matricesLimit = (int)Math.ceil(configsLikeValue + 1 + 3.5F + matricesValue3 - 0.01F);
      if (!(matricesValue3 <= 0.0F) && scissorDisableLimit < editLimit + 16 && matricesLimit > floorCtx) {
         int matricesValue10 = Math.abs(context.getMatrices().m00() - 1.0F) > 1.0E-4F ? 2 : 0;
         this.renderMaxFlag(context, itemStack, editLimit, floorCtx, matricesBound, matricesValue6, scissorDisableLimit, intersectsCenterBound);
         this.renderMaxFlag(context, itemStack, editLimit, floorCtx, Math.max(matricesBound, scissorDisableLimit - matricesValue10), matricesLimit, matricesValue7, intersectsCenterBound);
      } else {
         this.renderMaxFlag(context, itemStack, editLimit, floorCtx, matricesBound, matricesValue6, matricesValue7, intersectsCenterBound);
      }
   }

   private static float getAmount(float editLimit, float editLimit2, float editValue) {
      return editLimit < editLimit2 ? editLimit2 : (editLimit > editValue ? editValue : editLimit);
   }

   public int getValue() {
      return 32;
   }

   private float getTime(long editValue) {
      if (this.displayedItem == null) {
         return 0.0F;
      } else {
         float editFactor = MatrixStackUtilsAmount.getAmountForFloat((float)(editValue - this.itemFadeStartNanos) / 1.5E8F / 0.7F);
         if (this.editFadeStartNanos == 0L) {
            return editFactor;
         } else {
            float editFactor2 = 1.0F - MatrixStackUtilsAmount.getMax((float)(editValue - this.editFadeStartNanos) / 9.0E7F);
            if (editFactor2 <= 0.0F) {
               this.displayedItem = null;
               this.editFadeStartNanos = 0L;
               return 0.0F;
            } else {
               return editFactor * editFactor2;
            }
         }
      }
   }

   private void renderThumbAmount(DrawContext context, int thumbValue, int value, int downloadCommunityValue, int matrixStackLimit, int mouseY, long nanoTime) {
      int itemsSize = this.thumbItemList.size();
      int thumbDivisor = (itemsSize + 12 - 1) / 12;
      float amount = Math.max(0, thumbDivisor - 7) * 24;
      this.thumbMaxScroll = amount;
      this.editAmount = getAmount(this.editAmount, 0.0F, amount);
      float thumbAmount = this.thumbActive ? this.editAmount : getAmount(this.editAmount);
      this.thumbAmount = (float)MatrixIdentifierUtils.getAmount(this.thumbAmount, thumbAmount, this.thumbActive ? 40.0 : 18.0, MatrixClearUtils.getAmount());
      if (Math.abs(this.thumbAmount - thumbAmount) < 0.05F) {
         this.thumbAmount = thumbAmount;
      }

      this.thumbAmount = getAmount(this.thumbAmount, 0.0F, amount);
      boolean thumbFlag = this.thumbAmount == thumbAmount && !this.thumbActive;
      if (!thumbFlag) {
         this.nanoTimeThumb = nanoTime;
      }

      float hashRegistryValue = MatrixStackUtilsAmount.getAmount(this.thumbAmount);
      int floor5 = (int)Math.floor(hashRegistryValue);
      float deltaVar = hashRegistryValue - floor5;
      this.thumbScrollOffset = hashRegistryValue;
      this.thumbX = value;
      this.thumbY = downloadCommunityValue;
      if (itemsSize == 0) {
         this.thumbBarAmount = 0.0F;
         this.handleTimeText(null, -1, thumbValue, value, downloadCommunityValue, floor5, nanoTime);
         boolean empty2 = !this.replace.isEmpty();
         if (!empty2 && this.search != 0) {
            MatrixStackUtilsAmount.renderMatricesPop(
               context,
               MatrixStackUtilsAmount.Hits.INSTANCE,
               this.savedAnims,
               "m:nosel",
               value,
               downloadCommunityValue,
               286.0F,
               166.0F,
               IcoGenUtils.getIdentifierGEN_CORZCLIENT_MAP("search"),
               "No items selected",
               "Switch to All and click items to add them.",
               null,
               matrixStackLimit,
               mouseY,
               null
            );
         } else {
            MatrixStackUtilsAmount.renderMatricesPop(
               context,
               MatrixStackUtilsAmount.Hits.INSTANCE,
               this.savedAnims,
               "m:none",
               value,
               downloadCommunityValue,
               286.0F,
               166.0F,
               IcoGenUtils.getIdentifierGEN_CORZCLIENT_MAP("search"),
               "No items match",
               this.search == 1 ? "None of your selected items match." : "Try a different name.",
               null,
               matrixStackLimit,
               mouseY,
               null
            );
         }
      } else {
         boolean thumbFlag3 = !this.switchSelectedFlag && MatrixStackUtilsAmount.canFlag(matrixStackLimit, mouseY, value - 1.0F, downloadCommunityValue, 288.0F, 166.0F);
         int index = thumbFlag3 && !this.thumbActive ? getValue(matrixStackLimit, mouseY, value, downloadCommunityValue, hashRegistryValue, itemsSize) : -1;
         boolean thumbFlag4 = thumbFlag && !this.thumbEntrance.isTime();
         this.handleTimeText(index >= 0 && thumbFlag4 ? this.thumbItemList.get(index) : null, index, thumbValue, value, downloadCommunityValue, floor5, nanoTime);
         int max = Math.max(0, floor5 / 24);
         int thumbLimit = Math.min(thumbDivisor - 1, max + 7);
         this.thumbValue = max;
         float thumbValue6 = downloadCommunityValue - 1;
         float thumbValue7 = downloadCommunityValue + 166 + 1;
         boolean thumbFlag5 = deltaVar > 0.0F;
         context.enableScissor(value - 2, downloadCommunityValue - 1, value + 286 + 2, downloadCommunityValue + 166 + 1);
         if (thumbFlag5) {
            MatrixPeekUtilsRender.getMatrixStack().push();
            MatrixPeekUtilsRender.getMatrixStack().translate(0.0F, -deltaVar, 0.0F);
            context.getMatrices().pushMatrix();
            context.getMatrices().translate(0.0F, -deltaVar);
         }

         try {
            for (int i = max; i <= thumbLimit; i++) {
               int configsLikeValue = downloadCommunityValue + i * 24 - floor5;
               float deltaVar2 = configsLikeValue - deltaVar;
               float min2 = Math.min(deltaVar2 + 22.0F - thumbValue6, thumbValue7 - deltaVar2);
               if (!(min2 <= 0.0F)) {
                  float panelOverlay = CorzRoundedUtils.getPanelOverlay(min2 >= 22.0F ? 1.0F : min2 / 22.0F);

                  try {
                     int thumbValue9 = i * 12;
                     int min3 = Math.min(itemsSize, thumbValue9 + 12);

                     for (int j = thumbValue9; j < min3; j++) {
                        int deltaVar3 = j - thumbValue9;
                        this.renderAmountTime(context, this.thumbItemList.get(j), value + deltaVar3 * 24, configsLikeValue, i - max + (deltaVar3 >> 2), j == index, nanoTime);
                     }
                  } finally {
                     CorzRoundedUtils.handlePanelOverlay(panelOverlay);
                  }
               }
            }
         } finally {
            if (thumbFlag5) {
               context.getMatrices().popMatrix();
               MatrixPeekUtilsRender.getMatrixStack().pop();
            }

            context.disableScissor();
         }

         if (!this.thumbFxMap.isEmpty()) {
            Iterator iterator = this.thumbFxMap.values().iterator();

            while (iterator.hasNext()) {
               SearchEdit.Fx fx = (SearchEdit.Fx)iterator.next();
               if (fx.switchSelectedSnapshot != this.switchSelectedCount || !fx.thumbActive) {
                  iterator.remove();
               }
            }
         }

         this.savedHits.handle(value - 1.0F, downloadCommunityValue, 288.0F, 166.0F, this::updateAmountTime);
         this.renderThumbAmount(context, value, downloadCommunityValue, thumbDivisor, amount, thumbFlag, thumbFlag3, matrixStackLimit, mouseY, nanoTime);
      }
   }

   public void handleAmountThumb(double thumbValue, double thumbValue2, int button) {
      if (this.openClusterFlag && !this.switchSelectedFlag && this.thumbActive) {
         float thumbDivisor = 166.0F - this.thumbBarAmount;
         if (!(thumbDivisor <= 0.0F) && !(this.thumbMaxScroll <= 0.0F)) {
            float thumbFactor = (float)(this.getAmountForDouble(thumbValue2) - this.thumbStartTarget) / thumbDivisor;
            this.editAmount = getAmount(this.thumbStartScroll + thumbFactor * this.thumbMaxScroll, 0.0F, this.thumbMaxScroll);
         }
      }
   }

   public void renderEditSelected(DrawContext context, int editSelectedValue, int mouseY) {
      VertexTextureFont fontRenderer = MatrixStackUtilsAmount.getFontRenderer();
      List list = this.itemList.getContainsItemList();
      int selectedValue = list.size();
      if (selectedValue != this.selectedValue) {
         this.selectedValue = selectedValue;
         this.selectedEditText = selectedValue == 0 ? "No items selected" : (selectedValue == 1 ? "1 item selected" : selectedValue + " items selected");
      }

      int selectedEditValueOffset = this.selectedEditValue + 4;
      float editSelectedValue3 = this.selectedEditValue + this.editSelectedValue - 4;
      float max4 = Math.max(40.0F, (float)Math.ceil(MatrixStackUtilsAmount.getAmount3("Edit")) + 18.0F);
      float deltaVar = editSelectedValue3 - max4;
      float editCluster = this.getEditCluster();
      String formatText = fontRenderer.formatText(this.itemList.getSettingName(), deltaVar - 8.0F - selectedEditValueOffset);
      fontRenderer.handleForStringFloat(formatText, selectedEditValueOffset, MatrixStackUtilsAmount.getAmount(this.editClusterValue + 5.0F, 11.0F, 1.0F), new Color(-3947581));
      MatrixStackUtilsAmount.handlePushScale(
         this.selectedEditText, selectedEditValueOffset, MatrixStackUtilsAmount.getAmount(this.editClusterValue + 16.0F, 11.0F, 0.82F), 0.82F, -7630437
      );
      float editSelectedValue5 = selectedEditValueOffset + Math.max(MatrixStackUtilsAmount.getAmount3(formatText), MatrixStackUtilsAmount.getAmount(this.selectedEditText, 0.82F));
      int floor6 = (int)Math.floor(deltaVar - 6.0F - editSelectedValue5 - 8.0F);
      int editSelectedLimit = Math.max(0, Math.min(Math.min(selectedValue, 4), (floor6 + 2) / 18));
      int editSelectedValue7 = (int)Math.floor(deltaVar) - 6 - (editSelectedLimit * 18 - 2);
      float matrixStackValue = editSelectedLimit == 0 ? deltaVar : editSelectedValue7;
      this.editSelectedAmount = matrixStackValue - this.selectedEditValue;
      boolean editSelectedFlag = !this.openClusterFlag && this.open == 0L && MatrixStackUtilsAmount.canFlag(editSelectedValue, mouseY, matrixStackValue, editCluster - 1.0F, editSelectedValue3 - matrixStackValue, 20.0F);
      float editSelectedValue9 = this.editSelectedAnims.getAmount("cluster", editSelectedFlag);
      MatrixStackUtilsAmount.getAmount(
         context, MatrixStackUtilsAmount.Hits.INSTANCE, this.editSelectedAnims, "open", "Edit", editSelectedValue3, editCluster, 18.0F, 40.0F, -4000, -4000, false, editSelectedValue9, null
      );
      if (!this.openClusterFlag) {
         int editClusterValueOffset = this.editClusterValue + 8;

         for (int i = 0; i < editSelectedLimit; i++) {
            DrawMatricesUtils.render(context, this.getItemStack((Item)list.get(i)), editSelectedValue7 + i * 18, editClusterValueOffset);
         }
      }

      if (this.open != 0L) {
         long editSelectedLimit2 = System.nanoTime() - this.open;
         if (editSelectedLimit2 >= 70000000L) {
            this.open = 0L;
            if (editSelectedLimit2 <= 400000000L) {
               this.updateClickTime();
            }
         }
      }
   }

   private ItemStack getItemStack(Item botCrystalText) {
      ItemStack itemStack = this.stackItemStackMap.get(botCrystalText);
      if (itemStack == null) {
         itemStack = new ItemStack(botCrystalText);
         this.stackItemStackMap.put(botCrystalText, itemStack);
      }

      return itemStack;
   }

   private void updateTime() {
      this.updateReplaceThumb();
      this.thumbEntrance.updateTimeFlagEntranceValue();
      this.clearbtnSearchClearEdit.updateTime();
   }

   private void updateClickTime() {
      this.openClusterFlag = true;
      this.switchSelectedFlag = false;
      this.saveSavedFlag = false;
      this.nanoTimeClick = System.nanoTime();
      this.clickAmount = 1.0F;
      this.append = "";
      this.focusFlag = true;
      this.clearbtnSearchClearEdit.updateTime();
      this.clearbtnSearchActive = true;
      this.search = 0;
      this.thumbActive = false;
      this.nanoTimeThumb = this.nanoTimeClick - 900000000L;
      this.savedValue = -1;
      this.listSize = -1;
      this.displayedItem = null;
      this.clickActive = false;
      this.clickValue = this.nanoTimeClick - 450000000L;
      this.thumbFxMap.clear();
      this.savedHits.update();
      this.savedAnims.handle(clickText -> false);
      this.clickText = "Select " + this.itemList.getSettingName().toLowerCase(Locale.ROOT);
      String description = this.itemList.getDescription();
      this.clickHintText = MatrixStackUtilsAmount.getFontRenderer()
         .formatText(description != null && !description.isBlank() ? description : "Click an item to add or remove it.", 348.7805F);
      this.updateReplaceThumb();
      this.thumbEntrance.updateTimeFlag();
   }

   private static float getAmount(float editValue) {
      return Math.round(editValue / 24.0F) * 24.0F;
   }

   private double getAmount(double editValue) {
      double scaledWidthOffset = getScaledWidth() + 155.0;
      return scaledWidthOffset + (editValue - scaledWidthOffset) / this.clickAmount;
   }

   private float getSwitchSelected() {
      float switchSelectedDivisor = (this.switchSelectedFlag ? 130.0F : 240.0F) * 1000000.0F;
      return (float)(System.nanoTime() - this.nanoTimeClick) / switchSelectedDivisor;
   }

   private double getAmountForDouble(double editValue) {
      double scaledHeightOffset = getScaledHeight() + 140.0;
      return scaledHeightOffset + (editValue - scaledHeightOffset) / this.clickAmount;
   }

   public boolean isClusterOpen() {
      this.isSwitchSelected();
      return this.openClusterFlag;
   }

   private void renderAmountTime(DrawContext context, Item goodsSlot, int editValue, int configsLikeValue, int entranceBound, boolean editFlag, long time) {
      MatrixStackUtilsAmount.Entrance amountFlag = this.thumbEntrance.getAmountFlag(entranceBound);
      if (!amountFlag.isAmount()) {
         float tabsSettingsAmount = amountFlag.tabsSettingsAmount;
         float editLimit = amountFlag.entranceProgress;
         float matrixScissorAmount = amountFlag.matrixScissorAmount;
         boolean editFlag2 = editFlag && amountFlag.activeFlag;
         boolean contains = this.thumbItemSet.contains(goodsSlot);
         SearchEdit.Fx fx = this.thumbFxMap.isEmpty() ? null : this.thumbFxMap.get(goodsSlot);
         if (fx == null && editFlag2) {
            fx = this.getAmountFlag(goodsSlot, contains);
         }

         float expandProgress4 = 0.0F;
         float cardMatrixFactor = contains ? 1.0F : 0.0F;
         float cardMatrixFactor5 = cardMatrixFactor;
         if (fx != null) {
            fx.switchSelectedSnapshot = this.switchSelectedCount;
            fx.expandProgress = (float)MatrixIdentifierUtils.getAmount(fx.expandProgress, editFlag2 ? 1.0 : 0.0, editFlag2 ? 16.0 : 11.0, MatrixClearUtils.getAmount());
            if (!editFlag2 && fx.expandProgress < 0.004F) {
               fx.expandProgress = 0.0F;
            }

            if (fx.contains != contains) {
               fx.contains = contains;
               fx.prevHoverProgress = fx.hoverProgress;
               fx.hoverStartNanos = time;
            }

            float matrixStackValue = fx.hoverStartNanos == 0L ? 1.0F : MatrixStackUtilsAmount.getMax((float)(time - fx.hoverStartNanos) / 1.9E8F);
            fx.hoverProgress = fx.prevHoverProgress + ((contains ? 1.0F : 0.0F) - fx.prevHoverProgress) * MatrixStackUtilsAmount.getAmountForFloat(matrixStackValue);
            expandProgress4 = fx.expandProgress;
            cardMatrixFactor = fx.hoverProgress;
            cardMatrixFactor5 = contains ? fx.prevHoverProgress + (1.0F - fx.prevHoverProgress) * MatrixStackUtilsAmount.getAmount(matrixStackValue, 2.4F) : cardMatrixFactor;
            if (fx.animStartNanos != 0L) {
               float editLimit3 = (float)(time - fx.animStartNanos) / 1.8E8F;
               if (editLimit3 >= 1.0F) {
                  fx.animStartNanos = 0L;
               } else if (editLimit3 > 0.0F) {
                  matrixScissorAmount *= 1.0F - 0.08F * getAmountForFloat(editLimit3);
               }
            }

            fx.thumbActive = editFlag2 || fx.expandProgress > 0.0F || matrixStackValue < 1.0F || fx.animStartNanos != 0L;
         }

         boolean translatePush = MatrixStackUtilsAmount.canTranslatePush(context, editValue + 11.0F, configsLikeValue + 11.0F, matrixScissorAmount);

         try {
            if (tabsSettingsAmount > 0.004F) {
               MatrixPeekUtilsRender.renderMatrixPeek(context, editValue, configsLikeValue, 22.0F, 22.0F, 5.0F, MatrixStackUtilsAmount.getValue(-1, 0.1F * tabsSettingsAmount));
            }

            if (editLimit > 0.002F) {
               float panelOverlay = CorzRoundedUtils.getPanelOverlay(editLimit);

               try {
                  int editValue7 = CardMatrixUtils.getValue(
                     MatrixStackUtilsAmount.getValue(-1, 0.04F + 0.05F * expandProgress4), CardMatrixUtils.getReleaseRight(0.13 + 0.05 * expandProgress4), cardMatrixFactor
                  );
                  MatrixPeekUtilsRender.renderMatrixPeek(context, editValue, configsLikeValue, 22.0F, 22.0F, 5.0F, editValue7);
                  boolean editFlag3 = cardMatrixFactor > 0.01F;
                  if (editFlag3) {
                     MatrixPeekUtilsRender.renderMatrixPeek(
                        context, editValue, configsLikeValue, 22.0F, 22.0F, 5.0F, MatrixPeekUtilsRender.getAmount(), CardMatrixUtils.getReleaseRight(0.85 * cardMatrixFactor)
                     );
                  }

                  this.renderMatrices(context, this.getItemStack(goodsSlot), editValue, configsLikeValue, editFlag3 ? 7.0F * getAmountFloat(cardMatrixFactor5) / 2.0F : 0.0F);
                  if (editFlag3) {
                     this.renderReleaseRight(context, editValue, configsLikeValue, cardMatrixFactor, cardMatrixFactor5);
                  }
               } finally {
                  CorzRoundedUtils.handlePanelOverlay(panelOverlay);
               }
            }
         } finally {
            MatrixStackUtilsAmount.renderPopMatrices(context, translatePush);
         }
      }
   }

   private void updateAppend() {
      if (!this.append.isEmpty()) {
         this.append = "";
         this.updateFocusTime();
         this.updateTime();
      }
   }

   private void updateThumbFlag() {
      this.openClusterFlag = false;
      this.switchSelectedFlag = false;
      this.saveSavedFlag = false;
      this.clickAmount = 1.0F;
      this.append = "";
      this.replace = "";
      this.focusFlag = false;
      this.editAmount = 0.0F;
      this.thumbAmount = 0.0F;
      this.replaceThumbAmount = 0.0F;
      this.thumbItem = null;
      this.displayedItem = null;
      this.clickActive = false;
      this.thumbItemList.clear();
      this.thumbItemSet.clear();
      this.listSize = -1;
      this.stackItemStackMap.clear();
      this.thumbFxMap.clear();
      this.savedHits.update();
   }

   private static int getValue(double editValue, double mouseY, int editValue3, int downloadCommunityValue, float hashRegistryValue, int editLimit) {
      int editBound = (int)Math.floor((editValue - (editValue3 - 1)) / 24.0);
      int floor6 = (int)Math.floor((mouseY - (downloadCommunityValue - 1) + hashRegistryValue) / 24.0);
      if (floor6 < 0) {
         return -1;
      } else {
         int max7 = Math.max(0, Math.min(11, editBound));
         int editValue8 = floor6 * 12 + max7;
         return editValue8 < editLimit ? editValue8 : -1;
      }
   }

   private void renderThumbAmount(DrawContext context, int thumbValue, int downloadCommunityValue, int thumbDivisor, float thumbLimit, boolean thumbFlag, boolean thumbFlag2, int matrixStackLimit, int scaledY, long nanoTime) {
      float matrixPeekValue = thumbValue + 286 + 4;
      boolean animsFlag = this.thumbActive || MatrixStackUtilsAmount.canFlag(matrixStackLimit, scaledY, matrixPeekValue - 3.0F, downloadCommunityValue, 8.0F, 166.0F);
      boolean thumbFlag4 = thumbLimit > 0.0F && (animsFlag || thumbFlag2 || nanoTime - this.nanoTimeThumb < 900000000L);
      float panelOverlayLimit = this.savedAnims.getAmount("m:bar", 0.0F, thumbFlag4 ? 1.0F : 0.0F, thumbFlag4 ? 16.0F : 8.0F);
      if (thumbLimit <= 0.0F) {
         this.thumbBarAmount = 0.0F;
         this.replaceThumbAmount = 0.0F;
      } else {
         if (this.thumbActive) {
            this.replaceThumbAmount = this.editAmount - this.thumbAmount;
         } else {
            this.replaceThumbAmount = (float)MatrixIdentifierUtils.getAmount(this.replaceThumbAmount, 0.0, 24.0, MatrixClearUtils.getAmount());
            if (Math.abs(this.replaceThumbAmount) < 0.05F) {
               this.replaceThumbAmount = 0.0F;
            }
         }

         float thumbBarAmount = MatrixStackUtilsAmount.getAmount(Math.max(18.0F, 1162.0F / thumbDivisor));
         float matrixStackValue = downloadCommunityValue + (166.0F - thumbBarAmount) * (getAmount(this.thumbAmount + this.replaceThumbAmount, 0.0F, thumbLimit) / thumbLimit);
         if (thumbFlag && this.replaceThumbAmount == 0.0F) {
            matrixStackValue = MatrixStackUtilsAmount.getAmount(matrixStackValue);
         }

         this.thumbBarAmount = thumbBarAmount;
         float cardMatrixFactor = this.savedAnims.getAmount("m:thumb", animsFlag);
         if (panelOverlayLimit > 0.01F) {
            float panelOverlay = CorzRoundedUtils.getPanelOverlay(panelOverlayLimit);

            try {
               MatrixPeekUtilsRender.renderMatrixPeek(context, matrixPeekValue, downloadCommunityValue, 2.0F, 166.0F, 1.0F, MatrixStackUtilsAmount.getValue(-1, 0.05F));
               MatrixPeekUtilsRender.renderMatrixPeek(
                  context,
                  matrixPeekValue,
                  matrixStackValue,
                  2.0F,
                  thumbBarAmount,
                  1.0F,
                  CardMatrixUtils.getValue(
                     MatrixStackUtilsAmount.getValue(-1, 0.24F), MatrixStackUtilsAmount.getValue(MatrixEspUtils.releaseRightValue, 0.92F), cardMatrixFactor
                  )
               );
            } finally {
               CorzRoundedUtils.handlePanelOverlay(panelOverlay);
            }
         }

         this.savedHits.handle(matrixPeekValue - 3.0F, downloadCommunityValue, 8.0F, 166.0F, this::updateAmountThumb);
      }
   }

   public void handleAmountFlag(double editValue, double mouseReleasedValue, int button) {
      if (this.thumbActive) {
         this.thumbActive = false;
         this.editAmount = getAmount(this.editAmount);
      }
   }

   private float getEditCluster() {
      return this.editClusterValue + 7.0F;
   }

   private boolean canEditSelected(double editSelectedValue, double scaledY) {
      float editSelectedValue3 = this.selectedEditValue + this.editSelectedValue - 4;
      float matrixStackValue = this.editSelectedAmount >= 0.0F ? this.selectedEditValue + this.editSelectedAmount : editSelectedValue3 - 40.0F;
      return MatrixStackUtilsAmount.canFlag(editSelectedValue, scaledY, matrixStackValue, this.getEditCluster() - 1.0F, editSelectedValue3 - matrixStackValue, 20.0F);
   }

   public boolean canOpenCluster(double clusterValue, double clusterValue2, int button) {
      if (this.isSwitchSelected()) {
         return true;
      } else if (button != 0) {
         return this.openClusterFlag;
      } else if (!this.openClusterFlag) {
         if (this.isOpen()) {
            return true;
         } else if (!this.canEditSelected(clusterValue, clusterValue2)) {
            return false;
         } else {
            this.editSelectedAnims.handleTimeForString("open");
            this.open = System.nanoTime();
            return true;
         }
      } else {
         double amount = this.getAmount(clusterValue);
         double scaledY = this.getAmountForDouble(clusterValue2);
         if (!MatrixStackUtilsAmount.canFlag(amount, scaledY, getScaledWidth(), getScaledHeight(), 310.0F, 280.0F)) {
            this.updateSwitchFlag();
            return true;
         } else {
            this.clusterMouseX = amount;
            this.clusterAmount = scaledY;
            this.savedHits.canAnimsPress(amount, scaledY);
            return true;
         }
      }
   }

   private void renderSaveSaved(DrawContext context, int savedValue, int scaledHeight) {
      VertexTextureFont fontRenderer = MatrixStackUtilsAmount.getFontRenderer();
      long nanoTime = System.nanoTime();
      int matrixStackLimit = this.switchSelectedFlag ? -4000 : this.scaledMouseValue;
      int scaledY = this.scaledMouseY;
      int topOffset = savedValue + 12;
      int savedValue5 = topOffset + 286;
      this.update();
      int value = this.itemList.getContainsItemList().size();
      if (value != this.savedValue) {
         if (this.savedValue >= 0) {
            this.savedAnims.handleTime("m:count");
         }

         this.savedValue = value;
         this.savedText = value + " selected";
      }

      int ctxOffset = scaledHeight + 12;
      float matrixStackValue = savedValue5 - 16;
      float savedValue7 = MatrixStackUtilsAmount.getAmount(this.savedText);
      String formatText = fontRenderer.formatText(this.clickText, (matrixStackValue - 8.0F - savedValue7 - 7.0F - topOffset) / 1.22F);
      float amountForDrawContextString = MatrixStackUtilsAmount.getAmountForDrawContextString(context, formatText, topOffset, ctxOffset, 16.0F);
      float savedValue9 = MatrixStackUtilsAmount.getAmount(amountForDrawContextString + 7.0F);
      float cardOffset = ctxOffset + 2.5F;
      int savedValue11 = CardMatrixUtils.getValue(
         -7630437, MatrixStackUtilsAmount.getValue(MatrixEspUtils.releaseRightValue, 1.0F), this.savedAnims.getAmount("m:has", value > 0 ? 1.0F : 0.0F, 14.0F)
      );
      boolean translatePush = MatrixStackUtilsAmount.canTranslatePush(context, savedValue9 + savedValue7 / 2.0F, cardOffset + 5.5F, this.savedAnims.getTime("m:count", 0.0F));
      MatrixStackUtilsAmount.getAmount(context, this.savedText, savedValue9, cardOffset, savedValue11);
      MatrixStackUtilsAmount.renderPopMatrices(context, translatePush);
      MatrixStackUtilsAmount.render(
         context, this.savedHits, this.savedAnims, "m:x", IcoGenUtils.getCrossIco(), matrixStackValue, ctxOffset + 0.0F, 16.0F, 10.0F, matrixStackLimit, scaledY, false, this::updateSwitchFlag
      );
      MatrixStackUtilsAmount.handlePushScale(this.clickHintText, topOffset, MatrixStackUtilsAmount.getAmount(ctxOffset + 16, 10.0F, 0.82F), 0.82F, -7630437);
      this.renderClearbtnSearch(context, topOffset, scaledHeight + 46, savedValue5, matrixStackLimit, scaledY);
      this.thumbEntrance.isTimeFlag();
      this.renderThumbAmount(context, savedValue, topOffset, scaledHeight + 75, matrixStackLimit, scaledY, nanoTime);
      int targetOffset = scaledHeight + 250;
      MatrixStackUtilsAmount.getAmount(context, this.savedHits, this.savedAnims, "m:reset", "Reset", topOffset, targetOffset, 18.0F, matrixStackLimit, scaledY, true, 0.0F, this::updateItemList);
      float releaseRightBound = (float)Math.ceil(MatrixStackUtilsAmount.getAmount3("Saved")) + 29.0F;
      float savedValue13 = MatrixStackUtilsAmount.getAmount(
         context,
         this.savedHits,
         this.savedAnims,
         "m:save",
         this.saveSavedFlag ? "Saved" : "Save",
         IcoGenUtils.getIcoCheck(),
         savedValue5,
         targetOffset,
         18.0F,
         releaseRightBound,
         matrixStackLimit,
         scaledY,
         true,
         this::updateSaveSaved
      );
      MatrixStackUtilsAmount.getAmount(
         context, this.savedHits, this.savedAnims, "m:cancel", "Cancel", savedValue13 - 6.0F, targetOffset, 18.0F, matrixStackLimit, scaledY, false, this::updateSwitchFlag
      );
      this.renderMaxText(context, nanoTime);
   }

   private void handleFlagMax(int editValue, int editValue2, int editValue3, int editLimit, int editValue4) {
      int editValue5 = editValue3 + editValue % 12 * 24;
      int editValue6 = editLimit + editValue / 12 * 24 - editValue4;
      this.editBoxX = Math.max(editValue2 + 6, Math.min(editValue2 + 310 - 6 - this.editValue, editValue5 + 11 - this.editValue / 2));
      this.editActive = editValue6 - 3 - 13 < editLimit;
      this.editBoxY = this.editActive ? editValue6 + 22 + 3 : editValue6 - 3 - 13;
   }

   private static int getScaledHeight() {
      return (MinecraftClient.getInstance().getWindow().getScaledHeight() - 280) / 2;
   }

   private SearchEdit.Fx getAmountFlag(Item goodsSlot, boolean contains) {
      SearchEdit.Fx amountFlag = new SearchEdit.Fx();
      amountFlag.contains = contains;
      amountFlag.hoverProgress = contains ? 1.0F : 0.0F;
      amountFlag.prevHoverProgress = amountFlag.hoverProgress;
      amountFlag.switchSelectedSnapshot = this.switchSelectedCount;
      amountFlag.thumbActive = true;
      this.thumbFxMap.put(goodsSlot, amountFlag);
      return amountFlag;
   }

   private void update() {
      List list = this.itemList.getContainsItemList();
      if (list.size() != this.listSize) {
         this.thumbItemSet.clear();
         this.thumbItemSet.addAll(list);
         this.listSize = list.size();
      }
   }

   private void handleTimeText(Item item, int editValue, int editValue2, int editValue3, int downloadCommunityValue, int editValue5, long time) {
      if (item != this.thumbItem) {
         this.thumbItem = item;
         this.editHoverStartNanos = time;
      }

      long editValue6 = time - this.clickValue < 450000000L ? 0L : 380000000L;
      boolean editFlag = this.thumbItem != null && time - this.editHoverStartNanos >= editValue6;
      if (editFlag) {
         if (this.displayedItem != this.thumbItem) {
            this.displayedItem = this.thumbItem;
            this.editText = MatrixStackUtilsAmount.getFontRenderer().formatText(this.getItemStack(this.displayedItem).getName().getString(), 348.7805F);
            this.editValue = (int)Math.ceil(MatrixStackUtilsAmount.getAmount(this.editText, 0.82F)) + 12;
            this.itemFadeStartNanos = time;
            this.editFadeStartNanos = 0L;
         } else if (this.editFadeStartNanos != 0L) {
            this.itemFadeStartNanos = time;
            this.editFadeStartNanos = 0L;
         }

         this.handleFlagMax(editValue, editValue2, editValue3, downloadCommunityValue, editValue5);
      } else if (this.displayedItem != null && this.editFadeStartNanos == 0L) {
         this.editFadeStartNanos = time;
      }

      float editLimit = this.getTime(time);
      this.clickActive = this.displayedItem != null && editLimit > 0.02F;
      if (editLimit > 0.5F) {
         this.clickValue = time;
      }
   }

   private void renderMaxText(DrawContext context, long editValue) {
      float panelOverlayLimit = this.getTime(editValue);
      if (this.displayedItem != null && !(panelOverlayLimit <= 0.01F)) {
         float x2 = 0.92F + 0.08F * MatrixStackUtilsAmount.getAmount((float)(editValue - this.itemFadeStartNanos) / 1.5E8F, 1.8F);
         boolean translatePush = MatrixStackUtilsAmount.canTranslatePush(context, this.editBoxX + this.editValue / 2.0F, this.editActive ? this.editBoxY : this.editBoxY + 13, x2);
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(panelOverlayLimit);

         try {
            MatrixPeekUtilsRender.renderMatrixPeekForDrawContextFloat(context, this.editBoxX + 1.0F, this.editBoxY + 2.0F, this.editValue - 2.0F, 12.0F, 5.0F, 1241513984, 6.0F, 1.0F);
            MatrixPeekUtilsRender.renderForDrawContextFloat(context, this.editBoxX, this.editBoxY, this.editValue, 13.0F, 5.0F, -15394785, 352321535);
            MatrixStackUtilsAmount.handlePushScale(this.editText, this.editBoxX + 6.0F, MatrixStackUtilsAmount.getAmount(this.editBoxY, 13.0F, 0.82F), 0.82F, -920587);
         } finally {
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
            MatrixStackUtilsAmount.renderPopMatrices(context, translatePush);
         }
      }
   }

   private static int getScaledWidth() {
      return (MinecraftClient.getInstance().getWindow().getScaledWidth() - 310) / 2;
   }

   private static String formatText(String editText, float editValue) {
      VertexTextureFont fontRenderer = MatrixStackUtilsAmount.getFontRenderer();
      if (editValue <= 0.0F) {
         return "";
      } else {
         String formatText = editText;

         while (formatText.length() > 1 && fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont(formatText) > editValue) {
            formatText = formatText.substring(1);
         }

         return formatText;
      }
   }

   private static float getAmountForFloat(float editValue) {
      return editValue < 0.3F ? MatrixStackUtilsAmount.getAmountForFloat(editValue / 0.3F) : 1.0F - MatrixStackUtilsAmount.getAmount((editValue - 0.3F) / 0.7F, 2.0F);
   }

   private static float getAmountFloat(float editValue) {
      return 0.4F + 0.6F * editValue;
   }

   private void handleSearchTime(int search) {
      if (search != this.search) {
         this.search = search;
         this.updateReplaceThumb();
         this.thumbEntrance.updateTimeFlag();
      }
   }

   private boolean isOpen() {
      if (this.open != 0L && System.nanoTime() - this.open > 400000000L) {
         this.open = 0L;
      }

      return this.open != 0L;
   }

   private void renderClearbtnSearch(DrawContext context, int clearbtnSearchValue, int brownValue, int matrixStackValue, int matrixStackLimit, int scaledY) {
      VertexTextureFont fontRenderer = MatrixStackUtilsAmount.getFontRenderer();
      float amountForDrawContextHits = MatrixStackUtilsAmount.getAmountForDrawContextHits(
         context, this.savedHits, this.savedAnims, "m:view", viewSelectedTexts, this.search, matrixStackValue, brownValue, 20.0F, matrixStackLimit, scaledY, this::handleSearchTime
      );
      float identifierText = amountForDrawContextHits - 6.0F - clearbtnSearchValue;
      float releaseRightBound = this.savedAnims.getAmount("m:focus", 0.0F, this.focusFlag ? 1.0F : 0.0F, 16.0F);
      float clearbtnSearchValue9 = this.savedAnims.getAmount("m:field", MatrixStackUtilsAmount.canFlag(matrixStackLimit, scaledY, clearbtnSearchValue, brownValue, identifierText, 20.0F));
      float releaseRight = MatrixStackUtilsAmount.getReleaseRight(context, clearbtnSearchValue, brownValue, identifierText, 20.0F, IcoGenUtils.getIdentifierGEN_CORZCLIENT_MAP("search"), releaseRightBound, clearbtnSearchValue9, 0.0F);
      this.savedHits.handle(clearbtnSearchValue, brownValue - 1.0F, identifierText, 22.0F, this::updateFocusTime);
      float clearbtnSearchLimit = this.savedAnims.getAmount("m:clear", 0.0F, this.append.isEmpty() ? 0.0F : 1.0F, 16.0F);
      float matrixPeekValue = clearbtnSearchValue + identifierText - 4.0F - 12.0F;
      float configsLikeValue = brownValue + 4.0F;
      float clearbtnSearchValue12 = (clearbtnSearchLimit > 0.01F ? matrixPeekValue - 4.0F : clearbtnSearchValue + identifierText - 8.0F) - releaseRight;
      int abcdefghijklmnopqrstuvwxyzBrown = fontRenderer.getAbcdefghijklmnopqrstuvwxyzBrown(brownValue, 20);
      String matrixStackText = formatText(this.append, clearbtnSearchValue12);
      if (this.append.isEmpty()) {
         MatrixStackUtilsAmount.handle(fontRenderer.formatText("Search items...", clearbtnSearchValue12), releaseRight, abcdefghijklmnopqrstuvwxyzBrown, MatrixStackUtilsAmount.getValue(-7630437, 0.72F));
      } else {
         MatrixStackUtilsAmount.handle(matrixStackText, releaseRight, abcdefghijklmnopqrstuvwxyzBrown, -920587);
      }

      if (this.focusFlag) {
         float clearbtnSearchValue14 = releaseRight + (this.append.isEmpty() ? 0.0F : MatrixStackUtilsAmount.getAmount3(matrixStackText) + 1.0F);
         if (this.clearbtnSearchActive) {
            this.clearbtnSearchClearEdit.handleAmount(clearbtnSearchValue14);
            this.clearbtnSearchActive = false;
         }

         int n4 = (int)fontRenderer.getAbcdefghijklmnopqrstuvwxyzBrown() - 2;
         this.clearbtnSearchClearEdit.renderAmountTime(context, clearbtnSearchValue14, brownValue + (20 - n4) / 2, n4, MatrixEspUtils.releaseRightValue, MatrixClearUtils.getAmount());
      }

      if (!(clearbtnSearchLimit <= 0.01F)) {
         boolean clearbtnSearchFlag = clearbtnSearchLimit > 0.5F;
         float clearbtnSearchValue16 = matrixPeekValue - 2.0F;
         float clearbtnSearchValue17 = clearbtnSearchValue + identifierText - clearbtnSearchValue16;
         float cardMatrixFactor = this.savedAnims.getAmount("m:clearh", clearbtnSearchFlag && MatrixStackUtilsAmount.canFlag(matrixStackLimit, scaledY, clearbtnSearchValue16, brownValue, clearbtnSearchValue17, 20.0F));
         float x = matrixPeekValue + 6.0F;
         float y = configsLikeValue + 6.0F;
         boolean translatePush = MatrixStackUtilsAmount.canTranslatePush(
            context, x, y, (0.8F + 0.2F * MatrixStackUtilsAmount.getAmountForFloat(clearbtnSearchLimit)) * this.savedAnims.getTime("m:clearbtn", 0.14F)
         );
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(clearbtnSearchLimit);

         try {
            if (cardMatrixFactor > 0.01F) {
               MatrixPeekUtilsRender.renderMatrixPeek(context, matrixPeekValue, configsLikeValue, 12.0F, 12.0F, 6.0F, MatrixStackUtilsAmount.getValue(-1, 0.08F * cardMatrixFactor));
            }

            MatrixPeekUtilsIdentifier.handle(
               x - 5.0F,
               y - 5.0F,
               10.0F,
               10.0F,
               IcoGenUtils.getIdentifierGEN_CORZCLIENT_MAP("close"),
               new Color(CardMatrixUtils.getValue(-7630437, -920587, cardMatrixFactor), true),
               MatrixPeekUtilsRender.getMatrixStack()
            );
         } finally {
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
            MatrixStackUtilsAmount.renderPopMatrices(context, translatePush);
         }

         if (clearbtnSearchFlag) {
            this.savedHits.handleFlag(clearbtnSearchValue16, brownValue, clearbtnSearchValue17, 20.0F, this::updateAppend, this.savedAnims, "m:clearbtn");
         }
      }
   }

   private static void renderScissorDisable(DrawContext context, ItemStack itemStack, int scissorDisableValue, int floorCtx, int editBound, int matricesLimit, int scissorDisableLimit, int intersectsCenterBound) {
      if (scissorDisableLimit > editBound && intersectsCenterBound > matricesLimit) {
         context.enableScissor(editBound, matricesLimit, scissorDisableLimit, intersectsCenterBound);

         try {
            DrawMatricesUtils.render(context, itemStack, scissorDisableValue, floorCtx);
         } finally {
            context.disableScissor();
         }
      }
   }

   private void renderMaxFlag(DrawContext context, ItemStack itemStack, int editLimit, int floorCtx, int editBound, int matricesLimit, int scissorDisableLimit, int intersectsCenterBound) {
      if (scissorDisableLimit > editBound && intersectsCenterBound > matricesLimit) {
         int rocketsBuyBound = this.editBoxX;
         int editBoxY5 = this.editBoxY;
         int spotBound = this.editBoxX + this.editValue;
         int editLimit5 = this.editBoxY + 13;
         if (this.clickActive && scissorDisableLimit > rocketsBuyBound && editBound < spotBound && intersectsCenterBound > editBoxY5 && matricesLimit < editLimit5) {
            int editValue = Math.max(editBound, rocketsBuyBound);
            int min2 = Math.min(scissorDisableLimit, spotBound);
            renderScissorDisable(context, itemStack, editLimit, floorCtx, editBound, matricesLimit, Math.min(scissorDisableLimit, rocketsBuyBound), intersectsCenterBound);
            renderScissorDisable(context, itemStack, editLimit, floorCtx, Math.max(editBound, spotBound), matricesLimit, scissorDisableLimit, intersectsCenterBound);
            renderScissorDisable(context, itemStack, editLimit, floorCtx, editValue, matricesLimit, min2, Math.min(intersectsCenterBound, editBoxY5));
            renderScissorDisable(context, itemStack, editLimit, floorCtx, editValue, Math.max(matricesLimit, editLimit5), min2, intersectsCenterBound);
         } else {
            if (editBound <= editLimit && matricesLimit <= floorCtx && scissorDisableLimit >= editLimit + 16 && intersectsCenterBound >= floorCtx + 16) {
               DrawMatricesUtils.render(context, itemStack, editLimit, floorCtx);
            } else {
               renderScissorDisable(context, itemStack, editLimit, floorCtx, editBound, matricesLimit, scissorDisableLimit, intersectsCenterBound);
            }
         }
      }
   }

   public boolean canAmountCluster(double clusterValue, double clusterValue2, double keybindBindValue) {
      if (this.isSwitchSelected()) {
         return true;
      } else if (!this.openClusterFlag) {
         return false;
      } else if (!this.thumbActive && !(this.thumbMaxScroll <= 0.0F)) {
         int scaledWidthOffset = getScaledWidth() + 12;
         int scaledHeightOffset = getScaledHeight() + 75;
         if (!MatrixStackUtilsAmount.canFlag(this.getAmount(clusterValue), this.getAmountForDouble(clusterValue2), scaledWidthOffset - 2.0F, scaledHeightOffset, 298.0F, 166.0F)) {
            return true;
         } else {
            this.editAmount = getAmount(this.editAmount - (float)keybindBindValue * 2.0F * 24.0F, 0.0F, this.thumbMaxScroll);
            this.nanoTimeThumb = System.nanoTime();
            return true;
         }
      } else {
         return true;
      }
   }

   public void handleEditSelected(int selectedEditValue, int editClusterValue, int editSelectedValue, int value) {
      this.selectedEditValue = selectedEditValue;
      this.editClusterValue = editClusterValue;
      this.editSelectedValue = editSelectedValue;
      this.editSelectedIndex = value;
   }

   private void updateAmountThumb() {
      this.thumbActive = true;
      this.thumbStartTarget = this.clusterAmount;
      this.thumbStartScroll = this.thumbAmount;
      this.editAmount = this.thumbAmount;
   }

   private void updateAmountTime() {
      int index = getValue(this.clusterMouseX, this.clusterAmount, this.thumbX, this.thumbY, this.thumbScrollOffset, this.thumbItemList.size());
      if (index >= 0) {
         int editValue = Math.max(0, index / 12 - this.thumbValue) + (index % 12 >> 2);
         if (this.thumbEntrance.getAmountFlag(editValue).matrixScissorActive) {
            Item item = this.thumbItemList.get(index);
            SearchEdit.Fx fx = this.thumbFxMap.get(item);
            if (fx == null) {
               fx = this.getAmountFlag(item, this.itemList.isContains(item));
            }

            fx.animStartNanos = System.nanoTime();
            this.itemList.handle3(item);
            this.listSize = -1;
         }
      }
   }

   private void updateSwitchFlag() {
      if (this.openClusterFlag && !this.switchSelectedFlag) {
         this.switchSelectedFlag = true;
         this.nanoTimeClick = System.nanoTime();
         this.focusFlag = false;
         this.thumbActive = false;
      }
   }

   public SearchEdit(ItemList itemList, int selectedEditValue, int editClusterValue, int editSelectedValue, int value) {
      this.open = 0L;
      this.selectedValue = -1;
      this.selectedEditText = "";
      this.savedHits = new MatrixStackUtilsAmount.Hits();
      this.savedAnims = new MatrixStackUtilsAmount.Anims();
      this.thumbEntrance = new MatrixStackUtilsAmount.Entrance();
      this.clearbtnSearchClearEdit = new ClearEdit();
      this.thumbItemList = new ArrayList<>();
      this.thumbItemSet = new HashSet<>();
      this.stackItemStackMap = new IdentityHashMap<>();
      this.thumbFxMap = new IdentityHashMap<>();
      this.openClusterFlag = false;
      this.switchSelectedFlag = false;
      this.saveSavedFlag = false;
      this.nanoTimeClick = 0L;
      this.clickAmount = 1.0F;
      this.switchSelectedCount = 0;
      this.append = "";
      this.replace = "";
      this.focusFlag = true;
      this.clearbtnSearchActive = false;
      this.search = 0;
      this.clickText = "";
      this.clickHintText = "";
      this.savedText = "";
      this.savedValue = -1;
      this.listSize = -1;
      this.editAmount = 0.0F;
      this.thumbAmount = 0.0F;
      this.thumbScrollOffset = 0.0F;
      this.thumbMaxScroll = 0.0F;
      this.thumbBarAmount = 0.0F;
      this.replaceThumbAmount = 0.0F;
      this.thumbValue = 0;
      this.thumbX = 0;
      this.thumbY = 0;
      this.nanoTimeThumb = 0L;
      this.thumbActive = false;
      this.thumbStartTarget = 0.0;
      this.thumbStartScroll = 0.0F;
      this.clusterMouseX = 0.0;
      this.clusterAmount = 0.0;
      this.thumbItem = null;
      this.displayedItem = null;
      this.editText = "";
      this.editHoverStartNanos = 0L;
      this.itemFadeStartNanos = 0L;
      this.editFadeStartNanos = 0L;
      this.clickValue = 0L;
      this.editActive = false;
      this.clickActive = false;
      this.itemList = itemList;
      this.selectedEditValue = selectedEditValue;
      this.editClusterValue = editClusterValue;
      this.editSelectedValue = editSelectedValue;
      this.editSelectedIndex = value;
   }

   private void updateScaledMouse() {
      MinecraftClient instance = MinecraftClient.getInstance();
      Window window = instance.getWindow();
      this.scaledMouseValue = (int)Math.floor(this.getAmount(instance.mouse.getScaledX(window)));
      this.scaledMouseY = (int)Math.floor(this.getAmountForDouble(instance.mouse.getScaledY(window)));
   }

   private void updateItemList() {
      this.itemList.update();
      this.listSize = -1;
      this.updateSwitchFlag();
   }

   private void renderReleaseRight(DrawContext context, int releaseRightValue, int configsLikeValue, float cardMatrixFactor, float releaseRightValue4) {
      float matrixPeekValue = releaseRightValue + 22 - 1 - 7;
      float ctxOffset = configsLikeValue + 1;
      boolean translatePush = MatrixStackUtilsAmount.canTranslatePush(context, matrixPeekValue + 3.5F, ctxOffset + 3.5F, getAmountFloat(releaseRightValue4));
      float panelOverlay = CorzRoundedUtils.getPanelOverlay(MatrixStackUtilsAmount.getMax(cardMatrixFactor * 1.8F));

      try {
         MatrixPeekUtilsRender.renderMatrixPeek(context, matrixPeekValue, ctxOffset, 7.0F, 7.0F, 3.5F, MatrixStackUtilsAmount.getValue(MatrixEspUtils.releaseRightValue, 1.0F));
         MatrixPeekUtilsIdentifier.handle(
            matrixPeekValue,
            ctxOffset,
            7.0F,
            7.0F,
            IcoGenUtils.getIcoCheck(),
            new Color(MatrixStackUtilsAmount.getReleaseRight(), true),
            MatrixPeekUtilsRender.getMatrixStack()
         );
      } finally {
         CorzRoundedUtils.handlePanelOverlay(panelOverlay);
         MatrixStackUtilsAmount.renderPopMatrices(context, translatePush);
      }
   }

   public boolean canAppendFocus(int appendFocusValue, int scancode, int modifiers) {
      if (this.isSwitchSelected()) {
         return true;
      } else if (!this.openClusterFlag) {
         return false;
      } else if (appendFocusValue == 256) {
         this.updateSwitchFlag();
         return true;
      } else {
         if (this.focusFlag && appendFocusValue == 259 && !this.append.isEmpty()) {
            this.append = this.append.substring(0, this.append.length() - 1);
            this.updateTime();
         }

         return true;
      }
   }

   private void updateSaveSaved() {
      this.saveSavedFlag = true;
      this.updateSwitchFlag();
   }

   public boolean canAppendFocus(char appendFocusLimit, int appendFocusValue) {
      if (this.isSwitchSelected()) {
         return true;
      } else if (!this.openClusterFlag) {
         return false;
      } else {
         if (this.focusFlag && appendFocusLimit >= ' ' && appendFocusLimit != 127) {
            this.append = this.append + appendFocusLimit;
            this.updateTime();
         }

         return true;
      }
   }

   private void updateReplaceThumb() {
      this.update();
      this.replace = this.append.toLowerCase(Locale.ROOT).trim().replace(' ', '_');
      boolean replaceThumbFlag = this.search == 1;
      this.thumbItemList.clear();

      for (Item item : Registries.ITEM) {
         if (item != Items.AIR
            && (!replaceThumbFlag || this.thumbItemSet.contains(item))
            && (this.replace.isEmpty() || Registries.ITEM.getId(item).getPath().contains(this.replace))) {
            this.thumbItemList.add(item);
         }
      }

      this.editAmount = 0.0F;
      this.thumbAmount = 0.0F;
      this.replaceThumbAmount = 0.0F;
      this.thumbItem = null;
      this.displayedItem = null;
      this.clickActive = false;
      this.thumbFxMap.clear();
   }

   private boolean isSwitchSelected() {
      if (this.switchSelectedFlag && this.getSwitchSelected() >= 1.0F) {
         this.updateThumbFlag();
      }

      return this.switchSelectedFlag;
   }

   public void renderSwitchSelected(DrawContext context) {
      if (this.openClusterFlag) {
         float switchSelected = this.getSwitchSelected();
         if (this.switchSelectedFlag && switchSelected >= 1.0F) {
            this.updateThumbFlag();
         } else {
            this.switchSelectedCount++;
            Window window = MinecraftClient.getInstance().getWindow();
            int scaledWidth = getScaledWidth();
            int scaledHeight = getScaledHeight();
            float amount = MatrixStackUtilsAmount.getAmount(switchSelected, this.switchSelectedFlag);
            this.clickAmount = amount;
            this.updateScaledMouse();
            this.savedHits.update();
            MatrixStackUtilsAmount.render(
               context, window.getScaledWidth(), window.getScaledHeight(), 0.56F * MatrixStackUtilsAmount.getAmountFloat(switchSelected, this.switchSelectedFlag)
            );
            boolean translatePush = MatrixStackUtilsAmount.canTranslatePush(context, scaledWidth + 155.0F, scaledHeight + 140.0F, amount);
            float panelOverlay = CorzRoundedUtils.getPanelOverlay(MatrixStackUtilsAmount.getAmountForFloatFlag(switchSelected, this.switchSelectedFlag));

            try {
               MatrixStackUtilsAmount.render(context, scaledWidth, scaledHeight, 310.0F, 280.0F);
               this.renderSaveSaved(context, scaledWidth, scaledHeight);
            } finally {
               CorzRoundedUtils.handlePanelOverlay(panelOverlay);
               MatrixStackUtilsAmount.renderPopMatrices(context, translatePush);
            }
         }
      }
   }

   private static final class Fx {
      float prevHoverProgress;
      float hoverProgress;
      long hoverStartNanos;
      boolean thumbActive;
      long animStartNanos;
      boolean contains;
      int switchSelectedSnapshot;
      float expandProgress;
   }
}
