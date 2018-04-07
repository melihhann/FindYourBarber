package com.example.agadimaganda.findyourownbarber;

import android.content.Intent;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class BarberViewActivity extends AppCompatActivity {

    private SectionsPageAdapter sectionPageAdapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barber_view);

        sectionPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.container);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

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
                        Bundle bundle = new Bundle();
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
        adapter.addFragment(new DetailsFragment(), "details");
        adapter.addFragment(new CommentsFragment(), "comments");
        adapter.addFragment(new MediaFragment(), "media");
        viewPager.setAdapter(adapter);
    }
}
