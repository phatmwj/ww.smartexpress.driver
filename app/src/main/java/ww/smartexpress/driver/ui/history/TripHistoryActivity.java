package ww.smartexpress.driver.ui.history;

import static android.nfc.tech.MifareUltralight.PAGE_SIZE;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ww.smartexpress.driver.BR;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.data.model.api.response.Booking;
import ww.smartexpress.driver.data.model.api.response.TripHistory;
import ww.smartexpress.driver.databinding.ActivityTripHistoryBinding;
import ww.smartexpress.driver.di.component.ActivityComponent;
import ww.smartexpress.driver.ui.base.activity.BaseActivity;
import ww.smartexpress.driver.ui.booking.details.BookingDetailsActivity;
import ww.smartexpress.driver.ui.history.adapter.TripHistoryAdapter;
import ww.smartexpress.driver.ui.view.EndlessRecyclerViewScrollListener;

public class TripHistoryActivity extends BaseActivity<ActivityTripHistoryBinding, TripHistoryViewModel> {
    private TripHistoryAdapter tripHistoryAdapter;
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
        getMyBooking();

    }


    public void getMyBooking(){
        if(viewModel.pageNumber.get() == 0){
            viewModel.showLoading();
        }
        viewModel.compositeDisposable.add(viewModel.getBookings()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if(response.isResult()){
                        viewModel.pageTotal.set(response.getData().getTotalPages());
                        viewModel.totalItem.set(response.getData().getTotalElements().intValue());
                        if(tripHistoryAdapter.getItemCount() == 0){
                            tripHistoryAdapter.setBookings(response.getData().getContent());
                            loadTripHistories();
                        }else {
                            tripHistoryAdapter.addListBooking(response.getData().getContent());
                        }
                        if(tripHistoryAdapter.getItemCount() == 0){
                            viewModel.isEmpty.set(true);
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

    public void loadTripHistories(){

        RecyclerView.LayoutManager layout = new LinearLayoutManager(this
                ,LinearLayoutManager.VERTICAL, false);
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

        EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener
                = new EndlessRecyclerViewScrollListener((LinearLayoutManager) layout) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {

//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
////                        Toast.makeText(view.getContext(), "Loading More ...",
////                                Toast.LENGTH_SHORT).show();
//
////                        List<Student> list = new ArrayList<Student>();
////                        for (int i = 0; i <= 5; i++) {
////                            list.add(new Student("Mới "+ i, 1988));
////                        }
////                        students.addAll(list);
////                        adapter.notifyDataSetChanged();
//                    }
//                }, 1500);
                int pageCurrent = viewModel.pageNumber.get();
                viewModel.pageNumber.set(pageCurrent+1);
                getMyBooking();

            }
        };
//Thêm Listener vào
        viewBinding.rcTripHistory.addOnScrollListener(endlessRecyclerViewScrollListener);

        viewBinding.rcTripHistory.setLayoutManager(layout);

    }
}
