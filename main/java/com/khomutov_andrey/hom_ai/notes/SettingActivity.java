/*
настройка виджета
 */
package com.khomutov_andrey.hom_ai.notes;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

public class SettingActivity extends AppCompatActivity {

    int widgetID= AppWidgetManager.INVALID_APPWIDGET_ID;
    Intent resultValue;
    private Control control;

    private long backPressed; // Для обработки двойного нажатия кнопки "назад"

    EditText editTextNotes;
    RecyclerView stickyViews;

    private Tracker mTracker; // for GoogleAnalytics


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_setting);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();//для получения через интент ID виджета
        Bundle extras=intent.getExtras();
        if(extras!=null){
            widgetID=extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        //проверяем корректность widgetID
        if(widgetID==AppWidgetManager.INVALID_APPWIDGET_ID){
            finish();
        }

        //формируем intent ответа
        resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
        //формируем отрицательный ответ, если пользователь сразу отменит, то виджет не создаём
        setResult(RESULT_CANCELED, resultValue);

        control = new Control(this);

        editTextNotes = (EditText)findViewById(R.id.editTextNotes);
        editTextNotes.setText(control.getTextWidget(widgetID));
        editTextNotes.addTextChangedListener(watcher);
        int resId = control.getBackgraundId(widgetID);
        if(resId>0){
            editTextNotes.setBackgroundResource(resId);
        }

        stickyViews = (RecyclerView)findViewById(R.id.backgraundList);
        //stickyViews.setHasFixedSize(true);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        stickyViews.setLayoutManager(lm);
        ArrayList<String> list = control.getBackgraundId();
        AdapterItem adapter = new AdapterItem(this, list);
        adapter.setClickListener(clickListener);
        stickyViews.setAdapter(adapter);

        //TODO:Убрать, подключить FireBase
        // for googleAnalytics
        AnalyticsApplication application = (AnalyticsApplication)getApplication();
        mTracker = application.getDefaultTracker();
    }


    @Override
    protected void onResume() {
        super.onResume();
        //TODO: Убрать, подключить FireBase
        // for googleAnalytics
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //return super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.action_notify_setting:
                notificationSettings();
                break;
            case R.id.action_save:
                control.onchangeText(widgetID, editTextNotes.getText().toString());
                //обновить виджет
                control.upDateWidget(widgetID);
                setResult(RESULT_OK, resultValue);
                finish();
                break;
        }
        return true;
    }


    @Override
    public void onBackPressed() {
        if(backPressed+2000 > System.currentTimeMillis()){
            control.onchangeText(widgetID, editTextNotes.getText().toString());
            //обновить виджет
            control.upDateWidget(widgetID);
            finish();
        }
        else{
            Toast.makeText(getBaseContext(),R.string.ntf_back_pressed,Toast.LENGTH_SHORT).show();
        }
        backPressed = System.currentTimeMillis();
    }

    public void notificationSettings(){
        Intent intent = new Intent(getApplicationContext(), NotificationSetting.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
        startActivity(intent);
    }

    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
        @Override
        public void afterTextChanged(Editable s) {
            control.onchangeText(widgetID, s.toString());
        }
    };

    AdapterItem.ItemClickListener clickListener = new AdapterItem.ItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            AdapterItem adapter = (AdapterItem) stickyViews.getAdapter();
            int resId = adapter.getRes(position);
            editTextNotes.setBackgroundResource(resId);
            control.saveBackgraundId(widgetID, resId);
        }
    };

}
