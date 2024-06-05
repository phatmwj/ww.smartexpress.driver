package ww.smartexpress.driver.ui.payout;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import androidx.annotation.Nullable;

import ww.smartexpress.driver.BR;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.data.model.api.ApiModelUtils;
import ww.smartexpress.driver.data.model.api.response.BankCard;
import ww.smartexpress.driver.databinding.ActivityPayoutBinding;
import ww.smartexpress.driver.di.component.ActivityComponent;
import ww.smartexpress.driver.ui.base.activity.BaseActivity;
import ww.smartexpress.driver.utils.NumberUtils;

public class PayoutActivity extends BaseActivity<ActivityPayoutBinding, PayoutViewModel> {
    @Override
    public int getLayoutId() {
        return R.layout.activity_payout;
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
        viewModel.balance.set(intent.getIntExtra("balance",0));

        String userId = viewModel.getRepository().getSharedPreferences().getUserId();
        if(userId != null){
            viewModel.getRepository().getRoomService().userDao().findById(Long.valueOf(userId)).observe(this, userEntity -> {
                Log.d("TAG", "onCreate: "+userEntity.toString());
                viewModel.user.set(userEntity);
            });
        }
        viewBinding.edtMoney.addTextChangedListener(new TextWatcher() {
            private String current = "";
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().equals("")) {
                    if (!charSequence.toString().equals(current)) {

                        String cleanString = charSequence.toString().replaceAll("[.]", "");
                        viewModel.money.set(cleanString);
                        Log.d("TAG", "onTextChanged: "+ viewModel.money.get());
                        double parsed = Double.parseDouble(cleanString);

                        String formated = NumberUtils.formatEdtTextCurrency(parsed);

                        current = formated;

                        viewBinding.edtMoney.setText(formated);
                        viewBinding.edtMoney.setSelection(formated.length());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void clickMoney(String money){
        viewModel.money.set(money);
        viewBinding.edtMoney.setText(money);
    }
}
