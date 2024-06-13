package net.minecraft.util.profiling.jfr.event;

import jdk.jfr.Category;
import jdk.jfr.Enabled;
import jdk.jfr.Event;
import jdk.jfr.Label;
import jdk.jfr.Name;
import jdk.jfr.StackTrace;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.storage.RegionFileVersion;
import net.minecraft.world.level.chunk.storage.RegionStorageInfo;

@Category({"Minecraft", "Storage"})
@StackTrace(false)
@Enabled(false)
public abstract class ChunkRegionIoEvent extends Event {
    @Name("regionPosX")
    @Label("Region X Position")
    public final int regionPosX;
    @Name("regionPosZ")
    @Label("Region Z Position")
    public final int regionPosZ;
    @Name("localPosX")
    @Label("Local X Position")
    public final int localChunkPosX;
    @Name("localPosZ")
    @Label("Local Z Position")
    public final int localChunkPosZ;
    @Name("chunkPosX")
    @Label("Chunk X Position")
    public final int chunkPosX;
    @Name("chunkPosZ")
    @Label("Chunk Z Position")
    public final int chunkPosZ;
    @Name("level")
    @Label("Level Id")
    public final String levelId;
    @Name("dimension")
    @Label("Dimension")
    public final String dimension;
    @Name("type")
    @Label("Type")
    public final String type;
    @Name("compression")
    @Label("Compression")
    public final String compression;
    @Name("bytes")
    @Label("Bytes")
    public final int bytes;

    public ChunkRegionIoEvent(RegionStorageInfo p_335007_, ChunkPos p_328585_, RegionFileVersion p_333736_, int p_333935_) {
        this.regionPosX = p_328585_.getRegionX();
        this.regionPosZ = p_328585_.getRegionZ();
        this.localChunkPosX = p_328585_.getRegionLocalX();
        this.localChunkPosZ = p_328585_.getRegionLocalZ();
        this.chunkPosX = p_328585_.x;
        this.chunkPosZ = p_328585_.z;
        this.levelId = p_335007_.f_314351_();
        this.dimension = p_335007_.f_316873_().location().toString();
        this.type = p_335007_.f_314842_();
        this.compression = "standard:" + p_333736_.getId();
        this.bytes = p_333935_;
    }

    public static class Fields {
        public static final String f_316393_ = "regionPosX";
        public static final String f_314673_ = "regionPosZ";
        public static final String f_315950_ = "localPosX";
        public static final String f_314565_ = "localPosZ";
        public static final String f_315748_ = "chunkPosX";
        public static final String f_315397_ = "chunkPosZ";
        public static final String f_315298_ = "level";
        public static final String f_314394_ = "dimension";
        public static final String f_315703_ = "type";
        public static final String f_317073_ = "compression";
        public static final String f_316556_ = "bytes";

        private Fields() {
        }
    }
}