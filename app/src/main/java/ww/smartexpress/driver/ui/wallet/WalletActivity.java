package ww.smartexpress.driver.ui.wallet;

import ww.smartexpress.driver.BR;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.databinding.ActivityWalletBinding;
import ww.smartexpress.driver.di.component.ActivityComponent;
import ww.smartexpress.driver.ui.base.activity.BaseActivity;

public class WalletActivity extends BaseActivity<ActivityWalletBinding, WalletViewModel> {
    @Override
    public int getLayoutId() {
        return R.layout.activity_wallet;
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
