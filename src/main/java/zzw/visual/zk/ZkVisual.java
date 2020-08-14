package zzw.visual.zk;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import zzw.visual.util.ZkUtils;
import zzw.visual.zk.client.ZkClient;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author zhenwei.wang
 * @description TODO
 * @date 2020/7/7
 */
public class ZkVisual extends Application {

    private static String zkAddress = "mt-zookeeper-vip:2181";

    @Override
    public void start(Stage primaryStage) {

        Scene scene = new Scene(borderPane());
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image("/img/zk.gif"));
        primaryStage.setTitle("Visual.");
        primaryStage.show();
    }

    private BorderPane borderPane() {
        BorderPane pane = new BorderPane();
        pane.setTop(top());
        pane.setLeft(left());
        pane.setCenter(center());
//        pane.setRight();
        pane.setBottom(bottom());
        return pane;
    }

    // left node data.
    private ListView<String> leftView;

    // center text
    private TreeView<String> treeView;

    private Node top() {
        HBox hBox = new HBox();
        hBox.setPadding(new Insets(15, 10, 15, 10));
        hBox.setSpacing(10);
        hBox.setStyle("-fx-background-color: #338d99;");

        TextField textField = new TextField();
        textField.setPromptText("Input your ip address, then Enter!");
        textField.setText(zkAddress);
        textField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                System.out.println(event.getText());
                TextField target = (TextField) event.getTarget();
                String text = target.getText();
                System.out.println(text);
                target.clear();
            }
        });
        hBox.getChildren().add(textField);


        ObservableList<String> cursors = FXCollections.observableArrayList();
        ChoiceBox<String> choiceBox = new ChoiceBox<>(cursors);
        hBox.getChildren().add(choiceBox);
        choiceBox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println(observable + "  " + oldValue + "  " + newValue);
            textField.setText(String.valueOf(newValue));
        });
        choiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println(observable + "  " + oldValue + "  " + newValue);
            textField.setText(String.valueOf(newValue));
        });

        Button ok = new Button("Connect it!");
        ok.setDefaultButton(true);
        ok.setPrefSize(100, 20);
        hBox.getChildren().add(ok);
        ok.setOnMouseClicked(event -> {
            String text = textField.getText();
            System.out.println(text);
            if (ZkUtils.isValidAddress(text)) {
                zkAddress = text;
                if (!cursors.contains(text)) {
                    cursors.add(text);
                }
                leftView.getItems().clear();
                leftView.setItems(leftData());
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION,
                        "it must be like:\n127.0.0.1:2181 \nand so on..",
                        ButtonType.CLOSE);
                alert.headerTextProperty().set("Invalid zk address");
                alert.setTitle("Warning.");
                alert.showAndWait();
            }
        });


        return hBox;
    }


    private Node left() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10)); // Set all sides to 10
        vbox.setSpacing(8);              // Gap between nodes

        Text title = new Text("Path");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        Text desc = new Text("     Default root path./");
        desc.setFont(Font.font("Arial", FontWeight.LIGHT, 10));
        HBox hBox = new HBox();
        hBox.getChildren().addAll(title, desc);
        hBox.alignmentProperty().setValue(Pos.CENTER);
        vbox.getChildren().add(hBox);
/*
        TreeItem<File> fileTreeItem = Dir.createNode(new File("e:/"));
        TreeView treeView = new TreeView(fileTreeItem);
        vbox.getChildren().add(treeView);
*/
        ObservableList<String> objects = FXCollections.emptyObservableList();
        leftView = new ListView<>(objects);
        vbox.getChildren().addAll(leftView);
        leftView.setOnMouseClicked(event -> {
//            System.out.println(event);
            Node node = event.getPickResult().getIntersectedNode();
            if (node instanceof Text) {
                Text text = (Text) node;
                String selectedPath = text.getText();
                System.out.println(selectedPath);
                if (selectedPath != null) {
                    inputPath.setText("/" + selectedPath);
                }
            }
            System.out.println(node.getClass().getName());
            System.out.println("---------------------------");
        });
        leftView.setMaxWidth(150);
        return vbox;
    }

    ZkClient client;

    private ObservableList<String> leftData() {
        client = ZkClient.newClient(zkAddress);
        List<String> children = client.getChildren("/", true);
        ObservableList<String> objects = FXCollections.observableArrayList();
        objects.addAll(children);
        return objects;
    }

    private ObservableList<String> pathData(String path) {
        if (ZkUtils.isValidPath(path)) {
            ZkClient client = ZkClient.newClient(zkAddress);
            List<String> children = client.getChildren(path, true);
            return FXCollections.observableArrayList(children);
        }
        return FXCollections.observableArrayList("none..");
    }

    private Node center() {
        AnchorPane anchorpane = new AnchorPane();

        Button buttonSave = new Button("Save");
        Button buttonCancel = new Button("Cancel");

        HBox hb = new HBox();
        hb.setPadding(new Insets(10)); // Set all sides to 10
        hb.setSpacing(8);              // Gap between nodes

        hb.setPadding(new Insets(0, 10, 10, 10));
        hb.setSpacing(10);
        hb.getChildren().addAll(buttonSave, buttonCancel);
//        GridPane gridPane = addGridPane();

        //中间：文本区
        Label centerLabel = new Label();
//        centerLabel.setAlignment(Pos.CENTER);
        //中间：顶部搜索框
        HBox hBox = centerBox(centerLabel);
        VBox bigCenter = new VBox();
        bigCenter.alignmentProperty().setValue(Pos.CENTER_LEFT);
        bigCenter.getChildren().addAll(hBox, centerLabel);
        anchorpane.getChildren().addAll(bigCenter, hb);
        // Anchor buttons to bottom right, anchor grid to top
        AnchorPane.setBottomAnchor(hb, 8.0);
        AnchorPane.setRightAnchor(hb, 5.0);
//        AnchorPane.setTopAnchor(hBox,5.0);
//        AnchorPane.setBottomAnchor(centerLabel, 1.0);
//        AnchorPane.setTopAnchor(gridPane, 10.0);
        anchorpane.setPrefWidth(500);
        return anchorpane;
    }

    //路径搜索框
    TextField inputPath;

    private HBox centerBox(Label centerLabel) {
        HBox hb = new HBox();
        hb.setPadding(new Insets(10)); // Set all sides to 10
        hb.setSpacing(8);              // Gap between nodes


        Text notice = new Text("Full path:");
        inputPath = new TextField();
        inputPath.setPromptText("full path here. like: /dubbo/xx.xx.xxxx");
//        path.setLayoutX(150);
        inputPath.setMinWidth(350);
        inputPath.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                TextField pathInput = (TextField) event.getTarget();
                String text = ((TextField) event.getTarget()).getText();
                System.out.println(text);
                pathInput.clear();
                ObservableList<String> pathData = pathData(text);
                treeView.setRoot(new TreeItem<>(text));
//                treeView
            }
        });
        Button searchButton = new Button("Search !");
        searchButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String path = inputPath.getText();
                if (ZkUtils.isValidPath(path)) {
                    List<String> children = client.getChildren(path);
                    centerLabel.setBackground(Background.EMPTY);
                    centerLabel.setText(children.toString());

                }
            }
        });
        hb.getChildren().addAll(notice, inputPath,searchButton);
        hb.alignmentProperty().setValue(Pos.CENTER);
        return hb;
    }

    private GridPane addGridPane() {

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(0, 10, 0, 10));

        // Category in column 2, row 1
        Text category = new Text("Sales:");
        category.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        grid.add(category, 1, 0);

        // Title in column 3, row 1
        Text chartTitle = new Text("Current Year");
        chartTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        grid.add(chartTitle, 2, 0);

        // Subtitle in columns 2-3, row 2
        Text chartSubtitle = new Text("Goods and Services");
        grid.add(chartSubtitle, 1, 1, 2, 1);

        // House icon in column 1, rows 1-2
        ImageView imageHouse = new ImageView(
                new Image("graphics/house.png"));
        grid.add(imageHouse, 0, 0, 1, 2);

        // Left label in column 1 (bottom), row 3
        Text goodsPercent = new Text("Goods\n80%");
        GridPane.setValignment(goodsPercent, VPos.BOTTOM);
        grid.add(goodsPercent, 0, 2);

        // Chart in columns 2-3, row 3
        ImageView imageChart = new ImageView(
                new Image("graphics/piechart.png"));
        grid.add(imageChart, 1, 2, 2, 1);

        // Right label in column 4 (top), row 3
        Text servicesPercent = new Text("Services\n20%");
        GridPane.setValignment(servicesPercent, VPos.TOP);
        grid.add(servicesPercent, 3, 2);

//        grid.setGridLinesVisible(true);
        return grid;
    }

    private Node bottom() {
        Label label = new Label(LocalDateTime.now().toString());
        label.setAlignment(Pos.BOTTOM_CENTER);
        return label;
    }
}
