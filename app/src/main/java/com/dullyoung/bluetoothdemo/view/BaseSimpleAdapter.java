package com.dullyoung.bluetoothdemo.view;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Dullyoung
 * Created by　Dullyoung on 2021/3/10
 **/
public abstract class BaseSimpleAdapter<T> extends BaseQuickAdapter<T, BaseViewHolder> {
    public BaseSimpleAdapter(int layoutResId, @Nullable List<T> data) {
        super(layoutResId, data);
    }
}
