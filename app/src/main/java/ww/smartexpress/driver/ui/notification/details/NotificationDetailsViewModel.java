package ww.smartexpress.driver.ui.notification.details;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import io.reactivex.rxjava3.core.Observable;
import ww.smartexpress.driver.MVVMApplication;
import ww.smartexpress.driver.data.Repository;
import ww.smartexpress.driver.data.model.api.ResponseWrapper;
import ww.smartexpress.driver.data.model.api.response.NewsResponse;
import ww.smartexpress.driver.data.model.api.response.NotificationServer;
import ww.smartexpress.driver.ui.base.activity.BaseViewModel;

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
}
