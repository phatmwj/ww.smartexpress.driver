package ww.smartexpress.driver.ui.booking.details;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ww.smartexpress.driver.MVVMApplication;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.constant.Constants;
import ww.smartexpress.driver.data.Repository;
import ww.smartexpress.driver.data.model.api.ResponseWrapper;
import ww.smartexpress.driver.data.model.api.response.CurrentBooking;
import ww.smartexpress.driver.ui.base.activity.BaseViewModel;

public class BookingDetailsViewModel extends BaseViewModel {
    public ObservableField<CurrentBooking> booking = new ObservableField<>(null);
    public ObservableField<String> date = new ObservableField<>();
    public ObservableField<Integer> star = new ObservableField<>(0);
    public BookingDetailsViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
    }

    public void back(){
        application.getCurrentActivity().onBackPressed();
    }

    public Observable<ResponseWrapper<CurrentBooking>> getBooking(Long id){
        return repository.getApiService().loadBooking(id);
    }

}
