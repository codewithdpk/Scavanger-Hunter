package com.deepak.scavengerhunter.Adaptors;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.deepak.scavengerhunter.Modals.HuntModal;
import com.deepak.scavengerhunter.R;
import com.deepak.scavengerhunter.activites.ViewSingleHuntActivity;



import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MyHuntsAdaptor extends RecyclerView.Adapter<MyHuntsAdaptor.ViewHolder> {

    private ArrayList<HuntModal> hunts;
    private LayoutInflater mInflater;
    public Context context;


    // data is passed into the constructor
    public MyHuntsAdaptor(Context context, ArrayList<HuntModal> hunts) {
        this.mInflater = LayoutInflater.from(context);
        this.hunts = hunts;
        this.context = context;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.a_hunt_layout_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final HuntModal huntModal = hunts.get(position);
        holder.tv_huntname.setText(huntModal.getName());
        holder.tv_posts.setText(huntModal.getPosts().length()+" Posts");
        holder.tv_startingPointName.setText(huntModal.getStartingArea());
        holder.tv_startingPointAddress.setText(huntModal.getCompleteStartingAddress());
        if(huntModal.getEndingArea().equals("none")){
            holder.tv_endingPointName.setText("Pending");
            holder.tv_endingPointAddress.setText("Pending");
        }else{
            holder.tv_endingPointName.setText(huntModal.getEndingArea());
            holder.tv_endingPointAddress.setText(huntModal.getEndingStartingAddress());
        }
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, ViewSingleHuntActivity.class);
                intent.putExtra("hunt_id",huntModal.getHunt_id());
                context.startActivity(intent);
            }
        });




    }

    // total number of rows
    @Override
    public int getItemCount() {
        return hunts.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_huntname;
        TextView tv_posts;
        TextView tv_startingPointName;
        TextView tv_startingPointAddress;
        TextView tv_endingPointName;
        TextView tv_endingPointAddress;
        CardView card;

        ViewHolder(View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card);
            tv_huntname = itemView.findViewById(R.id.tv_name_of_hunt);
            tv_posts = itemView.findViewById(R.id.tv_post_number);
            tv_startingPointName = itemView.findViewById(R.id.tv_starting_point_name);
            tv_startingPointAddress = itemView.findViewById(R.id.tv_starting_point_address);
            tv_endingPointName = itemView.findViewById(R.id.tv_ending_point_name);
            tv_endingPointAddress = itemView.findViewById(R.id.tv_ending_point_address);

        }


    }




}
