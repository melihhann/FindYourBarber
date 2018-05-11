package com.example.agadimaganda.findyourownbarber.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.agadimaganda.findyourownbarber.Activity.PopupActivity;
import com.example.agadimaganda.findyourownbarber.Object.Upload;
import com.example.agadimaganda.findyourownbarber.R;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import static android.support.v4.content.ContextCompat.startActivity;

/**
 * Created by Aga diMaganda on 18.04.2018.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    //Variables
    private static final String TAG = "ImageAdapter";

    private Context context;
    private static final int MAX_WIDTH = 500;
    private static final int MAX_HEIGHT = 300;
    private String barberName;
    private List<Upload> uploadList;

    //Database Connection
    DatabaseReference databaseReference;
    FirebaseAuth auth;
    StorageReference storageReference;
    FirebaseAnalytics firebaseAnalytics;

    public ImageAdapter(Context mContext, List<Upload> uploads){
        context = mContext;
        uploadList = uploads;

        databaseReference = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAnalytics = FirebaseAnalytics.getInstance(mContext);

        Bundle bundle = ((Activity) mContext).getIntent().getExtras();
        if(bundle != null) {
            barberName = bundle.getString("barberName");
            barberName = barberName.toUpperCase().replace(" ", "");
        }

    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_item, parent, false);
        return new ImageViewHolder(view);


    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, final int position) {
        Upload uploadCurrent = uploadList.get(position);


        //int size = (int) Math.ceil(Math.sqrt(MAX_WIDTH * MAX_HEIGHT));
        /*Picasso.with(context).load(uploadCurrent.getImageUrl()).transform(new BitmapTransform(MAX_WIDTH, MAX_HEIGHT))
                .resize(size, size).placeholder(R.mipmap.ic_person_add_black_24dp).centerInside().into(holder.imageViewItem);*/

        Glide.with(context).load(uploadCurrent.getImageUrl()).into(holder.imageViewItem);


        if(!(auth.getCurrentUser().getUid().equalsIgnoreCase(uploadCurrent.getUserId()))){
                    holder.imageDelete.setVisibility(View.INVISIBLE);
            }



        //Fotograf silme butonu
        if(holder.imageDelete != null){

            holder.imageDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //Yarattığımız Bundle tutacağı veriyi Analytics'e yollayacak.
                    Bundle params = new Bundle();
                    params.putInt("ButtonID", view.getId());
                    String buttonName = "Delete_Photo";

                    Log.e(TAG,"Silme tusuna basildi.");

                    Intent intent = new Intent((Activity) context, PopupActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("imageId", uploadList.get(position).getImageId());
                    bundle.putString("barberName", barberName);
                    bundle.putString("adapter", "ImageAdapter");
                    intent.putExtras(bundle);
                    context.startActivity(intent);

                    firebaseAnalytics.logEvent(buttonName, params);
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return uploadList.size();
    }



    public class ImageViewHolder extends RecyclerView.ViewHolder{

        public ImageView imageViewItem;
        public ImageView imageDelete;

        public ImageViewHolder(final View itemView) {
            super(itemView);

            imageViewItem = itemView.findViewById(R.id.image_view_item);
            imageDelete = itemView.findViewById(R.id.image_delete);

        }
    }



}


