package net.minecraft.world.level.gameevent.vibrations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public record VibrationInfo(
    Holder<GameEvent> gameEvent, float distance, Vec3 pos, @Nullable UUID uuid, @Nullable UUID projectileOwnerUuid, @Nullable Entity entity
) {
    public static final Codec<VibrationInfo> CODEC = RecordCodecBuilder.create(
        p_327437_ -> p_327437_.group(
                    BuiltInRegistries.GAME_EVENT.holderByNameCodec().fieldOf("game_event").forGetter(VibrationInfo::gameEvent),
                    Codec.floatRange(0.0F, Float.MAX_VALUE).fieldOf("distance").forGetter(VibrationInfo::distance),
                    Vec3.CODEC.fieldOf("pos").forGetter(VibrationInfo::pos),
                    UUIDUtil.CODEC.lenientOptionalFieldOf("source").forGetter(p_250608_ -> Optional.ofNullable(p_250608_.uuid())),
                    UUIDUtil.CODEC.lenientOptionalFieldOf("projectile_owner").forGetter(p_250607_ -> Optional.ofNullable(p_250607_.projectileOwnerUuid()))
                )
                .apply(
                    p_327437_,
                    (p_327438_, p_327439_, p_327440_, p_327441_, p_327442_) -> new VibrationInfo(
                            p_327438_, p_327439_, p_327440_, p_327441_.orElse(null), p_327442_.orElse(null)
                        )
                )
    );

    public VibrationInfo(Holder<GameEvent> p_332399_, float pDistance, Vec3 pPos, @Nullable UUID pUuid, @Nullable UUID pProjectileOwnerUuid) {
        this(p_332399_, pDistance, pPos, pUuid, pProjectileOwnerUuid, null);
    }

    public VibrationInfo(Holder<GameEvent> p_333558_, float pDistance, Vec3 pPos, @Nullable Entity pEntity) {
        this(p_333558_, pDistance, pPos, pEntity == null ? null : pEntity.getUUID(), getProjectileOwner(pEntity), pEntity);
    }

    @Nullable
    private static UUID getProjectileOwner(@Nullable Entity pEntity) {
        if (pEntity instanceof Projectile projectile && projectile.getOwner() != null) {
            return projectile.getOwner().getUUID();
        }

        return null;
    }

    public Optional<Entity> getEntity(ServerLevel pLevel) {
        return Optional.ofNullable(this.entity).or(() -> Optional.ofNullable(this.uuid).map(pLevel::getEntity));
    }

    public Optional<Entity> getProjectileOwner(ServerLevel pLevel) {
        return this.getEntity(pLevel)
            .filter(p_249829_ -> p_249829_ instanceof Projectile)
            .map(p_249388_ -> (Projectile)p_249388_)
            .map(Projectile::getOwner)
            .or(() -> Optional.ofNullable(this.projectileOwnerUuid).map(pLevel::getEntity));
    }
}