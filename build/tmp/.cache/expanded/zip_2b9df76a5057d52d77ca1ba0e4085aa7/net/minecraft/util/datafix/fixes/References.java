package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL.TypeReference;

public class References {
    public static final TypeReference LEVEL = m_323226_("level");
    public static final TypeReference PLAYER = m_323226_("player");
    public static final TypeReference CHUNK = m_323226_("chunk");
    public static final TypeReference HOTBAR = m_323226_("hotbar");
    public static final TypeReference OPTIONS = m_323226_("options");
    public static final TypeReference STRUCTURE = m_323226_("structure");
    public static final TypeReference STATS = m_323226_("stats");
    public static final TypeReference SAVED_DATA_COMMAND_STORAGE = m_323226_("saved_data/command_storage");
    public static final TypeReference SAVED_DATA_FORCED_CHUNKS = m_323226_("saved_data/chunks");
    public static final TypeReference SAVED_DATA_MAP_DATA = m_323226_("saved_data/map_data");
    public static final TypeReference SAVED_DATA_MAP_INDEX = m_323226_("saved_data/idcounts");
    public static final TypeReference SAVED_DATA_RAIDS = m_323226_("saved_data/raids");
    public static final TypeReference SAVED_DATA_RANDOM_SEQUENCES = m_323226_("saved_data/random_sequences");
    public static final TypeReference SAVED_DATA_STRUCTURE_FEATURE_INDICES = m_323226_("saved_data/structure_feature_indices");
    public static final TypeReference SAVED_DATA_SCOREBOARD = m_323226_("saved_data/scoreboard");
    public static final TypeReference ADVANCEMENTS = m_323226_("advancements");
    public static final TypeReference POI_CHUNK = m_323226_("poi_chunk");
    public static final TypeReference ENTITY_CHUNK = m_323226_("entity_chunk");
    public static final TypeReference BLOCK_ENTITY = m_323226_("block_entity");
    public static final TypeReference ITEM_STACK = m_323226_("item_stack");
    public static final TypeReference BLOCK_STATE = m_323226_("block_state");
    public static final TypeReference f_314331_ = m_323226_("flat_block_state");
    public static final TypeReference f_316623_ = m_323226_("data_components");
    public static final TypeReference f_315894_ = m_323226_("villager_trade");
    public static final TypeReference f_315042_ = m_323226_("particle");
    public static final TypeReference ENTITY_NAME = m_323226_("entity_name");
    public static final TypeReference ENTITY_TREE = m_323226_("entity_tree");
    public static final TypeReference ENTITY = m_323226_("entity");
    public static final TypeReference BLOCK_NAME = m_323226_("block_name");
    public static final TypeReference ITEM_NAME = m_323226_("item_name");
    public static final TypeReference GAME_EVENT_NAME = m_323226_("game_event_name");
    public static final TypeReference UNTAGGED_SPAWNER = m_323226_("untagged_spawner");
    public static final TypeReference STRUCTURE_FEATURE = m_323226_("structure_feature");
    public static final TypeReference OBJECTIVE = m_323226_("objective");
    public static final TypeReference TEAM = m_323226_("team");
    public static final TypeReference RECIPE = m_323226_("recipe");
    public static final TypeReference BIOME = m_323226_("biome");
    public static final TypeReference MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST = m_323226_("multi_noise_biome_source_parameter_list");
    public static final TypeReference WORLD_GEN_SETTINGS = m_323226_("world_gen_settings");

    public static TypeReference m_323226_(final String p_334673_) {
        return new TypeReference() {
            @Override
            public String typeName() {
                return p_334673_;
            }

            @Override
            public String toString() {
                return "@" + p_334673_;
            }
        };
    }
}