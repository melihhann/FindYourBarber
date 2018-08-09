package com.example.agadimaganda.findyourownbarber.Activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.example.agadimaganda.findyourownbarber.Adapter.PlaceAutocompleteAdapter;
import com.example.agadimaganda.findyourownbarber.Object.Barber;
import com.example.agadimaganda.findyourownbarber.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.Locale;
//Autocomplete imports
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;


public class AddBarberActivity extends AppCompatActivity implements OnMapReadyCallback, OnConnectionFailedListener {

    //Database
    private DatabaseReference mRef;

    //User Interface
    private EditText barberNameEditText;
    private Button button;
    private AutoCompleteTextView AutocompleteTextView;

    //Map
    private MapView mapView;
    private GoogleMap map;
    Geocoder geocoder;
    java.util.List<Address> address = null;
    Marker marker;

    //Classes
    private Barber newBarber = new Barber();
    //private coolMethods coolMethods = new coolMethods();

    //Adapters
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;

    //Variables
    private boolean selectionMade = false;
    private int currentId;
    private Boolean flag = false;
    private BitmapDescriptorFactory bitmapDescriptorFactory;
    private Boolean isMarkerAdded = false;
    private GoogleApiClient mGoogleApiClient;
    private LatLngBounds Lat_Long_Bounds = new LatLngBounds(new LatLng(36,24),new LatLng(42,25));
    private LatLng newBarberMarker;

    private AdapterView.OnItemClickListener AutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final AutocompletePrediction item = mPlaceAutocompleteAdapter.getItem(position);
            if (item != null) {
                final String placeId = item.getPlaceId();
                final CharSequence primaryText = item.getPrimaryText(null);
                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(mGoogleApiClient, placeId);
                placeResult.setResultCallback(UpdatePlaceDetailsCallback);
            }
        }
    };

    private ResultCallback<PlaceBuffer> UpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            try{
                if(places.getStatus().isSuccess() && places.getCount() > 0 && !selectionMade){
                    final Place berberLoc = places.get(0);
                    selectionMade = true;
                    java.util.List<Address> berberAddress = geocoder.getFromLocation(berberLoc.getLatLng().latitude, berberLoc.getLatLng().longitude, 1);
                    if(berberAddress != null){
                        if(berberAddress.get(0).getAdminArea() != null){
                            newBarber.setCity(berberAddress.get(0).getAdminArea());
                        }
                    }
                    newBarber.setLatitude(berberLoc.getLatLng().latitude);
                    newBarber.setLongitude(berberLoc.getLatLng().longitude);
                    if(isMarkerAdded){
                        marker.remove();
                        isMarkerAdded = false;
                    }
                    newBarberMarker = new LatLng(berberLoc.getLatLng().latitude, berberLoc.getLatLng().longitude);
                    marker = map.addMarker(new MarkerOptions()
                            .position(newBarberMarker).icon(bitmapDescriptorFactory.fromResource(R.drawable.makasufakbuyukufak)));

                    isMarkerAdded = true;
                    float zoomLevel = 16.0f; //This goes up to 21
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(newBarberMarker, zoomLevel));


                }
                places.release();
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_barber);

        //AutoComplete Stuff, doldur bosalt in text-field-Vra
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(this,mGoogleApiClient,Lat_Long_Bounds,null);
        AutocompleteTextView = (AutoCompleteTextView) findViewById(R.id.autocomplete_textView);
        AutocompleteTextView.setAdapter(mPlaceAutocompleteAdapter);
        AutocompleteTextView.setOnItemClickListener(AutocompleteClickListener);
        //AutoComplete Stuff, doldur bosalt in text-field-Vra



        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);//

        button =  findViewById(R.id.saveButtonAction);
        barberNameEditText = findViewById(R.id.barberName_editText);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Yarattığımız Bundle tutacağı veriyi Analytics'e yollayacak.
                Bundle params = new Bundle();
                params.putInt("ButtonID", view.getId());
                String buttonName = "Confirm_Add_Barber";

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
                    childRef.child("OVERALLRATING").setValue(0);

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


                    newBarberMarker = new LatLng(latLng.latitude, latLng.longitude);
                    marker = map.addMarker(new MarkerOptions()
                            .position(newBarberMarker).icon(bitmapDescriptorFactory.fromResource(R.drawable.makasufakbuyukufak)));

                    float zoomLevel = 16.0f; //This goes up to 21
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(newBarberMarker, zoomLevel));
                    isMarkerAdded = true;
                    newBarber.setLatitude(latLng.latitude);
                    newBarber.setLongitude(latLng.longitude);

                    try{
                        address = geocoder.getFromLocation(newBarber.getLatitude(), newBarber.getLongitude(), 1);
                        AutocompleteTextView.setText(address.get(0).getAddressLine(0)+" "+address.get(0).getAddressLine(1));

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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
