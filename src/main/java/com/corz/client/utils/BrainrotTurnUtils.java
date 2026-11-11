package com.corz.client.utils;

import java.util.List;
import com.corz.client.utils.WarmBrainrotBase;
import com.corz.client.utils.TungPupil;
import com.corz.client.utils.ShinThigh;
import com.corz.client.utils.LidBombardiro;
import com.corz.client.utils.SloshFore;
import com.corz.client.utils.BrrFore;
import com.corz.client.utils.ArmTrunk;
import com.corz.client.utils.EarChimpanzini;
import com.corz.client.utils.TailKatana;
import com.corz.client.utils.FingerClaw;
import com.corz.client.utils.FootBombombini;
import com.corz.client.utils.AmbalabuBoneca;
import com.corz.client.utils.LidRing;
import com.corz.client.utils.ShinFoot;
import com.corz.client.utils.TailFore;
import com.corz.client.utils.LegBurbaloni;
import com.corz.client.utils.ForeShinConst;
import com.corz.client.utils.CrownFore;
import com.corz.client.utils.ShinEar;
import com.corz.client.utils.TailEar;
import com.corz.client.utils.DinFore;
import com.corz.client.utils.DolfinitaBananita;

public final class BrainrotTurnUtils {
   public static final List<WarmBrainrotBase> BRAINROT_TURN_ITEMS = List.of(
      new TungPupil(),
      new ShinThigh(),
      new LidBombardiro(),
      new SloshFore(),
      new BrrFore(),
      new ArmTrunk(),
      new EarChimpanzini(),
      new TailKatana(),
      new FingerClaw(),
      new FootBombombini(),
      new AmbalabuBoneca(),
      new LidRing(),
      new ShinFoot(),
      new TailFore(),
      new LegBurbaloni(),
      new ForeShinConst(),
      new CrownFore(),
      new ShinEar(),
      new TailEar(),
      new DinFore(),
      new DolfinitaBananita()
   );

   public static String[] getBrainrotTurn() {
      String[] strings = new String[BRAINROT_TURN_ITEMS.size()];

      for (int i = 0; i < strings.length; i++) {
         strings[i] = BRAINROT_TURN_ITEMS.get(i).formatTungDinString();
      }

      return strings;
   }

   private BrainrotTurnUtils() {
   }

   public static int getBrainrotTurn(String brainrotTurnText) {
      for (int i = 0; i < BRAINROT_TURN_ITEMS.size(); i++) {
         if (BRAINROT_TURN_ITEMS.get(i).formatTungDinString().equals(brainrotTurnText)) {
            return i;
         }
      }

      return -1;
   }
}
