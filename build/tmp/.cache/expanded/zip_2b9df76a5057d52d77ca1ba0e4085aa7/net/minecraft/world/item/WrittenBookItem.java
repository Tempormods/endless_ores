package net.minecraft.world.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.util.StringUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.level.Level;

public class WrittenBookItem extends Item {
    public WrittenBookItem(Item.Properties pProperties) {
        super(pProperties);
    }

    @Override
    public Component getName(ItemStack pStack) {
        WrittenBookContent writtenbookcontent = pStack.m_323252_(DataComponents.f_315840_);
        if (writtenbookcontent != null) {
            String s = writtenbookcontent.f_316867_().f_315590_();
            if (!StringUtil.m_320314_(s)) {
                return Component.literal(s);
            }
        }

        return super.getName(pStack);
    }

    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext p_328911_, List<Component> pTooltip, TooltipFlag pFlag) {
        WrittenBookContent writtenbookcontent = pStack.m_323252_(DataComponents.f_315840_);
        if (writtenbookcontent != null) {
            if (!StringUtil.m_320314_(writtenbookcontent.f_316008_())) {
                pTooltip.add(Component.translatable("book.byAuthor", writtenbookcontent.f_316008_()).withStyle(ChatFormatting.GRAY));
            }

            pTooltip.add(Component.translatable("book.generation." + writtenbookcontent.f_314404_()).withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        pPlayer.openItemGui(itemstack, pHand);
        pPlayer.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide());
    }

    public static boolean resolveBookComponents(ItemStack pBookStack, CommandSourceStack pResolvingSource, @Nullable Player pResolvingPlayer) {
        WrittenBookContent writtenbookcontent = pBookStack.m_323252_(DataComponents.f_315840_);
        if (writtenbookcontent != null && !writtenbookcontent.f_316486_()) {
            WrittenBookContent writtenbookcontent1 = writtenbookcontent.m_318598_(pResolvingSource, pResolvingPlayer);
            if (writtenbookcontent1 != null) {
                pBookStack.m_322496_(DataComponents.f_315840_, writtenbookcontent1);
                return true;
            }

            pBookStack.m_322496_(DataComponents.f_315840_, writtenbookcontent.m_321462_());
        }

        return false;
    }
}