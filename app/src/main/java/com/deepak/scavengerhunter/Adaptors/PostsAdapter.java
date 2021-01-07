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
import com.deepak.scavengerhunter.Modals.PostsModal;
import com.deepak.scavengerhunter.R;
import com.deepak.scavengerhunter.activites.ViewSingleHuntActivity;

import java.util.ArrayList;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private ArrayList<PostsModal> postsList;
    private LayoutInflater mInflater;
    public Context context;


    // data is passed into the constructor
    public PostsAdapter(Context context, ArrayList<PostsModal> postsList) {
        this.mInflater = LayoutInflater.from(context);
        this.postsList = postsList;
        this.context = context;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.post_item_layout, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final PostsModal postsModal = postsList.get(position);
        holder.tv_post_name.setText(postsModal.getPost_name());
        holder.tv_post_address.setText(postsModal.getAddress());





    }

    // total number of rows
    @Override
    public int getItemCount() {
        return postsList.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_post_name;
        TextView tv_post_address;



        ViewHolder(View itemView) {
            super(itemView);
            tv_post_name = itemView.findViewById(R.id.tv_post_name);
            tv_post_address = itemView.findViewById(R.id.tv_address);


        }


    }




}
