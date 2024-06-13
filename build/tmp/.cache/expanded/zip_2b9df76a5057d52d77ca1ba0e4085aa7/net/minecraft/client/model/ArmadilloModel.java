package net.minecraft.client.model;

import net.minecraft.client.animation.definitions.ArmadilloAnimation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ArmadilloModel extends AgeableHierarchicalModel<Armadillo> {
    private static final float f_316750_ = 16.02F;
    private static final float f_314028_ = 25.0F;
    private static final float f_315172_ = 22.5F;
    private static final float f_315679_ = 16.5F;
    private static final float f_315435_ = 2.5F;
    private static final String f_315651_ = "head_cube";
    private static final String f_315194_ = "right_ear_cube";
    private static final String f_314191_ = "left_ear_cube";
    private final ModelPart f_314538_;
    private final ModelPart f_314464_;
    private final ModelPart f_314828_;
    private final ModelPart f_314455_;
    private final ModelPart f_314513_;
    private final ModelPart f_316544_;
    private final ModelPart f_316639_;

    public ArmadilloModel(ModelPart p_329798_) {
        super(0.6F, 16.02F);
        this.f_314538_ = p_329798_;
        this.f_314464_ = p_329798_.getChild("body");
        this.f_314828_ = p_329798_.getChild("right_hind_leg");
        this.f_314455_ = p_329798_.getChild("left_hind_leg");
        this.f_316544_ = this.f_314464_.getChild("head");
        this.f_316639_ = this.f_314464_.getChild("tail");
        this.f_314513_ = p_329798_.getChild("cube");
    }

    public static LayerDefinition m_320313_() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition partdefinition1 = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
                .texOffs(0, 20)
                .addBox(-4.0F, -7.0F, -10.0F, 8.0F, 8.0F, 12.0F, new CubeDeformation(0.3F))
                .texOffs(0, 40)
                .addBox(-4.0F, -7.0F, -10.0F, 8.0F, 8.0F, 12.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 21.0F, 4.0F)
        );
        partdefinition1.addOrReplaceChild(
            "tail",
            CubeListBuilder.create().texOffs(44, 53).addBox(-0.5F, -0.0865F, 0.0933F, 1.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, -3.0F, 1.0F, 0.5061F, 0.0F, 0.0F)
        );
        PartDefinition partdefinition2 = partdefinition1.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, -2.0F, -11.0F));
        partdefinition2.addOrReplaceChild(
            "head_cube",
            CubeListBuilder.create().texOffs(43, 15).addBox(-1.5F, -1.0F, -1.0F, 3.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.3927F, 0.0F, 0.0F)
        );
        PartDefinition partdefinition3 = partdefinition2.addOrReplaceChild("right_ear", CubeListBuilder.create(), PartPose.offset(-1.0F, -1.0F, 0.0F));
        partdefinition3.addOrReplaceChild(
            "right_ear_cube",
            CubeListBuilder.create().texOffs(43, 10).addBox(-2.0F, -3.0F, 0.0F, 2.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.5F, 0.0F, -0.6F, 0.1886F, -0.3864F, -0.0718F)
        );
        PartDefinition partdefinition4 = partdefinition2.addOrReplaceChild("left_ear", CubeListBuilder.create(), PartPose.offset(1.0F, -2.0F, 0.0F));
        partdefinition4.addOrReplaceChild(
            "left_ear_cube",
            CubeListBuilder.create().texOffs(47, 10).addBox(0.0F, -3.0F, 0.0F, 2.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.5F, 1.0F, -0.6F, 0.1886F, 0.3864F, 0.0718F)
        );
        partdefinition.addOrReplaceChild(
            "right_hind_leg",
            CubeListBuilder.create().texOffs(51, 31).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-2.0F, 21.0F, 4.0F)
        );
        partdefinition.addOrReplaceChild(
            "left_hind_leg",
            CubeListBuilder.create().texOffs(42, 31).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offset(2.0F, 21.0F, 4.0F)
        );
        partdefinition.addOrReplaceChild(
            "right_front_leg",
            CubeListBuilder.create().texOffs(51, 43).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-2.0F, 21.0F, -4.0F)
        );
        partdefinition.addOrReplaceChild(
            "left_front_leg",
            CubeListBuilder.create().texOffs(42, 43).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offset(2.0F, 21.0F, -4.0F)
        );
        partdefinition.addOrReplaceChild(
            "cube",
            CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -10.0F, -6.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 24.0F, 0.0F)
        );
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public ModelPart root() {
        return this.f_314538_;
    }

    public void setupAnim(Armadillo p_331950_, float p_336282_, float p_330081_, float p_331623_, float p_333282_, float p_333013_) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        if (p_331950_.m_322786_()) {
            this.f_314464_.skipDraw = true;
            this.f_314455_.visible = false;
            this.f_314828_.visible = false;
            this.f_316639_.visible = false;
            this.f_314513_.visible = true;
        } else {
            this.f_314464_.skipDraw = false;
            this.f_314455_.visible = true;
            this.f_314828_.visible = true;
            this.f_316639_.visible = true;
            this.f_314513_.visible = false;
            this.f_316544_.xRot = Mth.clamp(p_333013_, -22.5F, 25.0F) * (float) (Math.PI / 180.0);
            this.f_316544_.yRot = Mth.clamp(p_333282_, -32.5F, 32.5F) * (float) (Math.PI / 180.0);
        }

        this.animateWalk(ArmadilloAnimation.f_314004_, p_336282_, p_330081_, 16.5F, 2.5F);
        this.animate(p_331950_.f_314820_, ArmadilloAnimation.f_315893_, p_331623_, 1.0F);
        this.animate(p_331950_.f_313932_, ArmadilloAnimation.f_315724_, p_331623_, 1.0F);
        this.animate(p_331950_.f_316698_, ArmadilloAnimation.f_315658_, p_331623_, 1.0F);
    }
}