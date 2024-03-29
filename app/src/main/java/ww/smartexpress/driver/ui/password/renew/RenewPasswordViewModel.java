package ww.smartexpress.driver.ui.password.renew;

import android.content.Intent;

import androidx.databinding.ObservableField;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ww.smartexpress.driver.MVVMApplication;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.data.Repository;
import ww.smartexpress.driver.data.model.api.request.UpdateProfileRequest;
import ww.smartexpress.driver.data.model.room.UserEntity;
import ww.smartexpress.driver.ui.base.activity.BaseViewModel;
import ww.smartexpress.driver.ui.home.HomeActivity;

public class RenewPasswordViewModel extends BaseViewModel {
    public ObservableField<String> newPassword = new ObservableField<>("");
    public ObservableField<String> confirmNewPassword = new ObservableField<>("");
    public ObservableField<Boolean> isPwVisibility = new ObservableField<>(false);
    public ObservableField<Boolean> isCPwVisibility = new ObservableField<>(false);
    public ObservableField<Boolean> isForgot = new ObservableField<>(false);
    public ObservableField<UserEntity> user = new ObservableField<>();
    public RenewPasswordViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
    }

    public void setIsPwVisibility(){
        isPwVisibility.set(!isPwVisibility.get());
    }

    public void setIsCPwVisibility() {
        isCPwVisibility.set(!isCPwVisibility.get());
    }

    public void doNext(){
        Intent intent;
        if(isForgot.get()){
            intent = new Intent(getApplication().getCurrentActivity(), HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            getApplication().getCurrentActivity().startActivity(intent);
            getApplication().getCurrentActivity().finish();
        }else{
            UpdateProfileRequest updateProfileRequest = new UpdateProfileRequest();
            updateProfileRequest.setAvatar(user.get().getAvatar());
            updateProfileRequest.setFullName(user.get().getFullName());
            updateProfileRequest.setOldPassword(newPassword.get());
            updateProfileRequest.setNewPassword(confirmNewPassword.get());
            showLoading();
            compositeDisposable.add(repository.getApiService().updateProfile(updateProfileRequest)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(res->{
                                if(res.isResult()) {
                                    showSuccessMessage(application.getString(R.string.change_password_success));
                                    getApplication().getCurrentActivity().onBackPressed();
                                }else {
                                    showErrorMessage(res.getMessage());
                                }
                                hideLoading();
                            },err-> {
                                showErrorMessage(application.getString(R.string.newtwork_error));
                                err.printStackTrace();
                                hideLoading();
                            }
                    ));
        }
    }

    public void back(){
        getApplication().getCurrentActivity().onBackPressed();
    }

    public Repository getRepository(){
        return repository;
    }

}
