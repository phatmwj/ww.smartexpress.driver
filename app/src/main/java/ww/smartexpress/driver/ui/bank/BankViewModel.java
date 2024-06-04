package ww.smartexpress.driver.ui.bank;

import androidx.databinding.ObservableField;

import ww.smartexpress.driver.MVVMApplication;
import ww.smartexpress.driver.data.Repository;
import ww.smartexpress.driver.ui.base.activity.BaseViewModel;

public class BankViewModel extends BaseViewModel {

    public ObservableField<String> bankName = new ObservableField<>();
    public ObservableField<String> accountName = new ObservableField<>();
    public ObservableField<String> accountNumber = new ObservableField<>();
    public ObservableField<String> brand = new ObservableField<>();
    public ObservableField<String> password = new ObservableField<>();

    public ObservableField<Boolean> isVisibility = new ObservableField<>(false);
    public BankViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
    }

    public void setIsVisibilityPassword(){
        isVisibility.set(!isVisibility.get());
    }

    public void back(){
        application.getCurrentActivity().finish();
    }
}
