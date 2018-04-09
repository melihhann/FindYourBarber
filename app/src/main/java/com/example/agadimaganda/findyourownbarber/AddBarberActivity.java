package com.example.agadimaganda.findyourownbarber;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.math3.analysis.function.Add;

import static java.lang.StrictMath.toIntExact;

public class AddBarberActivity extends AppCompatActivity {

    //Database
    private DatabaseReference mRef;

    //User Interface
    private EditText barberNameEditText;
    private EditText latitudeEditText;
    private EditText longitudeEditText;
    private EditText cityEditText;
    private Button button;


    //Classes
    private Barber newBarber;
    //private coolMethods coolMethods = new coolMethods();

    //Variables
    private int currentId;
    private Boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_barber);

        button =  findViewById(R.id.saveButtonAction);
        barberNameEditText = findViewById(R.id.barberName_editText);
        latitudeEditText = findViewById(R.id.latitude_editText);
        longitudeEditText = findViewById(R.id.longitude_editText);
        cityEditText = findViewById(R.id.city_editText);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newBarber = new Barber();

                newBarber.setBarberName(barberNameEditText.getText().toString());
                newBarber.setLatitude(Double.valueOf(latitudeEditText.getText().toString()));
                newBarber.setLongitude(Double.valueOf(longitudeEditText.getText().toString()));
                newBarber.setCity((cityEditText.getText().toString()));

                    mRef = FirebaseDatabase.getInstance().getReference();
                    final DatabaseReference childRef = mRef.child("BARBERS").child(newBarber.getBarberName().toUpperCase().replace(" ", ""));
                    final DatabaseReference childRefId = mRef.child("LASTID");
                    mRef.keepSynced(true);

                    childRefId.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Long currentIdLong = (Long) dataSnapshot.getValue();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                int id = toIntExact(currentIdLong);
                                id = id + 1;
                                childRefId.setValue(id);
                                newBarber.setId(id-1);
                                childRef.child("ID").setValue(newBarber.getId());
                                flag = true;
                                Bundle bundle = new Bundle();
                                bundle.putBoolean("flag", flag);
                                Intent intent = new Intent(AddBarberActivity.this, MapsActivity.class);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    childRef.child("BARBERNAME").setValue(newBarber.getBarberName());
                    childRef.child("LATITUDE").setValue(newBarber.getLatitude());
                    childRef.child("LONGITUDE").setValue(newBarber.getLongitude());
                    childRef.child("CITY").setValue(newBarber.getCity());

                    /*
                    Bundle bundle = new Bundle();
                    bundle.putDouble("latitude",newBarber.getLatitude());
                    bundle.putDouble("longitude",newBarber.getLongitude());
                    bundle.putInt("id",newBarber.getId());
                    bundle.putString("barberName",newBarber.getBarberName());
                    Intent intent1 = new Intent(AddBarberActivity.this, MapsActivity.class);
                    intent1.putExtras(bundle);
                    startActivity(intent1);
                    */
                }

        });
    }

}
