package view;

import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import model.ParticleFactory;
import model.World;

public class Main extends Application {

    private static final int PARTICLE_COUNT = 150;
    private static final Color BACKGROUND = Color.rgb(16, 18, 24);

    @Override
    public void start(Stage stage) {
        Rectangle2D screen = Screen.getPrimary().getBounds();
        double width = screen.getWidth();
        double height = screen.getHeight();

        World world = new World(new ParticleFactory(new Random()), PARTICLE_COUNT, width, height);
        WorldPane pane = new WorldPane(world);

        Scene scene = new Scene(pane, width, height, BACKGROUND);
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case SPACE -> world.togglePause();
                case ESCAPE -> Platform.exit();
                default -> { }
            }
        });

        stage.setScene(scene);
        stage.setFullScreenExitHint("");
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        stage.setFullScreen(true);
        stage.show();

        new AnimationTimer() {
            private long previous = -1;

            @Override
            public void handle(long now) {
                if (previous >= 0) {
                    world.step((now - previous) / 1_000_000_000.0);
                }
                previous = now;
                pane.refresh();
            }
        }.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
