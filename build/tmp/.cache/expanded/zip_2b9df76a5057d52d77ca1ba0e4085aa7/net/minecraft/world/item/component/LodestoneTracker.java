package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;

public record LodestoneTracker(Optional<GlobalPos> f_315845_, boolean f_317017_) {
    public static final Codec<LodestoneTracker> f_314702_ = RecordCodecBuilder.create(
        p_328600_ -> p_328600_.group(
                    GlobalPos.CODEC.optionalFieldOf("target").forGetter(LodestoneTracker::f_315845_),
                    Codec.BOOL.optionalFieldOf("tracked", Boolean.valueOf(true)).forGetter(LodestoneTracker::f_317017_)
                )
                .apply(p_328600_, LodestoneTracker::new)
    );
    public static final StreamCodec<ByteBuf, LodestoneTracker> f_316697_ = StreamCodec.m_320349_(
        GlobalPos.f_314491_.m_321801_(ByteBufCodecs::m_319027_),
        LodestoneTracker::f_315845_,
        ByteBufCodecs.f_315514_,
        LodestoneTracker::f_317017_,
        LodestoneTracker::new
    );

    public LodestoneTracker m_321939_(ServerLevel p_333312_) {
        if (this.f_317017_ && !this.f_315845_.isEmpty()) {
            if (this.f_315845_.get().dimension() != p_333312_.dimension()) {
                return this;
            } else {
                BlockPos blockpos = this.f_315845_.get().pos();
                return p_333312_.isInWorldBounds(blockpos) && p_333312_.getPoiManager().existsAtPosition(PoiTypes.LODESTONE, blockpos)
                    ? this
                    : new LodestoneTracker(Optional.empty(), true);
            }
        } else {
            return this;
        }
    }
}