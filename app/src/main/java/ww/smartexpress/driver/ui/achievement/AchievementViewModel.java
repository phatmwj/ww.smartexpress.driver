package ww.smartexpress.driver.ui.achievement;

import androidx.databinding.ObservableField;

import ww.smartexpress.driver.MVVMApplication;
import ww.smartexpress.driver.data.Repository;
import ww.smartexpress.driver.ui.base.activity.BaseViewModel;

public class AchievementViewModel extends BaseViewModel {
    public ObservableField<String> driverName = new ObservableField<>("Tran To Hong Nguyen");
    public ObservableField<Float> star = new ObservableField<>(5.0f);
    public ObservableField<Integer> rate = new ObservableField<>(120);
    public ObservableField<String> time = new ObservableField<>("1 năm 2 tháng");
    public ObservableField<Integer> trip = new ObservableField<>(120);
    public ObservableField<Integer> achieveType = new ObservableField<>(0);
    public AchievementViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
    }

    public void back(){
        getApplication().getCurrentActivity().onBackPressed();
    }

    public void setAchieveType(int type){
        achieveType.set(type);
    }
}
