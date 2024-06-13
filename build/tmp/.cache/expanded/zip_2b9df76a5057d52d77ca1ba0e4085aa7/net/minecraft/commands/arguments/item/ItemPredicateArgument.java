package net.minecraft.commands.arguments.item;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.advancements.critereon.ItemSubPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.parsing.packrat.commands.Grammar;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemPredicateArgument implements ArgumentType<ItemPredicateArgument.Result> {
    private static final Collection<String> EXAMPLES = Arrays.asList("stick", "minecraft:stick", "#stick", "#stick{foo:'bar'}");
    static final DynamicCommandExceptionType f_315167_ = new DynamicCommandExceptionType(
        p_325619_ -> Component.m_307043_("argument.item.id.invalid", p_325619_)
    );
    static final DynamicCommandExceptionType f_315281_ = new DynamicCommandExceptionType(
        p_325632_ -> Component.m_307043_("arguments.item.tag.unknown", p_325632_)
    );
    static final DynamicCommandExceptionType f_315273_ = new DynamicCommandExceptionType(
        p_325626_ -> Component.m_307043_("arguments.item.component.unknown", p_325626_)
    );
    static final Dynamic2CommandExceptionType f_314770_ = new Dynamic2CommandExceptionType(
        (p_325624_, p_325625_) -> Component.m_307043_("arguments.item.component.malformed", p_325624_, p_325625_)
    );
    static final DynamicCommandExceptionType f_315940_ = new DynamicCommandExceptionType(
        p_325623_ -> Component.m_307043_("arguments.item.predicate.unknown", p_325623_)
    );
    static final Dynamic2CommandExceptionType f_315982_ = new Dynamic2CommandExceptionType(
        (p_325617_, p_325618_) -> Component.m_307043_("arguments.item.predicate.malformed", p_325617_, p_325618_)
    );
    private static final ResourceLocation f_314529_ = new ResourceLocation("count");
    static final Map<ResourceLocation, ItemPredicateArgument.ComponentWrapper> f_315330_ = Stream.of(
            new ItemPredicateArgument.ComponentWrapper(
                f_314529_, p_325630_ -> true, MinMaxBounds.Ints.CODEC.map(p_325633_ -> p_325622_ -> p_325633_.matches(p_325622_.getCount()))
            )
        )
        .collect(
            Collectors.toUnmodifiableMap(ItemPredicateArgument.ComponentWrapper::f_314512_, p_325629_ -> (ItemPredicateArgument.ComponentWrapper)p_325629_)
        );
    static final Map<ResourceLocation, ItemPredicateArgument.PredicateWrapper> f_315932_ = Stream.of(
            new ItemPredicateArgument.PredicateWrapper(
                f_314529_, MinMaxBounds.Ints.CODEC.map(p_325620_ -> p_325628_ -> p_325620_.matches(p_325628_.getCount()))
            )
        )
        .collect(
            Collectors.toUnmodifiableMap(ItemPredicateArgument.PredicateWrapper::f_316718_, p_325631_ -> (ItemPredicateArgument.PredicateWrapper)p_325631_)
        );
    private final Grammar<List<Predicate<ItemStack>>> f_316699_;

    public ItemPredicateArgument(CommandBuildContext pContext) {
        ItemPredicateArgument.Context itempredicateargument$context = new ItemPredicateArgument.Context(pContext);
        this.f_316699_ = ComponentPredicateParser.m_320116_(itempredicateargument$context);
    }

    public static ItemPredicateArgument itemPredicate(CommandBuildContext pContext) {
        return new ItemPredicateArgument(pContext);
    }

    public ItemPredicateArgument.Result parse(StringReader pReader) throws CommandSyntaxException {
        return Util.m_322468_(this.f_316699_.m_320023_(pReader))::test;
    }

    public static ItemPredicateArgument.Result getItemPredicate(CommandContext<CommandSourceStack> pContext, String pName) {
        return pContext.getArgument(pName, ItemPredicateArgument.Result.class);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> pContext, SuggestionsBuilder pBuilder) {
        return this.f_316699_.m_320779_(pBuilder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    static record ComponentWrapper(ResourceLocation f_314512_, Predicate<ItemStack> f_316514_, Decoder<? extends Predicate<ItemStack>> f_315916_) {
        public static <T> ItemPredicateArgument.ComponentWrapper m_324681_(
            ImmutableStringReader p_336159_, ResourceLocation p_334103_, DataComponentType<T> p_331569_
        ) throws CommandSyntaxException {
            Codec<T> codec = p_331569_.m_319878_();
            if (codec == null) {
                throw ItemPredicateArgument.f_315273_.createWithContext(p_336159_, p_334103_);
            } else {
                return new ItemPredicateArgument.ComponentWrapper(p_334103_, p_327858_ -> p_327858_.m_319951_(p_331569_), codec.map(p_335085_ -> p_331446_ -> {
                        T t = p_331446_.m_323252_(p_331569_);
                        return Objects.equals(p_335085_, t);
                    }));
            }
        }

        public Predicate<ItemStack> m_319206_(ImmutableStringReader p_333508_, RegistryOps<Tag> p_329031_, Tag p_332091_) throws CommandSyntaxException {
            DataResult<? extends Predicate<ItemStack>> dataresult = this.f_315916_.parse(p_329031_, p_332091_);
            return (Predicate<ItemStack>)dataresult.getOrThrow(
                p_331995_ -> ItemPredicateArgument.f_314770_.createWithContext(p_333508_, this.f_314512_.toString(), p_331995_)
            );
        }
    }

    static class Context
        implements ComponentPredicateParser.Context<Predicate<ItemStack>, ItemPredicateArgument.ComponentWrapper, ItemPredicateArgument.PredicateWrapper> {
        private final HolderLookup.RegistryLookup<Item> f_316102_;
        private final HolderLookup.RegistryLookup<DataComponentType<?>> f_314372_;
        private final HolderLookup.RegistryLookup<ItemSubPredicate.Type<?>> f_314596_;
        private final RegistryOps<Tag> f_316517_;

        Context(HolderLookup.Provider p_331757_) {
            this.f_316102_ = p_331757_.lookupOrThrow(Registries.ITEM);
            this.f_314372_ = p_331757_.lookupOrThrow(Registries.f_316190_);
            this.f_314596_ = p_331757_.lookupOrThrow(Registries.f_316222_);
            this.f_316517_ = p_331757_.m_318927_(NbtOps.INSTANCE);
        }

        public Predicate<ItemStack> m_320058_(ImmutableStringReader p_328916_, ResourceLocation p_333737_) throws CommandSyntaxException {
            Holder.Reference<Item> reference = this.f_316102_
                .m_254926_(ResourceKey.create(Registries.ITEM, p_333737_))
                .orElseThrow(() -> ItemPredicateArgument.f_315167_.createWithContext(p_328916_, p_333737_));
            return p_333639_ -> p_333639_.is(reference);
        }

        public Predicate<ItemStack> m_320585_(ImmutableStringReader p_332402_, ResourceLocation p_328228_) throws CommandSyntaxException {
            HolderSet<Item> holderset = this.f_316102_
                .m_255050_(TagKey.create(Registries.ITEM, p_328228_))
                .orElseThrow(() -> ItemPredicateArgument.f_315281_.createWithContext(p_332402_, p_328228_));
            return p_334213_ -> p_334213_.is(holderset);
        }

        public ItemPredicateArgument.ComponentWrapper m_319836_(ImmutableStringReader p_329300_, ResourceLocation p_330392_) throws CommandSyntaxException {
            ItemPredicateArgument.ComponentWrapper itempredicateargument$componentwrapper = ItemPredicateArgument.f_315330_.get(p_330392_);
            if (itempredicateargument$componentwrapper != null) {
                return itempredicateargument$componentwrapper;
            } else {
                DataComponentType<?> datacomponenttype = this.f_314372_
                    .m_254926_(ResourceKey.create(Registries.f_316190_, p_330392_))
                    .map(Holder::value)
                    .orElseThrow(() -> ItemPredicateArgument.f_315273_.createWithContext(p_329300_, p_330392_));
                return ItemPredicateArgument.ComponentWrapper.m_324681_(p_329300_, p_330392_, datacomponenttype);
            }
        }

        public Predicate<ItemStack> m_322026_(ImmutableStringReader p_330237_, ItemPredicateArgument.ComponentWrapper p_334559_, Tag p_328343_) throws CommandSyntaxException {
            return p_334559_.m_319206_(p_330237_, this.f_316517_, p_328343_);
        }

        public Predicate<ItemStack> m_319866_(ImmutableStringReader p_330923_, ItemPredicateArgument.ComponentWrapper p_336299_) {
            return p_336299_.f_316514_;
        }

        public ItemPredicateArgument.PredicateWrapper m_318819_(ImmutableStringReader p_330457_, ResourceLocation p_335636_) throws CommandSyntaxException {
            ItemPredicateArgument.PredicateWrapper itempredicateargument$predicatewrapper = ItemPredicateArgument.f_315932_.get(p_335636_);
            return itempredicateargument$predicatewrapper != null
                ? itempredicateargument$predicatewrapper
                : this.f_314596_
                    .m_254926_(ResourceKey.create(Registries.f_316222_, p_335636_))
                    .map(ItemPredicateArgument.PredicateWrapper::new)
                    .orElseThrow(() -> ItemPredicateArgument.f_315940_.createWithContext(p_330457_, p_335636_));
        }

        public Predicate<ItemStack> m_321322_(ImmutableStringReader p_332241_, ItemPredicateArgument.PredicateWrapper p_335982_, Tag p_333667_) throws CommandSyntaxException {
            return p_335982_.m_323868_(p_332241_, this.f_316517_, p_333667_);
        }

        @Override
        public Stream<ResourceLocation> m_319669_() {
            return this.f_316102_.listElementIds().map(ResourceKey::location);
        }

        @Override
        public Stream<ResourceLocation> m_322575_() {
            return this.f_316102_.listTagIds().map(TagKey::location);
        }

        @Override
        public Stream<ResourceLocation> m_321279_() {
            return Stream.concat(
                ItemPredicateArgument.f_315330_.keySet().stream(),
                this.f_314372_.listElements().filter(p_334864_ -> !p_334864_.value().m_322187_()).map(p_329470_ -> p_329470_.key().location())
            );
        }

        @Override
        public Stream<ResourceLocation> m_320604_() {
            return Stream.concat(ItemPredicateArgument.f_315932_.keySet().stream(), this.f_314596_.listElementIds().map(ResourceKey::location));
        }

        public Predicate<ItemStack> m_323255_(Predicate<ItemStack> p_328753_) {
            return p_328753_.negate();
        }

        public Predicate<ItemStack> m_320517_(List<Predicate<ItemStack>> p_329990_) {
            return Util.m_321702_(p_329990_);
        }
    }

    static record PredicateWrapper(ResourceLocation f_316718_, Decoder<? extends Predicate<ItemStack>> f_314434_) {
        public PredicateWrapper(Holder.Reference<ItemSubPredicate.Type<?>> p_327901_) {
            this(p_327901_.key().location(), p_327901_.value().f_316804_().map(p_330179_ -> p_330179_::m_321281_));
        }

        public Predicate<ItemStack> m_323868_(ImmutableStringReader p_335853_, RegistryOps<Tag> p_335697_, Tag p_330696_) throws CommandSyntaxException {
            DataResult<? extends Predicate<ItemStack>> dataresult = this.f_314434_.parse(p_335697_, p_330696_);
            return (Predicate<ItemStack>)dataresult.getOrThrow(
                p_334639_ -> ItemPredicateArgument.f_315982_.createWithContext(p_335853_, this.f_316718_.toString(), p_334639_)
            );
        }
    }

    public interface Result extends Predicate<ItemStack> {
    }
}