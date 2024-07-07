package ww.smartexpress.driver.ui.payout;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.Getter;
import timber.log.Timber;
import ww.smartexpress.driver.BR;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.data.model.api.ApiModelUtils;
import ww.smartexpress.driver.data.model.api.response.BankCard;
import ww.smartexpress.driver.data.model.api.response.PayoutTransaction;
import ww.smartexpress.driver.databinding.ActivityPayoutBinding;
import ww.smartexpress.driver.databinding.DialogPayoutRequestBinding;
import ww.smartexpress.driver.di.component.ActivityComponent;
import ww.smartexpress.driver.ui.base.activity.BaseActivity;
import ww.smartexpress.driver.ui.dialog.PasswordDialog;
import ww.smartexpress.driver.utils.NumberUtils;

public class PayoutActivity extends BaseActivity<ActivityPayoutBinding, PayoutViewModel> implements PasswordDialog.PasswordListener {
    @Getter
    private PasswordDialog passwordDialog;
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
                if(viewModel.user.get()!=null && viewModel.user.get().getBankCard() != null){
                    viewModel.bankCard.set(ApiModelUtils.fromJson(viewModel.user.get().getBankCard(), BankCard.class));
                }
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
        getMyPayoutRequest();
    }

    public void clickMoney(String money){
        viewModel.money.set(money);
        viewBinding.edtMoney.setText(money);
    }

    @Override
    public void confirm(String password) {
        Timber.tag("TAG").d("confirm: ");
        viewModel.confirmPass(password);
    }

    public void navigateToConfirmPassword(){
        passwordDialog = new PasswordDialog();
        passwordDialog.show(getSupportFragmentManager(),"passwordDialog");
    }

    public void getMyPayoutRequest(){
        viewModel.showLoading();
        viewModel.compositeDisposable.add(viewModel.getMyPayoutRequest()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                            viewModel.hideLoading();
                            if(response.isResult() && response.getData().getTotalElements() > 0){

                                viewModel.payoutTransactionList.set(response.getData().getContent());
                                dialogPayoutRequest(response.getData().getContent().get(0));

//                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext()
//                                ,LinearLayoutManager.VERTICAL, false);
//                        viewBinding.rcPayoutRequest.setLayoutManager(layoutManager);
//                        viewBinding.rcPayoutRequest.setItemAnimator(new DefaultItemAnimator());
//                        payoutRequestAdapter = new PayoutRequestAdapter(viewModel.payoutTransactionList.get());
//                        viewBinding.rcPayoutRequest.setAdapter(payoutRequestAdapter);
//
//                        payoutRequestAdapter.setOnItemClickListener(new PayoutRequestAdapter.OnItemClickListener() {
//                            @Override
//                            public void itemClick(PayoutTransaction payoutTransaction) {
//
//                            }
//
//                            @Override
//                            public void delete(PayoutTransaction payoutTransaction) {
//                                deletePayoutRequest(payoutTransaction);
//                            }
//                        });
                            }
                        },error->{
                            viewModel.showErrorMessage(getString(R.string.network_error));
                            error.printStackTrace();
                            viewModel.hideLoading();
                        })
        );
    }

    public void dialogPayoutRequest(PayoutTransaction payoutTransaction){
        Dialog dialog = new Dialog(PayoutActivity.this);
        DialogPayoutRequestBinding dialogLogoutBinding = DataBindingUtil.inflate(LayoutInflater.from(PayoutActivity.this),R.layout.dialog_payout_request,null, false);
        dialogLogoutBinding.setIvm(payoutTransaction);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(dialogLogoutBinding.getRoot());
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        dialog.setCanceledOnTouchOutside(false);

        dialogLogoutBinding.btnLogout.setOnClickListener(view -> {
            dialog.dismiss();
            viewModel.showLoading();
            viewModel.compositeDisposable.add(viewModel.deletePayoutRequest(payoutTransaction.getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response -> {
                        viewModel.hideLoading();
                        if(response.isResult()){
                            //viewModel.payoutTransactionList.get().remove(payoutTransaction);
                            viewModel.payoutTransactionList.set(new ArrayList<>());
                            Log.d("TAG", "dialogPayoutRequest: " + viewModel.payoutTransactionList.get().size());
                            viewModel.showSuccessMessage("Xóa yêu cầu rút tiền thành công");
                        }else {
                            viewModel.showErrorMessage(response.getMessage());
                        }
                    },error->{
                        viewModel.showErrorMessage(getString(R.string.network_error));
                        error.printStackTrace();
                        viewModel.hideLoading();
                    })
            );
        });
        dialogLogoutBinding.btnCancel.setOnClickListener(view -> {
            dialog.dismiss();
            onBackPressed();
        });
        dialog.show();
    }
}
