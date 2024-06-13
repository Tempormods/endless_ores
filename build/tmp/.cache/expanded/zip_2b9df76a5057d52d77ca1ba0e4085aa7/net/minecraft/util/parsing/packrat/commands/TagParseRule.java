package net.minecraft.util.parsing.packrat.commands;

import com.mojang.brigadier.StringReader;
import java.util.Optional;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.util.parsing.packrat.ParseState;
import net.minecraft.util.parsing.packrat.Rule;

public class TagParseRule implements Rule<StringReader, Tag> {
    public static final Rule<StringReader, Tag> f_315850_ = new TagParseRule();

    private TagParseRule() {
    }

    @Override
    public Optional<Tag> m_319437_(ParseState<StringReader> p_334310_) {
        p_334310_.m_322193_().skipWhitespace();
        int i = p_334310_.m_320129_();

        try {
            return Optional.of(new TagParser(p_334310_.m_322193_()).readValue());
        } catch (Exception exception) {
            p_334310_.m_323339_().m_323756_(i, exception);
            return Optional.empty();
        }
    }
}