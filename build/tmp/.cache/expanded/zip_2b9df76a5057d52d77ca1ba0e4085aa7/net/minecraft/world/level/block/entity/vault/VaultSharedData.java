package net.minecraft.world.level.block.entity.vault;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;

public class VaultSharedData {
    static final String f_315249_ = "shared_data";
    static Codec<VaultSharedData> f_314733_ = RecordCodecBuilder.create(
        p_332167_ -> p_332167_.group(
                    ItemStack.m_323240_("display_item").forGetter(p_328885_ -> p_328885_.f_316301_),
                    UUIDUtil.f_315796_.lenientOptionalFieldOf("connected_players", Set.of()).forGetter(p_333733_ -> p_333733_.f_316286_),
                    Codec.DOUBLE
                        .lenientOptionalFieldOf("connected_particles_range", Double.valueOf(VaultConfig.f_314544_.f_314383_()))
                        .forGetter(p_333675_ -> p_333675_.f_314556_)
                )
                .apply(p_332167_, VaultSharedData::new)
    );
    private ItemStack f_316301_ = ItemStack.EMPTY;
    private Set<UUID> f_316286_ = new ObjectLinkedOpenHashSet<>();
    private double f_314556_ = VaultConfig.f_314544_.f_314383_();
    boolean f_315421_;

    VaultSharedData(ItemStack p_336127_, Set<UUID> p_328242_, double p_334724_) {
        this.f_316301_ = p_336127_;
        this.f_316286_.addAll(p_328242_);
        this.f_314556_ = p_334724_;
    }

    VaultSharedData() {
    }

    public ItemStack m_321880_() {
        return this.f_316301_;
    }

    public boolean m_323977_() {
        return !this.f_316301_.isEmpty();
    }

    public void m_319450_(ItemStack p_328271_) {
        if (!ItemStack.matches(this.f_316301_, p_328271_)) {
            this.f_316301_ = p_328271_.copy();
            this.m_318900_();
        }
    }

    boolean m_321463_() {
        return !this.f_316286_.isEmpty();
    }

    Set<UUID> m_324860_() {
        return this.f_316286_;
    }

    double m_322015_() {
        return this.f_314556_;
    }

    void m_321245_(ServerLevel p_335653_, BlockPos p_328626_, VaultServerData p_333530_, VaultConfig p_327683_, double p_332168_) {
        Set<UUID> set = p_327683_.f_315955_()
            .m_305839_(p_335653_, p_327683_.f_315772_(), p_328626_, p_332168_, false)
            .stream()
            .filter(p_335249_ -> !p_333530_.m_319069_().contains(p_335249_))
            .collect(Collectors.toSet());
        if (!this.f_316286_.equals(set)) {
            this.f_316286_ = set;
            this.m_318900_();
        }
    }

    private void m_318900_() {
        this.f_315421_ = true;
    }

    void m_319383_(VaultSharedData p_334535_) {
        this.f_316301_ = p_334535_.f_316301_;
        this.f_316286_ = p_334535_.f_316286_;
        this.f_314556_ = p_334535_.f_314556_;
    }
}