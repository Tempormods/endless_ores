package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class LongTag extends NumericTag {
    private static final int SELF_SIZE_IN_BYTES = 16;
    public static final TagType<LongTag> TYPE = new TagType.StaticSize<LongTag>() {
        public LongTag load(DataInput p_128911_, NbtAccounter p_128913_) throws IOException {
            return LongTag.valueOf(readAccounted(p_128911_, p_128913_));
        }

        @Override
        public StreamTagVisitor.ValueResult parse(DataInput p_197506_, StreamTagVisitor p_197507_, NbtAccounter p_301736_) throws IOException {
            return p_197507_.visit(readAccounted(p_197506_, p_301736_));
        }

        private static long readAccounted(DataInput p_301733_, NbtAccounter p_301774_) throws IOException {
            p_301774_.accountBytes(16L);
            return p_301733_.readLong();
        }

        @Override
        public int size() {
            return 8;
        }

        @Override
        public String getName() {
            return "LONG";
        }

        @Override
        public String getPrettyName() {
            return "TAG_Long";
        }

        @Override
        public boolean isValue() {
            return true;
        }
    };
    private final long data;

    LongTag(long pData) {
        this.data = pData;
    }

    public static LongTag valueOf(long pData) {
        return pData >= -128L && pData <= 1024L ? LongTag.Cache.cache[(int)pData - -128] : new LongTag(pData);
    }

    @Override
    public void write(DataOutput pOutput) throws IOException {
        pOutput.writeLong(this.data);
    }

    @Override
    public int sizeInBytes() {
        return 16;
    }

    @Override
    public byte getId() {
        return 4;
    }

    @Override
    public TagType<LongTag> getType() {
        return TYPE;
    }

    public LongTag copy() {
        return this;
    }

    @Override
    public boolean equals(Object pOther) {
        return this == pOther ? true : pOther instanceof LongTag && this.data == ((LongTag)pOther).data;
    }

    @Override
    public int hashCode() {
        return (int)(this.data ^ this.data >>> 32);
    }

    @Override
    public void accept(TagVisitor pVisitor) {
        pVisitor.visitLong(this);
    }

    @Override
    public long getAsLong() {
        return this.data;
    }

    @Override
    public int getAsInt() {
        return (int)(this.data & -1L);
    }

    @Override
    public short getAsShort() {
        return (short)((int)(this.data & 65535L));
    }

    @Override
    public byte getAsByte() {
        return (byte)((int)(this.data & 255L));
    }

    @Override
    public double getAsDouble() {
        return (double)this.data;
    }

    @Override
    public float getAsFloat() {
        return (float)this.data;
    }

    @Override
    public Number getAsNumber() {
        return this.data;
    }

    @Override
    public StreamTagVisitor.ValueResult accept(StreamTagVisitor pVisitor) {
        return pVisitor.visit(this.data);
    }

    static class Cache {
        private static final int HIGH = 1024;
        private static final int LOW = -128;
        static final LongTag[] cache = new LongTag[1153];

        private Cache() {
        }

        static {
            for (int i = 0; i < cache.length; i++) {
                cache[i] = new LongTag((long)(-128 + i));
            }
        }
    }
}