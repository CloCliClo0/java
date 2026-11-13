package model;

public enum Species {
    TUNA(60, 200, 90),
    SHARK(225, 55, 55);

    private final int red;
    private final int green;
    private final int blue;

    Species(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
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
}
