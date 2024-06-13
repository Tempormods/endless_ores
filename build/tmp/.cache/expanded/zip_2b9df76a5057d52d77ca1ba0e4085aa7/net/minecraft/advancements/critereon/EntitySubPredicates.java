package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.CatVariant;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.FrogVariant;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.WolfVariant;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.animal.horse.Variant;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.npc.VillagerDataHolder;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.phys.Vec3;

public class EntitySubPredicates {
    public static final MapCodec<LightningBoltPredicate> f_314519_ = m_321283_("lightning", LightningBoltPredicate.CODEC);
    public static final MapCodec<FishingHookPredicate> f_314536_ = m_321283_("fishing_hook", FishingHookPredicate.CODEC);
    public static final MapCodec<PlayerPredicate> f_316746_ = m_321283_("player", PlayerPredicate.CODEC);
    public static final MapCodec<SlimePredicate> f_316352_ = m_321283_("slime", SlimePredicate.CODEC);
    public static final MapCodec<RaiderPredicate> f_315965_ = m_321283_("raider", RaiderPredicate.f_314357_);
    public static final EntitySubPredicates.EntityVariantPredicateType<Axolotl.Variant> f_315626_ = m_323048_(
        "axolotl",
        EntitySubPredicates.EntityVariantPredicateType.m_322727_(
            Axolotl.Variant.CODEC, p_334006_ -> p_334006_ instanceof Axolotl axolotl ? Optional.of(axolotl.getVariant()) : Optional.empty()
        )
    );
    public static final EntitySubPredicates.EntityVariantPredicateType<Boat.Type> f_316328_ = m_323048_(
        "boat",
        EntitySubPredicates.EntityVariantPredicateType.m_322727_(
            Boat.Type.CODEC, p_331113_ -> p_331113_ instanceof Boat boat ? Optional.of(boat.getVariant()) : Optional.empty()
        )
    );
    public static final EntitySubPredicates.EntityVariantPredicateType<Fox.Type> f_316909_ = m_323048_(
        "fox",
        EntitySubPredicates.EntityVariantPredicateType.m_322727_(
            Fox.Type.CODEC, p_334394_ -> p_334394_ instanceof Fox fox ? Optional.of(fox.getVariant()) : Optional.empty()
        )
    );
    public static final EntitySubPredicates.EntityVariantPredicateType<MushroomCow.MushroomType> f_315115_ = m_323048_(
        "mooshroom",
        EntitySubPredicates.EntityVariantPredicateType.m_322727_(
            MushroomCow.MushroomType.CODEC,
            p_334523_ -> p_334523_ instanceof MushroomCow mushroomcow ? Optional.of(mushroomcow.getVariant()) : Optional.empty()
        )
    );
    public static final EntitySubPredicates.EntityVariantPredicateType<Rabbit.Variant> f_315006_ = m_323048_(
        "rabbit",
        EntitySubPredicates.EntityVariantPredicateType.m_322727_(
            Rabbit.Variant.CODEC, p_334309_ -> p_334309_ instanceof Rabbit rabbit ? Optional.of(rabbit.getVariant()) : Optional.empty()
        )
    );
    public static final EntitySubPredicates.EntityVariantPredicateType<Variant> f_315038_ = m_323048_(
        "horse",
        EntitySubPredicates.EntityVariantPredicateType.m_322727_(
            Variant.CODEC, p_334549_ -> p_334549_ instanceof Horse horse ? Optional.of(horse.getVariant()) : Optional.empty()
        )
    );
    public static final EntitySubPredicates.EntityVariantPredicateType<Llama.Variant> f_316383_ = m_323048_(
        "llama",
        EntitySubPredicates.EntityVariantPredicateType.m_322727_(
            Llama.Variant.CODEC, p_336380_ -> p_336380_ instanceof Llama llama ? Optional.of(llama.getVariant()) : Optional.empty()
        )
    );
    public static final EntitySubPredicates.EntityVariantPredicateType<VillagerType> f_316021_ = m_323048_(
        "villager",
        EntitySubPredicates.EntityVariantPredicateType.m_322727_(
            BuiltInRegistries.VILLAGER_TYPE.byNameCodec(),
            p_334803_ -> p_334803_ instanceof VillagerDataHolder villagerdataholder ? Optional.of(villagerdataholder.getVariant()) : Optional.empty()
        )
    );
    public static final EntitySubPredicates.EntityVariantPredicateType<Parrot.Variant> f_316139_ = m_323048_(
        "parrot",
        EntitySubPredicates.EntityVariantPredicateType.m_322727_(
            Parrot.Variant.CODEC, p_327673_ -> p_327673_ instanceof Parrot parrot ? Optional.of(parrot.getVariant()) : Optional.empty()
        )
    );
    public static final EntitySubPredicates.EntityVariantPredicateType<TropicalFish.Pattern> f_315043_ = m_323048_(
        "tropical_fish",
        EntitySubPredicates.EntityVariantPredicateType.m_322727_(
            TropicalFish.Pattern.CODEC,
            p_330151_ -> p_330151_ instanceof TropicalFish tropicalfish ? Optional.of(tropicalfish.getVariant()) : Optional.empty()
        )
    );
    public static final EntitySubPredicates.EntityHolderVariantPredicateType<PaintingVariant> f_314385_ = m_323624_(
        "painting",
        EntitySubPredicates.EntityHolderVariantPredicateType.m_319287_(
            Registries.PAINTING_VARIANT, p_329680_ -> p_329680_ instanceof Painting painting ? Optional.of(painting.getVariant()) : Optional.empty()
        )
    );
    public static final EntitySubPredicates.EntityHolderVariantPredicateType<CatVariant> f_314140_ = m_323624_(
        "cat",
        EntitySubPredicates.EntityHolderVariantPredicateType.m_319287_(
            Registries.CAT_VARIANT, p_331742_ -> p_331742_ instanceof Cat cat ? Optional.of(cat.getVariant()) : Optional.empty()
        )
    );
    public static final EntitySubPredicates.EntityHolderVariantPredicateType<FrogVariant> f_316594_ = m_323624_(
        "frog",
        EntitySubPredicates.EntityHolderVariantPredicateType.m_319287_(
            Registries.FROG_VARIANT, p_334670_ -> p_334670_ instanceof Frog frog ? Optional.of(frog.getVariant()) : Optional.empty()
        )
    );
    public static final EntitySubPredicates.EntityHolderVariantPredicateType<WolfVariant> f_315732_ = m_323624_(
        "wolf",
        EntitySubPredicates.EntityHolderVariantPredicateType.m_319287_(
            Registries.f_317086_, p_334632_ -> p_334632_ instanceof Wolf wolf ? Optional.of(wolf.getVariant()) : Optional.empty()
        )
    );

    private static <T extends EntitySubPredicate> MapCodec<T> m_321283_(String p_328480_, MapCodec<T> p_332441_) {
        return Registry.register(BuiltInRegistries.f_313902_, p_328480_, p_332441_);
    }

    private static <V> EntitySubPredicates.EntityVariantPredicateType<V> m_323048_(
        String p_330409_, EntitySubPredicates.EntityVariantPredicateType<V> p_330951_
    ) {
        Registry.register(BuiltInRegistries.f_313902_, p_330409_, p_330951_.f_314924_);
        return p_330951_;
    }

    private static <V> EntitySubPredicates.EntityHolderVariantPredicateType<V> m_323624_(
        String p_329374_, EntitySubPredicates.EntityHolderVariantPredicateType<V> p_329883_
    ) {
        Registry.register(BuiltInRegistries.f_313902_, p_329374_, p_329883_.f_314467_);
        return p_329883_;
    }

    public static MapCodec<? extends EntitySubPredicate> m_322489_(Registry<MapCodec<? extends EntitySubPredicate>> p_335865_) {
        return f_314519_;
    }

    public static EntitySubPredicate m_319315_(Holder<CatVariant> p_331492_) {
        return f_314140_.m_318835_(HolderSet.direct(p_331492_));
    }

    public static EntitySubPredicate m_318658_(Holder<FrogVariant> p_333799_) {
        return f_316594_.m_318835_(HolderSet.direct(p_333799_));
    }

    public static EntitySubPredicate m_320274_(HolderSet<WolfVariant> p_335349_) {
        return f_315732_.m_318835_(p_335349_);
    }

    public static class EntityHolderVariantPredicateType<V> {
        final MapCodec<EntitySubPredicates.EntityHolderVariantPredicateType<V>.Instance> f_314467_;
        final Function<Entity, Optional<Holder<V>>> f_315554_;

        public static <V> EntitySubPredicates.EntityHolderVariantPredicateType<V> m_319287_(
            ResourceKey<? extends Registry<V>> p_335498_, Function<Entity, Optional<Holder<V>>> p_336153_
        ) {
            return new EntitySubPredicates.EntityHolderVariantPredicateType<>(p_335498_, p_336153_);
        }

        public EntityHolderVariantPredicateType(ResourceKey<? extends Registry<V>> p_332702_, Function<Entity, Optional<Holder<V>>> p_329584_) {
            this.f_315554_ = p_329584_;
            this.f_314467_ = RecordCodecBuilder.mapCodec(
                p_330908_ -> p_330908_.group(RegistryCodecs.homogeneousList(p_332702_).fieldOf("variant").forGetter(p_329421_ -> p_329421_.f_314024_))
                        .apply(p_330908_, p_331166_ -> new EntitySubPredicates.EntityHolderVariantPredicateType<V>.Instance(p_331166_))
            );
        }

        public EntitySubPredicate m_318835_(HolderSet<V> p_335527_) {
            return new EntitySubPredicates.EntityHolderVariantPredicateType.Instance(p_335527_);
        }

        class Instance implements EntitySubPredicate {
            final HolderSet<V> f_314024_;

            Instance(final HolderSet<V> p_331442_) {
                this.f_314024_ = p_331442_;
            }

            @Override
            public MapCodec<EntitySubPredicates.EntityHolderVariantPredicateType<V>.Instance> type() {
                return EntityHolderVariantPredicateType.this.f_314467_;
            }

            @Override
            public boolean matches(Entity p_330194_, ServerLevel p_330112_, @Nullable Vec3 p_329192_) {
                return EntityHolderVariantPredicateType.this.f_315554_.apply(p_330194_).filter(this.f_314024_::contains).isPresent();
            }
        }
    }

    public static class EntityVariantPredicateType<V> {
        final MapCodec<EntitySubPredicates.EntityVariantPredicateType<V>.Instance> f_314924_;
        final Function<Entity, Optional<V>> f_314447_;

        public static <V> EntitySubPredicates.EntityVariantPredicateType<V> m_322293_(Registry<V> p_331006_, Function<Entity, Optional<V>> p_335365_) {
            return new EntitySubPredicates.EntityVariantPredicateType<>(p_331006_.byNameCodec(), p_335365_);
        }

        public static <V> EntitySubPredicates.EntityVariantPredicateType<V> m_322727_(Codec<V> p_330954_, Function<Entity, Optional<V>> p_329190_) {
            return new EntitySubPredicates.EntityVariantPredicateType<>(p_330954_, p_329190_);
        }

        public EntityVariantPredicateType(Codec<V> p_329553_, Function<Entity, Optional<V>> p_333059_) {
            this.f_314447_ = p_333059_;
            this.f_314924_ = RecordCodecBuilder.mapCodec(
                p_330838_ -> p_330838_.group(p_329553_.fieldOf("variant").forGetter(p_332763_ -> p_332763_.f_316369_))
                        .apply(p_330838_, p_327954_ -> new EntitySubPredicates.EntityVariantPredicateType<V>.Instance(p_327954_))
            );
        }

        public EntitySubPredicate m_320150_(V p_335305_) {
            return new EntitySubPredicates.EntityVariantPredicateType.Instance(p_335305_);
        }

        class Instance implements EntitySubPredicate {
            final V f_316369_;

            Instance(final V p_332718_) {
                this.f_316369_ = p_332718_;
            }

            @Override
            public MapCodec<EntitySubPredicates.EntityVariantPredicateType<V>.Instance> type() {
                return EntityVariantPredicateType.this.f_314924_;
            }

            @Override
            public boolean matches(Entity p_333217_, ServerLevel p_332166_, @Nullable Vec3 p_334706_) {
                return EntityVariantPredicateType.this.f_314447_.apply(p_333217_).filter(this.f_316369_::equals).isPresent();
            }
        }
    }
}