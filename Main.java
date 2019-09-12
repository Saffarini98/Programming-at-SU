import java.io.*;
import java.io.IOException;
import java.io.FileWriter;
import java.util.*;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.util.Optional;


public class Main extends Application {
    ArrayList<Place> markMap = new ArrayList<Place>();
    HashMap<Position, Place> mapPositions = new HashMap<>();
    HashMap<String, Set<Place>> placeMap = new HashMap();
    HashMap<Category, HashSet<Place>> mapCategories = new HashMap<>();
    HashSet<Place> busSet = new HashSet<>();
    HashSet<Place> trainSet = new HashSet<>();
    HashSet<Place> undergroundSet = new HashSet<>();
    HashSet<Place> undescribedSet = new HashSet<>();
    private String filepath = null;
    private Stage window;
    RadioButton namedButton = new RadioButton("Named");
    RadioButton describedButton = new RadioButton("Described");
    private Scene scene1, scene2;
    ImageView imageView = new ImageView();
    ListView listView;
    Pane pane = new Pane();
    ScrollPane scrollPane = new ScrollPane();
    boolean clicked = false;
    boolean foundResult = false;
    boolean mouseTriangleMode = false;
    private boolean changed = false;


    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) {
        window = primaryStage;
        BorderPane root = new BorderPane();
        root.setCenter(pane);
        pane.getChildren().add(imageView);
        scrollPane.setContent(pane);

        mapCategories.put(Category.BUS, busSet);
        mapCategories.put(Category.TRAIN, trainSet);
        mapCategories.put(Category.UNDERGROUND, undergroundSet);
        mapCategories.put(Category.NONE, undescribedSet);


        /* -----------------------------------------MENU BAR-----------------------------------------*/
        BorderPane uppe = new BorderPane();
        FlowPane flow = new FlowPane();
        VBox vbox = new VBox();
        MenuBar menuBar = new MenuBar();
        vbox.getChildren().add(menuBar);
        Menu archiveMenu = new Menu("File");
        menuBar.getMenus().add(archiveMenu);
        MenuItem loadMapsItem = new MenuItem("Load Maps");
        archiveMenu.getItems().add(loadMapsItem);
        loadMapsItem.setOnAction(new LoadMapsHandler());
        MenuItem loadPlaces = new MenuItem("Load Places");
        archiveMenu.getItems().add(loadPlaces);
        loadPlaces.setOnAction(new LoadPlacesHandler());
        MenuItem saveItem = new MenuItem("Save");
        saveItem.setOnAction(new SaveHandler());
        archiveMenu.getItems().add(saveItem);
        MenuItem exitItem = new MenuItem("Exit");
        archiveMenu.getItems().add(exitItem);
        exitItem.setOnAction(new ExitHandler());
        uppe.setTop(vbox);
        window.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, new ExitHandler());

        /* -----------------------------------------TOP PANEL-----------------------------------------*/
        TextField searchField = new TextField();
        searchField.setPromptText("Search here");
        Button searchs = new Button("Search");
        Button newButton = new Button("New");
        Button hide = new Button("Hide");
        hide.setOnAction(new HideHandler());
        Button remove = new Button("Remove");
        Button coordinates = new Button("Coordinates");
        VBox left = new VBox();
        ToggleGroup group = new ToggleGroup();
        VBox vBox = new VBox();
        vBox.getChildren().addAll(namedButton, describedButton);
        namedButton.setToggleGroup(group);
        describedButton.setToggleGroup(group);

        namedButton.setSelected(true);
        root.setLeft(left);
        flow.getChildren().addAll(newButton, vBox,
                searchField, searchs, hide, remove, coordinates
        );
        flow.setHgap(8);
        flow.setAlignment(Pos.CENTER);
        uppe.setCenter(flow);
        root.setTop(uppe);


        /* -----------------------------------------RIGHT PANEL-----------------------------------------*/
        VBox rightBox = new VBox();
        Label categories = new Label("Categories");
        rightBox.setAlignment(Pos.CENTER);
        root.setRight(rightBox);
        Button hideCategories = new Button("Hide categories");
        hideCategories.setOnAction(new HideCategoriesHandler());
        ObservableList<String> options = FXCollections.observableArrayList(
                "Bus", "Underground", "Train");
        listView = new ListView(options);
        listView.setMaxSize(200, 100);
        rightBox.getChildren().addAll(categories, listView, hideCategories);
        categories.setFont(Font.font("Times", FontWeight.BOLD, 20));


        /* -----------------------------------------SHOW-----------------------------------------*/
        scene1 = new Scene(root, 1150, 793);
        window.setScene(scene1);
        window.setTitle("Karta JavaFX");
        window.show();


        /* -----------------------------------------SÖKNING-----------------------------------------*/
        searchs.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String find = searchField.getText();
                foundResult = false;
                Set<Place> p = placeMap.get(find);

                if (p == null) {
                    return;
                }

                for (Place place : p) {
                    markMap.add(place);
                    place.setMarked(true);
                    place.setVisible(true);
                    foundResult = true;

                }
                if (!foundResult) {
                    Alert message = new Alert(Alert.AlertType.ERROR, "Det finns inga platser med detta namn!");
                    message.showAndWait();
                    return;
                }

            }
        });


        /* -----------------------------------------COORDINATES-----------------------------------------*/
        coordinates.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TextField xCoordinate = new TextField();
                TextField yCoordinate = new TextField();
                Alert a = new Alert(Alert.AlertType.CONFIRMATION);
                GridPane grid = new GridPane();
                grid.setAlignment(Pos.CENTER);
                grid.setPadding(new Insets(10));
                grid.setHgap(5);
                grid.setVgap(10);
                grid.addRow(0, new Label("X:"), xCoordinate);
                grid.addRow(1, new Label("Y:"), yCoordinate);
                a.getDialogPane().setContent(grid);
                a.setTitle("Input coordinates");
                a.showAndWait();

                Double x = Double.valueOf(xCoordinate.getText());
                Double y = Double.valueOf(yCoordinate.getText());

                for (Place p : markMap) {
                    p.setMarked(false);
                }
                foundResult = false;
                if (mapPositions.containsKey(new Position(x, y))) {
                    Place p = mapPositions.get(new Position(x, y));
                    p.setMarked(true);
                    p.setVisible(true);
                    foundResult = true;
                } else {
                    Alert message = new Alert(Alert.AlertType.ERROR, "Det finns inga platser med dessa koordinater!");
                    message.showAndWait();
                    return;
                }
            }
        });


        /* -----------------------------------------NEW BUTTON-----------------------------------------*/
        newButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                pane.setCursor(Cursor.CROSSHAIR);
                mouseTriangleMode = true;
                pane.setOnMouseClicked(new ImageHandler());
                pane.setDisable(false);
                clicked = false;
            }
        });

        /* -----------------------------------------REMOVE BUTTON-----------------------------------------*/
        remove.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                removeMarked();
                changed = true;
            }
        });
    }

    public void removeMarked() {
        for (Place markedPlaces : markMap) {
            markedPlaces.setMarked(false);
            markedPlaces.setVisible(false);
            pane.getChildren().remove(markedPlaces);
            mapPositions.remove(markedPlaces.getPosition());
            mapCategories.get(markedPlaces.getCategory()).remove(markedPlaces);
            placeMap.get(markedPlaces.getName()).remove(markedPlaces);
        }
        markMap.clear();

    }

    public void clear() {
        mapCategories.clear();
        mapPositions.clear();
        placeMap.clear();
        markMap.clear();
        pane.getChildren().clear();
        pane.getChildren().add(imageView);

    }


    class ImageHandler implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent mouseEvent) {
            String namn;
            String description = null;
            boolean described = false;
            clicked = true;
            mouseTriangleMode = false; //kom ihåg att kontrollera INNAN du gör en ny plats!

            if (namedButton.isSelected()) {
                ButtonNamed buttonNamed = new ButtonNamed();
                Optional<ButtonType> buttonType = buttonNamed.showAndWait();
                if (buttonType.isPresent() && buttonType.get() == ButtonType.OK) {
                    namn = buttonNamed.getName();
                    if (namn.trim().isEmpty()) {
                        Alert msg = new Alert(Alert.AlertType.ERROR, "Tomt namn!");
                        msg.showAndWait();
                        return;
                    }
                } else {
                    return;
                }
            } else {
                ButtonDescribed buttonDescribed = new ButtonDescribed();
                Optional<ButtonType> buttonType = buttonDescribed.showAndWait();
                if (buttonType.isPresent() && buttonType.get() == ButtonType.OK) {
                    namn = buttonDescribed.getName();
                    description = buttonDescribed.getDescription();
                    if (namn.trim().isEmpty() && description.trim().isEmpty()) {
                        Alert msg = new Alert(Alert.AlertType.ERROR, "Tomt namn och beskrivning!");
                        msg.showAndWait();
                        return;
                    }
                } else {
                    return;
                }
                described = true;
            }
            mouseTriangleMode = false;
            Position position = new Position(mouseEvent.getX(), mouseEvent.getY());

            Category category = null;
            String selected = (String) listView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                category = Category.NONE;
            } else {
                switch (selected) {
                    case "Bus":
                        category = Category.BUS;
                        break;
                    case "Underground":
                        category = Category.UNDERGROUND;
                        break;
                    case "Train":
                        category = Category.TRAIN;
                        break;
                }
            }
            Place place = null;

            if (mapPositions.containsKey(position)) {
                Alert a = new Alert(Alert.AlertType.ERROR, "Du kan inte skapa en plats här.");
                a.showAndWait();
                return;
            }

            if (described) {
                place = new DescribedPlace(namn, position, category, description);
            } else {
                place = new NamedPlace(namn, position, category);
            }


            if (placeMap.get(namn) == null) {
                Set<Place> p = new HashSet<>();
                placeMap.put(namn, p);
            }
            placeMap.get(namn).add(place);


            if (place.getCategory() != Category.NONE) {
                HashSet<Place> set = mapCategories.get(place.getCategory());
                if (set == null) {
                    set = new HashSet<>();
                    mapCategories.put(place.getCategory(), set);
                }
                set.add(place);
            }


            mapPositions.put(position, place);
            clicked = false;
            pane.setCursor(Cursor.DEFAULT);
            addHandlerToPlace(place);
            pane.getChildren().add(place);
            pane.setOnMouseClicked(null);
            changed = true;
        }

    }

    /* -----------------------------------------LOAD MAPS-----------------------------------------*/
    class LoadMapsHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            clear();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select picture to open");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Images", "*.jpg", "*.png", "*.bmp"));
            File file = fileChooser.showOpenDialog(window);
            if (file != null) {
                String name = file.getAbsolutePath();
                javafx.scene.image.Image image = new Image("file:" + name);
                imageView.setImage(image);


                window.sizeToScene();
            }
        }
    }

    /* -----------------------------------------LOAD PLACES-----------------------------------------*/

    class LoadPlacesHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            if (changed) {
                Alert msg = new Alert(Alert.AlertType.CONFIRMATION, "Ändringar har gjorts. Vill du spara dessa förändringar?");
                ButtonType okButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
                ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
                ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                msg.getButtonTypes().clear();
                msg.getButtonTypes().addAll(okButton, noButton, cancelButton);
                Optional<ButtonType> btnType = msg.showAndWait();

                if (btnType.isPresent() && btnType.get() == okButton) {
                    saveToFile();
                } else if (btnType.isPresent() && btnType.get() == cancelButton) {
                    return;
                } else {

                }
            }
            clear();
            try {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Select text to open");
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("text", "*.txt"));
                File file = fileChooser.showOpenDialog(window);
                if (file != null) {
                    filepath = file.getAbsolutePath();
                    FileReader inFile = new FileReader(filepath);
                    BufferedReader in = new BufferedReader(inFile);
                    String line;


                    while ((line = in.readLine()) != null) {
                        String[] tokens = line.split(",");
                        Place p;

                        double x = Double.parseDouble(tokens[2]);
                        double y = Double.parseDouble(tokens[3]);
                        Position pos = new Position(x, y);


                        if (tokens[0].equalsIgnoreCase("Named")) {
                            Category kat = null;
                            switch (tokens[1]) {
                                case "Bus":
                                    kat = Category.BUS;
                                    break;
                                case "Underground":
                                    kat = Category.UNDERGROUND;
                                    break;
                                case "Train":
                                    kat = Category.TRAIN;
                                    break;
                                case "None":
                                    kat = Category.NONE;
                            }
                            String name = tokens[4];
                            p = new NamedPlace(name, pos, kat);
                        } else {
                            Category kat = null;
                            switch (tokens[1]) {
                                case "Bus":
                                    kat = Category.BUS;
                                    break;
                                case "Underground":
                                    kat = Category.UNDERGROUND;
                                    break;
                                case "Train":
                                    kat = Category.TRAIN;
                                    break;
                                case "None":
                                    kat = Category.NONE;
                            }

                            String name = tokens[4];
                            String desc = tokens[5];
                            p = new DescribedPlace(name, pos, kat, desc);
                        }

                        addHandlerToPlace(p);
                        mapPositions.put(p.getPosition(), p);


                        if (placeMap.get(p.getName()) == null) {
                            Set<Place> sp = new HashSet<>();
                            placeMap.put(p.getName(), sp);
                        }
                        placeMap.get(p.getName()).add(p);

                        if (p.getCategory() != Category.NONE) {
                            HashSet<Place> set = mapCategories.get(p.getCategory());
                            if (set == null) {
                                set = new HashSet<>();
                                mapCategories.put(p.getCategory(), set);
                            }
                            set.add(p);
                        }
                    }

                    pane.getChildren().removeAll(mapPositions.values());
                    pane.getChildren().addAll(mapPositions.values());
                }

            } catch (FileNotFoundException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
                alert.showAndWait();
                return;
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
                alert.showAndWait();
                return;

            }
        }

    }

    /* -----------------------------------------EXIT-----------------------------------------*/
    class ExitHandler implements EventHandler {
        @Override
        public void handle(Event event) {
            if (!changed) {
                System.exit(0);
            } else {
                Alert msg = new Alert(Alert.AlertType.CONFIRMATION, "Ändringar har gjorts. Vill du spara dessa förändringar?");
                ButtonType okButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
                ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
                ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                msg.getButtonTypes().clear();
                msg.getButtonTypes().addAll(okButton, noButton, cancelButton);
                Optional<ButtonType> btnType = msg.showAndWait();
                if (btnType.isPresent() && btnType.get() == okButton) {
                    saveToFile();
                } else if (btnType.isPresent() && btnType.get() == noButton) {
                    System.exit(0);
                } else event.consume();
            }
        }
    }

    /* -----------------------------------------HIDE-----------------------------------------*/
    class HideHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            for (Place markedPlaces : markMap) {
                markedPlaces.setMarked(false);
                markedPlaces.setVisible(false);
            }
            markMap.clear();
        }
    }

    /* -----------------------------------------HIDE CATEGORIES-----------------------------------------*/
    class HideCategoriesHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {

            String c = (String) listView.getSelectionModel().getSelectedItem();
            Category cat = null;
            if (c.equals("Bus")) {
                cat = Category.BUS;
            } else if (c.equals("Train")) {
                cat = Category.TRAIN;
            } else if (c.equals("Underground")) {
                cat = Category.UNDERGROUND;
            }

            for (Place p : mapCategories.get(cat)) {
                p.setVisible(false);
                p.setMarked(false);
                markMap.remove(p);
            }
        }
    }

    /* -----------------------------------------------SAVE-----------------------------------------------*/
    class SaveHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            saveToFile();
        }
    }

    public void saveToFile() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select text to save");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("text", "*.txt"));
            File file = fileChooser.showSaveDialog(window);

            if (file != null) {
                FileWriter outFile = new FileWriter(file);
                PrintWriter out = new PrintWriter(outFile);

                for (Place p : mapPositions.values()) {
                    out.println(p.toString());
                }
                out.close();
                changed = false;
            }
        } catch (FileNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.showAndWait();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.showAndWait();
        }
    }


    public void addHandlerToPlace(Place place) {
        place.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                if (e.getButton().equals(MouseButton.PRIMARY)) {
                    if (place.getMarked()) {
                        place.setStroke(Color.TRANSPARENT);
                        place.setMarked(false);
                        markMap.remove(place);
                    } else {
                        place.setStroke(Color.BLACK);
                        place.setMarked(true);
                        markMap.add(place);
                    }
                } else if (e.getButton().equals(MouseButton.SECONDARY)) {
                    String contentHolder = place.getName() + "[" + place.getPosition() + "]" + System.lineSeparator()
                            + System.lineSeparator();
                    Alert info = new Alert(Alert.AlertType.INFORMATION);
                    if (place instanceof DescribedPlace) {
                        info = new Alert(Alert.AlertType.INFORMATION,
                                place.getName() + "[" + place.getPosition() + "]" + System.lineSeparator()
                                        + System.lineSeparator() + ((DescribedPlace) place).getDescription());
                        info.showAndWait();
                        return;
                    }
                    info.setContentText(contentHolder);
                    info.showAndWait();
                    markMap.add(place);
                }
            }
        });
    }
}