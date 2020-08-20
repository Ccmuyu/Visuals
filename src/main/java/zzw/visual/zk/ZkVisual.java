package zzw.visual.zk;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.zookeeper.KeeperException;
import zzw.visual.util.ResourceLoader;
import zzw.visual.util.ZkUtils;
import zzw.visual.zk.client.ZkClient;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * @author zhenwei.wang
 * @description TODO
 * @date 2020/7/7
 */
public class ZkVisual extends Application {

    private static String zkAddress;

    static {
        Object prop = ResourceLoader.getProp("default.zk-address");
        zkAddress = prop.toString();
    }

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

    // client zookeeper proxy
    private ZkClient client;

    //路径搜索框
    private TextField inputPath;


    private Node top() {
        HBox hBox = new HBox();
        hBox.setPadding(new Insets(15, 10, 15, 10));
        hBox.setSpacing(10);
        hBox.setStyle("-fx-background-color: #338d99;");

        TextField inputAddress = new TextField();
        inputAddress.setPromptText("Input your ip address, then Enter!");
        inputAddress.setText(zkAddress);
        inputAddress.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                System.out.println(event.getText());
                TextField target = (TextField) event.getTarget();
                String text = target.getText();
                System.out.println(text);
                target.clear();
            }
        });
        ObservableList<String> pullDownList = FXCollections.observableArrayList();
        ChoiceBox<String> choiceBox = new ChoiceBox<>(pullDownList);
        choiceBox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println(observable + "  " + oldValue + "  " + newValue);
            inputAddress.setText(String.valueOf(newValue));
        });
        choiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println(observable + "  " + oldValue + "  " + newValue);
            inputAddress.setText(String.valueOf(newValue));
        });

        Button ok = new Button("Connect it!");
        ok.setDefaultButton(true);
        ok.setPrefSize(100, 20);
        ok.setOnMouseClicked(event -> {
            String text = inputAddress.getText();
            System.out.println(text);
            if (ZkUtils.isValidAddress(text)) {
                zkAddress = text;
                if (!pullDownList.contains(text)) {
                    pullDownList.add(text);
                }
                leftView.getItems().clear();
                //左列表数据填充
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
        Button disconnect = new Button();
        disconnect.setText("Disconnect");
        disconnect.setOnMouseClicked(event -> {
            System.out.println(event.getEventType());
            disconnectClient();
            clearLeftListView();
        });
        hBox.getChildren().addAll(inputAddress, choiceBox, ok, disconnect);
        return hBox;
    }

    private void clearLeftListView() {
        leftView.getItems().clear();
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


    private ObservableList<String> leftData() {
        List<String> children = getChildrenWithWarning("/");
        ObservableList<String> objects = FXCollections.observableArrayList();
        objects.addAll(children);
        return objects;
    }

    private ObservableList<String> pathData(String path) {
        if (ZkUtils.isValidPath(path)) {
            List<String> children = getChildrenWithWarning(path);
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

       /* hb.setPadding(new Insets(0, 10, 10, 10));
        hb.setSpacing(10);
        hb.getChildren().addAll(buttonSave, buttonCancel);*/

        //中间：文本区
        Label centerLabel = new Label();
        centerLabel.setWrapText(true);
        centerLabel.setFont(Font.getDefault());

        //中间：顶部搜索框
        HBox hBox = centerBox(centerLabel);
        VBox bigCenter = new VBox();
        bigCenter.alignmentProperty().setValue(Pos.CENTER_LEFT);
        bigCenter.getChildren().addAll(hBox, centerLabel);
        anchorpane.getChildren().addAll(bigCenter, hb);

        AnchorPane.setBottomAnchor(hb, 8.0);
        AnchorPane.setRightAnchor(hb, 5.0);
        anchorpane.setPrefWidth(500);
        return anchorpane;
    }


    private HBox centerBox(Label centerLabel) {
        HBox hb = new HBox();
        hb.setPadding(new Insets(10)); // Set all sides to 10
        hb.setSpacing(8);              // Gap between nodes


        Text notice = new Text("Full path:");
        inputPath = new TextField();
        inputPath.setPromptText("full path here. like: /dubbo/xx.xx.xxxx");
//        path.setLayoutX(150);
        inputPath.setMinWidth(350);

        Button searchButton = new Button("Search !");
        searchButton.setOnMouseClicked(event -> {
            String path = inputPath.getText();
            if (ZkUtils.isValidPath(path)) {
                List<String> children = getChildrenWithWarning(path);
                centerLabel.setBackground(Background.EMPTY);
                centerLabel.setText(children.toString());
                centerLabel.setWrapText(true);
            }
        });
        hb.getChildren().addAll(notice, inputPath, searchButton);
        hb.alignmentProperty().setValue(Pos.TOP_LEFT);
        return hb;
    }


    private Node bottom() {
        Label now = new Label(LocalDateTime.now().toString());
        now.setAlignment(Pos.BOTTOM_CENTER);
        Label author = new Label();
        author.setText(" Authored by zzw.  ");
        HBox box = new HBox();
        Label empty = new Label("    ");
        box.getChildren().addAll(now, empty, author);
        box.setAlignment(Pos.BOTTOM_LEFT);
        box.setPadding(new Insets(10)); // Set all sides to 10
        box.setSpacing(8);              // Gap between nodes

        return box;
    }

    private List<String> getChildrenWithWarning(String path) {
        if (client == null) {
            client = ZkClient.newClient(zkAddress);
        }
        List<String> children = null;
        try {
            children = client.getChildren(path);
        } catch (KeeperException e) {
            Alert warning = new Alert(Alert.AlertType.WARNING);
            warning.setTitle("KeeperException.");
            warning.setContentText(e.getMessage());
            warning.show();
        }
        return children == null ? Collections.emptyList() : children;
    }


    private void disconnectClient() {
        client.disconnect();
        client = null;
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Close success.", ButtonType.CLOSE);
        alert.show();
    }

}
