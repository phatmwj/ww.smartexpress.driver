package ww.smartexpress.driver.data.model.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseGeneric {

    private String code;

    private String data;

    private String message;

    private boolean result;

}
