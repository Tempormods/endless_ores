package net.minecraft.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.commands.execution.TraceCallbacks;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.OutgoingChatMessage;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.TaskChainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class CommandSourceStack implements ExecutionCommandSource<CommandSourceStack>, SharedSuggestionProvider, net.minecraftforge.common.extensions.IForgeCommandSourceStack {
    public static final SimpleCommandExceptionType ERROR_NOT_PLAYER = new SimpleCommandExceptionType(Component.translatable("permissions.requires.player"));
    public static final SimpleCommandExceptionType ERROR_NOT_ENTITY = new SimpleCommandExceptionType(Component.translatable("permissions.requires.entity"));
    public final CommandSource source;
    private final Vec3 worldPosition;
    private final ServerLevel level;
    private final int permissionLevel;
    private final String textName;
    private final Component displayName;
    private final MinecraftServer server;
    private final boolean silent;
    @Nullable
    private final Entity entity;
    private final CommandResultCallback f_302754_;
    private final EntityAnchorArgument.Anchor anchor;
    private final Vec2 rotation;
    private final CommandSigningContext signingContext;
    private final TaskChainer chatMessageChainer;

    public CommandSourceStack(
        CommandSource pSource,
        Vec3 pWorldPosition,
        Vec2 pRotation,
        ServerLevel pLevel,
        int pPermissionLevel,
        String pTextName,
        Component pDisplayName,
        MinecraftServer pServer,
        @Nullable Entity pEntity
    ) {
        this(
            pSource,
            pWorldPosition,
            pRotation,
            pLevel,
            pPermissionLevel,
            pTextName,
            pDisplayName,
            pServer,
            pEntity,
            false,
            CommandResultCallback.f_302577_,
            EntityAnchorArgument.Anchor.FEET,
            CommandSigningContext.ANONYMOUS,
            TaskChainer.immediate(pServer)
        );
    }

    protected CommandSourceStack(
        CommandSource pSource,
        Vec3 pWorldPosition,
        Vec2 pRotation,
        ServerLevel pLevel,
        int pPermissionLevel,
        String pTextName,
        Component pDisplayName,
        MinecraftServer pServer,
        @Nullable Entity pEntity,
        boolean pSilent,
        CommandResultCallback p_310300_,
        EntityAnchorArgument.Anchor pAnchor,
        CommandSigningContext pSigningContext,
        TaskChainer pChatMessageContainer
    ) {
        this.source = pSource;
        this.worldPosition = pWorldPosition;
        this.level = pLevel;
        this.silent = pSilent;
        this.entity = pEntity;
        this.permissionLevel = pPermissionLevel;
        this.textName = pTextName;
        this.displayName = pDisplayName;
        this.server = pServer;
        this.f_302754_ = p_310300_;
        this.anchor = pAnchor;
        this.rotation = pRotation;
        this.signingContext = pSigningContext;
        this.chatMessageChainer = pChatMessageContainer;
    }

    public CommandSourceStack withSource(CommandSource pSource) {
        return this.source == pSource
            ? this
            : new CommandSourceStack(
                pSource,
                this.worldPosition,
                this.rotation,
                this.level,
                this.permissionLevel,
                this.textName,
                this.displayName,
                this.server,
                this.entity,
                this.silent,
                this.f_302754_,
                this.anchor,
                this.signingContext,
                this.chatMessageChainer
            );
    }

    public CommandSourceStack withEntity(Entity pEntity) {
        return this.entity == pEntity
            ? this
            : new CommandSourceStack(
                this.source,
                this.worldPosition,
                this.rotation,
                this.level,
                this.permissionLevel,
                pEntity.getName().getString(),
                pEntity.getDisplayName(),
                this.server,
                pEntity,
                this.silent,
                this.f_302754_,
                this.anchor,
                this.signingContext,
                this.chatMessageChainer
            );
    }

    public CommandSourceStack withPosition(Vec3 pPos) {
        return this.worldPosition.equals(pPos)
            ? this
            : new CommandSourceStack(
                this.source,
                pPos,
                this.rotation,
                this.level,
                this.permissionLevel,
                this.textName,
                this.displayName,
                this.server,
                this.entity,
                this.silent,
                this.f_302754_,
                this.anchor,
                this.signingContext,
                this.chatMessageChainer
            );
    }

    public CommandSourceStack withRotation(Vec2 pRotation) {
        return this.rotation.equals(pRotation)
            ? this
            : new CommandSourceStack(
                this.source,
                this.worldPosition,
                pRotation,
                this.level,
                this.permissionLevel,
                this.textName,
                this.displayName,
                this.server,
                this.entity,
                this.silent,
                this.f_302754_,
                this.anchor,
                this.signingContext,
                this.chatMessageChainer
            );
    }

    public CommandSourceStack withCallback(CommandResultCallback p_310737_) {
        return Objects.equals(this.f_302754_, p_310737_)
            ? this
            : new CommandSourceStack(
                this.source,
                this.worldPosition,
                this.rotation,
                this.level,
                this.permissionLevel,
                this.textName,
                this.displayName,
                this.server,
                this.entity,
                this.silent,
                p_310737_,
                this.anchor,
                this.signingContext,
                this.chatMessageChainer
            );
    }

    public CommandSourceStack withCallback(CommandResultCallback p_311586_, BinaryOperator<CommandResultCallback> pResultConsumerSelector) {
        CommandResultCallback commandresultcallback = pResultConsumerSelector.apply(this.f_302754_, p_311586_);
        return this.withCallback(commandresultcallback);
    }

    public CommandSourceStack withSuppressedOutput() {
        return !this.silent && !this.source.alwaysAccepts()
            ? new CommandSourceStack(
                this.source,
                this.worldPosition,
                this.rotation,
                this.level,
                this.permissionLevel,
                this.textName,
                this.displayName,
                this.server,
                this.entity,
                true,
                this.f_302754_,
                this.anchor,
                this.signingContext,
                this.chatMessageChainer
            )
            : this;
    }

    public CommandSourceStack withPermission(int pPermissionLevel) {
        return pPermissionLevel == this.permissionLevel
            ? this
            : new CommandSourceStack(
                this.source,
                this.worldPosition,
                this.rotation,
                this.level,
                pPermissionLevel,
                this.textName,
                this.displayName,
                this.server,
                this.entity,
                this.silent,
                this.f_302754_,
                this.anchor,
                this.signingContext,
                this.chatMessageChainer
            );
    }

    public CommandSourceStack withMaximumPermission(int pPermissionLevel) {
        return pPermissionLevel <= this.permissionLevel
            ? this
            : new CommandSourceStack(
                this.source,
                this.worldPosition,
                this.rotation,
                this.level,
                pPermissionLevel,
                this.textName,
                this.displayName,
                this.server,
                this.entity,
                this.silent,
                this.f_302754_,
                this.anchor,
                this.signingContext,
                this.chatMessageChainer
            );
    }

    public CommandSourceStack withAnchor(EntityAnchorArgument.Anchor pAnchor) {
        return pAnchor == this.anchor
            ? this
            : new CommandSourceStack(
                this.source,
                this.worldPosition,
                this.rotation,
                this.level,
                this.permissionLevel,
                this.textName,
                this.displayName,
                this.server,
                this.entity,
                this.silent,
                this.f_302754_,
                pAnchor,
                this.signingContext,
                this.chatMessageChainer
            );
    }

    public CommandSourceStack withLevel(ServerLevel pLevel) {
        if (pLevel == this.level) {
            return this;
        } else {
            double d0 = DimensionType.getTeleportationScale(this.level.dimensionType(), pLevel.dimensionType());
            Vec3 vec3 = new Vec3(this.worldPosition.x * d0, this.worldPosition.y, this.worldPosition.z * d0);
            return new CommandSourceStack(
                this.source,
                vec3,
                this.rotation,
                pLevel,
                this.permissionLevel,
                this.textName,
                this.displayName,
                this.server,
                this.entity,
                this.silent,
                this.f_302754_,
                this.anchor,
                this.signingContext,
                this.chatMessageChainer
            );
        }
    }

    public CommandSourceStack facing(Entity pEntity, EntityAnchorArgument.Anchor pAnchor) {
        return this.facing(pAnchor.apply(pEntity));
    }

    public CommandSourceStack facing(Vec3 pLookPos) {
        Vec3 vec3 = this.anchor.apply(this);
        double d0 = pLookPos.x - vec3.x;
        double d1 = pLookPos.y - vec3.y;
        double d2 = pLookPos.z - vec3.z;
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        float f = Mth.wrapDegrees((float)(-(Mth.atan2(d1, d3) * 180.0F / (float)Math.PI)));
        float f1 = Mth.wrapDegrees((float)(Mth.atan2(d2, d0) * 180.0F / (float)Math.PI) - 90.0F);
        return this.withRotation(new Vec2(f, f1));
    }

    public CommandSourceStack withSigningContext(CommandSigningContext pSigningContext, TaskChainer pChatMessageChainer) {
        return pSigningContext == this.signingContext && pChatMessageChainer == this.chatMessageChainer
            ? this
            : new CommandSourceStack(
                this.source,
                this.worldPosition,
                this.rotation,
                this.level,
                this.permissionLevel,
                this.textName,
                this.displayName,
                this.server,
                this.entity,
                this.silent,
                this.f_302754_,
                this.anchor,
                pSigningContext,
                pChatMessageChainer
            );
    }

    public Component getDisplayName() {
        return this.displayName;
    }

    public String getTextName() {
        return this.textName;
    }

    @Override
    public boolean hasPermission(int pLevel) {
        return this.permissionLevel >= pLevel;
    }

    public Vec3 getPosition() {
        return this.worldPosition;
    }

    public ServerLevel getLevel() {
        return this.level;
    }

    @Nullable
    public Entity getEntity() {
        return this.entity;
    }

    public Entity getEntityOrException() throws CommandSyntaxException {
        if (this.entity == null) {
            throw ERROR_NOT_ENTITY.create();
        } else {
            return this.entity;
        }
    }

    public ServerPlayer getPlayerOrException() throws CommandSyntaxException {
        Entity entity = this.entity;
        if (entity instanceof ServerPlayer) {
            return (ServerPlayer)entity;
        } else {
            throw ERROR_NOT_PLAYER.create();
        }
    }

    @Nullable
    public ServerPlayer getPlayer() {
        return this.entity instanceof ServerPlayer serverplayer ? serverplayer : null;
    }

    public boolean isPlayer() {
        return this.entity instanceof ServerPlayer;
    }

    public Vec2 getRotation() {
        return this.rotation;
    }

    public MinecraftServer getServer() {
        return this.server;
    }

    public EntityAnchorArgument.Anchor getAnchor() {
        return this.anchor;
    }

    public CommandSigningContext getSigningContext() {
        return this.signingContext;
    }

    public TaskChainer getChatMessageChainer() {
        return this.chatMessageChainer;
    }

    public boolean shouldFilterMessageTo(ServerPlayer pReceiver) {
        ServerPlayer serverplayer = this.getPlayer();
        return pReceiver == serverplayer ? false : serverplayer != null && serverplayer.isTextFilteringEnabled() || pReceiver.isTextFilteringEnabled();
    }

    public void sendChatMessage(OutgoingChatMessage pMessage, boolean pShouldFilter, ChatType.Bound pBoundChatType) {
        if (!this.silent) {
            ServerPlayer serverplayer = this.getPlayer();
            if (serverplayer != null) {
                serverplayer.sendChatMessage(pMessage, pShouldFilter, pBoundChatType);
            } else {
                this.source.sendSystemMessage(pBoundChatType.decorate(pMessage.content()));
            }
        }
    }

    public void sendSystemMessage(Component pMessage) {
        if (!this.silent) {
            ServerPlayer serverplayer = this.getPlayer();
            if (serverplayer != null) {
                serverplayer.sendSystemMessage(pMessage);
            } else {
                this.source.sendSystemMessage(pMessage);
            }
        }
    }

    public void sendSuccess(Supplier<Component> pMessageSupplier, boolean pAllowLogging) {
        boolean flag = this.source.acceptsSuccess() && !this.silent;
        boolean flag1 = pAllowLogging && this.source.shouldInformAdmins() && !this.silent;
        if (flag || flag1) {
            Component component = pMessageSupplier.get();
            if (flag) {
                this.source.sendSystemMessage(component);
            }

            if (flag1) {
                this.broadcastToAdmins(component);
            }
        }
    }

    private void broadcastToAdmins(Component pMessage) {
        Component component = Component.translatable("chat.type.admin", this.getDisplayName(), pMessage).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC);
        if (this.server.getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK)) {
            for (ServerPlayer serverplayer : this.server.getPlayerList().getPlayers()) {
                if (serverplayer != this.source && this.server.getPlayerList().isOp(serverplayer.getGameProfile())) {
                    serverplayer.sendSystemMessage(component);
                }
            }
        }

        if (this.source != this.server && this.server.getGameRules().getBoolean(GameRules.RULE_LOGADMINCOMMANDS)) {
            this.server.sendSystemMessage(component);
        }
    }

    public void sendFailure(Component pMessage) {
        if (this.source.acceptsFailure() && !this.silent) {
            this.source.sendSystemMessage(Component.empty().append(pMessage).withStyle(ChatFormatting.RED));
        }
    }

    @Override
    public CommandResultCallback m_304794_() {
        return this.f_302754_;
    }

    @Override
    public Collection<String> getOnlinePlayerNames() {
        return Lists.newArrayList(this.server.getPlayerNames());
    }

    @Override
    public Collection<String> getAllTeams() {
        return this.server.getScoreboard().getTeamNames();
    }

    @Override
    public Stream<ResourceLocation> getAvailableSounds() {
        return BuiltInRegistries.SOUND_EVENT.stream().map(SoundEvent::getLocation);
    }

    @Override
    public Stream<ResourceLocation> getRecipeNames() {
        return this.server.getRecipeManager().getRecipeIds();
    }

    @Override
    public CompletableFuture<Suggestions> customSuggestion(CommandContext<?> pContext) {
        return Suggestions.empty();
    }

    @Override
    public CompletableFuture<Suggestions> suggestRegistryElements(
        ResourceKey<? extends Registry<?>> pResourceKey,
        SharedSuggestionProvider.ElementSuggestionType pRegistryKey,
        SuggestionsBuilder pBuilder,
        CommandContext<?> pContext
    ) {
        return this.registryAccess().registry(pResourceKey).map(p_212328_ -> {
            this.suggestRegistryElements((Registry<?>)p_212328_, pRegistryKey, pBuilder);
            return pBuilder.buildFuture();
        }).orElseGet(Suggestions::empty);
    }

    @Override
    public Set<ResourceKey<Level>> levels() {
        return this.server.levelKeys();
    }

    @Override
    public RegistryAccess registryAccess() {
        return this.server.registryAccess();
    }

    @Override
    public FeatureFlagSet enabledFeatures() {
        return this.level.enabledFeatures();
    }

    @Override
    public CommandDispatcher<CommandSourceStack> m_305649_() {
        return this.getServer().getFunctions().getDispatcher();
    }

    @Override
    public void m_305988_(CommandExceptionType p_311431_, Message p_311914_, boolean p_312997_, @Nullable TraceCallbacks p_310681_) {
        if (p_310681_ != null) {
            p_310681_.m_180099_(p_311914_.getString());
        }

        if (!p_312997_) {
            this.sendFailure(ComponentUtils.fromMessage(p_311914_));
        }
    }

    @Override
    public boolean m_306225_() {
        return this.silent;
    }
}
