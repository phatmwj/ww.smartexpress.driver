package ww.smartexpress.driver.ui.fragment.profile;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ww.smartexpress.driver.MVVMApplication;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.data.Repository;
import ww.smartexpress.driver.data.local.prefs.PreferencesService;
import ww.smartexpress.driver.data.model.api.response.ProfileResponse;
import ww.smartexpress.driver.data.model.api.response.SettingResponse;
import ww.smartexpress.driver.data.model.api.response.WalletResponse;
import ww.smartexpress.driver.data.model.room.UserEntity;
import ww.smartexpress.driver.databinding.DialogLogoutBinding;
import ww.smartexpress.driver.ui.achievement.AchievementActivity;
import ww.smartexpress.driver.ui.allbike.AllBikeServiceActivity;
import ww.smartexpress.driver.ui.auth.AuthActivity;
import ww.smartexpress.driver.ui.award.AwardActivity;
import ww.smartexpress.driver.ui.base.fragment.BaseFragmentViewModel;
import ww.smartexpress.driver.ui.history.TripHistoryActivity;
import ww.smartexpress.driver.ui.home.HomeActivity;
import ww.smartexpress.driver.ui.index.IndexActivity;
import ww.smartexpress.driver.ui.password.renew.RenewPasswordActivity;
import ww.smartexpress.driver.ui.profile.edit.EditProfileActivity;
import ww.smartexpress.driver.ui.statistic.StatisticActivity;
import ww.smartexpress.driver.ui.wallet.WalletActivity;

public class ProfileFragmentViewModel extends BaseFragmentViewModel {
    public MutableLiveData<ProfileResponse> profile = new MutableLiveData<>();;
    public ObservableField<WalletResponse> wallet = new ObservableField<>();
    public ObservableField<UserEntity> user = new ObservableField<>();
    public ObservableField<SettingResponse> settings = new ObservableField<>();
    public ProfileFragmentViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
        getMyWallet();
        getHotline();
    }
    public Repository getRepository(){
        return repository;
    }

    public void goProfileSetting(){
        Intent intent = new Intent(application.getCurrentActivity(), EditProfileActivity.class);
        application.getCurrentActivity().startActivity(intent);
    }
    public void goActivity(){
        Intent intent = new Intent(application.getCurrentActivity(), StatisticActivity.class);
        application.getCurrentActivity().startActivity(intent);
    }
    public void goAllBikeConfig(){
        Intent intent = new Intent(application.getCurrentActivity(), AllBikeServiceActivity.class);
        application.getCurrentActivity().startActivity(intent);
    }
    public void goTripHistory(){
        Intent intent = new Intent(application.getCurrentActivity(), TripHistoryActivity.class);
        application.getCurrentActivity().startActivity(intent);
    }
    public void goWallet(){
        Intent intent = new Intent(application.getCurrentActivity(), WalletActivity.class);
        application.getCurrentActivity().startActivity(intent);
    }

    public void logoutDialog(){
        Dialog dialog = new Dialog(application.getCurrentActivity());
        DialogLogoutBinding dialogLogoutBinding = DataBindingUtil.inflate(LayoutInflater.from(application.getCurrentActivity()),R.layout.dialog_logout,null, false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(dialogLogoutBinding.getRoot());
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        dialog.setCanceledOnTouchOutside(true);

        dialogLogoutBinding.btnLogout.setOnClickListener(view -> {
            repository.getSharedPreferences().removeKey(PreferencesService.KEY_BEARER_TOKEN);
            Intent intent = new Intent(application.getCurrentActivity(), AuthActivity.class);
            application.getCurrentActivity().startActivity(intent);
            application.getCurrentActivity().finish();
            dialog.dismiss();
        });
        dialogLogoutBinding.btnCancel.setOnClickListener(view -> {
            dialog.dismiss();
        });
        dialog.show();
    }

    public void goReviewByCustomer(){
        Intent intent = new Intent(application.getCurrentActivity(), AchievementActivity.class);
        application.getCurrentActivity().startActivity(intent);
    }

    public void goAwardProgram(){
        Intent intent = new Intent(application.getCurrentActivity(), AwardActivity.class);
        application.getCurrentActivity().startActivity(intent);
    }

    public void renewPassword(){
        Intent intent = new Intent(application.getCurrentActivity(), RenewPasswordActivity.class);
        application.getCurrentActivity().startActivity(intent);
    }

    public void getMyWallet(){
        compositeDisposable.add(repository.getApiService().getMyWallet()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if(response.isResult()){
                        wallet.set(response.getData());
                    }else {
                        showErrorMessage(response.getMessage());
                    }
                },error->{
                    showErrorMessage(application.getString(R.string.newtwork_error));
                    error.printStackTrace();
                })
        );
    }

    public void getHotline(){
        showLoading();
        compositeDisposable.add(repository.getApiService().getSettings("support-hotline")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if(response.isResult()){
                        settings.set(response.getData());
                        hideLoading();
                    }else {
                        hideLoading();
                        showErrorMessage(response.getMessage());
                    }
                },error->{
                    hideLoading();
                    showErrorMessage(application.getString(R.string.newtwork_error));
                    error.printStackTrace();
                })
        );
    }

    public void callSystem(){
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + settings.get().getSettingValue()));
        application.getCurrentActivity().startActivity(callIntent);
    }
}
