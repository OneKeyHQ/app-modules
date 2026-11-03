package cn.jiguang.api;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import java.nio.ByteBuffer;

public interface JAction {
   boolean isSupportedCMD(int var1);

   long dispatchMessage(Context var1, long var2, int var4, Object var5, ByteBuffer var6);

   void onActionRun(Context var1, long var2, Bundle var4, Object var5);

   void onEvent(Context var1, long var2, int var4);

   void handleMessage(Context var1, long var2, Object var4);

   IBinder getBinderByType(String var1);

   String getSdkVersion();

   void dispatchTimeOutMessage(Context var1, long var2, long var4, int var6);
}
