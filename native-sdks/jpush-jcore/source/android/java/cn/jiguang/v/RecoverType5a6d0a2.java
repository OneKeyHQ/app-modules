package cn.jiguang.v;

import android.content.Context;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.d.RecoverType5a68d10;

public class RecoverType5a6d0a2 implements IInterface {
   private IBinder recoverField_a;
   private String recoverField_b;

   public RecoverType5a6d0a2(IBinder var1) {
      this.recoverField_a = var1;
      this.recoverField_b = RecoverType5a68d10.recoverMethod_c_e96ea081(
         "NZZdxIY39aBpJdeuRJ0VLp5xPlLZJqfiSSPak26ACVv4Y6EB33Z8c96zLnz4UMIR2tR6ldXn18NaPke3N1cdFQ=="
      );
   }

   @Override
   public IBinder asBinder() {
      return null;
   }

   public String recoverMethod_a_e501eb0d(Context var1) {
      if (var1 == null) {
         return "";
      }

      Parcel var2 = Parcel.obtain();
      Parcel var3 = Parcel.obtain();
      String var4 = "";

      try {
         var2.writeInterfaceToken(this.recoverField_b);
         this.recoverField_a.transact(1, var2, var3, 0);
         var3.readException();
         var4 = var3.readString();
      } catch (Throwable var9) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("SumsungAdvertisingInterface", "get Ids-o error: " + var9.getMessage());
      } finally {
         var3.recycle();
         var2.recycle();
      }

      return var4;
   }
}
