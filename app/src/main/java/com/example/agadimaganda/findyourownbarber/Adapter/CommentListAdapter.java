package com.example.agadimaganda.findyourownbarber.Adapter;

import android.app.Activity;
import android.app.LauncherActivity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.agadimaganda.findyourownbarber.Object.Barber;
import com.example.agadimaganda.findyourownbarber.Object.Comment;
import com.example.agadimaganda.findyourownbarber.Object.Like;
import com.example.agadimaganda.findyourownbarber.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.TimeZone;

import static android.content.Intent.getIntent;

/**
 * Created by Aga diMaganda on 22.04.2018.
 */

public class CommentListAdapter extends ArrayAdapter<Comment> {

    private static final String TAG = "CommentListAdapter";

    //user Interface
    private LayoutInflater inflater;
    private int layoutResource;
    private Context mContext;



    //Database Connection
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;

    //Classes
    private Barber barber = new Barber();

    public CommentListAdapter(@NonNull Context context, int resource, @NonNull List<Comment> objects) {
        super(context, resource, objects);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        layoutResource = resource;
    }


    private static class ViewHolder{
        TextView comment, timestamp, reply, likes;
        ImageView like, dislike;

    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewHolder viewHolder;
        Comment item = getItem(position);

        if(convertView == null){
            convertView = inflater.inflate(layoutResource, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.comment = (TextView) convertView.findViewById(R.id.comment);
            viewHolder.timestamp = (TextView) convertView.findViewById(R.id.comment_time_posted);
            viewHolder.reply = (TextView) convertView.findViewById(R.id.comment_reply);
            viewHolder.likes = (TextView) convertView.findViewById(R.id.comment_likes);
            viewHolder.like = (ImageView) convertView.findViewById(R.id.comment_like);
            viewHolder.dislike = (ImageView) convertView.findViewById(R.id.comment_dislike);

            viewHolder.like.setTag(position);
            viewHolder.dislike.setTag(position);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }



        //set the comment
        if(viewHolder.comment != null){
            viewHolder.comment.setText(item.getComment());
        }

        //set the likes
        if(viewHolder.likes != null){
            viewHolder.likes.setText(item.getLikes().toString() + " Beğeni");
        }

        //set like dislike imageView

        if(item.getLikePosNeg() != null){

            if(item.getLikePosNeg().toString().equalsIgnoreCase("NEUTRAL")){
                viewHolder.like.setBackgroundColor(Color.WHITE);
                viewHolder.dislike.setBackgroundColor(Color.WHITE);
            }else if(item.getLikePosNeg().toString().equalsIgnoreCase("POSITIVE")){
                viewHolder.like.setBackgroundColor(Color.GREEN);
                viewHolder.dislike.setBackgroundColor(Color.WHITE);
            }else if(item.getLikePosNeg().toString().equalsIgnoreCase("NEGATIVE")){
                viewHolder.like.setBackgroundColor(Color.WHITE);
                viewHolder.dislike.setBackgroundColor(Color.RED);
            }
        }

        //set the timestamp difference
        if(viewHolder.timestamp != null)
        {
            String timestampDifference = getTimestampDifference(getItem(position));
            if(!timestampDifference.equals("0")){
                viewHolder.timestamp.setText(timestampDifference + "g");
            }else{
                viewHolder.timestamp.setText("bugün");
            }
        }




        //Database Connection
        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();


        //CommentsFragment sayfasından berber bilgilerini alma
        Intent intent = ((Activity) mContext).getIntent();
        Bundle bundle = ((Activity) mContext).getIntent().getExtras();

        if(intent != null && bundle != null){
            barber.setBarberName(bundle.getString("barberName"));
            barber.setLatitude(bundle.getDouble("latitude"));
            barber.setLongitude(bundle.getDouble("longitude"));
            barber.setCity(bundle.getString("city"));
        }



            //Beğenme Butonu
            viewHolder.like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final Query query = databaseReference.child("BARBERS").child(barber.getBarberName().toUpperCase().replace(" ","")).child("COMMENTS");

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                                final String userId = snapshot.getKey();

                                for(final DataSnapshot snapshotChild : snapshot.getChildren()){

                                    String commentId = snapshotChild.getKey();
                                    final Comment comment = new Comment();
                                    comment.setComment(snapshotChild.getValue(Comment.class).getComment());
                                    comment.setUserId(snapshotChild.getValue(Comment.class).getUserId());

                                    if(comment.getComment() != null){
                                        if(viewHolder.comment.getText().equals(comment.getComment())){

                                            final DatabaseReference reference = databaseReference.child("BARBERS").child(barber.getBarberName().toUpperCase().replace(" ",""))
                                                    .child("COMMENTS").child(userId).child(commentId).child("LIKE");

                                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {

                                                    String userIdWhoLikes = auth.getCurrentUser().getUid();
                                                    String posNegLike = (String) dataSnapshot.child("USERS").child(userIdWhoLikes).getValue();
                                                    Long currentLikeValue = (Long) dataSnapshot.child("commentLike").getValue();

                                                    if(posNegLike == null || posNegLike.equalsIgnoreCase("NEGATIVE") || posNegLike.equalsIgnoreCase("NEUTRAL")){

                                                        reference.child("USERS").child(userIdWhoLikes).setValue("POSITIVE");

                                                        if(posNegLike == null || posNegLike.equalsIgnoreCase("NEUTRAL")){
                                                            currentLikeValue = currentLikeValue + 1;
                                                        }else if(posNegLike.equalsIgnoreCase("NEGATIVE")){
                                                            currentLikeValue = currentLikeValue + 2;
                                                        }

                                                        DatabaseReference likeRef = reference.child("commentLike");
                                                        viewHolder.likes.setText(currentLikeValue + " Beğeni");
                                                        viewHolder.dislike.setBackgroundColor(Color.WHITE);
                                                        viewHolder.like.setBackgroundColor(Color.GREEN);
                                                        likeRef.setValue(currentLikeValue);

                                                    }else{

                                                        reference.child("USERS").child(userIdWhoLikes).setValue("NEUTRAL");
                                                        currentLikeValue = currentLikeValue - 1;
                                                        DatabaseReference likeRef = reference.child("commentLike");
                                                        viewHolder.likes.setText(currentLikeValue + " Beğeni");
                                                        viewHolder.like.setBackgroundColor(Color.WHITE);
                                                        likeRef.setValue(currentLikeValue);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            });


        //Beğenmeme Butonu
        viewHolder.dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Query query = databaseReference.child("BARBERS").child(barber.getBarberName().toUpperCase().replace(" ","")).child("COMMENTS");

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                            final String userId = snapshot.getKey();

                            for(final DataSnapshot snapshotChild : snapshot.getChildren()){

                                String commentId = snapshotChild.getKey();

                                final Comment comment = new Comment();
                                comment.setComment(snapshotChild.getValue(Comment.class).getComment());
                                comment.setUserId(snapshotChild.getValue(Comment.class).getUserId());

                                if(comment.getComment() != null){
                                    if(viewHolder.comment.getText().equals(comment.getComment())){

                                        final DatabaseReference reference = databaseReference.child("BARBERS").child(barber.getBarberName().toUpperCase().replace(" ",""))
                                                .child("COMMENTS").child(userId).child(commentId).child("LIKE");

                                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                String userIdWhoLikes = auth.getCurrentUser().getUid();
                                                String posNegLike = (String) dataSnapshot.child("USERS").child(userIdWhoLikes).getValue();
                                                Long currentLikeValue = (Long) dataSnapshot.child("commentLike").getValue();

                                                if(posNegLike == null || posNegLike.equalsIgnoreCase("POSITIVE") || posNegLike.equalsIgnoreCase("NEUTRAL")){

                                                    reference.child("USERS").child(userIdWhoLikes).setValue("NEGATIVE");

                                                    if(posNegLike == null || posNegLike.equalsIgnoreCase("NEUTRAL")){
                                                        currentLikeValue = currentLikeValue - 1;
                                                    }else if(posNegLike.equalsIgnoreCase("POSITIVE")){
                                                        currentLikeValue = currentLikeValue - 2;
                                                    }

                                                    DatabaseReference likeRef = reference.child("commentLike");
                                                    viewHolder.likes.setText(currentLikeValue + " Beğeni");
                                                    viewHolder.dislike.setBackgroundColor(Color.RED);
                                                    viewHolder.like.setBackgroundColor(Color.WHITE);
                                                    likeRef.setValue(currentLikeValue);

                                                }else{

                                                    reference.child("USERS").child(userIdWhoLikes).setValue("NEUTRAL");
                                                    currentLikeValue = currentLikeValue + 1;
                                                    DatabaseReference likeRef = reference.child("commentLike");
                                                    viewHolder.likes.setText(currentLikeValue + " Beğeni");
                                                    viewHolder.dislike.setBackgroundColor(Color.WHITE);
                                                    likeRef.setValue(currentLikeValue);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        return convertView;
    }

    public String getTimestampDifference(Comment comment){

        String difference = "";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm:ss'Z'", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("Turkey"));
        Date today = c.getTime();
        Date timestamp;
        final String commentTimestamp = comment.getDateCreated();
        try {
            timestamp = sdf.parse(commentTimestamp);
            difference = String.valueOf(Math.round(((today.getTime() - timestamp.getTime())) / 1000 / 60 / 60 / 24));
        }catch (ParseException e){
            Log.e(TAG, "getTimestampDifference: ParseException: " + e.getMessage());
            difference = "0";
        }
        return difference;
    }

}
