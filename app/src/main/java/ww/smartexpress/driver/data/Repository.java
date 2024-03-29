package ww.smartexpress.driver.data;

import ww.smartexpress.driver.data.local.prefs.PreferencesService;
import ww.smartexpress.driver.data.local.room.RoomService;
import ww.smartexpress.driver.data.remote.ApiService;


public interface Repository {

    /**
     * ################################## Preference section ##################################
     */
    String getToken();

    void setToken(String token);

    PreferencesService getSharedPreferences();


    /**
     * ################################## Remote api ##################################
     */
    ApiService getApiService();

    RoomService getRoomService();

}
