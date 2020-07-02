package zk.serialize;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;

/**
 * @author zhenwei.wang
 * @description TODO
 * @date 2020/6/30
 */
public class Json {

    private static final Logger log = LoggerFactory.getLogger(Json.class);
    private static ObjectMapper mapper;
    public static final String date_pattern = "yyyy-MM-dd HH:mm:ss";
    static {
        mapper = new ObjectMapper();
        mapper.setDateFormat(new SimpleDateFormat(date_pattern));

//        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }


    public static <T> String writeString(T t) {
        try {
            return mapper.writeValueAsString(t);
        } catch (JsonProcessingException e) {
            log.error("json error..",e);
        }
        return null;
    }

}
