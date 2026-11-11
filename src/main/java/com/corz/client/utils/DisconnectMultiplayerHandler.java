package com.corz.client.utils;

import net.minecraft.network.packet.Packet;

public interface DisconnectMultiplayerHandler {
   long getKnockbackWater(Packet<?> packet, boolean knockbackWaterFlag, long knockbackWaterValue);
}
