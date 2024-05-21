package ww.smartexpress.driver.ui.shipping;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonArray;
import com.google.maps.android.PolyUtil;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import ww.smartexpress.driver.BR;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.constant.Constants;
import ww.smartexpress.driver.data.model.api.request.DriverStateRequest;
import ww.smartexpress.driver.databinding.ActivityShippingBinding;
import ww.smartexpress.driver.databinding.DialogCancelBinding;
import ww.smartexpress.driver.databinding.DialogOrderDetailsBinding;
import ww.smartexpress.driver.databinding.DialogShippingImgBinding;
import ww.smartexpress.driver.di.component.ActivityComponent;
import ww.smartexpress.driver.ui.base.activity.BaseActivity;

public class ShippingActivity extends BaseActivity<ActivityShippingBinding, ShippingViewModel> implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private LocationRequest locationRequest;
    private Marker currentLocationMarker;
    private LocationManager locationManager;
    private LatLng currentLocation;
    private LatLng customerLocation;
    private LatLng destinationLocation;
    private Marker destinationMarker;
    private Polyline polyline;
    private Handler handler = new Handler();
    private Runnable runnable;
    final int durationInSeconds = 30;
    final int updateInterval = 1000;

    //
    private Bitmap updatedAvatar;
    private static final int CAMERA_REQUEST = 100;
    private static final int STORAGE_REQUEST = 200;
    String cameraPermission[];
    String storagePermission[];
    Bitmap photo;
    DialogShippingImgBinding dialogBinding;
    private final ActivityResultLauncher<IntentSenderRequest> locationSettingsLauncher =
            registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                } else {
                    Toast.makeText(this, R.string.notify_disabled_gps, Toast.LENGTH_SHORT).show();
                }
            });
    @Override
    public int getLayoutId() {
        return R.layout.activity_shipping;
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
        if(intent.getLongExtra("bookingId",0) != 0){
            viewModel.currentBookingId.postValue(intent.getLongExtra("bookingId",0));
        }

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        viewModel.currentBookingId.observe(this, id ->{
            viewModel.loadBooking(id);
        });

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if (location == null) {
            return;
        }
        currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        if (currentLocationMarker == null) {
            currentLocationMarker = mMap.addMarker(new MarkerOptions().position(currentLocation).title("Driver current location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16));
        } else {
            currentLocationMarker.setPosition(currentLocation);
        }
        if (viewModel.status.get() == Constants.BOOKING_ACCEPTED && !viewModel.isShowDirection.get()) {
            BitmapDescriptor desIc = BitmapDescriptorFactory.fromResource(R.drawable.location_flag);
            if (destinationMarker != null) {
                destinationMarker.setVisible(true);
            }
            if (destinationMarker == null) {
                destinationMarker = mMap.addMarker(new MarkerOptions().position(customerLocation).title(viewModel.booking.get().getPickupAddress()).icon(desIc));
            } else {
                destinationMarker.setPosition(customerLocation);
            }
            loadMapDirection(currentLocation, customerLocation);
            viewModel.isShowDirection.set(true);
        }
        if (viewModel.status.get() == Constants.BOOKING_PICKUP && !viewModel.isShowDirection.get()) {
            if (polyline != null) {
                polyline.remove();
            }
            if (destinationMarker != null) {
                destinationMarker.setVisible(true);
            }
            BitmapDescriptor desIc = BitmapDescriptorFactory.fromResource(R.drawable.location_flag);
            if (destinationMarker == null) {
                destinationMarker = mMap.addMarker(new MarkerOptions().position(destinationLocation).title(viewModel.booking.get().getDestinationAddress()).icon(desIc));
            } else {
                destinationMarker.setPosition(destinationLocation);
            }
            loadMapDirection(currentLocation, destinationLocation);
            viewModel.isShowDirection.set(true);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        getCurrentLocation();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, this);

        viewModel.bookingValue.observe(this, currentBooking -> {
            customerLocation = new LatLng(currentBooking.getPickupLat(), currentBooking.getPickupLong());
            destinationLocation = new LatLng(currentBooking.getDestinationLat(), currentBooking.getDestinationLong());
        });

        viewBinding.cardBooking.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BitmapDescriptor desIc = BitmapDescriptorFactory.fromResource(R.drawable.location_flag);
                switch (viewModel.status.get()) {
                    case Constants.BOOKING_VISIBLE:
                        viewModel.acceptBooking();
//                        BitmapDescriptor desIc = BitmapDescriptorFactory.fromResource(R.drawable.location_flag);
                        viewModel.isShowDirection.set(false);
                        handler.removeCallbacks(runnable);
                        viewBinding.cardBooking.progressText.setText(String.valueOf(durationInSeconds));
                        viewBinding.cardBooking.progressBar.setProgress(0);
                        if (destinationMarker != null) {
                            destinationMarker.setVisible(true);
                        }
                        if (destinationMarker == null) {
                            destinationMarker = mMap.addMarker(new MarkerOptions().position(customerLocation).title(viewModel.booking.get().getPickupAddress()).icon(desIc));
                        } else {
                            destinationMarker.setPosition(customerLocation);
                        }
                        if (currentLocation != null) {
                            loadMapDirection(currentLocation, customerLocation);
                            viewModel.isShowDirection.set(true);
                        }
                        updateLocationUpdatesInterval(10000);
                        break;
                    case Constants.BOOKING_ACCEPTED:
                        imageBookingDialog();
                        break;
                    default:
                        break;
                }


            }
        });

        viewBinding.cardBooking.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });

        viewBinding.cardBooking.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("alo", "onClick: " + viewModel.status.get());
                switch (viewModel.status.get()) {
                    case Constants.BOOKING_PICKUP:
                        imageBookingDialog();
                        break;
                    case Constants.BOOKING_SUCCESS:
                        viewModel.getApplication().setCurrentBookingId(null);
                        viewModel.isShowDirection.set(false);
                        viewModel.status.set(Constants.BOOKING_NONE);
                        break;
                    case Constants.BOOKING_CANCELED:
                    case Constants.BOOKING_CUSTOMER_CANCEL:
                        viewModel.status.set(Constants.BOOKING_NONE);
                        if (polyline != null) {
                            polyline.remove();
                        }
                        if (destinationMarker != null) {
                            destinationMarker.setVisible(false);
                        }
                        updateLocationUpdatesInterval(60000);
                        break;
                    default:
                        if (polyline != null) {
                            polyline.remove();
                        }
                        if (destinationMarker != null) {
                            destinationMarker.setVisible(false);
                        }
                        updateLocationUpdatesInterval(60000);
                        break;
                }
            }
        });
    }


    private void updateLocationUpdatesInterval(long timeUpdate) {
        if (locationManager != null) {
            locationManager.removeUpdates(this);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, timeUpdate, 0, this);
            Log.d("TAG", "updateLocationUpdatesInterval: "+ timeUpdate);
        }
    }

    // progress bar countdown
    private void startCountdown(){
        handler.postDelayed(runnable = new Runnable() {
            private int remainingTime = durationInSeconds;
            @Override
            public void run() {
                if (remainingTime >= 0) {
                    viewBinding.cardBooking.progressText.setText(String.valueOf(remainingTime));
                    viewBinding.cardBooking.progressBar.setProgress((durationInSeconds - remainingTime) * 100 / durationInSeconds);

                    remainingTime--;
                    handler.postDelayed(this, updateInterval);
                } else {
                    // Nếu hết thời gian, thực hiện các công việc sau khi đếm ngược kết thúc
                    viewModel.rejectBooking();
                    viewBinding.cardBooking.progressText.setText(String.valueOf(durationInSeconds));
                    viewBinding.cardBooking.progressBar.setProgress(0);
                }
            }
        }, updateInterval);
    }

    public void cancel() {
        cancelDialog();
    }


    public void cancelDialog() {
        Dialog dialog = new Dialog(this);
        DialogCancelBinding dialogCancelBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_cancel, null, false);
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
            switch (viewModel.status.get()) {
                case Constants.BOOKING_VISIBLE:
                    viewModel.rejectBooking();
                    handler.removeCallbacks(runnable);
                    viewBinding.cardBooking.progressText.setText(String.valueOf(durationInSeconds));
                    viewBinding.cardBooking.progressBar.setProgress(0);
                    dialog.dismiss();
                    break;
                case Constants.BOOKING_ACCEPTED:
                    viewModel.cancelBooking();
                    updateLocationUpdatesInterval(60000);
                    break;
                default:
                    break;
            }
            viewModel.status.set(Constants.BOOKING_CANCELED);
            dialog.dismiss();
        });

        dialogCancelBinding.btnCancel.setOnClickListener(view -> {
            dialog.dismiss();
        });
        dialog.show();
    }

    public void orderDetailsDialog(){
        Dialog dialog = new Dialog(this);
        DialogOrderDetailsBinding dialogOrderDetailsBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_order_details, null, false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(dialogOrderDetailsBinding.getRoot());
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        dialog.setCanceledOnTouchOutside(true);
        dialogOrderDetailsBinding.imgClose.setOnClickListener(v->{
            dialog.dismiss();
        });
        dialog.show();
    }


    private void displayLocationSettingsRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
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

    // gg maps polyline
    public void loadMapDirection(LatLng origin, LatLng des) {
        viewModel.compositeDisposable.add(viewModel.getMapDirection(origin, des)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    String status = response.get("status").getAsString();

                    if (status.equals("OK")) {

                        JsonArray routes = response.getAsJsonArray("routes");
                        ArrayList<LatLng> points;
                        PolylineOptions polylineOptions = null;

                        for (int i = 0; i < routes.size(); i++) {
                            points = new ArrayList<>();
                            polylineOptions = new PolylineOptions();
                            JsonArray legs = routes.get(i).getAsJsonObject().getAsJsonArray("legs");

                            for (int j = 0; j < legs.size(); j++) {
                                JsonArray steps = legs.get(j).getAsJsonObject().getAsJsonArray("steps");

                                for (int k = 0; k < steps.size(); k++) {
                                    String polyline = steps.get(k).getAsJsonObject().get("polyline").getAsJsonObject().get("points").getAsString();
                                    List<LatLng> latLngList = PolyUtil.decode(polyline);

                                    for (int l = 0; l < latLngList.size(); l++) {
                                        LatLng position = new LatLng(latLngList.get(l).latitude, latLngList.get(l).longitude);
                                        points.add(position);
                                    }
                                }
                            }

                            polylineOptions.addAll(points);
                            polylineOptions.width(10);
                            polylineOptions.color(ContextCompat.getColor(this, R.color.item_background));
                            polylineOptions.geodesic(true);
                        }

                        polyline = mMap.addPolyline(polylineOptions);
                        Log.d("TAG", "loadMapDirection: ");

                    }

                }, err -> {
                    viewModel.showErrorMessage(getString(R.string.newtwork_error));
                    err.printStackTrace();
                }));
    }

    private void getCurrentLocation() {
        FusedLocationProviderClient mFusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location == null) {
                            return;
                        }
                        currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        if (currentLocationMarker == null) {
                            currentLocationMarker = mMap.addMarker(new MarkerOptions().position(currentLocation).title("Driver current location"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16));
                        } else {
                            currentLocationMarker.setPosition(currentLocation);
                        }
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

    public Uri getUriFromBitmap(Context context, Bitmap bitmap) {
        Uri uri = null;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        uri = Uri.parse(path);
        return uri;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImagePicker.REQUEST_CODE) {
            if (resultCode == this.RESULT_OK && data != null) {
                Uri resultUri = data.getData();
                if(resultUri == null) return;
                final InputStream imageStream;
                try {
                    imageStream = this.getContentResolver().openInputStream(resultUri);
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

    public void imageBookingDialog() {
        Dialog dialog = new Dialog(this);
        dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_shipping_img, null, false);
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

        if(viewModel.status.get() == Constants.BOOKING_ACCEPTED){
            dialogBinding.title.setText("Xác nhận lấy hàng");
        }else {
            dialogBinding.title.setText("Xác nhận trả hàng");
        }

        dialogBinding.btnConfirm.setOnClickListener(view -> {
            BitmapDescriptor desIc = BitmapDescriptorFactory.fromResource(R.drawable.location_flag);
            switch (viewModel.status.get()) {
                case Constants.BOOKING_PICKUP:
                    updateBooking(Constants.BOOKING_STATE_DONE);
                    viewModel.getApplication().setCurrentBookingId(null);
                    if (polyline != null) {
                        polyline.remove();
                    }
                    if (destinationMarker != null) {
                        destinationMarker.setVisible(false);
//                            destinationMarker.remove();
                    }
                    viewModel.isShowDirection.set(false);
                    updateLocationUpdatesInterval(60000);
                    break;
                case Constants.BOOKING_ACCEPTED:
                    updateBooking(Constants.BOOKING_STATE_PICKUP_SUCCESS);
                    if (polyline != null) {
                        polyline.remove();
                    }
                    if (destinationMarker != null) {
                        destinationMarker.setVisible(true);
                    }
                    viewModel.isShowDirection.set(false);
                    if (destinationMarker == null) {
                        destinationMarker = mMap.addMarker(new MarkerOptions().position(destinationLocation).title(viewModel.booking.get().getDestinationAddress()).icon(desIc));
                    } else {
                        destinationMarker.setPosition(destinationLocation);
                    }
                    if (currentLocation != null) {
                        loadMapDirection(currentLocation, destinationLocation);
                        viewModel.isShowDirection.set(true);
                    }
                    break;
                default:
                    break;
            }
            dialog.dismiss();
        });
        dialogBinding.imgCamera.setOnClickListener(a -> {
        });
        dialogBinding.takePhoto.setOnClickListener(a -> {

            if (!checkCameraPermission()) {
                requestCameraPermission();
            } else {
                takeFromCamera();
            }
        });
        dialogBinding.fromLib.setOnClickListener(a -> {
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
    public void updateBooking(int state) {

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
                            viewModel.updateStateBooking(state);
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

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        displayLocationSettingsRequest();
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
}
