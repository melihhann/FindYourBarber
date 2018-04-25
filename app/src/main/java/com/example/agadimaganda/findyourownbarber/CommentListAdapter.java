package com.example.agadimaganda.findyourownbarber;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Aga diMaganda on 22.04.2018.
 */

public class CommentListAdapter extends ArrayAdapter<Comment> {

    private static final String TAG = "CommentListAdapter";
    private LayoutInflater inflater;
    private int layoutResource;
    private Context mContext;

    public CommentListAdapter(@NonNull Context context, int resource, @NonNull List<Comment> objects) {
        super(context, resource, objects);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        layoutResource = resource;
    }


    private static class ViewHolder{
        TextView comment, timestamp, reply, likes;
        ImageView like;

    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewHolder viewHolder;

        if(convertView == null){
            convertView = inflater.inflate(layoutResource, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.comment = (TextView) convertView.findViewById(R.id.comment);
            viewHolder.timestamp = (TextView) convertView.findViewById(R.id.comment_time_posted);
            viewHolder.reply = (TextView) convertView.findViewById(R.id.comment_reply);
            viewHolder.likes = (TextView) convertView.findViewById(R.id.comment_likes);
            viewHolder.like = (ImageView) convertView.findViewById(R.id.comment_like);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //set the comment
        viewHolder.comment.setText(getItem(position).getComment());

        //set the timestamp difference
        String timestampDifference = getTimestampDifference(getItem(position));
        if(!timestampDifference.equals("0")){
            viewHolder.timestamp.setText(timestampDifference + " g");
        }else{
            viewHolder.timestamp.setText("bugün");
        }



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
