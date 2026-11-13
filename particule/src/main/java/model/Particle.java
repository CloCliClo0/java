package model;

import java.util.Objects;

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

    public double speedSquared() {
        return velocityX * velocityX + velocityY * velocityY;
    }

    public void advance(double seconds) {
        x += velocityX * seconds;
        y += velocityY * seconds;
    }

    public boolean touches(Particle other) {
        double dx = other.x - x;
        double dy = other.y - y;
        double diameter = 2.0 * RADIUS;
        return other != this && dx * dx + dy * dy <= diameter * diameter;
    }

    public void collideWith(Particle other) {
        if (!touches(other)) {
            return;
        }
        double dx = other.x - x;
        double dy = other.y - y;
        double distance = Math.hypot(dx, dy);

        double normalX = distance > 0.0 ? dx / distance : 1.0;
        double normalY = distance > 0.0 ? dy / distance : 0.0;

        double closingSpeed = (velocityX - other.velocityX) * normalX
                + (velocityY - other.velocityY) * normalY;
        if (closingSpeed > 0.0) {
            velocityX -= closingSpeed * normalX;
            velocityY -= closingSpeed * normalY;
            other.velocityX += closingSpeed * normalX;
            other.velocityY += closingSpeed * normalY;
        }

        double push = (2.0 * RADIUS - distance) / 2.0;
        x -= normalX * push;
        y -= normalY * push;
        other.x += normalX * push;
        other.y += normalY * push;
    }

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

    public boolean isInside(double width, double height) {
        return x >= RADIUS && x <= width - RADIUS && y >= RADIUS && y <= height - RADIUS;
    }
}
