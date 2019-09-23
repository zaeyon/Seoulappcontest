package com.example.seoulapp.ui.dashboard;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import static java.sql.Types.NULL;

public class DashboardViewModel extends ViewModel {

  private MutableLiveData<String> mText;

  public DashboardViewModel() {
    mText = new MutableLiveData<>();
    mText.setValue("This is dashboard fragment");
  }

  public int Increace(int[] a, int i, int value){ //s
    a[i] = value + 1;
    return a[i];
  }

  public LiveData<String> getText() {
    return mText;
  }
}