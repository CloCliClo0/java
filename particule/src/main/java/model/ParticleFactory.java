package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Creates the starting particles: free spot, random direction and color, fixed speed.
 * The random generator is given from outside, so a draw can be replayed with a seed.
 */
public final class ParticleFactory {

    private static final double INITIAL_SPEED = 150.0;
    private static final int MAX_ATTEMPTS_PER_PARTICLE = 10_000;

    private final Random random;

    public ParticleFactory(Random random) {
        this.random = Objects.requireNonNull(random, "random");
    }

    public List<Particle> createParticles(int count, double width, double height) {
        if (count < 0) {
            throw new IllegalArgumentException("count must be non-negative");
        }
        List<Particle> particles = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            particles.add(createInFreeSpot(particles, width, height));
        }
        return particles;
    }

    private Particle createInFreeSpot(List<Particle> existing, double width, double height) {
        for (int attempt = 0; attempt < MAX_ATTEMPTS_PER_PARTICLE; attempt++) {
            Particle candidate = createRandom(width, height);
            if (candidate.isInside(width, height) && existing.stream().noneMatch(candidate::touches)) {
                return candidate;
            }
        }
        throw new IllegalStateException("no free spot left: the area is too small for " + (existing.size() + 1) + " particles");
    }

    private ParticleColor createRandomColor() {
        return new ParticleColor(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    private Particle createRandom(double width, double height) {
        double x = random.nextDouble() * width;
        double y = random.nextDouble() * height;
        double angle = random.nextDouble() * 2.0 * Math.PI;
        return new Particle(x, y,
                INITIAL_SPEED * Math.cos(angle), INITIAL_SPEED * Math.sin(angle),
                createRandomColor());
    }
}
