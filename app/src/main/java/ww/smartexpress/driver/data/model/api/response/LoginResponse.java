package ww.smartexpress.driver.data.model.api.response;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class LoginResponse {

    private String access_token;

    private String refresh_token;

    private String token_type;

    private Long user_id;

    private Integer user_kind;

}
