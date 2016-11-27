package helpers;

import com.google.common.util.concurrent.ListenableFuture;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;

/**
 * Created by alfonslange on 21/09/16.
 */
public abstract class ListenableFutureCreatedCallback implements IListenableFutureCallback{
    public boolean canceled;
}
