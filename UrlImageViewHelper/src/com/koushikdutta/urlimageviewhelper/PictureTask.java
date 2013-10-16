package com.koushikdutta.urlimageviewhelper;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;

/**
 * Created with IntelliJ IDEA.
 * User: Dario
 * Date: 16.10.13.
 * Time: 12:43
 * To change this template use File | Settings | File Templates.
 */
public class PictureTask {
    public AsyncTask asyncOperation;
    public String targetUrl;
    public View imageView;

    public PictureTask(AsyncTask asyncOperation, String targetUrl, View imageView) {
        this.asyncOperation = asyncOperation;
        this.targetUrl = targetUrl;
        this.imageView = imageView;
    }

    public boolean cancelAsyncOperation() {
        return null != asyncOperation && asyncOperation.cancel(true);
    }

    // operation is not cancelled if targetUrl is matched
    public boolean cancleAsyncOperation(View targetView, String targetUrl) {
        return targetView == imageView
                && !TextUtils.equals(targetUrl, this.targetUrl)
                && cancelAsyncOperation();
    }
}
