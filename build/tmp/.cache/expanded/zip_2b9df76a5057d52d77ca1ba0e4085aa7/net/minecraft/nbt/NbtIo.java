package net.minecraft.nbt;

import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UTFDataFormatException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.Util;
import net.minecraft.util.DelegateDataOutput;
import net.minecraft.util.FastBufferedInputStream;

public class NbtIo {
    private static final OpenOption[] f_302613_ = new OpenOption[]{
        StandardOpenOption.SYNC, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING
    };

    public static CompoundTag readCompressed(Path p_310303_, NbtAccounter p_311830_) throws IOException {
        CompoundTag compoundtag;
        try (
            InputStream inputstream = Files.newInputStream(p_310303_);
            InputStream inputstream1 = new FastBufferedInputStream(inputstream);
        ) {
            compoundtag = readCompressed(inputstream1, p_311830_);
        }

        return compoundtag;
    }

    private static DataInputStream createDecompressorStream(InputStream pZippedStream) throws IOException {
        return new DataInputStream(new FastBufferedInputStream(new GZIPInputStream(pZippedStream)));
    }

    private static DataOutputStream m_306903_(OutputStream p_310411_) throws IOException {
        return new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(p_310411_)));
    }

    public static CompoundTag readCompressed(InputStream p_313037_, NbtAccounter p_312435_) throws IOException {
        CompoundTag compoundtag;
        try (DataInputStream datainputstream = createDecompressorStream(p_313037_)) {
            compoundtag = read(datainputstream, p_312435_);
        }

        return compoundtag;
    }

    public static void parseCompressed(Path p_310443_, StreamTagVisitor pVisitor, NbtAccounter pAccounter) throws IOException {
        try (
            InputStream inputstream = Files.newInputStream(p_310443_);
            InputStream inputstream1 = new FastBufferedInputStream(inputstream);
        ) {
            parseCompressed(inputstream1, pVisitor, pAccounter);
        }
    }

    public static void parseCompressed(InputStream pZippedStream, StreamTagVisitor pVisitor, NbtAccounter pAccounter) throws IOException {
        try (DataInputStream datainputstream = createDecompressorStream(pZippedStream)) {
            parse(datainputstream, pVisitor, pAccounter);
        }
    }

    public static void writeCompressed(CompoundTag pCompoundTag, Path p_310344_) throws IOException {
        try (
            OutputStream outputstream = Files.newOutputStream(p_310344_, f_302613_);
            OutputStream outputstream1 = new BufferedOutputStream(outputstream);
        ) {
            writeCompressed(pCompoundTag, outputstream1);
        }
    }

    public static void writeCompressed(CompoundTag pCompoundTag, OutputStream pOutputStream) throws IOException {
        try (DataOutputStream dataoutputstream = m_306903_(pOutputStream)) {
            write(pCompoundTag, dataoutputstream);
        }
    }

    public static void write(CompoundTag pCompoundTag, Path p_311890_) throws IOException {
        try (
            OutputStream outputstream = Files.newOutputStream(p_311890_, f_302613_);
            OutputStream outputstream1 = new BufferedOutputStream(outputstream);
            DataOutputStream dataoutputstream = new DataOutputStream(outputstream1);
        ) {
            write(pCompoundTag, dataoutputstream);
        }
    }

    @Nullable
    public static CompoundTag read(Path p_310670_) throws IOException {
        if (!Files.exists(p_310670_)) {
            return null;
        } else {
            CompoundTag compoundtag;
            try (
                InputStream inputstream = Files.newInputStream(p_310670_);
                DataInputStream datainputstream = new DataInputStream(inputstream);
            ) {
                compoundtag = read(datainputstream, NbtAccounter.unlimitedHeap());
            }

            return compoundtag;
        }
    }

    public static CompoundTag read(DataInput pInput) throws IOException {
        return read(pInput, NbtAccounter.unlimitedHeap());
    }

    public static CompoundTag read(DataInput pInput, NbtAccounter pAccounter) throws IOException {
        Tag tag = readUnnamedTag(pInput, pAccounter);
        if (tag instanceof CompoundTag) {
            return (CompoundTag)tag;
        } else {
            throw new IOException("Root tag must be a named compound tag");
        }
    }

    public static void write(CompoundTag pCompoundTag, DataOutput pOutput) throws IOException {
        m_306638_(pCompoundTag, pOutput);
    }

    public static void parse(DataInput pInput, StreamTagVisitor pVisitor, NbtAccounter pAccounter) throws IOException {
        TagType<?> tagtype = TagTypes.getType(pInput.readByte());
        if (tagtype == EndTag.TYPE) {
            if (pVisitor.visitRootEntry(EndTag.TYPE) == StreamTagVisitor.ValueResult.CONTINUE) {
                pVisitor.visitEnd();
            }
        } else {
            switch (pVisitor.visitRootEntry(tagtype)) {
                case HALT:
                default:
                    break;
                case BREAK:
                    StringTag.skipString(pInput);
                    tagtype.skip(pInput, pAccounter);
                    break;
                case CONTINUE:
                    StringTag.skipString(pInput);
                    tagtype.parse(pInput, pVisitor, pAccounter);
            }
        }
    }

    public static Tag readAnyTag(DataInput pInput, NbtAccounter pAccounter) throws IOException {
        byte b0 = pInput.readByte();
        return (Tag)(b0 == 0 ? EndTag.INSTANCE : readTagSafe(pInput, pAccounter, b0));
    }

    public static void writeAnyTag(Tag pTag, DataOutput pOutput) throws IOException {
        pOutput.writeByte(pTag.getId());
        if (pTag.getId() != 0) {
            pTag.write(pOutput);
        }
    }

    public static void writeUnnamedTag(Tag pTag, DataOutput pOutput) throws IOException {
        pOutput.writeByte(pTag.getId());
        if (pTag.getId() != 0) {
            pOutput.writeUTF("");
            pTag.write(pOutput);
        }
    }

    public static void m_306638_(Tag p_310490_, DataOutput p_311501_) throws IOException {
        writeUnnamedTag(p_310490_, new NbtIo.StringFallbackDataOutput(p_311501_));
    }

    private static Tag readUnnamedTag(DataInput pInput, NbtAccounter pAccounter) throws IOException {
        byte b0 = pInput.readByte();
        if (b0 == 0) {
            return EndTag.INSTANCE;
        } else {
            StringTag.skipString(pInput);
            return readTagSafe(pInput, pAccounter, b0);
        }
    }

    private static Tag readTagSafe(DataInput pInput, NbtAccounter pAccounter, byte pType) {
        try {
            return TagTypes.getType(pType).load(pInput, pAccounter);
        } catch (IOException ioexception) {
            CrashReport crashreport = CrashReport.forThrowable(ioexception, "Loading NBT data");
            CrashReportCategory crashreportcategory = crashreport.addCategory("NBT Tag");
            crashreportcategory.setDetail("Tag type", pType);
            throw new ReportedNbtException(crashreport);
        }
    }

    public static class StringFallbackDataOutput extends DelegateDataOutput {
        public StringFallbackDataOutput(DataOutput p_311190_) {
            super(p_311190_);
        }

        @Override
        public void writeUTF(String p_311566_) throws IOException {
            try {
                super.writeUTF(p_311566_);
            } catch (UTFDataFormatException utfdataformatexception) {
                Util.logAndPauseIfInIde("Failed to write NBT String", utfdataformatexception);
                super.writeUTF("");
            }
        }
    }
}