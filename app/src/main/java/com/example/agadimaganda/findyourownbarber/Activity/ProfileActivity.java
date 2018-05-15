package com.example.agadimaganda.findyourownbarber.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.agadimaganda.findyourownbarber.Adapter.SectionsPageAdapter;
import com.example.agadimaganda.findyourownbarber.R;
import com.example.agadimaganda.findyourownbarber.Fragment.UserCommentsFragment;
import com.example.agadimaganda.findyourownbarber.Fragment.UserDetailsFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    //User Interface
    private BottomNavigationView bottomNavigationView;
    private android.support.v7.widget.Toolbar userNameToolbar;
    private ViewPager viewPager;
    private SectionsPageAdapter sectionPageAdapter;

    //Database Connection
    FirebaseAuth auth;
    DatabaseReference databaseReference;

    //Variables
    String userName;
    String userLastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);

        sectionPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.containerProfile);
        setupViewPager(viewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        userNameToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        //Kullanıcı ismini title olarak alma.
        Query query = databaseReference.child("USERS").child(auth.getCurrentUser().getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userName = (String) dataSnapshot.child("NAME").getValue();
                userLastName = (String) dataSnapshot.child("LASTNAME").getValue();
                userNameToolbar.setTitle(userName + " " + userLastName); 
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


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
                    }
                }
                else if(position == 1){
                    UserCommentsFragment fragment = (UserCommentsFragment) getSupportFragmentManager()
                            .findFragmentByTag("android:switcher:" + R.id.tabItem2 + ":" +
                                    viewPager.getCurrentItem());
                    if(fragment != null){
                        Intent intent = new Intent(ProfileActivity.this, UserCommentsFragment.class);
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
