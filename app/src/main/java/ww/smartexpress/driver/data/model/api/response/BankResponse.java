package ww.smartexpress.driver.data.model.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankResponse {
    private int id;
    private String name;
    private String code;
    private String bin;
    private String logo;
    private int transferSupported;
    private int lookupSupported;
    private String short_name;
    private int support;
    private int isTransfer;
    private String swift_code;

}
