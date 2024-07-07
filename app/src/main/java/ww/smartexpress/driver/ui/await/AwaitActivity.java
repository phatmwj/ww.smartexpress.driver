package ww.smartexpress.driver.ui.await;

import ww.smartexpress.driver.BR;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.databinding.ActivityAwaitBinding;
import ww.smartexpress.driver.di.component.ActivityComponent;
import ww.smartexpress.driver.ui.base.activity.BaseActivity;

public class AwaitActivity extends BaseActivity<ActivityAwaitBinding, AwaitViewModel> {
    @Override
    public int getLayoutId() {
        return R.layout.activity_await;
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
