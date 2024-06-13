package net.minecraft.world.item;

import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.component.FireworkExplosion;

public class FireworkStarItem extends Item {
    public FireworkStarItem(Item.Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(ItemStack p_332511_, Item.TooltipContext p_330557_, List<Component> pTooltipComponents, TooltipFlag p_328121_) {
        FireworkExplosion fireworkexplosion = p_332511_.m_323252_(DataComponents.f_315608_);
        if (fireworkexplosion != null) {
            fireworkexplosion.m_319025_(p_330557_, pTooltipComponents::add, p_328121_);
        }
    }
}