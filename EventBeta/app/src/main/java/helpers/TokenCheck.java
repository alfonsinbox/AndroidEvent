package helpers;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponseCallback;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;

import java.util.ArrayList;

import objects.AccessToken;
import objects.Globals;

/**
 * Created by alfon on 2016-07-18.
 */
public abstract class TokenCheck implements ITokenCheckCallback {

    private static final String TAG = "TokenCheck";

    private static Context context;
    private static MobileServiceClient mClient = Globals.mClient;
    private static boolean running = false;
    private static ArrayList<ITokenCheckCallback> callbacks = new ArrayList<>();

    public static void checkToken(ITokenCheckCallback callback) {

        Log.d(TAG, "checkToken: STARTING TOKEN VALIDATION CHECK");

        callbacks.add(callback);

        if (!running) {
            Log.d(TAG, "checkToken: Started one running process");
            running = true;

            int validTimeLeft = AuthUtilities.accessTokenValidTimeLeft(context);

            // Check if user has access_token from before and the it hasn't expired yet
            if (!AuthUtilities.getLocalToken(Globals.PREFERENCE_USER_ACCESS_TOKEN, context).equals("")
                    && validTimeLeft > 60) {

                Log.d(TAG, "checkToken: Access token exists and is valid, expires in " + validTimeLeft + " seconds");
                sendAllSuccess();
            } else {

                /** Probably bad practice to be able to refresh the access token (using itself),
                 * it makes it easier to gain control over someone's account */

                Log.d(TAG, "checkToken: No valid access token, refreshing with Refresh token");
                // Get access_token from refresh_token
                ServiceFilterResponseCallback refreshedAccessTokenResponseCallback = new ServiceFilterResponseCallback() {
                    @Override
                    public void onResponse(ServiceFilterResponse response, Exception exception) {
                        if (exception != null) {
                            exception.printStackTrace();
                            sendAllFailure(exception);
                            return;
                        }
                        Log.d(TAG, "onResponse: Token was successfully refreshed!");
                        AccessToken accessToken = new Gson().fromJson(response.getContent(), AccessToken.class);
                        AuthUtilities.saveAccessToken(accessToken.token, accessToken.expires, context);

                        sendAllSuccess();
                    }
                };
                AuthUtilities.refreshAccessToken(context, mClient, refreshedAccessTokenResponseCallback);
            }
        }
    }

    private static void sendAllSuccess() {
        running = false;
        for(ITokenCheckCallback callback : callbacks){
            callback.onSuccess();
        }
        callbacks = new ArrayList<>();
    }

    private static void sendAllFailure(Exception exception) {
        running = false;
        for(ITokenCheckCallback callback : callbacks){
            callback.onFailure(exception);
        }
        callbacks = new ArrayList<>();
    }

    public static void setContext(Context context) {
        TokenCheck.context = context;
    }


}
