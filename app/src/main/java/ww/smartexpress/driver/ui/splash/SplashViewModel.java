package ww.smartexpress.driver.ui.splash;

import android.content.Intent;
import android.util.Log;

import ww.smartexpress.driver.MVVMApplication;
import ww.smartexpress.driver.data.Repository;
import ww.smartexpress.driver.ui.auth.AuthActivity;
import ww.smartexpress.driver.ui.base.activity.BaseViewModel;
import ww.smartexpress.driver.ui.home.HomeActivity;
import ww.smartexpress.driver.ui.index.IndexActivity;

public class SplashViewModel extends BaseViewModel {
    public SplashViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
    }

    public void checkLogin(){
        Log.d("TAG", "checkLogin: "+ repository.getSharedPreferences().getToken());
        if(repository.getSharedPreferences().getToken().equals("NULL") || repository.getSharedPreferences().getToken().isEmpty()){
            Intent intent = new Intent(application.getCurrentActivity(), AuthActivity.class);
            application.getCurrentActivity().startActivity(intent);
            application.getCurrentActivity().finish();
        }else {
            Intent intent = new Intent(application.getCurrentActivity(), HomeActivity.class);
            application.getCurrentActivity().startActivity(intent);
            application.getCurrentActivity().finish();
        }
    }
}
