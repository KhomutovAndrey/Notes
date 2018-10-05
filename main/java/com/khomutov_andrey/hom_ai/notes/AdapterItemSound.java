package com.khomutov_andrey.hom_ai.notes;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;

/**
 * Created by hom-ai on 27.12.2017.
 */

public class AdapterItemSound extends RecyclerView.Adapter<AdapterItemSound.ViewHolder> {
    Context mContext;
    ArrayList<Uri> arrayUri;
    private ItemPlay mPlayListener;
    private ItemCeck mCheckListener;
    private View checkedView; //TODO: Убрать, когда реализую переключение через ViewHolder
    private ViewHolder vhPref;

    public interface ItemPlay {
        void onPlay(View view, Uri uri, boolean play);// Запускает/останавливает воспроизведение  звука
        //void onChecked(int position, Uri uri);// Сохраняет выбранный звуковой ресурс в настройки
    }

    public interface ItemCeck{
        void onChecked(int position, Uri uri);// Сохраняет выбранный звуковой ресурс в настройки
    }

    public AdapterItemSound(Context context, ArrayList<Uri> arrayUri) {
        this.mContext = context;
        this.arrayUri = arrayUri;
        //Log.d("HolderLog", "AdapterItemSound");
    }

    public void setPlayListener(ItemPlay playListener){
        this.mPlayListener = playListener;
    }

    public void setCheckListener(ItemCeck checkListener){
        this.mCheckListener = checkListener;
    }

    @Override
    public AdapterItemSound.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Log.d("HolderLog", "onCreate");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sound_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AdapterItemSound.ViewHolder holder, int position) {
        //Log.d("HolderLog", "onBind");

        holder.tvSoundTitle.setText("sound"+String.valueOf(position+1));
    }

    @Override
    public int getItemCount() {
        return arrayUri.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ToggleButton tbSound;
        public TextView tvSoundTitle;
        private ViewHolder instance;

        public ViewHolder(View itemView) {
            super(itemView);
            instance = this;
            //Log.d("HolderLog", "ViewHolder");

            tbSound = (ToggleButton) itemView.findViewById(R.id.tbSound);
            tvSoundTitle = (TextView) itemView.findViewById(R.id.tvSoundTitle);
            //tvSoundTitle.setText("sound_"+String.valueOf(getAdapterPosition()));
            tvSoundTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Log.d("HolderLog", v.getClass().getSimpleName());
                    if(mPlayListener !=null){
                        if(vhPref!=null){
                            vhPref.tvSoundTitle.setTextColor(v.getResources().getColor(android.R.color.darker_gray));
                            vhPref.tbSound.setChecked(false);
                            //((TextView)checkedView).setTextColor(v.getResources().getColor(android.R.color.darker_gray));
                        }
                        //checkedView = v;
                        vhPref = instance;
                        //((TextView)v).setTextColor(v.getResources().getColor(R.color.colorAccent));
                        instance.tvSoundTitle.setTextColor(v.getResources().getColor(R.color.colorAccent));
                        instance.tbSound.setChecked(true);
                        Uri uri = arrayUri.get(getAdapterPosition());
                        boolean flag = instance.tbSound.isChecked();
                        mPlayListener.onPlay(v, uri, flag);
                        if(mCheckListener!=null){
                            mCheckListener.onChecked(getAdapterPosition(), uri);
                        }

                    }
                }
            });
        }

    }

}
