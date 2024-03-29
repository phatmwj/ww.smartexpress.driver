package ww.smartexpress.driver.data.websocket;

import lombok.Data;

@Data
public class SocketEventModel {
    public static final String EVENT_ONLINE = "online";
    public static final String EVENT_OFFLINE = "offline";
    public static final String EVENT_ERROR = "error";
    public static final String EVENT_MESSAGE = "message";

    private String event = EVENT_MESSAGE;
    private Message message;


    public SocketEventModel(Message message) {
        this.message = message;
    }

}
