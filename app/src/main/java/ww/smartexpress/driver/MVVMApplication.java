package ww.smartexpress.driver;

import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ww.smartexpress.driver.constant.Constants;
import ww.smartexpress.driver.data.model.api.ApiModelUtils;
import ww.smartexpress.driver.data.model.api.response.BookingCode;
import ww.smartexpress.driver.data.model.api.response.BookingId;
import ww.smartexpress.driver.data.model.api.response.ChatMessage;
import ww.smartexpress.driver.data.model.api.response.DepositMessage;
import ww.smartexpress.driver.data.model.api.response.DepositSuccess;
import ww.smartexpress.driver.data.model.api.response.NewsNotification;
import ww.smartexpress.driver.data.model.api.response.TransactionMessage;
import ww.smartexpress.driver.data.model.other.ToastMessage;
import ww.smartexpress.driver.data.websocket.Command;
import ww.smartexpress.driver.data.websocket.Message;
import ww.smartexpress.driver.data.websocket.SocketEventModel;
import ww.smartexpress.driver.data.websocket.SocketListener;
import ww.smartexpress.driver.data.websocket.WebSocketLiveData;
import ww.smartexpress.driver.databinding.DialogNotificationBinding;
import ww.smartexpress.driver.databinding.ItemMarqueeNewsBinding;
import ww.smartexpress.driver.di.component.AppComponent;
import ww.smartexpress.driver.di.component.DaggerAppComponent;
import ww.smartexpress.driver.others.MyTimberDebugTree;
import ww.smartexpress.driver.others.MyTimberReleaseTree;
import ww.smartexpress.driver.ui.chat.ChatActivity;
import ww.smartexpress.driver.ui.home.HomeActivity;
import ww.smartexpress.driver.ui.main.MainActivity;
import ww.smartexpress.driver.ui.qrcode.QrcodeActivity;
import ww.smartexpress.driver.ui.shipping.ShippingActivity;
import ww.smartexpress.driver.ui.wallet.WalletActivity;
import ww.smartexpress.driver.utils.DialogUtils;

import es.dmoral.toasty.Toasty;
import io.reactivex.rxjava3.subjects.PublishSubject;
import lombok.Getter;
import lombok.Setter;
import timber.log.Timber;
import ww.smartexpress.driver.utils.NumberUtils;

public class MVVMApplication extends Application implements LifecycleObserver, SocketListener {
    @Setter
    @Getter
    private AppCompatActivity currentActivity;

    @Getter
    private AppComponent appComponent;
    private Boolean inBackground;

    @Getter
    private WebSocketLiveData webSocketLiveData;

    @Getter
    @Setter
    private Boolean paused = false;
    @Getter
    @Setter
    private String currentBookingId;
    @Getter
    @Setter
    private String cancelBookingId;
    @Getter
    @Setter
    private Long detailsBookingId;
    @Getter
    @Setter
    private Long deleteBookingId;
    @Getter
    @Setter
    private Long chatBookingId;
    @Getter
    @Setter
    private Map<Long, Integer> countDownTime = new HashMap<>();
    @Getter
    @Setter
    private ChatMessage chatMessage = null;
    @Getter
    @Setter
    private Boolean isDepositSuccess = false;
    @Getter
    @Setter
    private DepositMessage depositMessage = null;
    @Getter
    @Setter
    private List<Long> notificationIdList = new ArrayList<>();
    @Getter
    @Setter
    private Map<Long, Integer> roomMsgCount = new HashMap<>();

    @Getter
    @Setter
    private List<Long> newMsgBookings = new ArrayList<>();
    @Override
    public void onCreate() {
        super.onCreate();

        // Enable firebase log
        FirebaseCrashlytics firebaseCrashlytics = FirebaseCrashlytics.getInstance();
        firebaseCrashlytics.setCrashlyticsCollectionEnabled(true);


        if (BuildConfig.DEBUG) {
            Timber.plant(new MyTimberDebugTree());
        } else {
            Timber.plant(new MyTimberReleaseTree(firebaseCrashlytics));
        }

        appComponent = DaggerAppComponent.builder()
                .application(this)
                .build();
        appComponent.inject(this);

        // Init Toasty
        Toasty.Config.getInstance()
                .allowQueue(false)
                .apply();
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
//        insertMock();
//        startOrderSchedule();

        //websocket
        webSocketLiveData = WebSocketLiveData.getInstance();
        webSocketLiveData.setSocketListener(this);
        webSocketLiveData.setAppOnline(true);

//        Intent serviceIntent = new Intent(this, ForegroundService.class);
//        startService(serviceIntent);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onAppBackgrounded() {
        //App in background
        Timber.d("APP IN BACKGROUND");
        inBackground = true;
        webSocketLiveData.setAppOnline(false);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onAppForegrounded() {
        // App in foreground
        Timber.d("APP IN FOREGROUND");
        inBackground = false;
        webSocketLiveData.setAppOnline(true);
    }

    public PublishSubject<Integer> showDialogNoInternetAccess() {
        final PublishSubject<Integer> subject = PublishSubject.create();
        currentActivity.runOnUiThread(() ->
                DialogUtils.dialogConfirm(currentActivity, currentActivity.getResources().getString(R.string.newtwork_error),
                        currentActivity.getResources().getString(R.string.newtwork_error_button_retry),
                        (dialogInterface, i) -> subject.onNext(1), currentActivity.getResources().getString(R.string.newtwork_error_button_exit),
                        (dialogInterface, i) -> System.exit(0))
        );
        return subject;
    }

    public void startSocket(String token){
        webSocketLiveData.setSession(token);
        webSocketLiveData.startSocket();
    }

    public void stopSocket(){
        webSocketLiveData.stopSocket();
    }
    @Override
    public void onMessage(SocketEventModel socketEventModel) {
        if (paused){
            Timber.d("APP PAUSED, IGNORE DATA RECEIVE");
            return;
        }
        currentActivity.runOnUiThread(()->handleSocket(socketEventModel));
    }

    public void handleSocket(SocketEventModel socketEventModel){
        if(!webSocketLiveData.isAppOnline()){
            return ;
        }
        if(Objects.equals(socketEventModel.getEvent(), SocketEventModel.EVENT_MESSAGE)){
            if(socketEventModel.getMessage().getResponseCode() == 200){
                switch (socketEventModel.getMessage().getCmd()){
                    case Command.CM_CONTACT_DRIVER:
                        navigateToBooking(socketEventModel);
                        break;
                    case Command.CM_SEND_MESSAGE:
                        navigateToChat(socketEventModel);
                        break;
                    case Command.CM_CUSTOMER_CANCEL_BOOKING:
                        handleCancelBooking(socketEventModel);
                        break;
                    case Command.CM_CLIENT_RECEIVED_PUSH_NOTIFICATION:
                        handleDepositSuccess(socketEventModel);
                        break;
                    default:
                        break;
                }
            }else {
//                handleCancelBooking(socketEventModel);
            }
        }

    }
    public void navigateToBooking(SocketEventModel socketEventModel){
            Message message = socketEventModel.getMessage();
            currentBookingId = message.getDataObject(BookingId.class).getBookingId();
            countDownTime.put(Long.parseLong(currentBookingId), 30);
            Intent intent = new Intent(currentActivity,HomeActivity.class);
            intent.putExtra("activityfragment", "activity fragment");
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            currentActivity.startActivity(intent);
//        Message message = socketEventModel.getMessage();
//        currentBookingId = message.getDataObject(BookingId.class).getBookingId();
//        Intent intent = new Intent(currentActivity,HomeActivity.class);
//        intent.putExtra("homefragment", "home fragment");
//        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//        currentActivity.startActivity(intent);
    }

    public void navigateToChat(SocketEventModel socketEventModel){
        Message message = socketEventModel.getMessage();
        if(Objects.equals(message.getApp(), Constants.APP_SERVER)){
            chatMessage = message.getDataObject(ChatMessage.class);
            chatBookingId = Long.valueOf(chatMessage.getBookingId());
            if(currentActivity instanceof ChatActivity && Objects.equals(((ChatActivity) currentActivity).getBookingId(), chatBookingId)){
                Intent intent = new Intent(currentActivity, ChatActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                currentActivity.startActivity(intent);
            }else{
//                Intent intent = new Intent(currentActivity, ChatActivity.class);
//                intent.putExtra("codeBooking", chatMessage.getCodeBooking());
//                intent.putExtra("roomId", Long.valueOf(chatMessage.getRoomId()));
//                intent.putExtra("bookingId", Long.valueOf(chatMessage.getBookingId()));
//                currentActivity.startActivity(intent);
                ToastMessage toastMessage = new ToastMessage(ToastMessage.TYPE_SUCCESS, "Bạn có tin nhắn mới!");
                toastMessage.showMessage(currentActivity);
                //


                Integer i = roomMsgCount.get(chatBookingId);
                if(i != null && i!=0){
                    roomMsgCount.put(chatBookingId,i+1);
                }else {
                    roomMsgCount.put(chatBookingId, 1);
                }
//                detailsBookingId = chatBookingId;
                if(currentActivity instanceof HomeActivity){
                    Log.d("TAG", "navigateToChat: ");
                    ((HomeActivity) currentActivity).onResume();
                }
                if(currentActivity instanceof ShippingActivity && ((ShippingActivity)currentActivity).getBookingId().equals(String.valueOf(chatBookingId))){
                    Intent intent = new Intent(currentActivity, ShippingActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    currentActivity.startActivity(intent);
                }
                newMsgBookings.add(chatBookingId);
            }
        }

    }

    public void handleCancelBooking(SocketEventModel socketEventModel){
        Message message = socketEventModel.getMessage();
        cancelBookingId = message.getDataObject(BookingId.class).getBookingId();
        Intent intent;
        if(currentActivity instanceof ShippingActivity && Objects.equals(((ShippingActivity) currentActivity).getBookingId(), cancelBookingId)){
            intent = new Intent(currentActivity, ShippingActivity.class);
        }else {
            intent = new Intent(currentActivity, HomeActivity.class);
            intent.putExtra("activityfragment", "activity fragment");
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        currentActivity.startActivity(intent);
    }

    public void handleDepositSuccess(SocketEventModel socketEventModel){
        isDepositSuccess = true;
        Message message = socketEventModel.getMessage();
        DepositSuccess depositSuccess = message.getDataObject(DepositSuccess.class);
        depositMessage = ApiModelUtils.fromJson(depositSuccess.getMessage(),DepositMessage.class);
        Intent intent ;
        ToastMessage toastMessage;
        notificationIdList.add(depositMessage.getNotificationId());
        if(currentActivity instanceof HomeActivity){
            ((HomeActivity) currentActivity).onResume();
        }
        switch (depositSuccess.getKind()){
            case 1:
                if(currentActivity instanceof WalletActivity || currentActivity instanceof QrcodeActivity){
                    intent = new Intent(currentActivity, WalletActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    currentActivity.startActivity(intent);
                }
                //binding.setTitle("Bạn đã nạp thành công "+ NumberUtils.formatCurrency(Double.valueOf(depositMessage.getMoney()))+" vào ví");
                toastMessage = new ToastMessage(ToastMessage.TYPE_SUCCESS, "Bạn đã nạp thành công "+ NumberUtils.formatCurrency(Double.valueOf(depositMessage.getMoney()))+" vào ví");
                toastMessage.showMessage(currentActivity);
                break;
            case 2:
                if(currentActivity instanceof WalletActivity){
                    intent = new Intent(currentActivity, WalletActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    currentActivity.startActivity(intent);
                }
                toastMessage = new ToastMessage(ToastMessage.TYPE_SUCCESS, "Yêu cầu rút "+NumberUtils.formatCurrency(Double.valueOf(depositMessage.getMoney()))+" đã được chấp nhận");
                toastMessage.showMessage(currentActivity);
                break;
            case 3:
                toastMessage = new ToastMessage(ToastMessage.TYPE_SUCCESS, "Yêu cầu rút "+NumberUtils.formatCurrency(Double.valueOf(depositMessage.getMoney()))+" bị từ chối vì: "+depositMessage.getReason());
                toastMessage.showMessage(currentActivity);
                break;
            case 4: case 5: case 6://NOTIFICATION_KIND_SYSTEM
                toastMessage = new ToastMessage(ToastMessage.TYPE_SUCCESS, "Bạn có thông báo mới!");
                toastMessage.showMessage(currentActivity);
//                showMarqueeDialog(message);
                break;
            default:
                break;
        }



    }


    public void showMarqueeDialog(Message message) {
        Dialog marqueeDialog = new Dialog(getCurrentActivity(), R.style.FullScreenDialog);
        TransactionMessage tm = message.getDataObject(TransactionMessage.class);
        NewsNotification newsNotification = ApiModelUtils.fromJson(tm.getMessage(), NewsNotification.class);
        ItemMarqueeNewsBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(this), R.layout.item_marquee_news, null, false);
        marqueeDialog.setContentView(binding.getRoot());

        binding.setIvm(newsNotification);
        String title = "";
        switch (newsNotification.getKind()){
            case 4:
                title = "Thông báo hệ thống: " + newsNotification.getDescription();
                binding.marqueeText.setText(title);
                break;
            case 5:
                title = "Khuyến mãi: " + newsNotification.getDescription();
                binding.marqueeText.setText(title);
                break;
            case 6:
                title = "Cảnh báo: " + newsNotification.getDescription();
                binding.marqueeText.setText(title);
                break;
            default:
                break;
        }

        binding.marqueeText.setSelected(true);

        Window window = marqueeDialog.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.gravity = Gravity.TOP;
        layoutParams.y = 0;
        layoutParams.dimAmount = 0.0f;
        window.setAttributes(layoutParams);
        marqueeDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        marqueeDialog.getWindow().setGravity(Gravity.TOP);
        marqueeDialog.setCanceledOnTouchOutside(false);
        marqueeDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        marqueeDialog.show();


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                marqueeDialog.dismiss();
            }
        }, 20000); // Thời gian tính bằng mili giây (ở đây là 20 giây)

    }

    @Override
    public void onTimeout(Message message) {

    }

    @Override
    public void onError() {

    }

    @Override
    public void onClosed() {

    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onSessionExpire() {

    }

    @Override
    public void onPingFailure() {

    }

}
