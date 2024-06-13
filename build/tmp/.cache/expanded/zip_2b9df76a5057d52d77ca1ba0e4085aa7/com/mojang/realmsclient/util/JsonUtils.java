package com.mojang.realmsclient.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.util.UndashedUuid;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class JsonUtils {
    public static <T> T getRequired(String pKey, JsonObject pJson, Function<JsonObject, T> pOutput) {
        JsonElement jsonelement = pJson.get(pKey);
        if (jsonelement == null || jsonelement.isJsonNull()) {
            throw new IllegalStateException("Missing required property: " + pKey);
        } else if (!jsonelement.isJsonObject()) {
            throw new IllegalStateException("Required property " + pKey + " was not a JsonObject as espected");
        } else {
            return pOutput.apply(jsonelement.getAsJsonObject());
        }
    }

    @Nullable
    public static <T> T m_304933_(String p_309589_, JsonObject p_310739_, Function<JsonObject, T> p_310530_) {
        JsonElement jsonelement = p_310739_.get(p_309589_);
        if (jsonelement == null || jsonelement.isJsonNull()) {
            return null;
        } else if (!jsonelement.isJsonObject()) {
            throw new IllegalStateException("Required property " + p_309589_ + " was not a JsonObject as espected");
        } else {
            return p_310530_.apply(jsonelement.getAsJsonObject());
        }
    }

    public static String getRequiredString(String pKey, JsonObject pJson) {
        String s = getStringOr(pKey, pJson, null);
        if (s == null) {
            throw new IllegalStateException("Missing required property: " + pKey);
        } else {
            return s;
        }
    }

    public static String m_305973_(String p_309497_, JsonObject p_310406_, String p_312706_) {
        JsonElement jsonelement = p_310406_.get(p_309497_);
        if (jsonelement != null) {
            return jsonelement.isJsonNull() ? p_312706_ : jsonelement.getAsString();
        } else {
            return p_312706_;
        }
    }

    @Nullable
    public static String getStringOr(String pKey, JsonObject pJson, @Nullable String pDefaultValue) {
        JsonElement jsonelement = pJson.get(pKey);
        if (jsonelement != null) {
            return jsonelement.isJsonNull() ? pDefaultValue : jsonelement.getAsString();
        } else {
            return pDefaultValue;
        }
    }

    @Nullable
    public static UUID getUuidOr(String pKey, JsonObject pJson, @Nullable UUID pDefaultValue) {
        String s = getStringOr(pKey, pJson, null);
        return s == null ? pDefaultValue : UndashedUuid.fromStringLenient(s);
    }

    public static int getIntOr(String pKey, JsonObject pJson, int pDefaultValue) {
        JsonElement jsonelement = pJson.get(pKey);
        if (jsonelement != null) {
            return jsonelement.isJsonNull() ? pDefaultValue : jsonelement.getAsInt();
        } else {
            return pDefaultValue;
        }
    }

    public static long getLongOr(String pKey, JsonObject pJson, long pDefaultValue) {
        JsonElement jsonelement = pJson.get(pKey);
        if (jsonelement != null) {
            return jsonelement.isJsonNull() ? pDefaultValue : jsonelement.getAsLong();
        } else {
            return pDefaultValue;
        }
    }

    public static boolean getBooleanOr(String pKey, JsonObject pJson, boolean pDefaultValue) {
        JsonElement jsonelement = pJson.get(pKey);
        if (jsonelement != null) {
            return jsonelement.isJsonNull() ? pDefaultValue : jsonelement.getAsBoolean();
        } else {
            return pDefaultValue;
        }
    }

    public static Date getDateOr(String pKey, JsonObject pJson) {
        JsonElement jsonelement = pJson.get(pKey);
        return jsonelement != null ? new Date(Long.parseLong(jsonelement.getAsString())) : new Date();
    }
}