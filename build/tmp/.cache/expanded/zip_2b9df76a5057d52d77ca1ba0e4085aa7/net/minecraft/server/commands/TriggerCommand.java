package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ObjectiveArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ReadOnlyScoreInfo;
import net.minecraft.world.scores.ScoreAccess;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class TriggerCommand {
    private static final SimpleCommandExceptionType ERROR_NOT_PRIMED = new SimpleCommandExceptionType(Component.translatable("commands.trigger.failed.unprimed"));
    private static final SimpleCommandExceptionType ERROR_INVALID_OBJECTIVE = new SimpleCommandExceptionType(Component.translatable("commands.trigger.failed.invalid"));

    public static void register(CommandDispatcher<CommandSourceStack> pDispatcher) {
        pDispatcher.register(
            Commands.literal("trigger")
                .then(
                    Commands.argument("objective", ObjectiveArgument.objective())
                        .suggests((p_139146_, p_139147_) -> suggestObjectives(p_139146_.getSource(), p_139147_))
                        .executes(
                            p_308912_ -> simpleTrigger(p_308912_.getSource(), p_308912_.getSource().getPlayerOrException(), ObjectiveArgument.getObjective(p_308912_, "objective"))
                        )
                        .then(
                            Commands.literal("add")
                                .then(
                                    Commands.argument("value", IntegerArgumentType.integer())
                                        .executes(
                                            p_308911_ -> addValue(
                                                    p_308911_.getSource(),
                                                    p_308911_.getSource().getPlayerOrException(),
                                                    ObjectiveArgument.getObjective(p_308911_, "objective"),
                                                    IntegerArgumentType.getInteger(p_308911_, "value")
                                                )
                                        )
                                )
                        )
                        .then(
                            Commands.literal("set")
                                .then(
                                    Commands.argument("value", IntegerArgumentType.integer())
                                        .executes(
                                            p_308913_ -> setValue(
                                                    p_308913_.getSource(),
                                                    p_308913_.getSource().getPlayerOrException(),
                                                    ObjectiveArgument.getObjective(p_308913_, "objective"),
                                                    IntegerArgumentType.getInteger(p_308913_, "value")
                                                )
                                        )
                                )
                        )
                )
        );
    }

    public static CompletableFuture<Suggestions> suggestObjectives(CommandSourceStack pSource, SuggestionsBuilder pBuilder) {
        ScoreHolder scoreholder = pSource.getEntity();
        List<String> list = Lists.newArrayList();
        if (scoreholder != null) {
            Scoreboard scoreboard = pSource.getServer().getScoreboard();

            for (Objective objective : scoreboard.getObjectives()) {
                if (objective.getCriteria() == ObjectiveCriteria.TRIGGER) {
                    ReadOnlyScoreInfo readonlyscoreinfo = scoreboard.m_305759_(scoreholder, objective);
                    if (readonlyscoreinfo != null && !readonlyscoreinfo.isLocked()) {
                        list.add(objective.getName());
                    }
                }
            }
        }

        return SharedSuggestionProvider.suggest(list, pBuilder);
    }

    private static int addValue(CommandSourceStack pSource, ServerPlayer p_310899_, Objective p_310001_, int pAmount) throws CommandSyntaxException {
        ScoreAccess scoreaccess = getScore(pSource.getServer().getScoreboard(), p_310899_, p_310001_);
        int i = scoreaccess.m_305196_(pAmount);
        pSource.sendSuccess(() -> Component.translatable("commands.trigger.add.success", p_310001_.getFormattedDisplayName(), pAmount), true);
        return i;
    }

    private static int setValue(CommandSourceStack pSource, ServerPlayer p_312734_, Objective p_309575_, int pValue) throws CommandSyntaxException {
        ScoreAccess scoreaccess = getScore(pSource.getServer().getScoreboard(), p_312734_, p_309575_);
        scoreaccess.m_305183_(pValue);
        pSource.sendSuccess(() -> Component.translatable("commands.trigger.set.success", p_309575_.getFormattedDisplayName(), pValue), true);
        return pValue;
    }

    private static int simpleTrigger(CommandSourceStack pSource, ServerPlayer p_310805_, Objective p_313189_) throws CommandSyntaxException {
        ScoreAccess scoreaccess = getScore(pSource.getServer().getScoreboard(), p_310805_, p_313189_);
        int i = scoreaccess.m_305196_(1);
        pSource.sendSuccess(() -> Component.translatable("commands.trigger.simple.success", p_313189_.getFormattedDisplayName()), true);
        return i;
    }

    private static ScoreAccess getScore(Scoreboard p_309433_, ScoreHolder p_310288_, Objective pObjective) throws CommandSyntaxException {
        if (pObjective.getCriteria() != ObjectiveCriteria.TRIGGER) {
            throw ERROR_INVALID_OBJECTIVE.create();
        } else {
            ReadOnlyScoreInfo readonlyscoreinfo = p_309433_.m_305759_(p_310288_, pObjective);
            if (readonlyscoreinfo != null && !readonlyscoreinfo.isLocked()) {
                ScoreAccess scoreaccess = p_309433_.getOrCreatePlayerScore(p_310288_, pObjective);
                scoreaccess.m_305263_();
                return scoreaccess;
            } else {
                throw ERROR_NOT_PRIMED.create();
            }
        }
    }
}