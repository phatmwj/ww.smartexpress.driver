package ww.smartexpress.driver.data.remote;

import com.google.gson.JsonObject;

import okhttp3.RequestBody;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import ww.smartexpress.driver.data.model.api.ResponseGeneric;
import ww.smartexpress.driver.data.model.api.ResponseListObj;
import ww.smartexpress.driver.data.model.api.ResponseWrapper;
import ww.smartexpress.driver.data.model.api.request.CancelBookingRequest;
import ww.smartexpress.driver.data.model.api.request.ChangeStateRequest;
import ww.smartexpress.driver.data.model.api.request.DriverStateRequest;
import ww.smartexpress.driver.data.model.api.request.EventBookingRequest;
import ww.smartexpress.driver.data.model.api.request.ForgetPassRequest;
import ww.smartexpress.driver.data.model.api.request.IncomeRequest;
import ww.smartexpress.driver.data.model.api.request.LoginRequest;
import ww.smartexpress.driver.data.model.api.request.PositionRequest;
import ww.smartexpress.driver.data.model.api.request.RegisterRequest;
import ww.smartexpress.driver.data.model.api.request.ResetPassRequest;
import ww.smartexpress.driver.data.model.api.request.UpdateBookingRequest;
import ww.smartexpress.driver.data.model.api.request.UpdateProfileRequest;
import ww.smartexpress.driver.data.model.api.response.ActivityRate;
import ww.smartexpress.driver.data.model.api.response.Booking;
import ww.smartexpress.driver.data.model.api.response.CategoryResponse;
import ww.smartexpress.driver.data.model.api.response.CurrentBooking;
import ww.smartexpress.driver.data.model.api.response.DriverServiceResponse;
import ww.smartexpress.driver.data.model.api.response.ForgetPassResponse;
import ww.smartexpress.driver.data.model.api.response.IncomeResponse;
import ww.smartexpress.driver.data.model.api.response.LoginResponse;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;
import ww.smartexpress.driver.data.model.api.response.ProfileResponse;
import ww.smartexpress.driver.data.model.api.response.RegisterResponse;
import ww.smartexpress.driver.data.model.api.response.RoomResponse;
import ww.smartexpress.driver.data.model.api.response.ServiceOnlineResponse;
import ww.smartexpress.driver.data.model.api.response.ServiceResponse;
import ww.smartexpress.driver.data.model.api.response.UploadFileResponse;

public interface ApiService {

    @POST("/v1/driver/login")
    @Headers({"IgnoreAuth:1"})
    Observable<ResponseWrapper<LoginResponse>> login(@Body LoginRequest request);

    @POST("/v1/driver/register")
    @Headers({"IgnoreAuth:1"})
    Observable<ResponseWrapper<RegisterResponse>> register(@Body RegisterRequest request);

    @GET("/v1/driver/profile")
    Observable<ResponseWrapper<ProfileResponse>> getProfile();

    @PUT("/v1/position/update-position")
    Observable<ResponseGeneric> updatePosition(@Body PositionRequest request);

    @POST("/v1/driver/request-forget-password")
    @Headers({"IgnoreAuth:1"})
    Observable<ResponseWrapper<ForgetPassResponse>> forgetPassword(@Body ForgetPassRequest request);

    @PUT("/v1/driver/update-profile")
    Observable<ResponseGeneric> updateProfile(@Body UpdateProfileRequest request);

    @POST("/v1/driver/reset-password")
    @Headers({"IgnoreAuth:1"})
    Observable<ResponseWrapper<ForgetPassResponse>> resetPassword(@Body ResetPassRequest request);

    @POST("/v1/file/upload")
    @Headers({"isMedia:1"})
    Observable<ResponseWrapper<UploadFileResponse>> uploadFile(@Body RequestBody requestBody);

    @GET("/v1/category/auto-complete")
    Observable<ResponseWrapper<ResponseListObj<CategoryResponse>>> getCategoryAutoComplete(@Query("kind") Integer kind, @Query("name") String name,
                                                                                           @Query("categoryId") Long categoryId, @Query("status") Integer status);
    @GET("/v1/user-service/auto-complete")
    Observable<ResponseWrapper<ResponseListObj<ServiceResponse>>> getServiceAutoComplete(@Query("categoryId") Long categoryId, @Query("id") Long id,
                                                                                         @Query("kind") Integer kind, @Query("name") String name, @Query("status") Integer status);

    @GET("/v1/driver-service/auto-complete")
    Observable<ResponseWrapper<ResponseListObj<DriverServiceResponse>>> getDriverService(@Query("driverId") Long driverId,@Query("id") Long id, @Query("serviceId") Long serviceId,
                                                                                         @Query("state") Integer state, @Query("status") Integer status);

    @PUT("/v1/driver-service/change-state")
    Observable<ResponseGeneric> changeStateService(@Body ChangeStateRequest changeStateRequest);

    @PUT("/v1/driver/change-state")
    Observable<ResponseGeneric> changeStateDriver(@Body DriverStateRequest request);

    @GET("/v1/driver-service/my-service-online")
    Observable<ResponseWrapper<ServiceOnlineResponse>> getDriverState();

    @GET("/v1/booking/my-booking")
    Observable<ResponseWrapper<ResponseListObj<Booking>>> getMyBooking(@Query("endDate") String endDate, @Query("startDate") String startDate, @Query("page") Integer pageNumber,
                                                                       @Query("size") Integer pageSize, @Query("state") Integer state);

    @POST("/v1/driver/sum-income")
    Observable<ResponseWrapper<IncomeResponse>> statisticIncome(@Body IncomeRequest request);

    @GET("/v1/booking/activity-rate")
    Observable<ResponseWrapper<ActivityRate>> getActivityRate(@Query("endDate") String endDate, @Query("startDate") String startDate);

    @GET("/v1/booking/detail-booking/{id}")
    Observable<ResponseWrapper<CurrentBooking>> loadBooking(@Path("id") Long id);
    @PUT("/v1/booking/accept")
    Observable<ResponseWrapper<CurrentBooking>> acceptBooking(@Body EventBookingRequest request);
    @PUT("/v1/booking/cancel")
    Observable<ResponseGeneric> cancelBooking(@Body CancelBookingRequest request);
    @GET("/v1/booking/my-current-booking")
    Observable<ResponseWrapper<ResponseListObj<CurrentBooking>>> getCurrentBooking();
    @GET("/directions/json")
    @Headers({"isSearchLocation:1"})
    Observable<JsonObject> getMapDirection(@Query("destination") String destination, @Query("mode") String mode,
                                           @Query("origin") String origin, @Query("key") String api);
    @PUT("/v1/booking/update-state")
    Observable<ResponseGeneric> updateStateBooking(@Body UpdateBookingRequest request);
    @GET("/v1/room/get/{id}")
    Observable<ResponseWrapper<RoomResponse>> getRoomChat(@Path("id") Long id);
    @PUT("/v1/booking/reject")
    Observable<ResponseGeneric> rejectBooking(@Body CancelBookingRequest request);
}
