package ww.smartexpress.driver.data.model.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankCard {
    private String bankName;
    private String accountNumber;
    private String accountName;
    private String branch;
}
