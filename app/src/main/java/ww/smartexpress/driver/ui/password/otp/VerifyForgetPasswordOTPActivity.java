package ww.smartexpress.driver.ui.password.otp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import ww.smartexpress.driver.BR;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.constant.Constants;
import ww.smartexpress.driver.databinding.ActivityVerifyForgetPasswordOtpBinding;
import ww.smartexpress.driver.di.component.ActivityComponent;
import ww.smartexpress.driver.ui.auth.AuthActivity;
import ww.smartexpress.driver.ui.base.activity.BaseActivity;

public class VerifyForgetPasswordOTPActivity extends BaseActivity<ActivityVerifyForgetPasswordOtpBinding, VerifyForgetPasswordOTPViewModel> {
    @Override
    public int getLayoutId() {
        return R.layout.activity_verify_forget_password_otp;
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
        viewModel.kind.set(intent.getIntExtra(Constants.VERIFY_OPTION, 1));

        viewBinding.layoutHeader.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VerifyForgetPasswordOTPActivity.this, AuthActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                finish();
            }
        });

        setupOTP();
    }

    public void setupOTP(){
        viewBinding.inputOTP1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d("TAG", "onTextChanged: " + charSequence.toString());
                if(!charSequence.toString().trim().isEmpty()){
                    viewBinding.inputOTP2.requestFocus();
                    Log.d("TAG", "onTextChanged: ");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.d("TAG", "afterTextChanged: " + editable.toString());
            }
        });
        viewBinding.inputOTP2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty()){
                    viewBinding.inputOTP3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        viewBinding.inputOTP3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty()){
                    viewBinding.inputOTP4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        viewBinding.inputOTP4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty()){
                    if(!viewModel.otp1.get().isEmpty() && !viewModel.otp2.get().isEmpty() && !viewModel.otp3.get().isEmpty() && !viewModel.otp4.get().isEmpty()){
                        Log.d("TAG", "onTextChanged: ok");
                        viewBinding.inputOTP4.clearFocus();
                        VerifyForgetPasswordOTPActivity.this.hideKeyboard();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }
}
