package net.minecraft.server.packs;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nullable;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.util.GsonHelper;
import org.slf4j.Logger;

public abstract class AbstractPackResources implements PackResources {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final PackLocationInfo f_315234_;

    protected AbstractPackResources(PackLocationInfo p_332936_) {
        this.f_315234_ = p_332936_;
    }

    @Nullable
    @Override
    public <T> T getMetadataSection(MetadataSectionSerializer<T> pDeserializer) throws IOException {
        IoSupplier<InputStream> iosupplier = this.getRootResource(new String[]{"pack.mcmeta"});
        if (iosupplier == null) {
            return null;
        } else {
            Object object;
            try (InputStream inputstream = iosupplier.get()) {
                object = getMetadataFromStream(pDeserializer, inputstream);
            }

            return (T)object;
        }
    }

    @Nullable
    public static <T> T getMetadataFromStream(MetadataSectionSerializer<T> pDeserializer, InputStream pInputStream) {
        JsonObject jsonobject;
        try (BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(pInputStream, StandardCharsets.UTF_8))) {
            jsonobject = GsonHelper.parse(bufferedreader);
        } catch (Exception exception1) {
            LOGGER.error("Couldn't load {} metadata", pDeserializer.getMetadataSectionName(), exception1);
            return null;
        }

        if (!jsonobject.has(pDeserializer.getMetadataSectionName())) {
            return null;
        } else {
            try {
                return pDeserializer.fromJson(GsonHelper.getAsJsonObject(jsonobject, pDeserializer.getMetadataSectionName()));
            } catch (Exception exception) {
                LOGGER.error("Couldn't load {} metadata", pDeserializer.getMetadataSectionName(), exception);
                return null;
            }
        }
    }

    @Override
    public PackLocationInfo m_318586_() {
        return this.f_315234_;
    }

    @Override
    public String toString() {
       return String.format(java.util.Locale.ROOT, "%s: %s", getClass().getName(), this.m_318586_());
    }
}
