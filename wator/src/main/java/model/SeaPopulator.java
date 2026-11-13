package model;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public final class SeaPopulator {

    private static final double TUNA_START_RATIO = 0.30;
    private static final double SHARK_START_RATIO = 0.03;

    private final Random random;

    public SeaPopulator(Random random) {
        this.random = Objects.requireNonNull(random, "random");
    }

    public Sea populate(Sea sea) {
        List<Position> positions = sea.allPositions();
        Collections.shuffle(positions, random);
        int tunas = (int) Math.round(sea.getCellCount() * TUNA_START_RATIO);
        int sharks = (int) Math.round(sea.getCellCount() * SHARK_START_RATIO);
        for (int i = 0; i < tunas; i++) {
            sea.place(new Tuna(), positions.get(i));
        }
        for (int i = tunas; i < tunas + sharks; i++) {
            sea.place(new Shark(), positions.get(i));
        }
        return sea;
    }
}
