package helpers;

/**
 * Created by alfon on 2016-07-18.
 */
public interface ITokenCheckCallback {
    void onSuccess();
    void onFailure(Exception exception);
}
