package ww.smartexpress.driver.ui.cod;

import android.app.Activity;
import android.content.Intent;

import androidx.databinding.ObservableField;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ww.smartexpress.driver.MVVMApplication;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.data.Repository;
import ww.smartexpress.driver.data.model.api.request.PayoutRequest;
import ww.smartexpress.driver.data.model.api.response.AccountCOD;
import ww.smartexpress.driver.ui.base.activity.BaseViewModel;

public class CodViewModel extends BaseViewModel {

    public ObservableField<String> money = new ObservableField<>();
    public CodViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
//        getMyCOD();
    }

    public void back(){
        application.getCurrentActivity().finish();

    }

    public void doDone(){

        showLoading();
        compositeDisposable.add(repository.getApiService().updateCOD(new AccountCOD(Double.valueOf(money.get())))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if(response.isResult()){
                        showSuccessMessage(response.getMessage());
                        Intent intent = new Intent();
                        intent.putExtra("maxCod", money.get());
                        application.getCurrentActivity().setResult(Activity.RESULT_OK, intent);
                        back();
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

    public void getMyCOD(){
        showLoading();
        compositeDisposable.add(repository.getApiService().getAccountCOD()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if(response.isResult()){
                        money.set(String.valueOf(response.getData().getMaxCOD()));
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
