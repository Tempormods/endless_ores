package net.minecraft.world.item.trading;

import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public class MerchantOffers extends ArrayList<MerchantOffer> {
    public static final Codec<MerchantOffers> f_315743_ = MerchantOffer.f_315817_
        .listOf()
        .fieldOf("Recipes")
        .xmap(MerchantOffers::new, Function.identity())
        .codec();
    public static final StreamCodec<RegistryFriendlyByteBuf, MerchantOffers> f_315991_ = MerchantOffer.f_316658_
        .m_321801_(ByteBufCodecs.m_323312_(MerchantOffers::new));

    public MerchantOffers() {
    }

    private MerchantOffers(int p_220323_) {
        super(p_220323_);
    }

    private MerchantOffers(Collection<MerchantOffer> p_331802_) {
        super(p_331802_);
    }

    @Nullable
    public MerchantOffer getRecipeFor(ItemStack pStackA, ItemStack pStackB, int pIndex) {
        if (pIndex > 0 && pIndex < this.size()) {
            MerchantOffer merchantoffer1 = this.get(pIndex);
            return merchantoffer1.satisfiedBy(pStackA, pStackB) ? merchantoffer1 : null;
        } else {
            for (int i = 0; i < this.size(); i++) {
                MerchantOffer merchantoffer = this.get(i);
                if (merchantoffer.satisfiedBy(pStackA, pStackB)) {
                    return merchantoffer;
                }
            }

            return null;
        }
    }

    public MerchantOffers copy() {
        MerchantOffers merchantoffers = new MerchantOffers(this.size());

        for (MerchantOffer merchantoffer : this) {
            merchantoffers.add(merchantoffer.copy());
        }

        return merchantoffers;
    }
}