package ww.smartexpress.driver.ui.fragment.notification;

import androidx.databinding.ObservableField;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ww.smartexpress.driver.MVVMApplication;
import ww.smartexpress.driver.data.Repository;
import ww.smartexpress.driver.data.model.api.ResponseGeneric;
import ww.smartexpress.driver.data.model.api.ResponseListObj;
import ww.smartexpress.driver.data.model.api.ResponseWrapper;
import ww.smartexpress.driver.data.model.api.response.NotificationRead;
import ww.smartexpress.driver.data.model.api.response.NotificationResponse;
import ww.smartexpress.driver.data.model.api.response.WalletTransaction;
import ww.smartexpress.driver.ui.base.fragment.BaseFragmentViewModel;

public class NotificationFragmentViewModel extends BaseFragmentViewModel {

    public ObservableField<Integer> pageNumber = new ObservableField<>(0);
    public ObservableField<Integer> pageSize = new ObservableField<>(10);
    public ObservableField<Integer> pageTotal = new ObservableField<>();
    public ObservableField<Integer> totalElement = new ObservableField<>();
    public ObservableField<Integer> totalUnread = new ObservableField<>();

    public ObservableField<FlexibleAdapter> mFlexibleAdapter = new ObservableField<>();
    public NotificationFragmentViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
    }

    public Observable<ResponseWrapper<ResponseListObj<NotificationResponse>>> getMyNotification(){
        return repository.getApiService().getMyNotification(pageNumber.get(),pageSize.get());
    }

    public Observable<ResponseGeneric> readAllNotification(){
        return repository.getApiService().readAllNotification();
    }

    public void readNotification(Long id){
        compositeDisposable.add(repository.getApiService().readNotification(new NotificationRead(id))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {

                },error->{
                    error.printStackTrace();
                })
        );
    }

    public Observable<ResponseWrapper<NotificationResponse>> getNotificationById(Long id){
        return repository.getApiService().getNotification(id);
    }

}
