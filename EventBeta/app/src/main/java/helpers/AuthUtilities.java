package helpers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.Pair;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponseCallback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import objects.Globals;

/**
 * Created by alfon on 2016-02-18.
 */
public class AuthUtilities {

    private static final String TAG = "AuthUtilities";

    public static void requestRefreshToken(MobileServiceClient mClient, String login_name, String password, ServiceFilterResponseCallback serviceFilterResponseCallback) {
        List<Pair<String, String>> params = new ArrayList<>();
        login_name = "admin";
        password = "aMaSaa642#";
        params.add(Pair.create("login_name", login_name));
        params.add(Pair.create("password", password));
        params.add(Pair.create("device", android.os.Build.MODEL));

        List<Pair<String, String>> headers = new ArrayList<>();

        mClient.invokeApi("auth/refresh_token/get", null, "POST", headers, params, serviceFilterResponseCallback);
    }

    public static void renewAccessToken(Context activity, MobileServiceClient mClient, ServiceFilterResponseCallback serviceFilterResponseCallback) {
        List<Pair<String, String>> params = new ArrayList<>();

        List<Pair<String, String>> headers = new ArrayList<>();
        headers.add(Pair.create("X-ZUMO-AUTH", getLocalToken(Globals.PREFERENCE_USER_ACCESS_TOKEN, activity)));

        mClient.invokeApi("auth/access_token/renew", null, "POST", headers, params, serviceFilterResponseCallback);
    }

    public static void refreshAccessToken(Context activity, MobileServiceClient mClient, ServiceFilterResponseCallback serviceFilterResponseCallback) {
        List<Pair<String, String>> params = new ArrayList<>();
        Log.d(TAG, "refreshAccessToken: " + Globals.PREFERENCE_USER_REFRESH_TOKEN);
        params.add(Pair.create("refresh_token", getLocalToken(Globals.PREFERENCE_USER_REFRESH_TOKEN, activity)));

        List<Pair<String, String>> headers = new ArrayList<>();

        mClient.invokeApi("auth/access_token/refresh", null, "POST", headers, params, serviceFilterResponseCallback);
    }

    public static Boolean saveAccessToken(String token, int expires, Context activity) {
        try {
            SharedPreferences storedUserData = activity.getSharedPreferences(Globals.PREFERENCES_USERSETTINGS, 0);
            SharedPreferences.Editor editor = storedUserData.edit();
            editor.putString(Globals.PREFERENCE_USER_ACCESS_TOKEN, token);
            editor.putInt(Globals.PREFERENCE_USER_ACCESS_TOKEN_EXPIRES, expires);
            Calendar authTokenValidTo = Calendar.getInstance();
            authTokenValidTo.add(Calendar.SECOND, expires);
            editor.putInt(Globals.PREFERENCE_USER_ACCESS_TOKEN_VALID_TO, ((int) authTokenValidTo.getTimeInMillis()));
            editor.apply();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static Boolean saveRefreshToken(String token, Context activity) {
        try {
            SharedPreferences storedUserData = activity.getSharedPreferences(Globals.PREFERENCES_USERSETTINGS, 0);
            SharedPreferences.Editor editor = storedUserData.edit();
            editor.putString(Globals.PREFERENCE_USER_REFRESH_TOKEN, token);
            editor.apply();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static Boolean removeToken(String tokenName, Context activity) {
        try {
            SharedPreferences storedUserData = activity.getSharedPreferences(Globals.PREFERENCES_USERSETTINGS, 0);
            SharedPreferences.Editor editor = storedUserData.edit();
            editor.putString(tokenName, "");
            editor.commit();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static String getLocalToken(String tokenName, Activity activity) {
        return activity.getSharedPreferences(Globals.PREFERENCES_USERSETTINGS, 0).getString(tokenName, "");
    }

    public static String getLocalToken(String tokenName, Context context) {
        return context.getSharedPreferences(Globals.PREFERENCES_USERSETTINGS, 0).getString(tokenName, "");
    }

    public static int accessTokenValidTimeLeft(Context activity){
        int timeNow = (int) Calendar.getInstance().getTimeInMillis();
        int validTo = activity.getSharedPreferences(Globals.PREFERENCES_USERSETTINGS, Context.MODE_PRIVATE).getInt(Globals.PREFERENCE_USER_ACCESS_TOKEN_VALID_TO, 0);
        return (validTo - timeNow) / (1000);
    }

}
