package ww.smartexpress.driver.ui.award;

import ww.smartexpress.driver.MVVMApplication;
import ww.smartexpress.driver.data.Repository;
import ww.smartexpress.driver.ui.base.activity.BaseViewModel;

public class AwardViewModel extends BaseViewModel {
    public AwardViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
    }

    public void back(){
        application.getCurrentActivity().onBackPressed();
    }
}
