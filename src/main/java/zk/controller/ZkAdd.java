package zk.controller;

import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author zhenwei.wang
 * @description TODO
 * @date 2020/6/30
 */
public class ZkAdd {

    @FXML
    Button zkAdd;

    private static Set<String> zkTable = new ConcurrentSkipListSet<>();

    @FXML
    public void addZk(MouseEvent event) {
        System.out.println("hi");
        PickResult pickResult = event.getPickResult();
        String name = event.getEventType().getName();
        EventTarget target = event.getTarget();
        zkTable.add("");
    }
}
