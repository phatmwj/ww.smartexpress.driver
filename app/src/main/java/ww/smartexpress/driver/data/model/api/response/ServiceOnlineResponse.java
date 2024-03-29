package ww.smartexpress.driver.data.model.api.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceOnlineResponse {
    private int driverState;
    private List<ServiceOnline> content;
    private int totalElements;
    private int totalPages;
}
