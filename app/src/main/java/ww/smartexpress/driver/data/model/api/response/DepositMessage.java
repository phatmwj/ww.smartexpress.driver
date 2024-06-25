package ww.smartexpress.driver.data.model.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepositMessage {
    private Long notificationId;
    private Long transactionId;
    private int money;
    private String transactionCode;
    private String image;
    private String reason;
}
