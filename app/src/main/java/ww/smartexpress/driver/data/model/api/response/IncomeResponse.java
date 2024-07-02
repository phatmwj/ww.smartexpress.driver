package ww.smartexpress.driver.data.model.api.response;

import java.util.List;

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
    private int totalBooking;
    List<Double> totalMoneyOfDays;
}
