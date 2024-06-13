package net.minecraft.world.item;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.level.Level;

public class SuspiciousStewItem extends Item {
    public static final int DEFAULT_DURATION = 160;

    public SuspiciousStewItem(Item.Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext p_333118_, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, p_333118_, pTooltipComponents, pIsAdvanced);
        if (pIsAdvanced.isCreative()) {
            List<MobEffectInstance> list = new ArrayList<>();
            SuspiciousStewEffects suspicioussteweffects = pStack.m_322304_(DataComponents.f_316666_, SuspiciousStewEffects.f_314102_);

            for (SuspiciousStewEffects.Entry suspicioussteweffects$entry : suspicioussteweffects.f_315993_()) {
                list.add(suspicioussteweffects$entry.m_320712_());
            }

            PotionContents.m_319937_(list, pTooltipComponents::add, 1.0F, p_333118_.m_319443_());
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pEntityLiving) {
        SuspiciousStewEffects suspicioussteweffects = pStack.m_322304_(DataComponents.f_316666_, SuspiciousStewEffects.f_314102_);

        for (SuspiciousStewEffects.Entry suspicioussteweffects$entry : suspicioussteweffects.f_315993_()) {
            pEntityLiving.addEffect(suspicioussteweffects$entry.m_320712_());
        }

        super.finishUsingItem(pStack, pLevel, pEntityLiving);
        return pEntityLiving.m_322042_() ? pStack : new ItemStack(Items.BOWL);
    }
}