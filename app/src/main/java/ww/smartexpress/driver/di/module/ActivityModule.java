package ww.smartexpress.driver.di.module;

import android.content.Context;

import androidx.core.util.Supplier;
import androidx.lifecycle.ViewModelProvider;

import ww.smartexpress.driver.MVVMApplication;
import ww.smartexpress.driver.ViewModelProviderFactory;
import ww.smartexpress.driver.data.Repository;
import ww.smartexpress.driver.data.model.api.response.BookingDetails;
import ww.smartexpress.driver.di.scope.ActivityScope;
import ww.smartexpress.driver.ui.achievement.AchievementViewModel;
import ww.smartexpress.driver.ui.allbike.AllBikeServiceViewModel;
import ww.smartexpress.driver.ui.award.AwardViewModel;
import ww.smartexpress.driver.ui.base.activity.BaseActivity;
import ww.smartexpress.driver.ui.booking.details.BookingDetailsViewModel;
import ww.smartexpress.driver.ui.chat.ChatViewModel;
import ww.smartexpress.driver.ui.fragment.income.details.IncomeDetailsViewModel;
import ww.smartexpress.driver.ui.history.TripHistoryViewModel;
import ww.smartexpress.driver.ui.home.HomeViewModel;
import ww.smartexpress.driver.ui.index.IndexViewModel;
import ww.smartexpress.driver.ui.login.LoginViewModel;
import ww.smartexpress.driver.ui.main.MainViewModel;
import ww.smartexpress.driver.ui.password.forget.ForgetPasswordViewModel;
import ww.smartexpress.driver.ui.password.otp.ForgetPasswordOTPViewModel;
import ww.smartexpress.driver.ui.password.renew.RenewPasswordViewModel;
import ww.smartexpress.driver.ui.profile.ProfileViewModel;
import ww.smartexpress.driver.ui.qrcode.QrcodeViewModel;
import ww.smartexpress.driver.ui.shipping.ShippingViewModel;
import ww.smartexpress.driver.ui.signup.SignUpViewModel;
import ww.smartexpress.driver.ui.splash.SplashViewModel;
import ww.smartexpress.driver.ui.welcome.WelcomeViewModel;
import ww.smartexpress.driver.ui.profile.edit.EditProfileViewModel;
import ww.smartexpress.driver.ui.statistic.StatisticViewModel;
import ww.smartexpress.driver.utils.GetInfo;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class ActivityModule {

    private final BaseActivity<?, ?> activity;

    public ActivityModule(BaseActivity<?, ?> activity) {
        this.activity = activity;
    }

    @Named("access_token")
    @Provides
    @ActivityScope
    String provideToken(Repository repository) {
        return repository.getToken();
    }

    @Named("device_id")
    @Provides
    @ActivityScope
    String provideDeviceId(Context applicationContext) {
        return GetInfo.getAll(applicationContext);
    }


    @Provides
    @ActivityScope
    MainViewModel provideMainViewModel(Repository repository, Context application) {
        Supplier<MainViewModel> supplier = () -> new MainViewModel(repository, (MVVMApplication) application);
        ViewModelProviderFactory<MainViewModel> factory = new ViewModelProviderFactory<>(MainViewModel.class, supplier);
        return new ViewModelProvider(activity, factory).get(MainViewModel.class);
    }

    @Provides
    @ActivityScope
    ProfileViewModel provideProfileViewModel(Repository repository, Context application) {
        Supplier<ProfileViewModel> supplier = () -> new ProfileViewModel(repository, (MVVMApplication) application);
        ViewModelProviderFactory<ProfileViewModel> factory = new ViewModelProviderFactory<>(ProfileViewModel.class, supplier);
        return new ViewModelProvider(activity, factory).get(ProfileViewModel.class);
    }
    @Provides
    @ActivityScope
    EditProfileViewModel provideEditProfileViewModel(Repository repository, Context application) {
        Supplier<EditProfileViewModel> supplier = () -> new EditProfileViewModel(repository, (MVVMApplication) application);
        ViewModelProviderFactory<EditProfileViewModel> factory = new ViewModelProviderFactory<>(EditProfileViewModel.class, supplier);
        return new ViewModelProvider(activity, factory).get(EditProfileViewModel.class);
    }
    @Provides
    @ActivityScope
    AllBikeServiceViewModel provideAllBikeServiceViewModel(Repository repository, Context application) {
        Supplier<AllBikeServiceViewModel> supplier = () -> new AllBikeServiceViewModel(repository, (MVVMApplication) application);
        ViewModelProviderFactory<AllBikeServiceViewModel> factory = new ViewModelProviderFactory<>(AllBikeServiceViewModel.class, supplier);
        return new ViewModelProvider(activity, factory).get(AllBikeServiceViewModel.class);
    }

    @Provides
    @ActivityScope
    StatisticViewModel provideStatisticViewModel(Repository repository, Context application) {
        Supplier<StatisticViewModel> supplier = () -> new StatisticViewModel(repository, (MVVMApplication) application);
        ViewModelProviderFactory<StatisticViewModel> factory = new ViewModelProviderFactory<>(StatisticViewModel.class, supplier);
        return new ViewModelProvider(activity, factory).get(StatisticViewModel.class);
    }

    @Provides
    @ActivityScope
    SplashViewModel provideSplashViewModel(Repository repository, Context application) {
        Supplier<SplashViewModel> supplier = () -> new SplashViewModel(repository, (MVVMApplication) application);
        ViewModelProviderFactory<SplashViewModel> factory = new ViewModelProviderFactory<>(SplashViewModel.class, supplier);
        return new ViewModelProvider(activity, factory).get(SplashViewModel.class);
    }

    @Provides
    @ActivityScope
    WelcomeViewModel provideWelcomeViewModel(Repository repository, Context application) {
        Supplier<WelcomeViewModel> supplier = () -> new WelcomeViewModel(repository, (MVVMApplication) application);
        ViewModelProviderFactory<WelcomeViewModel> factory = new ViewModelProviderFactory<>(WelcomeViewModel.class, supplier);
        return new ViewModelProvider(activity, factory).get(WelcomeViewModel.class);
    }

    @Provides
    @ActivityScope
    SignUpViewModel provideSignUpViewModel(Repository repository, Context application) {
        Supplier<SignUpViewModel> supplier = () -> new SignUpViewModel(repository, (MVVMApplication) application);
        ViewModelProviderFactory<SignUpViewModel> factory = new ViewModelProviderFactory<>(SignUpViewModel.class, supplier);
        return new ViewModelProvider(activity, factory).get(SignUpViewModel.class);
    }

    @Provides
    @ActivityScope
    HomeViewModel provideHomeViewModel(Repository repository, Context application) {
        Supplier<HomeViewModel> supplier = () -> new HomeViewModel(repository, (MVVMApplication) application);
        ViewModelProviderFactory<HomeViewModel> factory = new ViewModelProviderFactory<>(HomeViewModel.class, supplier);
        return new ViewModelProvider(activity, factory).get(HomeViewModel.class);
    }

    @Provides
    @ActivityScope
    TripHistoryViewModel provideTripHistoryViewModel(Repository repository, Context application) {
        Supplier<TripHistoryViewModel> supplier = () -> new TripHistoryViewModel(repository, (MVVMApplication) application);
        ViewModelProviderFactory<TripHistoryViewModel> factory = new ViewModelProviderFactory<>(TripHistoryViewModel.class, supplier);
        return new ViewModelProvider(activity, factory).get(TripHistoryViewModel.class);
    }

    @Provides
    @ActivityScope
    AchievementViewModel provideAchievementViewModel(Repository repository, Context application) {
        Supplier<AchievementViewModel> supplier = () -> new AchievementViewModel(repository, (MVVMApplication) application);
        ViewModelProviderFactory<AchievementViewModel> factory = new ViewModelProviderFactory<>(AchievementViewModel.class, supplier);
        return new ViewModelProvider(activity, factory).get(AchievementViewModel.class);
    }

    @Provides
    @ActivityScope
    IncomeDetailsViewModel provideIncomeDetailsViewModel(Repository repository, Context application) {
        Supplier<IncomeDetailsViewModel> supplier = () -> new IncomeDetailsViewModel(repository, (MVVMApplication) application);
        ViewModelProviderFactory<IncomeDetailsViewModel> factory = new ViewModelProviderFactory<>(IncomeDetailsViewModel.class, supplier);
        return new ViewModelProvider(activity, factory).get(IncomeDetailsViewModel.class);
    }

    @Provides
    @ActivityScope
    AwardViewModel provideAwardViewModel(Repository repository, Context application) {
        Supplier<AwardViewModel> supplier = () -> new AwardViewModel(repository, (MVVMApplication) application);
        ViewModelProviderFactory<AwardViewModel> factory = new ViewModelProviderFactory<>(AwardViewModel.class, supplier);
        return new ViewModelProvider(activity, factory).get(AwardViewModel.class);
    }

    @Provides
    @ActivityScope
    LoginViewModel provideLoginViewModel(Repository repository, Context application) {
        Supplier<LoginViewModel> supplier = () -> new LoginViewModel(repository, (MVVMApplication) application);
        ViewModelProviderFactory<LoginViewModel> factory = new ViewModelProviderFactory<>(LoginViewModel.class, supplier);
        return new ViewModelProvider(activity, factory).get(LoginViewModel.class);
    }

    @Provides
    @ActivityScope
    ForgetPasswordViewModel provideForgetPasswordViewModel(Repository repository, Context application) {
        Supplier<ForgetPasswordViewModel> supplier = () -> new ForgetPasswordViewModel(repository, (MVVMApplication) application);
        ViewModelProviderFactory<ForgetPasswordViewModel> factory = new ViewModelProviderFactory<>(ForgetPasswordViewModel.class, supplier);
        return new ViewModelProvider(activity, factory).get(ForgetPasswordViewModel.class);
    }

    @Provides
    @ActivityScope
    ForgetPasswordOTPViewModel provideForgetPasswordOTPViewModel(Repository repository, Context application) {
        Supplier<ForgetPasswordOTPViewModel> supplier = () -> new ForgetPasswordOTPViewModel(repository, (MVVMApplication) application);
        ViewModelProviderFactory<ForgetPasswordOTPViewModel> factory = new ViewModelProviderFactory<>(ForgetPasswordOTPViewModel.class, supplier);
        return new ViewModelProvider(activity, factory).get(ForgetPasswordOTPViewModel.class);
    }

    @Provides
    @ActivityScope
    RenewPasswordViewModel provideRenewPasswordViewModel(Repository repository, Context application) {
        Supplier<RenewPasswordViewModel> supplier = () -> new RenewPasswordViewModel(repository, (MVVMApplication) application);
        ViewModelProviderFactory<RenewPasswordViewModel> factory = new ViewModelProviderFactory<>(RenewPasswordViewModel.class, supplier);
        return new ViewModelProvider(activity, factory).get(RenewPasswordViewModel.class);
    }

    @Provides
    @ActivityScope
    IndexViewModel provideIndexViewModel(Repository repository, Context application) {
        Supplier<IndexViewModel> supplier = () -> new IndexViewModel(repository, (MVVMApplication) application);
        ViewModelProviderFactory<IndexViewModel> factory = new ViewModelProviderFactory<>(IndexViewModel.class, supplier);
        return new ViewModelProvider(activity, factory).get(IndexViewModel.class);
    }

    @Provides
    @ActivityScope
    ChatViewModel provideChatViewModel(Repository repository, Context application) {
        Supplier<ChatViewModel> supplier = () -> new ChatViewModel(repository, (MVVMApplication) application);
        ViewModelProviderFactory<ChatViewModel> factory = new ViewModelProviderFactory<>(ChatViewModel.class, supplier);
        return new ViewModelProvider(activity, factory).get(ChatViewModel.class);
    }

    @Provides
    @ActivityScope
    BookingDetailsViewModel provideBookingDetailsViewModel(Repository repository, Context application) {
        Supplier<BookingDetailsViewModel> supplier = () -> new BookingDetailsViewModel(repository, (MVVMApplication) application);
        ViewModelProviderFactory<BookingDetailsViewModel> factory = new ViewModelProviderFactory<>(BookingDetailsViewModel.class, supplier);
        return new ViewModelProvider(activity, factory).get(BookingDetailsViewModel.class);
    }

    @Provides
    @ActivityScope
    ShippingViewModel provideShippingViewModel(Repository repository, Context application) {
        Supplier<ShippingViewModel> supplier = () -> new ShippingViewModel(repository, (MVVMApplication) application);
        ViewModelProviderFactory<ShippingViewModel> factory = new ViewModelProviderFactory<>(ShippingViewModel.class, supplier);
        return new ViewModelProvider(activity, factory).get(ShippingViewModel.class);
    }

    @Provides
    @ActivityScope
    QrcodeViewModel provideQrcodeViewModel(Repository repository, Context application) {
        Supplier<QrcodeViewModel> supplier = () -> new QrcodeViewModel(repository, (MVVMApplication) application);
        ViewModelProviderFactory<QrcodeViewModel> factory = new ViewModelProviderFactory<>(QrcodeViewModel.class, supplier);
        return new ViewModelProvider(activity, factory).get(QrcodeViewModel.class);
    }
}
