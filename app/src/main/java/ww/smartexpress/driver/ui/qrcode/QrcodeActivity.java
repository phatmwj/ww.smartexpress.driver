package ww.smartexpress.driver.ui.qrcode;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import eu.davidea.flexibleadapter.databinding.BR;
import ww.smartexpress.driver.R;
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

        ImageView imageView = findViewById(R.id.imageView);
        String text = "otpauth://totp/LifeUni%3A0972141969%40gmail.com?secret=FPXFGON33ZZVRBVDWZGUI354BAH4CLR4&issuer=LifeUni"; // Chỗ này bạn có thể đặt chuỗi văn bản mà bạn muốn mã hóa thành QR code

        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            BitMatrix bitMatrix = barcodeEncoder.encode(text, BarcodeFormat.QR_CODE, 400, 400);
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
}
