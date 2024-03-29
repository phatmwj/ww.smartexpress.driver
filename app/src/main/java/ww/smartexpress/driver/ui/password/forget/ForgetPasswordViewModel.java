package ww.smartexpress.driver.ui.password.forget;

import android.content.Intent;

import androidx.databinding.ObservableField;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ww.smartexpress.driver.MVVMApplication;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.data.Repository;
import ww.smartexpress.driver.data.model.api.request.ForgetPassRequest;
import ww.smartexpress.driver.data.model.api.request.LoginRequest;
import ww.smartexpress.driver.ui.base.activity.BaseViewModel;
import ww.smartexpress.driver.ui.home.HomeActivity;
import ww.smartexpress.driver.ui.password.otp.ForgetPasswordOTPActivity;

public class ForgetPasswordViewModel extends BaseViewModel {
    public ObservableField<String> phoneNumber = new ObservableField<>();
    public ForgetPasswordViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
    }


    public void onTextChanged(CharSequence s, int start, int before, int count) {
        phoneNumber.set(s.toString());
    }

    public void clickButton(){
        if(phoneNumber.get() == null || phoneNumber.get().isEmpty()){
            showErrorMessage(application.getString(R.string.empty_phone_error));
            return;
        }
        showLoading();
        compositeDisposable.add(repository.getApiService().forgetPassword( new ForgetPassRequest(phoneNumber.get()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if(response.isResult()){
                        String userId = response.getData().getUserId();
                        Intent intent = new Intent(getApplication().getCurrentActivity(), ForgetPasswordOTPActivity.class);
                        intent.putExtra("userId", userId);
                        getApplication().getCurrentActivity().startActivity(intent);
                        showSuccessMessage(response.getMessage());
                    }else {
                        showErrorMessage(response.getMessage());
                    }
                    hideLoading();
                },error->{
                    hideLoading();
                    showErrorMessage(application.getString(R.string.newtwork_error));
                    error.printStackTrace();
                })
        );
    }
}
