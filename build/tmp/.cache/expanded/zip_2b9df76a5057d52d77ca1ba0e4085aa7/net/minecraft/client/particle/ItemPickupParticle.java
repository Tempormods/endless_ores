package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemPickupParticle extends Particle {
    private static final int LIFE_TIME = 3;
    private final RenderBuffers renderBuffers;
    private final Entity itemEntity;
    private final Entity target;
    private int life;
    private final EntityRenderDispatcher entityRenderDispatcher;
    private double f_302522_;
    private double f_302758_;
    private double f_303670_;
    private double f_303041_;
    private double f_302502_;
    private double f_302216_;

    public ItemPickupParticle(EntityRenderDispatcher pEntityRenderDispatcher, RenderBuffers pBuffers, ClientLevel pLevel, Entity pItemEntity, Entity pTarget) {
        this(pEntityRenderDispatcher, pBuffers, pLevel, pItemEntity, pTarget, pItemEntity.getDeltaMovement());
    }

    private ItemPickupParticle(
        EntityRenderDispatcher pEntityRenderDispatcher, RenderBuffers pBuffers, ClientLevel pLevel, Entity pItemEntity, Entity pTarget, Vec3 pSpeedVector
    ) {
        super(pLevel, pItemEntity.getX(), pItemEntity.getY(), pItemEntity.getZ(), pSpeedVector.x, pSpeedVector.y, pSpeedVector.z);
        this.renderBuffers = pBuffers;
        this.itemEntity = this.getSafeCopy(pItemEntity);
        this.target = pTarget;
        this.entityRenderDispatcher = pEntityRenderDispatcher;
        this.m_306961_();
        this.m_307839_();
    }

    private Entity getSafeCopy(Entity pEntity) {
        return (Entity)(!(pEntity instanceof ItemEntity) ? pEntity : ((ItemEntity)pEntity).copy());
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    @Override
    public void render(VertexConsumer pBuffer, Camera pRenderInfo, float pPartialTicks) {
        float f = ((float)this.life + pPartialTicks) / 3.0F;
        f *= f;
        double d0 = Mth.lerp((double)pPartialTicks, this.f_303041_, this.f_302522_);
        double d1 = Mth.lerp((double)pPartialTicks, this.f_302502_, this.f_302758_);
        double d2 = Mth.lerp((double)pPartialTicks, this.f_302216_, this.f_303670_);
        double d3 = Mth.lerp((double)f, this.itemEntity.getX(), d0);
        double d4 = Mth.lerp((double)f, this.itemEntity.getY(), d1);
        double d5 = Mth.lerp((double)f, this.itemEntity.getZ(), d2);
        MultiBufferSource.BufferSource multibuffersource$buffersource = this.renderBuffers.bufferSource();
        Vec3 vec3 = pRenderInfo.getPosition();
        this.entityRenderDispatcher
            .render(
                this.itemEntity,
                d3 - vec3.x(),
                d4 - vec3.y(),
                d5 - vec3.z(),
                this.itemEntity.getYRot(),
                pPartialTicks,
                new PoseStack(),
                multibuffersource$buffersource,
                this.entityRenderDispatcher.getPackedLightCoords(this.itemEntity, pPartialTicks)
            );
        multibuffersource$buffersource.endBatch();
    }

    @Override
    public void tick() {
        this.life++;
        if (this.life == 3) {
            this.remove();
        }

        this.m_307839_();
        this.m_306961_();
    }

    private void m_306961_() {
        this.f_302522_ = this.target.getX();
        this.f_302758_ = (this.target.getY() + this.target.getEyeY()) / 2.0;
        this.f_303670_ = this.target.getZ();
    }

    private void m_307839_() {
        this.f_303041_ = this.f_302522_;
        this.f_302502_ = this.f_302758_;
        this.f_302216_ = this.f_303670_;
    }
}