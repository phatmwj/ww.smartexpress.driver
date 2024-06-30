package ww.smartexpress.driver.ui.notification.details;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;

import androidx.annotation.Nullable;
import androidx.databinding.Observable;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ww.smartexpress.driver.BR;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.data.model.api.ApiModelUtils;
import ww.smartexpress.driver.data.model.api.request.PayoutRequest;
import ww.smartexpress.driver.data.model.api.response.NotificationServer;
import ww.smartexpress.driver.databinding.ActivityNotificationDetailsBinding;
import ww.smartexpress.driver.di.component.ActivityComponent;
import ww.smartexpress.driver.ui.base.activity.BaseActivity;

public class NotificationDetailsActivity extends BaseActivity<ActivityNotificationDetailsBinding, NotificationDetailsViewModel> {
    @Override
    public int getLayoutId() {
        return R.layout.activity_notification_details;
    }

    @Override
    public int getBindingVariable() {
        return BR.vm;
    }

    @Override
    public void performDependencyInjection(ActivityComponent buildComponent) {
        buildComponent.inject(this);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        viewModel.notification.postValue(ApiModelUtils.fromJson(intent.getStringExtra("messageNoti"), NotificationServer.class));


//        String htmlContent = "<h1>GrabCar | Thưởng 10.000đ/chuyến xe GrabCar có điểm đón/trả khách tại Lễ hội pháo hoa Quốc tế Đà Nẵng vào các ngày 22, 29/6/2024 và 13/7/2024</h1><p><strong style=\"background-color: transparent; color: rgb(0, 177, 79);\">Quý Đối tác thân mến,</strong></p><p><strong style=\"background-color: transparent; color: rgb(0, 177, 79);\">&nbsp;</strong></p><p><span style=\"color: rgb(0, 0, 0);\">Nhằm mang lại cơ hội gia tăng thu nhập khi Đối tác thực hiện chuyến xe, Grab triển khai chương trình&nbsp;</span><strong style=\"color: rgb(0, 177, 79);\">thưởng khi hoàn thành chuyến xe GrabCar có điểm đón/trả khách tại Lễ hội pháo hoa</strong>&nbsp;<strong style=\"color: rgb(0, 177, 79);\">Đà Nẵng</strong><span style=\"color: rgb(0, 0, 0);\">, cụ thể như sau:</span></p><p style=\"text-align: center;\"><img src=\"https://assets.grab.com/wp-content/uploads/sites/11/2024/06/24173453/thu__o____ng_pha__o_hoa___a___na____ng_4w_social.png\"></p><p><strong style=\"background-color: transparent; color: rgb(0, 0, 0);\">LƯU Ý:</strong></p><ul><li><span style=\"background-color: transparent;\">Tiền thưởng sẽ được tự động thanh toán vào&nbsp;</span><strong style=\"background-color: transparent; color: rgb(0, 177, 79);\">Ví Tiền mặt&nbsp;</strong><span style=\"background-color: transparent;\">của Đối tác&nbsp;</span><strong style=\"background-color: transparent; color: rgb(0, 177, 79);\">ngay sau khi hoàn thành chuyến xe.</strong></li><li><span style=\"background-color: transparent;\">Chuyến xe được tính là&nbsp;</span></li><li><strong style=\"background-color: transparent; color: rgb(0, 177, 79);\">hợp lệ khi đạt đầy đủ điều kiện</strong><span style=\"background-color: transparent;\">&nbsp;của chương trình và:</span></li><li class=\"ql-indent-1\"><span style=\"background-color: transparent;\">Có điểm đón/trả khách tại khu vực áp dụng.</span></li><li class=\"ql-indent-1\"><span style=\"background-color: transparent;\">Thời điểm nhận cuốc trong khung giờ và thời gian áp dụng.</span></li><li class=\"ql-indent-1\"><span style=\"background-color: transparent;\">Chỉ áp dụng đối với chuyến xe hoàn thành.</span></li><li><span style=\"background-color: transparent;\">Các chuyến xe sử dụng tính năng&nbsp;</span><strong style=\"background-color: transparent;\">Điểm đến yêu thích</strong><span style=\"background-color: transparent;\">&nbsp;được tính vào các chương trình này.</span></li><li><span style=\"background-color: transparent;\">Áp dụng&nbsp;</span><strong style=\"background-color: transparent; color: rgb(0, 177, 79);\">đồng thời&nbsp;</strong><span style=\"background-color: transparent;\">với các chương trình thưởng khác.</span></li><li><span style=\"background-color: transparent;\">Điều kiện nhận thưởng của chương trình có thể thay đổi tùy theo quyết định của Grab vào từng thời điểm cụ thể và sẽ được thông báo đến Đối tác trước khi áp dụng.</span></li></ul><p><br></p><ul><li>Các khoản thu<span style=\"background-color: transparent;\">ế phát sinh (nếu có) sẽ được Grab khấu trừ trước khi chuyển tiền thưởng cho Đối tác theo quy định của pháp luật.</span></li></ul><p><br></p><ul><li>Trong trường<span style=\"background-color: transparent;\"> hợp Đối tác có bất kỳ hành vi nào vi phạm quy định, chính sách Grab, các quy định pháp luật, hoặc Grab có đủ cơ sở để xác minh Đối tác có hành vi gian lận hoặc bị nghi ngờ hành vi đó là nhằm mục đích gian lận, trục lợi từ chính sách thưởng này, Đối tác sẽ không được tiếp tục tham gia chương trình thưởng này, có thể bị xử lý theo quy định tại Bộ Quy tắc ứng xử và Grab có toàn quyền truy thu số tiền thưởng đã thanh toán trước đó.</span></li></ul><p><br></p><ul><li>Các điều ki<span style=\"background-color: transparent;\">ện của chương trình có thể thay đổi và sẽ được thông báo đến Đối tác, Grab có toàn quyền quyết định điều kiện và mức thưởng cuối cùng.</span></li></ul><p><br></p><p>(*)</p><ul><li>&nbsp;Khu vực <span style=\"background-color: transparent;\">áp dụng có điểm đón và trả khách nằm rìa khu vực các tuyến đường cấm, Đối tác tham khảo&nbsp;</span></li></ul><p>TẠI ĐÂY</p><p>.</p><p><br></p><ul><li>Chuyến<span style=\"background-color: transparent;\"> xe được áp dụng chương trình thưởng trên là chuyến xe có&nbsp;</span></li><li>hiển <strong style=\"background-color: transparent; color: rgb(0, 177, 79);\">thị tiền thưởng&nbsp;</strong></li><li>ở mà<span style=\"background-color: transparent;\">n hình nhận cuốc.</span></li></ul><p><br></p><p>Mọi<span style=\"background-color: transparent; color: rgb(0, 0, 0);\"> thắc mắc Đối tác vui lòng truy cập mục&nbsp;</span></p><p>Tr<strong style=\"background-color: transparent; color: rgb(0, 177, 79);\">ợ Giúp</strong></p><p>&nbsp;<span style=\"background-color: transparent; color: rgb(0, 0, 0);\">trực tiếp trên ứng dụng Grab Driver để được hỗ trợ.</span></p><p><br></p><p><span style=\"background-color: transparent; color: rgb(0, 0, 0);\">&nbsp;</span></p><p><span style=\"background-color: transparent; color: rgb(0, 0, 0);\">Mến chúc Đối tác hoạt động hiệu quả và an toàn.</span></p>";
//
//        String htmlContentWithCss = addCssToHtmlContent(htmlContent);
        WebSettings webSettings = viewBinding.webview.getSettings();
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        webSettings.setJavaScriptEnabled(true);

        viewBinding.webview.setHorizontalScrollBarEnabled(false);

        viewBinding.webview.setOnTouchListener(new View.OnTouchListener() {
            float m_downX;
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getPointerCount() > 1) {
                    //Multi touch detected
                    return true;
                }

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        // save the x
                        m_downX = event.getX();
                        break;
                    }
                    case MotionEvent.ACTION_MOVE:
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP: {
                        // set x so that it doesn't move
                        event.setLocation(m_downX, event.getY());
                        break;
                    }

                }
                return false;
            }
        });

        viewModel.notification.observe(this, noti -> {
            getNews(noti.getId());
        });

//        viewBinding.webview.loadData(htmlContentWithCss, "text/html", "UTF-8");

    }

    private void getNews(Long id){
        viewModel.showLoading();
        viewModel.compositeDisposable.add(viewModel.getNews(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if(response.isResult()){
//                        viewModel.showSuccessMessage(response.getMessage());
                        viewModel.news.set(response.getData());
                        String htmlContentWithCss = addCssToHtmlContent(response.getData().getContent());
                        viewBinding.webview.loadData(htmlContentWithCss, "text/html", "UTF-8");
                    }else {
                        viewModel.showErrorMessage(response.getMessage());
                    }
                    viewModel.hideLoading();
                },error->{
                    viewModel.hideLoading();
                    viewModel.showErrorMessage(application.getString(R.string.newtwork_error));
                    error.printStackTrace();
                })
        );
    }

    private String addCssToHtmlContent(String htmlContent) {
        String css = "<style>" +
                "img { max-width: 100%; object-fit: cover; }" +
                "</style>";

        String headTag = "<head>";
        int headIndex = htmlContent.indexOf(headTag) + headTag.length();

        // Chèn CSS ngay sau thẻ <head>
        return htmlContent.substring(0, headIndex) + css + htmlContent.substring(headIndex);
    }
}
