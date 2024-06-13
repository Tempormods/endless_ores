package net.minecraft.commands.arguments.item;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import it.unimi.dsi.fastutil.objects.ReferenceArraySet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.mutable.MutableObject;

public class ItemParser {
    static final DynamicCommandExceptionType ERROR_UNKNOWN_ITEM = new DynamicCommandExceptionType(
        p_308407_ -> Component.m_307043_("argument.item.id.invalid", p_308407_)
    );
    static final DynamicCommandExceptionType f_315145_ = new DynamicCommandExceptionType(
        p_308406_ -> Component.m_307043_("arguments.item.component.unknown", p_308406_)
    );
    static final Dynamic2CommandExceptionType f_315026_ = new Dynamic2CommandExceptionType(
        (p_325613_, p_325614_) -> Component.m_307043_("arguments.item.component.malformed", p_325613_, p_325614_)
    );
    static final SimpleCommandExceptionType f_316029_ = new SimpleCommandExceptionType(Component.translatable("arguments.item.component.expected"));
    static final DynamicCommandExceptionType f_316330_ = new DynamicCommandExceptionType(
        p_325615_ -> Component.m_307043_("arguments.item.component.repeated", p_325615_)
    );
    private static final DynamicCommandExceptionType f_316165_ = new DynamicCommandExceptionType(
        p_325616_ -> Component.m_307043_("arguments.item.malformed", p_325616_)
    );
    public static final char f_316864_ = '[';
    public static final char f_314470_ = ']';
    public static final char f_316986_ = ',';
    public static final char f_317037_ = '=';
    static final Function<SuggestionsBuilder, CompletableFuture<Suggestions>> SUGGEST_NOTHING = SuggestionsBuilder::buildFuture;
    final HolderLookup.RegistryLookup<Item> items;
    final DynamicOps<Tag> f_314775_;

    public ItemParser(HolderLookup.Provider p_332470_) {
        this.items = p_332470_.lookupOrThrow(Registries.ITEM);
        this.f_314775_ = p_332470_.m_318927_(NbtOps.INSTANCE);
    }

    public ItemParser.ItemResult parse(StringReader p_329942_) throws CommandSyntaxException {
        final MutableObject<Holder<Item>> mutableobject = new MutableObject<>();
        final DataComponentMap.Builder datacomponentmap$builder = DataComponentMap.m_323371_();
        this.m_322282_(p_329942_, new ItemParser.Visitor() {
            @Override
            public void m_320426_(Holder<Item> p_328041_) {
                mutableobject.setValue(p_328041_);
            }

            @Override
            public <T> void m_320689_(DataComponentType<T> p_331133_, T p_330958_) {
                datacomponentmap$builder.m_322739_(p_331133_, p_330958_);
            }
        });
        Holder<Item> holder = Objects.requireNonNull(mutableobject.getValue(), "Parser gave no item");
        DataComponentMap datacomponentmap = datacomponentmap$builder.m_318826_();
        m_323692_(p_329942_, holder, datacomponentmap);
        return new ItemParser.ItemResult(holder, datacomponentmap);
    }

    private static void m_323692_(StringReader p_331709_, Holder<Item> p_331328_, DataComponentMap p_336221_) throws CommandSyntaxException {
        DataComponentMap datacomponentmap = DataComponentMap.m_319349_(p_331328_.value().m_320917_(), p_336221_);
        DataResult<Unit> dataresult = ItemStack.m_320698_(datacomponentmap);
        dataresult.getOrThrow(p_325612_ -> f_316165_.createWithContext(p_331709_, p_325612_));
    }

    public void m_322282_(StringReader p_328566_, ItemParser.Visitor p_331669_) throws CommandSyntaxException {
        int i = p_328566_.getCursor();

        try {
            new ItemParser.State(p_328566_, p_331669_).m_323511_();
        } catch (CommandSyntaxException commandsyntaxexception) {
            p_328566_.setCursor(i);
            throw commandsyntaxexception;
        }
    }

    public CompletableFuture<Suggestions> fillSuggestions(SuggestionsBuilder pBuilder) {
        StringReader stringreader = new StringReader(pBuilder.getInput());
        stringreader.setCursor(pBuilder.getStart());
        ItemParser.SuggestionsVisitor itemparser$suggestionsvisitor = new ItemParser.SuggestionsVisitor();
        ItemParser.State itemparser$state = new ItemParser.State(stringreader, itemparser$suggestionsvisitor);

        try {
            itemparser$state.m_323511_();
        } catch (CommandSyntaxException commandsyntaxexception) {
        }

        return itemparser$suggestionsvisitor.m_319709_(pBuilder, stringreader);
    }

    public static record ItemResult(Holder<Item> item, DataComponentMap f_314839_) {
    }

    class State {
        private final StringReader f_316907_;
        private final ItemParser.Visitor f_316703_;

        State(final StringReader p_334622_, final ItemParser.Visitor p_332237_) {
            this.f_316907_ = p_334622_;
            this.f_316703_ = p_332237_;
        }

        public void m_323511_() throws CommandSyntaxException {
            this.f_316703_.m_318806_(this::m_321312_);
            this.m_321186_();
            this.f_316703_.m_318806_(this::m_320167_);
            if (this.f_316907_.canRead() && this.f_316907_.peek() == '[') {
                this.f_316703_.m_318806_(ItemParser.SUGGEST_NOTHING);
                this.m_318731_();
            }
        }

        private void m_321186_() throws CommandSyntaxException {
            int i = this.f_316907_.getCursor();
            ResourceLocation resourcelocation = ResourceLocation.read(this.f_316907_);
            this.f_316703_.m_320426_(ItemParser.this.items.m_254926_(ResourceKey.create(Registries.ITEM, resourcelocation)).orElseThrow(() -> {
                this.f_316907_.setCursor(i);
                return ItemParser.ERROR_UNKNOWN_ITEM.createWithContext(this.f_316907_, resourcelocation);
            }));
        }

        private void m_318731_() throws CommandSyntaxException {
            this.f_316907_.expect('[');
            this.f_316703_.m_318806_(this::m_319184_);
            Set<DataComponentType<?>> set = new ReferenceArraySet<>();

            while (this.f_316907_.canRead() && this.f_316907_.peek() != ']') {
                this.f_316907_.skipWhitespace();
                DataComponentType<?> datacomponenttype = m_324926_(this.f_316907_);
                if (!set.add(datacomponenttype)) {
                    throw ItemParser.f_316330_.create(datacomponenttype);
                }

                this.f_316703_.m_318806_(this::m_319243_);
                this.f_316907_.skipWhitespace();
                this.f_316907_.expect('=');
                this.f_316703_.m_318806_(ItemParser.SUGGEST_NOTHING);
                this.f_316907_.skipWhitespace();
                this.m_321930_(datacomponenttype);
                this.f_316907_.skipWhitespace();
                this.f_316703_.m_318806_(this::m_321789_);
                if (!this.f_316907_.canRead() || this.f_316907_.peek() != ',') {
                    break;
                }

                this.f_316907_.skip();
                this.f_316907_.skipWhitespace();
                this.f_316703_.m_318806_(this::m_319184_);
                if (!this.f_316907_.canRead()) {
                    throw ItemParser.f_316029_.createWithContext(this.f_316907_);
                }
            }

            this.f_316907_.expect(']');
            this.f_316703_.m_318806_(ItemParser.SUGGEST_NOTHING);
        }

        public static DataComponentType<?> m_324926_(StringReader p_330692_) throws CommandSyntaxException {
            if (!p_330692_.canRead()) {
                throw ItemParser.f_316029_.createWithContext(p_330692_);
            } else {
                int i = p_330692_.getCursor();
                ResourceLocation resourcelocation = ResourceLocation.read(p_330692_);
                DataComponentType<?> datacomponenttype = BuiltInRegistries.f_315333_.get(resourcelocation);
                if (datacomponenttype != null && !datacomponenttype.m_322187_()) {
                    return datacomponenttype;
                } else {
                    p_330692_.setCursor(i);
                    throw ItemParser.f_315145_.createWithContext(p_330692_, resourcelocation);
                }
            }
        }

        private <T> void m_321930_(DataComponentType<T> p_330643_) throws CommandSyntaxException {
            int i = this.f_316907_.getCursor();
            Tag tag = new TagParser(this.f_316907_).readValue();
            DataResult<T> dataresult = p_330643_.m_319588_().parse(ItemParser.this.f_314775_, tag);
            this.f_316703_.m_320689_(p_330643_, dataresult.getOrThrow(p_335662_ -> {
                this.f_316907_.setCursor(i);
                return ItemParser.f_315026_.createWithContext(this.f_316907_, p_330643_.toString(), p_335662_);
            }));
        }

        private CompletableFuture<Suggestions> m_320167_(SuggestionsBuilder p_333169_) {
            if (p_333169_.getRemaining().isEmpty()) {
                p_333169_.suggest(String.valueOf('['));
            }

            return p_333169_.buildFuture();
        }

        private CompletableFuture<Suggestions> m_321789_(SuggestionsBuilder p_335586_) {
            if (p_335586_.getRemaining().isEmpty()) {
                p_335586_.suggest(String.valueOf(','));
                p_335586_.suggest(String.valueOf(']'));
            }

            return p_335586_.buildFuture();
        }

        private CompletableFuture<Suggestions> m_319243_(SuggestionsBuilder p_335223_) {
            if (p_335223_.getRemaining().isEmpty()) {
                p_335223_.suggest(String.valueOf('='));
            }

            return p_335223_.buildFuture();
        }

        private CompletableFuture<Suggestions> m_321312_(SuggestionsBuilder p_329594_) {
            return SharedSuggestionProvider.suggestResource(ItemParser.this.items.listElementIds().map(ResourceKey::location), p_329594_);
        }

        private CompletableFuture<Suggestions> m_319184_(SuggestionsBuilder p_331521_) {
            String s = p_331521_.getRemaining().toLowerCase(Locale.ROOT);
            SharedSuggestionProvider.filterResources(BuiltInRegistries.f_315333_.entrySet(), s, p_328035_ -> p_328035_.getKey().location(), p_335760_ -> {
                DataComponentType<?> datacomponenttype = p_335760_.getValue();
                if (datacomponenttype.m_319878_() != null) {
                    ResourceLocation resourcelocation = p_335760_.getKey().location();
                    p_331521_.suggest(resourcelocation.toString() + "=");
                }
            });
            return p_331521_.buildFuture();
        }
    }

    static class SuggestionsVisitor implements ItemParser.Visitor {
        private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> f_316929_ = ItemParser.SUGGEST_NOTHING;

        @Override
        public void m_318806_(Function<SuggestionsBuilder, CompletableFuture<Suggestions>> p_328999_) {
            this.f_316929_ = p_328999_;
        }

        public CompletableFuture<Suggestions> m_319709_(SuggestionsBuilder p_335628_, StringReader p_329757_) {
            return this.f_316929_.apply(p_335628_.createOffset(p_329757_.getCursor()));
        }
    }

    public interface Visitor {
        default void m_320426_(Holder<Item> p_333631_) {
        }

        default <T> void m_320689_(DataComponentType<T> p_331805_, T p_331331_) {
        }

        default void m_318806_(Function<SuggestionsBuilder, CompletableFuture<Suggestions>> p_330945_) {
        }
    }
}