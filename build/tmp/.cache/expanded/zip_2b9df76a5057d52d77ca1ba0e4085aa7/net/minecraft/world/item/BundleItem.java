package net.minecraft.world.item;

import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.math.Fraction;

public class BundleItem extends Item {
    private static final int BAR_COLOR = Mth.color(0.4F, 0.4F, 1.0F);
    private static final int f_316822_ = 64;

    public BundleItem(Item.Properties pProperties) {
        super(pProperties);
    }

    public static float getFullnessDisplay(ItemStack pStack) {
        BundleContents bundlecontents = pStack.m_322304_(DataComponents.f_315394_, BundleContents.f_316266_);
        return bundlecontents.m_320631_().floatValue();
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack pStack, Slot pSlot, ClickAction pAction, Player pPlayer) {
        if (pStack.getCount() != 1 || pAction != ClickAction.SECONDARY) {
            return false;
        } else {
            BundleContents bundlecontents = pStack.m_323252_(DataComponents.f_315394_);
            if (bundlecontents == null) {
                return false;
            } else {
                ItemStack itemstack = pSlot.getItem();
                BundleContents.Mutable bundlecontents$mutable = new BundleContents.Mutable(bundlecontents);
                if (itemstack.isEmpty()) {
                    this.playRemoveOneSound(pPlayer);
                    ItemStack itemstack1 = bundlecontents$mutable.m_324664_();
                    if (itemstack1 != null) {
                        ItemStack itemstack2 = pSlot.safeInsert(itemstack1);
                        bundlecontents$mutable.m_319811_(itemstack2);
                    }
                } else if (itemstack.getItem().canFitInsideContainerItems()) {
                    int i = bundlecontents$mutable.m_325088_(pSlot, pPlayer);
                    if (i > 0) {
                        this.playInsertSound(pPlayer);
                    }
                }

                pStack.m_322496_(DataComponents.f_315394_, bundlecontents$mutable.m_322369_());
                return true;
            }
        }
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack pStack, ItemStack pOther, Slot pSlot, ClickAction pAction, Player pPlayer, SlotAccess pAccess) {
        if (pStack.getCount() != 1) return false;
        if (pAction == ClickAction.SECONDARY && pSlot.allowModification(pPlayer)) {
            BundleContents bundlecontents = pStack.m_323252_(DataComponents.f_315394_);
            if (bundlecontents == null) {
                return false;
            } else {
                BundleContents.Mutable bundlecontents$mutable = new BundleContents.Mutable(bundlecontents);
                if (pOther.isEmpty()) {
                    ItemStack itemstack = bundlecontents$mutable.m_324664_();
                    if (itemstack != null) {
                        this.playRemoveOneSound(pPlayer);
                        pAccess.set(itemstack);
                    }
                } else {
                    int i = bundlecontents$mutable.m_319811_(pOther);
                    if (i > 0) {
                        this.playInsertSound(pPlayer);
                    }
                }

                pStack.m_322496_(DataComponents.f_315394_, bundlecontents$mutable.m_322369_());
                return true;
            }
        } else {
            return false;
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pUsedHand);
        if (dropContents(itemstack, pPlayer)) {
            this.playDropContentsSound(pPlayer);
            pPlayer.awardStat(Stats.ITEM_USED.get(this));
            return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide());
        } else {
            return InteractionResultHolder.fail(itemstack);
        }
    }

    @Override
    public boolean isBarVisible(ItemStack pStack) {
        BundleContents bundlecontents = pStack.m_322304_(DataComponents.f_315394_, BundleContents.f_316266_);
        return bundlecontents.m_320631_().compareTo(Fraction.ZERO) > 0;
    }

    @Override
    public int getBarWidth(ItemStack pStack) {
        BundleContents bundlecontents = pStack.m_322304_(DataComponents.f_315394_, BundleContents.f_316266_);
        return Math.min(1 + Mth.m_320106_(bundlecontents.m_320631_(), 12), 13);
    }

    @Override
    public int getBarColor(ItemStack pStack) {
        return BAR_COLOR;
    }

    private static boolean dropContents(ItemStack pStack, Player pPlayer) {
        BundleContents bundlecontents = pStack.m_323252_(DataComponents.f_315394_);
        if (bundlecontents != null && !bundlecontents.m_319610_()) {
            pStack.m_322496_(DataComponents.f_315394_, BundleContents.f_316266_);
            if (pPlayer instanceof ServerPlayer) {
                bundlecontents.m_322107_().forEach(p_327106_ -> pPlayer.drop(p_327106_, true));
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack pStack) {
        return !pStack.m_319951_(DataComponents.f_314049_) && !pStack.m_319951_(DataComponents.f_316186_)
            ? Optional.ofNullable(pStack.m_323252_(DataComponents.f_315394_)).map(BundleTooltip::new)
            : Optional.empty();
    }

    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext p_329599_, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        BundleContents bundlecontents = pStack.m_323252_(DataComponents.f_315394_);
        if (bundlecontents != null) {
            int i = Mth.m_320106_(bundlecontents.m_320631_(), 64);
            pTooltipComponents.add(Component.translatable("item.minecraft.bundle.fullness", i, 64).withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public void onDestroyed(ItemEntity pItemEntity) {
        BundleContents bundlecontents = pItemEntity.getItem().m_323252_(DataComponents.f_315394_);
        if (bundlecontents != null) {
            pItemEntity.getItem().m_322496_(DataComponents.f_315394_, BundleContents.f_316266_);
            ItemUtils.onContainerDestroyed(pItemEntity, bundlecontents.m_322107_());
        }
    }

    private void playRemoveOneSound(Entity pEntity) {
        pEntity.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + pEntity.level().getRandom().nextFloat() * 0.4F);
    }

    private void playInsertSound(Entity pEntity) {
        pEntity.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + pEntity.level().getRandom().nextFloat() * 0.4F);
    }

    private void playDropContentsSound(Entity pEntity) {
        pEntity.playSound(SoundEvents.BUNDLE_DROP_CONTENTS, 0.8F, 0.8F + pEntity.level().getRandom().nextFloat() * 0.4F);
    }
}
