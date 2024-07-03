package ww.smartexpress.driver.ui.password.forget;

import android.content.Intent;

import androidx.databinding.ObservableField;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ww.smartexpress.driver.MVVMApplication;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.data.Repository;
import ww.smartexpress.driver.data.model.api.ResponseWrapper;
import ww.smartexpress.driver.data.model.api.request.ResetPasswordRequest;
import ww.smartexpress.driver.ui.auth.AuthActivity;
import ww.smartexpress.driver.ui.base.activity.BaseViewModel;

public class ResetForgetPasswordViewModel extends BaseViewModel {
    public ObservableField<String> otp = new ObservableField<>("");
    public ObservableField<String> userId = new ObservableField<>("");
    public ObservableField<String> newPassword = new ObservableField<>("");
    public ObservableField<String> newCPassword = new ObservableField<>("");

    public ObservableField<Boolean> isVisibilityN = new ObservableField<>(false);
    public ObservableField<Boolean> isVisibilityC = new ObservableField<>(false);

    public ResetForgetPasswordViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
    }

    public void setIsVisibilityPasswordC(){
        isVisibilityC.set(!isVisibilityC.get());
    }

    public void setIsVisibilityPasswordN(){
        isVisibilityN.set(!isVisibilityN.get());
    }

    Observable<ResponseWrapper<String>> resetPassword(ResetPasswordRequest request) {
        return repository.getApiService().resetPassword(request)
                .doOnNext(response -> {

                });
    }

    public void resetPassword(){
        if(newPassword.get().contains(" ")){
            showErrorMessage(application.getString(R.string.invalid_pw));
            return;
        }

        ResetPasswordRequest request = ResetPasswordRequest.builder()
                .newPassword(newPassword.get())
                .otp(otp.get())
                .userId(userId.get())
                .build();

        compositeDisposable.add(resetPassword(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    hideLoading();
                    if(response.isResult()){
                        showSuccessMessage("Đổi mật khẩu thành công");
                        Intent intent = new Intent(getApplication().getCurrentActivity(), AuthActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        getApplication().getCurrentActivity().startActivity(intent);
                        getApplication().getCurrentActivity().finish();
                    }else{
                        showErrorMessage("Xảy ra lỗi, vui lòng thử lại!");
                    }
                }, err -> {
                    hideLoading();
                    showErrorMessage(application.getString(R.string.network_error));
                }));
    }
}
