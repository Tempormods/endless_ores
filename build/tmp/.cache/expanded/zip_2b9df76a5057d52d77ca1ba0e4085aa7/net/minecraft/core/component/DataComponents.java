package net.minecraft.core.component;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.UnaryOperator;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.EncoderCache;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Unit;
import net.minecraft.world.LockCode;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.AdventureModePredicate;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.DebugStickState;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.world.item.component.MapDecorations;
import net.minecraft.world.item.component.MapItemColor;
import net.minecraft.world.item.component.MapPostProcessing;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.component.SeededContainerLoot;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.item.component.WritableBookContent;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.PotDecorations;
import net.minecraft.world.level.saveddata.maps.MapId;

public class DataComponents {
    static final EncoderCache f_315405_ = new EncoderCache(512);
    public static final DataComponentType<CustomData> f_316665_ = m_319350_("custom_data", p_333248_ -> p_333248_.m_319357_(CustomData.f_314012_));
    public static final DataComponentType<Integer> f_314701_ = m_319350_(
        "max_stack_size", p_333287_ -> p_333287_.m_319357_(ExtraCodecs.intRange(1, 99)).m_321554_(ByteBufCodecs.f_316730_)
    );
    public static final DataComponentType<Integer> f_316415_ = m_319350_(
        "max_damage", p_330941_ -> p_330941_.m_319357_(ExtraCodecs.POSITIVE_INT).m_321554_(ByteBufCodecs.f_316730_)
    );
    public static final DataComponentType<Integer> f_313972_ = m_319350_(
        "damage", p_333134_ -> p_333134_.m_319357_(ExtraCodecs.NON_NEGATIVE_INT).m_321554_(ByteBufCodecs.f_316730_)
    );
    public static final DataComponentType<Unbreakable> f_315410_ = m_319350_(
        "unbreakable", p_335474_ -> p_335474_.m_319357_(Unbreakable.f_316475_).m_321554_(Unbreakable.f_315219_)
    );
    public static final DataComponentType<Component> f_316016_ = m_319350_(
        "custom_name", p_332927_ -> p_332927_.m_319357_(ComponentSerialization.f_303675_).m_321554_(ComponentSerialization.f_315335_).m_319193_()
    );
    public static final DataComponentType<Component> f_314548_ = m_319350_(
        "item_name", p_332965_ -> p_332965_.m_319357_(ComponentSerialization.f_303675_).m_321554_(ComponentSerialization.f_315335_).m_319193_()
    );
    public static final DataComponentType<ItemLore> f_315745_ = m_319350_(
        "lore", p_328310_ -> p_328310_.m_319357_(ItemLore.f_316205_).m_321554_(ItemLore.f_316332_).m_319193_()
    );
    public static final DataComponentType<Rarity> f_315029_ = m_319350_(
        "rarity", p_332804_ -> p_332804_.m_319357_(Rarity.f_317080_).m_321554_(Rarity.f_316344_)
    );
    public static final DataComponentType<ItemEnchantments> f_314658_ = m_319350_(
        "enchantments", p_331708_ -> p_331708_.m_319357_(ItemEnchantments.f_315579_).m_321554_(ItemEnchantments.f_316523_).m_319193_()
    );
    public static final DataComponentType<AdventureModePredicate> f_315118_ = m_319350_(
        "can_place_on", p_328700_ -> p_328700_.m_319357_(AdventureModePredicate.f_314196_).m_321554_(AdventureModePredicate.f_315519_).m_319193_()
    );
    public static final DataComponentType<AdventureModePredicate> f_316977_ = m_319350_(
        "can_break", p_334730_ -> p_334730_.m_319357_(AdventureModePredicate.f_314196_).m_321554_(AdventureModePredicate.f_315519_).m_319193_()
    );
    public static final DataComponentType<ItemAttributeModifiers> f_316119_ = m_319350_(
        "attribute_modifiers", p_327741_ -> p_327741_.m_319357_(ItemAttributeModifiers.f_316595_).m_321554_(ItemAttributeModifiers.f_314793_).m_319193_()
    );
    public static final DataComponentType<CustomModelData> f_315513_ = m_319350_(
        "custom_model_data", p_332321_ -> p_332321_.m_319357_(CustomModelData.f_315539_).m_321554_(CustomModelData.f_316249_)
    );
    public static final DataComponentType<Unit> f_316186_ = m_319350_(
        "hide_additional_tooltip", p_332868_ -> p_332868_.m_319357_(Codec.unit(Unit.INSTANCE)).m_321554_(StreamCodec.m_323136_(Unit.INSTANCE))
    );
    public static final DataComponentType<Unit> f_314049_ = m_319350_(
        "hide_tooltip", p_335810_ -> p_335810_.m_319357_(Codec.unit(Unit.INSTANCE)).m_321554_(StreamCodec.m_323136_(Unit.INSTANCE))
    );
    public static final DataComponentType<Integer> f_315107_ = m_319350_(
        "repair_cost", p_329633_ -> p_329633_.m_319357_(ExtraCodecs.NON_NEGATIVE_INT).m_321554_(ByteBufCodecs.f_316730_)
    );
    public static final DataComponentType<Unit> f_314367_ = m_319350_(
        "creative_slot_lock", p_331381_ -> p_331381_.m_321554_(StreamCodec.m_323136_(Unit.INSTANCE))
    );
    public static final DataComponentType<Boolean> f_315974_ = m_319350_(
        "enchantment_glint_override", p_331407_ -> p_331407_.m_319357_(Codec.BOOL).m_321554_(ByteBufCodecs.f_315514_)
    );
    public static final DataComponentType<Unit> f_314381_ = m_319350_("intangible_projectile", p_334511_ -> p_334511_.m_319357_(Codec.unit(Unit.INSTANCE)));
    public static final DataComponentType<FoodProperties> f_315399_ = m_319350_(
        "food", p_332099_ -> p_332099_.m_319357_(FoodProperties.f_316600_).m_321554_(FoodProperties.f_317144_).m_319193_()
    );
    public static final DataComponentType<Unit> f_315720_ = m_319350_(
        "fire_resistant", p_329265_ -> p_329265_.m_319357_(Codec.unit(Unit.INSTANCE)).m_321554_(StreamCodec.m_323136_(Unit.INSTANCE))
    );
    public static final DataComponentType<Tool> f_314833_ = m_319350_(
        "tool", p_335506_ -> p_335506_.m_319357_(Tool.f_314592_).m_321554_(Tool.f_314459_).m_319193_()
    );
    public static final DataComponentType<ItemEnchantments> f_314515_ = m_319350_(
        "stored_enchantments", p_332435_ -> p_332435_.m_319357_(ItemEnchantments.f_315579_).m_321554_(ItemEnchantments.f_316523_).m_319193_()
    );
    public static final DataComponentType<DyedItemColor> f_315011_ = m_319350_(
        "dyed_color", p_331118_ -> p_331118_.m_319357_(DyedItemColor.f_316765_).m_321554_(DyedItemColor.f_314233_)
    );
    public static final DataComponentType<MapItemColor> f_315135_ = m_319350_(
        "map_color", p_335015_ -> p_335015_.m_319357_(MapItemColor.f_316789_).m_321554_(MapItemColor.f_316757_)
    );
    public static final DataComponentType<MapId> f_315230_ = m_319350_("map_id", p_329955_ -> p_329955_.m_319357_(MapId.f_316792_).m_321554_(MapId.f_315416_));
    public static final DataComponentType<MapDecorations> f_316761_ = m_319350_(
        "map_decorations", p_333417_ -> p_333417_.m_319357_(MapDecorations.f_315314_).m_319193_()
    );
    public static final DataComponentType<MapPostProcessing> f_316856_ = m_319350_(
        "map_post_processing", p_335188_ -> p_335188_.m_321554_(MapPostProcessing.f_315810_)
    );
    public static final DataComponentType<ChargedProjectiles> f_314625_ = m_319350_(
        "charged_projectiles", p_335344_ -> p_335344_.m_319357_(ChargedProjectiles.f_316545_).m_321554_(ChargedProjectiles.f_316708_).m_319193_()
    );
    public static final DataComponentType<BundleContents> f_315394_ = m_319350_(
        "bundle_contents", p_328223_ -> p_328223_.m_319357_(BundleContents.f_316485_).m_321554_(BundleContents.f_316702_).m_319193_()
    );
    public static final DataComponentType<PotionContents> f_314188_ = m_319350_(
        "potion_contents", p_331403_ -> p_331403_.m_319357_(PotionContents.f_315880_).m_321554_(PotionContents.f_315823_).m_319193_()
    );
    public static final DataComponentType<SuspiciousStewEffects> f_316666_ = m_319350_(
        "suspicious_stew_effects", p_333712_ -> p_333712_.m_319357_(SuspiciousStewEffects.f_314598_).m_321554_(SuspiciousStewEffects.f_316377_).m_319193_()
    );
    public static final DataComponentType<WritableBookContent> f_314472_ = m_319350_(
        "writable_book_content", p_335814_ -> p_335814_.m_319357_(WritableBookContent.f_316245_).m_321554_(WritableBookContent.f_316228_).m_319193_()
    );
    public static final DataComponentType<WrittenBookContent> f_315840_ = m_319350_(
        "written_book_content", p_330688_ -> p_330688_.m_319357_(WrittenBookContent.f_315766_).m_321554_(WrittenBookContent.f_315227_).m_319193_()
    );
    public static final DataComponentType<ArmorTrim> f_315199_ = m_319350_(
        "trim", p_334669_ -> p_334669_.m_319357_(ArmorTrim.CODEC).m_321554_(ArmorTrim.f_317134_).m_319193_()
    );
    public static final DataComponentType<DebugStickState> f_314352_ = m_319350_(
        "debug_stick_state", p_330393_ -> p_330393_.m_319357_(DebugStickState.f_314822_).m_319193_()
    );
    public static final DataComponentType<CustomData> f_315141_ = m_319350_(
        "entity_data", p_330635_ -> p_330635_.m_319357_(CustomData.f_316298_).m_321554_(CustomData.f_316654_)
    );
    public static final DataComponentType<CustomData> f_315524_ = m_319350_(
        "bucket_entity_data", p_335954_ -> p_335954_.m_319357_(CustomData.f_314012_).m_321554_(CustomData.f_316654_)
    );
    public static final DataComponentType<CustomData> f_316520_ = m_319350_(
        "block_entity_data", p_329366_ -> p_329366_.m_319357_(CustomData.f_316298_).m_321554_(CustomData.f_316654_)
    );
    public static final DataComponentType<Holder<Instrument>> f_316614_ = m_319350_(
        "instrument", p_330109_ -> p_330109_.m_319357_(Instrument.CODEC).m_321554_(Instrument.f_315407_).m_319193_()
    );
    public static final DataComponentType<Integer> f_314953_ = m_319350_(
        "ominous_bottle_amplifier", p_328390_ -> p_328390_.m_319357_(ExtraCodecs.intRange(0, 4)).m_321554_(ByteBufCodecs.f_316730_)
    );
    public static final DataComponentType<List<ResourceLocation>> f_315842_ = m_319350_(
        "recipes", p_327890_ -> p_327890_.m_319357_(ResourceLocation.CODEC.listOf()).m_319193_()
    );
    public static final DataComponentType<LodestoneTracker> f_314784_ = m_319350_(
        "lodestone_tracker", p_333432_ -> p_333432_.m_319357_(LodestoneTracker.f_314702_).m_321554_(LodestoneTracker.f_316697_).m_319193_()
    );
    public static final DataComponentType<FireworkExplosion> f_315608_ = m_319350_(
        "firework_explosion", p_331824_ -> p_331824_.m_319357_(FireworkExplosion.f_315661_).m_321554_(FireworkExplosion.f_316358_).m_319193_()
    );
    public static final DataComponentType<Fireworks> f_316632_ = m_319350_(
        "fireworks", p_335894_ -> p_335894_.m_319357_(Fireworks.f_316251_).m_321554_(Fireworks.f_314560_).m_319193_()
    );
    public static final DataComponentType<ResolvableProfile> f_315901_ = m_319350_(
        "profile", p_334854_ -> p_334854_.m_319357_(ResolvableProfile.f_315352_).m_321554_(ResolvableProfile.f_316630_).m_319193_()
    );
    public static final DataComponentType<ResourceLocation> f_315959_ = m_319350_(
        "note_block_sound", p_333150_ -> p_333150_.m_319357_(ResourceLocation.CODEC).m_321554_(ResourceLocation.f_314488_)
    );
    public static final DataComponentType<BannerPatternLayers> f_314522_ = m_319350_(
        "banner_patterns", p_328399_ -> p_328399_.m_319357_(BannerPatternLayers.f_315309_).m_321554_(BannerPatternLayers.f_316168_).m_319193_()
    );
    public static final DataComponentType<DyeColor> f_315952_ = m_319350_(
        "base_color", p_328641_ -> p_328641_.m_319357_(DyeColor.CODEC).m_321554_(DyeColor.f_313960_)
    );
    public static final DataComponentType<PotDecorations> f_316536_ = m_319350_(
        "pot_decorations", p_336126_ -> p_336126_.m_319357_(PotDecorations.f_314944_).m_321554_(PotDecorations.f_315461_).m_319193_()
    );
    public static final DataComponentType<ItemContainerContents> f_316065_ = m_319350_(
        "container", p_329021_ -> p_329021_.m_319357_(ItemContainerContents.f_315263_).m_321554_(ItemContainerContents.f_315529_).m_319193_()
    );
    public static final DataComponentType<BlockItemStateProperties> f_315479_ = m_319350_(
        "block_state", p_329706_ -> p_329706_.m_319357_(BlockItemStateProperties.f_315463_).m_321554_(BlockItemStateProperties.f_317137_).m_319193_()
    );
    public static final DataComponentType<List<BeehiveBlockEntity.Occupant>> f_314066_ = m_319350_(
        "bees",
        p_329155_ -> p_329155_.m_319357_(BeehiveBlockEntity.Occupant.f_314670_)
                .m_321554_(BeehiveBlockEntity.Occupant.f_316641_.m_321801_(ByteBufCodecs.m_324765_()))
                .m_319193_()
    );
    public static final DataComponentType<LockCode> f_315242_ = m_319350_("lock", p_327916_ -> p_327916_.m_319357_(LockCode.f_316192_));
    public static final DataComponentType<SeededContainerLoot> f_314304_ = m_319350_(
        "container_loot", p_332758_ -> p_332758_.m_319357_(SeededContainerLoot.f_315295_)
    );
    public static final DataComponentMap f_316136_ = DataComponentMap.m_323371_()
        .m_322739_(f_314701_, 64)
        .m_322739_(f_315745_, ItemLore.f_315439_)
        .m_322739_(f_314658_, ItemEnchantments.f_314789_)
        .m_322739_(f_315107_, 0)
        .m_322739_(f_316119_, ItemAttributeModifiers.f_314473_)
        .m_322739_(f_315029_, Rarity.COMMON)
        .m_318826_();

    public static DataComponentType<?> m_318674_(Registry<DataComponentType<?>> p_330257_) {
        return f_316665_;
    }

    private static <T> DataComponentType<T> m_319350_(String p_335254_, UnaryOperator<DataComponentType.Builder<T>> p_329979_) {
        return Registry.register(BuiltInRegistries.f_315333_, p_335254_, p_329979_.apply(DataComponentType.m_320209_()).m_318929_());
    }
}