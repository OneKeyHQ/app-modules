package cn.jiguang.y;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.d.RecoverType5a68d10;
import java.util.concurrent.LinkedBlockingQueue;

public class RecoverType5a6dbe6 {
   Context recoverField_a;
   String recoverField_b = RecoverType5a68d10.recoverMethod_c_e96ea081("7vqR3nNqKoDAlUrx74tE1g==");
   public final LinkedBlockingQueue recoverField_c = new LinkedBlockingQueue(1);
   ServiceConnection recoverField_d = new ServiceConnection() {
      @Override
      public void onServiceConnected(ComponentName var1, IBinder var2) {
         try {
            RecoverType5a6dbe6.this.recoverField_c.put(var2);
         } catch (Throwable var4) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("MsaOpenIDHelper", "linkedBlockingQueue error: " + var4.getMessage());
         }
      }

      @Override
      public void onServiceDisconnected(ComponentName var1) {
      }
   };

   public RecoverType5a6dbe6(Context var1) {
      this.recoverField_a = var1;
   }

   private void recoverMethod_a_aad3b1ff(String var1) {
      Intent var2 = new Intent();
      var2.setClassName(this.recoverField_b, RecoverType5a68d10.recoverMethod_c_e96ea081("dYE0CPNDFJTaYbJHvdYTELPkLL9xmlHysh4O1rCwUiLYHLJ6VxirNHcrSo3Xn2UJ"));
      var2.setAction(RecoverType5a68d10.recoverMethod_c_e96ea081("AdKS/+o+CvOMDbvCnPWRYS8wCU0Ooy6tyMT0tXjLMUvWRKeDq6D985Ar35mAOPu+"));
      var2.putExtra(RecoverType5a68d10.recoverMethod_c_e96ea081("iCmt493CpcicRY98bz8pUhEUlhy7cl4p/yoqnN2nlNM="), var1);

      try {
         var2.putExtra(RecoverType5a68d10.recoverMethod_c_e96ea081("iCmt493CpcicRY98bz8pUjI+wbDT5u8oLPJxFa2B2Zg="), true);
         if (this.recoverField_a.startService(var2) != null) {
            return;
         }
      } catch (Throwable var4) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("MsaOpenIDHelper", "startMsaklServer error: " + var4.getMessage());
      }
   }

   public String recoverMethod_a_73cf11cb() {
      try {
         String var1 = this.recoverField_a.getPackageName();
         this.recoverMethod_a_aad3b1ff(var1);
         Intent var2 = new Intent();
         var2.setClassName(this.recoverField_b, RecoverType5a68d10.recoverMethod_c_e96ea081("dYE0CPNDFJTaYbJHvdYTEBK/MhYfAvKlbba3ZFsuZ+on9OG47ABUmhfzcQhqhJOU"));
         var2.setAction(RecoverType5a68d10.recoverMethod_c_e96ea081("AdKS/+o+CvOMDbvCnPWRYUnA2ZKRLZpCerAAd95VliYNvOuVTnj4/o7GUNhJXhWl"));
         var2.putExtra(RecoverType5a68d10.recoverMethod_c_e96ea081("iCmt493CpcicRY98bz8pUhEUlhy7cl4p/yoqnN2nlNM="), var1);
         boolean var3 = this.recoverField_a.bindService(var2, this.recoverField_d, 1);
         if (var3) {
            try {
               IBinder var4 = (IBinder)this.recoverField_c.take();
               RecoverType5a6dbe7.Member375f82e4.Memberdd8a5ea1 var5 = new RecoverType5a6dbe7.Member375f82e4.Memberdd8a5ea1(var4);
               String var6 = var5.recoverMethod_a_73cf11cb();
               boolean var7 = var5.recoverMethod_b_9b79();
               this.recoverField_a.unbindService(this.recoverField_d);
               return var6;
            } catch (Throwable var13) {
               RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("MsaOpenIDHelper", "getOAID error: " + var13.getMessage());
            } finally {
               this.recoverField_a.unbindService(this.recoverField_d);
            }
         }
      } catch (Throwable var15) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("MsaOpenIDHelper", "get o failed: " + var15.getMessage());
      }

      return "";
   }
}
