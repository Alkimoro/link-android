package cn.linked.mine;

import android.os.Bundle;
import android.os.Environment;
import android.view.PixelCopy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class MineActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Fragment fragment=new MineFragment();
        MineFragmentViewModel viewModel=new MineFragmentViewModel(fragment);

        System.out.println(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS));
        System.out.println(getApplicationContext().getExternalFilesDir(null).getAbsolutePath());
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        System.out.println("onSave");
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
