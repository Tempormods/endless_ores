package net.minecraft.network.chat;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.stream.JsonReader;
import com.mojang.brigadier.Message;
import com.mojang.serialization.JsonOps;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.contents.DataSource;
import net.minecraft.network.chat.contents.KeybindContents;
import net.minecraft.network.chat.contents.NbtContents;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.network.chat.contents.ScoreContents;
import net.minecraft.network.chat.contents.SelectorContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.ChunkPos;

public interface Component extends Message, FormattedText {
    Style getStyle();

    ComponentContents getContents();

    @Override
    default String getString() {
        return FormattedText.super.getString();
    }

    default String getString(int pMaxLength) {
        StringBuilder stringbuilder = new StringBuilder();
        this.visit(p_130673_ -> {
            int i = pMaxLength - stringbuilder.length();
            if (i <= 0) {
                return STOP_ITERATION;
            } else {
                stringbuilder.append(p_130673_.length() <= i ? p_130673_ : p_130673_.substring(0, i));
                return Optional.empty();
            }
        });
        return stringbuilder.toString();
    }

    List<Component> getSiblings();

    @Nullable
    default String m_306448_() {
        if (this.getContents() instanceof PlainTextContents plaintextcontents && this.getSiblings().isEmpty() && this.getStyle().isEmpty()) {
            return plaintextcontents.m_305315_();
        }

        return null;
    }

    default MutableComponent plainCopy() {
        return MutableComponent.create(this.getContents());
    }

    default MutableComponent copy() {
        return new MutableComponent(this.getContents(), new ArrayList<>(this.getSiblings()), this.getStyle());
    }

    FormattedCharSequence getVisualOrderText();

    @Override
    default <T> Optional<T> visit(FormattedText.StyledContentConsumer<T> pAcceptor, Style pStyle) {
        Style style = this.getStyle().applyTo(pStyle);
        Optional<T> optional = this.getContents().visit(pAcceptor, style);
        if (optional.isPresent()) {
            return optional;
        } else {
            for (Component component : this.getSiblings()) {
                Optional<T> optional1 = component.visit(pAcceptor, style);
                if (optional1.isPresent()) {
                    return optional1;
                }
            }

            return Optional.empty();
        }
    }

    @Override
    default <T> Optional<T> visit(FormattedText.ContentConsumer<T> pAcceptor) {
        Optional<T> optional = this.getContents().visit(pAcceptor);
        if (optional.isPresent()) {
            return optional;
        } else {
            for (Component component : this.getSiblings()) {
                Optional<T> optional1 = component.visit(pAcceptor);
                if (optional1.isPresent()) {
                    return optional1;
                }
            }

            return Optional.empty();
        }
    }

    default List<Component> toFlatList() {
        return this.toFlatList(Style.EMPTY);
    }

    default List<Component> toFlatList(Style pStyle) {
        List<Component> list = Lists.newArrayList();
        this.visit((p_178403_, p_178404_) -> {
            if (!p_178404_.isEmpty()) {
                list.add(literal(p_178404_).withStyle(p_178403_));
            }

            return Optional.empty();
        }, pStyle);
        return list;
    }

    default boolean contains(Component pOther) {
        if (this.equals(pOther)) {
            return true;
        } else {
            List<Component> list = this.toFlatList();
            List<Component> list1 = pOther.toFlatList(this.getStyle());
            return Collections.indexOfSubList(list, list1) != -1;
        }
    }

    static Component nullToEmpty(@Nullable String pText) {
        return (Component)(pText != null ? literal(pText) : CommonComponents.EMPTY);
    }

    static MutableComponent literal(String pText) {
        return MutableComponent.create(PlainTextContents.m_307377_(pText));
    }

    static MutableComponent translatable(String pKey) {
        return MutableComponent.create(new TranslatableContents(pKey, null, TranslatableContents.NO_ARGS));
    }

    static MutableComponent translatable(String pKey, Object... pArgs) {
        return MutableComponent.create(new TranslatableContents(pKey, null, pArgs));
    }

    static MutableComponent m_307043_(String p_312579_, Object... p_312922_) {
        for (int i = 0; i < p_312922_.length; i++) {
            Object object = p_312922_[i];
            if (!TranslatableContents.m_306839_(object) && !(object instanceof Component)) {
                p_312922_[i] = String.valueOf(object);
            }
        }

        return translatable(p_312579_, p_312922_);
    }

    static MutableComponent translatableWithFallback(String pKey, @Nullable String pFallback) {
        return MutableComponent.create(new TranslatableContents(pKey, pFallback, TranslatableContents.NO_ARGS));
    }

    static MutableComponent translatableWithFallback(String pKey, @Nullable String pFallback, Object... pArgs) {
        return MutableComponent.create(new TranslatableContents(pKey, pFallback, pArgs));
    }

    static MutableComponent empty() {
        return MutableComponent.create(PlainTextContents.f_302486_);
    }

    static MutableComponent keybind(String pName) {
        return MutableComponent.create(new KeybindContents(pName));
    }

    static MutableComponent nbt(String pNbtPathPattern, boolean pInterpreting, Optional<Component> pSeparator, DataSource pDataSource) {
        return MutableComponent.create(new NbtContents(pNbtPathPattern, pInterpreting, pSeparator, pDataSource));
    }

    static MutableComponent score(String pName, String pObjective) {
        return MutableComponent.create(new ScoreContents(pName, pObjective));
    }

    static MutableComponent selector(String pPattern, Optional<Component> pSeparator) {
        return MutableComponent.create(new SelectorContents(pPattern, pSeparator));
    }

    static Component m_306983_(Date p_313239_) {
        return literal(p_313239_.toString());
    }

    static Component m_304916_(Message p_312086_) {
        return (Component)(p_312086_ instanceof Component component ? component : literal(p_312086_.getString()));
    }

    static Component m_306730_(UUID p_311149_) {
        return literal(p_311149_.toString());
    }

    static Component m_305236_(ResourceLocation p_311439_) {
        return literal(p_311439_.toString());
    }

    static Component m_307221_(ChunkPos p_312850_) {
        return literal(p_312850_.toString());
    }

    public static class Serializer {
        private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

        private Serializer() {
        }

        static MutableComponent m_130719_(JsonElement p_130720_, HolderLookup.Provider p_334184_) {
            return (MutableComponent)ComponentSerialization.f_303288_
                .parse(p_334184_.m_318927_(JsonOps.INSTANCE), p_130720_)
                .getOrThrow(JsonParseException::new);
        }

        static JsonElement m_130705_(Component pSrc, HolderLookup.Provider p_332074_) {
            return ComponentSerialization.f_303288_.encodeStart(p_332074_.m_318927_(JsonOps.INSTANCE), pSrc).getOrThrow(JsonParseException::new);
        }

        public static String toJson(Component pComponent, HolderLookup.Provider p_334954_) {
            return GSON.toJson(m_130705_(pComponent, p_334954_));
        }

        @Nullable
        public static MutableComponent fromJson(String p_332445_, HolderLookup.Provider p_334661_) {
            JsonElement jsonelement = JsonParser.parseString(p_332445_);
            return jsonelement == null ? null : m_130719_(jsonelement, p_334661_);
        }

        @Nullable
        public static MutableComponent fromJson(@Nullable JsonElement p_330936_, HolderLookup.Provider p_331821_) {
            return p_330936_ == null ? null : m_130719_(p_330936_, p_331821_);
        }

        @Nullable
        public static MutableComponent fromJsonLenient(String pJson, HolderLookup.Provider p_335522_) {
            JsonReader jsonreader = new JsonReader(new StringReader(pJson));
            jsonreader.setLenient(true);
            JsonElement jsonelement = JsonParser.parseReader(jsonreader);
            return jsonelement == null ? null : m_130719_(jsonelement, p_335522_);
        }
    }

    public static class SerializerAdapter implements JsonDeserializer<MutableComponent>, JsonSerializer<Component> {
        private final HolderLookup.Provider f_315688_;

        public SerializerAdapter(HolderLookup.Provider p_330707_) {
            this.f_315688_ = p_330707_;
        }

        public MutableComponent deserialize(JsonElement p_311708_, Type p_310257_, JsonDeserializationContext p_310325_) throws JsonParseException {
            return Component.Serializer.m_130719_(p_311708_, this.f_315688_);
        }

        public JsonElement serialize(Component p_309493_, Type p_310679_, JsonSerializationContext p_312693_) {
            return Component.Serializer.m_130705_(p_309493_, this.f_315688_);
        }
    }
}