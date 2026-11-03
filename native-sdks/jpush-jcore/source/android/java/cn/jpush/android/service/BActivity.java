package cn.jpush.android.service;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import cn.jiguang.ak.RecoverTypeaf2a9368;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.api.JCoreManager;
import cn.jiguang.d.RecoverType5a68d10;
import cn.jiguang.internal.JConstants;

public class BActivity extends Activity {
   private static final String TAG = "BActivity";

   @Override
   protected void onCreate(Bundle var1) {
      super.onCreate(var1);
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("BActivity", "BActivity oncreate");
      if (RecoverType5a68d10.recoverMethod_f_f0554de7(this.getApplicationContext()) > 0L && JConstants.isInstrumentationHookFailed) {
         JCoreManager.init(this.getApplicationContext());
      }

      this.handleStart();
   }

   private void handleStart() {
      try {
         RecoverTypeaf2a9368.recoverMethod_a_cf277f22(this.getApplicationContext(), this.getIntent() != null ? this.getIntent().getExtras() : null, 8);
      } catch (Throwable var3) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("BActivity", "handle start error#" + var3);
      }

      try {
         this.finish();
      } catch (Throwable var2) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("BActivity", "finish error#" + var2);
      }
   }

   @Override
   protected void onNewIntent(Intent var1) {
      super.onNewIntent(var1);
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("BActivity", "BActivity onNewIntent");
      this.handleStart();
   }
}
