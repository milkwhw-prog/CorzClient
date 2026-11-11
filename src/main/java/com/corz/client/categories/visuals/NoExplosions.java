package com.corz.client.categories.visuals;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import ai.auth.Module;
import com.corz.client.utils.IconsPngType;
public class NoExplosions extends Module {
   private final BooleanSetting booleanBase;
   private final BooleanSetting otherExplosions;
   private final BooleanSetting tntExplosions;
   private final BooleanSetting crystalExplosions = new BooleanSetting("Crystal Explosions", "Remove end crystal explosion effects", true);
   private final BooleanSetting creeperExplosions;

   public boolean isBase() {
      return this.crystalExplosions.isEnabled()
         && this.booleanBase.isEnabled()
         && this.tntExplosions.isEnabled()
         && this.creeperExplosions.isEnabled()
         && this.otherExplosions.isEnabled();
   }

   public NoExplosions() {
      super("No Explosions", IconsPngType.VISUAL);
      this.booleanBase = new BooleanSetting("Anchor Explosions", "Remove respawn anchor explosion effects", true);
      this.tntExplosions = new BooleanSetting("TNT Explosions", "Remove TNT explosion effects", true);
      this.creeperExplosions = new BooleanSetting("Creeper Explosions", "Remove creeper explosion effects", true);
      this.otherExplosions = new BooleanSetting("Other Explosions", "Remove all other explosion effects", true);
      this.addSettings(new ConfigSetting[]{this.crystalExplosions, this.booleanBase, this.tntExplosions, this.creeperExplosions, this.otherExplosions});
   }

   public boolean isOtherExplosions() {
      return this.otherExplosions.isEnabled();
   }
}
