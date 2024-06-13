package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.network.Filterable;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetBookCoverFunction extends LootItemConditionalFunction {
    public static final MapCodec<SetBookCoverFunction> f_314861_ = RecordCodecBuilder.mapCodec(
        p_334246_ -> commonFields(p_334246_)
                .and(
                    p_334246_.group(
                        Filterable.m_322486_(Codec.string(0, 32)).optionalFieldOf("title").forGetter(p_332216_ -> p_332216_.f_314087_),
                        Codec.STRING.optionalFieldOf("author").forGetter(p_329081_ -> p_329081_.f_315706_),
                        ExtraCodecs.intRange(0, 3).optionalFieldOf("generation").forGetter(p_332368_ -> p_332368_.f_314489_)
                    )
                )
                .apply(p_334246_, SetBookCoverFunction::new)
    );
    private final Optional<String> f_315706_;
    private final Optional<Filterable<String>> f_314087_;
    private final Optional<Integer> f_314489_;

    public SetBookCoverFunction(
        List<LootItemCondition> p_335903_, Optional<Filterable<String>> p_331140_, Optional<String> p_331575_, Optional<Integer> p_328783_
    ) {
        super(p_335903_);
        this.f_315706_ = p_331575_;
        this.f_314087_ = p_331140_;
        this.f_314489_ = p_328783_;
    }

    @Override
    protected ItemStack run(ItemStack p_331816_, LootContext p_333079_) {
        p_331816_.m_322591_(DataComponents.f_315840_, WrittenBookContent.f_315751_, this::m_320233_);
        return p_331816_;
    }

    private WrittenBookContent m_320233_(WrittenBookContent p_331548_) {
        return new WrittenBookContent(
            this.f_314087_.orElseGet(p_331548_::f_316867_),
            this.f_315706_.orElseGet(p_331548_::f_316008_),
            this.f_314489_.orElseGet(p_331548_::f_314404_),
            p_331548_.m_319402_(),
            p_331548_.f_316486_()
        );
    }

    @Override
    public LootItemFunctionType<SetBookCoverFunction> getType() {
        return LootItemFunctions.f_315027_;
    }
}