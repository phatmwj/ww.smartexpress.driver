package ww.smartexpress.driver.ui.chat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.FileNotFoundException;
import java.io.InputStream;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.Getter;
import lombok.Setter;
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
    private Bitmap updatedAvatar;
    private static final int CAMERA_REQUEST = 100;
    private static final int STORAGE_REQUEST = 200;
    String[] cameraPermission;
    String[] storagePermission;
    Bitmap photo;
    private static final int PICK_IMAGE_REQUEST = 1;
    BottomSheetBehavior sheetBehavior;
    @Getter
    @Setter
    private Long bookingId;
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
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        Intent intent = getIntent();
        if (intent.getLongExtra("roomId",0)!=0){
            viewModel.roomId.set(String.valueOf(intent.getLongExtra("roomId",0)));
        }
        if (intent.getStringExtra("codeBooking")!= null){
            viewModel.codeBooking.set(intent.getStringExtra("codeBooking"));
        }
        if (intent.getLongExtra("bookingId",0)!= 0){
            viewModel.bookingId.set(String.valueOf(intent.getLongExtra("bookingId",0)));
            bookingId = Long.valueOf(viewModel.bookingId.get());
        }

        messageAdapter = new MessageAdapter();
        messageAdapter.currentUserId = viewModel.getUserId();
        loadChatDetails();
//        loadMessages();
        viewModel.messageChat.observe(this, messageChat -> {
            messageAdapter.addMessage(messageChat);
            viewBinding.rcChat.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
        });

        sheetBehavior = BottomSheetBehavior.from(viewBinding.bottomSheet);
        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                    case BottomSheetBehavior.STATE_EXPANDED:
                    case BottomSheetBehavior.STATE_COLLAPSED:
                    case BottomSheetBehavior.STATE_DRAGGING:
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                viewBinding.mainLayout.setTranslationY(-slideOffset * bottomSheet.getHeight());
            }
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
        viewModel.compositeDisposable.add(viewModel.getRoomChat(Long.valueOf(viewModel.roomId.get()))
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
//                        viewModel.showSuccessMessage(response.getMessage());
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImagePicker.REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                Uri resultUri = data.getData();
                if(resultUri == null) return;
                final InputStream imageStream;
                try {
                    imageStream = getContentResolver().openInputStream(resultUri);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
                photo = BitmapFactory.decodeStream(imageStream);
                viewBinding.imageView.setImageBitmap(photo);
                updatedAvatar = photo;
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        }

    }

    @Override
    public void onBackPressed() {
        hideKeyboard();
        super.onBackPressed();
    }

    public void takeFromCamera() {
        ImagePicker.with(this)
                .cameraOnly()
                .cropSquare()
                .start();
    }

    public void takeFromGallery() {
        ImagePicker.with(this)
                .galleryOnly()
                .cropSquare()
                .start();
    }

    private void requestStoragePermission() {
        requestPermissions(storagePermission, STORAGE_REQUEST);
    }

    private void requestCameraPermission() {
        requestPermissions(cameraPermission, CAMERA_REQUEST);
    }

    // checking storage permissions
    private Boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    // checking camera permissions
    private Boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_REQUEST: {
                if (grantResults.length > 0) {
                    boolean camera_accepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageaccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (camera_accepted && writeStorageaccepted) {
                        takeFromCamera();
                    } else {
                        viewModel.showErrorMessage("Please Enable Camera and Storage Permissions");
                    }
                }
            }
            break;
            case STORAGE_REQUEST: {
                if (grantResults.length > 0) {
                    boolean writeStorageaccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageaccepted) {
                        takeFromGallery();
                    } else {
                        viewModel.showErrorMessage("Please Enable Storage Permissions");
                    }
                }
            }
            break;
        }
    }

    public void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
}
