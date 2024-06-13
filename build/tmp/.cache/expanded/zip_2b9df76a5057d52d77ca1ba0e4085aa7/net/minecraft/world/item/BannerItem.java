package net.minecraft.world.item;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.AbstractBannerBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import org.apache.commons.lang3.Validate;

public class BannerItem extends StandingAndWallBlockItem {
    public BannerItem(Block pBlock, Block pWallBlock, Item.Properties pProperties) {
        super(pBlock, pWallBlock, pProperties, Direction.DOWN);
        Validate.isInstanceOf(AbstractBannerBlock.class, pBlock);
        Validate.isInstanceOf(AbstractBannerBlock.class, pWallBlock);
    }

    public static void appendHoverTextFromBannerBlockEntityTag(ItemStack pStack, List<Component> pTooltipComponents) {
        BannerPatternLayers bannerpatternlayers = pStack.m_323252_(DataComponents.f_314522_);
        if (bannerpatternlayers != null) {
            for (int i = 0; i < Math.min(bannerpatternlayers.f_315710_().size(), 6); i++) {
                BannerPatternLayers.Layer bannerpatternlayers$layer = bannerpatternlayers.f_315710_().get(i);
                pTooltipComponents.add(bannerpatternlayers$layer.m_323334_().withStyle(ChatFormatting.GRAY));
            }
        }
    }

    public DyeColor getColor() {
        return ((AbstractBannerBlock)this.getBlock()).getColor();
    }

    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext p_327823_, List<Component> pTooltip, TooltipFlag pFlag) {
        appendHoverTextFromBannerBlockEntityTag(pStack, pTooltip);
    }
}