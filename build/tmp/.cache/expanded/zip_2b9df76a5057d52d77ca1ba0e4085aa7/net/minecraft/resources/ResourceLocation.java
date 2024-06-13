package net.minecraft.resources;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import java.lang.reflect.Type;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.ResourceLocationException;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.GsonHelper;
import org.apache.commons.lang3.StringUtils;

/**
 * An immutable location of a resource, in terms of a path and namespace.
 * <p>
 * This is used as an identifier for a resource, usually for those housed in a {@link net.minecraft.core.Registry}, such
 * as blocks and items.
 * <p>
 * {@code minecraft} is always taken as the default namespace for a resource location when none is explicitly stated.
 * When using this for registering objects, this namespace <strong>should</strong> only be used for resources added by
 * Minecraft itself.
 * <p>
 * Generally, and by the implementation of {@link #toString()}, the string representation of this class is expressed in
 * the form {@code namespace:path}. The colon is also used as the default separator for parsing strings as a {@code
 * ResourceLocation}.
 * @see net.minecraft.resources.ResourceKey
 */
public class ResourceLocation implements Comparable<ResourceLocation> {
    public static final Codec<ResourceLocation> CODEC = Codec.STRING.comapFlatMap(ResourceLocation::read, ResourceLocation::toString).stable();
    public static final StreamCodec<ByteBuf, ResourceLocation> f_314488_ = ByteBufCodecs.f_315450_.m_323038_(ResourceLocation::new, ResourceLocation::toString);
    public static final SimpleCommandExceptionType ERROR_INVALID = new SimpleCommandExceptionType(Component.translatable("argument.id.invalid"));
    public static final char NAMESPACE_SEPARATOR = ':';
    public static final String DEFAULT_NAMESPACE = "minecraft";
    public static final String REALMS_NAMESPACE = "realms";
    private final String namespace;
    private final String path;

    protected ResourceLocation(String pNamespace, String pPath, @Nullable ResourceLocation.Dummy pDummy) {
        this.namespace = pNamespace;
        this.path = pPath;
    }

    public ResourceLocation(String pNamespace, String pPath) {
        this(assertValidNamespace(pNamespace, pPath), assertValidPath(pNamespace, pPath), null);
    }

    private ResourceLocation(String[] pDecomposedLocation) {
        this(pDecomposedLocation[0], pDecomposedLocation[1]);
    }

    public ResourceLocation(String pLocation) {
        this(decompose(pLocation, ':'));
    }

    public static ResourceLocation of(String pLocation, char pSeparator) {
        return new ResourceLocation(decompose(pLocation, pSeparator));
    }

    @Nullable
    public static ResourceLocation tryParse(String pLocation) {
        try {
            return new ResourceLocation(pLocation);
        } catch (ResourceLocationException resourcelocationexception) {
            return null;
        }
    }

    @Nullable
    public static ResourceLocation tryBuild(String pNamespace, String pPath) {
        try {
            return new ResourceLocation(pNamespace, pPath);
        } catch (ResourceLocationException resourcelocationexception) {
            return null;
        }
    }

    protected static String[] decompose(String pLocation, char pSeparator) {
        String[] astring = new String[]{"minecraft", pLocation};
        int i = pLocation.indexOf(pSeparator);
        if (i >= 0) {
            astring[1] = pLocation.substring(i + 1);
            if (i >= 1) {
                astring[0] = pLocation.substring(0, i);
            }
        }

        return astring;
    }

    public static DataResult<ResourceLocation> read(String p_135838_) {
        try {
            return DataResult.success(new ResourceLocation(p_135838_));
        } catch (ResourceLocationException resourcelocationexception) {
            return DataResult.error(() -> "Not a valid resource location: " + p_135838_ + " " + resourcelocationexception.getMessage());
        }
    }

    public String getPath() {
        return this.path;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public ResourceLocation withPath(String pPath) {
        return new ResourceLocation(this.namespace, assertValidPath(this.namespace, pPath), null);
    }

    public ResourceLocation withPath(UnaryOperator<String> pPathOperator) {
        return this.withPath(pPathOperator.apply(this.path));
    }

    public ResourceLocation withPrefix(String pPathPrefix) {
        return this.withPath(pPathPrefix + this.path);
    }

    public ResourceLocation withSuffix(String pPathSuffix) {
        return this.withPath(this.path + pPathSuffix);
    }

    @Override
    public String toString() {
        return this.namespace + ":" + this.path;
    }

    @Override
    public boolean equals(Object pOther) {
        if (this == pOther) {
            return true;
        } else {
            return !(pOther instanceof ResourceLocation resourcelocation)
                ? false
                : this.namespace.equals(resourcelocation.namespace) && this.path.equals(resourcelocation.path);
        }
    }

    @Override
    public int hashCode() {
        return 31 * this.namespace.hashCode() + this.path.hashCode();
    }

    public int compareTo(ResourceLocation pOther) {
        int i = this.path.compareTo(pOther.path);
        if (i == 0) {
            i = this.namespace.compareTo(pOther.namespace);
        }

        return i;
    }

    /** Normal compare sorts by path first, this compares namespace first. */
    public int compareNamespaced(ResourceLocation o) {
        int ret = this.namespace.compareTo(o.namespace);
        return ret != 0 ? ret : this.path.compareTo(o.path);
    }

    public String toDebugFileName() {
        return this.toString().replace('/', '_').replace(':', '_');
    }

    public String toLanguageKey() {
        return this.namespace + "." + this.path;
    }

    public String toShortLanguageKey() {
        return this.namespace.equals("minecraft") ? this.path : this.toLanguageKey();
    }

    public String toLanguageKey(String pType) {
        return pType + "." + this.toLanguageKey();
    }

    public String toLanguageKey(String pType, String pKey) {
        return pType + "." + this.toLanguageKey() + "." + pKey;
    }

    private static String m_324283_(StringReader p_330450_) {
        int i = p_330450_.getCursor();

        while (p_330450_.canRead() && isAllowedInResourceLocation(p_330450_.peek())) {
            p_330450_.skip();
        }

        return p_330450_.getString().substring(i, p_330450_.getCursor());
    }

    public static ResourceLocation read(StringReader pReader) throws CommandSyntaxException {
        int i = pReader.getCursor();
        String s = m_324283_(pReader);

        try {
            return new ResourceLocation(s);
        } catch (ResourceLocationException resourcelocationexception) {
            pReader.setCursor(i);
            throw ERROR_INVALID.createWithContext(pReader);
        }
    }

    public static ResourceLocation m_320588_(StringReader p_330926_) throws CommandSyntaxException {
        int i = p_330926_.getCursor();
        String s = m_324283_(p_330926_);
        if (s.isEmpty()) {
            throw ERROR_INVALID.createWithContext(p_330926_);
        } else {
            try {
                return new ResourceLocation(s);
            } catch (ResourceLocationException resourcelocationexception) {
                p_330926_.setCursor(i);
                throw ERROR_INVALID.createWithContext(p_330926_);
            }
        }
    }

    public static boolean isAllowedInResourceLocation(char pCharacter) {
        return pCharacter >= '0' && pCharacter <= '9'
            || pCharacter >= 'a' && pCharacter <= 'z'
            || pCharacter == '_'
            || pCharacter == ':'
            || pCharacter == '/'
            || pCharacter == '.'
            || pCharacter == '-';
    }

    public static boolean isValidPath(String pPath) {
        for (int i = 0; i < pPath.length(); i++) {
            if (!validPathChar(pPath.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    public static boolean isValidNamespace(String pNamespace) {
        for (int i = 0; i < pNamespace.length(); i++) {
            if (!validNamespaceChar(pNamespace.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    private static String assertValidNamespace(String pNamespae, String pPath) {
        if (!isValidNamespace(pNamespae)) {
            throw new ResourceLocationException("Non [a-z0-9_.-] character in namespace of location: " + pNamespae + ":" + pPath);
        } else {
            return pNamespae;
        }
    }

    public static boolean validPathChar(char pPathChar) {
        return pPathChar == '_'
            || pPathChar == '-'
            || pPathChar >= 'a' && pPathChar <= 'z'
            || pPathChar >= '0' && pPathChar <= '9'
            || pPathChar == '/'
            || pPathChar == '.';
    }

    public static boolean validNamespaceChar(char pNamespaceChar) {
        return pNamespaceChar == '_' || pNamespaceChar == '-' || pNamespaceChar >= 'a' && pNamespaceChar <= 'z' || pNamespaceChar >= '0' && pNamespaceChar <= '9' || pNamespaceChar == '.';
    }

    public static boolean isValidResourceLocation(String pLocation) {
        String[] astring = decompose(pLocation, ':');
        return isValidNamespace(StringUtils.isEmpty(astring[0]) ? "minecraft" : astring[0]) && isValidPath(astring[1]);
    }

    private static String assertValidPath(String pNamespace, String pPath) {
        if (!isValidPath(pPath)) {
            throw new ResourceLocationException("Non [a-z0-9/._-] character in path of location: " + pNamespace + ":" + pPath);
        } else {
            return pPath;
        }
    }

    protected interface Dummy {
    }

    public static class Serializer implements JsonDeserializer<ResourceLocation>, JsonSerializer<ResourceLocation> {
        public ResourceLocation deserialize(JsonElement pJson, Type pTypeOfT, JsonDeserializationContext pContext) throws JsonParseException {
            return new ResourceLocation(GsonHelper.convertToString(pJson, "location"));
        }

        public JsonElement serialize(ResourceLocation pSrc, Type pTypeOfSrc, JsonSerializationContext pContext) {
            return new JsonPrimitive(pSrc.toString());
        }
    }
}
