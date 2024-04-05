package ww.smartexpress.driver.ui.fragment.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonArray;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ww.smartexpress.driver.BR;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.constant.Constants;
import ww.smartexpress.driver.data.model.api.request.DriverStateRequest;
import ww.smartexpress.driver.databinding.DialogCancelBinding;
import ww.smartexpress.driver.databinding.DialogOrderDetailsBinding;
import ww.smartexpress.driver.databinding.FragmentHomeBinding;
import ww.smartexpress.driver.di.component.FragmentComponent;
import ww.smartexpress.driver.ui.base.fragment.BaseFragment;

public class HomeFragment extends BaseFragment<FragmentHomeBinding, HomeFragmentViewModel> implements OnMapReadyCallback, LocationListener {
    private GoogleMap mMap;
    private LocationRequest locationRequest;
    private Marker currentLocationMarker;
    private LocationManager locationManager;
    private LatLng currentLocation;
    private LatLng customerLocation;
    private LatLng destinationLocation;
    private Marker destinationMarker;
    private Polyline polyline;

    private Boolean isLogin;
    private Handler handler = new Handler();
    private Runnable runnable;
    final int durationInSeconds = 30;
    final int updateInterval = 1000;
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
        return R.layout.fragment_home;
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getActivity().getIntent();
        isLogin = intent.getBooleanExtra("isLogin", false);
//        if(isLogin && viewModel.state.get()==0){
//            viewModel.state.set(1);
//            viewModel.changeDriverState(new DriverStateRequest(viewModel.state.get()));
//        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        binding.setLifecycleOwner(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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

//        loadBooking();
        if (viewModel.getApplication().getCurrentBookingId() != null) {
            viewModel.loadBooking();
            viewModel.status.set(Constants.BOOKING_VISIBLE);
            if (viewModel.status.get() == Constants.BOOKING_ACCEPTED && !viewModel.isShowDirection.get()) {
                loadMapDirection(currentLocation, customerLocation);
                viewModel.isShowDirection.set(true);
            }
        }


    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        getCurrentLocation();

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 0, this);

        viewModel.booking.observe(this, currentBooking -> {
            customerLocation = new LatLng(currentBooking.getPickupLat(), currentBooking.getPickupLong());
            destinationLocation = new LatLng(currentBooking.getDestinationLat(), currentBooking.getDestinationLong());
        });
        viewModel.currentBookingId.observe(this, id -> {
            binding.switchState.setClickable(false);
            updateLocationUpdatesInterval(10000);
        });

        binding.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BitmapDescriptor desIc = BitmapDescriptorFactory.fromResource(R.drawable.location_flag);
                switch (viewModel.status.get()) {
                    case Constants.BOOKING_VISIBLE:
                        viewModel.acceptBooking();
//                        BitmapDescriptor desIc = BitmapDescriptorFactory.fromResource(R.drawable.location_flag);
                        viewModel.isShowDirection.set(false);
                        handler.removeCallbacks(runnable);
                        binding.progressText.setText(String.valueOf(durationInSeconds));
                        binding.progressBar.setProgress(0);
                        if (destinationMarker != null) {
                            destinationMarker.setVisible(true);
                        }
                        if (destinationMarker == null) {
                            destinationMarker = mMap.addMarker(new MarkerOptions().position(customerLocation).title(viewModel.booking.getValue().getPickupAddress()).icon(desIc));
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
                        viewModel.updateStateBooking(Constants.BOOKING_STATE_PICKUP_SUCCESS);
                        if (polyline != null) {
                            polyline.remove();
                        }
                        if (destinationMarker != null) {
                            destinationMarker.setVisible(true);
                        }
                        viewModel.isShowDirection.set(false);
//                        BitmapDescriptor desIc = BitmapDescriptorFactory.fromResource(R.drawable.location_flag);
                        if (destinationMarker == null) {
                            destinationMarker = mMap.addMarker(new MarkerOptions().position(destinationLocation).title(viewModel.booking.getValue().getDestinationAddress()).icon(desIc));
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


            }
        });

        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });

        binding.orderDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderDetailsDialog();
            }
        });

        binding.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("alo", "onClick: " + viewModel.status.get());
                switch (viewModel.status.get()) {
                    case Constants.BOOKING_PICKUP:
                        viewModel.updateStateBooking(Constants.BOOKING_STATE_DONE);
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
                    case Constants.BOOKING_SUCCESS:
                        viewModel.getApplication().setCurrentBookingId(null);
                        viewModel.isShowDirection.set(false);
                        viewModel.status.set(Constants.BOOKING_NONE);
                        binding.switchState.setClickable(true);
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
                        binding.switchState.setClickable(true);
                        updateLocationUpdatesInterval(60000);
                        break;
                    default:
                        if (polyline != null) {
                            polyline.remove();
                        }
                        if (destinationMarker != null) {
                            destinationMarker.setVisible(false);
                        }
                        binding.switchState.setClickable(true);
                        updateLocationUpdatesInterval(60000);
                        break;
                }
            }
        });

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

    private void startCountdown(){
        handler.postDelayed(runnable = new Runnable() {
            private int remainingTime = durationInSeconds;
            @Override
            public void run() {
                if (remainingTime >= 0) {
                    binding.progressText.setText(String.valueOf(remainingTime));
                    binding.progressBar.setProgress((durationInSeconds - remainingTime) * 100 / durationInSeconds);

                    remainingTime--;
                    handler.postDelayed(this, updateInterval);
                } else {
                    // Nếu hết thời gian, thực hiện các công việc sau khi đếm ngược kết thúc
                    viewModel.rejectBooking();
                    binding.progressText.setText(String.valueOf(durationInSeconds));
                    binding.progressBar.setProgress(0);
                }
            }
        }, updateInterval);
    }

    public void cancel() {
        cancelDialog();
    }


    public void cancelDialog() {
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
            switch (viewModel.status.get()) {
                case Constants.BOOKING_VISIBLE:
                    viewModel.rejectBooking();
                    handler.removeCallbacks(runnable);
                    binding.progressText.setText(String.valueOf(durationInSeconds));
                    binding.progressBar.setProgress(0);
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
        Dialog dialog = new Dialog(getActivity());
        DialogOrderDetailsBinding dialogOrderDetailsBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.dialog_order_details, null, false);
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

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("TAG", "onResume: ");
        if (viewModel.getApplication().getCurrentBookingId() != null) {
            if (viewModel.status.get() != Constants.BOOKING_VISIBLE) {
                viewModel.loadBooking();
                viewModel.status.set(Constants.BOOKING_VISIBLE);
                startCountdown();
                viewModel.getApplication().setCurrentBookingId(null);
                binding.switchState.setClickable(false);
                Log.d("TAG", "onResume1: ");
            }
        }
        if (viewModel.getApplication().getCustomerCancelBooking()) {
            viewModel.status.set(Constants.BOOKING_CUSTOMER_CANCEL);
            viewModel.getApplication().setCustomerCancelBooking(false);
            binding.switchState.setClickable(true);
        }
    }



    @Override
    public void onLocationChanged(@NonNull Location location) {
        if (location == null) {
            return;
        }
        if (viewModel.state.get() == 1) {
            viewModel.latitude.set(String.valueOf(location.getLatitude()));
            viewModel.longitude.set(String.valueOf(location.getLongitude()));
            viewModel.updatePosition();
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
                destinationMarker = mMap.addMarker(new MarkerOptions().position(customerLocation).title(viewModel.booking.getValue().getPickupAddress()).icon(desIc));
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
                destinationMarker = mMap.addMarker(new MarkerOptions().position(destinationLocation).title(viewModel.booking.getValue().getDestinationAddress()).icon(desIc));
            } else {
                destinationMarker.setPosition(destinationLocation);
            }
            loadMapDirection(currentLocation, destinationLocation);
            viewModel.isShowDirection.set(true);
        }
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
//        LocationListener.super.onProviderDisabled(provider);
        displayLocationSettingsRequest();
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
//        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
//        LocationListener.super.onStatusChanged(provider, status, extras);
    }

    private void displayLocationSettingsRequest() {
        locationRequest = LocationRequest.create();
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
                            polylineOptions.color(ContextCompat.getColor(getContext(), R.color.item_background));
                            polylineOptions.geodesic(true);
                        }

                        polyline = mMap.addPolyline(polylineOptions);
                        Log.d("TAG", "loadMapDirection: ");

                        LatLngBounds bounds = new LatLngBounds.Builder().include(origin).include(des).build();
                        Point point = new Point();
                        getActivity().getWindowManager().getDefaultDisplay().getSize(point);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, point.x, 150, 30));

                    }

                }, err -> {
                    viewModel.showErrorMessage(getString(R.string.newtwork_error));
                    err.printStackTrace();
                }));
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
                        currentLocation =
                                new LatLng(location.getLatitude(), location.getLongitude());
//                        if(viewModel.state.get()==1){
//                            viewModel.updatePosition();
//                        }
                        Log.d("TAG", "onSuccess: "+isLogin);
                        if(isLogin){
                            viewModel.state.set(1);
                            viewModel.changeDriverState(new DriverStateRequest(viewModel.state.get()));
                        }else {
                            viewModel.getStateDriver();
                        }
                        if (currentLocationMarker == null) {
                            currentLocationMarker = mMap.addMarker(new MarkerOptions().position(currentLocation).title("Driver current location"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16));
                        } else {
                            currentLocationMarker.setPosition(currentLocation);
                        }
                    }
                });
    }

}
