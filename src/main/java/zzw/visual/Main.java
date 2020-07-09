package zzw.visual;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws Exception{
        /*Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();*/

        Scene scene = new Scene(borderPane(primaryStage));
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image("/img/zk.gif"));
        primaryStage.setTitle("Visual.");
        primaryStage.show();
    }

    private BorderPane borderPane(Stage primaryStage) {
        BorderPane pane = new BorderPane();
//        pane.setTop(top());
        pane.setTop(menuBar(primaryStage));
//        pane.setLeft();
//        pane.setCenter();
//        pane.setRight();
//        pane.setBottom();

        return pane;
    }

    private MenuBar menuBar(Stage primaryStage) {
        MenuBar bar = new MenuBar();
        bar.getMenus().add(mainMenu());
        bar.getMenus().add(zookeeper());
        bar.getMenus().add(helpMenu());
//        bar.prefWidthProperty().bind(primaryStage.widthProperty());
        return bar;
    }

    private Menu mainMenu() {
        Menu menu = new Menu("Main");
        MenuItem zookeeper = new MenuItem("New");
        menu.getItems().add(zookeeper);
        MenuItem exit = new Menu("exit");
        exit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println(event.getEventType());
                System.out.println(event.getSource());
                Platform.exit();
            }
        });
        menu.getItems().add(exit);
        return menu;
    }

    private Menu helpMenu() {
        Menu help = new Menu("Help");
        MenuItem about = new MenuItem("About");
        help.getItems().add(about);
        return help;
    }

    private Menu zookeeper() {
        Menu zk = new Menu("Zookeeper");
        ObservableList<MenuItem> items = zk.getItems();
        items.add(new MenuItem("1"));
        items.add(new MenuItem("2"));
        Menu second = new Menu("3");
        second.getItems().add(new CheckMenuItem("a"));
        second.getItems().add(new CheckMenuItem("b"));
        second.getItems().add(new CheckMenuItem("c"));
        items.add(second);
        return zk;
    }

    private Node left() {

        return null;
    }
}
