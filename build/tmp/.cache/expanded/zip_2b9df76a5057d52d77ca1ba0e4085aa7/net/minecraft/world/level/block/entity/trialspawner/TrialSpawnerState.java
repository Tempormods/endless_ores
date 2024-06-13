package net.minecraft.world.level.block.entity.trialspawner;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.OminousItemSpawner;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

public enum TrialSpawnerState implements StringRepresentable {
    INACTIVE("inactive", 0, TrialSpawnerState.ParticleEmission.f_302687_, -1.0, false),
    WAITING_FOR_PLAYERS("waiting_for_players", 4, TrialSpawnerState.ParticleEmission.f_302488_, 200.0, true),
    ACTIVE("active", 8, TrialSpawnerState.ParticleEmission.f_302540_, 1000.0, true),
    WAITING_FOR_REWARD_EJECTION("waiting_for_reward_ejection", 8, TrialSpawnerState.ParticleEmission.f_302488_, -1.0, false),
    EJECTING_REWARD("ejecting_reward", 8, TrialSpawnerState.ParticleEmission.f_302488_, -1.0, false),
    COOLDOWN("cooldown", 0, TrialSpawnerState.ParticleEmission.f_303781_, -1.0, false);

    private static final float f_303262_ = 40.0F;
    private static final int f_302549_ = Mth.floor(30.0F);
    private final String f_303350_;
    private final int f_302810_;
    private final double f_303620_;
    private final TrialSpawnerState.ParticleEmission f_302969_;
    private final boolean f_303677_;

    private TrialSpawnerState(
        final String p_309652_, final int p_311553_, final TrialSpawnerState.ParticleEmission p_309474_, final double p_312481_, final boolean p_310488_
    ) {
        this.f_303350_ = p_309652_;
        this.f_302810_ = p_311553_;
        this.f_302969_ = p_309474_;
        this.f_303620_ = p_312481_;
        this.f_303677_ = p_310488_;
    }

    TrialSpawnerState m_308008_(BlockPos p_313024_, TrialSpawner p_310869_, ServerLevel p_313233_) {
        TrialSpawnerData trialspawnerdata = p_310869_.m_305472_();
        TrialSpawnerConfig trialspawnerconfig = p_310869_.m_306177_();

        return switch (this) {
            case INACTIVE -> trialspawnerdata.m_307031_(p_310869_, p_313233_, WAITING_FOR_PLAYERS) == null ? this : WAITING_FOR_PLAYERS;
            case WAITING_FOR_PLAYERS -> {
                if (!trialspawnerdata.m_306286_(p_310869_, p_313233_.random)) {
                    yield INACTIVE;
                } else {
                    trialspawnerdata.m_304942_(p_313233_, p_313024_, p_310869_);
                    yield trialspawnerdata.f_303462_.isEmpty() ? this : ACTIVE;
                }
            }
            case ACTIVE -> {
                if (!trialspawnerdata.m_306286_(p_310869_, p_313233_.random)) {
                    yield INACTIVE;
                } else {
                    int i = trialspawnerdata.m_305480_(p_313024_);
                    trialspawnerdata.m_304942_(p_313233_, p_313024_, p_310869_);
                    if (p_310869_.m_322987_()) {
                        this.m_322742_(p_313233_, p_313024_, p_310869_);
                    }

                    if (trialspawnerdata.m_305025_(trialspawnerconfig, i)) {
                        if (trialspawnerdata.m_307352_()) {
                            trialspawnerdata.f_303712_ = p_313233_.getGameTime() + (long)p_310869_.m_320388_();
                            trialspawnerdata.f_302930_ = 0;
                            trialspawnerdata.f_302458_ = 0L;
                            yield WAITING_FOR_REWARD_EJECTION;
                        }
                    } else if (trialspawnerdata.m_305681_(p_313233_, trialspawnerconfig, i)) {
                        p_310869_.m_305361_(p_313233_, p_313024_).ifPresent(p_327378_ -> {
                            trialspawnerdata.f_302440_.add(p_327378_);
                            trialspawnerdata.f_302930_++;
                            trialspawnerdata.f_302458_ = p_313233_.getGameTime() + (long)trialspawnerconfig.f_303452_();
                            trialspawnerconfig.f_303733_().getRandom(p_313233_.getRandom()).ifPresent(p_327384_ -> {
                                trialspawnerdata.f_303191_ = Optional.of(p_327384_.data());
                                p_310869_.m_306727_();
                            });
                        });
                    }

                    yield this;
                }
            }
            case WAITING_FOR_REWARD_EJECTION -> {
                if (trialspawnerdata.m_305761_(p_313233_, 40.0F, p_310869_.m_320388_())) {
                    p_313233_.playSound(null, p_313024_, SoundEvents.f_302837_, SoundSource.BLOCKS);
                    yield EJECTING_REWARD;
                } else {
                    yield this;
                }
            }
            case EJECTING_REWARD -> {
                if (!trialspawnerdata.m_305594_(p_313233_, (float)f_302549_, p_310869_.m_320388_())) {
                    yield this;
                } else if (trialspawnerdata.f_303462_.isEmpty()) {
                    p_313233_.playSound(null, p_313024_, SoundEvents.f_302735_, SoundSource.BLOCKS);
                    trialspawnerdata.f_303012_ = Optional.empty();
                    yield COOLDOWN;
                } else {
                    if (trialspawnerdata.f_303012_.isEmpty()) {
                        trialspawnerdata.f_303012_ = trialspawnerconfig.f_302816_().getRandomValue(p_313233_.getRandom());
                    }

                    trialspawnerdata.f_303012_.ifPresent(p_327391_ -> p_310869_.m_306218_(p_313233_, p_313024_, (ResourceKey<LootTable>)p_327391_));
                    trialspawnerdata.f_303462_.remove(trialspawnerdata.f_303462_.iterator().next());
                    yield this;
                }
            }
            case COOLDOWN -> {
                trialspawnerdata.m_304942_(p_313233_, p_313024_, p_310869_);
                if (!trialspawnerdata.f_303462_.isEmpty()) {
                    trialspawnerdata.f_302930_ = 0;
                    trialspawnerdata.f_302458_ = 0L;
                    yield ACTIVE;
                } else if (trialspawnerdata.m_305171_(p_313233_)) {
                    trialspawnerdata.f_303712_ = 0L;
                    p_310869_.m_324138_(p_313233_, p_313024_);
                    yield WAITING_FOR_PLAYERS;
                } else {
                    yield this;
                }
            }
        };
    }

    private void m_322742_(ServerLevel p_332885_, BlockPos p_332679_, TrialSpawner p_327911_) {
        TrialSpawnerData trialspawnerdata = p_327911_.m_305472_();
        TrialSpawnerConfig trialspawnerconfig = p_327911_.m_306177_();
        ItemStack itemstack = trialspawnerdata.m_319751_(p_332885_, trialspawnerconfig, p_332679_).getRandomValue(p_332885_.random).orElse(ItemStack.EMPTY);
        if (!itemstack.isEmpty()) {
            if (this.m_324229_(p_332885_, trialspawnerdata)) {
                m_319371_(p_332885_, p_332679_, p_327911_, trialspawnerdata).ifPresent(p_327373_ -> {
                    OminousItemSpawner ominousitemspawner = OminousItemSpawner.m_321091_(p_332885_, itemstack);
                    ominousitemspawner.moveTo(p_327373_);
                    p_332885_.addFreshEntity(ominousitemspawner);
                    float f = (p_332885_.getRandom().nextFloat() - p_332885_.getRandom().nextFloat()) * 0.2F + 1.0F;
                    p_332885_.playSound(null, BlockPos.containing(p_327373_), SoundEvents.f_315712_, SoundSource.BLOCKS, 1.0F, f);
                    trialspawnerdata.f_303712_ = p_332885_.getGameTime() + p_327911_.m_320710_().m_324829_();
                });
            }
        }
    }

    private static Optional<Vec3> m_319371_(ServerLevel p_332378_, BlockPos p_330701_, TrialSpawner p_331338_, TrialSpawnerData p_334280_) {
        List<Player> list = p_334280_.f_303462_
            .stream()
            .map(p_332378_::getPlayerByUUID)
            .filter(Objects::nonNull)
            .filter(
                p_327387_ -> !p_327387_.isCreative()
                        && !p_327387_.isSpectator()
                        && p_327387_.isAlive()
                        && p_327387_.distanceToSqr(p_330701_.getCenter()) <= (double)Mth.square(p_331338_.m_321887_())
            )
            .toList();
        if (list.isEmpty()) {
            return Optional.empty();
        } else {
            Entity entity = m_323848_(list, p_334280_.f_302440_, p_331338_, p_330701_, p_332378_);
            return entity == null ? Optional.empty() : m_320878_(entity, p_332378_);
        }
    }

    private static Optional<Vec3> m_320878_(Entity p_332455_, ServerLevel p_334568_) {
        Vec3 vec3 = p_332455_.position();
        Vec3 vec31 = vec3.relative(Direction.UP, (double)(p_332455_.getBbHeight() + 2.0F + (float)p_334568_.random.nextInt(4)));
        BlockHitResult blockhitresult = p_334568_.clip(
            new ClipContext(vec3, vec31, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, CollisionContext.empty())
        );
        Vec3 vec32 = blockhitresult.getBlockPos().getCenter().relative(Direction.DOWN, 1.0);
        BlockPos blockpos = BlockPos.containing(vec32);
        return !p_334568_.getBlockState(blockpos).getCollisionShape(p_334568_, blockpos).isEmpty() ? Optional.empty() : Optional.of(vec32);
    }

    @Nullable
    private static Entity m_323848_(List<Player> p_328857_, Set<UUID> p_330482_, TrialSpawner p_335914_, BlockPos p_330933_, ServerLevel p_330297_) {
        Stream<Entity> stream = p_330482_.stream()
            .map(p_330297_::getEntity)
            .filter(Objects::nonNull)
            .filter(p_327381_ -> p_327381_.isAlive() && p_327381_.distanceToSqr(p_330933_.getCenter()) <= (double)Mth.square(p_335914_.m_321887_()));
        List<? extends Entity> list = p_330297_.random.nextBoolean() ? stream.toList() : p_328857_;
        if (list.isEmpty()) {
            return null;
        } else {
            return list.size() == 1 ? list.getFirst() : Util.getRandom(list, p_330297_.random);
        }
    }

    private boolean m_324229_(ServerLevel p_332151_, TrialSpawnerData p_334161_) {
        return p_332151_.getGameTime() >= p_334161_.f_303712_;
    }

    public int m_304822_() {
        return this.f_302810_;
    }

    public double m_305120_() {
        return this.f_303620_;
    }

    public boolean m_307384_() {
        return this.f_303620_ >= 0.0;
    }

    public boolean m_306216_() {
        return this.f_303677_;
    }

    public void m_306816_(Level p_310333_, BlockPos p_312414_, boolean p_333242_) {
        this.f_302969_.m_308004_(p_310333_, p_310333_.getRandom(), p_312414_, p_333242_);
    }

    @Override
    public String getSerializedName() {
        return this.f_303350_;
    }

    static class LightLevel {
        private static final int f_303256_ = 0;
        private static final int f_302741_ = 4;
        private static final int f_303097_ = 8;

        private LightLevel() {
        }
    }

    interface ParticleEmission {
        TrialSpawnerState.ParticleEmission f_302687_ = (p_311158_, p_313095_, p_309870_, p_333658_) -> {
        };
        TrialSpawnerState.ParticleEmission f_302488_ = (p_327396_, p_327397_, p_327398_, p_327399_) -> {
            if (p_327397_.nextInt(2) == 0) {
                Vec3 vec3 = p_327398_.getCenter().offsetRandom(p_327397_, 0.9F);
                m_307767_(p_327399_ ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.SMALL_FLAME, vec3, p_327396_);
            }
        };
        TrialSpawnerState.ParticleEmission f_302540_ = (p_327392_, p_327393_, p_327394_, p_327395_) -> {
            Vec3 vec3 = p_327394_.getCenter().offsetRandom(p_327393_, 1.0F);
            m_307767_(ParticleTypes.SMOKE, vec3, p_327392_);
            m_307767_(p_327395_ ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME, vec3, p_327392_);
        };
        TrialSpawnerState.ParticleEmission f_303781_ = (p_312500_, p_312202_, p_311828_, p_334641_) -> {
            Vec3 vec3 = p_311828_.getCenter().offsetRandom(p_312202_, 0.9F);
            if (p_312202_.nextInt(3) == 0) {
                m_307767_(ParticleTypes.SMOKE, vec3, p_312500_);
            }

            if (p_312500_.getGameTime() % 20L == 0L) {
                Vec3 vec31 = p_311828_.getCenter().add(0.0, 0.5, 0.0);
                int i = p_312500_.getRandom().nextInt(4) + 20;

                for (int j = 0; j < i; j++) {
                    m_307767_(ParticleTypes.SMOKE, vec31, p_312500_);
                }
            }
        };

        private static void m_307767_(SimpleParticleType p_311275_, Vec3 p_310309_, Level p_310163_) {
            p_310163_.addParticle(p_311275_, p_310309_.x(), p_310309_.y(), p_310309_.z(), 0.0, 0.0, 0.0);
        }

        void m_308004_(Level p_310445_, RandomSource p_311021_, BlockPos p_310003_, boolean p_330593_);
    }

    static class SpinningMob {
        private static final double f_303131_ = -1.0;
        private static final double f_303460_ = 200.0;
        private static final double f_302416_ = 1000.0;

        private SpinningMob() {
        }
    }
}