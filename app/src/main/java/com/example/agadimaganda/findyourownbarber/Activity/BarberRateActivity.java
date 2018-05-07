package com.example.agadimaganda.findyourownbarber.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.agadimaganda.findyourownbarber.Object.Barber;
import com.example.agadimaganda.findyourownbarber.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class BarberRateActivity extends AppCompatActivity {



    //DB
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth auth;
    //Objects
    private Barber barber = new Barber();
    //UI
    private Button button;
    //Variables
    private int divisor;
    private double totalRating;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barber_rate);

        final RatingBar ratingBar = (RatingBar) findViewById(R.id.barber_rating_bar);
        button = findViewById(R.id.barber_rating_button);
        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (intent != null && bundle != null) {
            //isNewBarberAdded = bundle.getBoolean("flag");
            barber.setBarberName(bundle.getString("barberName"));
            barber.setLatitude(bundle.getDouble("latitude"));
            barber.setLongitude(bundle.getDouble("longitude"));
            barber.setId(bundle.getInt("id"));
            barber.setCity(bundle.getString("city"));
            barber.setBarberRate(bundle.getDouble("rating"));
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(BarberRateActivity.this,MapsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("barberName", barber.getBarberName());
                bundle.putDouble("latitude", barber.getLatitude());
                bundle.putDouble("longitude", barber.getLongitude());
                bundle.putString("city", barber.getCity());
                bundle.putDouble("rating",barber.getBarberRate());
                intent.putExtras(bundle);
                final DatabaseReference dbRef = databaseReference.child("BARBERS").child(barber.getBarberName().toUpperCase().replace(" ","")).child("RATINGS").child("USERS").child(auth.getCurrentUser().getUid());
                if(ratingBar.getRating()==0.0f){
                    Toast.makeText(getApplicationContext(),"Lütfen puan veriniz.",Toast.LENGTH_LONG).show();
                    return;
                }
                dbRef.child("RATING").setValue(ratingBar.getRating());
                Query query = databaseReference.child("BARBERS").child(barber.getBarberName().toUpperCase().replace(" ","")).child("RATINGS");
                query.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        divisor = (int) dataSnapshot.getChildrenCount();
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                            for(DataSnapshot childSnap : snapshot.getChildren()){
                                //Long rating = (Long) childSnap.getValue();
                                totalRating = totalRating + Double.parseDouble(String.valueOf(childSnap.getValue()));
                            }



                        }
                        barber.setBarberRate(totalRating/divisor);
                        DatabaseReference dbRefRating = databaseReference.child("BARBERS").child(barber.getBarberName().toUpperCase().replace(" ",""));
                        dbRefRating.child("OVERALLRATING").setValue(barber.getBarberRate());
                        //ratingTextView.setText(barber.getBarberRate().toString());

                    }


                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                startActivity(intent);


            }
        });

    }

}
