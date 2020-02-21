package edu.wpi.cs3733.c20.teamS.pathDisplaying;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.c20.teamS.database.EdgeData;
import edu.wpi.cs3733.c20.teamS.database.NodeData;
import edu.wpi.cs3733.c20.teamS.database.DatabaseController;
import edu.wpi.cs3733.c20.teamS.pathfinding.*;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class PathDisplay {
    private String floornum;
    private String floornum2;
    private int counter = 0;
    private NodeData startNode;
    private NodeData endNode;
    Group groupPath = new Group();
    Group group;
    private IPathfinding algorithm;
    VBox parentVBox;

    public int getCounter() {return counter;}

    public PathDisplay(Group group, VBox parentVBox, IPathfinding pathfinding) {
        this.group = group;
        this.parentVBox = parentVBox;
        this.algorithm = pathfinding;
    }

    public void setNode(NodeData data) {
        if(counter % 2 == 0) {startNode = data; floornum = "0" + data.getFloor();}
        if(counter % 2 != 0) {endNode = data; floornum2 = "0" + data.getFloor();}
        counter++;
    }

    public void pathDraw(int currentFloor) {
        int first = 1;
        String cf = "0" + currentFloor;
        group.getChildren().remove(groupPath);
        groupPath.getChildren().clear();
        if(counter >= 2) {
            boolean sameFloor = startNode.getFloor() == endNode.getFloor();
            DatabaseController dbc = new DatabaseController();
            Set<NodeData> nd = dbc.getAllNodes();

            Set<EdgeData> ed = dbc.getAllEdges();
            MutableGraph<NodeData> graph = GraphBuilder.undirected().allowsSelfLoops(true).build();
            graph.allowsSelfLoops();

            for(NodeData data: nd) {
                if(data.getNodeID().equals(startNode.getNodeID())) {startNode = data;}
                if(data.getNodeID().equals(endNode.getNodeID())) {endNode = data;}
                graph.addNode(data);
            }
            for(EdgeData data: ed) {
                String startID = data.getStartNode();
                String endID = data.getEndNode();
                NodeData start = new NodeData();
                NodeData end = new NodeData();
                for(NodeData data1: nd) {
                    if(startID.equals(data1.getNodeID())) {start = data1;}
                    if(endID.equals(data1.getNodeID())) {end = data1;}
                }
                if(start != null && end != null && !sameFloor) {
                    graph.putEdge(start, end);
                }
                else if(start != null && end != null && sameFloor) {
                    if(start.getFloor() == currentFloor && end.getFloor() == currentFloor) {
                        graph.putEdge(start, end);
                    }
                }
            }

            PathingContext pathContext = new PathingContext(this.algorithm);
            Path path = pathContext.executePathfind(graph, startNode, endNode);
            List<NodeData> work = path.startToFinish();
            WrittenInstructions directions = new WrittenInstructions(work);
            List<String> words = directions.directions();
            System.out.println(words.size());
            int offset = 30;
            parentVBox.getChildren().clear();
            JFXTextField directionLabel = new JFXTextField();
            directionLabel.setText("Directions");
            JFXTextField space = new JFXTextField();
            for(String direct : words) {
                JFXTextArea text = new JFXTextArea();
                text.setText(direct);
                text.setPrefHeight(offset);
                parentVBox.getChildren().add(text);
            }

            for(int i = 0; i < work.size() - 1; i++) {
                EdgeData data = new EdgeData(work.get(i).getNodeID() + "_" + work.get(i + 1).getNodeID(), work.get(i).getNodeID(),work.get(i + 1).getNodeID());
                if(data.getEdgeID().substring(data.getEdgeID().length()-2).equals(cf)) {
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
                        line1.setStroke(Color.RED);
                        line1.setFill(Color.RED.deriveColor(1, 1, 1, 0.5));
                        line1.setStrokeWidth(10);
                        groupPath.getChildren().add(line1);
                        if(first == 1) {
                            if(startNode.getFloor() == currentFloor) {
                            Circle circle = new Circle (startNode.getxCoordinate(),startNode.getyCoordinate(),15);
                            circle.setFill(Color.RED);
                            groupPath.getChildren().add(circle);}

                            if(endNode.getFloor() == currentFloor) {
                                ImageView pinIcon = new ImageView();
                                pinIcon.setImage(new Image("images/Icons/pin.png"));
                                pinIcon.setX(endNode.getxCoordinate() - 20);
                                pinIcon.setY(endNode.getyCoordinate() - 60);
                                pinIcon.setPreserveRatio(true);
                                pinIcon.setFitWidth(40);
                                groupPath.getChildren().add(pinIcon);
                            }
                            first = 2;
                        }
                    }
                }
            }

            group.getChildren().add(groupPath);
        }
    }
}