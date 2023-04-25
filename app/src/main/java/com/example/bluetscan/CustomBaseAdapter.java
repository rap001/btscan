package com.example.bluetscan;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Set;

public class CustomBaseAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    String[] str;
    String[] str1;
    Set<BluetoothDevice> btset;

    public CustomBaseAdapter(Context ctx, Set<BluetoothDevice> bset){
        context=ctx;
        btset=bset;
        str=new String[bset.size()];
        str1=new String[bset.size()];
        int i=0;
        if(bset.size()>0)
        {
            for(BluetoothDevice device:bset){
                str[i]=device.getName();
                str1[i]=device.getAddress();
                i++;
            }
        }
        inflater=LayoutInflater.from(ctx);

    }
    public void changeBtSet(BluetoothDevice device){
        if(btset.size()!=0)
        {
            btset.add(device);
        }
        else{
            System.out.println("empty ptset");
        }

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
        img.setImageResource(R.drawable.bluetooth);
        textView.setText(str[i]);
        textView1.setText(str1[i]);
        return convertView;
    }
}
