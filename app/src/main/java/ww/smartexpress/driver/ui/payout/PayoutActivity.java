package ww.smartexpress.driver.ui.payout;

import ww.smartexpress.driver.BR;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.databinding.ActivityPayoutBinding;
import ww.smartexpress.driver.di.component.ActivityComponent;
import ww.smartexpress.driver.ui.base.activity.BaseActivity;

public class PayoutActivity extends BaseActivity<ActivityPayoutBinding, PayoutViewModel> {
    @Override
    public int getLayoutId() {
        return R.layout.activity_payout;
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
