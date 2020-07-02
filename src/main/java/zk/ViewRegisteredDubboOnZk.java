package zk;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.json.JSON;
import com.alibaba.dubbo.common.utils.UrlUtils;
import org.I0Itec.zkclient.ZkClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.alibaba.dubbo.common.utils.Assert.notNull;

/**
 * @author zhenwei.wang
 * @date 2020/6/11
 * 检查在zk上注册的dubbo接口
 */
public class ViewRegisteredDubboOnZk {

    private String dataId = null;

    private URL CONSUMER_URL;

    private static final Joiner j = Joiner.on("|").useForNull("nil");


    public final List<String> getIpList() {
        return ipList;
    }

    private volatile List<String> ipList = new ArrayList<>();


    private static List<URL> toURLs(URL consumer, List<String> providers) {

        List<URL> urls = new ArrayList<>();

        if (providers != null && providers.size() > 0) {
            urls = providers.stream().map(URL::decode)
                    .filter(provider -> provider.contains("://"))
                    .map(URL::valueOf)
                    .filter(url -> UrlUtils.isMatch(consumer, url))
                    .collect(Collectors.toList());
        }
        return urls;

    }

    // 解析服务提供者地址列表为ip:port格式
    private void parseIpList(List<String> ipSet) {
        List<URL> urlList = toURLs(CONSUMER_URL, ipSet);
        final List<String> ipListTemp = urlList.stream().map(URL::getAddress).collect(Collectors.toList());
        this.ipList = ipListTemp;
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
        this.dataId = "/" + zkGroup + "/" + interfaces + "/providers";

        String consumeUrl = "consumer://127.0.0.1/?group=" + serviceGroup + "&interface=" + interfaces + "&version=" + version;
        CONSUMER_URL = URL.valueOf(consumeUrl);
        System.out.println(j.join("init zk ", zkServerAddr, this.dataId, consumeUrl));

        ZkClient zkClient = new ZkClient(zkServerAddr);

        List<String> list = zkClient.subscribeChildChanges(this.dataId, (parentPath, children) -> {
            parseIpList(children);
            try {
                System.out.println((j.join("ipList changed:", JSON.json(ipList))));
            } catch (IOException e) {
            }
        });
        parseIpList(list);
    }


    public static void main(String[] a) throws InterruptedException {
        ViewRegisteredDubboOnZk zk = new ViewRegisteredDubboOnZk();
        zk.init("mt-zookeeper-vip:2181",
                "dubbo",
                "tf56.payOnlineFacade.facade.PaymentApplyFacadeService",
                "payOnline",
                "1.0.0");
        try {
            System.out.println((j.join("parseIpList", JSON.json(zk.getIpList()))));
        } catch (IOException e) {

        }
        Thread.currentThread().join();

    }
}
