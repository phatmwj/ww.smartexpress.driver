package ww.smartexpress.driver.ui.password.otp;

import android.content.Intent;
import android.os.CountDownTimer;

import androidx.databinding.ObservableField;

import java.util.Locale;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ww.smartexpress.driver.MVVMApplication;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.constant.Constants;
import ww.smartexpress.driver.data.Repository;
import ww.smartexpress.driver.data.model.api.ResponseWrapper;
import ww.smartexpress.driver.data.model.api.request.ForgetPasswordRequest;
import ww.smartexpress.driver.data.model.api.response.CustomerIdResponse;
import ww.smartexpress.driver.ui.base.activity.BaseViewModel;
import ww.smartexpress.driver.ui.password.forget.ResetForgetPasswordActivity;

public class VerifyForgetPasswordOTPViewModel extends BaseViewModel {
    public ObservableField<String> otp1 = new ObservableField<>("");
    public ObservableField<String> otp2 = new ObservableField<>("");
    public ObservableField<String> otp3 = new ObservableField<>("");
    public ObservableField<String> otp4 = new ObservableField<>("");

    public ObservableField<Integer> kind = new ObservableField<>(1);
    public ObservableField<String> userId = new ObservableField<>("");
    public ObservableField<String> phone = new ObservableField<>();
    public ObservableField<String> countdownOTP = new ObservableField<>();
    public CountDownTimer countDownTimer;

    public VerifyForgetPasswordOTPViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
        setCountdownOTP();
    }

    public void verifyOTP(){
        String otp = otp1.get() + otp2.get() + otp3.get() + otp4.get();
        Intent intent = new Intent(getApplication().getCurrentActivity(), ResetForgetPasswordActivity.class);
        intent.putExtra(Constants.OTP, otp);
        intent.putExtra(Constants.KEY_USER_ID, userId.get());
        getApplication().getCurrentActivity().startActivity(intent);
        getApplication().getCurrentActivity().finish();
    }

    public void setCountdownOTP() {

        long OTPDurationInMillis = 1000*60*2; //30s
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

    public void resendOtp(){
        if(phone.get().trim().length() != 10){
            showErrorMessage("Số điện thoại không hợp lệ");
            return;
        }
        ForgetPasswordRequest request = ForgetPasswordRequest.builder()
                .phone(phone.get())
                .kind(1)
                .build();

        showLoading();
        compositeDisposable.add(requestForgetPassword(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    hideLoading();
                    if(response.isResult() && response.getData() != null){
                        countDownTimer.cancel();
                        setCountdownOTP();
                        showSuccessMessage("Yêu cầu thành công, vui lòng kiểm tra điện thoại của bạn");
                    }else{
                        showErrorMessage(response.getMessage());
                    }
                }, err -> {
                    hideLoading();
                    showErrorMessage(application.getString(R.string.newtwork_error));
                }));
    }

    Observable<ResponseWrapper<CustomerIdResponse>> requestForgetPassword(ForgetPasswordRequest request) {
        return repository.getApiService().forgetPassword(request)
                .doOnNext(response -> {

                });
    }
}
