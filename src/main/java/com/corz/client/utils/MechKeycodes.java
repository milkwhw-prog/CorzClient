package com.corz.client.utils;

import java.util.Arrays;
import org.lwjgl.glfw.GLFW;

public final class MechKeycodes {
   private static final int[] windowsValues = new int[349];
   private static final int[] windowsScancodeTable = new int[349];
   private static final boolean startsWith = System.getProperty("os.name", "").toLowerCase().startsWith("windows");

   static {
      Arrays.fill(windowsValues, -1);
      Arrays.fill(windowsScancodeTable, Integer.MIN_VALUE);
      handleWindows(256, 1);

      for (int i = 0; i < 10; i++) {
         handleWindows(290 + i, 59 + i);
      }

      handleWindows(300, 87);
      handleWindows(301, 88);
      handleWindows(96, 41);

      for (int j = 1; j <= 9; j++) {
         handleWindows(48 + j, 1 + j);
      }

      handleWindows(48, 11);
      handleWindows(45, 12);
      handleWindows(61, 13);
      handleWindows(259, 14);
      handleWindows(258, 15);
      handle("QWERTYUIOP", 16);
      handleWindows(91, 26);
      handleWindows(93, 27);
      handleWindows(257, 28);
      handleWindows(341, 29);
      handle("ASDFGHJKL", 30);
      handleWindows(59, 39);
      handleWindows(39, 40);
      handleWindows(340, 42);
      handleWindows(92, 43);
      handle("ZXCVBNM", 44);
      handleWindows(44, 51);
      handleWindows(46, 52);
      handleWindows(47, 53);
      handleWindows(344, 54);
      handleWindows(332, 55);
      handleWindows(342, 56);
      handleWindows(32, 57);
      handleWindows(280, 58);
      handleWindows(282, 69);
      handleWindows(281, 70);
      handleWindows(327, 71);
      handleWindows(328, 72);
      handleWindows(329, 73);
      handleWindows(333, 74);
      handleWindows(324, 75);
      handleWindows(325, 76);
      handleWindows(326, 77);
      handleWindows(334, 78);
      handleWindows(321, 79);
      handleWindows(322, 80);
      handleWindows(323, 81);
      handleWindows(320, 82);
      handleWindows(330, 83);
      handleWindows(161, 86);
      handleWindows(336, 3597);
      handleWindows(335, 3612);
      handleWindows(345, 3613);
      handleWindows(331, 3637);
      handleWindows(283, 3639);
      handleWindows(346, 3640);
      handleWindows(284, 3653);
      handleWindows(268, 3655);
      handleWindows(266, 3657);
      handleWindows(269, 3663);
      handleWindows(267, 3665);
      handleWindows(260, 3666);
      handleWindows(261, 3667);
      handleWindows(343, 3675);
      handleWindows(347, 3676);
      handleWindows(348, 3677);
      handleWindows(265, 57416);
      handleWindows(263, 57419);
      handleWindows(262, 57421);
      handleWindows(264, 57424);
   }

   private static void handle(String mechKeycodesText, int mechKeycodesValue) {
      for (int i = 0; i < mechKeycodesText.length(); i++) {
         handleWindows(mechKeycodesText.charAt(i), mechKeycodesValue + i);
      }
   }

   public static int getStarts(int startsValue) {
      return getValue(startsValue, startsWith ? getWindowsScancode(startsValue) : -1, startsWith);
   }

   public static int getValue(int mechKeycodesValue, int mechKeycodesLimit, boolean mechKeycodesFlag) {
      if (mechKeycodesValue != 284 && mechKeycodesValue != 282) {
         if (mechKeycodesFlag) {
            int mechKeycodesValue3 = getValue(mechKeycodesLimit);
            if (mechKeycodesValue3 != -1) {
               return mechKeycodesValue3;
            }
         }

         return getWindows(mechKeycodesValue);
      } else {
         return getWindows(mechKeycodesValue);
      }
   }

   public static int getValue(int mechKeycodesLimit) {
      if (mechKeycodesLimit <= 0 || mechKeycodesLimit > 511) {
         return -1;
      } else if (mechKeycodesLimit == 325) {
         return 69;
      } else if (mechKeycodesLimit != 69 && mechKeycodesLimit != 326) {
         int mechKeycodesValue = mechKeycodesLimit & 0xFF;
         if ((mechKeycodesLimit & 256) == 0) {
            return mechKeycodesValue;
         } else {
            return mechKeycodesValue != 72 && mechKeycodesValue != 75 && mechKeycodesValue != 77 && mechKeycodesValue != 80 ? 3584 | mechKeycodesValue : 57344 | mechKeycodesValue;
         }
      } else {
         return 3653;
      }
   }

   public static int getWindows(int index) {
      return index >= 0 && index < windowsValues.length ? windowsValues[index] : -1;
   }

   private static int getWindowsScancode(int index) {
      if (index >= 0 && index < windowsScancodeTable.length) {
         int windowsScancode = windowsScancodeTable[index];
         if (windowsScancode != Integer.MIN_VALUE) {
            return windowsScancode;
         } else {
            int glfwGetKeyScancode2;
            try {
               glfwGetKeyScancode2 = GLFW.glfwGetKeyScancode(index);
            } catch (Throwable e) {
               glfwGetKeyScancode2 = -1;
            }

            windowsScancodeTable[index] = glfwGetKeyScancode2;
            return glfwGetKeyScancode2;
         }
      } else {
         return -1;
      }
   }

   private static void handleWindows(int index, int windowsValue) {
      windowsValues[index] = windowsValue;
   }

   public static int[] getArray() {
      return Arrays.stream(windowsValues).filter(arrayValue -> arrayValue != -1).distinct().sorted().toArray();
   }

   private MechKeycodes() {
   }
}
