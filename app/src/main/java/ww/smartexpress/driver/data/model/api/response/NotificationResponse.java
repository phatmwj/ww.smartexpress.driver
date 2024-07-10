package ww.smartexpress.driver.data.model.api.response;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.viewholders.FlexibleViewHolder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.constant.Constants;
import ww.smartexpress.driver.data.model.api.ApiModelUtils;
import ww.smartexpress.driver.databinding.ItemNotificationBinding;
import ww.smartexpress.driver.utils.DateUtils;
import ww.smartexpress.driver.utils.NumberUtils;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse extends AbstractFlexibleItem<NotificationResponse.NotificationViewHolder> {

    private String createdDate;
    private Long id;
    private Integer kind;
    private String modifiedDate;
    private String msg;
    private String refId;
    private Integer state;
    private Integer status;
    private Long userId;
    private Integer userKind;

    @Override
    public int getLayoutRes() {
        return R.layout.item_notification;
    }

    @Override
    public NotificationViewHolder createViewHolder(View view, FlexibleAdapter<IFlexible> adapter) {
        return new NotificationViewHolder(ItemNotificationBinding.bind(view), adapter);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, NotificationViewHolder holder, int position, List<Object> payloads) {
        Context context = holder.itemView.getContext();

        NotificationMessage notificationMessage = ApiModelUtils.fromJson(msg, NotificationMessage.class);
        NotificationServer notificationServer = ApiModelUtils.fromJson(msg, NotificationServer.class);
        String message = "";
        String title = "";
        switch (kind){
            case Constants.NOTIFICATION_KIND_PROMOTION:
                message = notificationServer.getDescription();
                title = "Phiếu giảm giá";
                break;
            case Constants.NOTIFICATION_KIND_WARNING:
                message = notificationServer.getDescription();
                title = "Cảnh báo";
                break;
            case Constants.NOTIFICATION_KIND_SYSTEM:
                message = notificationServer.getDescription();
                title = "Hệ thống";
                break;
            case Constants.NOTIFICATION_KIND_APPROVE_PAYOUT:
                title = "Ví Smart Express";
                message = "Yêu cầu rút "+NumberUtils.formatCurrency(notificationMessage.getMoney())+" đã được chấp nhận" ;
                break;
            case Constants.NOTIFICATION_KIND_REJECT_PAYOUT:
                title = "Ví Smart Express";
                message = "Yêu cầu rút "+NumberUtils.formatCurrency(notificationMessage.getMoney())+" bị từ chối";
                break;
            case Constants.NOTIFICATION_KIND_DEPOSIT_SUCCESSFULLY:
                title = "Ví Smart Express";
                message = "Bạn đã nạp thành công "+ NumberUtils.formatCurrency(notificationMessage.getMoney())+" vào ví";
                break;
            default:;
                break;
        }
        holder.binding.setIvm(this);
        holder.binding.setTitle(title);
        holder.binding.setMsg(message);
        holder.binding.executePendingBindings();
    }

    public class NotificationViewHolder extends FlexibleViewHolder {

        ItemNotificationBinding binding;

        public NotificationViewHolder(@NonNull ItemNotificationBinding view, FlexibleAdapter adapter) {
            super(view.getRoot(), adapter);
            this.binding = view;
        }
    }
}
