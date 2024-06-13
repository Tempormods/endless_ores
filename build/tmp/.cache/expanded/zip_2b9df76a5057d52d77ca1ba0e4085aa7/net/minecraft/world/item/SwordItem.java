package net.minecraft.world.item;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class SwordItem extends TieredItem {
    public SwordItem(Tier pTier, Item.Properties pProperties) {
        super(pTier, pProperties.m_324556_(DataComponents.f_314833_, m_319740_()));
    }

    private static Tool m_319740_() {
        return new Tool(List.of(Tool.Rule.m_321859_(List.of(Blocks.COBWEB), 15.0F), Tool.Rule.m_319062_(BlockTags.SWORD_EFFICIENT, 1.5F)), 1.0F, 2);
    }

    public static ItemAttributeModifiers m_322253_(Tier p_336181_, int p_335543_, float p_331072_) {
        return ItemAttributeModifiers.m_324327_()
            .m_324947_(
                Attributes.ATTACK_DAMAGE,
                new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", (double)((float)p_335543_ + p_336181_.getAttackDamageBonus()), AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND
            )
            .m_324947_(
                Attributes.ATTACK_SPEED,
                new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", (double)p_331072_, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND
            )
            .m_320246_();
    }

    @Override
    public boolean canAttackBlock(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer) {
        return !pPlayer.isCreative();
    }

    @Override
    public boolean hurtEnemy(ItemStack pStack, LivingEntity pTarget, LivingEntity pAttacker) {
        pStack.hurtAndBreak(1, pAttacker, EquipmentSlot.MAINHAND);
        return true;
    }

    @Override
    public boolean canPerformAction(ItemStack stack, net.minecraftforge.common.ToolAction toolAction) {
       return net.minecraftforge.common.ToolActions.DEFAULT_SWORD_ACTIONS.contains(toolAction);
    }
}
