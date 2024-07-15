package ww.smartexpress.driver.ui.signup;

import android.content.Intent;

import androidx.databinding.ObservableField;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ww.smartexpress.driver.MVVMApplication;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.data.Repository;
import ww.smartexpress.driver.data.model.api.request.LoginRequest;
import ww.smartexpress.driver.data.model.api.request.RegisterRequest;
import ww.smartexpress.driver.ui.base.activity.BaseViewModel;
import ww.smartexpress.driver.ui.home.HomeActivity;
import ww.smartexpress.driver.ui.login.LoginActivity;

public class SignUpViewModel extends BaseViewModel {
    public ObservableField<String> fullName = new ObservableField<>("");
    public ObservableField<String> email = new ObservableField<>("");
    public ObservableField<String> phone = new ObservableField<>("");
    public ObservableField<String> password = new ObservableField<>("");
    public ObservableField<Boolean> isVisibility = new ObservableField<>(false);
    public SignUpViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
    }

    public void signup(){
        if(fullName.get() == null || fullName.get().isEmpty()){
            showErrorMessage(application.getString(R.string.empty_fullname_error));
            return;
        }
//        if(email.get() == null || email.get().isEmpty()){
//            showErrorMessage(application.getString(R.string.empty_email_error));
//            return;
//        }
        if(phone.get() == null || phone.get().isEmpty()){
            showErrorMessage(application.getString(R.string.empty_phone_error));
            return;
        }
        if(password.get() == null || password.get().isEmpty()){
            showErrorMessage(application.getString(R.string.empty_password_error));
            return;
        }
        RegisterRequest request = new RegisterRequest(fullName.get(), password.get(), phone.get(),false);
        showLoading();
        compositeDisposable.add(repository.getApiService().register(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if(response.isResult()){
                        showSuccessMessage(response.getMessage());
                        Intent intent = new Intent(getApplication().getCurrentActivity(), LoginActivity.class);
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

    public void setIsVisibilityPassword(){
        isVisibility.set(!isVisibility.get());
    }
}
