package cn.jiguang.r;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.d.RecoverType5a68d10;

public class RecoverType5a6c19e implements IInterface {
   private IBinder recoverField_a;
   private String recoverField_b;

   public RecoverType5a6c19e(IBinder var1) {
      this.recoverField_a = var1;
      this.recoverField_b = RecoverType5a68d10.recoverMethod_c_e96ea081(
         "axDZqud6H+CDQBXA/yBXOryhAsxzPh1PgN4N991nACfTSsXdCfZsE70k+cjPDPVB6dhHsO0zfFF8mVkwxeVKdQ=="
      );
   }

   @Override
   public IBinder asBinder() {
      return null;
   }

   public String recoverMethod_a_73cf11cb() {
      Parcel var1 = Parcel.obtain();
      Parcel var2 = Parcel.obtain();
      String var3 = "";

      try {
         var1.writeInterfaceToken(this.recoverField_b);
         this.recoverField_a.transact(1, var1, var2, 0);
         var2.readException();
         var3 = var2.readString();
      } catch (Exception var8) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("HuaweiAdvertisingInterface", "getIds error: " + var8.getMessage());
      } finally {
         var2.recycle();
         var1.recycle();
      }

      return var3;
   }
}
