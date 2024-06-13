package net.minecraft.world.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.block.Block;

public class DiggerItem extends TieredItem {
    public DiggerItem(Tier pTier, TagKey<Block> pBlocks, Item.Properties pProperties) {
        super(pTier, pProperties.m_324556_(DataComponents.f_314833_, pTier.m_323879_(pBlocks)));
    }

    public static ItemAttributeModifiers m_320415_(Tier p_329664_, float p_328988_, float p_329067_) {
        return ItemAttributeModifiers.m_324327_()
            .m_324947_(
                Attributes.ATTACK_DAMAGE,
                new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", (double)(p_328988_ + p_329664_.getAttackDamageBonus()), AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND
            )
            .m_324947_(
                Attributes.ATTACK_SPEED,
                new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", (double)p_329067_, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND
            )
            .m_320246_();
    }

    @Override
    public boolean hurtEnemy(ItemStack pStack, LivingEntity pTarget, LivingEntity pAttacker) {
        pStack.hurtAndBreak(2, pAttacker, EquipmentSlot.MAINHAND);
        return true;
    }
}