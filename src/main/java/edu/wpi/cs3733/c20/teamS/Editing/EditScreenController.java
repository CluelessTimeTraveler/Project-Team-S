package edu.wpi.cs3733.c20.teamS.Editing;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import edu.wpi.cs3733.c20.teamS.MapZoomer;
import edu.wpi.cs3733.c20.teamS.serviceRequests.Employee;
import edu.wpi.cs3733.c20.teamS.serviceRequests.SelectServiceScreen;
import edu.wpi.cs3733.c20.teamS.database.DatabaseController;
import edu.wpi.cs3733.c20.teamS.database.EdgeData;
import edu.wpi.cs3733.c20.teamS.database.NodeData;
import edu.wpi.cs3733.c20.teamS.mainToLoginScreen;
import edu.wpi.cs3733.c20.teamS.serviceRequests.Employee;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;

public class EditScreenController implements Initializable {
    private MoveNodes moveNode = new MoveNodes();
    private Stage stage;


    private Employee loggedIn;

    /**
     *
     * @param stage the stage to take over
     * @param employee the employee that logged in
     */
    public EditScreenController(Stage stage, Employee employee) {
        this.stage  = stage;
        this.loggedIn = employee;
    }

    int current_floor = 2;
    String newFloor;
    private MapZoomer zoomer;
    private double currentHval;
    private double currentVval;

    private void keepCurrentPosition(double Hval, double Vval, MapZoomer zoomer){
        zoomer.zoomSet();
        scrollPane.setHvalue(Hval);
        scrollPane.setVvalue(Vval);
    }

    Image floor1 = new Image("images/Floors/HospitalFloor1.png");
    Image floor2 = new Image("images/Floors/HospitalFloor2.png");
    Image floor3 = new Image("images/Floors/HospitalFloor3.png");
    Image floor4 = new Image("images/Floors/HospitalFloor4.png");
    Image floor5 = new Image("images/Floors/HospitalFloor5.png");
    @FXML
    JFXRadioButton addNodeRadio;
    @FXML
    JFXRadioButton removeNodeRadio;
    @FXML
    JFXRadioButton addEdgeRadio;
    @FXML
    JFXRadioButton removeEdgeRadio;
    @FXML
    JFXRadioButton moveNodeRadio;
    @FXML
    JFXRadioButton showInfoRadio;

    @FXML
    private ImageView mapImage;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private JFXButton floorButton1;
    @FXML
    private JFXButton floorButton2;
    @FXML
    private JFXButton floorButton3;
    @FXML
    private JFXButton floorButton4;
    @FXML
    private JFXButton floorButton5;
    @FXML
    private JFXButton downButton;
    @FXML
    private JFXButton upButton;

    @FXML
    JFXButton zoomInButton;
    @FXML
    JFXButton zoomOutButton;

        @FXML private JFXButton cancelEditsButton;
        @FXML private JFXButton confirmEditButton;

        public JFXButton getFloorButton2() {return floorButton2;}

    public void onLogOut() {
        mainToLoginScreen back = new mainToLoginScreen(stage);
    }

    private void unselectALL() {
        addNodeRadio.selectedProperty().set(false);
        removeNodeRadio.selectedProperty().set(false);
        removeEdgeRadio.selectedProperty().set(false);
        addEdgeRadio.selectedProperty().set(false);
        moveNodeRadio.selectedProperty().set(false);
        showInfoRadio.selectedProperty().set(false);
    }

    @FXML
    void onUpClicked(ActionEvent event) {
        current_floor += 1;
        if (current_floor == 1) {
            set1();
            mapImage.setImage(floor1);
            current_floor = 1;
            drawNodesEdges();
        } else if (current_floor == 2) {
            set2();
            mapImage.setImage(floor2);
            current_floor = 2;
            drawNodesEdges();
        } else if (current_floor == 3) {
            set3();
            mapImage.setImage(floor3);
            current_floor = 3;
            drawNodesEdges();
        } else if (current_floor == 4) {
            set4();
            mapImage.setImage(floor4);
            current_floor = 4;
            drawNodesEdges();
        } else if (current_floor == 5) {
            set5();
            mapImage.setImage(floor5);
            current_floor = 5;
            drawNodesEdges();
        }
    }

    @FXML
    void onDownClicked(ActionEvent event) {
        current_floor -= 1;
        if (current_floor == 1) {
            set1();
            mapImage.setImage(floor1);
            current_floor = 1;
            drawNodesEdges();
        } else if (current_floor == 2) {
            set2();
            mapImage.setImage(floor2);
            current_floor = 2;
            drawNodesEdges();

        } else if (current_floor == 3) {
            set3();
            mapImage.setImage(floor3);
            current_floor = 3;
            drawNodesEdges();
        } else if (current_floor == 4) {
            set4();
            mapImage.setImage(floor4);
            current_floor = 4;
            drawNodesEdges();
        } else if (current_floor == 5) {
            set5();
            mapImage.setImage(floor5);
            current_floor = 5;
            drawNodesEdges();
        }
    }

    void set1() {
        floorButton1.setStyle("-fx-background-color: #f6bd38; -fx-font: 32 System;");
        floorButton2.setStyle("-fx-background-color: #ffffff; -fx-font: 22 System;");
        floorButton3.setStyle("-fx-background-color: #ffffff; -fx-font: 22 System;");
        floorButton4.setStyle("-fx-background-color: #ffffff; -fx-font: 22 System;");
        floorButton5.setStyle("-fx-background-color: #ffffff; -fx-font: 22 System;");
        upButton.setDisable(false);
        downButton.setDisable(true);
    }

    void set2() {
        floorButton1.setStyle("-fx-background-color: #ffffff; -fx-font: 22 System;");
        floorButton2.setStyle("-fx-background-color: #f6bd38; -fx-font: 32 System;");
        floorButton3.setStyle("-fx-background-color: #ffffff; -fx-font: 22 System;");
        floorButton4.setStyle("-fx-background-color: #ffffff; -fx-font: 22 System;");
        floorButton5.setStyle("-fx-background-color: #ffffff; -fx-font: 22 System;");
        upButton.setDisable(false);
        downButton.setDisable(false);
    }

    void set3() {
        floorButton1.setStyle("-fx-background-color: #ffffff; -fx-font: 22 System;");
        floorButton2.setStyle("-fx-background-color: #ffffff; -fx-font: 22 System;");
        floorButton3.setStyle("-fx-background-color: #f6bd38; -fx-font: 32 System;");
        floorButton4.setStyle("-fx-background-color: #ffffff; -fx-font: 22 System;");
        floorButton5.setStyle("-fx-background-color: #ffffff; -fx-font: 22 System;");
        upButton.setDisable(false);
        downButton.setDisable(false);
    }

    void set4() {
        floorButton1.setStyle("-fx-background-color: #ffffff; -fx-font: 22 System;");
        floorButton2.setStyle("-fx-background-color: #ffffff; -fx-font: 22 System;");
        floorButton3.setStyle("-fx-background-color: #ffffff; -fx-font: 22 System;");
        floorButton4.setStyle("-fx-background-color: #f6bd38; -fx-font: 32 System;");
        floorButton5.setStyle("-fx-background-color: #ffffff; -fx-font: 22 System;");
        upButton.setDisable(false);
        downButton.setDisable(false);
    }

    void set5() {
        floorButton1.setStyle("-fx-background-color: #ffffff; -fx-font: 22 System;");
        floorButton2.setStyle("-fx-background-color: #ffffff; -fx-font: 22 System;");
        floorButton3.setStyle("-fx-background-color: #ffffff; -fx-font: 22 System;");
        floorButton4.setStyle("-fx-background-color: #ffffff; -fx-font: 22 System;");
        floorButton5.setStyle("-fx-background-color: #f6bd38; -fx-font: 32 System;");
        upButton.setDisable(true);
        downButton.setDisable(false);
    }

    @FXML
    void onFloorClicked1(ActionEvent event) {
        set1();
        mapImage.setImage(floor1);
        current_floor = 1;
        drawNodesEdges();
    }


    @FXML
    void onFloorClicked2(ActionEvent event) {
        set2();
        mapImage.setImage(floor2);
        current_floor = 2;
        drawNodesEdges();
    }


    @FXML
    void onFloorClicked3(ActionEvent event) {
        set3();
        mapImage.setImage(floor3);
        current_floor = 3;
        drawNodesEdges();
    }


    @FXML
    void onFloorClicked4(ActionEvent event) {
        set4();
        mapImage.setImage(floor4);
        current_floor = 4;
        drawNodesEdges();

    }


    @FXML
    void onFloorClicked5(ActionEvent event) {
        set5();
        mapImage.setImage(floor5);
        current_floor = 5;
        drawNodesEdges();
    }

    @FXML
    void onHelpClicked(ActionEvent event) {
    }

    @FXML
    void onStaffClicked(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXML/loginScreen.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage window = new Stage();
            window.initModality(Modality.APPLICATION_MODAL);
            window.setTitle("Login to the System");
            window.setScene(new Scene(root1));
            window.setResizable(false);
            window.show();
        } catch (Exception e) {
            System.out.println("Can't load new window");
        }
    }

    @FXML
    void onZoomInClicked(ActionEvent event) {
        this.zoomer.zoomIn();
        if (zoomer.getZoomStage() == 3) {
            zoomInButton.setDisable(true);
            zoomOutButton.setDisable(false);
        } else {
            zoomOutButton.setDisable(false);
            zoomOutButton.setDisable(false);
        }
    }

    @FXML
    void onZoomOutClicked(ActionEvent event) {
        this.zoomer.zoomOut();
        if (zoomer.getZoomStage() == -2) {
            zoomOutButton.setDisable(true);
            zoomInButton.setDisable(false);
        } else {
            zoomOutButton.setDisable(false);
            zoomInButton.setDisable(false);
        }
    }

    public JFXButton getFloor2() {
        return floorButton2;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        zoomer = new MapZoomer(mapImage, scrollPane);
    }

    public void drawNodesEdges() {
        currentHval = scrollPane.getHvalue();
        currentVval = scrollPane.getVvalue();
        unselectALL();
        moveNode.setScale(zoomer.zoomFactor());
        moveNode.setCurrent_floor(current_floor);

        String floor = "0" + current_floor;

        Group group = new Group();

        group.getChildren().clear();

        group.getChildren().add(mapImage);

        MapEditingTasks tester = new MapEditingTasks(group);

        DatabaseController dbc = new DatabaseController();
        Set<NodeData> nd = dbc.getAllNodes();

        for(NodeData data : nd) {
            Circle circle1 = new Circle(data.getxCoordinate(), data.getyCoordinate(), 25);
            circle1.setStroke(Color.ORANGE);
            circle1.setFill(Color.ORANGE.deriveColor(1, 1, 1, 0.5));
            if (data.getNodeType().equals("ELEV")) {
                circle1.setFill(Color.GREEN.deriveColor(1, 1, 1, 0.5));
            }
            circle1.setVisible(false);
            if (data.getNodeID().substring(data.getNodeID().length() - 2).equals(floor)) {
                circle1.setVisible(true);
            }
            group.getChildren().add(circle1);
        }

        Set<EdgeData> ed = dbc.getAllEdges();

        for(EdgeData data : ed) {
            if(data.getEdgeID().substring(data.getEdgeID().length()-2).equals(floor)) {
                String start = data.getStartNode();
                String end = data.getEndNode();
                int startX = 0;
                int startY = 0;
                int endX = 0;
                int endY = 0;
                boolean checker1 = false;
                boolean checker2 = false;
                for(NodeData check: nd) {
                    if(check.getNodeID().equals(start)) {
                        checker1 = true;
                        startX = (int)check.getxCoordinate();
                        startY = (int)check.getyCoordinate();
                    }
                    if(check.getNodeID().equals(end)) {
                        checker2 = true;
                        endX = (int)check.getxCoordinate();
                        endY = (int)check.getyCoordinate();
                    }
                }
                if(checker1 && checker2) {
                    Line line1 = new Line();
                    line1.setStartX(startX);
                    line1.setStartY(startY);
                    line1.setEndX(endX);
                    line1.setEndY(endY);
                    line1.setStroke(Color.BLUE);
                    line1.setFill(Color.BLUE.deriveColor(1, 1, 1, 0.5));
                    line1.setStrokeWidth(5);
                    line1.setVisible(false);
                    if (data.getEdgeID().substring(data.getEdgeID().length() - 2).equals(floor)) {
                        line1.setVisible(true);
                    }
                    group.getChildren().add(line1);
                }
            }
        }


        moveNodeRadio.setOnAction(e -> {currentHval = scrollPane.getHvalue();
            currentVval = scrollPane.getVvalue();tester.moveNodes(mapImage, current_floor, moveNode); keepCurrentPosition(currentHval, currentVval, zoomer);});
        showInfoRadio.setOnAction(e -> {currentHval = scrollPane.getHvalue();
            currentVval = scrollPane.getVvalue();tester.showNodeInfo(mapImage, current_floor); keepCurrentPosition(currentHval, currentVval, zoomer);});

        addNodeRadio.setOnAction(e -> {
            currentHval = scrollPane.getHvalue();
            currentVval = scrollPane.getVvalue();
            tester.drawNodes(current_floor);
            keepCurrentPosition(currentHval, currentVval, zoomer);
        });

        removeNodeRadio.setOnAction(e -> {
            currentHval = scrollPane.getHvalue();
            currentVval = scrollPane.getVvalue();
            tester.removeNodes(mapImage, current_floor);
            keepCurrentPosition(currentHval, currentVval, zoomer);

        });

        addEdgeRadio.setOnAction(e -> {
            currentHval = scrollPane.getHvalue();
            currentVval = scrollPane.getVvalue();
            tester.addEdge(mapImage, current_floor);
            keepCurrentPosition(currentHval, currentVval, zoomer);

        });

        removeEdgeRadio.setOnAction(e -> {
            currentHval = scrollPane.getHvalue();
            currentVval = scrollPane.getVvalue();
            tester.removeEdge(mapImage, current_floor);
            keepCurrentPosition(currentHval, currentVval, zoomer);

        });

        confirmEditButton.setOnAction(e -> {
            tester.saveChanges();

        });

        cancelEditsButton.setOnAction(e -> {
            tester.cancelChanges();
            new MapEditingScreen(stage, loggedIn);
        });

        scrollPane.setContent(group);

        //Keeps the zoom the same throughout each screen/floor change.
        keepCurrentPosition(currentHval, currentVval, zoomer);
    }

    @FXML
    void onNewServiceClicked(ActionEvent event) {
        SelectServiceScreen.showDialog(loggedIn);
    }

    @FXML
    void onActiveServiceClicked(ActionEvent event) {

    }
}
