package com.dullyoung.bluetoothdemo.view.adapter;

import android.bluetooth.BluetoothDevice;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.dullyoung.bluetoothdemo.R;
import com.dullyoung.bluetoothdemo.model.BlueToothInfo;
import com.dullyoung.bluetoothdemo.view.BaseSimpleAdapter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Dullyoung
 * Created by　Dullyoung on 2021/3/10
 **/
public class BTAdapter extends BaseSimpleAdapter<BluetoothDevice> {
    public BTAdapter(@Nullable List<BluetoothDevice> data) {
        super(R.layout.item_blue_tooth_list, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, BluetoothDevice blueToothInfo) {
        holder.setText(R.id.tv_name, blueToothInfo.getName());
        holder.setText(R.id.tv_mac, blueToothInfo.getAddress());
    }
}
