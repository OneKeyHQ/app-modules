package cn.jiguang.q;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.d.RecoverType5a68d10;

public class RecoverType5a6bddd implements IInterface {
   private IBinder recoverField_a;
   private String recoverField_b = "";

   public RecoverType5a6bddd(IBinder var1) {
      this.recoverField_a = var1;
      this.recoverField_b = RecoverType5a68d10.recoverMethod_c_e96ea081(
         "+Fc+/S0DV5xukan0E/9N4RvXQpEI8h8+6y3k9NAvwjKewmlEeKVcapnVp59DHSw2ZuSa3pDDty/LQ1R0HbmN4Tu7alH/m3nVMfORBLpozB8="
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
      } catch (Throwable var8) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("AdvertisingInterface", "getId error: " + var8.getMessage());
      } finally {
         var2.recycle();
         var1.recycle();
      }

      return var3;
   }
}
