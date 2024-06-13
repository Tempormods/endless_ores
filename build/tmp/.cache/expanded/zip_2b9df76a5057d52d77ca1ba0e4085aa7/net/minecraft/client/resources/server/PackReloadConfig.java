package net.minecraft.client.resources.server;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface PackReloadConfig {
    void m_305726_(PackReloadConfig.Callbacks p_312695_);

    @OnlyIn(Dist.CLIENT)
    public interface Callbacks {
        void m_304954_();

        void m_304685_(boolean p_313000_);

        List<PackReloadConfig.IdAndPath> m_305324_();
    }

    @OnlyIn(Dist.CLIENT)
    public static record IdAndPath(UUID f_302551_, Path f_303359_) {
    }
}