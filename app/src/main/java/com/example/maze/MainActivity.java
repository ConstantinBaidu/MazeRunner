package com.example.maze;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.maze.util.ConnectedThread;

public class MainActivity extends AppCompatActivity  {
    BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    private int numPress = 0;
    private ImageView iconBtn;
    private Button bluetoothBtn;
    private Button pathBtn;
    private Button joystickBtn;
    public boolean isConnectedBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(getWindow().FEATURE_NO_TITLE);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        getWindow().setClipToOutline(true);

        setContentView(R.layout.activity_main);

        iconBtn = (ImageView) findViewById(R.id.iconBtn);
        iconBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(numPress>=6) {
                    numPress = 0;
                    openBluetoothActivity();
                }else {
                    numPress++;
                }

            }
        });

        bluetoothBtn = (Button) findViewById(R.id.bluetoothBtn);
        bluetoothBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBluetoothActivity();
            }
        });

        pathBtn = (Button) findViewById(R.id.pathBtn);
        pathBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPathActivity();
            }
        });

        joystickBtn = (Button) findViewById(R.id.joystickBtn);
        joystickBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                openJoystickActivity();
            }
        });


        try {
            isConnectedBt = ConnectedThread.isConnect();
        }catch(Exception ex) {
            isConnectedBt = false;
        }
    }

    public void showMessage(String txt) {
        Toast.makeText(this.getApplicationContext(),
                txt, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        System.exit(0);
    }

    private void openBluetoothActivity() {
            Intent intent = new Intent(this, BluetoothActivity.class);
            startActivity(intent);
    }

    private void openPathActivity() {
        if(isConnectedBt) {
            Intent intent = new Intent(this, PathActivity.class);
            startActivity(intent);
        } else {
            showMessage("Attenzione, connessione bluetooth assente.");
        }
    }

    private void openJoystickActivity() {
        if(isConnectedBt) {
            Intent intent = new Intent(this, JoystickActivity.class);
            startActivity(intent);
        } else {
            showMessage("Attenzione, connessione bluetooth assente.");
        }
    }

}