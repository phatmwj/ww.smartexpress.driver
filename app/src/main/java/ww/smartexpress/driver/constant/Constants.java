package ww.smartexpress.driver.constant;

public class Constants {
    public static final String DB_NAME = "room";
    public static final String PREF_NAME = "mvvm.prefs";

    public static final String VALUE_BEARER_TOKEN_DEFAULT = "NULL";

    //Local Action manager
    public static final String ACTION_EXPIRED_TOKEN = "ACTION_EXPIRED_TOKEN";
    public static final String INSTAGRAM_LOGIN_URL = "https://www.instagram.com/accounts/login/";
    public static final String INSTAGRAM_URL = "https://www.instagram.com/";
    public static final int LOCATION_PERMISSION_CODE = 1;
    public static final char SYMBOLS = '.';
    public static final String ROOM_ID = "ROOM_ID";
    public static String dateFormat = "dd/MM/yyyy HH:mm";
    public static final String FILE_TYPE_AVATAR = "AVATAR";
    public static final String APP_DRIVER = "DRIVER_APP";
    public static final String APP_SERVER = "BACKEND_SOCKET_APP";

    public static final int BOOKING_VISIBLE = 1;
    public static final int BOOKING_NONE = 0;
    public static final int BOOKING_ACCEPTED = 2;
    public static final int BOOKING_CANCELED = -1;
    public static final int BOOKING_SUCCESS = 4;
    public static final int BOOKING_PICKUP = 3;
    public static final int BOOKING_CUSTOMER_CANCEL = -2;
    public static final String GEO_API_KEY = "AIzaSyDQFJ-AGut2GL97rVRvf2q1SJLwABJjWOU";

    public static final int BOOKING_STATE_CANCEL = -100;
    public static final int BOOKING_STATE_BOOKING = 0;
    public static final int BOOKING_STATE_DRIVER_ACCEPT = 100 ;
    public static final int BOOKING_STATE_PICKUP_SUCCESS = 200 ;
    public static final int BOOKING_STATE_DONE = 300 ;

    public static final Integer NOTIFICATION_KIND_DEPOSIT_SUCCESSFULLY = 1;
    public static final Integer NOTIFICATION_KIND_APPROVE_PAYOUT = 2;
    public static final Integer NOTIFICATION_KIND_REJECT_PAYOUT = 3;

    private Constants() {

    }
}
