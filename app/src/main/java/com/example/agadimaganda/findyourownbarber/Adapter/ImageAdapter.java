package com.example.agadimaganda.findyourownbarber.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.agadimaganda.findyourownbarber.Object.Upload;
import com.example.agadimaganda.findyourownbarber.R;

import java.util.List;

/**
 * Created by Aga diMaganda on 18.04.2018.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private Context context;
    private List<Upload> uploadList;
    private static final int MAX_WIDTH = 500;
    private static final int MAX_HEIGHT = 300;

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

        int size = (int) Math.ceil(Math.sqrt(MAX_WIDTH * MAX_HEIGHT));


        /*Picasso.with(context).load(uploadCurrent.getImageUrl()).transform(new BitmapTransform(MAX_WIDTH, MAX_HEIGHT))
                .resize(size, size).placeholder(R.mipmap.ic_person_add_black_24dp).centerInside().into(holder.imageViewItem);*/

        Glide.with(context).load(uploadCurrent.getImageUrl()).into(holder.imageViewItem);

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
