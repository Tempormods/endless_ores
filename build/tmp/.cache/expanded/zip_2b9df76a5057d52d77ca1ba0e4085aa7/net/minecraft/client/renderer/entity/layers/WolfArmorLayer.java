package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Map;
import net.minecraft.client.model.WolfModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.Crackiness;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WolfArmorLayer extends RenderLayer<Wolf, WolfModel<Wolf>> {
    private final WolfModel<Wolf> f_314635_;
    private static final Map<Crackiness.Level, ResourceLocation> f_316679_ = Map.of(
        Crackiness.Level.LOW,
        new ResourceLocation("textures/entity/wolf/wolf_armor_crackiness_low.png"),
        Crackiness.Level.MEDIUM,
        new ResourceLocation("textures/entity/wolf/wolf_armor_crackiness_medium.png"),
        Crackiness.Level.HIGH,
        new ResourceLocation("textures/entity/wolf/wolf_armor_crackiness_high.png")
    );

    public WolfArmorLayer(RenderLayerParent<Wolf, WolfModel<Wolf>> p_329010_, EntityModelSet p_329062_) {
        super(p_329010_);
        this.f_314635_ = new WolfModel<>(p_329062_.bakeLayer(ModelLayers.f_315853_));
    }

    public void render(
        PoseStack p_331942_,
        MultiBufferSource p_332785_,
        int p_336082_,
        Wolf p_327877_,
        float p_332161_,
        float p_333130_,
        float p_333869_,
        float p_332527_,
        float p_334109_,
        float p_331749_
    ) {
        if (p_327877_.m_324194_()) {
            ItemStack itemstack = p_327877_.m_319275_();
            if (itemstack.getItem() instanceof AnimalArmorItem animalarmoritem && animalarmoritem.m_319458_() == AnimalArmorItem.BodyType.CANINE) {
                this.getParentModel().copyPropertiesTo(this.f_314635_);
                this.f_314635_.prepareMobModel(p_327877_, p_332161_, p_333130_, p_333869_);
                this.f_314635_.setupAnim(p_327877_, p_332161_, p_333130_, p_332527_, p_334109_, p_331749_);
                VertexConsumer vertexconsumer = p_332785_.getBuffer(RenderType.entityCutoutNoCull(animalarmoritem.m_320881_()));
                this.f_314635_.renderToBuffer(p_331942_, vertexconsumer, p_336082_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
                this.m_319869_(p_331942_, p_332785_, p_336082_, itemstack, animalarmoritem);
                this.m_323883_(p_331942_, p_332785_, p_336082_, itemstack);
                return;
            }
        }
    }

    private void m_319869_(PoseStack p_332352_, MultiBufferSource p_333624_, int p_329264_, ItemStack p_331351_, AnimalArmorItem p_333020_) {
        if (p_331351_.is(ItemTags.f_314020_)) {
            int i = DyedItemColor.m_322889_(p_331351_, 0);
            if (FastColor.ARGB32.alpha(i) == 0) {
                return;
            }

            ResourceLocation resourcelocation = p_333020_.m_323746_();
            if (resourcelocation == null) {
                return;
            }

            float f = (float)FastColor.ARGB32.red(i) / 255.0F;
            float f1 = (float)FastColor.ARGB32.green(i) / 255.0F;
            float f2 = (float)FastColor.ARGB32.blue(i) / 255.0F;
            this.f_314635_.renderToBuffer(p_332352_, p_333624_.getBuffer(RenderType.entityCutoutNoCull(resourcelocation)), p_329264_, OverlayTexture.NO_OVERLAY, f, f1, f2, 1.0F);
        }
    }

    private void m_323883_(PoseStack p_332031_, MultiBufferSource p_334884_, int p_329468_, ItemStack p_332244_) {
        Crackiness.Level crackiness$level = Crackiness.f_315625_.m_318874_(p_332244_);
        if (crackiness$level != Crackiness.Level.NONE) {
            ResourceLocation resourcelocation = f_316679_.get(crackiness$level);
            VertexConsumer vertexconsumer = p_334884_.getBuffer(RenderType.entityTranslucent(resourcelocation));
            this.f_314635_.renderToBuffer(p_332031_, vertexconsumer, p_329468_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}