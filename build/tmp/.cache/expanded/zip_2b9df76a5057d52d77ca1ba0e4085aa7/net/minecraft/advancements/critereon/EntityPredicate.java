package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;

public record EntityPredicate(
    Optional<EntityTypePredicate> entityType,
    Optional<DistancePredicate> distanceToPlayer,
    Optional<LocationPredicate> location,
    Optional<LocationPredicate> steppingOnLocation,
    Optional<MobEffectsPredicate> effects,
    Optional<NbtPredicate> nbt,
    Optional<EntityFlagsPredicate> flags,
    Optional<EntityEquipmentPredicate> equipment,
    Optional<EntitySubPredicate> subPredicate,
    Optional<EntityPredicate> vehicle,
    Optional<EntityPredicate> passenger,
    Optional<EntityPredicate> targetedEntity,
    Optional<String> team,
    Optional<SlotsPredicate> f_315910_
) {
    public static final Codec<EntityPredicate> CODEC = Codec.recursive(
        "EntityPredicate",
        p_296121_ -> RecordCodecBuilder.create(
                p_296120_ -> p_296120_.group(
                            EntityTypePredicate.CODEC.optionalFieldOf("type").forGetter(EntityPredicate::entityType),
                            DistancePredicate.CODEC.optionalFieldOf("distance").forGetter(EntityPredicate::distanceToPlayer),
                            LocationPredicate.CODEC.optionalFieldOf("location").forGetter(EntityPredicate::location),
                            LocationPredicate.CODEC.optionalFieldOf("stepping_on").forGetter(EntityPredicate::steppingOnLocation),
                            MobEffectsPredicate.CODEC.optionalFieldOf("effects").forGetter(EntityPredicate::effects),
                            NbtPredicate.CODEC.optionalFieldOf("nbt").forGetter(EntityPredicate::nbt),
                            EntityFlagsPredicate.CODEC.optionalFieldOf("flags").forGetter(EntityPredicate::flags),
                            EntityEquipmentPredicate.CODEC.optionalFieldOf("equipment").forGetter(EntityPredicate::equipment),
                            EntitySubPredicate.CODEC.optionalFieldOf("type_specific").forGetter(EntityPredicate::subPredicate),
                            p_296121_.optionalFieldOf("vehicle").forGetter(EntityPredicate::vehicle),
                            p_296121_.optionalFieldOf("passenger").forGetter(EntityPredicate::passenger),
                            p_296121_.optionalFieldOf("targeted_entity").forGetter(EntityPredicate::targetedEntity),
                            Codec.STRING.optionalFieldOf("team").forGetter(EntityPredicate::team),
                            SlotsPredicate.f_316958_.optionalFieldOf("slots").forGetter(EntityPredicate::f_315910_)
                        )
                        .apply(p_296120_, EntityPredicate::new)
            )
    );
    public static final Codec<ContextAwarePredicate> f_303210_ = Codec.withAlternative(ContextAwarePredicate.f_303282_, CODEC, EntityPredicate::wrap);

    public static ContextAwarePredicate wrap(EntityPredicate.Builder p_298584_) {
        return wrap(p_298584_.build());
    }

    public static Optional<ContextAwarePredicate> wrap(Optional<EntityPredicate> pPredicate) {
        return pPredicate.map(EntityPredicate::wrap);
    }

    public static List<ContextAwarePredicate> wrap(EntityPredicate.Builder... pBuilders) {
        return Stream.of(pBuilders).map(EntityPredicate::wrap).toList();
    }

    public static ContextAwarePredicate wrap(EntityPredicate p_286570_) {
        LootItemCondition lootitemcondition = LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, p_286570_).build();
        return new ContextAwarePredicate(List.of(lootitemcondition));
    }

    public boolean matches(ServerPlayer pPlayer, @Nullable Entity pEntity) {
        return this.matches(pPlayer.serverLevel(), pPlayer.position(), pEntity);
    }

    public boolean matches(ServerLevel pLevel, @Nullable Vec3 pPosition, @Nullable Entity pEntity) {
        if (pEntity == null) {
            return false;
        } else if (this.entityType.isPresent() && !this.entityType.get().matches(pEntity.getType())) {
            return false;
        } else {
            if (pPosition == null) {
                if (this.distanceToPlayer.isPresent()) {
                    return false;
                }
            } else if (this.distanceToPlayer.isPresent()
                && !this.distanceToPlayer
                    .get()
                    .matches(pPosition.x, pPosition.y, pPosition.z, pEntity.getX(), pEntity.getY(), pEntity.getZ())) {
                return false;
            }

            if (this.location.isPresent() && !this.location.get().matches(pLevel, pEntity.getX(), pEntity.getY(), pEntity.getZ())) {
                return false;
            } else {
                if (this.steppingOnLocation.isPresent()) {
                    Vec3 vec3 = Vec3.atCenterOf(pEntity.getOnPos());
                    if (!this.steppingOnLocation.get().matches(pLevel, vec3.x(), vec3.y(), vec3.z())) {
                        return false;
                    }
                }

                if (this.effects.isPresent() && !this.effects.get().matches(pEntity)) {
                    return false;
                } else if (this.flags.isPresent() && !this.flags.get().matches(pEntity)) {
                    return false;
                } else if (this.equipment.isPresent() && !this.equipment.get().matches(pEntity)) {
                    return false;
                } else if (this.subPredicate.isPresent() && !this.subPredicate.get().matches(pEntity, pLevel, pPosition)) {
                    return false;
                } else if (this.vehicle.isPresent() && !this.vehicle.get().matches(pLevel, pPosition, pEntity.getVehicle())) {
                    return false;
                } else if (this.passenger.isPresent()
                    && pEntity.getPassengers().stream().noneMatch(p_296124_ -> this.passenger.get().matches(pLevel, pPosition, p_296124_))) {
                    return false;
                } else if (this.targetedEntity.isPresent()
                    && !this.targetedEntity.get().matches(pLevel, pPosition, pEntity instanceof Mob ? ((Mob)pEntity).getTarget() : null)) {
                    return false;
                } else {
                    if (this.team.isPresent()) {
                        Team team = pEntity.getTeam();
                        if (team == null || !this.team.get().equals(team.getName())) {
                            return false;
                        }
                    }

                    return this.f_315910_.isPresent() && !this.f_315910_.get().m_319960_(pEntity)
                        ? false
                        : !this.nbt.isPresent() || this.nbt.get().matches(pEntity);
                }
            }
        }
    }

    public static LootContext createContext(ServerPlayer pPlayer, Entity pEntity) {
        LootParams lootparams = new LootParams.Builder(pPlayer.serverLevel())
            .withParameter(LootContextParams.THIS_ENTITY, pEntity)
            .withParameter(LootContextParams.ORIGIN, pPlayer.position())
            .create(LootContextParamSets.ADVANCEMENT_ENTITY);
        return new LootContext.Builder(lootparams).create(Optional.empty());
    }

    public static class Builder {
        private Optional<EntityTypePredicate> entityType = Optional.empty();
        private Optional<DistancePredicate> distanceToPlayer = Optional.empty();
        private Optional<LocationPredicate> location = Optional.empty();
        private Optional<LocationPredicate> steppingOnLocation = Optional.empty();
        private Optional<MobEffectsPredicate> effects = Optional.empty();
        private Optional<NbtPredicate> nbt = Optional.empty();
        private Optional<EntityFlagsPredicate> flags = Optional.empty();
        private Optional<EntityEquipmentPredicate> equipment = Optional.empty();
        private Optional<EntitySubPredicate> subPredicate = Optional.empty();
        private Optional<EntityPredicate> vehicle = Optional.empty();
        private Optional<EntityPredicate> passenger = Optional.empty();
        private Optional<EntityPredicate> targetedEntity = Optional.empty();
        private Optional<String> team = Optional.empty();
        private Optional<SlotsPredicate> f_315815_ = Optional.empty();

        public static EntityPredicate.Builder entity() {
            return new EntityPredicate.Builder();
        }

        public EntityPredicate.Builder of(EntityType<?> pEntityType) {
            this.entityType = Optional.of(EntityTypePredicate.of(pEntityType));
            return this;
        }

        public EntityPredicate.Builder of(TagKey<EntityType<?>> pEntityTypeTag) {
            this.entityType = Optional.of(EntityTypePredicate.of(pEntityTypeTag));
            return this;
        }

        public EntityPredicate.Builder entityType(EntityTypePredicate pEntityType) {
            this.entityType = Optional.of(pEntityType);
            return this;
        }

        public EntityPredicate.Builder distance(DistancePredicate pDistanceToPlayer) {
            this.distanceToPlayer = Optional.of(pDistanceToPlayer);
            return this;
        }

        public EntityPredicate.Builder located(LocationPredicate.Builder pLocation) {
            this.location = Optional.of(pLocation.build());
            return this;
        }

        public EntityPredicate.Builder steppingOn(LocationPredicate.Builder pSteppingOnLocation) {
            this.steppingOnLocation = Optional.of(pSteppingOnLocation.build());
            return this;
        }

        public EntityPredicate.Builder effects(MobEffectsPredicate.Builder pEffects) {
            this.effects = pEffects.build();
            return this;
        }

        public EntityPredicate.Builder nbt(NbtPredicate pNbt) {
            this.nbt = Optional.of(pNbt);
            return this;
        }

        public EntityPredicate.Builder flags(EntityFlagsPredicate.Builder pFlags) {
            this.flags = Optional.of(pFlags.build());
            return this;
        }

        public EntityPredicate.Builder equipment(EntityEquipmentPredicate.Builder pEquipment) {
            this.equipment = Optional.of(pEquipment.build());
            return this;
        }

        public EntityPredicate.Builder equipment(EntityEquipmentPredicate pEquipment) {
            this.equipment = Optional.of(pEquipment);
            return this;
        }

        public EntityPredicate.Builder subPredicate(EntitySubPredicate pSubPredicate) {
            this.subPredicate = Optional.of(pSubPredicate);
            return this;
        }

        public EntityPredicate.Builder vehicle(EntityPredicate.Builder pVehicle) {
            this.vehicle = Optional.of(pVehicle.build());
            return this;
        }

        public EntityPredicate.Builder passenger(EntityPredicate.Builder pPassenger) {
            this.passenger = Optional.of(pPassenger.build());
            return this;
        }

        public EntityPredicate.Builder targetedEntity(EntityPredicate.Builder pTargetedEntity) {
            this.targetedEntity = Optional.of(pTargetedEntity.build());
            return this;
        }

        public EntityPredicate.Builder team(String pTeam) {
            this.team = Optional.of(pTeam);
            return this;
        }

        public EntityPredicate.Builder m_322083_(SlotsPredicate p_330666_) {
            this.f_315815_ = Optional.of(p_330666_);
            return this;
        }

        public EntityPredicate build() {
            return new EntityPredicate(
                this.entityType,
                this.distanceToPlayer,
                this.location,
                this.steppingOnLocation,
                this.effects,
                this.nbt,
                this.flags,
                this.equipment,
                this.subPredicate,
                this.vehicle,
                this.passenger,
                this.targetedEntity,
                this.team,
                this.f_315815_
            );
        }
    }
}