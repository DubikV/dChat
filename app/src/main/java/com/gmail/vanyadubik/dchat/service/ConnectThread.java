package com.gmail.vanyadubik.dchat.service;


import android.content.Context;

import com.gmail.vanyadubik.dchat.model.ChatClient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class ConnectThread  extends Thread {

    Context context;
    Socket socket;
    ChatClient connectClient;
    String msgToSend = "";

    ConnectThread(Context context, ChatClient client, Socket socket){
        this.context = context;
        this.connectClient = client;
        this.socket= socket;
        client.setSocket(socket);
        client.setChatThread(this);
    }

    @Override
    public void run() {
        DataInputStream dataInputStream = null;
        DataOutputStream dataOutputStream = null;

//        try {
//            dataInputStream = new DataInputStream(socket.getInputStream());
//            dataOutputStream = new DataOutputStream(socket.getOutputStream());
//
//            String n = dataInputStream.readUTF();
//
//            connectClient.setName(n);
//
//            msgLog += connectClient.name + " connected@" +
//                    connectClient.socket.getInetAddress() +
//                    ":" + connectClient.socket.getPort() + "\n";
//            context.runOnUiThread(new Runnable() {
//
//                @Override
//                public void run() {
//                    chatMsg.setText(msgLog);
//                }
//            });
//
//            dataOutputStream.writeUTF("Welcome " + n + "\n");
//            dataOutputStream.flush();
//
//            broadcastMsg(n + " join our chat.\n");
//
//            while (true) {
//                if (dataInputStream.available() > 0) {
//                    String newMsg = dataInputStream.readUTF();
//
//
//                    msgLog += n + ": " + newMsg;
//                    MainActivity.this.runOnUiThread(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            chatMsg.setText(msgLog);
//                        }
//                    });
//
//                    broadcastMsg(n + ": " + newMsg);
//                }
//
//                if(!msgToSend.equals("")){
//                    dataOutputStream.writeUTF(msgToSend);
//                    dataOutputStream.flush();
//                    msgToSend = "";
//                }
//
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (dataInputStream != null) {
//                try {
//                    dataInputStream.close();
//                } catch (IOException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//            }
//
//            if (dataOutputStream != null) {
//                try {
//                    dataOutputStream.close();
//                } catch (IOException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//            }
//
//            userList.remove(connectClient);
//
//            context.runOnUiThread(new Runnable() {
//
//                @Override
//                public void run() {
//                    spUsersAdapter.notifyDataSetChanged();
//                    Toast.makeText(MainActivity.this,
//                            connectClient.name + " removed.", Toast.LENGTH_LONG).show();
//
//                    msgLog += "-- " + connectClient.name + " leaved\n";
//                    context.runOnUiThread(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            chatMsg.setText(msgLog);
//                        }
//                    });
//
//                    broadcastMsg("-- " + connectClient.name + " leaved\n");
//                }
//            });
//        }

    }

    private void sendMsg(String msg){
        msgToSend = msg;
    }

}
