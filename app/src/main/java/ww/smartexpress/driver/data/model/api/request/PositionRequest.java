package ww.smartexpress.driver.data.model.api.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PositionRequest {

    private int isBusy;

    private String latitude;

    private String longitude;

    private int status;

    private String timeUpdate;

}
