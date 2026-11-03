package cn.jiguang.android;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public interface IDataShare extends IInterface {
   String DESCRIPTOR = "cn.jiguang.android.IDataShare";

   IBinder getBinderByType(String var1, String var2) throws android.os.RemoteException;

   void onAction(String var1, String var2, Bundle var3) throws android.os.RemoteException;

   Bundle execute(String var1, String var2, Bundle var3) throws android.os.RemoteException;

   String bind(IDataShare var1, String var2) throws android.os.RemoteException;

   class Member9979f04f implements IDataShare {
      @Override
      public IBinder getBinderByType(String var1, String var2) throws android.os.RemoteException {
         return null;
      }

      @Override
      public void onAction(String var1, String var2, Bundle var3) throws android.os.RemoteException {
      }

      @Override
      public Bundle execute(String var1, String var2, Bundle var3) throws android.os.RemoteException {
         return null;
      }

      @Override
      public String bind(IDataShare var1, String var2) throws android.os.RemoteException {
         return null;
      }

      @Override
      public IBinder asBinder() {
         return null;
      }
   }

   class Member9ad6b76 {
      private static Object readTypedObject(Parcel var0, Creator var1) {
         return var0.readInt() != 0 ? var1.createFromParcel(var0) : null;
      }

      private static void writeTypedObject(Parcel var0, Parcelable var1, int var2) {
         if (var1 != null) {
            var0.writeInt(1);
            var1.writeToParcel(var0, var2);
         } else {
            var0.writeInt(0);
         }
      }
   }

   abstract class Membereeaed960 extends Binder implements IDataShare {
      static final int TRANSACTION_getBinderByType = 1;
      static final int TRANSACTION_onAction = 2;
      static final int TRANSACTION_execute = 3;
      static final int TRANSACTION_bind = 4;

      public Membereeaed960() {
         this.attachInterface(this, "cn.jiguang.android.IDataShare");
      }

      public static IDataShare asInterface(IBinder var0) {
         if (var0 == null) {
            return null;
         }

         IInterface var1 = var0.queryLocalInterface("cn.jiguang.android.IDataShare");
         return var1 != null && var1 instanceof IDataShare ? (IDataShare)var1 : new IDataShare.Membereeaed960.Memberfe5e061d(var0);
      }

      @Override
      public IBinder asBinder() {
         return this;
      }

      @Override
      public boolean onTransact(int var1, Parcel var2, Parcel var3, int var4) throws android.os.RemoteException {
         String var5 = "cn.jiguang.android.IDataShare";
         if (var1 >= 1 && var1 <= 16777215) {
            var2.enforceInterface(var5);
         }

         switch (var1) {
            case 1598968902:
               var3.writeString(var5);
               return true;
            default:
               switch (var1) {
                  case 1:
                     String var12 = var2.readString();
                     String var15 = var2.readString();
                     IBinder var18 = this.getBinderByType(var12, var15);
                     var3.writeNoException();
                     var3.writeStrongBinder(var18);
                     break;
                  case 2:
                     String var11 = var2.readString();
                     String var14 = var2.readString();
                     Bundle var17 = (Bundle)IDataShare.Member9ad6b76.readTypedObject(var2, Bundle.CREATOR);
                     this.onAction(var11, var14, var17);
                     break;
                  case 3:
                     String var10 = var2.readString();
                     String var13 = var2.readString();
                     Bundle var16 = (Bundle)IDataShare.Member9ad6b76.readTypedObject(var2, Bundle.CREATOR);
                     Bundle var9 = this.execute(var10, var13, var16);
                     var3.writeNoException();
                     IDataShare.Member9ad6b76.writeTypedObject(var3, var9, 1);
                     break;
                  case 4:
                     IDataShare var6 = asInterface(var2.readStrongBinder());
                     String var7 = var2.readString();
                     String var8 = this.bind(var6, var7);
                     var3.writeNoException();
                     var3.writeString(var8);
                     break;
                  default:
                     return super.onTransact(var1, var2, var3, var4);
               }

               return true;
         }
      }

      private static class Memberfe5e061d implements IDataShare {
         private IBinder recoverField_a;

         Memberfe5e061d(IBinder var1) {
            this.recoverField_a = var1;
         }

         @Override
         public IBinder asBinder() {
            return this.recoverField_a;
         }

         @Override
         public IBinder getBinderByType(String var1, String var2) throws android.os.RemoteException {
            Parcel var3 = Parcel.obtain();
            Parcel var4 = Parcel.obtain();

            IBinder var5;
            try {
               var3.writeInterfaceToken("cn.jiguang.android.IDataShare");
               var3.writeString(var1);
               var3.writeString(var2);
               boolean var6 = this.recoverField_a.transact(1, var3, var4, 0);
               var4.readException();
               var5 = var4.readStrongBinder();
            } finally {
               var4.recycle();
               var3.recycle();
            }

            return var5;
         }

         @Override
         public void onAction(String var1, String var2, Bundle var3) throws android.os.RemoteException {
            Parcel var4 = Parcel.obtain();

            try {
               var4.writeInterfaceToken("cn.jiguang.android.IDataShare");
               var4.writeString(var1);
               var4.writeString(var2);
               IDataShare.Member9ad6b76.writeTypedObject(var4, var3, 0);
               boolean var5 = this.recoverField_a.transact(2, var4, null, 1);
            } finally {
               var4.recycle();
            }
         }

         @Override
         public Bundle execute(String var1, String var2, Bundle var3) throws android.os.RemoteException {
            Parcel var4 = Parcel.obtain();
            Parcel var5 = Parcel.obtain();

            Bundle var6;
            try {
               var4.writeInterfaceToken("cn.jiguang.android.IDataShare");
               var4.writeString(var1);
               var4.writeString(var2);
               IDataShare.Member9ad6b76.writeTypedObject(var4, var3, 0);
               boolean var7 = this.recoverField_a.transact(3, var4, var5, 0);
               var5.readException();
               var6 = (Bundle)IDataShare.Member9ad6b76.readTypedObject(var5, Bundle.CREATOR);
            } finally {
               var5.recycle();
               var4.recycle();
            }

            return var6;
         }

         @Override
         public String bind(IDataShare var1, String var2) throws android.os.RemoteException {
            Parcel var3 = Parcel.obtain();
            Parcel var4 = Parcel.obtain();

            String var5;
            try {
               var3.writeInterfaceToken("cn.jiguang.android.IDataShare");
               var3.writeStrongInterface(var1);
               var3.writeString(var2);
               boolean var6 = this.recoverField_a.transact(4, var3, var4, 0);
               var4.readException();
               var5 = var4.readString();
            } finally {
               var4.recycle();
               var3.recycle();
            }

            return var5;
         }
      }
   }
}
