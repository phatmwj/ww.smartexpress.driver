package ww.smartexpress.driver.ui.qrcode;

import androidx.databinding.ObservableField;

import ww.smartexpress.driver.MVVMApplication;
import ww.smartexpress.driver.data.Repository;
import ww.smartexpress.driver.ui.base.activity.BaseViewModel;

public class QrcodeViewModel extends BaseViewModel {

    public ObservableField<String> momoPaymentInfo = new ObservableField<>();
    public QrcodeViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
    }

    public void back(){
        application.getCurrentActivity().finish();
    }
}
