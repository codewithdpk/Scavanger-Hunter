package com.deepak.scavengerhunter.Adaptors;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.deepak.scavengerhunter.Modals.PostsModal;
import com.deepak.scavengerhunter.R;

import java.util.ArrayList;

public class PostsBlueAdapter  extends RecyclerView.Adapter<PostsBlueAdapter.ViewHolder> {

    private final ArrayList<PostsModal> postsList;
    private final LayoutInflater mInflater;
    public Context context;


    // data is passed into the constructor
    public PostsBlueAdapter(Context context, ArrayList<PostsModal> postsList) {
        this.mInflater = LayoutInflater.from(context);
        this.postsList = postsList;
        this.context = context;
    }

    // inflates the row layout from xml when needed
    @Override
    public PostsBlueAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_layout_post, parent, false);
        return new PostsBlueAdapter.ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(PostsBlueAdapter.ViewHolder holder, int position) {
        final PostsModal postsModal = postsList.get(position);
        holder.tv_post_name.setText(postsModal.getPost_name());






    }

    // total number of rows
    @Override
    public int getItemCount() {
        return postsList.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_post_name;



        ViewHolder(View itemView) {
            super(itemView);
            tv_post_name = itemView.findViewById(R.id.tv_post_name);


        }


    }




}

