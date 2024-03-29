package ww.smartexpress.driver.ui.password.otp;

import android.os.CountDownTimer;

import androidx.databinding.ObservableField;

import java.util.Locale;

import ww.smartexpress.driver.MVVMApplication;
import ww.smartexpress.driver.data.Repository;
import ww.smartexpress.driver.ui.base.activity.BaseViewModel;

public class ForgetPasswordOTPViewModel extends BaseViewModel {

    public ObservableField<String> phone = new ObservableField<>(" +8312345678");
    public ObservableField<String> countdownOTP = new ObservableField<>();
    public ObservableField<String> fpOTP1 = new ObservableField<>();
    public ObservableField<String> fpOTP2 = new ObservableField<>();
    public ObservableField<String> fpOTP3 = new ObservableField<>();
    public ObservableField<String> fpOTP4 = new ObservableField<>();

    public CountDownTimer countDownTimer;

    public ForgetPasswordOTPViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
        setCountdownOTP();

    }


    public void setCountdownOTP() {

        long OTPDurationInMillis = 30000; //30s
        long intervalInMillis = 1000; //1s tick

        countDownTimer = new CountDownTimer(OTPDurationInMillis, intervalInMillis) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = (millisUntilFinished / (60 * 1000)) % 60;
                long seconds = (millisUntilFinished / 1000) % 60;
                String countdownText = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
                countdownOTP.set(" (" + countdownText + ")");
            }
            @Override
            public void onFinish() {
                back();
            }
        };

        countDownTimer.start();
    }

    public void back(){
        application.getCurrentActivity().onBackPressed();
    }

}
