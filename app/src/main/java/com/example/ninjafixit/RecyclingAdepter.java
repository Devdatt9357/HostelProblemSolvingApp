package com.example.ninjafixit;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class RecyclingAdepter extends RecyclerView.Adapter<RecyclingAdepter.ViewHolder> {
    private Context mContext;
    private ArrayList<Messages> messageList;
    private RecycleClick listner;

    public RecyclingAdepter(Context mContext, ArrayList<Messages> messageList,RecycleClick listner) {
        this.mContext = mContext;
        this.messageList = messageList;
        this.listner=listner;
    }

    @NonNull
    @Override
    public RecyclingAdepter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclingAdepter.ViewHolder holder, int position) {
        holder.textview.setText(messageList.get(position).getName());
        holder.description.setText(messageList.get(position).getDescription());
        Glide.with(mContext).load(messageList.get(position).getImageUrl()).into(holder.imageview);

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView imageview;
        TextView textview;
        TextView description;
        CardView parent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageview=itemView.findViewById(R.id.imageView58);
            textview=itemView.findViewById(R.id.textView77);
            description=itemView.findViewById(R.id.textView80);
            parent=itemView.findViewById(R.id.recycle_card);

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
