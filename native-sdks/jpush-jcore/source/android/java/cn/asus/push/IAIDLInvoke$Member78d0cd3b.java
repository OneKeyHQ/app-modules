package cn.asus.push;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;

public abstract class IAIDLInvoke$Member78d0cd3b extends Binder implements IAIDLInvoke {
   private static final String DESCRIPTOR = "cn.asus.push.IAIDLInvoke";
   static final int TRANSACTION_syncCall = 1;
   static final int TRANSACTION_asyncCall = 2;

   public IAIDLInvoke$Member78d0cd3b() {
      this.attachInterface(this, "cn.asus.push.IAIDLInvoke");
   }

   public static IAIDLInvoke asInterface(IBinder var0) {
      if (var0 == null) {
         return null;
      }

      IInterface var1;
      return (var1 = var0.queryLocalInterface("cn.asus.push.IAIDLInvoke")) != null && var1 instanceof IAIDLInvoke
         ? (IAIDLInvoke)var1
         : new RecoverType4fccc7a9(var0);
   }

   @Override
   public IBinder asBinder() {
      return this;
   }

   @Override
   public boolean onTransact(int var1, Parcel var2, Parcel var3, int var4) throws android.os.RemoteException {
      String var5 = "cn.asus.push.IAIDLInvoke";
      switch (var1) {
         case 1:
            var2.enforceInterface(var5);
            DataBuffer var7;
            if (0 != var2.readInt()) {
               var7 = (DataBuffer)DataBuffer.CREATOR.createFromParcel(var2);
            } else {
               var7 = null;
            }

            this.syncCall(var7);
            var3.writeNoException();
            return true;
         case 2:
            var2.enforceInterface(var5);
            DataBuffer var6;
            if (0 != var2.readInt()) {
               var6 = (DataBuffer)DataBuffer.CREATOR.createFromParcel(var2);
            } else {
               var6 = null;
            }

            IAIDLCallback var8 = IAIDLCallback$Member32295d4e.asInterface(var2.readStrongBinder());
            this.asyncCall(var6, var8);
            return true;
         case 1598968902:
            var3.writeString(var5);
            return true;
         default:
            return super.onTransact(var1, var2, var3, var4);
      }
   }

   public static boolean setDefaultImpl(IAIDLInvoke var0) {
      if (RecoverType4fccc7a9.recoverField_a == null && var0 != null) {
         RecoverType4fccc7a9.recoverField_a = var0;
         return true;
      } else {
         return false;
      }
   }

   public static IAIDLInvoke getDefaultImpl() {
      return RecoverType4fccc7a9.recoverField_a;
   }
}
