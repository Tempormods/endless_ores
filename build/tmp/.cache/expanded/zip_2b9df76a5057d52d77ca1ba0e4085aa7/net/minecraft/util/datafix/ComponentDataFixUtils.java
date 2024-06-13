package net.minecraft.util.datafix;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Optional;
import net.minecraft.util.GsonHelper;

public class ComponentDataFixUtils {
    private static final String f_302503_ = m_304899_("");

    public static <T> Dynamic<T> m_307115_(DynamicOps<T> p_312596_, String p_312893_) {
        String s = m_304899_(p_312893_);
        return new Dynamic<>(p_312596_, p_312596_.createString(s));
    }

    public static <T> Dynamic<T> m_307375_(DynamicOps<T> p_310010_) {
        return new Dynamic<>(p_310010_, p_310010_.createString(f_302503_));
    }

    private static String m_304899_(String p_309616_) {
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("text", p_309616_);
        return GsonHelper.toStableString(jsonobject);
    }

    public static <T> Dynamic<T> m_306987_(DynamicOps<T> p_310384_, String p_313033_) {
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("translate", p_313033_);
        return new Dynamic<>(p_310384_, p_310384_.createString(GsonHelper.toStableString(jsonobject)));
    }

    public static <T> Dynamic<T> m_304636_(Dynamic<T> p_309728_) {
        return DataFixUtils.orElse(p_309728_.asString().map(p_312090_ -> m_307115_(p_309728_.getOps(), p_312090_)).result(), p_309728_);
    }

    public static Dynamic<?> m_324174_(Dynamic<?> p_328370_) {
        Optional<String> optional = p_328370_.asString().result();
        if (optional.isEmpty()) {
            return p_328370_;
        } else {
            String s = optional.get();
            if (!s.isEmpty() && !s.equals("null")) {
                char c0 = s.charAt(0);
                char c1 = s.charAt(s.length() - 1);
                if (c0 == '"' && c1 == '"' || c0 == '{' && c1 == '}' || c0 == '[' && c1 == ']') {
                    try {
                        JsonElement jsonelement = JsonParser.parseString(s);
                        if (jsonelement.isJsonPrimitive()) {
                            return m_307115_(p_328370_.getOps(), jsonelement.getAsString());
                        }

                        return p_328370_.createString(GsonHelper.toStableString(jsonelement));
                    } catch (JsonParseException jsonparseexception) {
                    }
                }

                return m_307115_(p_328370_.getOps(), s);
            } else {
                return m_307375_(p_328370_.getOps());
            }
        }
    }

    public static Optional<String> m_321143_(String p_331580_) {
        try {
            JsonElement jsonelement = JsonParser.parseString(p_331580_);
            if (jsonelement.isJsonObject()) {
                JsonObject jsonobject = jsonelement.getAsJsonObject();
                JsonElement jsonelement1 = jsonobject.get("translate");
                if (jsonelement1 != null && jsonelement1.isJsonPrimitive()) {
                    return Optional.of(jsonelement1.getAsString());
                }
            }
        } catch (JsonParseException jsonparseexception) {
        }

        return Optional.empty();
    }
}