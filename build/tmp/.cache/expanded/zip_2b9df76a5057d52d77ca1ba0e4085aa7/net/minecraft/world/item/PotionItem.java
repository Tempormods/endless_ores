package net.minecraft.world.item;

import java.util.List;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class PotionItem extends Item {
    private static final int DRINK_DURATION = 32;

    public PotionItem(Item.Properties pProperties) {
        super(pProperties);
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack itemstack = super.getDefaultInstance();
        itemstack.m_322496_(DataComponents.f_314188_, new PotionContents(Potions.WATER));
        return itemstack;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pEntityLiving) {
        Player player = pEntityLiving instanceof Player ? (Player)pEntityLiving : null;
        if (player instanceof ServerPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)player, pStack);
        }

        if (!pLevel.isClientSide) {
            PotionContents potioncontents = pStack.m_322304_(DataComponents.f_314188_, PotionContents.f_313984_);
            potioncontents.m_325077_(p_327729_ -> {
                if (p_327729_.getEffect().value().isInstantenous()) {
                    p_327729_.getEffect().value().applyInstantenousEffect(player, player, pEntityLiving, p_327729_.getAmplifier(), 1.0);
                } else {
                    pEntityLiving.addEffect(p_327729_);
                }
            });
        }

        if (player != null) {
            player.awardStat(Stats.ITEM_USED.get(this));
            pStack.m_321439_(1, player);
        }

        if (player == null || !player.m_322042_()) {
            if (pStack.isEmpty()) {
                return new ItemStack(Items.GLASS_BOTTLE);
            }

            if (player != null) {
                player.getInventory().add(new ItemStack(Items.GLASS_BOTTLE));
            }
        }

        pEntityLiving.gameEvent(GameEvent.DRINK);
        return pStack;
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        BlockPos blockpos = pContext.getClickedPos();
        Player player = pContext.getPlayer();
        ItemStack itemstack = pContext.getItemInHand();
        PotionContents potioncontents = itemstack.m_322304_(DataComponents.f_314188_, PotionContents.f_313984_);
        BlockState blockstate = level.getBlockState(blockpos);
        if (pContext.getClickedFace() != Direction.DOWN && blockstate.is(BlockTags.CONVERTABLE_TO_MUD) && potioncontents.m_323649_(Potions.WATER)) {
            level.playSound(null, blockpos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 1.0F, 1.0F);
            player.setItemInHand(pContext.getHand(), ItemUtils.createFilledResult(itemstack, player, new ItemStack(Items.GLASS_BOTTLE)));
            player.awardStat(Stats.ITEM_USED.get(itemstack.getItem()));
            if (!level.isClientSide) {
                ServerLevel serverlevel = (ServerLevel)level;

                for (int i = 0; i < 5; i++) {
                    serverlevel.sendParticles(
                        ParticleTypes.SPLASH,
                        (double)blockpos.getX() + level.random.nextDouble(),
                        (double)(blockpos.getY() + 1),
                        (double)blockpos.getZ() + level.random.nextDouble(),
                        1,
                        0.0,
                        0.0,
                        0.0,
                        1.0
                    );
                }
            }

            level.playSound(null, blockpos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(null, GameEvent.FLUID_PLACE, blockpos);
            level.setBlockAndUpdate(blockpos, Blocks.MUD.defaultBlockState());
            return InteractionResult.sidedSuccess(level.isClientSide);
        } else {
            return InteractionResult.PASS;
        }
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 32;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.DRINK;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        return ItemUtils.startUsingInstantly(pLevel, pPlayer, pHand);
    }

    @Override
    public String getDescriptionId(ItemStack pStack) {
        return Potion.getName(pStack.m_322304_(DataComponents.f_314188_, PotionContents.f_313984_).f_317059_(), this.getDescriptionId() + ".effect.");
    }

    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext p_332780_, List<Component> pTooltip, TooltipFlag pFlag) {
        PotionContents potioncontents = pStack.m_323252_(DataComponents.f_314188_);
        if (potioncontents != null) {
            potioncontents.m_324933_(pTooltip::add, 1.0F, p_332780_.m_319443_());
        }
    }
}