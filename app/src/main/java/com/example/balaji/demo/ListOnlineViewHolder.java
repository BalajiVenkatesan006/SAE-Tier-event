package com.example.balaji.demo;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class ListOnlineViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtEmail;
    ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public ListOnlineViewHolder(View itemView){
        super(itemView);
        txtEmail=(TextView)itemView.findViewById(R.id.txt_email);
        txtEmail.setOnClickListener(this);
    }

    public ItemClickListener getItemClickListener() {
        return itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition());
    }
}

