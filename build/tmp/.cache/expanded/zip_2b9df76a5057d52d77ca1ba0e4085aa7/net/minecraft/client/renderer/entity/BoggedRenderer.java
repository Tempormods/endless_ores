package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.BoggedModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.SkeletonClothingLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Bogged;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BoggedRenderer extends SkeletonRenderer<Bogged> {
    private static final ResourceLocation f_316256_ = new ResourceLocation("textures/entity/skeleton/bogged.png");
    private static final ResourceLocation f_316202_ = new ResourceLocation("textures/entity/skeleton/bogged_overlay.png");

    public BoggedRenderer(EntityRendererProvider.Context p_329255_) {
        super(p_329255_, ModelLayers.f_316561_, ModelLayers.f_316376_, new BoggedModel(p_329255_.bakeLayer(ModelLayers.f_316706_)));
        this.addLayer(new SkeletonClothingLayer<>(this, p_329255_.getModelSet(), ModelLayers.f_316513_, f_316202_));
    }

    public ResourceLocation getTextureLocation(Bogged p_336247_) {
        return f_316256_;
    }
}