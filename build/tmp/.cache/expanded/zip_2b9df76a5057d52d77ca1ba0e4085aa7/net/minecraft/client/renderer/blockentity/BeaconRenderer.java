package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.List;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BeaconRenderer implements BlockEntityRenderer<BeaconBlockEntity> {
    public static final ResourceLocation BEAM_LOCATION = new ResourceLocation("textures/entity/beacon_beam.png");
    public static final int MAX_RENDER_Y = 1024;

    public BeaconRenderer(BlockEntityRendererProvider.Context pContext) {
    }

    public void render(BeaconBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        long i = pBlockEntity.getLevel().getGameTime();
        List<BeaconBlockEntity.BeaconBeamSection> list = pBlockEntity.getBeamSections();
        int j = 0;

        for (int k = 0; k < list.size(); k++) {
            BeaconBlockEntity.BeaconBeamSection beaconblockentity$beaconbeamsection = list.get(k);
            renderBeaconBeam(
                pPoseStack,
                pBuffer,
                pPartialTick,
                i,
                j,
                k == list.size() - 1 ? 1024 : beaconblockentity$beaconbeamsection.getHeight(),
                beaconblockentity$beaconbeamsection.getColor()
            );
            j += beaconblockentity$beaconbeamsection.getHeight();
        }
    }

    private static void renderBeaconBeam(
        PoseStack pPoseStack, MultiBufferSource pBufferSource, float pPartialTick, long pGameTime, int pYOffset, int pHeight, float[] pColors
    ) {
        renderBeaconBeam(pPoseStack, pBufferSource, BEAM_LOCATION, pPartialTick, 1.0F, pGameTime, pYOffset, pHeight, pColors, 0.2F, 0.25F);
    }

    public static void renderBeaconBeam(
        PoseStack pPoseStack,
        MultiBufferSource pBufferSource,
        ResourceLocation pBeamLocation,
        float pPartialTick,
        float pTextureScale,
        long pGameTime,
        int pYOffset,
        int pHeight,
        float[] pColors,
        float pBeamRadius,
        float pGlowRadius
    ) {
        int i = pYOffset + pHeight;
        pPoseStack.pushPose();
        pPoseStack.translate(0.5, 0.0, 0.5);
        float f = (float)Math.floorMod(pGameTime, 40) + pPartialTick;
        float f1 = pHeight < 0 ? f : -f;
        float f2 = Mth.frac(f1 * 0.2F - (float)Mth.floor(f1 * 0.1F));
        float f3 = pColors[0];
        float f4 = pColors[1];
        float f5 = pColors[2];
        pPoseStack.pushPose();
        pPoseStack.mulPose(Axis.YP.rotationDegrees(f * 2.25F - 45.0F));
        float f6 = 0.0F;
        float f8 = 0.0F;
        float f9 = -pBeamRadius;
        float f10 = 0.0F;
        float f11 = 0.0F;
        float f12 = -pBeamRadius;
        float f13 = 0.0F;
        float f14 = 1.0F;
        float f15 = -1.0F + f2;
        float f16 = (float)pHeight * pTextureScale * (0.5F / pBeamRadius) + f15;
        renderPart(
            pPoseStack,
            pBufferSource.getBuffer(RenderType.beaconBeam(pBeamLocation, false)),
            f3,
            f4,
            f5,
            1.0F,
            pYOffset,
            i,
            0.0F,
            pBeamRadius,
            pBeamRadius,
            0.0F,
            f9,
            0.0F,
            0.0F,
            f12,
            0.0F,
            1.0F,
            f16,
            f15
        );
        pPoseStack.popPose();
        f6 = -pGlowRadius;
        float f7 = -pGlowRadius;
        f8 = -pGlowRadius;
        f9 = -pGlowRadius;
        f13 = 0.0F;
        f14 = 1.0F;
        f15 = -1.0F + f2;
        f16 = (float)pHeight * pTextureScale + f15;
        renderPart(
            pPoseStack,
            pBufferSource.getBuffer(RenderType.beaconBeam(pBeamLocation, true)),
            f3,
            f4,
            f5,
            0.125F,
            pYOffset,
            i,
            f6,
            f7,
            pGlowRadius,
            f8,
            f9,
            pGlowRadius,
            pGlowRadius,
            pGlowRadius,
            0.0F,
            1.0F,
            f16,
            f15
        );
        pPoseStack.popPose();
    }

    private static void renderPart(
        PoseStack pPoseStack,
        VertexConsumer pConsumer,
        float pRed,
        float pGreen,
        float pBlue,
        float pAlpha,
        int pMinY,
        int pMaxY,
        float pX0,
        float pZ0,
        float pX1,
        float pZ1,
        float pX2,
        float pZ2,
        float pX3,
        float pZ3,
        float pMinU,
        float pMaxU,
        float pMinV,
        float pMaxV
    ) {
        PoseStack.Pose posestack$pose = pPoseStack.last();
        renderQuad(
            posestack$pose,
            pConsumer,
            pRed,
            pGreen,
            pBlue,
            pAlpha,
            pMinY,
            pMaxY,
            pX0,
            pZ0,
            pX1,
            pZ1,
            pMinU,
            pMaxU,
            pMinV,
            pMaxV
        );
        renderQuad(
            posestack$pose,
            pConsumer,
            pRed,
            pGreen,
            pBlue,
            pAlpha,
            pMinY,
            pMaxY,
            pX3,
            pZ3,
            pX2,
            pZ2,
            pMinU,
            pMaxU,
            pMinV,
            pMaxV
        );
        renderQuad(
            posestack$pose,
            pConsumer,
            pRed,
            pGreen,
            pBlue,
            pAlpha,
            pMinY,
            pMaxY,
            pX1,
            pZ1,
            pX3,
            pZ3,
            pMinU,
            pMaxU,
            pMinV,
            pMaxV
        );
        renderQuad(
            posestack$pose,
            pConsumer,
            pRed,
            pGreen,
            pBlue,
            pAlpha,
            pMinY,
            pMaxY,
            pX2,
            pZ2,
            pX0,
            pZ0,
            pMinU,
            pMaxU,
            pMinV,
            pMaxV
        );
    }

    private static void renderQuad(
        PoseStack.Pose p_332343_,
        VertexConsumer pConsumer,
        float pRed,
        float pGreen,
        float pBlue,
        float pAlpha,
        int pMinY,
        int pMaxY,
        float pMinX,
        float pMinZ,
        float pMaxX,
        float pMaxZ,
        float pMinU,
        float pMaxU,
        float pMinV,
        float pMaxV
    ) {
        addVertex(p_332343_, pConsumer, pRed, pGreen, pBlue, pAlpha, pMaxY, pMinX, pMinZ, pMaxU, pMinV);
        addVertex(p_332343_, pConsumer, pRed, pGreen, pBlue, pAlpha, pMinY, pMinX, pMinZ, pMaxU, pMaxV);
        addVertex(p_332343_, pConsumer, pRed, pGreen, pBlue, pAlpha, pMinY, pMaxX, pMaxZ, pMinU, pMaxV);
        addVertex(p_332343_, pConsumer, pRed, pGreen, pBlue, pAlpha, pMaxY, pMaxX, pMaxZ, pMinU, pMinV);
    }

    private static void addVertex(
        PoseStack.Pose p_334631_,
        VertexConsumer pConsumer,
        float pRed,
        float pGreen,
        float pBlue,
        float pAlpha,
        int pY,
        float pX,
        float pZ,
        float pU,
        float pV
    ) {
        pConsumer.m_320578_(p_334631_, pX, (float)pY, pZ)
            .color(pRed, pGreen, pBlue, pAlpha)
            .uv(pU, pV)
            .overlayCoords(OverlayTexture.NO_OVERLAY)
            .uv2(15728880)
            .normal(p_334631_, 0.0F, 1.0F, 0.0F)
            .endVertex();
    }

    public boolean shouldRenderOffScreen(BeaconBlockEntity pBlockEntity) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }

    public boolean shouldRender(BeaconBlockEntity pBlockEntity, Vec3 pCameraPos) {
        return Vec3.atCenterOf(pBlockEntity.getBlockPos()).multiply(1.0, 0.0, 1.0).closerThan(pCameraPos.multiply(1.0, 0.0, 1.0), (double)this.getViewDistance());
    }
}