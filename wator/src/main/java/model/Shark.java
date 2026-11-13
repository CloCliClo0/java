package model;

import java.util.List;
import java.util.Random;

public final class Shark extends Animal {
    private static final int REPRODUCTION_PERIOD = 10;
    private static final int EAT_LIMIT = 3;

    private int turnsWithoutEating;

    @Override protected int reproductionPeriod() { 
        return REPRODUCTION_PERIOD; 
    }

    @Override protected Animal createOffspring() {
        return new Shark(); 
    }

    @Override public Species getSpecies() { 
        return Species.SHARK; 
    }

    @Override
    protected Position chooseDestination(Position from, Sea sea, Random random) {
        List<Position> prey = sea.occupiedNeighbors(from).stream()
                .filter(p -> sea.animalAt(p).isPrey()).toList();
        return pickRandom(prey, random)
                .or(() -> pickRandom(sea.emptyNeighbors(from), random))
                .orElse(from);
    }

    @Override
    protected void moveTo(Position from, Position to, Sea sea) {
        if (!sea.isEmpty(to)) {            // only prey can be there
            sea.remove(to).die();          // the eaten tuna will not act this turn
            turnsWithoutEating = 0;
        }
        super.moveTo(from, to, sea);
    }

    @Override protected void startTurn() { 
        turnsWithoutEating++; 
    }

    @Override protected boolean isStarving() { 
        return turnsWithoutEating >= EAT_LIMIT; 
    }
}