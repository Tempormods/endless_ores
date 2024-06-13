package net.minecraft.world.entity.vehicle;

import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HasCustomInventoryScreen;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootTable;

public class ChestBoat extends Boat implements HasCustomInventoryScreen, ContainerEntity {
    private static final int CONTAINER_SIZE = 27;
    private NonNullList<ItemStack> itemStacks = NonNullList.withSize(27, ItemStack.EMPTY);
    @Nullable
    private ResourceKey<LootTable> lootTable;
    private long lootTableSeed;

    public ChestBoat(EntityType<? extends Boat> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public ChestBoat(Level pLevel, double pX, double pY, double pZ) {
        super(EntityType.CHEST_BOAT, pLevel);
        this.setPos(pX, pY, pZ);
        this.xo = pX;
        this.yo = pY;
        this.zo = pZ;
    }

    @Override
    protected float getSinglePassengerXOffset() {
        return 0.15F;
    }

    @Override
    protected int getMaxPassengers() {
        return 1;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        this.addChestVehicleSaveData(pCompound, this.m_321891_());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.readChestVehicleSaveData(pCompound, this.m_321891_());
    }

    @Override
    public void m_38227_(DamageSource pDamageSource) {
        this.m_305179_(this.getDropItem());
        this.chestVehicleDestroyed(pDamageSource, this.level(), this);
    }

    @Override
    public void remove(Entity.RemovalReason pReason) {
        if (!this.level().isClientSide && pReason.shouldDestroy()) {
            Containers.dropContents(this.level(), this, this);
        }

        super.remove(pReason);
    }

    @Override
    public InteractionResult interact(Player pPlayer, InteractionHand pHand) {
        if (this.canAddPassenger(pPlayer) && !pPlayer.isSecondaryUseActive()) {
            return super.interact(pPlayer, pHand);
        } else {
            InteractionResult interactionresult = this.interactWithContainerVehicle(pPlayer);
            if (interactionresult.consumesAction()) {
                this.gameEvent(GameEvent.CONTAINER_OPEN, pPlayer);
                PiglinAi.angerNearbyPiglins(pPlayer, true);
            }

            return interactionresult;
        }
    }

    @Override
    public void openCustomInventoryScreen(Player pPlayer) {
        pPlayer.openMenu(this);
        if (!pPlayer.level().isClientSide) {
            this.gameEvent(GameEvent.CONTAINER_OPEN, pPlayer);
            PiglinAi.angerNearbyPiglins(pPlayer, true);
        }
    }

    @Override
    public Item getDropItem() {
        return switch (this.getVariant()) {
            case SPRUCE -> Items.SPRUCE_CHEST_BOAT;
            case BIRCH -> Items.BIRCH_CHEST_BOAT;
            case JUNGLE -> Items.JUNGLE_CHEST_BOAT;
            case ACACIA -> Items.ACACIA_CHEST_BOAT;
            case CHERRY -> Items.CHERRY_CHEST_BOAT;
            case DARK_OAK -> Items.DARK_OAK_CHEST_BOAT;
            case MANGROVE -> Items.MANGROVE_CHEST_BOAT;
            case BAMBOO -> Items.BAMBOO_CHEST_RAFT;
            default -> Items.OAK_CHEST_BOAT;
        };
    }

    @Override
    public void clearContent() {
        this.clearChestVehicleContent();
    }

    @Override
    public int getContainerSize() {
        return 27;
    }

    @Override
    public ItemStack getItem(int pSlot) {
        return this.getChestVehicleItem(pSlot);
    }

    @Override
    public ItemStack removeItem(int pSlot, int pAmount) {
        return this.removeChestVehicleItem(pSlot, pAmount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int pSlot) {
        return this.removeChestVehicleItemNoUpdate(pSlot);
    }

    @Override
    public void setItem(int pSlot, ItemStack pStack) {
        this.setChestVehicleItem(pSlot, pStack);
    }

    @Override
    public SlotAccess getSlot(int pSlot) {
        return this.getChestVehicleSlot(pSlot);
    }

    @Override
    public void setChanged() {
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return this.isChestVehicleStillValid(pPlayer);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        if (this.lootTable != null && pPlayer.isSpectator()) {
            return null;
        } else {
            this.unpackLootTable(pPlayerInventory.player);
            return ChestMenu.threeRows(pContainerId, pPlayerInventory, this);
        }
    }

    public void unpackLootTable(@Nullable Player pPlayer) {
        this.unpackChestVehicleLootTable(pPlayer);
    }

    @Nullable
    @Override
    public ResourceKey<LootTable> getLootTable() {
        return this.lootTable;
    }

    @Override
    public void setLootTable(@Nullable ResourceKey<LootTable> p_329383_) {
        this.lootTable = p_329383_;
    }

    @Override
    public long getLootTableSeed() {
        return this.lootTableSeed;
    }

    @Override
    public void setLootTableSeed(long pLootTableSeed) {
        this.lootTableSeed = pLootTableSeed;
    }

    @Override
    public NonNullList<ItemStack> getItemStacks() {
        return this.itemStacks;
    }

    @Override
    public void clearItemStacks() {
        this.itemStacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
    }

    @Override
    public void stopOpen(Player pPlayer) {
        this.level().gameEvent(GameEvent.CONTAINER_CLOSE, this.position(), GameEvent.Context.of(pPlayer));
    }

    private net.minecraftforge.common.util.LazyOptional<?> itemHandler = net.minecraftforge.common.util.LazyOptional.of(() -> new net.minecraftforge.items.wrapper.InvWrapper(this));

    @Override
    public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable net.minecraft.core.Direction facing) {
        if (capability == net.minecraftforge.common.capabilities.ForgeCapabilities.ITEM_HANDLER && this.isAlive()) {
             return itemHandler.cast();
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemHandler.invalidate();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        itemHandler = net.minecraftforge.common.util.LazyOptional.of(() -> new net.minecraftforge.items.wrapper.InvWrapper(this));
    }
}