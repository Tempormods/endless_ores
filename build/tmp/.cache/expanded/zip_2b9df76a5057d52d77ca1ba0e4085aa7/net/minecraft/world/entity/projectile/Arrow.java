package net.minecraft.world.entity.projectile;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;

public class Arrow extends AbstractArrow {
    private static final int EXPOSED_POTION_DECAY_TIME = 600;
    private static final int NO_EFFECT_COLOR = -1;
    private static final EntityDataAccessor<Integer> ID_EFFECT_COLOR = SynchedEntityData.defineId(Arrow.class, EntityDataSerializers.INT);
    private static final byte EVENT_POTION_PUFF = 0;

    public Arrow(EntityType<? extends Arrow> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public Arrow(Level pLevel, double p_312497_, double p_312591_, double p_311058_, ItemStack p_310811_) {
        super(EntityType.ARROW, p_312497_, p_312591_, p_311058_, pLevel, p_310811_);
        this.updateColor();
    }

    public Arrow(Level pLevel, LivingEntity p_310439_, ItemStack p_310691_) {
        super(EntityType.ARROW, p_310439_, pLevel, p_310691_);
        this.updateColor();
    }

    private PotionContents m_324886_() {
        return this.m_307069_().m_322304_(DataComponents.f_314188_, PotionContents.f_313984_);
    }

    private void m_324373_(PotionContents p_328713_) {
        this.m_307069_().m_322496_(DataComponents.f_314188_, p_328713_);
        this.updateColor();
    }

    @Override
    protected void m_320726_(ItemStack p_332340_) {
        super.m_320726_(p_332340_);
        this.updateColor();
    }

    private void updateColor() {
        PotionContents potioncontents = this.m_324886_();
        this.entityData.set(ID_EFFECT_COLOR, potioncontents.equals(PotionContents.f_313984_) ? -1 : potioncontents.m_318943_());
    }

    public void addEffect(MobEffectInstance pEffectInstance) {
        this.m_324373_(this.m_324886_().m_324984_(pEffectInstance));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder p_331799_) {
        super.defineSynchedData(p_331799_);
        p_331799_.m_318949_(ID_EFFECT_COLOR, -1);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            if (this.inGround) {
                if (this.inGroundTime % 5 == 0) {
                    this.makeParticle(1);
                }
            } else {
                this.makeParticle(2);
            }
        } else if (this.inGround && this.inGroundTime != 0 && !this.m_324886_().equals(PotionContents.f_313984_) && this.inGroundTime >= 600) {
            this.level().broadcastEntityEvent(this, (byte)0);
            this.m_320726_(new ItemStack(Items.ARROW));
        }
    }

    private void makeParticle(int pParticleAmount) {
        int i = this.getColor();
        if (i != -1 && pParticleAmount > 0) {
            for (int j = 0; j < pParticleAmount; j++) {
                this.level()
                    .addParticle(ColorParticleOption.m_318840_(ParticleTypes.ENTITY_EFFECT, i), this.getRandomX(0.5), this.getRandomY(), this.getRandomZ(0.5), 0.0, 0.0, 0.0);
            }
        }
    }

    public int getColor() {
        return this.entityData.get(ID_EFFECT_COLOR);
    }

    @Override
    protected void doPostHurtEffects(LivingEntity pLiving) {
        super.doPostHurtEffects(pLiving);
        Entity entity = this.getEffectSource();
        PotionContents potioncontents = this.m_324886_();
        if (potioncontents.f_317059_().isPresent()) {
            for (MobEffectInstance mobeffectinstance : potioncontents.f_317059_().get().value().getEffects()) {
                pLiving.addEffect(
                    new MobEffectInstance(
                        mobeffectinstance.getEffect(),
                        Math.max(mobeffectinstance.mapDuration(p_268168_ -> p_268168_ / 8), 1),
                        mobeffectinstance.getAmplifier(),
                        mobeffectinstance.isAmbient(),
                        mobeffectinstance.isVisible()
                    ),
                    entity
                );
            }
        }

        for (MobEffectInstance mobeffectinstance1 : potioncontents.m_321614_()) {
            pLiving.addEffect(mobeffectinstance1, entity);
        }
    }

    @Override
    protected ItemStack m_321416_() {
        return new ItemStack(Items.ARROW);
    }

    @Override
    public void handleEntityEvent(byte pId) {
        if (pId == 0) {
            int i = this.getColor();
            if (i != -1) {
                float f = (float)(i >> 16 & 0xFF) / 255.0F;
                float f1 = (float)(i >> 8 & 0xFF) / 255.0F;
                float f2 = (float)(i >> 0 & 0xFF) / 255.0F;

                for (int j = 0; j < 20; j++) {
                    this.level()
                        .addParticle(
                            ColorParticleOption.m_321894_(ParticleTypes.ENTITY_EFFECT, f, f1, f2),
                            this.getRandomX(0.5),
                            this.getRandomY(),
                            this.getRandomZ(0.5),
                            0.0,
                            0.0,
                            0.0
                        );
                }
            }
        } else {
            super.handleEntityEvent(pId);
        }
    }
}