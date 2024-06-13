package net.minecraft.network.chat.contents;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.MapCodec;
import java.util.stream.Stream;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.util.StringRepresentable;

public interface DataSource {
    MapCodec<DataSource> f_303844_ = ComponentSerialization.m_306757_(
        new DataSource.Type[]{EntityDataSource.f_303369_, BlockDataSource.f_303290_, StorageDataSource.f_302204_},
        DataSource.Type::f_303489_,
        DataSource::m_306070_,
        "source"
    );

    Stream<CompoundTag> getData(CommandSourceStack pSource) throws CommandSyntaxException;

    DataSource.Type<?> m_306070_();

    public static record Type<T extends DataSource>(MapCodec<T> f_303489_, String f_303145_) implements StringRepresentable {
        @Override
        public String getSerializedName() {
            return this.f_303145_;
        }
    }
}