package com.example.agadimaganda.findyourownbarber;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static java.lang.StrictMath.toIntExact;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private final static int MY_PERMISSION_FINE_LOCATION = 101;
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton addBarberButton;
    private Marker marker;
    private BitmapDescriptorFactory bitmapDescriptorFactory;//Yarayabilir.
    private ArrayList<Barber> barberListUpdated = new ArrayList<>();
    private ArrayList<Barber> barberListCurrent = new ArrayList<>();
    private Boolean isNewBarberAdded = false;
    private DatabaseReference refForbarberList;


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
                Intent intent2 = new Intent(MapsActivity.this, AddBarberActivity.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent2);
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


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

                        if(idLong != null){
                        barber.setId(idLong.intValue());
                        barber.setBarberName(barberName);
                        barber.setLatitude(latitude);
                        barber.setLongitude(longitude);
                        barber.setCity(city);

                        LatLng newBarberMarker = new LatLng(barber.getLatitude(), barber.getLongitude());
                        marker = mMap.addMarker(new MarkerOptions()
                                .position(newBarberMarker)
                                .title(barber.getBarberName())
                                //.snippet(Berber Puani)
                                .icon(bitmapDescriptorFactory.fromResource(R.drawable.makasufakbuyukufak)));

                        marker.setTag(barber);
                       }
                    //}
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

                        Barber barber = new Barber();
                        Long idLong  = (Long) snapshot.child("ID").getValue();
                            if(idLong != null){
                                barber.setId(idLong.intValue());
                                barber.setBarberName(barberName);
                                barber.setLatitude(latitude);
                                barber.setLongitude(longitude);
                                barber.setCity(city);
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

                intent.putExtras(bundle);
                startActivity(intent);
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
