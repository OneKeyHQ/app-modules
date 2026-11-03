package cn.jpush.android.ui;

import android.app.Activity;
import android.os.Bundle;
import cn.jpush.android.local.ActionHelper;
import cn.jpush.android.local.ProxyActivityAction;

public class PopWinActivity extends Activity {
   private static final String TAG = "PopWinActivity";
   private ProxyActivityAction activityAction;

   @Override
   protected void onCreate(Bundle var1) {
      super.onCreate(var1);
      this.getWindow().setFlags(8192, 8192);
      this.activityAction = ActionHelper.getInstance().getPopWinActivity(this);
      if (this.activityAction != null) {
         this.activityAction.onCreate(this, var1);
      }
   }

   @Override
   public void onBackPressed() {
      super.onBackPressed();
      if (this.activityAction != null) {
         this.activityAction.onBackPressed(this);
      }
   }

   @Override
   protected void onResume() {
      super.onResume();
      if (this.activityAction != null) {
         this.activityAction.onResume(this);
      }
   }

   @Override
   protected void onPause() {
      super.onPause();
      if (this.activityAction != null) {
         this.activityAction.onResume(this);
      }
   }

   @Override
   protected void onDestroy() {
      if (this.activityAction != null) {
         this.activityAction.onDestroy(this);
      }

      super.onDestroy();
   }

   public void startPushActivity(String var1) {
      if (this.activityAction != null) {
         Bundle var2 = new Bundle();
         var2.putString("url", var1);
         this.activityAction.onEvent(this, "start_push_activity", var2);
      }
   }
}
