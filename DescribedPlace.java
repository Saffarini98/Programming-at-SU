import java.io.Serializable;


public class DescribedPlace extends Place implements Serializable {
    private String description;


    public DescribedPlace(String name, Position position, Category category, String description) {
        super(name, position, category);
        this.description = description;
    }


    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Described," + getCategory().name().substring(0, 1).toUpperCase() + getCategory().name().substring(1).toLowerCase() + "," + getPosition().getX() + "," + getPosition().getY() +
                "," + getName() + "," + getDescription();
    }
}
