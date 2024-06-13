package net.minecraft.network.chat.contents;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;

public record BlockDataSource(String posPattern, @Nullable Coordinates compiledPos) implements DataSource {
    public static final MapCodec<BlockDataSource> f_302989_ = RecordCodecBuilder.mapCodec(
        p_309816_ -> p_309816_.group(Codec.STRING.fieldOf("block").forGetter(BlockDataSource::posPattern)).apply(p_309816_, BlockDataSource::new)
    );
    public static final DataSource.Type<BlockDataSource> f_303290_ = new DataSource.Type<>(f_302989_, "block");

    public BlockDataSource(String pPosPattern) {
        this(pPosPattern, compilePos(pPosPattern));
    }

    @Nullable
    private static Coordinates compilePos(String pPosPattern) {
        try {
            return BlockPosArgument.blockPos().parse(new StringReader(pPosPattern));
        } catch (CommandSyntaxException commandsyntaxexception) {
            return null;
        }
    }

    @Override
    public Stream<CompoundTag> getData(CommandSourceStack pSource) {
        if (this.compiledPos != null) {
            ServerLevel serverlevel = pSource.getLevel();
            BlockPos blockpos = this.compiledPos.getBlockPos(pSource);
            if (serverlevel.isLoaded(blockpos)) {
                BlockEntity blockentity = serverlevel.getBlockEntity(blockpos);
                if (blockentity != null) {
                    return Stream.of(blockentity.saveWithFullMetadata(pSource.registryAccess()));
                }
            }
        }

        return Stream.empty();
    }

    @Override
    public DataSource.Type<?> m_306070_() {
        return f_303290_;
    }

    @Override
    public String toString() {
        return "block=" + this.posPattern;
    }

    @Override
    public boolean equals(Object pOther) {
        if (this == pOther) {
            return true;
        } else {
            if (pOther instanceof BlockDataSource blockdatasource && this.posPattern.equals(blockdatasource.posPattern)) {
                return true;
            }

            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.posPattern.hashCode();
    }
}