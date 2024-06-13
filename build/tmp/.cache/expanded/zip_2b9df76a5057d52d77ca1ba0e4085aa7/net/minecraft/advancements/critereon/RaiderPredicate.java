package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.phys.Vec3;

public record RaiderPredicate(boolean f_315999_, boolean f_315971_) implements EntitySubPredicate {
    public static final MapCodec<RaiderPredicate> f_314357_ = RecordCodecBuilder.mapCodec(
        p_334461_ -> p_334461_.group(
                    Codec.BOOL.optionalFieldOf("has_raid", Boolean.valueOf(false)).forGetter(RaiderPredicate::f_315999_),
                    Codec.BOOL.optionalFieldOf("is_captain", Boolean.valueOf(false)).forGetter(RaiderPredicate::f_315971_)
                )
                .apply(p_334461_, RaiderPredicate::new)
    );
    public static final RaiderPredicate f_316933_ = new RaiderPredicate(false, true);

    @Override
    public MapCodec<RaiderPredicate> type() {
        return EntitySubPredicates.f_315965_;
    }

    @Override
    public boolean matches(Entity p_333043_, ServerLevel p_332324_, @Nullable Vec3 p_334148_) {
        return !(p_333043_ instanceof Raider raider) ? false : raider.m_320449_() == this.f_315999_ && raider.m_324198_() == this.f_315971_;
    }
}