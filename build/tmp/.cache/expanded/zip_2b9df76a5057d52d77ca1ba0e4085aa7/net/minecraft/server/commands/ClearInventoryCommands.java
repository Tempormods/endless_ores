package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.item.ItemPredicateArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class ClearInventoryCommands {
    private static final DynamicCommandExceptionType ERROR_SINGLE = new DynamicCommandExceptionType(
        p_308637_ -> Component.m_307043_("clear.failed.single", p_308637_)
    );
    private static final DynamicCommandExceptionType ERROR_MULTIPLE = new DynamicCommandExceptionType(
        p_308634_ -> Component.m_307043_("clear.failed.multiple", p_308634_)
    );

    public static void register(CommandDispatcher<CommandSourceStack> pDispatcher, CommandBuildContext pContext) {
        pDispatcher.register(
            Commands.literal("clear")
                .requires(p_136704_ -> p_136704_.hasPermission(2))
                .executes(p_326228_ -> m_323323_(p_326228_.getSource(), Collections.singleton(p_326228_.getSource().getPlayerOrException()), p_180029_ -> true))
                .then(
                    Commands.argument("targets", EntityArgument.players())
                        .executes(p_326232_ -> m_323323_(p_326232_.getSource(), EntityArgument.getPlayers(p_326232_, "targets"), p_180027_ -> true))
                        .then(
                            Commands.argument("item", ItemPredicateArgument.itemPredicate(pContext))
                                .executes(
                                    p_326233_ -> m_323323_(
                                            p_326233_.getSource(),
                                            EntityArgument.getPlayers(p_326233_, "targets"),
                                            ItemPredicateArgument.getItemPredicate(p_326233_, "item")
                                        )
                                )
                                .then(
                                    Commands.argument("maxCount", IntegerArgumentType.integer(0))
                                        .executes(
                                            p_326231_ -> clearInventory(
                                                    p_326231_.getSource(),
                                                    EntityArgument.getPlayers(p_326231_, "targets"),
                                                    ItemPredicateArgument.getItemPredicate(p_326231_, "item"),
                                                    IntegerArgumentType.getInteger(p_326231_, "maxCount")
                                                )
                                        )
                                )
                        )
                )
        );
    }

    private static int m_323323_(CommandSourceStack p_333436_, Collection<ServerPlayer> p_334305_, Predicate<ItemStack> p_336088_) throws CommandSyntaxException {
        return clearInventory(p_333436_, p_334305_, p_336088_, -1);
    }

    private static int clearInventory(CommandSourceStack pSource, Collection<ServerPlayer> pTargetPlayers, Predicate<ItemStack> pItemPredicate, int pMaxCount) throws CommandSyntaxException {
        int i = 0;

        for (ServerPlayer serverplayer : pTargetPlayers) {
            i += serverplayer.getInventory().clearOrCountMatchingItems(pItemPredicate, pMaxCount, serverplayer.inventoryMenu.getCraftSlots());
            serverplayer.containerMenu.broadcastChanges();
            serverplayer.inventoryMenu.slotsChanged(serverplayer.getInventory());
        }

        if (i == 0) {
            if (pTargetPlayers.size() == 1) {
                throw ERROR_SINGLE.create(pTargetPlayers.iterator().next().getName());
            } else {
                throw ERROR_MULTIPLE.create(pTargetPlayers.size());
            }
        } else {
            int j = i;
            if (pMaxCount == 0) {
                if (pTargetPlayers.size() == 1) {
                    pSource.sendSuccess(() -> Component.translatable("commands.clear.test.single", j, pTargetPlayers.iterator().next().getDisplayName()), true);
                } else {
                    pSource.sendSuccess(() -> Component.translatable("commands.clear.test.multiple", j, pTargetPlayers.size()), true);
                }
            } else if (pTargetPlayers.size() == 1) {
                pSource.sendSuccess(() -> Component.translatable("commands.clear.success.single", j, pTargetPlayers.iterator().next().getDisplayName()), true);
            } else {
                pSource.sendSuccess(() -> Component.translatable("commands.clear.success.multiple", j, pTargetPlayers.size()), true);
            }

            return i;
        }
    }
}