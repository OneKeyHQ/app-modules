package cn.jiguang.u;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.Signature;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.text.TextUtils;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.bk.RecoverTypeaf2b07cf;
import cn.jiguang.bk.RecoverTypeaf2b07d4;
import cn.jiguang.d.RecoverType5a68d10;
import java.security.MessageDigest;

public class RecoverType5a6cce1 implements IInterface {
   private IBinder recoverField_a;
   private String recoverField_b;
   private String recoverField_c;
   private String recoverField_d;
   private String recoverField_e;
   private String recoverField_f;

   public RecoverType5a6cce1(IBinder var1) {
      this.recoverField_a = var1;
      this.recoverField_c = RecoverType5a68d10.recoverMethod_c_e96ea081("bFWKQsRzI9d+ktdqxw44MA==");
      this.recoverField_d = RecoverType5a68d10.recoverMethod_c_e96ea081("vWw3rdN6/HN94yy83HO8Lw==");
      this.recoverField_e = RecoverType5a68d10.recoverMethod_c_e96ea081("Ha7IUc4OlbRIQFpw3FJeZw==");
      this.recoverField_f = RecoverType5a68d10.recoverMethod_c_e96ea081("qFFOesfckPwVmbfqzGl5oBCLe9WD17E04n+Ic7C9fCw=");
   }

   @Override
   public IBinder asBinder() {
      return null;
   }

   public String recoverMethod_a_e501eb0d(Context var1) {
      return var1 == null ? "" : this.recoverMethod_a_f169196d(var1.getPackageName(), this.recoverMethod_b_e501eb0d(var1), this.recoverField_c);
   }

   private String recoverMethod_a_f169196d(String var1, String var2, String var3) {
      Parcel var4 = Parcel.obtain();
      Parcel var5 = Parcel.obtain();

      try {
         var4.writeInterfaceToken(this.recoverField_f);
         var4.writeString(var1);
         var4.writeString(var2);
         var4.writeString(var3);
         this.recoverField_a.transact(1, var4, var5, 0);
         var5.readException();
         return var5.readString();
      } catch (Throwable var11) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("OppoAdvertisingInterface", "getIdByType error: " + var11.getMessage());
      } finally {
         var5.recycle();
         var4.recycle();
      }

      return "";
   }

   private String recoverMethod_b_e501eb0d(Context var1) {
      if (TextUtils.isEmpty(this.recoverField_b)) {
         PackageInfo var2 = RecoverTypeaf2b07cf.recoverMethod_a_9537863d(var1, 64);
         if (null == var2) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("OppoAdvertisingInterface", "package get sign failed");
            return this.recoverField_b;
         }

         Signature[] var3 = var2.signatures;
         if (var3 != null && var3.length > 0) {
            byte[] var4 = var3[0].toByteArray();

            try {
               MessageDigest var5 = MessageDigest.getInstance(RecoverTypeaf2b07d4.recoverField_b);
               if (var5 != null) {
                  byte[] var6 = var5.digest(var4);
                  StringBuilder var7 = new StringBuilder();

                  for (byte var11 : var6) {
                     var7.append(Integer.toHexString(var11 & 255 | 256).substring(1, 3));
                  }

                  this.recoverField_b = var7.toString();
               }
            } catch (Throwable var12) {
               RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("OppoAdvertisingInterface", "package sign error: " + var12.getMessage());
            }
         }
      }

      return this.recoverField_b;
   }
}
