package com.gmail.vanyadubik.dchat.service;

import android.widget.Toast;

import com.gmail.vanyadubik.dchat.R;
import com.gmail.vanyadubik.dchat.activity.ChatActivity;
import com.gmail.vanyadubik.dchat.model.ChatMessage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;

public class ChatClient {
	private ChatActivity activity;
    private String name;
    private String dstAddress;
    private int dstPort;
    private ChatClientThread chatClientThread;
    private String serverNameID;

	public ChatClient(ChatActivity activity, String name, String address, int port) {
		this.activity = activity;
        this.name = name;
        this.dstAddress = address;
        this.dstPort = port;
        this.serverNameID =  activity.getString(R.string.id_server);
		chatClientThread = new ChatClientThread();
        chatClientThread.start();
	}

    private class ChatClientThread extends Thread {

        String msgToSend = "";
        boolean goOut = false;

        @Override
        public void run() {
            Socket socket = null;
            DataOutputStream dataOutputStream = null;
            DataInputStream dataInputStream = null;

            try {
                socket = new Socket(dstAddress, dstPort);
                dataOutputStream = new DataOutputStream(
                        socket.getOutputStream());
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream.writeUTF(name);
                dataOutputStream.flush();

                while (!goOut) {
                    if (dataInputStream.available() > 0) {
                        String msgLog = dataInputStream.readUTF();

                        ChatMessage chatMessage = null;
                        if(msgLog.contains(serverNameID)){
                            chatMessage = new ChatMessage(serverNameID, msgLog.replace(serverNameID,""), false, true, new Date());
                        }else{
                            String[] data = msgLog.split(activity.getString(R.string.diviner_message));
                            String nameUser = data[0];
                            String message = data[1];

                            if(nameUser.equals(name)) {
                                chatMessage = new ChatMessage(activity.getString(R.string.user_me), message, true, false, new Date());
                            }else{
                                chatMessage = new ChatMessage(nameUser, message, false, false, new Date());
                            }
                        }

                        final ChatMessage finalChatMessage = chatMessage;
                        activity.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                activity.showMessage(finalChatMessage);
                            }
                        });
                    }

                    if(!msgToSend.equals("")){
                        dataOutputStream.writeUTF(msgToSend);
                        dataOutputStream.flush();
                        msgToSend = "";
                    }
                }

            } catch (UnknownHostException e) {
                e.printStackTrace();
                final String eString = e.toString();
                activity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(activity, eString, Toast.LENGTH_LONG).show();
                    }

                });
            } catch (IOException e) {
                e.printStackTrace();
                final String eString = e.toString();
                activity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(activity, eString, Toast.LENGTH_LONG).show();
                    }

                });
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
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

                if (dataInputStream != null) {
                    try {
                        dataInputStream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                activity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        activity.showLoginDialog();
                    }

                });
            }

        }

        private void sendMsg(String msg){
            msgToSend = msg;
        }

        private void disconn(){
            goOut = true;
        }
    }

    public void sendMessage(String msg){
        chatClientThread.sendMsg(msg);
    }

    public void disconnect(){
        chatClientThread.sendMsg(activity.getString(R.string.living));
        chatClientThread.disconn();
    }


}
