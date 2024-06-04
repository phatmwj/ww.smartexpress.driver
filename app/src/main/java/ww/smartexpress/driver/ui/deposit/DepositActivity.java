package ww.smartexpress.driver.ui.deposit;

import android.content.Intent;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import eu.davidea.flexibleadapter.databinding.BR;
import vn.momo.momo_partner.AppMoMoLib;
import vn.momo.momo_partner.MoMoParameterNamePayment;
import vn.momo.momo_partner.utils.MoMoConfig;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.databinding.ActivityDepositBinding;
import ww.smartexpress.driver.databinding.ActivityLoginBinding;
import ww.smartexpress.driver.di.component.ActivityComponent;
import ww.smartexpress.driver.ui.base.activity.BaseActivity;

public class DepositActivity extends BaseActivity<ActivityDepositBinding, DepositViewModel> {

    @Override
    public int getLayoutId() {
        return R.layout.activity_deposit;
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
