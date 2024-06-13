package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class ParticleArgument implements ArgumentType<ParticleOptions> {
    private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "particle{foo:bar}");
    public static final DynamicCommandExceptionType ERROR_UNKNOWN_PARTICLE = new DynamicCommandExceptionType(
        p_308358_ -> Component.m_307043_("particle.notFound", p_308358_)
    );
    public static final DynamicCommandExceptionType f_315297_ = new DynamicCommandExceptionType(
        p_325596_ -> Component.m_307043_("particle.invalidOptions", p_325596_)
    );
    private final HolderLookup.Provider f_316524_;

    public ParticleArgument(CommandBuildContext pBuildContext) {
        this.f_316524_ = pBuildContext;
    }

    public static ParticleArgument particle(CommandBuildContext pBuildContext) {
        return new ParticleArgument(pBuildContext);
    }

    public static ParticleOptions getParticle(CommandContext<CommandSourceStack> pContext, String pName) {
        return pContext.getArgument(pName, ParticleOptions.class);
    }

    public ParticleOptions parse(StringReader pReader) throws CommandSyntaxException {
        return readParticle(pReader, this.f_316524_);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public static ParticleOptions readParticle(StringReader pReader, HolderLookup.Provider p_333534_) throws CommandSyntaxException {
        ParticleType<?> particletype = readParticleType(pReader, p_333534_.lookupOrThrow(Registries.PARTICLE_TYPE));
        return readParticle(pReader, (ParticleType<ParticleOptions>)particletype, p_333534_);
    }

    private static ParticleType<?> readParticleType(StringReader pReader, HolderLookup<ParticleType<?>> pParticleTypeLookup) throws CommandSyntaxException {
        ResourceLocation resourcelocation = ResourceLocation.read(pReader);
        ResourceKey<ParticleType<?>> resourcekey = ResourceKey.create(Registries.PARTICLE_TYPE, resourcelocation);
        return pParticleTypeLookup.m_254926_(resourcekey).orElseThrow(() -> ERROR_UNKNOWN_PARTICLE.createWithContext(pReader, resourcelocation)).value();
    }

    private static <T extends ParticleOptions> T readParticle(StringReader pReader, ParticleType<T> pType, HolderLookup.Provider p_329867_) throws CommandSyntaxException {
        CompoundTag compoundtag;
        if (pReader.canRead() && pReader.peek() == '{') {
            compoundtag = new TagParser(pReader).readStruct();
        } else {
            compoundtag = new CompoundTag();
        }

        return pType.codec().codec().parse(p_329867_.m_318927_(NbtOps.INSTANCE), compoundtag).getOrThrow(f_315297_::create);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> pContext, SuggestionsBuilder pBuilder) {
        HolderLookup.RegistryLookup<ParticleType<?>> registrylookup = this.f_316524_.lookupOrThrow(Registries.PARTICLE_TYPE);
        return SharedSuggestionProvider.suggestResource(registrylookup.listElementIds().map(ResourceKey::location), pBuilder);
    }
}