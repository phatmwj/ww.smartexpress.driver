package ww.smartexpress.driver.ui.welcome;


import android.content.Intent;

import androidx.databinding.ObservableBoolean;

import ww.smartexpress.driver.MVVMApplication;
import ww.smartexpress.driver.data.Repository;
import ww.smartexpress.driver.ui.base.activity.BaseViewModel;
import ww.smartexpress.driver.ui.home.HomeActivity;
import ww.smartexpress.driver.ui.index.IndexActivity;
import ww.smartexpress.driver.ui.login.LoginActivity;
import ww.smartexpress.driver.ui.signup.SignUpActivity;

public class WelcomeViewModel extends BaseViewModel {
    public ObservableBoolean checkLocationPermission = new ObservableBoolean(false);
    public WelcomeViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
    }

    public void doNext(){
        if(repository.getSharedPreferences().getToken().equals("NULL") || repository.getSharedPreferences().getToken().isEmpty()){
            Intent intent = new Intent(application.getCurrentActivity(), IndexActivity.class);
            application.getCurrentActivity().startActivity(intent);
            application.getCurrentActivity().finish();
        }else {
            Intent intent = new Intent(application.getCurrentActivity(), HomeActivity.class);
            application.getCurrentActivity().startActivity(intent);
            application.getCurrentActivity().finish();
        }
    }
}
