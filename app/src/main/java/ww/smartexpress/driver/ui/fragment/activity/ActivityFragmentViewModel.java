package ww.smartexpress.driver.ui.fragment.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import java.text.SimpleDateFormat;
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
import ww.smartexpress.driver.data.model.api.ResponseWrapper;
import ww.smartexpress.driver.data.model.api.request.CancelBookingRequest;
import ww.smartexpress.driver.data.model.api.request.DriverStateRequest;
import ww.smartexpress.driver.data.model.api.request.EventBookingRequest;
import ww.smartexpress.driver.data.model.api.request.PositionRequest;
import ww.smartexpress.driver.data.model.api.request.UpdateBookingRequest;
import ww.smartexpress.driver.data.model.api.response.CurrentBooking;
import ww.smartexpress.driver.data.model.api.response.UploadFileResponse;
import ww.smartexpress.driver.data.model.room.UserEntity;
import ww.smartexpress.driver.ui.base.fragment.BaseFragmentViewModel;
import ww.smartexpress.driver.ui.chat.ChatActivity;
import ww.smartexpress.driver.utils.DateUtils;

public class ActivityFragmentViewModel extends BaseFragmentViewModel {

    public ObservableField<Integer> state = new ObservableField<>(0);

    public ObservableField<UserEntity> user = new ObservableField<>();

    public ObservableField<String> latitude = new ObservableField<>();
    public ObservableField<String> longitude = new ObservableField<>();
    public ObservableField<Integer> isBusy = new ObservableField<>(0);
    public MutableLiveData<List<CurrentBooking>> bookingList = new MutableLiveData<>(new ArrayList<>());
    public MutableLiveData<Long> newBookingId = new MutableLiveData<>();
    public MutableLiveData<CurrentBooking> bookingUpdate = new MutableLiveData<>(new CurrentBooking());
    public MutableLiveData<CurrentBooking> newBooking = new MutableLiveData<>(new CurrentBooking());
    public ObservableField<Integer> positionUpdate = new ObservableField<>(null);

    public ObservableField<String> image = new ObservableField<>(null);
    public ActivityFragmentViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);

        loadProfile();

        getCurrentBooking();

        getStateDriver();
    }

    public MVVMApplication getApplication(){
        return application;
    }
    public void updatePosition(){
        PositionRequest positionRequest = new PositionRequest();
        positionRequest.setIsBusy(isBusy.get());
        positionRequest.setLatitude(latitude.get());
        positionRequest.setLongitude(longitude.get());
        positionRequest.setStatus(1);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        positionRequest.setTimeUpdate(DateUtils.convertToUTC(format.format(date)));

        compositeDisposable.add(repository.getApiService().updatePosition(positionRequest)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                            if(response.isResult()){
                            }else {
                                showErrorMessage(response.getMessage());
                            }
                        },error->{
                            error.printStackTrace();
                        })
        );
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

    public void getCurrentBooking(){
        showLoading();
        compositeDisposable.add(repository.getApiService().getCurrentBooking()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                            if(response.isResult()){
                                if(response.getData().getContent()!=null && response.getData().getTotalElements()>0){
                                    bookingList.setValue(response.getData().getContent());
                                    for (CurrentBooking booking: response.getData().getContent()
                                    ) {
                                        application.getWebSocketLiveData().getCodeBooking().add(booking.getCode());
                                    }
                                    application.getWebSocketLiveData().sendPing();
                                }else {
                                    bookingList.setValue(new ArrayList<>());
                                }
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

    public void loadBooking(long id){
        showLoading();
        compositeDisposable.add(repository.getApiService().loadBooking(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if(response.isResult()){
                            bookingUpdate.setValue(response.getData());
//                        showSuccessMessage(response.getMessage());
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

    public void loadNewBooking(long id){
        showLoading();
        compositeDisposable.add(repository.getApiService().loadBooking(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                            if(response.isResult()){
                                newBooking.setValue(response.getData());
                                newBookingId.setValue(response.getData().getId());
                                application.getWebSocketLiveData().getCodeBooking().add(0,response.getData().getCode());
                                application.getWebSocketLiveData().sendPing();
//                        showSuccessMessage(response.getMessage());
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

    public void loadCancelBooking(long id){
        showLoading();
        compositeDisposable.add(repository.getApiService().loadBooking(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                            if(response.isResult()){
                                positionUpdate.set(application.getWebSocketLiveData().getCodeBooking().indexOf(response.getData())+1);
                                bookingUpdate.setValue(response.getData());
//                        showSuccessMessage(response.getMessage());
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


    public void updateStateBooking(int state, long id){
        UpdateBookingRequest request = new UpdateBookingRequest();
        request.setId(id);
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
                        loadBooking(id);
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

    public void cancelBooking(long id){
        CancelBookingRequest request = new CancelBookingRequest();
        request.setId(id);
        request.setNote(null);
        showLoading();
        compositeDisposable.add(repository.getApiService().cancelBooking(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if(response.isResult()){
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

    public void rejectBooking(long id){
        CancelBookingRequest request = new CancelBookingRequest();
        request.setId(id);
        request.setNote(null);
        showLoading();
        compositeDisposable.add(repository.getApiService().rejectBooking(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if(response.isResult()){
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

    public Observable<ResponseWrapper<UploadFileResponse>> uploadAvatar(RequestBody requestBody){
        return repository.getApiService().uploadFile(requestBody);
    }

    public void acceptBooking(long id){
        EventBookingRequest request = new EventBookingRequest();
        request.setBookingId(id);
        request.setNote(null);
        showLoading();
        compositeDisposable.add(repository.getApiService().acceptBooking(request)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                            if(response.isResult()){
                                repository.getSharedPreferences().setLong(Constants.ROOM_ID,response.getData().getRoom().getId());
                                loadBooking(id);
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

    public void openChat(String codeBooking, Long roomId){
        Intent intent = new Intent(application.getCurrentActivity(), ChatActivity.class);
        intent.putExtra("codeBooking", codeBooking);
        intent.putExtra("roomId", roomId);
        application.getCurrentActivity().startActivity(intent);
    }


}
