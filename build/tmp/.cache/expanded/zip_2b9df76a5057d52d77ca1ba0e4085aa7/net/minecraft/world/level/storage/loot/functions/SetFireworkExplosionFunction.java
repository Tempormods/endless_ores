package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetFireworkExplosionFunction extends LootItemConditionalFunction {
    public static final MapCodec<SetFireworkExplosionFunction> f_316365_ = RecordCodecBuilder.mapCodec(
        p_332148_ -> commonFields(p_332148_)
                .and(
                    p_332148_.group(
                        FireworkExplosion.Shape.f_314578_.optionalFieldOf("shape").forGetter(p_328575_ -> p_328575_.f_314735_),
                        FireworkExplosion.f_314508_.optionalFieldOf("colors").forGetter(p_335296_ -> p_335296_.f_314363_),
                        FireworkExplosion.f_314508_.optionalFieldOf("fade_colors").forGetter(p_333347_ -> p_333347_.f_316588_),
                        Codec.BOOL.optionalFieldOf("trail").forGetter(p_329057_ -> p_329057_.f_316845_),
                        Codec.BOOL.optionalFieldOf("twinkle").forGetter(p_333792_ -> p_333792_.f_315196_)
                    )
                )
                .apply(p_332148_, SetFireworkExplosionFunction::new)
    );
    public static final FireworkExplosion f_315719_ = new FireworkExplosion(FireworkExplosion.Shape.SMALL_BALL, IntList.of(), IntList.of(), false, false);
    final Optional<FireworkExplosion.Shape> f_314735_;
    final Optional<IntList> f_314363_;
    final Optional<IntList> f_316588_;
    final Optional<Boolean> f_316845_;
    final Optional<Boolean> f_315196_;

    public SetFireworkExplosionFunction(
        List<LootItemCondition> p_328435_,
        Optional<FireworkExplosion.Shape> p_335053_,
        Optional<IntList> p_331523_,
        Optional<IntList> p_331948_,
        Optional<Boolean> p_330337_,
        Optional<Boolean> p_335969_
    ) {
        super(p_328435_);
        this.f_314735_ = p_335053_;
        this.f_314363_ = p_331523_;
        this.f_316588_ = p_331948_;
        this.f_316845_ = p_330337_;
        this.f_315196_ = p_335969_;
    }

    @Override
    protected ItemStack run(ItemStack p_328627_, LootContext p_327748_) {
        p_328627_.m_322591_(DataComponents.f_315608_, f_315719_, this::m_320539_);
        return p_328627_;
    }

    private FireworkExplosion m_320539_(FireworkExplosion p_329657_) {
        return new FireworkExplosion(
            this.f_314735_.orElseGet(p_329657_::f_316547_),
            this.f_314363_.orElseGet(p_329657_::f_316201_),
            this.f_316588_.orElseGet(p_329657_::f_314743_),
            this.f_316845_.orElseGet(p_329657_::f_316522_),
            this.f_315196_.orElseGet(p_329657_::f_316285_)
        );
    }

    @Override
    public LootItemFunctionType<SetFireworkExplosionFunction> getType() {
        return LootItemFunctions.f_314869_;
    }
}