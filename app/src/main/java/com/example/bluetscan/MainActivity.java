package com.example.bluetscan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;



import com.google.android.material.tabs.TabLayout;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        TabLayout tabs = findViewById(R.id.tabs);

        ViewPager viewPager=findViewById(R.id.views);
        tabs.setupWithViewPager(viewPager);

        tabs.addTab(tabs.newTab().setText("Scan"));
        tabs.addTab(tabs.newTab().setText("History"));

        MyPagerAdapter pg=new MyPagerAdapter(getSupportFragmentManager());

        pg.addFragment(new FragmentA(),"Scan");
        pg.addFragment(new FragmentB(),"History");



        viewPager.setAdapter(pg);


    }

}
