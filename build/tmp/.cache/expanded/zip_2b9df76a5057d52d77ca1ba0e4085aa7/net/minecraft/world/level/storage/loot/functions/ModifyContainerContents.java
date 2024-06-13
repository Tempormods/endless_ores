package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.ContainerComponentManipulator;
import net.minecraft.world.level.storage.loot.ContainerComponentManipulators;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class ModifyContainerContents extends LootItemConditionalFunction {
    public static final MapCodec<ModifyContainerContents> f_315705_ = RecordCodecBuilder.mapCodec(
        p_334502_ -> commonFields(p_334502_)
                .and(
                    p_334502_.group(
                        ContainerComponentManipulators.f_315037_.fieldOf("component").forGetter(p_328799_ -> p_328799_.f_317121_),
                        LootItemFunctions.f_314213_.fieldOf("modifier").forGetter(p_332200_ -> p_332200_.f_315648_)
                    )
                )
                .apply(p_334502_, ModifyContainerContents::new)
    );
    private final ContainerComponentManipulator<?> f_317121_;
    private final LootItemFunction f_315648_;

    private ModifyContainerContents(List<LootItemCondition> p_329722_, ContainerComponentManipulator<?> p_330185_, LootItemFunction p_330905_) {
        super(p_329722_);
        this.f_317121_ = p_330185_;
        this.f_315648_ = p_330905_;
    }

    @Override
    public LootItemFunctionType<ModifyContainerContents> getType() {
        return LootItemFunctions.f_314745_;
    }

    @Override
    public ItemStack run(ItemStack p_329760_, LootContext p_328367_) {
        if (p_329760_.isEmpty()) {
            return p_329760_;
        } else {
            this.f_317121_.m_319566_(p_329760_, p_332662_ -> this.f_315648_.apply(p_332662_, p_328367_));
            return p_329760_;
        }
    }

    @Override
    public void validate(ValidationContext p_332171_) {
        super.validate(p_332171_);
        this.f_315648_.validate(p_332171_.forChild(".modifier"));
    }
}