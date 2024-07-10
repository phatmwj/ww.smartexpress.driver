package ww.smartexpress.driver.di.component;

import ww.smartexpress.driver.di.module.ActivityModule;
import ww.smartexpress.driver.di.scope.ActivityScope;
import ww.smartexpress.driver.ui.achievement.AchievementActivity;
import ww.smartexpress.driver.ui.allbike.AllBikeServiceActivity;
import ww.smartexpress.driver.ui.auth.AuthActivity;
import ww.smartexpress.driver.ui.await.AwaitActivity;
import ww.smartexpress.driver.ui.award.AwardActivity;
import ww.smartexpress.driver.ui.bank.BankActivity;
import ww.smartexpress.driver.ui.booking.details.BookingDetailsActivity;
import ww.smartexpress.driver.ui.chat.ChatActivity;
import ww.smartexpress.driver.ui.cod.CodActivity;
import ww.smartexpress.driver.ui.deposit.DepositActivity;
import ww.smartexpress.driver.ui.fragment.income.details.IncomeDetailsActivity;
import ww.smartexpress.driver.ui.history.TripHistoryActivity;
import ww.smartexpress.driver.ui.home.HomeActivity;
import ww.smartexpress.driver.ui.index.IndexActivity;
import ww.smartexpress.driver.ui.login.LoginActivity;
import ww.smartexpress.driver.ui.main.MainActivity;

import dagger.Component;
import ww.smartexpress.driver.ui.notification.details.NotificationDetailsActivity;
import ww.smartexpress.driver.ui.password.forget.ForgetPasswordActivity;
import ww.smartexpress.driver.ui.password.forget.ResetForgetPasswordActivity;
import ww.smartexpress.driver.ui.password.otp.ForgetPasswordOTPActivity;
import ww.smartexpress.driver.ui.password.otp.VerifyForgetPasswordOTPActivity;
import ww.smartexpress.driver.ui.password.renew.RenewPasswordActivity;
import ww.smartexpress.driver.ui.payout.PayoutActivity;
import ww.smartexpress.driver.ui.payout.details.PayoutDetailsActivity;
import ww.smartexpress.driver.ui.profile.edit.EditProfileActivity;
import ww.smartexpress.driver.ui.qrcode.QrcodeActivity;
import ww.smartexpress.driver.ui.shipping.ShippingActivity;
import ww.smartexpress.driver.ui.shipping.ShippingViewModel;
import ww.smartexpress.driver.ui.statistic.StatisticActivity;
import ww.smartexpress.driver.ui.signup.SignUpActivity;
import ww.smartexpress.driver.ui.splash.SplashActivity;
import ww.smartexpress.driver.ui.wallet.WalletActivity;
import ww.smartexpress.driver.ui.wallet.transaction.TransactionActivity;
import ww.smartexpress.driver.ui.wallet.transaction.details.TransactionDetailsActivity;
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
    void inject(ShippingActivity shippingActivity);

    void inject(QrcodeActivity qrcodeActivity);

    void inject(WalletActivity walletActivity);

    void inject(DepositActivity depositActivity);

    void inject(PayoutActivity payoutActivity);

    void inject(BankActivity bankActivity);
    void inject(TransactionActivity transactionActivity);

    void inject(AuthActivity authActivity);

    void inject(CodActivity codActivity);

    void inject(NotificationDetailsActivity notificationDetailsActivity);

    void inject(ResetForgetPasswordActivity resetForgetPasswordActivity);

    void inject(VerifyForgetPasswordOTPActivity verifyForgetPasswordOTPActivity);

    void inject(AwaitActivity awaitActivity);

    void inject(TransactionDetailsActivity transactionDetailsActivity);

    void inject(PayoutDetailsActivity payoutDetailsActivity);
}

