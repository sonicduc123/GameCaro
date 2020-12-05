package com.example.p2papplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;

public class SetNameActivity extends Activity {
    EditText namePlayer;
    Button btnSubmit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setname);

        namePlayer = (EditText) findViewById(R.id.namePlayer);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SetNameActivity.this, RoomActivity.class);
                MainActivity.namePlayer = String.valueOf(namePlayer.getText());
                startActivityForResult(intent, 1122);
            }
        });
    }
}
