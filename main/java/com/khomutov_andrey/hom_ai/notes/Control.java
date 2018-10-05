package com.khomutov_andrey.hom_ai.notes;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by hom-ai on 13.12.2017.
 */

public class Control implements ControlListener, AdapterItemSound.ItemPlay {

    private Context mContext;
    private SharedPreferences mPreferances; //Настройки хрянящие данные виджета (текст и тп.) по его номеру.
    private MediaPlayer player=null;


    public Control(Context context) {
        mContext = context;
        mPreferances = mContext.getSharedPreferences(WIDGET_PREF, Context.MODE_PRIVATE);
    }

    private SharedPreferences getPreferances(){
        if (mPreferances == null) {
            mPreferances = mContext.getSharedPreferences(WIDGET_PREF, Context.MODE_PRIVATE);
        }
        return mPreferances;
    }

    @Override
    public String getTextWidget(int widgetId) {
        String text = "";
        if (mPreferances == null) {
            mPreferances = mContext.getSharedPreferences(WIDGET_PREF, Context.MODE_PRIVATE);
        }
        text = mPreferances.getString(WIDGET_NOTES + widgetId, mContext.getString(R.string.settings_header));
        return text;
    }

    @Override
    public int getBackgraundId(int widgetId) {
        if (mPreferances == null) {
            mPreferances = mContext.getSharedPreferences(WIDGET_PREF, Context.MODE_PRIVATE);
        }
        int id = mPreferances.getInt(WIDGET_BACKGRAUND+widgetId, 0);
        return id;
    }

    @Override
    public HashMap<String, Object> getNotifyParam(int widgetID) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        if (mPreferances == null) {
            mPreferances = mContext.getSharedPreferences(WIDGET_PREF, Context.MODE_PRIVATE);
        }
        Resources resources = mContext.getResources();
        //int resId = R.raw.r1;
        Uri defSound = new Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(resources.getResourcePackageName(R.raw.r1))
                .appendPath(resources.getResourceTypeName(R.raw.r1))
                .appendPath(resources.getResourceEntryName(R.raw.r1))
                .build();

        boolean notify_on = mPreferances.getBoolean(ControlListener.NOTIFY_ON + widgetID, false);
        String title = mPreferances.getString(ControlListener.NOTIFY_TITLE + widgetID, "");
        long iTime = mPreferances.getLong(ControlListener.NOTIFY_TIME + widgetID, System.currentTimeMillis());
        String notify_sound = mPreferances.getString(ControlListener.NOTIFY_SOUND_URI + widgetID, defSound.toString());


        params.put(ControlListener.NOTIFY_ON, notify_on);
        params.put(ControlListener.NOTIFY_TITLE, title);
        params.put(ControlListener.NOTIFY_TIME, iTime);
        params.put(ControlListener.NOTIFY_SOUND_URI, notify_sound);
        return params;
    }

    @Override
    public void saveParamNotify(int widgetID, Map<String, Object> params) {
        if (mPreferances == null) {
            mPreferances = mContext.getSharedPreferences(WIDGET_PREF, Context.MODE_PRIVATE);
        }
        SharedPreferences.Editor editor = mPreferances.edit();
        editor.putBoolean(ControlListener.NOTIFY_ON + widgetID, (boolean) params.get(ControlListener.NOTIFY_ON));
        editor.putString(ControlListener.NOTIFY_TITLE + widgetID, (String) params.get(ControlListener.NOTIFY_TITLE));
        editor.putLong(ControlListener.NOTIFY_TIME + widgetID, (long) params.get(ControlListener.NOTIFY_TIME));
        editor.putString(ControlListener.NOTIFY_SOUND_URI+widgetID, (String)params.get(ControlListener.NOTIFY_SOUND_URI));
        Log.d("alarmNotify", "control.saveParamNotify: "+(String)params.get(ControlListener.NOTIFY_SOUND_URI));
        editor.commit();
    }

    @Override
    public void onchangeText(int widgetID, String text) {
        if (mPreferances == null) {
            mPreferances = mContext.getSharedPreferences(WIDGET_PREF, Context.MODE_PRIVATE);
        }
        SharedPreferences.Editor editor = mPreferances.edit();
        editor.putString(WIDGET_NOTES + widgetID, text);
        editor.commit();
    }

    @Override
    public void saveBackgraundId(int widgetId, int resId) {
        if (mPreferances == null) {
            mPreferances = mContext.getSharedPreferences(WIDGET_PREF, Context.MODE_PRIVATE);
        }
        SharedPreferences.Editor editor = mPreferances.edit();
        editor.putInt(WIDGET_BACKGRAUND + widgetId, resId);
        editor.commit();
    }

    @Override
    public void save(int widgetID, Map<String, Object> params) {
        saveParamNotify(widgetID, params);
        //Обновляем виджет на экране
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
        AppWidget.updateWidget(mContext, appWidgetManager, widgetID);
    }

    @Override
    public void save(int widgetId, String text) {
        onchangeText(widgetId, text);
        upDateWidget(widgetId);
    }

    @Override
    public void saveSoundUri(int widgetId, String stringUri) {
        if (mPreferances == null) {
            mPreferances = mContext.getSharedPreferences(WIDGET_PREF, Context.MODE_PRIVATE);
        }
        SharedPreferences.Editor editor = mPreferances.edit();
        editor.putString(NOTIFY_SOUND_URI + widgetId, stringUri);
        Log.d("alarmNotify", "contro.saveSoundUri: "+stringUri);
        editor.commit();
    }

    @Override
    public void upDateWidget(int widgetID) {
        //Обновляем виджет на экране
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
        AppWidget.updateWidget(mContext, appWidgetManager, widgetID);
    }

    @Override
    public void setAlarm(int widgetID, Map<String, Object> paramNotify) {
        boolean notify_on = (boolean) paramNotify.get(NOTIFY_ON);
        String title = (String) paramNotify.get(NOTIFY_TITLE);
        String text = (String) paramNotify.get(WIDGET_NOTES);
        long iTime = (long) paramNotify.get(NOTIFY_TIME);
        // Настраиваем уведомление и устанавливаем расписание срабатывания
        AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(mContext, AlarmNotifyReceiver.class); // intent для будильника (который выбросит уведомление)
        alarmIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);// передаём widgetID
        alarmIntent.putExtra("ticker", title);
        alarmIntent.putExtra("title", title);
        alarmIntent.putExtra("text", text);

        PendingIntent pIntent = PendingIntent.getBroadcast(mContext, widgetID, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        am.cancel(pIntent);// Отменяем, если такой уже установлен
        if (notify_on) {
            am.set(AlarmManager.RTC_WAKEUP, iTime, pIntent);//
        } else { // удалить оповещение
            NotificationManager nm = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
            nm.cancel(widgetID);
        }
    }

    public ArrayList<String> getBackgraundId() {
        //Log.d("background_List", "start");
        ArrayList<String> list = new ArrayList<>();
        XmlPullParser parser = mContext.getResources().getXml(R.xml.backgraund);
        String name;
        int id;
        try {
            while (parser.getEventType()!=XmlPullParser.END_DOCUMENT){
                if(parser.getEventType()==XmlPullParser.START_TAG && parser.getName().equals("item")){
                    name = parser.getAttributeValue(0);
                    id = mContext.getResources().getIdentifier(name, "drawable", mContext.getPackageName());
                    list.add(String.valueOf(id));
                }
                parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Log.d("background_List", list.toString());
        return list;
    }


    @Override
    public void onPlay(View view, Uri uri, boolean play) {
        playerRelese();
        player = new MediaPlayer();
        try {
            player.setDataSource(mContext, uri);
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void playerRelese(){
        if(player!=null){
            player.release();
            player=null;
        }
    }

}
