package com.koushikdutta.urlimageviewhelper;

import java.io.InputStream;
import java.util.Date;

import android.content.Context;
import android.os.AsyncTask;

public interface UrlDownloader {
    public static interface UrlDownloaderCallback {
        public void onDownloadComplete(UrlDownloader downloader, InputStream in, String filename, Long expirationTime);
    }
    
    public AsyncTask download(Context context, String url, String filename, UrlDownloaderCallback callback, Runnable completion);
    public boolean allowCache();
    public boolean canDownloadUrl(String url);
}