package zk.zk;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * @author zhenwei.wang
 * @description zk主页面
 * @date 2020/6/11
 */
public class Page extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Zk visual.");
        primaryStage.setResizable(true);
//        primaryStage.setScene(new Scene(getGridPane(), 500, 300));
        primaryStage.getIcons().add(new Image("/img/zk.gif"));
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("fxml/zk-add.fxml")));
        primaryStage.setScene(new Scene(root, 500, 300));
        primaryStage.show();
        Image image = new Image("/img/piechart.png");

    }




    @Deprecated
    private GridPane getGridPane() {
        GridPane pane = new GridPane();
        pane.setAlignment(Pos.CENTER);
        pane.setHgap(30);
        pane.setVgap(50);
        pane.setAccessibleText("qq");
        pane.setPadding(new Insets(2, 2, 2, 2));

        Button bu = new Button("1");
        bu.setOnAction(event -> {
            Object source = event.getSource();
            System.out.println(source);
            System.out.println(event.getEventType().getName());
            System.out.println("b1 click");
            System.out.println("################################");
        });
        pane.add(bu, 0, 0);

        Button bu2 = new Button("2");
        pane.add(bu2, 1, 0);
        bu2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println(((Button) event.getSource()).getText());
                System.out.println(event.getEventType().getName());
                System.out.println("b1 click");
                System.out.println("################################");
            }
        });

//        pane.add(new Button("3"), 0, 1);
//        pane.add(new Button("4"), 1, 1);
//        pane.add(new Button("5"), 0, 2);
//        pane.add(new Button("6"), 1, 2);

        TextField field = new TextField();
        field.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                System.out.println(event.getCode());
                if (event.getCode() == KeyCode.ENTER) {
                    System.out.println("----------------------");
                    System.out.println("here your input.");
                    System.out.println(event.getCharacter());
                }
            }
        });
        field.setPromptText("新增zk地址，回车键确认");
        pane.add(field, 3, 1);
        return pane;
    }
}
