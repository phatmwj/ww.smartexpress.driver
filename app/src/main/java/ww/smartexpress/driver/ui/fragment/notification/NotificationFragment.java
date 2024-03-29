package ww.smartexpress.driver.ui.fragment.notification;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ww.smartexpress.driver.BR;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.data.model.api.response.Notification;
import ww.smartexpress.driver.data.model.api.response.Notification;
import ww.smartexpress.driver.databinding.FragmentNotificationBinding;
import ww.smartexpress.driver.di.component.FragmentComponent;
import ww.smartexpress.driver.ui.base.fragment.BaseFragment;
import ww.smartexpress.driver.ui.fragment.notification.adapter.NotificationAdapter;

public class NotificationFragment extends BaseFragment<FragmentNotificationBinding, NotificationFragmentViewModel> {
    
    NotificationAdapter notificationAdapter;
    @Override
    public int getBindingVariable() {
        return BR.vm;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_notification;
    }

    @Override
    protected void performDataBinding() {
        loadNotifications();
    }

    @Override
    protected void performDependencyInjection(FragmentComponent buildComponent) {
        buildComponent.inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void loadNotifications(){
        List<Notification> notifications = new ArrayList<>();
        notifications.add(new Notification("1"));
        notifications.add(new Notification("2"));
        notifications.add(new Notification("1"));
        notifications.add(new Notification("2"));
        notifications.add(new Notification("1"));
        notifications.add(new Notification("2"));


        RecyclerView.LayoutManager layout = new LinearLayoutManager(getActivity().getApplicationContext()
                ,LinearLayoutManager.VERTICAL, false);
        binding.rcNotification.setLayoutManager(layout);
        binding.rcNotification.setItemAnimator(new DefaultItemAnimator());
        notificationAdapter = new NotificationAdapter(notifications);
        binding.rcNotification.setAdapter(notificationAdapter);
        notificationAdapter.setOnItemClickListener(notification -> {

        });
    }
}
