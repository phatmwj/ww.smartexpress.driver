package ww.smartexpress.driver.data.model.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityRate {
    private int totalBookingAccept;
    private int totalBookingCancel;
    private int totalBookingDone;
    private int totalBookingReject;
}
