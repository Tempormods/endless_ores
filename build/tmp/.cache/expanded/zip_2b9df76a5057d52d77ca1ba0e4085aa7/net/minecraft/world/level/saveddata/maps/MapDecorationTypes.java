package net.minecraft.world.level.saveddata.maps;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.MapColor;

public class MapDecorationTypes {
    private static final int f_316294_ = 12741452;
    public static final Holder<MapDecorationType> f_315151_ = m_324915_("player", "player", false, true);
    public static final Holder<MapDecorationType> f_314957_ = m_324915_("frame", "frame", true, true);
    public static final Holder<MapDecorationType> f_314314_ = m_324915_("red_marker", "red_marker", false, true);
    public static final Holder<MapDecorationType> f_313983_ = m_324915_("blue_marker", "blue_marker", false, true);
    public static final Holder<MapDecorationType> f_315740_ = m_324915_("target_x", "target_x", true, false);
    public static final Holder<MapDecorationType> f_314505_ = m_324915_("target_point", "target_point", true, false);
    public static final Holder<MapDecorationType> f_314040_ = m_324915_("player_off_map", "player_off_map", false, true);
    public static final Holder<MapDecorationType> f_314334_ = m_324915_("player_off_limits", "player_off_limits", false, true);
    public static final Holder<MapDecorationType> f_314725_ = m_323626_("mansion", "woodland_mansion", true, 5393476, false, true);
    public static final Holder<MapDecorationType> f_315089_ = m_323626_("monument", "ocean_monument", true, 3830373, false, true);
    public static final Holder<MapDecorationType> f_315046_ = m_324915_("banner_white", "white_banner", true, true);
    public static final Holder<MapDecorationType> f_316187_ = m_324915_("banner_orange", "orange_banner", true, true);
    public static final Holder<MapDecorationType> f_314327_ = m_324915_("banner_magenta", "magenta_banner", true, true);
    public static final Holder<MapDecorationType> f_314597_ = m_324915_("banner_light_blue", "light_blue_banner", true, true);
    public static final Holder<MapDecorationType> f_314679_ = m_324915_("banner_yellow", "yellow_banner", true, true);
    public static final Holder<MapDecorationType> f_314477_ = m_324915_("banner_lime", "lime_banner", true, true);
    public static final Holder<MapDecorationType> f_316207_ = m_324915_("banner_pink", "pink_banner", true, true);
    public static final Holder<MapDecorationType> f_315054_ = m_324915_("banner_gray", "gray_banner", true, true);
    public static final Holder<MapDecorationType> f_315781_ = m_324915_("banner_light_gray", "light_gray_banner", true, true);
    public static final Holder<MapDecorationType> f_315526_ = m_324915_("banner_cyan", "cyan_banner", true, true);
    public static final Holder<MapDecorationType> f_316950_ = m_324915_("banner_purple", "purple_banner", true, true);
    public static final Holder<MapDecorationType> f_315143_ = m_324915_("banner_blue", "blue_banner", true, true);
    public static final Holder<MapDecorationType> f_314021_ = m_324915_("banner_brown", "brown_banner", true, true);
    public static final Holder<MapDecorationType> f_315589_ = m_324915_("banner_green", "green_banner", true, true);
    public static final Holder<MapDecorationType> f_314287_ = m_324915_("banner_red", "red_banner", true, true);
    public static final Holder<MapDecorationType> f_316506_ = m_324915_("banner_black", "black_banner", true, true);
    public static final Holder<MapDecorationType> f_316825_ = m_324915_("red_x", "red_x", true, false);
    public static final Holder<MapDecorationType> f_316569_ = m_323626_("village_desert", "desert_village", true, MapColor.COLOR_LIGHT_GRAY.col, false, true);
    public static final Holder<MapDecorationType> f_316626_ = m_323626_("village_plains", "plains_village", true, MapColor.COLOR_LIGHT_GRAY.col, false, true);
    public static final Holder<MapDecorationType> f_316030_ = m_323626_("village_savanna", "savanna_village", true, MapColor.COLOR_LIGHT_GRAY.col, false, true);
    public static final Holder<MapDecorationType> f_316629_ = m_323626_("village_snowy", "snowy_village", true, MapColor.COLOR_LIGHT_GRAY.col, false, true);
    public static final Holder<MapDecorationType> f_314288_ = m_323626_("village_taiga", "taiga_village", true, MapColor.COLOR_LIGHT_GRAY.col, false, true);
    public static final Holder<MapDecorationType> f_315224_ = m_323626_("jungle_temple", "jungle_temple", true, MapColor.COLOR_LIGHT_GRAY.col, false, true);
    public static final Holder<MapDecorationType> f_314582_ = m_323626_("swamp_hut", "swamp_hut", true, MapColor.COLOR_LIGHT_GRAY.col, false, true);
    public static final Holder<MapDecorationType> f_314672_ = m_323626_("trial_chambers", "trial_chambers", true, 12741452, false, true);

    public static Holder<MapDecorationType> m_324454_(Registry<MapDecorationType> p_329539_) {
        return f_315151_;
    }

    private static Holder<MapDecorationType> m_324915_(String p_329494_, String p_335821_, boolean p_327749_, boolean p_330406_) {
        return m_323626_(p_329494_, p_335821_, p_327749_, -1, p_330406_, false);
    }

    private static Holder<MapDecorationType> m_323626_(
        String p_329296_, String p_330955_, boolean p_335378_, int p_330214_, boolean p_328908_, boolean p_332062_
    ) {
        ResourceKey<MapDecorationType> resourcekey = ResourceKey.create(Registries.f_313969_, new ResourceLocation(p_329296_));
        MapDecorationType mapdecorationtype = new MapDecorationType(new ResourceLocation(p_330955_), p_335378_, p_330214_, p_332062_, p_328908_);
        return Registry.registerForHolder(BuiltInRegistries.f_315353_, resourcekey, mapdecorationtype);
    }
}