package net.minecraft.core;

import com.mojang.serialization.Lifecycle;
import java.util.Optional;
import net.minecraft.server.packs.repository.KnownPack;

public record RegistrationInfo(Optional<KnownPack> f_315839_, Lifecycle f_313951_) {
    public static final RegistrationInfo f_316022_ = new RegistrationInfo(Optional.empty(), Lifecycle.stable());
}