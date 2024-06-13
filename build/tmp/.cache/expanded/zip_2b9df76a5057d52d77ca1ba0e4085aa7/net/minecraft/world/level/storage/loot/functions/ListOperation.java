package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import org.slf4j.Logger;

public interface ListOperation {
    MapCodec<ListOperation> f_317149_ = m_320139_(Integer.MAX_VALUE);

    static MapCodec<ListOperation> m_320139_(int p_334114_) {
        return ListOperation.Type.f_313966_
            .<ListOperation>dispatchMap("mode", ListOperation::m_320900_, p_328481_ -> p_328481_.f_314222_)
            .validate(p_336261_ -> {
                if (p_336261_ instanceof ListOperation.ReplaceSection listoperation$replacesection && listoperation$replacesection.f_317088_().isPresent()) {
                    int i = listoperation$replacesection.f_317088_().get();
                    if (i > p_334114_) {
                        return DataResult.error(() -> "Size value too large: " + i + ", max size is " + p_334114_);
                    }
                }

                return DataResult.success(p_336261_);
            });
    }

    ListOperation.Type m_320900_();

    default <T> List<T> m_323335_(List<T> p_334598_, List<T> p_335380_) {
        return this.m_320579_(p_334598_, p_335380_, Integer.MAX_VALUE);
    }

    <T> List<T> m_320579_(List<T> p_329737_, List<T> p_327893_, int p_332636_);

    public static class Append implements ListOperation {
        private static final Logger f_316450_ = LogUtils.getLogger();
        public static final ListOperation.Append f_316964_ = new ListOperation.Append();
        public static final MapCodec<ListOperation.Append> f_314139_ = MapCodec.unit(() -> f_316964_);

        private Append() {
        }

        @Override
        public ListOperation.Type m_320900_() {
            return ListOperation.Type.APPEND;
        }

        @Override
        public <T> List<T> m_320579_(List<T> p_330728_, List<T> p_331859_, int p_335288_) {
            if (p_330728_.size() + p_331859_.size() > p_335288_) {
                f_316450_.error("Contents overflow in section append");
                return p_330728_;
            } else {
                return Stream.concat(p_330728_.stream(), p_331859_.stream()).toList();
            }
        }
    }

    public static record Insert(int f_315739_) implements ListOperation {
        private static final Logger f_316944_ = LogUtils.getLogger();
        public static final MapCodec<ListOperation.Insert> f_314720_ = RecordCodecBuilder.mapCodec(
            p_329650_ -> p_329650_.group(ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("offset", 0).forGetter(ListOperation.Insert::f_315739_))
                    .apply(p_329650_, ListOperation.Insert::new)
        );

        @Override
        public ListOperation.Type m_320900_() {
            return ListOperation.Type.INSERT;
        }

        @Override
        public <T> List<T> m_320579_(List<T> p_336295_, List<T> p_330545_, int p_335268_) {
            int i = p_336295_.size();
            if (this.f_315739_ > i) {
                f_316944_.error("Cannot insert when offset is out of bounds");
                return p_336295_;
            } else if (i + p_330545_.size() > p_335268_) {
                f_316944_.error("Contents overflow in section insertion");
                return p_336295_;
            } else {
                Builder<T> builder = ImmutableList.builder();
                builder.addAll(p_336295_.subList(0, this.f_315739_));
                builder.addAll(p_330545_);
                builder.addAll(p_336295_.subList(this.f_315739_, i));
                return builder.build();
            }
        }
    }

    public static class ReplaceAll implements ListOperation {
        public static final ListOperation.ReplaceAll f_315656_ = new ListOperation.ReplaceAll();
        public static final MapCodec<ListOperation.ReplaceAll> f_317147_ = MapCodec.unit(() -> f_315656_);

        private ReplaceAll() {
        }

        @Override
        public ListOperation.Type m_320900_() {
            return ListOperation.Type.REPLACE_ALL;
        }

        @Override
        public <T> List<T> m_320579_(List<T> p_333557_, List<T> p_331455_, int p_335044_) {
            return p_331455_;
        }
    }

    public static record ReplaceSection(int f_316975_, Optional<Integer> f_317088_) implements ListOperation {
        private static final Logger f_314587_ = LogUtils.getLogger();
        public static final MapCodec<ListOperation.ReplaceSection> f_317063_ = RecordCodecBuilder.mapCodec(
            p_332380_ -> p_332380_.group(
                        ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("offset", 0).forGetter(ListOperation.ReplaceSection::f_316975_),
                        ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("size").forGetter(ListOperation.ReplaceSection::f_317088_)
                    )
                    .apply(p_332380_, ListOperation.ReplaceSection::new)
        );

        public ReplaceSection(int p_335251_) {
            this(p_335251_, Optional.empty());
        }

        @Override
        public ListOperation.Type m_320900_() {
            return ListOperation.Type.REPLACE_SECTION;
        }

        @Override
        public <T> List<T> m_320579_(List<T> p_336048_, List<T> p_331104_, int p_333303_) {
            int i = p_336048_.size();
            if (this.f_316975_ > i) {
                f_314587_.error("Cannot replace when offset is out of bounds");
                return p_336048_;
            } else {
                Builder<T> builder = ImmutableList.builder();
                builder.addAll(p_336048_.subList(0, this.f_316975_));
                builder.addAll(p_331104_);
                int j = this.f_316975_ + this.f_317088_.orElse(p_331104_.size());
                if (j < i) {
                    builder.addAll(p_336048_.subList(j, i));
                }

                List<T> list = builder.build();
                if (list.size() > p_333303_) {
                    f_314587_.error("Contents overflow in section replacement");
                    return p_336048_;
                } else {
                    return list;
                }
            }
        }
    }

    public static record StandAlone<T>(List<T> f_316147_, ListOperation f_317032_) {
        public static <T> Codec<ListOperation.StandAlone<T>> m_321359_(Codec<T> p_333263_, int p_334839_) {
            return RecordCodecBuilder.create(
                p_334562_ -> p_334562_.group(
                            p_333263_.sizeLimitedListOf(p_334839_).fieldOf("values").forGetter(p_331378_ -> p_331378_.f_316147_),
                            ListOperation.m_320139_(p_334839_).forGetter(p_330703_ -> p_330703_.f_317032_)
                        )
                        .apply(p_334562_, ListOperation.StandAlone::new)
            );
        }

        public List<T> m_321030_(List<T> p_334156_) {
            return this.f_317032_.m_323335_(p_334156_, this.f_316147_);
        }
    }

    public static enum Type implements StringRepresentable {
        REPLACE_ALL("replace_all", ListOperation.ReplaceAll.f_317147_),
        REPLACE_SECTION("replace_section", ListOperation.ReplaceSection.f_317063_),
        INSERT("insert", ListOperation.Insert.f_314720_),
        APPEND("append", ListOperation.Append.f_314139_);

        public static final Codec<ListOperation.Type> f_313966_ = StringRepresentable.fromEnum(ListOperation.Type::values);
        private final String f_314038_;
        final MapCodec<? extends ListOperation> f_314222_;

        private Type(final String p_332297_, final MapCodec<? extends ListOperation> p_336238_) {
            this.f_314038_ = p_332297_;
            this.f_314222_ = p_336238_;
        }

        public MapCodec<? extends ListOperation> m_320170_() {
            return this.f_314222_;
        }

        @Override
        public String getSerializedName() {
            return this.f_314038_;
        }
    }
}