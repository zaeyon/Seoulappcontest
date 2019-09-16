package com.example.seoulapp.ui.dashboard;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.support.annotation.Nullable;
import android.support.annotation.NonNull;

import com.example.seoulapp.MainActivity;
import com.example.seoulapp.R;



public class DashboardFragment extends ListFragment {

    private DashboardViewModel dashboardViewModel;
    ListView lv;
    TextView tv_comment;
    ImageView User_img;
    ImageView onclick_heart;
    ImageView profile_img;
    static int count; //즐찾도 할까?

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        //추상화된 view를 실제적으로
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        final View getItemId = inflater.inflate(R.layout.review_listview_itemview, container, false);

        tv_comment = getItemId.findViewById(R.id.comment);
        User_img = (ImageView) getItemId.findViewById(R.id.User_img);
        onclick_heart = getItemId.findViewById(R.id.love_filed);
        profile_img = getItemId.findViewById(R.id.User_profilePicture);

 /*       setImageShape(profile_img);
        User_img.setClickable(true);*/

       /* dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);*/
        ReviewAdapter adapter = new ReviewAdapter();
        lv = inflater.inflate(R.layout.fragment_dashboard, container, false).findViewById(R.id.Review_List);

        setListAdapter(adapter);
        adapter.addItem((ContextCompat.getDrawable(getActivity(), R.drawable.camera_icon2)), ContextCompat.getDrawable(getActivity(), R.drawable.test_beach), "unsplash", "text");
        adapter.addItem((ContextCompat.getDrawable(getActivity(), R.drawable.icon_heart)), ContextCompat.getDrawable(getActivity(), R.drawable.ic_dashboard_black_24dp), "sterning", "aaaaa");
        adapter.addItem((ContextCompat.getDrawable(getActivity(), R.drawable.ic_notifications_black_24dp)), ContextCompat.getDrawable(getActivity(), R.drawable.icon_picture), "sonder", "bbbb");
        adapter.addItem((ContextCompat.getDrawable(getActivity(), R.drawable.test_beach)), ContextCompat.getDrawable(getActivity(), R.drawable.star), "sonder", "bbbb");
        //db에서 받아온 값으로..

       /* final TextView textView = (TextView) root.findViewById(R.id.text_dashboard);
        dashboardViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    //actionBar에 버튼 추가
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu._action_bar, menu);
    }

    @Override //버튼 눌렀을 시의 응답
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();

        if (id == R.id.action_button) {
            Intent intent = new Intent(getActivity(), CreateReviewAct.class);
            startActivity(intent);
            //process your onClick here
        }
        return super.onOptionsItemSelected(item);
    }

}
