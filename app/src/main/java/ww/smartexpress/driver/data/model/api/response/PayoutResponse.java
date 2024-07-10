package ww.smartexpress.driver.data.model.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayoutResponse {
    private Long id;
    private Integer status;
    private String modifiedDate;
    private String createdDate;
    private double money;
    private Integer kind;
    private Integer state;
    private String bankCard;
    private String transactionCode;
    private String image;
    private String note;

}
