package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FishingHookRenderer extends EntityRenderer<FishingHook> {
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/entity/fishing_hook.png");
    private static final RenderType RENDER_TYPE = RenderType.entityCutout(TEXTURE_LOCATION);
    private static final double VIEW_BOBBING_SCALE = 960.0;

    public FishingHookRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    public void render(FishingHook pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        Player player = pEntity.getPlayerOwner();
        if (player != null) {
            pPoseStack.pushPose();
            pPoseStack.pushPose();
            pPoseStack.scale(0.5F, 0.5F, 0.5F);
            pPoseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
            pPoseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            PoseStack.Pose posestack$pose = pPoseStack.last();
            VertexConsumer vertexconsumer = pBuffer.getBuffer(RENDER_TYPE);
            vertex(vertexconsumer, posestack$pose, pPackedLight, 0.0F, 0, 0, 1);
            vertex(vertexconsumer, posestack$pose, pPackedLight, 1.0F, 0, 1, 1);
            vertex(vertexconsumer, posestack$pose, pPackedLight, 1.0F, 1, 1, 0);
            vertex(vertexconsumer, posestack$pose, pPackedLight, 0.0F, 1, 0, 0);
            pPoseStack.popPose();
            float f = player.getAttackAnim(pPartialTicks);
            float f1 = Mth.sin(Mth.sqrt(f) * (float) Math.PI);
            Vec3 vec3 = this.m_322634_(player, f1, pPartialTicks);
            Vec3 vec31 = pEntity.getPosition(pPartialTicks).add(0.0, 0.25, 0.0);
            float f2 = (float)(vec3.x - vec31.x);
            float f3 = (float)(vec3.y - vec31.y);
            float f4 = (float)(vec3.z - vec31.z);
            VertexConsumer vertexconsumer1 = pBuffer.getBuffer(RenderType.lineStrip());
            PoseStack.Pose posestack$pose1 = pPoseStack.last();
            int i = 16;

            for (int j = 0; j <= 16; j++) {
                stringVertex(f2, f3, f4, vertexconsumer1, posestack$pose1, fraction(j, 16), fraction(j + 1, 16));
            }

            pPoseStack.popPose();
            super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
        }
    }

    private Vec3 m_322634_(Player p_328037_, float p_328369_, float p_332926_) {
        int i = p_328037_.getMainArm() == HumanoidArm.RIGHT ? 1 : -1;
        ItemStack itemstack = p_328037_.getMainHandItem();
        if (!itemstack.canPerformAction(net.minecraftforge.common.ToolActions.FISHING_ROD_CAST)) {
            i = -i;
        }

        if (this.entityRenderDispatcher.options.getCameraType().isFirstPerson() && p_328037_ == Minecraft.getInstance().player) {
            double d4 = 960.0 / (double)this.entityRenderDispatcher.options.fov().get().intValue();
            Vec3 vec3 = this.entityRenderDispatcher
                .camera
                .getNearPlane()
                .getPointOnPlane((float)i * 0.525F, -0.1F)
                .scale(d4)
                .yRot(p_328369_ * 0.5F)
                .xRot(-p_328369_ * 0.7F);
            return p_328037_.getEyePosition(p_332926_).add(vec3);
        } else {
            float f = Mth.lerp(p_332926_, p_328037_.yBodyRotO, p_328037_.yBodyRot) * (float) (Math.PI / 180.0);
            double d0 = (double)Mth.sin(f);
            double d1 = (double)Mth.cos(f);
            float f1 = p_328037_.getScale();
            double d2 = (double)i * 0.35 * (double)f1;
            double d3 = 0.8 * (double)f1;
            float f2 = p_328037_.isCrouching() ? -0.1875F : 0.0F;
            return p_328037_.getEyePosition(p_332926_).add(-d1 * d2 - d0 * d3, (double)f2 - 0.45 * (double)f1, -d0 * d2 + d1 * d3);
        }
    }

    private static float fraction(int pNumerator, int pDenominator) {
        return (float)pNumerator / (float)pDenominator;
    }

    private static void vertex(
        VertexConsumer pConsumer, PoseStack.Pose p_328848_, int pLightmapUV, float pX, int pY, int pU, int pV
    ) {
        pConsumer.m_320578_(p_328848_, pX - 0.5F, (float)pY - 0.5F, 0.0F)
            .color(255, 255, 255, 255)
            .uv((float)pU, (float)pV)
            .overlayCoords(OverlayTexture.NO_OVERLAY)
            .uv2(pLightmapUV)
            .normal(p_328848_, 0.0F, 1.0F, 0.0F)
            .endVertex();
    }

    private static void stringVertex(
        float pX, float pY, float pZ, VertexConsumer pConsumer, PoseStack.Pose pPose, float p_174124_, float p_174125_
    ) {
        float f = pX * p_174124_;
        float f1 = pY * (p_174124_ * p_174124_ + p_174124_) * 0.5F + 0.25F;
        float f2 = pZ * p_174124_;
        float f3 = pX * p_174125_ - f;
        float f4 = pY * (p_174125_ * p_174125_ + p_174125_) * 0.5F + 0.25F - f1;
        float f5 = pZ * p_174125_ - f2;
        float f6 = Mth.sqrt(f3 * f3 + f4 * f4 + f5 * f5);
        f3 /= f6;
        f4 /= f6;
        f5 /= f6;
        pConsumer.m_320578_(pPose, f, f1, f2).color(0, 0, 0, 255).normal(pPose, f3, f4, f5).endVertex();
    }

    public ResourceLocation getTextureLocation(FishingHook pEntity) {
        return TEXTURE_LOCATION;
    }
}
