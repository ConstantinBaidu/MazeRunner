package com.example.maze.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.maze.BluetoothActivity;
import com.example.maze.R;

import java.util.ArrayList;
//Classe che serve per stampare un Layout XML adattato in un ListView, una Adapter.
public class DeviceAdapter extends ArrayAdapter<BluetoothDevices> {
    private Context mContext;
    private int mResource;


    public DeviceAdapter(@NonNull BluetoothActivity context, int resource, @NonNull ArrayList<BluetoothDevices> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        convertView = layoutInflater.inflate(mResource, parent, false);

        TextView txtName = convertView.findViewById(R.id.txtName);
        TextView txtSub = convertView.findViewById(R.id.txtSub);

        txtName.setText(getItem(position).getNomeDispositivo());
        txtSub.setText(getItem(position).getIndirizzoMac());

        return convertView;
    }
}
