package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public interface EntitySubPredicate {
    Codec<EntitySubPredicate> CODEC = BuiltInRegistries.f_313902_.byNameCodec().dispatch(EntitySubPredicate::type, Function.identity());

    MapCodec<? extends EntitySubPredicate> type();

    boolean matches(Entity pEntity, ServerLevel pLevel, @Nullable Vec3 pPosition);
}