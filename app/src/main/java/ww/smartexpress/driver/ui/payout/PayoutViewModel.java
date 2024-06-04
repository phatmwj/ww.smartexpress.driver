package ww.smartexpress.driver.ui.payout;

import android.content.Intent;
import android.util.Log;

import androidx.databinding.ObservableField;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ww.smartexpress.driver.MVVMApplication;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.data.Repository;
import ww.smartexpress.driver.data.model.api.ApiModelUtils;
import ww.smartexpress.driver.data.model.api.request.DepositRequest;
import ww.smartexpress.driver.data.model.api.request.PayoutRequest;
import ww.smartexpress.driver.data.model.api.response.BankCard;
import ww.smartexpress.driver.data.model.api.response.MomoPaymentResponse;
import ww.smartexpress.driver.data.model.api.response.PayosPaymentResponse;
import ww.smartexpress.driver.ui.base.activity.BaseViewModel;
import ww.smartexpress.driver.ui.qrcode.QrcodeActivity;

public class PayoutViewModel extends BaseViewModel {

    public ObservableField<String> money = new ObservableField<>();
    public PayoutViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
    }

    public void back(){
        application.getCurrentActivity().finish();
    }

    public void doDone(){
        if(money.get() == null && Integer.valueOf(money.get())>50000){
            showErrorMessage("Số tiền rút không hợp lệ");
            return;
        }
        BankCard bankCard = new BankCard("BIDV","3143965789","Nguyễn Công Phát","NNTMCP VN");
        showLoading();
            compositeDisposable.add(repository.getApiService().payout(new PayoutRequest(ApiModelUtils.toJson(bankCard), 0,Integer.valueOf(money.get()),1))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(response -> {
                                if(response.isResult()){

                                    hideLoading();
                                    showSuccessMessage(response.getMessage());
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
}
