package com.example.bluetscan;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;


public class FragmentB extends Fragment {

    private static final int PERMISSION_REQUEST_CODE2 =101 ;
    public static final String ACTION_DEVICE_FOUND = "com.example.bluetscan.ACTION_DEVICE_FOUND";

    HashMap<BluetoothDevice, Short> btset;
    CustomBaseAdapter cd;
    BluetoothAdapter adapter;
    ListView listView;
    private Handler handler;
    private Runnable updateRunnable;
    private static final int UPDATE_INTERVAL = 20000;
    private BroadcastReceiver bluetoothDeviceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if ("com.example.bluetscan.BLUETOOTH_DEVICE_FOUND".equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra("device");
                short rssi = intent.getShortExtra("rssi", Short.MIN_VALUE);
                onDataChanged(device, rssi);
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_b, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = BluetoothAdapter.getDefaultAdapter();
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED||ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {

            // Request permission
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_ADMIN,Manifest.permission.BLUETOOTH_SCAN}, PERMISSION_REQUEST_CODE2);
        }
        IntentFilter filter = new IntentFilter("com.example.bluetscan.BLUETOOTH_DEVICE_FOUND");
        getActivity().registerReceiver(bluetoothDeviceReceiver, filter);
        Set<BluetoothDevice> btset1 = adapter.getBondedDevices();
        btset=new HashMap<BluetoothDevice,Short>();
        for (BluetoothDevice device : btset1){
            btset.put(device,new Short("0"));
        }
        listView=(ListView)view.findViewById(R.id.list);
        cd=new CustomBaseAdapter(getActivity(),btset);
        listView.setAdapter(cd);
        updateDevices();
    }

    public void onDataChanged(BluetoothDevice newSet,short rssi) {
        HashMap<BluetoothDevice,Short> updatedSet = new HashMap<BluetoothDevice, Short>(btset) ;
        updatedSet.put(newSet,new Short(rssi));
        btset.putAll(updatedSet);
        cd.setData(btset);
        cd.notifyDataSetChanged();
    }
    private void updateDevices() {
        handler = new Handler();
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                // Clear the current devices
                btset.clear();
                // Get the updated bonded devices


                // Notify the adapter about the changes
                cd.setData(btset);
                cd.notifyDataSetChanged();

                // Schedule the next update
                handler.postDelayed(this, UPDATE_INTERVAL);
            }
        };

        // Schedule the initial update
        handler.postDelayed(updateRunnable, UPDATE_INTERVAL);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(bluetoothDeviceReceiver);
        handler.removeCallbacks(updateRunnable);
    }
}