package net.minecraft.data.structures;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import net.minecraft.DetectedVersion;
import net.minecraft.SharedConstants;
import net.minecraft.data.CachedOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.Bootstrap;

public class SnbtDatafixer {
    public static void main(String[] pArgs) throws IOException {
        SharedConstants.setVersion(DetectedVersion.BUILT_IN);
        Bootstrap.bootStrap();

        for (String s : pArgs) {
            updateInDirectory(s);
        }
    }

    private static void updateInDirectory(String pPath) throws IOException {
        try (Stream<Path> stream = Files.walk(Paths.get(pPath))) {
            stream.filter(p_300817_ -> p_300817_.toString().endsWith(".snbt")).forEach(p_298886_ -> {
                try {
                    String s = Files.readString(p_298886_);
                    CompoundTag compoundtag = NbtUtils.snbtToStructure(s);
                    CompoundTag compoundtag1 = StructureUpdater.update(p_298886_.toString(), compoundtag);
                    NbtToSnbt.writeSnbt(CachedOutput.NO_CACHE, p_298886_, NbtUtils.structureToSnbt(compoundtag1));
                } catch (IOException | CommandSyntaxException commandsyntaxexception) {
                    throw new RuntimeException(commandsyntaxexception);
                }
            });
        }
    }
}