package com.dullyoung.bluetoothdemo.model;

/**
 * @author Dullyoung
 * Created by　Dullyoung on 2021/3/10
 **/
public class MsgInfo {
    private String name;
    private String content;
    private boolean isSelf;

    public MsgInfo(String name, String content, boolean isSelf) {
        this.name = name;
        this.content = content;
        this.isSelf = isSelf;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isSelf() {
        return isSelf;
    }

    public void setSelf(boolean self) {
        isSelf = self;
    }
}
