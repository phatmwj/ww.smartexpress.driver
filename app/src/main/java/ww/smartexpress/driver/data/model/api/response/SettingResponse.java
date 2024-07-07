package ww.smartexpress.driver.data.model.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SettingResponse {
    private String dataType;
    private String description;
    private String groupName;
    private Long id;
    private Boolean isEditable;
    private Boolean isSystem;
    private String settingKey;
    private String settingValue;
}
