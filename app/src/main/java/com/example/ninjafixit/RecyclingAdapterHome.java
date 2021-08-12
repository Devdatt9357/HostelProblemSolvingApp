package com.example.ninjafixit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class RecyclingAdapterHome extends RecyclerView.Adapter<RecyclingAdapterHome.ViewHolder> {
    private Context mContext;
    private ArrayList<Messages> messageList;
    private RecyclingAdapterHome.RecycleClick listner;

    public RecyclingAdapterHome(Context mContext, ArrayList<Messages> messageList, RecyclingAdapterHome.RecycleClick listner) {
        this.mContext = mContext;
        this.messageList = messageList;
        this.listner=listner;
    }

    @NonNull
    @Override
    public RecyclingAdapterHome.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_item_home,parent,false);
        return new RecyclingAdapterHome.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclingAdapterHome.ViewHolder holder, int position) {
        holder.textview.setText(messageList.get(position).getHeadname());
        Glide.with(mContext).load(messageList.get(position).getHeadUrl()).into(holder.imageview);

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView imageview;
        TextView textview;
        CardView parent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageview=itemView.findViewById(R.id.imageView52);
            textview=itemView.findViewById(R.id.textView81);
            parent=itemView.findViewById(R.id.card_home);

            parent.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listner.onClick(v,getAdapterPosition());
        }
    }
    public interface RecycleClick{
        void onClick(View v,int position);
    }


}
