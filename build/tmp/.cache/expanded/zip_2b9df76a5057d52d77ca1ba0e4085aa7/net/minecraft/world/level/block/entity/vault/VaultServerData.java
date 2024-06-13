package net.minecraft.world.level.block.entity.vault;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class VaultServerData {
    static final String f_315387_ = "server_data";
    static Codec<VaultServerData> f_315361_ = RecordCodecBuilder.create(
        p_331703_ -> p_331703_.group(
                    UUIDUtil.f_315796_.lenientOptionalFieldOf("rewarded_players", Set.of()).forGetter(p_331366_ -> p_331366_.f_315329_),
                    Codec.LONG.lenientOptionalFieldOf("state_updating_resumes_at", Long.valueOf(0L)).forGetter(p_329044_ -> p_329044_.f_316915_),
                    ItemStack.CODEC.listOf().lenientOptionalFieldOf("items_to_eject", List.of()).forGetter(p_328322_ -> p_328322_.f_314906_),
                    Codec.INT.lenientOptionalFieldOf("total_ejections_needed", Integer.valueOf(0)).forGetter(p_329419_ -> p_329419_.f_315977_)
                )
                .apply(p_331703_, VaultServerData::new)
    );
    private static final int f_316541_ = 128;
    private final Set<UUID> f_315329_ = new ObjectLinkedOpenHashSet<>();
    private long f_316915_;
    private final List<ItemStack> f_314906_ = new ObjectArrayList<>();
    private long f_315545_;
    private int f_315977_;
    boolean f_316353_;

    VaultServerData(Set<UUID> p_334629_, long p_331265_, List<ItemStack> p_330511_, int p_333688_) {
        this.f_315329_.addAll(p_334629_);
        this.f_316915_ = p_331265_;
        this.f_314906_.addAll(p_330511_);
        this.f_315977_ = p_333688_;
    }

    VaultServerData() {
    }

    void m_319976_(long p_336284_) {
        this.f_315545_ = p_336284_;
    }

    long m_322332_() {
        return this.f_315545_;
    }

    Set<UUID> m_319069_() {
        return this.f_315329_;
    }

    boolean m_324059_(Player p_336078_) {
        return this.f_315329_.contains(p_336078_.getUUID());
    }

    @VisibleForTesting
    public void m_320315_(Player p_332874_) {
        this.f_315329_.add(p_332874_.getUUID());
        if (this.f_315329_.size() > 128) {
            Iterator<UUID> iterator = this.f_315329_.iterator();
            if (iterator.hasNext()) {
                iterator.next();
                iterator.remove();
            }
        }

        this.m_319478_();
    }

    long m_318811_() {
        return this.f_316915_;
    }

    void m_319712_(long p_330777_) {
        this.f_316915_ = p_330777_;
        this.m_319478_();
    }

    List<ItemStack> m_321830_() {
        return this.f_314906_;
    }

    void m_323313_() {
        this.f_315977_ = 0;
        this.m_319478_();
    }

    void m_322800_(List<ItemStack> p_332570_) {
        this.f_314906_.clear();
        this.f_314906_.addAll(p_332570_);
        this.f_315977_ = this.f_314906_.size();
        this.m_319478_();
    }

    ItemStack m_320646_() {
        return this.f_314906_.isEmpty() ? ItemStack.EMPTY : Objects.requireNonNullElse(this.f_314906_.get(this.f_314906_.size() - 1), ItemStack.EMPTY);
    }

    ItemStack m_319513_() {
        if (this.f_314906_.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            this.m_319478_();
            return Objects.requireNonNullElse(this.f_314906_.remove(this.f_314906_.size() - 1), ItemStack.EMPTY);
        }
    }

    void m_323813_(VaultServerData p_329637_) {
        this.f_316915_ = p_329637_.m_318811_();
        this.f_314906_.clear();
        this.f_314906_.addAll(p_329637_.f_314906_);
        this.f_315329_.clear();
        this.f_315329_.addAll(p_329637_.f_315329_);
    }

    private void m_319478_() {
        this.f_316353_ = true;
    }

    public float m_321331_() {
        return this.f_315977_ == 1 ? 1.0F : 1.0F - Mth.inverseLerp((float)this.m_321830_().size(), 1.0F, (float)this.f_315977_);
    }
}