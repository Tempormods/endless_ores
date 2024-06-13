package net.minecraft.util.parsing.packrat.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.util.parsing.packrat.Atom;
import net.minecraft.util.parsing.packrat.Dictionary;
import net.minecraft.util.parsing.packrat.ErrorCollector;
import net.minecraft.util.parsing.packrat.ErrorEntry;
import net.minecraft.util.parsing.packrat.ParseState;

public record Grammar<T>(Dictionary<StringReader> f_317156_, Atom<T> f_316874_) {
    public Optional<T> m_322930_(ParseState<StringReader> p_333096_) {
        return p_333096_.m_319664_(this.f_316874_);
    }

    public T m_320023_(StringReader p_333110_) throws CommandSyntaxException {
        ErrorCollector.LongestOnly<StringReader> longestonly = new ErrorCollector.LongestOnly<>();
        StringReaderParserState stringreaderparserstate = new StringReaderParserState(this.f_317156_(), longestonly, p_333110_);
        Optional<T> optional = this.m_322930_(stringreaderparserstate);
        if (optional.isPresent()) {
            return optional.get();
        } else {
            List<Exception> list = longestonly.m_321518_().stream().<Exception>mapMulti((p_331304_, p_328464_) -> {
                if (p_331304_.f_316152_() instanceof Exception exception1) {
                    p_328464_.accept(exception1);
                }
            }).toList();

            for (Exception exception : list) {
                if (exception instanceof CommandSyntaxException commandsyntaxexception) {
                    throw commandsyntaxexception;
                }
            }

            if (list.size() == 1 && list.get(0) instanceof RuntimeException runtimeexception) {
                throw runtimeexception;
            } else {
                throw new IllegalStateException(
                    "Failed to parse: " + longestonly.m_321518_().stream().map(ErrorEntry::toString).collect(Collectors.joining(", "))
                );
            }
        }
    }

    public CompletableFuture<Suggestions> m_320779_(SuggestionsBuilder p_327864_) {
        StringReader stringreader = new StringReader(p_327864_.getInput());
        stringreader.setCursor(p_327864_.getStart());
        ErrorCollector.LongestOnly<StringReader> longestonly = new ErrorCollector.LongestOnly<>();
        StringReaderParserState stringreaderparserstate = new StringReaderParserState(this.f_317156_(), longestonly, stringreader);
        this.m_322930_(stringreaderparserstate);
        List<ErrorEntry<StringReader>> list = longestonly.m_321518_();
        if (list.isEmpty()) {
            return p_327864_.buildFuture();
        } else {
            SuggestionsBuilder suggestionsbuilder = p_327864_.createOffset(longestonly.m_323319_());

            for (ErrorEntry<StringReader> errorentry : list) {
                if (errorentry.f_316048_() instanceof ResourceSuggestion resourcesuggestion) {
                    SharedSuggestionProvider.suggestResource(resourcesuggestion.m_319106_(), suggestionsbuilder);
                } else {
                    SharedSuggestionProvider.suggest(errorentry.f_316048_().m_318855_(stringreaderparserstate), suggestionsbuilder);
                }
            }

            return suggestionsbuilder.buildFuture();
        }
    }
}