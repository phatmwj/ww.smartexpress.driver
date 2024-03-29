package ww.smartexpress.driver.data;

import ww.smartexpress.driver.data.local.prefs.PreferencesService;
import ww.smartexpress.driver.data.local.room.RoomService;
import ww.smartexpress.driver.data.remote.ApiService;

import javax.inject.Inject;

public class AppRepository implements Repository {

    private final ApiService mApiService;
    private final PreferencesService mPreferencesHelper;
    private final RoomService roomService;

    @Inject
    public AppRepository(PreferencesService preferencesHelper, ApiService apiService, RoomService roomService) {
        this.mPreferencesHelper = preferencesHelper;
        this.mApiService = apiService;
        this.roomService = roomService;
    }

    /**
     * ################################## Preference section ##################################
     */
    @Override
    public String getToken() {
        return mPreferencesHelper.getToken();
    }

    @Override
    public void setToken(String token) {
        mPreferencesHelper.setToken(token);
    }

    @Override
    public PreferencesService getSharedPreferences() {
        return mPreferencesHelper;
    }


    /**
     * ################################## Remote api ##################################
     */
    @Override
    public ApiService getApiService() {
        return mApiService;
    }


    @Override
    public RoomService getRoomService() {
        return roomService;
    }
}
