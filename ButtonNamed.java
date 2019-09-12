import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;



public class ButtonNamed extends Alert {

    private TextField platsensNamn = new TextField();


    public ButtonNamed() {
        super(AlertType.CONFIRMATION);
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(10));
        grid.setHgap(5);
        grid.setVgap(10);
        grid.addRow(0, new Label("Name of the place:"), platsensNamn);

        setTitle("Named Place");
        setHeaderText(null);
        getDialogPane().setContent(grid);

    }

    public String getName(){
        return platsensNamn.getText();
    }

}
