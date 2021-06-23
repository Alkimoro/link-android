package cn.linked.baselib;

import android.content.Context;
import android.view.LayoutInflater;

import androidx.lifecycle.LifecycleOwner;

/**
 *  整合Fragment 和 Activity 的共同功能
 * */
public interface UIContext {

    LifecycleOwner getLifecycleOwner();

    Context getContext();

    LayoutInflater getLayoutInflater();

}
