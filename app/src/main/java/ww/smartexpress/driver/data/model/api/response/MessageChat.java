package ww.smartexpress.driver.data.model.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageChat {
    private Long id;
    private Room room;
    private int state;
    private String msg;
    private String timeSend;
    private Long sender;
    private Long receiver;
    private String senderAvatar;
}
