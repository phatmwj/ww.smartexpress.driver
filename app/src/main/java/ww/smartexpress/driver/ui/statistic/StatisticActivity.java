package ww.smartexpress.driver.ui.statistic;

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
}
