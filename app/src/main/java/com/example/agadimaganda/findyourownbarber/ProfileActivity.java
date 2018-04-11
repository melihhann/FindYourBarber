package com.example.agadimaganda.findyourownbarber;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    //User Interface
    private BottomNavigationView bottomNavigationView;
    private android.support.v7.widget.Toolbar userNameToolbar;
    private ViewPager viewPager;
    private  SectionsPageAdapter sectionPageAdapter;

    //Firebase Connection
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;

    //Variables
    private  String name;
    private String lastname;
    private String email;
    private int age;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);

        sectionPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.containerAga);
        setupViewPager(viewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        userNameToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);


        Firebase.setAndroidContext(this);
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference childRef = databaseReference.child("USERS");


        // TODO: 11.04.2018 BUNA GIRMIYOR. 
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null ){
                    email = firebaseAuth.getCurrentUser().getEmail();

                    childRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                //String barberName = (String) snapshot.child("BARBERNAME").getValue();
                                String userEmailComp = (String) snapshot.child("EMAIL").getValue();

                                if(userEmailComp.equalsIgnoreCase(email)){
                                    name = (String) snapshot.child("NAME").getValue();
                                    lastname = (String) snapshot.child("LASTNAME").getValue();
                                    Long userAgeLong = (Long) dataSnapshot.getValue();
                                    age = userAgeLong.intValue();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        };



        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 0){
                    UserDetailsFragment fragment = (UserDetailsFragment) getSupportFragmentManager()
                            .findFragmentByTag("android:switcher:" + R.id.tabItem+ ":" +
                                    viewPager.getCurrentItem());
                    if(fragment != null){
                        Intent intent = new Intent(ProfileActivity.this, UserDetailsFragment.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("email", email);
                        bundle.putInt("age", age);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }
                else if(position == 1){
                    UserCommentsFragment fragment = (UserCommentsFragment) getSupportFragmentManager()
                            .findFragmentByTag("android:switcher:" + R.id.tabItem2 + ":" +
                                    viewPager.getCurrentItem());
                    if(fragment != null){
                        Intent intent = new Intent(ProfileActivity.this, UserCommentsFragment.class);
                        Bundle bundle = new Bundle();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.nav_home:
                        Intent intent0 = new Intent(ProfileActivity.this, MapsActivity.class);
                        startActivity(intent0);
                        break;

                    case R.id.nav_profile:
                        break;

                    case R.id.nav_settings:
                        Intent intent2 = new Intent(ProfileActivity.this, SettingsActivity.class);
                        startActivity(intent2);
                        break;
                }

                return false;
            }
        });
    }


    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new UserDetailsFragment(), "detaylar");
        adapter.addFragment(new UserCommentsFragment(), "yorumlar");
        viewPager.setAdapter(adapter);
    }

}
