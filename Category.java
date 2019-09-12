import javafx.scene.paint.Color;

public enum Category {

    NONE (Color.BLACK, Color.GRAY),
    BUS   (Color.RED,Color.BLACK),
    UNDERGROUND (Color.BLUE,Color.BLACK),
    TRAIN (Color.GREEN, Color.BLACK);

    Color colorMiddle;
    Color colorBorder;


    Category(Color middle, Color border) {
        colorMiddle = middle;
        colorBorder = border;

    }
}
