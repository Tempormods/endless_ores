package net.minecraft.world.item;

import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.level.Level;

public class ThrowablePotionItem extends PotionItem implements ProjectileItem {
    public ThrowablePotionItem(Item.Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        if (!pLevel.isClientSide) {
            ThrownPotion thrownpotion = new ThrownPotion(pLevel, pPlayer);
            thrownpotion.setItem(itemstack);
            thrownpotion.shootFromRotation(pPlayer, pPlayer.getXRot(), pPlayer.getYRot(), -20.0F, 0.5F, 1.0F);
            pLevel.addFreshEntity(thrownpotion);
        }

        pPlayer.awardStat(Stats.ITEM_USED.get(this));
        itemstack.m_321439_(1, pPlayer);
        return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide());
    }

    @Override
    public Projectile m_319847_(Level p_332520_, Position p_329324_, ItemStack p_333928_, Direction p_335406_) {
        ThrownPotion thrownpotion = new ThrownPotion(p_332520_, p_329324_.x(), p_329324_.y(), p_329324_.z());
        thrownpotion.setItem(p_333928_);
        return thrownpotion;
    }

    @Override
    public ProjectileItem.DispenseConfig m_320420_() {
        return ProjectileItem.DispenseConfig.m_321505_()
            .m_324742_(ProjectileItem.DispenseConfig.f_316643_.f_315383_() * 0.5F)
            .m_318910_(ProjectileItem.DispenseConfig.f_316643_.f_317028_() * 1.25F)
            .m_321407_();
    }
}