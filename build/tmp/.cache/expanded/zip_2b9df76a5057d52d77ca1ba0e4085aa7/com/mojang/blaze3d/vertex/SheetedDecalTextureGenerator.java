package com.mojang.blaze3d.vertex;

import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

@OnlyIn(Dist.CLIENT)
public class SheetedDecalTextureGenerator extends DefaultedVertexConsumer {
    private final VertexConsumer delegate;
    private final Matrix4f cameraInversePose;
    private final Matrix3f normalInversePose;
    private final float textureScale;
    private float x;
    private float y;
    private float z;
    private int overlayU;
    private int overlayV;
    private int lightCoords;
    private float nx;
    private float ny;
    private float nz;

    public SheetedDecalTextureGenerator(VertexConsumer pDelegate, PoseStack.Pose p_332899_, float pTextureScale) {
        this.delegate = pDelegate;
        this.cameraInversePose = new Matrix4f(p_332899_.pose()).invert();
        this.normalInversePose = new Matrix3f(p_332899_.normal()).invert();
        this.textureScale = pTextureScale;
        this.resetState();
    }

    private void resetState() {
        this.x = 0.0F;
        this.y = 0.0F;
        this.z = 0.0F;
        this.overlayU = 0;
        this.overlayV = 10;
        this.lightCoords = 15728880;
        this.nx = 0.0F;
        this.ny = 1.0F;
        this.nz = 0.0F;
    }

    @Override
    public void endVertex() {
        Vector3f vector3f = this.normalInversePose.transform(new Vector3f(this.nx, this.ny, this.nz));
        Direction direction = net.minecraftforge.client.ForgeHooksClient.getNearestStable(vector3f.x(), vector3f.y(), vector3f.z());
        Vector4f vector4f = this.cameraInversePose.transform(new Vector4f(this.x, this.y, this.z, 1.0F));
        vector4f.rotateY((float) Math.PI);
        vector4f.rotateX((float) (-Math.PI / 2));
        vector4f.rotate(direction.getRotation());
        float f = -vector4f.x() * this.textureScale;
        float f1 = -vector4f.y() * this.textureScale;
        this.delegate
            .vertex((double)this.x, (double)this.y, (double)this.z)
            .color(1.0F, 1.0F, 1.0F, 1.0F)
            .uv(f, f1)
            .overlayCoords(this.overlayU, this.overlayV)
            .uv2(this.lightCoords)
            .normal(this.nx, this.ny, this.nz)
            .endVertex();
        this.resetState();
    }

    @Override
    public VertexConsumer vertex(double pX, double pY, double pZ) {
        this.x = (float)pX;
        this.y = (float)pY;
        this.z = (float)pZ;
        return this;
    }

    @Override
    public VertexConsumer color(int pRed, int pGreen, int pBlue, int pAlpha) {
        return this;
    }

    @Override
    public VertexConsumer uv(float pU, float pV) {
        return this;
    }

    @Override
    public VertexConsumer overlayCoords(int pU, int pV) {
        this.overlayU = pU;
        this.overlayV = pV;
        return this;
    }

    @Override
    public VertexConsumer uv2(int pU, int pV) {
        this.lightCoords = pU | pV << 16;
        return this;
    }

    @Override
    public VertexConsumer normal(float pX, float pY, float pZ) {
        this.nx = pX;
        this.ny = pY;
        this.nz = pZ;
        return this;
    }
}
