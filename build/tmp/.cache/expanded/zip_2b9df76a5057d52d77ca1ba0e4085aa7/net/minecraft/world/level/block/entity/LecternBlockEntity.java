package net.minecraft.world.level.block.entity;

import javax.annotation.Nullable;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.Clearable;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.LecternMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.item.component.WritableBookContent;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class LecternBlockEntity extends BlockEntity implements Clearable, MenuProvider {
    public static final int DATA_PAGE = 0;
    public static final int NUM_DATA = 1;
    public static final int SLOT_BOOK = 0;
    public static final int NUM_SLOTS = 1;
    private final Container bookAccess = new Container() {
        @Override
        public int getContainerSize() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return LecternBlockEntity.this.book.isEmpty();
        }

        @Override
        public ItemStack getItem(int p_59580_) {
            return p_59580_ == 0 ? LecternBlockEntity.this.book : ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeItem(int p_59582_, int p_59583_) {
            if (p_59582_ == 0) {
                ItemStack itemstack = LecternBlockEntity.this.book.split(p_59583_);
                if (LecternBlockEntity.this.book.isEmpty()) {
                    LecternBlockEntity.this.onBookItemRemove();
                }

                return itemstack;
            } else {
                return ItemStack.EMPTY;
            }
        }

        @Override
        public ItemStack removeItemNoUpdate(int p_59590_) {
            if (p_59590_ == 0) {
                ItemStack itemstack = LecternBlockEntity.this.book;
                LecternBlockEntity.this.book = ItemStack.EMPTY;
                LecternBlockEntity.this.onBookItemRemove();
                return itemstack;
            } else {
                return ItemStack.EMPTY;
            }
        }

        @Override
        public void setItem(int p_59585_, ItemStack p_59586_) {
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public void setChanged() {
            LecternBlockEntity.this.setChanged();
        }

        @Override
        public boolean stillValid(Player p_59588_) {
            return Container.stillValidBlockEntity(LecternBlockEntity.this, p_59588_) && LecternBlockEntity.this.hasBook();
        }

        @Override
        public boolean canPlaceItem(int p_59592_, ItemStack p_59593_) {
            return false;
        }

        @Override
        public void clearContent() {
        }
    };
    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int p_59600_) {
            return p_59600_ == 0 ? LecternBlockEntity.this.page : 0;
        }

        @Override
        public void set(int p_59602_, int p_59603_) {
            if (p_59602_ == 0) {
                LecternBlockEntity.this.setPage(p_59603_);
            }
        }

        @Override
        public int getCount() {
            return 1;
        }
    };
    ItemStack book = ItemStack.EMPTY;
    int page;
    private int pageCount;

    public LecternBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityType.LECTERN, pPos, pBlockState);
    }

    public ItemStack getBook() {
        return this.book;
    }

    public boolean hasBook() {
        return this.book.is(Items.WRITABLE_BOOK) || this.book.is(Items.WRITTEN_BOOK);
    }

    public void setBook(ItemStack pStack) {
        this.setBook(pStack, null);
    }

    void onBookItemRemove() {
        this.page = 0;
        this.pageCount = 0;
        LecternBlock.resetBookState(null, this.getLevel(), this.getBlockPos(), this.getBlockState(), false);
    }

    public void setBook(ItemStack pStack, @Nullable Player pPlayer) {
        this.book = this.resolveBook(pStack, pPlayer);
        this.page = 0;
        this.pageCount = m_322615_(this.book);
        this.setChanged();
    }

    void setPage(int pPage) {
        int i = Mth.clamp(pPage, 0, this.pageCount - 1);
        if (i != this.page) {
            this.page = i;
            this.setChanged();
            LecternBlock.signalPageChange(this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    public int getPage() {
        return this.page;
    }

    public int getRedstoneSignal() {
        float f = this.pageCount > 1 ? (float)this.getPage() / ((float)this.pageCount - 1.0F) : 1.0F;
        return Mth.floor(f * 14.0F) + (this.hasBook() ? 1 : 0);
    }

    private ItemStack resolveBook(ItemStack pStack, @Nullable Player pPlayer) {
        if (this.level instanceof ServerLevel && pStack.is(Items.WRITTEN_BOOK)) {
            WrittenBookItem.resolveBookComponents(pStack, this.createCommandSourceStack(pPlayer), pPlayer);
        }

        return pStack;
    }

    private CommandSourceStack createCommandSourceStack(@Nullable Player pPlayer) {
        String s;
        Component component;
        if (pPlayer == null) {
            s = "Lectern";
            component = Component.literal("Lectern");
        } else {
            s = pPlayer.getName().getString();
            component = pPlayer.getDisplayName();
        }

        Vec3 vec3 = Vec3.atCenterOf(this.worldPosition);
        return new CommandSourceStack(
            CommandSource.NULL, vec3, Vec2.ZERO, (ServerLevel)this.level, 2, s, component, this.level.getServer(), pPlayer
        );
    }

    @Override
    public boolean onlyOpCanSetNbt() {
        return true;
    }

    @Override
    protected void m_318667_(CompoundTag p_331238_, HolderLookup.Provider p_333677_) {
        super.m_318667_(p_331238_, p_333677_);
        if (p_331238_.contains("Book", 10)) {
            this.book = this.resolveBook(ItemStack.m_323951_(p_333677_, p_331238_.getCompound("Book")).orElse(ItemStack.EMPTY), null);
        } else {
            this.book = ItemStack.EMPTY;
        }

        this.pageCount = m_322615_(this.book);
        this.page = Mth.clamp(p_331238_.getInt("Page"), 0, this.pageCount - 1);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider p_331979_) {
        super.saveAdditional(pTag, p_331979_);
        if (!this.getBook().isEmpty()) {
            pTag.put("Book", this.getBook().save(p_331979_));
            pTag.putInt("Page", this.page);
        }
    }

    @Override
    public void clearContent() {
        this.setBook(ItemStack.EMPTY);
    }

    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new LecternMenu(pContainerId, this.bookAccess, this.dataAccess);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.lectern");
    }

    private static int m_322615_(ItemStack p_330049_) {
        WrittenBookContent writtenbookcontent = p_330049_.m_323252_(DataComponents.f_315840_);
        if (writtenbookcontent != null) {
            return writtenbookcontent.m_319402_().size();
        } else {
            WritableBookContent writablebookcontent = p_330049_.m_323252_(DataComponents.f_314472_);
            return writablebookcontent != null ? writablebookcontent.m_319402_().size() : 0;
        }
    }
}