package com.example.bluetscan;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.util.List;

public class BluetoothScanService extends Service {

    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "BluetoothScanServiceChannel";
    private static final String TAG = "BluetoothScanService";

    private BluetoothAdapter adapter;
    private BluetoothLeScanner bleScanner;
    private PowerManager.WakeLock wakeLock;
    private ScanCallback scanCallback;
    private BroadcastReceiver broadcastReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        adapter = BluetoothAdapter.getDefaultAdapter();
        bleScanner = adapter.getBluetoothLeScanner();
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        }
        registerReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForegroundService();
        acquireWakeLock();
        startBluetoothScanning();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopBluetoothScanning();
        releaseWakeLock();
        unregisterReceiver();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startForegroundService() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Bluetooth Scanning Service")
                .setContentText("Scanning for Bluetooth devices")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(NOTIFICATION_ID, notification);
    }

    private void acquireWakeLock() {
        if (wakeLock != null && !wakeLock.isHeld()) {
            wakeLock.acquire();
        }
    }

    private void releaseWakeLock() {
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
    }

    private void startBluetoothScanning() {
        scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                BluetoothDevice device = result.getDevice();
                short rssi = result.getRssi();
                // Process the scanned Bluetooth device
                Log.d(TAG, "Device: " + device.getName() + ", RSSI: " + rssi);
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
                // Handle batch scan results if needed
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                // Handle scan failure
            }
        };

        // Start classic Bluetooth scanning
        adapter.startDiscovery();

        // Start BLE scanning
        bleScanner.startScan(scanCallback);
    }

    private void stopBluetoothScanning() {
        // Stop classic Bluetooth scanning
        adapter.cancelDiscovery();

        // Stop BLE scanning
        if (bleScanner != null && scanCallback != null) {
            bleScanner.stopScan(scanCallback);
        }
    }

    private void registerReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Handle Bluetooth-related broadcasts
            }
        };
        IntentFilter filter = new IntentFilter();
        // Add necessary actions to the filter for Bluetooth-related broadcasts
        registerReceiver(broadcastReceiver, filter);
    }

    private void unregisterReceiver() {
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
    }
}