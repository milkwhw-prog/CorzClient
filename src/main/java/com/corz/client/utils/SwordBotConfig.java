package com.corz.client.utils;

public record SwordBotConfig(boolean aggressive, int fightRange, float turnScale, boolean strafe, boolean jumpReset, boolean crits) {
   public static final SwordBotConfig INSTANCE = new SwordBotConfig(false, 16, 1.0F, true, true, true);
}
