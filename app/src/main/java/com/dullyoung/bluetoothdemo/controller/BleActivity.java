package com.dullyoung.bluetoothdemo.controller;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
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
import com.dullyoung.bluetoothdemo.utils.VKit;
import com.dullyoung.bluetoothdemo.view.adapter.BleAdapter;
import com.dullyoung.bluetoothdemo.view.adapter.ChatAdapter;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Dullyoung
 */
public class BleActivity extends BaseActivity {


    @BindView(R.id.rv_list)
    RecyclerView mRvList;
    @BindView(R.id.rv_chat)
    RecyclerView mRvChat;
    @BindView(R.id.btn_search)
    Button mBtnSearch;
    @BindView(R.id.et_input)
    EditText mEtInput;
    @BindView(R.id.btn_send)
    Button mBtnSend;
    @BindView(R.id.ll_btn)
    LinearLayout mLlBtn;

    private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 101;
    private static final long SCAN_TIME_OUT = 10000;
    private static final String TAG = "aaaa";
    private BleAdapter mBleAdapter;
    private BluetoothGatt mBluetoothGatt;
    private final UUID mUUID = UUID.fromString("12345678-1234-1234-1234-123456123456");
    private BluetoothManager bluetoothManager;

    private ChatAdapter mChatAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_ble;
    }

    @Override
    protected void initViews() {
        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mProgressDialog = new ProgressDialog(this);
        setRvList();
        setRvChat();
        autoConnect();
    }

    private ProgressDialog mProgressDialog;

    private void autoConnect() {
        mProgressDialog.setCancelable(false);
        mProgressDialog.setTitle("???????????????");
        mProgressDialog.show();
        if (mBluetoothGatt == null) {
            Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
            for (BluetoothDevice device : devices) {
                mBluetoothGatt = device.connectGatt(getContext(), false, new MGattCallBack());
                if (mBluetoothGatt == null) {
                    continue;
                }
                if (mBluetoothGatt.getService(UUID_SERVER) != null) {
                    break;
                }
            }
        }
        mProgressDialog.dismiss();
    }

    private void setRvChat() {
        mChatAdapter = new ChatAdapter(null);
        mRvChat.setAdapter(mChatAdapter);
        mRvChat.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvChat.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
    }


    private void setRvList() {
        mBleAdapter = new BleAdapter(null);
        mRvList.setAdapter(mBleAdapter);
        mRvList.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mBleAdapter.setOnItemClickListener((adapter, view, position) -> {
            ScanResult scanResult = mBleAdapter.getData().get(position);
            //???????????????????????????UUID?????????????????????????????????????????????????????????????????????????????????
            // ????????????????????????????????????????????????????????????UUID
            mBluetoothGatt = scanResult.getDevice().connectGatt(getContext(), false, new MGattCallBack());
        });

    }


    @OnClick({R.id.btn_search, R.id.btn_send, R.id.btn_service})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_search:
                mRvChat.setVisibility(View.INVISIBLE);
                mRvList.setVisibility(View.VISIBLE);
                startScan();
                break;
            case R.id.btn_send:
                sendData(mEtInput.getText().toString());
                break;
            case R.id.btn_service:
                mRvChat.setVisibility(View.VISIBLE);
                mRvList.setVisibility(View.INVISIBLE);
                startAdvertising("BleChatServer");
                break;
            default:
                break;
        }
    }


    /**
     * ??????????????????????????????????????????
     */
    private void startScan() {
        if (!CommonUtils.checkBleSupport(this)) {
            return;
        }
        getPermissionHelper().setMustPermissions2(Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN);
        getPermissionHelper().checkAndRequestPermission(this, new PermissionHelper.OnRequestPermissionsCallback() {
            @Override
            public void onRequestPermissionSuccess() {
                if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    return;
                }
                realScan();
            }

            @Override
            public void onRequestPermissionError() {
                ToastCompat.show(getContext(), "??????????????????");
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            startScan();
        }
    }

    /**
     * ??????????????????
     */
    private void realScan() {
        BluetoothLeScanner scanner = mBluetoothAdapter.getBluetoothLeScanner();
        scanner.startScan(mScanCallback);
        mBleAdapter.setNewInstance(null);
        Log.i(TAG, "realScan: " + System.currentTimeMillis());
        VKit.postDelay(SCAN_TIME_OUT, () -> {
            Log.i(TAG, "stopScan: " + System.currentTimeMillis());
            scanner.stopScan(mScanCallback);
        });
    }


    /**
     * ????????????
     */
    ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            runOnUiThread(() -> {
                mBleAdapter.addData(result);
                mBleAdapter.notifyItemInserted(mBleAdapter.getData().size());
            });
            Log.i("ScanCallback", "onScanResult: " + callbackType + "ScanResult" + result);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            Log.i("ScanCallback", "onBatchScanResults: " + results.size());
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.i("ScanCallback", "onScanFailed: " + errorCode);
        }
    };


    private class MGattCallBack extends BluetoothGattCallback {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                VKit.post(() -> {
                    ToastCompat.show(getContext(), "????????????????????????");
                });
                Log.i(TAG, "onConnectionStateChange ????????????");
                //????????????
                boolean a = gatt.discoverServices();
                Log.i(TAG, "onConnectionStateChange: " + a);
            } else if (newState == BluetoothProfile.STATE_CONNECTING) {
                Log.i(TAG, "onConnectionStateChange ?????????......");
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "onConnectionStateChange ????????????");
            } else if (newState == BluetoothProfile.STATE_DISCONNECTING) {
                Log.i(TAG, "onConnectionStateChange ???????????????......");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            //????????????????????????????????????????????????????????????
            BluetoothGattService service = mBluetoothGatt.getService(UUID_SERVER);
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID_CHAR_READ);
            boolean b = mBluetoothGatt.setCharacteristicNotification(characteristic, true);
            Log.i(TAG, "onServicesDiscovered ???????????? " + b);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            String data = new String(characteristic.getValue());
            Log.i(TAG, "onCharacteristicChanged ?????????????????? " + data);
        }
    }


    public void sendData(String msg) {
        if (msg.getBytes().length > 20) {
            ToastCompat.show(getContext(), "BLE??????????????????20??????");
            return;
        }
        BluetoothGattService service = null;
        if (mBluetoothGatt == null) {

            /**
             * ??????????????????????????????????????????????????????????????????
             * {@link BluetoothDevice#connectGatt(Context, boolean, BluetoothGattCallback)}????????????????????????
             * ??????????????????????????????????????????????????????????????????????????????????????????GATT
             */
            Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
            for (BluetoothDevice device : devices) {
                mBluetoothGatt = device.connectGatt(getContext(), false, new MGattCallBack());
                if (mBluetoothGatt == null) {
                    continue;
                }
                service = mBluetoothGatt.getService(UUID_SERVER);
                if (service != null) {
                    break;
                }
            }
        } else {
            service = mBluetoothGatt.getService(UUID_SERVER);
        }


        if (service == null) {
            ToastCompat.show(getContext(), "??????????????????");
            return;
        }
        //????????????

        //?????????????????????
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID_CHAR_WRITE);
        //??????setCharacteristicNotification  ???????????????
        mBluetoothGatt.setCharacteristicNotification(characteristic, true);
        characteristic.setValue(msg.getBytes());
        mBluetoothGatt.writeCharacteristic(characteristic);
        ToastCompat.show(getContext(), "???????????????" + msg);
        Log.i(TAG, "sendData ??????????????????");
    }


    /**
     * ??????Ble????????????????????????
     */
    public void startAdvertising(String name) {
        //BLE????????????
        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setConnectable(true)
                .setTimeout(0)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                .build();
        AdvertiseData advertiseData = new AdvertiseData.Builder()
                .setIncludeDeviceName(true)
                .setIncludeTxPowerLevel(true)
                .build();
        mBluetoothAdapter.setName(name);
        //????????????
        BluetoothLeAdvertiser bluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
        bluetoothLeAdvertiser.startAdvertising(settings, advertiseData, advertiseCallback);
    }


    /**
     * ??????????????????
     */
    private AdvertiseCallback advertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
            VKit.post(() -> {
                ToastCompat.show(getContext(), "??????????????????" + settingsInEffect.toString());
            });
            Log.i(TAG, "?????????????????? " + settingsInEffect.toString());
            addService();
        }
    };


    /**
     * ??????uuid
     */
    public static UUID UUID_SERVER = UUID.fromString("0000fff2-0000-1000-8000-00805f9b34fb");

    /**
     * ?????????????????
     */
    public static UUID UUID_CHAR_READ = UUID.fromString("0000ffe3-0000-1000-8000-00805f9b34fb");

    /**
     * ???????????????
     */
    public static final UUID UUID_CHAR_WRITE = UUID.fromString("0000ffe4-0000-1000-8000-00805f9b34fb");


    /**
     * ??????????????????UUID???????????????
     */
    private void addService() {
        BluetoothGattService gattService = new BluetoothGattService(UUID_SERVER, BluetoothGattService.SERVICE_TYPE_PRIMARY);
        //??????????????????
        BluetoothGattCharacteristic characteristicRead = new BluetoothGattCharacteristic(UUID_CHAR_READ,
                BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                BluetoothGattCharacteristic.PERMISSION_READ);
        //??????????????????
        BluetoothGattCharacteristic characteristicWrite = new BluetoothGattCharacteristic(UUID_CHAR_WRITE,
                BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_READ
                        | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                BluetoothGattCharacteristic.PERMISSION_WRITE | BluetoothGattCharacteristic.PERMISSION_READ);
        //????????????????????????????????????
        gattService.addCharacteristic(characteristicRead);
        gattService.addCharacteristic(characteristicWrite);
        //????????????????????????
        bluetoothGattServer = bluetoothManager.openGattServer(this, gattServerCallback);
        //????????????
        bluetoothGattServer.addService(gattService);
    }

    /**
     * ????????? ??????
     */
    private BluetoothGattServer bluetoothGattServer;

    /**
     * ?????????????????????????????????
     */
    private BluetoothGattServerCallback gattServerCallback = new BluetoothGattServerCallback() {
        @Override
        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            super.onConnectionStateChange(device, status, newState);
            String state = "";
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                state = "????????????";
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                state = "????????????";
            }
            VKit.post(() -> {
                ToastCompat.show(getContext(), device.getName() + device.toString() + (newState == 2 ? "????????????" : "????????????"));
            });
            Log.i(TAG, "onConnectionStateChange device=" + device.toString() + " status=" + status + " newState=" + state);
        }

        @Override
        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value);
            String data = new String(value);
            Log.i(TAG, "???????????????????????????????????? " + data);
            //???????????????????????????
            runOnUiThread(() -> {
                mRvChat.setVisibility(View.VISIBLE);
                mRvList.setVisibility(View.INVISIBLE);
                mChatAdapter.addData(new MsgInfo(device.getName(), data, false));
                mChatAdapter.notifyItemInserted(mChatAdapter.getData().size());
            });
            bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, characteristic.getValue());
        }

    };


}