package net.minecraft.world.entity.monster.breeze;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.LongJumpUtil;
import net.minecraft.world.entity.ai.behavior.Swim;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class LongJump extends Behavior<Breeze> {
    private static final int f_302520_ = 4;
    private static final int f_302830_ = 10;
    private static final int f_303106_ = 2;
    private static final int f_302385_ = Math.round(10.0F);
    private static final float f_303309_ = 1.4F;
    private static final ObjectArrayList<Integer> f_303569_ = new ObjectArrayList<>(Lists.newArrayList(40, 55, 60, 75, 80));

    @VisibleForTesting
    public LongJump() {
        super(
            Map.of(
                MemoryModuleType.ATTACK_TARGET,
                MemoryStatus.VALUE_PRESENT,
                MemoryModuleType.f_303679_,
                MemoryStatus.VALUE_ABSENT,
                MemoryModuleType.f_302823_,
                MemoryStatus.REGISTERED,
                MemoryModuleType.f_303370_,
                MemoryStatus.REGISTERED,
                MemoryModuleType.f_302834_,
                MemoryStatus.VALUE_ABSENT,
                MemoryModuleType.WALK_TARGET,
                MemoryStatus.VALUE_ABSENT,
                MemoryModuleType.f_315858_,
                MemoryStatus.REGISTERED
            ),
            200
        );
    }

    public static boolean m_322743_(ServerLevel p_328434_, Breeze p_330036_) {
        if (!p_330036_.onGround() && !p_330036_.isInWater()) {
            return false;
        } else if (Swim.m_319678_(p_330036_)) {
            return false;
        } else if (p_330036_.getBrain().checkMemory(MemoryModuleType.f_303370_, MemoryStatus.VALUE_PRESENT)) {
            return true;
        } else {
            LivingEntity livingentity = p_330036_.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
            if (livingentity == null) {
                return false;
            } else if (m_305810_(p_330036_, livingentity)) {
                p_330036_.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
                return false;
            } else if (m_305854_(p_330036_, livingentity)) {
                return false;
            } else if (!m_306406_(p_328434_, p_330036_)) {
                return false;
            } else {
                BlockPos blockpos = m_307423_(p_330036_, BreezeUtil.m_318815_(livingentity, p_330036_.getRandom()));
                if (blockpos == null) {
                    return false;
                } else {
                    BlockState blockstate = p_328434_.getBlockState(blockpos.below());
                    if (p_330036_.getType().isBlockDangerous(blockstate)) {
                        return false;
                    } else if (!BreezeUtil.m_320427_(p_330036_, blockpos.getCenter()) && !BreezeUtil.m_320427_(p_330036_, blockpos.above(4).getCenter())) {
                        return false;
                    } else {
                        p_330036_.getBrain().setMemory(MemoryModuleType.f_303370_, blockpos);
                        return true;
                    }
                }
            }
        }
    }

    protected boolean checkExtraStartConditions(ServerLevel p_312411_, Breeze p_309539_) {
        return m_322743_(p_312411_, p_309539_);
    }

    protected boolean canStillUse(ServerLevel p_310673_, Breeze p_311330_, long p_310051_) {
        return p_311330_.getPose() != Pose.STANDING && !p_311330_.getBrain().hasMemoryValue(MemoryModuleType.f_303679_);
    }

    protected void start(ServerLevel p_310741_, Breeze p_312948_, long p_311377_) {
        if (p_312948_.getBrain().checkMemory(MemoryModuleType.f_302823_, MemoryStatus.VALUE_ABSENT)) {
            p_312948_.getBrain().setMemoryWithExpiry(MemoryModuleType.f_302823_, Unit.INSTANCE, (long)f_302385_);
        }

        p_312948_.setPose(Pose.INHALING);
        p_310741_.playSound(null, p_312948_, SoundEvents.f_314895_, SoundSource.HOSTILE, 1.0F, 1.0F);
        p_312948_.getBrain()
            .getMemory(MemoryModuleType.f_303370_)
            .ifPresent(p_311106_ -> p_312948_.lookAt(EntityAnchorArgument.Anchor.EYES, p_311106_.getCenter()));
    }

    protected void tick(ServerLevel p_312629_, Breeze p_310204_, long p_313176_) {
        boolean flag = p_310204_.isInWater();
        if (!flag && p_310204_.getBrain().checkMemory(MemoryModuleType.f_315858_, MemoryStatus.VALUE_PRESENT)) {
            p_310204_.getBrain().eraseMemory(MemoryModuleType.f_315858_);
        }

        if (m_322162_(p_310204_)) {
            Vec3 vec3 = p_310204_.getBrain()
                .getMemory(MemoryModuleType.f_303370_)
                .flatMap(p_327018_ -> m_307982_(p_310204_, p_310204_.getRandom(), Vec3.atBottomCenterOf(p_327018_)))
                .orElse(null);
            if (vec3 == null) {
                p_310204_.setPose(Pose.STANDING);
                return;
            }

            if (flag) {
                p_310204_.getBrain().setMemory(MemoryModuleType.f_315858_, Unit.INSTANCE);
            }

            p_310204_.playSound(SoundEvents.f_303771_, 1.0F, 1.0F);
            p_310204_.setPose(Pose.LONG_JUMPING);
            p_310204_.setYRot(p_310204_.yBodyRot);
            p_310204_.setDiscardFriction(true);
            p_310204_.setDeltaMovement(vec3);
        } else if (m_321687_(p_310204_)) {
            p_310204_.playSound(SoundEvents.f_303831_, 1.0F, 1.0F);
            p_310204_.setPose(Pose.STANDING);
            p_310204_.setDiscardFriction(false);
            boolean flag1 = p_310204_.getBrain().hasMemoryValue(MemoryModuleType.HURT_BY);
            p_310204_.getBrain().setMemoryWithExpiry(MemoryModuleType.f_303679_, Unit.INSTANCE, flag1 ? 2L : 10L);
            p_310204_.getBrain().setMemoryWithExpiry(MemoryModuleType.f_302834_, Unit.INSTANCE, 100L);
        }
    }

    protected void stop(ServerLevel p_309511_, Breeze p_311681_, long p_312980_) {
        if (p_311681_.getPose() == Pose.LONG_JUMPING || p_311681_.getPose() == Pose.INHALING) {
            p_311681_.setPose(Pose.STANDING);
        }

        p_311681_.getBrain().eraseMemory(MemoryModuleType.f_303370_);
        p_311681_.getBrain().eraseMemory(MemoryModuleType.f_302823_);
        p_311681_.getBrain().eraseMemory(MemoryModuleType.f_315858_);
    }

    private static boolean m_322162_(Breeze p_330141_) {
        return p_330141_.getBrain().getMemory(MemoryModuleType.f_302823_).isEmpty() && p_330141_.getPose() == Pose.INHALING;
    }

    private static boolean m_321687_(Breeze p_330755_) {
        boolean flag = p_330755_.getPose() == Pose.LONG_JUMPING;
        boolean flag1 = p_330755_.onGround();
        boolean flag2 = p_330755_.isInWater() && p_330755_.getBrain().checkMemory(MemoryModuleType.f_315858_, MemoryStatus.VALUE_ABSENT);
        return flag && (flag1 || flag2);
    }

    @Nullable
    private static BlockPos m_307423_(LivingEntity p_312785_, Vec3 p_311613_) {
        ClipContext clipcontext = new ClipContext(
            p_311613_, p_311613_.relative(Direction.DOWN, 10.0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, p_312785_
        );
        HitResult hitresult = p_312785_.level().clip(clipcontext);
        if (hitresult.getType() == HitResult.Type.BLOCK) {
            return BlockPos.containing(hitresult.getLocation()).above();
        } else {
            ClipContext clipcontext1 = new ClipContext(
                p_311613_, p_311613_.relative(Direction.UP, 10.0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, p_312785_
            );
            HitResult hitresult1 = p_312785_.level().clip(clipcontext1);
            return hitresult1.getType() == HitResult.Type.BLOCK ? BlockPos.containing(hitresult1.getLocation()).above() : null;
        }
    }

    private static boolean m_305810_(Breeze p_310244_, LivingEntity p_309508_) {
        return !p_309508_.closerThan(p_310244_, 24.0);
    }

    private static boolean m_305854_(Breeze p_310091_, LivingEntity p_311303_) {
        return p_311303_.distanceTo(p_310091_) - 4.0F <= 0.0F;
    }

    private static boolean m_306406_(ServerLevel p_312023_, Breeze p_313218_) {
        BlockPos blockpos = p_313218_.blockPosition();

        for (int i = 1; i <= 4; i++) {
            BlockPos blockpos1 = blockpos.relative(Direction.UP, i);
            if (!p_312023_.getBlockState(blockpos1).isAir() && !p_312023_.getFluidState(blockpos1).is(FluidTags.WATER)) {
                return false;
            }
        }

        return true;
    }

    private static Optional<Vec3> m_307982_(Breeze p_310143_, RandomSource p_313023_, Vec3 p_309973_) {
        for (int i : Util.shuffledCopy(f_303569_, p_313023_)) {
            Optional<Vec3> optional = LongJumpUtil.m_305962_(p_310143_, p_309973_, 1.4F, i, false);
            if (optional.isPresent()) {
                return optional;
            }
        }

        return Optional.empty();
    }
}