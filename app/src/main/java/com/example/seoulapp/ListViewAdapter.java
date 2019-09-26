package com.example.seoulapp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.CropTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;


public class ListViewAdapter extends BaseAdapter implements Filterable {

    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<ListViewItem> listViewItemList = new ArrayList<ListViewItem>();
   // 필터링된 결과 데이터를 저장하기 위한 ArrayList. 최초에는 전체 리스트 보유
    private ArrayList<ListViewItem> filteredItemList = listViewItemList;

    Filter listFilter;

    @Override
    public Filter getFilter(){
        if(listFilter == null){
            listFilter = new ListFilter();
        }
        return listFilter;
    }

    private class ListFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint == null || constraint.length() == 0) {
                results.values = listViewItemList;
                results.count = listViewItemList.size();
            } else {
                ArrayList<ListViewItem> itemList = new ArrayList<ListViewItem>();

                for (ListViewItem item : listViewItemList) {
                    if (item.getShopName().toUpperCase().contains(constraint.toString().toUpperCase())) {
                        itemList.add(item);
                    }
                }

                results.values = itemList;
                results.count = itemList.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            // update listview by filtered data list
            filteredItemList = (ArrayList<ListViewItem>) results.values;

            // notify
            if (results.count > 0) {
                notifyDataSetChanged();
                ;
            } else {
                notifyDataSetInvalidated();
            }
        }
    }



    // ListViewAdapter의 생성자
    public ListViewAdapter() {

    }

    // Adapter에 사용되는 데이터의 개수를 리턴
    @Override
    public int getCount() {
        return filteredItemList.size();
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_item, parent, false);

        }

        ImageView shopProfileImageView = convertView.findViewById(R.id.shop_profileImage);

        ImageView shopRepImageView1 = convertView.findViewById(R.id.shop_representation1);
        ImageView shopRepImageView2 = convertView.findViewById(R.id.shop_representation2);
        ImageView shopRepImageView3 = convertView.findViewById(R.id.shop_representation3);


        TextView shopBuildingView = convertView.findViewById(R.id.shop_building);
        TextView shopFloorView = convertView.findViewById(R.id.shop_floor);
        TextView shopNameView = convertView.findViewById(R.id.shop_name);
        TextView shopRocationView = convertView.findViewById(R.id.shop_rocation);

        // Data Set(filteredItemList)에서 position에 위치한 데이터 참조 획득
        ListViewItem listViewItem = filteredItemList.get(position);

        shopBuildingView.setText(listViewItem.getShopBuilding());
        shopFloorView.setText(listViewItem.getShopFloor());
        shopNameView.setText(listViewItem.getShopName());
        shopRocationView.setText(listViewItem.getShopRocation());

        Glide.with(context).load(listViewItem.getProfileImage())
                .apply(RequestOptions.circleCropTransform())
                .into(shopProfileImageView);

        Glide.with(context).load(listViewItem.getShopRepresentation1()).into(shopRepImageView1);
        Glide.with(context).load(listViewItem.getShopRepresentation2()).into(shopRepImageView2);
        Glide.with(context).load(listViewItem.getShopRepresentation3()).into(shopRepImageView3);

        return convertView;
    }

    // 지정된 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수
    @Override
    public long getItemId(int position) {
        return position;
    }

    // 지정된 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성
    public void addItem(String profileImage, String name,String build,String floor, String rocation,String category, String style, String intro, String req1, String req2, String req3) {
        ListViewItem item = new ListViewItem();

        item.setProfileImage(profileImage);
        item.setShopName(name);
        item.setShopBuilding(build);
        item.setShopFloor(floor);
        item.setShopRocation(rocation);
        item.setShopCategory(category);
        item.setShopStyle(style);
        item.setShopIntro(intro);
        item.setShopRepresentation1(req1);
        item.setShopRepresentation2(req2);
        item.setShopRepresentation3(req3);
        listViewItemList.add(item);
    }
}
