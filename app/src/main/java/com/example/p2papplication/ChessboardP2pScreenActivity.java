package com.example.p2papplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

public class ChessboardP2pScreenActivity extends Activity {
    int row = 15, col = 15;
    GridView gridView;
    AdapterGridView adapter;
    int[][] list = new int[row][col];
    int VALUE_PROGRESS1 = 0, VALUE_PROGRESS2 = 0;
    int NEW_VALUE_PROGRESS1 = 0, NEW_VALUE_PROGRESS2 = 0;
    int globalVar = 0;
    int winPlayer1 = 0;
    int winPlayer2 = 0;
    int value, intClient;
    boolean check = true;

    TextView namePlayer;
    TextView namePlayer1;

    Button btnMusic;
    Button btnRedo;
    Button btnHome;
    Button btnBackground;



    boolean music = true;
    MediaPlayer soundClick;
    MediaPlayer soundClick1;
    MediaPlayer soundBackground;

    ProgressBar progressBar1;
    ProgressBar progressBar2;

    Animation scaleUp, scaleDown;

    private static final long START_TIME_IN_MILLIS = 20000;
    private static final int listBackground[] = { R.drawable.background5, R.drawable.background4, R.drawable.bg_round, R.drawable.background1, R.drawable.background, R.drawable.background2, R.drawable.background3};
    int indexBackground = 1;
    private TextView mTextViewCountDown;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning = false;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    Handler myHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chessboard_screen);

        gridView = findViewById(R.id.gridView);
        progressBar1 = findViewById(R.id.progress1);
        progressBar2 = findViewById(R.id.progress2);

        namePlayer = (TextView) findViewById(R.id.namePlayer);
        namePlayer.setText("Player1: " + MainActivity.namePlayer);

        namePlayer1 = (TextView) findViewById(R.id.namePlayer2);
        namePlayer1.setText("Player2: " + RoomActivity.namePlayer2.substring(3));

        btnBackground = (Button) findViewById(R.id.btnBackground);
        btnHome = (Button) findViewById(R.id.btnHome);
        btnMusic = (Button) findViewById(R.id.btnMusic);
        btnRedo = (Button) findViewById(R.id.btnRedo);
        mTextViewCountDown = findViewById(R.id.text_view_countdown);

        soundClick = MediaPlayer.create(ChessboardP2pScreenActivity.this, R.raw.click);
        soundClick1 = MediaPlayer.create(ChessboardP2pScreenActivity.this, R.raw.click1);
        scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up);
        scaleDown = AnimationUtils.loadAnimation(this, R.anim.scale_down);

        if(music) {
            soundBackground = MediaPlayer.create(this, R.raw.background);
            soundBackground.setLooping(false);
        }

        adapter = new AdapterGridView(this, R.layout.item_gridview, list);
        gridView.setAdapter(adapter);
        initData();

        if(value == 1)
            myHandler.post(p2);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                View newView = v;
                final CustomTextView customTextView = v.findViewById(R.id.custom_text);
                if (list[position / col][position % col] != 0) {
                    soundClick.start();
                    return;
                }
                if (list[position / col][position % col] == 0) {
                    customTextView.setBackgroundResource(R.drawable.item);
                }
                soundClick.start();

                if (globalVar % 2 == value && check == true) {
                    if (list[position / col][position % col] == 0) {
                        if (value == 0) {
                            customTextView.setBackgroundResource(R.drawable.x);
                            list[position / col][position % col] = 1;
                        }
                        else {
                            customTextView.setBackgroundResource(R.drawable.o);
                            list[position / col][position % col] = -1;
                        }

                        Log.e("me","P1: " + position + " " + value);

                        if(intClient == 0) {
                            ((Client) RoomActivity.clientThread).sendMsg(position);
                        }
                        else
                            ((Server) RoomActivity.serverThread).sendMsg(position);
                        //yeu cau gui position
                        globalVar++;
                        check = false;
                        //Toast.makeText(ChessboardP2pScreenActivity.this, " " + list[position / col][position % col], Toast.LENGTH_LONG).show();

                        myHandler.post(p2);
                    }
                }else {
                    if(check == true){
                    if (list[position / col][position % col] == 0) {
                        if (value == 0) {
                            customTextView.setBackgroundResource(R.drawable.o);
                            list[position / col][position % col] = -1;//o danh tai day
                        }
                        else {
                            customTextView.setBackgroundResource(R.drawable.x);
                            list[position / col][position % col] = 1;//x danh tai day
                        }
                        //Toast.makeText(ChessboardP2pScreenActivity.this, " " +list[position / col][position % col], Toast.LENGTH_LONG).show();

                        globalVar++;
                    }}else {
                    //Toast.makeText(ChessboardP2pScreenActivity.this, "chua toi luot may", Toast.LENGTH_LONG ).show();
                }
                }
                isWin(list, position, value);
            }
        });

        btnMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundClick1.start();
                btnMusic.startAnimation(scaleUp);
                if(music == true) {
                    btnMusic.setBackgroundResource(R.drawable.ic_baseline_music_off_24);
                    music = false;
                    soundBackground.pause();
                    return;
                } else {
                    btnMusic.setBackgroundResource(R.drawable.ic_baseline_music_note_24);
                    music = true;
                    soundBackground.start();
                }
                btnMusic.startAnimation(scaleDown);
            }
        });


        btnHome.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                soundClick1.start();
                btnHome.startAnimation(scaleUp);
                //gui tn out tran cho may khac
                Log.e("me","P1: Tao out");
                if(intClient == 0) {
                    ((Client) RoomActivity.clientThread).sendMsg(-10);
                }
                else
                    ((Server) RoomActivity.serverThread).sendMsg(-10);


                Intent intent = new Intent(ChessboardP2pScreenActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                btnHome.startAnimation(scaleDown);
                startActivity(intent);
                onDestroy();
            }
        });


        /*
        btnRedo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundClick1.start();
                btnRedo.startAnimation(scaleUp);
                if(playerPositioning != -1 && machinePositioning != -1) {
                    list[playerPositioning / col][playerPositioning % col] = 0;
                    isRedo = true;
                    gridView.getOnItemClickListener().onItemClick(gridView, gridView.getChildAt(playerPositioning), playerPositioning, (long)playerPositioning);
                    list[machinePositioning / col][machinePositioning % col] = 0;
                    isRedo = true;
                    gridView.getOnItemClickListener().onItemClick(gridView, gridView.getChildAt(machinePositioning), machinePositioning, (long)machinePositioning);

                }
                btnRedo.startAnimation(scaleDown);
            }
        });*/

        btnBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundClick1.start();
                btnBackground.startAnimation(scaleUp);
                LinearLayout layout = (LinearLayout) findViewById(R.id.layoutBoard);
                if(indexBackground == listBackground.length) indexBackground = 0;
                layout.setBackgroundResource(listBackground[indexBackground]);
                indexBackground++;
                btnBackground.startAnimation(scaleDown);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (RoomActivity.clientThread != null)
            ((Client) RoomActivity.clientThread).cancel();
        if (RoomActivity.serverThread != null)
            ((Server) RoomActivity.serverThread).cancel();
        renewData(value);
        finish();
    }

    private void renewData(int turn) {
        value = turn;
        RoomActivity.newPos = -1;
        globalVar = 0;

        if (value==1)
            check = false;

        for(int i = 0; i < row; i++)
            for(int j = 0; j < col; j++)
                list[i][j] = 0;
        adapter.notifyDataSetChanged();
    }


    private void initData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        int turn = bundle.getInt("turn");
        value = turn +2;
        intClient =value;

        if (value==1)
            check = false;
        //Log.e("yors","Thuuuuuuuuuu nhaaa cho anhhhhhhhhhhh" + value);

        for(int i = 0; i < row; i++)
            for(int j = 0; j < col; j++)
                list[i][j] = 0;
        adapter.notifyDataSetChanged();
    }

    public class AdapterGridView extends BaseAdapter {
        Context myContext;
        int myLayout;
        int[][] arr;

        public AdapterGridView(Context myContext, int myLayout, int[][] arr) {
            this.myContext = myContext;
            this.myLayout = myLayout;
            this.arr = arr;
        }

        @Override
        public int getCount() {
            return row * col;
        }

        @Override
        public Object getItem(int position) {
            return arr[position / col][position % col];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            View item;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                item = inflater.inflate(myLayout, null);
            } else {
                item = view;
            }

            final CustomTextView customTextView = item.findViewById(R.id.custom_text);
            customTextView.setBackgroundResource(R.drawable.item);


            // scroll up down khong bi mat background item
            if (arr[position / col][position % col] == 1) {
                customTextView.setBackgroundResource(R.drawable.x);
            }
            if (arr[position / col][position % col] == -1) {
                customTextView.setBackgroundResource(R.drawable.o);
            }

            return item;

        }
    }

    //value = 0 -> x//value = 1 -> o
    //value = 0 ->type = 1// value = 1 -> type = -1
    public void isWin(int[][] arr, int position, int value) {
        int yourPoint, player2Point;
        if (value == 0)//neu  ban la x
            {
                if(list[position / col][position %col] == 1) {
                    yourPoint = calcMaxPoint(arr, position, 1);
                    player2Point = calcMaxPoint(arr, position, -1) -1;

                }
                else {
                    yourPoint = calcMaxPoint(arr, position, 1) -1;
                    player2Point = calcMaxPoint(arr, position, -1);
                }

        }
        else//ban la o
            {
                if(list[position / col][position %col] == 1) {
                    player2Point = calcMaxPoint(arr, position, 1);
                    yourPoint = calcMaxPoint(arr, position, -1) -1;

                }
                else {
                    player2Point = calcMaxPoint(arr, position, 1) -1;
                    yourPoint = calcMaxPoint(arr, position, -1);
                }
        }

        Log.e("yourPoint","     : " + yourPoint);
        Log.e("player2Point","     : " + player2Point);

        if(yourPoint >= 5) {
            openWinDialog("You win! \nWould you like to continue next road?", value);
            return;
        }
        if(player2Point >= 5) {
            openWinDialog("You loseeeeee!\nWould you like to revenge?", -1);
            return;
        }
    }

    //winner = 0 hoac 1 neu ban thang
    //winner = -1 neu doi thu thang
    private void openWinDialog(String message, final int winner) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(ChessboardP2pScreenActivity.this).inflate(
                R.layout.layout_warning_dialog,
                (ConstraintLayout) findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        ((TextView) view.findViewById(R.id.textTitle)).setText("Notification");
        ((TextView) view.findViewById(R.id.textMessage)).setText(message);
        ((Button) view.findViewById(R.id.buttonNo)).setText("Home");
        ((Button) view.findViewById(R.id.buttonYes)).setText("Continue");
        if (winner ==-10)
        {
            view.findViewById(R.id.buttonYes).setVisibility(view.GONE);
        }
        ((ImageView) view.findViewById(R.id.imageIcon)).setImageResource(R.drawable.ic_baseline_notifications_24);

        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        view.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundClick1.start();
                if(winner == value) {
                    winPlayer1++;
                    if(progressBar1.getProgress() == 100) {
                        progressBar1.setProgress(100);
                    } else
                    {
                        NEW_VALUE_PROGRESS1 += 20;
                        onStart();
                    }
                } else {
                    winPlayer2++;
                    if(progressBar2.getProgress() == 100) {
                        progressBar2.setProgress(100);
                    }
                    else
                    {
                        NEW_VALUE_PROGRESS2 += 20;
                        onStart();
                    }
                }
                if (winner == value)
                {
                    renewData( 1 );
                }else
                {
                    renewData(0);
                }
                alertDialog.dismiss();
            }
        });

        view.findViewById(R.id.buttonNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundClick1.start();
                if (winner != -10){ //kiem tra doi thu thoat giua tran?
                    //gui tn out tran cho may khac
                    if(intClient == 0) {
                        ((Client) RoomActivity.clientThread).sendMsg(-10);
                    }
                    else
                        ((Server) RoomActivity.serverThread).sendMsg(-10);
                }


                Intent intent =new Intent(ChessboardP2pScreenActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                alertDialog.dismiss();
            }
        });
        builder.setCancelable(false);
        alertDialog.show();
    }


    public int calcMaxPoint(int[][] arr, int position, int type) {
        int point = 0;
        int hang = position / col;
        int cot = position % col;

        // hang doc
        int dem = 1;
        int index = 1;
        while (hang + index < row && arr[hang+index][cot] == type) {
            dem++;
            index++;
        }
        index = 1;
        while(hang - index >= 0 && type==arr[hang-index][cot]){
            dem++;
            index++;
        }
        if(dem > point) point = dem;

        // hang ngang
        dem = 1;
        index = 1;
        while (cot + index < col && type==arr[hang][cot+index]){
            dem++;
            index++;
        }
        index = 1;
        while (cot - index >=0 && type==arr[hang][cot- index]){
            dem++;
            index++;
        }
        if(dem > point) point = dem;


        // duong cheo trai
        dem=1;
        index= 1;
        while (hang + index < row && cot + index < col && type==arr[hang+ index][cot+index]){
            dem++;
            index++;
        }
        index= 1;
        while (cot - index >=0 && hang - index >= 0 && type==arr[hang- index][cot- index]) {
            dem++;
            index++;
        }
        if(dem > point) point = dem;


        // duong cheo phai
        dem= 1;
        index= 1;
        while (hang + index < row && cot - index >= 0 && type==arr[hang + index][cot-index] ){
            dem++;
            index++;
        }
        index= 1;
        while (cot + index < col && hang - index >= 0 && type==arr[hang - index][cot+ index] ){
            dem++;
            index++;
        }
        if(dem > point) point = dem;

        return point;
    };



    // thread reload image
    @Override
    protected void onStart() {
        super.onStart();
        Thread myBackgroundThread = new Thread( backgroundTask, "backAlias1");
        myBackgroundThread.start();
    };

    private Runnable foregroundRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                progressBar1.setProgress(VALUE_PROGRESS1);
                progressBar2.setProgress(VALUE_PROGRESS2);
            }
            catch (Exception e) { //Log.e("", e.getMessage());
                 }
        }
    };

    private Runnable p2 = new Runnable() {
        @Override
        public void run() {
            try {
                int newPosition = RoomActivity.newPos;

                Log.e("yors","P2: " + newPosition);
                if(newPosition==-10)
                {
                    openWinDialog("Your friend is quit the room", -10);
                    return;
                }
                if ( newPosition >=0 ){
                    if (list[newPosition / col][newPosition % col] == 0 ) {
                        check = true;
                        gridView.getOnItemClickListener().onItemClick(gridView,
                                gridView.getChildAt(newPosition), newPosition, (long)newPosition);
                    }else{
                        myHandler.post(p2);
                    }}
                else{
                        myHandler.post(p2);
                }
            }catch (Exception e) {
                //Log.e("", e.getMessage());
            }
        }
    };

    private Runnable backgroundTask = new Runnable() {
        @Override
        public void run() {
            try {
                if(NEW_VALUE_PROGRESS1 != VALUE_PROGRESS1) {
                    for (int n = 0; n < 20; n++) {
                        Thread.sleep(100);
                        VALUE_PROGRESS1++;
                        myHandler.post(foregroundRunnable);
                    }
                    return;
                }

                if(NEW_VALUE_PROGRESS2 != VALUE_PROGRESS2) {
                    for (int n = 0; n < 20; n++) {
                        Thread.sleep(100);
                        VALUE_PROGRESS2++;
                        myHandler.post(foregroundRunnable);
                    }
                    return;
                }
            }
            catch (InterruptedException e) { //Log.e("", e.getMessage());
                }
        }
    };
}

