package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.CrossedArmsItemLayer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WanderingTraderRenderer extends MobRenderer<WanderingTrader, VillagerModel<WanderingTrader>> {
    private static final ResourceLocation VILLAGER_BASE_SKIN = new ResourceLocation("textures/entity/wandering_trader.png");

    public WanderingTraderRenderer(EntityRendererProvider.Context p_174441_) {
        super(p_174441_, new VillagerModel<>(p_174441_.bakeLayer(ModelLayers.WANDERING_TRADER)), 0.5F);
        this.addLayer(new CustomHeadLayer<>(this, p_174441_.getModelSet(), p_174441_.getItemInHandRenderer()));
        this.addLayer(new CrossedArmsItemLayer<>(this, p_174441_.getItemInHandRenderer()));
    }

    public ResourceLocation getTextureLocation(WanderingTrader pEntity) {
        return VILLAGER_BASE_SKIN;
    }

    protected void scale(WanderingTrader pLivingEntity, PoseStack pPoseStack, float pPartialTickTime) {
        float f = 0.9375F;
        pPoseStack.scale(0.9375F, 0.9375F, 0.9375F);
    }
}