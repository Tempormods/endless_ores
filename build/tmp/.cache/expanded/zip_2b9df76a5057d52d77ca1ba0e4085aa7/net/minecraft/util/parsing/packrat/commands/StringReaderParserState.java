package net.minecraft.util.parsing.packrat.commands;

import com.mojang.brigadier.StringReader;
import net.minecraft.util.parsing.packrat.Dictionary;
import net.minecraft.util.parsing.packrat.ErrorCollector;
import net.minecraft.util.parsing.packrat.ParseState;

public class StringReaderParserState extends ParseState<StringReader> {
    private final StringReader f_315929_;

    public StringReaderParserState(Dictionary<StringReader> p_328307_, ErrorCollector<StringReader> p_327936_, StringReader p_332446_) {
        super(p_328307_, p_327936_);
        this.f_315929_ = p_332446_;
    }

    public StringReader m_322193_() {
        return this.f_315929_;
    }

    @Override
    public int m_320129_() {
        return this.f_315929_.getCursor();
    }

    @Override
    public void m_321642_(int p_331895_) {
        this.f_315929_.setCursor(p_331895_);
    }
}