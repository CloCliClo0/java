package view;

import java.util.List;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import model.Body;
import model.ParticleColor;
import model.World;

final class WorldPane extends Pane {

    private final List<Sprite> sprites;

    WorldPane(World world) {
        sprites = world.getBodies().stream().map(Sprite::new).toList();
        getChildren().addAll(sprites.stream().map(Sprite::circle).toList());
        refresh();
    }

    void refresh() {
        sprites.forEach(Sprite::refresh);
    }

    private record Sprite(Body body, Circle circle) {

        Sprite(Body body) {
            this(body, new Circle(body.getRadius(), toFx(body.getColor())));
        }

        void refresh() {
            circle.setCenterX(body.getX());
            circle.setCenterY(body.getY());
        }

        private static Color toFx(ParticleColor color) {
            return Color.rgb(color.getRed(), color.getGreen(), color.getBlue());
        }
    }
}
