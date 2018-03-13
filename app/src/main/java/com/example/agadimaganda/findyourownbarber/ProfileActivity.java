package com.example.agadimaganda.findyourownbarber;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class ProfileActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.nav_home:
                        Intent intent0 = new Intent(ProfileActivity.this, MapsActivity.class);
                        //intent0.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);﻿
                        startActivity(intent0);
                        break;

                    case R.id.nav_profile:
                        break;

                    case R.id.nav_settings:
                        Intent intent2 = new Intent(ProfileActivity.this, SettingsActivity.class);
                        //intent2.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);﻿
                        startActivity(intent2);
                        break;
                }

                return false;
            }
        });
    }

}
