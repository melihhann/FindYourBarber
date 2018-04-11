package com.example.agadimaganda.findyourownbarber;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserDetailsFragment extends Fragment {


    private static final String TAG = "userDetailsFragment";
    private TextView emailTextView;
    private TextView ageTextView;

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



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_details, container, false);
        emailTextView =  (TextView) view.findViewById(R.id.emailTextView);
        ageTextView = (TextView) view.findViewById(R.id.ageTextView);

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


        // TODO: 11.04.2018 BUNA GIRMIYOR.

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

}
