package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.scores.ScoreAccess;

public class OperationArgument implements ArgumentType<OperationArgument.Operation> {
    private static final Collection<String> EXAMPLES = Arrays.asList("=", ">", "<");
    private static final SimpleCommandExceptionType ERROR_INVALID_OPERATION = new SimpleCommandExceptionType(Component.translatable("arguments.operation.invalid"));
    private static final SimpleCommandExceptionType ERROR_DIVIDE_BY_ZERO = new SimpleCommandExceptionType(Component.translatable("arguments.operation.div0"));

    public static OperationArgument operation() {
        return new OperationArgument();
    }

    public static OperationArgument.Operation getOperation(CommandContext<CommandSourceStack> pContext, String pName) {
        return pContext.getArgument(pName, OperationArgument.Operation.class);
    }

    public OperationArgument.Operation parse(StringReader pReader) throws CommandSyntaxException {
        if (!pReader.canRead()) {
            throw ERROR_INVALID_OPERATION.createWithContext(pReader);
        } else {
            int i = pReader.getCursor();

            while (pReader.canRead() && pReader.peek() != ' ') {
                pReader.skip();
            }

            return getOperation(pReader.getString().substring(i, pReader.getCursor()));
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> pContext, SuggestionsBuilder pBuilder) {
        return SharedSuggestionProvider.suggest(new String[]{"=", "+=", "-=", "*=", "/=", "%=", "<", ">", "><"}, pBuilder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    private static OperationArgument.Operation getOperation(String pName) throws CommandSyntaxException {
        return (pName.equals("><") ? (p_308356_, p_308357_) -> {
            int i = p_308356_.m_306505_();
            p_308356_.m_305183_(p_308357_.m_306505_());
            p_308357_.m_305183_(i);
        } : getSimpleOperation(pName));
    }

    private static OperationArgument.SimpleOperation getSimpleOperation(String pName) throws CommandSyntaxException {
        return switch (pName) {
            case "=" -> (p_103298_, p_103299_) -> p_103299_;
            case "+=" -> Integer::sum;
            case "-=" -> (p_103292_, p_103293_) -> p_103292_ - p_103293_;
            case "*=" -> (p_103289_, p_103290_) -> p_103289_ * p_103290_;
            case "/=" -> (p_264713_, p_264714_) -> {
            if (p_264714_ == 0) {
                throw ERROR_DIVIDE_BY_ZERO.create();
            } else {
                return Mth.floorDiv(p_264713_, p_264714_);
            }
        };
            case "%=" -> (p_103271_, p_103272_) -> {
            if (p_103272_ == 0) {
                throw ERROR_DIVIDE_BY_ZERO.create();
            } else {
                return Mth.positiveModulo(p_103271_, p_103272_);
            }
        };
            case "<" -> Math::min;
            case ">" -> Math::max;
            default -> throw ERROR_INVALID_OPERATION.create();
        };
    }

    @FunctionalInterface
    public interface Operation {
        void apply(ScoreAccess p_310471_, ScoreAccess p_312233_) throws CommandSyntaxException;
    }

    @FunctionalInterface
    interface SimpleOperation extends OperationArgument.Operation {
        int apply(int pTargetScore, int pSourceScore) throws CommandSyntaxException;

        @Override
        default void apply(ScoreAccess p_311079_, ScoreAccess p_311087_) throws CommandSyntaxException {
            p_311079_.m_305183_(this.apply(p_311079_.m_306505_(), p_311087_.m_306505_()));
        }
    }
}