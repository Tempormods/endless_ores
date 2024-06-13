package net.minecraft.world.item;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.DataResult.Error;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.util.NullOps;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.DigDurabilityEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.saveddata.maps.MapId;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.slf4j.Logger;

public final class ItemStack implements DataComponentHolder, net.minecraftforge.common.extensions.IForgeItemStack {
    public static final Codec<Holder<Item>> f_303113_ = BuiltInRegistries.ITEM
        .holderByNameCodec()
        .validate(
            p_327177_ -> p_327177_.m_318604_(Items.AIR.builtInRegistryHolder())
                    ? DataResult.error(() -> "Item must not be minecraft:air")
                    : DataResult.success(p_327177_)
        );
    public static final Codec<ItemStack> CODEC = Codec.lazyInitialized(
        () -> RecordCodecBuilder.create(
                p_327163_ -> p_327163_.group(
                            f_303113_.fieldOf("id").forGetter(ItemStack::getItemHolder),
                            ExtraCodecs.POSITIVE_INT.fieldOf("count").orElse(1).forGetter(ItemStack::getCount),
                            DataComponentPatch.f_315187_
                                .optionalFieldOf("components", DataComponentPatch.f_315512_)
                                .forGetter(p_327171_ -> p_327171_.f_315342_.m_320212_())
                        )
                        .apply(p_327163_, ItemStack::new)
            )
    );
    public static final Codec<ItemStack> f_302992_ = Codec.lazyInitialized(
        () -> RecordCodecBuilder.create(
                p_327178_ -> p_327178_.group(
                            f_303113_.fieldOf("id").forGetter(ItemStack::getItemHolder),
                            DataComponentPatch.f_315187_
                                .optionalFieldOf("components", DataComponentPatch.f_315512_)
                                .forGetter(p_327155_ -> p_327155_.f_315342_.m_320212_())
                        )
                        .apply(p_327178_, (p_327172_, p_327173_) -> new ItemStack(p_327172_, 1, p_327173_))
            )
    );
    public static final Codec<ItemStack> f_315780_ = CODEC.validate(ItemStack::m_323584_);
    public static final Codec<ItemStack> f_316270_ = f_302992_.validate(ItemStack::m_323584_);
    public static final Codec<ItemStack> f_316315_ = ExtraCodecs.m_319082_(CODEC)
        .xmap(p_327153_ -> p_327153_.orElse(ItemStack.EMPTY), p_327154_ -> p_327154_.isEmpty() ? Optional.empty() : Optional.of(p_327154_));
    public static final Codec<ItemStack> f_315943_ = f_303113_.xmap(ItemStack::new, ItemStack::getItemHolder);
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemStack> f_314979_ = new StreamCodec<RegistryFriendlyByteBuf, ItemStack>() {
        private static final StreamCodec<RegistryFriendlyByteBuf, Holder<Item>> f_315034_ = ByteBufCodecs.m_322636_(Registries.ITEM);

        public ItemStack m_318688_(RegistryFriendlyByteBuf p_328393_) {
            int i = p_328393_.readVarInt();
            if (i <= 0) {
                return ItemStack.EMPTY;
            } else {
                Holder<Item> holder = f_315034_.m_318688_(p_328393_);
                DataComponentPatch datacomponentpatch = DataComponentPatch.f_314779_.m_318688_(p_328393_);
                return new ItemStack(holder, i, datacomponentpatch);
            }
        }

        public void m_318638_(RegistryFriendlyByteBuf p_332266_, ItemStack p_335702_) {
            if (p_335702_.isEmpty()) {
                p_332266_.writeVarInt(0);
            } else {
                p_332266_.writeVarInt(p_335702_.getCount());
                f_315034_.m_318638_(p_332266_, p_335702_.getItemHolder());
                DataComponentPatch.f_314779_.m_318638_(p_332266_, p_335702_.f_315342_.m_320212_());
            }
        }
    };
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemStack> f_315801_ = new StreamCodec<RegistryFriendlyByteBuf, ItemStack>() {
        public ItemStack m_318688_(RegistryFriendlyByteBuf p_327992_) {
            ItemStack itemstack = ItemStack.f_314979_.m_318688_(p_327992_);
            if (itemstack.isEmpty()) {
                throw new DecoderException("Empty ItemStack not allowed");
            } else {
                return itemstack;
            }
        }

        public void m_318638_(RegistryFriendlyByteBuf p_331904_, ItemStack p_328866_) {
            if (p_328866_.isEmpty()) {
                throw new EncoderException("Empty ItemStack not allowed");
            } else {
                ItemStack.f_314979_.m_318638_(p_331904_, p_328866_);
            }
        }
    };
    public static final StreamCodec<RegistryFriendlyByteBuf, List<ItemStack>> f_315592_ = f_314979_.m_321801_(ByteBufCodecs.m_323312_(NonNullList::createWithCapacity));
    public static final StreamCodec<RegistryFriendlyByteBuf, List<ItemStack>> f_314960_ = f_315801_.m_321801_(ByteBufCodecs.m_323312_(NonNullList::createWithCapacity));
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final ItemStack EMPTY = new ItemStack((Void)null);
    private static final Component DISABLED_ITEM_TOOLTIP = Component.translatable("item.disabled").withStyle(ChatFormatting.RED);
    private int count;
    private int popTime;
    @Deprecated
    @Nullable
    private final Item item;
    final PatchedDataComponentMap f_315342_;
    @Nullable
    private Entity entityRepresentation;

    private static DataResult<ItemStack> m_323584_(ItemStack p_332181_) {
        DataResult<Unit> dataresult = m_320698_(p_332181_.m_318732_());
        if (dataresult.isError()) {
            return dataresult.map(p_327165_ -> p_332181_);
        } else {
            return p_332181_.getCount() > p_332181_.getMaxStackSize()
                ? DataResult.error(() -> "Item stack with stack size of " + p_332181_.getCount() + " was larger than maximum: " + p_332181_.getMaxStackSize())
                : DataResult.success(p_332181_);
        }
    }

    public static StreamCodec<RegistryFriendlyByteBuf, ItemStack> m_319263_(final StreamCodec<RegistryFriendlyByteBuf, ItemStack> p_332790_) {
        return new StreamCodec<RegistryFriendlyByteBuf, ItemStack>() {
            public ItemStack m_318688_(RegistryFriendlyByteBuf p_330762_) {
                ItemStack itemstack = p_332790_.m_318688_(p_330762_);
                if (!itemstack.isEmpty()) {
                    RegistryOps<Unit> registryops = p_330762_.m_319626_().m_318927_(NullOps.f_314313_);
                    ItemStack.CODEC.encodeStart(registryops, itemstack).getOrThrow(DecoderException::new);
                }

                return itemstack;
            }

            public void m_318638_(RegistryFriendlyByteBuf p_336131_, ItemStack p_329943_) {
                p_332790_.m_318638_(p_336131_, p_329943_);
            }
        };
    }

    public Optional<TooltipComponent> getTooltipImage() {
        return this.getItem().getTooltipImage(this);
    }

    @Override
    public DataComponentMap m_318732_() {
        return (DataComponentMap)(!this.isEmpty() ? this.f_315342_ : DataComponentMap.f_314291_);
    }

    public DataComponentMap m_322741_() {
        return !this.isEmpty() ? this.getItem().m_320917_() : DataComponentMap.f_314291_;
    }

    public DataComponentPatch m_324277_() {
        return !this.isEmpty() ? this.f_315342_.m_320212_() : DataComponentPatch.f_315512_;
    }

    public ItemStack(ItemLike pItem) {
        this(pItem, 1);
    }

    public ItemStack(Holder<Item> pTag) {
        this(pTag.value(), 1);
    }

    public ItemStack(Holder<Item> p_310702_, int p_41605_, DataComponentPatch p_328221_) {
        this(p_310702_.value(), p_41605_, PatchedDataComponentMap.m_322493_(p_310702_.value().m_320917_(), p_328221_));
    }

    public ItemStack(Holder<Item> pItem, int pCount) {
        this(pItem.value(), pCount);
    }

    public ItemStack(ItemLike pItem, int pCount) {
        this(pItem, pCount, new PatchedDataComponentMap(pItem.asItem().m_320917_()));
    }

    private ItemStack(ItemLike p_331826_, int p_332766_, PatchedDataComponentMap p_333722_) {
        this.item = p_331826_.asItem();
        this.count = p_332766_;
        this.f_315342_ = p_333722_;
        this.getItem().m_324094_(this);
    }

    private ItemStack(@Nullable Void p_282703_) {
        this.item = null;
        this.f_315342_ = new PatchedDataComponentMap(DataComponentMap.f_314291_);
    }

    public static DataResult<Unit> m_320698_(DataComponentMap p_336343_) {
        return p_336343_.m_321946_(DataComponents.f_316415_) && p_336343_.m_322806_(DataComponents.f_314701_, 1) > 1
            ? DataResult.error(() -> "Item cannot be both damageable and stackable")
            : DataResult.success(Unit.INSTANCE);
    }

    public static Optional<ItemStack> m_323951_(HolderLookup.Provider p_332204_, Tag p_336056_) {
        return CODEC.parse(p_332204_.m_318927_(NbtOps.INSTANCE), p_336056_)
            .resultOrPartial(p_327167_ -> LOGGER.error("Tried to load invalid item: '{}'", p_327167_));
    }

    public static ItemStack m_318937_(HolderLookup.Provider p_333870_, CompoundTag p_328391_) {
        return p_328391_.isEmpty() ? EMPTY : m_323951_(p_333870_, p_328391_).orElse(EMPTY);
    }

    public boolean isEmpty() {
        return this == EMPTY || this.item == Items.AIR || this.count <= 0;
    }

    public boolean isItemEnabled(FeatureFlagSet pEnabledFlags) {
        return this.isEmpty() || this.getItem().isEnabled(pEnabledFlags);
    }

    public ItemStack split(int pAmount) {
        int i = Math.min(pAmount, this.getCount());
        ItemStack itemstack = this.copyWithCount(i);
        this.shrink(i);
        return itemstack;
    }

    public ItemStack copyAndClear() {
        if (this.isEmpty()) {
            return EMPTY;
        } else {
            ItemStack itemstack = this.copy();
            this.setCount(0);
            return itemstack;
        }
    }

    public Item getItem() {
        return this.isEmpty() ? Items.AIR : this.item;
    }

    public Holder<Item> getItemHolder() {
        return this.getItem().builtInRegistryHolder();
    }

    public boolean is(TagKey<Item> pTag) {
        return this.getItem().builtInRegistryHolder().is(pTag);
    }

    public boolean is(Item pItem) {
        return this.getItem() == pItem;
    }

    public boolean is(Predicate<Holder<Item>> pItem) {
        return pItem.test(this.getItem().builtInRegistryHolder());
    }

    public boolean is(Holder<Item> pItem) {
        return this.getItem().builtInRegistryHolder() == pItem;
    }

    public boolean is(HolderSet<Item> pItem) {
        return pItem.contains(this.getItemHolder());
    }

    public Stream<TagKey<Item>> getTags() {
        return this.getItem().builtInRegistryHolder().tags();
    }

    public InteractionResult useOn(UseOnContext pContext) {
        if (!pContext.getLevel().isClientSide) return net.minecraftforge.common.ForgeHooks.onPlaceItemIntoWorld(pContext);
        return onItemUse(pContext, (c) -> getItem().useOn(pContext));
    }

    public InteractionResult onItemUseFirst(UseOnContext pContext) {
        return onItemUse(pContext, (c) -> getItem().onItemUseFirst(this, pContext));
    }

    private InteractionResult onItemUse(UseOnContext pContext, java.util.function.Function<UseOnContext, InteractionResult> callback) {
        Player player = pContext.getPlayer();
        BlockPos blockpos = pContext.getClickedPos();
        if (player != null && !player.getAbilities().mayBuild && !this.m_321400_(new BlockInWorld(pContext.getLevel(), blockpos, false))) {
            return InteractionResult.PASS;
        } else {
            Item item = this.getItem();
            InteractionResult interactionresult = callback.apply(pContext);
            if (player != null && interactionresult.shouldAwardStats()) {
                player.awardStat(Stats.ITEM_USED.get(item));
            }

            return interactionresult;
        }
    }

    public float getDestroySpeed(BlockState pState) {
        return this.getItem().getDestroySpeed(this, pState);
    }

    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        return this.getItem().use(pLevel, pPlayer, pUsedHand);
    }

    public ItemStack finishUsingItem(Level pLevel, LivingEntity pLivingEntity) {
        return this.getItem().finishUsingItem(this, pLevel, pLivingEntity);
    }

    public Tag m_321167_(HolderLookup.Provider p_330500_, Tag p_332574_) {
        if (this.isEmpty()) {
            throw new IllegalStateException("Cannot encode empty ItemStack");
        } else {
            return CODEC.encode(this, p_330500_.m_318927_(NbtOps.INSTANCE), p_332574_).getOrThrow();
        }
    }

    public Tag save(HolderLookup.Provider p_328490_) {
        if (this.isEmpty()) {
            throw new IllegalStateException("Cannot encode empty ItemStack");
        } else {
            return CODEC.encodeStart(p_328490_.m_318927_(NbtOps.INSTANCE), this).getOrThrow();
        }
    }

    public Tag m_324153_(HolderLookup.Provider p_335413_) {
        return (Tag)(this.isEmpty() ? new CompoundTag() : this.m_321167_(p_335413_, new CompoundTag()));
    }

    public int getMaxStackSize() {
        return this.m_322304_(DataComponents.f_314701_, Integer.valueOf(1));
    }

    public boolean isStackable() {
        return this.getMaxStackSize() > 1 && (!this.isDamageableItem() || !this.isDamaged());
    }

    public boolean isDamageableItem() {
        return this.m_319951_(DataComponents.f_316415_) && !this.m_319951_(DataComponents.f_315410_) && this.m_319951_(DataComponents.f_313972_);
    }

    public boolean isDamaged() {
        return this.isDamageableItem() && this.getDamageValue() > 0;
    }

    public int getDamageValue() {
        return Mth.clamp(this.m_322304_(DataComponents.f_313972_, Integer.valueOf(0)), 0, this.getMaxDamage());
    }

    public void setDamageValue(int pDamage) {
        this.m_322496_(DataComponents.f_313972_, Mth.clamp(pDamage, 0, this.getMaxDamage()));
    }

    public int getMaxDamage() {
        return this.m_322304_(DataComponents.f_316415_, Integer.valueOf(0));
    }

    public void hurt(int pAmount, RandomSource pRandom, @Nullable ServerPlayer pUser, Runnable p_331215_) {
        if (this.isDamageableItem()) {
            if (pAmount > 0) {
                int i = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING, this);
                int j = 0;

                for (int k = 0; i > 0 && k < pAmount; k++) {
                    if (DigDurabilityEnchantment.shouldIgnoreDurabilityDrop(this, i, pRandom)) {
                        j++;
                    }
                }

                pAmount -= j;
                if (pAmount <= 0) {
                    return;
                }
            }

            if (pUser != null && pAmount != 0) {
                CriteriaTriggers.ITEM_DURABILITY_CHANGED.trigger(pUser, this, this.getDamageValue() + pAmount);
            }

            int l = this.getDamageValue() + pAmount;
            this.setDamageValue(l);
            if (l >= this.getMaxDamage()) {
                p_331215_.run();
            }
        }
    }

    public void hurtAndBreak(int pAmount, LivingEntity pEntity, EquipmentSlot p_335324_) {
        if (!pEntity.level().isClientSide) {
            if (pEntity instanceof Player player && player.m_322042_()) {
                return;
            }

            this.hurt(pAmount, pEntity.getRandom(), pEntity instanceof ServerPlayer serverplayer ? serverplayer : null, () -> {
                if (pEntity instanceof Player player) {
                    net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, this, p_335324_);
                    player.stopUsingItem(); // Forge: fix MC-168573
                }
                pEntity.broadcastBreakEvent(p_335324_);
                Item item = this.getItem();
                this.shrink(1);
                if (pEntity instanceof Player) {
                    ((Player)pEntity).awardStat(Stats.ITEM_BROKEN.get(item));
                }

                this.setDamageValue(0);
            });
        }
    }

    public boolean isBarVisible() {
        return this.getItem().isBarVisible(this);
    }

    public int getBarWidth() {
        return this.getItem().getBarWidth(this);
    }

    public int getBarColor() {
        return this.getItem().getBarColor(this);
    }

    public boolean overrideStackedOnOther(Slot pSlot, ClickAction pAction, Player pPlayer) {
        return this.getItem().overrideStackedOnOther(this, pSlot, pAction, pPlayer);
    }

    public boolean overrideOtherStackedOnMe(ItemStack pStack, Slot pSlot, ClickAction pAction, Player pPlayer, SlotAccess pAccess) {
        return this.getItem().overrideOtherStackedOnMe(this, pStack, pSlot, pAction, pPlayer, pAccess);
    }

    public void hurtEnemy(LivingEntity pEntity, Player pPlayer) {
        Item item = this.getItem();
        ItemEnchantments itemenchantments = this.m_319737_();
        if (item.hurtEnemy(this, pEntity, pPlayer)) {
            pPlayer.awardStat(Stats.ITEM_USED.get(item));
            EnchantmentHelper.m_323486_(pPlayer, pEntity, itemenchantments);
        }
    }

    public void mineBlock(Level pLevel, BlockState pState, BlockPos pPos, Player pPlayer) {
        Item item = this.getItem();
        if (item.mineBlock(this, pLevel, pState, pPos, pPlayer)) {
            pPlayer.awardStat(Stats.ITEM_USED.get(item));
        }
    }

    public boolean isCorrectToolForDrops(BlockState pState) {
        return this.getItem().isCorrectToolForDrops(this, pState);
    }

    public InteractionResult interactLivingEntity(Player pPlayer, LivingEntity pEntity, InteractionHand pUsedHand) {
        return this.getItem().interactLivingEntity(this, pPlayer, pEntity, pUsedHand);
    }

    public ItemStack copy() {
        if (this.isEmpty()) {
            return EMPTY;
        } else {
            ItemStack itemstack = new ItemStack(this.getItem(), this.count, this.f_315342_.m_319920_());
            itemstack.setPopTime(this.getPopTime());
            return itemstack;
        }
    }

    public ItemStack copyWithCount(int pCount) {
        if (this.isEmpty()) {
            return EMPTY;
        } else {
            ItemStack itemstack = this.copy();
            itemstack.setCount(pCount);
            return itemstack;
        }
    }

    public ItemStack m_319323_(ItemLike p_334328_, int p_334821_) {
        return this.isEmpty() ? EMPTY : this.m_320013_(p_334328_, p_334821_);
    }

    public ItemStack m_320013_(ItemLike p_332114_, int p_333334_) {
        return new ItemStack(p_332114_.asItem().builtInRegistryHolder(), p_333334_, this.f_315342_.m_320212_());
    }

    public static boolean matches(ItemStack pStack, ItemStack pOther) {
        if (pStack == pOther) {
            return true;
        } else {
            return pStack.getCount() != pOther.getCount() ? false : m_322370_(pStack, pOther);
        }
    }

    @Deprecated
    public static boolean m_319597_(List<ItemStack> p_335471_, List<ItemStack> p_334624_) {
        if (p_335471_.size() != p_334624_.size()) {
            return false;
        } else {
            for (int i = 0; i < p_335471_.size(); i++) {
                if (!matches(p_335471_.get(i), p_334624_.get(i))) {
                    return false;
                }
            }

            return true;
        }
    }

    public static boolean isSameItem(ItemStack pStack, ItemStack pOther) {
        return pStack.is(pOther.getItem());
    }

    public static boolean m_322370_(ItemStack p_334397_, ItemStack p_331609_) {
        if (!p_334397_.is(p_331609_.getItem())) {
            return false;
        } else {
            return p_334397_.isEmpty() && p_331609_.isEmpty() ? true : Objects.equals(p_334397_.f_315342_, p_331609_.f_315342_);
        }
    }

    public static MapCodec<ItemStack> m_323240_(String p_336149_) {
        return CODEC.lenientOptionalFieldOf(p_336149_)
            .xmap(p_327174_ -> p_327174_.orElse(EMPTY), p_327162_ -> p_327162_.isEmpty() ? Optional.empty() : Optional.of(p_327162_));
    }

    public static int m_322198_(@Nullable ItemStack p_334004_) {
        if (p_334004_ != null) {
            int i = 31 + p_334004_.getItem().hashCode();
            return 31 * i + p_334004_.m_318732_().hashCode();
        } else {
            return 0;
        }
    }

    @Deprecated
    public static int m_318747_(List<ItemStack> p_333449_) {
        int i = 0;

        for (ItemStack itemstack : p_333449_) {
            i = i * 31 + m_322198_(itemstack);
        }

        return i;
    }

    public String getDescriptionId() {
        return this.getItem().getDescriptionId(this);
    }

    @Override
    public String toString() {
        return this.getCount() + " " + this.getItem();
    }

    public void inventoryTick(Level pLevel, Entity pEntity, int pInventorySlot, boolean pIsCurrentItem) {
        if (this.popTime > 0) {
            this.popTime--;
        }

        if (this.getItem() != null) {
            this.getItem().inventoryTick(this, pLevel, pEntity, pInventorySlot, pIsCurrentItem);
        }
    }

    public void onCraftedBy(Level pLevel, Player pPlayer, int pAmount) {
        pPlayer.awardStat(Stats.ITEM_CRAFTED.get(this.getItem()), pAmount);
        this.getItem().onCraftedBy(this, pLevel, pPlayer);
    }

    public void m_305085_(Level p_311164_) {
        this.getItem().m_42912_(this, p_311164_);
    }

    public int getUseDuration() {
        return this.getItem().getUseDuration(this);
    }

    public UseAnim getUseAnimation() {
        return this.getItem().getUseAnimation(this);
    }

    public void releaseUsing(Level pLevel, LivingEntity pLivingEntity, int pTimeLeft) {
        this.getItem().releaseUsing(this, pLevel, pLivingEntity, pTimeLeft);
    }

    public boolean useOnRelease() {
        return this.getItem().useOnRelease(this);
    }

    @Nullable
    public <T> T m_322496_(DataComponentType<? super T> p_332666_, @Nullable T p_335655_) {
        return this.f_315342_.m_322371_(p_332666_, p_335655_);
    }

    @Nullable
    public <T, U> T m_324919_(DataComponentType<T> p_331418_, T p_327708_, U p_332086_, BiFunction<T, U, T> p_329834_) {
        return this.m_322496_(p_331418_, p_329834_.apply(this.m_322304_(p_331418_, p_327708_), p_332086_));
    }

    @Nullable
    public <T> T m_322591_(DataComponentType<T> p_329905_, T p_329705_, UnaryOperator<T> p_335114_) {
        T t = this.m_322304_(p_329905_, p_329705_);
        return this.m_322496_(p_329905_, p_335114_.apply(t));
    }

    @Nullable
    public <T> T m_319322_(DataComponentType<? extends T> p_333259_) {
        return this.f_315342_.m_321460_(p_333259_);
    }

    public void m_320623_(DataComponentPatch p_336111_) {
        DataComponentPatch datacomponentpatch = this.f_315342_.m_320212_();
        this.f_315342_.m_320975_(p_336111_);
        Optional<Error<ItemStack>> optional = m_323584_(this).error();
        if (optional.isPresent()) {
            LOGGER.error("Failed to apply component patch '{}' to item: '{}'", p_336111_, optional.get().message());
            this.f_315342_.m_324830_(datacomponentpatch);
        } else {
            this.getItem().m_324094_(this);
        }
    }

    public void m_319238_(DataComponentPatch p_328534_) {
        this.f_315342_.m_320975_(p_328534_);
        this.getItem().m_324094_(this);
    }

    public void m_323474_(DataComponentMap p_335208_) {
        this.f_315342_.m_324935_(p_335208_);
        this.getItem().m_324094_(this);
    }

    public Component getHoverName() {
        Component component = this.m_323252_(DataComponents.f_316016_);
        if (component != null) {
            return component;
        } else {
            Component component1 = this.m_323252_(DataComponents.f_314548_);
            return component1 != null ? component1 : this.getItem().getName(this);
        }
    }

    private <T extends TooltipProvider> void m_319928_(
        DataComponentType<T> p_331934_, Item.TooltipContext p_333562_, Consumer<Component> p_334534_, TooltipFlag p_333715_
    ) {
        T t = (T)this.m_323252_(p_331934_);
        if (t != null) {
            t.m_319025_(p_333562_, p_334534_, p_333715_);
        }
    }

    public List<Component> getTooltipLines(Item.TooltipContext p_331329_, @Nullable Player pPlayer, TooltipFlag pIsAdvanced) {
        if (!pIsAdvanced.isCreative() && this.m_319951_(DataComponents.f_314049_)) {
            return List.of();
        } else {
            List<Component> list = Lists.newArrayList();
            MutableComponent mutablecomponent = Component.empty().append(this.getHoverName()).withStyle(this.getRarity().m_321696_());
            if (this.m_319951_(DataComponents.f_316016_)) {
                mutablecomponent.withStyle(ChatFormatting.ITALIC);
            }

            list.add(mutablecomponent);
            if (!pIsAdvanced.isAdvanced() && !this.m_319951_(DataComponents.f_316016_) && this.is(Items.FILLED_MAP)) {
                MapId mapid = this.m_323252_(DataComponents.f_315230_);
                if (mapid != null) {
                    list.add(MapItem.getTooltipForId(mapid));
                }
            }

            Consumer<Component> consumer = list::add;
            if (!this.m_319951_(DataComponents.f_316186_)) {
                this.getItem().appendHoverText(this, p_331329_, list, pIsAdvanced);
            }

            this.m_319928_(DataComponents.f_315199_, p_331329_, consumer, pIsAdvanced);
            this.m_319928_(DataComponents.f_314515_, p_331329_, consumer, pIsAdvanced);
            this.m_319928_(DataComponents.f_314658_, p_331329_, consumer, pIsAdvanced);
            this.m_319928_(DataComponents.f_315011_, p_331329_, consumer, pIsAdvanced);
            this.m_319928_(DataComponents.f_315745_, p_331329_, consumer, pIsAdvanced);
            this.m_322901_(consumer, pPlayer);
            this.m_319928_(DataComponents.f_315410_, p_331329_, consumer, pIsAdvanced);
            AdventureModePredicate adventuremodepredicate = this.m_323252_(DataComponents.f_316977_);
            if (adventuremodepredicate != null && adventuremodepredicate.m_324667_()) {
                consumer.accept(CommonComponents.EMPTY);
                consumer.accept(AdventureModePredicate.f_315565_);
                adventuremodepredicate.m_318685_(consumer);
            }

            AdventureModePredicate adventuremodepredicate1 = this.m_323252_(DataComponents.f_315118_);
            if (adventuremodepredicate1 != null && adventuremodepredicate1.m_324667_()) {
                consumer.accept(CommonComponents.EMPTY);
                consumer.accept(AdventureModePredicate.f_314797_);
                adventuremodepredicate1.m_318685_(consumer);
            }

            if (pIsAdvanced.isAdvanced()) {
                if (this.isDamaged()) {
                    list.add(Component.translatable("item.durability", this.getMaxDamage() - this.getDamageValue(), this.getMaxDamage()));
                }

                list.add(Component.literal(BuiltInRegistries.ITEM.getKey(this.getItem()).toString()).withStyle(ChatFormatting.DARK_GRAY));
                int i = this.f_315342_.m_319491_();
                if (i > 0) {
                    list.add(Component.translatable("item.components", i).withStyle(ChatFormatting.DARK_GRAY));
                }
            }

            if (pPlayer != null && !this.getItem().isEnabled(pPlayer.level().enabledFeatures())) {
                list.add(DISABLED_ITEM_TOOLTIP);
            }

            net.minecraftforge.event.ForgeEventFactory.onItemTooltip(this, pPlayer, list, pIsAdvanced);

            return list;
        }
    }

    private void m_322901_(Consumer<Component> p_333346_, @Nullable Player p_332769_) {
        ItemAttributeModifiers itemattributemodifiers = this.m_322304_(DataComponents.f_316119_, ItemAttributeModifiers.f_314473_);
        if (itemattributemodifiers.f_315588_()) {
            for (EquipmentSlot equipmentslot : EquipmentSlot.values()) {
                MutableBoolean mutableboolean = new MutableBoolean(true);
                this.m_321237_(equipmentslot, (p_327160_, p_327161_) -> {
                    if (mutableboolean.isTrue()) {
                        p_333346_.accept(CommonComponents.EMPTY);
                        p_333346_.accept(Component.translatable("item.modifiers." + equipmentslot.getName()).withStyle(ChatFormatting.GRAY));
                        mutableboolean.setFalse();
                    }

                    this.m_322829_(p_333346_, p_332769_, p_327160_, p_327161_);
                });
            }
        }
    }

    private void m_322829_(Consumer<Component> p_332944_, @Nullable Player p_328442_, Holder<Attribute> p_336373_, AttributeModifier p_332746_) {
        double d0 = p_332746_.amount();
        boolean flag = false;
        if (p_328442_ != null) {
            if (p_332746_.id() == Item.BASE_ATTACK_DAMAGE_UUID) {
                d0 += p_328442_.getAttributeBaseValue(Attributes.ATTACK_DAMAGE);
                d0 += (double)EnchantmentHelper.getDamageBonus(this, null);
                flag = true;
            } else if (p_332746_.id() == Item.BASE_ATTACK_SPEED_UUID) {
                d0 += p_328442_.getAttributeBaseValue(Attributes.ATTACK_SPEED);
                flag = true;
            }
        }

        double d1;
        if (p_332746_.operation() == AttributeModifier.Operation.ADD_MULTIPLIED_BASE || p_332746_.operation() == AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
            )
         {
            d1 = d0 * 100.0;
        } else if (p_336373_.m_318604_(Attributes.KNOCKBACK_RESISTANCE)) {
            d1 = d0 * 10.0;
        } else {
            d1 = d0;
        }

        if (flag) {
            p_332944_.accept(
                CommonComponents.space()
                    .append(
                        Component.translatable(
                            "attribute.modifier.equals." + p_332746_.operation().m_324661_(),
                            ItemAttributeModifiers.f_315079_.format(d1),
                            Component.translatable(p_336373_.value().getDescriptionId())
                        )
                    )
                    .withStyle(ChatFormatting.DARK_GREEN)
            );
        } else if (d0 > 0.0) {
            p_332944_.accept(
                Component.translatable(
                        "attribute.modifier.plus." + p_332746_.operation().m_324661_(),
                        ItemAttributeModifiers.f_315079_.format(d1),
                        Component.translatable(p_336373_.value().getDescriptionId())
                    )
                    .withStyle(ChatFormatting.BLUE)
            );
        } else if (d0 < 0.0) {
            p_332944_.accept(
                Component.translatable(
                        "attribute.modifier.take." + p_332746_.operation().m_324661_(),
                        ItemAttributeModifiers.f_315079_.format(-d1),
                        Component.translatable(p_336373_.value().getDescriptionId())
                    )
                    .withStyle(ChatFormatting.RED)
            );
        }
    }

    public boolean hasFoil() {
        Boolean obool = this.m_323252_(DataComponents.f_315974_);
        return obool != null ? obool : this.getItem().isFoil(this);
    }

    public Rarity getRarity() {
        Rarity rarity = this.m_322304_(DataComponents.f_315029_, Rarity.COMMON);
        if (!this.isEnchanted()) {
            return rarity;
        } else {
            return switch (rarity) {
                case COMMON, UNCOMMON -> Rarity.RARE;
                case RARE -> Rarity.EPIC;
                default -> rarity;
            };
        }
    }

    public boolean isEnchantable() {
        if (!this.getItem().isEnchantable(this)) {
            return false;
        } else {
            ItemEnchantments itemenchantments = this.m_323252_(DataComponents.f_314658_);
            return itemenchantments != null && itemenchantments.m_324000_();
        }
    }

    public void enchant(Enchantment pEnchantment, int pLevel) {
        EnchantmentHelper.m_320959_(this, p_327170_ -> p_327170_.m_323014_(pEnchantment, pLevel));
    }

    public boolean isEnchanted() {
        return !this.m_322304_(DataComponents.f_314658_, ItemEnchantments.f_314789_).m_324000_();
    }

    public ItemEnchantments m_319737_() {
        return this.m_322304_(DataComponents.f_314658_, ItemEnchantments.f_314789_);
    }

    public boolean isFramed() {
        return this.entityRepresentation instanceof ItemFrame;
    }

    public void setEntityRepresentation(@Nullable Entity pEntity) {
        if (!this.isEmpty()) {
            this.entityRepresentation = pEntity;
        }
    }

    @Nullable
    public ItemFrame getFrame() {
        return this.entityRepresentation instanceof ItemFrame ? (ItemFrame)this.getEntityRepresentation() : null;
    }

    @Nullable
    public Entity getEntityRepresentation() {
        return !this.isEmpty() ? this.entityRepresentation : null;
    }

    public void m_321237_(EquipmentSlot p_331036_, BiConsumer<Holder<Attribute>, AttributeModifier> p_334430_) {
        ItemAttributeModifiers itemattributemodifiers = this.m_322304_(DataComponents.f_316119_, ItemAttributeModifiers.f_314473_);
        if (!itemattributemodifiers.f_314826_().isEmpty()) {
            itemattributemodifiers.m_322073_(p_331036_, p_334430_);
        } else {
            this.getItem().getDefaultAttributeModifiers().m_322073_(p_331036_, p_334430_);
        }
    }

    public Component getDisplayName() {
        MutableComponent mutablecomponent = Component.empty().append(this.getHoverName());
        if (this.m_319951_(DataComponents.f_316016_)) {
            mutablecomponent.withStyle(ChatFormatting.ITALIC);
        }

        MutableComponent mutablecomponent1 = ComponentUtils.wrapInSquareBrackets(mutablecomponent);
        if (!this.isEmpty()) {
            mutablecomponent1.withStyle(this.getRarity().m_321696_())
                .withStyle(p_220170_ -> p_220170_.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackInfo(this))));
        }

        return mutablecomponent1;
    }

    public boolean m_321400_(BlockInWorld p_331134_) {
        AdventureModePredicate adventuremodepredicate = this.m_323252_(DataComponents.f_315118_);
        return adventuremodepredicate != null && adventuremodepredicate.m_322201_(p_331134_);
    }

    public boolean m_323082_(BlockInWorld p_333133_) {
        AdventureModePredicate adventuremodepredicate = this.m_323252_(DataComponents.f_316977_);
        return adventuremodepredicate != null && adventuremodepredicate.m_322201_(p_333133_);
    }

    public int getPopTime() {
        return this.popTime;
    }

    public void setPopTime(int pPopTime) {
        this.popTime = pPopTime;
    }

    public int getCount() {
        return this.isEmpty() ? 0 : this.count;
    }

    public void setCount(int pCount) {
        this.count = pCount;
    }

    public void m_324521_(int p_328100_) {
        if (!this.isEmpty() && this.getCount() > p_328100_) {
            this.setCount(p_328100_);
        }
    }

    public void grow(int pIncrement) {
        this.setCount(this.getCount() + pIncrement);
    }

    public void shrink(int pDecrement) {
        this.grow(-pDecrement);
    }

    public void m_321439_(int p_329683_, @Nullable LivingEntity p_334302_) {
        if (p_334302_ == null || !p_334302_.m_322042_()) {
            this.shrink(p_329683_);
        }
    }

    public void onUseTick(Level pLevel, LivingEntity pLivingEntity, int pCount) {
        this.getItem().onUseTick(pLevel, pLivingEntity, this, pCount);
    }

    /** @deprecated Forge: Use {@linkplain net.minecraftforge.common.extensions.IForgeItemStack#onDestroyed(ItemEntity, net.minecraft.world.damagesource.DamageSource) damage source sensitive version} */
    public void onDestroyed(ItemEntity pItemEntity) {
        this.getItem().onDestroyed(pItemEntity);
    }

    public SoundEvent getDrinkingSound() {
        return this.getItem().getDrinkingSound();
    }

    public SoundEvent getEatingSound() {
        return this.getItem().getEatingSound();
    }

    public SoundEvent m_321057_() {
        return this.getItem().m_318629_();
    }

    public boolean m_325012_(DamageSource p_334859_) {
        return !this.m_319951_(DataComponents.f_315720_) || !p_334859_.is(DamageTypeTags.IS_FIRE);
    }
}
