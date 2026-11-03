package cn.asus.push;

import android.os.IBinder;
import android.os.Parcel;

final class RecoverType4fccc7a8 implements IAIDLCallback {
   private IBinder recoverField_b;
   public static IAIDLCallback recoverField_a;

   RecoverType4fccc7a8(IBinder var1) {
      this.recoverField_b = var1;
   }

   @Override
   public final IBinder asBinder() {
      return this.recoverField_b;
   }

   @Override
   public final void call(DataBuffer var1) throws android.os.RemoteException {
      Parcel var2 = Parcel.obtain();

      try {
         var2.writeInterfaceToken("cn.asus.push.IAIDLCallback");
         if (var1 != null) {
            var2.writeInt(1);
            var1.writeToParcel(var2, 0);
         } else {
            var2.writeInt(0);
         }

         if (this.recoverField_b.transact(1, var2, null, 1) || IAIDLCallback$Member32295d4e.getDefaultImpl() == null) {
            return;
         }

         IAIDLCallback$Member32295d4e.getDefaultImpl().call(var1);
      } finally {
         var2.recycle();
      }
   }
}
