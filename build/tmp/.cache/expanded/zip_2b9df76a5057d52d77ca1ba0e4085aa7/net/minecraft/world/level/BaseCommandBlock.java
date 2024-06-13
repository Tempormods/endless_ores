package net.minecraft.world.level;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringUtil;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public abstract class BaseCommandBlock implements CommandSource {
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
    private static final Component DEFAULT_NAME = Component.literal("@");
    private long lastExecution = -1L;
    private boolean updateLastExecution = true;
    private int successCount;
    private boolean trackOutput = true;
    @Nullable
    private Component lastOutput;
    private String command = "";
    @Nullable
    private Component f_314290_;

    public int getSuccessCount() {
        return this.successCount;
    }

    public void setSuccessCount(int pSuccessCount) {
        this.successCount = pSuccessCount;
    }

    public Component getLastOutput() {
        return this.lastOutput == null ? CommonComponents.EMPTY : this.lastOutput;
    }

    public CompoundTag save(CompoundTag pCompound, HolderLookup.Provider p_329299_) {
        pCompound.putString("Command", this.command);
        pCompound.putInt("SuccessCount", this.successCount);
        if (this.f_314290_ != null) {
            pCompound.putString("CustomName", Component.Serializer.toJson(this.f_314290_, p_329299_));
        }

        pCompound.putBoolean("TrackOutput", this.trackOutput);
        if (this.lastOutput != null && this.trackOutput) {
            pCompound.putString("LastOutput", Component.Serializer.toJson(this.lastOutput, p_329299_));
        }

        pCompound.putBoolean("UpdateLastExecution", this.updateLastExecution);
        if (this.updateLastExecution && this.lastExecution > 0L) {
            pCompound.putLong("LastExecution", this.lastExecution);
        }

        return pCompound;
    }

    public void load(CompoundTag pNbt, HolderLookup.Provider p_329410_) {
        this.command = pNbt.getString("Command");
        this.successCount = pNbt.getInt("SuccessCount");
        if (pNbt.contains("CustomName", 8)) {
            this.m_321484_(BlockEntity.m_336414_(pNbt.getString("CustomName"), p_329410_));
        } else {
            this.m_321484_(null);
        }

        if (pNbt.contains("TrackOutput", 1)) {
            this.trackOutput = pNbt.getBoolean("TrackOutput");
        }

        if (pNbt.contains("LastOutput", 8) && this.trackOutput) {
            try {
                this.lastOutput = Component.Serializer.fromJson(pNbt.getString("LastOutput"), p_329410_);
            } catch (Throwable throwable) {
                this.lastOutput = Component.literal(throwable.getMessage());
            }
        } else {
            this.lastOutput = null;
        }

        if (pNbt.contains("UpdateLastExecution")) {
            this.updateLastExecution = pNbt.getBoolean("UpdateLastExecution");
        }

        if (this.updateLastExecution && pNbt.contains("LastExecution")) {
            this.lastExecution = pNbt.getLong("LastExecution");
        } else {
            this.lastExecution = -1L;
        }
    }

    public void setCommand(String pCommand) {
        this.command = pCommand;
        this.successCount = 0;
    }

    public String getCommand() {
        return this.command;
    }

    public boolean performCommand(Level pLevel) {
        if (pLevel.isClientSide || pLevel.getGameTime() == this.lastExecution) {
            return false;
        } else if ("Searge".equalsIgnoreCase(this.command)) {
            this.lastOutput = Component.literal("#itzlipofutzli");
            this.successCount = 1;
            return true;
        } else {
            this.successCount = 0;
            MinecraftServer minecraftserver = this.getLevel().getServer();
            if (minecraftserver.isCommandBlockEnabled() && !StringUtil.isNullOrEmpty(this.command)) {
                try {
                    this.lastOutput = null;
                    CommandSourceStack commandsourcestack = this.createCommandSourceStack().withCallback((p_45418_, p_45419_) -> {
                        if (p_45418_) {
                            this.successCount++;
                        }
                    });
                    minecraftserver.getCommands().performPrefixedCommand(commandsourcestack, this.command);
                } catch (Throwable throwable) {
                    CrashReport crashreport = CrashReport.forThrowable(throwable, "Executing command block");
                    CrashReportCategory crashreportcategory = crashreport.addCategory("Command to be executed");
                    crashreportcategory.setDetail("Command", this::getCommand);
                    crashreportcategory.setDetail("Name", () -> this.getName().getString());
                    throw new ReportedException(crashreport);
                }
            }

            if (this.updateLastExecution) {
                this.lastExecution = pLevel.getGameTime();
            } else {
                this.lastExecution = -1L;
            }

            return true;
        }
    }

    public Component getName() {
        return this.f_314290_ != null ? this.f_314290_ : DEFAULT_NAME;
    }

    @Nullable
    public Component m_323895_() {
        return this.f_314290_;
    }

    public void m_321484_(@Nullable Component p_327944_) {
        this.f_314290_ = p_327944_;
    }

    @Override
    public void sendSystemMessage(Component pComponent) {
        if (this.trackOutput) {
            this.lastOutput = Component.literal("[" + TIME_FORMAT.format(new Date()) + "] ").append(pComponent);
            this.onUpdated();
        }
    }

    public abstract ServerLevel getLevel();

    public abstract void onUpdated();

    public void setLastOutput(@Nullable Component pLastOutputMessage) {
        this.lastOutput = pLastOutputMessage;
    }

    public void setTrackOutput(boolean pShouldTrackOutput) {
        this.trackOutput = pShouldTrackOutput;
    }

    public boolean isTrackOutput() {
        return this.trackOutput;
    }

    public InteractionResult usedBy(Player pPlayer) {
        if (!pPlayer.canUseGameMasterBlocks()) {
            return InteractionResult.PASS;
        } else {
            if (pPlayer.getCommandSenderWorld().isClientSide) {
                pPlayer.openMinecartCommandBlock(this);
            }

            return InteractionResult.sidedSuccess(pPlayer.level().isClientSide);
        }
    }

    public abstract Vec3 getPosition();

    public abstract CommandSourceStack createCommandSourceStack();

    @Override
    public boolean acceptsSuccess() {
        return this.getLevel().getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK) && this.trackOutput;
    }

    @Override
    public boolean acceptsFailure() {
        return this.trackOutput;
    }

    @Override
    public boolean shouldInformAdmins() {
        return this.getLevel().getGameRules().getBoolean(GameRules.RULE_COMMANDBLOCKOUTPUT);
    }

    public abstract boolean isValid();
}