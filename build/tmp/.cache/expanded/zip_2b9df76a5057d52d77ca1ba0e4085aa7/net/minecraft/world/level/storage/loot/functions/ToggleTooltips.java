package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.AdventureModePredicate;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class ToggleTooltips extends LootItemConditionalFunction {
    private static final Map<DataComponentType<?>, ToggleTooltips.ComponentToggle<?>> f_315725_ = Stream.of(
            new ToggleTooltips.ComponentToggle<>(DataComponents.f_315199_, ArmorTrim::m_323960_),
            new ToggleTooltips.ComponentToggle<>(DataComponents.f_315011_, DyedItemColor::m_320147_),
            new ToggleTooltips.ComponentToggle<>(DataComponents.f_314658_, ItemEnchantments::m_323674_),
            new ToggleTooltips.ComponentToggle<>(DataComponents.f_314515_, ItemEnchantments::m_323674_),
            new ToggleTooltips.ComponentToggle<>(DataComponents.f_315410_, Unbreakable::m_320618_),
            new ToggleTooltips.ComponentToggle<>(DataComponents.f_316977_, AdventureModePredicate::m_322095_),
            new ToggleTooltips.ComponentToggle<>(DataComponents.f_315118_, AdventureModePredicate::m_322095_),
            new ToggleTooltips.ComponentToggle<>(DataComponents.f_316119_, ItemAttributeModifiers::m_323423_)
        )
        .collect(Collectors.toMap(ToggleTooltips.ComponentToggle::f_316853_, p_331423_ -> (ToggleTooltips.ComponentToggle<?>)p_331423_));
    private static final Codec<ToggleTooltips.ComponentToggle<?>> f_314765_ = BuiltInRegistries.f_315333_
        .byNameCodec()
        .comapFlatMap(
            p_332617_ -> {
                ToggleTooltips.ComponentToggle<?> componenttoggle = f_315725_.get(p_332617_);
                return componenttoggle != null
                    ? DataResult.success(componenttoggle)
                    : DataResult.error(() -> "Can't toggle tooltip visiblity for " + BuiltInRegistries.f_315333_.getKey((DataComponentType<?>)p_332617_));
            },
            ToggleTooltips.ComponentToggle::f_316853_
        );
    public static final MapCodec<ToggleTooltips> f_315888_ = RecordCodecBuilder.mapCodec(
        p_330512_ -> commonFields(p_330512_)
                .and(Codec.unboundedMap(f_314765_, Codec.BOOL).fieldOf("toggles").forGetter(p_331447_ -> p_331447_.f_315101_))
                .apply(p_330512_, ToggleTooltips::new)
    );
    private final Map<ToggleTooltips.ComponentToggle<?>, Boolean> f_315101_;

    private ToggleTooltips(List<LootItemCondition> p_330048_, Map<ToggleTooltips.ComponentToggle<?>, Boolean> p_332012_) {
        super(p_330048_);
        this.f_315101_ = p_332012_;
    }

    @Override
    protected ItemStack run(ItemStack p_334443_, LootContext p_331872_) {
        this.f_315101_.forEach((p_330543_, p_329622_) -> p_330543_.m_324204_(p_334443_, p_329622_));
        return p_334443_;
    }

    @Override
    public LootItemFunctionType<ToggleTooltips> getType() {
        return LootItemFunctions.f_316954_;
    }

    static record ComponentToggle<T>(DataComponentType<T> f_316853_, ToggleTooltips.TooltipWither<T> f_313919_) {
        public void m_324204_(ItemStack p_332822_, boolean p_333699_) {
            T t = p_332822_.m_323252_(this.f_316853_);
            if (t != null) {
                p_332822_.m_322496_(this.f_316853_, this.f_313919_.m_322205_(t, p_333699_));
            }
        }
    }

    @FunctionalInterface
    interface TooltipWither<T> {
        T m_322205_(T p_328719_, boolean p_327686_);
    }
}