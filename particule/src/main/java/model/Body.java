package model;

/**
 * What the view is allowed to know about a particle: where it is, its size, its color.
 * No method allows modifying it.
 */
public interface Body {

    double getX();

    double getY();

    double getRadius();

    ParticleColor getColor();
}
