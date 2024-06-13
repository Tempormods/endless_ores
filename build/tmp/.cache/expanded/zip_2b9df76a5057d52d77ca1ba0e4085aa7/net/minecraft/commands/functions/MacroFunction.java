package net.minecraft.commands.functions;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.commands.ExecutionCommandSource;
import net.minecraft.commands.FunctionInstantiationException;
import net.minecraft.commands.execution.UnboundEntryAction;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class MacroFunction<T extends ExecutionCommandSource<T>> implements CommandFunction<T> {
    private static final DecimalFormat f_302462_ = Util.make(new DecimalFormat("#"), p_312286_ -> {
        p_312286_.setMaximumFractionDigits(15);
        p_312286_.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.US));
    });
    private static final int f_303588_ = 8;
    private final List<String> f_303264_;
    private final Object2ObjectLinkedOpenHashMap<List<String>, InstantiatedFunction<T>> f_303756_ = new Object2ObjectLinkedOpenHashMap<>(8, 0.25F);
    private final ResourceLocation f_302268_;
    private final List<MacroFunction.Entry<T>> f_303053_;

    public MacroFunction(ResourceLocation p_311437_, List<MacroFunction.Entry<T>> p_310862_, List<String> p_310686_) {
        this.f_302268_ = p_311437_;
        this.f_303053_ = p_310862_;
        this.f_303264_ = p_310686_;
    }

    @Override
    public ResourceLocation m_304900_() {
        return this.f_302268_;
    }

    @Override
    public InstantiatedFunction<T> m_304684_(@Nullable CompoundTag p_309697_, CommandDispatcher<T> p_309980_) throws FunctionInstantiationException {
        if (p_309697_ == null) {
            throw new FunctionInstantiationException(Component.translatable("commands.function.error.missing_arguments", Component.m_305236_(this.m_304900_())));
        } else {
            List<String> list = new ArrayList<>(this.f_303264_.size());

            for (String s : this.f_303264_) {
                Tag tag = p_309697_.get(s);
                if (tag == null) {
                    throw new FunctionInstantiationException(
                        Component.translatable("commands.function.error.missing_argument", Component.m_305236_(this.m_304900_()), s)
                    );
                }

                list.add(m_306275_(tag));
            }

            InstantiatedFunction<T> instantiatedfunction = this.f_303756_.getAndMoveToLast(list);
            if (instantiatedfunction != null) {
                return instantiatedfunction;
            } else {
                if (this.f_303756_.size() >= 8) {
                    this.f_303756_.removeFirst();
                }

                InstantiatedFunction<T> instantiatedfunction1 = this.m_304946_(this.f_303264_, list, p_309980_);
                this.f_303756_.put(list, instantiatedfunction1);
                return instantiatedfunction1;
            }
        }
    }

    private static String m_306275_(Tag p_313061_) {
        if (p_313061_ instanceof FloatTag floattag) {
            return f_302462_.format((double)floattag.getAsFloat());
        } else if (p_313061_ instanceof DoubleTag doubletag) {
            return f_302462_.format(doubletag.getAsDouble());
        } else if (p_313061_ instanceof ByteTag bytetag) {
            return String.valueOf(bytetag.getAsByte());
        } else if (p_313061_ instanceof ShortTag shorttag) {
            return String.valueOf(shorttag.getAsShort());
        } else {
            return p_313061_ instanceof LongTag longtag ? String.valueOf(longtag.getAsLong()) : p_313061_.getAsString();
        }
    }

    private static void m_306611_(List<String> p_313206_, IntList p_310595_, List<String> p_310258_) {
        p_310258_.clear();
        p_310595_.forEach(p_312583_ -> p_310258_.add(p_313206_.get(p_312583_)));
    }

    private InstantiatedFunction<T> m_304946_(List<String> p_312865_, List<String> p_312778_, CommandDispatcher<T> p_311234_) throws FunctionInstantiationException {
        List<UnboundEntryAction<T>> list = new ArrayList<>(this.f_303053_.size());
        List<String> list1 = new ArrayList<>(p_312778_.size());

        for (MacroFunction.Entry<T> entry : this.f_303053_) {
            m_306611_(p_312778_, entry.m_305533_(), list1);
            list.add(entry.m_307174_(list1, p_311234_, this.f_302268_));
        }

        return new PlainTextFunction<>(this.m_304900_().withPath(p_312634_ -> p_312634_ + "/" + p_312865_.hashCode()), list);
    }

    interface Entry<T> {
        IntList m_305533_();

        UnboundEntryAction<T> m_307174_(List<String> p_312452_, CommandDispatcher<T> p_313016_, ResourceLocation p_311242_) throws FunctionInstantiationException;
    }

    static class MacroEntry<T extends ExecutionCommandSource<T>> implements MacroFunction.Entry<T> {
        private final StringTemplate f_302706_;
        private final IntList f_302959_;
        private final T f_314219_;

        public MacroEntry(StringTemplate p_309563_, IntList p_312180_, T p_336169_) {
            this.f_302706_ = p_309563_;
            this.f_302959_ = p_312180_;
            this.f_314219_ = p_336169_;
        }

        @Override
        public IntList m_305533_() {
            return this.f_302959_;
        }

        @Override
        public UnboundEntryAction<T> m_307174_(List<String> p_312101_, CommandDispatcher<T> p_309379_, ResourceLocation p_312655_) throws FunctionInstantiationException {
            String s = this.f_302706_.m_307082_(p_312101_);

            try {
                return CommandFunction.m_305325_(p_309379_, this.f_314219_, new StringReader(s));
            } catch (CommandSyntaxException commandsyntaxexception) {
                throw new FunctionInstantiationException(
                    Component.translatable("commands.function.error.parse", Component.m_305236_(p_312655_), s, commandsyntaxexception.getMessage())
                );
            }
        }
    }

    static class PlainTextEntry<T> implements MacroFunction.Entry<T> {
        private final UnboundEntryAction<T> f_303841_;

        public PlainTextEntry(UnboundEntryAction<T> p_309648_) {
            this.f_303841_ = p_309648_;
        }

        @Override
        public IntList m_305533_() {
            return IntLists.emptyList();
        }

        @Override
        public UnboundEntryAction<T> m_307174_(List<String> p_311533_, CommandDispatcher<T> p_311835_, ResourceLocation p_311102_) {
            return this.f_303841_;
        }
    }
}