package ww.smartexpress.driver.data.model.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayoutTransaction {
    private Long id;
    private Integer money;
    private Integer kind;
    private Integer status;
    private Integer state;
    private String createdDate;
    private String bankCard;
}
