package com.example.agadimaganda.findyourownbarber;

import android.content.Intent;
import android.os.Build;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toolbar;

public class BarberViewActivity extends AppCompatActivity {

    private SectionsPageAdapter sectionPageAdapter;
    private ViewPager viewPager;
    private Barber barber = new Barber();
    private android.support.v7.widget.Toolbar barberNameToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barber_view);

        sectionPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(viewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        barberNameToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);


        //MapsActivity'den gelen bilgileri alma.
        Intent intent = getIntent();
        Bundle bundle = getIntent().getExtras();
        if (intent != null && bundle != null) {
            //isNewBarberAdded = bundle.getBoolean("flag");
            barber.setBarberName(bundle.getString("barberName"));
            barber.setLatitude(bundle.getDouble("latitude"));
            barber.setLongitude(bundle.getDouble("longitude"));
            barber.setId(bundle.getInt("id"));
            barber.setCity(bundle.getString("city"));
        }

        if(barber != null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                barberNameToolbar.setTitle(barber.getBarberName());
            }
        }


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 0){
                    DetailsFragment fragment = (DetailsFragment) getSupportFragmentManager()
                            .findFragmentByTag("android:switcher:" + R.id.tabItem+ ":" +
                                    viewPager.getCurrentItem());
                    if(fragment != null){
                        Intent intent = new Intent(BarberViewActivity.this, DetailsFragment.class);
                    }
                }
                else if(position == 1){
                    CommentsFragment fragment = (CommentsFragment) getSupportFragmentManager()
                            .findFragmentByTag("android:switcher:" + R.id.tabItem2 + ":" +
                                    viewPager.getCurrentItem());
                    if(fragment != null){
                        Intent intent = new Intent(BarberViewActivity.this, CommentsFragment.class);
                    }
                }
                else if(position == 2){
                    MediaFragment fragment = (MediaFragment) getSupportFragmentManager()
                            .findFragmentByTag("android:switcher:" + R.id.tabItem3 + ":" +
                                    viewPager.getCurrentItem());
                    if(fragment != null){
                        Intent intent = new Intent(BarberViewActivity.this, MediaFragment.class);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new DetailsFragment(), "detaylar");
        adapter.addFragment(new CommentsFragment(), "yorumlar");
        adapter.addFragment(new MediaFragment(), "medya");
        viewPager.setAdapter(adapter);
    }
}