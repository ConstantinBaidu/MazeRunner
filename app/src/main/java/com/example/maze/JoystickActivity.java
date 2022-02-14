package com.example.maze;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.maze.util.ConnectedThread;

public class JoystickActivity extends AppCompatActivity {

    public ImageView btnNorth;
    public ImageView btnSouth;
    public ImageView btnWest;
    public ImageView btnEast;
    public ImageView btnPlus;
    public ImageView btnMinus;
    public ImageView btnLight;
    public TextView velocitatxt;
    public boolean isConnectedBt;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(getWindow().FEATURE_NO_TITLE);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        getWindow().setClipToOutline(true);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joystick);

        sendCommand("J");

        velocitatxt = (TextView) findViewById(R.id.velocitatxt);

        btnPlus = (ImageView) findViewById(R.id.btnPlus);
        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int n = Integer.parseInt(velocitatxt.getText().toString());
                if(n<400  && isConnectedBt) {
                    int v = n + 50;
                    velocitatxt.setText(String.valueOf(v));
                }
                if(isConnectedBt)
                    sendCommand("P");
            }
        });

        btnMinus = (ImageView) findViewById(R.id.btnMinus);
        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int n = Integer.parseInt(velocitatxt.getText().toString());
                if (n > 0 && isConnectedBt) {
                    int v = n - 50;
                    velocitatxt.setText(String.valueOf(v));
                }
                if(isConnectedBt)
                    sendCommand("M");
            }
        });

        btnLight = (ImageView) findViewById(R.id.btnLight);
        btnLight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // Pressed
                    if(isConnectedBt)
                        sendCommand("L");
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Released
                    if(isConnectedBt)
                        sendCommand("l");
                }
                return true;
            }
        });

        btnNorth = (ImageView) findViewById(R.id.btnNorth);
        btnNorth.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // Pressed
                    if(isConnectedBt)
                        sendCommand("A");
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Released
                    if(isConnectedBt)
                        sendCommand("s");
                }
                return true;
            }
        });

        btnSouth = (ImageView) findViewById(R.id.btnSouth);
        btnSouth.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // Pressed
                    if(isConnectedBt)
                        sendCommand("I");
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Released
                    if(isConnectedBt)
                        sendCommand("s");
                }
                return true;
            }
        });

        btnWest = (ImageView) findViewById(R.id.btnWest);
        btnWest.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // Pressed
                    if(isConnectedBt)
                        sendCommand("D");
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Released
                    if(isConnectedBt)
                        sendCommand("s");
                }
                return true;
            }
        });

        btnEast = (ImageView) findViewById(R.id.btnEast);
        btnEast.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // Pressed
                    if(isConnectedBt)
                        sendCommand("S");
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Released
                    if(isConnectedBt)
                        sendCommand("s");
                }
                return true;
            }
        });

        try {
            isConnectedBt = ConnectedThread.isConnect();
        }catch(Exception ex) {
            isConnectedBt = false;
        }
    }

    public void sendCommand(String a) {
        if(ConnectedThread.isConnect()) {
            String atxt = a;
            ConnectedThread.write(atxt.getBytes());
        }
    }
}