package ww.smartexpress.driver.ui.allbike;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ww.smartexpress.driver.BR;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.data.model.api.request.ChangeStateRequest;
import ww.smartexpress.driver.data.model.api.response.DriverServiceResponse;
import ww.smartexpress.driver.data.model.api.response.RateType;
import ww.smartexpress.driver.data.model.api.response.ServiceResponse;
import ww.smartexpress.driver.databinding.ActivityAllbikeServiceBinding;
import ww.smartexpress.driver.di.component.ActivityComponent;
import ww.smartexpress.driver.ui.achievement.adapter.AchievementRateTypeAdapter;
import ww.smartexpress.driver.ui.allbike.adapter.ServiceAdapter;
import ww.smartexpress.driver.ui.base.activity.BaseActivity;

public class AllBikeServiceActivity extends BaseActivity<ActivityAllbikeServiceBinding, AllBikeServiceViewModel> {
    
    private ServiceAdapter serviceAdapter;
    @Override
    public int getLayoutId() {
        return R.layout.activity_allbike_service;
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
        getDriverService();

    }

    public void loadService(){

        RecyclerView.LayoutManager layout = new LinearLayoutManager(this
                ,LinearLayoutManager.VERTICAL, false);

        viewBinding.rcService.setLayoutManager(layout);
        viewBinding.rcService.setItemAnimator(new DefaultItemAnimator());
        viewBinding.rcService.setAdapter(serviceAdapter);

        serviceAdapter.setOnItemClickListener(new ServiceAdapter.OnItemClickListener() {
            @Override
            public void itemClick(int position) {

            }
            @Override
            public void changeStateService(DriverServiceResponse serviceResponse, int newState) {
                ChangeStateRequest request = new ChangeStateRequest();
                request.setDriverServiceId(serviceResponse.getId());
                request.setNewState(newState);
                AllBikeServiceActivity.this.changeStateService(request);
            }
        });

    }

    public void getDriverService() {
        viewModel.showLoading();
        viewModel.compositeDisposable.add(viewModel.getService()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response ->{
                    if(response.isResult()){
                        viewModel.services1.set(response.getData().getContent());
                        serviceAdapter = new ServiceAdapter(viewModel.services1.get());
                        loadService();
                    }else {
                        viewModel.showErrorMessage(response.getMessage());
                    }
                    viewModel.hideLoading();
                }, err -> {
                    viewModel.hideLoading();
                    viewModel.showErrorMessage(application.getString(R.string.newtwork_error));
                    err.printStackTrace();
                }));
    }

    public void changeStateService(ChangeStateRequest request){
        viewModel.compositeDisposable.add(viewModel.changeStateService(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response ->{
                    if(response.isResult()){
                        viewModel.showSuccessMessage(response.getMessage());
                    }else {
                        viewModel.showErrorMessage(response.getMessage());
                    }
                }, err -> {
                    viewModel.showErrorMessage(application.getString(R.string.newtwork_error));
                    err.printStackTrace();
                }));
    }
}
