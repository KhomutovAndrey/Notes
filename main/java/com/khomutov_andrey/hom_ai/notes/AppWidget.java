package com.khomutov_andrey.hom_ai.notes;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

/**
 * Created by hom-ai on 18.01.2016.
 */
public class AppWidget extends AppWidgetProvider{

    final static String ACTION_CHANGE="com.khomutov_andrey.hom_ai.notes";

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        SharedPreferences.Editor editor=context.getSharedPreferences(ControlListener.WIDGET_PREF,Context.MODE_PRIVATE).edit();
        for(int widgetId:appWidgetIds){
            editor.remove(ControlListener.WIDGET_NOTES+widgetId);
        }
        editor.commit();
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        SharedPreferences sp = context.getSharedPreferences(ControlListener.WIDGET_PREF, Context.MODE_PRIVATE);

        for(int id: appWidgetIds){
            updateWidget(context, appWidgetManager, id);
            boolean notify_on = sp.getBoolean(ControlListener.NOTIFY_ON + id, false);
            long notify_time = sp.getLong(ControlListener.NOTIFY_TIME + id, 0);
            if(notify_on && notify_time>0) {
                setAlarm(context, id, notify_time);
            }
        }
    }

    static void updateWidget(Context context, AppWidgetManager appWidgetManager, int widgetId){
        //получаем текст заметки, сохранённый в преференс
        Control control = new Control(context);
        String notesText= control.getTextWidget(widgetId);
        int resId = control.getBackgraundId(widgetId);
        //String notesText = sp.getString(ControlListener.WIDGET_NOTES+widgetId,null);
        if(notesText==null)return;
        //настраиваем внешний вид виджета
        RemoteViews widgetView = new RemoteViews(context.getPackageName(), R.layout.app_widget);
        widgetView.setTextViewText(R.id.textNotes, notesText);
        if(resId>0) {
            widgetView.setInt(R.id.textNotes, "setBackgroundResource", resId); //работает
        }

        //Для открытия конфигурационного окна
        Intent configIntent = new Intent(context, SettingActivity.class);
        configIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
        configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        PendingIntent pIntent = PendingIntent.getActivity(context,widgetId,configIntent,0);
        widgetView.setOnClickPendingIntent(R.id.textNotes, pIntent);

        appWidgetManager.updateAppWidget(widgetId,widgetView);
    }

    private void setAlarm(Context context, int widget_id, long notify_time){
        // интент для установки будильника для виджета
        // Настраиваем уведомление и устанавливаем расписание срабатывания
        Intent intent = new Intent(context, AlarmNotifyReceiver.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widget_id);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, widget_id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(pIntent);// Отменяем, если такой уже установлен
        am.set(AlarmManager.RTC_WAKEUP, notify_time, pIntent);//
    }

}
