package net.minecraft.world.inventory;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CrafterBlock;

public class CrafterMenu extends AbstractContainerMenu implements ContainerListener {
    protected static final int f_302535_ = 9;
    private static final int f_303567_ = 9;
    private static final int f_302593_ = 36;
    private static final int f_302352_ = 36;
    private static final int f_303379_ = 45;
    private final ResultContainer f_302757_ = new ResultContainer();
    private final ContainerData f_302713_;
    private final Player f_303686_;
    private final CraftingContainer f_303456_;

    public CrafterMenu(int p_310742_, Inventory p_312080_) {
        super(MenuType.f_303509_, p_310742_);
        this.f_303686_ = p_312080_.player;
        this.f_302713_ = new SimpleContainerData(10);
        this.f_303456_ = new TransientCraftingContainer(this, 3, 3);
        this.m_305799_(p_312080_);
    }

    public CrafterMenu(int p_312262_, Inventory p_309729_, CraftingContainer p_309543_, ContainerData p_312942_) {
        super(MenuType.f_303509_, p_312262_);
        this.f_303686_ = p_309729_.player;
        this.f_302713_ = p_312942_;
        this.f_303456_ = p_309543_;
        checkContainerSize(p_309543_, 9);
        p_309543_.startOpen(p_309729_.player);
        this.m_305799_(p_309729_);
        this.addSlotListener(this);
    }

    private void m_305799_(Inventory p_312143_) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int k = j + i * 3;
                this.addSlot(new CrafterSlot(this.f_303456_, k, 26 + j * 18, 17 + i * 18, this));
            }
        }

        for (int l = 0; l < 3; l++) {
            for (int j1 = 0; j1 < 9; j1++) {
                this.addSlot(new Slot(p_312143_, j1 + l * 9 + 9, 8 + j1 * 18, 84 + l * 18));
            }
        }

        for (int i1 = 0; i1 < 9; i1++) {
            this.addSlot(new Slot(p_312143_, i1, 8 + i1 * 18, 142));
        }

        this.addSlot(new NonInteractiveResultSlot(this.f_302757_, 0, 134, 35));
        this.addDataSlots(this.f_302713_);
        this.m_305871_();
    }

    public void m_305921_(int p_312148_, boolean p_312187_) {
        CrafterSlot crafterslot = (CrafterSlot)this.getSlot(p_312148_);
        this.f_302713_.set(crafterslot.index, p_312187_ ? 0 : 1);
        this.broadcastChanges();
    }

    public boolean m_305638_(int p_311661_) {
        return p_311661_ > -1 && p_311661_ < 9 ? this.f_302713_.get(p_311661_) == 1 : false;
    }

    public boolean m_305504_() {
        return this.f_302713_.get(9) == 1;
    }

    @Override
    public ItemStack quickMoveStack(Player p_313133_, int p_309724_) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(p_309724_);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (p_309724_ < 9) {
                if (!this.moveItemStackTo(itemstack1, 9, 45, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, 9, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(p_313133_, itemstack1);
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player p_309546_) {
        return this.f_303456_.stillValid(p_309546_);
    }

    private void m_305871_() {
        if (this.f_303686_ instanceof ServerPlayer serverplayer) {
            Level level = serverplayer.level();
            ItemStack itemstack = CrafterBlock.m_305919_(level, this.f_303456_)
                .map(p_327072_ -> p_327072_.value().assemble(this.f_303456_, level.registryAccess()))
                .orElse(ItemStack.EMPTY);
            this.f_302757_.setItem(0, itemstack);
        }
    }

    public Container m_306655_() {
        return this.f_303456_;
    }

    @Override
    public void slotChanged(AbstractContainerMenu p_313164_, int p_310604_, ItemStack p_312680_) {
        this.m_305871_();
    }

    @Override
    public void dataChanged(AbstractContainerMenu p_312122_, int p_310028_, int p_310424_) {
    }
}