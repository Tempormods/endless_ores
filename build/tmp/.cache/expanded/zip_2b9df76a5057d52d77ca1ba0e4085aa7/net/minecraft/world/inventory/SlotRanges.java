package net.minecraft.world.inventory;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EquipmentSlot;

public class SlotRanges {
    private static final List<SlotRange> f_315983_ = Util.make(new ArrayList<>(), p_329672_ -> {
        m_323062_(p_329672_, "contents", 0);
        m_322310_(p_329672_, "container.", 0, 54);
        m_322310_(p_329672_, "hotbar.", 0, 9);
        m_322310_(p_329672_, "inventory.", 9, 27);
        m_322310_(p_329672_, "enderchest.", 200, 27);
        m_322310_(p_329672_, "villager.", 300, 8);
        m_322310_(p_329672_, "horse.", 500, 15);
        int i = EquipmentSlot.MAINHAND.getIndex(98);
        int j = EquipmentSlot.OFFHAND.getIndex(98);
        m_323062_(p_329672_, "weapon", i);
        m_323062_(p_329672_, "weapon.mainhand", i);
        m_323062_(p_329672_, "weapon.offhand", j);
        m_321475_(p_329672_, "weapon.*", i, j);
        i = EquipmentSlot.HEAD.getIndex(100);
        j = EquipmentSlot.CHEST.getIndex(100);
        int k = EquipmentSlot.LEGS.getIndex(100);
        int l = EquipmentSlot.FEET.getIndex(100);
        int i1 = EquipmentSlot.BODY.getIndex(105);
        m_323062_(p_329672_, "armor.head", i);
        m_323062_(p_329672_, "armor.chest", j);
        m_323062_(p_329672_, "armor.legs", k);
        m_323062_(p_329672_, "armor.feet", l);
        m_323062_(p_329672_, "armor.body", i1);
        m_321475_(p_329672_, "armor.*", i, j, k, l, i1);
        m_323062_(p_329672_, "horse.saddle", 400);
        m_323062_(p_329672_, "horse.chest", 499);
        m_323062_(p_329672_, "player.cursor", 499);
        m_322310_(p_329672_, "player.crafting.", 500, 4);
    });
    public static final Codec<SlotRange> f_314179_ = StringRepresentable.m_306774_(() -> f_315983_.toArray(new SlotRange[0]));
    private static final Function<String, SlotRange> f_314499_ = StringRepresentable.m_306378_(f_315983_.toArray(new SlotRange[0]), p_331861_ -> p_331861_);

    private static SlotRange m_322348_(String p_328484_, int p_335544_) {
        return SlotRange.m_324344_(p_328484_, IntLists.singleton(p_335544_));
    }

    private static SlotRange m_320805_(String p_330835_, IntList p_333821_) {
        return SlotRange.m_324344_(p_330835_, IntLists.unmodifiable(p_333821_));
    }

    private static SlotRange m_321881_(String p_333478_, int... p_336035_) {
        return SlotRange.m_324344_(p_333478_, IntList.of(p_336035_));
    }

    private static void m_323062_(List<SlotRange> p_332328_, String p_334715_, int p_328171_) {
        p_332328_.add(m_322348_(p_334715_, p_328171_));
    }

    private static void m_322310_(List<SlotRange> p_328374_, String p_331284_, int p_329588_, int p_336322_) {
        IntList intlist = new IntArrayList(p_336322_);

        for (int i = 0; i < p_336322_; i++) {
            int j = p_329588_ + i;
            p_328374_.add(m_322348_(p_331284_ + i, j));
            intlist.add(j);
        }

        p_328374_.add(m_320805_(p_331284_ + "*", intlist));
    }

    private static void m_321475_(List<SlotRange> p_329581_, String p_328279_, int... p_332253_) {
        p_329581_.add(m_321881_(p_328279_, p_332253_));
    }

    @Nullable
    public static SlotRange m_323685_(String p_328330_) {
        return f_314499_.apply(p_328330_);
    }

    public static Stream<String> m_321260_() {
        return f_315983_.stream().map(StringRepresentable::getSerializedName);
    }

    public static Stream<String> m_322944_() {
        return f_315983_.stream().filter(p_336128_ -> p_336128_.m_319620_() == 1).map(StringRepresentable::getSerializedName);
    }
}