package com.example.bluetscan;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;
import java.util.Set;

public class FragmentA extends Fragment {

    private static final int PERMISSION_REQUEST_CODE = 99;
    private static final int PERMISSION_REQUEST_CODE1 = 100;
    public static String interval;
    public static boolean isBle;
    private boolean isScanning;
    private Button btn;
    private ScanCallback scanCallback;
    private ListView lv;
    private Runnable stop;
    private BroadcastReceiver broadcastReceiver;
    private BluetoothAdapter adapter;
    private BluetoothLeScanner ble;
    private SharedPreferences preferences;
    private Handler handler;
    private Handler intervalHandler = new Handler();
    private Handler updateHandler;
    private Runnable updateRunnable,intervalRunnable;
    private long INTERVAL;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_a, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = BluetoothAdapter.getDefaultAdapter();
        handler = new Handler();
        updateHandler = new Handler();
        ble = adapter.getBluetoothLeScanner();



        isScanning = false;
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Request permission
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        }
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {

            // Request permission
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH_SCAN}, PERMISSION_REQUEST_CODE1);
        }

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        System.out.println(preferences.getString("inter", null));

        if (!adapter.isEnabled()) {
            AlertDialogExample.showAlertDialog(getActivity(), "Bluetooth", "Enable Bluetooth");
        }

        btn = view.findViewById(R.id.round_button);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isScanning) {
                    if (!adapter.isEnabled()) {
                        AlertDialogExample.showAlertDialog(getActivity(), "Bluetooth", "Enable Bluetooth");
                    }
                    interval = preferences.getString("inter", " ");
                    switch (interval) {
                        case "6 sec":
                            INTERVAL = 6000;
                            break;
                        case "1 sec":
                            INTERVAL = 1000;
                            break;
                        case "1.5 sec":
                            INTERVAL = 1500;
                            break;
                        case "2 sec":
                            INTERVAL = 2000;
                            break;
                        case "3 sec":
                            INTERVAL = 3000;
                            break;
                    }
                    isBle = preferences.getBoolean("useBLE", true);
                    isScanning=true;
                    Intent serviceIntent = new Intent(getActivity(), BluetoothScanService.class);
                    getActivity().startService(serviceIntent);
                    btn.setText("Stop");
                } else {
                    btn.setText("Scan");
                    Intent stopService = new Intent(getActivity(), BluetoothScanService.class);
                    getActivity().stopService(stopService);
                    isScanning=false;

                }
            }
        });

    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        //code here
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Stop discovery and unregister the receiver
        if (adapter != null) {
            adapter.cancelDiscovery();
        }
        getActivity().unregisterReceiver(broadcastReceiver);
        updateHandler.removeCallbacks(updateRunnable);
    }

}
