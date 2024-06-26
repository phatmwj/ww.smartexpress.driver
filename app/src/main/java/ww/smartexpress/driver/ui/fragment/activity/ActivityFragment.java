package ww.smartexpress.driver.ui.fragment.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import ww.smartexpress.driver.BR;
import ww.smartexpress.driver.MVVMApplication;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.constant.Constants;
import ww.smartexpress.driver.data.model.api.request.DriverStateRequest;
import ww.smartexpress.driver.data.model.api.response.CurrentBooking;
import ww.smartexpress.driver.databinding.DialogCancelBinding;
import ww.smartexpress.driver.databinding.DialogShippingImgBinding;
import ww.smartexpress.driver.databinding.FragmentShippingBinding;
import ww.smartexpress.driver.di.component.FragmentComponent;
import ww.smartexpress.driver.ui.base.fragment.BaseFragment;
import ww.smartexpress.driver.ui.fragment.activity.adapter.ShippingAdapter;
import ww.smartexpress.driver.ui.shipping.ShippingActivity;

public class ActivityFragment extends BaseFragment<FragmentShippingBinding, ActivityFragmentViewModel> implements LocationListener {

    ShippingAdapter shippingAdapter;
    private LocationManager locationManager;
    private Boolean isLogin;
    private Bitmap updatedAvatar;
    private static final int CAMERA_REQUEST = 100;
    private static final int STORAGE_REQUEST = 200;
    String[] cameraPermission;
    String[] storagePermission;
    Bitmap photo;
    DialogShippingImgBinding dialogBinding;

    MVVMApplication mvvmApplication;

    private final ActivityResultLauncher<IntentSenderRequest> locationSettingsLauncher =
            registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                } else {
                    Toast.makeText(getContext(), R.string.notify_disabled_gps, Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    public int getBindingVariable() {
        return BR.vm;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_shipping;
    }

    @Override
    protected void performDataBinding() {
        binding.setLifecycleOwner(this);
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getActivity().getIntent();
        isLogin = intent.getBooleanExtra("isLogin", false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mvvmApplication = (MVVMApplication)  getContext().getApplicationContext();

        shippingAdapter = new ShippingAdapter(getContext());

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        getCurrentLocation();

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20000, 0, this);

        viewModel.bookingList.observe(this,currentBookings1 -> {
            shippingAdapter.setBookingList(currentBookings1);
            shippingAdapter.addMapIdPos();
            getBooking();
        });

        viewModel.bookingUpdate.observe(this,currentBooking -> {
            if(viewModel.positionUpdate.get()!= null){
                shippingAdapter.updateItem(currentBooking.getId() ,currentBooking);
                binding.rcShipping.smoothScrollToPosition(viewModel.positionUpdate.get());
                viewModel.positionUpdate.set(null);
            }
        });

        viewModel.newBooking.observe(this,newBooking -> {
            shippingAdapter.addItem(newBooking);
            binding.rcShipping.smoothScrollToPosition(0);
        });


        binding.switchState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (viewModel.state.get() == 1 && b) {
                    return;
                }
                if (b) {
                    viewModel.state.set(1);
                    binding.switchState.setThumbDrawable(ContextCompat.getDrawable(getContext(), R.drawable.thumb_on));
                } else {
                    viewModel.state.set(0);
                    binding.switchState.setThumbDrawable(ContextCompat.getDrawable(getContext(), R.drawable.thumb_off));
                }
                viewModel.changeDriverState(new DriverStateRequest(viewModel.state.get()));
            }
        });
    }

    private void getBooking(){
        binding.rcShipping.setAdapter(shippingAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.rcShipping.setLayoutManager(layoutManager);

        shippingAdapter.setOnItemClickListener(new ShippingAdapter.OnItemClickListener() {
            @Override
            public void itemClick(int position, Long bookingId) {
                viewModel.positionUpdate.set(shippingAdapter.getMapIdPos().get(bookingId));
                mvvmApplication.setDetailsBookingId(bookingId);
                Intent intent = new Intent(getActivity(), ShippingActivity.class);
                intent.putExtra("bookingId",shippingAdapter.getBookingList().get(position).getId() );
                startActivity(intent);
            }

            @Override
            public void accept_booking(int position, Long bookingId) {
                viewModel.positionUpdate.set(shippingAdapter.getMapIdPos().get(bookingId));
                viewModel.acceptBooking(bookingId);
//                binding.rcShipping.smoothScrollToPosition(position);
            }

            @Override
            public void pickup_booking(int position, Long bookingId) {
                viewModel.positionUpdate.set(shippingAdapter.getMapIdPos().get(bookingId));
                imageBookingDialog(bookingId);
//                binding.rcShipping.smoothScrollToPosition(position);
            }

            @Override
            public void reject_booking(int position, Long bookingId) {
                viewModel.positionUpdate.set(shippingAdapter.getMapIdPos().get(bookingId));
                cancelDialog(bookingId);
            }

            @Override
            public void done_booking(int position, Long bookingId) {
                viewModel.positionUpdate.set(shippingAdapter.getMapIdPos().get(bookingId));
                imageBookingDialog(bookingId);
//                binding.rcShipping.smoothScrollToPosition(position);
//                viewModel.updateStateBooking(Constants.BOOKING_STATE_DONE, currentBookings.get(position).getId());
            }

            @Override
            public void navigate_chat(int position, Long bookingId) {
                int pos = shippingAdapter.getMapIdPos().get(bookingId);
                viewModel.positionUpdate.set(shippingAdapter.getMapIdPos().get(bookingId));
                viewModel.openChat(shippingAdapter.getBookingList().get(pos).getCode(), shippingAdapter.getBookingList().get(pos).getRoom().getId(),shippingAdapter.getBookingList().get(pos).getId());
            }

            @Override
            public void navigate_call(int position, Long bookingId) {
                viewModel.positionUpdate.set(shippingAdapter.getMapIdPos().get(bookingId));
            }

            @Override
            public void delete_booking(int position, Long bookingId) {
                deleteBooking(bookingId);
            }

            @Override
            public void countdown_end(int position, Long bookingId) {
                viewModel.rejectBooking(bookingId);
                deleteBooking(bookingId);
            }
        });
    }

    private void takeFromCamera() {
        ImagePicker.with(this)
                .cameraOnly()
                .cropSquare()
                .start();
    }

    private void takeFromGallery() {
        ImagePicker.with(this)
                .galleryOnly()
                .cropSquare()
                .start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImagePicker.REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK && data != null) {
                Uri resultUri = data.getData();
                if(resultUri == null) return;
                final InputStream imageStream;
                try {
                    imageStream = getActivity().getContentResolver().openInputStream(resultUri);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
                photo = BitmapFactory.decodeStream(imageStream);
                dialogBinding.imgCamera.setImageBitmap(photo);
                updatedAvatar = photo;
                dialogBinding.btnConfirm.setEnabled(true);
            }
        }

    }

    private void requestStoragePermission() {
        requestPermissions(storagePermission, STORAGE_REQUEST);
    }

    private void requestCameraPermission() {
        requestPermissions(cameraPermission, CAMERA_REQUEST);
    }

    // checking storage permissions
    private Boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    // checking camera permissions
    private Boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
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

    public void imageBookingDialog(Long bookingId) {
        CurrentBooking booking = shippingAdapter.getBookingList().get(shippingAdapter.getMapIdPos().get(bookingId));
        Dialog dialog = new Dialog(getActivity());
        dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.dialog_shipping_img, null, false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(dialogBinding.getRoot());
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        dialog.setCanceledOnTouchOutside(true);
        if(updatedAvatar != null){
            dialogBinding.btnConfirm.setEnabled(true);
        }{
            dialogBinding.btnConfirm.setEnabled(false);
        }

        if(booking.getState() == Constants.BOOKING_STATE_DRIVER_ACCEPT){
            dialogBinding.title.setText("Xác nhận lấy hàng");
        }else {
            dialogBinding.title.setText("Xác nhận trả hàng");
        }

        dialogBinding.btnConfirm.setOnClickListener(view -> {
            switch (booking.getState()) {
                case Constants.BOOKING_STATE_PICKUP_SUCCESS:
                    updateBooking(Constants.BOOKING_STATE_DONE, bookingId);
                    break;
                case Constants.BOOKING_STATE_DRIVER_ACCEPT:
                    updateBooking(Constants.BOOKING_STATE_PICKUP_SUCCESS, bookingId);
                    break;
                default:
                    break;
            }
            dialog.dismiss();
        });
        dialogBinding.imgCamera.setOnClickListener(a -> {
//            getNewAvatar();
        });
        dialogBinding.takePhoto.setOnClickListener(a -> {
//            getNewAvatar();
            if (!checkCameraPermission()) {
                requestCameraPermission();
            } else {
                takeFromCamera();
            }
        });
        dialogBinding.fromLib.setOnClickListener(a -> {
            Log.d("TAG", "imageBookingDialog: ");
//            getNewAvatar();
            if (!checkStoragePermission()) {
                requestStoragePermission();
            } else {
                takeFromGallery();
            }
        });


        dialogBinding.btnCancel.setOnClickListener(b -> {
            dialog.dismiss();
        });

        dialog.show();
    }


    public void cancelDialog(Long bookingId) {
        int pos = shippingAdapter.getMapIdPos().get(bookingId);
        Dialog dialog = new Dialog(getActivity());
        DialogCancelBinding dialogCancelBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.dialog_cancel, null, false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(dialogCancelBinding.getRoot());
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        dialog.setCanceledOnTouchOutside(true);

        dialogCancelBinding.btnConfirm.setOnClickListener(view -> {
            switch (shippingAdapter.getBookingList().get(pos).getState()) {
                case Constants.BOOKING_STATE_BOOKING:
                    viewModel.rejectBooking(shippingAdapter.getBookingList().get(pos).getId());
                    deleteBooking(bookingId);
                    break;
                case Constants.BOOKING_STATE_DRIVER_ACCEPT:
                    Log.d("TAG", "cancelDialog: ");
                    viewModel.cancelBooking(shippingAdapter.getBookingList().get(pos).getId());
                    updateLocationUpdatesInterval(20000);
                    deleteBooking(bookingId);
                    break;
                default:
                    break;
            }
            dialog.dismiss();
        });

        dialogCancelBinding.btnCancel.setOnClickListener(view -> {
            dialog.dismiss();
        });
        dialog.show();
    }
    public void updateBooking(int state, long id) {

        viewModel.showLoading();
        // Upload image if necessary
        if (updatedAvatar != null) {
            // Upload avatar then update profile

            // Convert the Bitmap to a byte array
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            updatedAvatar.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] imageByteArray = byteArrayOutputStream.toByteArray();

            // Create a request body for the image
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageByteArray);

            // Create a multipart request builder
            MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM);
            requestBodyBuilder.addFormDataPart("file", "image.jpg", requestFile);
            requestBodyBuilder.addFormDataPart("type", Constants.FILE_TYPE_AVATAR);

            viewModel.compositeDisposable.add(viewModel.uploadAvatar(requestBodyBuilder.build())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(uploadResponse -> {
                        if (uploadResponse.isResult() && uploadResponse.getData().getFilePath() != null) {
                            viewModel.image.set(uploadResponse.getData().getFilePath());
                            viewModel.updateStateBooking(state, id);
                            viewModel.hideLoading();
                        }
                        viewModel.hideLoading();
                    })
            );

        }else {
            viewModel.showErrorMessage("Vui lòng cập nhật hình ảnh");
            viewModel.hideLoading();
        }
    }

    private void updateLocationUpdatesInterval(long timeUpdate) {
        if (locationManager != null) {
            locationManager.removeUpdates(this);
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, timeUpdate, 0, this);
            Log.d("TAG", "updateLocationUpdatesInterval: "+ timeUpdate);
        }
    }
    private void getCurrentLocation() {
        FusedLocationProviderClient mFusedLocationClient =
                LocationServices.getFusedLocationProviderClient(getContext());
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location == null) {
                            return;
                        }
                        viewModel.latitude.set(String.valueOf(location.getLatitude()));
                        viewModel.longitude.set(String.valueOf(location.getLongitude()));

                        Log.d("TAG", "onSuccess: "+isLogin);
                        if(isLogin){
                            viewModel.state.set(1);
                            viewModel.changeDriverState(new DriverStateRequest(viewModel.state.get()));
                        }else {
                            viewModel.getStateDriver();
                        }
                    }
                });
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        if (viewModel.state.get() == 1) {
            viewModel.latitude.set(String.valueOf(location.getLatitude()));
            viewModel.longitude.set(String.valueOf(location.getLongitude()));
            viewModel.updatePosition();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
//        LocationListener.super.onStatusChanged(provider, status, extras);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
//        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
//        LocationListener.super.onProviderDisabled(provider);
        displayLocationSettingsRequest();
    }

    private void displayLocationSettingsRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        SettingsClient settingsClient = LocationServices.getSettingsClient(getContext());
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());
        task.addOnSuccessListener(locationSettingsResponse -> {
            // Vị trí đã được bật, thực hiện các công việc cần thiết
        });

        task.addOnFailureListener(e -> {
            if (e instanceof ResolvableApiException) {
                ResolvableApiException resolvable = (ResolvableApiException) e;
                // Sử dụng ActivityResultLauncher để bắt đầu IntentSender
                IntentSenderRequest request = new IntentSenderRequest.Builder(resolvable.getResolution()).build();
                locationSettingsLauncher.launch(request);
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        if (mvvmApplication.getCurrentBookingId() != null) {
            viewModel.loadNewBooking(Long.parseLong(mvvmApplication.getCurrentBookingId()));
            mvvmApplication.setCurrentBookingId(null);
            binding.switchState.setClickable(false);
        }
        if (mvvmApplication.getCancelBookingId() != null) {
            Log.d("TAG", "onResume: "+shippingAdapter.getMapIdPos().get(Long.parseLong(mvvmApplication.getCancelBookingId())));
            viewModel.positionUpdate.set(shippingAdapter.getMapIdPos().get(Long.parseLong(mvvmApplication.getCancelBookingId())));
            viewModel.loadCancelBooking(Long.parseLong(mvvmApplication.getCancelBookingId()));
            mvvmApplication.setCancelBookingId(null);
            binding.switchState.setClickable(true);
        }
        if(mvvmApplication.getDetailsBookingId()!= null){
            viewModel.loadBooking(mvvmApplication.getDetailsBookingId());
            mvvmApplication.setDetailsBookingId(null);
        }
        if(mvvmApplication.getDeleteBookingId() != null){
            if(viewModel.positionUpdate.get()!= null){
                deleteBooking(mvvmApplication.getDeleteBookingId());
            }
            mvvmApplication.setDeleteBookingId(null);
        }
    }

    void deleteBooking(Long bookingId){
        mvvmApplication.getWebSocketLiveData().getCodeBooking().remove(bookingId);
        mvvmApplication.getWebSocketLiveData().sendPing();
        shippingAdapter.removeItem(bookingId);
        if(shippingAdapter.getMapIdPos().get(bookingId)!=0){
            binding.rcShipping.smoothScrollToPosition(shippingAdapter.getMapIdPos().get(bookingId)-1);
        }
        viewModel.positionUpdate.set(null);
    }


}
