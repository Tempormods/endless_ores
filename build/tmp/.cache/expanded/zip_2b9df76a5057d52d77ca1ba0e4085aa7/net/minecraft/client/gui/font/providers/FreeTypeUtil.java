package net.minecraft.client.gui.font.providers;

import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.freetype.FT_Vector;
import org.lwjgl.util.freetype.FreeType;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class FreeTypeUtil {
    private static final Logger f_317036_ = LogUtils.getLogger();
    public static final Object f_314736_ = new Object();
    private static long f_316971_ = 0L;

    public static long m_319232_() {
        synchronized (f_314736_) {
            if (f_316971_ == 0L) {
                try (MemoryStack memorystack = MemoryStack.stackPush()) {
                    PointerBuffer pointerbuffer = memorystack.mallocPointer(1);
                    m_322856_(FreeType.FT_Init_FreeType(pointerbuffer), "Initializing FreeType library");
                    f_316971_ = pointerbuffer.get();
                }
            }

            return f_316971_;
        }
    }

    public static void m_322856_(int p_328560_, String p_336278_) {
        if (p_328560_ != 0) {
            throw new IllegalStateException("FreeType error: " + m_323623_(p_328560_) + " (" + p_336278_ + ")");
        }
    }

    public static boolean m_321328_(int p_333415_, String p_334613_) {
        if (p_333415_ != 0) {
            f_317036_.error("FreeType error: {} ({})", m_323623_(p_333415_), p_334613_);
            return true;
        } else {
            return false;
        }
    }

    private static String m_323623_(int p_328820_) {
        String s = FreeType.FT_Error_String(p_328820_);
        return s != null ? s : "Unrecognized error: 0x" + Integer.toHexString(p_328820_);
    }

    public static FT_Vector m_320720_(FT_Vector p_332923_, float p_329595_, float p_330314_) {
        long i = (long)Math.round(p_329595_ * 64.0F);
        long j = (long)Math.round(p_330314_ * 64.0F);
        return p_332923_.set(i, j);
    }

    public static float m_320022_(FT_Vector p_334185_) {
        return (float)p_334185_.x() / 64.0F;
    }

    public static void m_319347_() {
        synchronized (f_314736_) {
            if (f_316971_ != 0L) {
                FreeType.FT_Done_Library(f_316971_);
                f_316971_ = 0L;
            }
        }
    }
}