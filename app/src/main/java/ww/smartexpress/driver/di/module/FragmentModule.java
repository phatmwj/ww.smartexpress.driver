package ww.smartexpress.driver.di.module;

import android.content.Context;

import androidx.core.util.Supplier;
import androidx.lifecycle.ViewModelProvider;

import ww.smartexpress.driver.MVVMApplication;
import ww.smartexpress.driver.ViewModelProviderFactory;
import ww.smartexpress.driver.data.Repository;
import ww.smartexpress.driver.di.scope.FragmentScope;
import ww.smartexpress.driver.ui.base.fragment.BaseFragment;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import ww.smartexpress.driver.ui.fragment.activity.ActivityFragmentViewModel;
import ww.smartexpress.driver.ui.fragment.home.HomeFragmentViewModel;
import ww.smartexpress.driver.ui.fragment.income.IncomeFragmentViewModel;
import ww.smartexpress.driver.ui.fragment.notification.NotificationFragmentViewModel;
import ww.smartexpress.driver.ui.fragment.profile.ProfileFragmentViewModel;

@Module
public class FragmentModule {

    private final BaseFragment<?, ?> fragment;

    public FragmentModule(BaseFragment<?, ?> fragment) {
        this.fragment = fragment;
    }

    @Named("access_token")
    @Provides
    @FragmentScope
    String provideToken(Repository repository) {
        return repository.getToken();
    }

    @Provides
    @FragmentScope
    HomeFragmentViewModel provideHomeFragmentViewModel(Repository repository, Context application){
        Supplier<HomeFragmentViewModel> supplier = () -> new HomeFragmentViewModel(repository, (MVVMApplication) application);
        ViewModelProviderFactory<HomeFragmentViewModel> factory = new ViewModelProviderFactory<>(HomeFragmentViewModel.class, supplier);
        return new ViewModelProvider(fragment, factory).get(HomeFragmentViewModel.class);
    }

    @Provides
    @FragmentScope
    ProfileFragmentViewModel provideProfileFragmentViewModel(Repository repository, Context application){
        Supplier<ProfileFragmentViewModel> supplier = () -> new ProfileFragmentViewModel(repository, (MVVMApplication) application);
        ViewModelProviderFactory<ProfileFragmentViewModel> factory = new ViewModelProviderFactory<>(ProfileFragmentViewModel.class, supplier);
        return new ViewModelProvider(fragment, factory).get(ProfileFragmentViewModel.class);
    }

    @Provides
    @FragmentScope
    IncomeFragmentViewModel provideIncomeFragmentViewModel(Repository repository, Context application){
        Supplier<IncomeFragmentViewModel> supplier = () -> new IncomeFragmentViewModel(repository, (MVVMApplication) application);
        ViewModelProviderFactory<IncomeFragmentViewModel> factory = new ViewModelProviderFactory<>(IncomeFragmentViewModel.class, supplier);
        return new ViewModelProvider(fragment, factory).get(IncomeFragmentViewModel.class);
    }

    @Provides
    @FragmentScope
    NotificationFragmentViewModel provideNotificationFragmentViewModel(Repository repository, Context application){
        Supplier<NotificationFragmentViewModel> supplier = () -> new NotificationFragmentViewModel(repository, (MVVMApplication) application);
        ViewModelProviderFactory<NotificationFragmentViewModel> factory = new ViewModelProviderFactory<>(NotificationFragmentViewModel.class, supplier);
        return new ViewModelProvider(fragment, factory).get(NotificationFragmentViewModel.class);
    }

    @Provides
    @FragmentScope
    ActivityFragmentViewModel provideActivityFragmentViewModel(Repository repository, Context application){
        Supplier<ActivityFragmentViewModel> supplier = () -> new ActivityFragmentViewModel(repository, (MVVMApplication) application);
        ViewModelProviderFactory<ActivityFragmentViewModel> factory = new ViewModelProviderFactory<>(ActivityFragmentViewModel.class, supplier);
        return new ViewModelProvider(fragment, factory).get(ActivityFragmentViewModel.class);
    }

}
