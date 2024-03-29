package ww.smartexpress.driver.ui.history;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ww.smartexpress.driver.BR;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.data.model.api.response.Booking;
import ww.smartexpress.driver.data.model.api.response.TripHistory;
import ww.smartexpress.driver.databinding.ActivityTripHistoryBinding;
import ww.smartexpress.driver.di.component.ActivityComponent;
import ww.smartexpress.driver.ui.base.activity.BaseActivity;
import ww.smartexpress.driver.ui.booking.details.BookingDetailsActivity;
import ww.smartexpress.driver.ui.history.adapter.TripHistoryAdapter;

public class TripHistoryActivity extends BaseActivity<ActivityTripHistoryBinding, TripHistoryViewModel> {
    private TripHistoryAdapter tripHistoryAdapter;
    private List<Booking> bookingList;
    @Override
    public int getLayoutId() {
        return R.layout.activity_trip_history;
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
        tripHistoryAdapter = new TripHistoryAdapter();
        bookingList = new ArrayList<>();
        viewModel.bookingList.observe(this, bookings -> {
            // Update UI or adapter when bookingList changes
            if(bookingList == null || bookingList.isEmpty()){
                bookingList = bookings;
            }else {
                bookingList.addAll(bookings);
            }
            tripHistoryAdapter.setBookings(bookingList);
            if(bookingList == null || bookingList.isEmpty()){
                viewModel.isEmpty.set(true);
            }
        });

        loadTripHistories();
    }

    public void loadTripHistories(){

        RecyclerView.LayoutManager layout = new LinearLayoutManager(this
                ,LinearLayoutManager.VERTICAL, false);
        viewBinding.rcTripHistory.setLayoutManager(layout);
        viewBinding.rcTripHistory.setItemAnimator(new DefaultItemAnimator());
        viewBinding.rcTripHistory.setAdapter(tripHistoryAdapter);

        tripHistoryAdapter.setOnItemClickListener(new TripHistoryAdapter.OnItemClickListener() {
            @Override
            public void itemClick(Booking booking) {
                Intent intent = new Intent(getApplicationContext(), BookingDetailsActivity.class);
                intent.putExtra("bookingId",booking.getId());
                startActivity(intent);
            }
        });

    }
}
