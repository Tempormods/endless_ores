package net.minecraft.world.level.chunk.status;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public record WorldGenContext(ServerLevel f_314224_, ChunkGenerator f_315907_, StructureTemplateManager f_315698_, ThreadedLevelLightEngine f_315420_) {
}