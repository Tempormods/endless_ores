package net.minecraft.world.entity.vehicle;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class Minecart extends AbstractMinecart {
    public Minecart(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public Minecart(Level pLevel, double pX, double pY, double pZ) {
        super(EntityType.MINECART, pLevel, pX, pY, pZ);
    }

    @Override
    public InteractionResult interact(Player pPlayer, InteractionHand pHand) {
        InteractionResult ret = super.interact(pPlayer, pHand);
        if (ret.consumesAction()) return ret;
        if (pPlayer.isSecondaryUseActive()) {
            return InteractionResult.PASS;
        } else if (this.isVehicle()) {
            return InteractionResult.PASS;
        } else if (!this.level().isClientSide) {
            return pPlayer.startRiding(this) ? InteractionResult.CONSUME : InteractionResult.PASS;
        } else {
            return InteractionResult.SUCCESS;
        }
    }

    @Override
    protected Item getDropItem() {
        return Items.MINECART;
    }

    @Override
    public void activateMinecart(int pX, int pY, int pZ, boolean pReceivingPower) {
        if (pReceivingPower) {
            if (this.isVehicle()) {
                this.ejectPassengers();
            }

            if (this.m_305464_() == 0) {
                this.m_306256_(-this.m_305195_());
                this.m_307446_(10);
                this.m_305563_(50.0F);
                this.markHurt();
            }
        }
    }

    @Override
    public AbstractMinecart.Type getMinecartType() {
        return AbstractMinecart.Type.RIDEABLE;
    }
}
