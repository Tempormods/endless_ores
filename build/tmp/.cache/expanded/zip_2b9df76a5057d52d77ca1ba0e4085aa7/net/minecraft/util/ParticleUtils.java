package net.minecraft.util;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class ParticleUtils {
    public static void spawnParticlesOnBlockFaces(Level pLevel, BlockPos pPos, ParticleOptions pParticle, IntProvider pCount) {
        for (Direction direction : Direction.values()) {
            spawnParticlesOnBlockFace(pLevel, pPos, pParticle, pCount, direction, () -> getRandomSpeedRanges(pLevel.random), 0.55);
        }
    }

    public static void spawnParticlesOnBlockFace(
        Level pLevel, BlockPos pPos, ParticleOptions pParticle, IntProvider pCount, Direction pDirection, Supplier<Vec3> pSpeedSupplier, double p_216325_
    ) {
        int i = pCount.sample(pLevel.random);

        for (int j = 0; j < i; j++) {
            spawnParticleOnFace(pLevel, pPos, pDirection, pParticle, pSpeedSupplier.get(), p_216325_);
        }
    }

    private static Vec3 getRandomSpeedRanges(RandomSource pRandom) {
        return new Vec3(Mth.nextDouble(pRandom, -0.5, 0.5), Mth.nextDouble(pRandom, -0.5, 0.5), Mth.nextDouble(pRandom, -0.5, 0.5));
    }

    public static void spawnParticlesAlongAxis(
        Direction.Axis pAxis, Level pLevel, BlockPos pPos, double p_144971_, ParticleOptions pParticle, UniformInt pCount
    ) {
        Vec3 vec3 = Vec3.atCenterOf(pPos);
        boolean flag = pAxis == Direction.Axis.X;
        boolean flag1 = pAxis == Direction.Axis.Y;
        boolean flag2 = pAxis == Direction.Axis.Z;
        int i = pCount.sample(pLevel.random);

        for (int j = 0; j < i; j++) {
            double d0 = vec3.x + Mth.nextDouble(pLevel.random, -1.0, 1.0) * (flag ? 0.5 : p_144971_);
            double d1 = vec3.y + Mth.nextDouble(pLevel.random, -1.0, 1.0) * (flag1 ? 0.5 : p_144971_);
            double d2 = vec3.z + Mth.nextDouble(pLevel.random, -1.0, 1.0) * (flag2 ? 0.5 : p_144971_);
            double d3 = flag ? Mth.nextDouble(pLevel.random, -1.0, 1.0) : 0.0;
            double d4 = flag1 ? Mth.nextDouble(pLevel.random, -1.0, 1.0) : 0.0;
            double d5 = flag2 ? Mth.nextDouble(pLevel.random, -1.0, 1.0) : 0.0;
            pLevel.addParticle(pParticle, d0, d1, d2, d3, d4, d5);
        }
    }

    public static void spawnParticleOnFace(Level pLevel, BlockPos pPos, Direction pDirection, ParticleOptions pParticle, Vec3 pSpeed, double p_216312_) {
        Vec3 vec3 = Vec3.atCenterOf(pPos);
        int i = pDirection.getStepX();
        int j = pDirection.getStepY();
        int k = pDirection.getStepZ();
        double d0 = vec3.x + (i == 0 ? Mth.nextDouble(pLevel.random, -0.5, 0.5) : (double)i * p_216312_);
        double d1 = vec3.y + (j == 0 ? Mth.nextDouble(pLevel.random, -0.5, 0.5) : (double)j * p_216312_);
        double d2 = vec3.z + (k == 0 ? Mth.nextDouble(pLevel.random, -0.5, 0.5) : (double)k * p_216312_);
        double d3 = i == 0 ? pSpeed.x() : 0.0;
        double d4 = j == 0 ? pSpeed.y() : 0.0;
        double d5 = k == 0 ? pSpeed.z() : 0.0;
        pLevel.addParticle(pParticle, d0, d1, d2, d3, d4, d5);
    }

    public static void spawnParticleBelow(Level pLevel, BlockPos pPos, RandomSource pRandom, ParticleOptions pParticle) {
        double d0 = (double)pPos.getX() + pRandom.nextDouble();
        double d1 = (double)pPos.getY() - 0.05;
        double d2 = (double)pPos.getZ() + pRandom.nextDouble();
        pLevel.addParticle(pParticle, d0, d1, d2, 0.0, 0.0, 0.0);
    }

    public static void m_320303_(LevelAccessor p_335531_, BlockPos p_329785_, int p_335673_, ParticleOptions p_330338_) {
        double d0 = 0.5;
        BlockState blockstate = p_335531_.getBlockState(p_329785_);
        double d1 = blockstate.isAir() ? 1.0 : blockstate.getShape(p_335531_, p_329785_).max(Direction.Axis.Y);
        m_324507_(p_335531_, p_329785_, p_335673_, 0.5, d1, true, p_330338_);
    }

    public static void m_324507_(
        LevelAccessor p_332146_, BlockPos p_333994_, int p_332880_, double p_335286_, double p_334021_, boolean p_328793_, ParticleOptions p_329517_
    ) {
        RandomSource randomsource = p_332146_.getRandom();

        for (int i = 0; i < p_332880_; i++) {
            double d0 = randomsource.nextGaussian() * 0.02;
            double d1 = randomsource.nextGaussian() * 0.02;
            double d2 = randomsource.nextGaussian() * 0.02;
            double d3 = 0.5 - p_335286_;
            double d4 = (double)p_333994_.getX() + d3 + randomsource.nextDouble() * p_335286_ * 2.0;
            double d5 = (double)p_333994_.getY() + randomsource.nextDouble() * p_334021_;
            double d6 = (double)p_333994_.getZ() + d3 + randomsource.nextDouble() * p_335286_ * 2.0;
            if (p_328793_ || !p_332146_.getBlockState(BlockPos.containing(d4, d5, d6).below()).isAir()) {
                p_332146_.addParticle(p_329517_, d4, d5, d6, d0, d1, d2);
            }
        }
    }

    public static void m_324552_(LevelAccessor p_333323_, BlockPos p_331250_, int p_329230_) {
        Vec3 vec3 = p_331250_.getCenter().add(0.0, 0.5, 0.0);
        BlockParticleOption blockparticleoption = new BlockParticleOption(ParticleTypes.f_314186_, p_333323_.getBlockState(p_331250_));

        for (int i = 0; (float)i < (float)p_329230_ / 3.0F; i++) {
            double d0 = vec3.x + p_333323_.getRandom().nextGaussian() / 2.0;
            double d1 = vec3.y;
            double d2 = vec3.z + p_333323_.getRandom().nextGaussian() / 2.0;
            double d3 = p_333323_.getRandom().nextGaussian() * 0.2F;
            double d4 = p_333323_.getRandom().nextGaussian() * 0.2F;
            double d5 = p_333323_.getRandom().nextGaussian() * 0.2F;
            p_333323_.addParticle(blockparticleoption, d0, d1, d2, d3, d4, d5);
        }

        for (int j = 0; (float)j < (float)p_329230_ / 1.5F; j++) {
            double d6 = vec3.x + 3.5 * Math.cos((double)j) + p_333323_.getRandom().nextGaussian() / 2.0;
            double d7 = vec3.y;
            double d8 = vec3.z + 3.5 * Math.sin((double)j) + p_333323_.getRandom().nextGaussian() / 2.0;
            double d9 = p_333323_.getRandom().nextGaussian() * 0.05F;
            double d10 = p_333323_.getRandom().nextGaussian() * 0.05F;
            double d11 = p_333323_.getRandom().nextGaussian() * 0.05F;
            p_333323_.addParticle(blockparticleoption, d6, d7, d8, d9, d10, d11);
        }
    }
}