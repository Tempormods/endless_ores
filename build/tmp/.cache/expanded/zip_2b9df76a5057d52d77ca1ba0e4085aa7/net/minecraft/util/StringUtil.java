package net.minecraft.util;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

public class StringUtil {
    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)\\u00A7[0-9A-FK-OR]");
    private static final Pattern LINE_PATTERN = Pattern.compile("\\r\\n|\\v");
    private static final Pattern LINE_END_PATTERN = Pattern.compile("(?:\\r\\n|\\v)$");

    public static String formatTickDuration(int pTicks, float p_313197_) {
        int i = Mth.floor((float)pTicks / p_313197_);
        int j = i / 60;
        i %= 60;
        int k = j / 60;
        j %= 60;
        return k > 0 ? String.format(Locale.ROOT, "%02d:%02d:%02d", k, j, i) : String.format(Locale.ROOT, "%02d:%02d", j, i);
    }

    public static String stripColor(String pText) {
        return STRIP_COLOR_PATTERN.matcher(pText).replaceAll("");
    }

    public static boolean isNullOrEmpty(@Nullable String pString) {
        return StringUtils.isEmpty(pString);
    }

    public static String truncateStringIfNecessary(String pString, int pMaxSize, boolean pAddEllipsis) {
        if (pString.length() <= pMaxSize) {
            return pString;
        } else {
            return pAddEllipsis && pMaxSize > 3 ? pString.substring(0, pMaxSize - 3) + "..." : pString.substring(0, pMaxSize);
        }
    }

    public static int lineCount(String pString) {
        if (pString.isEmpty()) {
            return 0;
        } else {
            Matcher matcher = LINE_PATTERN.matcher(pString);
            int i = 1;

            while (matcher.find()) {
                i++;
            }

            return i;
        }
    }

    public static boolean endsWithNewLine(String pString) {
        return LINE_END_PATTERN.matcher(pString).find();
    }

    public static String trimChatMessage(String pString) {
        return truncateStringIfNecessary(pString, 256, false);
    }

    public static boolean m_324425_(char p_336025_) {
        return p_336025_ != 167 && p_336025_ >= ' ' && p_336025_ != 127;
    }

    public static boolean m_319148_(String p_328576_) {
        return p_328576_.length() > 16 ? false : p_328576_.chars().filter(p_333267_ -> p_333267_ <= 32 || p_333267_ >= 127).findAny().isEmpty();
    }

    public static String m_319203_(String p_329405_) {
        return m_323063_(p_329405_, false);
    }

    public static String m_323063_(String p_335196_, boolean p_329791_) {
        StringBuilder stringbuilder = new StringBuilder();

        for (char c0 : p_335196_.toCharArray()) {
            if (m_324425_(c0)) {
                stringbuilder.append(c0);
            } else if (p_329791_ && c0 == '\n') {
                stringbuilder.append(c0);
            }
        }

        return stringbuilder.toString();
    }

    public static boolean m_321934_(int p_332672_) {
        return Character.isWhitespace(p_332672_) || Character.isSpaceChar(p_332672_);
    }

    public static boolean m_320314_(@Nullable String p_334499_) {
        return p_334499_ != null && p_334499_.length() != 0 ? p_334499_.chars().allMatch(StringUtil::m_321934_) : true;
    }
}