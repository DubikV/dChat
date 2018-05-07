package com.gmail.vanyadubik.dchat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gmail.vanyadubik.dchat.R;
import com.gmail.vanyadubik.dchat.model.ChatMessage;

import java.util.List;

public class ChatMessageAdapter extends BaseAdapter {

    private List<ChatMessage> list;
    private LayoutInflater layoutInflater;

    public ChatMessageAdapter(Context context, List<ChatMessage> list) {
        this.list = list;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        ChatMessage chatMessage = getDataTable(position);

        if (chatMessage.isOwn()) {
            view = layoutInflater.inflate(R.layout.item_message_user, parent, false);
        }else if (chatMessage.isServer()) {
            view = layoutInflater.inflate(R.layout.item_message_server, parent, false);
        } else {
            view = layoutInflater.inflate(R.layout.item_message_agent, parent, false);
        }


        TextView textUser = (TextView) view.findViewById(R.id.text_user);
        textUser.setText(chatMessage.getMessage());

        if (!chatMessage.isServer()) {
            TextView nameUser = (TextView) view.findViewById(R.id.name_user);
            nameUser.setText(chatMessage.getUser());
        }

        return view;
    }

    private ChatMessage getDataTable(int position) {
        return (ChatMessage) getItem(position);
    }

}
