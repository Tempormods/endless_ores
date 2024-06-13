package net.minecraft.world.phys.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleList;

public class IdenticalMerger implements IndexMerger {
    private final DoubleList coords;

    public IdenticalMerger(DoubleList pCoords) {
        this.coords = pCoords;
    }

    @Override
    public boolean forMergedIndexes(IndexMerger.IndexConsumer pConsumer) {
        int i = this.coords.size() - 1;

        for (int j = 0; j < i; j++) {
            if (!pConsumer.merge(j, j, j)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int size() {
        return this.coords.size();
    }

    @Override
    public DoubleList getList() {
        return this.coords;
    }
}