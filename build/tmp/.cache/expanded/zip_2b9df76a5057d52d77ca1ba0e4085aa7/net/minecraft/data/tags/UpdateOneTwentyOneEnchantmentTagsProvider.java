package net.minecraft.data.tags;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

public class UpdateOneTwentyOneEnchantmentTagsProvider extends EnchantmentTagsProvider {
    public UpdateOneTwentyOneEnchantmentTagsProvider(PackOutput p_335525_, CompletableFuture<HolderLookup.Provider> p_327977_) {
        super(p_335525_, p_327977_, FeatureFlagSet.of(FeatureFlags.VANILLA, FeatureFlags.f_302467_));
    }

    @Override
    protected void addTags(HolderLookup.Provider p_330325_) {
        this.m_321058_(
            p_330325_,
            new Enchantment[]{
                Enchantments.BINDING_CURSE,
                Enchantments.VANISHING_CURSE,
                Enchantments.RIPTIDE,
                Enchantments.CHANNELING,
                Enchantments.f_316259_,
                Enchantments.FROST_WALKER,
                Enchantments.SHARPNESS,
                Enchantments.SMITE,
                Enchantments.BANE_OF_ARTHROPODS,
                Enchantments.IMPALING,
                Enchantments.f_314636_,
                Enchantments.f_314294_,
                Enchantments.f_316771_,
                Enchantments.PIERCING,
                Enchantments.SWEEPING_EDGE,
                Enchantments.MULTISHOT,
                Enchantments.FIRE_ASPECT,
                Enchantments.f_316779_,
                Enchantments.KNOCKBACK,
                Enchantments.f_316860_,
                Enchantments.f_314710_,
                Enchantments.BLAST_PROTECTION,
                Enchantments.FIRE_PROTECTION,
                Enchantments.PROJECTILE_PROTECTION,
                Enchantments.f_315602_,
                Enchantments.f_316753_,
                Enchantments.f_316023_,
                Enchantments.SILK_TOUCH,
                Enchantments.f_314659_,
                Enchantments.f_316758_,
                Enchantments.QUICK_CHARGE,
                Enchantments.f_314874_,
                Enchantments.RESPIRATION,
                Enchantments.AQUA_AFFINITY,
                Enchantments.SOUL_SPEED,
                Enchantments.SWIFT_SNEAK,
                Enchantments.DEPTH_STRIDER,
                Enchantments.THORNS,
                Enchantments.LOYALTY,
                Enchantments.UNBREAKING,
                Enchantments.f_316098_,
                Enchantments.MENDING
            }
        );
    }
}