package ww.smartexpress.driver.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;

import ww.smartexpress.driver.BR;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.databinding.ActivitySplashBinding;
import ww.smartexpress.driver.di.component.ActivityComponent;
import ww.smartexpress.driver.ui.base.activity.BaseActivity;
import ww.smartexpress.driver.ui.index.IndexActivity;
import ww.smartexpress.driver.ui.welcome.WelcomeActivity;

public class SplashActivity extends BaseActivity<ActivitySplashBinding, SplashViewModel> {
    @Override
    public int getLayoutId() {
        return R.layout.activity_splash;
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
        // Delay for 5 seconds (5000 milliseconds)
        long delayMillis = 3000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (checkLocationPermissions()) {
                    viewModel.checkLogin();
                } else {
                    startActivity(new Intent(getApplicationContext(), WelcomeActivity.class));
                    finish();
                }
            }
        }, delayMillis);
    }
}
