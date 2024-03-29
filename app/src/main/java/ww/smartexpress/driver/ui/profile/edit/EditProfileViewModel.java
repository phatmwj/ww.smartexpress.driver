package ww.smartexpress.driver.ui.profile.edit;


import android.annotation.SuppressLint;

import androidx.databinding.ObservableField;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.RequestBody;
import ww.smartexpress.driver.MVVMApplication;
import ww.smartexpress.driver.data.Repository;
import ww.smartexpress.driver.data.model.api.ResponseGeneric;
import ww.smartexpress.driver.data.model.api.ResponseWrapper;
import ww.smartexpress.driver.data.model.api.request.UpdateProfileRequest;
import ww.smartexpress.driver.data.model.api.response.UploadFileResponse;
import ww.smartexpress.driver.data.model.room.UserEntity;
import ww.smartexpress.driver.ui.base.activity.BaseViewModel;

public class EditProfileViewModel extends BaseViewModel {
    public ObservableField<String> fullName = new ObservableField<>();
    public ObservableField<String> email = new ObservableField<>();
    public ObservableField<String> phone = new ObservableField<>();
    public ObservableField<String> avatar = new ObservableField<>();
    public ObservableField<UserEntity> user = new ObservableField<>();
    public ObservableField<String> oldPassword = new ObservableField<>("");
    public ObservableField<Boolean> isPwVisibility = new ObservableField<>(false);

    public EditProfileViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
    }
    public Repository getRepository(){
        return repository;
    }

    public void back(){
        application.getCurrentActivity().onBackPressed();
    }

    @SuppressLint("CheckResult")
    public void confirm(){
        UpdateProfileRequest updateProfileRequest = new UpdateProfileRequest();
        updateProfileRequest.setAvatar(avatar.get());
        updateProfileRequest.setFullName(fullName.get());
        updateProfileRequest.setOldPassword(oldPassword.get());
        showLoading();
        compositeDisposable.add(repository.getApiService().updateProfile(updateProfileRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(res->{
                    if(res.isResult()) {
                        user.get().setFullName(fullName.get());
                        user.get().setAvatar(avatar.get());
                        repository.getRoomService().userDao().insert(user.get())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(()-> {

                                }, throwable -> {

                                });
                        showSuccessMessage(res.getMessage());
                    }else {
                        showErrorMessage(res.getMessage());
                    }
                    hideLoading();
                },err-> {
                    err.printStackTrace();
                    hideLoading();
                }
                ));
    }

    public Observable<ResponseGeneric> updateProfile(UpdateProfileRequest request){
        return repository.getApiService().updateProfile(request);
    }

    public Observable<ResponseWrapper<UploadFileResponse>> uploadAvatar(RequestBody requestBody){
        return repository.getApiService().uploadFile(requestBody);
    }

    public void setIsPwVisibility(){
        isPwVisibility.set(!isPwVisibility.get());
    }

}
