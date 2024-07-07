package ww.smartexpress.driver.ui.wallet.transaction.details;

import android.os.Bundle;

import androidx.annotation.Nullable;

import ww.smartexpress.driver.BR;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.databinding.ActivityTransactionDetailBinding;
import ww.smartexpress.driver.di.component.ActivityComponent;
import ww.smartexpress.driver.ui.base.activity.BaseActivity;

public class TransactionDetailsActivity extends BaseActivity<ActivityTransactionDetailBinding, TransactionDetailsViewModel> {
    @Override
    public int getLayoutId() {
        return R.layout.activity_transaction_detail;
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
        Long id = getIntent().getLongExtra("transactionId",0);
        viewModel.getTransactionDetails(id);
    }
}
