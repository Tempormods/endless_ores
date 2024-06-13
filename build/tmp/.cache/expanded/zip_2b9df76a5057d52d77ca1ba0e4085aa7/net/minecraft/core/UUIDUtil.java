package net.minecraft.core;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import com.mojang.util.UndashedUuid;
import io.netty.buffer.ByteBuf;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.IntStream;
import net.minecraft.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public final class UUIDUtil {
    public static final Codec<UUID> CODEC = Codec.INT_STREAM
        .comapFlatMap(p_325718_ -> Util.fixedSize(p_325718_, 4).map(UUIDUtil::uuidFromIntArray), p_235888_ -> Arrays.stream(uuidToIntArray(p_235888_)));
    public static final Codec<Set<UUID>> f_302298_ = Codec.list(CODEC).xmap(Sets::newHashSet, Lists::newArrayList);
    public static final Codec<Set<UUID>> f_315796_ = Codec.list(CODEC).xmap(Sets::newLinkedHashSet, Lists::newArrayList);
    public static final Codec<UUID> STRING_CODEC = Codec.STRING.comapFlatMap(p_274732_ -> {
        try {
            return DataResult.success(UUID.fromString(p_274732_), Lifecycle.stable());
        } catch (IllegalArgumentException illegalargumentexception) {
            return DataResult.error(() -> "Invalid UUID " + p_274732_ + ": " + illegalargumentexception.getMessage());
        }
    }, UUID::toString);
    public static final Codec<UUID> AUTHLIB_CODEC = Codec.withAlternative(Codec.STRING.comapFlatMap(p_296331_ -> {
        try {
            return DataResult.success(UndashedUuid.fromStringLenient(p_296331_), Lifecycle.stable());
        } catch (IllegalArgumentException illegalargumentexception) {
            return DataResult.error(() -> "Invalid UUID " + p_296331_ + ": " + illegalargumentexception.getMessage());
        }
    }, UndashedUuid::toString), CODEC);
    public static final Codec<UUID> f_302497_ = Codec.withAlternative(CODEC, STRING_CODEC);
    public static final StreamCodec<ByteBuf, UUID> f_315346_ = new StreamCodec<ByteBuf, UUID>() {
        public UUID m_318688_(ByteBuf p_332317_) {
            return FriendlyByteBuf.m_323841_(p_332317_);
        }

        public void m_318638_(ByteBuf p_331213_, UUID p_327754_) {
            FriendlyByteBuf.m_321780_(p_331213_, p_327754_);
        }
    };
    public static final int UUID_BYTES = 16;
    private static final String UUID_PREFIX_OFFLINE_PLAYER = "OfflinePlayer:";

    private UUIDUtil() {
    }

    public static UUID uuidFromIntArray(int[] p_235886_) {
        return new UUID((long)p_235886_[0] << 32 | (long)p_235886_[1] & 4294967295L, (long)p_235886_[2] << 32 | (long)p_235886_[3] & 4294967295L);
    }

    public static int[] uuidToIntArray(UUID pUuid) {
        long i = pUuid.getMostSignificantBits();
        long j = pUuid.getLeastSignificantBits();
        return leastMostToIntArray(i, j);
    }

    private static int[] leastMostToIntArray(long pMost, long pLeast) {
        return new int[]{(int)(pMost >> 32), (int)pMost, (int)(pLeast >> 32), (int)pLeast};
    }

    public static byte[] uuidToByteArray(UUID pUuid) {
        byte[] abyte = new byte[16];
        ByteBuffer.wrap(abyte).order(ByteOrder.BIG_ENDIAN).putLong(pUuid.getMostSignificantBits()).putLong(pUuid.getLeastSignificantBits());
        return abyte;
    }

    public static UUID readUUID(Dynamic<?> pDynamic) {
        int[] aint = pDynamic.asIntStream().toArray();
        if (aint.length != 4) {
            throw new IllegalArgumentException("Could not read UUID. Expected int-array of length 4, got " + aint.length + ".");
        } else {
            return uuidFromIntArray(aint);
        }
    }

    public static UUID createOfflinePlayerUUID(String pUsername) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + pUsername).getBytes(StandardCharsets.UTF_8));
    }

    public static GameProfile m_307091_(String p_309926_) {
        UUID uuid = createOfflinePlayerUUID(p_309926_);
        return new GameProfile(uuid, p_309926_);
    }
}