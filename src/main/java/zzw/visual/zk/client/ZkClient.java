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

    private static Logger log = LoggerFactory.getLogger(ZkClient.class);
    private ZooKeeper zooKeeper;

    private String zkAddress;

    private int timeout;

    private static Map<String, ZkClient> cachedPool = new ConcurrentHashMap<>();

    public static ZkClient newClient(String zkAddress) {
        ZkClient client = cachedPool.get(zkAddress);
        if (client == null) {
            cachedPool.putIfAbsent(zkAddress, new ZkClient(zkAddress));
        }
        return cachedPool.get(zkAddress);
    }

    private ZkClient(String zkAddress) {
        this.zkAddress = zkAddress;
        this.timeout = 30000;
        doConnect();
    }

    public List<String> getChildren(String path) {
        return getChildren(path, false);
    }

    public List<String> getChildren(String path, boolean watch) {

        try {
            return zooKeeper.getChildren(path, watch);
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    private void doConnect() {
        try {
            zooKeeper = new ZooKeeper(zkAddress, timeout, watchedEvent -> {
                System.out.println("watch event:" + watchedEvent.getType()
                        + ", path:" + watchedEvent.getPath() +
                        ", state:" + watchedEvent.getState());
                log.info("init success..");
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        ZkClient client = ZkClient.newClient("mt-zookeeper-vip:2181");

        while (true) {
            System.out.println("\n");
            System.out.println("input your path.");
            byte[] b = new byte[1024];
            System.in.read(b);
            String read = new String(b);
            read = read.replaceAll("\r\n", "").trim();
            if (!read.startsWith("/")) {
                System.out.println("path must start with \"/\"");
                continue;
            }
            List<String> children = client.getChildren(read, true);
            System.out.println(children);

        }
    }
}
