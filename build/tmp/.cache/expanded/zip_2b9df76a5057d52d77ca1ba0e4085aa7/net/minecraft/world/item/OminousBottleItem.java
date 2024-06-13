package net.minecraft.world.item;

import java.util.List;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;

public class OminousBottleItem extends Item {
    private static final int f_313942_ = 32;
    public static final int f_316721_ = 120000;
    public static final int f_316759_ = 0;
    public static final int f_315293_ = 4;

    public OminousBottleItem(Item.Properties p_336019_) {
        super(p_336019_);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack p_332481_, Level p_329423_, LivingEntity p_328184_) {
        if (p_328184_ instanceof ServerPlayer serverplayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverplayer, p_332481_);
            serverplayer.awardStat(Stats.ITEM_USED.get(this));
        }

        p_332481_.m_321439_(1, p_328184_);
        if (!p_329423_.isClientSide) {
            p_329423_.playSound(null, p_328184_.blockPosition(), SoundEvents.f_314218_, p_328184_.getSoundSource(), 1.0F, 1.0F);
            Integer integer = p_332481_.m_322304_(DataComponents.f_314953_, Integer.valueOf(0));
            p_328184_.removeEffect(MobEffects.BAD_OMEN);
            p_328184_.addEffect(new MobEffectInstance(MobEffects.BAD_OMEN, 120000, integer, false, false, true));
        }

        return p_332481_;
    }

    @Override
    public int getUseDuration(ItemStack p_334717_) {
        return 32;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack p_334953_) {
        return UseAnim.DRINK;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level p_332013_, Player p_330900_, InteractionHand p_332260_) {
        return ItemUtils.startUsingInstantly(p_332013_, p_330900_, p_332260_);
    }

    @Override
    public void appendHoverText(ItemStack p_332316_, Item.TooltipContext p_327872_, List<Component> p_328132_, TooltipFlag p_328273_) {
        super.appendHoverText(p_332316_, p_327872_, p_328132_, p_328273_);
        Integer integer = p_332316_.m_322304_(DataComponents.f_314953_, Integer.valueOf(0));
        List<MobEffectInstance> list = List.of(new MobEffectInstance(MobEffects.BAD_OMEN, 120000, integer, false, false, true));
        PotionContents.m_319937_(list, p_328132_::add, 1.0F, p_327872_.m_319443_());
    }
}