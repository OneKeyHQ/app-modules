package cn.asus.push;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;

public abstract class IAIDLCallback$Member32295d4e extends Binder implements IAIDLCallback {
   private static final String DESCRIPTOR = "cn.asus.push.IAIDLCallback";
   static final int TRANSACTION_call = 1;

   public IAIDLCallback$Member32295d4e() {
      this.attachInterface(this, "cn.asus.push.IAIDLCallback");
   }

   public static IAIDLCallback asInterface(IBinder var0) {
      if (var0 == null) {
         return null;
      }

      IInterface var1;
      return (var1 = var0.queryLocalInterface("cn.asus.push.IAIDLCallback")) != null && var1 instanceof IAIDLCallback
         ? (IAIDLCallback)var1
         : new RecoverType4fccc7a8(var0);
   }

   @Override
   public IBinder asBinder() {
      return this;
   }

   @Override
   public boolean onTransact(int var1, Parcel var2, Parcel var3, int var4) throws android.os.RemoteException {
      String var5 = "cn.asus.push.IAIDLCallback";
      switch (var1) {
         case 1:
            var2.enforceInterface(var5);
            DataBuffer var6;
            if (0 != var2.readInt()) {
               var6 = (DataBuffer)DataBuffer.CREATOR.createFromParcel(var2);
            } else {
               var6 = null;
            }

            this.call(var6);
            return true;
         case 1598968902:
            var3.writeString(var5);
            return true;
         default:
            return super.onTransact(var1, var2, var3, var4);
      }
   }

   public static boolean setDefaultImpl(IAIDLCallback var0) {
      if (RecoverType4fccc7a8.recoverField_a == null && var0 != null) {
         RecoverType4fccc7a8.recoverField_a = var0;
         return true;
      } else {
         return false;
      }
   }

   public static IAIDLCallback getDefaultImpl() {
      return RecoverType4fccc7a8.recoverField_a;
   }
}
