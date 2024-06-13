package net.minecraft.util.parsing.packrat;

import java.util.stream.Stream;

public interface SuggestionSupplier<S> {
    Stream<String> m_318855_(ParseState<S> p_335189_);

    static <S> SuggestionSupplier<S> m_319654_() {
        return p_331625_ -> Stream.empty();
    }
}