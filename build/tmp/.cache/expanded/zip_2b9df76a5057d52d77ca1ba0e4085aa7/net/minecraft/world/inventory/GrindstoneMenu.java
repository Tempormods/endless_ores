package net.minecraft.world.inventory;

import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public class GrindstoneMenu extends AbstractContainerMenu {
    public static final int MAX_NAME_LENGTH = 35;
    public static final int INPUT_SLOT = 0;
    public static final int ADDITIONAL_SLOT = 1;
    public static final int RESULT_SLOT = 2;
    private static final int INV_SLOT_START = 3;
    private static final int INV_SLOT_END = 30;
    private static final int USE_ROW_SLOT_START = 30;
    private static final int USE_ROW_SLOT_END = 39;
    private final Container resultSlots = new ResultContainer();
    final Container repairSlots = new SimpleContainer(2) {
        @Override
        public void setChanged() {
            super.setChanged();
            GrindstoneMenu.this.slotsChanged(this);
        }
    };
    private final ContainerLevelAccess access;
    private int xp = -1;

    public GrindstoneMenu(int pContainerId, Inventory pPlayerInventory) {
        this(pContainerId, pPlayerInventory, ContainerLevelAccess.NULL);
    }

    public GrindstoneMenu(int pContainerId, Inventory pPlayerInventory, final ContainerLevelAccess pAccess) {
        super(MenuType.GRINDSTONE, pContainerId);
        this.access = pAccess;
        this.addSlot(new Slot(this.repairSlots, 0, 49, 19) {
            /**
             * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
             */
            @Override
            public boolean mayPlace(ItemStack p_39607_) {
                return p_39607_.isDamageableItem() || EnchantmentHelper.m_322755_(p_39607_) || p_39607_.canGrindstoneRepair();
            }
        });
        this.addSlot(new Slot(this.repairSlots, 1, 49, 40) {
            /**
             * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
             */
            @Override
            public boolean mayPlace(ItemStack p_39616_) {
                return p_39616_.isDamageableItem() || EnchantmentHelper.m_322755_(p_39616_) || p_39616_.canGrindstoneRepair();
            }
        });
        this.addSlot(new Slot(this.resultSlots, 2, 129, 34) {
            /**
             * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
             */
            @Override
            public boolean mayPlace(ItemStack p_39630_) {
                return false;
            }

            @Override
            public void onTake(Player p_150574_, ItemStack p_150575_) {
                if (net.minecraftforge.common.ForgeHooks.onGrindstoneTake(GrindstoneMenu.this.repairSlots, pAccess, this::getExperienceAmount)) return;
                pAccess.execute((p_39634_, p_39635_) -> {
                    if (p_39634_ instanceof ServerLevel) {
                        ExperienceOrb.award((ServerLevel)p_39634_, Vec3.atCenterOf(p_39635_), this.getExperienceAmount(p_39634_));
                    }

                    p_39634_.levelEvent(1042, p_39635_, 0);
                });
                GrindstoneMenu.this.repairSlots.setItem(0, ItemStack.EMPTY);
                GrindstoneMenu.this.repairSlots.setItem(1, ItemStack.EMPTY);
            }

            /**
             * Returns the total amount of XP stored in all the input slots of this container. The return value is
             * randomized, so that it returns between 50% and 100% of the total XP.
             */
            private int getExperienceAmount(Level p_39632_) {
                if (xp > -1) return xp;
                int l = 0;
                l += this.getExperienceFromItem(GrindstoneMenu.this.repairSlots.getItem(0));
                l += this.getExperienceFromItem(GrindstoneMenu.this.repairSlots.getItem(1));
                if (l > 0) {
                    int i1 = (int)Math.ceil((double)l / 2.0);
                    return i1 + p_39632_.random.nextInt(i1);
                } else {
                    return 0;
                }
            }

            /**
             * Returns the total amount of XP stored in the enchantments of this stack.
             */
            private int getExperienceFromItem(ItemStack p_39637_) {
                int l = 0;
                ItemEnchantments itemenchantments = EnchantmentHelper.m_324152_(p_39637_);

                for (Entry<Holder<Enchantment>> entry : itemenchantments.m_320130_()) {
                    Enchantment enchantment = entry.getKey().value();
                    int i1 = entry.getIntValue();
                    if (!enchantment.isCurse()) {
                        l += enchantment.getMinCost(i1);
                    }
                }

                return l;
            }
        });

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(pPlayerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int k = 0; k < 9; k++) {
            this.addSlot(new Slot(pPlayerInventory, k, 8 + k * 18, 142));
        }
    }

    @Override
    public void slotsChanged(Container pInventory) {
        super.slotsChanged(pInventory);
        if (pInventory == this.repairSlots) {
            this.createResult();
        }
    }

    private void createResult() {
        this.resultSlots.setItem(0, this.m_321447_(this.repairSlots.getItem(0), this.repairSlots.getItem(1)));
        this.broadcastChanges();
    }

    private ItemStack m_321447_(ItemStack p_335167_, ItemStack p_329934_) {
        var event = net.minecraftforge.event.ForgeEventFactory.onGrindstoneChange(p_335167_, p_329934_, this.resultSlots, -1);
        if (event.isCanceled()) {
            this.xp = -1;
            return ItemStack.EMPTY;
        } else if (!event.getOutput().isEmpty()) {
            this.xp = event.getXp();
            return event.getOutput();
        } else {
            this.xp = Integer.MIN_VALUE;
        }

        boolean flag = !p_335167_.isEmpty() || !p_329934_.isEmpty();
        if (!flag) {
            return ItemStack.EMPTY;
        } else if (p_335167_.getCount() <= 1 && p_329934_.getCount() <= 1) {
            boolean flag1 = !p_335167_.isEmpty() && !p_329934_.isEmpty();
            if (!flag1) {
                ItemStack itemstack = !p_335167_.isEmpty() ? p_335167_ : p_329934_;
                return !EnchantmentHelper.m_322755_(itemstack) ? ItemStack.EMPTY : this.m_323249_(itemstack.copy());
            } else {
                return this.m_323116_(p_335167_, p_329934_);
            }
        } else {
            return ItemStack.EMPTY;
        }
    }

    private ItemStack m_323116_(ItemStack p_327826_, ItemStack p_328339_) {
        if (!p_327826_.is(p_328339_.getItem())) {
            return ItemStack.EMPTY;
        } else {
            int i = Math.max(p_327826_.getMaxDamage(), p_328339_.getMaxDamage());
            int j = p_327826_.getMaxDamage() - p_327826_.getDamageValue();
            int k = p_328339_.getMaxDamage() - p_328339_.getDamageValue();
            int l = j + k + i * 5 / 100;
            int i1 = 1;
            if (!p_327826_.isDamageableItem()) {
                if (p_327826_.getMaxStackSize() < 2 || !ItemStack.matches(p_327826_, p_328339_)) {
                    return ItemStack.EMPTY;
                }

                i1 = 2;
            }

            ItemStack itemstack = p_327826_.copyWithCount(i1);
            if (itemstack.isDamageableItem()) {
                itemstack.m_322496_(DataComponents.f_316415_, i);
                itemstack.setDamageValue(Math.max(i - l, 0));
            }

            this.m_323158_(itemstack, p_328339_);
            return this.m_323249_(itemstack);
        }
    }

    private void m_323158_(ItemStack p_332353_, ItemStack p_333431_) {
        EnchantmentHelper.m_320959_(p_332353_, p_327085_ -> {
            ItemEnchantments itemenchantments = EnchantmentHelper.m_324152_(p_333431_);

            for (Entry<Holder<Enchantment>> entry : itemenchantments.m_320130_()) {
                Enchantment enchantment = entry.getKey().value();
                if (!enchantment.isCurse() || p_327085_.m_319403_(enchantment) == 0) {
                    p_327085_.m_323014_(enchantment, entry.getIntValue());
                }
            }
        });
    }

    private ItemStack m_323249_(ItemStack p_332592_) {
        ItemEnchantments itemenchantments = EnchantmentHelper.m_320959_(
            p_332592_, p_327083_ -> p_327083_.m_319910_(p_327086_ -> !p_327086_.value().isCurse())
        );
        if (p_332592_.is(Items.ENCHANTED_BOOK) && itemenchantments.m_324000_()) {
            p_332592_ = p_332592_.m_319323_(Items.BOOK, p_332592_.getCount());
        }

        int i = 0;

        for (int j = 0; j < itemenchantments.m_322852_(); j++) {
            i = AnvilMenu.calculateIncreasedRepairCost(i);
        }

        p_332592_.m_322496_(DataComponents.f_315107_, i);
        return p_332592_;
    }

    @Override
    public void removed(Player pPlayer) {
        super.removed(pPlayer);
        this.access.execute((p_39575_, p_39576_) -> this.clearContainer(pPlayer, this.repairSlots));
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(this.access, pPlayer, Blocks.GRINDSTONE);
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            ItemStack itemstack2 = this.repairSlots.getItem(0);
            ItemStack itemstack3 = this.repairSlots.getItem(1);
            if (pIndex == 2) {
                if (!this.moveItemStackTo(itemstack1, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemstack1, itemstack);
            } else if (pIndex != 0 && pIndex != 1) {
                if (!itemstack2.isEmpty() && !itemstack3.isEmpty()) {
                    if (pIndex >= 3 && pIndex < 30) {
                        if (!this.moveItemStackTo(itemstack1, 30, 39, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (pIndex >= 30 && pIndex < 39 && !this.moveItemStackTo(itemstack1, 3, 30, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(itemstack1, 0, 2, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 3, 39, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(pPlayer, itemstack1);
        }

        return itemstack;
    }
}
