package ww.smartexpress.driver.data.model.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsResponse {
    private Long id;
    private Integer status;
    private String modifiedDate;
    private String createdDate;
    private String title;
    private String description;
    private String content;
    private Integer state;
    private Integer kind;
    private Integer creatorId;
    private String publishedDate;
    private Integer target;
    private String banner;
}
