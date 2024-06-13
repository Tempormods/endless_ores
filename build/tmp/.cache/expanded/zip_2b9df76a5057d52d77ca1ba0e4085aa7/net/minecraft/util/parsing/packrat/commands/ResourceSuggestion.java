package net.minecraft.util.parsing.packrat.commands;

import com.mojang.brigadier.StringReader;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.parsing.packrat.ParseState;
import net.minecraft.util.parsing.packrat.SuggestionSupplier;

public interface ResourceSuggestion extends SuggestionSupplier<StringReader> {
    Stream<ResourceLocation> m_319106_();

    @Override
    default Stream<String> m_318855_(ParseState<StringReader> p_334233_) {
        return this.m_319106_().map(ResourceLocation::toString);
    }
}