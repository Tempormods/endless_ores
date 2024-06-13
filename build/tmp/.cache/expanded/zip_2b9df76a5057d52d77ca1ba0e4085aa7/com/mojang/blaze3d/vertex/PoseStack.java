package com.mojang.blaze3d.vertex;

import com.google.common.collect.Queues;
import com.mojang.math.MatrixUtil;
import java.util.ArrayDeque;
import java.util.Deque;
import net.minecraft.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class PoseStack implements net.minecraftforge.client.extensions.IForgePoseStack {
    private final Deque<PoseStack.Pose> poseStack = Util.make(Queues.newArrayDeque(), p_85848_ -> {
        Matrix4f matrix4f = new Matrix4f();
        Matrix3f matrix3f = new Matrix3f();
        p_85848_.add(new PoseStack.Pose(matrix4f, matrix3f));
    });

    public void translate(double pX, double pY, double pZ) {
        this.translate((float)pX, (float)pY, (float)pZ);
    }

    public void translate(float pX, float pY, float pZ) {
        PoseStack.Pose posestack$pose = this.poseStack.getLast();
        posestack$pose.pose.translate(pX, pY, pZ);
    }

    public void scale(float pX, float pY, float pZ) {
        PoseStack.Pose posestack$pose = this.poseStack.getLast();
        posestack$pose.pose.scale(pX, pY, pZ);
        if (Math.abs(pX) == Math.abs(pY) && Math.abs(pY) == Math.abs(pZ)) {
            if (pX < 0.0F || pY < 0.0F || pZ < 0.0F) {
                posestack$pose.normal.scale(Math.signum(pX), Math.signum(pY), Math.signum(pZ));
            }
        } else {
            posestack$pose.normal.scale(1.0F / pX, 1.0F / pY, 1.0F / pZ);
            posestack$pose.f_317074_ = false;
        }
    }

    public void mulPose(Quaternionf pQuaternion) {
        PoseStack.Pose posestack$pose = this.poseStack.getLast();
        posestack$pose.pose.rotate(pQuaternion);
        posestack$pose.normal.rotate(pQuaternion);
    }

    public void rotateAround(Quaternionf pQuaternion, float pX, float pY, float pZ) {
        PoseStack.Pose posestack$pose = this.poseStack.getLast();
        posestack$pose.pose.rotateAround(pQuaternion, pX, pY, pZ);
        posestack$pose.normal.rotate(pQuaternion);
    }

    public void pushPose() {
        this.poseStack.addLast(new PoseStack.Pose(this.poseStack.getLast()));
    }

    public void popPose() {
        this.poseStack.removeLast();
    }

    public PoseStack.Pose last() {
        return this.poseStack.getLast();
    }

    public boolean clear() {
        return this.poseStack.size() == 1;
    }

    public void setIdentity() {
        PoseStack.Pose posestack$pose = this.poseStack.getLast();
        posestack$pose.pose.identity();
        posestack$pose.normal.identity();
        posestack$pose.f_317074_ = true;
    }

    public void m_318714_(Matrix4f p_332918_) {
        PoseStack.Pose posestack$pose = this.poseStack.getLast();
        posestack$pose.pose.mul(p_332918_);
        if (!MatrixUtil.m_321551_(p_332918_)) {
            if (MatrixUtil.m_319661_(p_332918_)) {
                posestack$pose.normal.mul(new Matrix3f(p_332918_));
            } else {
                posestack$pose.m_319145_();
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static final class Pose {
        final Matrix4f pose;
        final Matrix3f normal;
        boolean f_317074_ = true;

        Pose(Matrix4f pPose, Matrix3f pNormal) {
            this.pose = pPose;
            this.normal = pNormal;
        }

        Pose(PoseStack.Pose p_328466_) {
            this.pose = new Matrix4f(p_328466_.pose);
            this.normal = new Matrix3f(p_328466_.normal);
            this.f_317074_ = p_328466_.f_317074_;
        }

        void m_319145_() {
            this.normal.set(this.pose).invert().transpose();
            this.f_317074_ = false;
        }

        public Matrix4f pose() {
            return this.pose;
        }

        public Matrix3f normal() {
            return this.normal;
        }

        public Vector3f m_322076_(Vector3f p_332767_, Vector3f p_333196_) {
            return this.m_323822_(p_332767_.x, p_332767_.y, p_332767_.z, p_333196_);
        }

        public Vector3f m_323822_(float p_333912_, float p_334796_, float p_329732_, Vector3f p_328781_) {
            Vector3f vector3f = this.normal.transform(p_333912_, p_334796_, p_329732_, p_328781_);
            return this.f_317074_ ? vector3f : vector3f.normalize();
        }

        public PoseStack.Pose m_323639_() {
            return new PoseStack.Pose(this);
        }
    }
}
