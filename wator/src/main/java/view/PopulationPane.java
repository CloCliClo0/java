package view;

import java.util.List;
import java.util.function.ToIntFunction;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import model.Census;
import model.History;
import model.Species;

final class PopulationPane extends Canvas {

    private static final Color BACKGROUND = Color.rgb(12, 14, 20);
    private static final Color LABEL = Color.rgb(150, 156, 170);
    private static final double LINE_WIDTH = 2;

    PopulationPane(double width, double height) {
        super(width, height);
    }

    void refresh(History history) {
        GraphicsContext gc = getGraphicsContext2D();
        gc.setFill(BACKGROUND);
        gc.fillRect(0, 0, getWidth(), getHeight());

        List<Census> censuses = history.getCensuses();
        if (censuses.size() < 2) {
            return;
        }
        int max = Math.max(1, censuses.stream().mapToInt(c -> Math.max(c.tunas(), c.sharks())).max().orElse(1));
        drawCurve(gc, censuses, Census::tunas, max, Colors.of(Species.TUNA));
        drawCurve(gc, censuses, Census::sharks, max, Colors.of(Species.SHARK));

        gc.setFill(LABEL);
        gc.fillText(String.valueOf(max), 4, 12);
    }

    private void drawCurve(GraphicsContext gc, List<Census> censuses, ToIntFunction<Census> value, int max, Color color) {
        int count = censuses.size();
        double[] xs = new double[count];
        double[] ys = new double[count];
        for (int i = 0; i < count; i++) {
            xs[i] = i * getWidth() / (count - 1);
            ys[i] = getHeight() - value.applyAsInt(censuses.get(i)) * getHeight() / max;
        }
        gc.setStroke(color);
        gc.setLineWidth(LINE_WIDTH);
        gc.strokePolyline(xs, ys, count);
    }
}
