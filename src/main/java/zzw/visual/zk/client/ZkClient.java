package zzw.visual.zk.client;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhenwei.wang
 * @description TODO
 * @date 2020/7/8
 */
public class ZkClient {

    private static final Logger log = LoggerFactory.getLogger(ZkClient.class);
    private ZooKeeper zooKeeper;

    private final String zkAddress;

    private final int timeout;

    private static final Map<String, ZkClient> cachedPool = new ConcurrentHashMap<>();
    public static final String MAX_BUFFER_KEY = "jute.maxbuffer";
    public static final String MAX_BUFFER_VALUE = String.valueOf((1 << 20) * 10);

    public static ZkClient newClient(String zkAddress) {
        ZkClient client = cachedPool.get(zkAddress);
        if (client == null) {
            cachedPool.putIfAbsent(zkAddress, new ZkClient(zkAddress));
        }
        return cachedPool.get(zkAddress);
    }

    public void disconnect() {
        try {
            zooKeeper.close();
            cachedPool.remove(zkAddress);
        } catch (InterruptedException e) {
            log.error("", e);
        }
    }

    private ZkClient(String zkAddress) {
        this.zkAddress = zkAddress;
        this.timeout = 30000;
        checkZKMaxBuffer();
        doConnect();
    }

    private void checkZKMaxBuffer() {
        String bufferSize = System.getProperty(MAX_BUFFER_KEY);
        if (bufferSize == null) {
            System.setProperty(MAX_BUFFER_KEY, MAX_BUFFER_VALUE);
        }
    }


    public List<String> getChildren(String path) throws KeeperException {
        return getChildren(path, false);
    }

    public List<String> getChildren(String path, boolean watch) throws KeeperException {

        try {
            return zooKeeper.getChildren(path, watch);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    private void doConnect() {
        try {
            zooKeeper = new ZooKeeper(zkAddress, timeout, watchedEvent -> {
                log.info("watch event:" + watchedEvent.getType()
                        + ", path:" + watchedEvent.getPath() +
                        ", state:" + watchedEvent.getState());
                log.info("init success..");
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
