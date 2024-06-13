package net.minecraft.world.level.block.entity;

import com.google.common.annotations.VisibleForTesting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SculkCatalystBlock;
import net.minecraft.world.level.block.SculkSpreader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.phys.Vec3;

public class SculkCatalystBlockEntity extends BlockEntity implements GameEventListener.Provider<SculkCatalystBlockEntity.CatalystListener> {
    private final SculkCatalystBlockEntity.CatalystListener catalystListener;

    public SculkCatalystBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityType.SCULK_CATALYST, pPos, pBlockState);
        this.catalystListener = new SculkCatalystBlockEntity.CatalystListener(pBlockState, new BlockPositionSource(pPos));
    }

    public static void serverTick(Level pLevel, BlockPos pPos, BlockState pState, SculkCatalystBlockEntity pSculkCatalyst) {
        pSculkCatalyst.catalystListener.getSculkSpreader().updateCursors(pLevel, pPos, pLevel.getRandom(), true);
    }

    @Override
    protected void m_318667_(CompoundTag p_334885_, HolderLookup.Provider p_332157_) {
        super.m_318667_(p_334885_, p_332157_);
        this.catalystListener.sculkSpreader.load(p_334885_);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider p_332461_) {
        this.catalystListener.sculkSpreader.save(pTag);
        super.saveAdditional(pTag, p_332461_);
    }

    public SculkCatalystBlockEntity.CatalystListener m_280221_() {
        return this.catalystListener;
    }

    public static class CatalystListener implements GameEventListener {
        public static final int PULSE_TICKS = 8;
        final SculkSpreader sculkSpreader;
        private final BlockState blockState;
        private final PositionSource positionSource;

        public CatalystListener(BlockState pBlockState, PositionSource pPositionSource) {
            this.blockState = pBlockState;
            this.positionSource = pPositionSource;
            this.sculkSpreader = SculkSpreader.createLevelSpreader();
        }

        @Override
        public PositionSource getListenerSource() {
            return this.positionSource;
        }

        @Override
        public int getListenerRadius() {
            return 8;
        }

        @Override
        public GameEventListener.DeliveryMode getDeliveryMode() {
            return GameEventListener.DeliveryMode.BY_DISTANCE;
        }

        @Override
        public boolean handleGameEvent(ServerLevel pLevel, Holder<GameEvent> p_332335_, GameEvent.Context pContext, Vec3 pPos) {
            if (p_332335_.m_318604_(GameEvent.ENTITY_DIE) && pContext.sourceEntity() instanceof LivingEntity livingentity) {
                if (!livingentity.wasExperienceConsumed()) {
                    int i = livingentity.getExperienceReward();
                    if (livingentity.shouldDropExperience() && i > 0) {
                        this.sculkSpreader.addCursors(BlockPos.containing(pPos.relative(Direction.UP, 0.5)), i);
                        this.tryAwardItSpreadsAdvancement(pLevel, livingentity);
                    }

                    livingentity.skipDropExperience();
                    this.positionSource
                        .getPosition(pLevel)
                        .ifPresent(p_327311_ -> this.bloom(pLevel, BlockPos.containing(p_327311_), this.blockState, pLevel.getRandom()));
                }

                return true;
            } else {
                return false;
            }
        }

        @VisibleForTesting
        public SculkSpreader getSculkSpreader() {
            return this.sculkSpreader;
        }

        private void bloom(ServerLevel pLevel, BlockPos pPos, BlockState pState, RandomSource pRandom) {
            pLevel.setBlock(pPos, pState.setValue(SculkCatalystBlock.PULSE, Boolean.valueOf(true)), 3);
            pLevel.scheduleTick(pPos, pState.getBlock(), 8);
            pLevel.sendParticles(
                ParticleTypes.SCULK_SOUL,
                (double)pPos.getX() + 0.5,
                (double)pPos.getY() + 1.15,
                (double)pPos.getZ() + 0.5,
                2,
                0.2,
                0.0,
                0.2,
                0.0
            );
            pLevel.playSound(null, pPos, SoundEvents.SCULK_CATALYST_BLOOM, SoundSource.BLOCKS, 2.0F, 0.6F + pRandom.nextFloat() * 0.4F);
        }

        private void tryAwardItSpreadsAdvancement(Level pLevel, LivingEntity pEntity) {
            if (pEntity.getLastHurtByMob() instanceof ServerPlayer serverplayer) {
                DamageSource damagesource = pEntity.getLastDamageSource() == null ? pLevel.damageSources().playerAttack(serverplayer) : pEntity.getLastDamageSource();
                CriteriaTriggers.KILL_MOB_NEAR_SCULK_CATALYST.trigger(serverplayer, pEntity, damagesource);
            }
        }
    }
}