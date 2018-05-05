package com.gmail.vanyadubik.dchat.service;

import android.content.Context;

import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer extends Thread {

    Context context;
    ServerSocket serverSocket;

    ChatServer(Context context, ServerSocket serverSocket){
        this.context = context;
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        Socket socket = null;

//        try {
//            serverSocket = new ServerSocket(SocketServerPORT);
//            context.runOnUiThread(new Runnable() {
//
//                @Override
//                public void run() {
//                    infoPort.setText("I'm waiting here: "
//                            + serverSocket.getLocalPort());
//                }
//            });
//
//            while (true) {
//                socket = serverSocket.accept();
//                ChatClient client = new ChatClient();
//                userList.add(client);
//                ConnectThread connectThread = new ConnectThread(client, socket);
//                connectThread.start();
//
//                context.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        spUsersAdapter.notifyDataSetChanged();
//                    }
//                });
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (socket != null) {
//                try {
//                    socket.close();
//                } catch (IOException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//            }
//        }

    }

}
