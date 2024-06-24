package ww.smartexpress.driver.ui.wallet;

import android.content.Intent;

import androidx.databinding.ObservableField;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ww.smartexpress.driver.MVVMApplication;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.data.Repository;
import ww.smartexpress.driver.data.model.api.ApiModelUtils;
import ww.smartexpress.driver.data.model.api.response.BankCard;
import ww.smartexpress.driver.data.model.api.response.BankResponse;
import ww.smartexpress.driver.data.model.api.response.WalletResponse;
import ww.smartexpress.driver.data.model.room.UserEntity;
import ww.smartexpress.driver.ui.bank.BankActivity;
import ww.smartexpress.driver.ui.base.activity.BaseViewModel;
import ww.smartexpress.driver.ui.deposit.DepositActivity;
import ww.smartexpress.driver.ui.payout.PayoutActivity;
import ww.smartexpress.driver.ui.wallet.transaction.TransactionActivity;

public class WalletViewModel extends BaseViewModel {

    public ObservableField<WalletResponse> wallet = new ObservableField<>();
    public ObservableField<UserEntity> user = new ObservableField<>();
    public ObservableField<BankCard> bankCard = new ObservableField<>();
    public WalletViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
    }

    public Repository getRepository(){
        return repository;
    }

    public void back(){
        application.getCurrentActivity().finish();
    }

    public void navigateDeposit(){
        Intent intent = new Intent(application.getCurrentActivity(), DepositActivity.class);
        application.getCurrentActivity().startActivity(intent);
    }

    public void navigateTransaction(){
        Intent intent = new Intent(application.getCurrentActivity(), TransactionActivity.class);
        application.getCurrentActivity().startActivity(intent);
    }

    public void navigatePayout(){
        if(bankCard.get() == null){
            showErrorMessage("Vui lòng cập nhập tài khoản ngân hàng!");
            return;
        }
        Intent intent = new Intent(application.getCurrentActivity(), PayoutActivity.class);
        intent.putExtra("balance", wallet.get().getBalance());
        application.getCurrentActivity().startActivity(intent);
    }

    public void navigateBank(){
        Intent intent = new Intent(application.getCurrentActivity(), BankActivity.class);
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
