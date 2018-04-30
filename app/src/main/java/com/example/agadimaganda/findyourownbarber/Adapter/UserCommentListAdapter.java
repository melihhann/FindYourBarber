package com.example.agadimaganda.findyourownbarber.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.agadimaganda.findyourownbarber.Object.Comment;
import com.example.agadimaganda.findyourownbarber.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Aga diMaganda on 25.04.2018.
 */

public class UserCommentListAdapter extends ArrayAdapter<Comment> {

    private static final String TAG = "UserCommentListAdapter";
    private LayoutInflater inflater;
    private int layoutResource;
    private Context mContext;

    public UserCommentListAdapter(@NonNull Context context, int resource, @NonNull List<Comment> objects) {
        super(context, resource, objects);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        layoutResource = resource;
    }


    private static class ViewHolder{
        TextView comment, timestamp, likes, barberName;

    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final UserCommentListAdapter.ViewHolder viewHolder;

        if(convertView == null){
            convertView = inflater.inflate(layoutResource, parent, false);
            viewHolder = new UserCommentListAdapter.ViewHolder();

            viewHolder.comment = (TextView) convertView.findViewById(R.id.user_comment);
            viewHolder.timestamp = (TextView) convertView.findViewById(R.id.user_comment_time_posted);
            viewHolder.likes = (TextView) convertView.findViewById(R.id.user_comment_likes);
            viewHolder.barberName = (TextView) convertView.findViewById(R.id.barber_name);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (UserCommentListAdapter.ViewHolder) convertView.getTag();
        }

        //set the comment
        viewHolder.comment.setText(getItem(position).getComment());

        //set the likes
        viewHolder.likes.setText(getItem(position).getLikes().toString() + " Beğeni");



        //set the timestamp difference
        String timestampDifference = getTimestampDifference(getItem(position));
        if(!timestampDifference.equals("0")){
            viewHolder.timestamp.setText(timestampDifference + " g");
        }else{
            viewHolder.timestamp.setText("bugün");
        }

        //set the barberName
        viewHolder.barberName.setText(getItem(position).getBarberName());

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
