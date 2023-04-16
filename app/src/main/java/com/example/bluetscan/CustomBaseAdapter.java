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
    BluetoothDevice [] btSet;
    LayoutInflater inflater;

    public CustomBaseAdapter(Context ctx, BluetoothDevice [] btArr){
        context=ctx;
        btSet=btArr;
        inflater=LayoutInflater.from(ctx);
    }
    @Override
    public int getCount() {
        return btSet.length;
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
        ImageView img=(ImageView) convertView.findViewById(R.id.imageicon);
        img.setImageResource(R.drawable.bluetooth);
        textView.setText(btSet[i].getAddress());
        return convertView;
    }
}
