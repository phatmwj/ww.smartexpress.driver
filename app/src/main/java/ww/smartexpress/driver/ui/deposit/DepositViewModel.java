package ww.smartexpress.driver.ui.deposit;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.databinding.ObservableField;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import vn.momo.momo_partner.AppMoMoLib;
import vn.momo.momo_partner.MoMoParameterNamePayment;
import ww.smartexpress.driver.MVVMApplication;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.data.Repository;
import ww.smartexpress.driver.data.model.api.ApiModelUtils;
import ww.smartexpress.driver.data.model.api.request.DepositRequest;
import ww.smartexpress.driver.data.model.api.request.LoginRequest;
import ww.smartexpress.driver.data.model.api.response.DataPayment;
import ww.smartexpress.driver.data.model.api.response.MomoPaymentResponse;
import ww.smartexpress.driver.data.model.api.response.PayosPaymentResponse;
import ww.smartexpress.driver.ui.base.activity.BaseViewModel;
import ww.smartexpress.driver.ui.home.HomeActivity;
import ww.smartexpress.driver.ui.qrcode.QrcodeActivity;

public class DepositViewModel extends BaseViewModel {

    public ObservableField<String> money = new ObservableField<>();
    public ObservableField<Integer> paymentKind = new ObservableField<>();
    public DepositViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
    }

    public void back(){
        application.getCurrentActivity().finish();
    }

    public void doDone(){
        if(money.get() == null && Integer.valueOf(money.get())>10000){
            showErrorMessage("Số tiền nạp bị lỗi");
            return;
        }
        if(paymentKind.get() == null){
            showErrorMessage("Vui lòng chọn phương thức thanh toán");
            return;
        }
        showLoading();
        if(paymentKind.get() ==1 ){
            compositeDisposable.add(repository.getApiService().depositMomo(new DepositRequest(Integer.valueOf(money.get()), paymentKind.get()))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response -> {
                        if(response.isResult()){
                            Intent intent = new Intent(application.getCurrentActivity(), QrcodeActivity.class);
                            intent.putExtra("momoPaymentInfo", response.getData().getPaymentInfo());
                            MomoPaymentResponse data = ApiModelUtils.fromJson(response.getData().getPaymentInfo(), MomoPaymentResponse.class);
                            intent.putExtra("qrString", data.getQrCodeUrl());
                            intent.putExtra("payUrl", data.getPayUrl());
                            application.getCurrentActivity().startActivity(intent);
//                                openMoMoDeeplink(data.getDeeplink());
                            hideLoading();
                            showSuccessMessage(response.getMessage());
                        }else {
                            hideLoading();
                            showErrorMessage(response.getMessage());
                        }
                    },error->{
                        hideLoading();
                        showErrorMessage(application.getString(R.string.newtwork_error));
                        error.printStackTrace();
                    })
            );
        }else {
            compositeDisposable.add(repository.getApiService().depositPayos(new DepositRequest(Integer.valueOf(money.get()), paymentKind.get()))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response -> {
                        if(response.isResult()){
                            hideLoading();
                            PayosPaymentResponse data = ApiModelUtils.fromJson(response.getData().getPaymentInfo(), PayosPaymentResponse.class);
                            Intent intent = new Intent(application.getCurrentActivity(), QrcodeActivity.class);
                            intent.putExtra("paymentInfo", response.getData().getPaymentInfo());
                            application.getCurrentActivity().startActivity(intent);
//                            openMoMoDeeplink("https://dl.vietqr.io/pay?app=bidv&ba=CAS0585858714@vcb&am=790000");
                            showSuccessMessage(response.getMessage());
                        }else {
                            hideLoading();
                            showErrorMessage(response.getMessage());
                        }
                    },error->{
                        hideLoading();
                        showErrorMessage(application.getString(R.string.newtwork_error));
                        error.printStackTrace();
                    })
            );
        }

    }

    private void openMoMoDeeplink(String deeplink) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(deeplink));
        application.getCurrentActivity().startActivity(intent);
//        if (intent.resolveActivity(application.getPackageManager()) != null) {
//            application.getCurrentActivity().startActivity(intent);
//        } else {
//            showWarningMessage("MoMo app is not installed");
//        }
    }
    public void onPayos(Boolean b){
        if (b){
            paymentKind.set(2);
        }else {
            if(paymentKind.get() ==1){
                return;
            }
            paymentKind.set(null);
        }
    }

    public void onMomo(Boolean b){
        if (b){
            paymentKind.set(1);
        }else {
            if(paymentKind.get() ==2){
                return;
            }
            paymentKind.set(null);
        }
    }

}
