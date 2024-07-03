package ww.smartexpress.driver.data.model.api.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ForgetPasswordRequest {
    private String phone;
    private Integer kind;
}
