package net.minecraft.commands.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.List;

public record StringTemplate(List<String> f_303498_, List<String> f_302286_) {
    public static StringTemplate m_307164_(String p_311822_, int p_311237_) {
        Builder<String> builder = ImmutableList.builder();
        Builder<String> builder1 = ImmutableList.builder();
        int i = p_311822_.length();
        int j = 0;
        int k = p_311822_.indexOf(36);

        while (k != -1) {
            if (k != i - 1 && p_311822_.charAt(k + 1) == '(') {
                builder.add(p_311822_.substring(j, k));
                int l = p_311822_.indexOf(41, k + 1);
                if (l == -1) {
                    throw new IllegalArgumentException("Unterminated macro variable in macro '" + p_311822_ + "' on line " + p_311237_);
                }

                String s = p_311822_.substring(k + 2, l);
                if (!m_306157_(s)) {
                    throw new IllegalArgumentException("Invalid macro variable name '" + s + "' on line " + p_311237_);
                }

                builder1.add(s);
                j = l + 1;
                k = p_311822_.indexOf(36, j);
            } else {
                k = p_311822_.indexOf(36, k + 1);
            }
        }

        if (j == 0) {
            throw new IllegalArgumentException("Macro without variables on line " + p_311237_);
        } else {
            if (j != i) {
                builder.add(p_311822_.substring(j));
            }

            return new StringTemplate(builder.build(), builder1.build());
        }
    }

    private static boolean m_306157_(String p_312174_) {
        for (int i = 0; i < p_312174_.length(); i++) {
            char c0 = p_312174_.charAt(i);
            if (!Character.isLetterOrDigit(c0) && c0 != '_') {
                return false;
            }
        }

        return true;
    }

    public String m_307082_(List<String> p_310551_) {
        StringBuilder stringbuilder = new StringBuilder();

        for (int i = 0; i < this.f_302286_.size(); i++) {
            stringbuilder.append(this.f_303498_.get(i)).append(p_310551_.get(i));
            CommandFunction.m_319153_(stringbuilder);
        }

        if (this.f_303498_.size() > this.f_302286_.size()) {
            stringbuilder.append(this.f_303498_.get(this.f_303498_.size() - 1));
        }

        CommandFunction.m_319153_(stringbuilder);
        return stringbuilder.toString();
    }
}