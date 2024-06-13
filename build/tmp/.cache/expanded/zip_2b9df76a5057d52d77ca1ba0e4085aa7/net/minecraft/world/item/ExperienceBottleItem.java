package net.minecraft.world.item;

import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownExperienceBottle;
import net.minecraft.world.level.Level;

public class ExperienceBottleItem extends Item implements ProjectileItem {
    public ExperienceBottleItem(Item.Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        pLevel.playSound(
            null,
            pPlayer.getX(),
            pPlayer.getY(),
            pPlayer.getZ(),
            SoundEvents.EXPERIENCE_BOTTLE_THROW,
            SoundSource.NEUTRAL,
            0.5F,
            0.4F / (pLevel.getRandom().nextFloat() * 0.4F + 0.8F)
        );
        if (!pLevel.isClientSide) {
            ThrownExperienceBottle thrownexperiencebottle = new ThrownExperienceBottle(pLevel, pPlayer);
            thrownexperiencebottle.setItem(itemstack);
            thrownexperiencebottle.shootFromRotation(pPlayer, pPlayer.getXRot(), pPlayer.getYRot(), -20.0F, 0.7F, 1.0F);
            pLevel.addFreshEntity(thrownexperiencebottle);
        }

        pPlayer.awardStat(Stats.ITEM_USED.get(this));
        itemstack.m_321439_(1, pPlayer);
        return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide());
    }

    @Override
    public Projectile m_319847_(Level p_329027_, Position p_329351_, ItemStack p_330574_, Direction p_336102_) {
        ThrownExperienceBottle thrownexperiencebottle = new ThrownExperienceBottle(p_329027_, p_329351_.x(), p_329351_.y(), p_329351_.z());
        thrownexperiencebottle.setItem(p_330574_);
        return thrownexperiencebottle;
    }

    @Override
    public ProjectileItem.DispenseConfig m_320420_() {
        return ProjectileItem.DispenseConfig.m_321505_()
            .m_324742_(ProjectileItem.DispenseConfig.f_316643_.f_315383_() * 0.5F)
            .m_318910_(ProjectileItem.DispenseConfig.f_316643_.f_317028_() * 1.25F)
            .m_321407_();
    }
}