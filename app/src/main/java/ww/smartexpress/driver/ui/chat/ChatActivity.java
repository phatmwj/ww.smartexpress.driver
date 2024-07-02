package ww.smartexpress.driver.ui.chat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.Getter;
import lombok.Setter;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import ww.smartexpress.driver.BR;
import ww.smartexpress.driver.MVVMApplication;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.constant.Constants;
import ww.smartexpress.driver.data.model.api.response.ChatMessage;
import ww.smartexpress.driver.data.model.api.response.MessageChat;
import ww.smartexpress.driver.databinding.ActivityChatBinding;
import ww.smartexpress.driver.di.component.ActivityComponent;
import ww.smartexpress.driver.ui.base.activity.BaseActivity;
import ww.smartexpress.driver.ui.chat.adapter.ImageAdapter;
import ww.smartexpress.driver.ui.chat.adapter.MessageAdapter;

public class ChatActivity extends BaseActivity<ActivityChatBinding, ChatViewModel> {

    MessageAdapter messageAdapter;
    BottomSheetBehavior sheetBehavior;
    ImageAdapter imageAdapter;
    private static final int REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 100;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private static final int REQUEST_IMAGE_CAPTURE = 300;
    Bitmap photo = null;
    Uri imageUri;

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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        //load Message
        messageAdapter = new MessageAdapter();
        messageAdapter.currentUserId = viewModel.getUserId();
        loadChatDetails();
        viewModel.messageChat.observe(this, messageChat -> {
            messageAdapter.addMessage(messageChat);
            viewBinding.rcChat.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
        });


        //bottom sheet
        sheetBehavior = BottomSheetBehavior.from(viewBinding.bottomSheet);
        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        sheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_EXPANDED:
                        viewModel.isBottomSheetExpanded.set(true);
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        viewModel.isBottomSheetExpanded.set(false);
                        break;

                    case BottomSheetBehavior.STATE_HIDDEN:
                    case BottomSheetBehavior.STATE_DRAGGING:
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });


        //
        viewBinding.txtMsg.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                   if(viewModel.isImageLoaded.get()){
                       closeLib();
                   }
                } else {

                }
            }
        });

        viewBinding.layoutCropImage.setOnClickListener(view -> {
        });
        viewBinding.layoutCropImage.setOnTouchListener((view, motionEvent) -> {
            return false;
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePictureCamera();
                } else {
                    Toast.makeText(this, "Permission denied: Cannot access Camera", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_PERMISSION_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateUiSendImage();
                } else {
                    Toast.makeText(this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    public void loadImage(){
        imageAdapter = new ImageAdapter(getAllImages(this), this);
        GridLayoutManager layoutManager = new GridLayoutManager(this,3);
        viewBinding.rcImage.setLayoutManager(layoutManager);
        viewBinding.rcImage.setAdapter(imageAdapter);

        imageAdapter.setOnItemClickListener((uri, pos) -> {
            if(Objects.equals(imageAdapter.getImageSelected(), pos)){
                imageAdapter.setImageSelected(null);
                photo = null;
                imageUri = null;
                viewModel.isImageSelected.set(false);
            }else {
                imageAdapter.setImageSelected(pos);
                getBitmapFromUri(uri);
                imageUri = uri;
                viewModel.isImageSelected.set(true);
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
        messageAdapter.setOnItemClickListener(messageChat -> {
            hideKeyboard();
            if(viewModel.isImageLoaded.get()){
                closeLib();
            }
            if(messageChat.getMsg().contains("AVATAR")){
                viewModel.zoomImage(messageChat.getMsg());
            }
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
//        if (requestCode == ImagePicker.REQUEST_CODE) {
//            if (resultCode == RESULT_OK && data != null) {
//                Uri resultUri = data.getData();
//                if(resultUri == null) return;
//                final InputStream imageStream;
//                try {
//                    imageStream = getContentResolver().openInputStream(resultUri);
//                } catch (FileNotFoundException e) {
//                    throw new RuntimeException(e);
//                }
//                photo = BitmapFactory.decodeStream(imageStream);
//                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//            }
//        }

        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            if (resultUri != null) {
                viewBinding.imageCrop.setImageURI(resultUri);
                viewModel.isCropView.set(true);
                getBitmapFromUri(resultUri);
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
            Toast.makeText(this, "Crop error: " + cropError.getMessage(), Toast.LENGTH_SHORT).show();
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) extras.get("data");
            imageUri = bitmapToUri(bitmap);
            openCrop();
            viewModel.isCropView.set(true);
        }

    }

    @Override
    public void onBackPressed() {
        if(viewModel.isCropView.get()){
            cancelCrop();
            return;
        }
        if(viewModel.isImageLoaded.get()){
            closeLib();
            return;
        }
        hideKeyboard();
        super.onBackPressed();


    }

    public static List<Uri> getAllImages(Context context) {
        List<Uri> imageUris = new ArrayList<>();
        String[] projection = { MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME };

        // Query lấy tất cả các ảnh
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                MediaStore.Images.Media.DATE_ADDED + " DESC"
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int idColumn = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                long id = cursor.getLong(idColumn);
                Uri uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, String.valueOf(id));
                imageUris.add(uri);
            }
            cursor.close();
        }

        return imageUris;
    }

    public void openLib(){
        hideKeyboard();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_READ_EXTERNAL_STORAGE);
        } else {
            updateUiSendImage();
        }
    }

    private void updateUiSendImage(){
        loadImage();
        viewModel.isImageLoaded.set(true);
//        viewBinding.mainLayout.setTranslationY(-sheetBehavior.getPeekHeight());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0,0,0,sheetBehavior.getPeekHeight());
        viewBinding.mainLayout.setLayoutParams(params);
//        viewBinding.rcChat.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
    }
    public void closeLib(){
        photo = null;
        viewModel.isImageLoaded.set(false);
        viewModel.isImageSelected.set(false);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0,0,0,0);
        viewBinding.mainLayout.setLayoutParams(params);
        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void getBitmapFromUri(Uri uri){
        if(uri == null) return;
        final InputStream imageStream;
        try {
            imageStream = getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        photo = BitmapFactory.decodeStream(imageStream);
    }

    public void sendImageMessage() {

//        viewModel.showLoading();
        // Upload image if necessary
        if (photo != null) {
            // Upload avatar then update profile

            // Convert the Bitmap to a byte array
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] imageByteArray = byteArrayOutputStream.toByteArray();

            // Create a request body for the image
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageByteArray);

            // Create a multipart request builder
            MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM);
            requestBodyBuilder.addFormDataPart("file", "image.jpg", requestFile);
            requestBodyBuilder.addFormDataPart("type", Constants.FILE_TYPE_AVATAR);

            viewModel.compositeDisposable.add(viewModel.uploadImage(requestBodyBuilder.build())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(uploadResponse -> {
                        if (uploadResponse.isResult() && uploadResponse.getData().getFilePath() != null) {
//                          viewModel.image.set(uploadResponse.getData().getFilePath());
                            viewModel.sendImageMessage(uploadResponse.getData().getFilePath());
                            closeLib();
                            cancelCrop();
                        }
//                        viewModel.hideLoading();
                    },error ->{
//                        viewModel.hideLoading();
                            })
            );

        }else {
            viewModel.showErrorMessage("Chưa có ảnh nào được chọn");
            viewModel.hideLoading();
        }
    }

    public void openCrop(){
        String fileName = "croppedImage_" + System.currentTimeMillis() + ".jpg";
        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), fileName));

        UCrop.Options options = new UCrop.Options();
//        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
//        options.setCompressionQuality(90);
        options.setToolbarColor(ContextCompat.getColor(this, R.color.green_light_app)); // Màu thanh công cụ
//        options.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark)); // Màu thanh trạng thái
        options.setActiveControlsWidgetColor(ContextCompat.getColor(this, R.color.green_light_app)); // Màu của các nút điều khiển đang hoạt động
        options.setToolbarWidgetColor(ContextCompat.getColor(this, R.color.white)); // Màu văn bản trên thanh công cụ


        // Bắt đầu uCrop
        UCrop.of(imageUri, destinationUri)// Cài đặt tỷ lệ khung hình (ví dụ: 1:1)
                .withMaxResultSize(800, 800) // Cài đặt kích thước tối đa của ảnh kết quả
                .withOptions(options)
                .start(this);
    }

    public void cancelCrop(){
        viewModel.isCropView.set(false);
    }

    public void takePictureCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        }else {
            closeLib();
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private Uri bitmapToUri(Bitmap bitmap) {
        // Get the context's content resolver
        OutputStream outputStream = null;
        File file = null;
        Uri uri = null;

        try {
            // Create a file to save the bitmap
            file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "image_" + System.currentTimeMillis() + ".png");

            // Compress the bitmap and write it to the file
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();

            // Close the output stream
            outputStream.close();

            // Get the Uri of the saved file
            uri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Ensure the output stream is closed
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return uri;
    }

}
