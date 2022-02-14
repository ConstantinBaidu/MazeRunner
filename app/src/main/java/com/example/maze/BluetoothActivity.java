package com.example.maze;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.maze.util.BluetoothDevices;
import com.example.maze.util.ConnectedThread;
import com.example.maze.util.DeviceAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class BluetoothActivity extends AppCompatActivity {
    public Button searchBtn;
    //Attivo l'adattatore Bt

    BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    BluetoothSocket btSocket = null;
    BluetoothDevice btDevice;
    public ConnectedThread cThread = null;
    ArrayList<BluetoothDevice> btDeviceList = new ArrayList<BluetoothDevice>();

    Set<BluetoothDevice> btDevices;
    ArrayList<BluetoothDevices> deviceList =  new ArrayList<BluetoothDevices>();
    public Handler btHandler;

    int REQUEST_ENABLE_BT = 1;
    public ListView btListView;

    public boolean luce = false;

    public static final String MY_APP = "Maze";
    public String MODULE_MAC = "00:21:13:04:B1:EB"; //00:21:13:04:B1:EB
    public String MODULE_NAME = "HC-06"; //HC-06
    public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public ImageView iconDevice;
    public TextView nameDevice;
    public TextView macDevice;
    public TextView statusDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(getWindow().FEATURE_NO_TITLE);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        getWindow().setClipToOutline(true);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        iconDevice = findViewById(R.id.iconDevice);
        nameDevice = findViewById(R.id.nameDevice);
        macDevice = findViewById(R.id.macDevice);
        statusDevice = findViewById(R.id.statusDevice);

        if(btSocket!=null) {
            newView();
        }

        btListView = (ListView) findViewById(R.id.btListView);

        searchBtn = (Button) findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(searchBtn.getText().equals("Vedi Dispositivi Bluetooth")) {
                    if(btAdapter.isEnabled()) {
                        setDevice();
                        searchBtn.setVisibility(View.INVISIBLE);
                    }else{
                        Intent enableBt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBt, REQUEST_ENABLE_BT);
                    }
                }else if(searchBtn.getText().equals("Termina Connessione")) {
                    cThread.cancel();
                    onBackPressed();
                }

                    /*
                    try {
                        String sendtxt = "s";
                        cThread.write(sendtxt.getBytes());
                        btSocket = null;
                        newView();
                    }catch (Exception ex) {
                        Toast.makeText(getApplicationContext(),
                                "Si è verificato un errore, prova a riconnetterti.", Toast.LENGTH_LONG).show();
                    }
                     */

            }
        });

        setDevice();

        //Verifico se il mio Bt è attivato
        if(!btAdapter.isEnabled()) {
            searchBtn.setText("Vedi Dispositivi Bluetooth");
            searchBtn.setVisibility(View.VISIBLE);
            Intent enableBt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBt, REQUEST_ENABLE_BT);
        }
    }

    private void setDevice() {
        btDevices = btAdapter.getBondedDevices();
        if(btDevices.size()>0) {
            for(BluetoothDevice device:btDevices) {
                btDeviceList.add(device);
                deviceList.add(new BluetoothDevices(device.getName(), device.getAddress()));
            }

            DeviceAdapter btAdapter = new DeviceAdapter(this, R.layout.listrow, deviceList);
            btListView.setAdapter(btAdapter);
            btListView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        // Pressed
                        findViewById(R.id.mazeLogo).setVisibility(View.INVISIBLE);
                        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        // Released
                        initiateBluetoothProcess();
                    }
                    return true;
                }
            });
            btListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    MODULE_MAC = btDeviceList.get(i).getAddress();
                    MODULE_NAME = btDeviceList.get(i).getName();
                }

            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == REQUEST_ENABLE_BT){
            initiateBluetoothProcess();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void initiateBluetoothProcess(){

        if(btAdapter.isEnabled()){

            //attempt to connect to bluetooth module
            BluetoothSocket tmp = null;
            btDevice = btAdapter.getRemoteDevice(MODULE_MAC);

            //create socket
            try {
                tmp = btDevice.createRfcommSocketToServiceRecord(MY_UUID);
                btSocket = tmp;
                btSocket.connect();
                Log.e("[BLUETOOTH]","Connected to: "+btDevice.getName());
            }catch(IOException e){
                try{btSocket.close();}catch(IOException c){return;}
                Log.e("[BLUETOOTH]","DISCONNECTED to: "+btDevice.getName());
                findViewById(R.id.mazeLogo).setVisibility(View.VISIBLE);
                findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(),
                        "Si è verificato un errore.", Toast.LENGTH_LONG).show();
            }

            Log.e("[BLUETOOTH]", "Creating handler");
            btHandler = new Handler(Looper.getMainLooper()){
                @Override
                public void handleMessage(Message msg) {
                    //super.handleMessage(msg);
                    if(msg.what == ConnectedThread.RESPONSE_MESSAGE){
                        String txt = (String)msg.obj;
                    }
                }
            };

            Log.e("[BLUETOOTH]", "Creating and running Thread");
            cThread = new ConnectedThread(btSocket,btHandler);
            cThread.start();
            cThread.isAlive();
        }

        if(btSocket.isConnected()) {
            findViewById(R.id.mazeLogo).setVisibility(View.VISIBLE);
            findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
            newView();
        }

    }

    public void newView() {
        if(btSocket.isConnected()) {
            btListView.setVisibility(View.INVISIBLE);
            iconDevice.setVisibility(View.VISIBLE);
            nameDevice.setText(MODULE_NAME);
            macDevice.setText(MODULE_MAC);
            nameDevice.setVisibility(View.VISIBLE);
            macDevice.setVisibility(View.VISIBLE);
            statusDevice.setVisibility(View.VISIBLE);
            searchBtn.setText("Termina Connessione");
            searchBtn.setVisibility(View.VISIBLE);
        }else {
            btListView.setVisibility(View.VISIBLE);
            iconDevice.setVisibility(View.INVISIBLE);
            nameDevice.setText(MODULE_NAME);
            macDevice.setText(MODULE_MAC);
            nameDevice.setVisibility(View.INVISIBLE);
            macDevice.setVisibility(View.INVISIBLE);
            statusDevice.setVisibility(View.INVISIBLE);
            searchBtn.setVisibility(View.INVISIBLE);
        }
    }
}