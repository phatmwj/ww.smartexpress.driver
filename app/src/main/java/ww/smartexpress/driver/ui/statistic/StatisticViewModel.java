package ww.smartexpress.driver.ui.statistic;

import android.util.Log;

import androidx.databinding.ObservableField;

import java.util.Calendar;
import java.util.Date;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ww.smartexpress.driver.MVVMApplication;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.data.Repository;
import ww.smartexpress.driver.data.model.api.request.IncomeRequest;
import ww.smartexpress.driver.ui.base.activity.BaseViewModel;
import ww.smartexpress.driver.utils.DateUtils;

public class StatisticViewModel extends BaseViewModel {

    public ObservableField<Integer> statisticTime = new ObservableField<>(0);
    public ObservableField<String> incomeDateString = new ObservableField<>();
    public ObservableField<Date> currentDate = new ObservableField<>(new Date());
    public ObservableField<String> startDate = new ObservableField<>();
    public ObservableField<String> endDate = new ObservableField<>();
    public ObservableField<String> cancelRate = new ObservableField<>("0");
    public ObservableField<String> completeRate = new ObservableField<>("0");
    public ObservableField<String> acceptRate = new ObservableField<>("0");
    public StatisticViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
        statistic();
    }
    public void back(){
        application.getCurrentActivity().onBackPressed();
    }

    public void statisticDate(){
        currentDate.set(new Date());
        statisticTime.set(0);
        statistic();
    }
    public void statisticMonth(){
        currentDate.set(new Date());
        statisticTime.set(2);
        statistic();
    }
    public void statisticWeek(){
        currentDate.set(new Date());
        statisticTime.set(1);
        statistic();
    }

    public void statistic(){
        cancelRate.set("0");
        acceptRate.set("0");
        completeRate.set("0");
        if(statisticTime.get()== 0){
            startDate.set(DateUtils.dateStartFormat(currentDate.get()));
            endDate.set(DateUtils.dateEndFormat(currentDate.get()));
            incomeDateString.set((DateUtils.dateOnlyFormat(startDate.get())));
        }
        if(statisticTime.get()==1){
            startDate.set(DateUtils.startWeekFormat(currentDate.get()));
            endDate.set(DateUtils.endWeekFormat(currentDate.get()));
            incomeDateString.set("Từ "+DateUtils.dateOnlyFormat(startDate.get())+ " đến "+DateUtils.dateOnlyFormat(endDate.get()));
        }
        if(statisticTime.get()==2){
            startDate.set(DateUtils.startMonthFormat(currentDate.get()));
            endDate.set(DateUtils.endMonthFormat(currentDate.get()));
            incomeDateString.set("Từ "+DateUtils.dateOnlyFormat(startDate.get())+ " đến "+DateUtils.dateOnlyFormat(endDate.get()));
        }
        showLoading();
        compositeDisposable.add(repository.getApiService().getActivityRate(DateUtils.convertToUTC(endDate.get()),DateUtils.convertToUTC(startDate.get()))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                            if(response.isResult()){
                                int total = response.getData().getTotalBookingAccept()+response.getData().getTotalBookingCancel();
                                Log.d("TAG", "statistic: "+total);
                                if(total==0){
                                    hideLoading();
                                    return;
                                }
                                double accept= (double) response.getData().getTotalBookingAccept()/total*100;
                                double complete = 0;
                                if(response.getData().getTotalBookingAccept()!=0){
                                    complete = (double) response.getData().getTotalBookingSuccess()/response.getData().getTotalBookingAccept()*100;
                                }
                                double cancel= (double) response.getData().getTotalBookingCancel()/total*100;
                                cancelRate.set(String.format("%.0f",cancel));
                                acceptRate.set(String.format("%.0f",accept));
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

    public void doNext(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate.get());
        if(statisticTime.get()== 0){
            calendar.add(Calendar.DAY_OF_MONTH,1);
            currentDate.set(calendar.getTime());
        }
        if(statisticTime.get()==1){
            calendar.add(Calendar.WEEK_OF_YEAR,1);
            currentDate.set(calendar.getTime());
        }
        if(statisticTime.get()==2){
            calendar.add(Calendar.MONTH,1);
            currentDate.set(calendar.getTime());
        }
        statistic();
    }
    public void doAfter(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate.get());
        if(statisticTime.get()== 0){
            calendar.add(Calendar.DAY_OF_MONTH,-1);
            currentDate.set(calendar.getTime());
        }
        if(statisticTime.get()==1){
            calendar.add(Calendar.WEEK_OF_YEAR,-1);
            currentDate.set(calendar.getTime());
        }
        if(statisticTime.get()==2){
            calendar.add(Calendar.MONTH,-1);
            currentDate.set(calendar.getTime());
        }
        statistic();
    }
}
