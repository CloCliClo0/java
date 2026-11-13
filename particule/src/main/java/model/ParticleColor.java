package model;

public final class ParticleColor {
    private final int red;
    private final int green;
    private final int blue;

    public ParticleColor(int red, int green, int blue) {
        this.red = requireChannel(red, "red");
        this.green = requireChannel(green, "green");
        this.blue = requireChannel(blue, "blue");
    }

    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }

    private static int requireChannel(int value, String name) {
        if (value < 0 || value > 255) {
            throw new IllegalArgumentException(name + " must be between 0 and 255");
        }
        return value;
    }
}
