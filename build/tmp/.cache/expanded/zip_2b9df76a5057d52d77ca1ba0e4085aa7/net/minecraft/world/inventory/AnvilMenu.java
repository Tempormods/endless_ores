package net.minecraft.world.inventory;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

public class AnvilMenu extends ItemCombinerMenu {
    public static final int INPUT_SLOT = 0;
    public static final int ADDITIONAL_SLOT = 1;
    public static final int RESULT_SLOT = 2;
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final boolean DEBUG_COST = false;
    public static final int MAX_NAME_LENGTH = 50;
    public int repairItemCountCost;
    @Nullable
    private String itemName;
    private final DataSlot cost = DataSlot.standalone();
    private static final int COST_FAIL = 0;
    private static final int COST_BASE = 1;
    private static final int COST_ADDED_BASE = 1;
    private static final int COST_REPAIR_MATERIAL = 1;
    private static final int COST_REPAIR_SACRIFICE = 2;
    private static final int COST_INCOMPATIBLE_PENALTY = 1;
    private static final int COST_RENAME = 1;
    private static final int INPUT_SLOT_X_PLACEMENT = 27;
    private static final int ADDITIONAL_SLOT_X_PLACEMENT = 76;
    private static final int RESULT_SLOT_X_PLACEMENT = 134;
    private static final int SLOT_Y_PLACEMENT = 47;

    public AnvilMenu(int pContainerId, Inventory pPlayerInventory) {
        this(pContainerId, pPlayerInventory, ContainerLevelAccess.NULL);
    }

    public AnvilMenu(int pContainerId, Inventory pPlayerInventory, ContainerLevelAccess pAccess) {
        super(MenuType.ANVIL, pContainerId, pPlayerInventory, pAccess);
        this.addDataSlot(this.cost);
    }

    @Override
    protected ItemCombinerMenuSlotDefinition createInputSlotDefinitions() {
        return ItemCombinerMenuSlotDefinition.create()
            .withSlot(0, 27, 47, p_266635_ -> true)
            .withSlot(1, 76, 47, p_266634_ -> true)
            .withResultSlot(2, 134, 47)
            .build();
    }

    @Override
    protected boolean isValidBlock(BlockState pState) {
        return pState.is(BlockTags.ANVIL);
    }

    @Override
    protected boolean mayPickup(Player pPlayer, boolean pHasStack) {
        return (pPlayer.m_322042_() || pPlayer.experienceLevel >= this.cost.get()) && this.cost.get() > 0;
    }

    @Override
    protected void onTake(Player pPlayer, ItemStack pStack) {
        if (!pPlayer.getAbilities().instabuild) {
            pPlayer.giveExperienceLevels(-this.cost.get());
        }

        float breakChance = net.minecraftforge.event.ForgeEventFactory.onAnvilRepair(pPlayer, pStack, AnvilMenu.this.inputSlots.getItem(0), AnvilMenu.this.inputSlots.getItem(1)).getBreakChance();

        this.inputSlots.setItem(0, ItemStack.EMPTY);
        if (this.repairItemCountCost > 0) {
            ItemStack itemstack = this.inputSlots.getItem(1);
            if (!itemstack.isEmpty() && itemstack.getCount() > this.repairItemCountCost) {
                itemstack.shrink(this.repairItemCountCost);
                this.inputSlots.setItem(1, itemstack);
            } else {
                this.inputSlots.setItem(1, ItemStack.EMPTY);
            }
        } else {
            this.inputSlots.setItem(1, ItemStack.EMPTY);
        }

        this.cost.set(0);
        this.access.execute((p_150479_, p_150480_) -> {
            BlockState blockstate = p_150479_.getBlockState(p_150480_);
            if (!pPlayer.m_322042_() && blockstate.is(BlockTags.ANVIL) && pPlayer.getRandom().nextFloat() < breakChance) {
                BlockState blockstate1 = AnvilBlock.damage(blockstate);
                if (blockstate1 == null) {
                    p_150479_.removeBlock(p_150480_, false);
                    p_150479_.levelEvent(1029, p_150480_, 0);
                } else {
                    p_150479_.setBlock(p_150480_, blockstate1, 2);
                    p_150479_.levelEvent(1030, p_150480_, 0);
                }
            } else {
                p_150479_.levelEvent(1030, p_150480_, 0);
            }
        });
    }

    @Override
    public void createResult() {
        ItemStack itemstack = this.inputSlots.getItem(0);
        this.cost.set(1);
        int i = 0;
        long j = 0L;
        int k = 0;
        if (!itemstack.isEmpty() && EnchantmentHelper.m_320740_(itemstack)) {
            ItemStack itemstack1 = itemstack.copy();
            ItemStack itemstack2 = this.inputSlots.getItem(1);
            ItemEnchantments.Mutable itemenchantments$mutable = new ItemEnchantments.Mutable(EnchantmentHelper.m_324152_(itemstack1));
            j += (long)itemstack.m_322304_(DataComponents.f_315107_, Integer.valueOf(0)).intValue()
                + (long)itemstack2.m_322304_(DataComponents.f_315107_, Integer.valueOf(0)).intValue();
            this.repairItemCountCost = 0;
            boolean flag = false;

            if (!net.minecraftforge.common.ForgeHooks.onAnvilChange(this, itemstack, itemstack2, resultSlots, itemName, j, this.player)) return;
            if (!itemstack2.isEmpty()) {
                flag = itemstack2.m_319951_(DataComponents.f_314515_);
                if (itemstack1.isDamageableItem() && itemstack1.getItem().isValidRepairItem(itemstack, itemstack2)) {
                    int l2 = Math.min(itemstack1.getDamageValue(), itemstack1.getMaxDamage() / 4);
                    if (l2 <= 0) {
                        this.resultSlots.setItem(0, ItemStack.EMPTY);
                        this.cost.set(0);
                        return;
                    }

                    int j3;
                    for (j3 = 0; l2 > 0 && j3 < itemstack2.getCount(); j3++) {
                        int k3 = itemstack1.getDamageValue() - l2;
                        itemstack1.setDamageValue(k3);
                        i++;
                        l2 = Math.min(itemstack1.getDamageValue(), itemstack1.getMaxDamage() / 4);
                    }

                    this.repairItemCountCost = j3;
                } else {
                    if (!flag && (!itemstack1.is(itemstack2.getItem()) || !itemstack1.isDamageableItem())) {
                        this.resultSlots.setItem(0, ItemStack.EMPTY);
                        this.cost.set(0);
                        return;
                    }

                    if (itemstack1.isDamageableItem() && !flag) {
                        int l = itemstack.getMaxDamage() - itemstack.getDamageValue();
                        int i1 = itemstack2.getMaxDamage() - itemstack2.getDamageValue();
                        int j1 = i1 + itemstack1.getMaxDamage() * 12 / 100;
                        int k1 = l + j1;
                        int l1 = itemstack1.getMaxDamage() - k1;
                        if (l1 < 0) {
                            l1 = 0;
                        }

                        if (l1 < itemstack1.getDamageValue()) {
                            itemstack1.setDamageValue(l1);
                            i += 2;
                        }
                    }

                    ItemEnchantments itemenchantments = EnchantmentHelper.m_324152_(itemstack2);
                    boolean flag2 = false;
                    boolean flag3 = false;

                    for (Entry<Holder<Enchantment>> entry : itemenchantments.m_320130_()) {
                        Holder<Enchantment> holder = entry.getKey();
                        Enchantment enchantment = holder.value();
                        int i2 = itemenchantments$mutable.m_319403_(enchantment);
                        int j2 = entry.getIntValue();
                        j2 = i2 == j2 ? j2 + 1 : Math.max(j2, i2);
                        boolean flag1 = enchantment.canEnchant(itemstack);
                        if (this.player.getAbilities().instabuild || itemstack.is(Items.ENCHANTED_BOOK)) {
                            flag1 = true;
                        }

                        for (Holder<Enchantment> holder1 : itemenchantments$mutable.m_318718_()) {
                            if (!holder1.equals(holder) && !enchantment.isCompatibleWith(holder1.value())) {
                                flag1 = false;
                                i++;
                            }
                        }

                        if (!flag1) {
                            flag3 = true;
                        } else {
                            flag2 = true;
                            if (j2 > enchantment.getMaxLevel()) {
                                j2 = enchantment.getMaxLevel();
                            }

                            itemenchantments$mutable.m_319152_(enchantment, j2);
                            int l3 = enchantment.m_320305_();
                            if (flag) {
                                l3 = Math.max(1, l3 / 2);
                            }

                            i += l3 * j2;
                            if (itemstack.getCount() > 1) {
                                i = 40;
                            }
                        }
                    }

                    if (flag3 && !flag2) {
                        this.resultSlots.setItem(0, ItemStack.EMPTY);
                        this.cost.set(0);
                        return;
                    }
                }
            }

            if (this.itemName != null && !StringUtil.m_320314_(this.itemName)) {
                if (!this.itemName.equals(itemstack.getHoverName().getString())) {
                    k = 1;
                    i += k;
                    itemstack1.m_322496_(DataComponents.f_316016_, Component.literal(this.itemName));
                }
            } else if (itemstack.m_319951_(DataComponents.f_316016_)) {
                k = 1;
                i += k;
                itemstack1.m_319322_(DataComponents.f_316016_);
            }

            if (flag && !itemstack1.isBookEnchantable(itemstack2)) {
                itemstack1 = ItemStack.EMPTY;
            }

            int k2 = (int)Mth.clamp(j + (long)i, 0L, 2147483647L);
            this.cost.set(k2);
            if (i <= 0) {
                itemstack1 = ItemStack.EMPTY;
            }

            if (k == i && k > 0 && this.cost.get() >= 40) {
                this.cost.set(39);
            }

            if (this.cost.get() >= 40 && !this.player.getAbilities().instabuild) {
                itemstack1 = ItemStack.EMPTY;
            }

            if (!itemstack1.isEmpty()) {
                int i3 = itemstack1.m_322304_(DataComponents.f_315107_, Integer.valueOf(0));
                if (i3 < itemstack2.m_322304_(DataComponents.f_315107_, Integer.valueOf(0))) {
                    i3 = itemstack2.m_322304_(DataComponents.f_315107_, Integer.valueOf(0));
                }

                if (k != i || k == 0) {
                    i3 = calculateIncreasedRepairCost(i3);
                }

                itemstack1.m_322496_(DataComponents.f_315107_, i3);
                EnchantmentHelper.setEnchantments(itemstack1, itemenchantments$mutable.m_321565_());
            }

            this.resultSlots.setItem(0, itemstack1);
            this.broadcastChanges();
        } else {
            this.resultSlots.setItem(0, ItemStack.EMPTY);
            this.cost.set(0);
        }
    }

    public static int calculateIncreasedRepairCost(int pOldRepairCost) {
        return (int)Math.min((long)pOldRepairCost * 2L + 1L, 2147483647L);
    }

    public boolean setItemName(String pItemName) {
        String s = validateName(pItemName);
        if (s != null && !s.equals(this.itemName)) {
            this.itemName = s;
            if (this.getSlot(2).hasItem()) {
                ItemStack itemstack = this.getSlot(2).getItem();
                if (StringUtil.m_320314_(s)) {
                    itemstack.m_319322_(DataComponents.f_316016_);
                } else {
                    itemstack.m_322496_(DataComponents.f_316016_, Component.literal(s));
                }
            }

            this.createResult();
            return true;
        } else {
            return false;
        }
    }

    @Nullable
    private static String validateName(String pItemName) {
        String s = StringUtil.m_319203_(pItemName);
        return s.length() <= 50 ? s : null;
    }

    public int getCost() {
        return this.cost.get();
    }

    public void setMaximumCost(int value) {
       this.cost.set(value);
    }
}
