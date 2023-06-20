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

    private ListUpdate listener;

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
        stop = new Runnable() {
            @Override
            public void run() {
                if (adapter.isEnabled())
                    adapter.cancelDiscovery();
                updateHandler.postDelayed(updateRunnable, INTERVAL);
            }
        };

        intervalRunnable = new Runnable() {
            @Override
            public void run() {
                if (isBle) {
                    if (isScanning) {
                        stopScan();
                    } else {
                        startLeScan();
                    }

                    // Schedule the next execution after the interval
                    intervalHandler.postDelayed(this, INTERVAL);
                }
            }
        };

        updateRunnable = new Runnable() {
            @Override
            public void run() {
                // Call the method to update the devices here
                adapter.startDiscovery();
                // Schedule the next update after the specified interval
                updateHandler.postDelayed(stop, 1000);
                // INTERVAL is the time interval in milliseconds
            }
        };

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
                    startDiscovery();
                    btn.setText("Stop");
                } else {
                    stopScan();
                    updateHandler.removeCallbacks(updateRunnable);
                    updateHandler.removeCallbacks(stop);
                    intervalHandler.removeCallbacks(intervalRunnable);
                    btn.setText("Scan");
                }
            }
        });

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                    String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress();
                    if (listener != null) {
                        listener.sendData(device, rssi);
                    }
                    System.out.println(deviceName + " " + deviceHardwareAddress);
                }
            }
        };

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getActivity().registerReceiver(broadcastReceiver, filter);
    }

    private void updateDevices() {
        isBle = preferences.getBoolean("useBLE", true);
        adapter.cancelDiscovery();
        if (!isScanning) {
            startDiscovery();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (ListUpdate) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Error in retrieving data. Please try again");
        }
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

    private void startDiscovery() {
        isBle = preferences.getBoolean("useBLE", true);
        if (!isBle) {
            if (!isScanning) {
                startScan();
            }
        } else {
            if (!isScanning) {
                startLeScan();
            }
        }
    }

    private void startLeScan() {
        isScanning = true;
        scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                listener.sendData(result.getDevice(), (short) result.getRssi());
                System.out.println(result.getDevice().getName() + " " + result.getDevice().getAddress());
            }
        };

        ble.startScan(scanCallback);
        intervalHandler.postDelayed(intervalRunnable, INTERVAL);
    }

    private void startScan() {
        isScanning = true;
        adapter.startDiscovery();
        updateHandler.postDelayed(stop, 1000);
    }

    private void stopScan() {
        if (isBle) {
            if (isScanning) {
                isScanning = false;
                ble.stopScan(scanCallback);
                intervalHandler.removeCallbacks(intervalRunnable);
            }
        } else {
            if (isScanning) {
                isScanning = false;
                adapter.cancelDiscovery();
                updateHandler.removeCallbacks(stop);
            }
        }
    }

    public interface ListUpdate {
        void sendData(BluetoothDevice device, short rssi);
    }
}
