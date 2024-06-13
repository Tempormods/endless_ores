package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.WritableBookContent;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetWritableBookPagesFunction extends LootItemConditionalFunction {
    public static final MapCodec<SetWritableBookPagesFunction> f_314814_ = RecordCodecBuilder.mapCodec(
        p_330843_ -> commonFields(p_330843_)
                .and(
                    p_330843_.group(
                        WritableBookContent.f_313981_.fieldOf("pages").forGetter(p_329804_ -> p_329804_.f_315165_),
                        ListOperation.m_320139_(100).forGetter(p_333000_ -> p_333000_.f_316401_)
                    )
                )
                .apply(p_330843_, SetWritableBookPagesFunction::new)
    );
    private final List<Filterable<String>> f_315165_;
    private final ListOperation f_316401_;

    protected SetWritableBookPagesFunction(List<LootItemCondition> p_330949_, List<Filterable<String>> p_330006_, ListOperation p_334902_) {
        super(p_330949_);
        this.f_315165_ = p_330006_;
        this.f_316401_ = p_334902_;
    }

    @Override
    protected ItemStack run(ItemStack p_329402_, LootContext p_330509_) {
        p_329402_.m_322591_(DataComponents.f_314472_, WritableBookContent.f_314124_, this::m_324955_);
        return p_329402_;
    }

    public WritableBookContent m_324955_(WritableBookContent p_328886_) {
        List<Filterable<String>> list = this.f_316401_.m_320579_(p_328886_.m_319402_(), this.f_315165_, 100);
        return p_328886_.m_319955_(list);
    }

    @Override
    public LootItemFunctionType<SetWritableBookPagesFunction> getType() {
        return LootItemFunctions.f_315246_;
    }
}