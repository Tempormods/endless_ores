package net.minecraft.client.gui.screens;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NoticeWithLinkScreen extends Screen {
    private static final Component SYMLINK_WORLD_TITLE = Component.translatable("symlink_warning.title.world").withStyle(ChatFormatting.BOLD);
    private static final Component SYMLINK_WORLD_MESSAGE_TEXT = Component.translatable("symlink_warning.message.world", "https://aka.ms/MinecraftSymLinks");
    private static final Component SYMLINK_PACK_TITLE = Component.translatable("symlink_warning.title.pack").withStyle(ChatFormatting.BOLD);
    private static final Component SYMLINK_PACK_MESSAGE_TEXT = Component.translatable("symlink_warning.message.pack", "https://aka.ms/MinecraftSymLinks");
    private final Component message;
    private final String url;
    private final Runnable f_303272_;
    private final GridLayout layout = new GridLayout().rowSpacing(10);

    public NoticeWithLinkScreen(Component pTitle, Component pMessage, String pUrl, Runnable p_311369_) {
        super(pTitle);
        this.message = pMessage;
        this.url = pUrl;
        this.f_303272_ = p_311369_;
    }

    public static Screen createWorldSymlinkWarningScreen(Runnable p_309390_) {
        return new NoticeWithLinkScreen(SYMLINK_WORLD_TITLE, SYMLINK_WORLD_MESSAGE_TEXT, "https://aka.ms/MinecraftSymLinks", p_309390_);
    }

    public static Screen createPackSymlinkWarningScreen(Runnable p_310056_) {
        return new NoticeWithLinkScreen(SYMLINK_PACK_TITLE, SYMLINK_PACK_MESSAGE_TEXT, "https://aka.ms/MinecraftSymLinks", p_310056_);
    }

    @Override
    protected void init() {
        super.init();
        this.layout.defaultCellSetting().alignHorizontallyCenter();
        GridLayout.RowHelper gridlayout$rowhelper = this.layout.createRowHelper(1);
        gridlayout$rowhelper.addChild(new StringWidget(this.title, this.font));
        gridlayout$rowhelper.addChild(new MultiLineTextWidget(this.message, this.font).setMaxWidth(this.width - 50).setCentered(true));
        int i = 120;
        GridLayout gridlayout = new GridLayout().columnSpacing(5);
        GridLayout.RowHelper gridlayout$rowhelper1 = gridlayout.createRowHelper(3);
        gridlayout$rowhelper1.addChild(
            Button.builder(CommonComponents.GUI_OPEN_IN_BROWSER, p_300039_ -> Util.getPlatform().openUri(this.url)).size(120, 20).build()
        );
        gridlayout$rowhelper1.addChild(
            Button.builder(CommonComponents.GUI_COPY_LINK_TO_CLIPBOARD, p_299228_ -> this.minecraft.keyboardHandler.setClipboard(this.url)).size(120, 20).build()
        );
        gridlayout$rowhelper1.addChild(Button.builder(CommonComponents.GUI_BACK, p_300975_ -> this.onClose()).size(120, 20).build());
        gridlayout$rowhelper.addChild(gridlayout);
        this.repositionElements();
        this.layout.visitWidgets(this::addRenderableWidget);
    }

    @Override
    protected void repositionElements() {
        this.layout.arrangeElements();
        FrameLayout.centerInRectangle(this.layout, this.getRectangle());
    }

    @Override
    public Component getNarrationMessage() {
        return CommonComponents.joinForNarration(super.getNarrationMessage(), this.message);
    }

    @Override
    public void onClose() {
        this.f_303272_.run();
    }
}