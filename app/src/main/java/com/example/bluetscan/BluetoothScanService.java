package com.example.bluetscan;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
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
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.util.List;

public class BluetoothScanService extends Service {

    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "BluetoothScanServiceChannel";
    private static final String TAG = "BluetoothScanService";
    boolean isBle,isScanning;
    private long INTERVAL;
    SharedPreferences preferences;

    private BluetoothAdapter adapter;
    private BluetoothLeScanner bleScanner;
    private PowerManager.WakeLock wakeLock;
    private ScanCallback scanCallback;
    private BroadcastReceiver broadcastReceiver;
    Runnable stop,intervalRunnable,updateRunnable;
    Handler updateHandler,intervalHandler;

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
        updateHandler=new Handler();
        intervalHandler=new Handler();
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Bluetooth Scanning Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

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
                System.out.println(adapter.startDiscovery());
                // Schedule the next update after the specified interval
                updateHandler.postDelayed(stop, 1000);
                // INTERVAL is the time interval in milliseconds
            }
        };
        isScanning=false;

        String interval = preferences.getString("inter", " ");
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
                int rssi = result.getRssi();
                System.out.println(device.getName()+" "+device.getAddress());

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

        startDiscovery();
    }

    private void stopBluetoothScanning() {

        updateHandler.removeCallbacks(updateRunnable);
        updateHandler.removeCallbacks(stop);
        intervalHandler.removeCallbacks(intervalRunnable);
        stopScan();
    }

    private void registerReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                    sendBroadcast(device,rssi);
                }
            }
        };
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        // Add necessary actions to the filter for Bluetooth-related broadcasts
        registerReceiver(broadcastReceiver, filter);
    }
    private void sendBroadcast(BluetoothDevice device, short rssi) {
        Intent intent = new Intent("com.example.bluetscan.BLUETOOTH_DEVICE_FOUND");
        intent.putExtra("device", device);
        intent.putExtra("rssi", rssi);
        sendBroadcast(intent);
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
                sendBroadcast(result.getDevice(),(short) result.getRssi());
            }
        };

        bleScanner.startScan(scanCallback);
        intervalHandler.postDelayed(intervalRunnable, INTERVAL);
    }
    private void startScan() {
        isScanning = true;
        System.out.println(adapter.startDiscovery());
        updateHandler.postDelayed(stop, 1000);
    }
    private void stopScan() {
        if (isBle) {
            if (isScanning) {
                isScanning = false;
                bleScanner.stopScan(scanCallback);
            }
        } else {
            if (isScanning) {
                isScanning = false;
                adapter.cancelDiscovery();
            }
        }
    }

    private void unregisterReceiver() {
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
    }
}