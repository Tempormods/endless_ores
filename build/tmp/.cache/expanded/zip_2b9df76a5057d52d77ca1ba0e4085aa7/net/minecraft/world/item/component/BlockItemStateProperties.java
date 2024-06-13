package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

public record BlockItemStateProperties(Map<String, String> f_316576_) {
    public static final BlockItemStateProperties f_314882_ = new BlockItemStateProperties(Map.of());
    public static final Codec<BlockItemStateProperties> f_315463_ = Codec.unboundedMap(Codec.STRING, Codec.STRING)
        .xmap(BlockItemStateProperties::new, BlockItemStateProperties::f_316576_);
    private static final StreamCodec<ByteBuf, Map<String, String>> f_314318_ = ByteBufCodecs.m_322136_(
        Object2ObjectOpenHashMap::new, ByteBufCodecs.f_315450_, ByteBufCodecs.f_315450_
    );
    public static final StreamCodec<ByteBuf, BlockItemStateProperties> f_317137_ = f_314318_.m_323038_(
        BlockItemStateProperties::new, BlockItemStateProperties::f_316576_
    );

    public <T extends Comparable<T>> BlockItemStateProperties m_323763_(Property<T> p_334707_, T p_329394_) {
        return new BlockItemStateProperties(Util.m_321632_(this.f_316576_, p_334707_.getName(), p_334707_.getName(p_329394_)));
    }

    public <T extends Comparable<T>> BlockItemStateProperties m_323660_(Property<T> p_332443_, BlockState p_334050_) {
        return this.m_323763_(p_332443_, p_334050_.getValue(p_332443_));
    }

    @Nullable
    public <T extends Comparable<T>> T m_321723_(Property<T> p_329754_) {
        String s = this.f_316576_.get(p_329754_.getName());
        return s == null ? null : p_329754_.getValue(s).orElse(null);
    }

    public BlockState m_323904_(BlockState p_330089_) {
        StateDefinition<Block, BlockState> statedefinition = p_330089_.getBlock().getStateDefinition();

        for (Entry<String, String> entry : this.f_316576_.entrySet()) {
            Property<?> property = statedefinition.getProperty(entry.getKey());
            if (property != null) {
                p_330089_ = m_319877_(p_330089_, property, entry.getValue());
            }
        }

        return p_330089_;
    }

    private static <T extends Comparable<T>> BlockState m_319877_(BlockState p_335297_, Property<T> p_336285_, String p_328779_) {
        return p_336285_.getValue(p_328779_).map(p_335669_ -> p_335297_.setValue(p_336285_, p_335669_)).orElse(p_335297_);
    }

    public boolean m_322799_() {
        return this.f_316576_.isEmpty();
    }
}