package com.example.p2papplication;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;

public class RoomActivity extends Activity {
    static final int CLIENT = 1;
    static final int SERVER = 2;
    static final int RECEIVER = 3;
    static final int RECEIVERNAME = 4;

    //chuyen vi tri qua chessboardp2pscreenactivity
    static int newPos = -1;

    static String namePlayer2;

    Handler hMain = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what)
            {
                case CLIENT:
                {
                    lottieAnimationView.pauseAnimation();
                    lottieAnimationView.setVisibility(View.GONE);
                    txtWait.setText("SUCCESS");
                    //gui ten nguoi choi client
                    ((Client) clientThread).sendNamePlayer(MainActivity.namePlayer);
                    if (check1) {
                        check1 = false;
                        SorC = true;
                        inGame(-2);
                    }
                    txtWait.setVisibility(View.GONE);
                    break;
                }
                case SERVER:
                {
                    lottieAnimationView.pauseAnimation();
                    lottieAnimationView.setVisibility(View.GONE);
                    txtWait.setText("SUCCESS");
                    if (check) {
                        check = false;
                        SorC =false;
                        inGame(-1);
                    }
                    txtWait.setVisibility(View.GONE);
                    break;
                }
                case RECEIVER:
                {
                    byte[] readBuffer = (byte[]) msg.obj;
                    String tempString = new String(readBuffer, 0, msg.arg1);
                    newPos = Integer.parseInt(tempString);
                    break;
                }
                case RECEIVERNAME:
                {
                    byte[] readBuffer = (byte[]) msg.obj;
                    String tempString = new String(readBuffer, 0, msg.arg1);
                    namePlayer2 = "XO_" + tempString;
                    break;
                }
            }
            return true;
        }
    });//Handler


    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (device.getName() != null && device.getName().contains("XO_") && !listDevice.contains(device))
                {
                    //String deviceName = device.getName();
                    listDevice.add(device);
                    deviceName.add(device.getName());
                    //mDevice = device;
                    //Toast.makeText(getApplicationContext(), String.valueOf(stringArrayList.size()), Toast.LENGTH_SHORT).show();
                }
                adapter.notifyDataSetChanged();
                //   Toast.makeText(getApplicationContext(), "Debug Devices", Toast.LENGTH_SHORT).show();
            }
        }
    };

    boolean check = true;
    boolean check1 = true;
    boolean isUnregister = true;

    ArrayList<BluetoothDevice> listDevice = new ArrayList<BluetoothDevice>();
    ListView listView;
    ArrayList<String> deviceName = new ArrayList<String>();
    AdapterListView adapter;
    Button btnCreate;
    ImageButton btnRefresh;
    TextView txtWait, txt3;
    LottieAnimationView lottieAnimationView;


    private BluetoothAdapter BA;

    static Thread serverThread = null;
    static Thread clientThread = null;
    boolean SorC = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        listView = (ListView)findViewById(R.id.listRoom);

        btnRefresh = (ImageButton)findViewById(R.id.btnRefresh);
        btnCreate = (Button)findViewById(R.id.btnCreateRoom);

        txt3 = (TextView)findViewById(R.id.textView3);
        txtWait= (TextView)findViewById(R.id.txtWait);

        lottieAnimationView = (LottieAnimationView) findViewById(R.id.cat);

        handleBluetooth();

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("sdfsf","loi o day neddddd thg ngu 1");
                if (!isUnregister) {
                    Log.e("sdfsf","loi o day neddddd thg ngu 2");

                    deviceName.clear();
                    listDevice.clear();
                    BA.cancelDiscovery();
                    unregisterReceiver(receiver);
                    isUnregister = true;
                }

                Log.e("sdfsf","loi o day neddddd thg ngu 3");

                txt3.setVisibility(View.GONE);
                listView.setVisibility(View.GONE);
                lottieAnimationView.setVisibility(View.VISIBLE);
                lottieAnimationView.playAnimation();
                txtWait.setText("WAITING...");
                txtWait.setVisibility(View.VISIBLE);
                serverThread = new Server(hMain);
                Log.e("sdfsf","loi o day neddddd thg ngu 4");

                serverThread.start();
            }
        });

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRoom(v);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(RoomActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.clear();
        //unregisterReceiver(receiver);
        BA.cancelDiscovery();

        //turn off Bluetooth
        BA.disable();
    }

    public void handleBluetooth() {
        BA = BluetoothAdapter.getDefaultAdapter();

        //if device does not support Bluetooth
        if (BA == null) {
            new AlertDialog.Builder(this)
                    .setTitle("Not compatible")
                    .setMessage("Your phone does not support Bluetooth")
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

        //turn on
        int REQUEST_ENABLE_BLUETOOTH = 1;
        if (!BA.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, REQUEST_ENABLE_BLUETOOTH);
            //Toast.makeText(getApplicationContext(), "Turned on",Toast.LENGTH_LONG).show();
        } else {
            //Toast.makeText(getApplicationContext(), "Already on", Toast.LENGTH_LONG).show();
        }

        //change name of bluetooth device
        String OldName = BA.getName();
        String namePlayer = MainActivity.namePlayer;
        if (!OldName.equals("XO_" + namePlayer))
        {
            BA.setName("XO_" + namePlayer);
        }

        //get location permission
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number

        adapter = new AdapterListView(this, R.layout.item_listview, deviceName);
        listView.setAdapter(adapter);
    }

    public  void getRoom(View v) {
        check = true;
        if (!isUnregister) {
            unregisterReceiver(receiver);
            BA.cancelDiscovery();
            isUnregister = true;
        }

        deviceName.clear();
        listDevice.clear();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
        BA.startDiscovery();
        isUnregister = false;

        txtWait.setVisibility(View.GONE);
        txt3.setVisibility(View.GONE);
        lottieAnimationView.pauseAnimation();
        lottieAnimationView.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
    }

    class AdapterListView extends ArrayAdapter<String> {
        Button btnJoin;
        Context context;
        ArrayList<String> items;
        public AdapterListView( Context context, int layoutToBeInflated, ArrayList<String> items) {
            super(context, R.layout.item_listview, items);
            this.context = context;
            this.items = items;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View row;
            if(convertView == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                row = inflater.inflate(R.layout.item_listview, null);
            } else {
                row = convertView;
            }

            TextView room = (TextView) row.findViewById(R.id.roomName);
            room.setText(items.get(position));
            btnJoin = (Button)row.findViewById(R.id.btnJoin);
            btnJoin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isUnregister) {
                        BA.cancelDiscovery();
                        unregisterReceiver(receiver);
                        isUnregister = true;
                    }
                    namePlayer2 = deviceName.get(position);

                    listView.setVisibility(View.GONE);
                    txtWait.setText("WAITING...");
                    txtWait.setVisibility(View.VISIBLE);
                    lottieAnimationView.setVisibility(View.VISIBLE);
                    lottieAnimationView.playAnimation();
                    check1 = true;

                    clientThread = new Client(listDevice.get(position), hMain);
                    clientThread.start();

                    listDevice.clear();
                    deviceName.clear();
                }
            });

            return (row);
        }
    }

    public void inGame(int turn) {
        MediaPlayer soundClick;
        soundClick = MediaPlayer.create(this, R.raw.click1);
        soundClick.start();

        //Server se gui value 1 ->x ->di truoc
        //Client se nhan tn tu server value 1 -> set value cua client la 0 ->o ->di sau

        //

        Intent intent = new Intent(this, ChessboardP2pScreenActivity.class);

        Bundle bundle = new Bundle();
        bundle.putInt("turn", turn);
        intent.putExtras(bundle);

        startActivityForResult(intent, 1122);
    }
}