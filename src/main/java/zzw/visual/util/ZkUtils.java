package zzw.visual.util;

/**
 * @author zhenwei.wang
 * @date 2020/8/12
 */
public class ZkUtils {

    /**
     * host:port
     * 有效地址
     *
     * @param address
     * @return
     */
    public static boolean isValidAddress(String address) {
        if (address == null || address.isEmpty()) {
            return false;
        }
        if (!address.contains(":")) {
            return false;
        }
        if (address.split(":").length != 2) {
            return false;
        }
        return true;
    }

    /**
     * 有效路径。
     * /开头
     *
     * @param path
     * @return
     */
    public static boolean isValidPath(String path) {
        if (path == null || path.isEmpty()) {
            return false;
        }
        if (!path.startsWith("/")) {
            return false;
        }
        return true;
    }
}
