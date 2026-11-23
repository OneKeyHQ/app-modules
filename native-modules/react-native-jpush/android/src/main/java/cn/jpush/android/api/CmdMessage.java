package cn.jpush.android.api;

import android.os.Bundle;

public class CmdMessage {
    public int cmd;
    public int errorCode;
    public String msg;
    public Bundle extra;

    public CmdMessage(int cmd, int errorCode) {
        this.cmd = cmd;
        this.errorCode = errorCode;
    }

    public CmdMessage(int cmd, int errorCode, String msg) {
        this.cmd = cmd;
        this.errorCode = errorCode;
        this.msg = msg;
    }

    public CmdMessage(int cmd, int errorCode, String msg, Bundle extra) {
        this.cmd = cmd;
        this.errorCode = errorCode;
        this.msg = msg;
        this.extra = extra;
    }

    @Override
    public String toString() {
        return "CmdMessage{cmd=" + cmd + ", errorCode=" + errorCode + ", msg='" + msg + "', extra=" + extra + '}';
    }
}
