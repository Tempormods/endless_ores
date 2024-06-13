package net.minecraft.world.level.block.entity.vault;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.trialspawner.PlayerDetector;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;

public record VaultConfig(
    ResourceKey<LootTable> f_314615_,
    double f_316820_,
    double f_314383_,
    ItemStack f_313949_,
    Optional<ResourceKey<LootTable>> f_314838_,
    PlayerDetector f_315955_,
    PlayerDetector.EntitySelector f_315772_
) {
    static final String f_314429_ = "config";
    static VaultConfig f_314544_ = new VaultConfig();
    static Codec<VaultConfig> f_316227_ = RecordCodecBuilder.<VaultConfig>create(
            p_329931_ -> p_329931_.group(
                        ResourceKey.codec(Registries.f_314309_)
                            .lenientOptionalFieldOf("loot_table", f_314544_.f_314615_())
                            .forGetter(VaultConfig::f_314615_),
                        Codec.DOUBLE.lenientOptionalFieldOf("activation_range", Double.valueOf(f_314544_.f_316820_())).forGetter(VaultConfig::f_316820_),
                        Codec.DOUBLE.lenientOptionalFieldOf("deactivation_range", Double.valueOf(f_314544_.f_314383_())).forGetter(VaultConfig::f_314383_),
                        ItemStack.m_323240_("key_item").forGetter(VaultConfig::f_313949_),
                        ResourceKey.codec(Registries.f_314309_).lenientOptionalFieldOf("override_loot_table_to_display").forGetter(VaultConfig::f_314838_)
                    )
                    .apply(p_329931_, VaultConfig::new)
        )
        .validate(VaultConfig::m_325007_);

    private VaultConfig() {
        this(
            BuiltInLootTables.f_303517_,
            4.0,
            4.5,
            new ItemStack(Items.f_302928_),
            Optional.empty(),
            PlayerDetector.f_314914_,
            PlayerDetector.EntitySelector.f_315930_
        );
    }

    public VaultConfig(ResourceKey<LootTable> p_335368_, double p_335328_, double p_335598_, ItemStack p_328193_, Optional<ResourceKey<LootTable>> p_333693_) {
        this(p_335368_, p_335328_, p_335598_, p_328193_, p_333693_, f_314544_.f_315955_(), f_314544_.f_315772_());
    }

    private DataResult<VaultConfig> m_325007_() {
        return this.f_316820_ > this.f_314383_
            ? DataResult.error(() -> "Activation range must (" + this.f_316820_ + ") be less or equal to deactivation range (" + this.f_314383_ + ")")
            : DataResult.success(this);
    }
}