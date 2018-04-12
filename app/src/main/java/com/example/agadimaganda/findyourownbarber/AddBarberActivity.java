package com.example.agadimaganda.findyourownbarber;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.Locale;

public class AddBarberActivity extends AppCompatActivity implements OnMapReadyCallback {

    //Database
    private DatabaseReference mRef;

    //User Interface
    private EditText barberNameEditText;
    private Button button;

    //Map
    private MapView mapView;
    private GoogleMap map;
    Geocoder geocoder;
    java.util.List<Address> address = null;
    Marker marker;

    //Classes
    private Barber newBarber = new Barber();
    //private coolMethods coolMethods = new coolMethods();

    //Variables
    private int currentId;
    private Boolean flag = false;
    private BitmapDescriptorFactory bitmapDescriptorFactory;
    private Boolean isMarkerAdded = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_barber);


        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        // Gets to GoogleMap from the MapView and does initialization stuff
        mapView.getMapAsync(this);//

        button =  findViewById(R.id.saveButtonAction);
        barberNameEditText = findViewById(R.id.barberName_editText);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                newBarber.setBarberName(barberNameEditText.getText().toString());
                //newBarber.setLatitude(Double.valueOf(latitudeEditText.getText().toString()));
                //newBarber.setLongitude(Double.valueOf(longitudeEditText.getText().toString()));
                //newBarber.setCity((cityEditText.getText().toString()));

                    mRef = FirebaseDatabase.getInstance().getReference();
                    final DatabaseReference childRef = mRef.child("BARBERS").child(newBarber.getBarberName().toUpperCase().replace(" ", ""));
                    final DatabaseReference childRefId = mRef.child("LASTID");
                    mRef.keepSynced(true);

                    childRefId.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            Long currentIdLong = (Long) dataSnapshot.getValue();
                            int id = currentIdLong.intValue();
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

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    childRef.child("BARBERNAME").setValue(newBarber.getBarberName());
                    childRef.child("LATITUDE").setValue(newBarber.getLatitude());
                    childRef.child("LONGITUDE").setValue(newBarber.getLongitude());
                    childRef.child("CITY").setValue(newBarber.getCity());


                    marker.remove();
                    barberNameEditText.setText(""); 
                }

        });
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setMyLocationButtonEnabled(false);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        }

        geocoder = new Geocoder(this, Locale.getDefault());


        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                if(isMarkerAdded){
                    marker.remove();
                    isMarkerAdded = false;
                }

                LatLng newBarberMarker = new LatLng(latLng.latitude, latLng.longitude);
                 marker = map.addMarker(new MarkerOptions()
                        .position(newBarberMarker).icon(bitmapDescriptorFactory.fromResource(R.drawable.makasufakbuyukufak)));

                isMarkerAdded = true;
                newBarber.setLatitude(latLng.latitude);
                newBarber.setLongitude(latLng.longitude);

                try{
                    address = geocoder.getFromLocation(newBarber.getLatitude(), newBarber.getLongitude(), 1);
                }catch (IOException e){
                    e.printStackTrace();
                }

                if(address != null){
                    if(address.get(0).getAdminArea() != null){
                        newBarber.setCity(address.get(0).getAdminArea());
                    }
                }
            }
        });

    }


    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
