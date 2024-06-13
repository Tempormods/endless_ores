package net.minecraft.world.level.block.entity;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CommandBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class CommandBlockEntity extends BlockEntity {
    private boolean powered;
    private boolean auto;
    private boolean conditionMet;
    private final BaseCommandBlock commandBlock = new BaseCommandBlock() {
        @Override
        public void setCommand(String p_59157_) {
            super.setCommand(p_59157_);
            CommandBlockEntity.this.setChanged();
        }

        @Override
        public ServerLevel getLevel() {
            return (ServerLevel)CommandBlockEntity.this.level;
        }

        @Override
        public void onUpdated() {
            BlockState blockstate = CommandBlockEntity.this.level.getBlockState(CommandBlockEntity.this.worldPosition);
            this.getLevel().sendBlockUpdated(CommandBlockEntity.this.worldPosition, blockstate, blockstate, 3);
        }

        @Override
        public Vec3 getPosition() {
            return Vec3.atCenterOf(CommandBlockEntity.this.worldPosition);
        }

        @Override
        public CommandSourceStack createCommandSourceStack() {
            Direction direction = CommandBlockEntity.this.getBlockState().getValue(CommandBlock.FACING);
            return new CommandSourceStack(
                this,
                Vec3.atCenterOf(CommandBlockEntity.this.worldPosition),
                new Vec2(0.0F, direction.toYRot()),
                this.getLevel(),
                2,
                this.getName().getString(),
                this.getName(),
                this.getLevel().getServer(),
                null
            );
        }

        @Override
        public boolean isValid() {
            return !CommandBlockEntity.this.isRemoved();
        }
    };

    public CommandBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityType.COMMAND_BLOCK, pPos, pBlockState);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider p_327824_) {
        super.saveAdditional(pTag, p_327824_);
        this.commandBlock.save(pTag, p_327824_);
        pTag.putBoolean("powered", this.isPowered());
        pTag.putBoolean("conditionMet", this.wasConditionMet());
        pTag.putBoolean("auto", this.isAutomatic());
    }

    @Override
    protected void m_318667_(CompoundTag p_334160_, HolderLookup.Provider p_334209_) {
        super.m_318667_(p_334160_, p_334209_);
        this.commandBlock.load(p_334160_, p_334209_);
        this.powered = p_334160_.getBoolean("powered");
        this.conditionMet = p_334160_.getBoolean("conditionMet");
        this.setAutomatic(p_334160_.getBoolean("auto"));
    }

    @Override
    public boolean onlyOpCanSetNbt() {
        return true;
    }

    public BaseCommandBlock getCommandBlock() {
        return this.commandBlock;
    }

    public void setPowered(boolean pPowered) {
        this.powered = pPowered;
    }

    public boolean isPowered() {
        return this.powered;
    }

    public boolean isAutomatic() {
        return this.auto;
    }

    public void setAutomatic(boolean pAuto) {
        boolean flag = this.auto;
        this.auto = pAuto;
        if (!flag && pAuto && !this.powered && this.level != null && this.getMode() != CommandBlockEntity.Mode.SEQUENCE) {
            this.scheduleTick();
        }
    }

    public void onModeSwitch() {
        CommandBlockEntity.Mode commandblockentity$mode = this.getMode();
        if (commandblockentity$mode == CommandBlockEntity.Mode.AUTO && (this.powered || this.auto) && this.level != null) {
            this.scheduleTick();
        }
    }

    private void scheduleTick() {
        Block block = this.getBlockState().getBlock();
        if (block instanceof CommandBlock) {
            this.markConditionMet();
            this.level.scheduleTick(this.worldPosition, block, 1);
        }
    }

    public boolean wasConditionMet() {
        return this.conditionMet;
    }

    public boolean markConditionMet() {
        this.conditionMet = true;
        if (this.isConditional()) {
            BlockPos blockpos = this.worldPosition.relative(this.level.getBlockState(this.worldPosition).getValue(CommandBlock.FACING).getOpposite());
            if (this.level.getBlockState(blockpos).getBlock() instanceof CommandBlock) {
                BlockEntity blockentity = this.level.getBlockEntity(blockpos);
                this.conditionMet = blockentity instanceof CommandBlockEntity && ((CommandBlockEntity)blockentity).getCommandBlock().getSuccessCount() > 0;
            } else {
                this.conditionMet = false;
            }
        }

        return this.conditionMet;
    }

    public CommandBlockEntity.Mode getMode() {
        BlockState blockstate = this.getBlockState();
        if (blockstate.is(Blocks.COMMAND_BLOCK)) {
            return CommandBlockEntity.Mode.REDSTONE;
        } else if (blockstate.is(Blocks.REPEATING_COMMAND_BLOCK)) {
            return CommandBlockEntity.Mode.AUTO;
        } else {
            return blockstate.is(Blocks.CHAIN_COMMAND_BLOCK) ? CommandBlockEntity.Mode.SEQUENCE : CommandBlockEntity.Mode.REDSTONE;
        }
    }

    public boolean isConditional() {
        BlockState blockstate = this.level.getBlockState(this.getBlockPos());
        return blockstate.getBlock() instanceof CommandBlock ? blockstate.getValue(CommandBlock.CONDITIONAL) : false;
    }

    @Override
    protected void m_318741_(BlockEntity.DataComponentInput p_329968_) {
        super.m_318741_(p_329968_);
        this.commandBlock.m_321484_(p_329968_.m_319293_(DataComponents.f_316016_));
    }

    @Override
    protected void m_318837_(DataComponentMap.Builder p_329197_) {
        super.m_318837_(p_329197_);
        p_329197_.m_322739_(DataComponents.f_316016_, this.commandBlock.m_323895_());
    }

    @Override
    public void m_318942_(CompoundTag p_327869_) {
        super.m_318942_(p_327869_);
        p_327869_.remove("CustomName");
    }

    public static enum Mode {
        SEQUENCE,
        AUTO,
        REDSTONE;
    }
}