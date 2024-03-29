package ww.smartexpress.driver.ui.welcome;

import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;

import ww.smartexpress.driver.BR;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.constant.Constants;
import ww.smartexpress.driver.databinding.ActivityWelcomeBinding;
import ww.smartexpress.driver.di.component.ActivityComponent;
import ww.smartexpress.driver.ui.base.activity.BaseActivity;

public class WelcomeActivity extends BaseActivity<ActivityWelcomeBinding, WelcomeViewModel> {
    @Override
    public int getLayoutId() {
        return R.layout.activity_welcome;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!checkLocationPermissions()) {
            requestLocationPermissions();
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Constants.LOCATION_PERMISSION_CODE) {
            // Kiểm tra kết quả yêu cầu quyền truy cập vị trí
            boolean allPermissionsGranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
            if (allPermissionsGranted) {
                viewModel.checkLocationPermission.set(true);
            } else {
                finishAffinity();
                System.exit(0);
            }
        }
    }

}
