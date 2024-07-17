package ww.smartexpress.driver.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.navigation.NavigationBarView;

import ww.smartexpress.driver.BR;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.databinding.ActivityHomeBinding;
import ww.smartexpress.driver.di.component.ActivityComponent;
import ww.smartexpress.driver.ui.base.activity.BaseActivity;
import ww.smartexpress.driver.ui.fragment.activity.ActivityFragment;
import ww.smartexpress.driver.ui.fragment.home.HomeFragment;
import ww.smartexpress.driver.ui.fragment.income.IncomeFragment;
import ww.smartexpress.driver.ui.fragment.notification.NotificationFragment;
import ww.smartexpress.driver.ui.fragment.profile.ProfileFragment;

public class HomeActivity extends BaseActivity<ActivityHomeBinding, HomeViewModel> implements NavigationBarView.OnItemSelectedListener{

    public Fragment activeFragment = new Fragment();
    private HomeFragment homeFragment;
    private IncomeFragment incomeFragment;
    private ProfileFragment profileFragment;
    private NotificationFragment notificationFragment;

    private ActivityFragment activityFragment;
    BadgeDrawable badge;

    @Override
    public int getLayoutId() {
        return R.layout.activity_home;
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
        viewBinding.navigationView.setOnItemSelectedListener(this);
        Intent intent = getIntent();
        String fm = intent.getStringExtra("homefragment");
        if(fm != null && fm == "home fragment"){
            Log.d("TAG", "onCreate: go Home");
            viewBinding.navigationView.setSelectedItemId(R.id.home);
//            replaceFragmentHome();
            replaceFragmentActivity();
        }else {
            viewBinding.navigationView.setSelectedItemId(R.id.home);
        }
//        viewBinding.navigationView.setSelectedItemId(R.id.home);
        int menuItemId = viewBinding.navigationView.getMenu().getItem(2).getItemId();
        viewModel.totalUnread.observe(this, i ->{
            if(i == 0){
                viewBinding.navigationView.getOrCreateBadge(menuItemId);
                viewBinding.navigationView.removeBadge(menuItemId);
            }else {
                badge = viewBinding.navigationView.getOrCreateBadge(menuItemId);
                badge.setNumber(i);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.home:
//                replaceFragmentHome();
                replaceFragmentActivity();
                return true;
            case R.id.account:
                replaceFragmentProfile();
                return true;
            case R.id.income:
                replaceFragmentIncome();
                return true;
            case R.id.notification:
                replaceFragmentNotification();
                return true;
        }
        return false;
    }

    public void replaceFragmentHome(){
        if(homeFragment == null){
            homeFragment = new HomeFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.frameLayout, homeFragment, "home fragment").hide(activeFragment).commit();
        }
        else{
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.hide(activeFragment).show(homeFragment).commit();
        }
        activeFragment = homeFragment;
    }

    public void replaceFragmentActivity(){
        if(activityFragment == null){
            activityFragment = new ActivityFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.frameLayout, activityFragment, "activity fragment").hide(activeFragment).commit();
        }
        else{
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.hide(activeFragment).show(activityFragment).commit();
        }
        activeFragment = activityFragment;
    }
    public void replaceFragmentProfile(){
        if(profileFragment == null){
            profileFragment = new ProfileFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.frameLayout, profileFragment, "profile fragment").hide(activeFragment).commit();
        }
        else{
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.hide(activeFragment).show(profileFragment).commit();
            profileFragment.onResume();
        }
        activeFragment = profileFragment;
    }

    public void replaceFragmentIncome(){
        if(incomeFragment == null){
            Log.d("TAG", "replaceFragmentIncome: ");
            incomeFragment = new IncomeFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.frameLayout, incomeFragment, "income fragment").hide(activeFragment).commit();
        }
        else{
            Log.d("TAG", "replaceFragmentIncome2: ");
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.hide(activeFragment).show(incomeFragment).commit();
        }
        activeFragment = incomeFragment;
    }

    public void replaceFragmentNotification(){
        if(notificationFragment == null){
            notificationFragment = new NotificationFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.frameLayout, notificationFragment, "notification fragment").hide(activeFragment).commit();
        }
        else{
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.hide(activeFragment).show(notificationFragment).commit();
            notificationFragment.onResume();
        }
        activeFragment = notificationFragment;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent == null){
            return;
        }
        String fm = intent.getStringExtra("activityfragment");
        if(fm != null && fm == "activity fragment"){
            Log.d("TAG", "onCreate: go Home");
            viewBinding.navigationView.setSelectedItemId(R.id.home);
//            replaceFragmentHome();
            replaceFragmentActivity();
        }else {
            viewBinding.navigationView.setSelectedItemId(R.id.home);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.getMyNotification();
        if(notificationFragment != null){
            notificationFragment.onResume();
        }
        if(activityFragment != null){
            activityFragment.onResume();
        }
    }
}
