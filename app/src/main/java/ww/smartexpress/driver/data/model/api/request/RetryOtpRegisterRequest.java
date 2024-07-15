package ww.smartexpress.driver.data.model.api.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ww.smartexpress.driver.data.model.api.response.BaseBooking;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RetryOtpRegisterRequest extends BaseBooking {
    private String phone;
}
