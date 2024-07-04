package ww.smartexpress.driver.ui.wallet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;

import ww.smartexpress.driver.BR;
import ww.smartexpress.driver.MVVMApplication;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.data.model.api.ApiModelUtils;
import ww.smartexpress.driver.data.model.api.response.AccountCOD;
import ww.smartexpress.driver.data.model.api.response.BankCard;
import ww.smartexpress.driver.databinding.ActivityWalletBinding;
import ww.smartexpress.driver.di.component.ActivityComponent;
import ww.smartexpress.driver.ui.base.activity.BaseActivity;
import ww.smartexpress.driver.ui.cod.CodActivity;

public class WalletActivity extends BaseActivity<ActivityWalletBinding, WalletViewModel> {

    private ActivityResultLauncher<Intent> activityResultLauncher;
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

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if(data != null){
                            viewModel.accountCOD.set(new AccountCOD(Double.valueOf(data.getExtras().getString("maxCod"))));
                        }
                    }
                });

        viewModel.getMyWallet();
    }


    @Override
    protected void onResume() {
        super.onResume();
        MVVMApplication mvvmApplication = (MVVMApplication) getApplication();
        if(mvvmApplication.getIsDepositSuccess()){
            viewModel.getMyWallet();
            mvvmApplication.setIsDepositSuccess(false);
        }
    }

    public void navigateCOD(){
        Intent intent = new Intent(this, CodActivity.class);
        intent.putExtra("maxCod", (int) viewModel.accountCOD.get().getMaxCOD());
        activityResultLauncher.launch(intent);
    }
}
