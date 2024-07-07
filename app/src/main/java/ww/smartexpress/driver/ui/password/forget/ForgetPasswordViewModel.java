package ww.smartexpress.driver.ui.password.forget;

import android.content.Intent;

import androidx.databinding.ObservableField;

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
import ww.smartexpress.driver.ui.password.otp.VerifyForgetPasswordOTPActivity;


public class ForgetPasswordViewModel extends BaseViewModel {
    public ObservableField<Integer> kind = new ObservableField<>(1);
    public ObservableField<String> phone = new ObservableField<>("");
    public ObservableField<String> userId = new ObservableField<>("");
    public ForgetPasswordViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
    }

    Observable<ResponseWrapper<CustomerIdResponse>> requestForgetPassword(ForgetPasswordRequest request) {
        return repository.getApiService().forgetPassword(request)
                .doOnNext(response -> {

                });
    }

    public void doNext(){
        if(kind.get() == 1){
            if(phone.get().trim().length() != 10){
                showErrorMessage("Số điện thoại không hợp lệ");
                return;
            }
        }
        ForgetPasswordRequest request = ForgetPasswordRequest.builder()
                .phone(phone.get())
                .kind(kind.get())
                .build();
        showLoading();

        compositeDisposable.add(requestForgetPassword(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    hideLoading();
                    if(response.isResult() && response.getData() != null){
                        Intent intent = new Intent(getApplication().getCurrentActivity(), VerifyForgetPasswordOTPActivity.class);
                        intent.putExtra(Constants.KEY_USER_ID, response.getData().getUserId());
                        intent.putExtra(Constants.VERIFY_OPTION, kind.get());
                        intent.putExtra("phone", phone.get());
                        getApplication().getCurrentActivity().startActivity(intent);
                        getApplication().getCurrentActivity().finish();
                    }else{
                        showErrorMessage(response.getMessage());
                    }
                }, err -> {
                    hideLoading();
                    showErrorMessage(application.getString(R.string.newtwork_error));
                }));
    }
}
