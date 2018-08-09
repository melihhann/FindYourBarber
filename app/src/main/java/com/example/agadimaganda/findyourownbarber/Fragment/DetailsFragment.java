package com.example.agadimaganda.findyourownbarber.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.google.firebase.database.ValueEventListener;


public class DetailsFragment extends Fragment {

    private static final String TAG = "DetailsFragment";

    //User Interface
    private TextView cityTextView;
    private TextView ratingTextView;
    private FloatingActionButton button;
    private CheckBox favoriteCheckBox;

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
        favoriteCheckBox = (CheckBox) view.findViewById(R.id.favoriteCheckBox);
        cityTextView.setText(barber.getCity());
        ratingTextView.setText(String.format("%.2f", barber.getBarberRate()));

        button = view.findViewById(R.id.rateBarberButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            //Yarattığımız Bundle tutacağı veriyi Analytics'e yollayacak.
            Bundle params = new Bundle();
            params.putInt("ButtonID", view.getId());
            String buttonName = "Rate_Barber_Button";

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


        //Berberi favorilere ekleme tuşu
        favoriteCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if(isChecked){
                    final DatabaseReference childRef = databaseReference.child("USERS").child(auth.getCurrentUser().getUid()).child("FAVORITES");

                    childRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            childRef.child(barber.getBarberName()).setValue("favorite");
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }else{
                    final DatabaseReference childRef = databaseReference.child("USERS").child(auth.getCurrentUser().getUid()).child("FAVORITES");

                    childRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            childRef.child(barber.getBarberName()).setValue(null);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
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


        //Kullanıcının berberi favoriye alıp almadığını bulma.
        Query query = databaseReference.child("USERS").child(auth.getCurrentUser().getUid()).child("FAVORITES");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if(barber.getBarberName().equalsIgnoreCase(snapshot.getKey())){
                        favoriteCheckBox.setChecked(true);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
