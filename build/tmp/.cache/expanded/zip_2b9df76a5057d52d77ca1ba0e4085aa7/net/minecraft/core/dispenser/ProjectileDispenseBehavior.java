package net.minecraft.core.dispenser;

import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

public class ProjectileDispenseBehavior extends DefaultDispenseItemBehavior {
    private final ProjectileItem f_315655_;
    private final ProjectileItem.DispenseConfig f_316072_;

    public ProjectileDispenseBehavior(Item p_328671_) {
        if (p_328671_ instanceof ProjectileItem projectileitem) {
            this.f_315655_ = projectileitem;
            this.f_316072_ = projectileitem.m_320420_();
        } else {
            throw new IllegalArgumentException(p_328671_ + " not instance of " + ProjectileItem.class.getSimpleName());
        }
    }

    @Override
    public ItemStack execute(BlockSource p_334330_, ItemStack p_328814_) {
        Level level = p_334330_.level();
        Direction direction = p_334330_.state().getValue(DispenserBlock.FACING);
        Position position = this.f_316072_.f_316313_().m_323176_(p_334330_, direction);
        Projectile projectile = this.f_315655_.m_319847_(level, position, p_328814_, direction);
        this.f_315655_
            .m_319015_(
                projectile,
                (double)direction.getStepX(),
                (double)direction.getStepY(),
                (double)direction.getStepZ(),
                this.f_316072_.f_317028_(),
                this.f_316072_.f_315383_()
            );
        level.addFreshEntity(projectile);
        p_328814_.shrink(1);
        return p_328814_;
    }

    @Override
    protected void playSound(BlockSource p_330598_) {
        p_330598_.level().levelEvent(this.f_316072_.f_314791_().orElse(1002), p_330598_.pos(), 0);
    }
}