package ww.smartexpress.driver.ui.wallet.transaction;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ww.smartexpress.driver.MVVMApplication;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.data.Repository;
import ww.smartexpress.driver.data.model.api.response.WalletTransaction;
import ww.smartexpress.driver.ui.base.activity.BaseViewModel;

public class TransactionViewModel extends BaseViewModel {
    public ObservableField<Integer> pageNumber = new ObservableField<>(0);
    public ObservableField<Integer> pageSize = new ObservableField<>(10);
    public ObservableField<Integer> pageTotal = new ObservableField<>();
    public ObservableField<Integer> totalElement = new ObservableField<>();

    public MutableLiveData<List<WalletTransaction>> transactions = new MutableLiveData<>();
    public TransactionViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
        getTransaction();
    }
    public void back(){
        application.getCurrentActivity().finish();
    }

    public void getTransaction(){
        if (pageNumber.get() == 0){
            showLoading();
        }
        compositeDisposable.add(repository.getApiService().getWalletTransaction(pageNumber.get(),pageSize.get())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if(response.isResult()){
                        pageTotal.set(response.getData().getTotalPages());
                        totalElement.set(Math.toIntExact(response.getData().getTotalElements()));
                        if (response.getData().getContent()!=null){
                            transactions.setValue(response.getData().getContent());
                        }else{
                            transactions.setValue(new ArrayList<>());
                        }
                    }else {
                        showErrorMessage(response.getMessage());
                    }
                    hideLoading();
                },error->{
                    showErrorMessage(application.getString(R.string.newtwork_error));
                    error.printStackTrace();
                    hideLoading();
                })
        );
    }
}
