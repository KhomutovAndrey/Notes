package com.khomutov_andrey.hom_ai.notes;

import android.appwidget.AppWidgetManager;

import java.util.Map;

/**
 * Created by hom-ai on 13.12.2017.
 */

public interface ControlListener {
    public final static String WIDGET_PREF="widget_pref";// тэг файла настроек
    public final static String WIDGET_NOTES="widget_notes";// ключ для хранения текста виджета
    public final static String WIDGET_BACKGRAUND="widget_backgraund";//для хранения изображения виджета
    public final static String NOTIFY_TITLE="notify_title";
    public final static String NOTIFY_ON="notify_on";
    public final static String NOTIFY_TIME="notify_time";
    public final static String NOTIFY_DATE="notify_date";
    public final static String NOTIFY_SOUND_URI="notify_sound_uri";
    int widgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    public String getTextWidget(int widgetId);
    public Map<String,Object> getNotifyParam(int widgetID);
    public void saveParamNotify(int widgetID, Map<String,Object> params);
    public void onchangeText(int widgetId, String text);
    public void saveSoundUri(int widgetId, String stringUri);
    public void save(int widgetId, Map<String, Object> params);
    public void save(int widgetId, String text);
    public void upDateWidget(int widgetId);
    public void setAlarm(int widgetID, Map<String, Object> paramNityfy);
    public void saveBackgraundId(int widgetId, int resId);
    public int getBackgraundId(int widgetId);
}
