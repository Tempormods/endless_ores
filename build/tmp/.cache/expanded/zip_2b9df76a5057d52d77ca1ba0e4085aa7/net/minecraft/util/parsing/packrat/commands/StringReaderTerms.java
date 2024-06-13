package net.minecraft.util.parsing.packrat.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.stream.Stream;
import net.minecraft.util.parsing.packrat.Control;
import net.minecraft.util.parsing.packrat.ParseState;
import net.minecraft.util.parsing.packrat.Scope;
import net.minecraft.util.parsing.packrat.Term;

public interface StringReaderTerms {
    static Term<StringReader> m_323264_(String p_327924_) {
        return new StringReaderTerms.TerminalWord(p_327924_);
    }

    static Term<StringReader> m_321908_(char p_329750_) {
        return new StringReaderTerms.TerminalCharacter(p_329750_);
    }

    public static record TerminalCharacter(char f_314795_) implements Term<StringReader> {
        @Override
        public boolean m_319964_(ParseState<StringReader> p_330727_, Scope p_335740_, Control p_331061_) {
            p_330727_.m_322193_().skipWhitespace();
            int i = p_330727_.m_320129_();
            if (p_330727_.m_322193_().canRead() && p_330727_.m_322193_().read() == this.f_314795_) {
                return true;
            } else {
                p_330727_.m_323339_()
                    .m_322006_(
                        i,
                        p_332558_ -> Stream.of(String.valueOf(this.f_314795_)),
                        CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect().create(this.f_314795_)
                    );
                return false;
            }
        }
    }

    public static record TerminalWord(String f_315171_) implements Term<StringReader> {
        @Override
        public boolean m_319964_(ParseState<StringReader> p_333566_, Scope p_332362_, Control p_328812_) {
            p_333566_.m_322193_().skipWhitespace();
            int i = p_333566_.m_320129_();
            String s = p_333566_.m_322193_().readUnquotedString();
            if (!s.equals(this.f_315171_)) {
                p_333566_.m_323339_()
                    .m_322006_(i, p_331163_ -> Stream.of(this.f_315171_), CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect().create(this.f_315171_));
                return false;
            } else {
                return true;
            }
        }
    }
}