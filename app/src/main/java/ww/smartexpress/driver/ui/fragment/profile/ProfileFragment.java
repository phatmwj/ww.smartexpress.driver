package ww.smartexpress.driver.ui.fragment.profile;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ww.smartexpress.driver.BR;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.databinding.FragmentProfileBinding;
import ww.smartexpress.driver.di.component.FragmentComponent;
import ww.smartexpress.driver.ui.base.fragment.BaseFragment;

public class ProfileFragment extends BaseFragment<FragmentProfileBinding, ProfileFragmentViewModel> {
    @Override
    public int getBindingVariable() {
        return BR.vm;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_profile;
    }

    @Override
    protected void performDataBinding() {
        binding.setLifecycleOwner(this);

    }

    @Override
    protected void performDependencyInjection(FragmentComponent buildComponent) {
        buildComponent.inject(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String userId = viewModel.getRepository().getSharedPreferences().getUserId();
        if(userId != null) {
            viewModel.getRepository().getRoomService().userDao().findById(Long.valueOf(userId)).observe(this, userEntity -> {
                viewModel.user.set(userEntity);
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.getMyWallet();
    }
}
