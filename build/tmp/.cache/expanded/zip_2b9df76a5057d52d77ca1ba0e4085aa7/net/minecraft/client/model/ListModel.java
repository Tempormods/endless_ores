package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.function.Function;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class ListModel<E extends Entity> extends EntityModel<E> {
    public ListModel() {
        this(RenderType::entityCutoutNoCull);
    }

    public ListModel(Function<ResourceLocation, RenderType> pRenderType) {
        super(pRenderType);
    }

    @Override
    public void renderToBuffer(
        PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha
    ) {
        this.parts().forEach(p_103030_ -> p_103030_.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha));
    }

    public abstract Iterable<ModelPart> parts();
}