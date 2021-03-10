package com.dullyoung.bluetoothdemo.controller;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CpuUsageInfo;
import android.util.Log;
import android.widget.Button;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dullyoung.bluetoothdemo.R;
import com.dullyoung.bluetoothdemo.utils.CommonUtils;
import com.dullyoung.bluetoothdemo.utils.PermissionHelper;
import com.dullyoung.bluetoothdemo.view.adapter.BTAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {


    @BindView(R.id.rv_list)
    RecyclerView mRvList;
    @BindView(R.id.btn_search)
    Button mBtnSearch;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    private BTAdapter mBTAdapter;

    @Override
    protected void initViews() {
        setRvList();
        // Register for broadcasts when a device is discovered.
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) {
                return;
            }
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null && device.getName() != null && !mBTAdapter.getData().contains(device)) {
                    runOnUiThread(() -> {
                        mBTAdapter.addData(device);
                        mBTAdapter.notifyItemInserted(mBTAdapter.getData().size());
                    });
                }
            }

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Don't forget to unregister the ACTION_FOUND receiver.
        if (adapter != null && adapter.isDiscovering()) {
            adapter.cancelDiscovery();
        }
        unregisterReceiver(receiver);
    }

    private void setRvList() {
        mBTAdapter = new BTAdapter(null);
        mRvList.setAdapter(mBTAdapter);
        mRvList.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public static String TAG = "aaaa";
    BluetoothAdapter adapter;

    private void scanBt() {
        getPermissionHelper().setMustPermissions2(Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        getPermissionHelper().checkAndRequestPermission(this, new PermissionHelper.OnRequestPermissionsCallback() {
            @Override
            public void onRequestPermissionSuccess() {
                if (CommonUtils.isBlueToothEnable(getContext()) && CommonUtils.openBlueTooth(getContext())) {
                    adapter = BluetoothAdapter.getDefaultAdapter();
                    adapter.startDiscovery();
                    Set<BluetoothDevice> set = adapter.getBondedDevices();
                    List<BluetoothDevice> list = new ArrayList<>(set);
                    mBTAdapter.setNewInstance(list);
                }
            }

            @Override
            public void onRequestPermissionError() {

            }
        });

    }


    @OnClick(R.id.btn_search)
    public void onViewClicked() {
        scanBt();
    }
}