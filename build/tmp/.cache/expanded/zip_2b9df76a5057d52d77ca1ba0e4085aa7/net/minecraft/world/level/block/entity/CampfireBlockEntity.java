package net.minecraft.world.level.block.entity;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Clearable;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class CampfireBlockEntity extends BlockEntity implements Clearable {
    private static final int BURN_COOL_SPEED = 2;
    private static final int NUM_SLOTS = 4;
    private final NonNullList<ItemStack> items = NonNullList.withSize(4, ItemStack.EMPTY);
    private final int[] cookingProgress = new int[4];
    private final int[] cookingTime = new int[4];
    private final RecipeManager.CachedCheck<Container, CampfireCookingRecipe> quickCheck = RecipeManager.createCheck(RecipeType.CAMPFIRE_COOKING);

    public CampfireBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityType.CAMPFIRE, pPos, pBlockState);
    }

    public static void cookTick(Level pLevel, BlockPos pPos, BlockState pState, CampfireBlockEntity pBlockEntity) {
        boolean flag = false;

        for (int i = 0; i < pBlockEntity.items.size(); i++) {
            ItemStack itemstack = pBlockEntity.items.get(i);
            if (!itemstack.isEmpty()) {
                flag = true;
                pBlockEntity.cookingProgress[i]++;
                if (pBlockEntity.cookingProgress[i] >= pBlockEntity.cookingTime[i]) {
                    Container container = new SimpleContainer(itemstack);
                    ItemStack itemstack1 = pBlockEntity.quickCheck
                        .getRecipeFor(container, pLevel)
                        .map(p_327303_ -> p_327303_.value().assemble(container, pLevel.registryAccess()))
                        .orElse(itemstack);
                    if (itemstack1.isItemEnabled(pLevel.enabledFeatures())) {
                        Containers.dropItemStack(pLevel, (double)pPos.getX(), (double)pPos.getY(), (double)pPos.getZ(), itemstack1);
                        pBlockEntity.items.set(i, ItemStack.EMPTY);
                        pLevel.sendBlockUpdated(pPos, pState, pState, 3);
                        pLevel.m_322719_(GameEvent.BLOCK_CHANGE, pPos, GameEvent.Context.of(pState));
                    }
                }
            }
        }

        if (flag) {
            setChanged(pLevel, pPos, pState);
        }
    }

    public static void cooldownTick(Level pLevel, BlockPos pPos, BlockState pState, CampfireBlockEntity pBlockEntity) {
        boolean flag = false;

        for (int i = 0; i < pBlockEntity.items.size(); i++) {
            if (pBlockEntity.cookingProgress[i] > 0) {
                flag = true;
                pBlockEntity.cookingProgress[i] = Mth.clamp(pBlockEntity.cookingProgress[i] - 2, 0, pBlockEntity.cookingTime[i]);
            }
        }

        if (flag) {
            setChanged(pLevel, pPos, pState);
        }
    }

    public static void particleTick(Level pLevel, BlockPos pPos, BlockState pState, CampfireBlockEntity pBlockEntity) {
        RandomSource randomsource = pLevel.random;
        if (randomsource.nextFloat() < 0.11F) {
            for (int i = 0; i < randomsource.nextInt(2) + 2; i++) {
                CampfireBlock.makeParticles(pLevel, pPos, pState.getValue(CampfireBlock.SIGNAL_FIRE), false);
            }
        }

        int l = pState.getValue(CampfireBlock.FACING).get2DDataValue();

        for (int j = 0; j < pBlockEntity.items.size(); j++) {
            if (!pBlockEntity.items.get(j).isEmpty() && randomsource.nextFloat() < 0.2F) {
                Direction direction = Direction.from2DDataValue(Math.floorMod(j + l, 4));
                float f = 0.3125F;
                double d0 = (double)pPos.getX()
                    + 0.5
                    - (double)((float)direction.getStepX() * 0.3125F)
                    + (double)((float)direction.getClockWise().getStepX() * 0.3125F);
                double d1 = (double)pPos.getY() + 0.5;
                double d2 = (double)pPos.getZ()
                    + 0.5
                    - (double)((float)direction.getStepZ() * 0.3125F)
                    + (double)((float)direction.getClockWise().getStepZ() * 0.3125F);

                for (int k = 0; k < 4; k++) {
                    pLevel.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0, 5.0E-4, 0.0);
                }
            }
        }
    }

    public NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void m_318667_(CompoundTag p_333177_, HolderLookup.Provider p_333564_) {
        super.m_318667_(p_333177_, p_333564_);
        this.items.clear();
        ContainerHelper.loadAllItems(p_333177_, this.items, p_333564_);
        if (p_333177_.contains("CookingTimes", 11)) {
            int[] aint = p_333177_.getIntArray("CookingTimes");
            System.arraycopy(aint, 0, this.cookingProgress, 0, Math.min(this.cookingTime.length, aint.length));
        }

        if (p_333177_.contains("CookingTotalTimes", 11)) {
            int[] aint1 = p_333177_.getIntArray("CookingTotalTimes");
            System.arraycopy(aint1, 0, this.cookingTime, 0, Math.min(this.cookingTime.length, aint1.length));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider p_336279_) {
        super.saveAdditional(pTag, p_336279_);
        ContainerHelper.saveAllItems(pTag, this.items, true, p_336279_);
        pTag.putIntArray("CookingTimes", this.cookingProgress);
        pTag.putIntArray("CookingTotalTimes", this.cookingTime);
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider p_329092_) {
        CompoundTag compoundtag = new CompoundTag();
        ContainerHelper.saveAllItems(compoundtag, this.items, true, p_329092_);
        return compoundtag;
    }

    public Optional<RecipeHolder<CampfireCookingRecipe>> getCookableRecipe(ItemStack pStack) {
        return this.items.stream().noneMatch(ItemStack::isEmpty)
            ? Optional.empty()
            : this.quickCheck.getRecipeFor(new SimpleContainer(pStack), this.level);
    }

    public boolean placeFood(@Nullable Entity pEntity, ItemStack pStack, int pCookTime) {
        for (int i = 0; i < this.items.size(); i++) {
            ItemStack itemstack = this.items.get(i);
            if (itemstack.isEmpty()) {
                this.cookingTime[i] = pCookTime;
                this.cookingProgress[i] = 0;
                this.items.set(i, pStack.split(1));
                this.level.m_322719_(GameEvent.BLOCK_CHANGE, this.getBlockPos(), GameEvent.Context.of(pEntity, this.getBlockState()));
                this.markUpdated();
                return true;
            }
        }

        return false;
    }

    private void markUpdated() {
        this.setChanged();
        this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    public void dowse() {
        if (this.level != null) {
            this.markUpdated();
        }
    }

    @Override
    protected void m_318741_(BlockEntity.DataComponentInput p_333862_) {
        super.m_318741_(p_333862_);
        p_333862_.m_319031_(DataComponents.f_316065_, ItemContainerContents.f_316619_).m_322022_(this.getItems());
    }

    @Override
    protected void m_318837_(DataComponentMap.Builder p_333455_) {
        super.m_318837_(p_333455_);
        p_333455_.m_322739_(DataComponents.f_316065_, ItemContainerContents.m_320241_(this.getItems()));
    }

    @Override
    public void m_318942_(CompoundTag p_331425_) {
        p_331425_.remove("Items");
    }
}