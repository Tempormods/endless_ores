package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class InventoryChangeTrigger extends SimpleCriterionTrigger<InventoryChangeTrigger.TriggerInstance> {
    @Override
    public Codec<InventoryChangeTrigger.TriggerInstance> createInstance() {
        return InventoryChangeTrigger.TriggerInstance.f_302276_;
    }

    public void trigger(ServerPlayer pPlayer, Inventory pInventory, ItemStack pStack) {
        int i = 0;
        int j = 0;
        int k = 0;

        for (int l = 0; l < pInventory.getContainerSize(); l++) {
            ItemStack itemstack = pInventory.getItem(l);
            if (itemstack.isEmpty()) {
                j++;
            } else {
                k++;
                if (itemstack.getCount() >= itemstack.getMaxStackSize()) {
                    i++;
                }
            }
        }

        this.trigger(pPlayer, pInventory, pStack, i, j, k);
    }

    private void trigger(ServerPlayer pPlayer, Inventory pInventory, ItemStack pStack, int pFull, int pEmpty, int pOccupied) {
        this.trigger(pPlayer, p_43166_ -> p_43166_.matches(pInventory, pStack, pFull, pEmpty, pOccupied));
    }

    public static record TriggerInstance(
        Optional<ContextAwarePredicate> f_302582_, InventoryChangeTrigger.TriggerInstance.Slots f_303868_, List<ItemPredicate> f_303263_
    ) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<InventoryChangeTrigger.TriggerInstance> f_302276_ = RecordCodecBuilder.create(
            p_325218_ -> p_325218_.group(
                        EntityPredicate.f_303210_.optionalFieldOf("player").forGetter(InventoryChangeTrigger.TriggerInstance::playerPredicate),
                        InventoryChangeTrigger.TriggerInstance.Slots.f_302974_
                            .optionalFieldOf("slots", InventoryChangeTrigger.TriggerInstance.Slots.f_303621_)
                            .forGetter(InventoryChangeTrigger.TriggerInstance::f_303868_),
                        ItemPredicate.CODEC.listOf().optionalFieldOf("items", List.of()).forGetter(InventoryChangeTrigger.TriggerInstance::f_303263_)
                    )
                    .apply(p_325218_, InventoryChangeTrigger.TriggerInstance::new)
        );

        public static Criterion<InventoryChangeTrigger.TriggerInstance> hasItems(ItemPredicate.Builder... pItems) {
            return hasItems(Stream.of(pItems).map(ItemPredicate.Builder::build).toArray(ItemPredicate[]::new));
        }

        public static Criterion<InventoryChangeTrigger.TriggerInstance> hasItems(ItemPredicate... pItems) {
            return CriteriaTriggers.INVENTORY_CHANGED
                .createCriterion(
                    new InventoryChangeTrigger.TriggerInstance(Optional.empty(), InventoryChangeTrigger.TriggerInstance.Slots.f_303621_, List.of(pItems))
                );
        }

        public static Criterion<InventoryChangeTrigger.TriggerInstance> hasItems(ItemLike... pItems) {
            ItemPredicate[] aitempredicate = new ItemPredicate[pItems.length];

            for (int i = 0; i < pItems.length; i++) {
                aitempredicate[i] = new ItemPredicate(
                    Optional.of(HolderSet.direct(pItems[i].asItem().builtInRegistryHolder())),
                    MinMaxBounds.Ints.ANY,
                    DataComponentPredicate.f_314891_,
                    Map.of()
                );
            }

            return hasItems(aitempredicate);
        }

        public boolean matches(Inventory pInventory, ItemStack pStack, int pFull, int pEmpty, int pOccupied) {
            if (!this.f_303868_.m_307365_(pFull, pEmpty, pOccupied)) {
                return false;
            } else if (this.f_303263_.isEmpty()) {
                return true;
            } else if (this.f_303263_.size() != 1) {
                List<ItemPredicate> list = new ObjectArrayList<>(this.f_303263_);
                int i = pInventory.getContainerSize();

                for (int j = 0; j < i; j++) {
                    if (list.isEmpty()) {
                        return true;
                    }

                    ItemStack itemstack = pInventory.getItem(j);
                    if (!itemstack.isEmpty()) {
                        list.removeIf(p_325217_ -> p_325217_.test(itemstack));
                    }
                }

                return list.isEmpty();
            } else {
                return !pStack.isEmpty() && this.f_303263_.get(0).test(pStack);
            }
        }

        @Override
        public Optional<ContextAwarePredicate> playerPredicate() {
            return this.f_302582_;
        }

        public static record Slots(MinMaxBounds.Ints f_302306_, MinMaxBounds.Ints f_303437_, MinMaxBounds.Ints f_302651_) {
            public static final Codec<InventoryChangeTrigger.TriggerInstance.Slots> f_302974_ = RecordCodecBuilder.create(
                p_325219_ -> p_325219_.group(
                            MinMaxBounds.Ints.CODEC
                                .optionalFieldOf("occupied", MinMaxBounds.Ints.ANY)
                                .forGetter(InventoryChangeTrigger.TriggerInstance.Slots::f_302306_),
                            MinMaxBounds.Ints.CODEC
                                .optionalFieldOf("full", MinMaxBounds.Ints.ANY)
                                .forGetter(InventoryChangeTrigger.TriggerInstance.Slots::f_303437_),
                            MinMaxBounds.Ints.CODEC
                                .optionalFieldOf("empty", MinMaxBounds.Ints.ANY)
                                .forGetter(InventoryChangeTrigger.TriggerInstance.Slots::f_302651_)
                        )
                        .apply(p_325219_, InventoryChangeTrigger.TriggerInstance.Slots::new)
            );
            public static final InventoryChangeTrigger.TriggerInstance.Slots f_303621_ = new InventoryChangeTrigger.TriggerInstance.Slots(
                MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY
            );

            public boolean m_307365_(int p_310199_, int p_310886_, int p_309638_) {
                if (!this.f_303437_.matches(p_310199_)) {
                    return false;
                } else {
                    return !this.f_302651_.matches(p_310886_) ? false : this.f_302306_.matches(p_309638_);
                }
            }
        }
    }
}