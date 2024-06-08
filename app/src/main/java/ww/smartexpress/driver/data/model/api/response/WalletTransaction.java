package ww.smartexpress.driver.data.model.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletTransaction {
    private int money;
    private int kind;
    private int state;
    private String createdDate;
    private int paymentKind;
    private String paymentInfo;
}
