package com.dullyoung.bluetoothdemo.controller;

import android.content.Intent;
import android.view.View;

import com.dullyoung.bluetoothdemo.R;

import butterknife.OnClick;

/**
 * @author Dullyoung
 */
public class SplashActivity extends BaseActivity {


    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initViews() {

    }


    @OnClick({R.id.stv_ble, R.id.stv_ssp})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.stv_ble:
                startActivity(new Intent(this, BleActivity.class));
                finish();
                break;
            case R.id.stv_ssp:
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
            default:
                break;
        }
    }
}