package cn.jiguang.api;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import java.nio.ByteBuffer;

public interface JAction {
    long dispatchMessage(Context context, long connectionId, int command, Object header, ByteBuffer body);

    void dispatchTimeOutMessage(Context context, long connectionId, long rid, int command);

    boolean isSupportedCMD(int command);

    void onActionRun(Context context, long rid, Bundle bundle, Object handler);

    void onEvent(Context context, long rid, int eventType);

    IBinder getBinderByType(String type);

    String getSdkVersion();
}
