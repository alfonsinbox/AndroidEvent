package com.example.alfon.eventtest;

import com.google.common.util.concurrent.ListenableFuture;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;

/**
 * Created by alfon on 2016-07-22.
 */
public interface ListenableFutureCallback {
    void onSuccess(ListenableFuture<ServiceFilterResponse> listenableFuture);
}
