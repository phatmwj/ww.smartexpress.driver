package ww.smartexpress.driver.ui.password.forget;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;

import androidx.annotation.Nullable;
import androidx.databinding.Observable;

import ww.smartexpress.driver.BR;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.constant.Constants;
import ww.smartexpress.driver.databinding.ActivityResetForgetPasswordBinding;
import ww.smartexpress.driver.di.component.ActivityComponent;
import ww.smartexpress.driver.ui.base.activity.BaseActivity;

public class ResetForgetPasswordActivity extends BaseActivity<ActivityResetForgetPasswordBinding, ResetForgetPasswordViewModel> {
    @Override
    public int getLayoutId() {
        return R.layout.activity_reset_forget_password;
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

        Intent intent = getIntent();
        viewModel.userId.set(intent.getStringExtra(Constants.KEY_USER_ID));
        viewModel.otp.set(intent.getStringExtra(Constants.OTP));

    }
}
