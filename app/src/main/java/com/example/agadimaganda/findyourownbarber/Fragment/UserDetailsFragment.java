package com.example.agadimaganda.findyourownbarber.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.agadimaganda.findyourownbarber.Activity.BarberViewActivity;
import com.example.agadimaganda.findyourownbarber.Adapter.FavoriteBarberListAdapter;
import com.example.agadimaganda.findyourownbarber.Adapter.UserCommentListAdapter;
import com.example.agadimaganda.findyourownbarber.Object.Barber;
import com.example.agadimaganda.findyourownbarber.R;
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserDetailsFragment extends Fragment {


    private static final String TAG = "userDetailsFragment";

    //User Interface
    private TextView emailTextView;
    private TextView ageTextView;
    private ListView favoriteBarbersListView;
    private TextView favoriteBarberTextView;

    //Firebase Connection
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;

    //Variables
    private  String name;
    private String lastname;
    private String email;
    private int age;
    private String stringAge;
    private ArrayList<Barber> favoriteBarberList;
    private Boolean flag = false;
    Barber barber = new Barber();



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_details, container, false);
        emailTextView =  (TextView) view.findViewById(R.id.emailTextView);
        ageTextView = (TextView) view.findViewById(R.id.ageTextView);
        favoriteBarbersListView = (ListView) view.findViewById(R.id.favoriteBarbersListView);
        favoriteBarberTextView = (TextView) view.findViewById(R.id.favorite_text_view);

        favoriteBarberList = new ArrayList<>();



        //Favori berberleri gosterme
        Query query = databaseReference.child("USERS").child(auth.getCurrentUser().getUid()).child("FAVORITES");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Barber barber = new Barber();
                    barber.setBarberName(snapshot.getKey());
                    favoriteBarberList.add(barber);
                }

                if(favoriteBarberList.size() <= 0){
                    favoriteBarberTextView.setText("Favori Berber Bulunmamaktadır");
                }else{
                    favoriteBarberTextView.setText("Favori Berberler");
                }

                if(getActivity() != null){
                    FavoriteBarberListAdapter adapter = new FavoriteBarberListAdapter(getActivity(), R.layout.layout_favorite_barber, favoriteBarberList);
                    favoriteBarbersListView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        /*
        //Basılan berberin sayfasına gitme şeysi. Sıkıntıları var.
        favoriteBarbersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Query query = databaseReference.child("BARBERS");

                final int position = i;

                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                            String barberNameComp = favoriteBarberList.get(position).getBarberName().toUpperCase().replace(" ", "");
                            if(barberNameComp.equalsIgnoreCase(snapshot.getKey())){

                                flag = true;
                                String barberName = (String) snapshot.child("BARBERNAME").getValue();
                                Double barberRate = (Double) snapshot.child("OVERALLRATING").getValue();
                                String city = (String) snapshot.child("CITY").getValue();

                                barber.setBarberName(barberName);
                                barber.setBarberRate(barberRate);
                                barber.setCity(city);
                            }
                        }

                        if(flag){
                            intentMethod();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
        */



        return view;


    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getActivity().getIntent();
        Bundle bundle = getActivity().getIntent().getExtras();

        auth = FirebaseAuth.getInstance();
        email = auth.getCurrentUser().getEmail();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference childRef = databaseReference.child("USERS");

        if(email != null ){
            childRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        String userEmailComp = (String) snapshot.child("EMAIL").getValue();

                        if(userEmailComp.equalsIgnoreCase(email)){
                            name = (String) snapshot.child("NAME").getValue();
                            lastname = (String) snapshot.child("LASTNAME").getValue();
                            Long userAgeLong = (Long) snapshot.child("AGE").getValue();
                            age = userAgeLong.intValue();
                            stringAge = String.valueOf(age);
                            emailTextView.setText(email);
                            ageTextView.setText(stringAge);
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


    public void intentMethod(){

        Intent intent = new Intent(getActivity(), BarberViewActivity.class);
        Bundle bundle = new Bundle();

        bundle.putString("barberName", barber.getBarberName());
        // bundle.putInt("id", barber.getId());
        bundle.putString("city", barber.getCity());
        bundle.putDouble("rating",barber.getBarberRate());
        intent.putExtras(bundle);
        startActivity(intent);
    }

}
