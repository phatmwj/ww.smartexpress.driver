package ww.smartexpress.driver.constant;


import lombok.Data;

@Data
public class ErrorCode {

    /**
     * Starting error code Category
     */
    public static final String CATEGORY_ERROR_NOT_FOUND = "ERROR-CATEGORY-ERROR-0000";
    public static final String CATEGORY_ERROR_NAME_EXIST = "ERROR-CATEGORY-ERROR-0001";

    /**
     * Starting error code Service
     */
    public static final String SERVICE_ERROR_NOT_FOUND = "ERROR-SERVICE-ERROR-0000";
    public static final String SERVICE_ERROR_SERVICE_EXIST = "ERROR-SERVICE-ERROR-0001";
    public static final String SERVICE_ERROR_CATEGORY_NOT_FOUND = "ERROR-SERVICE-ERROR-0002";
    public static final String SERVICE_ERROR_NAME_EXIST = "ERROR-SERVICE-ERROR-0003";

    /**
     * Starting error code Driver
     */
    public static final String DRIVER_ERROR_NOT_FOUND = "ERROR-DRIVER-ERROR-0000";
    public static final String DRIVER_ERROR_PHONE_EXIST = "ERROR-DRIVER-ERROR-0001";
    public static final String DRIVER_ERROR_LOGIN_FAILED = "ERROR-DRIVER-ERROR-0002";
    public static final String DRIVER_ERROR_WRONG_PASSWORD = "ERROR-DRIVER-ERROR-0003";
    public static final String DRIVER_ERROR_EMAIL_NOT_FOUND = "ERROR-DRIVER-ERROR-0004";
    public static final String DRIVER_ERROR_OTP_INVALID = "ERROR-DRIVER-ERROR-0005";
    public static final String DRIVER_ERROR_EXPIRED_TIME = "ERROR-DRIVER-ERROR-0006";
    public static final String DRIVER_ERROR_NOT_ACTIVE = "ERROR-DRIVER-ERROR-0007";
    public static final String DRIVER_ERROR_POSITION_IS_BUSY = "ERROR-DRIVER-ERROR-0008";
    public static final String DRIVER_ERROR_STATE_OFF = "ERROR-DRIVER-ERROR-0009";
    public static final String DRIVER_ERROR_BANK_CARD_INVALID = "ERROR-DRIVER-ERROR-0010";
    public static final String DRIVER_ERROR_IDENTIFICATION_CARD_INVALID = "ERROR-DRIVER-ERROR-0011";
    public static final String DRIVER_ERROR_TOTP_MISSING = "ERROR-DRIVER-ERROR-0012";
    public static final String DRIVER_ERROR_TOTP_INVALID = "ERROR-DRIVER-ERROR-0013";
    public static final String DRIVER_ERROR_ENABLED_2FA = "ERROR-DRIVER-ERROR-0014";
    public static final String DRIVER_ERROR_DEVICE_NAME_NULL_WHEN_ENABLE_2FA = "ERROR-DRIVER-ERROR-0014";
    public static final String DRIVER_ERROR_ACCOUNT_NOT_VERIFIED = "ERROR-DRIVER-ERROR-0017";

    /**
     * Starting error code Driver Service
     */
    public static final String DRIVER_SERVICE_ERROR_NOT_FOUND = "ERROR-DRIVER-SERVICE-ERROR-0000";
    public static final String DRIVER_SERVICE_ERROR_DRIVER_NOT_FOUND = "ERROR-DRIVER-SERVICE-ERROR-0001";
    public static final String DRIVER_SERVICE_ERROR_SERVICE_NOT_FOUND = "ERROR-DRIVER-SERVICE-ERROR-0002";
    public static final String DRIVER_SERVICE_ERROR_DRIVER_EXIST_SERVICE = "ERROR-DRIVER-SERVICE-ERROR-0003";
    public static final String DRIVER_SERVICE_ERROR_STATE_OFF = "ERROR-DRIVER-SERVICE-ERROR-0004";
    public static final String DRIVER_SERVICE_ERROR_HAVE_OTHER_BOOKING = "ERROR-DRIVER-SERVICE-ERROR-0005";
    public static final String DRIVER_SERVICE_ERROR_NOT_ACTIVE = "ERROR-DRIVER-SERVICE-ERROR-0006";
    public static final String DRIVER_SERVICE_ERROR_POSITION_IS_BUSY = "ERROR-DRIVER-SERVICE-ERROR-0007";

    /**
     * Starting error code Position
     */
    public static final String POSITION_ERROR_NOT_FOUND = "ERROR-POSITION-ERROR-0000";
    public static final String POSITION_ERROR_EXIST = "ERROR-POSITION-ERROR-0001";
    public static final String POSITION_ERROR_LATITUDE_OR_LONGITUDE_NULL = "ERROR-POSITION-ERROR-0002";

    /**
     * Starting error code Driver Vehicle
     */
    public static final String DRIVER_VEHICLE_ERROR_NOT_FOUND = "ERROR-DRIVER_VEHICLE-ERROR-0000";
    public static final String DRIVER_VEHICLE_ERROR_EXIST = "ERROR-DRIVER_VEHICLE-ERROR-0001";
    public static final String DRIVER_VEHICLE_ERROR_PLATE_EXIST = "ERROR-DRIVER_VEHICLE-ERROR-0002";
    public static final String DRIVER_VEHICLE_ERROR_LICENSE_NO_EXIST = "ERROR-DRIVER_VEHICLE-ERROR-0003";
    public static final String DRIVER_VEHICLE_ERROR_DRIVER_NOT_FOUND = "ERROR-DRIVER_VEHICLE-ERROR-0004";

    /**
     * Starting error code Service Customer
     */
    public static final String CUSTOMER_ERROR_NOT_FOUND = "ERROR-CUSTOMER-ERROR-0000";
    public static final String CUSTOMER_ERROR_PHONE_EXIST = "ERROR-CUSTOMER-ERROR-0001";
    public static final String CUSTOMER_ERROR_EMAIL_EXIST = "ERROR-CUSTOMER-ERROR-0002";
    public static final String CUSTOMER_ERROR_LOGIN_FAILED = "ERROR-CUSTOMER-ERROR-0003";
    public static final String CUSTOMER_ERROR_WRONG_PASSWORD = "ERROR-CUSTOMER-ERROR-0004";
    public static final String CUSTOMER_ERROR_EMAIL_NOT_FOUND = "ERROR-CUSTOMER-ERROR-0005";
    public static final String CUSTOMER_ERROR_OTP_INVALID = "ERROR-CUSTOMER-ERROR-0006";
    public static final String CUSTOMER_ERROR_EXPIRED_TIME = "ERROR-CUSTOMER-ERROR-0007";
    public static final String CUSTOMER_ERROR_NOT_ACTIVE = "ERROR-CUSTOMER-ERROR-0008";
    public static final String CUSTOMER_ERROR_BANK_CARD_INVALID = "ERROR-CUSTOMER-ERROR-0009";
    public static final String CUSTOMER_ERROR_STATUS_NOT_PENDING = "ERROR-CUSTOMER-ERROR-0010";
    public static final String CUSTOMER_ERROR_STATUS_PENDING = "ERROR-CUSTOMER-ERROR-00011";

    /**
     * Starting error code Nation
     * */
    public static final String NATION_ERROR_NOT_FOUND = "ERROR-NATION-0001";
    public static final String NATION_ERROR_POSTCODE_EXIST = "ERROR-NATION-0002";
    public static final String NATION_ERROR_CANT_DELETE_RELATIONSHIP_WITH_DRIVER = "ERROR-NATION-0003";

    /**
     * Starting error code Booking
     * */
    public static final String BOOKING_ERROR_NOT_FOUND = "ERROR-BOOKING-0001";
    public static final String BOOKING_ERROR_NOT_ALLOW_CANCEL_WHEN_PICKED_UP = "ERROR-BOOKING-0002";
    public static final String BOOKING_ERROR_UPDATE_STATE_NOT_VALID = "ERROR-BOOKING-0003";
    public static final String BOOKING_ERROR_NOT_ALLOW_DELETE = "ERROR-BOOKING-0004";
    public static final String BOOKING_ERROR_CANCELED_BOOKING = "ERROR-BOOKING-0005";
    public static final String BOOKING_ERROR_NOT_ALLOW_DRIVER_UPDATE_STATE_CANCEL_OR_BOOKING = "ERROR-BOOKING-0006";
    public static final String BOOKING_ERROR_NOT_ALLOW_UPDATE_DRIVER = "ERROR-BOOKING-0007";
    public static final String BOOKING_ERROR_HAVE_OTHER_BOOKING = "ERROR-BOOKING-0008";
    public static final String BOOKING_ERROR_REVENUE_STATISTIC_EXCEED_NUMBER_OF_DAYS = "ERROR-BOOKING-0009";
    public static final String BOOKING_ERROR_REVENUE_STATISTIC_START_DATE_MUST_BEFORE_END_DATE = "ERROR-BOOKING-0010";
    public static final String BOOKING_ERROR_REVENUE_STATISTIC_INVALID_DATE_FORMAT = "ERROR-BOOKING-0011";
    public static final String BOOKING_ERROR_PAYMENT_MONEY_GREATER_THAN_BALANCE_IN_WALLET = "ERROR-BOOKING-0012";
    public static final String BOOKING_ERROR_HOLDING_MONEY_GREATER_THAN_BALANCE_IN_WALLET = "ERROR-BOOKING-0013";

    /**
     * Starting error code Booking Detail
     * */
    public static final String BOOKING_DETAIL_ERROR_NOT_FOUND = "ERROR-BOOKING-DETAIL-0001";

    /**
     * Starting error code Room
     */
    public static final String ROOM_ERROR_NOT_FOUND = "ERROR-ROOM-ERROR-0000";
    public static final String ROOM_ERROR_EXIST = "ERROR-ROOM-ERROR-0001";
    public static final String ROOM_ERROR_BOOKING_NOT_FOUND = "ERROR-ROOM-ERROR-0002";
    public static final String ROOM_ERROR_BOOKING_STATE_NOT_VALID = "ERROR-ROOM-ERROR-0003";
    public static final String ROOM_ERROR_DRIVER_NOT_FOUND_IN_BOOKING = "ERROR-ROOM-ERROR-0004";
    public static final String ROOM_ERROR_CUSTOMER_NOT_FOUND_IN_BOOKING = "ERROR-ROOM-ERROR-0005";

    /**
     * Starting error code Chat Detail
     */
    public static final String CHAT_DETAIL_ERROR_NOT_FOUND = "ERROR-CHAT-DETAIL-0001";
    public static final String CHAT_DETAIL_ERROR_CAN_NOT_UPDATE = "ERROR-CHAT-DETAIL-0002";
    public static final String CHAT_DETAIL_ERROR_NOT_TYPE = "ERROR-CHAT-DETAIL-0003";

    /**
     * Starting error code Sender
     */
    public static final String SENDER_ERROR_NOT_FOUND = "ERROR-SENDER-0001";

    /**
     * Starting error code Rating
     */
    public static final String RATING_ERROR_NOT_FOUND = "ERROR-RATING-ERROR-0000";
    public static final String RATING_ERROR_EXIST = "ERROR-RATING-ERROR-0001";
    public static final String RATING_ERROR_BOOKING_NOT_DONE = "ERROR-RATING-ERROR-0002";

    /**
     * Starting error code Promotion
     */
    public static final String PROMOTION_ERROR_NOT_FOUND = "ERROR-PROMOTION-ERROR-0000";
    public static final String PROMOTION_ERROR_STATE_INVALID = "ERROR-PROMOTION-ERROR-0001";
    public static final String PROMOTION_ERROR_QUANTITY_CAN_NOT_UPDATED = "ERROR-PROMOTION-ERROR-0002";
    public static final String PROMOTION_ERROR_USED = "ERROR-PROMOTION-ERROR-0003";

    /**
     * Starting error code Promotion Code
     */
    public static final String PROMOTION_CODE_ERROR_USED_UP = "ERROR-PROMOTION-CODE-ERROR-0000";

    /**
     * Starting error code Wallet
     */
    public static final String WALLET_ERROR_NOT_FOUND = "ERROR-WALLET-ERROR-0000";
    public static final String WALLET_ERROR_KIND_INVALID = "ERROR-WALLET-ERROR-0001";

    /**
     * Starting error code Wallet Transaction
     */
    public static final String WALLET_TRANSACTION_ERROR_NOT_FOUND = "ERROR-WALLET-TRANSACTION-ERROR-0000";
    public static final String WALLET_TRANSACTION_ERROR_MONEY_INVALID = "ERROR-WALLET-TRANSACTION-ERROR-0001";

    /**
     * Starting error code Request Pay Out
     */
    public static final String REQUEST_PAY_OUT_ERROR_NOT_FOUND = "ERROR-REQUEST-PAY-OUT-ERROR-0000";
    public static final String REQUEST_PAY_OUT_ERROR_STATE_INVALID = "ERROR-REQUEST-PAY-OUT-ERROR-0001";
    public static final String REQUEST_PAY_OUT_ERROR_BANK_CARD_INVALID = "ERROR-REQUEST-PAY-OUT-ERROR-0002";
    public static final String REQUEST_PAY_OUT_ERROR_TRANSACTION_CODE_OR_IMAGE_NULL = "ERROR-REQUEST-PAY-OUT-ERROR-0003";
    public static final String REQUEST_PAY_OUT_ERROR_NOTE_NULL = "ERROR-REQUEST-PAY-OUT-ERROR-0004";
    public static final String REQUEST_PAY_OUT_ERROR_MONEY_WITHDRAW_GREATER_THAN_BALANCE_IN_WALLET = "ERROR-REQUEST-PAY-OUT-ERROR-0005";
    public static final String REQUEST_PAY_OUT_ERROR_NOT_ALLOW_DELETE_WHEN_APPROVED_OR_REJECTED = "ERROR-REQUEST-PAY-OUT-ERROR-0006";
    public static final String REQUEST_PAY_OUT_ERROR_HAVE_REQUEST_PAY_OUT_WITH_STATE_INIT = "ERROR-REQUEST-PAY-OUT-ERROR-0007";
    public static final String REQUEST_PAY_OUT_ERROR_USER_NOT_HAVE_THIS_REQUEST_PAY_OUT = "ERROR-REQUEST-PAY-OUT-ERROR-0008";

    /**
     * Starting error code Settings
     * */
    public static final String SETTINGS_ERROR_NOT_FOUND = "ERROR-SETTING-0000";
    public static final String SETTINGS_ERROR_SETTING_KEY_EXISTED = "ERROR-SETTING-0001";
    /**
     * Starting error code Notification
     */
    public static final String NOTIFICATION_ERROR_NOT_FOUND = "ERROR-NOTIFICATION-ERROR-0000";

    public static final String NOTIFICATION_USER_ERROR_NOT_FOUND = "ERROR-NOTIFICATION-ERROR-0001";

    public static final String OTP_ERROR_NOT_CORRECT_OR_EXPIRED = "OTP_ERROR_NOT_CORRECT_OR_EXPIRED";

    /**
     * Starting error code News
     */
    public static final String NEWS_ERROR_NOT_FOUND = "ERROR-NEWS-ERROR-0000";
    public static final String NEWS_ERROR_TARGET_USER_NOT_FOUND = "ERROR-NEWS-ERROR-0001";
    public static final String NEWS_ERROR_ALREADY_PUBLISHED = "ERROR-NEWS-ERROR-0002";

}
