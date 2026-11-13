package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * The world: a rectangle, its particles, and the pause state.
 * It depends on no window: it can run in a plain test.
 */
public final class World {

    /** A longer step is split up, so that a particle never goes through another one. */
    private static final double MAX_SUBSTEP = 0.01;
    /** After a long stall (sleep, frozen window), never simulate more than this at once. */
    private static final double MAX_ELAPSED = 0.1;

    private final double width;
    private final double height;
    private final List<Particle> particles;
    private final List<Body> bodies;
    private boolean paused;

    /** Creates a world with {@code count} particles placed at random by the factory. */
    public World(ParticleFactory factory, int count, double width, double height) {
        this(width, height, Objects.requireNonNull(factory, "factory").createParticles(count, width, height));
    }

    /** Creates a world with ready-made particles (useful for tests). */
    public World(double width, double height, List<Particle> particles) {
        if (!(width > 0.0 && height > 0.0) || Double.isInfinite(width) || Double.isInfinite(height)) {
            throw new IllegalArgumentException("width and height must be positive and finite");
        }
        this.width = width;
        this.height = height;
        this.particles = new ArrayList<>(Objects.requireNonNull(particles, "particles"));
        this.bodies = Collections.unmodifiableList(this.particles);
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    /** Read-only view of the particles, for display. Their number never changes. */
    public List<Body> getBodies() {
        return bodies;
    }

    public boolean isPaused() {
        return paused;
    }

    public void togglePause() {
        paused = !paused;
    }

    /** Advances the world by {@code elapsedSeconds} seconds; does nothing while paused. */
    public void step(double elapsedSeconds) {
        if (!Double.isFinite(elapsedSeconds) || elapsedSeconds < 0.0) {
            throw new IllegalArgumentException("elapsedSeconds must be finite and non-negative");
        }
        if (paused) {
            return;
        }
        double total = Math.min(elapsedSeconds, MAX_ELAPSED);
        int substeps = (int) Math.ceil(total / MAX_SUBSTEP);
        for (int i = 0; i < substeps; i++) {
            substep(total / substeps);
        }
    }

    /** Sum of the squared speeds: it must stay constant over time. */
    public double totalSpeedSquared() {
        return particles.stream().mapToDouble(Particle::speedSquared).sum();
    }

    public boolean allParticlesInside() {
        return particles.stream().allMatch(p -> p.isInside(width, height));
    }

    private void substep(double seconds) {
        particles.forEach(p -> p.advance(seconds));
        resolveCollisions();
        // Walls come last: at the end of a step, no particle sticks out of the screen.
        particles.forEach(p -> p.bounceOnWalls(width, height));
    }

    private void resolveCollisions() {
        for (int i = 0; i < particles.size(); i++) {
            for (int j = i + 1; j < particles.size(); j++) {
                particles.get(i).collideWith(particles.get(j));
            }
        }
    }
}
