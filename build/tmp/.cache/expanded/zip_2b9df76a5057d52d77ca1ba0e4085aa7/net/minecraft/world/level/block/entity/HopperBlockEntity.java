package net.minecraft.world.level.block.entity;

import java.util.List;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.HopperMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class HopperBlockEntity extends RandomizableContainerBlockEntity implements Hopper {
    public static final int MOVE_ITEM_SPEED = 8;
    public static final int HOPPER_CONTAINER_SIZE = 5;
    private static final int[][] f_315191_ = new int[54][];
    private NonNullList<ItemStack> items = NonNullList.withSize(5, ItemStack.EMPTY);
    private int cooldownTime = -1;
    private long tickedGameTime;
    private Direction f_315657_;

    public HopperBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityType.HOPPER, pPos, pBlockState);
        this.f_315657_ = pBlockState.getValue(HopperBlock.FACING);
    }

    @Override
    protected void m_318667_(CompoundTag p_331195_, HolderLookup.Provider p_329407_) {
        super.m_318667_(p_331195_, p_329407_);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.m_307714_(p_331195_)) {
            ContainerHelper.loadAllItems(p_331195_, this.items, p_329407_);
        }

        this.cooldownTime = p_331195_.getInt("TransferCooldown");
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider p_334921_) {
        super.saveAdditional(pTag, p_334921_);
        if (!this.m_306148_(pTag)) {
            ContainerHelper.saveAllItems(pTag, this.items, p_334921_);
        }

        pTag.putInt("TransferCooldown", this.cooldownTime);
    }

    @Override
    public int getContainerSize() {
        return this.items.size();
    }

    @Override
    public ItemStack removeItem(int pIndex, int pCount) {
        this.m_306438_(null);
        return ContainerHelper.removeItem(this.m_58617_(), pIndex, pCount);
    }

    @Override
    public void setItem(int pIndex, ItemStack pStack) {
        this.m_306438_(null);
        this.m_58617_().set(pIndex, pStack);
        pStack.m_324521_(this.m_322387_(pStack));
    }

    @Override
    public void setBlockState(BlockState p_334323_) {
        super.setBlockState(p_334323_);
        this.f_315657_ = p_334323_.getValue(HopperBlock.FACING);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.hopper");
    }

    public static void pushItemsTick(Level pLevel, BlockPos pPos, BlockState pState, HopperBlockEntity pBlockEntity) {
        pBlockEntity.cooldownTime--;
        pBlockEntity.tickedGameTime = pLevel.getGameTime();
        if (!pBlockEntity.isOnCooldown()) {
            pBlockEntity.setCooldown(0);
            tryMoveItems(pLevel, pPos, pState, pBlockEntity, () -> suckInItems(pLevel, pBlockEntity));
        }
    }

    private static boolean tryMoveItems(Level pLevel, BlockPos pPos, BlockState pState, HopperBlockEntity pBlockEntity, BooleanSupplier pValidator) {
        if (pLevel.isClientSide) {
            return false;
        } else {
            if (!pBlockEntity.isOnCooldown() && pState.getValue(HopperBlock.ENABLED)) {
                boolean flag = false;
                if (!pBlockEntity.isEmpty()) {
                    flag = ejectItems(pLevel, pPos, pBlockEntity);
                }

                if (!pBlockEntity.inventoryFull()) {
                    flag |= pValidator.getAsBoolean();
                }

                if (flag) {
                    pBlockEntity.setCooldown(8);
                    setChanged(pLevel, pPos, pState);
                    return true;
                }
            }

            return false;
        }
    }

    private boolean inventoryFull() {
        for (ItemStack itemstack : this.items) {
            if (itemstack.isEmpty() || itemstack.getCount() != itemstack.getMaxStackSize()) {
                return false;
            }
        }

        return true;
    }

    private static boolean ejectItems(Level pLevel, BlockPos pPos, HopperBlockEntity p_329427_) {
        if (net.minecraftforge.items.VanillaInventoryCodeHooks.insertHook(p_329427_)) return true;
        Container container = getAttachedContainer(pLevel, pPos, p_329427_);
        if (container == null) {
            return false;
        } else {
            Direction direction = p_329427_.f_315657_.getOpposite();
            if (isFullContainer(container, direction)) {
                return false;
            } else {
                for (int i = 0; i < p_329427_.getContainerSize(); i++) {
                    ItemStack itemstack = p_329427_.getItem(i);
                    if (!itemstack.isEmpty()) {
                        int j = itemstack.getCount();
                        ItemStack itemstack1 = addItem(p_329427_, container, p_329427_.removeItem(i, 1), direction);
                        if (itemstack1.isEmpty()) {
                            container.setChanged();
                            return true;
                        }

                        itemstack.setCount(j);
                        if (j == 1) {
                            p_329427_.setItem(i, itemstack);
                        }
                    }
                }

                return false;
            }
        }
    }

    private static int[] getSlots(Container pContainer, Direction pDirection) {
        if (pContainer instanceof WorldlyContainer worldlycontainer) {
            return worldlycontainer.getSlotsForFace(pDirection);
        } else {
            int i = pContainer.getContainerSize();
            if (i < f_315191_.length) {
                int[] aint = f_315191_[i];
                if (aint != null) {
                    return aint;
                } else {
                    int[] aint1 = m_323455_(i);
                    f_315191_[i] = aint1;
                    return aint1;
                }
            } else {
                return m_323455_(i);
            }
        }
    }

    private static int[] m_323455_(int p_329697_) {
        int[] aint = new int[p_329697_];
        int i = 0;

        while (i < aint.length) {
            aint[i] = i++;
        }

        return aint;
    }

    private static boolean isFullContainer(Container pContainer, Direction pDirection) {
        int[] aint = getSlots(pContainer, pDirection);

        for (int i : aint) {
            ItemStack itemstack = pContainer.getItem(i);
            if (itemstack.getCount() < itemstack.getMaxStackSize()) {
                return false;
            }
        }

        return true;
    }

    public static boolean suckInItems(Level pLevel, Hopper pHopper) {
        Boolean ret = net.minecraftforge.items.VanillaInventoryCodeHooks.extractHook(pLevel, pHopper);
        if (ret != null) return ret;
        BlockPos blockpos = BlockPos.containing(pHopper.getLevelX(), pHopper.getLevelY() + 1.0, pHopper.getLevelZ());
        BlockState blockstate = pLevel.getBlockState(blockpos);
        Container container = getSourceContainer(pLevel, pHopper, blockpos, blockstate);
        if (container != null) {
            Direction direction = Direction.DOWN;

            for (int i : getSlots(container, direction)) {
                if (tryTakeInItemFromSlot(pHopper, container, i, direction)) {
                    return true;
                }
            }

            return false;
        } else {
            boolean flag = pHopper.m_320496_() && blockstate.isCollisionShapeFullBlock(pLevel, blockpos) && !blockstate.is(BlockTags.f_317104_);
            if (!flag) {
                for (ItemEntity itementity : getItemsAtAndAbove(pLevel, pHopper)) {
                    if (addItem(pHopper, itementity)) {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    private static boolean tryTakeInItemFromSlot(Hopper pHopper, Container pContainer, int pSlot, Direction pDirection) {
        ItemStack itemstack = pContainer.getItem(pSlot);
        if (!itemstack.isEmpty() && canTakeItemFromContainer(pHopper, pContainer, itemstack, pSlot, pDirection)) {
            int i = itemstack.getCount();
            ItemStack itemstack1 = addItem(pContainer, pHopper, pContainer.removeItem(pSlot, 1), null);
            if (itemstack1.isEmpty()) {
                pContainer.setChanged();
                return true;
            }

            itemstack.setCount(i);
            if (i == 1) {
                pContainer.setItem(pSlot, itemstack);
            }
        }

        return false;
    }

    public static boolean addItem(Container pContainer, ItemEntity pItem) {
        boolean flag = false;
        ItemStack itemstack = pItem.getItem().copy();
        ItemStack itemstack1 = addItem(null, pContainer, itemstack, null);
        if (itemstack1.isEmpty()) {
            flag = true;
            pItem.setItem(ItemStack.EMPTY);
            pItem.discard();
        } else {
            pItem.setItem(itemstack1);
        }

        return flag;
    }

    public static ItemStack addItem(@Nullable Container pSource, Container pDestination, ItemStack pStack, @Nullable Direction pDirection) {
        if (pDestination instanceof WorldlyContainer worldlycontainer && pDirection != null) {
            int[] aint = worldlycontainer.getSlotsForFace(pDirection);

            for (int k = 0; k < aint.length && !pStack.isEmpty(); k++) {
                pStack = tryMoveInItem(pSource, pDestination, pStack, aint[k], pDirection);
            }

            return pStack;
        }

        int i = pDestination.getContainerSize();

        for (int j = 0; j < i && !pStack.isEmpty(); j++) {
            pStack = tryMoveInItem(pSource, pDestination, pStack, j, pDirection);
        }

        return pStack;
    }

    private static boolean canPlaceItemInContainer(Container pContainer, ItemStack pStack, int pSlot, @Nullable Direction pDirection) {
        if (!pContainer.canPlaceItem(pSlot, pStack)) {
            return false;
        } else {
            if (pContainer instanceof WorldlyContainer worldlycontainer && !worldlycontainer.canPlaceItemThroughFace(pSlot, pStack, pDirection)) {
                return false;
            }

            return true;
        }
    }

    private static boolean canTakeItemFromContainer(Container pSource, Container pDestination, ItemStack pStack, int pSlot, Direction pDirection) {
        if (!pDestination.canTakeItem(pSource, pSlot, pStack)) {
            return false;
        } else {
            if (pDestination instanceof WorldlyContainer worldlycontainer && !worldlycontainer.canTakeItemThroughFace(pSlot, pStack, pDirection)) {
                return false;
            }

            return true;
        }
    }

    private static ItemStack tryMoveInItem(@Nullable Container pSource, Container pDestination, ItemStack pStack, int pSlot, @Nullable Direction pDirection) {
        ItemStack itemstack = pDestination.getItem(pSlot);
        if (canPlaceItemInContainer(pDestination, pStack, pSlot, pDirection)) {
            boolean flag = false;
            boolean flag1 = pDestination.isEmpty();
            if (itemstack.isEmpty()) {
                pDestination.setItem(pSlot, pStack);
                pStack = ItemStack.EMPTY;
                flag = true;
            } else if (canMergeItems(itemstack, pStack)) {
                int i = pStack.getMaxStackSize() - itemstack.getCount();
                int j = Math.min(pStack.getCount(), i);
                pStack.shrink(j);
                itemstack.grow(j);
                flag = j > 0;
            }

            if (flag) {
                if (flag1 && pDestination instanceof HopperBlockEntity hopperblockentity1 && !hopperblockentity1.isOnCustomCooldown()) {
                    int k = 0;
                    if (pSource instanceof HopperBlockEntity hopperblockentity && hopperblockentity1.tickedGameTime >= hopperblockentity.tickedGameTime) {
                        k = 1;
                    }

                    hopperblockentity1.setCooldown(8 - k);
                }

                pDestination.setChanged();
            }
        }

        return pStack;
    }

    @Nullable
    private static Container getAttachedContainer(Level pLevel, BlockPos pPos, HopperBlockEntity p_331744_) {
        return getContainerAt(pLevel, pPos.relative(p_331744_.f_315657_));
    }

    @Nullable
    private static Container getSourceContainer(Level pLevel, Hopper pHopper, BlockPos p_330370_, BlockState p_334668_) {
        return getContainerAt(pLevel, p_330370_, p_334668_, pHopper.getLevelX(), pHopper.getLevelY() + 1.0, pHopper.getLevelZ());
    }

    public static List<ItemEntity> getItemsAtAndAbove(Level pLevel, Hopper pHopper) {
        AABB aabb = pHopper.m_319170_().move(pHopper.getLevelX() - 0.5, pHopper.getLevelY() - 0.5, pHopper.getLevelZ() - 0.5);
        return pLevel.getEntitiesOfClass(ItemEntity.class, aabb, EntitySelector.ENTITY_STILL_ALIVE);
    }

    @Nullable
    public static Container getContainerAt(Level pLevel, BlockPos pPos) {
        return getContainerAt(
            pLevel,
            pPos,
            pLevel.getBlockState(pPos),
            (double)pPos.getX() + 0.5,
            (double)pPos.getY() + 0.5,
            (double)pPos.getZ() + 0.5
        );
    }

    @Nullable
    private static Container getContainerAt(Level pLevel, BlockPos p_330520_, BlockState p_334938_, double pX, double pY, double pZ) {
        Container container = m_319514_(pLevel, p_330520_, p_334938_);
        if (container == null) {
            container = m_322585_(pLevel, pX, pY, pZ);
        }

        return container;
    }

    @Nullable
    private static Container m_319514_(Level p_329847_, BlockPos p_329170_, BlockState p_328169_) {
        Block block = p_328169_.getBlock();
        if (block instanceof WorldlyContainerHolder) {
            return ((WorldlyContainerHolder)block).getContainer(p_328169_, p_329847_, p_329170_);
        } else if (p_328169_.hasBlockEntity() && p_329847_.getBlockEntity(p_329170_) instanceof Container container) {
            if (container instanceof ChestBlockEntity && block instanceof ChestBlock) {
                container = ChestBlock.getContainer((ChestBlock)block, p_328169_, p_329847_, p_329170_, true);
            }

            return container;
        } else {
            return null;
        }
    }

    @Nullable
    private static Container m_322585_(Level p_328239_, double p_335152_, double p_336273_, double p_330059_) {
        List<Entity> list = p_328239_.getEntities(
            (Entity)null,
            new AABB(p_335152_ - 0.5, p_336273_ - 0.5, p_330059_ - 0.5, p_335152_ + 0.5, p_336273_ + 0.5, p_330059_ + 0.5),
            EntitySelector.CONTAINER_ENTITY_SELECTOR
        );
        return !list.isEmpty() ? (Container)list.get(p_328239_.random.nextInt(list.size())) : null;
    }

    private static boolean canMergeItems(ItemStack pStack1, ItemStack pStack2) {
        return pStack1.getCount() <= pStack1.getMaxStackSize() && ItemStack.m_322370_(pStack1, pStack2);
    }

    @Override
    public double getLevelX() {
        return (double)this.worldPosition.getX() + 0.5;
    }

    @Override
    public double getLevelY() {
        return (double)this.worldPosition.getY() + 0.5;
    }

    @Override
    public double getLevelZ() {
        return (double)this.worldPosition.getZ() + 0.5;
    }

    @Override
    public boolean m_320496_() {
        return true;
    }

    public void setCooldown(int pCooldownTime) {
        this.cooldownTime = pCooldownTime;
    }

    private boolean isOnCooldown() {
        return this.cooldownTime > 0;
    }

    public boolean isOnCustomCooldown() {
        return this.cooldownTime > 8;
    }

    @Override
    protected NonNullList<ItemStack> m_58617_() {
        return this.items;
    }

    @Override
    protected void m_58609_(NonNullList<ItemStack> pItems) {
        this.items = pItems;
    }

    public static void entityInside(Level pLevel, BlockPos pPos, BlockState pState, Entity pEntity, HopperBlockEntity pBlockEntity) {
        if (pEntity instanceof ItemEntity itementity
            && !itementity.getItem().isEmpty()
            && pEntity.getBoundingBox()
                .move((double)(-pPos.getX()), (double)(-pPos.getY()), (double)(-pPos.getZ()))
                .intersects(pBlockEntity.m_319170_())) {
            tryMoveItems(pLevel, pPos, pState, pBlockEntity, () -> addItem(pBlockEntity, itementity));
        }
    }

    @Override
    protected AbstractContainerMenu createMenu(int pId, Inventory pPlayer) {
        return new HopperMenu(pId, pPlayer, this);
    }

    @Override
    protected net.minecraftforge.items.IItemHandler createUnSidedHandler() {
       return new net.minecraftforge.items.VanillaHopperItemHandler(this);
    }

    public long getLastUpdateTime() {
       return this.tickedGameTime;
    }
}