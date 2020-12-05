package com.example.p2papplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;


public class MainActivity extends Activity {
    static String namePlayer = "";

    Button buttonTwoPlayer, buttonComputer, buttonGuide;
    String playerName = "";
    ImageView imgX;
    ImageView imgO;
    ImageView imgTwoPlayer;
    ImageView imgComputer;
    int progressProcess = 50;
    boolean progressAppear = false;
    ProgressBar progressLoading;
    MediaPlayer soundClick;
    AlertDialog dialog;
    int MAX_PROGRESS = 0;
    int progressValue = 0;


    private Handler handler;
    Handler myHandler = new Handler();

    private void showButtons(){
        handler = new Handler();

        handler.postDelayed(new Runnable(){
            @Override
            public void run(){
                buttonTwoPlayer.setVisibility(View.VISIBLE);
            }
        }, 3000);
        handler.postDelayed(new Runnable(){
            @Override
            public void run(){
                buttonComputer.setVisibility(View.VISIBLE);
            }
        }, 3000);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onStart();
        setContentView(R.layout.activity_main);

//        soundBackground = MediaPlayer.create(this, R.raw.background);
//        soundBackground.setLooping(false);

        buttonTwoPlayer = findViewById(R.id.btnTwoPlayer);
        buttonComputer = findViewById(R.id.btnComputer);
        buttonGuide = findViewById(R.id.btnGuide);
        imgX = findViewById(R.id.imgLogo);
        imgO = findViewById(R.id.imgLogo1);
        imgTwoPlayer = findViewById(R.id.twoPlayerImg);
        imgComputer = findViewById(R.id.computerImg);
        playerName = "";
        soundClick = MediaPlayer.create(MainActivity.this, R.raw.click1);

        final Animation scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up);
        final Animation scaleDown = AnimationUtils.loadAnimation(this, R.anim.scale_down);
        final Animation animTop = AnimationUtils.loadAnimation(this, R.anim.top_anim);
        final Animation animBottom = AnimationUtils.loadAnimation(this, R.anim.bottom_anim);
        final Animation animLeft = AnimationUtils.loadAnimation(this, R.anim.left_anim);
        final Animation animRight = AnimationUtils.loadAnimation(this, R.anim.right_anim);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.CustomDialog);
        progressLoading = findViewById(R.id.progressLoading);
        View view = LayoutInflater.from(MainActivity.this).inflate(
                R.layout.progress_dialog,
                (LinearLayout) findViewById(R.id.layoutLoading)
        );
        builder.setView(view);
        progressLoading = ((ProgressBar) view.findViewById(R.id.progressLoading));
        progressLoading.setProgress(progressProcess);

        dialog = builder.create();


        imgX.setAnimation(animTop);
        imgO.setAnimation(animTop);
//        soundBackground.start();
        imgTwoPlayer.setAnimation(animLeft);
        imgComputer.setAnimation(animRight);

        buttonTwoPlayer.setVisibility(View.INVISIBLE);
        buttonComputer.setVisibility(View.INVISIBLE);
        showButtons();

        buttonTwoPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundClick.start();
                buttonTwoPlayer.startAnimation(scaleUp);
                Intent intent;
                if (namePlayer == "")
                    intent = new Intent(MainActivity.this, SetNameActivity.class);
                else
                    intent = new Intent(MainActivity.this, RoomActivity.class);
                buttonTwoPlayer.startAnimation(scaleDown);
                startActivityForResult(intent, 1122);
            }
        });

        buttonComputer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundClick.start();
                progressAppear = true;
                new MyTask().execute();
                buttonComputer.startAnimation(scaleUp);
                //Intent intent = new Intent(MainActivity.this, ChessboardScreenActivity.class);
                buttonComputer.startAnimation(scaleDown);
                //threadProgress();
                //startActivityForResult(intent, 1122);
            }
        });

        buttonGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundClick.start();
                buttonGuide.startAnimation(scaleUp);
                Intent intent = new Intent(MainActivity.this, GuideActivity.class);
                buttonGuide.startAnimation(scaleDown);
                startActivityForResult(intent, 1122);
            }
        });
    }

    public class MyTask extends AsyncTask<Void, Void, Void> {
        public void onPreExecute() {
               MAX_PROGRESS = 1000;
               onStart();
               dialog.show();
        }
        public Void doInBackground(Void... unused) {
            Intent intent = new Intent(MainActivity.this, ChessboardScreenActivity.class);
            startActivityForResult(intent, 1122);
            return null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(progressAppear) {
            progressLoading.setMax(MAX_PROGRESS);
            progressLoading.setProgress(0);
            progressLoading.setVisibility(View.VISIBLE);
        }
        Thread myBackgroundThread = new Thread( backgroundTask, "backAlias1");
        myBackgroundThread.start();
    }

    private Runnable foregroundRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                progressLoading.setProgress(progressValue);
            }
            catch (Exception e) { Log.e("", e.getMessage()); }
        }
    };

    private Runnable backgroundTask = new Runnable() {
        @Override
        public void run() {
            try {
                for (int n = 0; n < MAX_PROGRESS; n++) {
                    Thread.sleep(1);
                    progressValue++;
                    myHandler.post(foregroundRunnable);
                }
            }
            catch (InterruptedException e) { Log.e("", e.getMessage()); }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
//        soundBackground.pause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(dialog.isShowing()) dialog.dismiss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        soundBackground.stop();
    }
}