package com.example.agadimaganda.findyourownbarber.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.agadimaganda.findyourownbarber.R;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {

    //User Interface
    private BottomNavigationView bottomNavigationView;
    private TextView logoutTextView;

    //Database Reference
    private FirebaseAuth auth;
    private FirebaseAnalytics firebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        logoutTextView = (TextView) findViewById(R.id.logoutTextView);
        logoutTextView.setText("Oturumu Kapat");
        auth = FirebaseAuth.getInstance();
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.nav_home:
                        Intent intent0 = new Intent(SettingsActivity.this, MapsActivity.class);
                        //intent0.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(intent0);
                        break;

                    case R.id.nav_profile:
                        Intent intent1 = new Intent(SettingsActivity.this, ProfileActivity.class);
                        //intent1.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(intent1);
                        break;

                    case R.id.nav_settings:
                        break;
                }

                return false;
            }
        });
    }


    public void logout(View view) {

        //Yarattığımız Bundle tutacağı veriyi Analytics'e yollayacak.
        Bundle params = new Bundle();
        params.putInt("ButtonID", view.getId());
        String buttonName = "logout";

        auth.signOut();
        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
        startActivity(intent);
        firebaseAnalytics.logEvent(buttonName, params);
    }
}
