package model;

import java.util.Random;

public final class Tuna extends Animal {
    private static final int REPRODUCTION_PERIOD = 3;

    @Override protected int reproductionPeriod() {
        return REPRODUCTION_PERIOD;
    }

    @Override protected Animal createOffspring() {
        return new Tuna();
    }

    @Override public Species getSpecies() {
        return Species.TUNA;
    }

    @Override public boolean isPrey() {
        return true;
    }

    @Override
    protected Position chooseDestination(Position from, Sea sea, Random random) {
        return pickRandom(sea.emptyNeighbors(from), random).orElse(from);
    }
}
