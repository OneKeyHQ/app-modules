package cn.asus.push;

import android.os.IBinder;
import android.os.Parcel;

final class RecoverType4fccc7a9 implements IAIDLInvoke {
   private IBinder recoverField_b;
   public static IAIDLInvoke recoverField_a;

   RecoverType4fccc7a9(IBinder var1) {
      this.recoverField_b = var1;
   }

   @Override
   public final IBinder asBinder() {
      return this.recoverField_b;
   }

   @Override
   public final void syncCall(DataBuffer var1) throws android.os.RemoteException {
      Parcel var2 = Parcel.obtain();
      Parcel var3 = Parcel.obtain();

      try {
         var2.writeInterfaceToken("cn.asus.push.IAIDLInvoke");
         if (var1 != null) {
            var2.writeInt(1);
            var1.writeToParcel(var2, 0);
         } else {
            var2.writeInt(0);
         }

         if (this.recoverField_b.transact(1, var2, var3, 0) || IAIDLInvoke$Member78d0cd3b.getDefaultImpl() == null) {
            var3.readException();
            return;
         }

         IAIDLInvoke$Member78d0cd3b.getDefaultImpl().syncCall(var1);
      } finally {
         var3.recycle();
         var2.recycle();
      }
   }

   @Override
   public final void asyncCall(DataBuffer var1, IAIDLCallback var2) throws android.os.RemoteException {
      Parcel var3 = Parcel.obtain();

      try {
         var3.writeInterfaceToken("cn.asus.push.IAIDLInvoke");
         if (var1 != null) {
            var3.writeInt(1);
            var1.writeToParcel(var3, 0);
         } else {
            var3.writeInt(0);
         }

         var3.writeStrongBinder(var2 != null ? var2.asBinder() : null);
         if (this.recoverField_b.transact(2, var3, null, 1) || IAIDLInvoke$Member78d0cd3b.getDefaultImpl() == null) {
            return;
         }

         IAIDLInvoke$Member78d0cd3b.getDefaultImpl().asyncCall(var1, var2);
      } finally {
         var3.recycle();
      }
   }
}
