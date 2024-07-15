package ww.smartexpress.driver.ui.fragment.login;

import android.content.Intent;

import androidx.databinding.ObservableField;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ww.smartexpress.driver.MVVMApplication;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.constant.ErrorCode;
import ww.smartexpress.driver.data.Repository;
import ww.smartexpress.driver.data.model.api.request.LoginRequest;
import ww.smartexpress.driver.ui.await.AwaitActivity;
import ww.smartexpress.driver.ui.await.AwaitViewModel;
import ww.smartexpress.driver.ui.base.fragment.BaseFragmentViewModel;
import ww.smartexpress.driver.ui.home.HomeActivity;
import ww.smartexpress.driver.ui.password.forget.ForgetPasswordActivity;

public class LoginFragmentViewModel extends BaseFragmentViewModel {

    public ObservableField<String> phone = new ObservableField<>("");
    public ObservableField<String> password = new ObservableField<>("");

    public LoginFragmentViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
    }

    public void forgetPw(){
        Intent intent = new Intent(application.getCurrentActivity(), ForgetPasswordActivity.class);
        application.getCurrentActivity().startActivity(intent);
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
        compositeDisposable.add(repository.getApiService().login(new LoginRequest(password.get().trim(), phone.get(),false))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if(response.isResult()){
                        repository.getSharedPreferences().setToken(response.getData().getAccess_token());
                        Intent intent = new Intent(application.getCurrentActivity(), HomeActivity.class);
                        intent.putExtra("isLogin", true);
                        application.getCurrentActivity().startActivity(intent);
                        application.getCurrentActivity().finish();
                        hideLoading();
                    }else {
                        switch (response.getCode()){
                            case ErrorCode.DRIVER_ERROR_NOT_ACTIVE:
                                Intent intent = new Intent(application.getCurrentActivity(), AwaitActivity.class);
                                application.getCurrentActivity().startActivity(intent);
                                break;
                            default:
                                showErrorMessage(response.getMessage());
                                break;
                        }
                        hideLoading();
                    }
                },error->{
                    hideLoading();
                    showErrorMessage(application.getString(R.string.newtwork_error));
                    error.printStackTrace();
                })
        );
    }
}
