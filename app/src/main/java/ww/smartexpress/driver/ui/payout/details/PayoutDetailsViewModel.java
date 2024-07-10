package ww.smartexpress.driver.ui.payout.details;

import androidx.databinding.ObservableField;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ww.smartexpress.driver.MVVMApplication;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.data.Repository;
import ww.smartexpress.driver.data.model.api.response.PayoutResponse;
import ww.smartexpress.driver.ui.base.activity.BaseViewModel;

public class PayoutDetailsViewModel extends BaseViewModel {

    public ObservableField<PayoutResponse> payoutResponse = new ObservableField<>();
    public PayoutDetailsViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
    }

    public void getTransactionDetails(Long id){
        showLoading();
        compositeDisposable.add(repository.getApiService().getPayout(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if(response.isResult()){
                        payoutResponse.set(response.getData());
                    }else {
                        showErrorMessage(response.getMessage());
                    }
                    hideLoading();
                },error->{
                    hideLoading();
                    showErrorMessage(application.getString(R.string.newtwork_error));
                    error.printStackTrace();
                })
        );
    }
}
