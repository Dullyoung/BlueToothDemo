package com.dullyoung.bluetoothdemo.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.coorchice.library.SuperTextView;
import com.dullyoung.bluetoothdemo.R;
import com.dullyoung.bluetoothdemo.controller.server.ServerActivity;
import com.dullyoung.bluetoothdemo.controller.user.UserActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Dullyoung
 */
public class SplashActivity extends BaseActivity {


    @BindView(R.id.stv_server)
    SuperTextView mStvServer;
    @BindView(R.id.stv_user)
    SuperTextView mStvUser;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initViews() {

    }


    @OnClick({R.id.stv_server, R.id.stv_user, R.id.stv_link})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.stv_server:
                startActivity(new Intent(this, ServerActivity.class));
                finish();
                break;
            case R.id.stv_user:
                startActivity(new Intent(this, UserActivity.class));
                finish();
                break;
            case R.id.stv_link:
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
            default:
                break;
        }
    }
}