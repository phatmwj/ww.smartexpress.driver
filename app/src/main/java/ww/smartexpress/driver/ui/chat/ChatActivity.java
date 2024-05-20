package ww.smartexpress.driver.ui.chat;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ww.smartexpress.driver.BR;
import ww.smartexpress.driver.MVVMApplication;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.data.model.api.response.ChatMessage;
import ww.smartexpress.driver.data.model.api.response.MessageChat;
import ww.smartexpress.driver.databinding.ActivityChatBinding;
import ww.smartexpress.driver.di.component.ActivityComponent;
import ww.smartexpress.driver.ui.base.activity.BaseActivity;
import ww.smartexpress.driver.ui.chat.adapter.MessageAdapter;

public class ChatActivity extends BaseActivity<ActivityChatBinding, ChatViewModel> {

    MessageAdapter messageAdapter;
    private Long roomId;
    private String codeBooking;

    @Override
    public int getLayoutId() {
        return R.layout.activity_chat;
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
        Intent intent = getIntent();
        if (intent.getLongExtra("roomId",0)!=0){
            roomId = intent.getLongExtra("roomId",0);
        }
        if (intent.getStringExtra("codeBooking")!= null){
            codeBooking = intent.getStringExtra("codeBooking");
            viewModel.codeBooking.set(codeBooking);
        }

        messageAdapter = new MessageAdapter();
        messageAdapter.currentUserId = viewModel.getUserId();
        loadChatDetails();
//        loadMessages();
        viewModel.messageChat.observe(this, messageChat -> {
            messageAdapter.addMessage(messageChat);
            viewBinding.rcChat.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
        });
    }

    public void loadMessages(){
        messageAdapter.setMessages(viewModel.messageChatList.get());
        RecyclerView.LayoutManager layout = new LinearLayoutManager(this
                ,LinearLayoutManager.VERTICAL, false);
        viewBinding.rcChat.setLayoutManager(layout);
        viewBinding.rcChat.setItemAnimator(new DefaultItemAnimator());
        viewBinding.rcChat.setAdapter(messageAdapter);
        if(viewModel.messageChatList.get()!= null){
            viewBinding.rcChat.smoothScrollToPosition(viewModel.messageChatList.get().size() - 1);
        }
//        viewBinding.rcChat.scrollToPosition(messageAdapter.getItemCount()-1);
        messageAdapter.setOnItemClickListener(trip -> {

        });
    }

    public void loadChatDetails(){
        viewModel.showLoading();
        viewModel.compositeDisposable.add(viewModel.getRoomChat(roomId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if(response.isResult()){
                        viewModel.messageChatList.set(response.getData().getChatDetails());
                        messageAdapter.setMessages(viewModel.messageChatList.get());
                        viewModel.customerId.set(response.getData().getCustomer().getId());
                        viewModel.customerAvatar.set(response.getData().getCustomer().getAvatar());
                        viewModel.customerName.set(response.getData().getCustomer().getName());
                        loadMessages();
                        viewModel.showSuccessMessage(response.getMessage());
                    }else {
                        viewModel.showErrorMessage(response.getMessage());
                    }
                    viewModel.hideLoading();
                },error->{
                    viewModel.showErrorMessage(application.getString(R.string.newtwork_error));
                    error.printStackTrace();
                    viewModel.hideLoading();
                })
        );

    }

    @Override
    protected void onResume() {
        super.onResume();
        ChatMessage chatMessage = ((MVVMApplication) getApplication()).getChatMessage();
        if(chatMessage != null){
            MessageChat messageChat1 = new MessageChat();
            messageChat1.setSender(viewModel.customerId.get());
            messageChat1.setMsg(chatMessage.getMessage());
            messageChat1.setSenderAvatar(viewModel.customerAvatar.get());
            viewModel.messageChat.setValue(messageChat1);
            ((MVVMApplication) getApplication()).setChatMessage(null);
        }
    }

    @Override
    public void onBackPressed() {
        hideKeyboard();
        super.onBackPressed();
    }
}
