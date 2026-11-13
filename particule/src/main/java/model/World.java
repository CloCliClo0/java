package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class World {

    private static final double MAX_SUBSTEP = 0.01;
    private static final double MAX_ELAPSED = 0.1;

    private final double width;
    private final double height;
    private final List<Particle> particles;
    private final List<Body> bodies;
    private boolean paused;

    public World(ParticleFactory factory, int count, double width, double height) {
        this(width, height, Objects.requireNonNull(factory, "factory").createParticles(count, width, height));
    }

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

    public List<Body> getBodies() {
        return bodies;
    }

    public boolean isPaused() {
        return paused;
    }

    public void togglePause() {
        paused = !paused;
    }

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

    public double totalSpeedSquared() {
        return particles.stream().mapToDouble(Particle::speedSquared).sum();
    }

    public boolean allParticlesInside() {
        return particles.stream().allMatch(p -> p.isInside(width, height));
    }

    private void substep(double seconds) {
        particles.forEach(p -> p.advance(seconds));
        resolveCollisions();
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
