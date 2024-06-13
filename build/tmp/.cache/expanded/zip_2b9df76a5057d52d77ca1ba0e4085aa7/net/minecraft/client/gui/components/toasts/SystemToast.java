package net.minecraft.client.gui.components.toasts;

import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SystemToast implements Toast {
    private static final ResourceLocation BACKGROUND_SPRITE = new ResourceLocation("toast/system");
    private static final int MAX_LINE_SIZE = 200;
    private static final int LINE_SPACING = 12;
    private static final int MARGIN = 10;
    private final SystemToast.SystemToastId id;
    private Component title;
    private List<FormattedCharSequence> messageLines;
    private long lastChanged;
    private boolean changed;
    private final int width;
    private boolean f_303681_;

    public SystemToast(SystemToast.SystemToastId pId, Component pTitle, @Nullable Component pMessage) {
        this(
            pId,
            pTitle,
            nullToEmpty(pMessage),
            Math.max(
                160, 30 + Math.max(Minecraft.getInstance().font.width(pTitle), pMessage == null ? 0 : Minecraft.getInstance().font.width(pMessage))
            )
        );
    }

    public static SystemToast multiline(Minecraft pMinecraft, SystemToast.SystemToastId pId, Component pTitle, Component pMessage) {
        Font font = pMinecraft.font;
        List<FormattedCharSequence> list = font.split(pMessage, 200);
        int i = Math.max(200, list.stream().mapToInt(font::width).max().orElse(200));
        return new SystemToast(pId, pTitle, list, i + 30);
    }

    private SystemToast(SystemToast.SystemToastId pId, Component pTitle, List<FormattedCharSequence> pMessageLines, int pWidth) {
        this.id = pId;
        this.title = pTitle;
        this.messageLines = pMessageLines;
        this.width = pWidth;
    }

    private static ImmutableList<FormattedCharSequence> nullToEmpty(@Nullable Component pMessage) {
        return pMessage == null ? ImmutableList.of() : ImmutableList.of(pMessage.getVisualOrderText());
    }

    @Override
    public int width() {
        return this.width;
    }

    @Override
    public int height() {
        return 20 + Math.max(this.messageLines.size(), 1) * 12;
    }

    public void m_305676_() {
        this.f_303681_ = true;
    }

    @Override
    public Toast.Visibility render(GuiGraphics pGuiGraphics, ToastComponent pToastComponent, long pTimeSinceLastVisible) {
        if (this.changed) {
            this.lastChanged = pTimeSinceLastVisible;
            this.changed = false;
        }

        int i = this.width();
        if (i == 160 && this.messageLines.size() <= 1) {
            pGuiGraphics.blitSprite(BACKGROUND_SPRITE, 0, 0, i, this.height());
        } else {
            int j = this.height();
            int k = 28;
            int l = Math.min(4, j - 28);
            this.renderBackgroundRow(pGuiGraphics, i, 0, 0, 28);

            for (int i1 = 28; i1 < j - l; i1 += 10) {
                this.renderBackgroundRow(pGuiGraphics, i, 16, i1, Math.min(16, j - i1 - l));
            }

            this.renderBackgroundRow(pGuiGraphics, i, 32 - l, j - l, l);
        }

        if (this.messageLines.isEmpty()) {
            pGuiGraphics.drawString(pToastComponent.getMinecraft().font, this.title, 18, 12, -256, false);
        } else {
            pGuiGraphics.drawString(pToastComponent.getMinecraft().font, this.title, 18, 7, -256, false);

            for (int j1 = 0; j1 < this.messageLines.size(); j1++) {
                pGuiGraphics.drawString(pToastComponent.getMinecraft().font, this.messageLines.get(j1), 18, 18 + j1 * 12, -1, false);
            }
        }

        double d0 = (double)this.id.displayTime * pToastComponent.getNotificationDisplayTimeMultiplier();
        long k1 = pTimeSinceLastVisible - this.lastChanged;
        return !this.f_303681_ && (double)k1 < d0 ? Toast.Visibility.SHOW : Toast.Visibility.HIDE;
    }

    private void renderBackgroundRow(GuiGraphics pGuiGraphics, int pWidth, int p_282371_, int p_283613_, int p_282880_) {
        int i = p_282371_ == 0 ? 20 : 5;
        int j = Math.min(60, pWidth - i);
        ResourceLocation resourcelocation = BACKGROUND_SPRITE;
        pGuiGraphics.blitSprite(resourcelocation, 160, 32, 0, p_282371_, 0, p_283613_, i, p_282880_);

        for (int k = i; k < pWidth - j; k += 64) {
            pGuiGraphics.blitSprite(resourcelocation, 160, 32, 32, p_282371_, k, p_283613_, Math.min(64, pWidth - k - j), p_282880_);
        }

        pGuiGraphics.blitSprite(resourcelocation, 160, 32, 160 - j, p_282371_, pWidth - j, p_283613_, j, p_282880_);
    }

    public void reset(Component pTitle, @Nullable Component pMessage) {
        this.title = pTitle;
        this.messageLines = nullToEmpty(pMessage);
        this.changed = true;
    }

    public SystemToast.SystemToastId getToken() {
        return this.id;
    }

    public static void add(ToastComponent pToastComponent, SystemToast.SystemToastId pId, Component pTitle, @Nullable Component pMessage) {
        pToastComponent.addToast(new SystemToast(pId, pTitle, pMessage));
    }

    public static void addOrUpdate(ToastComponent pToastComponent, SystemToast.SystemToastId pId, Component pTitle, @Nullable Component pMessage) {
        SystemToast systemtoast = pToastComponent.getToast(SystemToast.class, pId);
        if (systemtoast == null) {
            add(pToastComponent, pId, pTitle, pMessage);
        } else {
            systemtoast.reset(pTitle, pMessage);
        }
    }

    public static void m_305701_(ToastComponent p_311181_, SystemToast.SystemToastId p_311637_) {
        SystemToast systemtoast = p_311181_.getToast(SystemToast.class, p_311637_);
        if (systemtoast != null) {
            systemtoast.m_305676_();
        }
    }

    public static void onWorldAccessFailure(Minecraft pMinecraft, String pMessage) {
        add(pMinecraft.getToasts(), SystemToast.SystemToastId.f_302792_, Component.translatable("selectWorld.access_failure"), Component.literal(pMessage));
    }

    public static void onWorldDeleteFailure(Minecraft pMinecraft, String pMessage) {
        add(pMinecraft.getToasts(), SystemToast.SystemToastId.f_302792_, Component.translatable("selectWorld.delete_failure"), Component.literal(pMessage));
    }

    public static void onPackCopyFailure(Minecraft pMinecraft, String pMessage) {
        add(pMinecraft.getToasts(), SystemToast.SystemToastId.f_302870_, Component.translatable("pack.copyFailure"), Component.literal(pMessage));
    }

    public static void m_321093_(Minecraft p_335579_) {
        addOrUpdate(
            p_335579_.getToasts(),
            SystemToast.SystemToastId.f_315009_,
            Component.translatable("chunk.toast.lowDiskSpace"),
            Component.translatable("chunk.toast.lowDiskSpace.description")
        );
    }

    public static void m_321637_(Minecraft p_335709_, ChunkPos p_330201_) {
        addOrUpdate(
            p_335709_.getToasts(),
            SystemToast.SystemToastId.f_316454_,
            Component.translatable("chunk.toast.loadFailure", p_330201_).withStyle(ChatFormatting.RED),
            Component.translatable("chunk.toast.checkLog")
        );
    }

    public static void m_323567_(Minecraft p_328693_, ChunkPos p_333444_) {
        addOrUpdate(
            p_328693_.getToasts(),
            SystemToast.SystemToastId.f_316449_,
            Component.translatable("chunk.toast.saveFailure", p_333444_).withStyle(ChatFormatting.RED),
            Component.translatable("chunk.toast.checkLog")
        );
    }

    @OnlyIn(Dist.CLIENT)
    public static class SystemToastId {
        public static final SystemToast.SystemToastId f_303336_ = new SystemToast.SystemToastId();
        public static final SystemToast.SystemToastId f_302937_ = new SystemToast.SystemToastId();
        public static final SystemToast.SystemToastId f_302887_ = new SystemToast.SystemToastId();
        public static final SystemToast.SystemToastId f_302792_ = new SystemToast.SystemToastId();
        public static final SystemToast.SystemToastId f_302870_ = new SystemToast.SystemToastId();
        public static final SystemToast.SystemToastId f_302682_ = new SystemToast.SystemToastId();
        public static final SystemToast.SystemToastId f_315009_ = new SystemToast.SystemToastId(10000L);
        public static final SystemToast.SystemToastId f_316454_ = new SystemToast.SystemToastId();
        public static final SystemToast.SystemToastId f_316449_ = new SystemToast.SystemToastId();
        public static final SystemToast.SystemToastId f_302594_ = new SystemToast.SystemToastId(10000L);
        final long displayTime;

        public SystemToastId(long p_311745_) {
            this.displayTime = p_311745_;
        }

        public SystemToastId() {
            this(5000L);
        }
    }
}