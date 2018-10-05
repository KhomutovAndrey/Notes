package com.khomutov_andrey.hom_ai.notes;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by hom-ai on 22.12.2017.
 */

public class AdapterListView extends ArrayAdapter<String> {
    private final Activity context;
    private final ArrayList<String> values;

    public AdapterListView(Activity context, ArrayList<String> values) {
        super(context, R.layout.list_item, values);
        this.context = context;
        this.values = values;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if(convertView==null){
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.list_item,null,true);
            //Log.d("background_List", values.toString());
            //Log.d("background_List", String.valueOf(R.drawable.stick1)+":"+String.valueOf(R.drawable.stick2)+":"+String.valueOf(R.drawable.stick3)+":"+String.valueOf(R.drawable.stick4)+":"+String.valueOf(R.drawable.stick5));
            //((ImageView)rowView.findViewById(R.id.imageView)).setImageResource(Integer.getInteger(values.get(position)));

        }
        return rowView;
    }
}
