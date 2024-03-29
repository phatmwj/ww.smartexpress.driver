package ww.smartexpress.driver.ui.fragment.income;

import android.content.Intent;

import androidx.databinding.ObservableField;

import java.util.Calendar;
import java.util.Date;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ww.smartexpress.driver.MVVMApplication;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.data.Repository;
import ww.smartexpress.driver.data.model.api.request.IncomeRequest;
import ww.smartexpress.driver.data.model.api.response.IncomeResponse;
import ww.smartexpress.driver.ui.base.fragment.BaseFragmentViewModel;
import ww.smartexpress.driver.ui.fragment.income.details.IncomeDetailsActivity;
import ww.smartexpress.driver.utils.DateUtils;

public class IncomeFragmentViewModel extends BaseFragmentViewModel {
    public ObservableField<Integer> incomeTime = new ObservableField<>(0);
    public ObservableField<String> incomeDateString = new ObservableField<>();
    public ObservableField<Date> currentDate = new ObservableField<>(new Date());
    public ObservableField<String> startDate = new ObservableField<>();
    public ObservableField<String> endDate = new ObservableField<>();
    public ObservableField<IncomeResponse> income = new ObservableField<>();
    public ObservableField<Integer> bookingState = new ObservableField<>(300);
    public ObservableField<Double> totalIncome = new ObservableField<>(0.0);
    public IncomeFragmentViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
        statistic();
    }

//    public void initDateTime(){
//        Date currentD = new Date();
//        currentDate.set(DateUtils.dateOnlyFormat(currentD));
//    }

    public void incomeDate(){
        currentDate.set(new Date());
        incomeTime.set(0);
        statistic();
    }
    public void incomeMonth(){
        currentDate.set(new Date());
        incomeTime.set(2);
        statistic();
    }
    public void incomeWeek(){
        currentDate.set(new Date());
        incomeTime.set(1);
        statistic();
    }

    public void incomeDetails(){
        Intent intent = new Intent(application.getCurrentActivity(), IncomeDetailsActivity.class);
        intent.putExtra("incomeTime",incomeTime.get());
        long timestamp = currentDate.get().getTime();
        intent.putExtra("currentDate",timestamp);
        application.getCurrentActivity().startActivity(intent);
    }

    public void statistic(){
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
        showLoading();
        compositeDisposable.add(repository.getApiService().statisticIncome(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if(response.isResult()){
                        income.set(response.getData());
//                        totalIncome.set(response.getData().to);
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

    public void doNext(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate.get());
        if(incomeTime.get()== 0){
            calendar.add(Calendar.DAY_OF_MONTH,1);
            currentDate.set(calendar.getTime());
        }
        if(incomeTime.get()==1){
            calendar.add(Calendar.WEEK_OF_YEAR,1);
            currentDate.set(calendar.getTime());
        }
        if(incomeTime.get()==2){
            calendar.add(Calendar.MONTH,1);
            currentDate.set(calendar.getTime());
        }
        statistic();
    }
    public void doAfter(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate.get());
        if(incomeTime.get()== 0){
            calendar.add(Calendar.DAY_OF_MONTH,-1);
            currentDate.set(calendar.getTime());
        }
        if(incomeTime.get()==1){
            calendar.add(Calendar.WEEK_OF_YEAR,-1);
            currentDate.set(calendar.getTime());
        }
        if(incomeTime.get()==2){
            calendar.add(Calendar.MONTH,-1);
            currentDate.set(calendar.getTime());
        }
        statistic();
    }
}
