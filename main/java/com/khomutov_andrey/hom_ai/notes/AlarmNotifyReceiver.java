package com.khomutov_andrey.hom_ai.notes;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import java.util.HashMap;

public class AlarmNotifyReceiver extends BroadcastReceiver {
    private NotificationManager nm;
    private Notification notification;
    Context context;
    Control control;
    int widgetID;
    String ticker;
    String title;
    String text;
    String stringSoundUri;
    boolean notify_on;
    long notify_time;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        Intent intent_notify = intent;
        widgetID = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        // Получаем настройки времени уведомления и данные для уведомления
        control = new Control(context);
        HashMap<String, Object> params = control.getNotifyParam(widgetID);
        title = (String)params.get(ControlListener.NOTIFY_TITLE);
        text = control.getTextWidget(widgetID);
        stringSoundUri = (String)params.get(ControlListener.NOTIFY_SOUND_URI);
        //getSettings();// Получаем настройки времени уведомления и данные для уведомления
        initNotify(context);// настраиваем и устанавливаем уведомление
    }

    private void initNotify(Context context) {
        Intent intentNotify = new Intent(context, SettingActivity.class);
        intentNotify.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID); // Будем открывать актвити настройки виджета, в нём записан полезный текст

        PendingIntent pIntent = PendingIntent.getActivity(context, widgetID, intentNotify, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builderNotify = new NotificationCompat.Builder(context);
        //настраиваем уведомление
        builderNotify.setContentIntent(pIntent);
        builderNotify.setTicker(title);
        builderNotify.setContentTitle(title);
        builderNotify.setContentText(text);
        builderNotify.setWhen(System.currentTimeMillis());
        builderNotify.setAutoCancel(true);
        builderNotify.setSmallIcon(R.drawable.ic_stat_notufy_list);
        builderNotify.setDefaults(Notification.DEFAULT_LIGHTS|Notification.DEFAULT_VIBRATE);
        //Uri ringUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

        Uri ringUri = Uri.parse(stringSoundUri);
        builderNotify.setSound(ringUri);


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            notification = builderNotify.getNotification();
        } else notification = builderNotify.build();

        nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(widgetID, notification);

    }

    /*
    private void getSettings() { // считываем настройки виджета и оповещения
        SharedPreferences sh = context.getSharedPreferences(ControlListener.WIDGET_PREF, Context.MODE_PRIVATE);
        ticker = sh.getString(ControlListener.NOTIFY_TITLE + widgetID, context.getString(R.string.notify_title));
        title = sh.getString(ControlListener.NOTIFY_TITLE + widgetID, context.getString(R.string.notify_title));
        text = sh.getString(ControlListener.WIDGET_NOTES + widgetID, context.getString(R.string.notify_text));
    }
    */

}
