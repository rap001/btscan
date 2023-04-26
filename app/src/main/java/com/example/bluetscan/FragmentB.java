package com.example.bluetscan;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class FragmentB extends Fragment {

    Set<BluetoothDevice> btset;
    CustomBaseAdapter cd;
    BluetoothAdapter adapter;
    ListView listView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_b, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter=BluetoothAdapter.getDefaultAdapter();
        btset =adapter.getBondedDevices();
        listView=(ListView)view.findViewById(R.id.list);
        cd=new CustomBaseAdapter(getActivity(),btset);
        listView.setAdapter(cd);
    }

    public void onDataChanged(BluetoothDevice newSet) {
        System.out.println(newSet.getAddress());
        Set<BluetoothDevice> updatedSet = new HashSet<>(btset);
        updatedSet.add(newSet);
        btset = updatedSet;
        cd.setData(btset);
        cd.notifyDataSetChanged();
    }


}
