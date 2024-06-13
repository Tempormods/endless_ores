package net.minecraft.client.renderer.item;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.CompassItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LightBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemProperties {
    private static final Map<ResourceLocation, ItemPropertyFunction> GENERIC_PROPERTIES = Maps.newHashMap();
    private static final ResourceLocation DAMAGED = new ResourceLocation("damaged");
    private static final ResourceLocation DAMAGE = new ResourceLocation("damage");
    private static final ClampedItemPropertyFunction PROPERTY_DAMAGED = (p_174660_, p_174661_, p_174662_, p_174663_) -> p_174660_.isDamaged() ? 1.0F : 0.0F;
    private static final ClampedItemPropertyFunction PROPERTY_DAMAGE = (p_174655_, p_174656_, p_174657_, p_174658_) -> Mth.clamp(
            (float)p_174655_.getDamageValue() / (float)p_174655_.getMaxDamage(), 0.0F, 1.0F
        );
    private static final Map<Item, Map<ResourceLocation, ItemPropertyFunction>> PROPERTIES = Maps.newHashMap();

    private static ClampedItemPropertyFunction registerGeneric(ResourceLocation pName, ClampedItemPropertyFunction pProperty) {
        return (ClampedItemPropertyFunction) registerGeneric(pName, (ItemPropertyFunction) pProperty);
    }

    public static ItemPropertyFunction registerGeneric(ResourceLocation pName, ItemPropertyFunction pProperty) {
        GENERIC_PROPERTIES.put(pName, pProperty);
        return pProperty;
    }

    private static void registerCustomModelData(ItemPropertyFunction pProperty) {
        GENERIC_PROPERTIES.put(new ResourceLocation("custom_model_data"), pProperty);
    }

    private static void register(Item pItem, ResourceLocation pName, ClampedItemPropertyFunction pProperty) {
        register(pItem, pName, (ItemPropertyFunction) pProperty);
    }

    public static void register(Item pItem, ResourceLocation pName, ItemPropertyFunction pProperty) {
        PROPERTIES.computeIfAbsent(pItem, p_117828_ -> Maps.newHashMap()).put(pName, pProperty);
    }

    @Nullable
    public static ItemPropertyFunction getProperty(ItemStack p_329554_, ResourceLocation pName) {
        if (p_329554_.getMaxDamage() > 0) {
            if (DAMAGE.equals(pName)) {
                return PROPERTY_DAMAGE;
            }

            if (DAMAGED.equals(pName)) {
                return PROPERTY_DAMAGED;
            }
        }

        ItemPropertyFunction itempropertyfunction = GENERIC_PROPERTIES.get(pName);
        if (itempropertyfunction != null) {
            return itempropertyfunction;
        } else {
            Map<ResourceLocation, ItemPropertyFunction> map = PROPERTIES.get(p_329554_.getItem());
            return map == null ? null : map.get(pName);
        }
    }

    static {
        registerGeneric(
            new ResourceLocation("lefthanded"),
            (p_174650_, p_174651_, p_174652_, p_174653_) -> p_174652_ != null && p_174652_.getMainArm() != HumanoidArm.RIGHT ? 1.0F : 0.0F
        );
        registerGeneric(
            new ResourceLocation("cooldown"),
            (p_174645_, p_174646_, p_174647_, p_174648_) -> p_174647_ instanceof Player
                    ? ((Player)p_174647_).getCooldowns().getCooldownPercent(p_174645_.getItem(), 0.0F)
                    : 0.0F
        );
        ClampedItemPropertyFunction clampeditempropertyfunction = (p_325556_, p_325557_, p_325558_, p_325559_) -> {
            ArmorTrim armortrim = p_325556_.m_323252_(DataComponents.f_315199_);
            return armortrim != null ? armortrim.material().value().itemModelIndex() : Float.NEGATIVE_INFINITY;
        };
        registerGeneric(ItemModelGenerators.TRIM_TYPE_PREDICATE_ID, clampeditempropertyfunction);
        registerCustomModelData((p_325552_, p_325553_, p_325554_, p_325555_) -> (float)p_325552_.m_322304_(DataComponents.f_315513_, CustomModelData.f_316694_).f_314084_());
        register(Items.BOW, new ResourceLocation("pull"), (p_174635_, p_174636_, p_174637_, p_174638_) -> {
            if (p_174637_ == null) {
                return 0.0F;
            } else {
                return p_174637_.getUseItem() != p_174635_ ? 0.0F : (float)(p_174635_.getUseDuration() - p_174637_.getUseItemRemainingTicks()) / 20.0F;
            }
        });
        register(
            Items.BRUSH,
            new ResourceLocation("brushing"),
            (p_272332_, p_272333_, p_272334_, p_272335_) -> p_272334_ != null && p_272334_.getUseItem() == p_272332_
                    ? (float)(p_272334_.getUseItemRemainingTicks() % 10) / 10.0F
                    : 0.0F
        );
        register(
            Items.BOW,
            new ResourceLocation("pulling"),
            (p_174630_, p_174631_, p_174632_, p_174633_) -> p_174632_ != null && p_174632_.isUsingItem() && p_174632_.getUseItem() == p_174630_ ? 1.0F : 0.0F
        );
        register(Items.BUNDLE, new ResourceLocation("filled"), (p_174625_, p_174626_, p_174627_, p_174628_) -> BundleItem.getFullnessDisplay(p_174625_));
        register(Items.CLOCK, new ResourceLocation("time"), new ClampedItemPropertyFunction() {
            private double rotation;
            private double rota;
            private long lastUpdateTick;

            @Override
            public float unclampedCall(ItemStack p_174665_, @Nullable ClientLevel p_174666_, @Nullable LivingEntity p_174667_, int p_174668_) {
                Entity entity = (Entity)(p_174667_ != null ? p_174667_ : p_174665_.getEntityRepresentation());
                if (entity == null) {
                    return 0.0F;
                } else {
                    if (p_174666_ == null && entity.level() instanceof ClientLevel) {
                        p_174666_ = (ClientLevel)entity.level();
                    }

                    if (p_174666_ == null) {
                        return 0.0F;
                    } else {
                        double d0;
                        if (p_174666_.dimensionType().natural()) {
                            d0 = (double)p_174666_.getTimeOfDay(1.0F);
                        } else {
                            d0 = Math.random();
                        }

                        d0 = this.wobble(p_174666_, d0);
                        return (float)d0;
                    }
                }
            }

            private double wobble(Level p_117904_, double p_117905_) {
                if (p_117904_.getGameTime() != this.lastUpdateTick) {
                    this.lastUpdateTick = p_117904_.getGameTime();
                    double d0 = p_117905_ - this.rotation;
                    d0 = Mth.positiveModulo(d0 + 0.5, 1.0) - 0.5;
                    this.rota += d0 * 0.1;
                    this.rota *= 0.9;
                    this.rotation = Mth.positiveModulo(this.rotation + this.rota, 1.0);
                }

                return this.rotation;
            }
        });
        register(Items.COMPASS, new ResourceLocation("angle"), new CompassItemPropertyFunction((p_325560_, p_325561_, p_325562_) -> {
            LodestoneTracker lodestonetracker = p_325561_.m_323252_(DataComponents.f_314784_);
            return lodestonetracker != null ? lodestonetracker.f_315845_().orElse(null) : CompassItem.getSpawnPosition(p_325560_);
        }));
        register(
            Items.RECOVERY_COMPASS,
            new ResourceLocation("angle"),
            new CompassItemPropertyFunction((p_234983_, p_234984_, p_234985_) -> p_234985_ instanceof Player player ? player.getLastDeathLocation().orElse(null) : null)
        );
        register(Items.CROSSBOW, new ResourceLocation("pull"), (p_174610_, p_174611_, p_174612_, p_174613_) -> {
            if (p_174612_ == null) {
                return 0.0F;
            } else {
                return CrossbowItem.isCharged(p_174610_) ? 0.0F : (float)(p_174610_.getUseDuration() - p_174612_.getUseItemRemainingTicks()) / (float)CrossbowItem.getChargeDuration(p_174610_);
            }
        });
        register(
            Items.CROSSBOW,
            new ResourceLocation("pulling"),
            (p_174605_, p_174606_, p_174607_, p_174608_) -> p_174607_ != null
                        && p_174607_.isUsingItem()
                        && p_174607_.getUseItem() == p_174605_
                        && !CrossbowItem.isCharged(p_174605_)
                    ? 1.0F
                    : 0.0F
        );
        register(
            Items.CROSSBOW, new ResourceLocation("charged"), (p_275891_, p_275892_, p_275893_, p_275894_) -> CrossbowItem.isCharged(p_275891_) ? 1.0F : 0.0F
        );
        register(Items.CROSSBOW, new ResourceLocation("firework"), (p_325563_, p_325564_, p_325565_, p_325566_) -> {
            ChargedProjectiles chargedprojectiles = p_325563_.m_323252_(DataComponents.f_314625_);
            return chargedprojectiles != null && chargedprojectiles.m_319117_(Items.FIREWORK_ROCKET) ? 1.0F : 0.0F;
        });
        register(Items.ELYTRA, new ResourceLocation("broken"), (p_174590_, p_174591_, p_174592_, p_174593_) -> ElytraItem.isFlyEnabled(p_174590_) ? 0.0F : 1.0F);
        register(Items.FISHING_ROD, new ResourceLocation("cast"), (p_174585_, p_174586_, p_174587_, p_174588_) -> {
            if (p_174587_ == null) {
                return 0.0F;
            } else {
                boolean flag = p_174587_.getMainHandItem() == p_174585_;
                boolean flag1 = p_174587_.getOffhandItem() == p_174585_;
                if (p_174587_.getMainHandItem().getItem() instanceof FishingRodItem) {
                    flag1 = false;
                }

                return (flag || flag1) && p_174587_ instanceof Player && ((Player)p_174587_).fishing != null ? 1.0F : 0.0F;
            }
        });
        register(
            Items.SHIELD,
            new ResourceLocation("blocking"),
            (p_174575_, p_174576_, p_174577_, p_174578_) -> p_174577_ != null && p_174577_.isUsingItem() && p_174577_.getUseItem() == p_174575_ ? 1.0F : 0.0F
        );
        register(
            Items.TRIDENT,
            new ResourceLocation("throwing"),
            (p_234996_, p_234997_, p_234998_, p_234999_) -> p_234998_ != null && p_234998_.isUsingItem() && p_234998_.getUseItem() == p_234996_ ? 1.0F : 0.0F
        );
        register(Items.LIGHT, new ResourceLocation("level"), (p_325567_, p_325568_, p_325569_, p_325570_) -> {
            BlockItemStateProperties blockitemstateproperties = p_325567_.m_322304_(DataComponents.f_315479_, BlockItemStateProperties.f_314882_);
            Integer integer = blockitemstateproperties.m_321723_(LightBlock.LEVEL);
            return integer != null ? (float)integer.intValue() / 16.0F : 1.0F;
        });
        register(
            Items.GOAT_HORN,
            new ResourceLocation("tooting"),
            (p_234978_, p_234979_, p_234980_, p_234981_) -> p_234980_ != null && p_234980_.isUsingItem() && p_234980_.getUseItem() == p_234978_ ? 1.0F : 0.0F
        );
    }
}