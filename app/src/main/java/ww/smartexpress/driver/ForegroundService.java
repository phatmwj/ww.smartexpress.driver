package ww.smartexpress.driver;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Random;

import ww.smartexpress.driver.data.websocket.Message;

public class ForegroundService extends Service {

    private static final int NOTIFICATION_ID = 123;
    private static final String CHANNEL_ID = "MyForegroundServiceChannel";

    private Handler mHandler;
    private Runnable mRunnable;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        Notification notification = buildNotification();
        startForeground(NOTIFICATION_ID, notification);
        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                showNotification("Hello", "This is a message from MyForegroundService");
                mHandler.postDelayed(this, 20000); // Gửi thông báo sau mỗi 20 giây
            }
        };
        mHandler.postDelayed(mRunnable, 20000); // Khởi động việc gửi thông báo đầu tiên
        // Thực hiện công việc của bạn ở đây, như kết nối WebSocket hoặc các tác vụ khác.
        return START_REDELIVER_INTENT;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Foreground Service Channel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private Notification buildNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Foreground Service")
                .setContentText("Foreground Service is running")
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();
    }

    private void showNotification(String title, String message) {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify((int) System.currentTimeMillis(), notification);
    }
}
