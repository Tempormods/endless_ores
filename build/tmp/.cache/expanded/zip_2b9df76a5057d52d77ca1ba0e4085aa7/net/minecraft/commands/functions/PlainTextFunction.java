package net.minecraft.commands.functions;

import com.mojang.brigadier.CommandDispatcher;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.commands.FunctionInstantiationException;
import net.minecraft.commands.execution.UnboundEntryAction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public record PlainTextFunction<T>(ResourceLocation f_303680_, List<UnboundEntryAction<T>> f_302327_) implements CommandFunction<T>, InstantiatedFunction<T> {
    @Override
    public InstantiatedFunction<T> m_304684_(@Nullable CompoundTag p_311629_, CommandDispatcher<T> p_311161_) throws FunctionInstantiationException {
        return this;
    }

    @Override
    public ResourceLocation m_304900_() {
        return this.f_303680_;
    }

    @Override
    public List<UnboundEntryAction<T>> m_306124_() {
        return this.f_302327_;
    }
}