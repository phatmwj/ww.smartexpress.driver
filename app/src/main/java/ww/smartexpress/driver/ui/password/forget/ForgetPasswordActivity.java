package ww.smartexpress.driver.ui.password.forget;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;

import androidx.annotation.Nullable;

import ww.smartexpress.driver.BR;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.constant.Constants;
import ww.smartexpress.driver.databinding.ActivityForgetPasswordBinding;
import ww.smartexpress.driver.di.component.ActivityComponent;
import ww.smartexpress.driver.ui.base.activity.BaseActivity;


public class ForgetPasswordActivity extends BaseActivity<ActivityForgetPasswordBinding, ForgetPasswordViewModel> {
    @Override
    public int getLayoutId() {
        return R.layout.activity_forget_password;
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
        viewModel.kind.set(intent.getIntExtra(Constants.VERIFY_OPTION, 1));

        viewBinding.layoutHeader.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if(viewModel.kind.get() == 1){
            InputFilter[] filters = new InputFilter[1];
            filters[0] = new InputFilter.LengthFilter(10);
            viewBinding.edtEmail.setFilters(filters);
        }
    }
}
