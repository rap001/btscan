package com.example.bluetscan;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainer;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Set;

public class FragmentA extends Fragment {


   public static String intervel;
   public static boolean isBle;
    Button btn;

    ListView lv;
    BroadcastReceiver broadcastReceiver;
    BluetoothAdapter adapter;
    Set<BluetoothDevice> btset;



    public interface ListUpdate
    {
        void sendData(BluetoothDevice device);
    }
    private ListUpdate listener;

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
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        System.out.println(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("inter", null));

        if (!adapter.isEnabled()) {
            AlertDialogExample.showAlertDialog(getActivity(), "Bluetooth", "Enable Bluetooth");
        }
        btn = view.findViewById(R.id.round_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btn.getText() == "Scan") {
                    if (!adapter.isEnabled()) {
                        AlertDialogExample.showAlertDialog(getActivity(), "Bluetooth", "Enable Bluetooth");
                    }
                    intervel=preferences.getString("inter"," ");
                    isBle=preferences.getBoolean("useBLE",true);
                    Toast toast = Toast.makeText(getContext(),intervel,Toast.LENGTH_SHORT);
                    toast.show();
                    if (isBle==false){
                        System.out.println(startDiscovery());

                    }
                    else{
                        //this part is for ble
                         startLeScan();
                    }
                    System.out.println(isBle);

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
                    if(listener!=null){
                        listener.sendData(device);
                    }
                    //System.out.println(deviceName + " " + deviceHardwareAddress);
                }
            }
        };
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (ListUpdate) ((MainActivity) getActivity());
        }
        catch (ClassCastException e) {
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
    }

    private boolean startDiscovery() {
        // Register for discovery events
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getActivity().registerReceiver(broadcastReceiver, filter);

        return adapter.startDiscovery();

    }
    private void startLeScan() {



        adapter.startDiscovery();
    }
}