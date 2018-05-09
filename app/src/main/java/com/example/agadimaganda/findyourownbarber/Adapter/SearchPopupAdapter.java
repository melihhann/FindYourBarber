package com.example.agadimaganda.findyourownbarber.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.agadimaganda.findyourownbarber.Activity.SearchPopupActivity;
import com.example.agadimaganda.findyourownbarber.Object.Barber;
import com.example.agadimaganda.findyourownbarber.Object.Comment;
import com.example.agadimaganda.findyourownbarber.R;

import java.util.List;

/**
 * Created by Aga diMaganda on 8.05.2018.
 */

public class SearchPopupAdapter extends ArrayAdapter<Barber> {

    private static final String TAG = "SearchPopupAdapter";
    private LayoutInflater inflater;
    private int layoutResource;
    private Context mContext;

    public SearchPopupAdapter(@NonNull Context context, int resource, @NonNull List<Barber> objects) {
        super(context, resource, objects);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        layoutResource = resource;
    }

    private static class ViewHolder{
        TextView barberName, rating;

    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final SearchPopupAdapter.ViewHolder viewHolder;

        if(convertView == null){
            convertView = inflater.inflate(layoutResource, parent, false);
            viewHolder = new SearchPopupAdapter.ViewHolder();

            viewHolder.barberName = (TextView) convertView.findViewById(R.id.barber_name_text_view);
            viewHolder.rating = (TextView) convertView.findViewById(R.id.rating_text_view);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (SearchPopupAdapter.ViewHolder) convertView.getTag();
        }

        //set the barberName
        if(viewHolder.barberName != null){
            viewHolder.barberName.setText(getItem(position).getBarberName());
        }


        //set the rating
        if(viewHolder.rating != null){
            viewHolder.rating.setText(String.format("%.2f", getItem(position).getBarberRate()));
        }



        return convertView;
    }
}
