package ww.smartexpress.driver.data.model.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Size {
    private int height;
    private int length;
    private int width;
    public String formatSize(){
        return this.length+"x"+width+"x"+height+"m";
    }
}
