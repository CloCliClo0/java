package model;

import java.util.Objects;

/**
 * A disc of fixed radius that moves in a straight line and bounces off the walls
 * and off other particles. All particles have the same mass.
 *
 * Bounces only flip or exchange velocity components, so they neither create
 * nor destroy kinetic energy.
 */
public final class Particle implements Body {

    private static final double RADIUS = 8.0;

    private double x;
    private double y;
    private double velocityX;
    private double velocityY;
    private final ParticleColor color;

    public Particle(double x, double y, double velocityX, double velocityY, ParticleColor color) {
        this.x = x;
        this.y = y;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.color = Objects.requireNonNull(color, "color");
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public double getRadius() {
        return RADIUS;
    }

    public double getVelocityX() {
        return velocityX;
    }

    public double getVelocityY() {
        return velocityY;
    }

    @Override
    public ParticleColor getColor() {
        return color;
    }

    /** Squared speed; proportional to the kinetic energy since all masses are equal. */
    public double speedSquared() {
        return velocityX * velocityX + velocityY * velocityY;
    }

    /** Moves the particle in a straight line for {@code seconds} seconds. */
    public void advance(double seconds) {
        x += velocityX * seconds;
        y += velocityY * seconds;
    }

    /** True if the two discs touch or overlap. */
    public boolean touches(Particle other) {
        double dx = other.x - x;
        double dy = other.y - y;
        double diameter = 2.0 * RADIUS;
        return other != this && dx * dx + dy * dy <= diameter * diameter;
    }

    /**
     * If the two particles touch, pushes them apart and, if they were getting closer,
     * bounces their velocities along the line between their centers.
     * With equal masses, an elastic collision simply exchanges the normal components.
     */
    public void collideWith(Particle other) {
        if (!touches(other)) {
            return;
        }
        double dx = other.x - x;
        double dy = other.y - y;
        double distance = Math.hypot(dx, dy);

        // Same center: the direction of the impact is undefined, so pick one.
        double normalX = distance > 0.0 ? dx / distance : 1.0;
        double normalY = distance > 0.0 ? dy / distance : 0.0;

        // Speed at which this particle approaches the other one along the normal.
        double closingSpeed = (velocityX - other.velocityX) * normalX
                + (velocityY - other.velocityY) * normalY;
        if (closingSpeed > 0.0) {
            velocityX -= closingSpeed * normalX;
            velocityY -= closingSpeed * normalY;
            other.velocityX += closingSpeed * normalX;
            other.velocityY += closingSpeed * normalY;
        }

        // Separate the discs so they do not stay stuck together.
        double push = (2.0 * RADIUS - distance) / 2.0;
        x -= normalX * push;
        y -= normalY * push;
        other.x += normalX * push;
        other.y += normalY * push;
    }

    /** Puts the particle back inside [0, width] x [0, height] and turns its velocity inward. */
    public void bounceOnWalls(double width, double height) {
        if (x < RADIUS) {
            x = RADIUS;
            velocityX = Math.abs(velocityX);
        } else if (x > width - RADIUS) {
            x = width - RADIUS;
            velocityX = -Math.abs(velocityX);
        }
        if (y < RADIUS) {
            y = RADIUS;
            velocityY = Math.abs(velocityY);
        } else if (y > height - RADIUS) {
            y = height - RADIUS;
            velocityY = -Math.abs(velocityY);
        }
    }

    /** True if the whole disc is inside [0, width] x [0, height]. */
    public boolean isInside(double width, double height) {
        return x >= RADIUS && x <= width - RADIUS && y >= RADIUS && y <= height - RADIUS;
    }
}
