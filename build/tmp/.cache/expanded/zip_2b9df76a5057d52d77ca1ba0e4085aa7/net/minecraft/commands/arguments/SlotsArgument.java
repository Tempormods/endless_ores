package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.ParserUtils;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.SlotRange;
import net.minecraft.world.inventory.SlotRanges;

public class SlotsArgument implements ArgumentType<SlotRange> {
    private static final Collection<String> f_315205_ = List.of("container.*", "container.5", "weapon");
    private static final DynamicCommandExceptionType f_314880_ = new DynamicCommandExceptionType(p_331324_ -> Component.m_307043_("slot.unknown", p_331324_));

    public static SlotsArgument m_324401_() {
        return new SlotsArgument();
    }

    public static SlotRange m_324723_(CommandContext<CommandSourceStack> p_333218_, String p_328819_) {
        return p_333218_.getArgument(p_328819_, SlotRange.class);
    }

    public SlotRange parse(StringReader p_329039_) throws CommandSyntaxException {
        String s = ParserUtils.m_320983_(p_329039_, p_329908_ -> p_329908_ != ' ');
        SlotRange slotrange = SlotRanges.m_323685_(s);
        if (slotrange == null) {
            throw f_314880_.createWithContext(p_329039_, s);
        } else {
            return slotrange;
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_329445_, SuggestionsBuilder p_329636_) {
        return SharedSuggestionProvider.suggest(SlotRanges.m_321260_(), p_329636_);
    }

    @Override
    public Collection<String> getExamples() {
        return f_315205_;
    }
}