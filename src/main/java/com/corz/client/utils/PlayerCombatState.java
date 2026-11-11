package com.corz.client.utils;

public record PlayerCombatState(
   float ehp, boolean totem, float fallEpf, int resistanceAmp, boolean fireResistant, int food, boolean usingItem, int pingMs, int style
) {
   public static final PlayerCombatState INSTANCE = new PlayerCombatState(20.0F, false, 0.0F, -1, false, 20, false, 0, 0);
}
