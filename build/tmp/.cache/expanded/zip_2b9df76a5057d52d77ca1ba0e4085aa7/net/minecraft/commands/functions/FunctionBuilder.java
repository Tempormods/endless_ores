package net.minecraft.commands.functions;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.commands.ExecutionCommandSource;
import net.minecraft.commands.execution.UnboundEntryAction;
import net.minecraft.resources.ResourceLocation;

class FunctionBuilder<T extends ExecutionCommandSource<T>> {
    @Nullable
    private List<UnboundEntryAction<T>> f_302654_ = new ArrayList<>();
    @Nullable
    private List<MacroFunction.Entry<T>> f_302447_;
    private final List<String> f_302518_ = new ArrayList<>();

    public void m_305257_(UnboundEntryAction<T> p_309592_) {
        if (this.f_302447_ != null) {
            this.f_302447_.add(new MacroFunction.PlainTextEntry<>(p_309592_));
        } else {
            this.f_302654_.add(p_309592_);
        }
    }

    private int m_305772_(String p_312711_) {
        int i = this.f_302518_.indexOf(p_312711_);
        if (i == -1) {
            i = this.f_302518_.size();
            this.f_302518_.add(p_312711_);
        }

        return i;
    }

    private IntList m_307723_(List<String> p_311467_) {
        IntArrayList intarraylist = new IntArrayList(p_311467_.size());

        for (String s : p_311467_) {
            intarraylist.add(this.m_305772_(s));
        }

        return intarraylist;
    }

    public void m_307020_(String p_312905_, int p_310777_, T p_328106_) {
        StringTemplate stringtemplate = StringTemplate.m_307164_(p_312905_, p_310777_);
        if (this.f_302654_ != null) {
            this.f_302447_ = new ArrayList<>(this.f_302654_.size() + 1);

            for (UnboundEntryAction<T> unboundentryaction : this.f_302654_) {
                this.f_302447_.add(new MacroFunction.PlainTextEntry<>(unboundentryaction));
            }

            this.f_302654_ = null;
        }

        this.f_302447_.add(new MacroFunction.MacroEntry<>(stringtemplate, this.m_307723_(stringtemplate.f_302286_()), p_328106_));
    }

    public CommandFunction<T> m_306609_(ResourceLocation p_311383_) {
        return (CommandFunction<T>)(this.f_302447_ != null
            ? new MacroFunction<>(p_311383_, this.f_302447_, this.f_302518_)
            : new PlainTextFunction<>(p_311383_, this.f_302654_));
    }
}