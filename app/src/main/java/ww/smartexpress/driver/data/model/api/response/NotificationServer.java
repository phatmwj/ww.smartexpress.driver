package ww.smartexpress.driver.data.model.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationServer {
    private Long id;
    private String title;
    private String description;
    private String publishedDate;
    private Integer kind;
    private String banner;
}
