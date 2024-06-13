package net.minecraft.client.multiplayer;

import com.mojang.authlib.GameProfile;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.telemetry.WorldSessionTelemetryManager;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public record CommonListenerCookie(
    GameProfile localGameProfile,
    WorldSessionTelemetryManager telemetryManager,
    RegistryAccess.Frozen receivedRegistries,
    FeatureFlagSet enabledFeatures,
    @Nullable String serverBrand,
    @Nullable ServerData serverData,
    @Nullable Screen postDisconnectScreen,
    Map<ResourceLocation, byte[]> f_314268_,
    @Nullable ChatComponent.State f_315631_,
    @Deprecated(forRemoval = true) boolean f_317100_
) {
}