package cn.jpush.android.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import cn.jpush.android.local.ActionHelper;
import cn.jpush.android.local.ProxyActivityAction;

public class PushActivity extends Activity {
   private static final String TAG = "PushActivity";
   public static final String FROM_OTHER_WAY = "from_way";
   public static final String FROM_UNION_AD = "from_union_ad";
   private ProxyActivityAction activityAction;

   @Override
   protected void onCreate(Bundle var1) {
      super.onCreate(var1);
      this.getWindow().setFlags(8192, 8192);
      this.activityAction = ActionHelper.getInstance().getPushActivity(this.getApplicationContext());
      if (this.activityAction != null) {
         this.activityAction.onCreate(this, var1);
      }
   }

   @Override
   protected void onNewIntent(Intent var1) {
      super.onNewIntent(var1);
      if (this.activityAction != null) {
         this.activityAction.onNewIntent(this, var1);
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
         this.activityAction.onPause(this);
      }
   }

   @Override
   protected void onDestroy() {
      if (this.activityAction != null) {
         this.activityAction.onDestroy(this);
      }

      super.onDestroy();
   }

   @Override
   public void onBackPressed() {
      if (this.activityAction != null) {
         this.activityAction.onBackPressed(this);
      }
   }

   public void showTitleBar() {
      if (this.activityAction != null) {
         this.activityAction.onEvent(this, "push_show_titlebar", null);
      }
   }
}
