package ww.smartexpress.driver.ui.shipping;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;


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
import ww.smartexpress.driver.data.model.api.request.EventBookingRequest;
import ww.smartexpress.driver.data.model.api.request.UpdateBookingRequest;
import ww.smartexpress.driver.data.model.api.response.CurrentBooking;
import ww.smartexpress.driver.data.model.api.response.ProfileResponse;
import ww.smartexpress.driver.data.model.api.response.Size;
import ww.smartexpress.driver.data.model.api.response.UploadFileResponse;
import ww.smartexpress.driver.data.model.room.UserEntity;
import ww.smartexpress.driver.ui.base.activity.BaseViewModel;
import ww.smartexpress.driver.ui.chat.ChatActivity;

public class ShippingViewModel extends BaseViewModel {

    public ObservableField<Integer> status = new ObservableField<>(Constants.BOOKING_NONE);

    public ObservableField<Integer> state = new ObservableField<>(0);
    public ObservableField<String> paymentMethod = new ObservableField<>("Tiền mặt");

    public ObservableField<ProfileResponse> profile = new ObservableField<>();

    public ObservableField<UserEntity> user = new ObservableField<>();

    public ObservableField<Long> userId = new ObservableField<>();
    public MutableLiveData<Long> currentBookingId = new MutableLiveData<>();
    public ObservableField<CurrentBooking> booking = new ObservableField<>();
    public MutableLiveData<CurrentBooking> bookingValue = new MutableLiveData<>();
    public ObservableField<Boolean> isShowDirection = new ObservableField<>(false);

    public ObservableField<String> size = new ObservableField<>("");

    public ObservableField<String> image = new ObservableField<>(null);


    public ShippingViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
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
        application.getCurrentActivity().startActivity(intent);
    }

    public void callCustomer(){
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + booking.get().getCustomer().getPhone()));
        application.getCurrentActivity().startActivity(callIntent);
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
                                Log.d("TAG", "acceptBooking: "+booking.get().getCode());
                                application.getWebSocketLiveData().getCodeBooking().add(booking.get().getCode());
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
    public void loadBooking(Long id){
        showLoading();
        compositeDisposable.add(repository.getApiService().loadBooking(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if(response.isResult()){
                        booking.set(response.getData());
                        bookingValue.postValue(response.getData());
                        size.set(getSize(response.getData().getService().getSize()));
                        if(response.getData().getState() == Constants.BOOKING_STATE_BOOKING){
                            status.set(Constants.BOOKING_VISIBLE);
                        }else if(response.getData().getState() == Constants.BOOKING_STATE_DRIVER_ACCEPT){
                            status.set(Constants.BOOKING_ACCEPTED);
                        }else if(response.getData().getState() == Constants.BOOKING_STATE_PICKUP_SUCCESS){
                            status.set(Constants.BOOKING_PICKUP);
                        }else if(response.getData().getState() == Constants.BOOKING_STATE_DONE){
                            status.set(Constants.BOOKING_SUCCESS);
                        }else if(response.getData().getState() == Constants.BOOKING_STATE_CANCEL){
                            status.set(Constants.BOOKING_CUSTOMER_CANCEL);
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
        request.setId(Long.valueOf(booking.get().getId()));
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
