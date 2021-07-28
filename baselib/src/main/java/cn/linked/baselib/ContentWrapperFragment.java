package cn.linked.baselib;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import lombok.Getter;
import lombok.Setter;

public class ContentWrapperFragment extends BaseFragment {

    @Setter
    @Getter
    private View layoutView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(layoutView != null) {
            return layoutView;
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

}
