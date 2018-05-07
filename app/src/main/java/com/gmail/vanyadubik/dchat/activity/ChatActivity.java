package com.gmail.vanyadubik.dchat.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.gmail.vanyadubik.dchat.R;
import com.gmail.vanyadubik.dchat.adapter.ChatMessageAdapter;
import com.gmail.vanyadubik.dchat.model.ChatMessage;
import com.gmail.vanyadubik.dchat.service.ChatClient;
import com.gmail.vanyadubik.dchat.service.ChatServer;
import com.gmail.vanyadubik.dchat.service.Modules;
import com.michael.easydialog.EasyDialog;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    public static final String ACTIVITY_CHAT_PARAM = "activity_chat_param";

    private AlertDialog alertQuestion;

    private Toolbar toolbar;
    private ListView chtMesgListView;
    private ChatMessageAdapter messageAdapter;
    private EditText editTextSend;
    private ImageButton buttonSend;
    private List<ChatMessage> chatMessageList;
    private ChatServer chatServer;
    private ChatClient chatClient;

    private Boolean isServetChat;
    private String serverIp;
    private int serverPort;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        chtMesgListView = (ListView) findViewById(R.id.message_list);
        editTextSend = (EditText) findViewById(R.id.send_message);
        buttonSend = (ImageButton) findViewById(R.id.send_button);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatClient.sendMessage(editTextSend.getText().toString());
                editTextSend.setText("");
            }
        });

        chtMesgListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ChatMessage chatMessage = chatMessageList.get(position);
                if(chatMessage!=null){
                    editTextSend.setText(chatMessage.getUser()+", ");
                }

            }
        });

        chatMessageList = new ArrayList<>();

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            isServetChat = extras.getBoolean(ACTIVITY_CHAT_PARAM);
        } else {
            finish();
        }
        if (isServetChat) {
            setTitle(getString(R.string.app_name)+" "+getString(R.string.star));

            serverIp = Modules.getIpAddress();

            chatServer = new ChatServer(this, serverIp);

        }else{

            showLoginDialog();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            showSettings();
            return true;
        }

        if (id == R.id.action_exit) {
            logOUT();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        messageAdapter = new ChatMessageAdapter(ChatActivity.this, chatMessageList);
        chtMesgListView.setAdapter(messageAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(chatServer!=null){
            chatServer.destroy();
        }

        if(chatClient!=null){
            chatClient.disconnect();
        }

    }

    private void showSettings(){

        View bubbleMessageView = this.getLayoutInflater().inflate(R.layout.layout_tip_content_horizontal, null);

        TextView serverTV = (TextView) bubbleMessageView.findViewById(R.id.textServer);
        serverTV.setText(serverIp);
        TextView portTV = (TextView) bubbleMessageView.findViewById(R.id.textPort);
        portTV.setText(String.valueOf(serverPort));

        final EasyDialog bubbleMessage = new EasyDialog(ChatActivity.this)
                .setLayout(bubbleMessageView)
                .setBackgroundColor(ChatActivity.this.getResources().getColor(R.color.colorPrimaryDark))
                .setLocationByAttachedView(toolbar)
                .setGravity(EasyDialog.GRAVITY_BOTTOM)
                .setAnimationTranslationShow(EasyDialog.DIRECTION_X, 1000, -600, 100, -50, 50, 0)
                .setAnimationAlphaShow(1000, 0.3f, 1.0f)
                .setAnimationTranslationDismiss(EasyDialog.DIRECTION_X, 500, -50, 800)
                .setAnimationAlphaDismiss(500, 1.0f, 0.0f)
                .setTouchOutsideDismiss(true)
                .setMatchParent(true)
                .setMarginLeftAndRight(100, 100)
                .setOutsideColor(ChatActivity.this.getResources().getColor(R.color.outside_color_dark_gray));

        ImageButton buttonMessage = (ImageButton) bubbleMessageView.findViewById(R.id.buttonMessage);

        buttonMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bubbleMessage.dismiss();
            }
        });

        bubbleMessage.show();
    }

    private void logOUT(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme);
        builder.setTitle(getString(R.string.action_exit));
        builder.setMessage(isServetChat ? getString(R.string.are_you_shure_server):getString(R.string.are_you_shure));

        builder.setPositiveButton(getString(R.string.questions_answer_yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                Intent intent = new Intent(ChatActivity.this, BootActivity.class);
                startActivity(intent);
                finish();
            }
        });

        builder.setNegativeButton(getString(R.string.questions_answer_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();

    }

    public void showLoginDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme);
        builder.setTitle(getResources().getString(R.string.enter_server_nickname));
        final View viewInflated = LayoutInflater.from(this).inflate(R.layout.login_activity, null);

        final EditText serverET = (EditText) viewInflated.findViewById(R.id.serverAddress);

        serverET.setVisibility(isServetChat? View.GONE : View.VISIBLE);

        final EditText loginET = (EditText) viewInflated.findViewById(R.id.login);

        builder.setView(viewInflated);

        builder.setPositiveButton(getResources().getString(R.string.questions_answer_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.action_exit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(ChatActivity.this, BootActivity.class);
                startActivity(intent);
                finish();
            }
        });

        builder.setCancelable(true);
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        alertQuestion = builder.create();
        alertQuestion.show();
        alertQuestion.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertQuestion.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean cancel = false;
                View focusView = null;

                if (TextUtils.isEmpty(String.valueOf(serverET.getText()))&&!isServetChat) {

                    serverET.setError(getString(R.string.error_field_required));
                    focusView = serverET;
                    cancel = true;
                }

                if (TextUtils.isEmpty(String.valueOf(loginET.getText()))) {

                    loginET.setError(getString(R.string.error_field_required));
                    focusView = loginET;
                    cancel = true;
                }
                if (cancel) {

                    focusView.requestFocus();

                } else {

                    alertQuestion.dismiss();

                    chatClient = new ChatClient(ChatActivity.this,
                            loginET.getText().toString()+(isServetChat ? " "+getString(R.string.star):""),
                            isServetChat? serverIp:serverET.getText().toString(),
                            8080);

                }



            }
        });
    }

    public void showMessage(ChatMessage chatMessage){
        chatMessageList.add(chatMessage);
        messageAdapter.notifyDataSetChanged();
    }

    public void setPort(int port){
        serverPort = port;
    }


}
