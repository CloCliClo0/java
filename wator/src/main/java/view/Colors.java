package view;

import javafx.scene.paint.Color;
import model.Species;

final class Colors {

    private Colors() {
    }

    static Color of(Species species) {
        return Color.rgb(species.getRed(), species.getGreen(), species.getBlue());
    }
}
