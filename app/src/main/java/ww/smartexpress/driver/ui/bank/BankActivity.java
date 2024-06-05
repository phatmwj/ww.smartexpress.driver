package ww.smartexpress.driver.ui.bank;

import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;

import androidx.annotation.Nullable;
import androidx.databinding.Observable;

import ww.smartexpress.driver.BR;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.databinding.ActivityBankCardBinding;
import ww.smartexpress.driver.di.component.ActivityComponent;
import ww.smartexpress.driver.ui.base.activity.BaseActivity;

public class BankActivity extends BaseActivity<ActivityBankCardBinding, BankViewModel> {

    @Override
    public int getLayoutId() {
        return R.layout.activity_bank_card;
    }

    @Override
    public int getBindingVariable() {
        return BR.vm;
    }

    @Override
    public void performDependencyInjection(ActivityComponent buildComponent) {
        buildComponent.inject(this);

        String userId = viewModel.getRepository().getSharedPreferences().getUserId();
        if(userId != null){
            viewModel.getRepository().getRoomService().userDao().findById(Long.valueOf(userId)).observe(this, userEntity -> {
                viewModel.user.set(userEntity);
            });
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel.isVisibility.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if(!viewModel.isVisibility.get()){
                    viewBinding.edtPw.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }else {
                    viewBinding.edtPw.setTransformationMethod(null);;
                }
            }
        });

        viewBinding.edtAccountNumber.setOnFocusChangeListener((view, b) -> {
            if(b){

            }else {
                viewModel.getAccountName();
            }
        });
    }
}
