package ww.smartexpress.driver.ui.index;

import android.content.Intent;

import ww.smartexpress.driver.MVVMApplication;
import ww.smartexpress.driver.data.Repository;
import ww.smartexpress.driver.ui.base.activity.BaseViewModel;
import ww.smartexpress.driver.ui.login.LoginActivity;
import ww.smartexpress.driver.ui.signup.SignUpActivity;

public class IndexViewModel extends BaseViewModel {
    public IndexViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
    }

    public void navigateToLogin(){
        Intent intent = new Intent(application.getCurrentActivity(), LoginActivity.class);
        application.getCurrentActivity().startActivity(intent);
    }

    public void navigateToRegister(){
        Intent intent = new Intent(application.getCurrentActivity(), SignUpActivity.class);
        application.getCurrentActivity().startActivity(intent);
    }
}
