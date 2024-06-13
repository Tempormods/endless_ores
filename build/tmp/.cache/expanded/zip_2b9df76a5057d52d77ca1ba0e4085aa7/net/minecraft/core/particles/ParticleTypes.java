package net.minecraft.core.particles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class ParticleTypes {
    public static final SimpleParticleType ANGRY_VILLAGER = register("angry_villager", false);
    public static final ParticleType<BlockParticleOption> BLOCK = register("block", false, BlockParticleOption::codec, BlockParticleOption::m_320731_);
    public static final ParticleType<BlockParticleOption> BLOCK_MARKER = register(
        "block_marker", true, BlockParticleOption::codec, BlockParticleOption::m_320731_
    );
    public static final SimpleParticleType BUBBLE = register("bubble", false);
    public static final SimpleParticleType CLOUD = register("cloud", false);
    public static final SimpleParticleType CRIT = register("crit", false);
    public static final SimpleParticleType DAMAGE_INDICATOR = register("damage_indicator", true);
    public static final SimpleParticleType DRAGON_BREATH = register("dragon_breath", false);
    public static final SimpleParticleType DRIPPING_LAVA = register("dripping_lava", false);
    public static final SimpleParticleType FALLING_LAVA = register("falling_lava", false);
    public static final SimpleParticleType LANDING_LAVA = register("landing_lava", false);
    public static final SimpleParticleType DRIPPING_WATER = register("dripping_water", false);
    public static final SimpleParticleType FALLING_WATER = register("falling_water", false);
    public static final ParticleType<DustParticleOptions> DUST = register(
        "dust", false, p_325812_ -> DustParticleOptions.CODEC, p_325805_ -> DustParticleOptions.f_315304_
    );
    public static final ParticleType<DustColorTransitionOptions> DUST_COLOR_TRANSITION = register(
        "dust_color_transition", false, p_325809_ -> DustColorTransitionOptions.CODEC, p_325803_ -> DustColorTransitionOptions.f_316000_
    );
    public static final SimpleParticleType EFFECT = register("effect", false);
    public static final SimpleParticleType ELDER_GUARDIAN = register("elder_guardian", true);
    public static final SimpleParticleType ENCHANTED_HIT = register("enchanted_hit", false);
    public static final SimpleParticleType ENCHANT = register("enchant", false);
    public static final SimpleParticleType END_ROD = register("end_rod", false);
    public static final ParticleType<ColorParticleOption> ENTITY_EFFECT = register(
        "entity_effect", false, ColorParticleOption::m_321856_, ColorParticleOption::m_323084_
    );
    public static final SimpleParticleType EXPLOSION_EMITTER = register("explosion_emitter", true);
    public static final SimpleParticleType EXPLOSION = register("explosion", true);
    public static final SimpleParticleType f_302334_ = register("gust", true);
    public static final SimpleParticleType f_314220_ = register("small_gust", false);
    public static final SimpleParticleType f_316181_ = register("gust_emitter_large", true);
    public static final SimpleParticleType f_315099_ = register("gust_emitter_small", true);
    public static final SimpleParticleType SONIC_BOOM = register("sonic_boom", true);
    public static final ParticleType<BlockParticleOption> FALLING_DUST = register(
        "falling_dust", false, BlockParticleOption::codec, BlockParticleOption::m_320731_
    );
    public static final SimpleParticleType FIREWORK = register("firework", false);
    public static final SimpleParticleType FISHING = register("fishing", false);
    public static final SimpleParticleType FLAME = register("flame", false);
    public static final SimpleParticleType f_314307_ = register("infested", false);
    public static final SimpleParticleType CHERRY_LEAVES = register("cherry_leaves", false);
    public static final SimpleParticleType SCULK_SOUL = register("sculk_soul", false);
    public static final ParticleType<SculkChargeParticleOptions> SCULK_CHARGE = register(
        "sculk_charge", true, p_325808_ -> SculkChargeParticleOptions.CODEC, p_325807_ -> SculkChargeParticleOptions.f_314611_
    );
    public static final SimpleParticleType SCULK_CHARGE_POP = register("sculk_charge_pop", true);
    public static final SimpleParticleType SOUL_FIRE_FLAME = register("soul_fire_flame", false);
    public static final SimpleParticleType SOUL = register("soul", false);
    public static final SimpleParticleType FLASH = register("flash", false);
    public static final SimpleParticleType HAPPY_VILLAGER = register("happy_villager", false);
    public static final SimpleParticleType COMPOSTER = register("composter", false);
    public static final SimpleParticleType HEART = register("heart", false);
    public static final SimpleParticleType INSTANT_EFFECT = register("instant_effect", false);
    public static final ParticleType<ItemParticleOption> ITEM = register("item", false, ItemParticleOption::codec, ItemParticleOption::m_322965_);
    public static final ParticleType<VibrationParticleOption> VIBRATION = register(
        "vibration", true, p_325806_ -> VibrationParticleOption.CODEC, p_325810_ -> VibrationParticleOption.f_315797_
    );
    public static final SimpleParticleType ITEM_SLIME = register("item_slime", false);
    public static final SimpleParticleType f_315496_ = register("item_cobweb", false);
    public static final SimpleParticleType ITEM_SNOWBALL = register("item_snowball", false);
    public static final SimpleParticleType LARGE_SMOKE = register("large_smoke", false);
    public static final SimpleParticleType LAVA = register("lava", false);
    public static final SimpleParticleType MYCELIUM = register("mycelium", false);
    public static final SimpleParticleType NOTE = register("note", false);
    public static final SimpleParticleType POOF = register("poof", true);
    public static final SimpleParticleType PORTAL = register("portal", false);
    public static final SimpleParticleType RAIN = register("rain", false);
    public static final SimpleParticleType SMOKE = register("smoke", false);
    public static final SimpleParticleType f_302345_ = register("white_smoke", false);
    public static final SimpleParticleType SNEEZE = register("sneeze", false);
    public static final SimpleParticleType SPIT = register("spit", true);
    public static final SimpleParticleType SQUID_INK = register("squid_ink", true);
    public static final SimpleParticleType SWEEP_ATTACK = register("sweep_attack", true);
    public static final SimpleParticleType TOTEM_OF_UNDYING = register("totem_of_undying", false);
    public static final SimpleParticleType UNDERWATER = register("underwater", false);
    public static final SimpleParticleType SPLASH = register("splash", false);
    public static final SimpleParticleType WITCH = register("witch", false);
    public static final SimpleParticleType BUBBLE_POP = register("bubble_pop", false);
    public static final SimpleParticleType CURRENT_DOWN = register("current_down", false);
    public static final SimpleParticleType BUBBLE_COLUMN_UP = register("bubble_column_up", false);
    public static final SimpleParticleType NAUTILUS = register("nautilus", false);
    public static final SimpleParticleType DOLPHIN = register("dolphin", false);
    public static final SimpleParticleType CAMPFIRE_COSY_SMOKE = register("campfire_cosy_smoke", true);
    public static final SimpleParticleType CAMPFIRE_SIGNAL_SMOKE = register("campfire_signal_smoke", true);
    public static final SimpleParticleType DRIPPING_HONEY = register("dripping_honey", false);
    public static final SimpleParticleType FALLING_HONEY = register("falling_honey", false);
    public static final SimpleParticleType LANDING_HONEY = register("landing_honey", false);
    public static final SimpleParticleType FALLING_NECTAR = register("falling_nectar", false);
    public static final SimpleParticleType FALLING_SPORE_BLOSSOM = register("falling_spore_blossom", false);
    public static final SimpleParticleType ASH = register("ash", false);
    public static final SimpleParticleType CRIMSON_SPORE = register("crimson_spore", false);
    public static final SimpleParticleType WARPED_SPORE = register("warped_spore", false);
    public static final SimpleParticleType SPORE_BLOSSOM_AIR = register("spore_blossom_air", false);
    public static final SimpleParticleType DRIPPING_OBSIDIAN_TEAR = register("dripping_obsidian_tear", false);
    public static final SimpleParticleType FALLING_OBSIDIAN_TEAR = register("falling_obsidian_tear", false);
    public static final SimpleParticleType LANDING_OBSIDIAN_TEAR = register("landing_obsidian_tear", false);
    public static final SimpleParticleType REVERSE_PORTAL = register("reverse_portal", false);
    public static final SimpleParticleType WHITE_ASH = register("white_ash", false);
    public static final SimpleParticleType SMALL_FLAME = register("small_flame", false);
    public static final SimpleParticleType SNOWFLAKE = register("snowflake", false);
    public static final SimpleParticleType DRIPPING_DRIPSTONE_LAVA = register("dripping_dripstone_lava", false);
    public static final SimpleParticleType FALLING_DRIPSTONE_LAVA = register("falling_dripstone_lava", false);
    public static final SimpleParticleType DRIPPING_DRIPSTONE_WATER = register("dripping_dripstone_water", false);
    public static final SimpleParticleType FALLING_DRIPSTONE_WATER = register("falling_dripstone_water", false);
    public static final SimpleParticleType GLOW_SQUID_INK = register("glow_squid_ink", true);
    public static final SimpleParticleType GLOW = register("glow", true);
    public static final SimpleParticleType WAX_ON = register("wax_on", true);
    public static final SimpleParticleType WAX_OFF = register("wax_off", true);
    public static final SimpleParticleType ELECTRIC_SPARK = register("electric_spark", true);
    public static final SimpleParticleType SCRAPE = register("scrape", true);
    public static final ParticleType<ShriekParticleOption> SHRIEK = register(
        "shriek", false, p_325811_ -> ShriekParticleOption.CODEC, p_325804_ -> ShriekParticleOption.f_314366_
    );
    public static final SimpleParticleType EGG_CRACK = register("egg_crack", false);
    public static final SimpleParticleType f_303068_ = register("dust_plume", false);
    public static final SimpleParticleType f_314928_ = register("trial_spawner_detection", true);
    public static final SimpleParticleType f_314692_ = register("trial_spawner_detection_ominous", true);
    public static final SimpleParticleType f_314380_ = register("vault_connection", true);
    public static final ParticleType<BlockParticleOption> f_314186_ = register(
        "dust_pillar", true, BlockParticleOption::codec, BlockParticleOption::m_320731_
    );
    public static final SimpleParticleType f_314395_ = register("ominous_spawning", true);
    public static final SimpleParticleType f_314966_ = register("raid_omen", false);
    public static final SimpleParticleType f_317125_ = register("trial_omen", false);
    public static final Codec<ParticleOptions> CODEC = BuiltInRegistries.PARTICLE_TYPE
        .byNameCodec()
        .dispatch("type", ParticleOptions::getType, ParticleType::codec);
    public static final StreamCodec<RegistryFriendlyByteBuf, ParticleOptions> f_314250_ = ByteBufCodecs.m_320159_(Registries.PARTICLE_TYPE)
        .m_321818_(ParticleOptions::getType, ParticleType::m_319843_);

    private static SimpleParticleType register(String pKey, boolean pOverrideLimiter) {
        return Registry.register(BuiltInRegistries.PARTICLE_TYPE, pKey, new SimpleParticleType(pOverrideLimiter));
    }

    private static <T extends ParticleOptions> ParticleType<T> register(
        String pKey,
        boolean pOverrideLimiter,
        final Function<ParticleType<T>, MapCodec<T>> pCodecFactory,
        final Function<ParticleType<T>, StreamCodec<? super RegistryFriendlyByteBuf, T>> p_333331_
    ) {
        return Registry.register(BuiltInRegistries.PARTICLE_TYPE, pKey, new ParticleType<T>(pOverrideLimiter) {
            @Override
            public MapCodec<T> codec() {
                return pCodecFactory.apply(this);
            }

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, T> m_319843_() {
                return p_333331_.apply(this);
            }
        });
    }
}