package com.koushikdutta.urlimageviewhelper;

import android.content.Context;
import android.os.AsyncTask;
import org.apache.http.NameValuePair;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.SimpleFormatter;

/**
 * Created with IntelliJ IDEA.
 * User: Dario
 * Date: 29.08.13.
 * Time: 11:31
 * To change this template use File | Settings | File Templates.
 */

public class CachingHttpUrlDownloader implements UrlDownloader {
    private UrlImageViewHelper.RequestPropertiesCallback mRequestPropertiesCallback;

    public UrlImageViewHelper.RequestPropertiesCallback getRequestPropertiesCallback() {
        return mRequestPropertiesCallback;
    }

    public void setRequestPropertiesCallback(final UrlImageViewHelper.RequestPropertiesCallback callback) {
        mRequestPropertiesCallback = callback;
    }

    @Override
    public AsyncTask download(final Context context, final String url, final String filename, final UrlDownloaderCallback callback, final Runnable completion) {
        final AsyncTask<Void, Void, Void> downloader = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... params) {
                try {
                    InputStream is = null;

                    String thisUrl = url;
                    HttpURLConnection urlConnection;
                    while (true) {
                        final URL u = new URL(thisUrl);
                        urlConnection = (HttpURLConnection)u.openConnection();
                        urlConnection.setInstanceFollowRedirects(true);

                        if (mRequestPropertiesCallback != null) {
                            final ArrayList<NameValuePair> props = mRequestPropertiesCallback.getHeadersForRequest(context, url);
                            if (props != null) {
                                for (final NameValuePair pair: props) {
                                    urlConnection.addRequestProperty(pair.getName(), pair.getValue());
                                }
                            }
                        }

                        if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_MOVED_TEMP && urlConnection.getResponseCode() != HttpURLConnection.HTTP_MOVED_PERM)
                            break;
                        thisUrl = urlConnection.getHeaderField("Location");
                    }

                    if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        UrlImageViewHelper.clog("Response Code: " + urlConnection.getResponseCode());
                        return null;
                    }

                    is = urlConnection.getInputStream();

                    // calculate expiration time
                    Long expirationTime = urlConnection.getHeaderFieldDate("Expires", 0);
                    expirationTime = expirationTime > System.currentTimeMillis() ? expirationTime : null;

                    callback.onDownloadComplete(CachingHttpUrlDownloader.this, is, filename, expirationTime);
                    return null;
                }
                catch (final Throwable e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(final Void result) {
                completion.run();
            }
        };

        UrlImageViewHelper.executeTask(downloader);
        return downloader;
    }

    @Override
    public boolean allowCache() {
        return true;
    }

    @Override
    public boolean canDownloadUrl(String url) {
        return url.startsWith("http");
    }
}
