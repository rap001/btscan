package com.example.bluetscan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ListView;


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

        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Set<BluetoothDevice> deviceSet=adapter.getBondedDevices();
        BluetoothDevice[] deviceArray = new BluetoothDevice[deviceSet.size()];
        deviceSet.toArray(deviceArray);
        CustomBaseAdapter customBaseAdapter=new CustomBaseAdapter(getApplicationContext(),deviceArray);
        listView=(ListView) listView.findViewById(R.id.customListView);
        listView.setAdapter(customBaseAdapter);

    }

}
