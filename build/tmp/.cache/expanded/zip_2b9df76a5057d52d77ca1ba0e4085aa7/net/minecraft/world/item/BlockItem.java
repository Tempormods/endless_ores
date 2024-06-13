package net.minecraft.world.item;

import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.shapes.CollisionContext;

public class BlockItem extends Item {
    @Deprecated
    private final Block block;

    public BlockItem(Block pBlock, Item.Properties pProperties) {
        super(pProperties);
        this.block = pBlock;
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        InteractionResult interactionresult = this.place(new BlockPlaceContext(pContext));
        if (!interactionresult.consumesAction() && pContext.getItemInHand().m_319951_(DataComponents.f_315399_)) {
            InteractionResult interactionresult1 = super.use(pContext.getLevel(), pContext.getPlayer(), pContext.getHand()).getResult();
            return interactionresult1 == InteractionResult.CONSUME ? InteractionResult.CONSUME_PARTIAL : interactionresult1;
        } else {
            return interactionresult;
        }
    }

    public InteractionResult place(BlockPlaceContext pContext) {
        if (!this.getBlock().isEnabled(pContext.getLevel().enabledFeatures())) {
            return InteractionResult.FAIL;
        } else if (!pContext.canPlace()) {
            return InteractionResult.FAIL;
        } else {
            BlockPlaceContext blockplacecontext = this.updatePlacementContext(pContext);
            if (blockplacecontext == null) {
                return InteractionResult.FAIL;
            } else {
                BlockState blockstate = this.getPlacementState(blockplacecontext);
                if (blockstate == null) {
                    return InteractionResult.FAIL;
                } else if (!this.placeBlock(blockplacecontext, blockstate)) {
                    return InteractionResult.FAIL;
                } else {
                    BlockPos blockpos = blockplacecontext.getClickedPos();
                    Level level = blockplacecontext.getLevel();
                    Player player = blockplacecontext.getPlayer();
                    ItemStack itemstack = blockplacecontext.getItemInHand();
                    BlockState blockstate1 = level.getBlockState(blockpos);
                    if (blockstate1.is(blockstate.getBlock())) {
                        blockstate1 = this.updateBlockStateFromTag(blockpos, level, itemstack, blockstate1);
                        this.updateCustomBlockEntityTag(blockpos, level, player, itemstack, blockstate1);
                        m_322358_(level, blockpos, itemstack);
                        blockstate1.getBlock().setPlacedBy(level, blockpos, blockstate1, player, itemstack);
                        if (player instanceof ServerPlayer) {
                            CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)player, blockpos, itemstack);
                        }
                    }

                    SoundType soundtype = blockstate1.getSoundType(level, blockpos, pContext.getPlayer());
                    level.playSound(
                        player, blockpos, this.getPlaceSound(blockstate1, level, blockpos, pContext.getPlayer()), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F
                    );
                    level.m_322719_(GameEvent.BLOCK_PLACE, blockpos, GameEvent.Context.of(player, blockstate1));
                    itemstack.m_321439_(1, player);
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
            }
        }
    }

    @Deprecated //Forge: Use more sensitive version {@link BlockItem#getPlaceSound(BlockState, IBlockReader, BlockPos, Entity) }
    protected SoundEvent getPlaceSound(BlockState pState) {
        return pState.getSoundType().getPlaceSound();
    }

    //Forge: Sensitive version of BlockItem#getPlaceSound
    protected SoundEvent getPlaceSound(BlockState state, Level world, BlockPos pos, Player entity) {
        return state.getSoundType(world, pos, entity).getPlaceSound();
    }

    @Nullable
    public BlockPlaceContext updatePlacementContext(BlockPlaceContext pContext) {
        return pContext;
    }

    private static void m_322358_(Level p_333389_, BlockPos p_335748_, ItemStack p_334527_) {
        BlockEntity blockentity = p_333389_.getBlockEntity(p_335748_);
        if (blockentity != null) {
            blockentity.m_322533_(p_334527_);
            blockentity.setChanged();
        }
    }

    protected boolean updateCustomBlockEntityTag(BlockPos pPos, Level pLevel, @Nullable Player pPlayer, ItemStack pStack, BlockState pState) {
        return updateCustomBlockEntityTag(pLevel, pPlayer, pPos, pStack);
    }

    @Nullable
    protected BlockState getPlacementState(BlockPlaceContext pContext) {
        BlockState blockstate = this.getBlock().getStateForPlacement(pContext);
        return blockstate != null && this.canPlace(pContext, blockstate) ? blockstate : null;
    }

    private BlockState updateBlockStateFromTag(BlockPos pPos, Level pLevel, ItemStack pStack, BlockState pState) {
        BlockItemStateProperties blockitemstateproperties = pStack.m_322304_(DataComponents.f_315479_, BlockItemStateProperties.f_314882_);
        if (blockitemstateproperties.m_322799_()) {
            return pState;
        } else {
            BlockState blockstate = blockitemstateproperties.m_323904_(pState);
            if (blockstate != pState) {
                pLevel.setBlock(pPos, blockstate, 2);
            }

            return blockstate;
        }
    }

    protected boolean canPlace(BlockPlaceContext pContext, BlockState pState) {
        Player player = pContext.getPlayer();
        CollisionContext collisioncontext = player == null ? CollisionContext.empty() : CollisionContext.of(player);
        return (!this.mustSurvive() || pState.canSurvive(pContext.getLevel(), pContext.getClickedPos()))
            && pContext.getLevel().isUnobstructed(pState, pContext.getClickedPos(), collisioncontext);
    }

    protected boolean mustSurvive() {
        return true;
    }

    protected boolean placeBlock(BlockPlaceContext pContext, BlockState pState) {
        return pContext.getLevel().setBlock(pContext.getClickedPos(), pState, 11);
    }

    public static boolean updateCustomBlockEntityTag(Level pLevel, @Nullable Player pPlayer, BlockPos pPos, ItemStack pStack) {
        MinecraftServer minecraftserver = pLevel.getServer();
        if (minecraftserver == null) {
            return false;
        } else {
            CustomData customdata = pStack.m_322304_(DataComponents.f_316520_, CustomData.f_317060_);
            if (!customdata.m_318976_()) {
                BlockEntity blockentity = pLevel.getBlockEntity(pPos);
                if (blockentity != null) {
                    if (pLevel.isClientSide || !blockentity.onlyOpCanSetNbt() || pPlayer != null && pPlayer.canUseGameMasterBlocks()) {
                        return customdata.m_323254_(blockentity, pLevel.registryAccess());
                    }

                    return false;
                }
            }

            return false;
        }
    }

    @Override
    public String getDescriptionId() {
        return this.getBlock().getDescriptionId();
    }

    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext p_327780_, List<Component> pTooltip, TooltipFlag pFlag) {
        super.appendHoverText(pStack, p_327780_, pTooltip, pFlag);
        this.getBlock().appendHoverText(pStack, p_327780_, pTooltip, pFlag);
    }

    public Block getBlock() {
        return this.block;
    }

    public void registerBlocks(Map<Block, Item> pBlockToItemMap, Item pItem) {
        pBlockToItemMap.put(this.getBlock(), pItem);
    }

    public void removeFromBlockToItemMap(Map<Block, Item> blockToItemMap, Item itemIn) {
        blockToItemMap.remove(this.getBlock());
    }

    @Override
    public boolean canFitInsideContainerItems() {
        return !(this.getBlock() instanceof ShulkerBoxBlock);
    }

    @Override
    public void onDestroyed(ItemEntity pItemEntity) {
        ItemContainerContents itemcontainercontents = pItemEntity.getItem().m_322496_(DataComponents.f_316065_, ItemContainerContents.f_316619_);
        if (itemcontainercontents != null) {
            ItemUtils.onContainerDestroyed(pItemEntity, itemcontainercontents.m_324961_());
        }
    }

    public static void setBlockEntityData(ItemStack pStack, BlockEntityType<?> pBlockEntityType, CompoundTag pBlockEntityData) {
        pBlockEntityData.remove("id");
        if (pBlockEntityData.isEmpty()) {
            pStack.m_319322_(DataComponents.f_316520_);
        } else {
            BlockEntity.addEntityType(pBlockEntityData, pBlockEntityType);
            pStack.m_322496_(DataComponents.f_316520_, CustomData.m_321102_(pBlockEntityData));
        }
    }

    @Override
    public FeatureFlagSet requiredFeatures() {
        return this.getBlock().requiredFeatures();
    }
}
