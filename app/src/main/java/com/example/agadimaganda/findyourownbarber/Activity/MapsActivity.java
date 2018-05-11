package com.example.agadimaganda.findyourownbarber.Activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.agadimaganda.findyourownbarber.Object.Barber;
import com.example.agadimaganda.findyourownbarber.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    //Variables
    private final static int MY_PERMISSION_FINE_LOCATION = 101;
    private ArrayList<Barber> barberListUpdated = new ArrayList<>();
    private ArrayList<Barber> barberListCurrent = new ArrayList<>();
    private ArrayList<String> barberNameArrayList = new ArrayList<>();
    private ArrayList<String> barberRateArrayList = new ArrayList<>();
    private Boolean isNewBarberAdded = false;
    private RatingBar ratingBar;

    //User Interface
    private GoogleMap mMap;
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton addBarberButton;
    private Marker marker;
    private BitmapDescriptorFactory bitmapDescriptorFactory;//Yarayabilir.
    private FloatingActionButton searchBarberAction;

    //Database Reference
    private DatabaseReference refForbarberList;
    private FirebaseAnalytics firebaseAnalytics;


    public MapsActivity(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        verifyPermissions();

        //AddBarberActivity calisti, yeni berber eklendi.
        Intent intent = getIntent();
        Bundle bundle = getIntent().getExtras();
        if (intent != null && bundle != null) {
            isNewBarberAdded = bundle.getBoolean("flag");
        }


        // TODO: 8.04.2018 intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        //Bottom Navigation View
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.nav_home:
                        break;

                    case R.id.nav_profile:
                        Intent intent1 = new Intent(MapsActivity.this, ProfileActivity.class);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent1);
                        break;

                    case R.id.nav_settings:
                        Intent intent2 = new Intent(MapsActivity.this, SettingsActivity.class);
                        intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent2);
                        break;
                }

                return false;//Shorter intent startActivity(new Intent(FirstActivity.this, SecondActivity.class));
            }
        });


        //Berber Ekleme Butonu
        addBarberButton = (FloatingActionButton) findViewById(R.id.addBarberButton);
        addBarberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Yarattığımız Bundle tutacağı veriyi Analytics'e yollayacak.
                Bundle params = new Bundle();
                params.putInt("ButtonID", view.getId());
                String buttonName = "Add_Barber";

                firebaseAnalytics.logEvent(buttonName, params);

                Intent intent2 = new Intent(MapsActivity.this, AddBarberActivity.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent2);
            }
        });

    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //Analytics Reference
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        //Dialog To Get App Rating From Users
        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
        builder.setCancelable(true);
        builder.setTitle("Puanla");
        builder.setMessage("Uygulamamıza 1'den 5'e kadar kaç puan verirsiniz?");
        builder.setView(R.layout.dialog_rate_me);
        ratingBar = findViewById(R.id.dialogRatingBar);
        builder.setPositiveButton("Puanla", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                firebaseAnalytics.setUserProperty("usersAppRating", Float.toString(ratingBar.getRating()) );
                Toast.makeText(getApplicationContext(),"Puanlama yapıldı.",Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();

        //Location Permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_FINE_LOCATION);
            }
        }



        //Database References
        refForbarberList = FirebaseDatabase.getInstance().getReference();
        refForbarberList.keepSynced(true);
        final DatabaseReference childRefMarker = refForbarberList.child("BARBERS");


        //Harita acildiginda Database'e ekli butun berberlerin Marker'larini haritaya ekliyor.
        childRefMarker.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                Barber barber = new Barber();

                String barberName = (String) snapshot.child("BARBERNAME").getValue();
                Double latitude = (Double) snapshot.child("LATITUDE").getValue();
                Double longitude = (Double) snapshot.child("LONGITUDE").getValue();
                String city = (String)  snapshot.child("CITY").getValue();
                Long idLong = (Long) snapshot.child("ID").getValue();
                Double rating = Double.parseDouble(String.valueOf(snapshot.child("OVERALLRATING").getValue()));

                        if(idLong != null){
                        barber.setId(idLong.intValue());
                        barber.setBarberName(barberName);
                        barber.setLatitude(latitude);
                        barber.setLongitude(longitude);
                        barber.setCity(city);
                        barber.setBarberRate(rating);
                        barberListCurrent.add(barber);

                        LatLng newBarberMarker = new LatLng(barber.getLatitude(), barber.getLongitude());

                        if(barber.getBarberRate() < 3){
                            marker = mMap.addMarker(new MarkerOptions()
                                    .position(newBarberMarker)
                                    .title(barber.getBarberName())
                                    .snippet(String.format("%.2f", barber.getBarberRate()))
                                    .icon(bitmapDescriptorFactory.fromResource(R.drawable.makasufakbuyukufak)));

                            marker.setTag(barber);
                        }else if(barber.getBarberRate() > 3 && barber.getBarberRate() < 4 ){
                            marker = mMap.addMarker(new MarkerOptions()
                                    .position(newBarberMarker)
                                    .title(barber.getBarberName())
                                    .snippet(String.format("%.2f", barber.getBarberRate()))
                                    .icon(bitmapDescriptorFactory.fromResource(R.drawable.scissors)));

                            marker.setTag(barber);
                        }else{
                            marker = mMap.addMarker(new MarkerOptions()
                                    .position(newBarberMarker)
                                    .title(barber.getBarberName())
                                    .snippet(String.format("%.2f", barber.getBarberRate()))
                                    .icon(bitmapDescriptorFactory.fromResource(R.drawable.hairdresser)));

                            marker.setTag(barber);
                        }
                       }
                }

                Bundle bundle = getIntent().getExtras();
                if(bundle != null){
                    String barberNameFromPopup = bundle.getString("barberName");

                    if(barberNameFromPopup != null){
                        for(int i=0; i < barberListCurrent.size(); i++){
                            if(barberNameFromPopup.trim().equalsIgnoreCase(barberListCurrent.get(i).getBarberName())){
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(barberListCurrent.get(i).getLatitude(), barberListCurrent.get(i).getLongitude()), 12.0f));
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //Yeni berber eklendigi zaman haritaya Marker ekliyor.
        if(isNewBarberAdded){

            childRefMarker.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                        childRefMarker.child("ornek").setValue("aga12");
                        String barberName = (String) snapshot.child("BARBERNAME").getValue();
                        Double latitude = (Double) snapshot.child("LATITUDE").getValue();
                        Double longitude = (Double) snapshot.child("LONGITUDE").getValue();
                        String city = (String) snapshot.child("CITY").getValue();
                        Double rating = Double.parseDouble(String.valueOf(snapshot.child("OVERALLRATING").getValue()));

                        Barber barber = new Barber();
                        Long idLong  = (Long) snapshot.child("ID").getValue();
                            if(idLong != null){
                                barber.setId(idLong.intValue());
                                barber.setBarberName(barberName);
                                barber.setLatitude(latitude);
                                barber.setLongitude(longitude);
                                barber.setCity(city);
                                barber.setBarberRate(rating);
                                barberListUpdated.add(barber);

                                if(barberListCurrent.size() < barberListUpdated.size()){

                                    for(int i = barberListCurrent.size(); i < barberListUpdated.size(); i++){
                                        barberListCurrent.add(barberListUpdated.get(i));
                                    }
                                }

                                for(int i = 0; i < barberListCurrent.size(); i++){
                                    LatLng newBarberMarker = new LatLng(barberListCurrent.get(barberListCurrent.size() - 1).getLatitude(), barberListCurrent.get(barberListCurrent.size() - 1).getLongitude());
                                    marker = mMap.addMarker(new MarkerOptions()
                                            .position(newBarberMarker)
                                            .title(barberListCurrent.get(barberListCurrent.size() - 1).getBarberName())
                                            //.snippet(Berber Puani)
                                            .icon(bitmapDescriptorFactory.fromResource(R.drawable.makasufakbuyukufak)));

                                    marker.setTag(barberListCurrent.get(barberListCurrent.size() - 1));
                                }
                            }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //Error Message
                }
            });

        }

        //Marker'a basildiginda, bastigi berber marker'inin sayfasina gidiyor
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                Barber barber = new Barber();
                barber = (Barber) marker.getTag();

                Intent intent = new Intent(MapsActivity.this, BarberViewActivity.class);
                Bundle bundle = new Bundle();

                bundle.putString("barberName", barber.getBarberName());
                bundle.putDouble("latitude", barber.getLatitude());
                bundle.putDouble("longitude", barber.getLongitude());
                bundle.putInt("id", barber.getId());
                bundle.putString("city", barber.getCity());
                bundle.putDouble("rating",barber.getBarberRate());

                intent.putExtras(bundle);
                startActivity(intent);
            }
        });


        //Berber Arama Butonu
        searchBarberAction = (FloatingActionButton) findViewById(R.id.searchButtonAction);
        searchBarberAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Yarattığımız Bundle tutacağı veriyi Analytics'e yollayacak.
                Bundle params = new Bundle();
                params.putInt("ButtonID", view.getId());
                String buttonName = "Search_Barber";

                barberNameArrayList = new ArrayList<>();
                barberRateArrayList = new ArrayList<>();

                if(barberListCurrent.size() == 0){
                    Toast.makeText(getApplicationContext(), "Berberler daha yüklenmedi.", Toast.LENGTH_SHORT).show();
                }else{
                    for(int i  = 0; i < barberListCurrent.size(); i++){
                        barberNameArrayList.add(barberListCurrent.get(i).getBarberName());
                        barberRateArrayList.add(barberListCurrent.get(i).getBarberRate().toString());
                    }

                    Intent intent = new Intent(MapsActivity.this, SearchPopupActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("barberNameArrayList", barberNameArrayList);
                    bundle.putStringArrayList("barberRateArrayList", barberRateArrayList);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }

                firebaseAnalytics.logEvent(buttonName, params);
            }
        });
    }



    /*
    //Eskiden kullanılan Location Permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSION_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                    }
                } else{
                    Toast.makeText(getApplicationContext(), "This app requires location permissions to be granted.", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }
    */


    //Kamera, Lokasyon ve Hafıza permisyonları
    private void verifyPermissions(){
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_FINE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[0]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[1]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[2]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[3]) == PackageManager.PERMISSION_GRANTED){
        }else{
            ActivityCompat.requestPermissions(MapsActivity.this, permissions, 1);
        }
    }
}
