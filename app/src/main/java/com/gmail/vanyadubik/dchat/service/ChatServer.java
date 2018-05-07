package com.gmail.vanyadubik.dchat.service;

import android.widget.Toast;

import com.gmail.vanyadubik.dchat.R;
import com.gmail.vanyadubik.dchat.activity.ChatActivity;
import com.gmail.vanyadubik.dchat.model.ChatMessage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatServer {
    private ChatActivity activity;
    private ServerSocket serverSocket;
    private List<ChatClient> userList = new ArrayList<ChatClient>();
	static final int socketServerPORT = 8080;
    private String serverIp;
    private String serverNameID;

	public ChatServer(ChatActivity activity, String serverIp) {
		this.activity = activity;
        this.serverIp = serverIp;
        this.serverNameID =  activity.getString(R.string.id_server);
		Thread chatServerThread = new Thread(new ChatServerThread());
        chatServerThread.start();
	}

    private class ChatServerThread extends Thread {

        @Override
        public void run() {
            Socket socket = null;

            try {
                serverSocket = new ServerSocket(socketServerPORT);
                activity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        activity.setPort(serverSocket.getLocalPort());

                        final String msgConn = activity.getString(R.string.chat_started) +" "+
                                serverIp + ":" + String.valueOf(serverSocket.getLocalPort());

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                activity.showMessage(new ChatMessage(serverNameID, msgConn, false, true, new Date()));
                                activity.showLoginDialog();
                            }
                        });
                    }
                });

                while (true) {
                    socket = serverSocket.accept();
                    ChatClient client = new ChatClient();
                    userList.add(client);
                    ConnectThread connectThread = new ConnectThread(client, socket);
                    connectThread.start();
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

                final String msgConn = connectClient.name + " " + activity.getString(R.string.connected) +" "+
                        connectClient.socket.getInetAddress() + ":" + connectClient.socket.getPort();

                activity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        activity.showMessage(new ChatMessage(serverNameID, msgConn, false, true, new Date()));
                    }
                });

                dataOutputStream.writeUTF(serverNameID + activity.getString(R.string.welcome)+" " + n + "");
                dataOutputStream.flush();

                broadcastMsg(serverNameID + n + " "+activity.getString(R.string.join_our_chat)+".");

                while (true) {
                    if (dataInputStream.available() > 0) {
                        final String newMsg = dataInputStream.readUTF();

                        broadcastMsg(n + activity.getString(R.string.diviner_message) + newMsg);
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

                activity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {


                        final String msgRem = connectClient.name + " "+activity.getString(R.string.leaved)+".";

                        Toast.makeText(activity, msgRem, Toast.LENGTH_LONG).show();

                        activity.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                activity.showMessage(new ChatMessage(serverNameID, msgRem, false, true, new Date()));
                            }
                        });

                        broadcastMsg(serverNameID + activity.getString(R.string.diviner_message)+
                                serverNameID +
                                 connectClient.name + " " + activity.getString(R.string.leaved));
                    }
                });
            }

        }

        private void sendMsg(String msg){
            msgToSend = msg;
        }

    }


    private void broadcastMsg(String msg){
       // String msgLog = "";
        for(int i=0; i<userList.size(); i++){
            userList.get(i).chatThread.sendMsg(msg);
          //  msgLog = "- send to " + userList.get(i).name + "\n";
        }

//        final String finalMsgLog = msgLog;
//        activity.runOnUiThread(new Runnable() {
//
//            @Override
//            public void run() {
//                activity.sendMessage(new ChatMessage("send to", finalMsgLog, false));
//            }
//        });
    }

    public void destroy(){

        if (userList.size()>0){
            broadcastMsg(serverNameID+activity.getString(R.string.chat_was_destroyed));
        }

        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
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
}
