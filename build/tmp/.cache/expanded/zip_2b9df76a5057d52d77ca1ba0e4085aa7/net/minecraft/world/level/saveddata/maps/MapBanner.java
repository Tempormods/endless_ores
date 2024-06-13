package net.minecraft.world.level.saveddata.maps;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BannerBlockEntity;

public record MapBanner(BlockPos pos, DyeColor color, Optional<Component> name) {
    public static final Codec<MapBanner> f_316001_ = RecordCodecBuilder.create(
        p_334027_ -> p_334027_.group(
                    BlockPos.CODEC.fieldOf("pos").forGetter(MapBanner::pos),
                    DyeColor.CODEC.lenientOptionalFieldOf("color", DyeColor.WHITE).forGetter(MapBanner::color),
                    ComponentSerialization.f_303675_.lenientOptionalFieldOf("name").forGetter(MapBanner::name)
                )
                .apply(p_334027_, MapBanner::new)
    );
    public static final Codec<List<MapBanner>> f_315914_ = f_316001_.listOf();

    @Nullable
    public static MapBanner fromWorld(BlockGetter pLevel, BlockPos pPos) {
        if (pLevel.getBlockEntity(pPos) instanceof BannerBlockEntity bannerblockentity) {
            DyeColor dyecolor = bannerblockentity.getBaseColor();
            Optional<Component> optional = Optional.ofNullable(bannerblockentity.getCustomName());
            return new MapBanner(pPos, dyecolor, optional);
        } else {
            return null;
        }
    }

    public Holder<MapDecorationType> getDecoration() {
        return switch (this.color) {
            case WHITE -> MapDecorationTypes.f_315046_;
            case ORANGE -> MapDecorationTypes.f_316187_;
            case MAGENTA -> MapDecorationTypes.f_314327_;
            case LIGHT_BLUE -> MapDecorationTypes.f_314597_;
            case YELLOW -> MapDecorationTypes.f_314679_;
            case LIME -> MapDecorationTypes.f_314477_;
            case PINK -> MapDecorationTypes.f_316207_;
            case GRAY -> MapDecorationTypes.f_315054_;
            case LIGHT_GRAY -> MapDecorationTypes.f_315781_;
            case CYAN -> MapDecorationTypes.f_315526_;
            case PURPLE -> MapDecorationTypes.f_316950_;
            case BLUE -> MapDecorationTypes.f_315143_;
            case BROWN -> MapDecorationTypes.f_314021_;
            case GREEN -> MapDecorationTypes.f_315589_;
            case RED -> MapDecorationTypes.f_314287_;
            case BLACK -> MapDecorationTypes.f_316506_;
        };
    }

    public String getId() {
        return "banner-" + this.pos.getX() + "," + this.pos.getY() + "," + this.pos.getZ();
    }
}