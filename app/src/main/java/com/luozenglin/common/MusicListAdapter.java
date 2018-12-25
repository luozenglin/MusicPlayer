package com.luozenglin.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.luozenglin.mymusicplayer.R;

import java.util.List;

public class MusicListAdapter extends ArrayAdapter<MusicItem> {

    private int resourceId;

    public MusicListAdapter(Context context, int textViewResourceId, List<MusicItem> objects){
        super(context,textViewResourceId,objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position,View convertView, ViewGroup parent) {
        MusicItem musicItem = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        TextView musicTitle = (TextView) view.findViewById(R.id.item_music_title_tv);
        TextView singer = (TextView) view.findViewById(R.id.item_singer_tv);
        musicTitle.setText(musicItem.getTitle());
        singer.setText(musicItem.getSinger());
        return view;
    }
}
