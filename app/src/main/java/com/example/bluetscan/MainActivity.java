package com.example.bluetscan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;

    private BluetoothAdapter mBluetoothAdapter;
    private ListView mDeviceListView;
    private ArrayAdapter<String> mDeviceListAdapter;
    private ArrayList<BluetoothDevice> mDeviceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Check if Bluetooth is supported on the device
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not supported", Toast.LENGTH_SHORT).show();
            finish();
        }


        // Initialize device list and adapter
        mDeviceList = new ArrayList<BluetoothDevice>();
        mDeviceListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        mDeviceListView = findViewById(R.id.device_list);
        mDeviceListView.setAdapter(mDeviceListAdapter);

        // Set up scan button click listener
        Button scanButton = findViewById(R.id.scan_button);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Bluetooth scan
                startBluetoothScan();
            }
        });
    }

    private void startBluetoothScan() {
        // Check if Bluetooth is enabled
        if (!mBluetoothAdapter.isEnabled()) {
            // Request to enable Bluetooth if it's not already enabled
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
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
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

            // Clear the device list before starting a new scan
            mDeviceList.clear();
            mDeviceListAdapter.clear();

            // Start the scan
            mBluetoothAdapter.getBluetoothLeScanner().startScan(mScanCallback);
            return;
        }

    }

    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            // Get the Bluetooth device from the scan result
            BluetoothDevice device = result.getDevice();

            // Add the device to the list if it's not already in it
            if (!mDeviceList.contains(device)) {
                mDeviceList.add(device);


                mDeviceListAdapter.add(device.getType() + "\n" + device.getAddress());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);

            // Handle scan failure
            Toast.makeText(MainActivity.this, "Bluetooth scan failed with error code " + errorCode, Toast.LENGTH_SHORT).show();
        }
    };
}
