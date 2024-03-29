package ww.smartexpress.driver.data.model.api.response;

import lombok.Data;

@Data
public class ProfileResponse {
    private Long id;
    private String fullName;
    private String phone;
    private String address;
    private String avatar;
    private double averageRating;
}
