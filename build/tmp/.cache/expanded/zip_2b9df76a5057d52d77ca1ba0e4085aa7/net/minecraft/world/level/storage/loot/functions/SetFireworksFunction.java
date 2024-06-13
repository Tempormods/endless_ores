package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetFireworksFunction extends LootItemConditionalFunction {
    public static final MapCodec<SetFireworksFunction> f_316516_ = RecordCodecBuilder.mapCodec(
        p_333231_ -> commonFields(p_333231_)
                .and(
                    p_333231_.group(
                        ListOperation.StandAlone.m_321359_(FireworkExplosion.f_315661_, 256)
                            .optionalFieldOf("explosions")
                            .forGetter(p_333881_ -> p_333881_.f_316748_),
                        ExtraCodecs.f_316863_.optionalFieldOf("flight_duration").forGetter(p_335946_ -> p_335946_.f_315337_)
                    )
                )
                .apply(p_333231_, SetFireworksFunction::new)
    );
    public static final Fireworks f_314460_ = new Fireworks(0, List.of());
    private final Optional<ListOperation.StandAlone<FireworkExplosion>> f_316748_;
    private final Optional<Integer> f_315337_;

    protected SetFireworksFunction(
        List<LootItemCondition> p_335106_, Optional<ListOperation.StandAlone<FireworkExplosion>> p_334501_, Optional<Integer> p_334583_
    ) {
        super(p_335106_);
        this.f_316748_ = p_334501_;
        this.f_315337_ = p_334583_;
    }

    @Override
    protected ItemStack run(ItemStack p_331574_, LootContext p_328031_) {
        p_331574_.m_322591_(DataComponents.f_316632_, f_314460_, this::m_324619_);
        return p_331574_;
    }

    private Fireworks m_324619_(Fireworks p_332116_) {
        return new Fireworks(
            this.f_315337_.orElseGet(p_332116_::f_317050_),
            this.f_316748_.<List<FireworkExplosion>>map(p_331021_ -> p_331021_.m_321030_(p_332116_.f_314926_())).orElse(p_332116_.f_314926_())
        );
    }

    @Override
    public LootItemFunctionType<SetFireworksFunction> getType() {
        return LootItemFunctions.f_315674_;
    }
}