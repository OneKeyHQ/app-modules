package cn.jiguang.p;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.d.RecoverType5a68d10;

public class RecoverType5a6ba1c implements IInterface {
   private IBinder recoverField_a;
   private String recoverField_b = "";

   public RecoverType5a6ba1c(IBinder var1) {
      this.recoverField_a = var1;
      this.recoverField_b = RecoverType5a68d10.recoverMethod_c_e96ea081("8lAhWp7NB89J3VIJU4lIGfGqvEuhZG7tBjI4IuOrU/y3j/vpb/c+G/Rylr/l3ZwQ");
   }

   @Override
   public IBinder asBinder() {
      return this.recoverField_a;
   }

   public String recoverMethod_a_73cf11cb() {
      Parcel var1 = Parcel.obtain();
      Parcel var2 = Parcel.obtain();

      try {
         var1.writeInterfaceToken(this.recoverField_b);
         this.recoverField_a.transact(3, var1, var2, 0);
         var2.readException();
         return var2.readString();
      } catch (Throwable var8) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("AsusAdvertisingInterface", "get ids-o e: " + var8.getMessage());
      } finally {
         var2.recycle();
         var1.recycle();
      }

      return "";
   }
}
