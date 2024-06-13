package net.minecraft.world.entity;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

public record EquipmentTable(ResourceKey<LootTable> f_316700_, Map<EquipmentSlot, Float> f_315505_) {
    public static final Codec<Map<EquipmentSlot, Float>> f_316849_ = Codec.either(Codec.FLOAT, Codec.unboundedMap(EquipmentSlot.CODEC, Codec.FLOAT))
        .xmap(p_330726_ -> p_330726_.map(EquipmentTable::m_322309_, Function.identity()), p_329105_ -> {
            boolean flag = p_329105_.values().stream().distinct().count() == 1L;
            boolean flag1 = p_329105_.keySet().containsAll(Arrays.asList(EquipmentSlot.values()));
            return flag && flag1 ? Either.left(p_329105_.values().stream().findFirst().orElse(0.0F)) : Either.right((Map<EquipmentSlot, Float>)p_329105_);
        });
    public static final Codec<EquipmentTable> f_313947_ = RecordCodecBuilder.create(
        p_328347_ -> p_328347_.group(
                    ResourceKey.codec(Registries.f_314309_).fieldOf("loot_table").forGetter(EquipmentTable::f_316700_),
                    f_316849_.optionalFieldOf("slot_drop_chances", Map.of()).forGetter(EquipmentTable::f_315505_)
                )
                .apply(p_328347_, EquipmentTable::new)
    );

    private static Map<EquipmentSlot, Float> m_322309_(float p_335505_) {
        return m_321920_(List.of(EquipmentSlot.values()), p_335505_);
    }

    private static Map<EquipmentSlot, Float> m_321920_(List<EquipmentSlot> p_331241_, float p_334411_) {
        Map<EquipmentSlot, Float> map = Maps.newHashMap();

        for (EquipmentSlot equipmentslot : p_331241_) {
            map.put(equipmentslot, p_334411_);
        }

        return map;
    }
}