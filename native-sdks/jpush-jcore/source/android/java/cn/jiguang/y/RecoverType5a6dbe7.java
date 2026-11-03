package cn.jiguang.y;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.d.RecoverType5a68d10;

public interface RecoverType5a6dbe7 extends IInterface {
   String recoverMethod_a_73cf11cb();

   boolean recoverMethod_b_9b79();

   abstract class Member375f82e4 extends Binder implements RecoverType5a6dbe7 {
      public static class Memberdd8a5ea1 implements RecoverType5a6dbe7 {
         private IBinder recoverField_a;

         public Memberdd8a5ea1(IBinder var1) {
            this.recoverField_a = var1;
         }

         @Override
         public IBinder asBinder() {
            return this.recoverField_a;
         }

         @Override
         public String recoverMethod_a_73cf11cb() {
            String var1 = null;
            Parcel var2 = Parcel.obtain();
            Parcel var3 = Parcel.obtain();

            try {
               var2.writeInterfaceToken(RecoverType5a68d10.recoverMethod_c_e96ea081("Halo+/ex0XYuOCl75w4mzBmlg/ol+vqLiEURlblgA+0="));
               this.recoverField_a.transact(3, var2, var3, 0);
               var3.readException();
               var1 = var3.readString();
            } catch (Throwable var8) {
               RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ZTEOAID", "getOAID obtain exception:" + var8.getMessage());
            } finally {
               var3.recycle();
               var2.recycle();
            }

            return var1;
         }

         @Override
         public boolean recoverMethod_b_9b79() {
            Parcel var1 = Parcel.obtain();
            Parcel var2 = Parcel.obtain();
            boolean var3 = false;

            try {
               var1.writeInterfaceToken(RecoverType5a68d10.recoverMethod_c_e96ea081("Halo+/ex0XYuOCl75w4mzBmlg/ol+vqLiEURlblgA+0="));
               this.recoverField_a.transact(1, var1, var2, 0);
               var2.readException();
               if (var2.readInt() != 0) {
                  var3 = true;
               }
            } catch (Throwable var8) {
               RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ZTEOAID", "isSupported obtain exception:" + var8.getMessage());
            } finally {
               var2.recycle();
               var1.recycle();
            }

            return var3;
         }
      }
   }
}
