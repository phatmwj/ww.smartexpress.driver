package ww.smartexpress.driver.ui.allbike;

import android.util.Log;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ww.smartexpress.driver.MVVMApplication;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.data.Repository;
import ww.smartexpress.driver.data.model.api.ResponseGeneric;
import ww.smartexpress.driver.data.model.api.ResponseListObj;
import ww.smartexpress.driver.data.model.api.ResponseWrapper;
import ww.smartexpress.driver.data.model.api.request.ChangeStateRequest;
import ww.smartexpress.driver.data.model.api.response.DriverServiceResponse;
import ww.smartexpress.driver.data.model.api.response.ServiceResponse;
import ww.smartexpress.driver.ui.base.activity.BaseViewModel;

public class AllBikeServiceViewModel extends BaseViewModel {

    public MutableLiveData<List<ServiceResponse>> services = new MutableLiveData<>();
    public ObservableField<List<DriverServiceResponse>> services1 = new ObservableField<>();
    public AllBikeServiceViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
    }
    public void back(){
        application.getCurrentActivity().onBackPressed();
    }

    public Observable<ResponseWrapper<ResponseListObj<DriverServiceResponse>>> getService(){
        String userId = repository.getSharedPreferences().getUserId();
        return repository.getApiService().getDriverService(Long.valueOf(userId), null, null,
                null, null)
                .doOnNext(response->{
                    services1.set(response.getData().getContent());
                });
    }

    public Observable<ResponseGeneric> changeStateService(ChangeStateRequest request){
        return repository.getApiService().changeStateService(request);
    }

}
