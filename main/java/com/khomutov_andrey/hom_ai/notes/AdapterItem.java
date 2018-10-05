package com.khomutov_andrey.hom_ai.notes;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * Created by hom-ai on 21.12.2017.
 */

public class AdapterItem extends RecyclerView.Adapter<AdapterItem.ViewHolder> {
    //private LayoutInflater inflater;//???
    private ArrayList<String> items;
    private static ItemClickListener mClickListener;

    public interface ItemClickListener{
        void onItemClick(View view, int position);
    }

    AdapterItem(Context context, ArrayList<String> items){
        this.items = items;
        //this.inflater = LayoutInflater.from(context);//???
    }

    @Override
    public AdapterItem.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final AdapterItem.ViewHolder holder, int position) {
        int id = Integer.parseInt(items.get(position));
        holder.imageView.setImageResource(id);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setClickListener(ItemClickListener itemClickListener){
        this.mClickListener = itemClickListener;
    }

    public int getRes(int position){
        return Integer.parseInt(items.get(position));
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView imageView;
        //public TextView mTextView;
        public LinearLayout layout;
        //private Object mClickListener;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView)itemView.findViewById(R.id.imageView);
            layout = (LinearLayout) itemView.findViewById(R.id.layout);
            itemView.setOnClickListener(this);
            //mTextView = (TextView) v.findViewById(R.id.textView);
        }

        @Override
        public void onClick(View v) {
            if(mClickListener !=null){
                mClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }

}
