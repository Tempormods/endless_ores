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
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.level.Level;

public class SnowballItem extends Item implements ProjectileItem {
    public SnowballItem(Item.Properties pProperties) {
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
            SoundEvents.SNOWBALL_THROW,
            SoundSource.NEUTRAL,
            0.5F,
            0.4F / (pLevel.getRandom().nextFloat() * 0.4F + 0.8F)
        );
        if (!pLevel.isClientSide) {
            Snowball snowball = new Snowball(pLevel, pPlayer);
            snowball.setItem(itemstack);
            snowball.shootFromRotation(pPlayer, pPlayer.getXRot(), pPlayer.getYRot(), 0.0F, 1.5F, 1.0F);
            pLevel.addFreshEntity(snowball);
        }

        pPlayer.awardStat(Stats.ITEM_USED.get(this));
        itemstack.m_321439_(1, pPlayer);
        return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide());
    }

    @Override
    public Projectile m_319847_(Level p_331733_, Position p_331858_, ItemStack p_327677_, Direction p_328077_) {
        Snowball snowball = new Snowball(p_331733_, p_331858_.x(), p_331858_.y(), p_331858_.z());
        snowball.setItem(p_327677_);
        return snowball;
    }
}