package ww.smartexpress.driver.ui.home;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.helpers.EmptyViewHelper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ww.smartexpress.driver.MVVMApplication;
import ww.smartexpress.driver.R;
import ww.smartexpress.driver.constant.Constants;
import ww.smartexpress.driver.data.Repository;
import ww.smartexpress.driver.data.model.api.ResponseListObj;
import ww.smartexpress.driver.data.model.api.ResponseWrapper;
import ww.smartexpress.driver.data.model.api.response.NotificationResponse;
import ww.smartexpress.driver.data.model.api.response.ProfileResponse;
import ww.smartexpress.driver.data.model.api.response.RoomListResponse;
import ww.smartexpress.driver.data.model.room.UserEntity;
import ww.smartexpress.driver.ui.base.activity.BaseViewModel;
import ww.smartexpress.driver.ui.view.ProgressItem;

public class HomeViewModel extends BaseViewModel {

    public ObservableField<ProfileResponse> profile = new ObservableField<>();
    public MutableLiveData<Integer> totalUnread = new MutableLiveData<>(0);

    public HomeViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
//        loadProfile();
//        getCurrentBooking();
        getMyNotification();
        loadRoomList();
    }

    public void loadProfile(){
        compositeDisposable.add(repository.getApiService().getProfile()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if(response.isResult()){
                        profile.set(response.getData());
                        repository.getSharedPreferences().setUserId(profile.get().getId().toString());
                        UserEntity userEntity = new UserEntity();
                        userEntity.setUserId(profile.get().getId());
                        userEntity.setAvatar(profile.get().getAvatar());
                        userEntity.setFullName(profile.get().getFullName());
                        userEntity.setPhone(profile.get().getPhone());
                        userEntity.setAddress(profile.get().getAddress());

                        repository.getRoomService().userDao().insert(userEntity)
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

    public void getCurrentBooking(){
        showLoading();
        compositeDisposable.add(repository.getApiService().getCurrentBooking(null)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if(response.isResult()){
//                        application.setCurrentBookingId(String.valueOf(response.getData().getId()));
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

    public void getMyNotification(){
        compositeDisposable.add(repository.getApiService().getMyNotification(0,10)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if(response.isResult()){
                        totalUnread.setValue(Math.toIntExact(response.getData().getTotalUnread()));
                    }else {
                    }
                },error->{
                    showErrorMessage(application.getString(R.string.newtwork_error));
                    error.printStackTrace();
                })
        );
    }

    public void loadRoomList(){
        compositeDisposable.add(repository.getApiService().getRoomList(0,100)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if(response.isResult()){
                        if(response.getData()!=null && response.getData().size() >0){
                            for (RoomListResponse roomListResponse: response.getData()){
                                application.getRoomMsgCount().put(roomListResponse.getId(), roomListResponse.getAmount());
                            }
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
}
