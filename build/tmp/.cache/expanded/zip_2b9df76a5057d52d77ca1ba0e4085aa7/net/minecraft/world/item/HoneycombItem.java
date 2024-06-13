package net.minecraft.world.item;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class HoneycombItem extends Item implements SignApplicator {
    public static final Supplier<BiMap<Block, Block>> WAXABLES = Suppliers.memoize(
        () -> ImmutableBiMap.<Block, Block>builder()
                .put(Blocks.COPPER_BLOCK, Blocks.WAXED_COPPER_BLOCK)
                .put(Blocks.EXPOSED_COPPER, Blocks.WAXED_EXPOSED_COPPER)
                .put(Blocks.WEATHERED_COPPER, Blocks.WAXED_WEATHERED_COPPER)
                .put(Blocks.OXIDIZED_COPPER, Blocks.WAXED_OXIDIZED_COPPER)
                .put(Blocks.CUT_COPPER, Blocks.WAXED_CUT_COPPER)
                .put(Blocks.EXPOSED_CUT_COPPER, Blocks.WAXED_EXPOSED_CUT_COPPER)
                .put(Blocks.WEATHERED_CUT_COPPER, Blocks.WAXED_WEATHERED_CUT_COPPER)
                .put(Blocks.OXIDIZED_CUT_COPPER, Blocks.WAXED_OXIDIZED_CUT_COPPER)
                .put(Blocks.CUT_COPPER_SLAB, Blocks.WAXED_CUT_COPPER_SLAB)
                .put(Blocks.EXPOSED_CUT_COPPER_SLAB, Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB)
                .put(Blocks.WEATHERED_CUT_COPPER_SLAB, Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB)
                .put(Blocks.OXIDIZED_CUT_COPPER_SLAB, Blocks.WAXED_OXIDIZED_CUT_COPPER_SLAB)
                .put(Blocks.CUT_COPPER_STAIRS, Blocks.WAXED_CUT_COPPER_STAIRS)
                .put(Blocks.EXPOSED_CUT_COPPER_STAIRS, Blocks.WAXED_EXPOSED_CUT_COPPER_STAIRS)
                .put(Blocks.WEATHERED_CUT_COPPER_STAIRS, Blocks.WAXED_WEATHERED_CUT_COPPER_STAIRS)
                .put(Blocks.OXIDIZED_CUT_COPPER_STAIRS, Blocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS)
                .put(Blocks.f_302689_, Blocks.f_303363_)
                .put(Blocks.f_303448_, Blocks.f_302554_)
                .put(Blocks.f_302507_, Blocks.f_302612_)
                .put(Blocks.f_303811_, Blocks.f_303118_)
                .put(Blocks.f_302565_, Blocks.f_302634_)
                .put(Blocks.f_303201_, Blocks.f_303222_)
                .put(Blocks.f_303010_, Blocks.f_302853_)
                .put(Blocks.f_303016_, Blocks.f_303692_)
                .put(Blocks.f_303635_, Blocks.f_302395_)
                .put(Blocks.f_303609_, Blocks.f_302272_)
                .put(Blocks.f_302627_, Blocks.f_303663_)
                .put(Blocks.f_302247_, Blocks.f_303101_)
                .put(Blocks.f_303215_, Blocks.f_303013_)
                .put(Blocks.f_302995_, Blocks.f_303410_)
                .put(Blocks.f_303236_, Blocks.f_302872_)
                .put(Blocks.f_303549_, Blocks.f_303373_)
                .put(Blocks.f_302358_, Blocks.f_302439_)
                .put(Blocks.f_303271_, Blocks.f_303797_)
                .put(Blocks.f_303674_, Blocks.f_302556_)
                .put(Blocks.f_302668_, Blocks.f_302347_)
                .build()
    );
    public static final Supplier<BiMap<Block, Block>> WAX_OFF_BY_BLOCK = Suppliers.memoize(() -> WAXABLES.get().inverse());

    public HoneycombItem(Item.Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        BlockPos blockpos = pContext.getClickedPos();
        BlockState blockstate = level.getBlockState(blockpos);
        return getWaxed(blockstate).map(p_312459_ -> {
            Player player = pContext.getPlayer();
            ItemStack itemstack = pContext.getItemInHand();
            if (player instanceof ServerPlayer) {
                CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)player, blockpos, itemstack);
            }

            itemstack.shrink(1);
            level.setBlock(blockpos, p_312459_, 11);
            level.m_322719_(GameEvent.BLOCK_CHANGE, blockpos, GameEvent.Context.of(player, p_312459_));
            level.levelEvent(player, 3003, blockpos, 0);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }).orElse(InteractionResult.PASS);
    }

    public static Optional<BlockState> getWaxed(BlockState pState) {
        return Optional.ofNullable(WAXABLES.get().get(pState.getBlock())).map(p_150877_ -> p_150877_.withPropertiesOf(pState));
    }

    @Override
    public boolean tryApplyToSign(Level pLevel, SignBlockEntity pSign, boolean pIsFront, Player pPlayer) {
        if (pSign.setWaxed(true)) {
            pLevel.levelEvent(null, 3003, pSign.getBlockPos(), 0);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean canApplyToSign(SignText pText, Player pPlayer) {
        return true;
    }
}