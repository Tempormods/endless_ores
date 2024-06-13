package net.minecraft.world.item;

import net.minecraft.tags.BlockTags;

public class PickaxeItem extends DiggerItem {
    public PickaxeItem(Tier pTier, Item.Properties pProperties) {
        super(pTier, BlockTags.MINEABLE_WITH_PICKAXE, pProperties);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, net.minecraftforge.common.ToolAction toolAction) {
        return net.minecraftforge.common.ToolActions.DEFAULT_PICKAXE_ACTIONS.contains(toolAction);
    }
}
