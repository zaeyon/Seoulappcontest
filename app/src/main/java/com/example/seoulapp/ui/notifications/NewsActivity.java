package com.example.seoulapp.ui.notifications;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.example.seoulapp.R;

import java.util.ArrayList;

public class NewsActivity extends AppCompatActivity {
  private ListView m_oListView = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_news);

    String[] strNews = {"새로운 소식1", "새로운 소식2", "새로운 소식3"};
    int nNumCnt = 0;
    ArrayList<NewsData> oData = new ArrayList<>();
    for (int i = 0; i < strNews.length; ++i) {
      NewsData oItem = new NewsData();
      oItem.strNews = strNews[i];
      nNumCnt++;
      oData.add(oItem);
      if(nNumCnt >= strNews.length) nNumCnt = 0;
    }

    m_oListView = (ListView) findViewById(R.id.newsListView);
    NewsListAdapter oAdapter = new NewsListAdapter(oData);
    m_oListView.setAdapter(oAdapter);
  }
}