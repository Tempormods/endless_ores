package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Collection;
import java.util.List;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.ParserUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

public class StyleArgument implements ArgumentType<Style> {
    private static final Collection<String> f_303688_ = List.of("{\"bold\": true}\n");
    public static final DynamicCommandExceptionType f_302442_ = new DynamicCommandExceptionType(
        p_310381_ -> Component.m_307043_("argument.style.invalid", p_310381_)
    );
    private final HolderLookup.Provider f_315161_;

    private StyleArgument(HolderLookup.Provider p_329379_) {
        this.f_315161_ = p_329379_;
    }

    public static Style m_307431_(CommandContext<CommandSourceStack> p_311982_, String p_309702_) {
        return p_311982_.getArgument(p_309702_, Style.class);
    }

    public static StyleArgument m_305778_(CommandBuildContext p_331105_) {
        return new StyleArgument(p_331105_);
    }

    public Style parse(StringReader p_311382_) throws CommandSyntaxException {
        try {
            return ParserUtils.m_305320_(this.f_315161_, p_311382_, Style.Serializer.f_302851_);
        } catch (Exception exception) {
            String s = exception.getCause() != null ? exception.getCause().getMessage() : exception.getMessage();
            throw f_302442_.createWithContext(p_311382_, s);
        }
    }

    @Override
    public Collection<String> getExamples() {
        return f_303688_;
    }
}