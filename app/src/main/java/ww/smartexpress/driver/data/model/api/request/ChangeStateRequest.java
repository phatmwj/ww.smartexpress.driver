package ww.smartexpress.driver.data.model.api.request;

import lombok.Data;

@Data
public class ChangeStateRequest {

    private Long driverServiceId;

    private int newState;
}
