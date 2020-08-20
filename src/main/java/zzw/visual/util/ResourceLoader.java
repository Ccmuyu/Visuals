package zzw.visual.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhenwei.wang
 * @date 2020/8/20
 */
public class ResourceLoader {

    private static Logger log = LoggerFactory.getLogger(ResourceLoader.class);
    private static Map<Object, Object> props = new ConcurrentHashMap<>();

    static {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream inputStream = loader.getResourceAsStream("application.properties");
        Properties properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            log.error("ResourceLoader failed.", e);
        }
        if (!properties.isEmpty()) {
            props.putAll(properties);
        }

    }

    public static Object getProp(Object key) {
        return props.get(key);
    }

}
