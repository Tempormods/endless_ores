package net.minecraft.client.renderer.entity;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemEntityRenderer extends EntityRenderer<ItemEntity> {
    private static final float ITEM_BUNDLE_OFFSET_SCALE = 0.15F;
    private static final float FLAT_ITEM_BUNDLE_OFFSET_X = 0.0F;
    private static final float FLAT_ITEM_BUNDLE_OFFSET_Y = 0.0F;
    private static final float FLAT_ITEM_BUNDLE_OFFSET_Z = 0.09375F;
    private final ItemRenderer itemRenderer;
    private final RandomSource random = RandomSource.create();

    public ItemEntityRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.itemRenderer = pContext.getItemRenderer();
        this.shadowRadius = 0.15F;
        this.shadowStrength = 0.75F;
    }

    public ResourceLocation getTextureLocation(ItemEntity pEntity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

    public void render(ItemEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.pushPose();
        ItemStack itemstack = pEntity.getItem();
        this.random.setSeed((long)m_324215_(itemstack));
        BakedModel bakedmodel = this.itemRenderer.getModel(itemstack, pEntity.level(), null, pEntity.getId());
        boolean flag = bakedmodel.isGui3d();
        float f = 0.25F;
        float f1 = Mth.sin(((float)pEntity.getAge() + pPartialTicks) / 10.0F + pEntity.bobOffs) * 0.1F + 0.1F;
        float f2 = bakedmodel.getTransforms().getTransform(ItemDisplayContext.GROUND).scale.y();
        pPoseStack.translate(0.0F, f1 + 0.25F * f2, 0.0F);
        float f3 = pEntity.getSpin(pPartialTicks);
        pPoseStack.mulPose(Axis.YP.rotation(f3));
        m_323937_(this.itemRenderer, pPoseStack, pBuffer, pPackedLight, itemstack, bakedmodel, flag, this.random);
        pPoseStack.popPose();
        super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
    }

    public static int m_324215_(ItemStack p_330796_) {
        return p_330796_.isEmpty() ? 187 : Item.getId(p_330796_.getItem()) + p_330796_.getDamageValue();
    }

    @VisibleForTesting
    static int m_320283_(int p_329551_) {
        if (p_329551_ <= 1) {
            return 1;
        } else if (p_329551_ <= 16) {
            return 2;
        } else if (p_329551_ <= 32) {
            return 3;
        } else {
            return p_329551_ <= 48 ? 4 : 5;
        }
    }

    public static void m_318704_(
        ItemRenderer p_333213_, PoseStack p_329469_, MultiBufferSource p_334143_, int p_328622_, ItemStack p_329980_, RandomSource p_332633_, Level p_328051_
    ) {
        BakedModel bakedmodel = p_333213_.getModel(p_329980_, p_328051_, null, 0);
        m_323937_(p_333213_, p_329469_, p_334143_, p_328622_, p_329980_, bakedmodel, bakedmodel.isGui3d(), p_332633_);
    }

    public static void m_323937_(
        ItemRenderer p_329376_,
        PoseStack p_330844_,
        MultiBufferSource p_333382_,
        int p_334169_,
        ItemStack p_334880_,
        BakedModel p_334255_,
        boolean p_332793_,
        RandomSource p_331892_
    ) {
        int i = m_320283_(p_334880_.getCount());
        float f = p_334255_.getTransforms().ground.scale.x();
        float f1 = p_334255_.getTransforms().ground.scale.y();
        float f2 = p_334255_.getTransforms().ground.scale.z();
        if (!p_332793_) {
            float f3 = -0.0F * (float)(i - 1) * 0.5F * f;
            float f4 = -0.0F * (float)(i - 1) * 0.5F * f1;
            float f5 = -0.09375F * (float)(i - 1) * 0.5F * f2;
            p_330844_.translate(f3, f4, f5);
        }

        for (int j = 0; j < i; j++) {
            p_330844_.pushPose();
            if (j > 0) {
                if (p_332793_) {
                    float f7 = (p_331892_.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    float f9 = (p_331892_.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    float f6 = (p_331892_.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    p_330844_.translate(f7, f9, f6);
                } else {
                    float f8 = (p_331892_.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
                    float f10 = (p_331892_.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
                    p_330844_.translate(f8, f10, 0.0F);
                }
            }

            p_329376_.render(p_334880_, ItemDisplayContext.GROUND, false, p_330844_, p_333382_, p_334169_, OverlayTexture.NO_OVERLAY, p_334255_);
            p_330844_.popPose();
            if (!p_332793_) {
                p_330844_.translate(0.0F * f, 0.0F * f1, 0.09375F * f2);
            }
        }
    }
}