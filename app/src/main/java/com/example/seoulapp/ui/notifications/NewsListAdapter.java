package com.example.seoulapp.ui.notifications;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.seoulapp.R;

import java.util.ArrayList;

public class NewsListAdapter extends BaseAdapter {
    LayoutInflater inflater = null;
    private ArrayList<NewsData> m_oData = null;
    private int nListCnt = 0;

    public NewsListAdapter(ArrayList<NewsData> _oData) {
        m_oData = _oData;
        nListCnt = m_oData.size();
    }

    @Override
    public int getCount() {
        Log.i("TAG", "getCount");
        return nListCnt;
    }

    @Override
    public Object getItem(int position) { return null; }

    @Override
    public long getItemId(int position) { return 0; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            final Context context = parent.getContext();
            if(inflater == null) {
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            convertView = inflater.inflate(R.layout.listview_news, parent, false);
        }

        TextView oTextNews = (TextView) convertView.findViewById(R.id.textNews);

        oTextNews.setText(m_oData.get(position).strNews);

        return convertView;
    }
}
