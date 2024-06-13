package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ArmadilloModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ArmadilloRenderer extends MobRenderer<Armadillo, ArmadilloModel> {
    private static final ResourceLocation f_314445_ = new ResourceLocation("textures/entity/armadillo.png");

    public ArmadilloRenderer(EntityRendererProvider.Context p_333160_) {
        super(p_333160_, new ArmadilloModel(p_333160_.bakeLayer(ModelLayers.f_316495_)), 0.4F);
    }

    public ResourceLocation getTextureLocation(Armadillo p_327753_) {
        return f_314445_;
    }
}