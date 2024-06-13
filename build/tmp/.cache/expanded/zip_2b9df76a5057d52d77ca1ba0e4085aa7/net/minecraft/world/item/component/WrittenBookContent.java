package net.minecraft.world.item.component;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.network.Filterable;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.player.Player;

public record WrittenBookContent(Filterable<String> f_316867_, String f_316008_, int f_314404_, List<Filterable<Component>> f_314269_, boolean f_316486_)
    implements BookContent<Component, WrittenBookContent> {
    public static final WrittenBookContent f_315751_ = new WrittenBookContent(Filterable.m_323001_(""), "", 0, List.of(), true);
    public static final int f_316608_ = 32767;
    public static final int f_314147_ = 16;
    public static final int f_314437_ = 32;
    public static final int f_314113_ = 3;
    public static final int f_316111_ = 2;
    public static final Codec<Component> f_315642_ = ComponentSerialization.m_324597_(32767);
    public static final Codec<List<Filterable<Component>>> f_316151_ = m_322748_(f_315642_);
    public static final Codec<WrittenBookContent> f_315766_ = RecordCodecBuilder.create(
        p_329738_ -> p_329738_.group(
                    Filterable.m_322486_(Codec.string(0, 32)).fieldOf("title").forGetter(WrittenBookContent::f_316867_),
                    Codec.STRING.fieldOf("author").forGetter(WrittenBookContent::f_316008_),
                    ExtraCodecs.intRange(0, 3).optionalFieldOf("generation", 0).forGetter(WrittenBookContent::f_314404_),
                    f_316151_.optionalFieldOf("pages", List.of()).forGetter(WrittenBookContent::m_319402_),
                    Codec.BOOL.optionalFieldOf("resolved", Boolean.valueOf(false)).forGetter(WrittenBookContent::f_316486_)
                )
                .apply(p_329738_, WrittenBookContent::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, WrittenBookContent> f_315227_ = StreamCodec.m_319894_(
        Filterable.m_323964_(ByteBufCodecs.m_319534_(32)),
        WrittenBookContent::f_316867_,
        ByteBufCodecs.f_315450_,
        WrittenBookContent::f_316008_,
        ByteBufCodecs.f_316730_,
        WrittenBookContent::f_314404_,
        Filterable.m_323964_(ComponentSerialization.f_315335_).m_321801_(ByteBufCodecs.m_324765_()),
        WrittenBookContent::m_319402_,
        ByteBufCodecs.f_315514_,
        WrittenBookContent::f_316486_,
        WrittenBookContent::new
    );

    public WrittenBookContent(Filterable<String> f_316867_, String f_316008_, int f_314404_, List<Filterable<Component>> f_314269_, boolean f_316486_) {
        if (f_314404_ >= 0 && f_314404_ <= 3) {
            this.f_316867_ = f_316867_;
            this.f_316008_ = f_316008_;
            this.f_314404_ = f_314404_;
            this.f_314269_ = f_314269_;
            this.f_316486_ = f_316486_;
        } else {
            throw new IllegalArgumentException("Generation was " + f_314404_ + ", but must be between 0 and 3");
        }
    }

    private static Codec<Filterable<Component>> m_323953_(Codec<Component> p_335093_) {
        return Filterable.m_322486_(p_335093_);
    }

    public static Codec<List<Filterable<Component>>> m_322748_(Codec<Component> p_329056_) {
        return m_323953_(p_329056_).listOf();
    }

    @Nullable
    public WrittenBookContent m_319355_() {
        return this.f_314404_ >= 2 ? null : new WrittenBookContent(this.f_316867_, this.f_316008_, this.f_314404_ + 1, this.f_314269_, this.f_316486_);
    }

    @Nullable
    public WrittenBookContent m_318598_(CommandSourceStack p_333228_, @Nullable Player p_329707_) {
        if (this.f_316486_) {
            return null;
        } else {
            Builder<Filterable<Component>> builder = ImmutableList.builderWithExpectedSize(this.f_314269_.size());

            for (Filterable<Component> filterable : this.f_314269_) {
                Optional<Filterable<Component>> optional = m_321796_(p_333228_, p_329707_, filterable);
                if (optional.isEmpty()) {
                    return null;
                }

                builder.add(optional.get());
            }

            return new WrittenBookContent(this.f_316867_, this.f_316008_, this.f_314404_, builder.build(), true);
        }
    }

    public WrittenBookContent m_321462_() {
        return new WrittenBookContent(this.f_316867_, this.f_316008_, this.f_314404_, this.f_314269_, true);
    }

    private static Optional<Filterable<Component>> m_321796_(CommandSourceStack p_335264_, @Nullable Player p_333342_, Filterable<Component> p_328841_) {
        return p_328841_.m_320562_(p_335765_ -> {
            try {
                Component component = ComponentUtils.updateForEntity(p_335264_, p_335765_, p_333342_, 0);
                return m_324383_(component, p_335264_.registryAccess()) ? Optional.empty() : Optional.of(component);
            } catch (Exception exception) {
                return Optional.of(p_335765_);
            }
        });
    }

    private static boolean m_324383_(Component p_330243_, HolderLookup.Provider p_333440_) {
        return Component.Serializer.toJson(p_330243_, p_333440_).length() > 32767;
    }

    public List<Component> m_323359_(boolean p_335499_) {
        return Lists.transform(this.f_314269_, p_330517_ -> p_330517_.m_323302_(p_335499_));
    }

    public WrittenBookContent m_319955_(List<Filterable<Component>> p_330066_) {
        return new WrittenBookContent(this.f_316867_, this.f_316008_, this.f_314404_, p_330066_, false);
    }

    @Override
    public List<Filterable<Component>> m_319402_() {
        return this.f_314269_;
    }
}