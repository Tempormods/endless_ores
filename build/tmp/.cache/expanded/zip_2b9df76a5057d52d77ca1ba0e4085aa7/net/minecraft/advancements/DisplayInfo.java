package net.minecraft.advancements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class DisplayInfo {
    public static final Codec<DisplayInfo> f_302437_ = RecordCodecBuilder.create(
        p_309653_ -> p_309653_.group(
                    ItemStack.f_315780_.fieldOf("icon").forGetter(DisplayInfo::getIcon),
                    ComponentSerialization.f_303288_.fieldOf("title").forGetter(DisplayInfo::getTitle),
                    ComponentSerialization.f_303288_.fieldOf("description").forGetter(DisplayInfo::getDescription),
                    ResourceLocation.CODEC.optionalFieldOf("background").forGetter(DisplayInfo::getBackground),
                    AdvancementType.f_303602_.optionalFieldOf("frame", AdvancementType.TASK).forGetter(DisplayInfo::m_306629_),
                    Codec.BOOL.optionalFieldOf("show_toast", Boolean.valueOf(true)).forGetter(DisplayInfo::shouldShowToast),
                    Codec.BOOL.optionalFieldOf("announce_to_chat", Boolean.valueOf(true)).forGetter(DisplayInfo::shouldAnnounceChat),
                    Codec.BOOL.optionalFieldOf("hidden", Boolean.valueOf(false)).forGetter(DisplayInfo::isHidden)
                )
                .apply(p_309653_, DisplayInfo::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, DisplayInfo> f_314089_ = StreamCodec.m_324771_(DisplayInfo::serializeToNetwork, DisplayInfo::fromNetwork);
    private final Component title;
    private final Component description;
    private final ItemStack icon;
    private final Optional<ResourceLocation> background;
    private final AdvancementType f_302420_;
    private final boolean showToast;
    private final boolean announceChat;
    private final boolean hidden;
    private float x;
    private float y;

    public DisplayInfo(
        ItemStack pIcon,
        Component pTitle,
        Component pDescription,
        Optional<ResourceLocation> p_310626_,
        AdvancementType p_309985_,
        boolean pShowToast,
        boolean pAnnounceChat,
        boolean pHidden
    ) {
        this.title = pTitle;
        this.description = pDescription;
        this.icon = pIcon;
        this.background = p_310626_;
        this.f_302420_ = p_309985_;
        this.showToast = pShowToast;
        this.announceChat = pAnnounceChat;
        this.hidden = pHidden;
    }

    public void setLocation(float pX, float pY) {
        this.x = pX;
        this.y = pY;
    }

    public Component getTitle() {
        return this.title;
    }

    public Component getDescription() {
        return this.description;
    }

    public ItemStack getIcon() {
        return this.icon;
    }

    public Optional<ResourceLocation> getBackground() {
        return this.background;
    }

    public AdvancementType m_306629_() {
        return this.f_302420_;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public boolean shouldShowToast() {
        return this.showToast;
    }

    public boolean shouldAnnounceChat() {
        return this.announceChat;
    }

    public boolean isHidden() {
        return this.hidden;
    }

    private void serializeToNetwork(RegistryFriendlyByteBuf p_331479_) {
        ComponentSerialization.f_316335_.m_318638_(p_331479_, this.title);
        ComponentSerialization.f_316335_.m_318638_(p_331479_, this.description);
        ItemStack.f_315801_.m_318638_(p_331479_, this.icon);
        p_331479_.writeEnum(this.f_302420_);
        int i = 0;
        if (this.background.isPresent()) {
            i |= 1;
        }

        if (this.showToast) {
            i |= 2;
        }

        if (this.hidden) {
            i |= 4;
        }

        p_331479_.writeInt(i);
        this.background.ifPresent(p_331479_::writeResourceLocation);
        p_331479_.writeFloat(this.x);
        p_331479_.writeFloat(this.y);
    }

    private static DisplayInfo fromNetwork(RegistryFriendlyByteBuf p_330340_) {
        Component component = ComponentSerialization.f_316335_.m_318688_(p_330340_);
        Component component1 = ComponentSerialization.f_316335_.m_318688_(p_330340_);
        ItemStack itemstack = ItemStack.f_315801_.m_318688_(p_330340_);
        AdvancementType advancementtype = p_330340_.readEnum(AdvancementType.class);
        int i = p_330340_.readInt();
        Optional<ResourceLocation> optional = (i & 1) != 0 ? Optional.of(p_330340_.readResourceLocation()) : Optional.empty();
        boolean flag = (i & 2) != 0;
        boolean flag1 = (i & 4) != 0;
        DisplayInfo displayinfo = new DisplayInfo(itemstack, component, component1, optional, advancementtype, flag, false, flag1);
        displayinfo.setLocation(p_330340_.readFloat(), p_330340_.readFloat());
        return displayinfo;
    }
}