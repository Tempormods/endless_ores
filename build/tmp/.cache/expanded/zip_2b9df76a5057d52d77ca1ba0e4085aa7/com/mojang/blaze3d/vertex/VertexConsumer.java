package com.mojang.blaze3d.vertex;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Vec3i;
import net.minecraft.util.FastColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

@OnlyIn(Dist.CLIENT)
public interface VertexConsumer extends net.minecraftforge.client.extensions.IForgeVertexConsumer {
    VertexConsumer vertex(double pX, double pY, double pZ);

    VertexConsumer color(int pRed, int pGreen, int pBlue, int pAlpha);

    VertexConsumer uv(float pU, float pV);

    VertexConsumer overlayCoords(int pU, int pV);

    VertexConsumer uv2(int pU, int pV);

    VertexConsumer normal(float pX, float pY, float pZ);

    void endVertex();

    default void vertex(
        float pX,
        float pY,
        float pZ,
        float pRed,
        float pGreen,
        float pBlue,
        float pAlpha,
        float pTexU,
        float pTexV,
        int pOverlayUV,
        int pLightmapUV,
        float pNormalX,
        float pNormalY,
        float pNormalZ
    ) {
        this.vertex((double)pX, (double)pY, (double)pZ);
        this.color(pRed, pGreen, pBlue, pAlpha);
        this.uv(pTexU, pTexV);
        this.overlayCoords(pOverlayUV);
        this.uv2(pLightmapUV);
        this.normal(pNormalX, pNormalY, pNormalZ);
        this.endVertex();
    }

    void defaultColor(int pDefaultR, int pDefaultG, int pDefaultB, int pDefaultA);

    void unsetDefaultColor();

    default VertexConsumer color(float pRed, float pGreen, float pBlue, float pAlpha) {
        return this.color((int)(pRed * 255.0F), (int)(pGreen * 255.0F), (int)(pBlue * 255.0F), (int)(pAlpha * 255.0F));
    }

    default VertexConsumer color(int pColorARGB) {
        return this.color(
            FastColor.ARGB32.red(pColorARGB),
            FastColor.ARGB32.green(pColorARGB),
            FastColor.ARGB32.blue(pColorARGB),
            FastColor.ARGB32.alpha(pColorARGB)
        );
    }

    default VertexConsumer uv2(int pLightmapUV) {
        return this.uv2(pLightmapUV & 65535, pLightmapUV >> 16 & 65535);
    }

    default VertexConsumer overlayCoords(int pOverlayUV) {
        return this.overlayCoords(pOverlayUV & 65535, pOverlayUV >> 16 & 65535);
    }

    default void putBulkData(
        PoseStack.Pose pPoseEntry, BakedQuad pQuad, float pRed, float pGreen, float pBlue, float p_330684_, int pCombinedOverlay, int p_332867_
    ) {
        this.putBulkData(
            pPoseEntry,
            pQuad,
            new float[]{1.0F, 1.0F, 1.0F, 1.0F},
            pRed,
            pGreen,
            pBlue,
            p_330684_,
            new int[]{pCombinedOverlay, pCombinedOverlay, pCombinedOverlay, pCombinedOverlay},
            p_332867_,
            false
        );
    }

    default void putBulkData(
        PoseStack.Pose pPoseEntry,
        BakedQuad pQuad,
        float[] pColorMuls,
        float pRed,
        float pGreen,
        float pBlue,
        float alpha,
        int[] pCombinedLights,
        int pCombinedOverlay,
        boolean pMulColor
    ) {
        float[] afloat = new float[]{pColorMuls[0], pColorMuls[1], pColorMuls[2], pColorMuls[3]};
        int[] aint = new int[]{pCombinedLights[0], pCombinedLights[1], pCombinedLights[2], pCombinedLights[3]};
        int[] aint1 = pQuad.getVertices();
        Vec3i vec3i = pQuad.getDirection().getNormal();
        Matrix4f matrix4f = pPoseEntry.pose();
        Vector3f vector3f = pPoseEntry.m_323822_((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ(), new Vector3f());
        int i = 8;
        int j = aint1.length / 8;

        try (MemoryStack memorystack = MemoryStack.stackPush()) {
            ByteBuffer bytebuffer = memorystack.malloc(DefaultVertexFormat.BLOCK.getVertexSize());
            IntBuffer intbuffer = bytebuffer.asIntBuffer();

            for (int k = 0; k < j; k++) {
                intbuffer.clear();
                intbuffer.put(aint1, k * 8, 8);
                float f = bytebuffer.getFloat(0);
                float f1 = bytebuffer.getFloat(4);
                float f2 = bytebuffer.getFloat(8);
                float f3;
                float f4;
                float f5;
                if (pMulColor) {
                    float f6 = (float)(bytebuffer.get(12) & 255) / 255.0F;
                    float f7 = (float)(bytebuffer.get(13) & 255) / 255.0F;
                    float f8 = (float)(bytebuffer.get(14) & 255) / 255.0F;
                    f3 = f6 * afloat[k] * pRed;
                    f4 = f7 * afloat[k] * pGreen;
                    f5 = f8 * afloat[k] * pBlue;
                } else {
                    f3 = afloat[k] * pRed;
                    f4 = afloat[k] * pGreen;
                    f5 = afloat[k] * pBlue;
                }

                int l = applyBakedLighting(aint[k], bytebuffer);
                float f9 = bytebuffer.getFloat(16);
                float f10 = bytebuffer.getFloat(20);
                Vector4f vector4f = matrix4f.transform(new Vector4f(f, f1, f2, 1.0F));
                applyBakedNormals(vector3f, bytebuffer, pPoseEntry.normal());
                this.vertex(vector4f.x(), vector4f.y(), vector4f.z(), f3, f4, f5, alpha, f9, f10, pCombinedOverlay, l, vector3f.x(), vector3f.y(), vector3f.z());
            }
        }
    }

    default VertexConsumer m_320578_(PoseStack.Pose p_335163_, float p_328433_, float p_329238_, float p_332849_) {
        return this.vertex(p_335163_.pose(), p_328433_, p_329238_, p_332849_);
    }

    default VertexConsumer vertex(Matrix4f pMatrix, float pX, float pY, float pZ) {
        Vector3f vector3f = pMatrix.transformPosition(pX, pY, pZ, new Vector3f());
        return this.vertex((double)vector3f.x(), (double)vector3f.y(), (double)vector3f.z());
    }

    default VertexConsumer normal(PoseStack.Pose p_334620_, float pX, float pY, float pZ) {
        Vector3f vector3f = p_334620_.m_323822_(pX, pY, pZ, new Vector3f());
        return this.normal(vector3f.x(), vector3f.y(), vector3f.z());
    }
}
