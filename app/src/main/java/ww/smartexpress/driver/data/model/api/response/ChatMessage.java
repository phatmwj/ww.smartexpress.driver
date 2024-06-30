package ww.smartexpress.driver.data.model.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private String codeBooking;
    private String messageId;
    private String message;
    private String bookingId;
    private String roomId;
    private String avatar;
}
