package ww.smartexpress.driver.data.model.api;

import java.util.List;

import lombok.Data;

@Data
public class ResponseListObj<T> {
    private List<T> content;
    private Integer page;
    private Integer totalPages;
    private Long totalElements;
    private Long totalUnread;
}
