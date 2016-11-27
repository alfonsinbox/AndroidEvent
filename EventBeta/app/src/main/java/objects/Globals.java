package objects;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;

/**
 * Created by alfonslange on 13/09/16.
 */
public class Globals {
    public static final String SENDER_ID = "345618100514";
    public static final String PREFERENCES_USERSETTINGS = "UserSettings";
    public static final String PREFERENCE_GCM_REGISTRATION_ID = "PreferenceGcmRegId";
    public static final String PREFERENCE_REGISTRATION_ID = "PreferenceRegId";
    public static final String PREFERENCE_DEVICE_REGISTERED_FOR_PUSH = "PreferenceDeviceRegisteredForPush";
    public static final String PREFERENCE_USER_REFRESH_TOKEN = "refresh_token";
    public static final String PREFERENCE_USER_ACCESS_TOKEN = "access_token";
    public static final String PREFERENCE_USER_ACCESS_TOKEN_EXPIRES = "access_token_expires";
    public static final String PREFERENCE_USER_ACCESS_TOKEN_VALID_FROM = "access_token_valid_from";
    public static final String PREFERENCE_USER_ACCESS_TOKEN_VALID_TO = "access_token_valid_to";

    public static final String EXTRA_TOKEN_SERVICE_CALLBACK = "extra_token_service_callback";

    // Tillhör dålig implementation, försök komma på bättre metod
    public static final String ACTION_TOKEN_CHECK_CALLBACK_SUCCESS = "ActionTokenCheckCallbackSuccess";
    public static final String ACTION_TOKEN_CHECK_CALLBACK_FAILURE = "ActionTokenCheckCallbackFailure";

    public static MobileServiceClient mClient;
    public static void setmClient(MobileServiceClient mClient){
        Globals.mClient = mClient;
    }
}
