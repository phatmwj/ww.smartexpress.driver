package ww.smartexpress.driver.ui.wallet;

import android.content.Intent;

import androidx.databinding.ObservableField;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ww.smartexpress.driver.MVVMApplication;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.data.Repository;
import ww.smartexpress.driver.data.model.api.response.WalletResponse;
import ww.smartexpress.driver.ui.base.activity.BaseViewModel;
import ww.smartexpress.driver.ui.deposit.DepositActivity;
import ww.smartexpress.driver.ui.payout.PayoutActivity;

public class WalletViewModel extends BaseViewModel {

    public ObservableField<WalletResponse> wallet = new ObservableField<>();
    public WalletViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
        getMyWallet();
    }

    public void back(){
        application.getCurrentActivity().finish();
    }

    public void navigateDeposit(){
        Intent intent = new Intent(application.getCurrentActivity(), DepositActivity.class);
        application.getCurrentActivity().startActivity(intent);
    }

    public void navigatePayout(){
        Intent intent = new Intent(application.getCurrentActivity(), PayoutActivity.class);
        application.getCurrentActivity().startActivity(intent);
    }


    public void getMyWallet(){
        showLoading();
        compositeDisposable.add(repository.getApiService().getMyWallet()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if(response.isResult()){
                        wallet.set(response.getData());
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


}
