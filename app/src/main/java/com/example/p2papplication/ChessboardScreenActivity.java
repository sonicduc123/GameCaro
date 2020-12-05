package com.example.p2papplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
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

import static android.os.SystemClock.sleep;
import java.util.Locale;

public class ChessboardScreenActivity extends Activity {
    int row = 15, col = 15;
    GridView gridView;
    AdapterGridView adapter;
    int[][] list = new int[row][col];
    int VALUE_PROGRESS1 = 0, VALUE_PROGRESS2 = 0;
    int NEW_VALUE_PROGRESS1 = 0, NEW_VALUE_PROGRESS2 = 0;
    int winPlayer1 = 0;
    int winPlayer2 = 0;
    boolean luotchoi = true;
    boolean music = true;
    int playerPositioning = -1;
    int machinePositioning = -1;
    int positionWin = -1, typeWin = -1;
    boolean isRedo = false;

    MediaPlayer soundClick;
    MediaPlayer soundClick1;
    MediaPlayer soundBackground;

    ProgressBar progressBar1;
    ProgressBar progressBar2;

    Button btnMusic;
    Button btnRedo;
    Button btnHome;
    Button btnBackground;

    Animation scaleUp, scaleDown;

    // countdown
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
        btnBackground = findViewById(R.id.btnBackground);
        btnHome = findViewById(R.id.btnHome);
        btnMusic = findViewById(R.id.btnMusic);
        btnRedo = findViewById(R.id.btnRedo);
        mTextViewCountDown = findViewById(R.id.text_view_countdown);
        soundClick = MediaPlayer.create(ChessboardScreenActivity.this, R.raw.click);
        soundClick1 = MediaPlayer.create(ChessboardScreenActivity.this, R.raw.click1);
        scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up);
        scaleDown = AnimationUtils.loadAnimation(this, R.anim.scale_down);

        if(music) {
            soundBackground = MediaPlayer.create(this, R.raw.background);
            soundBackground.setLooping(false);
        }

        adapter = new AdapterGridView(this, R.layout.item_gridview, list);
        gridView.setAdapter(adapter);
        initData();

        // item click listener
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if(positionWin == -1){
                    final CustomTextView customTextView = v.findViewById(R.id.custom_text);
                    customTextView.setBackgroundResource(R.drawable.item);

                    if(isRedo) {
                        customTextView.setBackgroundResource(R.drawable.item);
                        isRedo = false;
                        return;
                    }

                    if(mTimerRunning) {
                        mCountDownTimer.cancel();
                        mTimerRunning = false;
                        resetTimer();
                    }

                    soundClick.start();

                    if(list[position / col][position % col] == -1) {
                        luotchoi = true;
                        machinePositioning = position;
                        customTextView.setBackgroundResource(R.drawable.o);
                        list[position / col][position % col] = -1;
                        if(isWin(list, position, -1) == 1) {
                            return;
                        }
                    }

                    if (list[position / col][position % col] == 1) {
                        customTextView.setBackgroundResource(R.drawable.x);
                        list[position / col][position % col] = 1;
                    }

                    if (list[position / col][position % col] == 0) {
                        luotchoi = false;
                        playerPositioning = position;
                        customTextView.setBackgroundResource(R.drawable.x);
                        list[position / col][position % col] = 1;
                        if(isWin(list, position, 1) == 1) {
                            return;
                        }
                        onStart();
                    }
                }
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
                Intent intent = new Intent(ChessboardScreenActivity.this, MainActivity.class);
                btnHome.startAnimation(scaleDown);
                startActivity(intent);
                onDestroy();
            }
        });

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
        });

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
    protected void onStop() {
        super.onStop();
        soundBackground.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        soundBackground.stop();
    }

    @Override
    public void onBackPressed() {

    }

    // countdown
    private void startTimer() {
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                onStart();
                if(mTimeLeftInMillis < 11000) {
                    mTextViewCountDown.startAnimation(scaleUp);
                    mTextViewCountDown.setTextColor(getColor(R.color.colorRed));
                    mTextViewCountDown.startAnimation(scaleDown);
                    onStart();
                }
                else
                    mTextViewCountDown.setTextColor(getColor(R.color.colorWhite));
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }
            @Override
            public void onFinish() {
                mTimerRunning = false;
            }
        }.start();
        mTimerRunning = true;
    }
    public void pauseTimer() {
        mCountDownTimer.cancel();
    }
    private void resetTimer() {
        mTimeLeftInMillis = START_TIME_IN_MILLIS;
    }
    private void updateCountDownText() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        mTextViewCountDown.setText(timeLeftFormatted);
    }

    // init list data
    private void initData() {
        playerPositioning = -1;
        machinePositioning = -1;
        positionWin = -1;
        typeWin = -1;

        for(int i = 0; i < row; i++)
            for(int j = 0; j < col; j++)
                list[i][j] = 0;

        adapter.notifyDataSetChanged();
    }

    // custom adapter
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

    // check win?
    public int isWin(int[][] arr, int position, int type) {
        int point1 = calcMaxPoint(arr, position, 1);
        int point2 = calcMaxPoint(arr, position, -1);

        if(point1 >= 5 && type == 1) {
            positionWin = position;
            pauseTimer();
            openWinDialog("You win. Next round", 1);
            return 1;
        }

        if(point2 >= 5 && type == -1) {
            positionWin = position;
            pauseTimer();
            showWinRange(-1);
            new CountDownTimer(2500, 1000) {

                public void onTick(long millisUntilFinished) {
                    pauseTimer();
                }

                public void onFinish() {
                    openWinDialog("You loseeee. Next round", -1);
                }
            }.start();
            return 1;
        }

        return 0;
    }

    public void showUpdown(final int position) {
        new CountDownTimer(2000, 1000) {

            public void onTick(long millisUntilFinished) {
                gridView.getChildAt(position).startAnimation(scaleUp);
                gridView.getChildAt(position).startAnimation(scaleDown);
            }

            public void onFinish() {
                gridView.getChildAt(position).setBackgroundColor(0);
            }
        }.start();
    }

    public void showWinRange(int type) {
        int index = 0;
        int nrow = positionWin / col;
        int ncol = positionWin % col;
        if(typeWin == 1) {
            index = 0;
            while (list[nrow + index][ncol] == type) {
                showUpdown((nrow + index) * col + ncol);
                index++;
            }
            index = 0;
            while (list[nrow - index][ncol] == type) {

                showUpdown((nrow - index) * col + ncol);
                index++;
            }
        }
        if(typeWin == 2) {
            index = 0;
            while (list[nrow][ncol + index] == type) {

                showUpdown((nrow) * col + ncol + index);
                index++;
            }
            index = 0;
            while (list[nrow][ncol - index] == type) {

                showUpdown((nrow) * col + ncol - index);
                index++;
            }
        }

        if(typeWin == 3) {
            index = 0;
            while (list[nrow + index][ncol + index] == type) {

                showUpdown((nrow + index) * col + ncol + index);
                index++;
            }
            index = 0;
            while (list[nrow - index][ncol - index] == type) {

                showUpdown((nrow - index) * col + ncol - index);
                index++;
            }
        }

        if(typeWin == 4) {
            index = 0;
            while (list[nrow + index][ncol - index] == type) {


                showUpdown((nrow + index) * col + ncol - index);
                index++;
            }
            index = 0;
            while (list[nrow - index][ncol - index] == type) {

                showUpdown((nrow - index) * col + ncol + index);
                index++;
            }
        }

    }

    // calc max point of 1 cell
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
        if(dem >= point) {
            point = dem;
            typeWin = 1;
        }

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
        if(dem >= point) {
            point = dem;
            typeWin = 2;
        }


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
        if(dem >= point) {
            point = dem;
            typeWin = 3;
        }


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
        if(dem >= point) {
            point = dem;
            typeWin = 4;
        }

        return point;
    };

    // show dialog and call thread progress increase
    private void openWinDialog(String message, final int winner) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(ChessboardScreenActivity.this).inflate(
                R.layout.layout_warning_dialog,
                (ConstraintLayout) findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        ((TextView) view.findViewById(R.id.textTitle)).setText("Notification");
        ((TextView) view.findViewById(R.id.textMessage)).setText(message);
        ((Button) view.findViewById(R.id.buttonNo)).setText("Home");
        ((Button) view.findViewById(R.id.buttonYes)).setText("Continue");
        ((ImageView) view.findViewById(R.id.imageIcon)).setImageResource(R.drawable.ic_baseline_notifications_24);

        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        view.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundClick1.start();
                if(winner == 1) {
                    winPlayer1++;
                    if(progressBar1.getProgress() == 100) {
                        progressBar1.setProgress(100);
                    } else
                    {
                        NEW_VALUE_PROGRESS1 = VALUE_PROGRESS1 + 20;
                        onStart();
                    }
                } else {
                    winPlayer2++;
                    if(progressBar2.getProgress() == 100) {
                        progressBar2.setProgress(100);
                    }
                    else
                    {
                        NEW_VALUE_PROGRESS2 = VALUE_PROGRESS2 + 20;
                        onStart();
                    }
                }

                if(machinePositioning != -1)
                    gridView.getChildAt(machinePositioning).setBackgroundColor(0);
                initData();
                if(mTimerRunning) {
                    mTextViewCountDown.setText("00:20");
                    mCountDownTimer.cancel();
                    mTimerRunning = false;
                    resetTimer();
                }
                alertDialog.dismiss();
            }

        });

        view.findViewById(R.id.buttonNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundClick1.start();
                Intent intent =new Intent(ChessboardScreenActivity.this, MainActivity.class);
                startActivity(intent);
                onDestroy();
                alertDialog.dismiss();

            }
        });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }





    // thread
    @Override
    protected void onStart() {
        super.onStart();
        if(music) soundBackground.start();
        Thread myBackgroundThread = new Thread( backgroundTask, "backAlias1");
        myBackgroundThread.start();
    };

    private Runnable progressRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                progressBar1.setProgress(VALUE_PROGRESS1);
                progressBar2.setProgress(VALUE_PROGRESS2);
                if(progressBar1.getProgress() == 100) {
                    VALUE_PROGRESS1 = -20;
                    VALUE_PROGRESS2 = 0;
                    NEW_VALUE_PROGRESS1 = 0;
                    NEW_VALUE_PROGRESS2 = 0;
                    progressBar1.setProgress(0);
                    progressBar2.setProgress(0);
                    openWinDialog("You win machine. Would you like to continue?", 1);
                }
                if(progressBar2.getProgress() == 100) {
                    VALUE_PROGRESS1 = 0;
                    VALUE_PROGRESS2 = -20;
                    NEW_VALUE_PROGRESS1 = 0;
                    NEW_VALUE_PROGRESS2 = 0;
                    progressBar1.setProgress(0);
                    progressBar2.setProgress(0);
                    openWinDialog("You loseee machine. Would you like to continue or run?", -1);
                }
            }
            catch (Exception e) { Log.e("", e.getMessage()); }
        }
    };

    private Runnable machineRunnable = new Runnable() {
        @SuppressLint("ResourceAsColor")
        @Override
        public void run() {
            try {
                sleep(200);
                int machinePosition = AIMachine.machineRun(list);
                list[machinePosition / col][machinePosition % col] = -1;
                if(machinePositioning != -1) gridView.getChildAt(machinePositioning).setBackgroundColor(0);
                gridView.getOnItemClickListener().onItemClick(gridView, gridView.getChildAt(machinePosition), machinePosition, (long)machinePosition);
                if(calcMaxPoint(list, machinePositioning, -1) < 5) {
                    gridView.getChildAt(machinePosition).setBackgroundColor(Color.rgb(255, 153, 51));
                }
                resetTimer();
                startTimer();
            }
            catch (Exception e) { Log.e("", e.getMessage()); }
        }
    };

    private Runnable countdownRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                if(machinePositioning != -1)
                    gridView.getChildAt(machinePositioning).setBackgroundColor(0);
                openWinDialog("Time out -> You lose \nWould you like to continue next road?", -1);
                resetTimer();
                initData();
            }
            catch (Exception e) { Log.e("", e.getMessage()); }
        }
    };

    private Runnable backgroundTask = new Runnable() {
        @Override
        public void run() {
            try {
                if(NEW_VALUE_PROGRESS1 == VALUE_PROGRESS1 + 20) {
                    for (int n = 0; n < 20; n++) {
                        Thread.sleep(100);
                        VALUE_PROGRESS1++;
                        myHandler.post(progressRunnable);
                    }
                    return;
                }

                if(NEW_VALUE_PROGRESS2 == VALUE_PROGRESS2 + 20) {
                    for (int n = 0; n < 20; n++) {
                        Thread.sleep(100);
                        VALUE_PROGRESS2++;
                        myHandler.post(progressRunnable);
                    }
                    return;
                }

                if(mTimeLeftInMillis < 1000) myHandler.post(countdownRunnable);

                if(luotchoi == false) myHandler.post(machineRunnable);
            }
            catch (InterruptedException e) { Log.e("", e.getMessage()); }
        }
    };

}

