package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.TimeArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.ServerTickRateManager;
import net.minecraft.util.TimeUtil;

public class TickCommand {
    private static final float f_303762_ = 10000.0F;
    private static final String f_303511_ = String.valueOf(20);

    public static void m_305933_(CommandDispatcher<CommandSourceStack> p_309698_) {
        p_309698_.register(
            Commands.literal("tick")
                .requires(p_313254_ -> p_313254_.hasPermission(3))
                .then(Commands.literal("query").executes(p_310022_ -> m_304871_(p_310022_.getSource())))
                .then(
                    Commands.literal("rate")
                        .then(
                            Commands.argument("rate", FloatArgumentType.floatArg(1.0F, 10000.0F))
                                .suggests((p_311314_, p_311289_) -> SharedSuggestionProvider.suggest(new String[]{f_303511_}, p_311289_))
                                .executes(p_312329_ -> m_305228_(p_312329_.getSource(), FloatArgumentType.getFloat(p_312329_, "rate")))
                        )
                )
                .then(
                    Commands.literal("step")
                        .executes(p_311036_ -> m_306872_(p_311036_.getSource(), 1))
                        .then(Commands.literal("stop").executes(p_309944_ -> m_306432_(p_309944_.getSource())))
                        .then(
                            Commands.argument("time", TimeArgument.time(1))
                                .suggests((p_313203_, p_312907_) -> SharedSuggestionProvider.suggest(new String[]{"1t", "1s"}, p_312907_))
                                .executes(p_312113_ -> m_306872_(p_312113_.getSource(), IntegerArgumentType.getInteger(p_312113_, "time")))
                        )
                )
                .then(
                    Commands.literal("sprint")
                        .then(Commands.literal("stop").executes(p_311524_ -> m_305934_(p_311524_.getSource())))
                        .then(
                            Commands.argument("time", TimeArgument.time(1))
                                .suggests((p_311140_, p_312761_) -> SharedSuggestionProvider.suggest(new String[]{"60s", "1d", "3d"}, p_312761_))
                                .executes(p_311082_ -> m_306741_(p_311082_.getSource(), IntegerArgumentType.getInteger(p_311082_, "time")))
                        )
                )
                .then(Commands.literal("unfreeze").executes(p_309501_ -> m_307855_(p_309501_.getSource(), false)))
                .then(Commands.literal("freeze").executes(p_312020_ -> m_307855_(p_312020_.getSource(), true)))
        );
    }

    private static String m_307814_(long p_312994_) {
        return String.format("%.1f", (float)p_312994_ / (float)TimeUtil.NANOSECONDS_PER_MILLISECOND);
    }

    private static int m_305228_(CommandSourceStack p_311838_, float p_312705_) {
        ServerTickRateManager servertickratemanager = p_311838_.getServer().m_306290_();
        servertickratemanager.m_307254_(p_312705_);
        String s = String.format("%.1f", p_312705_);
        p_311838_.sendSuccess(() -> Component.translatable("commands.tick.rate.success", s), true);
        return (int)p_312705_;
    }

    private static int m_304871_(CommandSourceStack p_310546_) {
        ServerTickRateManager servertickratemanager = p_310546_.getServer().m_306290_();
        String s = m_307814_(p_310546_.getServer().m_304840_());
        float f = servertickratemanager.m_306179_();
        String s1 = String.format("%.1f", f);
        if (servertickratemanager.m_306078_()) {
            p_310546_.sendSuccess(() -> Component.translatable("commands.tick.status.sprinting"), false);
            p_310546_.sendSuccess(() -> Component.translatable("commands.tick.query.rate.sprinting", s1, s), false);
        } else {
            if (servertickratemanager.m_306363_()) {
                p_310546_.sendSuccess(() -> Component.translatable("commands.tick.status.frozen"), false);
            } else if (servertickratemanager.m_307289_() < p_310546_.getServer().m_304840_()) {
                p_310546_.sendSuccess(() -> Component.translatable("commands.tick.status.lagging"), false);
            } else {
                p_310546_.sendSuccess(() -> Component.translatable("commands.tick.status.running"), false);
            }

            String s2 = m_307814_(servertickratemanager.m_307289_());
            p_310546_.sendSuccess(() -> Component.translatable("commands.tick.query.rate.running", s1, s, s2), false);
        }

        long[] along = Arrays.copyOf(p_310546_.getServer().m_307378_(), p_310546_.getServer().m_307378_().length);
        Arrays.sort(along);
        String s3 = m_307814_(along[along.length / 2]);
        String s4 = m_307814_(along[(int)((double)along.length * 0.95)]);
        String s5 = m_307814_(along[(int)((double)along.length * 0.99)]);
        p_310546_.sendSuccess(() -> Component.translatable("commands.tick.query.percentiles", s3, s4, s5, along.length), false);
        return (int)f;
    }

    private static int m_306741_(CommandSourceStack p_311527_, int p_312312_) {
        boolean flag = p_311527_.getServer().m_306290_().m_305001_(p_312312_);
        if (flag) {
            p_311527_.sendSuccess(() -> Component.translatable("commands.tick.sprint.stop.success"), true);
        }

        p_311527_.sendSuccess(() -> Component.translatable("commands.tick.status.sprinting"), true);
        return 1;
    }

    private static int m_307855_(CommandSourceStack p_309500_, boolean p_312715_) {
        ServerTickRateManager servertickratemanager = p_309500_.getServer().m_306290_();
        if (p_312715_) {
            if (servertickratemanager.m_306078_()) {
                servertickratemanager.m_306594_();
            }

            if (servertickratemanager.m_307006_()) {
                servertickratemanager.m_307971_();
            }
        }

        servertickratemanager.m_306419_(p_312715_);
        if (p_312715_) {
            p_309500_.sendSuccess(() -> Component.translatable("commands.tick.status.frozen"), true);
        } else {
            p_309500_.sendSuccess(() -> Component.translatable("commands.tick.status.running"), true);
        }

        return p_312715_ ? 1 : 0;
    }

    private static int m_306872_(CommandSourceStack p_312155_, int p_311495_) {
        ServerTickRateManager servertickratemanager = p_312155_.getServer().m_306290_();
        boolean flag = servertickratemanager.m_305215_(p_311495_);
        if (flag) {
            p_312155_.sendSuccess(() -> Component.translatable("commands.tick.step.success", p_311495_), true);
        } else {
            p_312155_.sendFailure(Component.translatable("commands.tick.step.fail"));
        }

        return 1;
    }

    private static int m_306432_(CommandSourceStack p_310383_) {
        ServerTickRateManager servertickratemanager = p_310383_.getServer().m_306290_();
        boolean flag = servertickratemanager.m_307971_();
        if (flag) {
            p_310383_.sendSuccess(() -> Component.translatable("commands.tick.step.stop.success"), true);
            return 1;
        } else {
            p_310383_.sendFailure(Component.translatable("commands.tick.step.stop.fail"));
            return 0;
        }
    }

    private static int m_305934_(CommandSourceStack p_312590_) {
        ServerTickRateManager servertickratemanager = p_312590_.getServer().m_306290_();
        boolean flag = servertickratemanager.m_306594_();
        if (flag) {
            p_312590_.sendSuccess(() -> Component.translatable("commands.tick.sprint.stop.success"), true);
            return 1;
        } else {
            p_312590_.sendFailure(Component.translatable("commands.tick.sprint.stop.fail"));
            return 0;
        }
    }
}