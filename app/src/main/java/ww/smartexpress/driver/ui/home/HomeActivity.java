package ww.smartexpress.driver.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationBarView;

import ww.smartexpress.driver.BR;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.databinding.ActivityHomeBinding;
import ww.smartexpress.driver.di.component.ActivityComponent;
import ww.smartexpress.driver.ui.base.activity.BaseActivity;
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
            replaceFragmentHome();
        }else {
            viewBinding.navigationView.setSelectedItemId(R.id.home);
        }
//        viewBinding.navigationView.setSelectedItemId(R.id.home);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.home:
                replaceFragmentHome();
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
    public void replaceFragmentProfile(){
        if(profileFragment == null){
            profileFragment = new ProfileFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.frameLayout, profileFragment, "profile fragment").hide(activeFragment).commit();
        }
        else{
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.hide(activeFragment).show(profileFragment).commit();
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
        String fm = intent.getStringExtra("homefragment");
        if(fm != null && fm == "home fragment"){
            Log.d("TAG", "onCreate: go Home");
            viewBinding.navigationView.setSelectedItemId(R.id.home);
            replaceFragmentHome();
        }else {
            viewBinding.navigationView.setSelectedItemId(R.id.home);
        }
    }
}
