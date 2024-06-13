package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameType;

public class SpectateCommand {
    private static final SimpleCommandExceptionType ERROR_SELF = new SimpleCommandExceptionType(Component.translatable("commands.spectate.self"));
    private static final DynamicCommandExceptionType ERROR_NOT_SPECTATOR = new DynamicCommandExceptionType(
        p_308882_ -> Component.m_307043_("commands.spectate.not_spectator", p_308882_)
    );

    public static void register(CommandDispatcher<CommandSourceStack> pDispatcher) {
        pDispatcher.register(
            Commands.literal("spectate")
                .requires(p_138682_ -> p_138682_.hasPermission(2))
                .executes(p_138692_ -> spectate(p_138692_.getSource(), null, p_138692_.getSource().getPlayerOrException()))
                .then(
                    Commands.argument("target", EntityArgument.entity())
                        .executes(p_138690_ -> spectate(p_138690_.getSource(), EntityArgument.getEntity(p_138690_, "target"), p_138690_.getSource().getPlayerOrException()))
                        .then(
                            Commands.argument("player", EntityArgument.player())
                                .executes(
                                    p_138680_ -> spectate(
                                            p_138680_.getSource(), EntityArgument.getEntity(p_138680_, "target"), EntityArgument.getPlayer(p_138680_, "player")
                                        )
                                )
                        )
                )
        );
    }

    private static int spectate(CommandSourceStack pSource, @Nullable Entity pTarget, ServerPlayer pPlayer) throws CommandSyntaxException {
        if (pPlayer == pTarget) {
            throw ERROR_SELF.create();
        } else if (pPlayer.gameMode.getGameModeForPlayer() != GameType.SPECTATOR) {
            throw ERROR_NOT_SPECTATOR.create(pPlayer.getDisplayName());
        } else {
            pPlayer.setCamera(pTarget);
            if (pTarget != null) {
                pSource.sendSuccess(() -> Component.translatable("commands.spectate.success.started", pTarget.getDisplayName()), false);
            } else {
                pSource.sendSuccess(() -> Component.translatable("commands.spectate.success.stopped"), false);
            }

            return 1;
        }
    }
}