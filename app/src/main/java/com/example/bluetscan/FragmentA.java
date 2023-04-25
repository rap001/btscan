package com.example.bluetscan;

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
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Set;

public class FragmentA extends Fragment {


    Button btn;
    ListView lv;
    BroadcastReceiver broadcastReceiver;
    BluetoothAdapter adapter;
    Set<BluetoothDevice> btset;

    public interface OnDataChangeListener {
        void onDataChanged(BluetoothDevice newSet);
    }

    private OnDataChangeListener listener;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_a, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = BluetoothAdapter.getDefaultAdapter();
        if (!adapter.isEnabled()) {
            AlertDialogExample.showAlertDialog(getActivity(), "Blueetooth", "Enable Bluetooth");
        }
        btn = view.findViewById(R.id.round_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btn.getText() == "Scan") {
                    if (!adapter.isEnabled()) {
                        AlertDialogExample.showAlertDialog(getActivity(), "Blueetooth", "Enable Bluetooth");
                    }
                    System.out.println(startDiscovery());
                    btn.setText("Stop");
                } else {
                    adapter.cancelDiscovery();
                    if (broadcastReceiver != null) {
                        System.out.println(broadcastReceiver.getResultData() + "  " + broadcastReceiver.toString());
                    }
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
                    String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress();
                    System.out.println(deviceName + " " + deviceHardwareAddress);
                }
            }
        };
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Stop discovery and unregister the receiver
        if (adapter != null) {

            adapter.cancelDiscovery();
        }
        getActivity().unregisterReceiver(broadcastReceiver);
    }

    private boolean startDiscovery() {
        // Register for discovery events
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getActivity().registerReceiver(broadcastReceiver, filter);

        return adapter.startDiscovery();

    }
}