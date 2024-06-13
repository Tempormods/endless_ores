package net.minecraft.commands.functions;

import java.util.List;
import net.minecraft.commands.execution.UnboundEntryAction;
import net.minecraft.resources.ResourceLocation;

public interface InstantiatedFunction<T> {
    ResourceLocation m_304900_();

    List<UnboundEntryAction<T>> m_306124_();
}