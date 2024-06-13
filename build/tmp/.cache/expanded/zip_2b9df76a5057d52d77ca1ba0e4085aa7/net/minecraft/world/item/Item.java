package net.minecraft.world.item;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureElement;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class Item implements FeatureElement, ItemLike, net.minecraftforge.common.extensions.IForgeItem {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, Holder<Item>> STREAM_CODEC =
        net.minecraft.network.codec.ByteBufCodecs.m_322636_(net.minecraft.core.registries.Registries.ITEM);
    public static final Map<Block, Item> BY_BLOCK = net.minecraftforge.registries.GameData.ItemCallbacks.getBlockItemMap();
    public static final UUID BASE_ATTACK_DAMAGE_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
    public static final UUID BASE_ATTACK_SPEED_UUID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
    public static final int f_315532_ = 64;
    public static final int f_314443_ = 99;
    public static final int MAX_BAR_WIDTH = 13;
    private final Holder.Reference<Item> builtInRegistryHolder = BuiltInRegistries.ITEM.createIntrusiveHolder(this);
    private final DataComponentMap f_315186_;
    @Nullable
    private final Item craftingRemainingItem;
    @Nullable
    private String descriptionId;
    private final FeatureFlagSet requiredFeatures;

    public static int getId(Item pItem) {
        return pItem == null ? 0 : BuiltInRegistries.ITEM.getId(pItem);
    }

    public static Item byId(int pId) {
        return BuiltInRegistries.ITEM.byId(pId);
    }

    @Deprecated
    public static Item byBlock(Block pBlock) {
        return BY_BLOCK.getOrDefault(pBlock, Items.AIR);
    }

    public Item(Item.Properties pProperties) {
        this.f_315186_ = pProperties.m_320473_();
        this.craftingRemainingItem = pProperties.craftingRemainingItem;
        this.requiredFeatures = pProperties.requiredFeatures;
        if (SharedConstants.IS_RUNNING_IN_IDE) {
            String s = this.getClass().getSimpleName();
            if (!s.endsWith("Item")) {
                LOGGER.error("Item classes should end with Item and {} doesn't.", s);
            }
        }
        initClient();
    }

    @Deprecated
    public Holder.Reference<Item> builtInRegistryHolder() {
        return this.builtInRegistryHolder;
    }

    @Nullable
    private DataComponentMap builtComponents = null;

    public DataComponentMap m_320917_() {
        if (builtComponents == null) {
            builtComponents = net.minecraftforge.common.ForgeHooks.gatherItemComponents(this, f_315186_);
        }

        return builtComponents;
    }

    public int m_320193_() {
        return builtComponents == null ? this.f_315186_.m_322806_(DataComponents.f_314701_, 1) : this.builtComponents.m_322806_(DataComponents.f_314701_, 1);
    }

    public void onUseTick(Level pLevel, LivingEntity pLivingEntity, ItemStack pStack, int pRemainingUseDuration) {
    }

    /** @deprecated Forge: {@link net.minecraftforge.common.extensions.IForgeItem#onDestroyed(ItemEntity, DamageSource) Use damage source sensitive version} */
    public void onDestroyed(ItemEntity pItemEntity) {
    }

    public void m_324094_(ItemStack p_336236_) {
    }

    public boolean canAttackBlock(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer) {
        return true;
    }

    @Override
    public Item asItem() {
        return this;
    }

    public InteractionResult useOn(UseOnContext pContext) {
        return InteractionResult.PASS;
    }

    public float getDestroySpeed(ItemStack pStack, BlockState pState) {
        Tool tool = pStack.m_323252_(DataComponents.f_314833_);
        return tool != null ? tool.m_325036_(pState) : 1.0F;
    }

    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pUsedHand);
        FoodProperties foodproperties = itemstack.m_323252_(DataComponents.f_315399_);
        if (foodproperties != null) {
            if (pPlayer.canEat(foodproperties.canAlwaysEat())) {
                pPlayer.startUsingItem(pUsedHand);
                return InteractionResultHolder.consume(itemstack);
            } else {
                return InteractionResultHolder.fail(itemstack);
            }
        } else {
            return InteractionResultHolder.pass(pPlayer.getItemInHand(pUsedHand));
        }
    }

    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity) {
        return pStack.m_319951_(DataComponents.f_315399_) ? pLivingEntity.eat(pLevel, pStack) : pStack;
    }

    public boolean isBarVisible(ItemStack pStack) {
        return pStack.isDamaged();
    }

    public int getBarWidth(ItemStack pStack) {
        return Mth.clamp(Math.round(13.0F - (float)pStack.getDamageValue() * 13.0F / (float)pStack.getMaxDamage()), 0, 13);
    }

    public int getBarColor(ItemStack pStack) {
        int i = pStack.getMaxDamage();
        float f = Math.max(0.0F, ((float)i - (float)pStack.getDamageValue()) / (float)i);
        return Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
    }

    public boolean overrideStackedOnOther(ItemStack pStack, Slot pSlot, ClickAction pAction, Player pPlayer) {
        return false;
    }

    public boolean overrideOtherStackedOnMe(ItemStack pStack, ItemStack pOther, Slot pSlot, ClickAction pAction, Player pPlayer, SlotAccess pAccess) {
        return false;
    }

    public float m_319585_(Player p_330411_, float p_327880_) {
        return 0.0F;
    }

    public boolean hurtEnemy(ItemStack pStack, LivingEntity pTarget, LivingEntity pAttacker) {
        return false;
    }

    public boolean mineBlock(ItemStack pStack, Level pLevel, BlockState pState, BlockPos pPos, LivingEntity pMiningEntity) {
        Tool tool = pStack.m_323252_(DataComponents.f_314833_);
        if (tool == null) {
            return false;
        } else {
            if (!pLevel.isClientSide && pState.getDestroySpeed(pLevel, pPos) != 0.0F && tool.f_315325_() > 0) {
                pStack.hurtAndBreak(tool.f_315325_(), pMiningEntity, EquipmentSlot.MAINHAND);
            }

            return true;
        }
    }

    public boolean isCorrectToolForDrops(ItemStack p_332232_, BlockState pBlock) {
        Tool tool = p_332232_.m_323252_(DataComponents.f_314833_);
        return tool != null && tool.m_322492_(pBlock);
    }

    public InteractionResult interactLivingEntity(ItemStack pStack, Player pPlayer, LivingEntity pInteractionTarget, InteractionHand pUsedHand) {
        return InteractionResult.PASS;
    }

    public Component getDescription() {
        return Component.translatable(this.getDescriptionId());
    }

    @Override
    public String toString() {
        return BuiltInRegistries.ITEM.getKey(this).getPath();
    }

    protected String getOrCreateDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = Util.makeDescriptionId("item", BuiltInRegistries.ITEM.getKey(this));
        }

        return this.descriptionId;
    }

    public String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }

    public String getDescriptionId(ItemStack pStack) {
        return this.getDescriptionId();
    }

    @Nullable
    @Deprecated // Use ItemStack sensitive version.
    public final Item getCraftingRemainingItem() {
        return this.craftingRemainingItem;
    }

    @Deprecated // Use ItemStack sensitive version.
    public boolean hasCraftingRemainingItem() {
        return this.craftingRemainingItem != null;
    }

    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
    }

    public void onCraftedBy(ItemStack pStack, Level pLevel, Player pPlayer) {
        this.m_42912_(pStack, pLevel);
    }

    public void m_42912_(ItemStack p_312780_, Level p_312645_) {
    }

    public boolean isComplex() {
        return false;
    }

    public UseAnim getUseAnimation(ItemStack pStack) {
        return pStack.m_319951_(DataComponents.f_315399_) ? UseAnim.EAT : UseAnim.NONE;
    }

    public int getUseDuration(ItemStack pStack) {
        FoodProperties foodproperties = pStack.m_323252_(DataComponents.f_315399_);
        return foodproperties != null ? foodproperties.m_319390_() : 0;
    }

    public void releaseUsing(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity, int pTimeCharged) {
    }

    public void appendHoverText(ItemStack pStack, Item.TooltipContext p_333372_, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
    }

    public Optional<TooltipComponent> getTooltipImage(ItemStack pStack) {
        return Optional.empty();
    }

    public Component getName(ItemStack pStack) {
        return Component.translatable(this.getDescriptionId(pStack));
    }

    public boolean isFoil(ItemStack pStack) {
        return pStack.isEnchanted();
    }

    public boolean isEnchantable(ItemStack pStack) {
        return pStack.getMaxStackSize() == 1 && pStack.m_319951_(DataComponents.f_316415_);
    }

    protected static BlockHitResult getPlayerPOVHitResult(Level pLevel, Player pPlayer, ClipContext.Fluid pFluidMode) {
        Vec3 vec3 = pPlayer.getEyePosition();
        Vec3 vec31 = vec3.add(pPlayer.calculateViewVector(pPlayer.getXRot(), pPlayer.getYRot()).scale(pPlayer.m_319993_()));
        return pLevel.clip(new ClipContext(vec3, vec31, ClipContext.Block.OUTLINE, pFluidMode, pPlayer));
    }

    @Deprecated // Use ItemStack sensitive version.
    public int getEnchantmentValue() {
        return 0;
    }

    public boolean isValidRepairItem(ItemStack pStack, ItemStack pRepairCandidate) {
        return false;
    }

    @Deprecated
    public ItemAttributeModifiers getDefaultAttributeModifiers() {
        return ItemAttributeModifiers.f_314473_;
    }

    public boolean useOnRelease(ItemStack pStack) {
        return false;
    }

    public ItemStack getDefaultInstance() {
        return new ItemStack(this);
    }

    public SoundEvent getDrinkingSound() {
        return SoundEvents.GENERIC_DRINK;
    }

    public SoundEvent getEatingSound() {
        return SoundEvents.GENERIC_EAT;
    }

    public SoundEvent m_318629_() {
        return SoundEvents.ITEM_BREAK;
    }

    public boolean canFitInsideContainerItems() {
        return true;
    }

    @Override
    public FeatureFlagSet requiredFeatures() {
        return this.requiredFeatures;
    }

    private Object renderProperties;

    /*
       DO NOT CALL, IT WILL DISAPPEAR IN THE FUTURE
       Call RenderProperties.get instead
     */
    public Object getRenderPropertiesInternal() {
        return renderProperties;
    }

    private void initClient() {
        // Minecraft instance isn't available in datagen, so don't call initializeClient if in datagen
        if (net.minecraftforge.fml.loading.FMLEnvironment.dist == net.minecraftforge.api.distmarker.Dist.CLIENT && !net.minecraftforge.fml.loading.FMLLoader.getLaunchHandler().isData()) {
            initializeClient(properties -> {
                if (properties == this) {
                    throw new IllegalStateException("Don't extend IItemRenderProperties in your item, use an anonymous class instead.");
                }
                this.renderProperties = properties;
            });
        }
    }

    public void initializeClient(java.util.function.Consumer<net.minecraftforge.client.extensions.common.IClientItemExtensions> consumer) { }

    public static class Properties {
        private static final Interner<DataComponentMap> f_315126_ = Interners.newStrongInterner();
        @Nullable
        private DataComponentMap.Builder f_316768_;
        @Nullable
        Item craftingRemainingItem;
        FeatureFlagSet requiredFeatures = FeatureFlags.VANILLA_SET;

        public Item.Properties food(FoodProperties pFood) {
            return this.m_324556_(DataComponents.f_315399_, pFood);
        }

        public Item.Properties stacksTo(int pMaxStackSize) {
            return this.m_324556_(DataComponents.f_314701_, pMaxStackSize);
        }

        public Item.Properties durability(int pMaxDamage) {
            this.m_324556_(DataComponents.f_316415_, pMaxDamage);
            this.m_324556_(DataComponents.f_314701_, 1);
            this.m_324556_(DataComponents.f_313972_, 0);
            return this;
        }

        public Item.Properties craftRemainder(Item pCraftingRemainingItem) {
            this.craftingRemainingItem = pCraftingRemainingItem;
            return this;
        }

        public Item.Properties rarity(Rarity pRarity) {
            return this.m_324556_(DataComponents.f_315029_, pRarity);
        }

        public Item.Properties fireResistant() {
            return this.m_324556_(DataComponents.f_315720_, Unit.INSTANCE);
        }

        public Item.Properties requiredFeatures(FeatureFlag... pRequiredFeatures) {
            this.requiredFeatures = FeatureFlags.REGISTRY.subset(pRequiredFeatures);
            return this;
        }

        public <T> Item.Properties m_324556_(DataComponentType<T> p_333852_, T p_330859_) {
            if (this.f_316768_ == null) {
                this.f_316768_ = DataComponentMap.m_323371_().m_321974_(DataComponents.f_316136_);
            }

            this.f_316768_.m_322739_(p_333852_, p_330859_);
            return this;
        }

        public Item.Properties m_324699_(ItemAttributeModifiers p_330293_) {
            return this.m_324556_(DataComponents.f_316119_, p_330293_);
        }

        DataComponentMap m_320473_() {
            DataComponentMap datacomponentmap = this.m_320101_();
            if (datacomponentmap.m_321946_(DataComponents.f_313972_) && datacomponentmap.m_322806_(DataComponents.f_314701_, 1) > 1) {
                throw new IllegalStateException("Item cannot have both durability and be stackable");
            } else {
                return datacomponentmap;
            }
        }

        private DataComponentMap m_320101_() {
            return this.f_316768_ == null ? DataComponents.f_316136_ : f_315126_.intern(this.f_316768_.m_318826_());
        }
    }

    public interface TooltipContext {
        Item.TooltipContext f_314080_ = new Item.TooltipContext() {
            @Nullable
            @Override
            public HolderLookup.Provider m_320287_() {
                return null;
            }

            @Override
            public float m_319443_() {
                return 20.0F;
            }

            @Nullable
            @Override
            public MapItemSavedData m_319467_(MapId p_334227_) {
                return null;
            }
        };

        @Nullable
        HolderLookup.Provider m_320287_();

        float m_319443_();

        @Nullable
        MapItemSavedData m_319467_(MapId p_335695_);

        static Item.TooltipContext m_324510_(@Nullable final Level p_332083_) {
            return p_332083_ == null ? f_314080_ : new Item.TooltipContext() {
                @Override
                public HolderLookup.Provider m_320287_() {
                    return p_332083_.registryAccess();
                }

                @Override
                public float m_319443_() {
                    return p_332083_.m_304826_().m_306179_();
                }

                @Override
                public MapItemSavedData m_319467_(MapId p_330171_) {
                    return p_332083_.getMapData(p_330171_);
                }
            };
        }

        static Item.TooltipContext m_322396_(final HolderLookup.Provider p_335652_) {
            return new Item.TooltipContext() {
                @Override
                public HolderLookup.Provider m_320287_() {
                    return p_335652_;
                }

                @Override
                public float m_319443_() {
                    return 20.0F;
                }

                @Nullable
                @Override
                public MapItemSavedData m_319467_(MapId p_332386_) {
                    return null;
                }
            };
        }
    }
}
