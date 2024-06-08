package ww.smartexpress.driver;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import ww.smartexpress.driver.constant.Constants;
import ww.smartexpress.driver.data.model.api.response.BookingCode;
import ww.smartexpress.driver.data.model.api.response.BookingId;
import ww.smartexpress.driver.data.model.api.response.ChatMessage;
import ww.smartexpress.driver.data.websocket.Command;
import ww.smartexpress.driver.data.websocket.Message;
import ww.smartexpress.driver.data.websocket.SocketEventModel;
import ww.smartexpress.driver.data.websocket.SocketListener;
import ww.smartexpress.driver.data.websocket.WebSocketLiveData;
import ww.smartexpress.driver.di.component.AppComponent;
import ww.smartexpress.driver.di.component.DaggerAppComponent;
import ww.smartexpress.driver.others.MyTimberDebugTree;
import ww.smartexpress.driver.others.MyTimberReleaseTree;
import ww.smartexpress.driver.ui.chat.ChatActivity;
import ww.smartexpress.driver.ui.home.HomeActivity;
import ww.smartexpress.driver.ui.main.MainActivity;
import ww.smartexpress.driver.ui.shipping.ShippingActivity;
import ww.smartexpress.driver.utils.DialogUtils;

import es.dmoral.toasty.Toasty;
import io.reactivex.rxjava3.subjects.PublishSubject;
import lombok.Getter;
import lombok.Setter;
import timber.log.Timber;

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

        Intent serviceIntent = new Intent(this, ForegroundService.class);
        startService(serviceIntent);
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
//            intent.putExtra("codeBooking", chatMessage.getCodeBooking());
//            intent.putExtra("roomId", Long.valueOf(chatMessage.getRoomId()));
//            intent.putExtra("bookingId", Long.valueOf(chatMessage.getBookingId()));
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                currentActivity.startActivity(intent);
            }else{
                Intent intent = new Intent(currentActivity, ChatActivity.class);
                intent.putExtra("codeBooking", chatMessage.getCodeBooking());
                intent.putExtra("roomId", Long.valueOf(chatMessage.getRoomId()));
                intent.putExtra("bookingId", Long.valueOf(chatMessage.getBookingId()));
//            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                currentActivity.startActivity(intent);
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
