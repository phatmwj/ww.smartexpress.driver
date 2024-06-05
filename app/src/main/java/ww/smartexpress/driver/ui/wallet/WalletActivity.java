package ww.smartexpress.driver.ui.wallet;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import ww.smartexpress.driver.BR;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.data.model.api.ApiModelUtils;
import ww.smartexpress.driver.data.model.api.response.BankCard;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String userId = viewModel.getRepository().getSharedPreferences().getUserId();
        if(userId != null){
            viewModel.getRepository().getRoomService().userDao().findById(Long.valueOf(userId)).observe(this, userEntity -> {
                viewModel.user.set(userEntity);
                if(viewModel.user.get()!=null && viewModel.user.get().getBankCard() != null){
                    viewModel.bankCard.set(ApiModelUtils.fromJson(viewModel.user.get().getBankCard(), BankCard.class));
                }
            });
        }

        viewModel.getMyWallet();
    }
}
