package ww.smartexpress.driver.ui.await;

import ww.smartexpress.driver.MVVMApplication;
import ww.smartexpress.driver.data.Repository;
import ww.smartexpress.driver.ui.base.activity.BaseViewModel;

public class AwaitViewModel extends BaseViewModel {
    public AwaitViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
    }
    public void back(){
        application.getCurrentActivity().finish();
    }
}
