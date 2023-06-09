package com.example.bluetscan;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CustomBaseAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    String[] str;
    String[] str1;
    Short [] rssi;
    short rssi_val;

    HashMap<BluetoothDevice,Short> btset;

    public CustomBaseAdapter(Context ctx, HashMap<BluetoothDevice,Short> bset){

        context=ctx;
        btset=bset;
        str=new String[btset.size()];
        str1=new String[btset.size()];
        rssi=new Short[btset.size()];
        int i=0;
        if(btset.size()>0)
        {
            for(BluetoothDevice device:btset.keySet()){
                str[i]=device.getName();
                str1[i]=device.getAddress();
                rssi[i]= btset.get(device);
                i++;
            }
        }
        inflater=LayoutInflater.from(ctx);

    }
    public void setData(HashMap<BluetoothDevice,Short> data) {
        this.btset=data;
        str=new String[btset.size()];
        str1=new String[btset.size()];
        rssi=new Short[btset.size()];
        int i=0;
        if(btset.size()>0)
        {
            for(BluetoothDevice device:btset.keySet()){
                str[i]=device.getName();
                str1[i]=device.getAddress();
                rssi[i]=data.get(device);
                i++;
            }
        }
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return str.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View convertView =inflater.inflate(R.layout.activity_coustam_list_view,null);
        TextView textView=(TextView) convertView.findViewById(R.id.title_textview);
        TextView textView1=(TextView) convertView.findViewById(R.id.addr);
        ImageView img=(ImageView) convertView.findViewById(R.id.imageicon);
        TextView timestamp=(TextView) convertView.findViewById(R.id.timestamp);
        TextView rssiValue=(TextView) convertView.findViewById(R.id.rssi);
        try {
            rssi_val=Short.parseShort(rssi[i].toString());
        }
        catch (Exception e){
            rssi_val=0;
        }

        if (isDarkModeEnabled()) {
            // Use the dark mode version of the icons
            if (rssi_val == 0) {
                img.setImageResource(R.drawable.no_signal_dark);
            } else if (rssi_val > -50) {
                img.setImageResource(R.drawable.excellent_dark);
            } else if (rssi_val > -70) {
                img.setImageResource(R.drawable.good_dark);
            } else if (rssi_val > -80) {
                img.setImageResource(R.drawable.fair_dark);
            } else {
                img.setImageResource(R.drawable.weak_dark);
            }
        } else {
            // Use the light mode version of the icons
            if (rssi_val == 0) {
                img.setImageResource(R.drawable.no_signal);
            } else if (rssi_val > -50) {
                img.setImageResource(R.drawable.excellent);
            } else if (rssi_val > -70) {
                img.setImageResource(R.drawable.good);
            } else if (rssi_val > -80) {
                img.setImageResource(R.drawable.fair);
            } else {
                img.setImageResource(R.drawable.weak);
            }
        }
        textView.setText(str[i]);
        textView1.setText(str1[i]);
        timestamp.setText(timeStamp());
        rssiValue.setText(rssi[i].toString() + " dBm");
        return convertView;
    }

    public String timeStamp() {
        Date current = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = dateFormat.format(current);
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String formattedTime = timeFormat.format(current);
        return formattedDate+"       "+formattedTime;
    }

    private boolean isDarkModeEnabled() {
        int currentNightMode = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES;
    }
}
