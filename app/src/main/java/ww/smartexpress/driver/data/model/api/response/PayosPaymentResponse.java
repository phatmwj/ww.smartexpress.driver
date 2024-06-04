package ww.smartexpress.driver.data.model.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayosPaymentResponse {
        private String code;
        private String desc ;
        private DataPayment data;
        private String signature;
}
