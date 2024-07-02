package ww.smartexpress.driver.ui.fragment.income.details;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ww.smartexpress.driver.BR;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.data.model.api.response.Booking;
import ww.smartexpress.driver.databinding.ActivityIncomeDetailsBinding;
import ww.smartexpress.driver.di.component.ActivityComponent;
import ww.smartexpress.driver.ui.base.activity.BaseActivity;
import ww.smartexpress.driver.ui.fragment.income.details.adapter.TripAdapter;
import ww.smartexpress.driver.utils.DateUtils;

public class IncomeDetailsActivity extends BaseActivity<ActivityIncomeDetailsBinding, IncomeDetailsViewModel> {

    TripAdapter tripAdapter;
    List<Double> moneyList;

    @Override
    public int getLayoutId() {
        return R.layout.activity_income_details;
    }

    @Override
    public int getBindingVariable() {
        return BR.vm;
    }

    @Override
    public void performDependencyInjection(ActivityComponent buildComponent) {
        buildComponent.inject(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewBinding.setLifecycleOwner(this);
        tripAdapter = new TripAdapter();

        Intent intent = getIntent();
        int incomeStatistic = intent.getIntExtra("incomeTime",0);
        long timestamp = intent.getLongExtra("currentDate", 0);
        viewModel.incomeTime.set(incomeStatistic);
        viewModel.currentDate.set(new Date(timestamp));

        if(incomeStatistic == 0){
            viewModel.incomeTimeString.set("Ngày");
            viewBinding.layoutChart.setVisibility(View.GONE);
        }else if(incomeStatistic == 1){
            viewModel.incomeTimeString.set("Tuần");
            viewBinding.layoutChart.setVisibility(View.VISIBLE);
        }else if(incomeStatistic == 2){
            viewModel.incomeTimeString.set("Tháng");
            viewBinding.layoutChart.setVisibility(View.VISIBLE);
        }
        viewModel.bookingList.observe(this, bookings -> {
            if(bookings == null){
                moneyList = new ArrayList<>();
            }
            try {
                if(viewModel.incomeTime.get() == 1){
                    loadStatistic2(noOfEmpWeek(),labelsWeek());
                } else if(viewModel.incomeTime.get() == 2){
                    loadStatistic2(noOfEmpMonth(), labelsMonth());
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });

        getIncome();
    }

    private void getIncome(){
        viewModel.showLoading();
        viewModel.compositeDisposable.add(viewModel.getIncomeDetails()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if(response.isResult()){
                        viewModel.income.set(response.getData());
                        viewModel.getActivityRate();
                        getMyBooking();
                    }else {
                        viewModel.showErrorMessage(response.getMessage());
                        viewModel.hideLoading();
                    }
                },error->{
                    viewModel.showErrorMessage(application.getString(R.string.newtwork_error));
                    error.printStackTrace();
                    viewModel.hideLoading();
                })
        );
    }

    public void loadTrips(){
        RecyclerView.LayoutManager layout = new LinearLayoutManager(this
                ,LinearLayoutManager.VERTICAL, false);
        viewBinding.rcTripHistory.setLayoutManager(layout);
        viewBinding.rcTripHistory.setItemAnimator(new DefaultItemAnimator());
        viewBinding.rcTripHistory.setAdapter(tripAdapter);
        tripAdapter.setOnItemClickListener(trip -> {

        });
    }

    @SuppressLint("ResourceAsColor")
    private void loadStatistic2(ArrayList noOfEmp, ArrayList<String> labels){

        //
        LineDataSet bardataset = new LineDataSet(noOfEmp, "No Of Employee");
        bardataset.setDrawValues(false);
        bardataset.setDrawIcons(false);
        bardataset.setColors(ContextCompat.getColor(this,R.color.green_light_app));
        LineData data = new LineData(bardataset);
        bardataset.setValueTextColor(ContextCompat.getColor(this,R.color.bar_chart_label));

        //
        XAxis xAxis = viewBinding.linechart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(labels.size());
        xAxis.setTextColor(ContextCompat.getColor(this,R.color.bar_chart_label));
        xAxis.setTypeface(ResourcesCompat.getFont(this, R.font.montserrat_medium));
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);

        //

        YAxis leftYAxis = viewBinding.linechart.getAxisLeft();
        leftYAxis.setDrawAxisLine(true);
        leftYAxis.setDrawGridLines(false);
        leftYAxis.setDrawLabels(true);
        leftYAxis.setAxisMinimum(0);
        leftYAxis.setTextColor(ContextCompat.getColor(this,R.color.bar_chart_label));

        YAxis rightYAxis = viewBinding.linechart.getAxisRight();
        rightYAxis.setDrawAxisLine(false);
        rightYAxis.setDrawGridLines(false);
        rightYAxis.setDrawLabels(false);
        rightYAxis.setTextColor(ContextCompat.getColor(this,R.color.bar_chart_label));

        //
        Description description = new Description();
        description.setText("(t)");
        viewBinding.linechart.setDescription(description);
        viewBinding.linechart.getDescription().setEnabled(true);

        viewBinding.linechart.setHighlightPerTapEnabled(false);
        viewBinding.linechart.setDragEnabled(false);
        viewBinding.linechart.animateY(0);
        viewBinding.linechart.setScaleEnabled(false);
        viewBinding.linechart.getLegend().setEnabled(false);
        viewBinding.linechart.setData(data);

        viewBinding.linechart.setNoDataText("");
        viewBinding.linechart.setNoDataTextColor(R.color.white);
        Paint p = viewBinding.linechart.getPaint(Chart.PAINT_INFO);
        p.setColor(R.color.white);
        viewBinding.linechart.invalidate();

    }
    public void staticIncomeInWeek() throws ParseException {
        if(viewModel.bookingList.getValue()==null){
            return;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(viewModel.currentDate.get());
        double totalDateMoney;
        moneyList =new ArrayList<>();
        for(int i = 0; i<7; i++){
            calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
            calendar.add(Calendar.DAY_OF_MONTH, i);
            Date  date = calendar.getTime();
            totalDateMoney = 0;
            for(Booking booking : viewModel.bookingList.getValue()){
                if(isDateInRange(booking.getCreatedDate(), DateUtils.dateStartFormat(date),DateUtils.dateEndFormat(date))){
                    totalDateMoney += booking.getMoney()*booking.getRatioShare()/100;
                }
            }
            moneyList.add(totalDateMoney);
        }

        for(int i = 0; i< moneyList.size(); i++){
            Log.d("TAG", "staticIncomeInWeek: "+ moneyList.get(i));
        }
    }

    public void getMyBooking(){
        viewModel.showLoading();
        viewModel.compositeDisposable.add(viewModel.getBookings()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if(response.isResult()){
                        viewModel.bookingList.setValue(response.getData().getContent());
                        if(response.getData().getContent() != null && response.getData().getContent().size()!= 0){
                            if(viewModel.pageNumber.get() == 0){
                                tripAdapter.setBookings(response.getData().getContent());
                                loadTrips();
                            }else {

                            }
                        }else {
                            tripAdapter.setBookings(response.getData().getContent());
                            loadTrips();
                        }
                    }else {
                        viewModel.showErrorMessage(response.getMessage());
                    }
                    viewModel.hideLoading();
                },error->{
                    viewModel.showErrorMessage(application.getString(R.string.newtwork_error));
                    error.printStackTrace();
                    viewModel.hideLoading();
                })
        );
    }

    public void statisticIncomeInMonth() throws ParseException {
        if(viewModel.bookingList.getValue()==null){
            return;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(viewModel.currentDate.get());
        double totalDateMoney;
        moneyList = new ArrayList<>();
        for(int i = 0; i<4; i++){
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.add(Calendar.WEEK_OF_YEAR, i);
            Date  date = calendar.getTime();
            totalDateMoney = 0;
            for(Booking booking : viewModel.bookingList.getValue()){
                if(isDateInRange(booking.getCreatedDate(), DateUtils.startWeekFormat(date),DateUtils.endWeekFormat(date))){
                    totalDateMoney += booking.getMoney()*booking.getRatioShare()/100;
                }
            }
            moneyList.add(totalDateMoney);
        }
        for(int i = 0; i< moneyList.size(); i++){
            Log.d("TAG", "staticIncomeInWeek: "+ moneyList.get(i));
        }
    }
    private boolean isDateInRange(String date, String startDate, String endDate) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        // Chuyển đổi chuỗi ngày thành đối tượng Date
        Date dateToCheck = dateFormat.parse(date);
        Date startDate1 = dateFormat.parse(DateUtils.convertToUTC(startDate));
        Date endDate1 = dateFormat.parse(DateUtils.convertToUTC(endDate));
        return dateToCheck.compareTo(startDate1) >= 0 && dateToCheck.compareTo(endDate1) <= 0;
    }

    private ArrayList noOfEmpWeek() throws ParseException {
//        staticIncomeInWeek();
        ArrayList noOfEmp = new ArrayList();
        List<Double> moneyOfDate = viewModel.income.get().getTotalMoneyOfDays();
//        if( moneyList.size() == 0){
//            noOfEmp.add(new BarEntry(0f,0));
//            noOfEmp.add(new BarEntry(1f, 0));
//            noOfEmp.add(new BarEntry(2f, 0));
//            noOfEmp.add(new BarEntry(3f, 0));
//            noOfEmp.add(new BarEntry(4f,0));
//            noOfEmp.add(new BarEntry(5f, 0));
//            noOfEmp.add(new BarEntry(6f, 0));
//        }else{
            noOfEmp.add(new BarEntry(0f,moneyOfDate.get(0).longValue()));
            noOfEmp.add(new BarEntry(1f, moneyOfDate.get(1).longValue()));
            noOfEmp.add(new BarEntry(2f, moneyOfDate.get(2).longValue()));
            noOfEmp.add(new BarEntry(3f, moneyOfDate.get(3).longValue()));
            noOfEmp.add(new BarEntry(4f, moneyOfDate.get(4).longValue()));
            noOfEmp.add(new BarEntry(5f, moneyOfDate.get(5).longValue()));
            noOfEmp.add(new BarEntry(6f, moneyOfDate.get(6).longValue()));
//        }
        return noOfEmp;
    }
    private ArrayList noOfEmpMonth() throws ParseException {
//        statisticIncomeInMonth();
        ArrayList noOfEmp = new ArrayList();
        List<Double> moneyOfDate = viewModel.income.get().getTotalMoneyOfDays();
        Double moneyOfWeed1 = 0.0;
        Double moneyOfWeed2 = 0.0;
        Double moneyOfWeed3 = 0.0;
        Double moneyOfWeed4 = 0.0;
        for(int i=0; i<7;i++){
            moneyOfWeed1 += moneyOfDate.get(i);
        }
        for(int i=7; i<14;i++){
            moneyOfWeed2 += moneyOfDate.get(i);
        }
        for(int i=14; i<21;i++){
            moneyOfWeed3 += moneyOfDate.get(i);
        }
        moneyOfWeed4 = viewModel.income.get().getTotalMoney() - moneyOfWeed2 - moneyOfWeed1 - moneyOfWeed3;
//        if( moneyList.size() == 0){
//            noOfEmp.add(new BarEntry(0f,0));
//            noOfEmp.add(new BarEntry(1f, 0));
//            noOfEmp.add(new BarEntry(2f, 0));
//            noOfEmp.add(new BarEntry(3f, 0));
//        }else {

            noOfEmp.add(new BarEntry(0f, moneyOfWeed1.longValue()));
            noOfEmp.add(new BarEntry(1f, moneyOfWeed2.longValue()));
            noOfEmp.add(new BarEntry(2f, moneyOfWeed3.longValue()));
            noOfEmp.add(new BarEntry(3f, moneyOfWeed4.longValue()));
//        }
        return noOfEmp;
    }

    private ArrayList<String> labelsWeek(){
        ArrayList<String> labels = new ArrayList<>();
        labels.add("Mon");
        labels.add("Tue");
        labels.add("Wed");
        labels.add("Thu");
        labels.add("Fri");
        labels.add("Sat");
        labels.add("Sun");
        return labels;
    }

    private ArrayList<String> labelsMonth(){
        ArrayList<String> labels = new ArrayList<>();
        labels.add("Tuần 1");
        labels.add("Tuần 2");
        labels.add("Tuần 3");
        labels.add("Tuần 4");
        return labels;
    }

    public void doNext(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(viewModel.currentDate.get());
        if(viewModel.incomeTime.get()== 0){
            calendar.add(Calendar.DAY_OF_MONTH,1);
            viewModel.currentDate.set(calendar.getTime());
        }
        if(viewModel.incomeTime.get()==1){
            calendar.add(Calendar.WEEK_OF_YEAR,1);
            viewModel.currentDate.set(calendar.getTime());
        }
        if(viewModel.incomeTime.get()==2){
            calendar.add(Calendar.MONTH,1);
            viewModel.currentDate.set(calendar.getTime());
        }
//        statistic();
        getIncome();
    }
    public void doAfter(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(viewModel.currentDate.get());
        if(viewModel.incomeTime.get()== 0){
            calendar.add(Calendar.DAY_OF_MONTH,-1);
            viewModel.currentDate.set(calendar.getTime());
        }
        if(viewModel.incomeTime.get()==1){
            calendar.add(Calendar.WEEK_OF_YEAR,-1);
            viewModel.currentDate.set(calendar.getTime());
        }
        if(viewModel.incomeTime.get()==2){
            calendar.add(Calendar.MONTH,-1);
            viewModel.currentDate.set(calendar.getTime());
        }
//        statistic();
        getIncome();
    }
}
