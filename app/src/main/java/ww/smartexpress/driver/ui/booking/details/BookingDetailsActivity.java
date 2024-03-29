package ww.smartexpress.driver.ui.booking.details;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import eu.davidea.flexibleadapter.databinding.BR;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.databinding.ActivityBookingDetailsBinding;
import ww.smartexpress.driver.di.component.ActivityComponent;
import ww.smartexpress.driver.ui.base.activity.BaseActivity;

public class BookingDetailsActivity extends BaseActivity<ActivityBookingDetailsBinding,BookingDetailsViewModel> {
    @Override
    public int getLayoutId() {
        return R.layout.activity_booking_details;
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
        Long id = intent.getLongExtra("bookingId", 0L);
        if(id!= 0L){
            loadBooking(id);
        }
    }

    public void loadBooking(Long id){
        viewModel.showLoading();
        viewModel.compositeDisposable.add(viewModel.getBooking(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if(response.isResult()){
                        viewModel.booking.set(response.getData());
                        viewModel.date.set(response.getData().getCreatedDate());
                        if(response.getData().getRating() != null){
                            viewModel.star.set(response.getData().getRating().getStar());
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
}
