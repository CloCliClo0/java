package view;

import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import model.World;

public class Main extends Application {

    private static final double PANEL_WIDTH = 340;
    private static final long TURNS_PER_SECOND = 10;
    private static final long TURN_NANOS = 1_000_000_000L / TURNS_PER_SECOND;
    private static final Color BACKGROUND = Color.rgb(16, 18, 24);

    @Override
    public void start(Stage stage) {
        Rectangle2D screen = Screen.getPrimary().getBounds();
        int rows = SeaPane.cellsIn(screen.getHeight());
        int columns = SeaPane.cellsIn(screen.getWidth() - PANEL_WIDTH);

        World world = new World(rows, columns, new Random());
        SeaPane seaPane = new SeaPane(world.getSea());
        StatsPane statsPane = new StatsPane(PANEL_WIDTH);
        Runnable refresh = () -> {
            seaPane.refresh();
            statsPane.refresh(world);
        };

        Scene scene = new Scene(new HBox(seaPane, statsPane), screen.getWidth(), screen.getHeight(), BACKGROUND);
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case SPACE -> {
                    world.togglePause();
                    refresh.run();
                }
                case ESCAPE -> Platform.exit();
                default -> { }
            }
        });

        stage.setScene(scene);
        stage.setFullScreenExitHint("");
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        stage.setFullScreen(true);
        stage.show();

        refresh.run();
        new AnimationTimer() {
            private long nextTurnAt = -1;

            @Override
            public void handle(long now) {
                if (nextTurnAt < 0) {
                    nextTurnAt = now + TURN_NANOS;
                }
                if (now >= nextTurnAt) {
                    world.nextTurn();
                    refresh.run();
                    nextTurnAt = Math.max(nextTurnAt + TURN_NANOS, now);
                }
            }
        }.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
