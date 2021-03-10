package com.dullyoung.bluetoothdemo.view.adapter;

import android.view.View;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.dullyoung.bluetoothdemo.R;
import com.dullyoung.bluetoothdemo.model.MsgInfo;
import com.dullyoung.bluetoothdemo.view.BaseSimpleAdapter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Dullyoung
 * Created by　Dullyoung on 2021/3/10
 **/
public class ChatAdapter extends BaseSimpleAdapter<MsgInfo> {

    public ChatAdapter(@Nullable List<MsgInfo> data) {
        super(R.layout.item_chat, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, MsgInfo msgInfo) {
        holder.setText(R.id.tv_content, msgInfo.getContent());
        if (msgInfo.isSelf()) {
            holder.getView(R.id.tv_name).setVisibility(View.GONE);
            holder.getView(R.id.tv_name2).setVisibility(View.VISIBLE);
            holder.setText(R.id.tv_name2, msgInfo.getName());
        } else {
            holder.getView(R.id.tv_name2).setVisibility(View.GONE);
            holder.getView(R.id.tv_name).setVisibility(View.VISIBLE);
            holder.setText(R.id.tv_name, msgInfo.getName());
        }
    }
}
