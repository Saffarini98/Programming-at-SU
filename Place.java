import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;


public class Place extends Polygon {
    private String name;
    private Position position;
    private Category category;
    private boolean marked = false;


    public Place(String name, Position position, Category category) {
        super(position.getX(), position.getY(), position.getX()-15, position.getY()-30,
                position.getX()+15, position.getY()-30);
        this.setVisible(true);
        this.name = name;
        this.position = position;
        this.category = category;
        setFill(category.colorMiddle);

    }


    public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }


    public Position getPosition() {
        return position;
    }


    public void setMarked(boolean mark) {
        marked = mark;
        if (marked){
            this.setStroke(category.colorBorder);
    }else{
          this.setStroke(Color.TRANSPARENT);
        }
    }

    public boolean getMarked() {
        return marked;
    }

    @Override
    public String toString() {
        return name;
    }


}

