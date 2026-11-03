package cn.jpush.android.g;

import android.content.Intent;
import android.text.TextUtils;
import cn.asus.push.DataBuffer;
import cn.asus.push.IAIDLCallback$Member32295d4e;
import cn.jiguang.push.asus.PushMessageReceiver;
import cn.jpush.android.f.RecoverType2e4cd5ab;
import cn.jpush.android.i.RecoverType2e4ce0ed;

public final class RecoverType2e4cd96b extends IAIDLCallback$Member32295d4e {
   @Override
   public final void call(DataBuffer var1) {
      try {
         RecoverType2e4ce0ed.recoverMethod_a_e925fa09("ClientAIDLCallBack", "[call] data:" + String.valueOf(var1) + ",cmd:" + var1.cmd);
         String var2 = var1.cmd;
         long var3 = var1.bundle.getLong("message_rid", -1L);
         String var5 = var1.bundle.getString("message_content");
         String var6;
         if (TextUtils.isEmpty(var6 = RecoverType2e4cd5ab.recoverMethod_b_e501eb0d(RecoverType2e4cd5ab.recoverField_a))) {
            Intent var8 = new Intent();
            var1.bundle.putString("message_cmd", var1.cmd);
            var8.putExtras(var1.bundle);
            var8.setAction("com.ups.push.PUSH_RESPONSE");
            var8.setPackage(RecoverType2e4cd5ab.recoverField_c);
            RecoverType2e4cd5ab.recoverField_a.sendBroadcast(var8);
            return;
         }

         ((PushMessageReceiver)Class.forName(var6).newInstance()).recoverMethod_a_3b017c5(RecoverType2e4cd5ab.recoverField_a, var3, var2, var5);
      } catch (Throwable var7) {
      }
   }
}
