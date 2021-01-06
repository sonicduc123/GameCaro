package com.example.p2papplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

public class SetNameActivity extends Activity {
    EditText namePlayer;
    Button btnSubmit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setname);

        namePlayer = (EditText) findViewById(R.id.namePlayer);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);

        namePlayer.setText("");

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.namePlayer = String.valueOf(namePlayer.getText());
                boolean isValid = check();
                if (isValid) {
                    Intent intent = new Intent(SetNameActivity.this, RoomActivity.class);
                    startActivityForResult(intent, 1122);
                }
            }
        });
    }
    public boolean check(){
        if(MainActivity.namePlayer.equals("")){
            boolean h = openWinDialog("You are not type your name. Do you want to continue?", 1);
            return h;
        }
        return true;
    };

    private boolean openWinDialog(String message, final int winner) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(SetNameActivity.this).inflate(
                R.layout.layout_warning_dialog,
                (ConstraintLayout) findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        ((TextView) view.findViewById(R.id.textTitle)).setText("Notification");
        ((TextView) view.findViewById(R.id.textMessage)).setText(message);
        ((Button) view.findViewById(R.id.buttonNo)).setText("No");
        ((Button) view.findViewById(R.id.buttonYes)).setText("Yes");

        ((ImageView) view.findViewById(R.id.imageIcon)).setImageResource(R.drawable.ic_baseline_notifications_24);

        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        view.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.namePlayer = "player";
                Intent intent = new Intent(SetNameActivity.this, RoomActivity.class);
                startActivityForResult(intent, 1122);
                alertDialog.dismiss();
            }
        });

        view.findViewById(R.id.buttonNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        builder.setCancelable(false);
        alertDialog.show();
        return false;
    };
}
