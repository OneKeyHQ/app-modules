package cn.jiguang.aa;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build.VERSION;
import android.telephony.CellInfo;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import cn.jiguang.ac.RecoverTypeaf2a755a;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.bh.RecoverTypeaf2afc7e;
import cn.jiguang.bk.RecoverTypeaf2b07c3;
import cn.jiguang.bk.RecoverTypeaf2b07d6;
import cn.jiguang.d.RecoverType5a68d11;
import java.util.ArrayList;
import java.util.List;

@SuppressLint("MissingPermission")
public class RecoverTypeaf2a6dd9 {
   private Context recoverField_a;
   private final TelephonyManager recoverField_b;
   private List recoverField_c;
   private boolean recoverField_d;
   private boolean recoverField_e;

   public RecoverTypeaf2a6dd9(Context var1, boolean var2) {
      this.recoverField_a = var1;
      this.recoverField_d = var2;
      this.recoverField_b = (TelephonyManager)var1.getSystemService("phone");
   }

   List recoverMethod_a_a9863e32() {
      return this.recoverField_c;
   }

   boolean recoverMethod_b_9b79() {
      return this.recoverField_e;
   }

   public void recoverMethod_c_9b75() {
      this.recoverField_e = false;
      if (this.recoverField_b == null) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JLocationCell", "get telephonyManager failed");
         RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_d_5d527811(2047);
         this.recoverField_e = true;
      } else {
         this.recoverField_c = new ArrayList();
         String var1 = RecoverTypeaf2b07d6.recoverMethod_a_5e799b7f(this.recoverField_a, this.recoverField_d);
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JLocationCell", "Network operator: " + var1);
         String var2 = RecoverTypeaf2b07d6.recoverMethod_b_5e799b7f(this.recoverField_a, this.recoverField_d);
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JLocationCell", "Network operator name: " + var2);
         if (VERSION.SDK_INT > 17) {
            RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_d_5d527811(2056);
            List var3 = this.recoverField_b.getAllCellInfo();
            if (var3 == null || var3.size() <= 0) {
               RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_d_5d527811(2046);
               this.recoverField_e = true;
               return;
            }

            try {
               long var4 = RecoverType5a68d11.recoverMethod_b_f0554de7(this.recoverField_a);

               for (Object recoveryElement_69 : var3) {
                  CellInfo var7 = (CellInfo)recoveryElement_69;
                  if (var7 != null) {
                     RecoverTypeaf2a755a var8 = RecoverTypeaf2b07c3.recoverMethod_a_b71aa1d4(var7);
                     if (var8 != null) {
                        if (TextUtils.equals(var8.recoverField_f, var1)) {
                           var8.recoverField_g = var2;
                        }

                        var8.recoverField_a = var4;
                        this.recoverField_c.add(var8);
                     }
                  }
               }

               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JLocationCell", "Collected cell info size: " + this.recoverField_c.size());
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JLocationCell", "Collected cell info: " + this.recoverField_c);
            } catch (Throwable var12) {
            } finally {
               this.recoverField_e = true;
            }
         } else {
            this.recoverField_b.listen(new RecoverTypeaf2a6dd9.Member8e466056(), 256);
         }
      }
   }

   private class Member8e466056 extends PhoneStateListener {
      private Member8e466056() {
      }

      @Override
      public void onSignalStrengthsChanged(SignalStrength var1) {
         try {
            super.onSignalStrengthsChanged(var1);
            RecoverTypeaf2a755a var2 = new RecoverTypeaf2a755a();
            var2.recoverField_j = var1.getGsmSignalStrength();
            CellLocation var3 = RecoverTypeaf2a6dd9.this.recoverField_b.getCellLocation();
            RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_d_5d527811(2056);
            if (var3 != null) {
               if (var3 instanceof GsmCellLocation) {
                  GsmCellLocation var4 = (GsmCellLocation)var3;
                  var2.recoverField_b = var4.getCid();
                  var2.recoverField_c = var4.getLac();
               } else if (var3 instanceof CdmaCellLocation) {
                  CdmaCellLocation var10 = (CdmaCellLocation)var3;
                  var2.recoverField_b = var10.getBaseStationId();
                  var2.recoverField_c = var10.getNetworkId();
               }

               RecoverTypeaf2a6dd9.this.recoverField_c.add(var2);
               RecoverTypeaf2a6dd9.this.recoverField_b.listen(this, 0);
               return;
            }
         } catch (Throwable var8) {
            return;
         } finally {
            RecoverTypeaf2a6dd9.this.recoverField_e = true;
         }
      }
   }
}
