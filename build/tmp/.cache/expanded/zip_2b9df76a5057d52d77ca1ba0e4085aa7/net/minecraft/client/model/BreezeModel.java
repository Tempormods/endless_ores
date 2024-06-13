package net.minecraft.client.model;

import net.minecraft.client.animation.definitions.BreezeAnimation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.breeze.Breeze;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BreezeModel<T extends Breeze> extends HierarchicalModel<T> {
    private static final float f_303565_ = 0.6F;
    private static final float f_303418_ = 0.8F;
    private static final float f_303728_ = 1.0F;
    private final ModelPart f_303110_;
    private final ModelPart f_302678_;
    private final ModelPart f_315695_;
    private final ModelPart f_315647_;
    private final ModelPart f_302278_;
    private final ModelPart f_302586_;
    private final ModelPart f_302591_;
    private final ModelPart f_302579_;

    public BreezeModel(ModelPart p_309507_) {
        super(RenderType::entityTranslucent);
        this.f_303110_ = p_309507_;
        this.f_315647_ = p_309507_.getChild("wind_body");
        this.f_302591_ = this.f_315647_.getChild("wind_bottom");
        this.f_302586_ = this.f_302591_.getChild("wind_mid");
        this.f_302278_ = this.f_302586_.getChild("wind_top");
        this.f_302678_ = p_309507_.getChild("body").getChild("head");
        this.f_315695_ = this.f_302678_.getChild("eyes");
        this.f_302579_ = p_309507_.getChild("body").getChild("rods");
    }

    public static LayerDefinition m_304895_(int p_329286_, int p_330152_) {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition partdefinition1 = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition partdefinition2 = partdefinition1.addOrReplaceChild("rods", CubeListBuilder.create(), PartPose.offset(0.0F, 8.0F, 0.0F));
        partdefinition2.addOrReplaceChild(
            "rod_1",
            CubeListBuilder.create().texOffs(0, 17).addBox(-1.0F, 0.0F, -3.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(2.5981F, -3.0F, 1.5F, -2.7489F, -1.0472F, 3.1416F)
        );
        partdefinition2.addOrReplaceChild(
            "rod_2",
            CubeListBuilder.create().texOffs(0, 17).addBox(-1.0F, 0.0F, -3.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-2.5981F, -3.0F, 1.5F, -2.7489F, 1.0472F, 3.1416F)
        );
        partdefinition2.addOrReplaceChild(
            "rod_3",
            CubeListBuilder.create().texOffs(0, 17).addBox(-1.0F, 0.0F, -3.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, -3.0F, -3.0F, 0.3927F, 0.0F, 0.0F)
        );
        PartDefinition partdefinition3 = partdefinition1.addOrReplaceChild(
            "head",
            CubeListBuilder.create()
                .texOffs(4, 24)
                .addBox(-5.0F, -5.0F, -4.2F, 10.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0)
                .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 4.0F, 0.0F)
        );
        partdefinition3.addOrReplaceChild(
            "eyes",
            CubeListBuilder.create()
                .texOffs(4, 24)
                .addBox(-5.0F, -5.0F, -4.2F, 10.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0)
                .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
        );
        PartDefinition partdefinition4 = partdefinition.addOrReplaceChild("wind_body", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition partdefinition5 = partdefinition4.addOrReplaceChild(
            "wind_bottom",
            CubeListBuilder.create().texOffs(1, 83).addBox(-2.5F, -7.0F, -2.5F, 5.0F, 7.0F, 5.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 24.0F, 0.0F)
        );
        PartDefinition partdefinition6 = partdefinition5.addOrReplaceChild(
            "wind_mid",
            CubeListBuilder.create()
                .texOffs(74, 28)
                .addBox(-6.0F, -6.0F, -6.0F, 12.0F, 6.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(78, 32)
                .addBox(-4.0F, -6.0F, -4.0F, 8.0F, 6.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(49, 71)
                .addBox(-2.5F, -6.0F, -2.5F, 5.0F, 6.0F, 5.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, -7.0F, 0.0F)
        );
        partdefinition6.addOrReplaceChild(
            "wind_top",
            CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(-9.0F, -8.0F, -9.0F, 18.0F, 8.0F, 18.0F, new CubeDeformation(0.0F))
                .texOffs(6, 6)
                .addBox(-6.0F, -8.0F, -6.0F, 12.0F, 8.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(105, 57)
                .addBox(-2.5F, -8.0F, -2.5F, 5.0F, 8.0F, 5.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, -6.0F, 0.0F)
        );
        return LayerDefinition.create(meshdefinition, p_329286_, p_330152_);
    }

    public void setupAnim(T p_310040_, float p_311440_, float p_313252_, float p_309514_, float p_311824_, float p_311398_) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        float f = p_309514_ * (float) Math.PI * -0.1F;
        this.f_302278_.x = Mth.cos(f) * 1.0F * 0.6F;
        this.f_302278_.z = Mth.sin(f) * 1.0F * 0.6F;
        this.f_302586_.x = Mth.sin(f) * 0.5F * 0.8F;
        this.f_302586_.z = Mth.cos(f) * 0.8F;
        this.f_302591_.x = Mth.cos(f) * -0.25F * 1.0F;
        this.f_302591_.z = Mth.sin(f) * -0.25F * 1.0F;
        this.f_302678_.y = 4.0F + Mth.cos(f) / 4.0F;
        this.f_302579_.yRot = p_309514_ * (float) Math.PI * 0.1F;
        this.animate(p_310040_.f_302318_, BreezeAnimation.f_302892_, p_309514_);
        this.animate(p_310040_.f_303623_, BreezeAnimation.f_303619_, p_309514_);
        this.animate(p_310040_.f_314343_, BreezeAnimation.f_314955_, p_309514_);
        this.animate(p_310040_.f_303038_, BreezeAnimation.f_302325_, p_309514_);
    }

    @Override
    public ModelPart root() {
        return this.f_303110_;
    }

    public ModelPart m_319970_() {
        return this.f_302678_;
    }

    public ModelPart m_323648_() {
        return this.f_315695_;
    }

    public ModelPart m_320822_() {
        return this.f_302579_;
    }

    public ModelPart m_321100_() {
        return this.f_315647_;
    }
}