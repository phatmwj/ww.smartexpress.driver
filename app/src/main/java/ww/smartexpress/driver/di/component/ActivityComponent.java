package ww.smartexpress.driver.di.component;

import ww.smartexpress.driver.di.module.ActivityModule;
import ww.smartexpress.driver.di.scope.ActivityScope;
import ww.smartexpress.driver.ui.achievement.AchievementActivity;
import ww.smartexpress.driver.ui.allbike.AllBikeServiceActivity;
import ww.smartexpress.driver.ui.award.AwardActivity;
import ww.smartexpress.driver.ui.booking.details.BookingDetailsActivity;
import ww.smartexpress.driver.ui.chat.ChatActivity;
import ww.smartexpress.driver.ui.fragment.income.details.IncomeDetailsActivity;
import ww.smartexpress.driver.ui.history.TripHistoryActivity;
import ww.smartexpress.driver.ui.home.HomeActivity;
import ww.smartexpress.driver.ui.index.IndexActivity;
import ww.smartexpress.driver.ui.login.LoginActivity;
import ww.smartexpress.driver.ui.main.MainActivity;

import dagger.Component;
import ww.smartexpress.driver.ui.password.forget.ForgetPasswordActivity;
import ww.smartexpress.driver.ui.password.otp.ForgetPasswordOTPActivity;
import ww.smartexpress.driver.ui.password.renew.RenewPasswordActivity;
import ww.smartexpress.driver.ui.profile.edit.EditProfileActivity;
import ww.smartexpress.driver.ui.statistic.StatisticActivity;
import ww.smartexpress.driver.ui.signup.SignUpActivity;
import ww.smartexpress.driver.ui.splash.SplashActivity;
import ww.smartexpress.driver.ui.welcome.WelcomeActivity;

@ActivityScope
@Component(modules = {ActivityModule.class}, dependencies = AppComponent.class)
public interface ActivityComponent {
    void inject(MainActivity activity);

    void inject(SplashActivity splashActivity);

    void inject(WelcomeActivity welcomeActivity);

    void inject(SignUpActivity signUpActivity);

    void inject(HomeActivity homeActivity);

    void inject(EditProfileActivity editProfileActivity);

    void inject(AllBikeServiceActivity allBikeServiceActivity);

    void inject(StatisticActivity statisticActivity);

    void inject(TripHistoryActivity tripHistoryActivity);

    void inject(AchievementActivity achievementActivity);

    void inject(IncomeDetailsActivity incomeDetailsActivity);

    void inject(AwardActivity awardActivity);

    void inject(LoginActivity loginActivity);

    void inject(ForgetPasswordActivity forgetPasswordActivity);

    void inject(ForgetPasswordOTPActivity forgetPasswordOTPActivity);

    void inject(RenewPasswordActivity renewPasswordActivity);

    void inject(IndexActivity indexActivity);

    void inject(ChatActivity chatActivity);

    void inject(BookingDetailsActivity bookingDetailsActivity);
}

