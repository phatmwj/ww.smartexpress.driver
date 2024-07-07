package ww.smartexpress.driver.ui.wallet.transaction.details;

import android.content.Intent;

import androidx.databinding.ObservableField;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ww.smartexpress.driver.MVVMApplication;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.data.Repository;
import ww.smartexpress.driver.data.model.api.response.WalletTransaction;
import ww.smartexpress.driver.ui.base.activity.BaseViewModel;
import ww.smartexpress.driver.ui.booking.details.BookingDetailsActivity;

public class TransactionDetailsViewModel extends BaseViewModel {

    public ObservableField<WalletTransaction> transaction = new ObservableField<>();
    public TransactionDetailsViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
    }

    public void getTransactionDetails(Long id){
        showLoading();
        compositeDisposable.add(repository.getApiService().getTransactionDetails(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if(response.isResult()){
                        transaction.set(response.getData());
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

    public void navigateToBooking(){
        Intent intent = new Intent(application.getCurrentActivity(), BookingDetailsActivity.class);
        intent.putExtra("bookingId", transaction.get().getBooking().getId());
        application.getCurrentActivity().startActivity(intent);
    }
}
