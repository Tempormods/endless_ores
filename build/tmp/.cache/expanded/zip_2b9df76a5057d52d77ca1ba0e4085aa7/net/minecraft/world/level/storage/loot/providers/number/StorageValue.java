package net.minecraft.world.level.storage.loot.providers.number;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import java.util.Optional;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootContext;

public record StorageValue(ResourceLocation f_316218_, NbtPathArgument.NbtPath f_314609_) implements NumberProvider {
    public static final MapCodec<StorageValue> f_316731_ = RecordCodecBuilder.mapCodec(
        p_330652_ -> p_330652_.group(
                    ResourceLocation.CODEC.fieldOf("storage").forGetter(StorageValue::f_316218_),
                    NbtPathArgument.NbtPath.f_314983_.fieldOf("path").forGetter(StorageValue::f_314609_)
                )
                .apply(p_330652_, StorageValue::new)
    );

    @Override
    public LootNumberProviderType getType() {
        return NumberProviders.f_314126_;
    }

    private Optional<NumericTag> m_323577_(LootContext p_329012_) {
        CompoundTag compoundtag = p_329012_.getLevel().getServer().getCommandStorage().get(this.f_316218_);

        try {
            List<Tag> list = this.f_314609_.get(compoundtag);
            if (list.size() == 1 && list.get(0) instanceof NumericTag numerictag) {
                return Optional.of(numerictag);
            }
        } catch (CommandSyntaxException commandsyntaxexception) {
        }

        return Optional.empty();
    }

    @Override
    public float getFloat(LootContext p_334554_) {
        return this.m_323577_(p_334554_).map(NumericTag::getAsFloat).orElse(0.0F);
    }

    @Override
    public int getInt(LootContext p_329755_) {
        return this.m_323577_(p_329755_).map(NumericTag::getAsInt).orElse(0);
    }
}