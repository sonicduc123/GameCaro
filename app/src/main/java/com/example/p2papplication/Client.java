package com.example.p2papplication;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ContentValues;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.util.UUID;

import static android.service.controls.ControlsProviderService.TAG;

public class Client extends Thread{
    public Handler hClient;
    Thread cTos;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    public Client(BluetoothDevice device, Handler h)
    {
        this.hClient = h;
        BluetoothSocket tmp = null;
        mmDevice = device;
        try {
            // Get a BluetoothSocket to connect with the given BluetoothDevice.
            // MY_UUID is the app's UUID string, also used in the server code.
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            Log.e(TAG, "Socket's create() method failed", e);
        }
        mmSocket = tmp;

    }

    @Override
    public void run() {
        super.run();
        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            mmSocket.connect();
            Log.e(ContentValues.TAG, "Success");
            Message m = Message.obtain(); //get null message
            m.what = 1;//client
            //use the handler to send message
            hClient.sendMessage(m);

            cTos = new P2p(mmSocket, hClient);
            cTos.start();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and return.
            try {
                mmSocket.close();
            } catch (IOException closeException) {
                Log.e(TAG, "Could not close the client socket", closeException);
            }
            return;
        }

    }
    public void sendMsg(int i){
        String str = Integer.toString(i);

        ((P2p) cTos).write(str.getBytes(), true);
    }

    public void sendNamePlayer(String namePlayer) {
        ((P2p) cTos).write(namePlayer.getBytes(), false);
    }

    // Closes the client socket and causes the thread to finish.
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the client socket", e);
        }
    }
}
