package com.example.seoulapp.ui.notifications;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.seoulapp.R;

import java.util.ArrayList;

public class ListAdapter extends BaseAdapter {

  private ArrayList<ItemData> m_oData = new ArrayList<ItemData>();

  /*
  public ListAdapter(ArrayList<ItemData> _oData) {
    m_oData = _oData;
    nListCnt = m_oData.size();
  }

   */

  public ListAdapter() {

  }

  @Override
  public int getCount() {
    return m_oData.size();
  }

  @Override
  public Object getItem(int position) {
    return m_oData.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {

    final int pos = position;
    final Context context = parent.getContext();

    if(convertView == null) {

      LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      convertView = inflater.inflate(R.layout.listview_favorite_shop, parent, false);
     }

    TextView oTextShopName = (TextView) convertView.findViewById(R.id.textShopName);
    ImageView oImageShopLogo = (ImageView) convertView.findViewById(R.id.imageShopLogo);

    ItemData item = m_oData.get(position);

    oTextShopName.setText(item.getStrShopName());

    Glide.with(context).load(item.getStrShopImage()).into(oImageShopLogo);


    return convertView;
  }

  public void addItem(String title, String image)
  {
    ItemData item = new ItemData();

    item.setStrShopName(title);
    item.setStrShopImage(image);

    m_oData.add(item);
  }
}

