package model;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

public final class World {

    private final Sea sea;
    private final Random random;
    private final History history = new History();
    private int turn;
    private boolean paused;

    public World(int rows, int columns, Random random) {
        this(new SeaPopulator(random).populate(new Sea(rows, columns)), random);
    }

    World(Sea sea, Random random) {
        this.sea = Objects.requireNonNull(sea, "sea");
        this.random = Objects.requireNonNull(random, "random");
        history.add(takeCensus());
    }

    public Sea getSea() {
        return sea;
    }

    public History getHistory() {
        return history;
    }

    public int getTurn() {
        return turn;
    }

    public boolean isPaused() {
        return paused;
    }

    public void togglePause() {
        paused = !paused;
    }

    public void nextTurn() {
        if (paused) {
            return;
        }
        List<Sea.Placement> placements = sea.placements();
        Collections.shuffle(placements, random);
        for (Sea.Placement placement : placements) {
            if (placement.animal().isAlive()) {
                placement.animal().act(placement.position(), sea, random);
            }
        }
        turn++;
        history.add(takeCensus());
    }

    private Census takeCensus() {
        List<Sea.Placement> placements = sea.placements();
        Map<Species, Long> populations = placements.stream()
                .collect(Collectors.groupingBy(p -> p.animal().getSpecies(), Collectors.counting()));
        return new Census(
                turn,
                populations.getOrDefault(Species.TUNA, 0L).intValue(),
                populations.getOrDefault(Species.SHARK, 0L).intValue(),
                sea.getCellCount() - placements.size());
    }
}
