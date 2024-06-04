package ww.smartexpress.driver.data.model.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MomoPaymentResponse {

    private String partnerCode;

    private String orderId;

    private String requestId;

    private int amount;

    private long responseTime;

    private String message;

    private int resultCode;

    private String payUrl;

    private String deeplink;

    private String qrCodeUrl;

    private String applink;

    private String deeplinkMiniApp;
}