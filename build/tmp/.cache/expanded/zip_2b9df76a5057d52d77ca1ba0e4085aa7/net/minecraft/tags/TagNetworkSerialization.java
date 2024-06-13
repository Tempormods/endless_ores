package net.minecraft.tags;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.RegistryLayer;

public class TagNetworkSerialization {
    public static Map<ResourceKey<? extends Registry<?>>, TagNetworkSerialization.NetworkPayload> serializeTagsToNetwork(LayeredRegistryAccess<RegistryLayer> pRegistryAccess) {
        return RegistrySynchronization.networkSafeRegistries(pRegistryAccess)
            .map(p_203949_ -> Pair.of(p_203949_.key(), serializeToNetwork(p_203949_.value())))
            .filter(p_326489_ -> p_326489_.getSecond().m_324247_() > 0)
            .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
    }

    private static <T> TagNetworkSerialization.NetworkPayload serializeToNetwork(Registry<T> pRegistry) {
        Map<ResourceLocation, IntList> map = new HashMap<>();
        pRegistry.getTags().forEach(p_326488_ -> {
            HolderSet<T> holderset = p_326488_.getSecond();
            IntList intlist = new IntArrayList(holderset.size());

            for (Holder<T> holder : holderset) {
                if (holder.kind() != Holder.Kind.REFERENCE) {
                    throw new IllegalStateException("Can't serialize unregistered value " + holder);
                }

                intlist.add(pRegistry.getId(holder.value()));
            }

            map.put(p_326488_.getFirst().location(), intlist);
        });
        return new TagNetworkSerialization.NetworkPayload(map);
    }

    static <T> void deserializeTagsFromNetwork(
        ResourceKey<? extends Registry<T>> pRegistryKey,
        Registry<T> p_203954_,
        TagNetworkSerialization.NetworkPayload pNetworkPayload,
        TagNetworkSerialization.TagOutput<T> pOutput
    ) {
        pNetworkPayload.tags.forEach((p_248278_, p_248279_) -> {
            TagKey<T> tagkey = TagKey.create(pRegistryKey, p_248278_);
            List<Holder<T>> list = p_248279_.intStream().mapToObj(p_203954_::getHolder).flatMap(Optional::stream).collect(Collectors.toUnmodifiableList());
            pOutput.accept(tagkey, list);
        });
    }

    public static final class NetworkPayload {
        final Map<ResourceLocation, IntList> tags;

        NetworkPayload(Map<ResourceLocation, IntList> pTags) {
            this.tags = pTags;
        }

        public void write(FriendlyByteBuf pBuffer) {
            pBuffer.writeMap(this.tags, FriendlyByteBuf::writeResourceLocation, FriendlyByteBuf::writeIntIdList);
        }

        public static TagNetworkSerialization.NetworkPayload read(FriendlyByteBuf pBuffer) {
            return new TagNetworkSerialization.NetworkPayload(pBuffer.readMap(FriendlyByteBuf::readResourceLocation, FriendlyByteBuf::readIntIdList));
        }

        public int m_324247_() {
            return this.tags.size();
        }

        public <T> void m_323556_(Registry<T> p_328181_) {
            if (this.m_324247_() != 0) {
                Map<TagKey<T>, List<Holder<T>>> map = new HashMap<>(this.m_324247_());
                TagNetworkSerialization.deserializeTagsFromNetwork(p_328181_.key(), p_328181_, this, map::put);
                p_328181_.bindTags(map);
            }
        }
    }

    @FunctionalInterface
    public interface TagOutput<T> {
        void accept(TagKey<T> p_203972_, List<Holder<T>> p_203973_);
    }
}