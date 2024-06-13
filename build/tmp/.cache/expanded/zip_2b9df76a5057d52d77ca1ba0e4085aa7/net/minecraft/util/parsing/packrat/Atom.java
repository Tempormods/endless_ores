package net.minecraft.util.parsing.packrat;

public record Atom<T>(String f_316440_) {
    @Override
    public String toString() {
        return "<" + this.f_316440_ + ">";
    }

    public static <T> Atom<T> m_320573_(String p_335186_) {
        return new Atom<>(p_335186_);
    }
}