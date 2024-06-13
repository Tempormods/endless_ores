package net.minecraft.world.item;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.enchantment.DensityEnchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class MaceItem extends Item {
    private static final int f_315599_ = 6;
    private static final float f_314059_ = -2.4F;
    private static final float f_314421_ = 1.5F;
    private static final float f_315869_ = 5.0F;
    public static final float f_315457_ = 3.5F;
    private static final float f_315270_ = 0.7F;
    private static final float f_316474_ = 3.0F;

    public MaceItem(Item.Properties p_329217_) {
        super(p_329217_);
    }

    public static ItemAttributeModifiers m_320958_() {
        return ItemAttributeModifiers.m_324327_()
            .m_324947_(
                Attributes.ATTACK_DAMAGE,
                new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", 6.0, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND
            )
            .m_324947_(
                Attributes.ATTACK_SPEED,
                new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", -2.4F, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND
            )
            .m_320246_();
    }

    public static Tool m_324286_() {
        return new Tool(List.of(), 1.0F, 2);
    }

    @Override
    public boolean canAttackBlock(BlockState p_330271_, Level p_332833_, BlockPos p_334020_, Player p_336375_) {
        return !p_336375_.isCreative();
    }

    @Override
    public int getEnchantmentValue() {
        return 15;
    }

    @Override
    public boolean hurtEnemy(ItemStack p_329476_, LivingEntity p_332492_, LivingEntity p_333391_) {
        p_329476_.hurtAndBreak(1, p_333391_, EquipmentSlot.MAINHAND);
        if (p_333391_ instanceof ServerPlayer serverplayer && m_320829_(serverplayer)) {
            ServerLevel serverlevel = (ServerLevel)p_333391_.level();
            serverplayer.f_316171_ = serverplayer.position();
            serverplayer.f_315903_ = true;
            serverplayer.setDeltaMovement(serverplayer.getDeltaMovement().with(Direction.Axis.Y, 0.01F));
            serverplayer.connection.send(new ClientboundSetEntityMotionPacket(serverplayer));
            if (p_332492_.onGround()) {
                serverplayer.m_324634_(true);
                SoundEvent soundevent = serverplayer.fallDistance > 5.0F ? SoundEvents.f_314713_ : SoundEvents.f_316241_;
                serverlevel.playSound(
                    null, serverplayer.getX(), serverplayer.getY(), serverplayer.getZ(), soundevent, serverplayer.getSoundSource(), 1.0F, 1.0F
                );
            } else {
                serverlevel.playSound(
                    null, serverplayer.getX(), serverplayer.getY(), serverplayer.getZ(), SoundEvents.f_314643_, serverplayer.getSoundSource(), 1.0F, 1.0F
                );
            }

            m_322114_(serverlevel, serverplayer, p_332492_);
            return true;
        }

        return false;
    }

    @Override
    public boolean isValidRepairItem(ItemStack p_335618_, ItemStack p_332323_) {
        return p_332323_.is(Items.f_315544_);
    }

    @Override
    public float m_319585_(Player p_336257_, float p_333106_) {
        int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.f_314294_, p_336257_);
        float f = DensityEnchantment.m_324546_(i, p_336257_.fallDistance);
        return m_320829_(p_336257_) ? 3.0F * p_336257_.fallDistance + f : 0.0F;
    }

    private static void m_322114_(Level p_332228_, Player p_335060_, Entity p_335011_) {
        p_332228_.levelEvent(2013, p_335011_.getOnPos(), 750);
        p_332228_.getEntitiesOfClass(LivingEntity.class, p_335011_.getBoundingBox().inflate(3.5), m_321647_(p_335060_, p_335011_)).forEach(p_328659_ -> {
            Vec3 vec3 = p_328659_.position().subtract(p_335011_.position());
            double d0 = m_320432_(p_335060_, p_328659_, vec3);
            Vec3 vec31 = vec3.normalize().scale(d0);
            if (d0 > 0.0) {
                p_328659_.push(vec31.x, 0.7F, vec31.z);
            }
        });
    }

    private static Predicate<LivingEntity> m_321647_(Player p_334836_, Entity p_334480_) {
        return p_328244_ -> {
            boolean flag;
            boolean flag1;
            boolean flag2;
            boolean flag5;
            label44: {
                flag = !p_328244_.isSpectator();
                flag1 = p_328244_ != p_334836_ && p_328244_ != p_334480_;
                flag2 = !p_334836_.isAlliedTo(p_328244_);
                if (p_328244_ instanceof ArmorStand armorstand && armorstand.isMarker()) {
                    flag5 = false;
                    break label44;
                }

                flag5 = true;
            }

            boolean flag3 = flag5;
            boolean flag4 = p_334480_.distanceToSqr(p_328244_) <= Math.pow(3.5, 2.0);
            return flag && flag1 && flag2 && flag3 && flag4;
        };
    }

    private static double m_320432_(Player p_328672_, LivingEntity p_334129_, Vec3 p_335583_) {
        return (3.5 - p_335583_.length()) * 0.7F * (double)(p_328672_.fallDistance > 5.0F ? 2 : 1) * (1.0 - p_334129_.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
    }

    public static boolean m_320829_(Player p_328263_) {
        return p_328263_.fallDistance > 1.5F && !p_328263_.isFallFlying();
    }
}