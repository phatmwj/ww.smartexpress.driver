package ww.smartexpress.driver.di.component;


import ww.smartexpress.driver.di.module.FragmentModule;
import ww.smartexpress.driver.di.scope.FragmentScope;

import dagger.Component;
import ww.smartexpress.driver.ui.fragment.home.HomeFragment;
import ww.smartexpress.driver.ui.fragment.income.IncomeFragment;
import ww.smartexpress.driver.ui.fragment.notification.NotificationFragment;
import ww.smartexpress.driver.ui.fragment.profile.ProfileFragment;

@FragmentScope
@Component(modules = {FragmentModule.class}, dependencies = AppComponent.class)
public interface FragmentComponent {
    void inject(HomeFragment homeFragment);
    void inject(ProfileFragment profileFragment);
    void inject(IncomeFragment incomeFragment);
    void inject(NotificationFragment notificationFragment);
}
