package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.nbt.NbtProvider;
import net.minecraft.world.level.storage.loot.providers.nbt.NbtProviders;
import org.apache.commons.lang3.mutable.MutableObject;

public class CopyCustomDataFunction extends LootItemConditionalFunction {
    public static final MapCodec<CopyCustomDataFunction> f_314264_ = RecordCodecBuilder.mapCodec(
        p_334162_ -> commonFields(p_334162_)
                .and(
                    p_334162_.group(
                        NbtProviders.CODEC.fieldOf("source").forGetter(p_330558_ -> p_330558_.f_316901_),
                        CopyCustomDataFunction.CopyOperation.f_315736_.listOf().fieldOf("ops").forGetter(p_327675_ -> p_327675_.f_315899_)
                    )
                )
                .apply(p_334162_, CopyCustomDataFunction::new)
    );
    private final NbtProvider f_316901_;
    private final List<CopyCustomDataFunction.CopyOperation> f_315899_;

    CopyCustomDataFunction(List<LootItemCondition> p_330573_, NbtProvider p_334617_, List<CopyCustomDataFunction.CopyOperation> p_334520_) {
        super(p_330573_);
        this.f_316901_ = p_334617_;
        this.f_315899_ = List.copyOf(p_334520_);
    }

    @Override
    public LootItemFunctionType<CopyCustomDataFunction> getType() {
        return LootItemFunctions.f_315380_;
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return this.f_316901_.getReferencedContextParams();
    }

    @Override
    public ItemStack run(ItemStack p_333117_, LootContext p_334578_) {
        Tag tag = this.f_316901_.get(p_334578_);
        if (tag == null) {
            return p_333117_;
        } else {
            MutableObject<CompoundTag> mutableobject = new MutableObject<>();
            Supplier<Tag> supplier = () -> {
                if (mutableobject.getValue() == null) {
                    mutableobject.setValue(p_333117_.m_322304_(DataComponents.f_316665_, CustomData.f_317060_).m_323330_());
                }

                return mutableobject.getValue();
            };
            this.f_315899_.forEach(p_329887_ -> p_329887_.m_322292_(supplier, tag));
            CompoundTag compoundtag = mutableobject.getValue();
            if (compoundtag != null) {
                CustomData.m_323150_(DataComponents.f_316665_, p_333117_, compoundtag);
            }

            return p_333117_;
        }
    }

    @Deprecated
    public static CopyCustomDataFunction.Builder m_324427_(NbtProvider p_335021_) {
        return new CopyCustomDataFunction.Builder(p_335021_);
    }

    public static CopyCustomDataFunction.Builder m_319309_(LootContext.EntityTarget p_329362_) {
        return new CopyCustomDataFunction.Builder(ContextNbtProvider.forContextEntity(p_329362_));
    }

    public static class Builder extends LootItemConditionalFunction.Builder<CopyCustomDataFunction.Builder> {
        private final NbtProvider f_314398_;
        private final List<CopyCustomDataFunction.CopyOperation> f_314900_ = Lists.newArrayList();

        Builder(NbtProvider p_328406_) {
            this.f_314398_ = p_328406_;
        }

        public CopyCustomDataFunction.Builder m_322757_(String p_331311_, String p_335916_, CopyCustomDataFunction.MergeStrategy p_332655_) {
            try {
                this.f_314900_
                    .add(
                        new CopyCustomDataFunction.CopyOperation(
                            NbtPathArgument.NbtPath.m_324408_(p_331311_), NbtPathArgument.NbtPath.m_324408_(p_335916_), p_332655_
                        )
                    );
                return this;
            } catch (CommandSyntaxException commandsyntaxexception) {
                throw new IllegalArgumentException(commandsyntaxexception);
            }
        }

        public CopyCustomDataFunction.Builder m_320941_(String p_333187_, String p_327847_) {
            return this.m_322757_(p_333187_, p_327847_, CopyCustomDataFunction.MergeStrategy.REPLACE);
        }

        protected CopyCustomDataFunction.Builder getThis() {
            return this;
        }

        @Override
        public LootItemFunction build() {
            return new CopyCustomDataFunction(this.getConditions(), this.f_314398_, this.f_314900_);
        }
    }

    static record CopyOperation(NbtPathArgument.NbtPath f_316253_, NbtPathArgument.NbtPath f_315689_, CopyCustomDataFunction.MergeStrategy f_314952_) {
        public static final Codec<CopyCustomDataFunction.CopyOperation> f_315736_ = RecordCodecBuilder.create(
            p_333172_ -> p_333172_.group(
                        NbtPathArgument.NbtPath.f_314983_.fieldOf("source").forGetter(CopyCustomDataFunction.CopyOperation::f_316253_),
                        NbtPathArgument.NbtPath.f_314983_.fieldOf("target").forGetter(CopyCustomDataFunction.CopyOperation::f_315689_),
                        CopyCustomDataFunction.MergeStrategy.f_316527_.fieldOf("op").forGetter(CopyCustomDataFunction.CopyOperation::f_314952_)
                    )
                    .apply(p_333172_, CopyCustomDataFunction.CopyOperation::new)
        );

        public void m_322292_(Supplier<Tag> p_328581_, Tag p_331330_) {
            try {
                List<Tag> list = this.f_316253_.get(p_331330_);
                if (!list.isEmpty()) {
                    this.f_314952_.m_321897_(p_328581_.get(), this.f_315689_, list);
                }
            } catch (CommandSyntaxException commandsyntaxexception) {
            }
        }
    }

    public static enum MergeStrategy implements StringRepresentable {
        REPLACE("replace") {
            @Override
            public void m_321897_(Tag p_327968_, NbtPathArgument.NbtPath p_329545_, List<Tag> p_330977_) throws CommandSyntaxException {
                p_329545_.set(p_327968_, Iterables.getLast(p_330977_));
            }
        },
        APPEND("append") {
            @Override
            public void m_321897_(Tag p_334866_, NbtPathArgument.NbtPath p_330111_, List<Tag> p_331184_) throws CommandSyntaxException {
                List<Tag> list = p_330111_.getOrCreate(p_334866_, ListTag::new);
                list.forEach(p_328852_ -> {
                    if (p_328852_ instanceof ListTag) {
                        p_331184_.forEach(p_333613_ -> ((ListTag)p_328852_).add(p_333613_.copy()));
                    }
                });
            }
        },
        MERGE("merge") {
            @Override
            public void m_321897_(Tag p_330874_, NbtPathArgument.NbtPath p_329263_, List<Tag> p_336007_) throws CommandSyntaxException {
                List<Tag> list = p_329263_.getOrCreate(p_330874_, CompoundTag::new);
                list.forEach(p_328276_ -> {
                    if (p_328276_ instanceof CompoundTag) {
                        p_336007_.forEach(p_330167_ -> {
                            if (p_330167_ instanceof CompoundTag) {
                                ((CompoundTag)p_328276_).merge((CompoundTag)p_330167_);
                            }
                        });
                    }
                });
            }
        };

        public static final Codec<CopyCustomDataFunction.MergeStrategy> f_316527_ = StringRepresentable.fromEnum(CopyCustomDataFunction.MergeStrategy::values);
        private final String f_314564_;

        public abstract void m_321897_(Tag p_335447_, NbtPathArgument.NbtPath p_334662_, List<Tag> p_335924_) throws CommandSyntaxException;

        MergeStrategy(final String p_328833_) {
            this.f_314564_ = p_328833_;
        }

        @Override
        public String getSerializedName() {
            return this.f_314564_;
        }
    }
}