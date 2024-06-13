package net.minecraft.util;

public class FastColor {
    public static int m_321952_(float p_331227_) {
        return Mth.floor(p_331227_ * 255.0F);
    }

    public static class ABGR32 {
        public static int alpha(int pPackedColor) {
            return pPackedColor >>> 24;
        }

        public static int red(int pPackedColor) {
            return pPackedColor & 0xFF;
        }

        public static int green(int pPackedColor) {
            return pPackedColor >> 8 & 0xFF;
        }

        public static int blue(int pPackedColor) {
            return pPackedColor >> 16 & 0xFF;
        }

        public static int transparent(int pPackedColor) {
            return pPackedColor & 16777215;
        }

        public static int opaque(int pPackedColor) {
            return pPackedColor | 0xFF000000;
        }

        public static int color(int pAlpha, int pBlue, int pGreen, int pRed) {
            return pAlpha << 24 | pBlue << 16 | pGreen << 8 | pRed;
        }

        public static int color(int pAlpha, int pPackedColor) {
            return pAlpha << 24 | pPackedColor & 16777215;
        }
    }

    public static class ARGB32 {
        public static int alpha(int pPackedColor) {
            return pPackedColor >>> 24;
        }

        public static int red(int pPackedColor) {
            return pPackedColor >> 16 & 0xFF;
        }

        public static int green(int pPackedColor) {
            return pPackedColor >> 8 & 0xFF;
        }

        public static int blue(int pPackedColor) {
            return pPackedColor & 0xFF;
        }

        public static int color(int pAlpha, int pRed, int pGreen, int pBlue) {
            return pAlpha << 24 | pRed << 16 | pGreen << 8 | pBlue;
        }

        public static int m_322882_(int p_330887_, int p_334659_, int p_333476_) {
            return color(255, p_330887_, p_334659_, p_333476_);
        }

        public static int multiply(int pPackedColourOne, int pPackedColorTwo) {
            return color(
                alpha(pPackedColourOne) * alpha(pPackedColorTwo) / 255,
                red(pPackedColourOne) * red(pPackedColorTwo) / 255,
                green(pPackedColourOne) * green(pPackedColorTwo) / 255,
                blue(pPackedColourOne) * blue(pPackedColorTwo) / 255
            );
        }

        public static int lerp(float pDelta, int pMin, int pMax) {
            int i = Mth.lerpInt(pDelta, alpha(pMin), alpha(pMax));
            int j = Mth.lerpInt(pDelta, red(pMin), red(pMax));
            int k = Mth.lerpInt(pDelta, green(pMin), green(pMax));
            int l = Mth.lerpInt(pDelta, blue(pMin), blue(pMax));
            return color(i, j, k, l);
        }

        public static int m_321570_(int p_330834_) {
            return p_330834_ | 0xFF000000;
        }

        public static int m_320289_(int p_332980_, int p_331547_) {
            return p_332980_ << 24 | p_331547_ & 16777215;
        }

        public static int m_323842_(float p_335433_, float p_328789_, float p_333322_, float p_328684_) {
            return color(FastColor.m_321952_(p_335433_), FastColor.m_321952_(p_328789_), FastColor.m_321952_(p_333322_), FastColor.m_321952_(p_328684_));
        }
    }
}