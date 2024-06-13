package net.minecraft.world;

public enum ItemInteractionResult {
    SUCCESS,
    CONSUME,
    CONSUME_PARTIAL,
    PASS_TO_DEFAULT_BLOCK_INTERACTION,
    SKIP_DEFAULT_BLOCK_INTERACTION,
    FAIL;

    public boolean m_321211_() {
        return this.m_321319_().consumesAction();
    }

    public static ItemInteractionResult m_322455_(boolean p_329094_) {
        return p_329094_ ? SUCCESS : CONSUME;
    }

    public InteractionResult m_321319_() {
        return switch (this) {
            case SUCCESS -> InteractionResult.SUCCESS;
            case CONSUME -> InteractionResult.CONSUME;
            case CONSUME_PARTIAL -> InteractionResult.CONSUME_PARTIAL;
            case PASS_TO_DEFAULT_BLOCK_INTERACTION, SKIP_DEFAULT_BLOCK_INTERACTION -> InteractionResult.PASS;
            case FAIL -> InteractionResult.FAIL;
        };
    }
}