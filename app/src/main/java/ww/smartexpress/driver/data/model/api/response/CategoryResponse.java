package ww.smartexpress.driver.data.model.api.response;

import lombok.Data;

@Data
public class CategoryResponse {

    private Long id;

    private Integer kind;

    private String name;

    private Integer status;

    private String image;

}
