package com.dullyoung.bluetoothdemo.controller;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dullyoung.bluetoothdemo.R;
import com.dullyoung.bluetoothdemo.model.MsgInfo;
import com.dullyoung.bluetoothdemo.utils.CommonUtils;
import com.dullyoung.bluetoothdemo.utils.PermissionHelper;
import com.dullyoung.bluetoothdemo.utils.ToastCompat;
import com.dullyoung.bluetoothdemo.view.adapter.BTAdapter;
import com.dullyoung.bluetoothdemo.view.adapter.ChatAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 *
 */
public class MainActivity extends BaseActivity {


    @BindView(R.id.rv_list)
    RecyclerView mRvList;
    @BindView(R.id.btn_search)
    Button mBtnSearch;
    @BindView(R.id.et_input)
    EditText mEtInput;
    @BindView(R.id.btn_send)
    Button mBtnSend;
    @BindView(R.id.ll_btn)
    LinearLayout mLlBtn;
    @BindView(R.id.rv_chat)
    RecyclerView mRvChat;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    /**
     * 搜索到的蓝牙列表
     */
    private BTAdapter mBTAdapter;

    /**
     * 聊天列表
     */
    private ChatAdapter mChatAdapter;

    /**
     * 用来读取消息的线程
     */
    private ReadThread mReadThread;

    @Override
    protected void initViews() {
        setRvList();
        setRvChat();
        // Register for broadcasts when a device is discovered.
        adapter = BluetoothAdapter.getDefaultAdapter();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
        //开启服务线程
        new AcceptThread().start();
        mReadThread = new ReadThread();
    }

    private void setRvChat() {
        mChatAdapter = new ChatAdapter(null);
        mRvChat.setAdapter(mChatAdapter);
        mRvChat.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvChat.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
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

    public static final String UUID_STRING = "1f-20-38-72-84";

    ConnectThread mConnectThread;

    private void setRvList() {
        mBTAdapter = new BTAdapter(null);
        mRvList.setAdapter(mBTAdapter);
        mRvList.setLayoutManager(new LinearLayoutManager(getContext()));
        mBTAdapter.setOnItemClickListener((adapter1, view, position) -> {
            BluetoothDevice device = mBTAdapter.getData().get(position);
            mRvList.setVisibility(View.INVISIBLE);
            mRvChat.setVisibility(View.VISIBLE);
            if (mConnectThread == null) {
                mConnectThread = new ConnectThread(device);
                mConnectThread.start();
            }
        });
        mRvList.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
    }

    public static String TAG = "aaaa";
    public final int REQUEST_ENABLE_BT = 123;
    private BluetoothAdapter adapter;


    /**
     * 扫描 同时设置聊天RV为不可见
     * 蓝牙列表可见
     */
    private void scanBt() {

        mRvList.setVisibility(View.VISIBLE);
        mRvChat.setVisibility(View.INVISIBLE);

        if (adapter.isDiscovering()) {
            return;
        }
        getPermissionHelper().setMustPermissions2(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                //起初设计是打算做个文件传输，后来发现与socket并无区别，懒得做了。以下两个权限可要可不要
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        getPermissionHelper().checkAndRequestPermission(this, new PermissionHelper.OnRequestPermissionsCallback() {
            @Override
            public void onRequestPermissionSuccess() {
                if (CommonUtils.isBlueToothSupport(getContext())) {
                    if (adapter.isEnabled()) {
                        adapter = BluetoothAdapter.getDefaultAdapter();
                        adapter.startDiscovery();//开始扫描
                    } else {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    }
                }
            }

            @Override
            public void onRequestPermissionError() {
                ToastCompat.show(getContext(), "蓝牙开启失败");
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            scanBt();
        }
    }


    @OnClick({R.id.btn_search, R.id.btn_send})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_search:
                scanBt();
                break;
            case R.id.btn_send:
                if (mSocket != null) {
                    try {
                        OutputStream outputStream = mSocket.getOutputStream();
                        outputStream.write(mEtInput.getText().toString().getBytes());
                        runOnUiThread(() -> {
                            mChatAdapter.addData(new MsgInfo("我", mEtInput.getText().toString(), true));
                            mChatAdapter.notifyItemInserted(mChatAdapter.getData().size());
                            mRvChat.smoothScrollToPosition(mChatAdapter.getData().size());
                            mEtInput.setText("");
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.i(TAG, "mSocket: IOException:" + e);
                    }
                } else {
                    Log.i(TAG, "mmSocket == null ");
                    ToastCompat.show(getContext(), "请先扫描建立链接");
                }
                break;
            default:
                break;
        }
    }


    /**
     * 链接线程
     * 通过{@link BluetoothDevice#createRfcommSocketToServiceRecord(UUID)}
     * 获取socket
     * 然后使用{@link BluetoothSocket#connect()}链接服务端
     */
    private class ConnectThread extends Thread {

        /**
         * 要链接的设备信息，通过扫描或者本地已保存的设备信息获取
         */
        private BluetoothDevice mDevice;

        public ConnectThread(BluetoothDevice device) {
            mDevice = device;
        }

        @Override
        public void run() {
            adapter.cancelDiscovery();
            try {
                targetName = mDevice.getName();
                mSocket = mDevice.createRfcommSocketToServiceRecord(UUID.fromString(UUID_STRING));
                mSocket.connect();
                mReadThread = new ReadThread();
                mReadThread.start();
            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
        }

    }

    private BluetoothSocket mSocket;

    /**
     * 服务线程 通过{@link BluetoothAdapter#listenUsingInsecureRfcommWithServiceRecord(String, UUID)}
     * 生成一个{@link #mSocket}
     * 然后调用{@link BluetoothServerSocket#accept()}
     * 阻塞线程知道客户端连接 有客户端连接后才会执行后面
     * {@code mReadThread = new ReadThread();}开启线程读取
     */
    private class AcceptThread extends Thread {
        public AcceptThread() {
        }

        @Override
        public void run() {
            try {
                BluetoothServerSocket tmp = null;
                tmp = adapter.listenUsingRfcommWithServiceRecord("BTCommunication", UUID.fromString(UUID_STRING));
                mSocket = tmp.accept();
                mReadThread = new ReadThread();
                mReadThread.start();
            } catch (IOException e) {
                Log.i(TAG, "Socket's listen() method failed", e);
            }
        }
    }

    /**
     * 通过获取{@link Socket#getInputStream()} 读取缓冲区数据
     */
    private class ReadThread extends Thread {
        byte[] buffer = new byte[1024];
        int bytes;
        InputStream mmInStream = null;

        @Override
        public void run() {
            try {
                mmInStream = mSocket.getInputStream();
                while ((bytes = mmInStream.read(buffer)) > 0) {
                    byte[] bufData = new byte[bytes];
                    System.arraycopy(buffer, 0, bufData, 0, bytes);
                    Log.i(TAG, "ReadThread: " + new String(bufData));
                    runOnUiThread(() -> {
                        mChatAdapter.addData(new MsgInfo(targetName, new String(bufData), false));
                        mChatAdapter.notifyItemInserted(mChatAdapter.getData().size());
                        mRvChat.smoothScrollToPosition(mChatAdapter.getData().size());
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 聊天对方的名字 默认 对方 在链接的时候以蓝牙设备名为准
     */
    private String targetName = "对方";
}