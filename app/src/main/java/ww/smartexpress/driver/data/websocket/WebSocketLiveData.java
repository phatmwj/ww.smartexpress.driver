package ww.smartexpress.driver.data.websocket;

import androidx.annotation.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;

import lombok.Getter;
import lombok.Setter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import timber.log.Timber;
import ww.smartexpress.driver.BuildConfig;
import ww.smartexpress.driver.constant.Constants;
import ww.smartexpress.driver.data.model.api.response.BookingCode;

public class WebSocketLiveData implements Runnable{
    private static final int SOCKET_STATE_NONE = 0;
    private static final int SOCKET_STATE_CONNECTING = 1;
    private static final int SOCKET_STATE_CONNECTED = 2;
    private static final int SOCKET_STATE_WAITING_RESPONSE = 3;

    @Setter
    private SocketListener socketListener;
    private BlockingQueue<Message> requests;
    private ScheduledExecutorService executor;

    private static WebSocketLiveData instance = new WebSocketLiveData();
    private WebSocket webSocket;
    @Getter
    private int socketState = SOCKET_STATE_NONE;

    private OkHttpClient okHttpClient;

    @Getter
    @Setter
    private String codeBooking = "0";

    @Setter
    @Getter
    private boolean isAppOnline = true;


    private long lastTimeCall = System.currentTimeMillis();
    private Message lastMessage;

    @Setter
    private String session;

    private Set<String> timeOutId = new HashSet<>();
    private Set<String> expireToken = new HashSet<>();
    private long timeout = Message.Timeout.DEFAULT; // default timeout in ms

    public void setTimeout(long timeout) {
        this.timeout = timeout;
        Timber.d("SET TIMEOUT TO " + timeout);
    }

    private WebSocketLiveData() {
        okHttpClient = new OkHttpClient.Builder() .build();
        executor = Executors.newSingleThreadScheduledExecutor();
        requests = new LinkedBlockingQueue<>();
    }

    public static WebSocketLiveData getInstance() {
        return instance;
    }

    public void startSocket(){
        Timber.d("========> vao startSocket");
        isRunning = true;
        requests.clear();
        new Thread(this).start();
    }

    public void stopSocket(){
        Timber.d("========> vao stopSocket");
        try {
            if (webSocket != null) {
                isRunning = false;
                webSocket.close(1000,null);
                socketState = SOCKET_STATE_NONE;
                webSocket = null;
            } else {
                Timber.d("WEB SOCKET NULL");
                isRunning = false;
                socketState = SOCKET_STATE_NONE;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void reconnect(){
        Timber.d("RECONNECT");
        WebSocketLiveData.this.webSocket.cancel(); // destroy previous callback
        socketState = SOCKET_STATE_NONE;
    }

    public Long getIdleTime(){
        if (lastTimeCall != -1 ) {
            return System.currentTimeMillis() - lastTimeCall;
        }
        return lastTimeCall;
    }

    protected void postValue(SocketEventModel value) {
        if( socketListener !=null){
            if (value != null && value.getMessage() != null && value.getMessage().getResponseCode() != null
            && (value.getMessage().getResponseCode() == 200 || value.getMessage().getResponseCode() == 400)) {
                socketListener.onMessage(value);
            } else if (value != null && value.getMessage() != null && value.getMessage().getResponseCode() != null
                    && value.getMessage().getResponseCode() == 401){
                expireToken.add(value.getMessage().getToken());
                socketListener.onSessionExpire();
            }
        }else{
            Timber.d("SOCKET LISTENER NULL");
        }

    }

    private synchronized void connect() {
        if(socketState == SOCKET_STATE_NONE){
            socketState = SOCKET_STATE_CONNECTING;
            Timber.d("========> VAO CONNECT");
            try {
                Request request = new Request.Builder().url(BuildConfig.WS_URL)
                        .addHeader("deviceId", "Android").build();
                webSocket = okHttpClient.newWebSocket(request, createNewSocketListener());
                Timber.d("========> CONNECTED");
            }catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    public boolean sendEvent(Message message) {
        if (webSocket == null) {
            //Timber.d("CAN NOT SEND, SOCKET NULL");
            return false;
        }

        if(socketState != SOCKET_STATE_NONE){
            //Timber.d("ADD REQUEST TO QUEUE");
            setTimeout(message.getTimeout());
            requests.add(message);
            return true;
        }

        return false;
    }

    private void sendMessage(){
        if (isAppOnline && lastMessage != null && webSocket != null) {
            if (expireToken.contains(lastMessage.getToken())) {
                if (!expireToken.contains(session)) {
                    Timber.d("UPDATE EXPIRE TOKEN");
                    lastMessage.setToken(session);
                } else {
                    Timber.d("IGNORE REQUEST FROM EXPIRE TOKEN");
                    lastMessage = null; // set message to null when token expire
                    socketListener.onSessionExpire();
                    return;
                }
            }
            lastTimeCall = System.currentTimeMillis();
            socketState = SOCKET_STATE_WAITING_RESPONSE;
            lastMessage.setRequestId(Long.toHexString(System.currentTimeMillis()));
            Timber.d(lastMessage.getPayload());
            webSocket.send(lastMessage.getPayload());
        } else {
            Timber.d("LAST MESSAGE NULL AND SOCKET NULL");
        }
    }

    public void sendPing(){
        if(session!=null && socketState == SOCKET_STATE_CONNECTED && webSocket != null && isAppOnline){
                lastTimeCall = System.currentTimeMillis();
                if (expireToken.contains(session)) {
                    Timber.d("IGNORE REQUEST FROM EXPIRE TOKEN");
                    socketListener.onSessionExpire();
                    return;
                }
                socketState = SOCKET_STATE_WAITING_RESPONSE;
                Message message = new Message();
                message.setCmd(Command.COMMAND_PING);
                message.setPlatform(0);
                message.setClientVersion("1.0");
                message.setLang("vi");
                message.setToken(session);
                message.setApp(Constants.APP_DRIVER);
                message.setData(new BookingCode(codeBooking));
//            String cmd = "{ \"cmd\": \"CLIENT_PING\", \"platform\": 1, \"token\": \""+session+"\" }";
                webSocket.send(message.getPayload());
                Timber.d("========>SEND: %s", message.getPayload());
        }else if(session == null){
            Timber.d("ERROR TOKEN NULL");
            stopSocket();
        }else {
            Timber.d("PING ERROR");
            stopSocket();
        }

    }

    public void handle401(){
        stopSocket();
        startSocket();
    }

    public void doVerifyToken(){
        if (expireToken.contains(session)) {
            Timber.d("IGNORE REQUEST FROM EXPIRE TOKEN");
            socketListener.onSessionExpire();
            return;
        }
        if (webSocket == null) {
            Timber.d("WEB SOCKET IS NULL, IGNORE SEND VERIFY");
            return;
        }
        lastTimeCall = System.currentTimeMillis();
        socketState = SOCKET_STATE_WAITING_RESPONSE;
//        String cmd = "{ \"cmd\": \"VERIFY_TOKEN_CLIENT\", \"platform\": 1, \"token\": \""+session+"\" }";
        Message message = new Message();
        message.setCmd(Command.COMMAND_VERIFY_TOKEN);
        message.setPlatform(0);
        message.setToken(session);
        webSocket.send(message.getPayload());
    }
    private WebSocketListener createNewSocketListener(){
        return  new WebSocketListener() {

            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                socketState = SOCKET_STATE_CONNECTED;
                if(isAppOnline && socketListener != null){
                    Message message = new Message();
                    message.setCmd(Command.COMMAND_CLIENT_INFO);
                    message.setPlatform(0);
                    message.setClientVersion("1.0");
                    message.setLang("vi");
                    message.setToken(session);
                    message.setApp(Constants.APP_DRIVER);
                    message.setData(new BookingCode(codeBooking));
                    webSocket.send(message.getPayload());
                    Timber.d("SEND: %s", message.getPayload());
                    socketListener.onConnected();
                }
                lastTimeCall = System.currentTimeMillis();
                Timber.d("========> vao onOpen");
//                if(session!=null){
//                    Timber.d("SESSION NOT NULL, VERIFY TOKEN");
//                    doVerifyToken();
//                }
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                Timber.d("========> RESPONSE %s", Message.fromJson(text,Message.class).getPayload());
                socketState = SOCKET_STATE_CONNECTED;
                lastTimeCall = System.currentTimeMillis();

                try {
                    SocketEventModel eventModel = new SocketEventModel(null);
                    Message message = Message.fromJson(text, Message.class);
                    if(!message.getCmd().equals(Command.COMMAND_PING)){
                        eventModel.setEvent(SocketEventModel.EVENT_MESSAGE);
                        eventModel.setMessage(message);
                        lastMessage = null; // set message to null when receive response
                        if (!timeOutId.contains(message.getRequestId())){
                            postValue(eventModel);
                        }
                    }else {
                        eventModel.setEvent(SocketEventModel.EVENT_MESSAGE);
                        eventModel.setMessage(message);
                        lastMessage = null; // set message to null when receive response
                        if (!timeOutId.contains(message.getRequestId())){
                            postValue(eventModel);
                        }
                    }
                    if (message.getCmd().equals(Command.COMMAND_PING) && message.getResponseCode() == 401){
                        expireToken.add(message.getToken());
                        socketListener.onPingFailure();
                    }
                }catch (Exception ex) {
                    ex.printStackTrace();
                    if(isAppOnline && socketListener!=null){
               //         Timber.d("IS APP ONLINE : %b, SOCKET NULL : %b",isAppOnline,socketListener == null);
                        socketListener.onError();
                    }
                }
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
                Timber.d("========> vao onClosed");
                lastTimeCall = System.currentTimeMillis();
                socketListener.onClosed();
                SocketEventModel eventModel = new SocketEventModel(null);
                eventModel.setEvent(SocketEventModel.EVENT_OFFLINE);
                postValue(eventModel);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
                super.onFailure(webSocket, t, response);
                Timber.d("========> vao onFailure");
                Timber.d(String.valueOf(response));
                WebSocketLiveData.this.webSocket.cancel();
                socketState = SOCKET_STATE_NONE;
                lastTimeCall = System.currentTimeMillis();
                if (lastMessage != null) {
                    timeOutId.add(lastMessage.getRequestId());
                }
                if(isAppOnline && socketListener!=null){
//                    Timber.d("IS APP ONLINE : %b, SOCKET NULL : %b",isAppOnline,socketListener == null);
//                    session = null;
                    socketListener.onError();
                }
            }
        };
    }

    private synchronized void postTimeout(){
        //Timber.d("========> vao postTimeout ");
       if(isAppOnline && socketListener!=null){
           socketListener.onTimeout(lastMessage);
       }
    }


    private boolean isRunning = true;
    @Override
    public void  run() {
        synchronized (this){
            while(isRunning){
                try {
                    if(socketState == SOCKET_STATE_NONE){
                        Timber.d("SOCKET STATE NONE");
                        connect();
                    }else if(socketState == SOCKET_STATE_CONNECTED && isAppOnline){
                        if(requests.size() > 0){
                            Timber.d("SOCKET STATE CONNECTED, GET REQUEST");
                            lastMessage = requests.take();
                        }
                        if(lastMessage!=null){
                            Timber.d("SOCKET STATE CONNECTED, SEND REQUEST");
                            sendMessage();
                        }else if(System.currentTimeMillis() - lastTimeCall > 20000){
                            //do ping to server if < 30 second
                            Timber.d("SEND PING");
                            sendPing();
                        }

                    }else if(socketState == SOCKET_STATE_WAITING_RESPONSE){
                        if(System.currentTimeMillis() - lastTimeCall > timeout){ //set timeout
                            if (lastMessage != null) {
                                timeOutId.add(lastMessage.getRequestId());
                            }
                            postTimeout();
//                            lastTimeCall = -1;
                            lastMessage = null; // set message null when timeout
                            socketState = SOCKET_STATE_CONNECTED;
                        }
                    }

                    wait(150L);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    public void clearQueue(){
      //  Timber.d("CLEAR REQUESTS QUEUE");
        requests.clear();
    }

}
