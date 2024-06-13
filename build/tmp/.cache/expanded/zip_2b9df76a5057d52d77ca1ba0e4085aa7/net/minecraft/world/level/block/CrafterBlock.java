package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeCache;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.CrafterBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class CrafterBlock extends BaseEntityBlock {
    public static final MapCodec<CrafterBlock> f_303783_ = m_306223_(CrafterBlock::new);
    public static final BooleanProperty f_302342_ = BlockStateProperties.f_303070_;
    public static final BooleanProperty f_303414_ = BlockStateProperties.TRIGGERED;
    private static final EnumProperty<FrontAndTop> f_303600_ = BlockStateProperties.ORIENTATION;
    private static final int f_303278_ = 6;
    private static final int f_303006_ = 4;
    private static final RecipeCache f_302794_ = new RecipeCache(10);
    private static final int f_314105_ = 17;

    public CrafterBlock(BlockBehaviour.Properties p_310228_) {
        super(p_310228_);
        this.registerDefaultState(
            this.stateDefinition
                .any()
                .setValue(f_303600_, FrontAndTop.NORTH_UP)
                .setValue(f_303414_, Boolean.valueOf(false))
                .setValue(f_302342_, Boolean.valueOf(false))
        );
    }

    @Override
    protected MapCodec<CrafterBlock> m_304657_() {
        return f_303783_;
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState p_309929_) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState p_311332_, Level p_310277_, BlockPos p_312038_) {
        return p_310277_.getBlockEntity(p_312038_) instanceof CrafterBlockEntity crafterblockentity ? crafterblockentity.m_304952_() : 0;
    }

    @Override
    protected void neighborChanged(BlockState p_309741_, Level p_312714_, BlockPos p_310958_, Block p_313237_, BlockPos p_312468_, boolean p_309615_) {
        boolean flag = p_312714_.hasNeighborSignal(p_310958_);
        boolean flag1 = p_309741_.getValue(f_303414_);
        BlockEntity blockentity = p_312714_.getBlockEntity(p_310958_);
        if (flag && !flag1) {
            p_312714_.scheduleTick(p_310958_, this, 4);
            p_312714_.setBlock(p_310958_, p_309741_.setValue(f_303414_, Boolean.valueOf(true)), 2);
            this.m_306927_(blockentity, true);
        } else if (!flag && flag1) {
            p_312714_.setBlock(p_310958_, p_309741_.setValue(f_303414_, Boolean.valueOf(false)).setValue(f_302342_, Boolean.valueOf(false)), 2);
            this.m_306927_(blockentity, false);
        }
    }

    @Override
    protected void tick(BlockState p_310321_, ServerLevel p_312701_, BlockPos p_311281_, RandomSource p_311092_) {
        this.m_305705_(p_310321_, p_312701_, p_311281_);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_310928_, BlockState p_311648_, BlockEntityType<T> p_310343_) {
        return p_310928_.isClientSide ? null : createTickerHelper(p_310343_, BlockEntityType.f_302698_, CrafterBlockEntity::m_307890_);
    }

    private void m_306927_(@Nullable BlockEntity p_312888_, boolean p_312611_) {
        if (p_312888_ instanceof CrafterBlockEntity crafterblockentity) {
            crafterblockentity.m_305342_(p_312611_);
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos p_311818_, BlockState p_310225_) {
        CrafterBlockEntity crafterblockentity = new CrafterBlockEntity(p_311818_, p_310225_);
        crafterblockentity.m_305342_(p_310225_.hasProperty(f_303414_) && p_310225_.getValue(f_303414_));
        return crafterblockentity;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext p_311294_) {
        Direction direction = p_311294_.getNearestLookingDirection().getOpposite();

        Direction direction1 = switch (direction) {
            case DOWN -> p_311294_.getHorizontalDirection().getOpposite();
            case UP -> p_311294_.getHorizontalDirection();
            case NORTH, SOUTH, WEST, EAST -> Direction.UP;
        };
        return this.defaultBlockState()
            .setValue(f_303600_, FrontAndTop.fromFrontAndTop(direction, direction1))
            .setValue(f_303414_, Boolean.valueOf(p_311294_.getLevel().hasNeighborSignal(p_311294_.getClickedPos())));
    }

    @Override
    public void setPlacedBy(Level p_311617_, BlockPos p_313069_, BlockState p_310230_, LivingEntity p_310379_, ItemStack p_311227_) {
        if (p_310230_.getValue(f_303414_)) {
            p_311617_.scheduleTick(p_313069_, this, 4);
        }
    }

    @Override
    protected void onRemove(BlockState p_310019_, Level p_310489_, BlockPos p_312335_, BlockState p_311081_, boolean p_310350_) {
        Containers.m_307148_(p_310019_, p_311081_, p_310489_, p_312335_);
        super.onRemove(p_310019_, p_310489_, p_312335_, p_311081_, p_310350_);
    }

    @Override
    protected InteractionResult use(BlockState p_309704_, Level p_312700_, BlockPos p_310945_, Player p_312953_, BlockHitResult p_309965_) {
        if (p_312700_.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity blockentity = p_312700_.getBlockEntity(p_310945_);
            if (blockentity instanceof CrafterBlockEntity) {
                p_312953_.openMenu((CrafterBlockEntity)blockentity);
            }

            return InteractionResult.CONSUME;
        }
    }

    protected void m_305705_(BlockState p_313036_, ServerLevel p_310451_, BlockPos p_310774_) {
        if (p_310451_.getBlockEntity(p_310774_) instanceof CrafterBlockEntity crafterblockentity) {
            Optional<RecipeHolder<CraftingRecipe>> optional = m_305919_(p_310451_, crafterblockentity);
            if (optional.isEmpty()) {
                p_310451_.levelEvent(1050, p_310774_, 0);
            } else {
                RecipeHolder<CraftingRecipe> recipeholder = optional.get();
                ItemStack itemstack = recipeholder.value().assemble(crafterblockentity, p_310451_.registryAccess());
                if (itemstack.isEmpty()) {
                    p_310451_.levelEvent(1050, p_310774_, 0);
                } else {
                    crafterblockentity.m_305296_(6);
                    p_310451_.setBlock(p_310774_, p_313036_.setValue(f_302342_, Boolean.valueOf(true)), 2);
                    itemstack.m_305085_(p_310451_);
                    this.m_304843_(p_310451_, p_310774_, crafterblockentity, itemstack, p_313036_, recipeholder);

                    for (ItemStack itemstack1 : recipeholder.value().getRemainingItems(crafterblockentity)) {
                        if (!itemstack1.isEmpty()) {
                            this.m_304843_(p_310451_, p_310774_, crafterblockentity, itemstack1, p_313036_, recipeholder);
                        }
                    }

                    crafterblockentity.m_58617_().forEach(p_312802_ -> {
                        if (!p_312802_.isEmpty()) {
                            p_312802_.shrink(1);
                        }
                    });
                    crafterblockentity.setChanged();
                }
            }
        }
    }

    public static Optional<RecipeHolder<CraftingRecipe>> m_305919_(Level p_311236_, CraftingContainer p_311957_) {
        return f_302794_.m_304754_(p_311236_, p_311957_);
    }

    private void m_304843_(
        ServerLevel p_336186_,
        BlockPos p_312358_,
        CrafterBlockEntity p_309887_,
        ItemStack p_310474_,
        BlockState p_310667_,
        RecipeHolder<CraftingRecipe> p_329387_
    ) {
        Direction direction = p_310667_.getValue(f_303600_).front();
        Container container = HopperBlockEntity.getContainerAt(p_336186_, p_312358_.relative(direction));
        ItemStack itemstack = p_310474_.copy();
        if (container != null && (container instanceof CrafterBlockEntity || p_310474_.getCount() > container.m_322387_(p_310474_))) {
            while (!itemstack.isEmpty()) {
                ItemStack itemstack2 = itemstack.copyWithCount(1);
                ItemStack itemstack1 = HopperBlockEntity.addItem(p_309887_, container, itemstack2, direction.getOpposite());
                if (!itemstack1.isEmpty()) {
                    break;
                }

                itemstack.shrink(1);
            }
        } else if (container != null) {
            while (!itemstack.isEmpty()) {
                int i = itemstack.getCount();
                itemstack = HopperBlockEntity.addItem(p_309887_, container, itemstack, direction.getOpposite());
                if (i == itemstack.getCount()) {
                    break;
                }
            }
        }

        if (!itemstack.isEmpty()) {
            Vec3 vec3 = Vec3.atCenterOf(p_312358_);
            Vec3 vec31 = vec3.relative(direction, 0.7);
            DefaultDispenseItemBehavior.spawnItem(p_336186_, itemstack, 6, direction, vec31);

            for (ServerPlayer serverplayer : p_336186_.getEntitiesOfClass(ServerPlayer.class, AABB.ofSize(vec3, 17.0, 17.0, 17.0))) {
                CriteriaTriggers.f_315310_.trigger(serverplayer, p_329387_.id(), p_309887_.m_58617_());
            }

            p_336186_.levelEvent(1049, p_312358_, 0);
            p_336186_.levelEvent(2010, p_312358_, direction.get3DDataValue());
        }
    }

    @Override
    protected RenderShape getRenderShape(BlockState p_311546_) {
        return RenderShape.MODEL;
    }

    @Override
    protected BlockState rotate(BlockState p_312403_, Rotation p_309910_) {
        return p_312403_.setValue(f_303600_, p_309910_.rotation().rotate(p_312403_.getValue(f_303600_)));
    }

    @Override
    protected BlockState mirror(BlockState p_310178_, Mirror p_311418_) {
        return p_310178_.setValue(f_303600_, p_311418_.rotation().rotate(p_310178_.getValue(f_303600_)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_310076_) {
        p_310076_.add(f_303600_, f_303414_, f_302342_);
    }
}