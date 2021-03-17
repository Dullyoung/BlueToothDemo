package com.dullyoung.bluetoothdemo.controller;

import android.content.Context;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.dullyoung.bluetoothdemo.model.EventStub;
import com.dullyoung.bluetoothdemo.utils.PermissionHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;

/**
 *
 * @author Dullyoung
 */
public abstract class BaseActivity extends AppCompatActivity   {

    protected PermissionHelper mPermissionHelper;

    public PermissionHelper getPermissionHelper() {
        return mPermissionHelper;
    }

    @Override
    protected void onCreate(@androidx.annotation.Nullable android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(getLayoutId());
        setTranslucentStatus();
        ButterKnife.bind(this);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        mPermissionHelper = new PermissionHelper();
        initVars();
        initViews();
    }


    protected void initVars() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @androidx.annotation.NonNull String[] permissions, @androidx.annotation.NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPermissionHelper.onRequestPermissionsResult(this, requestCode);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void stub(EventStub stub) {

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    public Context getContext() {
        return this;
    }

    protected abstract int getLayoutId();

    protected abstract void initViews();

    protected void setTranslucentStatus() {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.addFlags(Integer.MIN_VALUE);
            window.setStatusBarColor(0x00000000);
        } else if (Build.VERSION.SDK_INT >= 19) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    public void setFullScreen() {
        getWindow().getDecorView().setSystemUiVisibility(1280 | android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

}
