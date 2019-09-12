import java.io.Serializable;

public class NamedPlace extends Place implements Serializable {

    public NamedPlace(String name, Position position, Category category) {
        super(name, position, category);
    }

    @Override
    public String toString() {
        return "Named," + getCategory().name().substring(0, 1).toUpperCase() + getCategory().name().substring(1).toLowerCase()
                + "," + getPosition().getX() + "," + getPosition().getY() +
                "," + getName();
    }
}

