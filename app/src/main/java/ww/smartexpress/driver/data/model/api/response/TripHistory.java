package ww.smartexpress.driver.data.model.api.response;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TripHistory {
    private String id;
    private Date date;
    private Double price;
    private String origin;
    private String destination;
    private Boolean status;
}
