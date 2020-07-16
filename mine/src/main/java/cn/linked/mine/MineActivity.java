package cn.linked.mine;

import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class MineActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TestViewModel model =new ViewModelProvider(this,new ViewModelProvider.NewInstanceFactory()).get(TestViewModel.class);
        System.out.println(model.test);
        model.test="sdhsjfjsdfhkdgtgrt";
        if(savedInstanceState!=null){
            System.out.println(savedInstanceState.get("key"));
        }
        System.out.println("ViewModel: "+model);
        System.out.println("onCreate");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("key","ssdassdsdd");
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("onDestroy");
    }
}
