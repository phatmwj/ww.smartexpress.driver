package ww.smartexpress.driver.ui.fragment.income.details;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ww.smartexpress.driver.MVVMApplication;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.data.Repository;
import ww.smartexpress.driver.data.model.api.ResponseListObj;
import ww.smartexpress.driver.data.model.api.ResponseWrapper;
import ww.smartexpress.driver.data.model.api.request.IncomeRequest;
import ww.smartexpress.driver.data.model.api.response.Booking;
import ww.smartexpress.driver.data.model.api.response.IncomeResponse;
import ww.smartexpress.driver.ui.base.activity.BaseViewModel;
import ww.smartexpress.driver.utils.DateUtils;

public class IncomeDetailsViewModel extends BaseViewModel {
    public ObservableField<Integer> incomeTime = new ObservableField<>();
    public ObservableField<String> incomeDateString = new ObservableField<>();
    public ObservableField<Date> currentDate = new ObservableField<>(new Date());
    public ObservableField<String> startDate = new ObservableField<>();
    public ObservableField<String> endDate = new ObservableField<>();
    public ObservableField<IncomeResponse> income = new ObservableField<>();
    public ObservableField<Integer> bookingState = new ObservableField<>(300);
    public ObservableField<String> incomeTimeString = new ObservableField<>();
    public MutableLiveData<List<Booking>> bookingList = new MutableLiveData<>();
    public ObservableField<String> completeRate = new ObservableField<>("0");
    public ObservableField<Integer> pageNumber = new ObservableField<>(0);
    public ObservableField<Integer> pageSize = new ObservableField<>(10);

    public ObservableField<Integer> pageTotal = new ObservableField<>();

    public ObservableField<Integer> totalItem = new ObservableField<>(0);
    public IncomeDetailsViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
    }

    public void back(){
        application.getCurrentActivity().onBackPressed();
    }

    public Observable<ResponseWrapper<IncomeResponse>> getIncomeDetails(){
        completeRate.set("0");
        if(incomeTime.get()== 0){
            startDate.set(DateUtils.dateStartFormat(currentDate.get()));
            endDate.set(DateUtils.dateEndFormat(currentDate.get()));
            incomeDateString.set((DateUtils.dateOnlyFormat(startDate.get())));
        }
        if(incomeTime.get()==1){
            startDate.set(DateUtils.startWeekFormat(currentDate.get()));
            endDate.set(DateUtils.endWeekFormat(currentDate.get()));
            incomeDateString.set("Từ "+DateUtils.dateOnlyFormat(startDate.get())+ " đến "+DateUtils.dateOnlyFormat(endDate.get()));
        }
        if(incomeTime.get()==2){
            startDate.set(DateUtils.startMonthFormat(currentDate.get()));
            endDate.set(DateUtils.endMonthFormat(currentDate.get()));
            incomeDateString.set("Từ "+DateUtils.dateOnlyFormat(startDate.get())+ " đến "+DateUtils.dateOnlyFormat(endDate.get()));
        }
        IncomeRequest request = new IncomeRequest();
        request.setBookingState(bookingState.get());
        request.setEndDate(DateUtils.convertToUTC(endDate.get()));
        request.setStartDate(DateUtils.convertToUTC(startDate.get()));
        return  repository.getApiService().statisticIncome(request)
                .doOnNext(res ->{
                    income.set(res.getData());
                });
    }

//    public void statistic(){
//        completeRate.set("0");
//        if(incomeTime.get()== 0){
//            startDate.set(DateUtils.dateStartFormat(currentDate.get()));
//            endDate.set(DateUtils.dateEndFormat(currentDate.get()));
//            incomeDateString.set((DateUtils.dateOnlyFormat(startDate.get())));
//        }
//        if(incomeTime.get()==1){
//            startDate.set(DateUtils.startWeekFormat(currentDate.get()));
//            endDate.set(DateUtils.endWeekFormat(currentDate.get()));
//            incomeDateString.set("Từ "+DateUtils.dateOnlyFormat(startDate.get())+ " đến "+DateUtils.dateOnlyFormat(endDate.get()));
//        }
//        if(incomeTime.get()==2){
//            startDate.set(DateUtils.startMonthFormat(currentDate.get()));
//            endDate.set(DateUtils.endMonthFormat(currentDate.get()));
//            incomeDateString.set("Từ "+DateUtils.dateOnlyFormat(startDate.get())+ " đến "+DateUtils.dateOnlyFormat(endDate.get()));
//        }
//        IncomeRequest request = new IncomeRequest();
//        request.setBookingState(bookingState.get());
//        request.setEndDate(DateUtils.convertToUTC(endDate.get()));
//        request.setStartDate(DateUtils.convertToUTC(startDate.get()));
//        showLoading();
//        compositeDisposable.add(repository.getApiService().statisticIncome(request)
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(response -> {
//                            if(response.isResult()){
//                                income.set(response.getData());
//                                getMyBooking();
//                                getActivityRate();
//                            }else {
//                                showErrorMessage(response.getMessage());
//                            }
//                            hideLoading();
//                        },error->{
//                            showErrorMessage(application.getString(R.string.newtwork_error));
//                            error.printStackTrace();
//                            hideLoading();
//                        })
//        );
//    }

    public Observable<ResponseWrapper<ResponseListObj<Booking>>> getBookings(){
        return repository.getApiService().getMyBooking(DateUtils.convertToUTC(endDate.get()), DateUtils.convertToUTC(startDate.get()), pageNumber.get(),1000,
                bookingState.get());
    }

//    public void getMyBooking(){
//        showLoading();
//        compositeDisposable.add(repository.getApiService().getMyBooking(DateUtils.convertToUTC(endDate.get()), DateUtils.convertToUTC(startDate.get()), null,null,
//                        bookingState.get())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(response -> {
//                    if(response.isResult()){
//                        bookingList.setValue(response.getData().getContent());
//                    }else {
//                        showErrorMessage(response.getMessage());
//                    }
//                    hideLoading();
//                },error->{
//                    showErrorMessage(application.getString(R.string.newtwork_error));
//                    error.printStackTrace();
//                    hideLoading();
//                })
//        );
//    }
    public void getActivityRate(){
        showLoading();
        compositeDisposable.add(repository.getApiService().getActivityRate(DateUtils.convertToUTC(endDate.get()),DateUtils.convertToUTC(startDate.get()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if(response.isResult()){
                        if(response.getData().getTotalBookingDone()+response.getData().getTotalBookingCancel()==0){
                            return;
                        }
                        double complete = 0;
                        if(response.getData().getTotalBookingAccept()!=0){
                            complete = (double) response.getData().getTotalBookingDone()/(response.getData().getTotalBookingDone()+response.getData().getTotalBookingCancel())*100;
                        }
                        completeRate.set(String.format("%.0f",complete));
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
