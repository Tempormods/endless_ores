package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;

public record DebugStickState(Map<Holder<Block>, Property<?>> f_314311_) {
    public static final DebugStickState f_314030_ = new DebugStickState(Map.of());
    public static final Codec<DebugStickState> f_314822_ = Codec.<Holder<Block>, Property<?>>dispatchedMap(
            BuiltInRegistries.BLOCK.holderByNameCodec(),
            p_329333_ -> Codec.STRING
                    .comapFlatMap(
                        p_332541_ -> {
                            Property<?> property = p_329333_.value().getStateDefinition().getProperty(p_332541_);
                            return property != null
                                ? DataResult.success(property)
                                : DataResult.error(() -> "No property on " + p_329333_.m_323990_() + " with name: " + p_332541_);
                        },
                        Property::getName
                    )
        )
        .xmap(DebugStickState::new, DebugStickState::f_314311_);

    public DebugStickState m_319043_(Holder<Block> p_330343_, Property<?> p_334645_) {
        return new DebugStickState(Util.m_321632_(this.f_314311_, p_330343_, p_334645_));
    }
}