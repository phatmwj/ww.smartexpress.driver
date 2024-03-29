package ww.smartexpress.driver.ui.fragment.income.details;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ww.smartexpress.driver.BR;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.data.model.api.response.Booking;
import ww.smartexpress.driver.data.model.api.response.Trip;
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

        Intent intent = getIntent();
        int incomeStatistic = intent.getIntExtra("incomeTime",0);
        long timestamp = intent.getLongExtra("currentDate", 0);
        viewModel.incomeTime.set(incomeStatistic);
        viewModel.currentDate.set(new Date(timestamp));

        if(incomeStatistic == 0){
            viewModel.incomeTimeString.set("Ngày");
            viewBinding.barchart.setVisibility(View.GONE);
            viewBinding.underlineBarChart.setVisibility(View.GONE);
        }else if(incomeStatistic == 1){
            viewModel.incomeTimeString.set("Tuần");
            viewBinding.barchart.setVisibility(View.VISIBLE);
            viewBinding.underlineBarChart.setVisibility(View.VISIBLE);
//            loadStatistic(noOfEmpWeek(), labelsWeek());
        }else if(incomeStatistic == 2){
            viewModel.incomeTimeString.set("Tháng");
            viewBinding.barchart.setVisibility(View.VISIBLE);
            viewBinding.underlineBarChart.setVisibility(View.VISIBLE);
//            loadStatistic(noOfEmpMonth(), labelsMonth());
        }
        viewModel.statistic();
        tripAdapter = new TripAdapter();
        viewModel.bookingList.observe(this, bookings -> {
            tripAdapter.setBookings(bookings);
            if(bookings == null){
                moneyList = new ArrayList<>();
            }
            try {
                if(viewModel.incomeTime.get() == 1){
                    loadStatistic(noOfEmpWeek(), labelsWeek());
                } else if(viewModel.incomeTime.get() == 2){
                    loadStatistic(noOfEmpMonth(), labelsMonth());
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });
        loadTrips();

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


    private void loadStatistic(ArrayList noOfEmp, ArrayList<String> labels){

        //
        BarDataSet bardataset = new BarDataSet(noOfEmp, "No Of Employee");
        bardataset.setDrawValues(false);
        bardataset.setDrawIcons(false);
        bardataset.setColors(ContextCompat.getColor(this,R.color.bar_chart));
        BarData data = new BarData(bardataset);
        bardataset.setValueTextColor(ContextCompat.getColor(this,R.color.bar_chart_label));

        //
        XAxis xAxis = viewBinding.barchart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(labels.size());
        xAxis.setTextColor(ContextCompat.getColor(this,R.color.bar_chart_label));
        xAxis.setTypeface(ResourcesCompat.getFont(this, R.font.roboto_medium));
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);

        //

        YAxis leftYAxis = viewBinding.barchart.getAxisLeft();
        leftYAxis.setDrawAxisLine(false);
        leftYAxis.setDrawGridLines(false);
        leftYAxis.setDrawLabels(false);
        leftYAxis.setTextColor(ContextCompat.getColor(this,R.color.bar_chart_label));
        YAxis rightYAxis = viewBinding.barchart.getAxisRight();
        rightYAxis.setDrawAxisLine(false);
        rightYAxis.setDrawGridLines(false);
        rightYAxis.setDrawLabels(false);
        rightYAxis.setTextColor(ContextCompat.getColor(this,R.color.bar_chart_label));

        //
        viewBinding.barchart.getDescription().setEnabled(false);
        viewBinding.barchart.setHighlightPerTapEnabled(false);
        viewBinding.barchart.setDragEnabled(false);
        viewBinding.barchart.animateY(1000);
        viewBinding.barchart.setScaleEnabled(false);
        viewBinding.barchart.getLegend().setEnabled(false);
        viewBinding.barchart.setData(data);
        viewBinding.barchart.invalidate();

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
                    totalDateMoney += booking.getMoney();
                }
            }
            moneyList.add(totalDateMoney);
        }
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
                    totalDateMoney += booking.getMoney();
                }
            }
            moneyList.add(totalDateMoney);
        }
        Log.d("TAG", "statisticIncomeInMonth: "+ moneyList.toArray());
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
        staticIncomeInWeek();
        ArrayList noOfEmp = new ArrayList();
        if( moneyList.size() == 0){
            noOfEmp.add(new BarEntry(0f,0));
            noOfEmp.add(new BarEntry(1f, 0));
            noOfEmp.add(new BarEntry(2f, 0));
            noOfEmp.add(new BarEntry(3f, 0));
            noOfEmp.add(new BarEntry(4f,0));
            noOfEmp.add(new BarEntry(5f, 0));
            noOfEmp.add(new BarEntry(6f, 0));
        }else{
            noOfEmp.add(new BarEntry(0f,moneyList.get(0).longValue()));
            noOfEmp.add(new BarEntry(1f, moneyList.get(1).longValue()));
            noOfEmp.add(new BarEntry(2f, moneyList.get(2).longValue()));
            noOfEmp.add(new BarEntry(3f, moneyList.get(3).longValue()));
            noOfEmp.add(new BarEntry(4f, moneyList.get(4).longValue()));
            noOfEmp.add(new BarEntry(5f, moneyList.get(5).longValue()));
            noOfEmp.add(new BarEntry(6f, moneyList.get(6).longValue()));
        }
        return noOfEmp;
    }
    private ArrayList noOfEmpMonth() throws ParseException {
        statisticIncomeInMonth();
        ArrayList noOfEmp = new ArrayList();
        if( moneyList.size() == 0){
            noOfEmp.add(new BarEntry(0f,0));
            noOfEmp.add(new BarEntry(1f, 0));
            noOfEmp.add(new BarEntry(2f, 0));
            noOfEmp.add(new BarEntry(3f, 0));
        }else {
            noOfEmp.add(new BarEntry(0f, moneyList.get(0).longValue()));
            noOfEmp.add(new BarEntry(1f, moneyList.get(1).longValue()));
            noOfEmp.add(new BarEntry(2f, moneyList.get(2).longValue()));
            noOfEmp.add(new BarEntry(3f, moneyList.get(3).longValue()));
        }
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
}
