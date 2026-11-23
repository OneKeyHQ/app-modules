package cn.jiguang.api;

import android.content.Context;
import android.os.Bundle;
import java.nio.ByteBuffer;

public abstract class JDispatchAction {
    public abstract void dispatchMessage(
        Context context,
        String sdkType,
        int command,
        int version,
        long rid,
        long uid,
        ByteBuffer body
    );

    public void dispatchTimeOutMessage(Context context, String sdkType, long rid, int command) {
    }

    public abstract boolean isSupportedCMD(String sdkType, int command);

    public abstract short getRegFlag(String sdkType);

    public abstract short getLogPriority(String sdkType);

    public abstract short getLoginFlag(String sdkType);

    public abstract String getRegPriority(String sdkType);

    public abstract String getReportVersionKey(String sdkType);

    public abstract String getSdkVersion(String sdkType);

    public abstract short getUserCtrlProperty(String sdkType);

    public void handleMessage(Context context, String sdkType, Object message) {
    }

    public void onActionRun(Context context, String sdkType, String internalAction, Bundle bundle) {
    }

    public boolean checkActionInfo(String sdkType) {
        return true;
    }

    public Object beforRegister(Context context, String sdkType, int cmd, String key, int type) {
        return null;
    }

    public Object beforLogin(Context context, String sdkType, int cmd, String key, int type) {
        return null;
    }

    public Object getDataByCmd(Context context, int cmd) {
        return null;
    }

    public Bundle getPInfo(String sdkType) {
        return null;
    }

    public Object onSendData(Context context, String sdkType, long rid, int cmd, int sid) {
        return null;
    }
}
