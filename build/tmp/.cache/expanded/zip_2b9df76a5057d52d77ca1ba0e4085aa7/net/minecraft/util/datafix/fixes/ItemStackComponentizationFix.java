package net.minecraft.util.datafix.fixes;

import com.google.common.base.Splitter;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.OptionalDynamic;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.util.Mth;
import net.minecraft.util.datafix.ComponentDataFixUtils;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class ItemStackComponentizationFix extends DataFix {
    private static final int f_315428_ = 1;
    private static final int f_316529_ = 2;
    private static final int f_316101_ = 4;
    private static final int f_317107_ = 8;
    private static final int f_314391_ = 16;
    private static final int f_314773_ = 32;
    private static final int f_316617_ = 64;
    private static final int f_315787_ = 128;
    private static final Set<String> f_316668_ = Set.of("minecraft:potion", "minecraft:splash_potion", "minecraft:lingering_potion", "minecraft:tipped_arrow");
    private static final Set<String> f_316380_ = Set.of(
        "minecraft:pufferfish_bucket",
        "minecraft:salmon_bucket",
        "minecraft:cod_bucket",
        "minecraft:tropical_fish_bucket",
        "minecraft:axolotl_bucket",
        "minecraft:tadpole_bucket"
    );
    private static final List<String> f_314964_ = List.of(
        "NoAI", "Silent", "NoGravity", "Glowing", "Invulnerable", "Health", "Age", "Variant", "HuntingCooldown", "BucketVariantTag"
    );
    private static final Set<String> f_316408_ = Set.of(
        "attached",
        "bottom",
        "conditional",
        "disarmed",
        "drag",
        "enabled",
        "extended",
        "eye",
        "falling",
        "hanging",
        "has_bottle_0",
        "has_bottle_1",
        "has_bottle_2",
        "has_record",
        "has_book",
        "inverted",
        "in_wall",
        "lit",
        "locked",
        "occupied",
        "open",
        "persistent",
        "powered",
        "short",
        "signal_fire",
        "snowy",
        "triggered",
        "unstable",
        "waterlogged",
        "berries",
        "bloom",
        "shrieking",
        "can_summon",
        "up",
        "down",
        "north",
        "east",
        "south",
        "west",
        "slot_0_occupied",
        "slot_1_occupied",
        "slot_2_occupied",
        "slot_3_occupied",
        "slot_4_occupied",
        "slot_5_occupied",
        "cracked",
        "crafting"
    );
    private static final Splitter f_314333_ = Splitter.on(',');

    public ItemStackComponentizationFix(Schema p_331666_) {
        super(p_331666_, true);
    }

    private static void m_319773_(ItemStackComponentizationFix.ItemStackData p_335726_, Dynamic<?> p_329007_) {
        int i = p_335726_.m_318975_("HideFlags").asInt(0);
        p_335726_.m_322646_("Damage", "minecraft:damage", p_329007_.createInt(0));
        p_335726_.m_322646_("RepairCost", "minecraft:repair_cost", p_329007_.createInt(0));
        p_335726_.m_321392_("CustomModelData", "minecraft:custom_model_data");
        p_335726_.m_318975_("BlockStateTag").result().ifPresent(p_333568_ -> p_335726_.m_320760_("minecraft:block_state", m_320221_((Dynamic<?>)p_333568_)));
        p_335726_.m_321392_("EntityTag", "minecraft:entity_data");
        p_335726_.m_319995_("BlockEntityTag", false, p_330163_ -> {
            String s = NamespacedSchema.ensureNamespaced(p_330163_.get("id").asString(""));
            p_330163_ = m_321772_(p_335726_, p_330163_, s);
            Dynamic<?> dynamic2 = p_330163_.remove("id");
            return dynamic2.equals(p_330163_.emptyMap()) ? dynamic2 : p_330163_;
        });
        p_335726_.m_321392_("BlockEntityTag", "minecraft:block_entity_data");
        if (p_335726_.m_318975_("Unbreakable").asBoolean(false)) {
            Dynamic<?> dynamic = p_329007_.emptyMap();
            if ((i & 4) != 0) {
                dynamic = dynamic.set("show_in_tooltip", p_329007_.createBoolean(false));
            }

            p_335726_.m_320760_("minecraft:unbreakable", dynamic);
        }

        m_320987_(p_335726_, p_329007_, "Enchantments", "minecraft:enchantments", (i & 1) != 0);
        if (p_335726_.m_322886_("minecraft:enchanted_book")) {
            m_320987_(p_335726_, p_329007_, "StoredEnchantments", "minecraft:stored_enchantments", (i & 32) != 0);
        }

        p_335726_.m_319995_("display", false, p_332023_ -> m_320020_(p_335726_, p_332023_, i));
        m_319433_(p_335726_, p_329007_, i);
        m_320744_(p_335726_, p_329007_, i);
        Optional<? extends Dynamic<?>> optional = p_335726_.m_318975_("Trim").result();
        if (optional.isPresent()) {
            Dynamic<?> dynamic1 = (Dynamic<?>)optional.get();
            if ((i & 128) != 0) {
                dynamic1 = dynamic1.set("show_in_tooltip", dynamic1.createBoolean(false));
            }

            p_335726_.m_320760_("minecraft:trim", dynamic1);
        }

        if ((i & 32) != 0) {
            p_335726_.m_320760_("minecraft:hide_additional_tooltip", p_329007_.emptyMap());
        }

        if (p_335726_.m_322886_("minecraft:crossbow")) {
            p_335726_.m_318975_("Charged");
            p_335726_.m_322646_("ChargedProjectiles", "minecraft:charged_projectiles", p_329007_.createList(Stream.empty()));
        }

        if (p_335726_.m_322886_("minecraft:bundle")) {
            p_335726_.m_322646_("Items", "minecraft:bundle_contents", p_329007_.createList(Stream.empty()));
        }

        if (p_335726_.m_322886_("minecraft:filled_map")) {
            p_335726_.m_321392_("map", "minecraft:map_id");
            Map<? extends Dynamic<?>, ? extends Dynamic<?>> map = p_335726_.m_318975_("Decorations")
                .asStream()
                .map(ItemStackComponentizationFix::m_321104_)
                .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond, (p_328286_, p_333385_) -> p_328286_));
            if (!map.isEmpty()) {
                p_335726_.m_320760_("minecraft:map_decorations", p_329007_.createMap(map));
            }
        }

        if (p_335726_.m_319518_(f_316668_)) {
            m_318587_(p_335726_, p_329007_);
        }

        if (p_335726_.m_322886_("minecraft:writable_book")) {
            m_322954_(p_335726_, p_329007_);
        }

        if (p_335726_.m_322886_("minecraft:written_book")) {
            m_321607_(p_335726_, p_329007_);
        }

        if (p_335726_.m_322886_("minecraft:suspicious_stew")) {
            p_335726_.m_321392_("effects", "minecraft:suspicious_stew_effects");
        }

        if (p_335726_.m_322886_("minecraft:debug_stick")) {
            p_335726_.m_321392_("DebugProperty", "minecraft:debug_stick_state");
        }

        if (p_335726_.m_319518_(f_316380_)) {
            m_319058_(p_335726_, p_329007_);
        }

        if (p_335726_.m_322886_("minecraft:goat_horn")) {
            p_335726_.m_321392_("instrument", "minecraft:instrument");
        }

        if (p_335726_.m_322886_("minecraft:knowledge_book")) {
            p_335726_.m_321392_("Recipes", "minecraft:recipes");
        }

        if (p_335726_.m_322886_("minecraft:compass")) {
            m_320827_(p_335726_, p_329007_);
        }

        if (p_335726_.m_322886_("minecraft:firework_rocket")) {
            m_321950_(p_335726_);
        }

        if (p_335726_.m_322886_("minecraft:firework_star")) {
            m_322865_(p_335726_);
        }

        if (p_335726_.m_322886_("minecraft:player_head")) {
            p_335726_.m_318975_("SkullOwner").result().ifPresent(p_328052_ -> p_335726_.m_320760_("minecraft:profile", m_322595_((Dynamic<?>)p_328052_)));
        }
    }

    private static Dynamic<?> m_320221_(Dynamic<?> p_329060_) {
        return DataFixUtils.orElse(p_329060_.asMapOpt().result().map(p_331937_ -> p_331937_.collect(Collectors.toMap(Pair::getFirst, p_330078_ -> {
                String s = ((Dynamic)p_330078_.getFirst()).asString("");
                Dynamic<?> dynamic = (Dynamic<?>)p_330078_.getSecond();
                if (f_316408_.contains(s)) {
                    Optional<Boolean> optional = dynamic.asBoolean().result();
                    if (optional.isPresent()) {
                        return dynamic.createString(String.valueOf(optional.get()));
                    }
                }

                Optional<Number> optional1 = dynamic.asNumber().result();
                return optional1.isPresent() ? dynamic.createString(optional1.get().toString()) : dynamic;
            }))).map(p_329060_::createMap), p_329060_);
    }

    private static Dynamic<?> m_320020_(ItemStackComponentizationFix.ItemStackData p_333136_, Dynamic<?> p_329974_, int p_330088_) {
        p_333136_.m_322553_("minecraft:custom_name", p_329974_.get("Name"));
        p_333136_.m_322553_("minecraft:lore", p_329974_.get("Lore"));
        Optional<Integer> optional = p_329974_.get("color").asNumber().result().map(Number::intValue);
        boolean flag = (p_330088_ & 64) != 0;
        if (optional.isPresent() || flag) {
            Dynamic<?> dynamic = p_329974_.emptyMap().set("rgb", p_329974_.createInt(optional.orElse(10511680)));
            if (flag) {
                dynamic = dynamic.set("show_in_tooltip", p_329974_.createBoolean(false));
            }

            p_333136_.m_320760_("minecraft:dyed_color", dynamic);
        }

        Optional<String> optional1 = p_329974_.get("LocName").asString().result();
        if (optional1.isPresent()) {
            p_333136_.m_320760_("minecraft:item_name", ComponentDataFixUtils.m_306987_(p_329974_.getOps(), optional1.get()));
        }

        if (p_333136_.m_322886_("minecraft:filled_map")) {
            p_333136_.m_322553_("minecraft:map_color", p_329974_.get("MapColor"));
            p_329974_ = p_329974_.remove("MapColor");
        }

        return p_329974_.remove("Name").remove("Lore").remove("color").remove("LocName");
    }

    private static <T> Dynamic<T> m_321772_(ItemStackComponentizationFix.ItemStackData p_334120_, Dynamic<T> p_332622_, String p_334133_) {
        p_334120_.m_322553_("minecraft:lock", p_332622_.get("Lock"));
        p_332622_ = p_332622_.remove("Lock");
        Optional<Dynamic<T>> optional = p_332622_.get("LootTable").result();
        if (optional.isPresent()) {
            Dynamic<T> dynamic = p_332622_.emptyMap().set("loot_table", optional.get());
            long i = p_332622_.get("LootTableSeed").asLong(0L);
            if (i != 0L) {
                dynamic = dynamic.set("seed", p_332622_.createLong(i));
            }

            p_334120_.m_320760_("minecraft:container_loot", dynamic);
            p_332622_ = p_332622_.remove("LootTable").remove("LootTableSeed");
        }
        return switch (p_334133_) {
            case "minecraft:skull" -> {
                p_334120_.m_322553_("minecraft:note_block_sound", p_332622_.get("note_block_sound"));
                yield p_332622_.remove("note_block_sound");
            }
            case "minecraft:decorated_pot" -> {
                p_334120_.m_322553_("minecraft:pot_decorations", p_332622_.get("sherds"));
                Optional<Dynamic<T>> optional2 = p_332622_.get("item").result();
                if (optional2.isPresent()) {
                    p_334120_.m_320760_(
                        "minecraft:container",
                        p_332622_.createList(Stream.of(p_332622_.emptyMap().set("slot", p_332622_.createInt(0)).set("item", optional2.get())))
                    );
                }

                yield p_332622_.remove("sherds").remove("item");
            }
            case "minecraft:banner" -> {
                p_334120_.m_322553_("minecraft:banner_patterns", p_332622_.get("patterns"));
                Optional<Number> optional1 = p_332622_.get("Base").asNumber().result();
                if (optional1.isPresent()) {
                    p_334120_.m_320760_("minecraft:base_color", p_332622_.createString(BannerPatternFormatFix.m_322288_(optional1.get().intValue())));
                }

                yield p_332622_.remove("patterns").remove("Base");
            }
            case "minecraft:shulker_box", "minecraft:chest", "minecraft:trapped_chest", "minecraft:furnace", "minecraft:ender_chest", "minecraft:dispenser", "minecraft:dropper", "minecraft:brewing_stand", "minecraft:hopper", "minecraft:barrel", "minecraft:smoker", "minecraft:blast_furnace", "minecraft:campfire", "minecraft:chiseled_bookshelf", "minecraft:crafter" -> {
                List<Dynamic<T>> list = p_332622_.get("Items")
                    .asList(
                        p_334204_ -> p_334204_.emptyMap()
                                .set("slot", p_334204_.createInt(p_334204_.get("Slot").asByte((byte)0) & 255))
                                .set("item", p_334204_.remove("Slot"))
                    );
                if (!list.isEmpty()) {
                    p_334120_.m_320760_("minecraft:container", p_332622_.createList(list.stream()));
                }

                yield p_332622_.remove("Items");
            }
            case "minecraft:beehive" -> {
                p_334120_.m_322553_("minecraft:bees", p_332622_.get("bees"));
                yield p_332622_.remove("bees");
            }
            default -> p_332622_;
        };
    }

    private static void m_320987_(
        ItemStackComponentizationFix.ItemStackData p_332552_, Dynamic<?> p_328849_, String p_333260_, String p_334340_, boolean p_329498_
    ) {
        OptionalDynamic<?> optionaldynamic = p_332552_.m_318975_(p_333260_);
        List<Pair<String, Integer>> list = optionaldynamic.asList(Function.identity())
            .stream()
            .flatMap(p_331550_ -> m_320939_((Dynamic<?>)p_331550_).stream())
            .toList();
        if (!list.isEmpty() || p_329498_) {
            Dynamic<?> dynamic = p_328849_.emptyMap();
            Dynamic<?> dynamic1 = p_328849_.emptyMap();

            for (Pair<String, Integer> pair : list) {
                dynamic1 = dynamic1.set(pair.getFirst(), p_328849_.createInt(pair.getSecond()));
            }

            dynamic = dynamic.set("levels", dynamic1);
            if (p_329498_) {
                dynamic = dynamic.set("show_in_tooltip", p_328849_.createBoolean(false));
            }

            p_332552_.m_320760_(p_334340_, dynamic);
        }

        if (optionaldynamic.result().isPresent() && list.isEmpty()) {
            p_332552_.m_320760_("minecraft:enchantment_glint_override", p_328849_.createBoolean(true));
        }
    }

    private static Optional<Pair<String, Integer>> m_320939_(Dynamic<?> p_328387_) {
        return p_328387_.get("id")
            .asString()
            .apply2stable((p_335475_, p_333076_) -> Pair.of(p_335475_, Mth.clamp(p_333076_.intValue(), 0, 255)), p_328387_.get("lvl").asNumber())
            .result();
    }

    private static void m_319433_(ItemStackComponentizationFix.ItemStackData p_328938_, Dynamic<?> p_336252_, int p_331810_) {
        m_319793_(p_328938_, p_336252_, "CanDestroy", "minecraft:can_break", (p_331810_ & 8) != 0);
        m_319793_(p_328938_, p_336252_, "CanPlaceOn", "minecraft:can_place_on", (p_331810_ & 16) != 0);
    }

    private static void m_319793_(
        ItemStackComponentizationFix.ItemStackData p_331433_, Dynamic<?> p_332377_, String p_332474_, String p_333138_, boolean p_334219_
    ) {
        Optional<? extends Dynamic<?>> optional = p_331433_.m_318975_(p_332474_).result();
        if (!optional.isEmpty()) {
            Dynamic<?> dynamic = p_332377_.emptyMap()
                .set(
                    "predicates",
                    p_332377_.createList(
                        optional.get()
                            .asStream()
                            .map(
                                p_336172_ -> DataFixUtils.orElse(
                                        p_336172_.asString().map(p_330317_ -> m_324996_((Dynamic<?>)p_336172_, p_330317_)).result(), p_336172_
                                    )
                            )
                    )
                );
            if (p_334219_) {
                dynamic = dynamic.set("show_in_tooltip", p_332377_.createBoolean(false));
            }

            p_331433_.m_320760_(p_333138_, dynamic);
        }
    }

    private static Dynamic<?> m_324996_(Dynamic<?> p_330250_, String p_335482_) {
        int i = p_335482_.indexOf(91);
        int j = p_335482_.indexOf(123);
        int k = p_335482_.length();
        if (i != -1) {
            k = i;
        }

        if (j != -1) {
            k = Math.min(k, j);
        }

        String s = p_335482_.substring(0, k);
        Dynamic<?> dynamic = p_330250_.emptyMap().set("blocks", p_330250_.createString(s.trim()));
        int l = p_335482_.indexOf(93);
        if (i != -1 && l != -1) {
            Dynamic<?> dynamic1 = p_330250_.emptyMap();

            for (String s1 : f_314333_.split(p_335482_.substring(i + 1, l))) {
                int i1 = s1.indexOf(61);
                if (i1 != -1) {
                    String s2 = s1.substring(0, i1).trim();
                    String s3 = s1.substring(i1 + 1).trim();
                    dynamic1 = dynamic1.set(s2, p_330250_.createString(s3));
                }
            }

            dynamic = dynamic.set("state", dynamic1);
        }

        int j1 = p_335482_.indexOf(125);
        if (j != -1 && j1 != -1) {
            dynamic = dynamic.set("nbt", p_330250_.createString(p_335482_.substring(j, j1 + 1)));
        }

        return dynamic;
    }

    private static void m_320744_(ItemStackComponentizationFix.ItemStackData p_329869_, Dynamic<?> p_332943_, int p_330062_) {
        List<? extends Dynamic<?>> list = p_329869_.m_318975_("AttributeModifiers").asList(ItemStackComponentizationFix::m_320325_);
        boolean flag = (p_330062_ & 2) != 0;
        if (!list.isEmpty() || flag) {
            Dynamic<?> dynamic = p_332943_.emptyMap().set("modifiers", p_332943_.createList(list.stream()));
            if (flag) {
                dynamic = dynamic.set("show_in_tooltip", p_332943_.createBoolean(false));
            }

            p_329869_.m_320760_("minecraft:attribute_modifiers", dynamic);
        }
    }

    private static Dynamic<?> m_320325_(Dynamic<?> p_330280_) {
        Dynamic<?> dynamic = p_330280_.emptyMap()
            .set("name", p_330280_.createString(""))
            .set("amount", p_330280_.createDouble(0.0))
            .set("operation", p_330280_.createString("add_value"));
        dynamic = Dynamic.copyField(p_330280_, "AttributeName", dynamic, "type");
        dynamic = Dynamic.copyField(p_330280_, "Slot", dynamic, "slot");
        dynamic = Dynamic.copyField(p_330280_, "UUID", dynamic, "uuid");
        dynamic = Dynamic.copyField(p_330280_, "Name", dynamic, "name");
        dynamic = Dynamic.copyField(p_330280_, "Amount", dynamic, "amount");
        return Dynamic.copyAndFixField(p_330280_, "Operation", dynamic, "operation", p_334772_ -> {
            return p_334772_.createString(switch (p_334772_.asInt(0)) {
                case 1 -> "add_multiplied_base";
                case 2 -> "add_multiplied_total";
                default -> "add_value";
            });
        });
    }

    private static Pair<Dynamic<?>, Dynamic<?>> m_321104_(Dynamic<?> p_329859_) {
        Dynamic<?> dynamic = DataFixUtils.orElseGet(p_329859_.get("id").result(), () -> p_329859_.createString(""));
        Dynamic<?> dynamic1 = p_329859_.emptyMap()
            .set("type", p_329859_.createString(m_322192_(p_329859_.get("type").asInt(0))))
            .set("x", p_329859_.createDouble(p_329859_.get("x").asDouble(0.0)))
            .set("z", p_329859_.createDouble(p_329859_.get("z").asDouble(0.0)))
            .set("rotation", p_329859_.createFloat((float)p_329859_.get("rot").asDouble(0.0)));
        return Pair.of(dynamic, dynamic1);
    }

    private static String m_322192_(int p_328497_) {
        return switch (p_328497_) {
            case 1 -> "frame";
            case 2 -> "red_marker";
            case 3 -> "blue_marker";
            case 4 -> "target_x";
            case 5 -> "target_point";
            case 6 -> "player_off_map";
            case 7 -> "player_off_limits";
            case 8 -> "mansion";
            case 9 -> "monument";
            case 10 -> "banner_white";
            case 11 -> "banner_orange";
            case 12 -> "banner_magenta";
            case 13 -> "banner_light_blue";
            case 14 -> "banner_yellow";
            case 15 -> "banner_lime";
            case 16 -> "banner_pink";
            case 17 -> "banner_gray";
            case 18 -> "banner_light_gray";
            case 19 -> "banner_cyan";
            case 20 -> "banner_purple";
            case 21 -> "banner_blue";
            case 22 -> "banner_brown";
            case 23 -> "banner_green";
            case 24 -> "banner_red";
            case 25 -> "banner_black";
            case 26 -> "red_x";
            case 27 -> "village_desert";
            case 28 -> "village_plains";
            case 29 -> "village_savanna";
            case 30 -> "village_snowy";
            case 31 -> "village_taiga";
            case 32 -> "jungle_temple";
            case 33 -> "swamp_hut";
            default -> "player";
        };
    }

    private static void m_318587_(ItemStackComponentizationFix.ItemStackData p_329173_, Dynamic<?> p_331866_) {
        Dynamic<?> dynamic = p_331866_.emptyMap();
        Optional<String> optional = p_329173_.m_318975_("Potion").asString().result().filter(p_334871_ -> !p_334871_.equals("minecraft:empty"));
        if (optional.isPresent()) {
            dynamic = dynamic.set("potion", p_331866_.createString(optional.get()));
        }

        dynamic = p_329173_.m_320656_("CustomPotionColor", dynamic, "custom_color");
        dynamic = p_329173_.m_320656_("custom_potion_effects", dynamic, "custom_effects");
        if (!dynamic.equals(p_331866_.emptyMap())) {
            p_329173_.m_320760_("minecraft:potion_contents", dynamic);
        }
    }

    private static void m_322954_(ItemStackComponentizationFix.ItemStackData p_332414_, Dynamic<?> p_329764_) {
        Dynamic<?> dynamic = m_324158_(p_332414_, p_329764_);
        if (dynamic != null) {
            p_332414_.m_320760_("minecraft:writable_book_content", p_329764_.emptyMap().set("pages", dynamic));
        }
    }

    private static void m_321607_(ItemStackComponentizationFix.ItemStackData p_333609_, Dynamic<?> p_330312_) {
        Dynamic<?> dynamic = m_324158_(p_333609_, p_330312_);
        String s = p_333609_.m_318975_("title").asString("");
        Optional<String> optional = p_333609_.m_318975_("filtered_title").asString().result();
        Dynamic<?> dynamic1 = p_330312_.emptyMap();
        dynamic1 = dynamic1.set("title", m_319761_(p_330312_, s, optional));
        dynamic1 = p_333609_.m_320656_("author", dynamic1, "author");
        dynamic1 = p_333609_.m_320656_("resolved", dynamic1, "resolved");
        dynamic1 = p_333609_.m_320656_("generation", dynamic1, "generation");
        if (dynamic != null) {
            dynamic1 = dynamic1.set("pages", dynamic);
        }

        p_333609_.m_320760_("minecraft:written_book_content", dynamic1);
    }

    @Nullable
    private static Dynamic<?> m_324158_(ItemStackComponentizationFix.ItemStackData p_335763_, Dynamic<?> p_328532_) {
        List<String> list = p_335763_.m_318975_("pages").asList(p_331615_ -> p_331615_.asString(""));
        Map<String, String> map = p_335763_.m_318975_("filtered_pages").asMap(p_335169_ -> p_335169_.asString("0"), p_329927_ -> p_329927_.asString(""));
        if (list.isEmpty()) {
            return null;
        } else {
            List<Dynamic<?>> list1 = new ArrayList<>(list.size());

            for (int i = 0; i < list.size(); i++) {
                String s = list.get(i);
                String s1 = map.get(String.valueOf(i));
                list1.add(m_319761_(p_328532_, s, Optional.ofNullable(s1)));
            }

            return p_328532_.createList(list1.stream());
        }
    }

    private static Dynamic<?> m_319761_(Dynamic<?> p_330363_, String p_328510_, Optional<String> p_328222_) {
        Dynamic<?> dynamic = p_330363_.emptyMap().set("raw", p_330363_.createString(p_328510_));
        if (p_328222_.isPresent()) {
            dynamic = dynamic.set("filtered", p_330363_.createString(p_328222_.get()));
        }

        return dynamic;
    }

    private static void m_319058_(ItemStackComponentizationFix.ItemStackData p_328428_, Dynamic<?> p_327719_) {
        Dynamic<?> dynamic = p_327719_.emptyMap();

        for (String s : f_314964_) {
            dynamic = p_328428_.m_320656_(s, dynamic, s);
        }

        if (!dynamic.equals(p_327719_.emptyMap())) {
            p_328428_.m_320760_("minecraft:bucket_entity_data", dynamic);
        }
    }

    private static void m_320827_(ItemStackComponentizationFix.ItemStackData p_330486_, Dynamic<?> p_329466_) {
        Optional<? extends Dynamic<?>> optional = p_330486_.m_318975_("LodestonePos").result();
        Optional<? extends Dynamic<?>> optional1 = p_330486_.m_318975_("LodestoneDimension").result();
        if (!optional.isEmpty() || !optional1.isEmpty()) {
            boolean flag = p_330486_.m_318975_("LodestoneTracked").asBoolean(true);
            Dynamic<?> dynamic = p_329466_.emptyMap();
            if (optional.isPresent() && optional1.isPresent()) {
                dynamic = dynamic.set("target", p_329466_.emptyMap().set("pos", (Dynamic<?>)optional.get()).set("dimension", (Dynamic<?>)optional1.get()));
            }

            if (!flag) {
                dynamic = dynamic.set("tracked", p_329466_.createBoolean(false));
            }

            p_330486_.m_320760_("minecraft:lodestone_tracker", dynamic);
        }
    }

    private static void m_322865_(ItemStackComponentizationFix.ItemStackData p_334817_) {
        p_334817_.m_319995_("Explosion", true, p_334469_ -> {
            p_334817_.m_320760_("minecraft:firework_explosion", m_322691_(p_334469_));
            return p_334469_.remove("Type").remove("Colors").remove("FadeColors").remove("Trail").remove("Flicker");
        });
    }

    private static void m_321950_(ItemStackComponentizationFix.ItemStackData p_329939_) {
        p_329939_.m_319995_(
            "Fireworks",
            true,
            p_335612_ -> {
                Stream<? extends Dynamic<?>> stream = p_335612_.get("Explosions").asStream().map(ItemStackComponentizationFix::m_322691_);
                int i = p_335612_.get("Flight").asInt(0);
                p_329939_.m_320760_(
                    "minecraft:fireworks",
                    p_335612_.emptyMap().set("explosions", p_335612_.createList(stream)).set("flight_duration", p_335612_.createByte((byte)i))
                );
                return p_335612_.remove("Explosions").remove("Flight");
            }
        );
    }

    private static Dynamic<?> m_322691_(Dynamic<?> p_327955_) {
        p_327955_ = p_327955_.set("shape", p_327955_.createString(switch (p_327955_.get("Type").asInt(0)) {
            case 1 -> "large_ball";
            case 2 -> "star";
            case 3 -> "creeper";
            case 4 -> "burst";
            default -> "small_ball";
        })).remove("Type");
        p_327955_ = p_327955_.renameField("Colors", "colors");
        p_327955_ = p_327955_.renameField("FadeColors", "fade_colors");
        p_327955_ = p_327955_.renameField("Trail", "has_trail");
        return p_327955_.renameField("Flicker", "has_twinkle");
    }

    public static Dynamic<?> m_322595_(Dynamic<?> p_331244_) {
        Optional<String> optional = p_331244_.asString().result();
        if (optional.isPresent()) {
            return m_318903_(optional.get()) ? p_331244_.emptyMap().set("name", p_331244_.createString(optional.get())) : p_331244_.emptyMap();
        } else {
            String s = p_331244_.get("Name").asString("");
            Optional<? extends Dynamic<?>> optional1 = p_331244_.get("Id").result();
            Dynamic<?> dynamic = m_320461_(p_331244_.get("Properties"));
            Dynamic<?> dynamic1 = p_331244_.emptyMap();
            if (m_318903_(s)) {
                dynamic1 = dynamic1.set("name", p_331244_.createString(s));
            }

            if (optional1.isPresent()) {
                dynamic1 = dynamic1.set("id", (Dynamic<?>)optional1.get());
            }

            if (dynamic != null) {
                dynamic1 = dynamic1.set("properties", dynamic);
            }

            return dynamic1;
        }
    }

    private static boolean m_318903_(String p_332205_) {
        return p_332205_.length() > 16 ? false : p_332205_.chars().filter(p_331992_ -> p_331992_ <= 32 || p_331992_ >= 127).findAny().isEmpty();
    }

    @Nullable
    private static Dynamic<?> m_320461_(OptionalDynamic<?> p_329629_) {
        Map<String, List<Pair<String, Optional<String>>>> map = p_329629_.asMap(
            p_330311_ -> p_330311_.asString(""), p_331808_ -> p_331808_.asList(p_329294_ -> {
                    String s = p_329294_.get("Value").asString("");
                    Optional<String> optional = p_329294_.get("Signature").asString().result();
                    return Pair.of(s, optional);
                })
        );
        return map.isEmpty()
            ? null
            : p_329629_.createList(
                map.entrySet()
                    .stream()
                    .flatMap(
                        p_334576_ -> p_334576_.getValue()
                                .stream()
                                .map(
                                    p_335577_ -> {
                                        Dynamic<?> dynamic = p_329629_.emptyMap()
                                            .set("name", p_329629_.createString(p_334576_.getKey()))
                                            .set("value", p_329629_.createString(p_335577_.getFirst()));
                                        Optional<String> optional = p_335577_.getSecond();
                                        return optional.isPresent() ? dynamic.set("signature", p_329629_.createString(optional.get())) : dynamic;
                                    }
                                )
                    )
            );
    }

    @Override
    protected TypeRewriteRule makeRule() {
        return this.writeFixAndRead(
            "ItemStack componentization",
            this.getInputSchema().getType(References.ITEM_STACK),
            this.getOutputSchema().getType(References.ITEM_STACK),
            p_332668_ -> {
                Optional<? extends Dynamic<?>> optional = ItemStackComponentizationFix.ItemStackData.m_321368_(p_332668_).map(p_329220_ -> {
                    m_319773_(p_329220_, p_329220_.f_315045_);
                    return p_329220_.m_321852_();
                });
                return DataFixUtils.orElse(optional, p_332668_);
            }
        );
    }

    static class ItemStackData {
        private final String f_316466_;
        private final int f_315654_;
        private Dynamic<?> f_316709_;
        private final Dynamic<?> f_314897_;
        Dynamic<?> f_315045_;

        private ItemStackData(String p_330519_, int p_328981_, Dynamic<?> p_329116_) {
            this.f_316466_ = NamespacedSchema.ensureNamespaced(p_330519_);
            this.f_315654_ = p_328981_;
            this.f_316709_ = p_329116_.emptyMap();
            this.f_315045_ = p_329116_.get("tag").orElseEmptyMap();
            this.f_314897_ = p_329116_.remove("tag");
        }

        public static Optional<ItemStackComponentizationFix.ItemStackData> m_321368_(Dynamic<?> p_327873_) {
            return p_327873_.get("id")
                .asString()
                .apply2stable(
                    (p_336038_, p_334919_) -> new ItemStackComponentizationFix.ItemStackData(
                            p_336038_, p_334919_.intValue(), p_327873_.remove("id").remove("Count")
                        ),
                    p_327873_.get("Count").asNumber()
                )
                .result();
        }

        public OptionalDynamic<?> m_318975_(String p_327670_) {
            OptionalDynamic<?> optionaldynamic = this.f_315045_.get(p_327670_);
            this.f_315045_ = this.f_315045_.remove(p_327670_);
            return optionaldynamic;
        }

        public void m_320760_(String p_334273_, Dynamic<?> p_334484_) {
            this.f_316709_ = this.f_316709_.set(p_334273_, p_334484_);
        }

        public void m_322553_(String p_333518_, OptionalDynamic<?> p_335463_) {
            p_335463_.result().ifPresent(p_334065_ -> this.f_316709_ = this.f_316709_.set(p_333518_, (Dynamic<?>)p_334065_));
        }

        public Dynamic<?> m_320656_(String p_333380_, Dynamic<?> p_332781_, String p_332207_) {
            Optional<? extends Dynamic<?>> optional = this.m_318975_(p_333380_).result();
            return optional.isPresent() ? p_332781_.set(p_332207_, (Dynamic<?>)optional.get()) : p_332781_;
        }

        public void m_322646_(String p_330725_, String p_334538_, Dynamic<?> p_328097_) {
            Optional<? extends Dynamic<?>> optional = this.m_318975_(p_330725_).result();
            if (optional.isPresent() && !optional.get().equals(p_328097_)) {
                this.m_320760_(p_334538_, (Dynamic<?>)optional.get());
            }
        }

        public void m_321392_(String p_334513_, String p_335629_) {
            this.m_318975_(p_334513_).result().ifPresent(p_331999_ -> this.m_320760_(p_335629_, (Dynamic<?>)p_331999_));
        }

        public void m_319995_(String p_334224_, boolean p_331760_, UnaryOperator<Dynamic<?>> p_335156_) {
            OptionalDynamic<?> optionaldynamic = this.f_315045_.get(p_334224_);
            if (!p_331760_ || !optionaldynamic.result().isEmpty()) {
                Dynamic<?> dynamic = optionaldynamic.orElseEmptyMap();
                dynamic = p_335156_.apply(dynamic);
                if (dynamic.equals(dynamic.emptyMap())) {
                    this.f_315045_ = this.f_315045_.remove(p_334224_);
                } else {
                    this.f_315045_ = this.f_315045_.set(p_334224_, dynamic);
                }
            }
        }

        public Dynamic<?> m_321852_() {
            Dynamic<?> dynamic = this.f_315045_
                .emptyMap()
                .set("id", this.f_315045_.createString(this.f_316466_))
                .set("count", this.f_315045_.createInt(this.f_315654_));
            if (!this.f_315045_.equals(this.f_315045_.emptyMap())) {
                this.f_316709_ = this.f_316709_.set("minecraft:custom_data", this.f_315045_);
            }

            if (!this.f_316709_.equals(this.f_315045_.emptyMap())) {
                dynamic = dynamic.set("components", this.f_316709_);
            }

            return m_319171_(dynamic, this.f_314897_);
        }

        private static <T> Dynamic<T> m_319171_(Dynamic<T> p_331283_, Dynamic<?> p_335645_) {
            DynamicOps<T> dynamicops = p_331283_.getOps();
            return dynamicops.getMap(p_331283_.getValue())
                .flatMap(p_335224_ -> dynamicops.mergeToMap(p_335645_.convert(dynamicops).getValue(), (MapLike<T>)p_335224_))
                .map(p_334956_ -> new Dynamic<>(dynamicops, (T)p_334956_))
                .result()
                .orElse(p_331283_);
        }

        public boolean m_322886_(String p_328447_) {
            return this.f_316466_.equals(p_328447_);
        }

        public boolean m_319518_(Set<String> p_332697_) {
            return p_332697_.contains(this.f_316466_);
        }

        public boolean m_323634_(String p_329543_) {
            return this.f_316709_.get(p_329543_).result().isPresent();
        }
    }
}