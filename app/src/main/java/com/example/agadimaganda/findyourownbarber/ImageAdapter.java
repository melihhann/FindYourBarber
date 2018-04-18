package com.example.agadimaganda.findyourownbarber;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Aga diMaganda on 18.04.2018.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private Context context;
    private List<Upload> uploadList;

    public ImageAdapter(Context mContext, List<Upload> uploads){
        context = mContext;
        uploadList = uploads;

    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        Upload uploadCurrent = uploadList.get(position);
        Picasso.with(context).load(uploadCurrent.getImageUrl()).fit().centerCrop().into(holder.imageViewItem);
    }

    @Override
    public int getItemCount() {
        return uploadList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{

        public ImageView imageViewItem;

        public ImageViewHolder(View itemView) {
            super(itemView);

            imageViewItem = itemView.findViewById(R.id.image_view_item);
        }
    }
}
