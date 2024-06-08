package ww.smartexpress.driver.ui.fragment.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.RequestBody;
import ww.smartexpress.driver.MVVMApplication;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.constant.Constants;
import ww.smartexpress.driver.data.Repository;
import ww.smartexpress.driver.data.model.api.ApiModelUtils;
import ww.smartexpress.driver.data.model.api.ResponseWrapper;
import ww.smartexpress.driver.data.model.api.request.CancelBookingRequest;
import ww.smartexpress.driver.data.model.api.request.DriverStateRequest;
import ww.smartexpress.driver.data.model.api.request.EventBookingRequest;
import ww.smartexpress.driver.data.model.api.request.PositionRequest;
import ww.smartexpress.driver.data.model.api.request.UpdateBookingRequest;
import ww.smartexpress.driver.data.model.api.response.CurrentBooking;
import ww.smartexpress.driver.data.model.api.response.ProfileResponse;
import ww.smartexpress.driver.data.model.api.response.Size;
import ww.smartexpress.driver.data.model.api.response.UploadFileResponse;
import ww.smartexpress.driver.data.model.room.UserEntity;
import ww.smartexpress.driver.ui.base.fragment.BaseFragmentViewModel;
import ww.smartexpress.driver.ui.chat.ChatActivity;
import ww.smartexpress.driver.utils.DateUtils;

public class HomeFragmentViewModel extends BaseFragmentViewModel {
    public ObservableField<Integer> status = new ObservableField<>(Constants.BOOKING_NONE);
    public ObservableField<Integer> state = new ObservableField<>(0);
    public ObservableField<String> paymentMethod = new ObservableField<>("Tiền mặt");
    public ObservableField<ProfileResponse> profile = new ObservableField<>();
    public ObservableField<String> latitude = new ObservableField<>();
    public ObservableField<String> longitude = new ObservableField<>();
    public ObservableField<Integer> isBusy = new ObservableField<>(0);
    public ObservableField<UserEntity> user = new ObservableField<>();
    public ObservableField<Long> userId = new ObservableField<>();
    public ObservableField<Boolean> paused = new ObservableField<>(false);
    public MutableLiveData<String> currentBookingId = new MutableLiveData<>();
    public MutableLiveData<CurrentBooking> booking = new MutableLiveData<>();
    public ObservableField<Boolean> isShowDirection = new ObservableField<>(false);

    public ObservableField<String> size = new ObservableField<>("");

    public ObservableField<String> image = new ObservableField<>();

    public MutableLiveData<List<CurrentBooking>> bookingList = new MutableLiveData<>(new ArrayList<>());

    public HomeFragmentViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
        loadProfile();
//        getStateDriver();
        getCurrentBooking();
    }

    public Repository getRepository(){
        return repository;
    }
    public MVVMApplication getApplication(){
        return application;
    }

    public void openChat(){
//        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
//        sendIntent.setData(Uri.parse("sms:" + customerPhone.get()));
//        sendIntent.putExtra("sms_body", "");
//        application.getCurrentActivity().startActivity(sendIntent);
        Intent intent = new Intent(application.getCurrentActivity(), ChatActivity.class);
        intent.putExtra("codeBooking", booking.getValue().getCode());
        Log.d("TAG", "openChat: " + booking.getValue().getCode());
        application.getCurrentActivity().startActivity(intent);
    }

    public void callCustomer(){
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + booking.getValue().getCustomer().getPhone()));
        application.getCurrentActivity().startActivity(callIntent);
    }

    @SuppressLint("CheckResult")
    public void loadProfile(){
        compositeDisposable.add(repository.getApiService().getProfile()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if(response.isResult()){
                        repository.getSharedPreferences().setUserId(response.getData().getId().toString());
                        user.set(new UserEntity());
                        user.get().setUserId(response.getData().getId());
                        user.get().setAvatar(response.getData().getAvatar());
                        user.get().setFullName(response.getData().getFullName());
                        user.get().setPhone(response.getData().getPhone());
                        user.get().setAddress(response.getData().getAddress());
                        user.get().setAverageRating(response.getData().getAverageRating());

                        repository.getRoomService().userDao().insert(user.get())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(() -> {

                                }, throwable -> {

                                });
                    }else {
                        showErrorMessage(response.getMessage());
                    }
                },error->{
                    showErrorMessage(application.getString(R.string.newtwork_error));
                    error.printStackTrace();
                })
        );
    }

    public void updatePosition(){
        PositionRequest positionRequest = new PositionRequest();
        positionRequest.setIsBusy(isBusy.get());
        positionRequest.setLatitude(latitude.get());
        positionRequest.setLongitude(longitude.get());
        positionRequest.setStatus(1);
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            ZonedDateTime utcNow = ZonedDateTime.now().withZoneSameInstant(ZoneOffset.UTC);
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
//            positionRequest.setTimeUpdate(utcNow.format(formatter));
//        }
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        positionRequest.setTimeUpdate(DateUtils.convertToUTC(format.format(date)));

        compositeDisposable.add(repository.getApiService().updatePosition(positionRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if(response.isResult()){
//                        showSuccessMessage(response.getMessage());
                    }else {
                        showErrorMessage(response.getMessage());
                    }
                },error->{
//                    showErrorMessage(application.getString(R.string.newtwork_error));
                    error.printStackTrace();
                })
        );
    }

    public void changeDriverState(DriverStateRequest request ){
        compositeDisposable.add(repository.getApiService().changeStateDriver(request)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                            if(response.isResult()){
                                if(state.get()==1){
                                    updatePosition();
                                }
                                showSuccessMessage(response.getMessage());
                            }else {
                                showErrorMessage(response.getMessage());
                            }
                        },error->{
                            showErrorMessage(application.getString(R.string.newtwork_error));
                            error.printStackTrace();
                        })
        );
    }

    public void getStateDriver(){
        compositeDisposable.add(repository.getApiService().getDriverState()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if(response.isResult()){
                        state.set(response.getData().getDriverState());
                        if(state.get()==1){
                            updatePosition();
                        }
                    }else {
                        showErrorMessage(response.getMessage());
                    }
                },error->{
                    showErrorMessage(application.getString(R.string.newtwork_error));
                    error.printStackTrace();
                })
        );
    }

    public void acceptBooking(){
        EventBookingRequest request = new EventBookingRequest();
        request.setBookingId(Long.valueOf(currentBookingId.getValue()));
        request.setNote(null);
        showLoading();
        compositeDisposable.add(repository.getApiService().acceptBooking(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if(response.isResult()){
                        Log.d("TAG", "acceptBooking: "+booking.getValue().getCode());
//                        application.getWebSocketLiveData().getCodeBooking().add(booking.getValue().getCode());
                        application.getWebSocketLiveData().sendPing();
                        status.set(Constants.BOOKING_ACCEPTED);
//                        getCurrentBooking();
                        repository.getSharedPreferences().setLong(Constants.ROOM_ID,response.getData().getRoom().getId());
                        showSuccessMessage(response.getMessage());
                    }else {
                        showErrorMessage(response.getMessage());
                    }
                    hideLoading();
                },error->{
                    showErrorMessage(application.getString(R.string.newtwork_error));
                    error.printStackTrace();
                    hideLoading();
                })
        );
    }
    public void getCurrentBooking(){
        showLoading();
        compositeDisposable.add(repository.getApiService().getCurrentBooking()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if(response.isResult()){
//                        application.setCurrentBookingId(String.valueOf(response.getData().getId()));
//                        currentBookingId.setValue(String.valueOf(response.getData().getId()));
//                        size.set(getSize(response.getData().getService().getSize()));
//                        if(response.getData().getState()==Constants.BOOKING_STATE_DRIVER_ACCEPT){
//                            status.set(Constants.BOOKING_ACCEPTED);
//                            repository.getSharedPreferences().setLong(Constants.ROOM_ID,response.getData().getRoom().getId());
//                        }else if(response.getData().getState() == Constants.BOOKING_STATE_PICKUP_SUCCESS){
//                            status.set(Constants.BOOKING_PICKUP);
//                            repository.getSharedPreferences().setLong(Constants.ROOM_ID,response.getData().getRoom().getId());
//                        }
//                        booking.setValue(response.getData());
                        bookingList.setValue(response.getData().getContent());
                        for (CurrentBooking booking: bookingList.getValue()
                             ) {
//                            application.getWebSocketLiveData().getCodeBooking().add(booking.getCode());
                        }
                        application.getWebSocketLiveData().sendPing();
                        showSuccessMessage(response.getMessage());
                    }else {
//                        showErrorMessage(response.getMessage());
                    }
                    hideLoading();
                },error->{
                    showErrorMessage(application.getString(R.string.newtwork_error));
                    error.printStackTrace();
                    hideLoading();
                })
        );
    }

    public void loadBooking(){
        showLoading();
        compositeDisposable.add(repository.getApiService().loadBooking(Long.valueOf(application.getCurrentBookingId()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if(response.isResult()){
                        booking.setValue(response.getData());
                        size.set(getSize(response.getData().getService().getSize()));
                        currentBookingId.setValue(String.valueOf(booking.getValue().getId()));
                        status.set(Constants.BOOKING_VISIBLE);
                        showSuccessMessage(response.getMessage());
                    }else {
                        showErrorMessage(response.getMessage());
                    }
                    hideLoading();
                },error->{
                    showErrorMessage(application.getString(R.string.newtwork_error));
                    error.printStackTrace();
                    hideLoading();
                })
        );
    }

    Observable<JsonObject> getMapDirection(LatLng origin, LatLng destination) {
        return repository.getApiService().getMapDirection(destination.latitude + "," + destination.longitude, "driving", origin.latitude + "," + origin.longitude, Constants.GEO_API_KEY)
                .doOnNext(response -> {

                });
    }

    public void updateStateBooking(int state){
        UpdateBookingRequest request = new UpdateBookingRequest();
        request.setId(Long.valueOf(currentBookingId.getValue()));
        request.setNote(null);
        request.setState(state);
        if(state==Constants.BOOKING_STATE_PICKUP_SUCCESS){
            request.setPickupImage(image.get());
        }else if(state == Constants.BOOKING_STATE_DONE){
            request.setDeliveryImage(image.get());
        }
        showLoading();
        compositeDisposable.add(repository.getApiService().updateStateBooking(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if(response.isResult()){
                        if(state==Constants.BOOKING_STATE_PICKUP_SUCCESS){
                            status.set(Constants.BOOKING_PICKUP);
                        }else if(state == Constants.BOOKING_STATE_DONE){
                            status.set(Constants.BOOKING_SUCCESS);
                        }
                        showSuccessMessage(response.getMessage());
                    }else {
                        showErrorMessage(response.getMessage());
                    }
                    hideLoading();
                },error->{
                    showErrorMessage(application.getString(R.string.newtwork_error));
                    error.printStackTrace();
                    hideLoading();
                })
        );
    }

    public void cancelBooking(){
        CancelBookingRequest request = new CancelBookingRequest();
        request.setId(Long.valueOf(booking.getValue().getId()));
        request.setNote(null);
        showLoading();
        compositeDisposable.add(repository.getApiService().cancelBooking(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if(response.isResult()){
                        status.set(Constants.BOOKING_CANCELED);
                        showSuccessMessage(response.getMessage());
                    }else {
                        showErrorMessage(response.getMessage());
                    }
                    hideLoading();
                },error->{
                    showErrorMessage(application.getString(R.string.newtwork_error));
                    error.printStackTrace();
                    hideLoading();
                })
        );
    }

    public void rejectBooking(){
        CancelBookingRequest request = new CancelBookingRequest();
        request.setId(Long.valueOf(currentBookingId.getValue()));
        request.setNote(null);
        showLoading();
        compositeDisposable.add(repository.getApiService().rejectBooking(request)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                            if(response.isResult()){
                                status.set(Constants.BOOKING_NONE);
                                showSuccessMessage(response.getMessage());
                            }else {
                                showErrorMessage(response.getMessage());
                            }
                            hideLoading();
                        },error->{
                            showErrorMessage(application.getString(R.string.newtwork_error));
                            error.printStackTrace();
                            hideLoading();
                        })
        );
    }

    public String getSize(String sizeJson){
        Size size = ApiModelUtils.fromJson( sizeJson,Size.class);
        return size.formatSize();
    }

    public Observable<ResponseWrapper<UploadFileResponse>> uploadAvatar(RequestBody requestBody){
        return repository.getApiService().uploadFile(requestBody);
    }


}
