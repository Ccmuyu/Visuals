package zzw.visual.zk;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.json.JSON;
import com.alibaba.dubbo.common.utils.UrlUtils;
import org.I0Itec.zkclient.ZkClient;
import zzw.visual.util.Joiner;

import java.io.IOException;
import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.alibaba.dubbo.common.utils.Assert.notNull;

/**
 * @author zhenwei.wang
 * @date 2020/6/11
 * 检查在zk上注册的dubbo接口
 */
public class DubboServiceOnZk {

    private URL CONSUMER_URL;

    private static final Joiner j = Joiner.on("|").useForNull("nil");


    public final List<String> getIpList() {
        return ipList;
    }

    private volatile List<String> ipList = new ArrayList<>();


    private static List<URL> toURLs(URL consumer, List<String> providers) {

        List<URL> urls = new ArrayList<>();

        if (providers != null && providers.size() > 0) {
            urls = providers.stream()
                    .map(URL::decode)
                    .filter(provider -> provider.contains("://"))
                    .map(URL::valueOf)
                    .filter(url -> UrlUtils.isMatch(consumer, url))
                    .collect(Collectors.toList());
        }
        return urls;

    }

    // 解析服务提供者地址列表为ip:port格式
    private void parseIpList(List<String> ipSet) {
        for (String s : ipSet) {
            String decode = URLDecoder.decode(s);
            System.out.println(decode);
        }
        System.out.println("==============");
        List<URL> urlList = toURLs(CONSUMER_URL, ipSet);
        this.ipList = urlList.stream().map(URL::getAddress).collect(Collectors.toList());
    }


    public void init(String zkServerAddr, String zkGroup, String interfaces, String serviceGroup, String version) {
        notNull(zkServerAddr, "zkServerAddr is null.");
        notNull(interfaces, "dataId is null.");
        notNull(interfaces, "zkGroup is null.");
        notNull(interfaces, "serviceGroup is null.");

       /* String[] temp = interfaces.split(":");
        if (temp.length != 2) {
            throw new RuntimeException("dataId is illegal");
        }*/
        String dataId = "/" + zkGroup + "/" + interfaces + "/providers";

        String consumeUrl = "consumer://127.0.0.1/?group=" + serviceGroup + "&interface=" + interfaces + "&version=" + version;
        CONSUMER_URL = URL.valueOf(consumeUrl);
        System.out.println(j.join("init zk ", zkServerAddr, dataId, consumeUrl));

        ZkClient zkClient = new ZkClient(zkServerAddr);

        List<String> list = zkClient.subscribeChildChanges(dataId, (parentPath, children) -> {
            parseIpList(children);
            try {
                System.out.println((j.join("ipList changed:", JSON.json(ipList))));
                System.out.println(LocalDateTime.now());
            } catch (IOException ignored) {
            }
        });
        parseIpList(list);
    }


    public static void main(String[] a) throws InterruptedException {
        DubboServiceOnZk zk = new DubboServiceOnZk();
        String dev = "zookeeper0.dev.base.epayjd:2181";
        zk.init(dev,"devjd",
                //dubbo接口
                "",
                "","1.0.0");
        String targetIp = "10.177.0.238";

        try {
            System.out.println((j.join("parseIpList", JSON.json(zk.getIpList()))));
            zk.getIpList().stream().filter(e->e.contains(targetIp)).findAny().ifPresent((ip)->{
                System.out.println("TargetIp: " + ip);
            });
        } catch (IOException ignored) {

        }
        Thread.currentThread().join();

    }
}
