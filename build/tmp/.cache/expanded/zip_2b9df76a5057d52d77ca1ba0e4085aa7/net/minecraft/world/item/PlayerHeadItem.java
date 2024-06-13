package net.minecraft.world.item;

import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.SkullBlockEntity;

public class PlayerHeadItem extends StandingAndWallBlockItem {
    public PlayerHeadItem(Block pBlock, Block pWallBlock, Item.Properties pProperties) {
        super(pBlock, pWallBlock, pProperties, Direction.DOWN);
    }

    @Override
    public Component getName(ItemStack pStack) {
        ResolvableProfile resolvableprofile = pStack.m_323252_(DataComponents.f_315901_);
        return (Component)(resolvableprofile != null && resolvableprofile.f_316631_().isPresent()
            ? Component.translatable(this.getDescriptionId() + ".named", resolvableprofile.f_316631_().get())
            : super.getName(pStack));
    }

    @Override
    public void m_324094_(ItemStack p_330776_) {
        ResolvableProfile resolvableprofile = p_330776_.m_323252_(DataComponents.f_315901_);
        if (resolvableprofile != null && !resolvableprofile.m_320408_()) {
            resolvableprofile.m_322305_().thenAcceptAsync(p_330117_ -> p_330776_.m_322496_(DataComponents.f_315901_, p_330117_), SkullBlockEntity.CHECKED_MAIN_THREAD_EXECUTOR);
        }
    }
}