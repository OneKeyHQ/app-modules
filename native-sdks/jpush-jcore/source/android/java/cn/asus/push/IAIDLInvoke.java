package cn.asus.push;

import android.os.IInterface;

public interface IAIDLInvoke extends IInterface {
   void syncCall(DataBuffer var1) throws android.os.RemoteException;

   void asyncCall(DataBuffer var1, IAIDLCallback var2) throws android.os.RemoteException;
}
