package ww.smartexpress.driver.ui.auth;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.ToolbarWidgetWrapper;
import androidx.viewpager.widget.PagerAdapter;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import ww.smartexpress.driver.BR;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.databinding.ActivityAuthBinding;
import ww.smartexpress.driver.di.component.ActivityComponent;
import ww.smartexpress.driver.ui.auth.adapter.ViewPagerAdapter;
import ww.smartexpress.driver.ui.base.activity.BaseActivity;
import ww.smartexpress.driver.ui.fragment.login.LoginFragment;
import ww.smartexpress.driver.ui.fragment.signup.SignupFragment;

public class AuthActivity extends BaseActivity<ActivityAuthBinding, AuthViewModel> {

    ViewPagerAdapter viewPagerAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_auth;
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

        viewPagerAdapter = new ViewPagerAdapter(this);

        viewBinding.viewPager.setAdapter(viewPagerAdapter);

        new TabLayoutMediator(viewBinding.tabLayout, viewBinding.viewPager,
                (tab, position) -> {
                    if (position == 0)
                    {
                        tab.setText("Đăng nhập");
                    }
                    else if (position == 1)
                    {
                        tab.setText("Đăng ký");
                    }
                }
        ).attach();

        viewBinding.appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    viewBinding.collapsingToolbarLayout.setTitleEnabled(true);
                    isShow = true;
                } else if (isShow) {
                    viewBinding.collapsingToolbarLayout.setTitleEnabled(false);
                    isShow = false;
                }
            }
        });
    }
}
