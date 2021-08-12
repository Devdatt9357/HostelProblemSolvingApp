package com.example.ninjafixit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CheckAdapter extends RecyclerView.Adapter<CheckAdapter.MyHolder> {

    Context mContext;
    ArrayList<Messages> checkList;
    ArrayList<Messages> checkedList=new ArrayList<>();

    public CheckAdapter(Context mContext, ArrayList<Messages> checkList) {
        this.mContext = mContext;
        this.checkList = checkList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_list_item2,parent,false);
        MyHolder holder=new MyHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, final int position) {
        holder.description.setText(checkList.get(position).getCheckItem());

        holder.SetItemClickListner(new ItemClickListener() {
            @Override
            public void OnItemClick(View v, int Position) {
                CheckBox chk=(CheckBox) v;
                if(chk.isChecked()){
                    checkedList.add(checkList.get(Position));
                }
                else if(!chk.isChecked()){
                    checkedList.remove(checkList.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return checkList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView description;
        CheckBox chk;

        ItemClickListener itemClickListner;
        public MyHolder(@NonNull View itemView) {
            super(itemView);

            description=itemView.findViewById(R.id.textView79);
            chk=itemView.findViewById(R.id.checkBox);

            chk.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
           this.itemClickListner.OnItemClick(v,getLayoutPosition());
        }

        public void SetItemClickListner(ItemClickListener ic){
            this.itemClickListner=ic;
        }
    }

    public interface ItemClickListener {

        void OnItemClick(View v, int Position);
    }

}
