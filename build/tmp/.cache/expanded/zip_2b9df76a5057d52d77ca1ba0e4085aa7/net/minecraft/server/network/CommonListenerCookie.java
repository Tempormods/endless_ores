package net.minecraft.server.network;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.level.ClientInformation;

public record CommonListenerCookie(GameProfile gameProfile, int latency, ClientInformation clientInformation, boolean f_315441_) {
    public static CommonListenerCookie createInitial(GameProfile pGameProfile, boolean p_335270_) {
        return new CommonListenerCookie(pGameProfile, 0, ClientInformation.createDefault(), p_335270_);
    }
}