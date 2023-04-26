package com.example.bluetscan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;


import com.google.android.material.tabs.TabLayout;

import java.util.Objects;
import java.util.Set;


public class MainActivity extends AppCompatActivity implements FragmentA.ListUpdate {
    ListView listView;
    BroadcastReceiver broadcastReceiver;
    FragmentB fb;
    @SuppressLint({"MissingInflatedId", "RestrictedApi"})
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
        FragmentA fa=new FragmentA();
        fb=new FragmentB();
        pg.addFragment(fa, "Scan");
        pg.addFragment(fb, "History");



        viewPager.setAdapter(pg);

    }


    @Override
    public void sendData(BluetoothDevice device) {
        fb.onDataChanged(device);
    }
}
