package ww.smartexpress.driver.ui.achievement;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.databinding.Observable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import ww.smartexpress.driver.BR;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.data.model.api.response.RateType;
import ww.smartexpress.driver.databinding.ActivityAchievementBinding;
import ww.smartexpress.driver.di.component.ActivityComponent;
import ww.smartexpress.driver.ui.achievement.adapter.AchievementRateTypeAdapter;
import ww.smartexpress.driver.ui.base.activity.BaseActivity;

public class AchievementActivity extends BaseActivity<ActivityAchievementBinding, AchievementViewModel> {
    AchievementRateTypeAdapter achievementRateTypeAdapter;
    @Override
    public int getLayoutId() {
        return R.layout.activity_achievement;
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
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        super.onCreate(savedInstanceState);

        viewBinding.tlAchievement.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        load();
    }

    public void load(){
        List<RateType> rateTypeList = new ArrayList<>();
        rateTypeList.add(new RateType("1", "Trò chuyện vui vẻ",R.drawable.ic_icon_fun, 20));
        rateTypeList.add(new RateType("1", "Xe đẹp, sạch sẽ",R.drawable.ic_icon_blink, 20));
        rateTypeList.add(new RateType("1", "Chuyến đi an toàn",R.drawable.ic_icon_safe, 20));
        rateTypeList.add(new RateType("1", "Dich vụ xuất xắc",R.drawable.ic_icon_love, 20));

        RecyclerView.LayoutManager layout = new LinearLayoutManager(this
                ,LinearLayoutManager.VERTICAL, false);

        viewBinding.rcAchievementRate.setLayoutManager(layout);
        viewBinding.rcAchievementRate.setItemAnimator(new DefaultItemAnimator());
        achievementRateTypeAdapter = new AchievementRateTypeAdapter(rateTypeList);
        viewBinding.rcAchievementRate.setAdapter(achievementRateTypeAdapter);

        achievementRateTypeAdapter.setOnItemClickListener(new AchievementRateTypeAdapter.OnItemClickListener() {
            @Override
            public void itemClick(int position) {

            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        clearStatusBarFlags();
    }

    public void clearStatusBarFlags() {
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            // Xóa flags fullscreen đã thiết lập
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        }
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }
}
