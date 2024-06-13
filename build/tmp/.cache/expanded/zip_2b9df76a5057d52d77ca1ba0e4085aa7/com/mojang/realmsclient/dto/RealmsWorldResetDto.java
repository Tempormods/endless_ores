package com.mojang.realmsclient.dto;

import com.google.gson.annotations.SerializedName;
import java.util.Set;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsWorldResetDto extends ValueObject implements ReflectionBasedSerialization {
    @SerializedName("seed")
    private final String seed;
    @SerializedName("worldTemplateId")
    private final long worldTemplateId;
    @SerializedName("levelType")
    private final int levelType;
    @SerializedName("generateStructures")
    private final boolean generateStructures;
    @SerializedName("experiments")
    private final Set<String> f_303503_;

    public RealmsWorldResetDto(String pSeed, long pWorldTemplateId, int pLevelType, boolean pGenerateStructures, Set<String> p_309872_) {
        this.seed = pSeed;
        this.worldTemplateId = pWorldTemplateId;
        this.levelType = pLevelType;
        this.generateStructures = pGenerateStructures;
        this.f_303503_ = p_309872_;
    }
}