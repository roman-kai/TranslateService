package org.cloa.test.translateservice;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class TranslateService extends Service {
    NotificationManager nm;
    YandexTranslate translater;
    NotificationCompat.Builder builder;
    public static boolean isRunning = false;
    ClipboardManager clipboardManager;
    ClipboardManager.OnPrimaryClipChangedListener listener;
    Intent intent;
    PendingIntent pIntent;

    @Override
    public void onCreate() {
        super.onCreate();
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        builder = new NotificationCompat.Builder(this);
        translater = new YandexTranslate(getResources().getString(R.string.api_key));

        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        listener = new ClipboardManager.OnPrimaryClipChangedListener() {

            @Override
            public void onPrimaryClipChanged() {
                ClipData clipData = clipboardManager.getPrimaryClip();
                new TranslateRequest().execute(clipData.getItemAt(0).getText() + "");

            }
        };

        clipboardManager.addPrimaryClipChangedListener(listener);

        intent = new Intent(this, MainActivity.class);
        pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Log.d("TranslateService", "Created service");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        String status = isOnline() ? "Ready to translate" : "No internet connection";
        Notification notification = builder.setContentIntent(pIntent)
                .setSmallIcon(R.drawable.ic_launcher).setContentText(status)
                .setTicker(status).setContentTitle("Translate en-ru").build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        startForeground(1, notification);

        Toast.makeText(getApplicationContext(), "Service started", Toast.LENGTH_LONG).show();
        isRunning = true;
        return START_NOT_STICKY;
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    void sendNotification(String str) {
        Notification notification;
        notification = builder.setContentIntent(pIntent)
                .setSmallIcon(R.drawable.ic_launcher).setContentText(str).setTicker(str).setContentTitle("Translate en-ru").build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        nm.notify(1, notification);
    }

    public void onDestroy() {
        stop();
        Toast.makeText(getApplicationContext(), "Service stopped", Toast.LENGTH_LONG).show();
        Log.d("TranslateService", "Destroyed service");
    }

    public void stop() {
        clipboardManager.removePrimaryClipChangedListener(listener);
        isRunning = false;
        stopForeground(true);
    }

    public IBinder onBind(Intent arg0) {
        return null;
    }

    class TranslateRequest extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            String result = translater.translate(params[0]);
            if(result != null)
                sendNotification(result);
            else
                sendNotification("Error calling API...");

            return null;
        }

    }

}
