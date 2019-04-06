package com.siddharth.divinecare.Adapters;

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
import com.siddharth.divinecare.Activities.VideoActivity;
import com.siddharth.divinecare.Models.ModelVideoDetails;
import com.siddharth.divinecare.R;

import java.util.ArrayList;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {
    Context context;
    private ArrayList<ModelVideoDetails> modelVideoDetailsArrayList;

    public VideoAdapter(Context context, ArrayList<ModelVideoDetails> modelVideoDetailsArrayList) {
        this.context = context;
        this.modelVideoDetailsArrayList = modelVideoDetailsArrayList;
    }

    @NonNull
    @Override
    public VideoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.videolist, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final VideoAdapter.ViewHolder viewHolder, int i) {
        viewHolder.video_descriptio.setText(modelVideoDetailsArrayList.get(i).getVideoDesc());
        viewHolder.video_title.setText(modelVideoDetailsArrayList.get(i).getVideoName());
        Glide.with(context).load(modelVideoDetailsArrayList.get(i).getUrl()).into(viewHolder.image);
        viewHolder.tv_videoId.setText(modelVideoDetailsArrayList.get(i).getVideoId());

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
        return modelVideoDetailsArrayList.size();
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
