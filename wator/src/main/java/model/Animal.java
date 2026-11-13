package model;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public abstract class Animal {

    private int turnsSinceReproduction;
    private boolean alive = true;

    final void act(Position from, Sea sea, Random random) {
        startTurn();
        Position to = chooseDestination(from, sea, random);   // equals 'from' when it stays
        boolean moved = !to.equals(from);
        if (moved) {
            moveTo(from, to, sea);
        }
        turnsSinceReproduction++;
        if (isStarving()) {
            die();
            sea.remove(moved ? to : from);
            return;
        }
        if (moved && turnsSinceReproduction >= reproductionPeriod()) {
            sea.place(createOffspring(), from);               // into the cell it just left
            turnsSinceReproduction = 0;
        }
    }

    public boolean isAlive() { return alive; }
    void die() { alive = false; }
    public boolean isPrey() { return false; }
    public abstract Species getSpecies();

    protected abstract Position chooseDestination(Position from, Sea sea, Random random);
    protected abstract int reproductionPeriod();
    protected abstract Animal createOffspring();

    protected void startTurn() { }
    protected boolean isStarving() { return false; }
    protected void moveTo(Position from, Position to, Sea sea) { sea.move(from, to); }

    protected static Optional<Position> pickRandom(List<Position> positions, Random random) {
        return positions.isEmpty()
                ? Optional.empty()
                : Optional.of(positions.get(random.nextInt(positions.size())));
    }
}
