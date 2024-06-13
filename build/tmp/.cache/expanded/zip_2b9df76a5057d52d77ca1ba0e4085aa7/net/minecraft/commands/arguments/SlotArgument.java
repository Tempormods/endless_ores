package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.ParserUtils;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.SlotRange;
import net.minecraft.world.inventory.SlotRanges;

public class SlotArgument implements ArgumentType<Integer> {
    private static final Collection<String> EXAMPLES = Arrays.asList("container.5", "weapon");
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_SLOT = new DynamicCommandExceptionType(p_308386_ -> Component.m_307043_("slot.unknown", p_308386_));
    private static final DynamicCommandExceptionType f_315475_ = new DynamicCommandExceptionType(
        p_325606_ -> Component.m_307043_("slot.only_single_allowed", p_325606_)
    );

    public static SlotArgument slot() {
        return new SlotArgument();
    }

    public static int getSlot(CommandContext<CommandSourceStack> pContext, String pName) {
        return pContext.getArgument(pName, Integer.class);
    }

    public Integer parse(StringReader pReader) throws CommandSyntaxException {
        String s = ParserUtils.m_320983_(pReader, p_325605_ -> p_325605_ != ' ');
        SlotRange slotrange = SlotRanges.m_323685_(s);
        if (slotrange == null) {
            throw ERROR_UNKNOWN_SLOT.createWithContext(pReader, s);
        } else if (slotrange.m_319620_() != 1) {
            throw f_315475_.createWithContext(pReader, s);
        } else {
            return slotrange.m_319501_().getInt(0);
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> pContext, SuggestionsBuilder pBuilder) {
        return SharedSuggestionProvider.suggest(SlotRanges.m_322944_(), pBuilder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}