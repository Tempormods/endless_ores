package com.mojang.blaze3d.platform;

import ca.weblite.objc.Client;
import ca.weblite.objc.NSObject;
import com.sun.jna.Pointer;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Optional;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFWNativeCocoa;

@OnlyIn(Dist.CLIENT)
public class MacosUtil {
    private static final int f_303366_ = 8;
    private static final int NS_FULL_SCREEN_WINDOW_MASK = 16384;

    public static void toggleFullscreen(long pWindowId) {
        getNsWindow(pWindowId).filter(MacosUtil::m_304687_).ifPresent(MacosUtil::toggleFullscreen);
    }

    public static void m_305469_(long p_312472_) {
        getNsWindow(p_312472_).ifPresent(p_312903_ -> {
            long i = m_306498_(p_312903_);
            p_312903_.send("setStyleMask:", new Object[]{i & -9L});
        });
    }

    private static Optional<NSObject> getNsWindow(long pWindowId) {
        long i = GLFWNativeCocoa.glfwGetCocoaWindow(pWindowId);
        return i != 0L ? Optional.of(new NSObject(new Pointer(i))) : Optional.empty();
    }

    private static boolean m_304687_(NSObject p_311944_) {
        return (m_306498_(p_311944_) & 16384L) != 0L;
    }

    private static long m_306498_(NSObject p_309879_) {
        return (Long)p_309879_.sendRaw("styleMask", new Object[0]);
    }

    private static void toggleFullscreen(NSObject p_182524_) {
        p_182524_.send("toggleFullScreen:", new Object[]{Pointer.NULL});
    }

    public static void loadIcon(IoSupplier<InputStream> pIconStreamSupplier) throws IOException {
        try (InputStream inputstream = pIconStreamSupplier.get()) {
            String s = Base64.getEncoder().encodeToString(inputstream.readAllBytes());
            Client client = Client.getInstance();
            Object object = client.sendProxy("NSData", "alloc").send("initWithBase64Encoding:", s);
            Object object1 = client.sendProxy("NSImage", "alloc").send("initWithData:", object);
            client.sendProxy("NSApplication", "sharedApplication").send("setApplicationIconImage:", object1);
        }
    }
}