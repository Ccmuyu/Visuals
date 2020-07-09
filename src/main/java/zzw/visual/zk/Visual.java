package zzw.visual.zk;

import com.sun.javafx.scene.control.skin.LabeledText;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * @author zhenwei.wang
 * @description TODO
 * @date 2020/7/7
 */
public class Visual extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {

        Scene scene = new Scene(borderPane());
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image("/img/zk.gif"));
        primaryStage.setTitle("Visual.");
        primaryStage.show();
    }

    private BorderPane borderPane() {
        BorderPane pane = new BorderPane();
        pane.setTop(top());
//        pane.setLeft();
//        pane.setCenter();
//        pane.setRight();
//        pane.setBottom();
        return pane;
    }

    private Node top() {
        HBox hBox = new HBox();
        hBox.setPadding(new Insets(15, 10, 15, 10));
        hBox.setSpacing(5);

        Button ok = new Button("Ok");
        ok.setDefaultButton(true);
        ok.setPrefSize(100, 20);
        hBox.getChildren().add(ok);
      /*  Button cancel = new Button("Cancel");
        cancel.setPrefSize(100, 20);
        ok.setCancelButton(true);
        hBox.getChildren().add(cancel);*/
        TextField textField = new TextField();
        textField.setPromptText("input your ip address, then Enter!");
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

        ObservableList<Cursor> cursors = FXCollections.observableArrayList(Cursor.CLOSED_HAND, Cursor.CROSSHAIR, Cursor.DEFAULT);
        ChoiceBox<Cursor> choiceBox = new ChoiceBox<>(cursors);
        hBox.getChildren().add(choiceBox);
       /* choiceBox.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println(event.getEventType().getName());
                EventTarget target = event.getTarget();
                System.out.println(target);
                if (target instanceof Label) {
                    Label label = (Label) target;
                    String text = label.getText();
                    System.out.println("l :" + text);
                } else if (target instanceof LabeledText) {
                    LabeledText labeledText = (LabeledText) target;
                    String text = labeledText.getText();
                    System.out.println("lt:" + text);
                }

            }
        });*/
        choiceBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
//                System.out.println(String.format("%s,%s,%s", observable, oldValue, newValue));
                System.out.println(observable + "  " + oldValue + "  " + newValue);
                textField.setText(String.valueOf(newValue));
            }
        });
        choiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Cursor>() {
            @Override
            public void changed(ObservableValue<? extends Cursor> observable, Cursor oldValue, Cursor newValue) {
                System.out.println(observable + "  " + oldValue + "  " + newValue);
                textField.setText(String.valueOf(newValue));
            }
        });
        return hBox;
    }

}
