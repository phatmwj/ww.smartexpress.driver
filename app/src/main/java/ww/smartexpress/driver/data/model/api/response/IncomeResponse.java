package ww.smartexpress.driver.data.model.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IncomeResponse {
    private Long driverId;
    private Double totalMoney;
    private Double totalBookingMoney;
    private int totalBookingCancel;
}
