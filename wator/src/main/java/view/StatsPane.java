package view;

import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import model.Census;
import model.Species;
import model.World;

final class StatsPane extends VBox {

    private static final double PADDING = 20;
    private static final double SPACING = 12;
    private static final double CHART_HEIGHT = 220;
    private static final Color BACKGROUND = Color.rgb(0, 0, 0);
    private static final Color TEXT = Color.rgb(220, 224, 232);

    private final Text turn = label(TEXT);
    private final Text tunas = label(Colors.of(Species.TUNA));
    private final Text sharks = label(Colors.of(Species.SHARK));
    private final Text empty = label(TEXT);
    private final Text status = label(TEXT);
    private final PopulationPane chart;

    StatsPane(double width) {
        super(SPACING);
        setMinWidth(width);
        setPrefWidth(width);
        setMaxWidth(width);
        setPadding(new Insets(PADDING));
        setBackground(new Background(new BackgroundFill(BACKGROUND, CornerRadii.EMPTY, Insets.EMPTY)));
        chart = new PopulationPane(width - 2 * PADDING, CHART_HEIGHT);
        getChildren().addAll(turn, tunas, sharks, empty, status, label(TEXT, "Évolution des populations"), chart);
    }

    void refresh(World world) {
        Census census = world.getHistory().latest();
        turn.setText("Tour : " + world.getTurn());
        tunas.setText("Thons : " + census.tunas());
        sharks.setText("Requins : " + census.sharks());
        empty.setText("Cases vides : " + census.empty());
        status.setText(world.isPaused() ? "EN PAUSE" : "");
        chart.refresh(world.getHistory());
    }

    private static Text label(Color color) {
        return label(color, "");
    }

    private static Text label(Color color, String content) {
        Text text = new Text(content);
        text.setFill(color);
        text.setFont(Font.font("System", FontWeight.BOLD, 20));
        return text;
    }
}
