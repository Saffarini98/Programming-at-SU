import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;


public class ButtonDescribed extends Alert {

    private TextField platsensNamn = new TextField();
    private TextField platsensBeskrivning = new TextField();


    public ButtonDescribed() {
        super(AlertType.CONFIRMATION);
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(10));
        grid.setHgap(5);
        grid.setVgap(10);
        grid.addRow(0, new Label("Name of the place:"), platsensNamn);
        grid.addRow(1, new Label("Description"), platsensBeskrivning);

        setTitle("Described Place");
        setHeaderText(null);
        getDialogPane().setContent(grid);

    }

    public String getName() {
        return platsensNamn.getText();
    }

    public String getDescription() {
        return platsensBeskrivning.getText();
    }
}



