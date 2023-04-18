package com.example.bluetscan;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class  FragmentA extends Fragment {



    Button btn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_a, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BluetoothAdapter adapter=BluetoothAdapter.getDefaultAdapter();
        if(!adapter.isEnabled())
        {
            AlertDialogExample.showAlertDialog(getActivity(),"Bluetooth","please enable Bluetooth");
        }
        btn=view.findViewById(R.id.round_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btn.getText()=="start"){
                    btn.setText("Stop");
                }
                else{
                    btn.setText("start");
                }
            }
        });
    }
}