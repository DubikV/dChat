package com.gmail.vanyadubik.dchat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.gmail.vanyadubik.dchat.R;

import static com.gmail.vanyadubik.dchat.activity.ChatActivity.ACTIVITY_CHAT_PARAM;

public class BootActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boot);

        Button crtBtn = (Button) findViewById(R.id.button_create_chat);
        crtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startChat(true);
            }
        });

        Button connBtn = (Button) findViewById(R.id.button_connect_chat);
        connBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startChat(false);
            }
        });

    }

    private void startChat(Boolean ctreatChat){

        Intent intent = new Intent(BootActivity.this, ChatActivity.class);
        intent.putExtra(ACTIVITY_CHAT_PARAM, ctreatChat);
        startActivity(intent);
        finish();
    }

}
