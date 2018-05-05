package com.gmail.vanyadubik.dchat.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.vanyadubik.dchat.R;
import com.gmail.vanyadubik.dchat.adapter.ChatMessageAdapter;
import com.gmail.vanyadubik.dchat.model.ChatMessage;
import com.michael.easydialog.EasyDialog;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    public static final String ACTIVITY_CHAT_PARAM = "activity_chat_param";

    static final int SocketServerPORT = 8080;

   // private TextView infoIp, infoPort, chatMsg;
    private Spinner spUsers;
    private ArrayAdapter<ChatClient> spUsersAdapter;
    private Button btnSentTo;

    private String msgLog = "";

    private Toolbar toolbar;
    private ListView chtMesgListView;
    private ChatMessageAdapter messageAdapter;
    private EditText editTextSend;
    private List<ChatClient> userList;
    private List<ChatMessage> chatMessageList;
    private ServerSocket serverSocket;

    private Boolean isServetChat;
    private String serverIp;
    private int serverPort;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        infoIp = (TextView) findViewById(R.id.infoip);
//        infoPort = (TextView) findViewById(R.id.infoport);
//        chtMesgListView = (ListView) findViewById(R.id.message_list);
//
//        spUsers = (Spinner) findViewById(R.id.spusers);
//        userList = new ArrayList<ChatClient>();
//        spUsersAdapter = new ArrayAdapter<ChatClient>(
//                ChatActivity.this, android.R.layout.simple_spinner_item, userList);
//        spUsersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spUsers.setAdapter(spUsersAdapter);
//
//        btnSentTo = (Button)findViewById(R.id.sentto);
//        btnSentTo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                ChatClient client = (ChatClient)spUsers.getSelectedItem();
//                if(client != null){
//                    String dummyMsg = "Dummy message from server.\n";
//                    client.chatThread.sendMsg(dummyMsg);
//                    msgLog += "- Dummy message to " + client.name + "\n";
//                    chatMsg.setText(msgLog);
//
//                }else{
//                    Toast.makeText(ChatActivity.this, "No user connected", Toast.LENGTH_LONG).show();
//                }
//
//            }
//        });

//        infoIp.setText(getIpAddress());
//
//        ChatServerThread chatServerThread = new ChatServerThread();
//        chatServerThread.start();

        chtMesgListView = (ListView) findViewById(R.id.message_list);


        editTextSend = (EditText) findViewById(R.id.send_message);
        editTextSend.setOnTouchListener(new View.OnTouchListener() {
            final int DRAWABLE_LEFT = 0;
            final int DRAWABLE_TOP = 1;
            final int DRAWABLE_RIGHT = 2;
            final int DRAWABLE_BOTTOM = 3;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int leftEdgeOfRightDrawable = editTextSend.getRight()
                            - editTextSend.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width();
                    if (event.getRawX() >= leftEdgeOfRightDrawable) {

//                        ChatClient client = (ChatClient)spUsers.getSelectedItem();
//                        if(client != null){
//                            String dummyMsg = "Dummy message from server.\n";
//                            client.chatThread.sendMsg(dummyMsg);
//                            msgLog += "- Dummy message to " + client.name + "\n";
//                            //chatMsg.setText(msgLog);

                        if(!TextUtils.isEmpty(editTextSend.getText().toString())) {
                            sendMessage(new ChatMessage("Server", editTextSend.getText().toString(), true));
                            editTextSend.setText("");
                        }

//                        }else{
//                            Toast.makeText(ChatActivity.this, "No user connected", Toast.LENGTH_LONG).show();
//                        }

                        return true;
                    }
                }
                return false;
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

        userList = new ArrayList<ChatClient>();
        chatMessageList = new ArrayList<>();

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            isServetChat = extras.getBoolean(ACTIVITY_CHAT_PARAM);
        } else {
            finish();
        }
        if (isServetChat) {
            setTitle(getString(R.string.app_name)+" "+getString(R.string.star));

            getIpAddress();

            ChatServerThread chatServerThread = new ChatServerThread();
            chatServerThread.start();

        }else{


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
            showSettings();
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

        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
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


    private class ChatServerThread extends Thread {

        @Override
        public void run() {
            Socket socket = null;

            try {
                serverSocket = new ServerSocket(SocketServerPORT);
                ChatActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        serverPort = serverSocket.getLocalPort();
                    }
                });

                while (true) {
                    socket = serverSocket.accept();
                    ChatClient client = new ChatClient();
                    userList.add(client);
                    ConnectThread connectThread = new ConnectThread(client, socket);
                    connectThread.start();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            spUsersAdapter.notifyDataSetChanged();
                        }
                    });
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }

        }

    }


    private class ConnectThread extends Thread {

        Socket socket;
        ChatClient connectClient;
        String msgToSend = "";

        ConnectThread(ChatClient client, Socket socket){
            connectClient = client;
            this.socket= socket;
            client.socket = socket;
            client.chatThread = this;
        }

        @Override
        public void run() {
            DataInputStream dataInputStream = null;
            DataOutputStream dataOutputStream = null;

            try {
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());

                final String n = dataInputStream.readUTF();

                connectClient.name = n;

                msgLog += connectClient.name + " connected@" +
                        connectClient.socket.getInetAddress() +
                        ":" + connectClient.socket.getPort() + "\n";
                ChatActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        //chatMsg.setText(msgLog);
                        sendMessage(new ChatMessage("Server", msgLog, false));
                    }
                });

                dataOutputStream.writeUTF("Welcome " + n + "\n");
                dataOutputStream.flush();

                broadcastMsg(n + " join our chat.\n");

                while (true) {
                    if (dataInputStream.available() > 0) {
                        final String newMsg = dataInputStream.readUTF();


                        msgLog += n + ": " + newMsg;
                        ChatActivity.this.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                               // chatMsg.setText(msgLog);
                                sendMessage(new ChatMessage(n, newMsg, false));
                            }
                        });

                        broadcastMsg(n + ": " + newMsg);
                    }

                    if(!msgToSend.equals("")){
                        dataOutputStream.writeUTF(msgToSend);
                        dataOutputStream.flush();
                        msgToSend = "";
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (dataInputStream != null) {
                    try {
                        dataInputStream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                if (dataOutputStream != null) {
                    try {
                        dataOutputStream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                userList.remove(connectClient);

                ChatActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        spUsersAdapter.notifyDataSetChanged();
                        Toast.makeText(ChatActivity.this,
                                connectClient.name + " removed.", Toast.LENGTH_LONG).show();

                        msgLog += "-- " + connectClient.name + " leaved\n";
                        ChatActivity.this.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                //chatMsg.setText(msgLog);
                                sendMessage(new ChatMessage("Server", msgLog, false));
                            }
                        });

                        broadcastMsg("-- " + connectClient.name + " leaved\n");
                    }
                });
            }

        }

        private void sendMsg(String msg){
            msgToSend = msg;
        }

    }

    private void broadcastMsg(String msg){
        for(int i=0; i<userList.size(); i++){
            userList.get(i).chatThread.sendMsg(msg);
            msgLog += "- send to " + userList.get(i).name + "\n";
        }

        ChatActivity.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                //chatMsg.setText(msgLog);
                sendMessage(new ChatMessage("send to", msgLog, false));
            }
        });
    }

    private void getIpAddress() {
        serverIp = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        serverIp += inetAddress.getHostAddress() + "\n";
                    }

                }

            }

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(ChatActivity.this, "Something Wrong! " + e.toString() + "\n", Toast.LENGTH_LONG).show();
        }
    }

    class ChatClient {
        String name;
        Socket socket;
        ConnectThread chatThread;

        @Override
        public String toString() {
            return name + ": " + socket.getInetAddress().getHostAddress();
        }
    }

    private void sendMessage(ChatMessage chatMessage){
        chatMessageList.add(chatMessage);
        messageAdapter.notifyDataSetChanged();
    }
}
