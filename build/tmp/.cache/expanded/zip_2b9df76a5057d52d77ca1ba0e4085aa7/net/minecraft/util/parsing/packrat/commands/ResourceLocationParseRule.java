package net.minecraft.util.parsing.packrat.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.parsing.packrat.ParseState;
import net.minecraft.util.parsing.packrat.Rule;

public class ResourceLocationParseRule implements Rule<StringReader, ResourceLocation> {
    public static final Rule<StringReader, ResourceLocation> f_315313_ = new ResourceLocationParseRule();

    private ResourceLocationParseRule() {
    }

    @Override
    public Optional<ResourceLocation> m_319437_(ParseState<StringReader> p_335357_) {
        p_335357_.m_322193_().skipWhitespace();

        try {
            return Optional.of(ResourceLocation.m_320588_(p_335357_.m_322193_()));
        } catch (CommandSyntaxException commandsyntaxexception) {
            return Optional.empty();
        }
    }
}