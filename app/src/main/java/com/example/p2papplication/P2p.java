package com.example.p2papplication;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class P2p extends Thread{
    private BluetoothSocket bluetoothSocket;
    Handler hP2p;
    InputStream In;
    OutputStream Out;
    boolean isPosition = false;

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
                if (isPosition) {
                    hP2p.obtainMessage(3, bytes,-1, buffer).sendToTarget();
                }
                else {
                    hP2p.obtainMessage(4, bytes,-1, buffer).sendToTarget();
                    isPosition = true;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void write(byte[] bytes, boolean isPos)
    {
        isPosition = isPos;
        try{
            Out.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cancel(){
        try{
            bluetoothSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
    }
    }
}
