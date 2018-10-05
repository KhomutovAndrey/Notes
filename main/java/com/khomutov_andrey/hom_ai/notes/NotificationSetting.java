/*
Activity настроек уведомления
 */
package com.khomutov_andrey.hom_ai.notes;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.appwidget.AppWidgetManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class NotificationSetting extends AppCompatActivity {
    //private static final int NOTYFY_ID=101;
    int DIALOG_TIME=1, DIALOG_DATE=2;
    int hours=1, minutes=1, years=1, mouns=1, days=1;
    long iTime;
    String title;
    String text;
    Boolean notify_on;
    Uri notify_sound_uri=null;
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    SimpleDateFormat sdf2 = new SimpleDateFormat("dd:MM:yyyy");
    EditText etTime, etDate, etTitle;
    RecyclerView recViewSound;
    SwitchCompat sw;
    Control control;
    int widgetID = AppWidgetManager.INVALID_APPWIDGET_ID;
    HashMap<String, Object> params;
    private Tracker mTracker; // for GoogleAnalytics


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_setting);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //TODO: Убрать, подключить аналитику FireBase
        //получаем экземпляр трекера для GoogleAnalytics
        AnalyticsApplication application = (AnalyticsApplication)getApplication();
        mTracker = application.getDefaultTracker();

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras!=null){
            widgetID=extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }


        etTime = (EditText)findViewById(R.id.etTime);
        etDate = (EditText)findViewById(R.id.etDate);
        sw = (SwitchCompat)findViewById(R.id.switch1);
        etTitle = (EditText)findViewById(R.id.etTitle);

        etTime.setKeyListener(null);
        etTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_TIME);
            }
        });

        etDate.setKeyListener(null);
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_DATE);
            }
        });

        control = new Control(this);

        //уведомление
        /*
        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettingNotification();
                sendAlarm();
                NotificationSetting.this.finish();
            }
        });
        */
        recViewSound = (RecyclerView)findViewById(R.id.rvSound);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recViewSound.setLayoutManager(lm);
        ArrayList<Uri> soundList = getSoundList();
        AdapterItemSound adapterSound = new AdapterItemSound(this,soundList);
        adapterSound.setPlayListener(control);
        adapterSound.setCheckListener(checkItem);
        recViewSound.setAdapter(adapterSound);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //получаем сохранённые значения, что бы выставить их на экране
        params = control.getNotifyParam(widgetID);
        notify_on = (boolean)params.get(ControlListener.NOTIFY_ON);
        title = (String)params.get(ControlListener.NOTIFY_TITLE);
        iTime = (long)params.get(ControlListener.NOTIFY_TIME);
        String soundParam = (String)params.get(ControlListener.NOTIFY_SOUND_URI);
        notify_sound_uri = Uri.parse(soundParam);
        //Log.d("alarmNotify", "onResume: "+soundParam);
        //устанавливаем значения вьюшек
        if(notify_on){
            sw.setChecked(notify_on);
            etTitle.setText(title);
            etTime.setText(sdf.format(iTime));
            etDate.setText(sdf2.format(iTime));
        }else{
            sw.setChecked(notify_on);
        }

        //TODO: Убрать, подключить аналитику FireBase
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    protected void onPause() {
        super.onPause();
        control.playerRelese();
    }

    @Override
    protected void onStop() {
        super.onStop();
        control.playerRelese();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        control.playerRelese();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        menu.getItem(1).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_save: {
                saveSettingNotification();
                sendAlarm();
                NotificationSetting.this.finish();
            }
        }
        return true;
    }

    private void saveSettingNotification(){
        params = new HashMap<String, Object>();
        notify_on = sw.isChecked();
        title = etTitle.getText().toString();
        params.put(ControlListener.NOTIFY_ON, notify_on);
        params.put(ControlListener.NOTIFY_TITLE, title);
        params.put(ControlListener.NOTIFY_TIME, iTime);
        params.put(ControlListener.NOTIFY_SOUND_URI, notify_sound_uri.toString());
        //Log.d("alarmNotify", "Settinds-notify_sound_uri.toString(): "+notify_sound_uri.toString());
        control.saveParamNotify(widgetID, params);
        //this.finish();
    }

    public void sendAlarm(){
        text = control.getTextWidget(widgetID);
        params = control.getNotifyParam(widgetID);
        params.put(ControlListener.WIDGET_NOTES, text);
        // Настраиваем уведомление и устанавливаем расписание срабатывания
        control.setAlarm(widgetID, params);
    }

    public AdapterItemSound.ItemCeck checkItem = new AdapterItemSound.ItemCeck() {
        @Override
        public void onChecked(int position, Uri uri) {
            notify_sound_uri = uri;
            //Log.d("alarmNotify", "check-notify_sound_uri.toString(): "+notify_sound_uri.toString());
            control.saveSoundUri(widgetID, notify_sound_uri.toString());
        }
    };

    @Override
    protected Dialog onCreateDialog(int id) {
        if(id==DIALOG_TIME){
            Calendar calendar = Calendar.getInstance();
            hours = calendar.get(Calendar.HOUR_OF_DAY);//date.getHours();
            minutes = calendar.get(Calendar.MINUTE);//date.getMinutes();
            TimePickerDialog tpd = new TimePickerDialog(this, setTime, hours, minutes,true);
            return tpd;
        }
        if(id==DIALOG_DATE){
            Calendar calendar = Calendar.getInstance();
            years = calendar.get(Calendar.YEAR); //date.getYear();
            mouns = calendar.get(Calendar.MONTH); //date.getMonth();
            days = calendar.get(Calendar.DAY_OF_MONTH); //date.getDate();
            DatePickerDialog dpd = new DatePickerDialog(this,setDate,years, mouns, days);
            return dpd;
        }
        return super.onCreateDialog(id);
    }

    TimePickerDialog.OnTimeSetListener setTime = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            hours=hourOfDay;
            minutes=minute;
            Date date = new Date(iTime);
            date.setHours(hours);
            date.setMinutes(minutes);
            iTime=date.getTime();
            etTime.setText(sdf.format(iTime));
            etDate.setText(sdf2.format(iTime));
        }
    };

    DatePickerDialog.OnDateSetListener setDate = new DatePickerDialog.OnDateSetListener(){
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            years = year;
            mouns = monthOfYear;
            days = dayOfMonth;
            Date date = new Date(iTime);
            date.setYear(years-1900);
            date.setMonth(mouns);
            date.setDate(days);
            date.setHours(hours);
            date.setMinutes(minutes);
            date.setSeconds(0);
            iTime=date.getTime();
            etDate.setText(sdf2.format(iTime));
        }
    };



    //TODO: Вывести в control
    private ArrayList<Uri> getSoundList(){
        ArrayList<Uri> list = new ArrayList<Uri>();
        int resId = R.raw.r1;
        Resources resources = getResources();
        Uri ringUri = new Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(resources.getResourcePackageName(resId))
                .appendPath(resources.getResourceTypeName(resId))
                .appendPath(resources.getResourceEntryName(resId))
                .build();
        list.add(ringUri);

        //TODO: попробовать менять только .appendPath(resources.getResourceEntryName(resId))
        resId = R.raw.r2;
        ringUri = new Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(resources.getResourcePackageName(resId))
                .appendPath(resources.getResourceTypeName(resId))
                .appendPath(resources.getResourceEntryName(resId))
                .build();
        list.add(ringUri);
        resId = R.raw.r3;
        ringUri = new Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(resources.getResourcePackageName(resId))
                .appendPath(resources.getResourceTypeName(resId))
                .appendPath(resources.getResourceEntryName(resId))
                .build();
        list.add(ringUri);
        resId = R.raw.r4;
        ringUri = new Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(resources.getResourcePackageName(resId))
                .appendPath(resources.getResourceTypeName(resId))
                .appendPath(resources.getResourceEntryName(resId))
                .build();
        list.add(ringUri);
        resId = R.raw.r5;
        ringUri = new Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(resources.getResourcePackageName(resId))
                .appendPath(resources.getResourceTypeName(resId))
                .appendPath(resources.getResourceEntryName(resId))
                .build();
        list.add(ringUri);
        resId = R.raw.r6;
        ringUri = new Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(resources.getResourcePackageName(resId))
                .appendPath(resources.getResourceTypeName(resId))
                .appendPath(resources.getResourceEntryName(resId))
                .build();
        list.add(ringUri);
        return list;
    }


}
