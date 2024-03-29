package ww.smartexpress.driver.ui.login;

import android.content.Intent;

import androidx.databinding.ObservableField;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ww.smartexpress.driver.MVVMApplication;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.data.Repository;
import ww.smartexpress.driver.data.model.api.request.LoginRequest;
import ww.smartexpress.driver.ui.base.activity.BaseViewModel;
import ww.smartexpress.driver.ui.home.HomeActivity;
import ww.smartexpress.driver.ui.password.forget.ForgetPasswordActivity;

public class LoginViewModel extends BaseViewModel {
    public ObservableField<String> phone = new ObservableField<>("");
    public ObservableField<String> password = new ObservableField<>("");
    public ObservableField<Boolean> isVisibility = new ObservableField<>(false);
    public LoginViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
    }

    public void setIsVisibilityPassword(){
        isVisibility.set(!isVisibility.get());
    }
    public void forgetPw(){
        Intent intent = new Intent(getApplication().getCurrentActivity(), ForgetPasswordActivity.class);
        getApplication().getCurrentActivity().startActivity(intent);
    }
    public void doLogin(){
        if(phone.get() == null || phone.get().isEmpty()){
            showErrorMessage(application.getString(R.string.empty_phone_error));
            return;
        }
        if(password.get() == null || password.get().isEmpty()){
            showErrorMessage(application.getString(R.string.empty_password_error));
            return;
        }
        showLoading();
        compositeDisposable.add(repository.getApiService().login(new LoginRequest(password.get().trim(), phone.get()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if(response.isResult()){
                        repository.getSharedPreferences().setToken(response.getData().getAccess_token());
                        Intent intent = new Intent(getApplication().getCurrentActivity(), HomeActivity.class);
                        intent.putExtra("isLogin", true);
                        getApplication().getCurrentActivity().startActivity(intent);
                        getApplication().getCurrentActivity().finish();
                        hideLoading();
                    }else {
                        hideLoading();
                        showErrorMessage(response.getMessage());
                    }
                },error->{
                    hideLoading();
                    showErrorMessage(application.getString(R.string.newtwork_error));
                    error.printStackTrace();
                })
        );
    }



}
