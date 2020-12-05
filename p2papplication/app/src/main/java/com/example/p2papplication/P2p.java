package com.example.p2papplication;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class P2p extends Thread{
    private BluetoothSocket bluetoothSocket;
    Handler hP2p;
    InputStream In;
    OutputStream Out;

    public P2p(BluetoothSocket socket, Handler h)
    {
        this.hP2p = h;
        InputStream tempIn = null;
        OutputStream tempOut = null;
        this.bluetoothSocket = socket;

        try {
            tempIn = bluetoothSocket.getInputStream();
            tempOut = bluetoothSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        In = tempIn;
        Out = tempOut;
    }

    @Override
    public void run() {
        super.run();

        byte[] buffer = new byte[1024];
        int bytes;

        while (true){
            try{
                bytes = In.read(buffer);
                hP2p.obtainMessage(3, bytes,-1, buffer).sendToTarget();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void write(byte[] bytes)
    {
        try{
            Out.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
