package ww.smartexpress.driver.data.model.api.response;

import lombok.Data;

@Data
public class ServiceResponse {

    private Long id;

    private String name;

    private String description;

    private String image;

    private String price;

    private Integer kind;

    private CategoryResponse category;

    private String weight;

    private String size;
}
