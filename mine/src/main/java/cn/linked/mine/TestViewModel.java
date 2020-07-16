package cn.linked.mine;

import androidx.lifecycle.ViewModel;

public class TestViewModel extends ViewModel {
    public String test;
    @Override
    protected void onCleared() {
        System.out.println("TestViewModel======clear");
    }
}
