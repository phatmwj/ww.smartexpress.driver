package ww.smartexpress.driver.ui.password.renew;

import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;

import androidx.annotation.Nullable;
import androidx.databinding.Observable;

import ww.smartexpress.driver.BR;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.databinding.ActivityRenewPasswordBinding;
import ww.smartexpress.driver.di.component.ActivityComponent;
import ww.smartexpress.driver.ui.base.activity.BaseActivity;

public class RenewPasswordActivity extends BaseActivity<ActivityRenewPasswordBinding, RenewPasswordViewModel> {
    @Override
    public int getLayoutId() {
        return R.layout.activity_renew_password;
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
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            viewModel.isForgot.set(bundle.getBoolean("isForgot", false));
        }

        viewBinding.btnConfirm.setOnClickListener(view -> {
            hideKeyboard();
            viewModel.doNext();
        });

        String userId = viewModel.getRepository().getSharedPreferences().getUserId();
        if(userId != null){
            viewModel.getRepository().getRoomService().userDao().findById(Long.valueOf(userId)).observe(this, userEntity -> {
                viewModel.user.set(userEntity);
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
