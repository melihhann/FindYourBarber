package com.example.agadimaganda.findyourownbarber.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.agadimaganda.findyourownbarber.Activity.BarberRateActivity;
import com.example.agadimaganda.findyourownbarber.Activity.BarberViewActivity;
import com.example.agadimaganda.findyourownbarber.Object.Barber;
import com.example.agadimaganda.findyourownbarber.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


public class DetailsFragment extends Fragment {

    private static final String TAG = "DetailsFragment";

    //User Interface
    private TextView cityTextView;
    private TextView ratingTextView;
    private FloatingActionButton button;

    //Classes
    private Barber barber = new Barber();

    //Database Connection
    private FirebaseAuth auth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    //variables
    private int divisor = 0;
    private double rating = 0;
    private double totalRating = 0;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.details, container, false);
        cityTextView = (TextView) view.findViewById(R.id.cityTextView);
        ratingTextView = (TextView) view.findViewById(R.id.ratingTextView);
        cityTextView.setText(barber.getCity());
        ratingTextView.setText(barber.getBarberRate().toString());
        button = view.findViewById(R.id.rateBarberButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), BarberRateActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("barberName", barber.getBarberName());
                bundle.putDouble("latitude", barber.getLatitude());
                bundle.putDouble("longitude", barber.getLongitude());
                bundle.putString("city", barber.getCity());
                bundle.putDouble("rating",barber.getBarberRate());
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });
        return view;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getActivity().getIntent();
        Bundle bundle = getActivity().getIntent().getExtras();
        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();



        if(intent != null && bundle != null){
            barber.setBarberName(bundle.getString("barberName"));
            barber.setLatitude(bundle.getDouble("latitude"));
            barber.setLongitude(bundle.getDouble("longitude"));
            barber.setCity(bundle.getString("city"));
            barber.setBarberRate(bundle.getDouble("rating"));
        }
    }

}
