package com.example.bluetscan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;


import com.google.android.material.tabs.TabLayout;

import java.util.Set;


public class MainActivity extends AppCompatActivity {
    ListView listView;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabLayout tabs = findViewById(R.id.tabs);

        ViewPager viewPager = findViewById(R.id.views);
        tabs.setupWithViewPager(viewPager);

        tabs.addTab(tabs.newTab().setText("Scan"));
        tabs.addTab(tabs.newTab().setText("History"));

        MyPagerAdapter pg = new MyPagerAdapter(getSupportFragmentManager());

        pg.addFragment(new FragmentA(), "Scan");
        pg.addFragment(new FragmentB(), "History");


        viewPager.setAdapter(pg);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int item_id= item.getItemId();

        if (item_id==R.id.settings){
            setContentView(R.layout.settings);
        }

        return true;

    }
}
