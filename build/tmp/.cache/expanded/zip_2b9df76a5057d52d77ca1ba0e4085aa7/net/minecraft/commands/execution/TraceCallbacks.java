package net.minecraft.commands.execution;

import net.minecraft.resources.ResourceLocation;

public interface TraceCallbacks extends AutoCloseable {
    void m_180083_(int p_312367_, String p_309424_);

    void m_180086_(int p_309782_, String p_310974_, int p_309683_);

    void m_180099_(String p_312377_);

    void m_180090_(int p_313011_, ResourceLocation p_309460_, int p_309874_);

    @Override
    void close();
}