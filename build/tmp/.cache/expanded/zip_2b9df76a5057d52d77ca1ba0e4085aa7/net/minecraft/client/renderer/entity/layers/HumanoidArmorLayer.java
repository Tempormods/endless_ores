package net.minecraft.client.renderer.entity.layers;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Map;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HumanoidArmorLayer<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> extends RenderLayer<T, M> {
    private static final Map<String, ResourceLocation> ARMOR_LOCATION_CACHE = Maps.newHashMap();
    private final A innerModel;
    private final A outerModel;
    private final TextureAtlas armorTrimAtlas;

    public HumanoidArmorLayer(RenderLayerParent<T, M> pRenderer, A pInnerModel, A pOuterModel, ModelManager pModelManager) {
        super(pRenderer);
        this.innerModel = pInnerModel;
        this.outerModel = pOuterModel;
        this.armorTrimAtlas = pModelManager.getAtlas(Sheets.ARMOR_TRIMS_SHEET);
    }

    public void render(
        PoseStack pPoseStack,
        MultiBufferSource pBuffer,
        int pPackedLight,
        T pLivingEntity,
        float pLimbSwing,
        float pLimbSwingAmount,
        float pPartialTicks,
        float pAgeInTicks,
        float pNetHeadYaw,
        float pHeadPitch
    ) {
        this.renderArmorPiece(pPoseStack, pBuffer, pLivingEntity, EquipmentSlot.CHEST, pPackedLight, this.getArmorModel(EquipmentSlot.CHEST));
        this.renderArmorPiece(pPoseStack, pBuffer, pLivingEntity, EquipmentSlot.LEGS, pPackedLight, this.getArmorModel(EquipmentSlot.LEGS));
        this.renderArmorPiece(pPoseStack, pBuffer, pLivingEntity, EquipmentSlot.FEET, pPackedLight, this.getArmorModel(EquipmentSlot.FEET));
        this.renderArmorPiece(pPoseStack, pBuffer, pLivingEntity, EquipmentSlot.HEAD, pPackedLight, this.getArmorModel(EquipmentSlot.HEAD));
    }

    private void renderArmorPiece(PoseStack pPoseStack, MultiBufferSource pBuffer, T pLivingEntity, EquipmentSlot pSlot, int pPackedLight, A pModel) {
        ItemStack itemstack = pLivingEntity.getItemBySlot(pSlot);
        if (itemstack.getItem() instanceof ArmorItem armoritem) {
            if (armoritem.getEquipmentSlot() == pSlot) {
                this.getParentModel().copyPropertiesTo(pModel);
                this.setPartVisibility(pModel, pSlot);
                net.minecraft.client.model.Model model = getArmorModelHook(pLivingEntity, itemstack, pSlot, pModel);
                boolean flag = this.usesInnerModel(pSlot);
                ArmorMaterial armormaterial = armoritem.getMaterial().value();
                int i = itemstack.is(ItemTags.f_314020_) ? DyedItemColor.m_322889_(itemstack, -6265536) : -1;

                for (ArmorMaterial.Layer armormaterial$layer : armormaterial.f_315892_()) {
                    float f;
                    float f1;
                    float f2;
                    if (armormaterial$layer.m_324910_() && i != -1) {
                        f = (float)FastColor.ARGB32.red(i) / 255.0F;
                        f1 = (float)FastColor.ARGB32.green(i) / 255.0F;
                        f2 = (float)FastColor.ARGB32.blue(i) / 255.0F;
                    } else {
                        f = 1.0F;
                        f1 = 1.0F;
                        f2 = 1.0F;
                    }
                    var texture = net.minecraftforge.client.ForgeHooksClient.getArmorTexture(pLivingEntity, itemstack, pSlot, armormaterial$layer, flag);
                    this.renderModel(pPoseStack, pBuffer, pPackedLight, model, f, f1, f2, texture);
                }

                ArmorTrim armortrim = itemstack.m_323252_(DataComponents.f_315199_);
                if (armortrim != null) {
                    this.renderTrim(armoritem.getMaterial(), pPoseStack, pBuffer, pPackedLight, armortrim, model, flag);
                }

                if (itemstack.hasFoil()) {
                    this.renderGlint(pPoseStack, pBuffer, pPackedLight, model);
                }
            }
        }
    }

    protected void setPartVisibility(A pModel, EquipmentSlot pSlot) {
        pModel.setAllVisible(false);
        switch (pSlot) {
            case HEAD:
                pModel.head.visible = true;
                pModel.hat.visible = true;
                break;
            case CHEST:
                pModel.body.visible = true;
                pModel.rightArm.visible = true;
                pModel.leftArm.visible = true;
                break;
            case LEGS:
                pModel.body.visible = true;
                pModel.rightLeg.visible = true;
                pModel.leftLeg.visible = true;
                break;
            case FEET:
                pModel.rightLeg.visible = true;
                pModel.leftLeg.visible = true;
        }
    }

    private void renderModel(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, A pModel, float pRed, float pGreen, float pBlue, ResourceLocation p_328978_) {
        renderModel(pPoseStack, pBuffer, pPackedLight, (net.minecraft.client.model.Model)pModel, pRed, pGreen, pBlue, p_328978_);
    }

    private void renderModel(
        PoseStack pPoseStack,
        MultiBufferSource pBuffer,
        int pPackedLight,
        net.minecraft.client.model.Model pModel,
        float pRed,
        float pGreen,
        float pBlue,
        ResourceLocation p_328978_
    ) {
        VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.armorCutoutNoCull(p_328978_));
        pModel.renderToBuffer(pPoseStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, pRed, pGreen, pBlue, 1.0F);
    }

    private void renderTrim(Holder<ArmorMaterial> p_331988_, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, ArmorTrim pTrim, A pModel, boolean pInnerTexture) {
        renderTrim(p_331988_, pPoseStack, pBuffer, pPackedLight, pTrim, (net.minecraft.client.model.Model)pModel, pInnerTexture);
    }

    private void renderTrim(
        Holder<ArmorMaterial> p_331988_, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, ArmorTrim pTrim, net.minecraft.client.model.Model pModel, boolean pInnerTexture
    ) {
        TextureAtlasSprite textureatlassprite = this.armorTrimAtlas.getSprite(pInnerTexture ? pTrim.innerTexture(p_331988_) : pTrim.outerTexture(p_331988_));
        VertexConsumer vertexconsumer = textureatlassprite.wrap(pBuffer.getBuffer(Sheets.armorTrimsSheet(pTrim.pattern().value().decal())));
        pModel.renderToBuffer(pPoseStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void renderGlint(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, A pModel) {
        renderGlint(pPoseStack, pBuffer, pPackedLight, (net.minecraft.client.model.Model)pModel);
    }

    private void renderGlint(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, net.minecraft.client.model.Model pModel) {
        pModel.renderToBuffer(pPoseStack, pBuffer.getBuffer(RenderType.armorEntityGlint()), pPackedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    private A getArmorModel(EquipmentSlot pSlot) {
        return this.usesInnerModel(pSlot) ? this.innerModel : this.outerModel;
    }

    private boolean usesInnerModel(EquipmentSlot pSlot) {
        return pSlot == EquipmentSlot.LEGS;
    }

    /**
     * Hook to allow item-sensitive armor model. for HumanoidArmorLayer.
     */
    protected net.minecraft.client.model.Model getArmorModelHook(T entity, ItemStack itemStack, EquipmentSlot slot, A model) {
       return net.minecraftforge.client.ForgeHooksClient.getArmorModel(entity, itemStack, slot, model);
    }
}
