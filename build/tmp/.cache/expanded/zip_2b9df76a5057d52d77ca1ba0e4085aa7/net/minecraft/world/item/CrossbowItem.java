package net.minecraft.world.item;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class CrossbowItem extends ProjectileWeaponItem {
    private static final int MAX_CHARGE_DURATION = 25;
    public static final int DEFAULT_RANGE = 8;
    private boolean startSoundPlayed = false;
    private boolean midLoadSoundPlayed = false;
    private static final float START_SOUND_PERCENT = 0.2F;
    private static final float MID_SOUND_PERCENT = 0.5F;
    private static final float ARROW_POWER = 3.15F;
    private static final float FIREWORK_POWER = 1.6F;
    public static final float f_316815_ = 1.6F;

    public CrossbowItem(Item.Properties pProperties) {
        super(pProperties);
    }

    @Override
    public Predicate<ItemStack> getSupportedHeldProjectiles() {
        return ARROW_OR_FIREWORK;
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return ARROW_ONLY;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        ChargedProjectiles chargedprojectiles = itemstack.m_323252_(DataComponents.f_314625_);
        if (chargedprojectiles != null && !chargedprojectiles.m_324666_()) {
            this.performShooting(pLevel, pPlayer, pHand, itemstack, getShootingPower(chargedprojectiles), 1.0F, null);
            return InteractionResultHolder.consume(itemstack);
        } else if (!pPlayer.getProjectile(itemstack).isEmpty()) {
            this.startSoundPlayed = false;
            this.midLoadSoundPlayed = false;
            pPlayer.startUsingItem(pHand);
            return InteractionResultHolder.consume(itemstack);
        } else {
            return InteractionResultHolder.fail(itemstack);
        }
    }

    private static float getShootingPower(ChargedProjectiles p_331334_) {
        return p_331334_.m_319117_(Items.FIREWORK_ROCKET) ? 1.6F : 3.15F;
    }

    @Override
    public void releaseUsing(ItemStack pStack, Level pLevel, LivingEntity pEntityLiving, int pTimeLeft) {
        int i = this.getUseDuration(pStack) - pTimeLeft;
        float f = getPowerForTime(i, pStack);
        if (f >= 1.0F && !isCharged(pStack) && tryLoadProjectiles(pEntityLiving, pStack)) {
            pLevel.playSound(
                null,
                pEntityLiving.getX(),
                pEntityLiving.getY(),
                pEntityLiving.getZ(),
                SoundEvents.CROSSBOW_LOADING_END,
                pEntityLiving.getSoundSource(),
                1.0F,
                1.0F / (pLevel.getRandom().nextFloat() * 0.5F + 1.0F) + 0.2F
            );
        }
    }

    private static boolean tryLoadProjectiles(LivingEntity pShooter, ItemStack pCrossbowStack) {
        List<ItemStack> list = m_320555_(pCrossbowStack, pShooter.getProjectile(pCrossbowStack), pShooter);
        if (!list.isEmpty()) {
            pCrossbowStack.m_322496_(DataComponents.f_314625_, ChargedProjectiles.m_322388_(list));
            return true;
        } else {
            return false;
        }
    }

    public static boolean isCharged(ItemStack pCrossbowStack) {
        ChargedProjectiles chargedprojectiles = pCrossbowStack.m_322304_(DataComponents.f_314625_, ChargedProjectiles.f_316210_);
        return !chargedprojectiles.m_324666_();
    }

    @Override
    protected void shootProjectile(
        LivingEntity pShooter, Projectile p_335393_, int p_333089_, float pSoundPitch, float pVelocity, float pInaccuracy, @Nullable LivingEntity p_328705_
    ) {
        Vector3f vector3f;
        if (p_328705_ != null) {
            double d0 = p_328705_.getX() - pShooter.getX();
            double d1 = p_328705_.getZ() - pShooter.getZ();
            double d2 = Math.sqrt(d0 * d0 + d1 * d1);
            double d3 = p_328705_.getY(0.3333333333333333) - p_335393_.getY() + d2 * 0.2F;
            vector3f = m_323098_(pShooter, new Vec3(d0, d3, d1), pInaccuracy);
        } else {
            Vec3 vec3 = pShooter.getUpVector(1.0F);
            Quaternionf quaternionf = new Quaternionf()
                .setAngleAxis((double)(pInaccuracy * (float) (Math.PI / 180.0)), vec3.x, vec3.y, vec3.z);
            Vec3 vec31 = pShooter.getViewVector(1.0F);
            vector3f = vec31.toVector3f().rotate(quaternionf);
        }

        p_335393_.shoot((double)vector3f.x(), (double)vector3f.y(), (double)vector3f.z(), pSoundPitch, pVelocity);
        float f = m_321899_(pShooter.getRandom(), p_333089_);
        pShooter.level().playSound(null, pShooter.getX(), pShooter.getY(), pShooter.getZ(), SoundEvents.CROSSBOW_SHOOT, pShooter.getSoundSource(), 1.0F, f);
    }

    private static Vector3f m_323098_(LivingEntity p_333832_, Vec3 p_332433_, float p_331595_) {
        Vector3f vector3f = p_332433_.toVector3f().normalize();
        Vector3f vector3f1 = new Vector3f(vector3f).cross(new Vector3f(0.0F, 1.0F, 0.0F));
        if ((double)vector3f1.lengthSquared() <= 1.0E-7) {
            Vec3 vec3 = p_333832_.getUpVector(1.0F);
            vector3f1 = new Vector3f(vector3f).cross(vec3.toVector3f());
        }

        Vector3f vector3f2 = new Vector3f(vector3f).rotateAxis((float) (Math.PI / 2), vector3f1.x, vector3f1.y, vector3f1.z);
        return new Vector3f(vector3f).rotateAxis(p_331595_ * (float) (Math.PI / 180.0), vector3f2.x, vector3f2.y, vector3f2.z);
    }

    @Override
    protected Projectile loadProjectile(Level p_329989_, LivingEntity pShooter, ItemStack pCrossbowStack, ItemStack pAmmoStack, boolean pHasAmmo) {
        if (pAmmoStack.is(Items.FIREWORK_ROCKET)) {
            return new FireworkRocketEntity(p_329989_, pAmmoStack, pShooter, pShooter.getX(), pShooter.getEyeY() - 0.15F, pShooter.getZ(), true);
        } else {
            Projectile projectile = super.loadProjectile(p_329989_, pShooter, pCrossbowStack, pAmmoStack, pHasAmmo);
            if (projectile instanceof AbstractArrow abstractarrow) {
                abstractarrow.setShotFromCrossbow(true);
                abstractarrow.setSoundEvent(SoundEvents.CROSSBOW_HIT);
            }

            return projectile;
        }
    }

    @Override
    protected int m_319432_(ItemStack p_335533_) {
        return p_335533_.is(Items.FIREWORK_ROCKET) ? 3 : 1;
    }

    public void performShooting(
        Level pLevel, LivingEntity pShooter, InteractionHand pUsedHand, ItemStack pCrossbowStack, float pVelocity, float pInaccuracy, @Nullable LivingEntity p_329478_
    ) {
        if (!pLevel.isClientSide()) {
            if (pShooter instanceof Player player && net.minecraftforge.event.ForgeEventFactory.onArrowLoose(pCrossbowStack, pShooter.level(), player, 1, true) < 0) return;
            ChargedProjectiles chargedprojectiles = pCrossbowStack.m_322496_(DataComponents.f_314625_, ChargedProjectiles.f_316210_);
            if (chargedprojectiles != null && !chargedprojectiles.m_324666_()) {
                this.m_324710_(
                    pLevel, pShooter, pUsedHand, pCrossbowStack, chargedprojectiles.m_321623_(), pVelocity, pInaccuracy, pShooter instanceof Player, p_329478_
                );
                if (pShooter instanceof ServerPlayer serverplayer) {
                    CriteriaTriggers.SHOT_CROSSBOW.trigger(serverplayer, pCrossbowStack);
                    serverplayer.awardStat(Stats.ITEM_USED.get(pCrossbowStack.getItem()));
                }
            }
        }
    }

    private static float m_321899_(RandomSource p_335611_, int p_331713_) {
        return p_331713_ == 0 ? 1.0F : getRandomShotPitch((p_331713_ & 1) == 1, p_335611_);
    }

    private static float getRandomShotPitch(boolean pIsHighPitched, RandomSource pRandom) {
        float f = pIsHighPitched ? 0.63F : 0.43F;
        return 1.0F / (pRandom.nextFloat() * 0.5F + 1.8F) + f;
    }

    @Override
    public void onUseTick(Level pLevel, LivingEntity pLivingEntity, ItemStack pStack, int pCount) {
        if (!pLevel.isClientSide) {
            int i = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.QUICK_CHARGE, pStack);
            SoundEvent soundevent = this.getStartSound(i);
            SoundEvent soundevent1 = i == 0 ? SoundEvents.CROSSBOW_LOADING_MIDDLE : null;
            float f = (float)(pStack.getUseDuration() - pCount) / (float)getChargeDuration(pStack);
            if (f < 0.2F) {
                this.startSoundPlayed = false;
                this.midLoadSoundPlayed = false;
            }

            if (f >= 0.2F && !this.startSoundPlayed) {
                this.startSoundPlayed = true;
                pLevel.playSound(null, pLivingEntity.getX(), pLivingEntity.getY(), pLivingEntity.getZ(), soundevent, SoundSource.PLAYERS, 0.5F, 1.0F);
            }

            if (f >= 0.5F && soundevent1 != null && !this.midLoadSoundPlayed) {
                this.midLoadSoundPlayed = true;
                pLevel.playSound(null, pLivingEntity.getX(), pLivingEntity.getY(), pLivingEntity.getZ(), soundevent1, SoundSource.PLAYERS, 0.5F, 1.0F);
            }
        }
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return getChargeDuration(pStack) + 3;
    }

    public static int getChargeDuration(ItemStack pCrossbowStack) {
        int i = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.QUICK_CHARGE, pCrossbowStack);
        return i == 0 ? 25 : 25 - 5 * i;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.CROSSBOW;
    }

    private SoundEvent getStartSound(int pEnchantmentLevel) {
        switch (pEnchantmentLevel) {
            case 1:
                return SoundEvents.CROSSBOW_QUICK_CHARGE_1;
            case 2:
                return SoundEvents.CROSSBOW_QUICK_CHARGE_2;
            case 3:
                return SoundEvents.CROSSBOW_QUICK_CHARGE_3;
            default:
                return SoundEvents.CROSSBOW_LOADING_START;
        }
    }

    private static float getPowerForTime(int pUseTime, ItemStack pCrossbowStack) {
        float f = (float)pUseTime / (float)getChargeDuration(pCrossbowStack);
        if (f > 1.0F) {
            f = 1.0F;
        }

        return f;
    }

    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext p_333751_, List<Component> pTooltip, TooltipFlag pFlag) {
        ChargedProjectiles chargedprojectiles = pStack.m_323252_(DataComponents.f_314625_);
        if (chargedprojectiles != null && !chargedprojectiles.m_324666_()) {
            ItemStack itemstack = chargedprojectiles.m_321623_().get(0);
            pTooltip.add(Component.translatable("item.minecraft.crossbow.projectile").append(CommonComponents.SPACE).append(itemstack.getDisplayName()));
            if (pFlag.isAdvanced() && itemstack.is(Items.FIREWORK_ROCKET)) {
                List<Component> list = Lists.newArrayList();
                Items.FIREWORK_ROCKET.appendHoverText(itemstack, p_333751_, list, pFlag);
                if (!list.isEmpty()) {
                    for (int i = 0; i < list.size(); i++) {
                        list.set(i, Component.literal("  ").append(list.get(i)).withStyle(ChatFormatting.GRAY));
                    }

                    pTooltip.addAll(list);
                }
            }
        }
    }

    @Override
    public boolean useOnRelease(ItemStack pStack) {
        return pStack.is(this);
    }

    @Override
    public int getDefaultProjectileRange() {
        return 8;
    }
}
