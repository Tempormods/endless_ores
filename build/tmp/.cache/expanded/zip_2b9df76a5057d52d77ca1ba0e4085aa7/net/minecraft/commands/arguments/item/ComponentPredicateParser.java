package net.minecraft.commands.arguments.item;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.util.parsing.packrat.Atom;
import net.minecraft.util.parsing.packrat.Dictionary;
import net.minecraft.util.parsing.packrat.ParseState;
import net.minecraft.util.parsing.packrat.Scope;
import net.minecraft.util.parsing.packrat.Term;
import net.minecraft.util.parsing.packrat.commands.Grammar;
import net.minecraft.util.parsing.packrat.commands.ResourceLocationParseRule;
import net.minecraft.util.parsing.packrat.commands.ResourceLookupRule;
import net.minecraft.util.parsing.packrat.commands.StringReaderTerms;
import net.minecraft.util.parsing.packrat.commands.TagParseRule;

public class ComponentPredicateParser {
    public static <T, C, P> Grammar<List<T>> m_320116_(ComponentPredicateParser.Context<T, C, P> p_329972_) {
        Atom<List<T>> atom = Atom.m_320573_("top");
        Atom<Optional<T>> atom1 = Atom.m_320573_("type");
        Atom<Unit> atom2 = Atom.m_320573_("any_type");
        Atom<T> atom3 = Atom.m_320573_("element_type");
        Atom<T> atom4 = Atom.m_320573_("tag_type");
        Atom<List<T>> atom5 = Atom.m_320573_("conditions");
        Atom<List<T>> atom6 = Atom.m_320573_("alternatives");
        Atom<T> atom7 = Atom.m_320573_("term");
        Atom<T> atom8 = Atom.m_320573_("negation");
        Atom<T> atom9 = Atom.m_320573_("test");
        Atom<C> atom10 = Atom.m_320573_("component_type");
        Atom<P> atom11 = Atom.m_320573_("predicate_type");
        Atom<ResourceLocation> atom12 = Atom.m_320573_("id");
        Atom<Tag> atom13 = Atom.m_320573_("tag");
        Dictionary<StringReader> dictionary = new Dictionary<>();
        dictionary.m_320832_(
            atom,
            Term.m_319180_(
                Term.m_322077_(
                    Term.m_321288_(atom1),
                    StringReaderTerms.m_321908_('['),
                    Term.m_324824_(),
                    Term.m_325045_(Term.m_321288_(atom5)),
                    StringReaderTerms.m_321908_(']')
                ),
                Term.m_321288_(atom1)
            ),
            p_331933_ -> {
                Builder<T> builder = ImmutableList.builder();
                p_331933_.m_324370_(atom1).ifPresent(builder::add);
                List<T> list = p_331933_.m_324672_(atom5);
                if (list != null) {
                    builder.addAll(list);
                }

                return builder.build();
            }
        );
        dictionary.m_320832_(
            atom1,
            Term.m_319180_(
                Term.m_321288_(atom3), Term.m_322077_(StringReaderTerms.m_321908_('#'), Term.m_324824_(), Term.m_321288_(atom4)), Term.m_321288_(atom2)
            ),
            p_333155_ -> Optional.ofNullable(p_333155_.m_319017_(atom3, atom4))
        );
        dictionary.m_320832_(atom2, StringReaderTerms.m_321908_('*'), p_328666_ -> Unit.INSTANCE);
        dictionary.m_323201_(atom3, new ComponentPredicateParser.ElementLookupRule<>(atom12, p_329972_));
        dictionary.m_323201_(atom4, new ComponentPredicateParser.TagLookupRule<>(atom12, p_329972_));
        dictionary.m_320832_(
            atom5,
            Term.m_322077_(Term.m_321288_(atom6), Term.m_325045_(Term.m_322077_(StringReaderTerms.m_321908_(','), Term.m_321288_(atom5)))),
            p_332096_ -> {
                T t = p_329972_.m_320517_(p_332096_.m_324370_(atom6));
                return Optional.ofNullable(p_332096_.m_324672_(atom5)).map(p_331681_ -> Util.m_321242_(t, (List<T>)p_331681_)).orElse(List.of(t));
            }
        );
        dictionary.m_320832_(
            atom6,
            Term.m_322077_(Term.m_321288_(atom7), Term.m_325045_(Term.m_322077_(StringReaderTerms.m_321908_('|'), Term.m_321288_(atom6)))),
            p_334061_ -> {
                T t = p_334061_.m_324370_(atom7);
                return Optional.ofNullable(p_334061_.m_324672_(atom6)).map(p_334416_ -> Util.m_321242_(t, (List<T>)p_334416_)).orElse(List.of(t));
            }
        );
        dictionary.m_320832_(
            atom7,
            Term.m_319180_(Term.m_321288_(atom9), Term.m_322077_(StringReaderTerms.m_321908_('!'), Term.m_321288_(atom8))),
            p_335341_ -> p_335341_.m_320837_(atom9, atom8)
        );
        dictionary.m_320832_(atom8, Term.m_321288_(atom9), p_331974_ -> p_329972_.m_323255_(p_331974_.m_324370_(atom9)));
        dictionary.m_323801_(
            atom9,
            Term.m_319180_(
                Term.m_322077_(Term.m_321288_(atom10), StringReaderTerms.m_321908_('='), Term.m_324824_(), Term.m_321288_(atom13)),
                Term.m_322077_(Term.m_321288_(atom11), StringReaderTerms.m_321908_('~'), Term.m_324824_(), Term.m_321288_(atom13)),
                Term.m_321288_(atom10)
            ),
            (p_329079_, p_335425_) -> {
                P p = p_335425_.m_324672_(atom11);

                try {
                    if (p != null) {
                        Tag tag1 = p_335425_.m_324370_(atom13);
                        return Optional.of(p_329972_.m_321322_(p_329079_.m_322193_(), p, tag1));
                    } else {
                        C c = p_335425_.m_324370_(atom10);
                        Tag tag = p_335425_.m_324672_(atom13);
                        return Optional.of(tag != null ? p_329972_.m_322026_(p_329079_.m_322193_(), c, tag) : p_329972_.m_319866_(p_329079_.m_322193_(), c));
                    }
                } catch (CommandSyntaxException commandsyntaxexception) {
                    p_329079_.m_323339_().m_323756_(p_329079_.m_320129_(), commandsyntaxexception);
                    return Optional.empty();
                }
            }
        );
        dictionary.m_323201_(atom10, new ComponentPredicateParser.ComponentLookupRule<>(atom12, p_329972_));
        dictionary.m_323201_(atom11, new ComponentPredicateParser.PredicateLookupRule<>(atom12, p_329972_));
        dictionary.m_323201_(atom13, TagParseRule.f_315850_);
        dictionary.m_323201_(atom12, ResourceLocationParseRule.f_315313_);
        return new Grammar<>(dictionary, atom);
    }

    static class ComponentLookupRule<T, C, P> extends ResourceLookupRule<ComponentPredicateParser.Context<T, C, P>, C> {
        ComponentLookupRule(Atom<ResourceLocation> p_333171_, ComponentPredicateParser.Context<T, C, P> p_336202_) {
            super(p_333171_, p_336202_);
        }

        @Override
        protected C m_319888_(ImmutableStringReader p_335905_, ResourceLocation p_336332_) throws Exception {
            return this.f_314968_.m_319836_(p_335905_, p_336332_);
        }

        @Override
        public Stream<ResourceLocation> m_319106_() {
            return this.f_314968_.m_321279_();
        }
    }

    public interface Context<T, C, P> {
        T m_320058_(ImmutableStringReader p_331849_, ResourceLocation p_335307_) throws CommandSyntaxException;

        Stream<ResourceLocation> m_319669_();

        T m_320585_(ImmutableStringReader p_332583_, ResourceLocation p_334980_) throws CommandSyntaxException;

        Stream<ResourceLocation> m_322575_();

        C m_319836_(ImmutableStringReader p_331245_, ResourceLocation p_328438_) throws CommandSyntaxException;

        Stream<ResourceLocation> m_321279_();

        T m_322026_(ImmutableStringReader p_331435_, C p_331254_, Tag p_333206_) throws CommandSyntaxException;

        T m_319866_(ImmutableStringReader p_333214_, C p_331519_);

        P m_318819_(ImmutableStringReader p_329855_, ResourceLocation p_331711_) throws CommandSyntaxException;

        Stream<ResourceLocation> m_320604_();

        T m_321322_(ImmutableStringReader p_332946_, P p_329900_, Tag p_336108_) throws CommandSyntaxException;

        T m_323255_(T p_328958_);

        T m_320517_(List<T> p_330220_);
    }

    static class ElementLookupRule<T, C, P> extends ResourceLookupRule<ComponentPredicateParser.Context<T, C, P>, T> {
        ElementLookupRule(Atom<ResourceLocation> p_330685_, ComponentPredicateParser.Context<T, C, P> p_333665_) {
            super(p_330685_, p_333665_);
        }

        @Override
        protected T m_319888_(ImmutableStringReader p_336288_, ResourceLocation p_329752_) throws Exception {
            return this.f_314968_.m_320058_(p_336288_, p_329752_);
        }

        @Override
        public Stream<ResourceLocation> m_319106_() {
            return this.f_314968_.m_319669_();
        }
    }

    static class PredicateLookupRule<T, C, P> extends ResourceLookupRule<ComponentPredicateParser.Context<T, C, P>, P> {
        PredicateLookupRule(Atom<ResourceLocation> p_333095_, ComponentPredicateParser.Context<T, C, P> p_335118_) {
            super(p_333095_, p_335118_);
        }

        @Override
        protected P m_319888_(ImmutableStringReader p_334282_, ResourceLocation p_330262_) throws Exception {
            return this.f_314968_.m_318819_(p_334282_, p_330262_);
        }

        @Override
        public Stream<ResourceLocation> m_319106_() {
            return this.f_314968_.m_320604_();
        }
    }

    static class TagLookupRule<T, C, P> extends ResourceLookupRule<ComponentPredicateParser.Context<T, C, P>, T> {
        TagLookupRule(Atom<ResourceLocation> p_333575_, ComponentPredicateParser.Context<T, C, P> p_330358_) {
            super(p_333575_, p_330358_);
        }

        @Override
        protected T m_319888_(ImmutableStringReader p_335818_, ResourceLocation p_327854_) throws Exception {
            return this.f_314968_.m_320585_(p_335818_, p_327854_);
        }

        @Override
        public Stream<ResourceLocation> m_319106_() {
            return this.f_314968_.m_322575_();
        }
    }
}