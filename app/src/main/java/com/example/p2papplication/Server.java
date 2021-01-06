package com.example.p2papplication;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.UUID;

import static android.content.ContentValues.TAG;

public class Server extends Thread{
    int row = 10, col = 20;
    int[][] list = new int[row][col];
    private Handler hServer;
    boolean check = true;
    Thread sToc;

    BluetoothServerSocket mmServerSocket;
    UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    public Server(Handler h)
    {
        //this.check = check;
        this.hServer = h;
        BluetoothServerSocket temp = null;
        try {
            // MY_UUID is the app's UUID string, also used by the client code.
            BluetoothAdapter BA = BluetoothAdapter.getDefaultAdapter();
            temp = BA.listenUsingRfcommWithServiceRecord("Ahhhshiba",
                    MY_UUID);
        } catch (IOException e) {
            Log.e(TAG, "Socket's listen() method failed", e);

        }
        mmServerSocket = temp;
    }

    @Override
    public void run() {
        super.run();
        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned.
        while (true) {
            //if (!check) {
            //    Log.e("t1:<<runnable >>", "strop server");
            //    return;
            //}
            try {
                socket = mmServerSocket.accept();
            } catch (IOException e) {
                Log.e(TAG, "Socket's accept() method failed", e);
                Message m = Message.obtain(); //get null message
                m.what = 0;//Error
                //use the handler to send message
                hServer.sendMessage(m);
                break;
            }

            if (socket != null) {
                Log.e(TAG, "Success");
                // A connection was accepted. Perform work associated with
                // the connection in a separate thread.
                //manageMyConnectedSocket(socket);
                Message m = Message.obtain(); //get null message
                m.what = 2;//Server
                //use the handler to send message
                hServer.sendMessage(m);

                sToc = new P2p(socket, hServer);
                sToc.start();
                try {
                    mmServerSocket.close();
                } catch (IOException e) {
                    Log.e(TAG, "Socket's accept() method failed", e);
                }
                break;
            }
        }
    }

    public void sendMsg(int pos){
        String str = Integer.toString(pos);
        ((P2p) sToc).write(str.getBytes(), true);
    }

    // Closes the connect socket and causes the thread to finish.
    public void cancel() {
        try {
            mmServerSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the connect socket", e);
        }
    }

}
