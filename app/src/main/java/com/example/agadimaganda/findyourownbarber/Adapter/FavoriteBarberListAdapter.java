package com.example.agadimaganda.findyourownbarber.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.agadimaganda.findyourownbarber.Object.Barber;
import com.example.agadimaganda.findyourownbarber.R;

import java.util.List;

/**
 * Created by Aga diMaganda on 15.05.2018.
 */

public class FavoriteBarberListAdapter extends ArrayAdapter<Barber> {

    private LayoutInflater inflater;
    private int layoutResource;
    private Context mContext;

    public FavoriteBarberListAdapter(@NonNull Context context, int resource, @NonNull List<Barber> objects) {
        super(context, resource, objects);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        layoutResource = resource;
    }


    private static class ViewHolder{
        TextView barberName;

    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final FavoriteBarberListAdapter.ViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(layoutResource, parent, false);
            viewHolder = new FavoriteBarberListAdapter.ViewHolder();
            viewHolder.barberName = (TextView) convertView.findViewById(R.id.favorite_barber_name);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (FavoriteBarberListAdapter.ViewHolder) convertView.getTag();

        }

        //set the barberName
        if(viewHolder.barberName != null){
            viewHolder.barberName.setText(getItem(position).getBarberName());
        }

            return convertView;
        }

}
