package ww.smartexpress.driver.data.websocket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import lombok.Data;

@Data
public class Message {
    static Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Double.class, (JsonSerializer<Double>) (src, typeOfSrc, context) -> {
                if (src == src.longValue()) {
                    return new JsonPrimitive(src.longValue());
                }
                return new JsonPrimitive(src);
            })
            .setPrettyPrinting()
            .enableComplexMapKeySerialization()
            .create();

    private Integer platform = 1;
    private String cmd;
    private String clientVersion;
    private String lang;
    private Object data;
//    private String screen;
    private Integer responseCode;
    private String msg;
    private String token;
    private String requestId;
    private String app;
    private long timeout = Timeout.DEFAULT; // default timeout in ms
    public static class Timeout {
        public static final long NEVER = 300000;
        public static final long DEFAULT = 35000;
        private Timeout(){}
    }

    public String getPayload() {
        return GSON.toJson(this);
    }


    public <T> T getDataObject(Class<T> classOfT) {
        if (data == null) {
            return null;
        } else {
            T target = GSON.fromJson(GSON.toJsonTree(data), classOfT);
            return target;
        }
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        if (json == null || "".equals(json) ) {
            return null;
        } else {
            T target = null;
            try {
                target = GSON.fromJson(json, classOfT);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return target;
        }
    }
}
