package net.minecraft.data.info;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;

public class ItemListReport implements DataProvider {
    private final PackOutput f_314265_;
    private final CompletableFuture<HolderLookup.Provider> f_315834_;

    public ItemListReport(PackOutput p_333960_, CompletableFuture<HolderLookup.Provider> p_331732_) {
        this.f_314265_ = p_333960_;
        this.f_315834_ = p_331732_;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput p_328088_) {
        Path path = this.f_314265_.getOutputFolder(PackOutput.Target.REPORTS).resolve("items.json");
        return this.f_315834_.thenCompose(p_334112_ -> {
            JsonObject jsonobject = new JsonObject();
            RegistryOps<JsonElement> registryops = p_334112_.m_318927_(JsonOps.INSTANCE);
            p_334112_.lookupOrThrow(Registries.ITEM).listElements().forEach(p_331822_ -> {
                JsonObject jsonobject1 = new JsonObject();
                JsonArray jsonarray = new JsonArray();
                p_331822_.value().m_320917_().forEach(p_328173_ -> jsonarray.add(m_318784_((TypedDataComponent<?>)p_328173_, registryops)));
                jsonobject1.add("components", jsonarray);
                jsonobject.add(p_331822_.m_323990_(), jsonobject1);
            });
            return DataProvider.saveStable(p_328088_, jsonobject, path);
        });
    }

    private static <T> JsonElement m_318784_(TypedDataComponent<T> p_330714_, DynamicOps<JsonElement> p_328487_) {
        ResourceLocation resourcelocation = BuiltInRegistries.f_315333_.getKey(p_330714_.f_316611_());
        JsonElement jsonelement = p_330714_.m_318908_(p_328487_)
            .getOrThrow(p_329163_ -> new IllegalStateException("Failed to serialize component " + resourcelocation + ": " + p_329163_));
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("type", resourcelocation.toString());
        jsonobject.add("value", jsonelement);
        return jsonobject;
    }

    @Override
    public final String getName() {
        return "Item List";
    }
}