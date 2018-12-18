package com.live.almel.poststestassignment.ui;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.live.almel.poststestassignment.R;
import com.live.almel.poststestassignment.data.network.res.Post;

import java.util.List;


public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostViewHolder> {

    private List<Post> mPosts;
    private PostViewHolder.CustomClickListener mCustomClickListener;

    public PostsAdapter(List<Post> posts, PostViewHolder.CustomClickListener customClickListener) {
        mCustomClickListener = customClickListener;
        mPosts = posts;
    }


    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        Log.d("onCreateViewHolder", " ");
        return new PostViewHolder(convertView, mCustomClickListener);

    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = mPosts.get(position);
        holder.mTitle.setText(post.getTitle());
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mTitle;
        CustomClickListener mListener;

        PostViewHolder(View itemView, CustomClickListener customClickListener) {
            super(itemView);

            mListener = customClickListener;
            mTitle = itemView.findViewById(R.id.title_tv);

            itemView.findViewById(R.id.post_ll)
                    .setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onPostItemClick(getAdapterPosition());
        }

        public interface CustomClickListener {
            void onPostItemClick(int position);
        }
    }
}
