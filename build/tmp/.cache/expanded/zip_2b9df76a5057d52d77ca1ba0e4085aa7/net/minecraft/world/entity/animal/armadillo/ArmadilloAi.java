package net.minecraft.world.entity.animal.armadillo;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.AnimalMakeLove;
import net.minecraft.world.entity.ai.behavior.AnimalPanic;
import net.minecraft.world.entity.ai.behavior.BabyFollowAdult;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.CountDownCooldownTicks;
import net.minecraft.world.entity.ai.behavior.DoNothing;
import net.minecraft.world.entity.ai.behavior.FollowTemptation;
import net.minecraft.world.entity.ai.behavior.LookAtTargetSink;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.behavior.RandomLookAround;
import net.minecraft.world.entity.ai.behavior.RandomStroll;
import net.minecraft.world.entity.ai.behavior.RunOne;
import net.minecraft.world.entity.ai.behavior.SetEntityLookTargetSometimes;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromLookTarget;
import net.minecraft.world.entity.ai.behavior.Swim;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.ItemStack;

public class ArmadilloAi {
    private static final float f_315750_ = 2.0F;
    private static final float f_316852_ = 1.0F;
    private static final float f_316934_ = 1.25F;
    private static final float f_316148_ = 1.25F;
    private static final float f_313930_ = 1.0F;
    private static final double f_316217_ = 2.0;
    private static final double f_313976_ = 1.0;
    private static final UniformInt f_314100_ = UniformInt.of(5, 16);
    private static final ImmutableList<SensorType<? extends Sensor<? super Armadillo>>> f_314274_ = ImmutableList.of(
        SensorType.NEAREST_LIVING_ENTITIES, SensorType.HURT_BY, SensorType.f_316993_, SensorType.NEAREST_ADULT, SensorType.f_315808_
    );
    private static final ImmutableList<MemoryModuleType<?>> f_314417_ = ImmutableList.of(
        MemoryModuleType.IS_PANICKING,
        MemoryModuleType.HURT_BY,
        MemoryModuleType.HURT_BY_ENTITY,
        MemoryModuleType.WALK_TARGET,
        MemoryModuleType.LOOK_TARGET,
        MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
        MemoryModuleType.PATH,
        MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
        MemoryModuleType.TEMPTING_PLAYER,
        MemoryModuleType.TEMPTATION_COOLDOWN_TICKS,
        MemoryModuleType.GAZE_COOLDOWN_TICKS,
        MemoryModuleType.IS_TEMPTED,
        MemoryModuleType.BREED_TARGET,
        MemoryModuleType.NEAREST_VISIBLE_ADULT,
        MemoryModuleType.f_315790_
    );
    private static final OneShot<Armadillo> f_314476_ = BehaviorBuilder.create(
        p_336267_ -> p_336267_.group(p_336267_.absent(MemoryModuleType.f_315790_)).apply(p_336267_, p_332554_ -> (p_330349_, p_333991_, p_333776_) -> {
                    if (p_333991_.m_323155_()) {
                        p_333991_.m_324029_();
                        return true;
                    } else {
                        return false;
                    }
                })
    );

    public static Brain.Provider<Armadillo> m_318723_() {
        return Brain.provider(f_314417_, f_314274_);
    }

    protected static Brain<?> m_320021_(Brain<Armadillo> p_329024_) {
        m_324068_(p_329024_);
        m_322692_(p_329024_);
        m_323739_(p_329024_);
        p_329024_.setCoreActivities(Set.of(Activity.CORE));
        p_329024_.setDefaultActivity(Activity.IDLE);
        p_329024_.useDefaultActivity();
        return p_329024_;
    }

    private static void m_324068_(Brain<Armadillo> p_330038_) {
        p_330038_.addActivity(
            Activity.CORE, 0, ImmutableList.of(new Swim(0.8F), new ArmadilloAi.ArmadilloPanic(2.0F), new LookAtTargetSink(45, 90), new MoveToTargetSink() {
                @Override
                protected boolean checkExtraStartConditions(ServerLevel p_335403_, Mob p_333295_) {
                    if (p_333295_ instanceof Armadillo armadillo && armadillo.m_323155_()) {
                        return false;
                    }

                    return super.checkExtraStartConditions(p_335403_, p_333295_);
                }
            }, new CountDownCooldownTicks(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS), new CountDownCooldownTicks(MemoryModuleType.GAZE_COOLDOWN_TICKS), f_314476_)
        );
    }

    private static void m_322692_(Brain<Armadillo> p_334108_) {
        p_334108_.addActivity(
            Activity.IDLE,
            ImmutableList.of(
                Pair.of(0, SetEntityLookTargetSometimes.create(EntityType.PLAYER, 6.0F, UniformInt.of(30, 60))),
                Pair.of(1, new AnimalMakeLove(EntityType.f_316265_, 1.0F, 1)),
                Pair.of(
                    2,
                    new RunOne<>(
                        ImmutableList.of(
                            Pair.of(new FollowTemptation(p_329728_ -> 1.25F, p_335020_ -> p_335020_.isBaby() ? 1.0 : 2.0), 1),
                            Pair.of(BabyFollowAdult.create(f_314100_, 1.25F), 1)
                        )
                    )
                ),
                Pair.of(3, new RandomLookAround(UniformInt.of(150, 250), 30.0F, 0.0F, 0.0F)),
                Pair.of(
                    4,
                    new RunOne<>(
                        ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT),
                        ImmutableList.of(
                            Pair.of(RandomStroll.stroll(1.0F), 1),
                            Pair.of(SetWalkTargetFromLookTarget.create(1.0F, 3), 1),
                            Pair.of(new DoNothing(30, 60), 1)
                        )
                    )
                )
            )
        );
    }

    private static void m_323739_(Brain<Armadillo> p_333900_) {
        p_333900_.addActivityWithConditions(
            Activity.PANIC,
            ImmutableList.of(Pair.of(0, new ArmadilloAi.ArmadilloBallUp())),
            Set.of(Pair.of(MemoryModuleType.f_315790_, MemoryStatus.VALUE_PRESENT), Pair.of(MemoryModuleType.IS_PANICKING, MemoryStatus.VALUE_ABSENT))
        );
    }

    public static void m_318618_(Armadillo p_328298_) {
        p_328298_.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.PANIC, Activity.IDLE));
    }

    public static Predicate<ItemStack> m_323902_() {
        return p_330682_ -> p_330682_.is(ItemTags.f_316663_);
    }

    public static class ArmadilloBallUp extends Behavior<Armadillo> {
        static final int f_316786_ = 5 * TimeUtil.f_315347_ * 20;
        static final int f_316110_ = 5;
        static final int f_315080_ = 75;
        int f_314305_ = 0;
        boolean f_314339_;

        public ArmadilloBallUp() {
            super(Map.of(), f_316786_);
        }

        protected void tick(ServerLevel p_334605_, Armadillo p_330148_, long p_334278_) {
            super.tick(p_334605_, p_330148_, p_334278_);
            if (this.f_314305_ > 0) {
                this.f_314305_--;
            }

            if (p_330148_.m_320135_()) {
                p_330148_.m_323579_(Armadillo.ArmadilloState.SCARED);
                if (p_330148_.onGround()) {
                    p_330148_.playSound(SoundEvents.f_315082_);
                }
            } else {
                Armadillo.ArmadilloState armadillo$armadillostate = p_330148_.m_318651_();
                long i = p_330148_.getBrain().getTimeUntilExpiry(MemoryModuleType.f_315790_);
                boolean flag = i > 75L;
                if (flag != this.f_314339_) {
                    this.f_314305_ = this.m_323824_(p_330148_);
                }

                this.f_314339_ = flag;
                if (armadillo$armadillostate == Armadillo.ArmadilloState.SCARED) {
                    if (this.f_314305_ == 0 && p_330148_.onGround() && flag) {
                        p_334605_.broadcastEntityEvent(p_330148_, (byte)64);
                        this.f_314305_ = this.m_323824_(p_330148_);
                    }

                    if (i < (long)Armadillo.ArmadilloState.UNROLLING.m_323052_()) {
                        p_330148_.playSound(SoundEvents.f_314003_);
                        p_330148_.m_323579_(Armadillo.ArmadilloState.UNROLLING);
                    }
                } else if (armadillo$armadillostate == Armadillo.ArmadilloState.UNROLLING && i > (long)Armadillo.ArmadilloState.UNROLLING.m_323052_()) {
                    p_330148_.m_323579_(Armadillo.ArmadilloState.SCARED);
                }
            }
        }

        private int m_323824_(Armadillo p_335572_) {
            return Armadillo.ArmadilloState.SCARED.m_323052_() + p_335572_.getRandom().nextIntBetweenInclusive(100, 400);
        }

        protected boolean checkExtraStartConditions(ServerLevel p_332996_, Armadillo p_331814_) {
            return p_331814_.onGround();
        }

        protected boolean canStillUse(ServerLevel p_333827_, Armadillo p_332835_, long p_327907_) {
            return p_332835_.m_318651_().m_324774_();
        }

        protected void start(ServerLevel p_330354_, Armadillo p_328883_, long p_332299_) {
            p_328883_.m_318878_();
        }

        protected void stop(ServerLevel p_333949_, Armadillo p_328962_, long p_327908_) {
            if (!p_328962_.m_323949_()) {
                p_328962_.m_324029_();
            }
        }
    }

    public static class ArmadilloPanic extends AnimalPanic<Armadillo> {
        public ArmadilloPanic(float p_335847_) {
            super(p_335847_, Armadillo::m_322481_);
        }

        protected void start(ServerLevel p_329316_, Armadillo p_335334_, long p_332229_) {
            p_335334_.m_324029_();
            super.start(p_329316_, p_335334_, p_332229_);
        }
    }
}