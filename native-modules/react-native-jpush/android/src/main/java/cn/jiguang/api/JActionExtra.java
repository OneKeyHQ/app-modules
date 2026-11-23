package cn.jiguang.api;

import android.content.Context;

public abstract class JActionExtra {
    public Object beforRegister(Context context, int cmd, String extra) {
        return null;
    }

    public Object beforLogin(Context context, int cmd, String extra) {
        return null;
    }

    public boolean checkAction(int actionType) {
        return true;
    }

    public Object onSendData(Context context, long connId, long rid, int cmd, int ver) {
        return null;
    }
}
