package com.androidsmile.labbluetooth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class MainActivity extends AppCompatActivity {

    final String ON = "1";
    final String OFF = "0";

    BluetoothSPP bluetooth;

    Button connect;
    Button on;
    Button off;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("MiTag", "Hola desde log");

        bluetooth = new BluetoothSPP(this);

        connect = (Button) findViewById(R.id.connect);
        on = (Button) findViewById(R.id.on);
        off = (Button) findViewById(R.id.off);

        if (!bluetooth.isBluetoothAvailable()) {
            Toast.makeText(getApplicationContext(), "Bluetooth is not available", Toast.LENGTH_SHORT).show();
            finish();
        }

        bluetooth.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            public void onDeviceConnected(String name, String address) {
                connect.setText("Connected to " + name);
            }

            public void onDeviceDisconnected() {
                connect.setText("Connection lost");
            }

            public void onDeviceConnectionFailed() {
                connect.setText("Unable to connect");
            }
        });

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetooth.getServiceState() == BluetoothState.STATE_CONNECTED) {
                    bluetooth.disconnect();
                } else {
                    Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                }
            }
        });

        on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetooth.send(ON, true);
            }
        });

        off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetooth.send(OFF, true);
            }
        });
        bluetooth.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            public void onDataReceived(byte[] data, String message) {
                Log.d("MiTag", message);
                String string = message;
                String[] parts = string.split(" ");
                String part1 = parts[0];
                String part2 = parts[1];
                String part3 = parts[2];
                String part4 = parts[3];
                String part5 = parts[4];
                String part6 = parts[5];
                String part7 = parts[6];
                Toast.makeText(MainActivity.this, "Humedad del ambiente = "+part2+"\n"+"Temperatura del ambiente = "+part3+" ºC"+"\n"+"pH = "+part5+"\n"+"Humedad del suelo = "+part6+"\n"+"Salinidad = "+part7, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void onStart() {
        super.onStart();
        if (!bluetooth.isBluetoothEnabled()) {
            bluetooth.enable();
        } else {
            if (!bluetooth.isServiceAvailable()) {
                bluetooth.setupService();
                bluetooth.startService(BluetoothState.DEVICE_OTHER);
            }
        }
    }


    public void onDestroy() {
        super.onDestroy();
        bluetooth.stopService();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK)
                bluetooth.connect(data);
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bluetooth.setupService();
            } else {
                Toast.makeText(getApplicationContext()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

}
