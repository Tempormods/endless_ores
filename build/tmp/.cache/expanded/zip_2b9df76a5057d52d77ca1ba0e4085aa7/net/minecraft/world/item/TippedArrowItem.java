package net.minecraft.world.item;

import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;

public class TippedArrowItem extends ArrowItem {
    public TippedArrowItem(Item.Properties pProperties) {
        super(pProperties);
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack itemstack = super.getDefaultInstance();
        itemstack.m_322496_(DataComponents.f_314188_, new PotionContents(Potions.POISON));
        return itemstack;
    }

    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext p_328767_, List<Component> pTooltip, TooltipFlag pFlag) {
        PotionContents potioncontents = pStack.m_323252_(DataComponents.f_314188_);
        if (potioncontents != null) {
            potioncontents.m_324933_(pTooltip::add, 0.125F, p_328767_.m_319443_());
        }
    }

    @Override
    public String getDescriptionId(ItemStack pStack) {
        return Potion.getName(pStack.m_322304_(DataComponents.f_314188_, PotionContents.f_313984_).f_317059_(), this.getDescriptionId() + ".effect.");
    }
}