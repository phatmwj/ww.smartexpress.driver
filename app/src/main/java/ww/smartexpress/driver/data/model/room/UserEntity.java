package ww.smartexpress.driver.data.model.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
@Entity(tableName = "user"
//        ,foreignKeys = @ForeignKey(entity = OrderEntity.class,parentColumns = "id",childColumns = "user_id",onDelete = CASCADE)
)
public class UserEntity {
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    private long userId;

    private String fullName;

    private String phone;

    private String avatar;

    private String address;

    private double averageRating;

    private String bankCard;

    private String identificationCard;

}
