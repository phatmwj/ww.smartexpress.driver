package ww.smartexpress.driver.ui.payout.details;

import ww.smartexpress.driver.BR;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.databinding.ActivityPayoutDetailsBinding;
import ww.smartexpress.driver.di.component.ActivityComponent;
import ww.smartexpress.driver.ui.base.activity.BaseActivity;

public class PayoutDetailsActivity extends BaseActivity<ActivityPayoutDetailsBinding, PayoutDetailsViewModel> {
    @Override
    public int getLayoutId() {
        return R.layout.activity_payout_details;
    }

    @Override
    public int getBindingVariable() {
        return BR.vm;
    }

    @Override
    public void performDependencyInjection(ActivityComponent buildComponent) {
        buildComponent.inject(this);
        Long id = getIntent().getLongExtra("payoutId",0);
        viewModel.getTransactionDetails(id);
    }
}
