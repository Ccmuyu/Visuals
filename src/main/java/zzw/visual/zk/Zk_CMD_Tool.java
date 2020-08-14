package zzw.visual.zk;


import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;

import java.util.List;

public class Zk_CMD_Tool {
    // 工具信息
    private final static String TOOL_NAME = "ZooKeeper工具 ";
    private final static String TOOL_VERSION = "V0.1";
    private final static String TOOL_AUTHOR = "Jason Chen";
    private final static String TOOL_COPYRIGHT = "©2012 Jason 版权所有";

    // 相关命令编号
    private final static int ACTION_QUERY = 1;
    private final static int ACTION_CREATE = 2;
    private final static int ACTION_MODIFY = 3;
    private final static int ACTION_DELETE = 4;
    private final static int ACTION_CONFIG = 8;
    private final static int ACTION_ABOUT = 9;
    private final static int ACTION_QUIT = 0;

    // 默认配置信息
    private final static String DEFAULT_HOST = "mt-zookeeper-vip";
    private final static int DEFAULT_PORT = 2181;
    private final static int DEFAULT_TIMEOUT = 30000;

    // 当前配置信息
    private static String zkHost = DEFAULT_HOST;
    private static int zkPort = DEFAULT_PORT;
    private static int zkTimeout = DEFAULT_TIMEOUT;

    private static ZooKeeper zooKeeper = null;

    public static void main(String[] args) {
        try {
            openZk();

            StartMenu();
            while (true) {
                String command = getCommand();
                if (command == null || command.equals("")) {
                    System.out.println("\r");
                    continue;
                }
                int operate = Integer.parseInt(command);
                switch (operate) {
                    case ACTION_QUERY:
                        queryData();
                        break;
                    case ACTION_CREATE:
                        createData();
                        break;
                    case ACTION_MODIFY:
                        modifyData();
                        break;
                    case ACTION_DELETE:
                        deleteData();
                        break;
                    case ACTION_CONFIG:
                        configConnection();
                        break;
                    case ACTION_ABOUT:
                        about();
                        break;
                    case ACTION_QUIT:
                        exit();
                        break;
                    default:
                        System.out.println("没有该命令 " + operate);
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println("输入错误，错误信息如下： ");
            e.printStackTrace();
        }
    }

    // 开始菜单
    public static void StartMenu() {
        System.out.println("**********" + TOOL_NAME + " " + TOOL_VERSION + "**********");
        System.out.println(ACTION_QUERY + "、查询目录信息");
        System.out.println(ACTION_CREATE + "、创建目录节点");
        System.out.println(ACTION_MODIFY + "、修改目录节点");
        System.out.println(ACTION_DELETE + "、删除目录节点");
        System.out.println(ACTION_CONFIG + "、设置连接信息");
        System.out.println(ACTION_ABOUT + "、关于程序");
        System.out.println(ACTION_QUIT + "、退出");
        System.out.println("********************************");
    }

    // 获取输入信息
    public static String getCommand() {
        return getCommand("请输入命令", 1);
    }

    public static String getCommand(String message) {
        return getCommand(message, null, 100);
    }

    public static String getCommand(String message, int limit) {
        return getCommand(message, null, limit);
    }

    public static String getCommand(String message, String defaultValue) {
        return getCommand(message, defaultValue, 100);
    }

    public static String getCommand(String message, String defaultValue, int limit) {
        String strCommand = "";
        try {
            do {
                System.out.println();
                if (defaultValue == null) {
                    System.out.print(message + ": ");
                } else {
                    System.out.print(message + " [" + defaultValue + "]: ");
                }

                byte[] command = new byte[100];
                System.in.read(command);
                strCommand = new String(command);
                strCommand = strCommand.replaceAll("\r\n", "").trim();
                // 若存在默认值且直接输入回车，则直接使用默认值作为返回值
                if (defaultValue != null && "".equals(strCommand)) {
                    strCommand = defaultValue;
                }
            } while (strCommand.length() > limit);
        } catch (Exception e) {
            System.out.println("输入错误！！！");
        }
        return strCommand;
    }

    public static void queryData() {
        System.out.println("*****查询目录节点*****");

        String zpath = getCommand("目录节点路径", "/App");

        try {
            openZk();

            // 取出子目录节点列表
            System.out.println(zooKeeper.getChildren(zpath, true));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createData() {
        System.out.println("*****创建目录节点*****");

        String zpath = getCommand("目录节点路径", "/App");
        String zdata = getCommand("目录节点数据", zpath);
        String zacl = getCommand("目录节点权限", "0");
        String ztype = getCommand("目录节点类型", "E");

        try {
            openZk();

            zooKeeper.create(zpath, zdata.getBytes(), getAcl(zacl), getCreateMode(ztype));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void modifyData() {
        System.out.println("*****修改目录节点*****");

        String zpath = getCommand("目录节点路径", "/App");
        String zdata = getCommand("目录节点数据", zpath);

        try {
            openZk();

            zooKeeper.setData(zpath, zdata.getBytes(), -1);

            String parent = strLeftBack(zpath, "/");
            System.out.println("目录节点状态：[" + zooKeeper.exists(parent, false) + "]");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String strLeftBack(final String text, String subtext) {
        if (!hasText(text) || !hasText(subtext)) {
            return "";
        }

        int find = text.lastIndexOf(subtext);
        return (find != -1) ? text.substring(0, find) : "";
    }
    public static boolean hasText(String text) {
        return (text != null) && (!"".equals(text));
    }

    public static void deleteData() {
        System.out.println("*****删除目录节点*****");

        String zpath = getCommand("目录节点路径", "/App");
        String zversion = getCommand("目录节点路径", "-1");

        try {
            openZk();

            zooKeeper.delete(zpath, Integer.valueOf(zversion));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** 设置连接信息 */
    public static void configConnection() {
        System.out.println("*****设置连接信息*****");
        do {
            zkHost = getCommand("请输入服务器地址", zkHost);
            zkPort = Integer.valueOf(getCommand("请输入端口", "" + zkPort));
            zkTimeout = Integer.valueOf(getCommand("请输入连接超时", "" + zkTimeout));

            try {
                // 创建一个与服务器的连接
                zooKeeper = new ZooKeeper(zkHost + ":" + zkPort, zkTimeout, new Watcher() {

                    // 监控所有被触发的事件
                    public void process(WatchedEvent event) {
                        System.out.println("已经触发了[" + event.getType() + "]事件！");
                    }
                });
            } catch (Exception e) {
                zooKeeper = null;
                e.printStackTrace();
            }
        } while (zooKeeper == null);
        System.out.println(">>>测试连接成功!");
    }

    /** 关于 */
    public static void about() {
        System.out.println("************关于" + TOOL_NAME + "****************");
        System.out.println("|　　　　版本：" + TOOL_VERSION + "　　　　　　　　　　　|");
        System.out.println("|　　　　作者：" + TOOL_AUTHOR + "　　　　　　　　　　|");
        System.out.println("|　　　　　　　　　　　　　　　　　　　　|");
        System.out.println("|版本更新：　　　　　　　　　　　　　　　|");
        System.out.println("|V0.1　　　　　　　　　　　　　　　　　　|");
        System.out.println("|　　1.查询目录信息功能　　　　　　　　　|");
        System.out.println("|　　2.创建目录信息功能　　　　　　　　　|");
        System.out.println("|　　3.修改目录信息功能　　　　　　　　　|");
        System.out.println("|　　4.删除目录信息功能　　　　　　　　　|");
        System.out.println("|　　5.设置连接信息　　　　　　　　　　　|");
        System.out.println("|　　　　　　　　　　　　　　　　　　　　|");
        System.out.println("|　　　" + TOOL_COPYRIGHT + "　　　　　　|");
        System.out.println("*****************************************");
    }

    /** 退出 */
    public static void exit() {
        System.out.println("谢谢使用，再见！");
        try {
            closeZk();
            Thread.sleep(1000);
        } catch (Exception e) {
        }
        System.exit(0);
    }

    private static ZooKeeper openZk() {
        if(zooKeeper==null) {
            // 创建一个与服务器的连接
            try {
                zooKeeper = new ZooKeeper(zkHost + ":" + zkPort, zkTimeout, new Watcher() {

                    // 监控所有被触发的事件
                    public void process(WatchedEvent event) {
                        System.out.println("已经触发了[" + event.getType() + "]事件！");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return zooKeeper;
    }

    private static void closeZk() {
        if(zooKeeper!=null) {
            // 创建一个与服务器的连接
            try {
                zooKeeper.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static List<ACL> getAcl(String zacl) {
        return Ids.OPEN_ACL_UNSAFE;
    }
    private static CreateMode getCreateMode(String ztype) {
        if("P".equalsIgnoreCase(ztype)) {
            return CreateMode.PERSISTENT;
        } else if("PS".equalsIgnoreCase(ztype)) {
            return CreateMode.PERSISTENT_SEQUENTIAL;
        } else if("E".equalsIgnoreCase(ztype)) {
            return CreateMode.EPHEMERAL;
        } else{
            return CreateMode.EPHEMERAL_SEQUENTIAL;
        }
    }
}


