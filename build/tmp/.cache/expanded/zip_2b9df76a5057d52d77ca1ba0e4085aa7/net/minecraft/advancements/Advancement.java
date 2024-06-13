package net.minecraft.advancements;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.critereon.CriterionValidator;
import net.minecraft.core.HolderGetter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public record Advancement(
    Optional<ResourceLocation> parent,
    Optional<DisplayInfo> display,
    AdvancementRewards rewards,
    Map<String, Criterion<?>> criteria,
    AdvancementRequirements requirements,
    boolean sendsTelemetryEvent,
    Optional<Component> name
) {
    private static final Codec<Map<String, Criterion<?>>> f_302506_ = Codec.unboundedMap(Codec.STRING, Criterion.f_303845_)
        .validate(p_308091_ -> p_308091_.isEmpty() ? DataResult.error(() -> "Advancement criteria cannot be empty") : DataResult.success(p_308091_));
    public static final Codec<Advancement> f_303179_ = RecordCodecBuilder.<Advancement>create(
            p_325179_ -> p_325179_.group(
                        ResourceLocation.CODEC.optionalFieldOf("parent").forGetter(Advancement::parent),
                        DisplayInfo.f_302437_.optionalFieldOf("display").forGetter(Advancement::display),
                        AdvancementRewards.f_303002_.optionalFieldOf("rewards", AdvancementRewards.EMPTY).forGetter(Advancement::rewards),
                        f_302506_.fieldOf("criteria").forGetter(Advancement::criteria),
                        AdvancementRequirements.f_302300_.optionalFieldOf("requirements").forGetter(p_308099_ -> Optional.of(p_308099_.requirements())),
                        Codec.BOOL.optionalFieldOf("sends_telemetry_event", Boolean.valueOf(false)).forGetter(Advancement::sendsTelemetryEvent)
                    )
                    .apply(p_325179_, (p_308085_, p_308086_, p_308087_, p_308088_, p_308089_, p_308090_) -> {
                        AdvancementRequirements advancementrequirements = p_308089_.orElseGet(() -> AdvancementRequirements.allOf(p_308088_.keySet()));
                        return new Advancement(p_308085_, p_308086_, p_308087_, p_308088_, advancementrequirements, p_308090_);
                    })
        )
        .validate(Advancement::m_307846_);
    public static final StreamCodec<RegistryFriendlyByteBuf, Advancement> f_315156_ = StreamCodec.m_324771_(Advancement::write, Advancement::read);

    public Advancement(
        Optional<ResourceLocation> pParent,
        Optional<DisplayInfo> pDisplay,
        AdvancementRewards pRewards,
        Map<String, Criterion<?>> pCriteria,
        AdvancementRequirements pRequirements,
        boolean pSendsTelemetryEvent
    ) {
        this(pParent, pDisplay, pRewards, Map.copyOf(pCriteria), pRequirements, pSendsTelemetryEvent, pDisplay.map(Advancement::decorateName));
    }

    private static DataResult<Advancement> m_307846_(Advancement p_312373_) {
        return p_312373_.requirements().m_305436_(p_312373_.criteria().keySet()).map(p_308094_ -> p_312373_);
    }

    private static Component decorateName(DisplayInfo p_300038_) {
        Component component = p_300038_.getTitle();
        ChatFormatting chatformatting = p_300038_.m_306629_().m_305069_();
        Component component1 = ComponentUtils.mergeStyles(component.copy(), Style.EMPTY.withColor(chatformatting))
            .append("\n")
            .append(p_300038_.getDescription());
        Component component2 = component.copy().withStyle(p_138316_ -> p_138316_.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, component1)));
        return ComponentUtils.wrapInSquareBrackets(component2).withStyle(chatformatting);
    }

    public static Component name(AdvancementHolder pAdvancement) {
        return pAdvancement.value().name().orElseGet(() -> Component.literal(pAdvancement.id().toString()));
    }

    private void write(RegistryFriendlyByteBuf p_328062_) {
        p_328062_.writeOptional(this.parent, FriendlyByteBuf::writeResourceLocation);
        DisplayInfo.f_314089_.m_321801_(ByteBufCodecs::m_319027_).m_318638_(p_328062_, this.display);
        this.requirements.write(p_328062_);
        p_328062_.writeBoolean(this.sendsTelemetryEvent);
    }

    private static Advancement read(RegistryFriendlyByteBuf p_328348_) {
        return new Advancement(
            p_328348_.readOptional(FriendlyByteBuf::readResourceLocation),
            (Optional<DisplayInfo>)DisplayInfo.f_314089_.m_321801_(ByteBufCodecs::m_319027_).m_318688_(p_328348_),
            AdvancementRewards.EMPTY,
            Map.of(),
            new AdvancementRequirements(p_328348_),
            p_328348_.readBoolean()
        );
    }

    public boolean isRoot() {
        return this.parent.isEmpty();
    }

    public void m_306920_(ProblemReporter p_310503_, HolderGetter.Provider p_335087_) {
        this.criteria.forEach((p_325177_, p_325178_) -> {
            CriterionValidator criterionvalidator = new CriterionValidator(p_310503_.m_306146_(p_325177_), p_335087_);
            p_325178_.triggerInstance().serializeToJson(criterionvalidator);
        });
    }

    public static class Builder {
        private Optional<ResourceLocation> parent = Optional.empty();
        private Optional<DisplayInfo> display = Optional.empty();
        private AdvancementRewards rewards = AdvancementRewards.EMPTY;
        private final ImmutableMap.Builder<String, Criterion<?>> criteria = ImmutableMap.builder();
        private Optional<AdvancementRequirements> requirements = Optional.empty();
        private AdvancementRequirements.Strategy requirementsStrategy = AdvancementRequirements.Strategy.AND;
        private boolean sendsTelemetryEvent;

        public static Advancement.Builder advancement() {
            return new Advancement.Builder().sendsTelemetryEvent();
        }

        public static Advancement.Builder recipeAdvancement() {
            return new Advancement.Builder();
        }

        public Advancement.Builder parent(AdvancementHolder pParent) {
            this.parent = Optional.of(pParent.id());
            return this;
        }

        @Deprecated(
            forRemoval = true
        )
        public Advancement.Builder parent(ResourceLocation pParentId) {
            this.parent = Optional.of(pParentId);
            return this;
        }

        public Advancement.Builder display(
            ItemStack pStack,
            Component pTitle,
            Component pDescription,
            @Nullable ResourceLocation pBackground,
            AdvancementType p_310090_,
            boolean pShowToast,
            boolean pAnnounceToChat,
            boolean pHidden
        ) {
            return this.display(new DisplayInfo(pStack, pTitle, pDescription, Optional.ofNullable(pBackground), p_310090_, pShowToast, pAnnounceToChat, pHidden));
        }

        public Advancement.Builder display(
            ItemLike pItem,
            Component pTitle,
            Component pDescription,
            @Nullable ResourceLocation pBackground,
            AdvancementType p_309840_,
            boolean pShowToast,
            boolean pAnnounceToChat,
            boolean pHidden
        ) {
            return this.display(
                new DisplayInfo(
                    new ItemStack(pItem.asItem()), pTitle, pDescription, Optional.ofNullable(pBackground), p_309840_, pShowToast, pAnnounceToChat, pHidden
                )
            );
        }

        public Advancement.Builder display(DisplayInfo pDisplay) {
            this.display = Optional.of(pDisplay);
            return this;
        }

        public Advancement.Builder rewards(AdvancementRewards.Builder pRewardsBuilder) {
            return this.rewards(pRewardsBuilder.build());
        }

        public Advancement.Builder rewards(AdvancementRewards pRewards) {
            this.rewards = pRewards;
            return this;
        }

        public Advancement.Builder addCriterion(String pKey, Criterion<?> pCriterion) {
            this.criteria.put(pKey, pCriterion);
            return this;
        }

        public Advancement.Builder requirements(AdvancementRequirements.Strategy pRequirementsStrategy) {
            this.requirementsStrategy = pRequirementsStrategy;
            return this;
        }

        public Advancement.Builder requirements(AdvancementRequirements pRequirements) {
            this.requirements = Optional.of(pRequirements);
            return this;
        }

        public Advancement.Builder sendsTelemetryEvent() {
            this.sendsTelemetryEvent = true;
            return this;
        }

        public AdvancementHolder build(ResourceLocation pId) {
            Map<String, Criterion<?>> map = this.criteria.buildOrThrow();
            AdvancementRequirements advancementrequirements = this.requirements.orElseGet(() -> this.requirementsStrategy.create(map.keySet()));
            return new AdvancementHolder(
                pId, new Advancement(this.parent, this.display, this.rewards, map, advancementrequirements, this.sendsTelemetryEvent)
            );
        }

        public AdvancementHolder save(Consumer<AdvancementHolder> pOutput, String pId) {
            return save(pOutput, new ResourceLocation(pId));
        }

        public AdvancementHolder save(Consumer<AdvancementHolder> pOutput, ResourceLocation id) {
            AdvancementHolder advancementholder = this.build(id);
            pOutput.accept(advancementholder);
            return advancementholder;
        }
    }
}
