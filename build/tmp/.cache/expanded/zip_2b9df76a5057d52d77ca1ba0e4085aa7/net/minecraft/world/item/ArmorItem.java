package net.minecraft.world.item;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import java.util.EnumMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;

public class ArmorItem extends Item implements Equipable {
    private static final EnumMap<ArmorItem.Type, UUID> ARMOR_MODIFIER_UUID_PER_TYPE = Util.make(new EnumMap<>(ArmorItem.Type.class), p_327095_ -> {
        p_327095_.put(ArmorItem.Type.BOOTS, UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"));
        p_327095_.put(ArmorItem.Type.LEGGINGS, UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"));
        p_327095_.put(ArmorItem.Type.CHESTPLATE, UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"));
        p_327095_.put(ArmorItem.Type.HELMET, UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150"));
        p_327095_.put(ArmorItem.Type.BODY, UUID.fromString("C1C72771-8B8E-BA4A-ACE0-81A93C8928B2"));
    });
    public static final DispenseItemBehavior DISPENSE_ITEM_BEHAVIOR = new DefaultDispenseItemBehavior() {
        @Override
        protected ItemStack execute(BlockSource p_40408_, ItemStack p_40409_) {
            return ArmorItem.dispenseArmor(p_40408_, p_40409_) ? p_40409_ : super.execute(p_40408_, p_40409_);
        }
    };
    protected final ArmorItem.Type type;
    protected final Holder<ArmorMaterial> material;
    private final Supplier<ItemAttributeModifiers> defaultModifiers;

    public static boolean dispenseArmor(BlockSource pBlockSource, ItemStack pArmorItem) {
        BlockPos blockpos = pBlockSource.pos().relative(pBlockSource.state().getValue(DispenserBlock.FACING));
        List<LivingEntity> list = pBlockSource.level()
            .getEntitiesOfClass(LivingEntity.class, new AABB(blockpos), EntitySelector.NO_SPECTATORS.and(new EntitySelector.MobCanWearArmorEntitySelector(pArmorItem)));
        if (list.isEmpty()) {
            return false;
        } else {
            LivingEntity livingentity = list.get(0);
            EquipmentSlot equipmentslot = Mob.getEquipmentSlotForItem(pArmorItem);
            ItemStack itemstack = pArmorItem.split(1);
            livingentity.m_21035_(equipmentslot, itemstack);
            if (livingentity instanceof Mob) {
                ((Mob)livingentity).setDropChance(equipmentslot, 2.0F);
                ((Mob)livingentity).setPersistenceRequired();
            }

            return true;
        }
    }

    public ArmorItem(Holder<ArmorMaterial> p_329451_, ArmorItem.Type pType, Item.Properties pProperties) {
        super(pProperties);
        this.material = p_329451_;
        this.type = pType;
        DispenserBlock.registerBehavior(this, DISPENSE_ITEM_BEHAVIOR);
        this.defaultModifiers = Suppliers.memoize(
            () -> {
                int i = p_329451_.value().m_323068_(pType);
                float f = p_329451_.value().f_316002_();
                ItemAttributeModifiers.Builder itemattributemodifiers$builder = ItemAttributeModifiers.m_324327_();
                EquipmentSlotGroup equipmentslotgroup = EquipmentSlotGroup.m_320511_(pType.getSlot());
                UUID uuid = ARMOR_MODIFIER_UUID_PER_TYPE.get(pType);
                itemattributemodifiers$builder.m_324947_(
                    Attributes.ARMOR, new AttributeModifier(uuid, "Armor modifier", (double)i, AttributeModifier.Operation.ADD_VALUE), equipmentslotgroup
                );
                itemattributemodifiers$builder.m_324947_(
                    Attributes.ARMOR_TOUGHNESS, new AttributeModifier(uuid, "Armor toughness", (double)f, AttributeModifier.Operation.ADD_VALUE), equipmentslotgroup
                );
                float f1 = p_329451_.value().f_317001_();
                if (f1 > 0.0F) {
                    itemattributemodifiers$builder.m_324947_(
                        Attributes.KNOCKBACK_RESISTANCE,
                        new AttributeModifier(uuid, "Armor knockback resistance", (double)f1, AttributeModifier.Operation.ADD_VALUE),
                        equipmentslotgroup
                    );
                }

                return itemattributemodifiers$builder.m_320246_();
            }
        );
    }

    public ArmorItem.Type getType() {
        return this.type;
    }

    @Override
    public int getEnchantmentValue() {
        return this.material.value().f_313926_();
    }

    public Holder<ArmorMaterial> getMaterial() {
        return this.material;
    }

    @Override
    public boolean isValidRepairItem(ItemStack pToRepair, ItemStack pRepair) {
        return this.material.value().f_315867_().get().test(pRepair) || super.isValidRepairItem(pToRepair, pRepair);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        return this.swapWithEquipmentSlot(this, pLevel, pPlayer, pHand);
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers() {
        return this.defaultModifiers.get();
    }

    public int getDefense() {
        return this.material.value().m_323068_(this.type);
    }

    public float getToughness() {
        return this.material.value().f_316002_();
    }

    @Override
    public EquipmentSlot getEquipmentSlot() {
        return this.type.getSlot();
    }

    @Override
    public Holder<SoundEvent> getEquipSound() {
        return this.getMaterial().value().f_313996_();
    }

    public static enum Type implements StringRepresentable {
        HELMET(EquipmentSlot.HEAD, 11, "helmet"),
        CHESTPLATE(EquipmentSlot.CHEST, 16, "chestplate"),
        LEGGINGS(EquipmentSlot.LEGS, 15, "leggings"),
        BOOTS(EquipmentSlot.FEET, 13, "boots"),
        BODY(EquipmentSlot.BODY, 16, "body");

        public static final Codec<ArmorItem.Type> f_316159_ = StringRepresentable.m_306774_(ArmorItem.Type::values);
        private final EquipmentSlot slot;
        private final String name;
        private final int f_314374_;

        private Type(final EquipmentSlot pSlot, final int p_328437_, final String pName) {
            this.slot = pSlot;
            this.name = pName;
            this.f_314374_ = p_328437_;
        }

        public int m_321370_(int p_333841_) {
            return this.f_314374_ * p_333841_;
        }

        public EquipmentSlot getSlot() {
            return this.slot;
        }

        public String getName() {
            return this.name;
        }

        public boolean m_321718_() {
            return this == HELMET || this == CHESTPLATE || this == LEGGINGS || this == BOOTS;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}