package ww.smartexpress.driver.ui.signup.otp;

import android.os.CountDownTimer;
import android.util.Log;

import androidx.databinding.ObservableField;

import java.util.Locale;

import io.reactivex.rxjava3.core.Observable;
import ww.smartexpress.driver.MVVMApplication;
import ww.smartexpress.driver.data.Repository;
import ww.smartexpress.driver.data.model.api.ResponseWrapper;
import ww.smartexpress.driver.data.model.api.request.RetryOtpRegisterRequest;
import ww.smartexpress.driver.data.model.api.request.VerifyAccountRequest;
import ww.smartexpress.driver.data.model.api.response.CustomerIdResponse;
import ww.smartexpress.driver.ui.base.activity.BaseViewModel;

public class RegisterOTPViewModel extends BaseViewModel {


    public ObservableField<String> phone = new ObservableField<>("");
    public ObservableField<String> countdownOTP = new ObservableField<>();
    public ObservableField<String> fpOTP1 = new ObservableField<>();
    public ObservableField<String> fpOTP2 = new ObservableField<>();
    public ObservableField<String> fpOTP3 = new ObservableField<>();
    public ObservableField<String> fpOTP4 = new ObservableField<>();
    public ObservableField<Long>  milFinished = new ObservableField<>();

    public CountDownTimer countDownTimer;

    public ObservableField<String> userId = new ObservableField<>("");

    public RegisterOTPViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
    }

    public void setCountdownOTP() {

        long OTPDurationInMillis = 3*60*1000; //120s
        long intervalInMillis = 1000; //1s tick

        milFinished.set(-1L);

        countDownTimer = new CountDownTimer(OTPDurationInMillis, intervalInMillis) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = (millisUntilFinished / (60 * 1000)) % 60;
                long seconds = (millisUntilFinished / 1000) % 60;
                String countdownText = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
                countdownOTP.set(countdownText);
                //Log.d("TAG", "onTick: " + countdownOTP.get());
                Log.d("TAG", "onTick: " + millisUntilFinished);
            }
            @Override
            public void onFinish() {
                milFinished.set(0L);
            }
        };

        countDownTimer.start();
    }

    public void back(){
        application.getCurrentActivity().onBackPressed();
    }


    Observable<ResponseWrapper<String>> verifyAccount(VerifyAccountRequest request) {
        return repository.getApiService().verifyAccount(request)
                .doOnNext(response -> {
                });
    }

    Observable<ResponseWrapper<CustomerIdResponse>> retryOTPRegister(RetryOtpRegisterRequest request) {
        return repository.getApiService().retryOTPRegister(request)
                .doOnNext(response -> {
                });
    }

}
