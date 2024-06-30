package ww.smartexpress.driver.ui.chat;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import java.util.Date;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.RequestBody;
import ww.smartexpress.driver.MVVMApplication;
import ww.smartexpress.driver.constant.Constants;
import ww.smartexpress.driver.data.Repository;
import ww.smartexpress.driver.data.model.api.ResponseWrapper;
import ww.smartexpress.driver.data.model.api.response.ChatMessage;
import ww.smartexpress.driver.data.model.api.response.Customer;
import ww.smartexpress.driver.data.model.api.response.MessageChat;
import ww.smartexpress.driver.data.model.api.response.RoomResponse;
import ww.smartexpress.driver.data.model.api.response.UploadFileResponse;
import ww.smartexpress.driver.data.websocket.Command;
import ww.smartexpress.driver.data.websocket.Message;
import ww.smartexpress.driver.ui.base.activity.BaseViewModel;

public class ChatViewModel extends BaseViewModel {
    public ObservableField<String> message = new ObservableField<>();
    public ObservableField<Long> customerId = new ObservableField<>(0L);
    public ObservableField<String> customerAvatar = new ObservableField<>();
    public ObservableField<String> customerName = new ObservableField<>();
    public ObservableField<String> codeBooking = new ObservableField<>();
    public ObservableField<String> bookingId = new ObservableField<>();
    public ObservableField<String> roomId = new ObservableField<>();
    public MutableLiveData<MessageChat> messageChat = new MutableLiveData<>(null);
    public ObservableField<List<MessageChat>> messageChatList = new ObservableField<>();

    public ChatViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
    }

    public Long getUserId(){
        return Long.valueOf(repository.getSharedPreferences().getUserId());
    }

    public void back(){
        application.getCurrentActivity().onBackPressed();
    }

    public void sendMessage(){
        if(message.get()== null || message.get().trim().equals("")){
            return;
        }
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setCodeBooking(codeBooking.get());
        chatMessage.setMessage(message.get());
        chatMessage.setRoomId(roomId.get());
        chatMessage.setBookingId(bookingId.get());
        chatMessage.setMessageId(String.valueOf((new Date()).getTime()));
        chatMessage.setAvatar("avatar");

        Message message1 = new Message();
        message1.setCmd(Command.CM_SEND_MESSAGE);
        message1.setPlatform(0);
        message1.setClientVersion("1.0");
        message1.setLang("vi");
        message1.setToken(repository.getSharedPreferences().getToken());
        message1.setApp(Constants.APP_DRIVER);
        message1.setData(chatMessage);
        application.getWebSocketLiveData().sendEvent(message1);
        addMessage();
        message.set("");
    }
    public void addMessage(){
        MessageChat messageChat1 = new MessageChat();
        messageChat1.setSender(Long.valueOf(repository.getSharedPreferences().getUserId()));
        messageChat1.setMsg(message.get());
        messageChat.setValue(messageChat1);
    }

    public Observable<ResponseWrapper<RoomResponse>> getRoomChat(Long roomId){
        return repository.getApiService().getRoomChat(roomId)
                .doOnNext(res ->{
                    messageChatList.set(res.getData().getChatDetails());
                });
    }

    public Observable<ResponseWrapper<UploadFileResponse>> uploadImage(RequestBody requestBody){
        return repository.getApiService().uploadFile(requestBody);
    }



}
