package com.khomutov_andrey.hom_ai.notes;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;


public class Receiver extends BroadcastReceiver {
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        setNotification(intent);
    }

    private void setNotification (Intent intent){
        int widgetID = AppWidgetManager.INVALID_APPWIDGET_ID;
        Bundle extras = intent.getExtras();
        if(extras!=null){
            widgetID=extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        // получаем параметры уведомления
        SharedPreferences sh = context.getSharedPreferences(ControlListener.WIDGET_PREF, Context.MODE_PRIVATE);
        String ticker = sh.getString(ControlListener.NOTIFY_TITLE + widgetID, context.getString(R.string.notify_title));
        String title = sh.getString(ControlListener.NOTIFY_TITLE+widgetID, context.getString(R.string.notify_title));
        String text = sh.getString(ControlListener.WIDGET_NOTES + widgetID, context.getString(R.string.notify_text));
        //проверить на настройку напоминания (вкл/выкл)
        long iTime = sh.getLong(ControlListener.NOTIFY_TIME + widgetID,1);// Время, когда отобразить напоминание

        // Настраиваем уведомление и устанавливаем расписание срабатывания
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, AlarmNotifyReceiver.class); // intent для будильника (который выбросит уведомление)
        alarmIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);// передаём widgetID
        alarmIntent.putExtra("ticker",ticker);
        alarmIntent.putExtra("title", title);
        alarmIntent.putExtra("text", text);

        PendingIntent pIntent = PendingIntent.getBroadcast(context,widgetID,alarmIntent,PendingIntent.FLAG_CANCEL_CURRENT);
        am.cancel(pIntent);// Отменяем, если такой уже установлен
        am.set(AlarmManager.RTC_WAKEUP,iTime,pIntent);//
    }
}
