package net.wangtu.android.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import net.wangtu.android.R;
import net.wangtu.android.activity.HomeActivity;
import net.wangtu.android.common.statusbar.SystemStatusManager;
import net.wangtu.android.common.util.ImageCacheUtil;
import net.wangtu.android.common.util.Util;
import net.wangtu.android.util.LoginUtil;
import net.wangtu.android.util.album.XImageUtil;

/**
 * Created by zhangxz on 2017/7/4.
 */

public class BaseFragment extends Fragment {
    protected View leftBarBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View childView = toCreateView(inflater,container,savedInstanceState);
        childView.setPadding(0,Util.getStatusHeightIfNeed(getContext()),0,0);

        //设置padding
        View headerView = childView.findViewById(R.id.header_container);
        if(headerView != null){
            leftBarBtn = headerView.findViewById(R.id.header_back);
            ImageView imageView =  (ImageView)headerView.findViewById(R.id.header_back);
            ImageCacheUtil.lazyLoad(imageView,LoginUtil.getAvatarFile(),R.drawable.icon_header,false);
        }

        //childView.setPadding(0, Util.getStatusHeight(getContext()), 0, 0);
        return childView;
    }

    public View toCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return null;
    }
}
