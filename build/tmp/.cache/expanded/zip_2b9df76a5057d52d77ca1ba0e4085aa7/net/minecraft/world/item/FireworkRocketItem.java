package net.minecraft.world.item;

import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class FireworkRocketItem extends Item implements ProjectileItem {
    public static final byte[] CRAFTABLE_DURATIONS = new byte[]{1, 2, 3};
    public static final double ROCKET_PLACEMENT_OFFSET = 0.15;

    public FireworkRocketItem(Item.Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        if (!level.isClientSide) {
            ItemStack itemstack = pContext.getItemInHand();
            Vec3 vec3 = pContext.getClickLocation();
            Direction direction = pContext.getClickedFace();
            FireworkRocketEntity fireworkrocketentity = new FireworkRocketEntity(
                level,
                pContext.getPlayer(),
                vec3.x + (double)direction.getStepX() * 0.15,
                vec3.y + (double)direction.getStepY() * 0.15,
                vec3.z + (double)direction.getStepZ() * 0.15,
                itemstack
            );
            level.addFreshEntity(fireworkrocketentity);
            itemstack.shrink(1);
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        if (pPlayer.isFallFlying()) {
            ItemStack itemstack = pPlayer.getItemInHand(pHand);
            if (!pLevel.isClientSide) {
                FireworkRocketEntity fireworkrocketentity = new FireworkRocketEntity(pLevel, itemstack, pPlayer);
                pLevel.addFreshEntity(fireworkrocketentity);
                itemstack.m_321439_(1, pPlayer);
                pPlayer.awardStat(Stats.ITEM_USED.get(this));
            }

            return InteractionResultHolder.sidedSuccess(pPlayer.getItemInHand(pHand), pLevel.isClientSide());
        } else {
            return InteractionResultHolder.pass(pPlayer.getItemInHand(pHand));
        }
    }

    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext p_331795_, List<Component> pTooltip, TooltipFlag pFlag) {
        Fireworks fireworks = pStack.m_323252_(DataComponents.f_316632_);
        if (fireworks != null) {
            fireworks.m_319025_(p_331795_, pTooltip::add, pFlag);
        }
    }

    @Override
    public Projectile m_319847_(Level p_335383_, Position p_331917_, ItemStack p_329236_, Direction p_336387_) {
        return new FireworkRocketEntity(p_335383_, p_329236_.copyWithCount(1), p_331917_.x(), p_331917_.y(), p_331917_.z(), true);
    }

    @Override
    public ProjectileItem.DispenseConfig m_320420_() {
        return ProjectileItem.DispenseConfig.m_321505_().m_321513_(FireworkRocketItem::m_318884_).m_324742_(1.0F).m_318910_(0.5F).m_323513_(1004).m_321407_();
    }

    private static Vec3 m_318884_(BlockSource p_334708_, Direction p_335594_) {
        return p_334708_.center()
            .add(
                (double)p_335594_.getStepX() * (0.5000099999997474 - (double)EntityType.FIREWORK_ROCKET.getWidth() / 2.0),
                (double)p_335594_.getStepY() * (0.5000099999997474 - (double)EntityType.FIREWORK_ROCKET.getHeight() / 2.0)
                    - (double)EntityType.FIREWORK_ROCKET.getHeight() / 2.0,
                (double)p_335594_.getStepZ() * (0.5000099999997474 - (double)EntityType.FIREWORK_ROCKET.getWidth() / 2.0)
            );
    }
}