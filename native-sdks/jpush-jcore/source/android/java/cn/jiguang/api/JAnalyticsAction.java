package cn.jiguang.api;

import android.content.Context;

public interface JAnalyticsAction {
   void dispatchResume(Context var1);

   void dispatchPause(Context var1);

   void dispatchStatus(Context var1, String var2);
}
