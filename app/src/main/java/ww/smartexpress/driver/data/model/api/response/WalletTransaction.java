package ww.smartexpress.driver.data.model.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletTransaction {
    private Long id;
    private double money;
    private Integer kind;
    private Integer state;
    private Integer status;
    private String createdDate;
    private int paymentKind;
    private String paymentInfo;
    private CurrentBooking booking;
}
