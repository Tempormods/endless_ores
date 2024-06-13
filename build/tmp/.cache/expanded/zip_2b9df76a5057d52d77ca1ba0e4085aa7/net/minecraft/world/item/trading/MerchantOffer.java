package net.minecraft.world.item.trading;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public class MerchantOffer {
    public static final Codec<MerchantOffer> f_315817_ = RecordCodecBuilder.create(
        p_327696_ -> p_327696_.group(
                    ItemCost.f_314864_.fieldOf("buy").forGetter(p_328146_ -> p_328146_.baseCostA),
                    ItemCost.f_314864_.lenientOptionalFieldOf("buyB").forGetter(p_329936_ -> p_329936_.costB),
                    ItemStack.CODEC.fieldOf("sell").forGetter(p_330911_ -> p_330911_.result),
                    Codec.INT.lenientOptionalFieldOf("uses", Integer.valueOf(0)).forGetter(p_329708_ -> p_329708_.uses),
                    Codec.INT.lenientOptionalFieldOf("maxUses", Integer.valueOf(4)).forGetter(p_334393_ -> p_334393_.maxUses),
                    Codec.BOOL.lenientOptionalFieldOf("rewardExp", Boolean.valueOf(true)).forGetter(p_334163_ -> p_334163_.rewardExp),
                    Codec.INT.lenientOptionalFieldOf("specialPrice", Integer.valueOf(0)).forGetter(p_331018_ -> p_331018_.specialPriceDiff),
                    Codec.INT.lenientOptionalFieldOf("demand", Integer.valueOf(0)).forGetter(p_334425_ -> p_334425_.demand),
                    Codec.FLOAT.lenientOptionalFieldOf("priceMultiplier", Float.valueOf(0.0F)).forGetter(p_335604_ -> p_335604_.priceMultiplier),
                    Codec.INT.lenientOptionalFieldOf("xp", Integer.valueOf(1)).forGetter(p_334362_ -> p_334362_.xp)
                )
                .apply(p_327696_, MerchantOffer::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, MerchantOffer> f_316658_ = StreamCodec.m_320617_(
        MerchantOffer::m_324118_, MerchantOffer::m_320903_
    );
    private final ItemCost baseCostA;
    private final Optional<ItemCost> costB;
    private final ItemStack result;
    private int uses;
    private final int maxUses;
    private final boolean rewardExp;
    private int specialPriceDiff;
    private int demand;
    private final float priceMultiplier;
    private final int xp;

    private MerchantOffer(
        ItemCost p_329205_,
        Optional<ItemCost> p_330242_,
        ItemStack pBaseCostA,
        int pUses,
        int pMaxUses,
        boolean p_336032_,
        int pXp,
        int p_335600_,
        float pPriceMultiplier,
        int p_332893_
    ) {
        this.baseCostA = p_329205_;
        this.costB = p_330242_;
        this.result = pBaseCostA;
        this.uses = pUses;
        this.maxUses = pMaxUses;
        this.rewardExp = p_336032_;
        this.specialPriceDiff = pXp;
        this.demand = p_335600_;
        this.priceMultiplier = pPriceMultiplier;
        this.xp = p_332893_;
    }

    public MerchantOffer(ItemCost p_329227_, ItemStack pBaseCostA, int pMaxUses, int pXp, float pPriceMultiplier) {
        this(p_329227_, Optional.empty(), pBaseCostA, pMaxUses, pXp, pPriceMultiplier);
    }

    public MerchantOffer(ItemCost p_329055_, Optional<ItemCost> p_335735_, ItemStack pBaseCostA, int pMaxUses, int pXp, float pPriceMultiplier) {
        this(p_329055_, p_335735_, pBaseCostA, 0, pMaxUses, pXp, pPriceMultiplier);
    }

    public MerchantOffer(ItemCost p_335696_, Optional<ItemCost> p_334891_, ItemStack pBaseCostA, int pUses, int pMaxUses, int pXp, float pPriceMultiplier) {
        this(p_335696_, p_334891_, pBaseCostA, pUses, pMaxUses, pXp, pPriceMultiplier, 0);
    }

    public MerchantOffer(
        ItemCost p_330366_, Optional<ItemCost> p_332494_, ItemStack p_335597_, int p_333040_, int p_328201_, int p_334322_, float p_333357_, int p_332973_
    ) {
        this(p_330366_, p_332494_, p_335597_, p_333040_, p_328201_, true, 0, p_332973_, p_333357_, p_334322_);
    }

    private MerchantOffer(MerchantOffer pOther) {
        this(
            pOther.baseCostA,
            pOther.costB,
            pOther.result.copy(),
            pOther.uses,
            pOther.maxUses,
            pOther.rewardExp,
            pOther.specialPriceDiff,
            pOther.demand,
            pOther.priceMultiplier,
            pOther.xp
        );
    }

    public ItemStack getBaseCostA() {
        return this.baseCostA.f_315785_();
    }

    public ItemStack getCostA() {
        return this.baseCostA.f_315785_().copyWithCount(this.m_319639_(this.baseCostA));
    }

    private int m_319639_(ItemCost p_330475_) {
        int i = p_330475_.f_314096_();
        int j = Math.max(0, Mth.floor((float)(i * this.demand) * this.priceMultiplier));
        return Mth.clamp(i + j + this.specialPriceDiff, 1, p_330475_.f_315785_().getMaxStackSize());
    }

    public ItemStack getCostB() {
        return this.costB.map(ItemCost::f_315785_).orElse(ItemStack.EMPTY);
    }

    public ItemCost m_319694_() {
        return this.baseCostA;
    }

    public Optional<ItemCost> m_320423_() {
        return this.costB;
    }

    public ItemStack getResult() {
        return this.result;
    }

    public void updateDemand() {
        this.demand = this.demand + this.uses - (this.maxUses - this.uses);
    }

    public ItemStack assemble() {
        return this.result.copy();
    }

    public int getUses() {
        return this.uses;
    }

    public void resetUses() {
        this.uses = 0;
    }

    public int getMaxUses() {
        return this.maxUses;
    }

    public void increaseUses() {
        this.uses++;
    }

    public int getDemand() {
        return this.demand;
    }

    public void addToSpecialPriceDiff(int pAdd) {
        this.specialPriceDiff += pAdd;
    }

    public void resetSpecialPriceDiff() {
        this.specialPriceDiff = 0;
    }

    public int getSpecialPriceDiff() {
        return this.specialPriceDiff;
    }

    public void setSpecialPriceDiff(int pPrice) {
        this.specialPriceDiff = pPrice;
    }

    public float getPriceMultiplier() {
        return this.priceMultiplier;
    }

    public int getXp() {
        return this.xp;
    }

    public boolean isOutOfStock() {
        return this.uses >= this.maxUses;
    }

    public void setToOutOfStock() {
        this.uses = this.maxUses;
    }

    public boolean needsRestock() {
        return this.uses > 0;
    }

    public boolean shouldRewardExp() {
        return this.rewardExp;
    }

    public boolean satisfiedBy(ItemStack pPlayerOfferA, ItemStack pPlayerOfferB) {
        if (!this.baseCostA.m_320083_(pPlayerOfferA) || pPlayerOfferA.getCount() < this.m_319639_(this.baseCostA)) {
            return false;
        } else {
            return !this.costB.isPresent()
                ? pPlayerOfferB.isEmpty()
                : this.costB.get().m_320083_(pPlayerOfferB) && pPlayerOfferB.getCount() >= this.costB.get().f_314096_();
        }
    }

    public boolean take(ItemStack pPlayerOfferA, ItemStack pPlayerOfferB) {
        if (!this.satisfiedBy(pPlayerOfferA, pPlayerOfferB)) {
            return false;
        } else {
            pPlayerOfferA.shrink(this.getCostA().getCount());
            if (!this.getCostB().isEmpty()) {
                pPlayerOfferB.shrink(this.getCostB().getCount());
            }

            return true;
        }
    }

    public MerchantOffer copy() {
        return new MerchantOffer(this);
    }

    private static void m_324118_(RegistryFriendlyByteBuf p_331919_, MerchantOffer p_333750_) {
        ItemCost.f_317082_.m_318638_(p_331919_, p_333750_.m_319694_());
        ItemStack.f_315801_.m_318638_(p_331919_, p_333750_.getResult());
        ItemCost.f_316200_.m_318638_(p_331919_, p_333750_.m_320423_());
        p_331919_.writeBoolean(p_333750_.isOutOfStock());
        p_331919_.writeInt(p_333750_.getUses());
        p_331919_.writeInt(p_333750_.getMaxUses());
        p_331919_.writeInt(p_333750_.getXp());
        p_331919_.writeInt(p_333750_.getSpecialPriceDiff());
        p_331919_.writeFloat(p_333750_.getPriceMultiplier());
        p_331919_.writeInt(p_333750_.getDemand());
    }

    public static MerchantOffer m_320903_(RegistryFriendlyByteBuf p_335331_) {
        ItemCost itemcost = ItemCost.f_317082_.m_318688_(p_335331_);
        ItemStack itemstack = ItemStack.f_315801_.m_318688_(p_335331_);
        Optional<ItemCost> optional = ItemCost.f_316200_.m_318688_(p_335331_);
        boolean flag = p_335331_.readBoolean();
        int i = p_335331_.readInt();
        int j = p_335331_.readInt();
        int k = p_335331_.readInt();
        int l = p_335331_.readInt();
        float f = p_335331_.readFloat();
        int i1 = p_335331_.readInt();
        MerchantOffer merchantoffer = new MerchantOffer(itemcost, optional, itemstack, i, j, k, f, i1);
        if (flag) {
            merchantoffer.setToOutOfStock();
        }

        merchantoffer.setSpecialPriceDiff(l);
        return merchantoffer;
    }
}