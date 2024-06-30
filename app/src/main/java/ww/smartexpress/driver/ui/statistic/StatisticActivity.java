package ww.smartexpress.driver.ui.statistic;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.android.material.tabs.TabLayout;

import ww.smartexpress.driver.BR;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.databinding.ActivityStatisticBinding;
import ww.smartexpress.driver.di.component.ActivityComponent;
import ww.smartexpress.driver.ui.base.activity.BaseActivity;

public class StatisticActivity extends BaseActivity<ActivityStatisticBinding, StatisticViewModel> {
    @Override
    public int getLayoutId() {
        return R.layout.activity_statistic;
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

        viewBinding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Xử lý khi tab được chọn
                switch (tab.getPosition()) {
                    case 0:
                        viewModel.statisticDate();
                        break;
                    case 1:
                        viewModel.statisticWeek();
                        break;
                    case 2:
                        viewModel.statisticMonth();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Xử lý khi tab không được chọn
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Xử lý khi tab được chọn lại
                switch (tab.getPosition()) {
                    case 0:
                        viewModel.statisticDate();
                        break;
                    case 1:
                        viewModel.statisticWeek();
                        break;
                    case 2:
                        viewModel.statisticMonth();
                        break;
                    default:
                        break;
                }
            }
        });
    }
}
