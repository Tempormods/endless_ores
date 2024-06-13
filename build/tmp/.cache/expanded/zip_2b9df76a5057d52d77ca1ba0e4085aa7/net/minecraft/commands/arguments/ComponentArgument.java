package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.ParserUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;

public class ComponentArgument implements ArgumentType<Component> {
    private static final Collection<String> EXAMPLES = Arrays.asList("\"hello world\"", "\"\"", "\"{\"text\":\"hello world\"}", "[\"\"]");
    public static final DynamicCommandExceptionType ERROR_INVALID_JSON = new DynamicCommandExceptionType(
        p_308346_ -> Component.m_307043_("argument.component.invalid", p_308346_)
    );
    private final HolderLookup.Provider f_314990_;

    private ComponentArgument(HolderLookup.Provider p_328965_) {
        this.f_314990_ = p_328965_;
    }

    public static Component getComponent(CommandContext<CommandSourceStack> pContext, String pName) {
        return pContext.getArgument(pName, Component.class);
    }

    public static ComponentArgument textComponent(CommandBuildContext p_330669_) {
        return new ComponentArgument(p_330669_);
    }

    public Component parse(StringReader pReader) throws CommandSyntaxException {
        try {
            return ParserUtils.m_305320_(this.f_314990_, pReader, ComponentSerialization.f_303288_);
        } catch (Exception exception) {
            String s = exception.getCause() != null ? exception.getCause().getMessage() : exception.getMessage();
            throw ERROR_INVALID_JSON.createWithContext(pReader, s);
        }
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}