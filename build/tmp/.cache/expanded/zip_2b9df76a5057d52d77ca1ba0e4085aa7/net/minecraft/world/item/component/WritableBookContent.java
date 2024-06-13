package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.network.Filterable;

public record WritableBookContent(List<Filterable<String>> f_314930_) implements BookContent<String, WritableBookContent> {
    public static final WritableBookContent f_314124_ = new WritableBookContent(List.of());
    public static final int f_314989_ = 1024;
    public static final int f_315128_ = 100;
    private static final Codec<Filterable<String>> f_316974_ = Filterable.m_322486_(Codec.string(0, 1024));
    public static final Codec<List<Filterable<String>>> f_313981_ = f_316974_.sizeLimitedListOf(100);
    public static final Codec<WritableBookContent> f_316245_ = RecordCodecBuilder.create(
        p_327725_ -> p_327725_.group(f_313981_.optionalFieldOf("pages", List.of()).forGetter(WritableBookContent::m_319402_))
                .apply(p_327725_, WritableBookContent::new)
    );
    public static final StreamCodec<ByteBuf, WritableBookContent> f_316228_ = Filterable.m_323964_(ByteBufCodecs.m_319534_(1024))
        .m_321801_(ByteBufCodecs.m_319259_(100))
        .m_323038_(WritableBookContent::new, WritableBookContent::m_319402_);

    public WritableBookContent(List<Filterable<String>> f_314930_) {
        if (f_314930_.size() > 100) {
            throw new IllegalArgumentException("Got " + f_314930_.size() + " pages, but maximum is 100");
        } else {
            this.f_314930_ = f_314930_;
        }
    }

    public Stream<String> m_320046_(boolean p_333617_) {
        return this.f_314930_.stream().map(p_334234_ -> p_334234_.m_323302_(p_333617_));
    }

    public WritableBookContent m_319955_(List<Filterable<String>> p_334830_) {
        return new WritableBookContent(p_334830_);
    }

    @Override
    public List<Filterable<String>> m_319402_() {
        return this.f_314930_;
    }
}