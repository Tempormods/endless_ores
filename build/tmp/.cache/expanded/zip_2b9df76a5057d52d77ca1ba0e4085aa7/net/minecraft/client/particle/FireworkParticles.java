package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FireworkParticles {
    @OnlyIn(Dist.CLIENT)
    public static class FlashProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public FlashProvider(SpriteSet pSprites) {
            this.sprite = pSprites;
        }

        public Particle createParticle(
            SimpleParticleType pType,
            ClientLevel pLevel,
            double pX,
            double pY,
            double pZ,
            double pXSpeed,
            double pYSpeed,
            double pZSpeed
        ) {
            FireworkParticles.OverlayParticle fireworkparticles$overlayparticle = new FireworkParticles.OverlayParticle(
                pLevel, pX, pY, pZ
            );
            fireworkparticles$overlayparticle.pickSprite(this.sprite);
            return fireworkparticles$overlayparticle;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class OverlayParticle extends TextureSheetParticle {
        OverlayParticle(ClientLevel pLevel, double pX, double pY, double pZ) {
            super(pLevel, pX, pY, pZ);
            this.lifetime = 4;
        }

        @Override
        public ParticleRenderType getRenderType() {
            return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
        }

        @Override
        public void render(VertexConsumer pBuffer, Camera pRenderInfo, float pPartialTicks) {
            this.setAlpha(0.6F - ((float)this.age + pPartialTicks - 1.0F) * 0.25F * 0.5F);
            super.render(pBuffer, pRenderInfo, pPartialTicks);
        }

        @Override
        public float getQuadSize(float pScaleFactor) {
            return 7.1F * Mth.sin(((float)this.age + pScaleFactor - 1.0F) * 0.25F * (float) Math.PI);
        }
    }

    @OnlyIn(Dist.CLIENT)
    static class SparkParticle extends SimpleAnimatedParticle {
        private boolean trail;
        private boolean f_316321_;
        private final ParticleEngine engine;
        private float fadeR;
        private float fadeG;
        private float fadeB;
        private boolean hasFade;

        SparkParticle(
            ClientLevel pLevel,
            double pX,
            double pY,
            double pZ,
            double pXSpeed,
            double pYSpeed,
            double pZSpeed,
            ParticleEngine pEngine,
            SpriteSet pSprites
        ) {
            super(pLevel, pX, pY, pZ, pSprites, 0.1F);
            this.xd = pXSpeed;
            this.yd = pYSpeed;
            this.zd = pZSpeed;
            this.engine = pEngine;
            this.quadSize *= 0.75F;
            this.lifetime = 48 + this.random.nextInt(12);
            this.setSpriteFromAge(pSprites);
        }

        public void setTrail(boolean pTrail) {
            this.trail = pTrail;
        }

        public void m_323654_(boolean p_331075_) {
            this.f_316321_ = p_331075_;
        }

        @Override
        public void render(VertexConsumer pBuffer, Camera pRenderInfo, float pPartialTicks) {
            if (!this.f_316321_ || this.age < this.lifetime / 3 || (this.age + this.lifetime) / 3 % 2 == 0) {
                super.render(pBuffer, pRenderInfo, pPartialTicks);
            }
        }

        @Override
        public void tick() {
            super.tick();
            if (this.trail && this.age < this.lifetime / 2 && (this.age + this.lifetime) % 2 == 0) {
                FireworkParticles.SparkParticle fireworkparticles$sparkparticle = new FireworkParticles.SparkParticle(
                    this.level, this.x, this.y, this.z, 0.0, 0.0, 0.0, this.engine, this.sprites
                );
                fireworkparticles$sparkparticle.setAlpha(0.99F);
                fireworkparticles$sparkparticle.setColor(this.rCol, this.gCol, this.bCol);
                fireworkparticles$sparkparticle.age = fireworkparticles$sparkparticle.lifetime / 2;
                if (this.hasFade) {
                    fireworkparticles$sparkparticle.hasFade = true;
                    fireworkparticles$sparkparticle.fadeR = this.fadeR;
                    fireworkparticles$sparkparticle.fadeG = this.fadeG;
                    fireworkparticles$sparkparticle.fadeB = this.fadeB;
                }

                fireworkparticles$sparkparticle.f_316321_ = this.f_316321_;
                this.engine.add(fireworkparticles$sparkparticle);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class SparkProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public SparkProvider(SpriteSet pSprites) {
            this.sprites = pSprites;
        }

        public Particle createParticle(
            SimpleParticleType pType,
            ClientLevel pLevel,
            double pX,
            double pY,
            double pZ,
            double pXSpeed,
            double pYSpeed,
            double pZSpeed
        ) {
            FireworkParticles.SparkParticle fireworkparticles$sparkparticle = new FireworkParticles.SparkParticle(
                pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, Minecraft.getInstance().particleEngine, this.sprites
            );
            fireworkparticles$sparkparticle.setAlpha(0.99F);
            return fireworkparticles$sparkparticle;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class Starter extends NoRenderParticle {
        private static final double[][] f_314340_ = new double[][]{
            {0.0, 0.2}, {0.2, 0.2}, {0.2, 0.6}, {0.6, 0.6}, {0.6, 0.2}, {0.2, 0.2}, {0.2, 0.0}, {0.4, 0.0}, {0.4, -0.6}, {0.2, -0.6}, {0.2, -0.4}, {0.0, -0.4}
        };
        private static final double[][] f_314345_ = new double[][]{
            {0.0, 1.0},
            {0.3455, 0.309},
            {0.9511, 0.309},
            {0.3795918367346939, -0.12653061224489795},
            {0.6122448979591837, -0.8040816326530612},
            {0.0, -0.35918367346938773}
        };
        private int life;
        private final ParticleEngine engine;
        private final List<FireworkExplosion> explosions;
        private boolean twinkleDelay;

        public Starter(
            ClientLevel pLevel,
            double pX,
            double pY,
            double pZ,
            double pXSpeed,
            double pYSpeed,
            double pZSpeed,
            ParticleEngine pEngine,
            List<FireworkExplosion> p_332725_
        ) {
            super(pLevel, pX, pY, pZ);
            this.xd = pXSpeed;
            this.yd = pYSpeed;
            this.zd = pZSpeed;
            this.engine = pEngine;
            if (p_332725_.isEmpty()) {
                throw new IllegalArgumentException("Cannot create firework starter with no explosions");
            } else {
                this.explosions = p_332725_;
                this.lifetime = p_332725_.size() * 2 - 1;

                for (FireworkExplosion fireworkexplosion : p_332725_) {
                    if (fireworkexplosion.f_316285_()) {
                        this.twinkleDelay = true;
                        this.lifetime += 15;
                        break;
                    }
                }
            }
        }

        @Override
        public void tick() {
            if (this.life == 0) {
                boolean flag = this.isFarAwayFromCamera();
                boolean flag1 = false;
                if (this.explosions.size() >= 3) {
                    flag1 = true;
                } else {
                    for (FireworkExplosion fireworkexplosion : this.explosions) {
                        if (fireworkexplosion.f_316547_() == FireworkExplosion.Shape.LARGE_BALL) {
                            flag1 = true;
                            break;
                        }
                    }
                }

                SoundEvent soundevent1;
                if (flag1) {
                    soundevent1 = flag ? SoundEvents.FIREWORK_ROCKET_LARGE_BLAST_FAR : SoundEvents.FIREWORK_ROCKET_LARGE_BLAST;
                } else {
                    soundevent1 = flag ? SoundEvents.FIREWORK_ROCKET_BLAST_FAR : SoundEvents.FIREWORK_ROCKET_BLAST;
                }

                this.level
                    .playLocalSound(
                        this.x,
                        this.y,
                        this.z,
                        soundevent1,
                        SoundSource.AMBIENT,
                        20.0F,
                        0.95F + this.random.nextFloat() * 0.1F,
                        true
                    );
            }

            if (this.life % 2 == 0 && this.life / 2 < this.explosions.size()) {
                int j = this.life / 2;
                FireworkExplosion fireworkexplosion1 = this.explosions.get(j);
                boolean flag3 = fireworkexplosion1.f_316522_();
                boolean flag4 = fireworkexplosion1.f_316285_();
                IntList intlist = fireworkexplosion1.f_316201_();
                IntList intlist1 = fireworkexplosion1.f_314743_();
                if (intlist.isEmpty()) {
                    intlist = IntList.of(DyeColor.BLACK.getFireworkColor());
                }

                var factory = net.minecraftforge.client.FireworkShapeFactoryRegistry.get(fireworkexplosion1.f_316547_());
                if (factory != null)
                    factory.build(this, flag3, flag4, intlist.toIntArray(), intlist1.toIntArray());
                else
                switch (fireworkexplosion1.f_316547_()) {
                    case SMALL_BALL:
                        this.createParticleBall(0.25, 2, intlist, intlist1, flag3, flag4);
                        break;
                    case LARGE_BALL:
                        this.createParticleBall(0.5, 4, intlist, intlist1, flag3, flag4);
                        break;
                    case STAR:
                        this.createParticleShape(0.5, f_314345_, intlist, intlist1, flag3, flag4, false);
                        break;
                    case CREEPER:
                        this.createParticleShape(0.5, f_314340_, intlist, intlist1, flag3, flag4, true);
                        break;
                    case BURST:
                        this.createParticleBurst(intlist, intlist1, flag3, flag4);
                }

                int i = intlist.getInt(0);
                Particle particle = this.engine.createParticle(ParticleTypes.FLASH, this.x, this.y, this.z, 0.0, 0.0, 0.0);
                particle.setColor(
                    (float)FastColor.ARGB32.red(i) / 255.0F, (float)FastColor.ARGB32.green(i) / 255.0F, (float)FastColor.ARGB32.blue(i) / 255.0F
                );
            }

            this.life++;
            if (this.life > this.lifetime) {
                if (this.twinkleDelay) {
                    boolean flag2 = this.isFarAwayFromCamera();
                    SoundEvent soundevent = flag2 ? SoundEvents.FIREWORK_ROCKET_TWINKLE_FAR : SoundEvents.FIREWORK_ROCKET_TWINKLE;
                    this.level
                        .playLocalSound(
                            this.x,
                            this.y,
                            this.z,
                            soundevent,
                            SoundSource.AMBIENT,
                            20.0F,
                            0.9F + this.random.nextFloat() * 0.15F,
                            true
                        );
                }

                this.remove();
            }
        }

        private boolean isFarAwayFromCamera() {
            Minecraft minecraft = Minecraft.getInstance();
            return minecraft.gameRenderer.getMainCamera().getPosition().distanceToSqr(this.x, this.y, this.z) >= 256.0;
        }

        private void createParticle(
            double pX,
            double pY,
            double pZ,
            double pMotionX,
            double pMotionY,
            double pMotionZ,
            IntList p_329729_,
            IntList p_330193_,
            boolean pHasTrail,
            boolean pHasTwinkle
        ) {
            FireworkParticles.SparkParticle fireworkparticles$sparkparticle = (FireworkParticles.SparkParticle)this.engine
                .createParticle(ParticleTypes.FIREWORK, pX, pY, pZ, pMotionX, pMotionY, pMotionZ);
            fireworkparticles$sparkparticle.setTrail(pHasTrail);
            fireworkparticles$sparkparticle.m_323654_(pHasTwinkle);
            fireworkparticles$sparkparticle.setAlpha(0.99F);
            fireworkparticles$sparkparticle.setColor(Util.getRandom(p_329729_, this.random));
            if (!p_330193_.isEmpty()) {
                fireworkparticles$sparkparticle.setFadeColor(Util.getRandom(p_330193_, this.random));
            }
        }

        private void createParticleBall(double pSpeed, int pSize, IntList p_331387_, IntList p_331480_, boolean pTrail, boolean pTwinkle) {
            double d0 = this.x;
            double d1 = this.y;
            double d2 = this.z;

            for (int i = -pSize; i <= pSize; i++) {
                for (int j = -pSize; j <= pSize; j++) {
                    for (int k = -pSize; k <= pSize; k++) {
                        double d3 = (double)j + (this.random.nextDouble() - this.random.nextDouble()) * 0.5;
                        double d4 = (double)i + (this.random.nextDouble() - this.random.nextDouble()) * 0.5;
                        double d5 = (double)k + (this.random.nextDouble() - this.random.nextDouble()) * 0.5;
                        double d6 = Math.sqrt(d3 * d3 + d4 * d4 + d5 * d5) / pSpeed + this.random.nextGaussian() * 0.05;
                        this.createParticle(d0, d1, d2, d3 / d6, d4 / d6, d5 / d6, p_331387_, p_331480_, pTrail, pTwinkle);
                        if (i != -pSize && i != pSize && j != -pSize && j != pSize) {
                            k += pSize * 2 - 1;
                        }
                    }
                }
            }
        }

        private void createParticleShape(
            double pSpeed, double[][] pShape, IntList p_330103_, IntList p_332201_, boolean pTrail, boolean pTwinkle, boolean pCreeper
        ) {
            double d0 = pShape[0][0];
            double d1 = pShape[0][1];
            this.createParticle(this.x, this.y, this.z, d0 * pSpeed, d1 * pSpeed, 0.0, p_330103_, p_332201_, pTrail, pTwinkle);
            float f = this.random.nextFloat() * (float) Math.PI;
            double d2 = pCreeper ? 0.034 : 0.34;

            for (int i = 0; i < 3; i++) {
                double d3 = (double)f + (double)((float)i * (float) Math.PI) * d2;
                double d4 = d0;
                double d5 = d1;

                for (int j = 1; j < pShape.length; j++) {
                    double d6 = pShape[j][0];
                    double d7 = pShape[j][1];

                    for (double d8 = 0.25; d8 <= 1.0; d8 += 0.25) {
                        double d9 = Mth.lerp(d8, d4, d6) * pSpeed;
                        double d10 = Mth.lerp(d8, d5, d7) * pSpeed;
                        double d11 = d9 * Math.sin(d3);
                        d9 *= Math.cos(d3);

                        for (double d12 = -1.0; d12 <= 1.0; d12 += 2.0) {
                            this.createParticle(this.x, this.y, this.z, d9 * d12, d10, d11 * d12, p_330103_, p_332201_, pTrail, pTwinkle);
                        }
                    }

                    d4 = d6;
                    d5 = d7;
                }
            }
        }

        private void createParticleBurst(IntList p_336354_, IntList p_328829_, boolean pTrail, boolean pTwinkle) {
            double d0 = this.random.nextGaussian() * 0.05;
            double d1 = this.random.nextGaussian() * 0.05;

            for (int i = 0; i < 70; i++) {
                double d2 = this.xd * 0.5 + this.random.nextGaussian() * 0.15 + d0;
                double d3 = this.zd * 0.5 + this.random.nextGaussian() * 0.15 + d1;
                double d4 = this.yd * 0.5 + this.random.nextDouble() * 0.5;
                this.createParticle(this.x, this.y, this.z, d2, d4, d3, p_336354_, p_328829_, pTrail, pTwinkle);
            }
        }
    }
}
