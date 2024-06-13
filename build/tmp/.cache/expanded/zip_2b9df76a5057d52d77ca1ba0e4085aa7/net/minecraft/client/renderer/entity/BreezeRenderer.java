package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.BreezeModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.BreezeEyesLayer;
import net.minecraft.client.renderer.entity.layers.BreezeWindLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.breeze.Breeze;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BreezeRenderer extends MobRenderer<Breeze, BreezeModel<Breeze>> {
    private static final ResourceLocation f_302968_ = new ResourceLocation("textures/entity/breeze/breeze.png");

    public BreezeRenderer(EntityRendererProvider.Context p_311628_) {
        super(p_311628_, new BreezeModel<>(p_311628_.bakeLayer(ModelLayers.f_303100_)), 0.5F);
        this.addLayer(new BreezeWindLayer(this));
        this.addLayer(new BreezeEyesLayer(this));
    }

    public void render(Breeze p_334455_, float p_333681_, float p_331379_, PoseStack p_332688_, MultiBufferSource p_333828_, int p_331024_) {
        BreezeModel<Breeze> breezemodel = this.getModel();
        m_323838_(breezemodel, breezemodel.m_319970_(), breezemodel.m_320822_());
        super.render(p_334455_, p_333681_, p_331379_, p_332688_, p_333828_, p_331024_);
    }

    public ResourceLocation getTextureLocation(Breeze p_312626_) {
        return f_302968_;
    }

    public static BreezeModel<Breeze> m_323838_(BreezeModel<Breeze> p_328756_, ModelPart... p_332502_) {
        p_328756_.m_319970_().visible = false;
        p_328756_.m_323648_().visible = false;
        p_328756_.m_320822_().visible = false;
        p_328756_.m_321100_().visible = false;

        for (ModelPart modelpart : p_332502_) {
            modelpart.visible = true;
        }

        return p_328756_;
    }
}