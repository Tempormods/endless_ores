package net.minecraft.world.level.gameevent;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface PositionSourceType<T extends PositionSource> {
    PositionSourceType<BlockPositionSource> BLOCK = register("block", new BlockPositionSource.Type());
    PositionSourceType<EntityPositionSource> ENTITY = register("entity", new EntityPositionSource.Type());

    MapCodec<T> codec();

    StreamCodec<? super RegistryFriendlyByteBuf, T> m_322720_();

    static <S extends PositionSourceType<T>, T extends PositionSource> S register(String pId, S pType) {
        return Registry.register(BuiltInRegistries.POSITION_SOURCE_TYPE, pId, pType);
    }
}