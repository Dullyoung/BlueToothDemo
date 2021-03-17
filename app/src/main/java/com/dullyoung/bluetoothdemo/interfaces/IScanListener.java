package com.dullyoung.bluetoothdemo.interfaces;

import java.util.List;

/**
 * @author Dullyoung
 * Created by　Dullyoung on 2021/3/17
 **/
public interface IScanListener {
    /**
     * 开始扫描回调
     */
    void onStart();

    /**
     * 扫描成功后返回相关设备信息
     *
     * @param devices 扫描成功的设备列表
     * @param <T>     泛型
     */
    <T> void onSuccess(List<T> devices);

    /**
     * 失败回调
     *
     * @param s 失败原因
     */
    void onFail(String s);

}
