package zzw.visual.menu;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.util.Map;
import java.util.Properties;

/**
 * @author zhenwei.wang
 * @description TODO
 * @date 2020/7/8
 */
public class Dir extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public static TreeItem<File> createNode(final File f) {
        return new TreeItem<File>(f) {
            private boolean isLeaf;
            private boolean isFirstTimeChildren = true;
            private boolean isFirstTimeLeaf = true;

            @Override
            public ObservableList<TreeItem<File>> getChildren() {
                if (isFirstTimeChildren) {
                    isFirstTimeChildren = false;
                    super.getChildren().setAll(buildChildren(this));
                }
                return super.getChildren();
            }

            @Override
            public boolean isLeaf() {
                if (isFirstTimeLeaf) {
                    isFirstTimeLeaf = false;
                    File f = getValue();
                    isLeaf = f.isFile();
                }
                return isLeaf;
            }

            private ObservableList<TreeItem<File>> buildChildren(
                    TreeItem<File> treeItem) {
                File f = treeItem.getValue();
                if (f == null) {
                    return FXCollections.emptyObservableList();
                }
                if (f.isFile()) {
                    return FXCollections.emptyObservableList();
                }
                File[] files = f.listFiles();
                if (files != null) {
                    ObservableList<javafx.scene.control.TreeItem<File>> children = FXCollections
                            .observableArrayList();
                    for (File childFile : files) {
                        children.add(createNode(childFile));
                    }
                    return children;
                }
                return FXCollections.emptyObservableList();
            }
        };
    }

    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(new Group(), 300, 600);
        VBox vbox = new VBox();
        Properties properties = System.getProperties();
        System.out.println(properties);
        TreeItem<File> root = createNode(new File(getRootPath()));
        TreeView treeView = new TreeView<File>(root);
        vbox.getChildren().add(treeView);
        ((Group) scene.getRoot()).getChildren().add(vbox);
        stage.setScene(scene);
        stage.show();
    }

    private String getRootPath() {
        String property = System.getProperty("os.name");
        if (property.startsWith("Mac")) {
            return "/";
        } else {
            return "C:/";
        }
    }
}
