package net.minecraft.world.item;

import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;

public class LingeringPotionItem extends ThrowablePotionItem {
    public LingeringPotionItem(Item.Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext p_327733_, List<Component> pTooltip, TooltipFlag pFlag) {
        PotionContents potioncontents = pStack.m_322304_(DataComponents.f_314188_, PotionContents.f_313984_);
        potioncontents.m_324933_(pTooltip::add, 0.25F, p_327733_.m_319443_());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        pLevel.playSound(
            null,
            pPlayer.getX(),
            pPlayer.getY(),
            pPlayer.getZ(),
            SoundEvents.LINGERING_POTION_THROW,
            SoundSource.NEUTRAL,
            0.5F,
            0.4F / (pLevel.getRandom().nextFloat() * 0.4F + 0.8F)
        );
        return super.use(pLevel, pPlayer, pHand);
    }
}