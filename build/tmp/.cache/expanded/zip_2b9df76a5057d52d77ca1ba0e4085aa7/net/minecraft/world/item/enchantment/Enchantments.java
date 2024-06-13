package net.minecraft.world.item.enchantment;

import java.util.Optional;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;

public class Enchantments {
    private static final EquipmentSlot[] ARMOR_SLOTS = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    public static final Enchantment f_314710_ = register(
        "protection",
        new ProtectionEnchantment(
            Enchantment.m_324539_(ItemTags.f_317078_, 10, 4, Enchantment.m_318803_(1, 11), Enchantment.m_318803_(12, 11), 1, ARMOR_SLOTS),
            ProtectionEnchantment.Type.ALL
        )
    );
    public static final Enchantment FIRE_PROTECTION = register(
        "fire_protection",
        new ProtectionEnchantment(
            Enchantment.m_324539_(ItemTags.f_317078_, 5, 4, Enchantment.m_318803_(10, 8), Enchantment.m_318803_(18, 8), 2, ARMOR_SLOTS),
            ProtectionEnchantment.Type.FIRE
        )
    );
    public static final Enchantment f_315602_ = register(
        "feather_falling",
        new ProtectionEnchantment(
            Enchantment.m_324539_(ItemTags.f_314991_, 5, 4, Enchantment.m_318803_(5, 6), Enchantment.m_318803_(11, 6), 2, ARMOR_SLOTS),
            ProtectionEnchantment.Type.FALL
        )
    );
    public static final Enchantment BLAST_PROTECTION = register(
        "blast_protection",
        new ProtectionEnchantment(
            Enchantment.m_324539_(ItemTags.f_317078_, 2, 4, Enchantment.m_318803_(5, 8), Enchantment.m_318803_(13, 8), 4, ARMOR_SLOTS),
            ProtectionEnchantment.Type.EXPLOSION
        )
    );
    public static final Enchantment PROJECTILE_PROTECTION = register(
        "projectile_protection",
        new ProtectionEnchantment(
            Enchantment.m_324539_(ItemTags.f_317078_, 5, 4, Enchantment.m_318803_(3, 6), Enchantment.m_318803_(9, 6), 2, ARMOR_SLOTS),
            ProtectionEnchantment.Type.PROJECTILE
        )
    );
    public static final Enchantment RESPIRATION = register(
        "respiration",
        new Enchantment(Enchantment.m_324539_(ItemTags.f_316014_, 2, 3, Enchantment.m_318803_(10, 10), Enchantment.m_318803_(40, 10), 4, ARMOR_SLOTS))
    );
    public static final Enchantment AQUA_AFFINITY = register(
        "aqua_affinity", new Enchantment(Enchantment.m_324539_(ItemTags.f_316014_, 2, 1, Enchantment.m_322287_(1), Enchantment.m_322287_(41), 4, ARMOR_SLOTS))
    );
    public static final Enchantment THORNS = register(
        "thorns",
        new ThornsEnchantment(
            Enchantment.m_322764_(ItemTags.f_317078_, ItemTags.f_315053_, 1, 3, Enchantment.m_318803_(10, 20), Enchantment.m_318803_(60, 20), 8, ARMOR_SLOTS)
        )
    );
    public static final Enchantment DEPTH_STRIDER = register(
        "depth_strider",
        new WaterWalkerEnchantment(Enchantment.m_324539_(ItemTags.f_314991_, 2, 3, Enchantment.m_318803_(10, 10), Enchantment.m_318803_(25, 10), 4, ARMOR_SLOTS))
    );
    public static final Enchantment FROST_WALKER = register(
        "frost_walker",
        new FrostWalkerEnchantment(
            Enchantment.m_324539_(ItemTags.f_314991_, 2, 2, Enchantment.m_318803_(10, 10), Enchantment.m_318803_(25, 10), 4, EquipmentSlot.FEET)
        )
    );
    public static final Enchantment BINDING_CURSE = register(
        "binding_curse",
        new BindingCurseEnchantment(Enchantment.m_324539_(ItemTags.f_317097_, 1, 1, Enchantment.m_322287_(25), Enchantment.m_322287_(50), 8, ARMOR_SLOTS))
    );
    public static final Enchantment SOUL_SPEED = register(
        "soul_speed",
        new SoulSpeedEnchantment(
            Enchantment.m_324539_(ItemTags.f_314991_, 1, 3, Enchantment.m_318803_(10, 10), Enchantment.m_318803_(25, 10), 8, EquipmentSlot.FEET)
        )
    );
    public static final Enchantment SWIFT_SNEAK = register(
        "swift_sneak",
        new SwiftSneakEnchantment(
            Enchantment.m_324539_(ItemTags.f_314045_, 1, 3, Enchantment.m_318803_(25, 25), Enchantment.m_318803_(75, 25), 8, EquipmentSlot.LEGS)
        )
    );
    public static final Enchantment SHARPNESS = register(
        "sharpness",
        new DamageEnchantment(
            Enchantment.m_322764_(
                ItemTags.f_314502_, ItemTags.f_316261_, 10, 5, Enchantment.m_318803_(1, 11), Enchantment.m_318803_(21, 11), 1, EquipmentSlot.MAINHAND
            ),
            Optional.empty()
        )
    );
    public static final Enchantment SMITE = register(
        "smite",
        new DamageEnchantment(
            Enchantment.m_322764_(
                ItemTags.f_316107_, ItemTags.f_316261_, 5, 5, Enchantment.m_318803_(5, 8), Enchantment.m_318803_(25, 8), 2, EquipmentSlot.MAINHAND
            ),
            Optional.of(EntityTypeTags.f_314378_)
        )
    );
    public static final Enchantment BANE_OF_ARTHROPODS = register(
        "bane_of_arthropods",
        new DamageEnchantment(
            Enchantment.m_322764_(
                ItemTags.f_316107_, ItemTags.f_316261_, 5, 5, Enchantment.m_318803_(5, 8), Enchantment.m_318803_(25, 8), 2, EquipmentSlot.MAINHAND
            ),
            Optional.of(EntityTypeTags.f_314167_)
        )
    );
    public static final Enchantment KNOCKBACK = register(
        "knockback",
        new Enchantment(Enchantment.m_324539_(ItemTags.f_316261_, 5, 2, Enchantment.m_318803_(5, 20), Enchantment.m_318803_(55, 20), 2, EquipmentSlot.MAINHAND))
    );
    public static final Enchantment FIRE_ASPECT = register(
        "fire_aspect",
        new Enchantment(
            Enchantment.m_324539_(ItemTags.f_314461_, 2, 2, Enchantment.m_318803_(10, 20), Enchantment.m_318803_(60, 20), 4, EquipmentSlot.MAINHAND)
        )
    );
    public static final Enchantment f_316023_ = register(
        "looting",
        new LootBonusEnchantment(
            Enchantment.m_324539_(ItemTags.f_316261_, 2, 3, Enchantment.m_318803_(15, 9), Enchantment.m_318803_(65, 9), 4, EquipmentSlot.MAINHAND)
        )
    );
    public static final Enchantment SWEEPING_EDGE = register(
        "sweeping_edge",
        new Enchantment(Enchantment.m_324539_(ItemTags.f_316261_, 2, 3, Enchantment.m_318803_(5, 9), Enchantment.m_318803_(20, 9), 4, EquipmentSlot.MAINHAND))
    );
    public static final Enchantment f_316758_ = register(
        "efficiency",
        new Enchantment(
            Enchantment.m_324539_(ItemTags.f_314984_, 10, 5, Enchantment.m_318803_(1, 10), Enchantment.m_318803_(51, 10), 1, EquipmentSlot.MAINHAND)
        )
    );
    public static final Enchantment SILK_TOUCH = register(
        "silk_touch",
        new UntouchingEnchantment(
            Enchantment.m_324539_(ItemTags.f_314570_, 1, 1, Enchantment.m_322287_(15), Enchantment.m_322287_(65), 8, EquipmentSlot.MAINHAND)
        )
    );
    public static final Enchantment UNBREAKING = register(
        "unbreaking",
        new DigDurabilityEnchantment(
            Enchantment.m_324539_(ItemTags.f_314809_, 5, 3, Enchantment.m_318803_(5, 8), Enchantment.m_318803_(55, 8), 2, EquipmentSlot.MAINHAND)
        )
    );
    public static final Enchantment f_316753_ = register(
        "fortune",
        new LootBonusEnchantment(
            Enchantment.m_324539_(ItemTags.f_314570_, 2, 3, Enchantment.m_318803_(15, 9), Enchantment.m_318803_(65, 9), 4, EquipmentSlot.MAINHAND)
        )
    );
    public static final Enchantment f_314636_ = register(
        "power",
        new Enchantment(
            Enchantment.m_324539_(ItemTags.f_317054_, 10, 5, Enchantment.m_318803_(1, 10), Enchantment.m_318803_(16, 10), 1, EquipmentSlot.MAINHAND)
        )
    );
    public static final Enchantment f_316860_ = register(
        "punch",
        new Enchantment(
            Enchantment.m_324539_(ItemTags.f_317054_, 2, 2, Enchantment.m_318803_(12, 20), Enchantment.m_318803_(37, 20), 4, EquipmentSlot.MAINHAND)
        )
    );
    public static final Enchantment f_316779_ = register(
        "flame",
        new Enchantment(Enchantment.m_324539_(ItemTags.f_317054_, 2, 1, Enchantment.m_322287_(20), Enchantment.m_322287_(50), 4, EquipmentSlot.MAINHAND))
    );
    public static final Enchantment f_316098_ = register(
        "infinity",
        new ArrowInfiniteEnchantment(
            Enchantment.m_324539_(ItemTags.f_317054_, 1, 1, Enchantment.m_322287_(20), Enchantment.m_322287_(50), 8, EquipmentSlot.MAINHAND)
        )
    );
    public static final Enchantment f_314659_ = register(
        "luck_of_the_sea",
        new LootBonusEnchantment(
            Enchantment.m_324539_(ItemTags.f_313995_, 2, 3, Enchantment.m_318803_(15, 9), Enchantment.m_318803_(65, 9), 4, EquipmentSlot.MAINHAND)
        )
    );
    public static final Enchantment f_314874_ = register(
        "lure",
        new Enchantment(Enchantment.m_324539_(ItemTags.f_313995_, 2, 3, Enchantment.m_318803_(15, 9), Enchantment.m_318803_(65, 9), 4, EquipmentSlot.MAINHAND))
    );
    public static final Enchantment LOYALTY = register(
        "loyalty",
        new Enchantment(Enchantment.m_324539_(ItemTags.f_316827_, 5, 3, Enchantment.m_318803_(12, 7), Enchantment.m_322287_(50), 2, EquipmentSlot.MAINHAND))
    );
    public static final Enchantment IMPALING = register(
        "impaling",
        new DamageEnchantment(
            Enchantment.m_324539_(ItemTags.f_316827_, 2, 5, Enchantment.m_318803_(1, 8), Enchantment.m_318803_(21, 8), 4, EquipmentSlot.MAINHAND),
            Optional.of(EntityTypeTags.f_316359_)
        )
    );
    public static final Enchantment RIPTIDE = register(
        "riptide",
        new TridentRiptideEnchantment(
            Enchantment.m_324539_(ItemTags.f_316827_, 2, 3, Enchantment.m_318803_(17, 7), Enchantment.m_322287_(50), 4, EquipmentSlot.MAINHAND)
        )
    );
    public static final Enchantment CHANNELING = register(
        "channeling",
        new Enchantment(Enchantment.m_324539_(ItemTags.f_316827_, 1, 1, Enchantment.m_322287_(25), Enchantment.m_322287_(50), 8, EquipmentSlot.MAINHAND))
    );
    public static final Enchantment MULTISHOT = register(
        "multishot",
        new MultiShotEnchantment(
            Enchantment.m_324539_(ItemTags.f_317092_, 2, 1, Enchantment.m_322287_(20), Enchantment.m_322287_(50), 4, EquipmentSlot.MAINHAND)
        )
    );
    public static final Enchantment QUICK_CHARGE = register(
        "quick_charge",
        new Enchantment(Enchantment.m_324539_(ItemTags.f_317092_, 5, 3, Enchantment.m_318803_(12, 20), Enchantment.m_322287_(50), 2, EquipmentSlot.MAINHAND))
    );
    public static final Enchantment PIERCING = register(
        "piercing",
        new ArrowPiercingEnchantment(
            Enchantment.m_324539_(ItemTags.f_317092_, 10, 4, Enchantment.m_318803_(1, 10), Enchantment.m_322287_(50), 1, EquipmentSlot.MAINHAND)
        )
    );
    public static final Enchantment f_314294_ = register("density", new DensityEnchantment());
    public static final Enchantment f_316771_ = register("breach", new BreachEnchantment());
    public static final Enchantment f_316259_ = register("wind_burst", new WindBurstEnchantment());
    public static final Enchantment MENDING = register(
        "mending",
        new MendingEnchantment(
            Enchantment.m_324539_(ItemTags.f_314809_, 2, 1, Enchantment.m_318803_(25, 25), Enchantment.m_318803_(75, 25), 4, EquipmentSlot.values())
        )
    );
    public static final Enchantment VANISHING_CURSE = register(
        "vanishing_curse",
        new VanishingCurseEnchantment(
            Enchantment.m_324539_(ItemTags.f_314986_, 1, 1, Enchantment.m_322287_(25), Enchantment.m_322287_(50), 8, EquipmentSlot.values())
        )
    );

    private static Enchantment register(String pIdentifier, Enchantment pEnchantment) {
        return Registry.register(BuiltInRegistries.ENCHANTMENT, pIdentifier, pEnchantment);
    }
}