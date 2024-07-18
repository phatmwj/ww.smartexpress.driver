package ww.smartexpress.driver;

import android.app.Application;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
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
import ww.smartexpress.driver.ui.notification.details.NotificationDetailsActivity;
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
            createNotification("Có đơn hàng mới","Đơn hàng sẽ đóng sau 30s, mau nhanh nhận ngay nào!", intent);
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
                Intent intent = new Intent(currentActivity, ChatActivity.class);
                intent.putExtra("codeBooking", chatMessage.getCodeBooking());
                intent.putExtra("roomId", Long.valueOf(chatMessage.getRoomId()));
                intent.putExtra("bookingId", Long.valueOf(chatMessage.getBookingId()));
//                currentActivity.startActivity(intent);
//                ToastMessage toastMessage = new ToastMessage(ToastMessage.TYPE_SUCCESS, "Bạn có tin nhắn mới!");
//                toastMessage.showMessage(currentActivity);
                //


                Integer i = roomMsgCount.get(chatBookingId);
                if(i != null && i!=0){
                    roomMsgCount.put(chatBookingId,i+1);
                }else {
                    roomMsgCount.put(chatBookingId, 1);
                }
                newMsgBookings.add(chatBookingId);
//                detailsBookingId = chatBookingId;
                if(currentActivity instanceof HomeActivity){
                    Log.d("TAG", "navigateToChat: ");
                    ((HomeActivity) currentActivity).onResume();
                }
                if(currentActivity instanceof ShippingActivity && ((ShippingActivity)currentActivity).getBookingId().equals(String.valueOf(chatBookingId))){
                    Intent intent1 = new Intent(currentActivity, ShippingActivity.class);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    currentActivity.startActivity(intent);
                }

                createNotification("Tin nhắn từ đơn hàng: "+chatMessage.getCodeBooking(),chatMessage.getMessage(),intent);
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

        createNotification("Đơn hàng đã hủy","Bạn có 1 đơn hàng đã bị hủy bởi khách hàng.",intent);
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
                intent = new Intent(currentActivity, WalletActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                if(currentActivity instanceof WalletActivity || currentActivity instanceof QrcodeActivity){
                    currentActivity.startActivity(intent);
                }
                //binding.setTitle("Bạn đã nạp thành công "+ NumberUtils.formatCurrency(Double.valueOf(depositMessage.getMoney()))+" vào ví");
//                toastMessage = new ToastMessage(ToastMessage.TYPE_SUCCESS, "Bạn đã nạp thành công "+ NumberUtils.formatCurrency(Double.valueOf(depositMessage.getMoney()))+" vào ví");
//                toastMessage.showMessage(currentActivity);
                createNotification("Nạp tiền vào ví","Bạn đã nạp thành công "+ NumberUtils.formatCurrency(Double.valueOf(depositMessage.getMoney()))+" vào ví",intent);
                break;
            case 2:
                intent = new Intent(currentActivity, WalletActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                if(currentActivity instanceof WalletActivity){
                    currentActivity.startActivity(intent);
                }
                createNotification("Yêu cầu rút tiền","Yêu cầu rút "+NumberUtils.formatCurrency(Double.valueOf(depositMessage.getMoney()))+" đã được chấp nhận",intent);
                break;
            case 3:
                toastMessage = new ToastMessage(ToastMessage.TYPE_SUCCESS, "Yêu cầu rút "+NumberUtils.formatCurrency(Double.valueOf(depositMessage.getMoney()))+" bị từ chối vì: "+depositMessage.getReason());
                toastMessage.showMessage(currentActivity);
                createNotification("Yêu cầu rút tiền","Yêu cầu rút "+NumberUtils.formatCurrency(Double.valueOf(depositMessage.getMoney()))+" bị từ chối vì: "+depositMessage.getReason(),null);
                break;
            case 4: case 5: case 6://NOTIFICATION_KIND_SYSTEM
                createNotification("Smart Express","Bạn có thông báo mới!",null);
                break;
            default:
                break;
        }
    }

    public void createNotification(String title,String content, Intent notificationIntent){
        String id = "SmartExpress";

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = manager.getNotificationChannel(id);
            if(channel == null){
                channel = new NotificationChannel(id, "Channel Title", NotificationManager.IMPORTANCE_HIGH);

                channel.setDescription("[Channel Description]");
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{100,1000,200,340});
                channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                manager.createNotificationChannel(channel);
            }
        }
        if (notificationIntent == null) {
            notificationIntent = new Intent(this, HomeActivity.class);
        }

        PendingIntent contentIntent = PendingIntent.getActivity(getCurrentActivity(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, id)
                .setSmallIcon(R.mipmap.ic_launcher)
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.allwin_logo))
//                .setStyle(new NotificationCompat.BigPictureStyle()
//                        .bigLargeIcon(null)
//                )
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[]{100,1000,200,340})
                .setAutoCancel(false)
                .setTicker("Notification");

        builder.setContentIntent(contentIntent);

        NotificationManagerCompat m = NotificationManagerCompat.from(getApplicationContext());
        m.notify(0, builder.build());

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
