package ww.smartexpress.driver.ui.notification.details;

import android.content.Intent;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ww.smartexpress.driver.MVVMApplication;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.data.Repository;
import ww.smartexpress.driver.data.model.api.ResponseWrapper;
import ww.smartexpress.driver.data.model.api.request.LoginRequest;
import ww.smartexpress.driver.data.model.api.response.NewsResponse;
import ww.smartexpress.driver.data.model.api.response.NotificationRead;
import ww.smartexpress.driver.data.model.api.response.NotificationServer;
import ww.smartexpress.driver.ui.base.activity.BaseViewModel;
import ww.smartexpress.driver.ui.home.HomeActivity;

public class NotificationDetailsViewModel extends BaseViewModel {

    public MutableLiveData<NotificationServer> notification = new MutableLiveData<>();

    public ObservableField<NewsResponse> news = new ObservableField<>();
    public NotificationDetailsViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
    }

    public void back(){
        application.getCurrentActivity().finish();
    }

    public Observable<ResponseWrapper<NewsResponse>> getNews(Long id){
        return  repository.getApiService().getNews(id);
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
}
