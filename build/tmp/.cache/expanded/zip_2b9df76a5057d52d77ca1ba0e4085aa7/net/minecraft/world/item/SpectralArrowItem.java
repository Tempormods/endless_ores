package net.minecraft.world.item;

import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.SpectralArrow;
import net.minecraft.world.level.Level;

public class SpectralArrowItem extends ArrowItem {
    public SpectralArrowItem(Item.Properties pProperties) {
        super(pProperties);
    }

    @Override
    public AbstractArrow createArrow(Level pLevel, ItemStack pStack, LivingEntity pShooter) {
        return new SpectralArrow(pLevel, pShooter, pStack.copyWithCount(1));
    }

    @Override
    public Projectile m_319847_(Level p_331476_, Position p_329787_, ItemStack p_328274_, Direction p_330256_) {
        SpectralArrow spectralarrow = new SpectralArrow(p_331476_, p_329787_.x(), p_329787_.y(), p_329787_.z(), p_328274_.copyWithCount(1));
        spectralarrow.pickup = AbstractArrow.Pickup.ALLOWED;
        return spectralarrow;
    }
}