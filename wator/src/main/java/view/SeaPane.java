package view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import model.Animal;
import model.Position;
import model.Sea;

final class SeaPane extends Canvas {

    private static final double CELL_SIZE = 10;
    private static final Color WATER = Color.rgb(10, 36, 99);

    private final Sea sea;

    SeaPane(Sea sea) {
        super(sea.getColumns() * CELL_SIZE, sea.getRows() * CELL_SIZE);
        this.sea = sea;
    }

    static int cellsIn(double pixels) {
        return (int) (pixels / CELL_SIZE);
    }

    void refresh() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.setFill(WATER);
        gc.fillRect(0, 0, getWidth(), getHeight());
        for (int row = 0; row < sea.getRows(); row++) {
            for (int column = 0; column < sea.getColumns(); column++) {
                Animal animal = sea.animalAt(new Position(row, column));
                if (animal != null) {
                    gc.setFill(Colors.of(animal.getSpecies()));
                    gc.fillRect(column * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }
            }
        }
    }
}
