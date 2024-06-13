package net.minecraft.network.chat;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class HoverEvent {
    public static final Codec<HoverEvent> f_303270_ = Codec.withAlternative(
            HoverEvent.TypedHoverEvent.f_303383_.codec(), HoverEvent.TypedHoverEvent.f_302885_.codec()
        )
        .xmap(HoverEvent::new, p_326065_ -> p_326065_.f_303629_);
    private final HoverEvent.TypedHoverEvent<?> f_303629_;

    public <T> HoverEvent(HoverEvent.Action<T> pAction, T pValue) {
        this(new HoverEvent.TypedHoverEvent<>(pAction, pValue));
    }

    private HoverEvent(HoverEvent.TypedHoverEvent<?> p_313245_) {
        this.f_303629_ = p_313245_;
    }

    public HoverEvent.Action<?> getAction() {
        return this.f_303629_.f_303739_;
    }

    @Nullable
    public <T> T getValue(HoverEvent.Action<T> pActionType) {
        return this.f_303629_.f_303739_ == pActionType ? pActionType.cast(this.f_303629_.f_302951_) : null;
    }

    @Override
    public boolean equals(Object pOther) {
        if (this == pOther) {
            return true;
        } else {
            return pOther != null && this.getClass() == pOther.getClass() ? ((HoverEvent)pOther).f_303629_.equals(this.f_303629_) : false;
        }
    }

    @Override
    public String toString() {
        return this.f_303629_.toString();
    }

    @Override
    public int hashCode() {
        return this.f_303629_.hashCode();
    }

    public static class Action<T> implements StringRepresentable {
        public static final HoverEvent.Action<Component> SHOW_TEXT = new HoverEvent.Action<>(
            "show_text", true, ComponentSerialization.f_303288_, (p_326066_, p_326067_) -> DataResult.success(p_326066_)
        );
        public static final HoverEvent.Action<HoverEvent.ItemStackInfo> SHOW_ITEM = new HoverEvent.Action<>(
            "show_item", true, HoverEvent.ItemStackInfo.f_303860_, HoverEvent.ItemStackInfo::m_305901_
        );
        public static final HoverEvent.Action<HoverEvent.EntityTooltipInfo> SHOW_ENTITY = new HoverEvent.Action<>(
            "show_entity", true, HoverEvent.EntityTooltipInfo.f_302309_, HoverEvent.EntityTooltipInfo::m_307449_
        );
        public static final Codec<HoverEvent.Action<?>> f_303568_ = StringRepresentable.m_306774_(
            () -> new HoverEvent.Action[]{SHOW_TEXT, SHOW_ITEM, SHOW_ENTITY}
        );
        public static final Codec<HoverEvent.Action<?>> f_302584_ = f_303568_.validate(HoverEvent.Action::m_305202_);
        private final String name;
        private final boolean allowFromServer;
        final MapCodec<HoverEvent.TypedHoverEvent<T>> f_302304_;
        final MapCodec<HoverEvent.TypedHoverEvent<T>> f_303126_;

        public Action(String pName, boolean pAllowFromServer, Codec<T> p_311195_, final HoverEvent.LegacyConverter<T> p_333454_) {
            this.name = pName;
            this.allowFromServer = pAllowFromServer;
            this.f_302304_ = p_311195_.xmap(p_308563_ -> new HoverEvent.TypedHoverEvent<>(this, (T)p_308563_), p_308564_ -> p_308564_.f_302951_)
                .fieldOf("contents");
            this.f_303126_ = (new Codec<HoverEvent.TypedHoverEvent<T>>() {
                @Override
                public <D> DataResult<Pair<HoverEvent.TypedHoverEvent<T>, D>> decode(DynamicOps<D> p_333314_, D p_328005_) {
                    return ComponentSerialization.f_303288_.decode(p_333314_, p_328005_).flatMap(p_332506_ -> {
                        DataResult<T> dataresult;
                        if (p_333314_ instanceof RegistryOps<D> registryops) {
                            dataresult = p_333454_.m_324647_(p_332506_.getFirst(), registryops);
                        } else {
                            dataresult = p_333454_.m_324647_(p_332506_.getFirst(), null);
                        }

                        return dataresult.map(p_335904_ -> Pair.of(new HoverEvent.TypedHoverEvent<>(Action.this, (T)p_335904_), (D)p_332506_.getSecond()));
                    });
                }

                public <D> DataResult<D> encode(HoverEvent.TypedHoverEvent<T> p_328207_, DynamicOps<D> p_331514_, D p_330476_) {
                    return DataResult.error(() -> "Can't encode in legacy format");
                }
            }).fieldOf("value");
        }

        public boolean isAllowedFromServer() {
            return this.allowFromServer;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        T cast(Object pParameter) {
            return (T)pParameter;
        }

        @Override
        public String toString() {
            return "<action " + this.name + ">";
        }

        private static DataResult<HoverEvent.Action<?>> m_305202_(@Nullable HoverEvent.Action<?> p_311888_) {
            if (p_311888_ == null) {
                return DataResult.error(() -> "Unknown action");
            } else {
                return !p_311888_.isAllowedFromServer() ? DataResult.error(() -> "Action not allowed: " + p_311888_) : DataResult.success(p_311888_, Lifecycle.stable());
            }
        }
    }

    public static class EntityTooltipInfo {
        public static final Codec<HoverEvent.EntityTooltipInfo> f_302309_ = RecordCodecBuilder.create(
            p_326069_ -> p_326069_.group(
                        BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("type").forGetter(p_309982_ -> p_309982_.type),
                        UUIDUtil.f_302497_.fieldOf("id").forGetter(p_312795_ -> p_312795_.id),
                        ComponentSerialization.f_303288_.lenientOptionalFieldOf("name").forGetter(p_310270_ -> p_310270_.name)
                    )
                    .apply(p_326069_, HoverEvent.EntityTooltipInfo::new)
        );
        public final EntityType<?> type;
        public final UUID id;
        public final Optional<Component> name;
        @Nullable
        private List<Component> linesCache;

        public EntityTooltipInfo(EntityType<?> pType, UUID pId, @Nullable Component pName) {
            this(pType, pId, Optional.ofNullable(pName));
        }

        public EntityTooltipInfo(EntityType<?> p_312321_, UUID p_312750_, Optional<Component> p_312078_) {
            this.type = p_312321_;
            this.id = p_312750_;
            this.name = p_312078_;
        }

        public static DataResult<HoverEvent.EntityTooltipInfo> m_307449_(Component p_312203_, @Nullable RegistryOps<?> p_330158_) {
            try {
                CompoundTag compoundtag = TagParser.parseTag(p_312203_.getString());
                DynamicOps<JsonElement> dynamicops = (DynamicOps<JsonElement>)(p_330158_ != null ? p_330158_.m_322470_(JsonOps.INSTANCE) : JsonOps.INSTANCE);
                DataResult<Component> dataresult = ComponentSerialization.f_303288_.parse(dynamicops, JsonParser.parseString(compoundtag.getString("name")));
                EntityType<?> entitytype = BuiltInRegistries.ENTITY_TYPE.get(new ResourceLocation(compoundtag.getString("type")));
                UUID uuid = UUID.fromString(compoundtag.getString("id"));
                return dataresult.map(p_326072_ -> new HoverEvent.EntityTooltipInfo(entitytype, uuid, p_326072_));
            } catch (Exception exception) {
                return DataResult.error(() -> "Failed to parse tooltip: " + exception.getMessage());
            }
        }

        public List<Component> getTooltipLines() {
            if (this.linesCache == null) {
                this.linesCache = new ArrayList<>();
                this.name.ifPresent(this.linesCache::add);
                this.linesCache.add(Component.translatable("gui.entity_tooltip.type", this.type.getDescription()));
                this.linesCache.add(Component.literal(this.id.toString()));
            }

            return this.linesCache;
        }

        @Override
        public boolean equals(Object pOther) {
            if (this == pOther) {
                return true;
            } else if (pOther != null && this.getClass() == pOther.getClass()) {
                HoverEvent.EntityTooltipInfo hoverevent$entitytooltipinfo = (HoverEvent.EntityTooltipInfo)pOther;
                return this.type.equals(hoverevent$entitytooltipinfo.type)
                    && this.id.equals(hoverevent$entitytooltipinfo.id)
                    && this.name.equals(hoverevent$entitytooltipinfo.name);
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            int i = this.type.hashCode();
            i = 31 * i + this.id.hashCode();
            return 31 * i + this.name.hashCode();
        }
    }

    public static class ItemStackInfo {
        public static final Codec<HoverEvent.ItemStackInfo> f_303661_ = ItemStack.CODEC
            .xmap(HoverEvent.ItemStackInfo::new, HoverEvent.ItemStackInfo::getItemStack);
        private static final Codec<HoverEvent.ItemStackInfo> f_313999_ = ItemStack.f_315943_
            .xmap(HoverEvent.ItemStackInfo::new, HoverEvent.ItemStackInfo::getItemStack);
        public static final Codec<HoverEvent.ItemStackInfo> f_303860_ = Codec.withAlternative(f_303661_, f_313999_);
        private final Holder<Item> item;
        private final int count;
        private final DataComponentPatch f_316124_;
        @Nullable
        private ItemStack itemStack;

        ItemStackInfo(Holder<Item> p_328208_, int p_311558_, DataComponentPatch p_327933_) {
            this.item = p_328208_;
            this.count = p_311558_;
            this.f_316124_ = p_327933_;
        }

        public ItemStackInfo(ItemStack pStack) {
            this(pStack.getItemHolder(), pStack.getCount(), pStack.m_324277_());
        }

        @Override
        public boolean equals(Object pOther) {
            if (this == pOther) {
                return true;
            } else if (pOther != null && this.getClass() == pOther.getClass()) {
                HoverEvent.ItemStackInfo hoverevent$itemstackinfo = (HoverEvent.ItemStackInfo)pOther;
                return this.count == hoverevent$itemstackinfo.count
                    && this.item.equals(hoverevent$itemstackinfo.item)
                    && this.f_316124_.equals(hoverevent$itemstackinfo.f_316124_);
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            int i = this.item.hashCode();
            i = 31 * i + this.count;
            return 31 * i + this.f_316124_.hashCode();
        }

        public ItemStack getItemStack() {
            if (this.itemStack == null) {
                this.itemStack = new ItemStack(this.item, this.count, this.f_316124_);
            }

            return this.itemStack;
        }

        private static DataResult<HoverEvent.ItemStackInfo> m_305901_(Component p_309792_, @Nullable RegistryOps<?> p_329820_) {
            try {
                CompoundTag compoundtag = TagParser.parseTag(p_309792_.getString());
                DynamicOps<Tag> dynamicops = (DynamicOps<Tag>)(p_329820_ != null ? p_329820_.m_322470_(NbtOps.INSTANCE) : NbtOps.INSTANCE);
                return ItemStack.CODEC.parse(dynamicops, compoundtag).map(HoverEvent.ItemStackInfo::new);
            } catch (CommandSyntaxException commandsyntaxexception) {
                return DataResult.error(() -> "Failed to parse item tag: " + commandsyntaxexception.getMessage());
            }
        }
    }

    public interface LegacyConverter<T> {
        DataResult<T> m_324647_(Component p_329531_, @Nullable RegistryOps<?> p_327865_);
    }

    static record TypedHoverEvent<T>(HoverEvent.Action<T> f_303739_, T f_302951_) {
        public static final MapCodec<HoverEvent.TypedHoverEvent<?>> f_303383_ = HoverEvent.Action.f_302584_
            .dispatchMap("action", HoverEvent.TypedHoverEvent::f_303739_, p_326074_ -> p_326074_.f_302304_);
        public static final MapCodec<HoverEvent.TypedHoverEvent<?>> f_302885_ = HoverEvent.Action.f_302584_
            .dispatchMap("action", HoverEvent.TypedHoverEvent::f_303739_, p_326073_ -> p_326073_.f_303126_);
    }
}