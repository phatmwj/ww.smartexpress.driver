package ww.smartexpress.driver.ui.history;

import android.util.Log;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.Observable;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ww.smartexpress.driver.MVVMApplication;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.data.Repository;
import ww.smartexpress.driver.data.model.api.response.Booking;
import ww.smartexpress.driver.data.model.room.UserEntity;
import ww.smartexpress.driver.ui.base.activity.BaseViewModel;
import ww.smartexpress.driver.ui.history.adapter.TripHistoryAdapter;

public class TripHistoryViewModel extends BaseViewModel {

    public MutableLiveData<List<Booking>> bookingList = new MutableLiveData<>();
    public ObservableField<Boolean> isEmpty = new ObservableField<>(false);
    public ObservableField<Integer> pageNumber = new ObservableField<>(0);
    public ObservableField<Integer> pageSize = new ObservableField<>(10);
    public ObservableField<Integer> pageTotal = new ObservableField<>();
    public ObservableField<Integer> totalItem = new ObservableField<>(0);

    public TripHistoryViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
//        getMyBooking();
    }

    public void back(){
        getApplication().getCurrentActivity().onBackPressed();
    }


    public void getMyBooking(){
        showLoading();
        compositeDisposable.add(repository.getApiService().getMyBooking(null, null, pageNumber.get(),pageSize.get(),
                                                                        null)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if(response.isResult()){
                        bookingList.setValue(response.getData().getContent());
                        pageTotal.set(response.getData().getTotalPages());
                        totalItem.set(response.getData().getTotalElements().intValue());
                        Log.d("TAG", "getMyBooking: "+ pageTotal.get());
                    }else {
                        showErrorMessage(response.getMessage());
                        hideLoading();
                    }
                },error->{
                    showErrorMessage(application.getString(R.string.newtwork_error));
                    error.printStackTrace();
                    hideLoading();
                })
        );
    }

    public void loadMore(){
        int pageCurrent = pageNumber.get();
        pageNumber.set(pageCurrent+1);
        getMyBooking();
    }
}
