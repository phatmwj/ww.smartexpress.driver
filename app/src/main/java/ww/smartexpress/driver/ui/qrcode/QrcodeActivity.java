package ww.smartexpress.driver.ui.qrcode;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import eu.davidea.flexibleadapter.databinding.BR;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.data.model.api.ApiModelUtils;
import ww.smartexpress.driver.data.model.api.response.PayosPaymentResponse;
import ww.smartexpress.driver.databinding.ActivityQrcodeBinding;
import ww.smartexpress.driver.di.component.ActivityComponent;
import ww.smartexpress.driver.ui.base.activity.BaseActivity;

public class QrcodeActivity extends BaseActivity<ActivityQrcodeBinding, QrcodeViewModel> {
    @Override
    public int getLayoutId() {
        return R.layout.activity_qrcode;
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

        ImageView imageView = findViewById(R.id.imageView);
        String qrString = intent.getStringExtra("qrString");
        String payUrl = intent.getStringExtra("payUrl");
        String paymentInfo = intent.getStringExtra("paymentInfo");
//        viewBinding.webview.setWebViewClient(new WebViewClient());
//        viewBinding.webview.getSettings().setJavaScriptEnabled(true);
//        viewBinding.webview.loadUrl(payUrl);

        if(qrString != null){
            try {
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                BitMatrix bitMatrix = barcodeEncoder.encode(qrString, BarcodeFormat.QR_CODE, 700, 700);
                Bitmap bitmap = Bitmap.createBitmap(bitMatrix.getWidth(), bitMatrix.getHeight(), Bitmap.Config.RGB_565);

                for (int x = 0; x < bitMatrix.getWidth(); x++) {
                    for (int y = 0; y < bitMatrix.getHeight(); y++) {
                        bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                    }
                }

                imageView.setImageBitmap(bitmap);
            } catch (WriterException e) {
                e.printStackTrace();
            }
        }

        if(paymentInfo!= null){
            PayosPaymentResponse data = ApiModelUtils.fromJson(paymentInfo, PayosPaymentResponse.class);
            Glide.with(this)
                    .load("https://api.vietqr.io/image/970448-CAS0585858714-vJnoGzd.jpg?accountName="+data.getData().getAccountName()+"&amount="+data.getData().getAmount()+"&addInfo="+data.getData().getDescription())
                    .into(imageView);
        }

    }
}
