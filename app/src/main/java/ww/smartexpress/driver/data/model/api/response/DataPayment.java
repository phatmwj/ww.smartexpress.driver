package ww.smartexpress.driver.data.model.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataPayment {
        private String accountNumber;
        private String accountName;
        private Integer amount;
        private String description;
        private String orderCode;
        private String currency;
        private String paymentLinkId;
        private String status;
        private String checkoutUrl;
        private String qrCode;
        private String expiredAt;
}
