package cn.android.service;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import cn.jpush.android.helper.Logger;
import cn.jpush.android.local.ActionHelper;

public class JTransitActivity extends Activity {
   @Override
   protected void onCreate(Bundle var1) {
      try {
         super.onCreate(var1);
         this.getWindow().setFlags(8192, 8192);
         FrameLayout var2 = new FrameLayout(this);
         var2.setLayoutParams(new LayoutParams(-1, -1));
         var2.setBackgroundColor(0);
         this.setContentView(var2);
         this.recoverMethod_a_eb2e5432(this.getIntent());
      } catch (Throwable var3) {
      }
   }

   @Override
   protected void onNewIntent(Intent var1) {
      try {
         super.onNewIntent(var1);
         this.recoverMethod_a_eb2e5432(var1);
      } catch (Throwable var3) {
      }
   }

   private void recoverMethod_a_eb2e5432(Intent var1) {
      try {
         if (var1 != null) {
            ActionHelper.getInstance().handleNotificationIntent(this.getApplicationContext(), var1);
            return;
         }
      } catch (Throwable var6) {
         Logger.recoverMethod_d_e925fa09("JTransitActivity", "handle intent failed:" + var6.getMessage());
         return;
      } finally {
         this.finish();
      }
   }
}
