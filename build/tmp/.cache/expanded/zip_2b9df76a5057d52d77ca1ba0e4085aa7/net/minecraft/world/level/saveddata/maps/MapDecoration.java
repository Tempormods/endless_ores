package net.minecraft.world.level.saveddata.maps;

import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record MapDecoration(Holder<MapDecorationType> type, byte x, byte y, byte rot, Optional<Component> name) {
    public static final StreamCodec<RegistryFriendlyByteBuf, MapDecoration> f_314705_ = StreamCodec.m_319894_(
        MapDecorationType.f_315859_,
        MapDecoration::type,
        ByteBufCodecs.f_313954_,
        MapDecoration::x,
        ByteBufCodecs.f_313954_,
        MapDecoration::y,
        ByteBufCodecs.f_313954_,
        MapDecoration::rot,
        ComponentSerialization.f_315970_,
        MapDecoration::name,
        MapDecoration::new
    );

    public MapDecoration(Holder<MapDecorationType> type, byte x, byte y, byte rot, Optional<Component> name) {
        rot = (byte)(rot & 15);
        this.type = type;
        this.x = x;
        this.y = y;
        this.rot = rot;
        this.name = name;
    }

    public ResourceLocation m_318661_() {
        return this.type.value().f_315068_();
    }

    public boolean renderOnFrame() {
        return this.type.value().f_315585_();
    }
}