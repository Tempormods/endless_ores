package net.minecraft.world.level.block.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class BannerPatterns {
    public static final ResourceKey<BannerPattern> BASE = create("base");
    public static final ResourceKey<BannerPattern> SQUARE_BOTTOM_LEFT = create("square_bottom_left");
    public static final ResourceKey<BannerPattern> SQUARE_BOTTOM_RIGHT = create("square_bottom_right");
    public static final ResourceKey<BannerPattern> SQUARE_TOP_LEFT = create("square_top_left");
    public static final ResourceKey<BannerPattern> SQUARE_TOP_RIGHT = create("square_top_right");
    public static final ResourceKey<BannerPattern> STRIPE_BOTTOM = create("stripe_bottom");
    public static final ResourceKey<BannerPattern> STRIPE_TOP = create("stripe_top");
    public static final ResourceKey<BannerPattern> STRIPE_LEFT = create("stripe_left");
    public static final ResourceKey<BannerPattern> STRIPE_RIGHT = create("stripe_right");
    public static final ResourceKey<BannerPattern> STRIPE_CENTER = create("stripe_center");
    public static final ResourceKey<BannerPattern> STRIPE_MIDDLE = create("stripe_middle");
    public static final ResourceKey<BannerPattern> STRIPE_DOWNRIGHT = create("stripe_downright");
    public static final ResourceKey<BannerPattern> STRIPE_DOWNLEFT = create("stripe_downleft");
    public static final ResourceKey<BannerPattern> STRIPE_SMALL = create("small_stripes");
    public static final ResourceKey<BannerPattern> CROSS = create("cross");
    public static final ResourceKey<BannerPattern> STRAIGHT_CROSS = create("straight_cross");
    public static final ResourceKey<BannerPattern> TRIANGLE_BOTTOM = create("triangle_bottom");
    public static final ResourceKey<BannerPattern> TRIANGLE_TOP = create("triangle_top");
    public static final ResourceKey<BannerPattern> TRIANGLES_BOTTOM = create("triangles_bottom");
    public static final ResourceKey<BannerPattern> TRIANGLES_TOP = create("triangles_top");
    public static final ResourceKey<BannerPattern> DIAGONAL_LEFT = create("diagonal_left");
    public static final ResourceKey<BannerPattern> DIAGONAL_RIGHT = create("diagonal_up_right");
    public static final ResourceKey<BannerPattern> DIAGONAL_LEFT_MIRROR = create("diagonal_up_left");
    public static final ResourceKey<BannerPattern> DIAGONAL_RIGHT_MIRROR = create("diagonal_right");
    public static final ResourceKey<BannerPattern> CIRCLE_MIDDLE = create("circle");
    public static final ResourceKey<BannerPattern> RHOMBUS_MIDDLE = create("rhombus");
    public static final ResourceKey<BannerPattern> HALF_VERTICAL = create("half_vertical");
    public static final ResourceKey<BannerPattern> HALF_HORIZONTAL = create("half_horizontal");
    public static final ResourceKey<BannerPattern> HALF_VERTICAL_MIRROR = create("half_vertical_right");
    public static final ResourceKey<BannerPattern> HALF_HORIZONTAL_MIRROR = create("half_horizontal_bottom");
    public static final ResourceKey<BannerPattern> BORDER = create("border");
    public static final ResourceKey<BannerPattern> CURLY_BORDER = create("curly_border");
    public static final ResourceKey<BannerPattern> GRADIENT = create("gradient");
    public static final ResourceKey<BannerPattern> GRADIENT_UP = create("gradient_up");
    public static final ResourceKey<BannerPattern> BRICKS = create("bricks");
    public static final ResourceKey<BannerPattern> GLOBE = create("globe");
    public static final ResourceKey<BannerPattern> CREEPER = create("creeper");
    public static final ResourceKey<BannerPattern> SKULL = create("skull");
    public static final ResourceKey<BannerPattern> FLOWER = create("flower");
    public static final ResourceKey<BannerPattern> MOJANG = create("mojang");
    public static final ResourceKey<BannerPattern> PIGLIN = create("piglin");
    public static final ResourceKey<BannerPattern> f_314319_ = create("flow");
    public static final ResourceKey<BannerPattern> f_315913_ = create("guster");

    private static ResourceKey<BannerPattern> create(String pName) {
        return ResourceKey.create(Registries.BANNER_PATTERN, new ResourceLocation(pName));
    }

    public static void bootstrap(BootstrapContext<BannerPattern> p_335175_) {
        m_318872_(p_335175_, BASE);
        m_318872_(p_335175_, SQUARE_BOTTOM_LEFT);
        m_318872_(p_335175_, SQUARE_BOTTOM_RIGHT);
        m_318872_(p_335175_, SQUARE_TOP_LEFT);
        m_318872_(p_335175_, SQUARE_TOP_RIGHT);
        m_318872_(p_335175_, STRIPE_BOTTOM);
        m_318872_(p_335175_, STRIPE_TOP);
        m_318872_(p_335175_, STRIPE_LEFT);
        m_318872_(p_335175_, STRIPE_RIGHT);
        m_318872_(p_335175_, STRIPE_CENTER);
        m_318872_(p_335175_, STRIPE_MIDDLE);
        m_318872_(p_335175_, STRIPE_DOWNRIGHT);
        m_318872_(p_335175_, STRIPE_DOWNLEFT);
        m_318872_(p_335175_, STRIPE_SMALL);
        m_318872_(p_335175_, CROSS);
        m_318872_(p_335175_, STRAIGHT_CROSS);
        m_318872_(p_335175_, TRIANGLE_BOTTOM);
        m_318872_(p_335175_, TRIANGLE_TOP);
        m_318872_(p_335175_, TRIANGLES_BOTTOM);
        m_318872_(p_335175_, TRIANGLES_TOP);
        m_318872_(p_335175_, DIAGONAL_LEFT);
        m_318872_(p_335175_, DIAGONAL_RIGHT);
        m_318872_(p_335175_, DIAGONAL_LEFT_MIRROR);
        m_318872_(p_335175_, DIAGONAL_RIGHT_MIRROR);
        m_318872_(p_335175_, CIRCLE_MIDDLE);
        m_318872_(p_335175_, RHOMBUS_MIDDLE);
        m_318872_(p_335175_, HALF_VERTICAL);
        m_318872_(p_335175_, HALF_HORIZONTAL);
        m_318872_(p_335175_, HALF_VERTICAL_MIRROR);
        m_318872_(p_335175_, HALF_HORIZONTAL_MIRROR);
        m_318872_(p_335175_, BORDER);
        m_318872_(p_335175_, CURLY_BORDER);
        m_318872_(p_335175_, GRADIENT);
        m_318872_(p_335175_, GRADIENT_UP);
        m_318872_(p_335175_, BRICKS);
        m_318872_(p_335175_, GLOBE);
        m_318872_(p_335175_, CREEPER);
        m_318872_(p_335175_, SKULL);
        m_318872_(p_335175_, FLOWER);
        m_318872_(p_335175_, MOJANG);
        m_318872_(p_335175_, PIGLIN);
    }

    public static void m_318872_(BootstrapContext<BannerPattern> p_330964_, ResourceKey<BannerPattern> p_329824_) {
        p_330964_.m_321889_(p_329824_, new BannerPattern(p_329824_.location(), "block.minecraft.banner." + p_329824_.location().toShortLanguageKey()));
    }
}