package ww.smartexpress.driver.ui.payout;

import android.content.Intent;

import androidx.databinding.ObservableField;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ww.smartexpress.driver.MVVMApplication;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.constant.Constants;
import ww.smartexpress.driver.data.Repository;
import ww.smartexpress.driver.data.model.api.ResponseListObj;
import ww.smartexpress.driver.data.model.api.ResponseWrapper;
import ww.smartexpress.driver.data.model.api.request.ConfirmPasswordRequest;
import ww.smartexpress.driver.data.model.api.request.PayoutRequest;
import ww.smartexpress.driver.data.model.api.response.BankCard;
import ww.smartexpress.driver.data.model.api.response.PayoutTransaction;
import ww.smartexpress.driver.data.model.room.UserEntity;
import ww.smartexpress.driver.ui.bank.BankActivity;
import ww.smartexpress.driver.ui.base.activity.BaseViewModel;

public class PayoutViewModel extends BaseViewModel {

    public ObservableField<String> money = new ObservableField<>();
    public ObservableField<Integer> balance = new ObservableField<>(0);
    public ObservableField<UserEntity> user = new ObservableField<>();
    public ObservableField<BankCard> bankCard = new ObservableField<>();
    public ObservableField<List<PayoutTransaction>> payoutTransactionList = new ObservableField<>(new ArrayList<>());
    public PayoutViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
    }
    public Repository getRepository(){
        return repository;
    }

    public void back(){
        application.getCurrentActivity().finish();
    }

    public void doDone(){
        if(money.get() != null && Integer.valueOf(money.get())<50000){
            showErrorMessage("Số tiền rút tối thiểu là 50.000đ");
            return;
        }
        if(money.get() != null && Integer.valueOf(money.get())> balance.get()){
            showErrorMessage("Số tiền rút vượt quá số dư trong ví");
            return;
        }
        showLoading();
            compositeDisposable.add(repository.getApiService().payout(new PayoutRequest(user.get().getBankCard(),Integer.valueOf(money.get())))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(response -> {
                                if(response.isResult()){
                                    hideLoading();
                                    showSuccessMessage(response.getMessage());
                                    back();
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

    public void navigateBank(){
        Intent intent = new Intent(application.getCurrentActivity(), BankActivity.class);
        application.getCurrentActivity().startActivity(intent);
    }

    public Observable<ResponseWrapper<String>> confirmPassword(String password){
        ConfirmPasswordRequest confirmPasswordRequest = new ConfirmPasswordRequest(password);
        return repository.getApiService().confirmPassword(confirmPasswordRequest);
    }

    public void confirmPass(String password){
        showLoading();
        compositeDisposable.add(repository.getApiService().confirmPassword(new ConfirmPasswordRequest(password))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if(response.isResult()){
                        ((PayoutActivity)application.getCurrentActivity()).getPasswordDialog().getDialog().dismiss();
                        doDone();
                    }else {
                        showErrorMessage("Mật khẩu không chính xác");
                    }
                    hideLoading();
                },error->{
                    hideLoading();
                    showErrorMessage(application.getString(R.string.newtwork_error));
                    error.printStackTrace();
                })
        );
    }

    public Observable<ResponseWrapper<String>> deletePayoutRequest(Long id){
        return repository.getApiService().deletePayoutRequest(id);
    }

    public Observable<ResponseWrapper<ResponseListObj<PayoutTransaction>>> getMyPayoutRequest(){
        Long userId = Long.valueOf(repository.getSharedPreferences().getStringVal(Constants.KEY_USER_ID));
        return repository.getApiService().getMyPayoutRequest(userId, 0);
    }
}
