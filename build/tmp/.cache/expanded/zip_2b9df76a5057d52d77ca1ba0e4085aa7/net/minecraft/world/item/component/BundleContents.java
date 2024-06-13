package net.minecraft.world.item.component;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import org.apache.commons.lang3.math.Fraction;

public final class BundleContents implements TooltipComponent {
    public static final BundleContents f_316266_ = new BundleContents(List.of());
    public static final Codec<BundleContents> f_316485_ = ItemStack.CODEC.listOf().xmap(BundleContents::new, p_332949_ -> p_332949_.f_316519_);
    public static final StreamCodec<RegistryFriendlyByteBuf, BundleContents> f_316702_ = ItemStack.f_315801_
        .m_321801_(ByteBufCodecs.m_324765_())
        .m_323038_(BundleContents::new, p_328832_ -> p_328832_.f_316519_);
    private static final Fraction f_314974_ = Fraction.getFraction(1, 16);
    private static final int f_314700_ = -1;
    final List<ItemStack> f_316519_;
    final Fraction f_314240_;

    BundleContents(List<ItemStack> p_331924_, Fraction p_333046_) {
        this.f_316519_ = p_331924_;
        this.f_314240_ = p_333046_;
    }

    public BundleContents(List<ItemStack> p_334686_) {
        this(p_334686_, m_319074_(p_334686_));
    }

    private static Fraction m_319074_(List<ItemStack> p_336274_) {
        Fraction fraction = Fraction.ZERO;

        for (ItemStack itemstack : p_336274_) {
            fraction = fraction.add(m_323521_(itemstack).multiplyBy(Fraction.getFraction(itemstack.getCount(), 1)));
        }

        return fraction;
    }

    static Fraction m_323521_(ItemStack p_334916_) {
        BundleContents bundlecontents = p_334916_.m_323252_(DataComponents.f_315394_);
        if (bundlecontents != null) {
            return f_314974_.add(bundlecontents.m_320631_());
        } else {
            List<BeehiveBlockEntity.Occupant> list = p_334916_.m_322304_(DataComponents.f_314066_, List.of());
            return !list.isEmpty() ? Fraction.ONE : Fraction.getFraction(1, p_334916_.getMaxStackSize());
        }
    }

    public ItemStack m_321524_(int p_329557_) {
        return this.f_316519_.get(p_329557_);
    }

    public Stream<ItemStack> m_324878_() {
        return this.f_316519_.stream().map(ItemStack::copy);
    }

    public Iterable<ItemStack> m_323607_() {
        return this.f_316519_;
    }

    public Iterable<ItemStack> m_322107_() {
        return Lists.transform(this.f_316519_, ItemStack::copy);
    }

    public int m_321706_() {
        return this.f_316519_.size();
    }

    public Fraction m_320631_() {
        return this.f_314240_;
    }

    public boolean m_319610_() {
        return this.f_316519_.isEmpty();
    }

    @Override
    public boolean equals(Object p_330764_) {
        if (this == p_330764_) {
            return true;
        } else {
            return !(p_330764_ instanceof BundleContents bundlecontents)
                ? false
                : this.f_314240_.equals(bundlecontents.f_314240_) && ItemStack.m_319597_(this.f_316519_, bundlecontents.f_316519_);
        }
    }

    @Override
    public int hashCode() {
        return ItemStack.m_318747_(this.f_316519_);
    }

    @Override
    public String toString() {
        return "BundleContents" + this.f_316519_;
    }

    public static class Mutable {
        private final List<ItemStack> f_314198_;
        private Fraction f_315301_;

        public Mutable(BundleContents p_333063_) {
            this.f_314198_ = new ArrayList<>(p_333063_.f_316519_);
            this.f_315301_ = p_333063_.f_314240_;
        }

        public BundleContents.Mutable m_321086_() {
            this.f_314198_.clear();
            this.f_315301_ = Fraction.ZERO;
            return this;
        }

        private int m_324669_(ItemStack p_328563_) {
            if (!p_328563_.isStackable()) {
                return -1;
            } else {
                for (int i = 0; i < this.f_314198_.size(); i++) {
                    if (ItemStack.m_322370_(this.f_314198_.get(i), p_328563_)) {
                        return i;
                    }
                }

                return -1;
            }
        }

        private int m_323004_(ItemStack p_335684_) {
            Fraction fraction = Fraction.ONE.subtract(this.f_315301_);
            return Math.max(fraction.divideBy(BundleContents.m_323521_(p_335684_)).intValue(), 0);
        }

        public int m_319811_(ItemStack p_333873_) {
            if (!p_333873_.isEmpty() && p_333873_.getItem().canFitInsideContainerItems()) {
                int i = Math.min(p_333873_.getCount(), this.m_323004_(p_333873_));
                if (i == 0) {
                    return 0;
                } else {
                    this.f_315301_ = this.f_315301_.add(BundleContents.m_323521_(p_333873_).multiplyBy(Fraction.getFraction(i, 1)));
                    int j = this.m_324669_(p_333873_);
                    if (j != -1) {
                        ItemStack itemstack = this.f_314198_.remove(j);
                        ItemStack itemstack1 = itemstack.copyWithCount(itemstack.getCount() + i);
                        p_333873_.shrink(i);
                        this.f_314198_.add(0, itemstack1);
                    } else {
                        this.f_314198_.add(0, p_333873_.split(i));
                    }

                    return i;
                }
            } else {
                return 0;
            }
        }

        public int m_325088_(Slot p_333053_, Player p_329130_) {
            ItemStack itemstack = p_333053_.getItem();
            int i = this.m_323004_(itemstack);
            return this.m_319811_(p_333053_.safeTake(itemstack.getCount(), i, p_329130_));
        }

        @Nullable
        public ItemStack m_324664_() {
            if (this.f_314198_.isEmpty()) {
                return null;
            } else {
                ItemStack itemstack = this.f_314198_.remove(0).copy();
                this.f_315301_ = this.f_315301_.subtract(BundleContents.m_323521_(itemstack).multiplyBy(Fraction.getFraction(itemstack.getCount(), 1)));
                return itemstack;
            }
        }

        public Fraction m_321048_() {
            return this.f_315301_;
        }

        public BundleContents m_322369_() {
            return new BundleContents(List.copyOf(this.f_314198_), this.f_315301_);
        }
    }
}