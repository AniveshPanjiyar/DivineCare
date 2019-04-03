package com.siddharth.divinecare;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {
    Context context;
    private ArrayList<VideoDetails> videoDetailsArrayList;

    public VideoAdapter(Context context, ArrayList<VideoDetails> videoDetailsArrayList) {
        this.context = context;
        this.videoDetailsArrayList = videoDetailsArrayList;
    }

    @NonNull
    @Override
    public VideoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.videolist, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final VideoAdapter.ViewHolder viewHolder, int i) {
        viewHolder.video_descriptio.setText(videoDetailsArrayList.get(i).getVideoDesc());
        viewHolder.video_title.setText(videoDetailsArrayList.get(i).getVideoName());
        Glide.with(context).load(videoDetailsArrayList.get(i).getURL()).into(viewHolder.image);
        viewHolder.tv_videoId.setText(videoDetailsArrayList.get(i).getVideoId());

        viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), VideoActivity.class);
                intent.putExtra("videoId", viewHolder.tv_videoId.getText().toString());
                view.getContext().startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return videoDetailsArrayList






                .size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout;
        ImageView image;
        TextView video_title, video_descriptio, tv_videoId;

        public ViewHolder(View view) {
            super(view);
            linearLayout = view.findViewById(R.id.asser);
            image = view.findViewById(R.id.video_image);
            video_descriptio = view.findViewById(R.id.video_descriptio);
            video_title = view.findViewById(R.id.video_title);
            tv_videoId = view.findViewById(R.id.tv_videoId);
        }
    }
}
