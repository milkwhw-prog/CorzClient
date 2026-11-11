package com.corz.client.utils;

import com.corz.client.utils.MechPack.Clip;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import com.corz.client.utils.MechPack;
import com.corz.client.utils.Pcm;
import com.corz.client.utils.MechKeycodes;
import com.corz.client.utils.OggDecoder;

public final class KeyBank {
   public static final String CHERRY_BLUE = "Cherry MX Blue";
   private final int defaultBankValue;
   static final String ASSETS_CORZCLIENT_CREAMYKEYS = "/assets/corzclient/creamykeys/";
   public static final String HOLY_PANDAS = "Holy Pandas";
   public static final String[] creamTopreTexts = new String[]{"NK Cream", "Cherry MX Blue", "Holy Pandas", "Topre"};
   static final String[] topreHybridTexts = new String[]{"nk-cream", "cherrymx-blue-abs", "holy-pandas", "topre-purple-hybrid-pbt"};
   private final Map<Integer, Integer> bankIntegerMap;
   public final Pcm[] array;
   private final Map<Integer, Integer> integerMap;
   public static final String CREAM = "NK Cream";
   public final int errorSourceiValue;
   public static final String TOPRE = "Topre";

   private static int getFileLoad(Clip clip, KeyBank.Files files, Map<String, Pcm> map, Map<Clip, Integer> map2, List<Pcm> list) {
      if (clip == null) {
         return -1;
      } else {
         Integer file = (Integer)map2.get(clip);
         if (file != null) {
            return file;
         } else {
            int fileLoad = -1;
            Pcm botCrystalValue = (Pcm)map.get(clip.file());
            if (botCrystalValue == null && !map.containsKey(clip.file())) {
               try {
                  botCrystalValue = files.load(clip.file());
               } catch (Exception e) {
                  botCrystalValue = null;
               }

               map.put(clip.file(), botCrystalValue);
            }

            if (botCrystalValue != null) {
               Pcm pcm = clip.isStart() ? botCrystalValue.getChannelsRate(clip.startMs(), clip.lengthMs()) : botCrystalValue;
               if (pcm.getChannelsSamples() > 0) {
                  fileLoad = list.size();
                  list.add(pcm);
               }
            }

            map2.put(clip, fileLoad);
            return fileLoad;
         }
      }
   }

   public static int getCreamTopre(int creamTopreLimit) {
      return creamTopreLimit >= 0 && creamTopreLimit < creamTopreTexts.length ? creamTopreLimit : 0;
   }

   public int getValue(int bankValue) {
      Integer integer = this.bankIntegerMap.get(bankValue);
      return integer != null ? integer : this.defaultBankValue;
   }

   public int getValueForInt(int bankValue) {
      Integer integer = this.integerMap.get(bankValue);
      return integer != null ? integer : -1;
   }

   private KeyBank(int value, Pcm[] array, Map<Integer, Integer> map, Map<Integer, Integer> map2, int value2) {
      this.errorSourceiValue = value;
      this.array = array;
      this.bankIntegerMap = map;
      this.integerMap = map2;
      this.defaultBankValue = value2;
   }

   public static KeyBank getKeyBank(int bankValue, MechPack mechPack, KeyBank.Files files) {
      var hashMap = new HashMap();
      var hashMap2 = new HashMap();
      var arrayList = new ArrayList<Pcm>();
      var treeSet = new TreeSet<>(mechPack.getKeyboardDefine());

      for (int getItem : MechKeycodes.getArray()) {
         treeSet.add(getItem);
      }

      var hashMap3 = new HashMap();
      var hashMap4 = new HashMap();

      for (int treeItem : treeSet) {
         int fileLoad = getFileLoad(mechPack.getDefinesDefine(treeItem), files, hashMap, hashMap2, arrayList);
         if (fileLoad >= 0) {
            hashMap3.put(treeItem, fileLoad);
         }

         int finish = getFileLoad(mechPack.getSoundupDefine(treeItem), files, hashMap, hashMap2, arrayList);
         if (finish >= 0) {
            hashMap4.put(treeItem, finish);
         }
      }

      int fileLoad3 = getFileLoad(mechPack.getDefinesDefine(-1), files, hashMap, hashMap2, arrayList);
      return new KeyBank(bankValue, arrayList.toArray(new Pcm[0]), hashMap3, hashMap4, fileLoad3);
   }

   public static int getCreamTopre(String creamTopreText) {
      for (int i = 0; i < creamTopreTexts.length; i++) {
         if (creamTopreTexts[i].equals(creamTopreText)) {
            return i;
         }
      }

      return 0;
   }

   public static KeyBank getHybridTopre(int hybridTopreValue)  throws java.io.IOException {
      int creamTopre = getCreamTopre(hybridTopreValue);
      String hybridTopreText = "/assets/corzclient/creamykeys/" + topreHybridTexts[creamTopre] + "/";
      MechPack parseJson = MechPack.getParseJson(new String(getAllBytes(hybridTopreText + "config.json"), StandardCharsets.UTF_8));
      return getKeyBank(creamTopre, parseJson, file -> {
         byte[] var2x = getAllBytes(hybridTopreText + file);
         return file.toLowerCase().endsWith(".ogg") ? OggDecoder.getStbVorbis(var2x) : Pcm.getWavRiff(var2x);
      });
   }

   private static byte[] getAllBytes(String bytesText)  throws java.io.IOException {
      byte[] allBytes;
      try (InputStream resourceAsStream = KeyBank.class.getResourceAsStream(bytesText)) {
         if (resourceAsStream == null) {
            throw new IOException("missing " + bytesText);
         }

         allBytes = resourceAsStream.readAllBytes();
      }

      return allBytes;
   }

   public long getSamplesArray() {
      long samplesArray = 0L;

      for (Pcm pcm : this.array) {
         samplesArray += pcm.samples().length;
      }

      return samplesArray;
   }

   public interface Files {
      Pcm load(String filesText) throws java.io.IOException;
   }
}
