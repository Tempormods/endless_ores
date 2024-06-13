package net.minecraft.commands.arguments;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.serialization.Codec;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;

public class ResourceOrIdArgument<T> implements ArgumentType<Holder<T>> {
    private static final Collection<String> f_316926_ = List.of("foo", "foo:bar", "012", "{}", "true");
    public static final DynamicCommandExceptionType f_315623_ = new DynamicCommandExceptionType(
        p_334248_ -> Component.m_307043_("argument.resource_or_id.failed_to_parse", p_334248_)
    );
    private static final SimpleCommandExceptionType f_314815_ = new SimpleCommandExceptionType(Component.translatable("argument.resource_or_id.invalid"));
    private final HolderLookup.Provider f_315523_;
    private final boolean f_316282_;
    private final Codec<Holder<T>> f_315174_;

    protected ResourceOrIdArgument(CommandBuildContext p_334973_, ResourceKey<Registry<T>> p_336087_, Codec<Holder<T>> p_332112_) {
        this.f_315523_ = p_334973_;
        this.f_316282_ = p_334973_.lookup(p_336087_).isPresent();
        this.f_315174_ = p_332112_;
    }

    public static ResourceOrIdArgument.LootTableArgument m_324396_(CommandBuildContext p_329328_) {
        return new ResourceOrIdArgument.LootTableArgument(p_329328_);
    }

    public static Holder<LootTable> m_319428_(CommandContext<CommandSourceStack> p_335148_, String p_329251_) throws CommandSyntaxException {
        return m_323671_(p_335148_, p_329251_);
    }

    public static ResourceOrIdArgument.LootModifierArgument m_321589_(CommandBuildContext p_329720_) {
        return new ResourceOrIdArgument.LootModifierArgument(p_329720_);
    }

    public static Holder<LootItemFunction> m_320065_(CommandContext<CommandSourceStack> p_334458_, String p_330525_) {
        return m_323671_(p_334458_, p_330525_);
    }

    public static ResourceOrIdArgument.LootPredicateArgument m_324190_(CommandBuildContext p_330159_) {
        return new ResourceOrIdArgument.LootPredicateArgument(p_330159_);
    }

    public static Holder<LootItemCondition> m_323980_(CommandContext<CommandSourceStack> p_335366_, String p_334649_) {
        return m_323671_(p_335366_, p_334649_);
    }

    private static <T> Holder<T> m_323671_(CommandContext<CommandSourceStack> p_328476_, String p_329877_) {
        return p_328476_.getArgument(p_329877_, Holder.class);
    }

    @Nullable
    public Holder<T> parse(StringReader p_330381_) throws CommandSyntaxException {
        Tag tag = m_319147_(p_330381_);
        if (!this.f_316282_) {
            return null;
        } else {
            RegistryOps<Tag> registryops = this.f_315523_.m_318927_(NbtOps.INSTANCE);
            return this.f_315174_.parse(registryops, tag).getOrThrow(p_334690_ -> f_315623_.createWithContext(p_330381_, p_334690_));
        }
    }

    @VisibleForTesting
    static Tag m_319147_(StringReader p_331361_) throws CommandSyntaxException {
        int i = p_331361_.getCursor();
        Tag tag = new TagParser(p_331361_).readValue();
        if (m_318795_(p_331361_)) {
            return tag;
        } else {
            p_331361_.setCursor(i);
            ResourceLocation resourcelocation = ResourceLocation.read(p_331361_);
            if (m_318795_(p_331361_)) {
                return StringTag.valueOf(resourcelocation.toString());
            } else {
                p_331361_.setCursor(i);
                throw f_314815_.createWithContext(p_331361_);
            }
        }
    }

    private static boolean m_318795_(StringReader p_330624_) {
        return !p_330624_.canRead() || p_330624_.peek() == ' ';
    }

    @Override
    public Collection<String> getExamples() {
        return f_316926_;
    }

    public static class LootModifierArgument extends ResourceOrIdArgument<LootItemFunction> {
        protected LootModifierArgument(CommandBuildContext p_333515_) {
            super(p_333515_, Registries.f_316898_, LootItemFunctions.CODEC);
        }
    }

    public static class LootPredicateArgument extends ResourceOrIdArgument<LootItemCondition> {
        protected LootPredicateArgument(CommandBuildContext p_334679_) {
            super(p_334679_, Registries.f_315752_, LootItemConditions.CODEC);
        }
    }

    public static class LootTableArgument extends ResourceOrIdArgument<LootTable> {
        protected LootTableArgument(CommandBuildContext p_332797_) {
            super(p_332797_, Registries.f_314309_, LootTable.CODEC);
        }
    }
}